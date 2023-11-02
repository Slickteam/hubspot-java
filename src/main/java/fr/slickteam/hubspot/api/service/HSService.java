package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HSService {

    private final HttpService httpService;

    public HSService(HttpService httpService) {
        this.httpService = httpService;
    }

    public HSObject parseJSONData(JSONObject jsonBody, HSObject hsObject) {
        JSONObject jsonProperties;
        if(jsonBody.has("properties")) {
            jsonProperties = jsonBody.getJSONObject("properties");
        } else {
            jsonProperties = jsonBody;
        }

        Set<String> keys = jsonProperties.keySet();

        keys.forEach(key ->
                hsObject.setProperty(key,
                        jsonProperties.get(key) instanceof JSONObject && ((JSONObject) jsonProperties.get(key)).has("value")?
                                ((JSONObject) jsonProperties.get(key)).getString(
                                        "value") :
                                Optional.ofNullable(jsonProperties.get(key))
                                        .map(Object::toString)
                                        .orElse(null)
                )
        );
        return hsObject;
    }


    /**
     * Parse a Json object to a list of long Ids
     **/
    public List<Long> parseJsonObjectToIdList(String url) throws HubSpotException {
        JSONObject requestResponse = (JSONObject) httpService.getRequest(url);
        JSONArray results = (JSONArray) requestResponse.get("results");

        return IntStream.range(0, results.length())
                .mapToObj(results::getJSONObject)
                .map(resultObj -> Long.valueOf(resultObj.get("toObjectId").toString()))
                .collect(Collectors.toList());
    }

    public List<JSONObject> parseJsonResultToList (String url) throws HubSpotException {
        JSONObject requestResponse = (JSONObject) httpService.getRequest(url);
        JSONArray results = (JSONArray) requestResponse.get("results");
        return IntStream.range(0, results.length())
                .mapToObj(results::getJSONObject)
                .collect(Collectors.toList());
    }

    public List<JSONObject> parsePostJsonResultToList (String url, String body) throws HubSpotException {
        JSONObject requestResponse = (JSONObject) httpService.postRequest(url, body);
        JSONArray results = (JSONArray) requestResponse.get("results");
        return IntStream.range(0, results.length())
                .mapToObj(results::getJSONObject)
                .collect(Collectors.toList());
    }

    public HSObject getHSObject(String url) throws HubSpotException {
        try {
            HSObject object = new HSObject();
            parseJSONData((JSONObject) httpService.getRequest(url), object);
            return object;
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public HSObject createHSObject(String url, HSObject hsObject) throws HubSpotException {
        HSObject result = new HSObject();
        JSONObject jsonObject = (JSONObject) httpService.postRequest(url, hsObject.toJsonString());
        parseJSONData(jsonObject, result);

        return result;
    }

    public HSObject patchHSObject(HSObject object, String url) throws HubSpotException {
        String properties = object.toJsonString();

        try {
            HSObject result = new HSObject();
            JSONObject jsonObject = (JSONObject) httpService.patchRequest(url, properties);
            parseJSONData(jsonObject, result);

            return result;
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update object: " + object + ". Reason: " + e.getMessage(), e);
        }
    }

    public void deleteHSObject(String url) throws HubSpotException {
        httpService.deleteRequest(url);
    }
}
