package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONObject;

import java.util.Optional;
import java.util.Set;

public class HSService {

    private final HttpService httpService;

    public HSService(HttpService httpService) {
        this.httpService = httpService;
    }

    public HSObject parseJSONData(JSONObject jsonBody, HSObject hsObject) {
        JSONObject jsonProperties = jsonBody.getJSONObject("properties");

        Set<String> keys = jsonProperties.keySet();

        keys.forEach(key ->
                hsObject.setProperty(key,
                        jsonProperties.get(key) instanceof JSONObject ?
                                ((JSONObject) jsonProperties.get(key)).getString(
                                        "value") :
                                Optional.ofNullable(jsonProperties.get(key))
                                        .map(Object::toString)
                                        .orElse(null)
                )
        );
        return hsObject;
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
