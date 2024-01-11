package fr.slickteam.hubspot.api.webhook;

import com.google.common.hash.Hashing;
import fr.slickteam.hubspot.api.service.HSAssociationService;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HubSpotWebHookSignatureUtils {

    private static final System.Logger log = System.getLogger(HubSpotWebHookSignatureUtils.class.getName());

    private HubSpotWebHookSignatureUtils() {
        // default constructor
    }

    public static boolean isSignatureValid(String signature, String clientSecret, String requestMethod, String requestURI, String payload, String timestamp) {
        ByteBuffer utf8EncodedString = StandardCharsets.UTF_8.encode(requestMethod + requestURI + payload + timestamp);
        byte[] hash = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, clientSecret).hmac(utf8EncodedString);
        String newSignature = Base64.getEncoder().encodeToString(hash);
        log.log(System.Logger.Level.TRACE, "isSignatureValid - new signature : " + newSignature);
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
