package fr.slickteam.hubspot.api.service;

import tools.jackson.databind.JsonNode;
import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.HubSpotProperties;
import fr.slickteam.hubspot.api.utils.JsonUtils;
import fr.slickteam.hubspot.api.utils.StringUtils;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.TRACE;

/**
 * Service for handling requests to HubSpot API.
 */
public class HttpService {

    private static final System.Logger log = System.getLogger(HttpService.class.getName());
    private static final String MESSAGE = "message";
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String APPLICATION_JSON = "application/json";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ERRORS = "errors";

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
            jsonProperties.propertyNames().iterator().forEachRemaining(key -> {
                JsonNode value = jsonProperties.get(key);
                if (value.isObject() && value.has("value")) {
                    hsObject.setProperty(key, value.get("value").asString());
                } else if (value.isObject() || value.isArray()) {
                    hsObject.setProperty(key, value.toString());
                } else {
                    hsObject.setProperty(key, value.isNull() ? null : value.asString());
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
            httpGet.setHeader(ACCEPT, APPLICATION_JSON);
            httpGet.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            httpGet.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }

                if (response.getCode() == 401 && responseBody != null &&
                        oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpGet.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
                    return executeRequest(httpClient, httpGet);
                }
                return processResponseFromBody(response, responseBody);
            } catch (ParseException e) {
                throw new HubSpotException("Can not parse response body", e);
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
            httpPost.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
            httpPost.setHeader(ACCEPT, APPLICATION_JSON);
            httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            httpPost.setEntity(new StringEntity(properties, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }

                if (response.getCode() == 401 && responseBody != null &&
                        oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPost.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
                    return executeRequest(httpClient, httpPost);
                }
                return processResponseFromBody(response, responseBody);
            } catch (ParseException e) {
                throw new HubSpotException("Can not parse response body", e);
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
            httpPatch.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
            httpPatch.setHeader(ACCEPT, APPLICATION_JSON);
            httpPatch.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            httpPatch.setEntity(new StringEntity(properties, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }

                if (response.getCode() == 401 && responseBody != null &&
                        oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPatch.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
                    return executeRequest(httpClient, httpPatch);
                }
                return processResponseFromBody(response, responseBody);
            } catch (ParseException e) {
                throw new HubSpotException("Can not parse response body", e);
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
            httpPut.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
            httpPut.setHeader(ACCEPT, APPLICATION_JSON);
            httpPut.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            httpPut.setEntity(new StringEntity(properties, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }

                if (response.getCode() == 401 && responseBody != null &&
                        oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPut.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
                    return executeRequest(httpClient, httpPut);
                }
                return processResponseFromBody(response, responseBody);
            } catch (ParseException e) {
                throw new HubSpotException("Can not parse response body", e);
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
            httpPut.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
            httpPut.setHeader(ACCEPT, APPLICATION_JSON);
            httpPut.setHeader(CONTENT_TYPE, APPLICATION_JSON);

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                // Extract response body once to avoid input stream consumption issues
                HttpEntity entity = response.getEntity();
                String responseBody = null;
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }

                if (response.getCode() == 401 && responseBody != null &&
                        oauthTokenHasExpiredFromBody(responseBody)) {
                    refreshToken();
                    httpPut.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
                    return executeRequest(httpClient, httpPut);
                }
                return processResponseFromBody(response, responseBody);
            } catch (ParseException e) {
                throw new HubSpotException("Can not parse response body", e);
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
            httpDelete.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
            httpDelete.setHeader(ACCEPT, APPLICATION_JSON);
            httpDelete.setHeader(CONTENT_TYPE, APPLICATION_JSON);

            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                if (oauthTokenHasExpired(response)) {
                    refreshToken();
                    httpDelete.setHeader(AUTHORIZATION, BEARER + oAuthConfig.getAccessToken());
                    httpClient.execute(httpDelete)
                              .close(); // Execute and close immediately since we don't need the response
                }
            }
        } catch (IOException e) {
            throw new HubSpotException("Cannot make delete request: \n URL: " + url, e);
        }
    }


    private boolean oauthTokenHasExpired(CloseableHttpResponse response) {
        if (response.getCode() == 401) {
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
            return jsonNode.has("category") && "EXPIRED_AUTHENTICATION".equals(jsonNode.get("category").asString());
        } catch (Exception e) {
            log.log(ERROR, "Error parsing response body to check if OAuth token has expired", e);
            return false;
        }
    }

    private void refreshToken() throws HubSpotException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiBase + "/oauth/v1/token");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", REFRESH_TOKEN));
            params.add(new BasicNameValuePair("client_id", oAuthConfig.getClientId()));
            params.add(new BasicNameValuePair("client_secret", oAuthConfig.getClientSecret()));
            params.add(new BasicNameValuePair("redirect_uri", oAuthConfig.getRedirectUrl()));
            params.add(new BasicNameValuePair(REFRESH_TOKEN, oAuthConfig.getRefreshToken()));

            httpPost.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    JsonNode jsonNode = JsonUtils.parseJson(responseBody);

                    this.oAuthConfig.setRefreshToken(jsonNode.get(REFRESH_TOKEN).asString());
                    this.oAuthConfig.setAccessToken(jsonNode.get("access_token").asString());
                }
            } catch (ParseException e) {
                throw new HubSpotException("Can not parse response body", e);
            }
        } catch (IOException e) {
            throw new HubSpotException("Failed to refresh token", e);
        }
    }

    private JsonNode processResponse(CloseableHttpResponse response) throws HubSpotException, IOException,
            ParseException {
        HttpEntity entity = response.getEntity();
        String responseBody = null;
        if (entity != null) {
            responseBody = EntityUtils.toString(entity);
        }
        return processResponseFromBody(response, responseBody);
    }

    private JsonNode processResponseFromBody(CloseableHttpResponse response, String responseBody) throws HubSpotException {
        int statusCode = response.getCode();
        String statusText = response.getReasonPhrase();

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
                            if (jsonNode.has(ERRORS) && jsonNode.get(ERRORS).isArray()) {
                                message = StreamSupport.stream(jsonNode.get(ERRORS).spliterator(), false)
                                                       .map(error -> error.has(MESSAGE) ? error
                                                               .get(MESSAGE)
                                                               .asString() : "")
                                                       .toList()
                                                       .toString();
                            }
                            break;
                        default:
                            if (jsonNode.has(MESSAGE)) {
                                message = jsonNode.get(MESSAGE).asString();
                            }
                    }

                    if (StringUtils.isNullOrEmpty(message) && jsonNode.has(MESSAGE)) {
                        message = jsonNode.get(MESSAGE).asString();
                    }

                    if (!StringUtils.isNullOrEmpty(message)) {
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

    private JsonNode executeRequest(CloseableHttpClient httpClient, HttpUriRequestBase request) throws IOException,
            HubSpotException, ParseException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return processResponse(response);
        }
    }

    private static String getHttpErrorMessageAndStatus(CloseableHttpResponse response, String responseBody) {
        return "checkResponse : HTTP status : " + response.getCode() +
                " (" + response.getReasonPhrase() +
                ") | message = " + responseBody;
    }
}
