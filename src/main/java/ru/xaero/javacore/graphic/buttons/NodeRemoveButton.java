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

import static javax.swing.JOptionPane.YES_OPTION;

public class NodeRemoveButton extends JButton {
    private final String CONFIRM = "Are you sure?";
    private final String DELETE = "Remove files?";
    private final String DONE = "Files successfully removed";

    @NotNull
    private final MainService mainService;

    @NotNull
    private final MainPanel mainPanel;

    public NodeRemoveButton(@NotNull MainPanel panel) {
        mainService = CDI.current().select(MainService.class).get();
        this.mainPanel = panel;
        setBounds(265, 5, 115, 25);
        setText("Remove");
        addActionListener(action());
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                if (mainPanel.getSelectedNodes().size() > 0) {
                    int result = JOptionPane.showConfirmDialog(NodeRemoveButton.this, CONFIRM,
                            DELETE, JOptionPane.YES_NO_OPTION);
                    removeSelectedNodes(result);
                }
            }
        };
    }

    private void removeSelectedNodes(int result) throws RepositoryException {
        if (result == YES_OPTION) {
            NodeButton button;
            Node current;
            for (int i = 0; i < mainPanel.getSelectedNodes().size(); i++) {
                button = mainPanel.getSelectedNodes().get(i);
                current = button.getNode();
                mainService.remoteRemoveNode(current);
            }
            mainPanel.refresh(mainService.getNodes());
            mainPanel.setStatusMessage(DONE);
        }
    }
}
