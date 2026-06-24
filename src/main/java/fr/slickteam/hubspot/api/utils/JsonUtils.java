package fr.slickteam.hubspot.api.utils;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.core.JacksonException;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Json utils.
 */
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new JsonMapper();

    private JsonUtils() {
        //SONAR
    }

    /**
     * Gets json properties.
     *
     * @param properties the properties
     * @return the json properties
     */
    public static String getJsonProperties(List<String> properties) {
        StringJoiner stringJoiner = new StringJoiner(",\n", "", "\n");
        for (String property : properties) {
            stringJoiner.add("\"" + property + "\"");
        }
        return stringJoiner.toString();
    }

    /**
     * Gets json input list.
     *
     * @param idList the id list
     * @return the json input list
     */
    public static String getJsonInputList(List<Long> idList) {
        StringBuilder stringBuilder = new StringBuilder();

        int lastIndex = idList.size() - 1;
        int index = 0;

        for (long companyId : idList) {
            stringBuilder.append(" { \"id\": \"").append(companyId).append("\" }");
            if (index != lastIndex) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n");
            }
            index++;
        }
        return stringBuilder.toString();
    }
    
    /**
     * Convert object to JSON string
     *
     * @param object the object to convert
     * @return JSON string representation
     * @throws HubSpotException if conversion fails
     */
    public static String toJson(Object object) throws HubSpotException {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new HubSpotException("Failed to convert object to JSON", e);
        }
    }

    /**
     * Parse JSON string to JsonNode
     *
     * @param json the JSON string
     * @return the parsed JsonNode
     * @throws HubSpotException if parsing fails
     */
    public static JsonNode parseJson(String json) throws HubSpotException {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JacksonException e) {
            throw new HubSpotException("Failed to parse JSON", e);
        }
    }

    /**
     * Create a new ObjectNode
     *
     * @return a new empty ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    /**
     * Create a new ArrayNode
     *
     * @return a new empty ArrayNode
     */
    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    /**
     * Convert a map to ObjectNode
     *
     * @param map the map to convert
     * @return the ObjectNode
     */
    public static ObjectNode mapToObjectNode(Map<String, String> map) {
        ObjectNode node = createObjectNode();
        map.forEach(node::put);
        return node;
    }

    /**
     * Get the ObjectMapper instance
     *
     * @return the ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
