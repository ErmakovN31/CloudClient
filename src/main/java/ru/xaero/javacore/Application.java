package ru.xaero.javacore;

import ru.xaero.javacore.graphic.AppWindow;
import ru.xaero.javacore.services.SettingsLoadService;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.CDI;
import java.io.IOException;

public class Application {
    public static void main( String[] args ) throws IOException {
        SeContainerInitializer.newInstance().addPackages(Application.class).initialize()
                .select(SettingsLoadService.class).get().loadSettings();
        final AppWindow appWindow = CDI.current().select(AppWindow.class).get();
    }
}
