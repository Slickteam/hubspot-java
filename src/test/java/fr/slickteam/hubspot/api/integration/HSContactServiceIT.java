package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSContact;
import fr.slickteam.hubspot.api.service.HSContactService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.hamcrest.core.StringContains;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HSContactServiceIT {

    private String testEmail1;
    private final String testBadEmail = "test@test.test";
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";
    private final String testPhoneNumber = "TestPhoneNumber";
    private final String testLifeCycleStage = "customer";
    private List<Long> createdContactIds;
    private final String testAddress = "address";
    private final String testZip = "zip";
    private final String testCity = "city";
    private final String testCountry = "country";

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
        // add sleep to avoid "Too many requests" error
        sleep(100);
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

        Optional<HSContact> foundContact = hubSpot.contact().getByEmail(testEmail1);

        assertFalse(foundContact.isPresent());
    }

    @Test
    public void should_not_get_contact_when_email_is_null() throws Exception {

        Optional<HSContact> foundContact = hubSpot.contact().getByEmail(null);

        assertFalse(foundContact.isPresent());
    }

    @Test
    public void createContact_NetworkError_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);

        HSContactService mockHSContactService = mock(HSContactService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSContactService).create(contact);
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
    public void getContactByIdAndProperties_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        List<String> properties = Arrays.asList("email", "firstname", "lastname", "phone", "lifecyclestage");
        HSContact contactWithDetails = hubSpot.contact().getByIdAndProperties(contact.getId(), properties);

        assertEquals(contact.getId(), contactWithDetails.getId());
        assertEquals(contact.getPhoneNumber(), contactWithDetails.getPhoneNumber());
    }

    @Test
    public void getContactListByIdAndProperties_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        HSContact contact2 = hubSpot
                .contact()
                .create(new HSContact("test2@email.com", "testFirstname2", "testLastname2", testPhoneNumber, testLifeCycleStage));

        createdContactIds.add(contact.getId());
        createdContactIds.add(contact2.getId());

        List<String> properties = Arrays.asList("email", "firstname", "lastname", "phone", "lifecyclestage");

        List<HSContact> contacts = hubSpot.contact().getContactListByIdAndProperties(createdContactIds, properties);

        assertNotNull(contacts);
        assertNotNull(contacts.get(0).getPhoneNumber());
        assertEquals(2, contacts.size());
    }

    @Test
    public void getContactCompanies_Test() throws Exception {
        long contactId = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage))
                .getId();
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        hubSpot.company().create(company);
        hubSpot.association().contactToCompany(contactId, company.getId());
        List<HSCompany> companies = hubSpot.contact().getContactCompanies(contactId);
        assertNotNull(companies);
        assertFalse(companies.isEmpty());
        assertEquals(companies.get(0).getId(), company.getId());

        hubSpot.association().removeContactToCompany(contactId, company.getId());
        hubSpot.contact().delete(contactId);
        hubSpot.company().delete(company.getId());
    }

    @Test
    public void getContactCompanies_withProperties_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        hubSpot.company().create(company);
        hubSpot.association().contactToCompany(contact.getId(), company.getId());
        List<HSCompany> companies = hubSpot.contact().getContactCompanies(contact.getId(), List.of("address"));
        assertNotNull(companies);
        assertFalse(companies.isEmpty());
        assertEquals(companies.get(0).getId(), company.getId());
        assertEquals(companies.get(0).getProperty("address"), company.getProperty("address"));

        hubSpot.association().removeContactToCompany(contact.getId(), company.getId());
        hubSpot.contact().delete(contact.getId());
        hubSpot.company().delete(company.getId());
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

    @Ignore("Unexpected test behavior")
    @Test
    public void queryByDefaultSearchableProperties_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        List<String> responseProperties = Arrays.asList("id", "firstname", "lastname");

        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        assertFalse(hubSpot.contact().queryByDefaultSearchableProperties(testFirstname, responseProperties, 10).size() > 0);
    }
     @Test
    public void searchFilteredByProperties_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        Map<String, String> properties = new HashMap<>();
         List<String> responseProperties = Arrays.asList("id", "firstname", "lastname");
        HSContact createdContact = hubSpot.contact().create(contact);

        createdContactIds.add(createdContact.getId());

        assertNotEquals(0L, createdContact.getId());
        properties.put("hs_object_id", String.valueOf(createdContact.getId()));
        List<HSContact> hsContacts = hubSpot.contact().searchFilteredByProperties(properties, responseProperties, 10);
        assertFalse(hsContacts.size() > 0);
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
        exception.expectMessage(StringContains.containsString("Contact ID must be provided"));
        hubSpot.contact().delete(contact);
    }



}
