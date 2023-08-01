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
import java.util.*;

import static org.junit.Assert.*;

public class HSCompanyServiceIT {

    private String testEmail1;
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";
    private final String testPhoneNumber = "TestPhoneNumber";
    private final String testLifeCycleStage = "other";

    private Long createdCompanyId;
    private Long createdCompanyId2;
    private Long createdContact;
    private Long createdDealId;
    private Long createdDealId2;
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
        if (createdCompanyId2 != null) {
            hubSpot.company().delete(createdCompanyId);
        }
        if (createdContact != null) {
            hubSpot.contact().delete(createdContact);
        }
        if (createdDealId != null) {
            hubSpot.deal().delete(createdDealId);
        }
        if (createdDealId2 != null) {
            hubSpot.deal().delete(createdDealId2);
        }
    }

    @Test
    public void createCompany_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();

        assertNotEquals(0, company.getId());
    }

    @Test
    public void getCompanyByIdAndProperties_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        List<String> properties = Arrays.asList("phone", "address", "postal_code", "city", "country", "website", "description"
                , "email_societe", "hubspot_owner_id", "hs_parent_company_id");
        HSCompany companyWithDetails = hubSpot.company().getByIdAndProperties(company.getId(), properties);

        createdCompanyId = company.getId();
        assertEquals(company.getId(), companyWithDetails.getId());
        assertEquals(company.getDescription(), companyWithDetails.getDescription());
    }

    @Test
    public void getCompanyListByIdAndProperties_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        HSCompany company2 = getNewTestCompany();
        createdCompanyId = company.getId();
        createdCompanyId2 = company2.getId();

        List<String> properties = Arrays.asList("phone", "address", "postal_code", "city", "country", "website", "description"
                , "email_societe", "hubspot_owner_id", "hs_parent_company_id");

        List<Long> companyIdList = new ArrayList<>();
        companyIdList.add(company.getId());
        companyIdList.add(company2.getId());
        List<HSCompany> companies = hubSpot.company().getCompanyListByIdAndProperties(companyIdList, properties);

        assertNotNull(companies);
        assertNotNull(companies.get(0).getPhoneNumber());
        assertEquals(2, companies.size());
    }

    @Test
    @Ignore("Add comment to explain why this test is ignored")
    public void addContact_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));

        createdCompanyId = company.getId();
        createdContact = contact.getId();
        hubSpot.company().addContact(contact.getId(), company.getId());
    }

    @Ignore("Add comment to explain why this test is ignored")
    @Test
    public void getByDomain_Test() throws Exception {
        List<HSCompany> companies = hubSpot.company().getByDomain("Domain");
        assertTrue(companies.size() > 0);
    }

    @Test
    public void getAssociatedCompanies_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        HSCompany associatedCompany = getNewTestCompany();
        hubSpot.company().create(company);
        hubSpot.company().create(associatedCompany);
        hubSpot.association().companyToCompany(company.getId(), associatedCompany.getId(), HSAssociationTypeEnum.PARENT);
        List<HSAssociatedCompany> associatedCompanies = hubSpot.company().getAssociatedCompanies(company.getId());

        createdCompanyId = company.getId();
        createdCompanyId2 = associatedCompany.getId();

        assertNotNull(associatedCompanies);
        assertTrue(associatedCompanies.size() > 0);
        assertTrue(associatedCompanies.stream().anyMatch(ac -> ac.getCompany().getId() == associatedCompany.getId()));
    }

    @Test
    public void getCompanyContacts_Tests() throws Exception {
        HSCompany company = getNewTestCompany();
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
        hubSpot.association().contactToCompany(contact.getId(), company.getId());

        List<HSContact> contacts = hubSpot.company().getCompanyContacts(company.getId());

        createdCompanyId = company.getId();
        createdContact = contact.getId();

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
    public void getDeals_Tests() throws Exception {
        HSDeal deal1 = getNewTestDeal();
        HSDeal deal2 = getNewTestDeal();
        createdDealId = deal1.getId();
        createdDealId2 = deal2.getId();

        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();

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
    public void getLastDeal_Tests() throws Exception {
        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();
        HSDeal deal1 = getNewTestDeal();
        createdDealId = deal1.getId();
        hubSpot.association().dealToCompany(deal1.getId(), company.getId());

        HSDeal deal2 = getNewTestDeal();
        hubSpot.association().dealToCompany(deal2.getId(), company.getId());

        HSDeal lastDeal = hubSpot.company().getLastDeal(company.getId());

        assertNotNull(lastDeal);

        // clean data test in Hubspot
        hubSpot.deal().delete(deal1.getId());
        hubSpot.deal().delete(deal2.getId());
        hubSpot.company().delete(company.getId());
    }

    private HSCompany getNewTestCompany() throws HubSpotException {
        return hubSpot.company().create(new HSCompany("TestCompany" + Instant.now().getEpochSecond(), testPhoneNumber, "address", "zip", "city", "country", "description", "www.website.com"));
    }

    private HSDeal getNewTestDeal() throws HubSpotException {
        BigDecimal testDealAmount = BigDecimal.valueOf(120);
        LocalDate testDealContractStart = LocalDate.now();
        LocalDate testDealContractEnd = LocalDate.now();
        Map<String, String> contractDates = new HashMap<>();
        contractDates.put("date_debut_contrat", testDealContractStart.toString());
        contractDates.put("date_fin_contrat", testDealContractEnd.toString());
        return hubSpot.deal().create(new HSDeal("TestDeal" + Instant.now().getEpochSecond(), "4245948", "4245946", testDealAmount, contractDates));
    }
}
