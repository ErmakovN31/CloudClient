package ru.xaero.javacore.services;

import org.jetbrains.annotations.NotNull;
import ru.xaero.javacore.logging.Loggable;
import sun.misc.IOUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@ApplicationScoped
public class RemoteFileService {
    private final String NT_FILE = "nt:file";
    private final String NT_FOLDER = "nt:folder";
    private final String JCR_CONTENT = "jcr:content";
    private final String NT_RESOURCE = "nt:resource";
    private final String JCR_DATA = "jcr:data";
    private final String JCR_LAST_MODIFIED = "jcr:lastModified";
    private final String SEPARATOR = "/";

    @Inject
    @NotNull
    private MainService mainService;

    @NotNull
    private Session session;

    public RemoteFileService() {
    }

    @NotNull
    @Loggable
    public Node getRootNode() throws RepositoryException {
        session = mainService.getSession();
        if (session != null) return session.getRootNode();
        return null;
    }

    @NotNull
    @Loggable
    public List<Node> getNodes(@NotNull ArrayList<Node> nodes, @NotNull Node current) throws RepositoryException {
        nodes.clear();
        NodeIterator nodeIterator = current.getNodes();
        while (nodeIterator.hasNext()) {
            nodes.add(nodeIterator.nextNode());
        }
        return nodes;
    }

    @Loggable
    public void createFolder(@NotNull String name, @NotNull Node parent) throws RepositoryException {
        session = mainService.getSession();
        if (session != null && session.isLive() && !parent.hasNode(name)) {
            parent.addNode(name, NT_FOLDER);
            session.save();
        }
    }

    @NotNull
    @Loggable
    public Node createFolder(@NotNull File file, @NotNull Node parent) throws RepositoryException {
        session = mainService.getSession();
        if (session != null && session.isLive() && !parent.hasNode(file.getName())) {
            parent.addNode(file.getName(), NT_FOLDER);
            session.save();
        }
        return parent.getNode(file.getName());
    }

    @Loggable
    public void uploadFile(@NotNull Node parent,
                           @NotNull File file,
                           @NotNull byte[] data) throws RepositoryException {
        createFile(parent, file.getName(), data);
    }

    @Loggable
    public void createNodeCopy(@NotNull Node parent, @NotNull Node node) throws RepositoryException, IOException {
        byte[] data = readData(node);
        createFile(parent, node.getName(), data);
    }

    @Loggable
    private void createFile(@NotNull Node parent,
                            @NotNull String fileName,
                            @NotNull byte[] data) throws RepositoryException {
        session = mainService.getSession();
        if (session != null && session.isLive()) {
            if (parent.hasNode(fileName)) fileName = generateNodeName(parent, fileName);
            Node newNode = parent.addNode(fileName, NT_FILE);
            Node content = newNode.addNode(JCR_CONTENT, NT_RESOURCE);
            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            Binary binary = session.getValueFactory().createBinary(stream);
            content.setProperty(JCR_DATA, binary);
            Calendar created = Calendar.getInstance();
            content.setProperty(JCR_LAST_MODIFIED, created);
            binary.dispose();
            session.save();
        }
    }

    @NotNull
    @Loggable
    private String generateNodeName(@NotNull Node parent, @NotNull String fileName) throws RepositoryException {
        int id = 0;
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        StringBuilder newName;
        do {
            id++;
            newName = new StringBuilder(name);
            newName.append(" ");
            newName.append(id);
            newName.append(".");
            newName.append(extension);
        } while (parent.hasNode(newName.toString()));
        return newName.toString();
    }

    @Loggable
    public void copyNode(@NotNull Node node, @NotNull Node newParent) throws RepositoryException, IOException {
        if (node.isNodeType(NT_FILE)) createNodeCopy(newParent, node);
        if (node.isNodeType(NT_FOLDER)) {
            createFolder(node.getName(), newParent);
            createInnerNodes(node);
        }
    }

    @Loggable
    private void createInnerNodes(@NotNull Node node) throws RepositoryException, IOException {
        if (node.hasNodes()) {
            NodeIterator nodeIterator = node.getNodes();
            while (nodeIterator.hasNext()) {
                Node child = nodeIterator.nextNode();
                copyNode(child, node);
            }
        }
    }

    @NotNull
    @Loggable
    public byte[] readData(@NotNull Node node) throws RepositoryException, IOException {
        session = mainService.getSession();
        if (session != null && session.isLive()) {
            Node content = node.getNode(JCR_CONTENT);
            Binary binary = content.getProperty(JCR_DATA).getBinary();
            byte[] data = IOUtils.readNBytes(binary.getStream(), (int) binary.getSize());
            binary.dispose();
            return data;
        }
        return new byte[]{};
    }

    @Loggable
    public void cutPasteNode(@NotNull Node node, @NotNull Node newParent) throws RepositoryException {
        session = mainService.getSession();
        if (session != null && session.isLive()) {
            if (node.getParent() == newParent) return;
            if (newParent.hasNode(node.getName())) {
                if (node.isNodeType(NT_FILE)) {
                    String newName = generateNodeName(newParent, node.getName());
                    moveNode(node, newParent, newName);
                }
                if (node.isNodeType(NT_FOLDER)) {
                    moveInnerNodes(node, newParent);
                    removeNode(node);
                }
            } else {
                moveNode(node, newParent, node.getName());
            }
        }
    }

    @Loggable
    private void moveNode(@NotNull Node node,
                          @NotNull Node newParent,
                          @NotNull String name) throws RepositoryException {
        if (newParent.getPath().equals(getRootNode().getPath())) {
            session.move(node.getPath(), newParent.getPath() + name);
        } else {
            session.move(node.getPath(), newParent.getPath() + SEPARATOR + name);
        }
        session.save();
    }

    @Loggable
    private void moveInnerNodes(@NotNull Node node, @NotNull Node parent) throws RepositoryException {
        NodeIterator nodeIterator = node.getNodes();
        while (nodeIterator.hasNext()) {
            Node child = nodeIterator.nextNode();
            Node newParent = parent.getNode(node.getName());
            cutPasteNode(child, newParent);
        }
    }

    @Loggable
    public void renameNode(@NotNull String name, @NotNull Node node) throws RepositoryException {
        session = mainService.getSession();
        if (session != null && session.isLive()) moveNode(node, node.getParent(), name);
    }

    @Loggable
    public void removeNode(@NotNull Node node) throws RepositoryException {
        session = mainService.getSession();
        if (session != null && session.isLive()) {
            node.remove();
            session.save();
        }
    }
}
