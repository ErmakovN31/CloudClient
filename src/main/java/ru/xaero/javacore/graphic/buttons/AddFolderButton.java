package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import lombok.SneakyThrows;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.RepositoryException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddFolderButton extends JButton {

    private final String NEW_FOLDER = "Enter folder name";
    private final String NAME_EMPTY = "Folder name can't be empty";
    private final String NAME_EXISTS = "Folder name is already exists";
    private final String CREATE_FOLDER = "Create folder";
    private final String TOO_LONG_NAME = "Folder name is too long";
    private final String FOLDER_CREATED = "Folder is successfully created";

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    public AddFolderButton(@NotNull MainPanel mainPanel) {
        mainService = CDI.current().select(MainService.class).get();
        this.mainPanel = mainPanel;
        setText("New folder");
        setBounds(20, 5, 100, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                createFolder(NEW_FOLDER);
            }
        };
    }

    private void createFolder(@NotNull String msg) throws RepositoryException {
        String result = JOptionPane.showInputDialog(AddFolderButton.this,
                msg, CREATE_FOLDER, JOptionPane.INFORMATION_MESSAGE);
        if (result != null && result.isEmpty()) {
            createFolder(NAME_EMPTY);
            return;
        }
        if (result != null && mainService.hasNode(result)) {
            createFolder(NAME_EXISTS);
            return;
        }
        if (result != null && result.length() >= 25) {
            createFolder(TOO_LONG_NAME);
            return;
        }
        if (result == null) {
            return;
        }
        mainService.remoteCreateFolder(result);
        mainPanel.refresh(mainService.getNodes());
        mainPanel.setStatusMessage(FOLDER_CREATED);
    }
}
