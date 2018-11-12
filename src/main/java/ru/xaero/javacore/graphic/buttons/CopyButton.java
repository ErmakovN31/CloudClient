package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import ru.xaero.javacore.graphic.panels.MainPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CopyButton extends JButton {

    private final String COPIED = "Files added to buffer";

    @NotNull
    private final MainPanel mainPanel;

    public CopyButton(@NotNull MainPanel panel) {
        mainPanel = panel;
        setText("Copy");
        setBounds(480, 5, 90, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.copy();
                mainPanel.setStatusMessage(COPIED);
            }
        };
    }
}
