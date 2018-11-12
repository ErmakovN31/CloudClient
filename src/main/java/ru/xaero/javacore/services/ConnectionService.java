package ru.xaero.javacore.services;

import org.jetbrains.annotations.NotNull;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import ru.xaero.javacore.graphic.panels.Authorization;
import ru.xaero.javacore.logging.Loggable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.*;
import java.net.MalformedURLException;

@ApplicationScoped
public class ConnectionService {

    @Inject
    @NotNull
    private Authorization authorization;

    @Inject
    @NotNull
    private SettingsLoadService settingsLoadService;

    @NotNull
    private Repository repository;

    @NotNull
    private Session session;

    public ConnectionService() {
    }

    @NotNull
    @Loggable
    public Session loginSession(@NotNull String login, @NotNull String pass) throws RepositoryException {
        if (session == null) {
            try {
                repository = new URLRemoteRepository(settingsLoadService.serverAddress());
                if (login != null && pass != null) {
                    session = repository.login(new SimpleCredentials(login, pass.toCharArray()));
                } else {
                    session = repository.login(new GuestCredentials());
                }
            } catch (MalformedURLException e) {
                authorization.serverError();
            }
        }
        return session;
    }

    @Loggable
    public void exit() {
        if (session != null && session.isLive()) session.logout();
        System.exit(0);
    }
}
