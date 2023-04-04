package fr.slickteam.hubspot.api;

public abstract class HubSpot {
    public static volatile String apiKey;
    private static volatile String apiBase = "http://api.hubapi.com";

    public HubSpot() {
    }

    public static String getApiBase() {
        return apiBase;
    }
}
