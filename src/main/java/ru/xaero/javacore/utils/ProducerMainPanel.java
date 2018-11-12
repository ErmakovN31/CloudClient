package ru.xaero.javacore.utils;

import org.jetbrains.annotations.NotNull;
import ru.xaero.javacore.graphic.panels.MainPanel;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class ProducerMainPanel {

    private final MainPanel mainPanel = new MainPanel();

    @Inject
    private MainService mainService;

    @Produces
    @NotNull
    public MainPanel mainPanel() {
        mainPanel.setMainService(mainService);
        return mainPanel;
    }
}
