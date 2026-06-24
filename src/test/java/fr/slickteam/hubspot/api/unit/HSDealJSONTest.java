package fr.slickteam.hubspot.api.unit;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.domain.HSDeal;
import fr.slickteam.hubspot.api.service.HSDealService;
import fr.slickteam.hubspot.api.service.HubSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HSDealJSONTest {

    HSDealService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).deal();
    }

    @Test
    void parseDealData_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1},\"id\":71}";
        ObjectMapper mapper = new JsonMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSDeal deal = service.parseDealData(jsonNode);
        assertThat(deal.getId()).isEqualTo(71L);
        assertThat(deal.getProperty("test")).isEqualTo("1");
    }

    @Test
    void toJson_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1, \"test2\":2},\"id\":71}";
        ObjectMapper mapper = new JsonMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSDeal deal = service.parseDealData(jsonNode);
        JsonNode result = deal.toJson();
        JsonNode expected = mapper.readTree("{\"properties\":{\"test\":\"1\",\"test2\":\"2\"}}");
        assertThat(result).isEqualTo(expected);
    }
}
