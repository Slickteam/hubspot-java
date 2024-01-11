package fr.slickteam.hubspot.api.webhook;

import org.apache.commons.codec.digest.HmacUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HubSpotWebHookSignatureUtils {

    private HubSpotWebHookSignatureUtils() {
        // default constructor
    }

    public static boolean isSignatureValid(String signature, String clientSecret, String requestMethod, String requestURI, String payload, String timestamp) {
        String utf8EncodedString = StandardCharsets.UTF_8.encode(requestMethod + " " + reencodeURIForHubSpot(requestURI) + " " + payload + " " + timestamp).toString();
        String hash = new HmacUtils("HmacSHA256", clientSecret).hmacHex(utf8EncodedString);
        String newSignature = Base64.getEncoder().encodeToString(hash.getBytes());
        return newSignature.equals(signature);
    }

    private static String reencodeURIForHubSpot(String uri) {
        return uri.replace("%3A", ":")
                .replace("%2F", "/")
                .replace("%3F", "?")
                .replace("%40", "@")
                .replace("%21", "!")
                .replace("%24", "$")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2A", "*")
                .replace("%2C", ",")
                .replace("%3B", ";");
    }
}
