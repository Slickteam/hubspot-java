package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.service.HSDealService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HSDealServiceIT {


    private final String testDealName = "Test deal";
    private final String testDealStage = "qualifiedtobuy";
    private final String testPipeline = "default";
    private final Instant testCloseDate = Instant.now();
    private final BigDecimal testAmount = BigDecimal.valueOf(50);
    private Long createdDealId;
    private String testProductId = null;


    private HubSpot hubSpot;

    @BeforeEach
    void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        HSObject object = new HSObject();
        object.setProperty("price", testAmount.toString());
        object = hubSpot.hsService().createHSObject("/crm/v3/objects/products", object);

        testProductId = object.getProperty("hs_object_id");
    }


    @AfterEach
    void tearDown() throws Exception {
        if (createdDealId != null) {
            hubSpot.deal().delete(createdDealId);
        }
        if (testProductId != null) {
            hubSpot.hsService().deleteHSObject("/crm/v3/objects/products/" + testProductId);
            testProductId = null;
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }

    @Test
    void createDeal_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal = hubSpot.deal().create(deal);

        createdDealId = deal.getId();

        assertThat(deal.getId()).isNotZero();
        assertThat(hubSpot.deal().getByID(deal.getId()).getDealName()).isEqualTo(deal.getDealName());
    }

    @Test
    void createDeal_NetworkError_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);

        HSDealService mockHSDealService = mock(HSDealService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSDealService).create(deal);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.deal()).thenReturn(mockHSDealService);
        assertThatThrownBy(() -> mockHubSpot.deal().create(deal))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void createDealIncorrectProperty_Test() {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal.setProperty("badpropertyz", "Test value 1");

        assertThatThrownBy(() -> hubSpot.deal().create(deal))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void getDeal_Id_Test() throws Exception {
        long dealId = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate))
                .getId();
        createdDealId = dealId;

        HSDeal deal = hubSpot.deal().getByID(dealId);

        assertThat(deal.getId()).isEqualTo(dealId);
        assertThat(deal.getDealName()).isEqualTo(testDealName);
    }

    @Test
    void getDeal_Id_And_Properties_Test() throws Exception {
        LocalDate testDealContractStart = LocalDate.now();
        LocalDate testDealContractEnd = LocalDate.now();
        Map<String, String> contractDates = new HashMap<>();
        contractDates.put("date_debut_contrat", testDealContractStart.toString());
        contractDates.put("date_fin_contrat", testDealContractEnd.toString());
        List<String> properties = Arrays.asList("dealname","dealstage","pipeline","date_debut_contrat","date_fin_contrat","amount");
        long dealId = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, contractDates))
                .getId();
        createdDealId = dealId;

        HSDeal deal = hubSpot.deal().getByIdAndProperties(dealId, properties);

        assertThat(deal.getId()).isEqualTo(dealId);
        assertThat(deal.getDealName()).isEqualTo(testDealName);
    }

    @Test
    void getDeal_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertThat(hubSpot.deal().getByID(id)).isNull();
        assertThat(hubSpot.deal().getByID(id)).isNull();
    }


    @Test
    void patch_phone_Deal_Test() throws Exception {
        String testValue = "new phone number";
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setDealName(testValue);
        HSDeal result = hubSpot.deal().patch(editDeal);

        assertThat(result.getDealName()).isEqualTo(editDeal.getDealName());
    }


    @Test
    void patchDealIncorrectPredefinedFieldValue_Test() throws Exception {
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setDealStage("test invalid");

        assertThatThrownBy(() -> hubSpot.deal().patch(editDeal))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("");
    }

    @Test
    void patchDealMissedRequiredProperty_Test() throws Exception {
        String testProperty = "linkedinbio";
        String testValue = "Test value 1";
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();
        deal.setProperty(testProperty, testValue);
        HSDeal missedDeal = new HSDeal(deal.getDealName(),
                                                deal.getDealStage(),
                                                deal.getPipeline(),
                                                deal.getAmount(),
                                                deal.getCloseDate());

        assertThatThrownBy(() -> hubSpot.deal().patch(missedDeal))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchDealIncorrectProperty_Test() throws Exception {
        String testProperty = "badpropertyz";
        String testValue = "Test value 1";
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setProperty(testProperty, testValue);

        assertThatThrownBy(() -> hubSpot.deal().patch(editDeal))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchDeal_Bad_Stage_Test() {
        HSDeal deal = new HSDeal(testDealName, "wrong stage", testPipeline, testAmount, testCloseDate).setId(1);
        assertThatThrownBy(() -> {
            hubSpot.deal().create(deal);
            createdDealId = deal.getId();
        }).isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchDeal_Not_Found_Test() {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate).setId(-777);

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setDealName(deal.getDealName());

        assertThatThrownBy(() -> hubSpot.deal().patch(editDeal))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void deleteDeal_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal = hubSpot.deal().create(deal);
        createdDealId = deal.getId();

        hubSpot.deal().delete(deal);

        assertThat(hubSpot.deal().getByID(deal.getId())).isNull();

    }

    @Test
    void deleteDeal_by_id_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal = hubSpot.deal().create(deal);
        createdDealId = deal.getId();

        hubSpot.deal().delete(deal.getId());

        assertThat(hubSpot.deal().getByID(deal.getId())).isNull();

    }

    @Test
    void deleteDeal_No_ID_Test() {
        HSDeal deal = new HSDeal().setDealName(testDealName);

        assertThatThrownBy(() -> hubSpot.deal().delete(deal))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("Deal ID must be provided");
    }


    @Test
    void getDealListByIdAndProperties_Test() throws Exception {
        HSDeal deal = new HSDeal();
        HSDeal deal2 = new HSDeal();
        HSDeal deal3 = new HSDeal();
        HSDeal deal4 = new HSDeal();
        HSDeal deal5 = new HSDeal();
        try {
            deal = getNewTestDeal();
            deal2 = getNewTestDeal();
            deal3 = getNewTestDeal();
            deal4 = getNewTestDeal();
            deal5 = getNewTestDeal();

            List<String> properties = Arrays.asList("id", "name");

            List<HSDeal> deals = hubSpot.deal().getDealListByIdAndProperties(List.of(deal.getId(), deal2.getId(), deal3.getId(), deal4.getId(), deal5.getId()),
                    properties);

            assertThat(deals).isNotNull();
            assertThat(deals).hasSize(5);

            assertThat(deals.get(0).getId()).isEqualTo(deals.get(0).getId());
            assertThat(deals.get(1).getId()).isEqualTo(deals.get(1).getId());
        } finally {
            hubSpot.deal().delete(deal.getId());
            hubSpot.deal().delete(deal2.getId());
            hubSpot.deal().delete(deal3.getId());
            hubSpot.deal().delete(deal4.getId());
            hubSpot.deal().delete(deal5.getId());
        }
    }

    @Test
    void getAssociatedCompanies_Tests() throws Exception {
        Map<Long, List<Long>> savedDealsAndCompanies = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HSDeal deal = getNewTestDeal();
            List<Long> companies = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                HSCompany company = getNewTestCompany();
                hubSpot.association().dealToCompany(deal.getId(), company.getId());
                companies.add(company.getId());
            }
            savedDealsAndCompanies.put(deal.getId(), companies);
        }

        try {
            Map<Long, List<Long>> dealsCompanies = hubSpot.deal().getAssociatedCompanyIds(new ArrayList<>(savedDealsAndCompanies.keySet()));

            assertThat(dealsCompanies).isNotNull();
            assertThat(dealsCompanies).isNotEmpty();
            dealsCompanies.keySet().forEach(resDealId -> {
                assertThat(dealsCompanies.get(resDealId)).hasSameSizeAs(savedDealsAndCompanies.get(resDealId));
                assertThat(dealsCompanies.get(resDealId)).containsAll(savedDealsAndCompanies.get(resDealId));
            });
        } finally {
            savedDealsAndCompanies.forEach((key, value) -> {
                try {
                    hubSpot.deal().delete(key);
                    value.forEach(companyIds -> {
                        try {
                            hubSpot.company().delete(companyIds);
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
    void getAssociatedContacts_Tests() throws Exception {
        Map<Long, List<Long>> savedDealsAndContacts = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HSDeal deal = getNewTestDeal();
            List<Long> contacts = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                HSContact contact = getNewTestContact();
                hubSpot.association().dealToContact(deal.getId(), contact.getId());
                contacts.add(contact.getId());
            }
            savedDealsAndContacts.put(deal.getId(), contacts);
        }

        try {
            Map<Long, List<Long>> dealsContacts = hubSpot.deal().getAssociatedContactIds(new ArrayList<>(savedDealsAndContacts.keySet()));

            assertThat(dealsContacts).isNotNull();
            assertThat(dealsContacts).isNotEmpty();
            dealsContacts.keySet().forEach(resDealId -> {
                assertThat(dealsContacts.get(resDealId)).hasSameSizeAs(savedDealsAndContacts.get(resDealId));
                assertThat(dealsContacts.get(resDealId)).containsAll(savedDealsAndContacts.get(resDealId));
            });
        } finally {
            savedDealsAndContacts.forEach((key, value) -> {
                try {
                    hubSpot.deal().delete(key);
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
    void getAssociatedLineItems_Tests() throws Exception {
        Map<Long, List<Long>> savedDealsAndLineItems = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HSDeal deal = getNewTestDeal();
            List<Long> lineItems = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                HSLineItem lineItem = hubSpot.lineItem().create(new HSLineItem(testProductId, 1));
                hubSpot.association().dealToLineItem(deal.getId(), lineItem.getId());
                lineItems.add(lineItem.getId());
            }
            savedDealsAndLineItems.put(deal.getId(), lineItems);
        }

        try {
            Map<Long, List<Long>> dealsLineItems = hubSpot.deal().getAssociatedLineItemIds(new ArrayList<>(savedDealsAndLineItems.keySet()));

            assertThat(dealsLineItems).isNotNull();
            assertThat(dealsLineItems).isNotEmpty();
            dealsLineItems.keySet().forEach(resDealId -> {
                assertThat(dealsLineItems.get(resDealId)).hasSameSizeAs(savedDealsAndLineItems.get(resDealId));
                assertThat(dealsLineItems.get(resDealId)).containsAll(savedDealsAndLineItems.get(resDealId));
            });
        } finally {
            savedDealsAndLineItems.forEach((key, value) -> {
                try {
                    hubSpot.deal().delete(key);
                    value.forEach(lineItemId -> {
                        try {
                            hubSpot.lineItem().delete(lineItemId);
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
        return hubSpot.company().create(new HSCompany("TestCompany" + Instant.now().getEpochSecond(), "+33645218", "address", "10000", "city", "country", "description", "www.website.com"));
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

    private HSContact getNewTestContact() throws HubSpotException {
        return hubSpot.contact().create(new HSContact(UUID.randomUUID()+"@test.fr", "firstname", "IT", "02355", "customer"));
    }
}
