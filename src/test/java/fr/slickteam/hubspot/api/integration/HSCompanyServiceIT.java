package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.assocation.HSAssociatedCompany;
import fr.slickteam.hubspot.api.domain.assocation.HSAssociationTypeInput;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.domain.HSContact;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.hamcrest.core.StringContains;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class HSCompanyServiceIT {

    private String testEmail1;
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";
    private final String testPhoneNumber = "TestPhoneNumber";
    private final String testLifeCycleStage = "other";

    private final String testAddress = "address";
    private final String testZip = "zip";
    private final String testCity = "city";
    private final String testCountry = "country";

    private Long createdCompanyId;

    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        testEmail1 = "test1" + Instant.now().getEpochSecond() + "@mail.com";
    }

    @After
    public void tearDown() throws Exception {
        if (createdCompanyId != null) {
            hubSpot.company().delete(createdCompanyId);
        }
    }
    @Test
    public void createCompany_Test() throws Exception {
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        company = hubSpot.company().create(company);
        createdCompanyId = company.getId();

        assertNotEquals(0, company.getId());
    }

    @Test
    @Ignore("Add comment to explain why this test is ignored")
    public void addContact_Test() throws Exception {
        HSCompany company = hubSpot.company().create(new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry));
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        hubSpot.company().addContact(contact.getId(), company.getId());
    }

    @Ignore("Add comment to explain why this test is ignored")
    @Test
    public void getByDomain_Test() throws Exception {
        List<HSCompany> companies = hubSpot.company().getByDomain("Domain");
        assertTrue(companies.size() > 0);
    }

    @Test
    public void get_Associated_Companies_Test() throws Exception {
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        HSCompany associatedCompany = new HSCompany("TestCompany2"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        HSAssociationTypeInput associationType = new HSAssociationTypeInput().setType(HSAssociationTypeInput.TypeEnum.PARENT);
        hubSpot.company().create(company);
        hubSpot.company().create(associatedCompany);
        hubSpot.association().companyToCompany(company.getId(), associatedCompany.getId(), associationType);
        List<HSAssociatedCompany> associatedCompanies = hubSpot.company().getAssociatedCompanies(company.getId());
        assertNotNull(associatedCompanies);
        assertTrue(associatedCompanies.size() > 0);
        assertEquals(associatedCompanies.get(0).getCompany().getId(), associatedCompany.getId());
    }

    @Test
    public void get_company_contacts_Tests() throws Exception {
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        hubSpot.company().create(company);
        hubSpot.association().contactToCompany(contact.getId(), company.getId());

        List <HSContact> contacts = hubSpot.company().getCompanyContacts(company.getId());
        assertNotNull(contacts);
        assertTrue(contacts.size() > 0);
        assertEquals(contacts.get(0).getId(), contact.getId());

    }

    @Test
    public void patchCompany_Test() throws Exception {
        HSCompany company = hubSpot.company().create(new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry));
        createdCompanyId = company.getId();

        HSCompany editCompany = new HSCompany();
        editCompany.setId(company.getId());
        editCompany.setCountry("country2");
        HSCompany updatedCompany = hubSpot.company().patch(editCompany);

        assertEquals(editCompany.getCountry(), updatedCompany.getCountry());
    }

    @Test
    public void deleteCompany_Test() throws Exception {
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        company = hubSpot.company().create(company);
        createdCompanyId = company.getId();

        hubSpot.company().delete(company);

        assertNull(hubSpot.company().getByID(company.getId()));

    }

    @Test
    public void deleteCompany_by_id_Test() throws Exception {
        HSCompany company = new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry);
        company = hubSpot.company().create(company);
        createdCompanyId = company.getId();

        hubSpot.company().delete(company.getId());

        assertNull(hubSpot.company().getByID(company.getId()));

    }

    @Test
    public void deleteCompany_No_ID_Test() throws Exception {
        HSCompany company = new HSCompany();

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("Company ID must be provided"));
        hubSpot.company().delete(company);
    }
}
