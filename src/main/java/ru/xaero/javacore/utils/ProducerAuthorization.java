package ru.xaero.javacore.utils;

import org.jetbrains.annotations.NotNull;
import ru.xaero.javacore.graphic.panels.Authorization;
import ru.xaero.javacore.services.MainService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class ProducerAuthorization {

    private final Authorization authorization = new Authorization();

    @Inject
    private MainService mainService;

    @Produces
    @NotNull
    public Authorization authorization() {
        authorization.setMainService(mainService);
        return authorization;
    }
}
