package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.domain.HSPipeline;
import fr.slickteam.hubspot.api.service.HSPipelineService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HSPipelineJSONTest {

     HSPipelineService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).pipeline();
    }

    @Test
    void parsePipelineData_Test() throws Exception {
        String inputData = "{\"test\":1,\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSPipeline pipeline = service.parsePipelineData(jsonNode);
        assertEquals("71", pipeline.getId());
        assertEquals("1", pipeline.getProperty("test"));
    }

    @Test
    void toJson_Test() throws Exception {
        String inputData = "{\"test\":1, \"test2\":2,\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSPipeline pipeline = service.parsePipelineData(jsonNode);
        String result = pipeline.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"id\":\"71\",\"test\":\"1\"}}", result);
    }
}
