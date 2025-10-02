package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.domain.HSContact;
import fr.slickteam.hubspot.api.domain.PagedHSContactList;
import fr.slickteam.hubspot.api.service.HSContactService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HSContactServiceIT {

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

    @BeforeEach
    void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        testEmail1 = "test1" + Instant.now().getEpochSecond() + "@mail.com";
        createdContactIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() throws Exception {
        for (long createdContactId : createdContactIds) {
            hubSpot.contact().delete(createdContactId);
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }

    @Test
    void createContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);

        createdContactIds.add(contact.getId());

        assertThat(contact.getId()).isNotZero();
        assertThat(hubSpot.contact().getByID(contact.getId()).getEmail()).isEqualTo(contact.getEmail());
    }

    @Test
    void should_get_contact_by_email_when_contact_exist() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        Optional<HSContact> foundContact = hubSpot.contact().getByEmail(contact.getEmail());

        assertThat(foundContact).isPresent();
        assertThat(foundContact.get().getEmail()).isEqualTo(contact.getEmail());
    }

    @Test
    void should_not_get_contact_by_email_when_contact_does_not_exist() throws Exception {

        Optional<HSContact> foundContact = hubSpot.contact().getByEmail(testEmail1);

        assertThat(foundContact).isNotPresent();
    }

    @Test
    void should_not_get_contact_when_email_is_null() throws Exception {

        Optional<HSContact> foundContact = hubSpot.contact().getByEmail(null);

        assertThat(foundContact).isNotPresent();
    }

    @Test
    void createContact_NetworkError_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);

        HSContactService mockHSContactService = mock(HSContactService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSContactService).create(contact);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.contact()).thenReturn(mockHSContactService);
        assertThatThrownBy(() -> mockHubSpot.contact().create(contact))
                .isInstanceOf(HubSpotException.class);
    }

    @Test
    void createContactIncorrectProperty_Test() {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact.setProperty("badpropertyz", "Test value 1");

        assertThatThrownBy(() -> hubSpot.contact().create(contact))
                .isInstanceOf(HubSpotException.class);
    }

    @Test
    void createContactMissedRequiredProperty_Test() {
        HSContact contact = new HSContact();
        contact.setFirstname(testFirstname);
        contact.setLastname(testLastname);

        assertThatThrownBy(() -> hubSpot.contact().create(contact))
                .isInstanceOf(HubSpotException.class);
    }


    @Test
    void getContact_Id_Test() throws Exception {
        long contactId = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage))
                .getId();
        createdContactIds.add(contactId);

        HSContact contact = hubSpot.contact().getByID(contactId);

        assertThat(contact.getId()).isEqualTo(contactId);
        assertThat(contact.getFirstname()).isEqualTo(testFirstname);
    }

    @Test
    void getContact_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertThat(hubSpot.contact().getByID(id)).isNull();
    }

    @Test
    void getContactByIdAndProperties_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        List<String> properties = Arrays.asList("email", "firstname", "lastname", "phone", "lifecyclestage");
        HSContact contactWithDetails = hubSpot.contact().getByIdAndProperties(contact.getId(), properties);

        assertThat(contactWithDetails.getId()).isEqualTo(contact.getId());
        assertThat(contactWithDetails.getPhoneNumber()).isEqualTo(contact.getPhoneNumber());
    }

    @Test
    void getContactListByIdAndProperties_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        HSContact contact2 = hubSpot
                .contact()
                .create(new HSContact("test2@email.com",
                                      "testFirstname2",
                                      "testLastname2",
                                      testPhoneNumber,
                                      testLifeCycleStage));

        createdContactIds.add(contact.getId());
        createdContactIds.add(contact2.getId());

        List<String> properties = Arrays.asList("email", "firstname", "lastname", "phone", "lifecyclestage");

        List<HSContact> contacts = hubSpot.contact().getContactListByIdAndProperties(createdContactIds, properties);

        assertThat(contacts).isNotNull();
        assertThat(contacts.get(0).getPhoneNumber()).isNotNull();
        assertThat(contacts).hasSize(2);
    }

    @Test
    void getContactCompanies_Test() throws Exception {
        long contactId = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage))
                .getId();
        HSCompany company = new HSCompany("TestCompany" + Instant.now().getEpochSecond(),
                                          testPhoneNumber,
                                          testAddress,
                                          testZip,
                                          testCity,
                                          testCountry);
        hubSpot.company().create(company);
        hubSpot.association().contactToCompany(contactId, company.getId());
        List<HSCompany> companies = hubSpot.contact().getContactCompanies(contactId);
        assertThat(companies).isNotNull()
                             .isNotEmpty();
        assertThat(companies.get(0).getId()).isEqualTo(company.getId());

        hubSpot.association().removeContactToCompany(contactId, company.getId());
        hubSpot.contact().delete(contactId);
        hubSpot.company().delete(company.getId());
    }

    @Test
    void getContactCompanies_withProperties_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        HSCompany company = new HSCompany("TestCompany" + Instant.now().getEpochSecond(),
                                          testPhoneNumber,
                                          testAddress,
                                          testZip,
                                          testCity,
                                          testCountry);
        hubSpot.company().create(company);
        hubSpot.association().contactToCompany(contact.getId(), company.getId());
        List<HSCompany> companies = hubSpot.contact().getContactCompanies(contact.getId(), List.of("address"));
        assertThat(companies).isNotNull()
                             .isNotEmpty();
        assertThat(companies.get(0).getId()).isEqualTo(company.getId());
        assertThat(companies.get(0).getProperty("address")).isEqualTo(company.getProperty("address"));

        hubSpot.association().removeContactToCompany(contact.getId(), company.getId());
        hubSpot.contact().delete(contact.getId());
        hubSpot.company().delete(company.getId());
    }


    @Test
    void patch_phone_Contact_Test() throws Exception {
        String testValue = "new phone number";
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setPhoneNumber(testValue);
        HSContact result = hubSpot.contact().patch(editContact);

        assertThat(result.getPhoneNumber()).isEqualTo(editContact.getPhoneNumber());
    }


    @Test
    void patchContactIncorrectPredefinedFieldValue_Test() throws Exception {
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setEmail("email@ru");

        assertThatThrownBy(() -> hubSpot.contact().patch(editContact))
                .isInstanceOf(HubSpotException.class)
                .hasMessageContaining("");
    }

    @Test
    void patchContactMissedRequiredProperty_Test() throws Exception {
        String testProperty = "linkedinbio";
        String testValue = "Test value 1";
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());
        contact.setProperty(testProperty, testValue);
        HSContact missedContact = new HSContact(contact.getEmail(),
                                                contact.getFirstname(),
                                                contact.getLastname(),
                                                contact.getPhoneNumber(),
                                                contact.getLifeCycleStage());

        assertThatThrownBy(() -> hubSpot.contact().patch(missedContact))
                .isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchContactIncorrectProperty_Test() throws Exception {
        String testProperty = "badpropertyz";
        String testValue = "Test value 1";
        HSContact contact = hubSpot
                .contact()
                .create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        createdContactIds.add(contact.getId());

        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setProperty(testProperty, testValue);

        assertThatThrownBy(() -> hubSpot.contact().patch(editContact))
                .isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchContact_Bad_Email_Test() {
        HSContact contact = new HSContact(testBadEmail,
                                          testFirstname,
                                          testLastname,
                                          testPhoneNumber,
                                          testLifeCycleStage).setId(1);
        assertThatThrownBy(() -> {
            hubSpot.contact().create(contact);
            createdContactIds.add(contact.getId());

            hubSpot.contact().delete(contact);
        }).isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchContact_Not_Found_Test() {
        HSContact contact = new HSContact(testBadEmail,
                                          testFirstname,
                                          testLastname,
                                          testPhoneNumber,
                                          testLifeCycleStage).setId(-777);


        HSContact editContact = new HSContact();
        editContact.setId(contact.getId());
        editContact.setEmail(contact.getEmail());

        assertThatThrownBy(() -> hubSpot.contact().patch(editContact))
                .isInstanceOf(HubSpotException.class)
                .hasMessageContaining("Not Found");
    }

    @Test
    void deleteContact_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        hubSpot.contact().delete(contact);

        assertThat(hubSpot.contact().getByID(contact.getId())).isNull();

    }

    @Disabled("Unexpected test behavior")
    @Test
    void queryByDefaultSearchableProperties_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        List<String> responseProperties = Arrays.asList("id", "firstname", "lastname");

        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        assertThat(hubSpot
                           .contact()
                           .queryByDefaultSearchableProperties(testFirstname, responseProperties, 10)
                           .size() > 0).isFalse();
    }

    @Test
    void searchFilteredByProperties_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        Map<String, String> properties = new HashMap<>();
        List<String> responseProperties = Arrays.asList("id", "firstname", "lastname");
        HSContact createdContact = hubSpot.contact().create(contact);

        createdContactIds.add(createdContact.getId());

        assertThat(createdContact.getId()).isNotZero();
        properties.put("hs_object_id", String.valueOf(createdContact.getId()));
        List<HSContact> hsContacts = hubSpot.contact().searchFilteredByProperties(properties, responseProperties, 10);
        assertThat(hsContacts).isNotEmpty();
    }

    @Test
    void deleteContact_by_id_Test() throws Exception {
        HSContact contact = new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage);
        contact = hubSpot.contact().create(contact);
        createdContactIds.add(contact.getId());

        hubSpot.contact().delete(contact.getId());

        assertThat(hubSpot.contact().getByID(contact.getId())).isNull();

    }

    @Test
    void deleteContact_No_ID_Test() {
        HSContact contact = new HSContact().setEmail(testEmail1);

        assertThatThrownBy(() -> hubSpot.contact().delete(contact))
                .isInstanceOf(HubSpotException.class)
                .hasMessageContaining("Contact ID must be provided");
    }


    @Test
    void getContacts_Test() throws Exception {
        HSContact contact = new HSContact();
        HSContact contact2 = new HSContact();
        HSContact contact3 = new HSContact();
        HSContact contact4 = new HSContact();
        HSContact contact5 = new HSContact();
        try {
            contact = getNewTestContact();
            contact2 = getNewTestContact();
            contact3 = getNewTestContact();
            contact4 = getNewTestContact();
            contact5 = getNewTestContact();

            List<String> properties = Arrays.asList("id", "firstname", "lastname");

            PagedHSContactList contacts = hubSpot.contact().getContacts("0", 10, properties);

            assertThat(contacts).isNotNull();
            assertThat(contacts.getContacts()).hasSize(10);

            contacts = hubSpot.contact().getContacts("0", 2, properties);

            assertThat(contacts.getContacts()).hasSize(2);
            PagedHSContactList contactsNextPage = hubSpot.contact().getContacts(contacts.getAfter(), 2, properties);
            PagedHSContactList contactsBigPage = hubSpot.contact().getContacts("0", 4, properties);

            assertThat(contactsNextPage.getContacts()).hasSize(2);
            assertThat(contactsBigPage.getContacts()).hasSize(4);
            assertThat(contactsBigPage.getContacts().get(0).getId()).isEqualTo(contacts.getContacts().get(0).getId());
            assertThat(contactsBigPage.getContacts().get(1).getId()).isEqualTo(contacts.getContacts().get(1).getId());
            assertThat(contactsBigPage.getContacts().get(2).getId()).isEqualTo(contactsNextPage
                                                                                       .getContacts()
                                                                                       .get(0)
                                                                                       .getId());
            assertThat(contactsBigPage.getContacts().get(3).getId()).isEqualTo(contactsNextPage
                                                                                       .getContacts()
                                                                                       .get(1)
                                                                                       .getId());
        } finally {
            hubSpot.contact().delete(contact.getId());
            hubSpot.contact().delete(contact2.getId());
            hubSpot.contact().delete(contact3.getId());
            hubSpot.contact().delete(contact4.getId());
            hubSpot.contact().delete(contact5.getId());
        }
    }

    private HSContact getNewTestContact() throws HubSpotException {
        return hubSpot
                .contact()
                .create(new HSContact(UUID.randomUUID() + "@test.fr",
                                      testFirstname,
                                      testLastname,
                                      testPhoneNumber,
                                      testLifeCycleStage));
    }

}
