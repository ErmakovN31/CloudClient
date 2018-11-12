package ru.xaero.javacore.utils;

import org.jetbrains.annotations.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {

    @NotNull
    private String serverAddress = "http://localhost:8080/rmi";

    @NotNull
    private String localStorage = "cloudDownload";

    public Settings() {
    }
}
