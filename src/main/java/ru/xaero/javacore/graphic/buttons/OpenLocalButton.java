package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import lombok.SneakyThrows;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenLocalButton extends JButton {

    @NotNull
    private final MainService mainService;

    public OpenLocalButton() {
        mainService = CDI.current().select(MainService.class).get();
        setText("Open local files");
        setBounds(250, 35, 150, 25);
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                mainService.openLocalStorage();
            }
        };
    }
}
