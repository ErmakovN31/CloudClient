package ru.xaero.javacore.graphic.panels;

import org.jetbrains.annotations.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.xaero.javacore.graphic.buttons.*;
import ru.xaero.javacore.services.MainService;
import ru.xaero.javacore.graphic.buttons.OpenLocalButton;
import ru.xaero.javacore.utils.ScreenCoords;

import javax.enterprise.inject.Alternative;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Alternative
public class MainPanel extends JPanel {

    private final String PATH_PREFIX = "Path: ";
    private final String SHORT_PATH = ".../";
    private final String EMPTY = "";
    private final JLabel path = new JLabel(PATH_PREFIX);
    private final JLabel status = new JLabel();
    private final JPanel pathPanel = new JPanel(null);
    private final JPanel statusPanel = new JPanel(null);
    private final Directory workSpace = new Directory();
    private final PasteButton pasteButton = new PasteButton(this);
    private final List<NodeButton> nodeButtons = new ArrayList<>();
    private final List<NodeButton> selectedNodes = new ArrayList<>();

    @NotNull
    private final JScrollPane jScrollPane;

    @NotNull
    private MainService mainService;

    @NotNull
    private List<Node> nodeList;

    public MainPanel() {
        setLayout(new BorderLayout());
        setBounds(0, 0, ScreenCoords.leftBorder, ScreenCoords.upperBorder);
        setPathPanel(new Dimension(getWidth(), 90));
        setStatusPanel(new Dimension(getWidth(), 30));
        jScrollPane = new JScrollPane(workSpace, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setWorkSpace();
        add(pathPanel, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.SOUTH);
        add(jScrollPane, BorderLayout.CENTER);
        setVisible(false);
    }

    class Directory extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredSize() {
            return getSize();
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 60;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 150;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private void setPathPanel(Dimension panelDimension) {
        pathPanel.setPreferredSize(panelDimension);
        path.setBounds(25, 65, getWidth(), 20);
        pathPanel.add(path);
        pathPanel.add(new AddFolderButton(this));
        pathPanel.add(new BackButton(this));
        pathPanel.add(new NodeRenameButton(this));
        pathPanel.add(new NodeRemoveButton(this));
        pathPanel.add(new FileDownloadButton(this));
        pathPanel.add(new FileUploadButton(this));
        pathPanel.add(new CutButton(this));
        pathPanel.add(new CopyButton(this));
        pathPanel.add(pasteButton);
        pathPanel.add(new OpenLocalButton());
        pathPanel.add(new NewStorageButton(this));
    }

    private void setStatusPanel(Dimension panelDimension) {
        status.setForeground(Color.GREEN);
        statusPanel.setPreferredSize(panelDimension);
        status.setBounds(20, 5, getWidth(), 20);
        statusPanel.add(status);
    }

    private void setWorkSpace() {
        workSpace.setLayout(new BoxLayout(workSpace, BoxLayout.Y_AXIS));
        jScrollPane.setWheelScrollingEnabled(true);
    }

    public void getNodes() throws RepositoryException {
        nodeList = mainService.getNodes();
    }

    public void cut() {
        pasteButton.cut();
    }

    public void copy() {
        pasteButton.copy();
    }

    public void refresh(@NotNull List<Node> nodeList) throws RepositoryException {
        cleanPanel();
        int j = 0;
        j = showFolders(j, nodeList);
        showFiles(j, nodeList);
        workSpace.setSize(ScreenCoords.leftBorder, nodeButtons.size() * 30);
        setPath();
    }

    private void cleanPanel() {
        if (nodeButtons.size() > 0) {
            workSpace.removeAll();
            nodeButtons.clear();
            selectedNodes.clear();
            workSpace.setSize(ScreenCoords.leftBorder, nodeButtons.size() * 30);
        }
    }

    private int showFolders(int j, List<Node> nodeList) throws RepositoryException {
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).isNodeType("nt:folder")) {
                nodeButtons.add(new NodeButton(nodeList.get(i), this));
                workSpace.add(nodeButtons.get(j++));
            }
        }
        return j;
    }

    private void showFiles(int j, List<Node> nodeList) throws RepositoryException {
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).isNodeType("nt:file")) {
                nodeButtons.add(new NodeButton(nodeList.get(i), this));
                workSpace.add(nodeButtons.get(j++));
            }
        }
    }

    public void setPath() throws RepositoryException {
        path.setText(PATH_PREFIX + mainService.getPath());
        boolean isPathTooLong = path.getText().length() >= 101;
        if (isPathTooLong) path.setText(PATH_PREFIX + SHORT_PATH + mainService.getCurrentName());
    }

    public void setStatusMessage(@NotNull String msg) {
        status.setText(msg);
    }

    public void clearStatus() {
        status.setText(EMPTY);
    }

    public void enablePanel() throws RepositoryException {
        refresh(nodeList);
        setVisible(true);
    }
}
