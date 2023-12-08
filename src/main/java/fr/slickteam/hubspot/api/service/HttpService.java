package fr.slickteam.hubspot.api.service;

import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.HubSpotProperties;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.TRACE;
import static kong.unirest.Unirest.*;

/**
 * Service for handling requests to HubSpot API.
 */
public class HttpService {

    private static final System.Logger log = System.getLogger(HttpService.class.getName());
    public static final String MESSAGE = "message";

    private final String apiBase;
    private final OAuthConfig oAuthConfig;

    /**
     * Instantiates a new Http service.
     *
     * @param properties the properties
     */
    public HttpService(HubSpotProperties properties) {
        this.apiBase = properties.getApiBase();
        this.oAuthConfig = new OAuthConfig(properties);
    }

    /**
     * Parse json data hs object.
     *
     * @param jsonBody the json body
     * @param hsObject the hs object
     * @return the hs object
     */
    public HSObject parseJSONData(JSONObject jsonBody, HSObject hsObject) {
        JSONObject jsonProperties = jsonBody.getJSONObject("properties");

        Set<String> keys = jsonProperties.keySet();

        keys.forEach(key ->
                hsObject.setProperty(key,
                        jsonProperties.get(key) instanceof JSONObject ?
                                ((JSONObject) jsonProperties.get(key)).getString(
                                        "value") :
                                jsonProperties.get(key).toString()
                )
        );
        return hsObject;
    }

    /**
     * Gets request.
     *
     * @param url the url
     * @return the request
     * @throws HubSpotException the hub spot exception
     */
    public Object getRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = get(apiBase + url)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + oAuthConfig.getAccessToken()).asJson();
            if (oauthTokenHasExpired(resp)) {
                refreshToken();
                resp = get(apiBase + url)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + oAuthConfig.getAccessToken()).asJson();
            }
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data\n URL:" + url, e);
        }
    }

    /**
     * Post request object.
     *
     * @param url        the url
     * @param properties the properties
     * @return the object
     * @throws HubSpotException the hub spot exception
     */
    public Object postRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = post(apiBase + url)
                    .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(properties).asJson();
            if (oauthTokenHasExpired(resp)) {
                refreshToken();
                resp = post(apiBase + url)
                        .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .body(properties).asJson();
            }
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make a request: \n" + properties, e);
        }
    }

    /**
     * Patch request object.
     *
     * @param url        the url
     * @param properties the properties
     * @return the object
     * @throws HubSpotException the hub spot exception
     */
    public Object patchRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = patch(apiBase + url)
                    .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(properties).asJson();
            if (oauthTokenHasExpired(resp)) {
                refreshToken();
                resp = patch(apiBase + url)
                        .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .body(properties).asJson();
            }
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make a request: \n" + properties, e);
        }
    }

    /**
     * Put request object.
     *
     * @param url        the url
     * @param properties the properties
     * @return the object
     * @throws HubSpotException the hub spot exception
     */
    public Object putRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = put(apiBase + url)
                    .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(properties).asJson();
            if (oauthTokenHasExpired(resp)) {
                refreshToken();
                resp = put(apiBase + url)
                        .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .body(properties).asJson();
            }
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data", e);
        }
    }

    /**
     * Put request object.
     *
     * @param url the url
     * @return the object
     * @throws HubSpotException the hub spot exception
     */
    public Object putRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = put(apiBase + url)
                    .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json").asJson();
            if (oauthTokenHasExpired(resp)) {
                refreshToken();
                resp = put(apiBase + url)
                        .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json").asJson();
            }
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data", e);
        }
    }

    /**
     * Delete request.
     *
     * @param url the url
     * @throws HubSpotException the hub spot exception
     */
    public void deleteRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = delete(apiBase + url)
                    .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json").asJson();
            if (oauthTokenHasExpired(resp)) {
                refreshToken();
                delete(apiBase + url)
                        .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json").asJson();
            }
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make delete request: \n URL: " + url, e);
        }
    }


    private boolean oauthTokenHasExpired(HttpResponse<JsonNode> resp) {
        return resp.getStatus() == 401
               && "EXPIRED_AUTHENTICATION".equals(resp.getBody()
                .getObject()
                .getString("category"));
    }

    private void refreshToken() throws UnirestException {
        HttpResponse<JsonNode> rest = post(apiBase + "/oauth/v1/token")
                .field("grant_type", "refresh_token")
                .field("client_id", oAuthConfig.getClientId())
                .field("client_secret", oAuthConfig.getClientSecret())
                .field("redirect_uri", oAuthConfig.getRedirectUrl())
                .field("refresh_token", oAuthConfig.getRefreshToken())
                .asJson();
        this.oAuthConfig.setRefreshToken(rest.getBody().getObject().getString("refresh_token"));
        this.oAuthConfig.setAccessToken(rest.getBody().getObject().getString("access_token"));

    }

    private Object checkResponse(HttpResponse<JsonNode> resp) throws HubSpotException {
        if (204 != resp.getStatus() && 200 != resp.getStatus() && 201 != resp.getStatus() && 202 != resp.getStatus()) {
            String message = null;
            try {
                switch (resp.getStatus()) {
                    case 404:
                        message = resp.getStatusText();
                        break;
                    case 207:
                        message = ((List<JSONObject>) ((JSONArray) resp.getBody().getObject().get("errors")).toList()).stream()
                                .map(error -> error.get(MESSAGE))
                                .collect(Collectors.toList())
                                .toString();
                        break;
                    default:
                        resp.getBody().getObject().getString(MESSAGE);
                }

                if (Strings.isNullOrEmpty(message)) {
                    message = resp.getBody().getObject().getString(MESSAGE);
                }

                if (!Strings.isNullOrEmpty(message)) {
                    log.log(ERROR, getHttpErrorMessageAndStatus(resp));
                    throw new HubSpotException(message, resp.getStatus());
                } else {
                    log.log(ERROR, "checkResponse : message is empty");
                    throw new HubSpotException(resp.getStatusText(), resp.getStatus());
                }
            } catch (HubSpotException e) {
                throw e;
            } catch (Exception e) {
                log.log(ERROR, getHttpErrorMessageAndStatus(resp), e);
            }
        } else {
            if (resp.getBody() != null) {
                log.log(TRACE, getHttpErrorMessageAndStatus(resp));
                return resp.getBody().isArray() ? resp.getBody().getArray() : resp.getBody().getObject();
            } else {
                log.log(TRACE, "checkResponse : HTTP status : " + resp.getStatus() +
                               " (" + resp.getStatusText() + ")");
            }
        }
        return null;
    }

    private static String getHttpErrorMessageAndStatus(HttpResponse<JsonNode> resp) {
        return "checkResponse : HTTP status : " + resp.getStatus() +
               " (" + resp.getStatusText() +
               ") | message = " + resp.getBody().toString();
    }
}
