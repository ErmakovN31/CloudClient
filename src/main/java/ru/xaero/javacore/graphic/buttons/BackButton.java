package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import lombok.SneakyThrows;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackButton extends JButton {

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    public BackButton(@NotNull MainPanel panel) {
        mainService = CDI.current().select(MainService.class).get();
        mainPanel = panel;
        setText("Back");
        setBounds(580, 35, 100, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                mainService.back();
                mainPanel.refresh(mainService.getNodes());
                mainPanel.clearStatus();
            }
        };
    }
}
