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
import java.io.IOException;

public class NewStorageButton extends JButton {

    private final String TITLE = "Choose new download folder";
    private final String DONE = "New local storage successfully selected";

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    @NotNull
    private final JFileChooser fileChooser;

    public NewStorageButton(@NotNull MainPanel panel) {
        mainService = CDI.current().select(MainService.class).get();
        fileChooser = new JFileChooser();
        mainPanel = panel;
        setText("Set download folder");
        setBounds(400, 35, 150, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                initFileChooser();
                int result = fileChooser.showOpenDialog(NewStorageButton.this);
                uploadOption(result);
            }
        };
    }

    private void initFileChooser() {
        fileChooser.setDialogTitle(TITLE);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
    }

    private void uploadOption(int result) throws IOException, RepositoryException {
        if (result == JFileChooser.APPROVE_OPTION) {
            mainService.setNewLocalStorage(fileChooser.getSelectedFile());
            mainPanel.setStatusMessage(DONE);
        }
    }
}
