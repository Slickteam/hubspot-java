package fr.slickteam.hubspotApi.integration;

import fr.slickteam.hubspotApi.service.HSContactService;
import fr.slickteam.hubspotApi.domain.HSContact;
import fr.slickteam.hubspotApi.service.HubSpot;
import fr.slickteam.hubspotApi.utils.Helper;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HSContactServiceIT {

    private String testEmail1;
    private final String testBadEmail = "test@test.test";
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";
    private final String testPhoneNumber = "TestPhoneNumber";
    private final String testLifeCycleStage = "customer";
    private List<Long> createdContactIds;

    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        testEmail1 = "test1" + Instant.now().getEpochSecond() + "@mail.com";
        createdContactIds = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {
        for (long createdContactId : createdContactIds) {
            hubSpot.contact().delete(createdContactId);
        }
    }

    @Test
    public void createContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);

        createdContactIds.add(contact.getId());

        assertNotEquals(0L, contact.getId());
        assertEquals(contact.getEmail(), hubSpot.contact().getByID(contact.getId()).getEmail());
    }

    @Test
    public void should_get_contact_by_email_when_contact_exist() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        Optional<HSContact> foundContact = hubSpot.contact().getByEmail(contact.getEmail());

        assertTrue(foundContact.isPresent());
        assertEquals(contact.getEmail(), foundContact.get().getEmail());
    }

    @Test
    public void should_not_get_contact_by_email_when_contact_does_not_exist() throws Exception {

        Optional<HSContact>  foundContact = hubSpot.contact().getByEmail(testEmail1);

        assertFalse(foundContact.isPresent());
    }

    @Test
    public void should_not_get_contact_when_email_is_null() throws Exception {

        Optional<HSContact>  foundContact = hubSpot.contact().getByEmail(null);

        assertFalse(foundContact.isPresent());
    }

    @Test
    public void createContact_NetworkError_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);

        HSContactService mockHSContactService = mock(HSContactService.class);
        doThrow(new HubSpotException("Network error")).when(mockHSContactService).create(contact);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.contact()).thenReturn(mockHSContactService);
        exception.expect(HubSpotException.class);
        mockHubSpot.contact().create(contact);
    }

    @Test
    public void createContactIncorrectProperty_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact.setProperty("badpropertyz", "Test value 1");

        exception.expect(HubSpotException.class);
        hubSpot.contact().create(contact);
    }

    @Test
    public void createContactMissedRequiredProperty_Test() throws Exception {
        HSContact contact = new HSContact();
        contact.setFirstname(testFirstname);
        contact.setLastname(testLastname);

        exception.expect(HubSpotException.class);
        hubSpot.contact().create(contact);
    }


    @Test
    public void getContact_Id_Test() throws Exception {
        long contactId = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage))
                .getId();
        createdContactIds.add(contactId);

        HSContact contact = hubSpot.contact().getByID(contactId);

        assertEquals(contactId, contact.getId());
        assertEquals(testFirstname, contact.getFirstname());
    }

    @Test
    public void getContact_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpot.contact().getByID(id));
    }


    @Test
    public void patch_phone_Contact_Test() throws Exception {
        String test_value = "new phone number";
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setPhoneNumber(test_value);
        HSContact result = hubSpot.contact().patch(editContact);

        assertEquals(editContact.getPhoneNumber(), result.getPhoneNumber());
    }


    @Test
    public void patchContactIncorrectPredefinedFieldValue_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setEmail("email@ru");

        exception.expect(HubSpotException.class);
        exception.expectMessage("");
        hubSpot.contact().patch(editContact);
    }

    @Test
    public void patchContactMissedRequiredProperty_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value = "Test value 1";
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());
        contact.setProperty(test_property, test_value);
        HSContact missedContact = new HSContact(contact.getEmail(),
                                                contact.getFirstname(),
                                                contact.getLastname(),
                                                contact.getPhoneNumber(),
                                                contact.getLifeCycleStage());

        exception.expect(HubSpotException.class);
        hubSpot.contact().patch(missedContact);
    }

    @Test
    public void patchContactIncorrectProperty_Test() throws Exception {
        String test_property = "badpropertyz";
        String test_value = "Test value 1";
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setProperty(test_property, test_value);

        exception.expect(HubSpotException.class);
        hubSpot.contact().patch(editContact);
    }

    @Test
    public void patchContact_Bad_Email_Test() throws Exception {
        HSContact contact = new HSContact(testBadEmail,
                                          testFirstname,
                                          testLastname,
                                          testPhoneNumber,
                                          testLifeCycleStage).setId(1);
        exception.expect(HubSpotException.class);
        hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        hubSpot.contact().delete(contact);
    }

    @Test
    public void patchContact_Not_Found_Test() throws Exception {
        HSContact contact = new HSContact(testBadEmail,
                                          testFirstname,
                                          testLastname,
                                          testPhoneNumber,
                                          testLifeCycleStage).setId(-777);


        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setEmail(contact.getEmail());

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");

        hubSpot.contact().patch(editContact);
    }

    @Test
    public void deleteContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        hubSpot.contact().delete(contact);

        assertNull(hubSpot.contact().getByID(contact.getId()));

    }

    @Test
    public void deleteContact_by_id_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        hubSpot.contact().delete(contact.getId());

        assertNull(hubSpot.contact().getByID(contact.getId()));

    }

    @Test
    public void deleteContact_No_ID_Test() throws Exception {
        HSContact contact = new HSContact().setEmail(testEmail1);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("User ID must be provided"));
        hubSpot.contact().delete(contact);
    }
}
