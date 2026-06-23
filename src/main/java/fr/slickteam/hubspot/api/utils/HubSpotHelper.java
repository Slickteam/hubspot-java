/*
 * Copyright:
 *
 *  developer: Sergei Dubinin
 *  e-mail: sdubininit@gmail.com
 *  date: 14.10.2015 9:58
 *
 *  copyright (c) integrationagent.com
 */

package fr.slickteam.hubspot.api.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.Objects;

/**
 * The type Hub spot helper.
 */
public class HubSpotHelper {

    /**
     * Gets json object node.
     *
     * @param property the property
     * @param value    the value
     * @return the json object node
     */
    public static ObjectNode getJsonObject(String property, Object value) {
        ObjectNode node = JsonUtils.createObjectNode();
        node.put("property", property);
        if (value instanceof String) {
            node.put("value", (String) value);
        } else if (value instanceof Number) {
            node.put("value", value.toString());
        } else if (value instanceof Boolean) {
            node.put("value", (Boolean) value);
        } else if (value != null) {
            node.put("value", value.toString());
        }
        return node;
    }

    /**
     * Put json object.
     *
     * @param array    the array node
     * @param property the property
     * @param value    the value
     */
    public static void putJsonObject(ArrayNode array, String property, String value) {
        if (!StringUtils.isNullOrEmpty(value) && !value.equals("null")) {
            array.add(getJsonObject(property, value));
        }
    }

    /**
     * Put json object object node.
     *
     * @param node     the object node
     * @param property the property
     * @param value    the value
     * @return the object node
     */
    public static ObjectNode putJsonObject(ObjectNode node, String property, Object value) {
        if (!StringUtils.isNullOrEmpty(value + "") && !value.equals("null")) {
            if (value instanceof String val) {
                node.put(property, val);
            } else if (value instanceof Number val) {
                node.put(property, val.toString());
            } else if (value instanceof Boolean val) {
                node.put(property, val);
            }
        }
        return node;
    }

    /**
     * Map to json string string.
     *
     * @param map the map
     * @return the string
     * @throws HubSpotException if JSON conversion fails
     */
    public static String mapToJsonString(Map<String, String> map) throws HubSpotException {
        return JsonUtils.toJson(mapPropertiesToJson(map));
    }

    /**
     * Map properties to json object node.
     *
     * @param map the map
     * @return the object node
     */
    public static ObjectNode mapPropertiesToJson(Map<String, String> map) {
        ObjectNode properties = JsonUtils.createObjectNode();
        map.forEach((key, value) -> putJsonObject(properties, key, value));

        ObjectNode rootNode = JsonUtils.createObjectNode();
        rootNode.set("properties", properties);
        return rootNode;
    }

    /**
     * Map filters to json object node.
     *
     * @param map the map
     * @return the object node
     */
    public static ObjectNode mapFiltersToJson(Map<String, String> map) {
        ObjectNode filterNode = JsonUtils.createObjectNode();
        map.forEach((key, value) -> putJsonObject(filterNode, key, value));

        ArrayNode filtersArray = JsonUtils.createArrayNode();
        filtersArray.add(filterNode);

        ObjectNode rootNode = JsonUtils.createObjectNode();
        rootNode.set("filters", filtersArray);
        return rootNode;
    }
}
