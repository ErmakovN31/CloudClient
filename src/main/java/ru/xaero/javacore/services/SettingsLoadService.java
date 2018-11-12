package ru.xaero.javacore.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import ru.xaero.javacore.utils.Settings;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.net.URL;

@ApplicationScoped
public class SettingsLoadService {

    @NotNull
    private ObjectMapper mapper;

    @NotNull
    private File settingsFile;

    @NotNull
    private Settings clientSettings;

    public SettingsLoadService() {
        mapper = new ObjectMapper();
        settingsFile = new File("settings.json");
        clientSettings = new Settings();
    }

    public void loadSettings() throws IOException {
        if (settingsFile.exists()) {
            clientSettings = mapper.readValue(new FileInputStream(settingsFile), Settings.class);
        } else {
            settingsFile.createNewFile();
            mapper.writeValue(new FileOutputStream(settingsFile), clientSettings);
        }
    }

    public void saveNewStorage(@NotNull String newStorage) throws IOException {
        clientSettings.setLocalStorage(newStorage);
        mapper.writeValue(new FileOutputStream(settingsFile), clientSettings);
    }

    public void saveNewServer(@NotNull String newServer) throws IOException {
        clientSettings.setServerAddress(newServer);
        mapper.writeValue(new FileOutputStream(settingsFile), clientSettings);
    }

    @NotNull
    public String serverAddress() {
        return clientSettings.getServerAddress();
    }

    @NotNull
    public File localStorage() {
        return new File(clientSettings.getLocalStorage());
    }
}
