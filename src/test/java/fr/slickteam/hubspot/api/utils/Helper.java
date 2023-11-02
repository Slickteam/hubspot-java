package fr.slickteam.hubspot.api.utils;

import fr.slickteam.hubspot.api.service.HttpService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Helper {

    private static final System.Logger log = System.getLogger(Helper.class.getName());

    public static String getProperty(String key) {
        Properties p = new Properties();
        try {
            p.load(new FileReader("src//test//resources//config.properties"));
        } catch (IOException e) {
            log.log(System.Logger.Level.ERROR, "Error loading properties file: " + e.getMessage());
        }

        return p.getProperty(key);
    }

    public static HubSpotProperties provideHubspotProperties() throws IOException {
        HubSpotProperties hubSpotProperties = new HubSpotProperties();

        hubSpotProperties.setApiBase(HubSpotPropertiesHelper.loadPropertyValue("apiBase"));
        hubSpotProperties.setAccessToken(HubSpotPropertiesHelper.loadPropertyValue("accessToken"));
        hubSpotProperties.setClientId(HubSpotPropertiesHelper.loadPropertyValue("clientId"));
        hubSpotProperties.setClientSecret(HubSpotPropertiesHelper.loadPropertyValue("clientSecret"));
        hubSpotProperties.setRedirectUrl(HubSpotPropertiesHelper.loadPropertyValue("redirectUrl"));
        hubSpotProperties.setRefreshToken(HubSpotPropertiesHelper.loadPropertyValue("refreshToken"));

        return hubSpotProperties;
    }
}
