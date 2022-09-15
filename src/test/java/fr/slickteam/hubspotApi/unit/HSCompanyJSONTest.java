package fr.slickteam.hubspotApi.unit;

import fr.slickteam.hubspotApi.domain.HSCompany;
import fr.slickteam.hubspotApi.service.HSCompanyService;
import fr.slickteam.hubspotApi.service.HubSpot;
import fr.slickteam.hubspotApi.utils.Helper;
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
