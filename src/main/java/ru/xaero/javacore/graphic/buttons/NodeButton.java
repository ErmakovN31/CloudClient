package ru.xaero.javacore.graphic.buttons;

import org.jetbrains.annotations.NotNull;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;
import ru.xaero.javacore.utils.ScreenCoords;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.JOptionPane.YES_OPTION;

@Getter
public class NodeButton extends JButton {
    private static final int X_SPACE = 34;
    private static final String TITLE = "Open file";
    private static final String MESSAGE = "Download and open this file?";
    private static final String EXIST = "Overwrite";
    private static final String OVERWRITE = "File is already exists. Overwrite it?";

    @NotNull
    private final MainService mainService;

    @NotNull
    private final Node node;

    @NotNull
    private final MainPanel mainPanel;

    private long timer;

    public NodeButton(@NotNull Node node, @NotNull MainPanel panel) throws RepositoryException {
        mainService = CDI.current().select(MainService.class).get();
        this.node = node;
        mainPanel = panel;
        setText(node.getName());
        setIcon();
        setHorizontalAlignment(LEFT);
        setBackground(Color.white);
        setSelected(false);
        setMaximumSize(new Dimension(ScreenCoords.width - X_SPACE, 30));
        addActionListener(action());
    }

    private void setIcon() throws RepositoryException {
        if (node.isNodeType("nt:folder")) setIcon(new ImageIcon(getClass().getResource("/icon/folder.png")));
        if (node.isNodeType("nt:file")) setIcon(new ImageIcon(getClass().getResource("/icon/file.png")));
    }

    @NotNull
    private ActionListener action() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                if (onDoubleClick()) return;
                onSingleClick(e);
            }
        };
    }

    private void offButtons(@NotNull ArrayList<NodeButton> nodeButtons) {
        for (int i = 0; i < nodeButtons.size(); i++) {
            nodeButtons.get(i).setSelected(false);
            nodeButtons.get(i).setBackground(Color.white);
        }
        nodeButtons.clear();
    }

    private boolean onDoubleClick() throws RepositoryException, IOException {
        boolean isDoubleClick = Math.abs(timer - System.currentTimeMillis()) <= 500 && isSelected()
                && mainPanel.getSelectedNodes().size() <= 1;
        if (isDoubleClick) {
            if (node.isNodeType("nt:folder")) return folderDoubleClick();
            if (node.isNodeType("nt:file")) return fileDoubleClick();
        }
        timer = System.currentTimeMillis();
        return false;
    }

    private boolean folderDoubleClick() throws RepositoryException {
        setSelected(false);
        offButtons((ArrayList) mainPanel.getSelectedNodes());
        openFolder();
        return true;
    }

    private boolean fileDoubleClick() throws IOException, RepositoryException {
        setSelected(false);
        offButtons((ArrayList) mainPanel.getSelectedNodes());
        downloadAndOpen();
        return true;
    }

    private void openFolder() throws RepositoryException {
        mainService.setCurrent(node);
        mainPanel.refresh(mainService.getNodes());
        mainPanel.clearStatus();
    }

    private void downloadAndOpen() throws RepositoryException, IOException {
        int result = JOptionPane.showConfirmDialog(NodeButton.this, MESSAGE,
                TITLE, JOptionPane.YES_NO_OPTION);
        if (result == YES_OPTION) {
            if (exist()) {
                existDialog();
            } else {
                File file = new File(node.getName());
                mainService.localDownloadFile(node);
                mainService.openLocalFile(file);
            }
        }
    }

    private boolean exist() throws RepositoryException {
        File file = new File(node.getName());
        return mainService.localExist(file);
    }

    private void existDialog() throws IOException, RepositoryException {
        int result = JOptionPane.showConfirmDialog(NodeButton.this, OVERWRITE,
                EXIST, JOptionPane.YES_NO_OPTION);
        if (result == YES_OPTION) {
            File file = new File(node.getName());
            mainService.localOverwrite(file, node);
            mainService.openLocalFile(file);
        }
    }

    private void onSingleClick(ActionEvent e) {
        boolean isCtrlPressed = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0
                             && (e.getModifiers() & ActionEvent.SHIFT_MASK) == 0;
        boolean isShiftPressed = (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0
                              && (e.getModifiers() & ActionEvent.CTRL_MASK) == 0;
        if (isCtrlPressed) ctrlSelect();
        else if (isShiftPressed) shiftSelect();
        else selectNode();
    }

    private void shiftSelect() {
        if (mainPanel.getSelectedNodes().size() >= 1) selectCoupleOfNodes();
        else selectNode();
    }

    private void selectCoupleOfNodes() {
        NodeButton first = null;
        NodeButton last = null;
        NodeButton current;
        for (int i = 0; i < mainPanel.getNodeButtons().size(); i++) {
            current = mainPanel.getNodeButtons().get(i);
            if (current.isSelected() || current == this) {
                last = current;
                if (first == null) first = current;
            }
        }
        selectNodes(first, last);
    }

    private void selectNodes(@NotNull NodeButton first, @NotNull NodeButton last) {
        boolean selectable = false;
        NodeButton current;
        offButtons((ArrayList) mainPanel.getSelectedNodes());
        for (int i = 0; i < mainPanel.getNodeButtons().size(); i++) {
            current = mainPanel.getNodeButtons().get(i);
            if (current == first) {
                selectable = true;
            }
            if (selectable) {
                current.setSelected(true);
                current.setBackground(Color.cyan);
                mainPanel.getSelectedNodes().add(current);
            }
            if (current == last) {
                return;
            }
        }
    }

    private void ctrlSelect() {
        if (isSelected()) {
            setSelected(false);
            setBackground(Color.white);
            mainPanel.getSelectedNodes().remove(NodeButton.this);
        } else {
            setSelected(true);
            setBackground(Color.cyan);
            mainPanel.getSelectedNodes().add(NodeButton.this);
        }
    }

    private void selectNode() {
        offButtons((ArrayList) mainPanel.getSelectedNodes());
        setSelected(true);
        setBackground(Color.cyan);
        mainPanel.getSelectedNodes().add(NodeButton.this);
    }
}
