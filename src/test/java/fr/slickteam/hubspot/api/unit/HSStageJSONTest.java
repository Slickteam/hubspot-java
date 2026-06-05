package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.service.HSStageService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(stage.getId()).isEqualTo("71");
        assertThat(stage.getProperty("test")).isEqualTo("1");
    }

    @Test
    void toJson_Test() throws Exception {
        String inputData = "{\"test\":1, \"test2\":2,\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSStage stage = service.parseStageData(jsonNode);
        JsonNode result = stage.toJson();
        JsonNode expected = mapper.readTree("{\"properties\":{\"test\":\"1\",\"test2\":\"2\",\"id\":\"71\"}}");
        assertThat(result).isEqualTo(expected);
    }
}
