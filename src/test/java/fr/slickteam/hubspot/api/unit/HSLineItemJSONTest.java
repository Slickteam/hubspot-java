package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.domain.HSLineItem;
import fr.slickteam.hubspot.api.service.HSLineItemService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HSLineItemJSONTest {

    HSLineItemService service;


    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).lineItem();
    }

    @Test
    public void parseDealData_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1},\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSLineItem lineItem = service.parseLineItemData(jsonNode);
        assertEquals(lineItem.getId(), 71);
        assertEquals(lineItem.getProperty("test"), "1");
    }

    @Test
    public void toJson_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1, \"test2\":2},\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSLineItem lineItem = service.parseLineItemData(jsonNode);
        String result = lineItem.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"test\":\"1\"}}", result);
    }
}
