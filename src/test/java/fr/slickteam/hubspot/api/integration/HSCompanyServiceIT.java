package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.HubSpotOrdering;
import fr.slickteam.hubspot.api.utils.HubSpotSearchOperator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HSCompanyServiceIT {

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

    @BeforeEach
    void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        testEmail1 = "test1" + Instant.now().getEpochSecond() + "@mail.com";
    }

    @AfterEach
    void tearDown() throws Exception {
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
    void createCompany_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            createdCompanyId = company.getId();

            assertThat(company.getId()).isNotZero();
        } catch (Exception e) {
            assertThat(e).as("Exception occurred: " + e.getMessage()).isNull();
        } finally {
            hubSpot.company().delete(company.getId());
        }
    }

    @Test
    void getCompanyByIdAndProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            List<String> properties = Arrays.asList("phone",
                                                    "address",
                                                    "postal_code",
                                                    "city",
                                                    "country",
                                                    "website",
                                                    "description"
                    ,
                                                    "email_societe",
                                                    "hubspot_owner_id",
                                                    "hs_parent_company_id");
            HSCompany companyWithDetails = hubSpot.company().getByIdAndProperties(company.getId(), properties);

            createdCompanyId = company.getId();
            assertThat(company.getId()).isEqualTo(companyWithDetails.getId());
            assertThat(company.getDescription()).isEqualTo(companyWithDetails.getDescription());
        } finally {
            hubSpot.company().delete(company.getId());
        }
    }

    @Test
    void getCompanyListByIdAndProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany company2 = new HSCompany();
        try {
            company = getNewTestCompany();
            company2 = getNewTestCompany();
            createdCompanyId = company.getId();
            createdCompanyId2 = company2.getId();

            List<String> properties = Arrays.asList("phone",
                                                    "address",
                                                    "postal_code",
                                                    "city",
                                                    "country",
                                                    "website",
                                                    "description"
                    ,
                                                    "email_societe",
                                                    "hubspot_owner_id",
                                                    "hs_parent_company_id");

            List<Long> companyIdList = new ArrayList<>();
            companyIdList.add(company.getId());
            companyIdList.add(company2.getId());
            List<HSCompany> companies = hubSpot.company().getCompanyListByIdAndProperties(companyIdList, properties);

            assertThat(companies).isNotNull()
                                 .isNotEmpty()
                                 .hasSize(2);
            assertThat(companies.get(0).getPhoneNumber()).isNotNull();;
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
        }
    }

    @Test
    void getCompanies_Test() throws Exception {
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

            List<String> properties = Arrays.asList("phone",
                                                    "address",
                                                    "postal_code",
                                                    "city",
                                                    "country",
                                                    "website",
                                                    "description"
                    ,
                                                    "email_societe",
                                                    "hubspot_owner_id",
                                                    "hs_parent_company_id");

            PagedHSCompanyList companies = hubSpot.company().getCompanies("0", 10, properties);

            assertThat(companies).isNotNull();
            assertThat(companies.getCompanies()).hasSize(10);

            companies = hubSpot.company().getCompanies("0", 2, properties);

            assertThat(companies.getCompanies()).hasSize(2);
            PagedHSCompanyList companiesNextPage = hubSpot.company().getCompanies(companies.getAfter(), 2, properties);
            PagedHSCompanyList companiesBigPage = hubSpot.company().getCompanies("0", 4, properties);

            assertThat(companiesNextPage.getCompanies()).hasSize(2);
            assertThat(companiesBigPage.getCompanies()).hasSize(4);
            assertThat(companies.getCompanies().get(0).getId()).isEqualTo(companiesBigPage.getCompanies().get(0).getId());
            assertThat(companies.getCompanies().get(1).getId()).isEqualTo(companiesBigPage.getCompanies().get(1).getId());
            assertThat(companiesNextPage.getCompanies().get(0).getId())
                         .isEqualTo(companiesBigPage.getCompanies().get(2).getId());
            assertThat(companiesNextPage.getCompanies().get(1).getId())
                         .isEqualTo(companiesBigPage.getCompanies().get(3).getId());
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
            hubSpot.company().delete(company5.getId());
        }
    }

    @Test
    void getTotalNumberOfCompanies_Test() throws Exception {
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
            hubSpot
                    .association()
                    .companyToCompany(parentCompany.getId(), company.getId(), HSAssociationTypeEnum.PARENT);
            hubSpot
                    .association()
                    .companyToCompany(parentCompany.getId(), company2.getId(), HSAssociationTypeEnum.PARENT);
            hubSpot
                    .association()
                    .companyToCompany(parentCompany.getId(), company3.getId(), HSAssociationTypeEnum.PARENT);
            hubSpot
                    .association()
                    .companyToCompany(parentCompany.getId(), company4.getId(), HSAssociationTypeEnum.PARENT);

            assertThat(hubSpot.company().getTotalNumberOfCompanies()).isPositive();
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
            hubSpot.company().delete(parentCompany.getId());
        }
    }

    @Test
    void queryByDefaultSearchableProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany company2 = new HSCompany();
        HSCompany company3 = new HSCompany();
        HSCompany company4 = new HSCompany();
        List<String> responseProperties = Arrays.asList("id", "hs_parent_company_id", "name", "city", "zip");
        try {
            company = getNewTestCompany();
            company2 = getNewTestCompany();
            company3 = getNewTestCompany();
            company4 = getNewTestCompany();

            assertThat(hubSpot
                               .company()
                               .queryByDefaultSearchableProperties("test", responseProperties, 10)
                               .size()).isGreaterThanOrEqualTo(4);

        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
        }
    }

    @Test
    void searchFilteredByProperties_Test() throws Exception {
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

        List<String> responseProperties = Arrays.asList("id", "hs_parent_company_id", "name", "city", "zip");

        try {
            company = getNewTestCompany();
            company2 = getNewTestCompany();
            company3 = getNewTestCompany();
            company4 = getNewTestCompany();

            assertThat(hubSpot.company().searchFilteredByProperties(properties, responseProperties, 10).size()).isGreaterThanOrEqualTo(4);
            assertThat(hubSpot.company().searchFilteredByProperties(properties2, responseProperties, 10).size()).isGreaterThanOrEqualTo(4);
            assertThat(hubSpot.company().searchFilteredByProperties(properties3, responseProperties, 10).size()).isGreaterThanOrEqualTo(4);

        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(company2.getId());
            hubSpot.company().delete(company3.getId());
            hubSpot.company().delete(company4.getId());
        }
    }

    @Test
    void searchSortedFiltered_Test() throws Exception {
        List<String> responseProperties = Arrays.asList("id", "hs_parent_company_id", "name", "city", "zip");

        List<HSCompany> results = hubSpot.company()
                                         .searchSortedFiltered(List.of(new HSSearchPropertyFilter("name",
                                                                                                  null,
                                                                                                  "*Groupe*",
                                                                                                  null,
                                                                                                  HubSpotSearchOperator.CONTAINS_TOKEN)),
                                                               responseProperties,
                                                               List.of(new HSSortOrder("name",
                                                                                       HubSpotOrdering.ASCENDING)),
                                                               10);
        assertThat(results).as("Results size invalid: " + results.size()).isNotEmpty();
        results.forEach(c -> assertThat(c.getName().toLowerCase().contains("groupe"))
                                        .as("Company name invalid: " + c.getName()).isTrue());
    }

    @Test
    @Disabled("Add comment to explain why this test is ignored")
    void addContact_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSContact contact = new HSContact();
        try {
            company = getNewTestCompany();
            contact = hubSpot
                    .contact()
                    .create(new HSContact(testEmail1,
                                          testFirstname,
                                          testLastname,
                                          testPhoneNumber,
                                          testLifeCycleStage));

            createdCompanyId = company.getId();
            createdContact = contact.getId();
            hubSpot.company().addContact(contact.getId(), company.getId());
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.contact().delete(contact.getId());
        }
    }

    @Disabled("Add comment to explain why this test is ignored")
    @Test
    void getByDomain_Test() throws Exception {
        List<HSCompany> companies = hubSpot.company().getByDomain("Domain");
        assertThat(companies).isNotEmpty();
    }

    @Test
    void getAssociatedCompanies_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany associatedCompany = new HSCompany();
        try {
            company = getNewTestCompany();
            associatedCompany = getNewTestCompany();
            hubSpot.company().create(company);
            hubSpot.company().create(associatedCompany);
            hubSpot
                    .association()
                    .companyToCompany(company.getId(), associatedCompany.getId(), HSAssociationTypeEnum.PARENT);
            List<HSAssociatedCompany> associatedCompanies = hubSpot.company().getAssociatedCompanies(company.getId());

            createdCompanyId = company.getId();
            createdCompanyId2 = associatedCompany.getId();

            assertThat(associatedCompanies).isNotNull();
            assertThat(associatedCompanies).isNotEmpty();
            HSCompany finalAssociatedCompany = associatedCompany;
            assertThat(associatedCompanies
                               .stream()
                               .anyMatch(ac -> ac.getCompany().getId() == finalAssociatedCompany.getId())).isTrue();
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(associatedCompany.getId());
        }
    }

    @Test
    void getAssociatedCompanies_withProperties_Test() throws Exception {
        HSCompany company = new HSCompany();
        HSCompany associatedCompany = new HSCompany();
        try {
            company = hubSpot.company().create(getNewTestCompany());
            associatedCompany = hubSpot.company().create(getNewTestCompany());
            hubSpot
                    .association()
                    .companyToCompany(company.getId(), associatedCompany.getId(), HSAssociationTypeEnum.PARENT);
            List<HSAssociatedCompany> associatedCompanies = hubSpot
                    .company()
                    .getAssociatedCompanies(company.getId(), List.of("address"));

            createdCompanyId = company.getId();
            createdCompanyId2 = associatedCompany.getId();

            assertThat(associatedCompanies).isNotNull();
            assertThat(associatedCompanies).isNotEmpty();
            HSCompany finalAssociatedCompany = associatedCompany;
            assertThat(associatedCompanies
                               .stream()
                               .anyMatch(ac -> ac.getCompany().getId() == finalAssociatedCompany.getId())).isTrue();
            assertThat(associatedCompanies
                               .stream()
                               .anyMatch(ac -> ac
                                       .getCompany()
                                       .getProperty("address")
                                       .equals(finalAssociatedCompany.getAddress()))).isTrue();
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.company().delete(associatedCompany.getId());
        }
    }

    @Test
    void getCompanyContacts_Tests() throws Exception {
        HSCompany company = new HSCompany();
        HSContact contact = new HSContact();
        try {
            company = getNewTestCompany();
            contact = hubSpot
                    .contact()
                    .create(new HSContact(testEmail1,
                                          testFirstname,
                                          testLastname,
                                          testPhoneNumber,
                                          testLifeCycleStage));
            hubSpot.association().contactToCompany(contact.getId(), company.getId());

            List<HSContact> contacts = hubSpot
                    .company()
                    .getCompanyContacts(company.getId(), List.of("email", "lastname"));

            createdCompanyId = company.getId();
            createdContact = contact.getId();

            assertThat(contacts).isNotNull();
            assertThat(contacts).isNotEmpty();
            HSContact finalContact = contact;
            assertThat(contacts.stream().anyMatch(c -> c.getId() == finalContact.getId())).isTrue();
        } finally {
            hubSpot.company().delete(company.getId());
            hubSpot.contact().delete(contact.getId());
        }
    }

    @Test
    void getAssociatedContacts_Tests() throws Exception {
        Map<Long, List<Long>> savedCompaniesAndContacts = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HSCompany company = getNewTestCompany();
            List<Long> contacts = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                HSContact contact = hubSpot
                        .contact()
                        .create(new HSContact("testbac1" + i + j + "@mail.com",
                                              testFirstname,
                                              testLastname,
                                              testPhoneNumber,
                                              testLifeCycleStage));
                hubSpot.association().contactToCompany(contact.getId(), company.getId());
                contacts.add(contact.getId());
            }
            savedCompaniesAndContacts.put(company.getId(), contacts);
        }

        try {
            Map<Long, List<Long>> companiesContacts = hubSpot
                    .company()
                    .getAssociatedContacts(new ArrayList<>(savedCompaniesAndContacts.keySet()));

            assertThat(companiesContacts).isNotNull();
            assertThat(companiesContacts).isNotEmpty();
            companiesContacts.keySet().forEach(resCompanyId -> {
                assertThat(companiesContacts.get(resCompanyId).size())
                             .isEqualTo(savedCompaniesAndContacts.get(resCompanyId).size());
                assertThat(companiesContacts
                                   .get(resCompanyId)).containsAll(savedCompaniesAndContacts.get(resCompanyId));
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
    void patchCompany_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            createdCompanyId = company.getId();

            HSCompany editCompany = new HSCompany();
            editCompany.setId(company.getId());
            editCompany.setCountry("country2");
            HSCompany updatedCompany = hubSpot.company().patch(editCompany);

            assertThat(updatedCompany.getCountry()).isEqualTo(editCompany.getCountry());
        } finally {
            hubSpot.company().delete(company.getId());
        }
    }

    @Test
    void deleteCompany_Test() throws Exception {
        HSCompany company = new HSCompany();
        try {
            company = getNewTestCompany();
            createdCompanyId = company.getId();

            hubSpot.company().delete(company);

            assertThat(hubSpot.company().getByID(company.getId())).isNull();
        } finally {
            hubSpot.company().delete(company.getId());
        }
    }

    @Test
    void deleteCompany_by_id_Test() throws Exception {
        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();

        hubSpot.company().delete(company.getId());

        assertThat(hubSpot.company().getByID(company.getId())).isNull();

    }

    @Test
    void deleteCompany_No_ID_Test() {
        HSCompany company = new HSCompany();

        assertThatThrownBy(() -> hubSpot.company().delete(company))
                .isInstanceOf(HubSpotException.class)
                .hasMessageContaining("Company ID must be provided");
    }

    @Test
    void getDeals_Tests() throws Exception {
        HSDeal deal1 = getNewTestDeal();
        HSDeal deal2 = getNewTestDeal();
        createdDealId = deal1.getId();
        createdDealId2 = deal2.getId();

        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();

        hubSpot.association().dealToCompany(deal1.getId(), company.getId());
        hubSpot.association().dealToCompany(deal2.getId(), company.getId());

        List<HSDeal> associatedDeals = hubSpot.company().getDeals(company.getId());

        assertThat(associatedDeals).isNotNull();
        assertThat(associatedDeals).isNotEmpty();
        assertThat(associatedDeals.stream().anyMatch(deal -> deal.getId() == deal1.getId())).isTrue();
        assertThat(associatedDeals.stream().anyMatch(deal -> deal.getId() == deal2.getId())).isTrue();

        // clean data test in Hubspot
        hubSpot.deal().delete(deal1.getId());
        hubSpot.deal().delete(deal2.getId());
        hubSpot.company().delete(company.getId());
    }

    @Test
    void getLastDeal_Tests() throws Exception {
        HSCompany company = getNewTestCompany();
        createdCompanyId = company.getId();
        HSDeal deal1 = getNewTestDeal();
        createdDealId = deal1.getId();
        hubSpot.association().dealToCompany(deal1.getId(), company.getId());

        HSDeal deal2 = getNewTestDeal();
        hubSpot.association().dealToCompany(deal2.getId(), company.getId());

        HSDeal lastDeal = hubSpot.company().getLastDeal(company.getId());

        assertThat(lastDeal).isNotNull();

        // clean data test in Hubspot
        hubSpot.deal().delete(deal1.getId());
        hubSpot.deal().delete(deal2.getId());
        hubSpot.company().delete(company.getId());
    }

    @Test
    void getAssociatedDeals_Tests() throws Exception {
        Map<Long, List<Long>> savedCompaniesAndDeals = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HSCompany company = getNewTestCompany();
            List<Long> deals = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                HSDeal deal = getNewTestDeal();
                hubSpot.association().dealToCompany(deal.getId(), company.getId());
                deals.add(deal.getId());
            }
            savedCompaniesAndDeals.put(company.getId(), deals);
        }

        try {
            Map<Long, List<Long>> companiesDeals = hubSpot
                    .company()
                    .getAssociatedDealIds(new ArrayList<>(savedCompaniesAndDeals.keySet()));

            assertThat(companiesDeals).isNotNull();
            assertThat(companiesDeals).isNotEmpty();
            companiesDeals.keySet().forEach(resCompanyId -> {
                assertThat(companiesDeals.get(resCompanyId).size()).isEqualTo(savedCompaniesAndDeals.get(resCompanyId).size());
                assertThat(companiesDeals.get(resCompanyId).containsAll(savedCompaniesAndDeals.get(resCompanyId))).isTrue();
            });
        } finally {
            savedCompaniesAndDeals.forEach((key, value) -> {
                try {
                    hubSpot.company().delete(key);
                    value.forEach(dealId -> {
                        try {
                            hubSpot.deal().delete(dealId);
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

    private HSCompany getNewTestCompany() throws HubSpotException {
        return hubSpot
                .company()
                .create(new HSCompany("TestCompany" + Instant.now().getEpochSecond(),
                                      testPhoneNumber,
                                      "address",
                                      "10000",
                                      "city",
                                      "country",
                                      "description",
                                      "www.website.com"));
    }

    private HSCompany getNewTestCompany(String name) throws HubSpotException {
        return hubSpot
                .company()
                .create(new HSCompany(name,
                                      testPhoneNumber,
                                      "address",
                                      "10000",
                                      "city",
                                      "country",
                                      "description",
                                      "www.website.com"));
    }

    private HSDeal getNewTestDeal() throws HubSpotException {
        BigDecimal testDealAmount = BigDecimal.valueOf(120);
        LocalDate testDealContractStart = LocalDate.now();
        LocalDate testDealContractEnd = LocalDate.now();
        Map<String, String> contractDates = new HashMap<>();
        contractDates.put("date_debut_contrat", testDealContractStart.toString());
        contractDates.put("date_fin_contrat", testDealContractEnd.toString());
        return hubSpot
                .deal()
                .create(new HSDeal("TestDeal" + Instant.now().getEpochSecond(),
                                   "4245948",
                                   "4245946",
                                   testDealAmount,
                                   contractDates));
    }
}
