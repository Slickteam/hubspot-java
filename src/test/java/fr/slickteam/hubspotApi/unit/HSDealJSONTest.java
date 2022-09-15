package fr.slickteam.hubspotApi.unit;

import fr.slickteam.hubspotApi.domain.HSDeal;
import fr.slickteam.hubspotApi.service.HSDealService;
import fr.slickteam.hubspotApi.service.HubSpot;
import fr.slickteam.hubspotApi.utils.Helper;
import kong.unirest.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HSDealJSONTest {

    HSDealService service;

    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).deal();
    }

    @Test
    public void parseDealData_Test() {
        String inputData = "{properties:{test:1},id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSDeal deal = service.parseDealData(jsonObject);
        assertEquals(deal.getId(), 71);
        assertEquals(deal.getProperty("test"), "1");
    }

    @Test
    public void toJson_Test() {
        String inputData = "{properties:{test:1, test2:2},id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSDeal deal = service.parseDealData(jsonObject);
        String result = deal.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"test\":\"1\"}}", result);
    }
}
