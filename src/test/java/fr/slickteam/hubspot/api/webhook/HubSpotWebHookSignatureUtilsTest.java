package fr.slickteam.hubspot.api.webhook;

import junit.framework.TestCase;

public class HubSpotWebHookSignatureUtilsTest extends TestCase {

    public void testIsSignatureValid() {
        String body = "[{\"eventId\":100,\"subscriptionId\":2439752,\"portalId\":8136442,\"occurredAt\":1704970386905,\"subscriptionType\":\"deal.propertyChange\",\"attemptNumber\":0,\"objectId\":123,\"changeSource\":\"CRM\",\"propertyName\":\"dealstage\",\"propertyValue\":\"sample-value\"}]";
        String secret = "3da21a3d-5964-494c-a638-c56abb76d825";
        String uri = "https://api.beta/webhooks";
        String method = "POST";
        String timestamp = "1704975585199";
        String signature = "IirHW2rKOnmjwy3EF7Czp86mGntxQZVDVhKTlGj5jx8=";

        boolean result = HubSpotWebHookSignatureUtils.isSignatureValid(signature, secret, method, uri, body, timestamp);
        assertEquals(true, result);
    }
}