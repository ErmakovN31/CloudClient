package ru.xaero.javacore.utils;

import org.jetbrains.annotations.NotNull;
import ru.xaero.javacore.graphic.AppWindow;
import ru.xaero.javacore.graphic.panels.Authorization;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class ProducerAppWindow {

    private final AppWindow appWindow = new AppWindow();

    @Inject
    private MainService mainService;

    @Inject
    private MainPanel mainPanel;

    @Inject
    private Authorization authorization;

    @Produces
    @NotNull
    public AppWindow appWindow() {
        appWindow.setAuthorization(authorization);
        appWindow.setMainPanel(mainPanel);
        appWindow.setMainService(mainService);
        appWindow.initElements();
        return appWindow;
    }
}
