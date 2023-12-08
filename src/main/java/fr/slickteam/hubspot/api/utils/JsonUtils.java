package fr.slickteam.hubspot.api.utils;

import java.util.List;
import java.util.StringJoiner;

/**
 * Json utils.
 */
public final class JsonUtils {

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
}
