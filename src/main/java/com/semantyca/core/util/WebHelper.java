package com.semantyca.core.util;

import com.ibm.icu.text.Transliterator;
import com.semantyca.core.model.cnst.LanguageCode;

import java.io.File;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebHelper {
    /**
     * ICU/CLDR: map any script to Latin, then to ASCII (Cyrillic, Greek, accents, ligatures, etc.).
     */
    private static final Transliterator TO_ASCII_LATIN = Transliterator.getInstance("Any-Latin; Latin-ASCII");

    private static final Pattern WHITESPACE = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_ASCII_ALNUM_RUNS = Pattern.compile("[^a-z0-9]+");
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

        String[] stemAndExt = splitPreservingKnownExtension(input);
        String slug = processSlug(stemAndExt[0]);
        return slug + stemAndExt[1];
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

        String[] stemAndExt = splitPreservingKnownExtension(input);
        String extension = stemAndExt[1].toLowerCase(Locale.ROOT);
        String slug = processSlug(stemAndExt[0]);

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

    /** Splits a trailing {@code .ext} only for known file types so dots inside titles stay. */
    private static String[] splitPreservingKnownExtension(String input) {
        int lastDotIndex = input.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < input.length() - 1) {
            String extCandidate = input.substring(lastDotIndex + 1);
            if (isKnownFileExtension(extCandidate)) {
                return new String[] { input.substring(0, lastDotIndex), input.substring(lastDotIndex) };
            }
        }
        return new String[] { input, "" };
    }

    private static boolean isKnownFileExtension(String extensionWithoutDot) {
        return switch (extensionWithoutDot.toLowerCase(Locale.ROOT)) {
            case "mp3", "wav", "ogg", "flac", "jpg", "jpeg", "png", "gif", "webp", "pdf" -> true;
            default -> false;
        };
    }

    private static String processSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String s = WHITESPACE.matcher(input).replaceAll("-");
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);
        s = TO_ASCII_LATIN.transliterate(s);
        s = s.toLowerCase(Locale.ROOT);
        s = Normalizer.normalize(s, Normalizer.Form.NFKD);
        s = COMBINING_MARKS.matcher(s).replaceAll("");
        s = NON_ASCII_ALNUM_RUNS.matcher(s).replaceAll("-");
        s = MULTIPLE_DASHES.matcher(s).replaceAll("-");
        s = s.replaceAll("^-+|-+$", "");

        return s;
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