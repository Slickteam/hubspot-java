package fr.slickteam.hubspotApi.unit;

import fr.slickteam.hubspotApi.domain.HSLineItem;
import fr.slickteam.hubspotApi.service.HSLineItemService;
import fr.slickteam.hubspotApi.service.HubSpot;
import fr.slickteam.hubspotApi.utils.Helper;
import kong.unirest.json.JSONObject;
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
    public void parseDealData_Test() {
        String inputData = "{properties:{test:1},id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSLineItem lineItem = service.parseLineItemData(jsonObject);
        assertEquals(lineItem.getId(), 71);
        assertEquals(lineItem.getProperty("test"), "1");
    }

    @Test
    public void toJson_Test() {
        String inputData = "{properties:{test:1, test2:2},id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSLineItem lineItem = service.parseLineItemData(jsonObject);
        String result = lineItem.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"test\":\"1\"}}", result);
    }
}
