package ru.nsu.view;

import java.awt.*;

public final class AppSettings {
    private static int scalePercent = 100;

    private AppSettings() {
    }

    public static int getScalePercent() {
        return scalePercent;
    }

    public static void cycleScale() {
        scalePercent += 25;
        if (scalePercent > 300) {
            scalePercent = 100;
        }
    }

    public static int scaled(int basePixels) {
        return Math.max(1, basePixels * scalePercent / 100);
    }

    public static Dimension scaledSize(int baseWidth, int baseHeight) {
        return new Dimension(scaled(baseWidth), scaled(baseHeight));
    }
}
