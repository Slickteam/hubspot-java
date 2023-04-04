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

    public OAuthConfig(HubSpotProperties properties) {
        this.accessToken = properties.getAccessToken();
        this.clientId = properties.getClientId();
        this.clientSecret = properties.getClientSecret();
        this.redirectUrl = properties.getRedirectUrl();
        this.refreshToken = properties.getRefreshToken();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUrl() {

        return redirectUrl;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
