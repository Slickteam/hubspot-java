package fr.slickteam.hubspot.api.utils;

/**
 * Author: dlunev
 * Date: 8/18/15 3:27 PM
 */
public class HubSpotProperties {

    private String apiBase;
    private String accessToken;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String refreshToken;

    /**
     * Gets api base.
     *
     * @return the api base
     */
    public String getApiBase() {
        return apiBase;
    }

    /**
     * Sets api base.
     *
     * @param apiBase the api base
     */
    public void setApiBase(String apiBase) {
        this.apiBase = apiBase;
    }

    /**
     * Gets access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets client secret.
     *
     * @return the client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Sets client secret.
     *
     * @param clientSecret the client secret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Gets redirect url.
     *
     * @return the redirect url
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * Sets redirect url.
     *
     * @param redirectUrl the redirect url
     */
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /**
     * Gets refresh token.
     *
     * @return the refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets refresh token.
     *
     * @param refreshToken the refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
