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

import com.google.common.base.Strings;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Hub spot helper.
 */
public class HubSpotHelper {

    /**
     * Gets json object.
     *
     * @param property the property
     * @param value    the value
     * @return the json object
     */
    public static JSONObject getJsonObject(String property, Object value) {
        return new JSONObject()
                .put("property", property)
                .put("value", value);
    }

    /**
     * Put json object.
     *
     * @param ja       the ja
     * @param property the property
     * @param value    the value
     */
    public static void putJsonObject(JSONArray ja, String property, String value) {
        if (!Strings.isNullOrEmpty(value) && !value.equals("null")) {
            ja.put(getJsonObject(property, value));
        }
    }

    /**
     * Put json object json object.
     *
     * @param jo       the jo
     * @param property the property
     * @param value    the value
     * @return the json object
     */
    public static JSONObject putJsonObject(JSONObject jo, String property, Object value) {
        if (!Strings.isNullOrEmpty(value + "") && !value.equals("null")) {
            jo.put(property, value);
        }

        return jo;
    }

    /**
     * Map to json string string.
     *
     * @param map the map
     * @return the string
     */
    public static String mapToJsonString(Map<String, String> map) {
        return mapPropertiesToJson(map).toString();
    }

    /**
     * Map properties to json json object.
     *
     * @param map the map
     * @return the json object
     */
    public static JSONObject mapPropertiesToJson(Map<String, String> map) {
        JSONObject ja = new JSONObject();
        map.forEach((key, value) -> putJsonObject(ja, key, value));

        return new JSONObject().put("properties", ja);
    }

    /**
     * Map filters to json json object.
     *
     * @param map the map
     * @return the json object
     */
    public static JSONObject mapFiltersToJson(Map<String, String> map) {
        JSONObject ja = new JSONObject();
        map.forEach((key, value) -> putJsonObject(ja, key, value));

        List<JSONObject> jsonObjectList = new ArrayList<>();
        jsonObjectList.add(ja);

        return new JSONObject().put("filters", jsonObjectList);
    }
}
