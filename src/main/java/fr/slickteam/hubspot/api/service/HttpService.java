package fr.slickteam.hubspot.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.HubSpotProperties;
import fr.slickteam.hubspot.api.utils.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.TRACE;

/**
 * Service for handling requests to HubSpot API.
 */
public class HttpService {

    private static final System.Logger log = System.getLogger(HttpService.class.getName());
    private static final String MESSAGE = "message";

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
     * @param jsonNode the json node
     * @param hsObject the hs object
     * @return the hs object
     */
    public HSObject parseJSONData(JsonNode jsonNode, HSObject hsObject) {
        JsonNode jsonProperties = jsonNode.get("properties");
        if (jsonProperties != null && jsonProperties.isObject()) {
            jsonProperties.fieldNames().forEachRemaining(key -> {
                JsonNode value = jsonProperties.get(key);
                if (value.isObject() && value.has("value")) {
                    hsObject.setProperty(key, value.get("value").asText());
                } else {
                    hsObject.setProperty(key, value.asText());
                }
            });
        }
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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiBase + url);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
            
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }
                
                if (response.getStatusLine().getStatusCode() == 401 && responseBody != null && 
                    oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpGet.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
                    response.close();
                    return executeRequest(httpClient, httpGet);
                }
                return processResponseFromBody(response, responseBody);
            }
        } catch (IOException e) {
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
    public JsonNode postRequest(String url, String properties) throws HubSpotException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiBase + url);
            httpPost.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(properties, ContentType.APPLICATION_JSON));
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }
                
                if (response.getStatusLine().getStatusCode() == 401 && responseBody != null && 
                    oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPost.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
                    response.close();
                    return executeRequest(httpClient, httpPost);
                }
                return processResponseFromBody(response, responseBody);
            }
        } catch (IOException e) {
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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch httpPatch = new HttpPatch(apiBase + url);
            httpPatch.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
            httpPatch.setHeader("Accept", "application/json");
            httpPatch.setHeader("Content-Type", "application/json");
            httpPatch.setEntity(new StringEntity(properties, ContentType.APPLICATION_JSON));
            
            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }
                
                if (response.getStatusLine().getStatusCode() == 401 && responseBody != null && 
                    oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPatch.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
                    response.close();
                    return executeRequest(httpClient, httpPatch);
                }
                return processResponseFromBody(response, responseBody);
            }
        } catch (IOException e) {
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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(apiBase + url);
            httpPut.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setEntity(new StringEntity(properties, ContentType.APPLICATION_JSON));
            
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }
                
                if (response.getStatusLine().getStatusCode() == 401 && responseBody != null && 
                    oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPut.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
                    response.close();
                    return executeRequest(httpClient, httpPut);
                }
                return processResponseFromBody(response, responseBody);
            }
        } catch (IOException e) {
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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(apiBase + url);
            httpPut.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-Type", "application/json");
            
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }
                
                if (response.getStatusLine().getStatusCode() == 401 && responseBody != null && 
                    oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPut.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
                    response.close();
                    return executeRequest(httpClient, httpPut);
                }
                return processResponseFromBody(response, responseBody);
            }
        } catch (IOException e) {
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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(apiBase + url);
            httpDelete.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
            httpDelete.setHeader("Accept", "application/json");
            httpDelete.setHeader("Content-Type", "application/json");
            
            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                if (oauthTokenHasExpired(response)) {
                    refreshToken();
                    httpDelete.setHeader("Authorization", "Bearer " + oAuthConfig.getAccessToken());
                    response.close();
                    httpClient.execute(httpDelete).close(); // Execute and close immediately since we don't need the response
                }
            }
        } catch (IOException e) {
            throw new HubSpotException("Cannot make delete request: \n URL: " + url, e);
        }
    }


    private boolean oauthTokenHasExpired(CloseableHttpResponse response) {
        if (response.getStatusLine().getStatusCode() == 401) {
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    return oauthTokenHasExpiredFromBody(responseBody);
                }
            } catch (Exception e) {
                log.log(ERROR, "Error checking if OAuth token has expired", e);
            }
        }
        return false;
    }

    private boolean oauthTokenHasExpiredFromBody(String responseBody) {
        try {
            JsonNode jsonNode = JsonUtils.parseJson(responseBody);
            return jsonNode.has("category") && "EXPIRED_AUTHENTICATION".equals(jsonNode.get("category").asText());
        } catch (Exception e) {
            log.log(ERROR, "Error parsing response body to check if OAuth token has expired", e);
            return false;
        }
    }

    private void refreshToken() throws HubSpotException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiBase + "/oauth/v1/token");
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "refresh_token"));
            params.add(new BasicNameValuePair("client_id", oAuthConfig.getClientId()));
            params.add(new BasicNameValuePair("client_secret", oAuthConfig.getClientSecret()));
            params.add(new BasicNameValuePair("redirect_uri", oAuthConfig.getRedirectUrl()));
            params.add(new BasicNameValuePair("refresh_token", oAuthConfig.getRefreshToken()));
            
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    JsonNode jsonNode = JsonUtils.parseJson(responseBody);
                    
                    this.oAuthConfig.setRefreshToken(jsonNode.get("refresh_token").asText());
                    this.oAuthConfig.setAccessToken(jsonNode.get("access_token").asText());
                }
            }
        } catch (IOException e) {
            throw new HubSpotException("Failed to refresh token", e);
        }
    }

    private JsonNode processResponse(CloseableHttpResponse response) throws HubSpotException, IOException {
        HttpEntity entity = response.getEntity();
        String responseBody = null;
        if (entity != null) {
            responseBody = EntityUtils.toString(entity);
        }
        return processResponseFromBody(response, responseBody);
    }

    private JsonNode processResponseFromBody(CloseableHttpResponse response, String responseBody) throws HubSpotException {
        int statusCode = response.getStatusLine().getStatusCode();
        String statusText = response.getStatusLine().getReasonPhrase();
        
        if (statusCode != HttpStatus.SC_NO_CONTENT && statusCode != HttpStatus.SC_OK && 
            statusCode != HttpStatus.SC_CREATED && statusCode != HttpStatus.SC_ACCEPTED) {
            
            String message = null;
            
            if (responseBody != null) {
                JsonNode jsonNode = JsonUtils.parseJson(responseBody);
                
                try {
                    switch (statusCode) {
                        case HttpStatus.SC_NOT_FOUND:
                            message = statusText;
                            break;
                        case 207: // Multi-Status
                            if (jsonNode.has("errors") && jsonNode.get("errors").isArray()) {
                                message = StreamSupport.stream(jsonNode.get("errors").spliterator(), false)
                                    .map(error -> error.has(MESSAGE) ? error.get(MESSAGE).asText() : "")
                                    .collect(Collectors.toList())
                                    .toString();
                            }
                            break;
                        default:
                            if (jsonNode.has(MESSAGE)) {
                                message = jsonNode.get(MESSAGE).asText();
                            }
                    }
                    
                    if (Strings.isNullOrEmpty(message) && jsonNode.has(MESSAGE)) {
                        message = jsonNode.get(MESSAGE).asText();
                    }
                    
                    if (!Strings.isNullOrEmpty(message)) {
                        log.log(ERROR, getHttpErrorMessageAndStatus(response, responseBody));
                        throw new HubSpotException(message, statusCode);
                    } else {
                        log.log(ERROR, "checkResponse : message is empty");
                        throw new HubSpotException(statusText, statusCode);
                    }
                } catch (HubSpotException e) {
                    throw e;
                } catch (Exception e) {
                    log.log(ERROR, getHttpErrorMessageAndStatus(response, responseBody), e);
                }
            }
        } else {
            if (responseBody != null) {
                log.log(TRACE, getHttpErrorMessageAndStatus(response, responseBody));
                return JsonUtils.parseJson(responseBody);
            } else {
                log.log(TRACE, "checkResponse : HTTP status : " + statusCode + " (" + statusText + ")");
            }
        }
        return null;
    }
    
    private JsonNode executeRequest(CloseableHttpClient httpClient, HttpRequestBase request) throws IOException, HubSpotException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return processResponse(response);
        }
    }

    private static String getHttpErrorMessageAndStatus(CloseableHttpResponse response, String responseBody) {
        return "checkResponse : HTTP status : " + response.getStatusLine().getStatusCode() +
               " (" + response.getStatusLine().getReasonPhrase() +
               ") | message = " + responseBody;
    }
}
