package fr.slickteam.hubspot.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.JsonUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * The type Hs service.
 */
public class HSService {

    private final HttpService httpService;

    /**
     * Instantiates a new Hs service.
     *
     * @param httpService the http service
     */
    public HSService(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * Parse json data hs object.
     *
     * @param jsonBody the json body
     * @param hsObject the hs object
     * @return the hs object
     */
    public HSObject parseJSONData(JsonNode jsonBody, HSObject hsObject) {
        JsonNode jsonProperties;
        if (jsonBody.has("properties")) {
            jsonProperties = jsonBody.path("properties");
        } else {
            jsonProperties = jsonBody;
        }

        Map<String, JsonNode> jsonOrderedFields = new TreeMap<>();
        jsonProperties.fieldNames()
                      .forEachRemaining(name -> jsonOrderedFields.put(name, jsonProperties.path(name)));

        // Process the fields in sorted order
        jsonOrderedFields.forEach((name, value) -> {
            if (value.isObject() && value.has("value")) {
                hsObject.setProperty(name, value.path("value").asText());
            } else if (value.isArray()) {
                hsObject.setProperty(name, value.toString());
            } else {
                hsObject.setProperty(name, value.isNull() ? null : value.asText());
            }
        });
        
        return hsObject;
    }


    /**
     * Parse a Json object to a list of long Ids
     *
     * @param url the url
     * @return the list
     * @throws HubSpotException the hub spot exception
     */
    public List<Long> parseJsonObjectToIdList(String url) throws HubSpotException {
        JsonNode requestResponse = (JsonNode) httpService.getRequest(url);
        JsonNode results = requestResponse.path("results");

        return StreamSupport.stream(results.spliterator(), false)
                .map(resultObj -> Long.valueOf(resultObj.path("toObjectId").asText()))
                .collect(Collectors.toList());
    }

    /**
     * Parse json result to list list.
     *
     * @param url the url
     * @return the list
     * @throws HubSpotException the hub spot exception
     */
    public List<JsonNode> parseJsonResultToList (String url) throws HubSpotException {
        JsonNode requestResponse = (JsonNode) httpService.getRequest(url);
        JsonNode results = requestResponse.path("results");
        return StreamSupport.stream(results.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Parse post json result to list list.
     *
     * @param url  the url
     * @param body the body
     * @return the list
     * @throws HubSpotException the hub spot exception
     */
    public List<JsonNode> parsePostJsonResultToList (String url, String body) throws HubSpotException {
        JsonNode requestResponse = (JsonNode) httpService.postRequest(url, body);
        JsonNode results = requestResponse.path("results");
        return StreamSupport.stream(results.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Gets hs object.
     *
     * @param url the url
     * @return the hs object
     * @throws HubSpotException the hub spot exception
     */
    public HSObject getHSObject(String url) throws HubSpotException {
        try {
            HSObject object = new HSObject();
            parseJSONData((JsonNode) httpService.getRequest(url), object);
            return object;
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Create hs object hs object.
     *
     * @param url      the url
     * @param hsObject the hs object
     * @return the hs object
     * @throws HubSpotException the hub spot exception
     */
    public HSObject createHSObject(String url, HSObject hsObject) throws HubSpotException {
        HSObject result = new HSObject();
        JsonNode jsonNode = (JsonNode) httpService.postRequest(url, hsObject.toJsonString());
        parseJSONData(jsonNode, result);

        return result;
    }

    /**
     * Patch hs object hs object.
     *
     * @param object the object
     * @param url    the url
     * @return the hs object
     * @throws HubSpotException the hub spot exception
     */
    public HSObject patchHSObject(HSObject object, String url) throws HubSpotException {
        String properties = object.toJsonString();

        try {
            HSObject result = new HSObject();
            JsonNode jsonNode = (JsonNode) httpService.patchRequest(url, properties);
            parseJSONData(jsonNode, result);

            return result;
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update object: " + object + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Delete hs object.
     *
     * @param url the url
     * @throws HubSpotException the hub spot exception
     */
    public void deleteHSObject(String url) throws HubSpotException {
        httpService.deleteRequest(url);
    }

    /**
     * Parses a JSON response to extract a list of JSON nodes based on a specified key.
     * If the specified key points to an array, all elements in the array are returned as a list.
     * If the specified key points to a single object, that object is returned as a single-element list.
     *
     * @param response the JSON response object to be parsed
     * @param results the key in the JSON response indicating the desired data
     * @return a list of JSON nodes extracted from the response
     */
    public List<JsonNode> parseJSONResults(JsonNode response, String results) {
        JsonNode resultsNode = response.path(results);
        if (resultsNode.isArray()) {
            return StreamSupport.stream(resultsNode.spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            return List.of(resultsNode);
        }
    }
}
