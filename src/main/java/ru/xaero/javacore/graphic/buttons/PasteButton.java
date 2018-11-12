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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PasteButton extends JButton {

    private final String DONE = "Paste done";

    @NotNull
    private final List<NodeButton> buffer;

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    private enum Action {CUT, COPY}

    @NotNull
    private Action savedAction;

    public PasteButton(@NotNull MainPanel panel) {
        mainService = CDI.current().select(MainService.class).get();
        mainPanel = panel;
        buffer = new ArrayList<>();
        setText("Paste");
        setBounds(570, 5, 110, 25);
        addActionListener(buttonAction());
    }

    @NotNull
    private ActionListener buttonAction() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < buffer.size(); i++) {
                    pasteFile(buffer.get(i).getNode());
                }
                mainPanel.refresh(mainService.getNodes());
                mainPanel.setStatusMessage(DONE);
            }
        };
    }

    public void cut() {
        savedAction = Action.CUT;
        fillBuffer();
    }

    public void copy() {
        savedAction = Action.COPY;
        fillBuffer();
    }

    private void fillBuffer() {
        buffer.clear();
        buffer.addAll(mainPanel.getSelectedNodes());
    }

    private void pasteFile(@NotNull Node node) throws RepositoryException, IOException {
        switch (savedAction) {
            case CUT:
                mainService.pasteCuttedNode(node);
                break;
            case COPY:
                mainService.pasteCopiedNode(node);
                break;
        }
    }
}
