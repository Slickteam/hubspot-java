package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.hamcrest.core.StringContains;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
    private final String testDealStage = "4245948";
    private final String testDealPipeline = "4245946";
    private final BigDecimal testDealAmount = BigDecimal.valueOf(120);
    private final LocalDate testDealContractStart = LocalDate.now();
    private final LocalDate testDealContratEnd = LocalDate.now();

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
        hubSpot.company().create(company);
        hubSpot.company().create(associatedCompany);
        hubSpot.association().companyToCompany(company.getId(), associatedCompany.getId(), HSAssociationTypeEnum.PARENT);
        List<HSAssociatedCompany> associatedCompanies = hubSpot.company().getAssociatedCompanies(company.getId());
        assertNotNull(associatedCompanies);
        assertTrue(associatedCompanies.size() > 0);
        assertTrue(associatedCompanies.stream().anyMatch(ac -> ac.getCompany().getId() == associatedCompany.getId()));
    }

    @Test
    public void get_company_contacts_Tests() throws Exception {
        HSCompany company = getNewTestCompany();
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        hubSpot.association().contactToCompany(contact.getId(), company.getId());

        List <HSContact> contacts = hubSpot.company().getCompanyContacts(company.getId());
        assertNotNull(contacts);
        assertTrue(contacts.size() > 0);
        assertTrue(contacts.stream().anyMatch(c -> c.getId() == contact.getId()));

    }

    @Test
    public void patchCompany_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();

        HSCompany editCompany = new HSCompany();
        editCompany.setId(company.getId());
        editCompany.setCountry("country2");
        HSCompany updatedCompany = hubSpot.company().patch(editCompany);

        assertEquals(editCompany.getCountry(), updatedCompany.getCountry());
    }

    @Test
    public void deleteCompany_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();

        hubSpot.company().delete(company);

        assertNull(hubSpot.company().getByID(company.getId()));

    }

    @Test
    public void deleteCompany_by_id_Test() throws Exception {
        HSCompany company = getNewTestCompany();
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

    @Test
    public void get_deals_Tests() throws Exception {
        HSDeal deal1 = getNewTestDeal();
        HSDeal deal2 = getNewTestDeal();

        HSCompany company = getNewTestCompany();

        hubSpot.association().dealToCompany(deal1.getId(), company.getId());
        hubSpot.association().dealToCompany(deal2.getId(), company.getId());

        List<HSDeal> associatedDeals = hubSpot.company().getDeals(company.getId());

        assertNotNull(associatedDeals);
        assertTrue(associatedDeals.size() > 0);
        assertTrue(associatedDeals.stream().anyMatch(deal -> deal.getId() == deal1.getId()));
        assertTrue(associatedDeals.stream().anyMatch(deal -> deal.getId() == deal2.getId()));

        // clean data test in Hubspot
        hubSpot.deal().delete(deal1.getId());
        hubSpot.deal().delete(deal2.getId());
        hubSpot.company().delete(company.getId());
    }

    @Test
    public void get_last_deals_Tests() throws Exception {
        HSDeal deal1 = getNewTestDeal();
        HSDeal deal2 = getNewTestDeal();

        HSCompany company = getNewTestCompany();

        hubSpot.association().dealToCompany(deal1.getId(), company.getId());
        hubSpot.association().dealToCompany(deal2.getId(), company.getId());

        HSDeal associatedDeal = hubSpot.company().getLastDeal(company.getId());

        assertNotNull(associatedDeal);
        assertEquals(associatedDeal.getId(), deal2.getId());

        // clean data test in Hubspot
        hubSpot.deal().delete(deal1.getId());
        hubSpot.deal().delete(deal2.getId());
        hubSpot.company().delete(company.getId());
    }

    private HSCompany getNewTestCompany() throws HubSpotException {
        return hubSpot.company().create(new HSCompany("TestCompany"+ Instant.now().getEpochSecond(), testPhoneNumber, testAddress, testZip, testCity, testCountry));
    }
    private HSDeal getNewTestDeal() throws HubSpotException {
        return hubSpot.deal().create(new HSDeal("TestDeal"+ Instant.now().getEpochSecond(), testDealStage, testDealPipeline, testDealAmount, testDealContractStart, testDealContratEnd));
    }
}
