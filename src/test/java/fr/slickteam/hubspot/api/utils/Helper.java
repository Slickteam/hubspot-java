package fr.slickteam.hubspot.api.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Helper {

    public static String getProperty(String key) {
        Properties p = new Properties();
        try {
            p.load(new FileReader(new File("src//test//resources//config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String val = p.getProperty(key);
        return val;
    }

    public static HubSpotProperties provideHubspotProperties() throws IOException {
        HubSpotProperties hubSpotProperties = new HubSpotProperties();

        hubSpotProperties.setApiBase(HubSpotPropertiesHelper.loadPropertyValue("hubspot.apiBase"));
        hubSpotProperties.setAccessToken(HubSpotPropertiesHelper.loadPropertyValue("hubspot.accessToken"));
        hubSpotProperties.setClientId(HubSpotPropertiesHelper.loadPropertyValue("hubspot.clientId"));
        hubSpotProperties.setClientSecret(HubSpotPropertiesHelper.loadPropertyValue("hubspot.clientSecret"));
        hubSpotProperties.setRedirectUrl(HubSpotPropertiesHelper.loadPropertyValue("hubspot.redirectUrl"));
        hubSpotProperties.setRefreshToken(HubSpotPropertiesHelper.loadPropertyValue("hubspot.refreshToken"));

        return hubSpotProperties;
    }
}
