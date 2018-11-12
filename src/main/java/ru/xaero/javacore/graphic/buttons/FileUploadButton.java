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
import java.io.File;
import java.io.IOException;

public class FileUploadButton extends JButton {
    private final String TITLE = "Choose files";
    private final String DONE = "Files successfully uploaded";

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    @NotNull
    private final JFileChooser fileChooser;

    public FileUploadButton(@NotNull MainPanel panel) {
        mainService = CDI.current().select(MainService.class).get();
        mainPanel = panel;
        fileChooser = new JFileChooser();
        setText("Upload");
        setBounds(120, 35, 100, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                initFileChooser();
                int result = fileChooser.showOpenDialog(FileUploadButton.this);
                uploadOption(result);
            }
        };
    }

    private void initFileChooser() {
        fileChooser.setDialogTitle(TITLE);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
    }

    private void uploadOption(int result) throws IOException, RepositoryException {
        if (result == JFileChooser.APPROVE_OPTION) {
            uploadFiles(fileChooser.getSelectedFiles(), mainService.getCurrent());
            mainPanel.refresh(mainService.getNodes());
            mainPanel.setStatusMessage(DONE);
        }
    }

    private void uploadFiles(@NotNull File[] files, @NotNull Node current) throws IOException, RepositoryException {
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                Node folder = mainService.remoteUploadFolder(files[i], current);
                File[] innerFiles = files[i].listFiles();
                if (innerFiles.length > 0) uploadFiles(innerFiles, folder);
            } else {
                mainService.remoteUploadFile(files[i], current);
            }
        }
    }
}
