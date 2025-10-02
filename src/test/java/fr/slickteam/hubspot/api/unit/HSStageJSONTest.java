package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.service.HSStageService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HSStageJSONTest {

     HSStageService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).stage();
    }

    @Test
    void parseStageData_Test() throws Exception {
        String inputData = "{\"test\":1,\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);
        HSStage stage = service.parseStageData(jsonNode);

        assertEquals("71", stage.getId());
        assertEquals("1", stage.getProperty("test"));
    }

    @Test
    void toJson_Test() throws Exception {
        String inputData = "{\"test\":1, \"test2\":2,\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSStage stage = service.parseStageData(jsonNode);
        String result = stage.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"id\":\"71\",\"test\":\"1\"}}", result);
    }
}
