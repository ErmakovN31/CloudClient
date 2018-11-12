package ru.xaero.javacore.graphic.panels;

import org.jetbrains.annotations.NotNull;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.xaero.javacore.services.MainService;
import ru.xaero.javacore.utils.ScreenCoords;

import javax.enterprise.inject.Alternative;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@Setter
@Alternative
public class Authorization extends JPanel {

    private final String SET_SERVER = "Enter new server address (default: http://localhost:8080)";
    private final String ADDRESS_EMPTY = "Server can't be empty";
    private final String ADDRESS_POSTFIX = "/rmi";
    private final String TITLE = "Set server";
    private final String EMPTY = "";

    private final JLabel msg = new JLabel("Please login", SwingConstants.CENTER);
    private final JLabel login = new JLabel("login", SwingConstants.CENTER);
    private final JLabel password = new JLabel("password", SwingConstants.CENTER);
    private final JLabel status = new JLabel(EMPTY, SwingConstants.CENTER);
    private final JPanel fields = new JPanel(new GridLayout(2, 2));
    private final JPanel statusPanel = new JPanel(null);
    private final JButton setServer = new JButton("Change server");
    private final Dimension statusSize = new Dimension();
    private final JTextField loginField = new JTextField();
    private final JTextField passwordField = new JTextField();

    @NotNull
    private MainService mainService;

    public Authorization() throws HeadlessException {
        setBounds(0, 0, 400, 110);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        setLocation(ScreenCoords.CENTER_X - getWidth() / 2, ScreenCoords.CENTER_Y - getHeight() / 2);
        setStatusPanel();
        status.setForeground(Color.RED);
        loginField.addActionListener(loginFunction());
        passwordField.addActionListener(loginFunction());

        add(msg, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.SOUTH);
        add(fields, BorderLayout.CENTER);

        fields.add(login);
        fields.add(password);
        fields.add(loginField);
        fields.add(passwordField);

        setVisible(true);
    }

    private void setStatusPanel() {
        statusSize.setSize(getWidth(), 30);
        status.setBounds(0, 5, 250, 25);
        setServer.setBounds(250, 0, 150, 30);
        setServer.addActionListener(settingServer());
        statusPanel.setPreferredSize(statusSize);
        statusPanel.add(status);
        statusPanel.add(setServer);
    }

    public void focus() {
        loginField.grabFocus();
    }

    public void wrongLogin() {
        status.setText("Incorrect login/password");
    }

    public void loginError() {
        status.setText("Server error. Try again");
    }

    public void serverError() {
        status.setText("Wrong server address");
    }

    public void clearStatus() {
        status.setText(EMPTY);
    }

    @NotNull
    private ActionListener loginFunction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String pass = passwordField.getText();
                loginField.setText(EMPTY);
                passwordField.setText(EMPTY);
                loginField.grabFocus();
                mainService.login(login, pass);
                boolean isLogged = mainService.getSession() != null && mainService.getSession().isLive();
                if (isLogged) setVisible(false);
            }
        };
    }

    @NotNull
    private ActionListener settingServer() {
        return new ActionListener() {
            @Override
            @SneakyThrows
            public void actionPerformed(ActionEvent e) {
                checkServerAddress(SET_SERVER);
            }
        };
    }

    private void checkServerAddress(@NotNull String message) throws IOException {
        String result = JOptionPane.showInputDialog(Authorization.this,
                message, TITLE, JOptionPane.INFORMATION_MESSAGE);
        if (result != null && result.isEmpty()) {
            checkServerAddress(ADDRESS_EMPTY);
            return;
        }
        if (result == null) {
            return;
        }
        saveServer(result + ADDRESS_POSTFIX);
    }

    private void saveServer(@NotNull String address) throws IOException {
        mainService.setNewServer(address);
    }
}
