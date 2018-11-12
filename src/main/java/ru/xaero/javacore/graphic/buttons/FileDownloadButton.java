package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import lombok.SneakyThrows;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

public class FileDownloadButton extends JButton {
    private final String FILE = "nt:file";
    private final String FOLDER = "nt:folder";
    private final String EMPTY_PATH = "";
    private final String WINDOW_NAME = "Overwrite";
    private final String MESSAGE = "Do you want to overwrite existing files?";
    private final String DONE = "Files successfully downloaded";

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    @NotNull
    private List<NodeButton> selectedNodes;

    public FileDownloadButton(@NotNull MainPanel panel) {
        this.mainService = CDI.current().select(MainService.class).get();
        mainPanel = panel;
        setBounds(20, 35, 100, 25);
        setText("Download");
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                selectedNodes = mainPanel.getSelectedNodes();
                if (hasExistingFiles()) {
                    folderOverwriteDialog();
                } else {
                    cleanDownloadFiles();
                }
            }
        };
    }

    private void folderOverwriteDialog() throws IOException, RepositoryException {
        int result = JOptionPane.showConfirmDialog(FileDownloadButton.this, MESSAGE,
                WINDOW_NAME, JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == YES_OPTION) downloadAndOverwriteFiles();
        else if (result == NO_OPTION) cleanDownloadFiles();
    }

    private void downloadAndOverwriteFiles() throws RepositoryException, IOException {
        for (int i = 0; i < selectedNodes.size(); i++) {
            Node node = selectedNodes.get(i).getNode();
            if (node.isNodeType(FILE)) {
                if (mainService.localExist(node)) {
                    File file = new File(node.getName());
                    mainService.localOverwrite(file, node);
                } else {
                    mainService.localDownloadFile(node);
                }
            }
            if (node.isNodeType(FOLDER)) {
                if (node.hasNodes()) {
                    downloadAndOverwriteInnerFiles(node, EMPTY_PATH);
                } else {
                    mainService.localCreateFolder(new File(node.getName()));
                }
            }
        }
        mainPanel.setStatusMessage(DONE);
    }

    private void downloadAndOverwriteInnerFiles(@NotNull Node node,
                                                @NotNull String path) throws RepositoryException, IOException {
        NodeIterator nodeIterator = node.getNodes();
        String newPath = (path + node.getName() + "/");
        while (nodeIterator.hasNext()) {
            Node temp = nodeIterator.nextNode();
            if (temp.isNodeType(FILE)) {
                File file = new File(newPath + temp.getName());
                if (mainService.localExist(file)) mainService.localOverwrite(file, temp);
                else mainService.localDownloadFile(file, temp);
            }
            if (temp.isNodeType(FOLDER)) {
                if (temp.hasNodes()) downloadAndOverwriteInnerFiles(temp, newPath);
                else mainService.localCreateFolder(new File(newPath + temp.getName()));
            }
        }
    }

    private void cleanDownloadFiles() throws RepositoryException, IOException {
        for (int i = 0; i < selectedNodes.size(); i++) {
            Node node = selectedNodes.get(i).getNode();
            if (node.isNodeType(FILE)) {
                if (!mainService.localExist(node)) mainService.localDownloadFile(node);
            }
            if (node.isNodeType(FOLDER)) {
                if (node.hasNodes()) cleanDownloadInnerFiles(node, EMPTY_PATH);
                else mainService.localCreateFolder(new File(node.getName()));
            }
        }
        mainPanel.setStatusMessage(DONE);
    }

    private void cleanDownloadInnerFiles(@NotNull Node node,
                                         @NotNull String path) throws RepositoryException, IOException {
        NodeIterator nodeIterator = node.getNodes();
        String newPath = (path + node.getName() + "/");
        while (nodeIterator.hasNext()) {
            Node temp = nodeIterator.nextNode();
            if (temp.isNodeType(FILE)) {
                File file = new File(newPath + temp.getName());
                if (!mainService.localExist(file)) mainService.localDownloadFile(file, temp);
            }
            if (temp.isNodeType(FOLDER)) {
                if (temp.hasNodes()) downloadAndOverwriteInnerFiles(temp, newPath);
                else mainService.localCreateFolder(new File(newPath + temp.getName()));
            }
        }
    }

    private boolean hasExistingFiles() throws RepositoryException {
        for (int i = 0; i < selectedNodes.size(); i++) {
            Node node = selectedNodes.get(i).getNode();
            boolean fileExists = node.isNodeType(FILE) && mainService.localExist(node);
            boolean folderFilesExists = node.isNodeType(FOLDER) && node.hasNodes() && mainService.localExist(node)
                    && hasInnerExistingFiles(node, EMPTY_PATH);
            if (fileExists) return true;
            if (folderFilesExists) return true;
        }
        return false;
    }

    private boolean hasInnerExistingFiles(@NotNull Node node,
                                          @NotNull String path) throws RepositoryException {
        NodeIterator nodeIterator = node.getNodes();
        String newPath = (path + node.getName() + "/");
        while (nodeIterator.hasNext()) {
            Node temp = nodeIterator.nextNode();
            if (temp.isNodeType(FOLDER) && hasInnerExistingFiles(temp, newPath)) return true;
            if (temp.isNodeType(FILE)) {
                File file = new File(newPath + temp.getName());
                if (mainService.localExist(file)) return true;
            }
        }
        return false;
    }
}
