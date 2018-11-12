package ru.xaero.javacore.graphic;

import org.jetbrains.annotations.NotNull;
import lombok.Setter;
import ru.xaero.javacore.graphic.panels.Authorization;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;
import ru.xaero.javacore.utils.ScreenCoords;

import javax.enterprise.inject.Alternative;
import javax.jcr.RepositoryException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@Setter
@Alternative
public class AppWindow extends JFrame {

    @NotNull
    private MainService mainService;

    @NotNull
    private Authorization authorization;

    @NotNull
    private MainPanel mainPanel;

    @NotNull
    private JLayeredPane jLayeredPane;

    public AppWindow() throws HeadlessException {
        setTitle("Cloud Application");
        setBounds(ScreenCoords.getX(), ScreenCoords.getY(), ScreenCoords.width, ScreenCoords.height);
        addLogoutOnClose();
        setResizable(false);

        setVisible(true);
    }

    public void initElements() {
        jLayeredPane = getLayeredPane();
        jLayeredPane.add(authorization, jLayeredPane.POPUP_LAYER);
        jLayeredPane.add(mainPanel, jLayeredPane.DEFAULT_LAYER, 0);
        if (authorization.isVisible()) authorization.focus();
    }

    public void loginSuccess() throws RepositoryException {
        authorization.clearStatus();
        authorization.setVisible(false);
        mainPanel.getNodes();
        mainPanel.enablePanel();
    }

    public void wrongLogin() {
        authorization.wrongLogin();
    }

    public void loginError() {
        authorization.loginError();
    }

    private void addLogoutOnClose() {
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                boolean isSessionLive = mainService.getSession() != null && mainService.getSession().isLive();
                if (isSessionLive) mainService.exit();
                dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
    }
}
