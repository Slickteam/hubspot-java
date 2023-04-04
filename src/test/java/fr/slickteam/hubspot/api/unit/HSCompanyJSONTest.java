package fr.slickteam.hubspot.api.unit;

import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.service.HSCompanyService;
import fr.slickteam.hubspot.api.service.HubSpot;
import kong.unirest.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HSCompanyJSONTest {

    HSCompanyService service;

    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).company();
    }

    @Test
    public void parseCompanyData_Test() {
        String inputData = "{\"portalId\": 62515,\"id\": 10444744,\"isDeleted\": false,\"properties\": {\"description\": \"text\"}}";
        JSONObject jsonObject = new JSONObject(inputData);
        HSCompany company = service.parseCompanyData(jsonObject);
        assertEquals(company.getId(), 10444744);
        assertEquals(company.getProperty("description"), "text");
    }

    @Test
    public void toJson_Test() {
        String inputData = "{\"portalId\": 62515,\"id\": 10444744,\"isDeleted\": false,\"properties\": {\"description\": \"text\"}}";
        JSONObject jsonObject = new JSONObject(inputData);
        HSCompany company = service.parseCompanyData(jsonObject);

        String result = company.toJson().toString();
        assertEquals("{\"properties\":{\"description\":\"text\"}}", result);
    }
}
