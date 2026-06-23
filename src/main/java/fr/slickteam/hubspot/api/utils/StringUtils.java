package fr.slickteam.hubspot.api.utils;

/**
 * Utility class for string operations.
 */
public final class StringUtils {

    private StringUtils() {
        // default constructor
    }

    /**
     * Check if a string is null or empty.
     *
     * @param str the string to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
