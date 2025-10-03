package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.domain.HSLineItem;
import fr.slickteam.hubspot.api.service.HSLineItemService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HSLineItemJSONTest {

    HSLineItemService service;


    @BeforeEach
    void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).lineItem();
    }

    @Test
    void parseDealData_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1},\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSLineItem lineItem = service.parseLineItemData(jsonNode);
        assertThat(lineItem.getId()).isEqualTo(71L);
        assertThat(lineItem.getProperty("test")).isEqualTo("1");
    }

    @Test
    void toJson_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1, \"test2\":2},\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSLineItem lineItem = service.parseLineItemData(jsonNode);
        String result = lineItem.toJson().toString();
        assertThat(result).isEqualTo("{\"properties\":{\"test\":\"1\",\"test2\":\"2\"}}");
    }
}
