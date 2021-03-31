/*
 * Copyright:
 *
 *  developer: Sergei Dubinin
 *  e-mail: sdubininit@gmail.com
 *  date: 14.10.2015 9:58
 *  
 *  copyright (c) integrationagent.com
 */

package fr.slickteam.hubspotApi.utils;

import com.google.common.base.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HubSpotHelper {

    public static JSONObject getJsonObject(String property, Object value) {
        return new JSONObject()
                .put("property", property)
                .put("value", value);
    }

    public static void putJsonObject(JSONArray ja, String property, String value){
        if(!Strings.isNullOrEmpty(value) && !value.equals("null")){
            ja.put(getJsonObject(property, value));
        }
    }

    public static JSONObject putJsonObject(JSONObject jo, String property, Object value){
        if(!Strings.isNullOrEmpty(value + "") && !value.equals("null")){
            jo.put(property, value);
        }

        return jo;
    }

    public static String mapToJsonString(Map<String, String> map) {
        return mapPropertiesToJson(map).toString();
    }

    public static JSONObject mapPropertiesToJson(Map<String, String> map) {
        JSONObject ja = new JSONObject();
        map.forEach((key, value) -> putJsonObject(ja, key, value));

        return new JSONObject().put("properties", ja);
    }

    public static JSONObject mapFiltersToJson(Map<String, String> map) {
        JSONObject ja = new JSONObject();
        map.forEach((key, value) -> putJsonObject(ja, key, value));

        List<JSONObject> jsonObjectList = new ArrayList<>();
        jsonObjectList.add(ja);

        return new JSONObject().put("filters", jsonObjectList);
    }
}
