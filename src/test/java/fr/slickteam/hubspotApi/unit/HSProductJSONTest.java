package fr.slickteam.hubspotApi.unit;

import fr.slickteam.hubspotApi.domain.HSCompany;
import fr.slickteam.hubspotApi.domain.HSProduct;
import fr.slickteam.hubspotApi.service.HSCompanyService;
import fr.slickteam.hubspotApi.service.HSProductService;
import fr.slickteam.hubspotApi.service.HubSpot;
import fr.slickteam.hubspotApi.utils.Helper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class HSProductJSONTest {

    HSProductService service;

    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).product();
    }

    @Test
    public void parseProductData_Test() {
        String inputData = "{\"id\": \"512\",\"properties\": {\"description\": \"Onboarding service for data product\",\"hs_cost_of_goods_sold\": \"600.00\",\"hs_recurring_billing_period\": \"12\",\"hs_sku\": \"191902\",\"name\": \"Implementation Service\",\"price\": \"6000.00\"}}";
        JSONObject jsonObject = new JSONObject(inputData);
        HSProduct product = service.parseProductData(jsonObject);
        assertEquals(product.getId(), 512);
        assertEquals(product.getProperty("description"), "Onboarding service for data product");
        assertEquals(product.getProperty("hs_cost_of_goods_sold"), "600.00");
        assertEquals(product.getProperty("hs_recurring_billing_period"), "12");
        assertEquals(product.getProperty("hs_sku"), "191902");
        assertEquals(product.getProperty("name"), "Implementation Service");
        assertEquals(product.getProperty("price"), "6000.00");
    }

    @Test
    public void toJson_Test() {
        String inputData = "{\"id\": \"512\",\"properties\":{\"description\":\"Onboarding service for data product\"}}";
        JSONObject jsonObject = new JSONObject(inputData);
        HSProduct product = service.parseProductData(jsonObject);

        String result = product.toJson().toString();
        assertEquals("{\"properties\":{\"description\":\"Onboarding service for data product\"}}", result);
    }
}
