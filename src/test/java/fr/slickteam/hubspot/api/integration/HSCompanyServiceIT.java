package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.hamcrest.core.StringContains;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Thread.sleep;
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
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }

    @Test
    public void createCompany_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            createdCompanyId = company.getId();

            assertNotEquals(0, company.getId());
        } finally {
            hubSpot.company().delete(company.getId());
        }
    }

    @Test
    public void getCompanyByIdAndProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            List<String> properties = Arrays.asList("phone", "address", "postal_code", "city", "country", "website", "description"
                    , "email_societe", "hubspot_owner_id", "hs_parent_company_id");
            HSCompany companyWithDetails = hubSpot.company().getByIdAndProperties(company.getId(), properties);

            createdCompanyId = company.getId();
            assertEquals(company.getId(), companyWithDetails.getId());
            assertEquals(company.getDescription(), companyWithDetails.getDescription());
        } finally {
            hubSpot.company().delete(company.getId());
        }
    }

    @Test
    public void getCompanyListByIdAndProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany company2 = new HSCompany();
        try {
            company = getNewTestCompany();
            company2 = getNewTestCompany();
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
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
        }
    }

    @Test
    public void getCompanies_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany company2 = new HSCompany();
        HSCompany company3 = new HSCompany();
        HSCompany company4 = new HSCompany();
        HSCompany company5 = new HSCompany();
        try {
            company = getNewTestCompany();
            company2 = getNewTestCompany();
            company3 = getNewTestCompany();
            company4 = getNewTestCompany();
            company5 = getNewTestCompany();

            List<String> properties = Arrays.asList("phone", "address", "postal_code", "city", "country", "website", "description"
                    , "email_societe", "hubspot_owner_id", "hs_parent_company_id");

            PagedHSCompanyList companies = hubSpot.company().getCompanies("0", 10, properties);

            assertNotNull(companies);
            assertEquals(10, companies.getCompanies().size());

            companies = hubSpot.company().getCompanies("0", 2, properties);

            assertEquals(2, companies.getCompanies().size());
            PagedHSCompanyList companiesNextPage = hubSpot.company().getCompanies(companies.getAfter(), 2, properties);
            PagedHSCompanyList companiesBigPage = hubSpot.company().getCompanies("0", 4, properties);

            assertEquals(2, companiesNextPage.getCompanies().size());
            assertEquals(4, companiesBigPage.getCompanies().size());
            assertEquals(companies.getCompanies().get(0).getId(), companiesBigPage.getCompanies().get(0).getId());
            assertEquals(companies.getCompanies().get(1).getId(), companiesBigPage.getCompanies().get(1).getId());
            assertEquals(companiesNextPage.getCompanies().get(0).getId(), companiesBigPage.getCompanies().get(2).getId());
            assertEquals(companiesNextPage.getCompanies().get(1).getId(), companiesBigPage.getCompanies().get(3).getId());
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
            hubSpot.company().delete(company5.getId());
        }
    }

    @Test
    public void getTotalNumberOfCompanies_Test() throws Exception {
        HSCompany parentCompany = new HSCompany();
        HSCompany company = new HSCompany();
        HSCompany company2 = new HSCompany();
        HSCompany company3 = new HSCompany();
        HSCompany company4 = new HSCompany();
        try {
            parentCompany = getNewTestCompany();
            company = getNewTestCompany();
            company2 = getNewTestCompany();
            company3 = getNewTestCompany();
            company4 = getNewTestCompany();
            hubSpot.association().companyToCompany(parentCompany.getId(), company.getId(), HSAssociationTypeEnum.PARENT);
            hubSpot.association().companyToCompany(parentCompany.getId(), company2.getId(), HSAssociationTypeEnum.PARENT);
            hubSpot.association().companyToCompany(parentCompany.getId(), company3.getId(), HSAssociationTypeEnum.PARENT);
            hubSpot.association().companyToCompany(parentCompany.getId(), company4.getId(), HSAssociationTypeEnum.PARENT);

            assertTrue(hubSpot.company().getTotalNumberOfCompanies() > 0);
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
            hubSpot.company().delete(parentCompany.getId());
        }
    }

    @Test
    public void queryByDefaultSearchableProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany company2 = new HSCompany();
        HSCompany company3 = new HSCompany();
        HSCompany company4 = new HSCompany();
        try {
            company = getNewTestCompany();
            company2 = getNewTestCompany();
            company3 = getNewTestCompany();
            company4 = getNewTestCompany();

            assertTrue(hubSpot.company().queryByDefaultSearchableProperties("test", 100).size() >= 4);

        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
        }
    }

    @Test
    public void searchFilteredByProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany company2 = new HSCompany();
        HSCompany company3 = new HSCompany();
        HSCompany company4 = new HSCompany();
        Map<String, String> properties = new HashMap<>();
        properties.put("zip", "10000");

        Map<String, String> properties2 = new HashMap<>();
        properties2.put("city", "rennes");
        properties2.put("zip", "10000");

        Map<String, String> properties3 = new HashMap<>();
        properties3.put("city", "city");
        try {
            company = getNewTestCompany();
            company2 = getNewTestCompany();
            company3 = getNewTestCompany();
            company4 = getNewTestCompany();

            assertTrue(hubSpot.company().searchFilteredByProperties(properties, 100).size() >= 4);
            assertTrue(hubSpot.company().searchFilteredByProperties(properties2, 100).size() >= 4);
            assertTrue(hubSpot.company().searchFilteredByProperties(properties3, 100).size() >= 4);

        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
        }
    }

    @Test
    @Ignore("Add comment to explain why this test is ignored")
    public void addContact_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSContact contact = new HSContact();
        try {
            company = getNewTestCompany();
            contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));

            createdCompanyId = company.getId();
            createdContact = contact.getId();
            hubSpot.company().addContact(contact.getId(), company.getId());
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.contact().delete(contact.getId());
        }
    }

    @Ignore("Add comment to explain why this test is ignored")
    @Test
    public void getByDomain_Test() throws Exception {
        List<HSCompany> companies = hubSpot.company().getByDomain("Domain");
        assertFalse(companies.isEmpty());
    }

    @Test
    public void getAssociatedCompanies_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany associatedCompany = new HSCompany();
        try {
            company = getNewTestCompany();
            associatedCompany = getNewTestCompany();
            hubSpot.company().create(company);
            hubSpot.company().create(associatedCompany);
            hubSpot.association().companyToCompany(company.getId(), associatedCompany.getId(), HSAssociationTypeEnum.PARENT);
            List<HSAssociatedCompany> associatedCompanies = hubSpot.company().getAssociatedCompanies(company.getId());

            createdCompanyId = company.getId();
            createdCompanyId2 = associatedCompany.getId();

            assertNotNull(associatedCompanies);
            assertFalse(associatedCompanies.isEmpty());
            HSCompany finalAssociatedCompany = associatedCompany;
            assertTrue(associatedCompanies.stream().anyMatch(ac -> ac.getCompany().getId() == finalAssociatedCompany.getId()));
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(associatedCompany.getId());
        }
    }

    @Test
    public void getAssociatedCompanies_withProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany associatedCompany = new HSCompany();
        try {
            company = hubSpot.company().create(getNewTestCompany());
            associatedCompany = hubSpot.company().create(getNewTestCompany());
            hubSpot.association().companyToCompany(company.getId(), associatedCompany.getId(), HSAssociationTypeEnum.PARENT);
            List<HSAssociatedCompany> associatedCompanies = hubSpot.company().getAssociatedCompanies(company.getId(), List.of("address"));

            createdCompanyId = company.getId();
            createdCompanyId2 = associatedCompany.getId();

            assertNotNull(associatedCompanies);
            assertFalse(associatedCompanies.isEmpty());
            HSCompany finalAssociatedCompany = associatedCompany;
            assertTrue(associatedCompanies.stream().anyMatch(ac -> ac.getCompany().getId() == finalAssociatedCompany.getId()));
            assertTrue(associatedCompanies.stream().anyMatch(ac -> ac.getCompany().getProperty("address").equals(finalAssociatedCompany.getAddress())));
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(associatedCompany.getId());
        }
    }

    @Test
    public void getCompanyContacts_Tests() throws Exception {
        HSCompany company = new HSCompany();
        HSContact contact = new HSContact();
        try {
            company = getNewTestCompany();
            contact = hubSpot.contact().create(new HSContact(testEmail1, testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
            hubSpot.association().contactToCompany(contact.getId(), company.getId());

            List<HSContact> contacts = hubSpot.company().getCompanyContacts(company.getId(), List.of("email", "lastname"));

            createdCompanyId = company.getId();
            createdContact = contact.getId();

            assertNotNull(contacts);
            assertFalse(contacts.isEmpty());
            HSContact finalContact = contact;
            assertTrue(contacts.stream().anyMatch(c -> c.getId() == finalContact.getId()));
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.contact().delete(contact.getId());
        }
    }

    @Test
    public void getAssociatedContacts_Tests() throws Exception {
        Map<Long, List<Long>> savedCompaniesAndContacts = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HSCompany company = getNewTestCompany();
            List<Long> contacts = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                HSContact contact = hubSpot.contact().create(new HSContact("testbac1" + i + j + "@mail.com", testFirstname, testLastname, testPhoneNumber, testLifeCycleStage));
                hubSpot.association().contactToCompany(contact.getId(), company.getId());
                contacts.add(contact.getId());
            }
            savedCompaniesAndContacts.put(company.getId(), contacts);
        }

        try {
            Map<Long, List<Long>> companiesContacts = hubSpot.company().getAssociatedContacts(new ArrayList<>(savedCompaniesAndContacts.keySet()));

            assertNotNull(companiesContacts);
            assertFalse(companiesContacts.isEmpty());
            companiesContacts.keySet().forEach(resCompanyId -> {
                assertEquals(companiesContacts.get(resCompanyId).size(), savedCompaniesAndContacts.get(resCompanyId).size());
                assertTrue(companiesContacts.get(resCompanyId).containsAll(savedCompaniesAndContacts.get(resCompanyId)));
            });
        } finally {
            savedCompaniesAndContacts.forEach((key, value) -> {
                try {
                    hubSpot.company().delete(key);
                    value.forEach(contactId -> {
                        try {
                            hubSpot.contact().delete(contactId);
                        } catch (HubSpotException e) {
                            // do nothing
                        }
                    });
                } catch (HubSpotException e) {
                    // do nothing
                }
            });
        }
    }

    @Test
    public void patchCompany_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            createdCompanyId = company.getId();

            HSCompany editCompany = new HSCompany();
            editCompany.setId(company.getId());
            editCompany.setCountry("country2");
            HSCompany updatedCompany = hubSpot.company().patch(editCompany);

            assertEquals(editCompany.getCountry(), updatedCompany.getCountry());
        } finally {
            hubSpot.company().delete(company.getId());
        }
    }

    @Test
    public void deleteCompany_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            createdCompanyId = company.getId();

            hubSpot.company().delete(company);

            assertNull(hubSpot.company().getByID(company.getId()));
        } finally {
            hubSpot.company().delete(company.getId());
        }
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
        assertFalse(associatedDeals.isEmpty());
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
        return hubSpot.company().create(new HSCompany("TestCompany" + Instant.now().getEpochSecond(), testPhoneNumber, "address", "10000", "city", "country", "description", "www.website.com"));
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
