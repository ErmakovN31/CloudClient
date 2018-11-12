package ru.xaero.javacore.services;

import org.jetbrains.annotations.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.xaero.javacore.graphic.AppWindow;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.logging.Loggable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApplicationScoped
public class MainService {

    @Inject
    @NotNull
    private ConnectionService connectionService;

    @Inject
    @NotNull
    private RemoteFileService remoteFileService;

    @Inject
    @NotNull
    private LocalFileService localFileService;

    @Inject
    @NotNull
    private AppWindow appWindow;

    @Inject
    @NotNull
    private SettingsLoadService settingsLoadService;

    @Inject
    @NotNull
    private MainPanel mainPanel;

    @NotNull
    private List<Node> nodes;

    @NotNull
    private Session session;

    @NotNull
    private Node current;

    public MainService() {
        nodes = new ArrayList<>();
    }

    public void login(@NotNull String login, @NotNull String pass) {
        try {
            Session newSession = connectionService.loginSession(login, pass);
            if (newSession != null) {
                session = newSession;
                current = getRootNode();
                appWindow.loginSuccess();
            }
        } catch (LoginException e) {
            appWindow.wrongLogin();
        } catch (RepositoryException e) {
            appWindow.loginError();
            e.printStackTrace();
        }
    }

    @NotNull
    @Loggable
    private Node getRootNode() throws RepositoryException {
        return remoteFileService.getRootNode();
    }

    @NotNull
    @Loggable
    public String getPath() throws RepositoryException {
        return current.getPath();
    }

    @NotNull
    @Loggable
    public List<Node> getNodes() throws RepositoryException {
        return remoteFileService.getNodes((ArrayList) nodes, current);
    }

    @Loggable
    public boolean hasNode(@NotNull String name) throws RepositoryException {
        return current.hasNode(name);
    }

    @Loggable
    public void back() throws RepositoryException {
        if (!current.getPath().equals(getRootNode().getPath())) {
            current = current.getParent();
        }
    }

    @NotNull
    @Loggable
    public Node remoteUploadFolder(@NotNull File file, @NotNull Node parent) throws RepositoryException {
        return remoteFileService.createFolder(file, parent);
    }

    @Loggable
    public void remoteUploadFile(@NotNull File file, @NotNull Node parent) throws IOException, RepositoryException {
        byte[] data = localFileService.readData(file);
        remoteFileService.uploadFile(parent, file, data);
    }

    @Loggable
    public void remoteCreateFolder(@NotNull String name) throws RepositoryException {
        remoteFileService.createFolder(name, current);
    }

    @Loggable
    public void remoteRemoveNode(@NotNull Node node) throws RepositoryException {
        remoteFileService.removeNode(node);
    }

    @Loggable
    public void remoteRenameNode(@NotNull String name, @NotNull Node node) throws RepositoryException {
        remoteFileService.renameNode(name, node);
    }

    @Loggable
    public void setNewServer(@NotNull String newServer) throws IOException {
        settingsLoadService.saveNewServer(newServer);
    }

    @Loggable
    public void pasteCopiedNode(@NotNull Node node) throws IOException, RepositoryException {
        remoteFileService.copyNode(node, current);
    }

    @Loggable
    public void pasteCuttedNode(@NotNull Node node) throws RepositoryException {
        remoteFileService.cutPasteNode(node, current);
    }

    @Loggable
    public void localCreateFolder(@NotNull File file) {
        localFileService.createFolder(file);
    }

    @Loggable
    public void localDownloadFile(@NotNull Node node) throws IOException, RepositoryException {
        byte[] data = remoteFileService.readData(node);
        localFileService.createFile(new File(node.getName()), data);
    }

    @Loggable
    public void localDownloadFile(@NotNull File file, @NotNull Node node) throws IOException, RepositoryException {
        byte[] data = remoteFileService.readData(node);
        localFileService.createFile(file, data);
    }

    @Loggable
    public void openLocalFile(@NotNull File file) throws IOException {
        localFileService.openFile(file);
    }

    @Loggable
    public void setNewLocalStorage(@NotNull File newRoot) throws IOException {
        localFileService.setRoot(newRoot);
        settingsLoadService.saveNewStorage(newRoot.getPath());
    }

    @Loggable
    public boolean localExist(@NotNull Node node) throws RepositoryException {
        return localFileService.exists(new File(node.getName()));
    }

    @Loggable
    public boolean localExist(@NotNull File file) {
        return localFileService.exists(file);
    }

    @Loggable
    public void localOverwrite(@NotNull File file, @NotNull Node node) throws IOException, RepositoryException {
        byte[] data = remoteFileService.readData(node);
        localFileService.overwriteFile(file, data);
    }

    @Loggable
    public void openLocalStorage() throws IOException {
        localFileService.openLocalStorage();
    }

    @NotNull
    @Loggable
    public String getCurrentName() throws RepositoryException {
        return current.getName();
    }

    @Loggable
    public void exit() {
        connectionService.exit();
    }
}
