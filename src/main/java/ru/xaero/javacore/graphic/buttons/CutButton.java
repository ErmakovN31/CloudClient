package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import ru.xaero.javacore.graphic.panels.MainPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CutButton extends JButton {

    private final String CUTTED = "Files added to buffer";

    @NotNull
    private final MainPanel mainPanel;

    public CutButton(@NotNull MainPanel panel) {
        mainPanel = panel;
        setText("Cut");
        setBounds(410, 5, 70, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.cut();
                mainPanel.setStatusMessage(CUTTED);
            }
        };
    }
}
