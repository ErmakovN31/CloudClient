package ru.xaero.javacore.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Константы для окна в зависимости от разрешения рабочего стола
 */
public abstract class ScreenCoords {

    @NotNull
    public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public static final int X_INDENT = 16;
    public static final int Y_INDENT = 39;
    public static final int width = (int) (screen.width * 0.9f);
    public static final int height = (int) (screen.height * 0.9f);
    public static final int CENTER_X = (width - X_INDENT) / 2;
    public static final int CENTER_Y = (height - Y_INDENT) / 2;
    public static final int leftBorder = width - X_INDENT;
    public static final int upperBorder = height - Y_INDENT;

    private static final int x = (int) (screen.width * 0.05f);
    private static final int y = (int) (screen.height * 0.05f);

    public static int getX() {
        if (screen.width >= 1024) {
            return x;
        }
        return (int) (1024 * 0.05f);
    }

    public static int getY() {
        if (screen.width >= 768) {
            return y;
        }
        return (int) (768 * 0.05f);
    }
}
