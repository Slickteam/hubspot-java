package fr.slickteam.hubspot.api.utils;

public final class StringUtils {

    private StringUtils() {
        // default constructor
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
