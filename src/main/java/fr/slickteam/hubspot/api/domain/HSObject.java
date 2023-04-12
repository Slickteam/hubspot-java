package fr.slickteam.hubspot.api.domain;

import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.utils.HubSpotHelper;
import kong.unirest.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class HSObject {
    protected Map<String, String> properties = new HashMap<>();

    public HSObject() {
    }

    public HSObject(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public HSObject addProperties(Map<String, String> properties) {
        properties.forEach(this::setProperty);
        return this;
    }

    public HSObject setProperties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    public HSObject setProperty(String property, String value) {
        if (value != null && !"null".equals(value)) {
            this.properties.put(property, value);
        }

        return this;
    }

    public String getProperty(String property) {
        return this.properties.get(property);
    }

    public long getLongProperty(String property) {
        return !Strings.isNullOrEmpty(getProperty(property)) ? Long.parseLong(getProperty(property)) : 0;
    }

    public BigDecimal getBigDecimalProperty(String property) {
        return !Strings.isNullOrEmpty(getProperty(property)) ? new BigDecimal(getProperty(property)) : BigDecimal.valueOf(0);
    }

    public Date getBigDecimalProperty(String property) {
        return !Strings.isNullOrEmpty(getProperty(property)) ? new BigDecimal(getProperty(property)) : BigDecimal.valueOf(0);
    }

    public long getDateProperty(Date property) {
        return !Strings.isNullOrEmpty(getProperty(property)) ? new Date(getProperty(property)) : new Date();
    }

    public String toJsonString() {
        return toJson().toString();
    }

    public JSONObject toJson() {

        Map<String, String> properties = new HashMap<>(getProperties());
        properties.remove("vid");

        return HubSpotHelper.mapPropertiesToJson(properties);
    }
}
