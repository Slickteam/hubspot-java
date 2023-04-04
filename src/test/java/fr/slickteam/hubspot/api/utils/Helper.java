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

        hubSpotProperties.setApiBase(HubSpotPropertiesHelper.loadPropertyValue("apiBase"));
        hubSpotProperties.setAccessToken(HubSpotPropertiesHelper.loadPropertyValue("accessToken"));
        hubSpotProperties.setClientId(HubSpotPropertiesHelper.loadPropertyValue("clientId"));
        hubSpotProperties.setClientSecret(HubSpotPropertiesHelper.loadPropertyValue("clientSecret"));
        hubSpotProperties.setRedirectUrl(HubSpotPropertiesHelper.loadPropertyValue("redirectUrl"));
        hubSpotProperties.setRefreshToken(HubSpotPropertiesHelper.loadPropertyValue("refreshToken"));

        return hubSpotProperties;
    }
}
