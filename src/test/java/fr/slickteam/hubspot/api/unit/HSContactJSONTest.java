package fr.slickteam.hubspot.api.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.slickteam.hubspot.api.domain.HSContact;
import fr.slickteam.hubspot.api.service.HSContactService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HSContactJSONTest {

    HSContactService service;

    @Before
    public void setUp() throws IOException {
        service = new HubSpot(Helper.provideHubspotProperties()).contact();
    }

    @Test
    public void parseContactData_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1},\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSContact contact = service.parseContactData(jsonNode);
        assertEquals(contact.getId(), 71);
        assertEquals(contact.getProperty("test"), "1");
    }

    @Test
    public void toJson_Test() throws Exception {
        String inputData = "{\"properties\":{\"test\":1, \"test2\":2},\"id\":71}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(inputData);

        HSContact contact = service.parseContactData(jsonNode);
        String result = contact.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"test\":\"1\"}}", result);
    }
}
