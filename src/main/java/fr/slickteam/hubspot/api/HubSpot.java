package fr.slickteam.hubspot.api;

/**
 * The type Hub spot.
 */
public abstract class HubSpot {
    /**
     * The constant apiKey.
     */
    public static volatile String apiKey;
    private static volatile String apiBase = "http://api.hubapi.com";

    /**
     * Instantiates a new Hub spot.
     */
    public HubSpot() {
    }

    /**
     * Gets api base.
     *
     * @return the api base
     */
    public static String getApiBase() {
        return apiBase;
    }
}
