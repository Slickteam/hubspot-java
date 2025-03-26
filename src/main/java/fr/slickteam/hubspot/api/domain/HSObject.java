package fr.slickteam.hubspot.api.domain;

import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.utils.HubSpotHelper;
import kong.unirest.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base type for all HubSpot Object managed through the API.
 * All the properties are saved in a map.
 */
public class HSObject implements Serializable {

    /**
     * The Properties.
     */
    protected Map<String, String> properties = new HashMap<>();

    /**
     * Instantiates a new Hs object.
     */
    public HSObject() {
    }

    /**
     * Instantiates a new Hs object.
     *
     * @param properties the properties
     */
    public HSObject(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Gets properties.
     *
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Add properties hs object.
     *
     * @param properties the properties
     * @return the hs object
     */
    public HSObject addProperties(Map<String, String> properties) {
        properties.forEach(this::setProperty);
        return this;
    }

    /**
     * Sets properties.
     *
     * @param properties the properties
     * @return the properties
     */
    public HSObject setProperties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Sets property.
     *
     * @param property the property
     * @param value    the value
     * @return the property
     */
    public HSObject setProperty(String property, String value) {
        if (value != null && !"null".equals(value)) {
            this.properties.put(property, value);
        }

        return this;
    }

    /**
     * Gets property.
     *
     * @param property the property
     * @return the property
     */
    public String getProperty(String property) {
        return this.properties.get(property);
    }

    /**
     * Gets long property.
     *
     * @param property the property
     * @return the long property
     */
    public long getLongProperty(String property) {
        return Strings.isNullOrEmpty(getProperty(property)) ? 0 : Long.parseLong(getProperty(property)) ;
    }

    /**
     * Gets big decimal property.
     *
     * @param property the property
     * @return the big decimal property
     */
    public BigDecimal getBigDecimalProperty(String property) {
        return Strings.isNullOrEmpty(getProperty(property)) ? BigDecimal.valueOf(0) : new BigDecimal(getProperty(property));
    }

    /**
     * Gets int property.
     *
     * @param property the property
     * @return the int property
     */
    public int getIntProperty(String property) {
        return Integer.parseInt(getProperty(property));
    }

    /**
     * Gets boolean property.
     *
     * @param property the property
     * @return the boolean property
     */
    public boolean getBooleanProperty(String property) {
        return !Strings.isNullOrEmpty(getProperty(property)) && Boolean.parseBoolean(getProperty(property));
    }

    /**
     * Gets date property.
     *
     * @param property the property
     * @return the date property
     */
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

    /**
     * To json string string.
     *
     * @return the string
     */
    public String toJsonString() {
        return toJson().toString();
    }

    /**
     * To json json object.
     *
     * @return the json object
     */
    public JSONObject toJson() {

        Map<String, String> properties = new HashMap<>(getProperties());
        properties.remove("vid");

        return HubSpotHelper.mapPropertiesToJson(properties);
    }

    @Override
    public String toString() {
        return "HSObject{" +
                "properties=" + properties +
                '}';
    }
}
