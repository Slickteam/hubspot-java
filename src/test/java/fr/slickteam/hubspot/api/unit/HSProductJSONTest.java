package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.domain.HSProduct;
import fr.slickteam.hubspot.api.service.HSProductService;
import fr.slickteam.hubspot.api.service.HubSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HSProductJSONTest {

    HSProductService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).product();
    }

    @Test
    void parseProductData_Test() throws Exception {
        String inputData = "{\"id\": \"512\",\"properties\": {\"description\": \"Onboarding service for data product\",\"hs_cost_of_goods_sold\": \"600.00\",\"hs_recurring_billing_period\": \"12\",\"hs_sku\": \"191902\",\"name\": \"Implementation Service\",\"price\": \"6000.00\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);
        HSProduct product = service.parseProductData(jsonNode);
        assertEquals(512, product.getId());
        assertEquals("Onboarding service for data product", product.getProperty("description"));
        assertEquals("600.00", product.getProperty("hs_cost_of_goods_sold"));
        assertEquals("12", product.getProperty("hs_recurring_billing_period"));
        assertEquals("191902", product.getProperty("hs_sku"));
        assertEquals("Implementation Service", product.getProperty("name"));
        assertEquals("6000.00", product.getProperty("price"));
    }

    @Test
    void toJson_Test() throws Exception {
        String inputData = "{\"id\": \"512\",\"properties\":{\"description\":\"Onboarding service for data product\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);
        HSProduct product = service.parseProductData(jsonNode);
        JsonNode result = product.toJson();
        JsonNode expected = mapper.readTree("{\"properties\":{\"description\":\"Onboarding service for data product\"}}");
        assertEquals(expected, result);
    }
}
