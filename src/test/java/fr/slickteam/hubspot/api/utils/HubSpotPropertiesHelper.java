/*
 * Copyright:
 *
 *  developer: Sergei Dubinin
 *  e-mail: sdubininit@gmail.com
 *  date: 14.10.2015 9:58
 *  
 *  copyright (c) integrationagent.com
 */

package fr.slickteam.hubspot.api.utils;

import java.io.IOException;
import java.util.Properties;

public class HubSpotPropertiesHelper {

    public static Properties loadProperties() throws IOException {
        Properties res = new Properties();
        res.load(HubSpotPropertiesHelper.class.getClassLoader().getResourceAsStream("config.properties"));
        return res;
    }

    public static String loadPropertyValue(String propertyName) throws IOException {
        String valueFromFile = loadProperties().getProperty(propertyName);
        if (valueFromFile == null || (valueFromFile.isBlank() || valueFromFile.equals("..."))) {
            return System.getenv(propertyName);
        }
        return valueFromFile;
    }
}
