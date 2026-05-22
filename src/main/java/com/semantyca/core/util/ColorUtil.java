package com.semantyca.core.util;

import java.util.Random;

public class ColorUtil {

    private static final Random RANDOM = new Random();

    public static String[] generateContrastColorPair() {
        String bg = generateDistinctBackground();
        return new String[]{bg, contrastingFontColor(bg)};
    }

    public static String contrastingFontColor(String hexBg) {
        if (hexBg == null || hexBg.length() < 7) {
            return "#1A1A1A";
        }
        int r = Integer.parseInt(hexBg.substring(1, 3), 16);
        int g = Integer.parseInt(hexBg.substring(3, 5), 16);
        int b = Integer.parseInt(hexBg.substring(5, 7), 16);
        double L = 0.2126 * linearize(r / 255.0)
                 + 0.7152 * linearize(g / 255.0)
                 + 0.0722 * linearize(b / 255.0);
        return L > 0.179 ? "#1A1A1A" : "#FFFFFF";
    }

    private static String generateDistinctBackground() {
        int r, g, b;
        do {
            r = 100 + RANDOM.nextInt(156);
            g = 100 + RANDOM.nextInt(156);
            b = 100 + RANDOM.nextInt(156);
            if (Math.max(Math.max(r, g), b) < 200) {
                switch (RANDOM.nextInt(3)) {
                    case 0: r = 200 + RANDOM.nextInt(56); break;
                    case 1: g = 200 + RANDOM.nextInt(56); break;
                    case 2: b = 200 + RANDOM.nextInt(56); break;
                }
            }
        } while (isGrayish(r, g, b) || isBrownish(r, g, b) || isTooDark(r, g, b) || isTooWhite(r, g, b));
        return String.format("#%02X%02X%02X", r, g, b);
    }

    private static double linearize(double c) {
        return c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    private static boolean isGrayish(int r, int g, int b) {
        return Math.abs(r - g) < 30 && Math.abs(g - b) < 30 && Math.abs(r - b) < 30;
    }

    private static boolean isBrownish(int r, int g, int b) {
        return r > g && g > b && r < 180 && g < 120;
    }

    private static boolean isTooDark(int r, int g, int b) {
        return (299 * r + 587 * g + 114 * b) / 1000 < 150;
    }

    private static boolean isTooWhite(int r, int g, int b) {
        int brightness = (299 * r + 587 * g + 114 * b) / 1000;
        return brightness > 240 || (r > 245 && g > 245 && b > 245);
    }
}
