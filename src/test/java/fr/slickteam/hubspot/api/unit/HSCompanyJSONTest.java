package fr.slickteam.hubspot.api.unit;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.service.HSCompanyService;
import fr.slickteam.hubspot.api.service.HubSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HSCompanyJSONTest {

    HSCompanyService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).company();
    }

    @Test
    void parseCompanyData_Test() throws Exception {
        String inputData = "{\"portalId\": 62515,\"id\": 10444744,\"isDeleted\": false,\"properties\": {\"description\": \"text\"}}";
        ObjectMapper mapper = new JsonMapper();
        JsonNode jsonNode = mapper.readTree(inputData);
        HSCompany company = service.parseCompanyData(jsonNode);
        assertEquals(10444744, company.getId());
        assertEquals("text", company.getProperty("description"));
    }

    @Test
    void toJson_Test() throws Exception {
        String inputData = "{\"portalId\": 62515,\"id\": 10444744,\"isDeleted\": false,\"properties\": {\"description\": \"text\"}}";
        ObjectMapper mapper = new JsonMapper();
        JsonNode jsonNode = mapper.readTree(inputData);
        HSCompany company = service.parseCompanyData(jsonNode);

        JsonNode result = company.toJson();
        JsonNode expected = mapper.readTree("{\"properties\":{\"description\":\"text\"}}");
        assertEquals(expected, result);
    }
}
