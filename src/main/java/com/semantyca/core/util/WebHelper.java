package com.semantyca.core.util;

import com.semantyca.core.model.cnst.LanguageCode;

import java.io.File;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebHelper {
    private static final Pattern UNSAFE_CHARS = Pattern.compile("[\\\\/:*?\"<>|\\s'`~!@#$%^&()+=\\[\\]{}|;,]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");

    public static String generateSlug(String element1, String element2) {
        if (element1 == null) element1 = "";
        if (element2 == null) element2 = "";
        String input = element1 + " " + element2;
        return processSlug(input);
    }

    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String fileName;
        String extension = "";

        int lastDotIndex = input.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < input.length() - 1) {
            fileName = input.substring(0, lastDotIndex);
            extension = input.substring(lastDotIndex);
        } else {
            fileName = input;
        }

        String slug = processSlug(fileName);
        return slug + extension;
    }

    public static String generateSlugPath(String... segments) {
        if (segments == null || segments.length == 0) {
            return "";
        }

        return Arrays.stream(segments)
                .map(WebHelper::generateSlug)
                .filter(slug -> !slug.isEmpty())
                .collect(Collectors.joining("/"));
    }

    public static String generatePersonSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        int atIndex = input.indexOf('@');
        if (atIndex > 0) {
            input = input.substring(0, atIndex);
        }

        String extension = "";
        int lastDotIndex = input.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < input.length() - 1) {
            extension = input.substring(lastDotIndex).toLowerCase();
            input = input.substring(0, lastDotIndex);
        }

        String slug = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        return slug + extension;
    }

    public static String generateSlug(EnumMap<LanguageCode, String> localizedName) {
        if (localizedName == null || localizedName.isEmpty()) {
            return "";
        }

        String name = localizedName.get(LanguageCode.en);
        if (name == null || name.trim().isEmpty()) {
            name = localizedName.values().stream()
                    .filter(value -> value != null && !value.trim().isEmpty())
                    .findFirst()
                    .orElse("");
        }

        return generateSlug(name);
    }

    protected static String getMimeType(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (fileExtension) {
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "ogg" -> "audio/ogg";
            case "flac" -> "audio/flac";
            default -> "application/octet-stream";
        };
    }

    private static String processSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String cleaned = UNSAFE_CHARS.matcher(normalized).replaceAll("");
        String lowercase = cleaned.toLowerCase();
        String finalSlug = MULTIPLE_DASHES.matcher(lowercase).replaceAll("-");
        finalSlug = finalSlug.replaceAll("^-+|-+$", "");

        return finalSlug;
    }

    public static String generateRandomBrightColor() {
        Random random = new Random();
        int r, g, b;

        do {
            r = 100 + random.nextInt(156); // 100-255
            g = 100 + random.nextInt(156); // 100-255
            b = 100 + random.nextInt(156); // 100-255

            if (Math.max(Math.max(r, g), b) < 200) {
                int brightComponent = random.nextInt(3);
                switch (brightComponent) {
                    case 0: r = 200 + random.nextInt(56); break;
                    case 1: g = 200 + random.nextInt(56); break;
                    case 2: b = 200 + random.nextInt(56); break;
                }
            }
        } while (isGrayish(r, g, b) || isBrownish(r, g, b) || isTooDark(r, g, b) || isTooWhite(r, g, b));

        return String.format("#%02X%02X%02X", r, g, b);
    }

    private static boolean isGrayish(int r, int g, int b) {
        return Math.abs(r - g) < 30 && Math.abs(g - b) < 30 && Math.abs(r - b) < 30;
    }

    private static boolean isBrownish(int r, int g, int b) {
        return r > g && g > b && r < 180 && g < 120;
    }

    private static boolean isTooDark(int r, int g, int b) {
        int perceivedBrightness = (299 * r + 587 * g + 114 * b) / 1000;
        return perceivedBrightness < 150;
    }

    private static boolean isTooWhite(int r, int g, int b) {
        int perceivedBrightness = (299 * r + 587 * g + 114 * b) / 1000;
        return perceivedBrightness > 240 || (r > 245 && g > 245 && b > 245);
    }
}