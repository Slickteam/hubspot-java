package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.domain.HSProduct;
import fr.slickteam.hubspot.api.service.HSProductService;
import fr.slickteam.hubspot.api.service.HubSpot;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HSProductJSONTest {

    HSProductService service;

    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).product();
    }

    @Test
    public void parseProductData_Test() throws Exception {
        String inputData = "{\"id\": \"512\",\"properties\": {\"description\": \"Onboarding service for data product\",\"hs_cost_of_goods_sold\": \"600.00\",\"hs_recurring_billing_period\": \"12\",\"hs_sku\": \"191902\",\"name\": \"Implementation Service\",\"price\": \"6000.00\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);
        HSProduct product = service.parseProductData(jsonNode);
        assertEquals(product.getId(), 512);
        assertEquals(product.getProperty("description"), "Onboarding service for data product");
        assertEquals(product.getProperty("hs_cost_of_goods_sold"), "600.00");
        assertEquals(product.getProperty("hs_recurring_billing_period"), "12");
        assertEquals(product.getProperty("hs_sku"), "191902");
        assertEquals(product.getProperty("name"), "Implementation Service");
        assertEquals(product.getProperty("price"), "6000.00");
    }

    @Test
    public void toJson_Test() throws Exception {
        String inputData = "{\"id\": \"512\",\"properties\":{\"description\":\"Onboarding service for data product\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);
        HSProduct product = service.parseProductData(jsonNode);

        String result = product.toJson().toString();
        assertEquals("{\"properties\":{\"description\":\"Onboarding service for data product\"}}", result);
    }
}
