package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import lombok.SneakyThrows;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NodeRenameButton extends JButton {
    private final String RENAME_NODE = "Rename file";
    private final String ENTER_NAME = "Enter new file name";
    private final String NAME_EMPTY = "File name can't be empty";
    private final String NAME_EXISTS = "File name is already exists";
    private final String TOO_LONG_NAME = "Folder name is too long";
    private final String DONE = "File names are successfully changed";

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    @NotNull
    private Node node;

    public NodeRenameButton(@NotNull MainPanel mainPanel) {
        mainService = CDI.current().select(MainService.class).get();
        this.mainPanel = mainPanel;
        setText("Rename");
        setBounds(150, 5, 115, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                if (mainPanel.getSelectedNodes().size() > 0) {
                    NodeButton current;
                    for (int i = 0; i < mainPanel.getSelectedNodes().size(); i++) {
                        current = mainPanel.getSelectedNodes().get(i);
                        node = current.getNode();
                        renameNode(ENTER_NAME);
                    }
                    mainPanel.refresh(mainService.getNodes());
                    mainPanel.setStatusMessage(DONE);
                }
            }
        };
    }

    private void renameNode(@NotNull String msg) throws RepositoryException {
        String result = JOptionPane.showInputDialog(NodeRenameButton.this,
                msg, RENAME_NODE, JOptionPane.INFORMATION_MESSAGE);
        if (result != null && result.isEmpty()) {
            renameNode(NAME_EMPTY);
            return;
        }
        if (result != null && mainService.hasNode(result)) {
            renameNode(NAME_EXISTS);
            return;
        }
        if (result != null && result.length() >= 25) {
            renameNode(TOO_LONG_NAME);
            return;
        }
        if (result == null) {
            return;
        }
        mainService.remoteRenameNode(result, node);
    }
}
