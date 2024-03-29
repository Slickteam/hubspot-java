package fr.slickteam.hubspot.api.unit;

import fr.slickteam.hubspot.api.domain.HSContact;
import fr.slickteam.hubspot.api.service.HSContactService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import kong.unirest.json.JSONObject;
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
    public void parseContactData_Test() {
        String inputData = "{properties:{test:1},id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSContact contact = service.parseContactData(jsonObject);
        assertEquals(contact.getId(), 71);
        assertEquals(contact.getProperty("test"), "1");
    }

    @Test
    public void toJson_Test() {
        String inputData = "{properties:{test:1, test2:2},id:71}";
        JSONObject jsonObject = new JSONObject(inputData);

        HSContact contact = service.parseContactData(jsonObject);
        String result = contact.toJson().toString();
        assertEquals("{\"properties\":{\"test2\":\"2\",\"test\":\"1\"}}", result);
    }
}
