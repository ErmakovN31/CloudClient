package ru.xaero.javacore.services;

import org.jetbrains.annotations.NotNull;
import lombok.Setter;
import ru.xaero.javacore.logging.Loggable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Setter
@ApplicationScoped
public class LocalFileService {

    private final String SEPARATOR = "/";
    private final String EXPLORER = "explorer ";

    @NotNull
    private File root;

    @Inject
    @NotNull
    private SettingsLoadService settingsLoadService;

    public LocalFileService() {
        root = CDI.current().select(SettingsLoadService.class).get().localStorage();
        initStorage();
    }

    @Loggable
    public void initStorage() {
        if (!root.exists()) root.mkdir();
    }

    @Loggable
    public void openLocalStorage() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(EXPLORER + root);
    }

    @Loggable
    public void createFolder(@NotNull File file) {
        file = new File(root.toPath() + SEPARATOR + file.getPath());
        if (!file.exists()) file.mkdirs();
    }

    @Loggable
    public void createFile(@NotNull File file, @NotNull byte[] data) throws IOException {
        file = new File(root.toPath() + SEPARATOR + file.getPath());
        createParentFolder(file);
        if (!file.exists()) {
            file.createNewFile();
            Path path = Paths.get(file.toURI());
            Files.write(path, data);
        }
    }

    @Loggable
    private void createParentFolder(@NotNull File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    @NotNull
    @Loggable
    public byte[] readData(@NotNull File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    @Loggable
    public boolean exists(@NotNull File file) {
        return new File(root.toPath() + "/" + file.getPath()).exists();
    }

    @Loggable
    public void overwriteFile(@NotNull File file, @NotNull byte[] data) throws IOException {
        File temp = new File(root.toPath() + SEPARATOR + file.getPath());
        if (temp.exists()) {
            temp.delete();
            createFile(file, data);
        }
    }

    @Loggable
    public void openFile(@NotNull File file) throws IOException {
        file = new File(root.toPath() + SEPARATOR + file.getPath());
        Desktop.getDesktop().open(file);
    }
}
