package fr.slickteam.hubspot.api.unit;

import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.service.HSStageService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import kong.unirest.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HSStageJSONTest {

     HSStageService service;

    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).stage();
    }

    @Test
    public void parseStageData_Test() {
        String inputData = "{test:1,id:71}";
        JSONObject jsonObject = new JSONObject(inputData);
        HSStage stage = service.parseStageData(jsonObject);

        assertEquals(71, stage.getId());
        assertEquals("1", stage.getProperty("test"));
    }

    @Test
    public void toJson_Test() {
        String inputData = "{test:1, test2:2,id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSStage stage = service.parseStageData(jsonObject);
        String result = stage.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"id\":\"71\",\"test\":\"1\"}}", result);
    }
}
