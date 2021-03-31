package fr.slickteam.hubspotApi.service;

import com.google.common.base.Strings;
import com.mashape.unirest.request.BaseRequest;
import fr.slickteam.hubspotApi.domain.HSObject;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fr.slickteam.hubspotApi.utils.HubSpotProperties;
import org.json.JSONObject;

import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;

public class HttpService {

    private final String apiBase;
    private final OAuthConfig oAuthConfig;

    public HttpService(HubSpotProperties properties) {
        this.apiBase = properties.getApiBase();
        this.oAuthConfig = new OAuthConfig(properties);
    }

    public HSObject parseJSONData(JSONObject jsonBody, HSObject hsObject) {
        JSONObject jsonProperties = jsonBody.getJSONObject("properties");

        Set<String> keys = jsonProperties.keySet();

        keys.stream().forEach(key ->
                                      hsObject.setProperty(key,
                                                          jsonProperties.get(key) instanceof JSONObject ?
                                                                  ((JSONObject) jsonProperties.get(key)).getString(
                                                                          "value") :
                                                                  jsonProperties.get(key).toString()
                                      )
        );
        return hsObject;
    }

    public Object getRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = executeRequest(url, null,
                                                         (a, b) -> Unirest.get(apiBase + url)
                                                                          .header("Accept", "application/json")
                                                                          .header("Content-Type", "application/json")
                                                                          .header("Authorization", "Bearer " + oAuthConfig.getAccessToken()));
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data\n URL:" + url, e);
        }
    }

    public Object postRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = executeRequest(url, properties,
                                                         (a, b) -> Unirest.post(apiBase + url)
                                                                          .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                                                                          .header("accept", "application/json")
                                                                          .header("Content-Type", "application/json")
                                                                          .body(properties));
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make a request: \n" + properties, e);
        }
    }

    public Object patchRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = executeRequest(url, properties,
                                                         (a, b) -> Unirest
                                                                 .patch(apiBase + url)
                                                                 .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                                                                 .header("accept", "application/json")
                                                                 .header("Content-Type", "application/json")
                                                                 .body(properties));

            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make a request: \n" + properties, e);
        }
    }

    public Object putRequest(String url, String properties) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = executeRequest(url, properties,
                                                         (a, b) -> Unirest
                                                                 .put(apiBase + url)
                                                                 .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                                                                 .header("accept", "application/json")
                                                                 .header("Content-Type", "application/json")
                                                                 .body(properties));
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data", e);
        }
    }

    public Object putRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = executeRequest(url, null,
                                                         (a, b) -> Unirest
                                                                 .put(apiBase + url)
                                                                 .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                                                                 .header("accept", "application/json")
                                                                 .header("Content-Type", "application/json"));
            return checkResponse(resp);
        } catch (UnirestException e) {
            throw new HubSpotException("Can not get data", e);
        }
    }

    public void deleteRequest(String url) throws HubSpotException {
        try {
            HttpResponse<JsonNode> resp = executeRequest(url, null,
                                                         (a, b) -> Unirest.delete(apiBase + url)
                                                                          .header("Authorization", "Bearer " + oAuthConfig.getAccessToken())
                                                                          .header("accept", "application/json")
                                                                          .header("Content-Type", "application/json"));
        } catch (UnirestException e) {
            throw new HubSpotException("Cannot make delete request: \n URL: " + url, e);
        }
    }

    private HttpResponse<JsonNode> executeRequest(String url, String body,
                                                  BiFunction<String, String, BaseRequest> createRequestFunction) throws UnirestException {
        HttpResponse<JsonNode> resp = createRequestFunction.apply(url, body).asJson();
        if (oauthTokenHasExpired(resp)) {
            refreshToken();
            resp = createRequestFunction.apply(url, body).asJson();
        }
        return resp;
    }


    private boolean oauthTokenHasExpired(HttpResponse<JsonNode> resp) {
        return resp.getStatus() == 401
                && "EXPIRED_AUTHENTICATION".equals(resp.getBody()
                       .getObject()
                       .getString("category"));
    }

    private void refreshToken() throws UnirestException {
        HttpResponse<JsonNode> rest = Unirest.post(apiBase + "/oauth/v1/token")
                                             .field("grant_type", "refresh_token")
                                             .field("client_id", oAuthConfig.getClientId())
                                             .field("client_secret", oAuthConfig.getClientSecret() )
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
                message = (resp.getStatus() == 404) ? resp.getStatusText() : resp
                        .getBody()
                        .getObject()
                        .getString("message");
            } catch (Exception e) {
            }

            if (!Strings.isNullOrEmpty(message)) {
                throw new HubSpotException(message, resp.getStatus());
            } else {
                throw new HubSpotException(resp.getStatusText(), resp.getStatus());
            }
        } else {
            if (resp.getBody() != null) {
                return resp.getBody().isArray() ? resp.getBody().getArray() : resp.getBody().getObject();
            } else {
                return null;
            }
        }
    }
}
