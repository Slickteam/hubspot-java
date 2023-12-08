package fr.slickteam.hubspot.api.unit;

import fr.slickteam.hubspot.api.domain.HSPipeline;
import fr.slickteam.hubspot.api.service.HSPipelineService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import kong.unirest.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HSPipelineJSONTest {

     HSPipelineService service;

    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).pipeline();
    }

    @Test
    public void parsePipelineData_Test() {
        String inputData = "{test:1,id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSPipeline pipeline = service.parsePipelineData(jsonObject);
        assertEquals("71", pipeline.getId());
        assertEquals("1", pipeline.getProperty("test"));
    }

    @Test
    public void toJson_Test() {
        String inputData = "{test:1, test2:2,id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSPipeline pipeline = service.parsePipelineData(jsonObject);
        String result = pipeline.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"id\":\"71\",\"test\":\"1\"}}", result);
    }
}
