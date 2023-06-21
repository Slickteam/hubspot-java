package fr.slickteam.hubspot.api.domain;

import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.utils.HubSpotHelper;
import kong.unirest.json.JSONObject;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

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
        return Strings.isNullOrEmpty(getProperty(property)) ? 0 : Long.parseLong(getProperty(property)) ;
    }

    public BigDecimal getBigDecimalProperty(String property) {
        return Strings.isNullOrEmpty(getProperty(property)) ? BigDecimal.valueOf(0) : new BigDecimal(getProperty(property));
    }

    public int getIntProperty(String property) {
        return Strings.isNullOrEmpty(getProperty(property)) ? 0 : Integer.parseInt(getProperty(property));
    }

    public boolean getBooleanProperty(String property) {
        return !Strings.isNullOrEmpty(getProperty(property)) && Boolean.parseBoolean(getProperty(property));
    }

    public Instant getDateProperty(String property) {
        if (Strings.isNullOrEmpty(getProperty(property))) {
            return Instant.now();
        } else {
            try {
                return Instant.parse(getProperty(property));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid Instant value for property " + property);
            }
        }
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
