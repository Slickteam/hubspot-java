package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.utils.HubSpotProperties;

/**
 * Class for OAuth2 properties configuration.
 */
public class OAuthConfig {

    private String accessToken;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private String refreshToken;

    /**
     * Instantiates a new O auth config.
     *
     * @param properties the properties
     */
    public OAuthConfig(HubSpotProperties properties) {
        this.accessToken = properties.getAccessToken();
        this.clientId = properties.getClientId();
        this.clientSecret = properties.getClientSecret();
        this.redirectUrl = properties.getRedirectUrl();
        this.refreshToken = properties.getRefreshToken();
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
     * Gets client id.
     *
     * @return the client id
     */
    public String getClientId() {
        return clientId;
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
     * Gets redirect url.
     *
     * @return the redirect url
     */
    public String getRedirectUrl() {

        return redirectUrl;
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

    /**
     * Sets access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
