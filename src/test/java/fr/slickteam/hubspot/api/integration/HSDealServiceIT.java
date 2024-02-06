package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.service.HSDealService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HSDealServiceIT {


    private final String testDealName = "Test deal";
    private final String testDealStage = "qualifiedtobuy";
    private final String testPipeline = "default";
    private final Instant testCloseDate = Instant.now();
    private final BigDecimal testAmount = BigDecimal.valueOf(50);
    private Long createdDealId;
    private String testProductId = null;


    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        HSObject object = new HSObject();
        object.setProperty("price", testAmount.toString());
        object = hubSpot.hsService().createHSObject("/crm/v3/objects/products", object);

        testProductId = object.getProperty("hs_object_id");
    }


    @After
    public void tearDown() throws Exception {
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
    public void createDeal_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal = hubSpot.deal().create(deal);

        createdDealId = deal.getId();

        assertNotEquals(0L, deal.getId());
        assertEquals(deal.getDealName(), hubSpot.deal().getByID(deal.getId()).getDealName());
    }

    @Test
    public void createDeal_NetworkError_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);

        HSDealService mockHSDealService = mock(HSDealService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSDealService).create(deal);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.deal()).thenReturn(mockHSDealService);
        exception.expect(HubSpotException.class);
        mockHubSpot.deal().create(deal);
    }

    @Test
    public void createDealIncorrectProperty_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal.setProperty("badpropertyz", "Test value 1");

        exception.expect(HubSpotException.class);
        hubSpot.deal().create(deal);
    }

    @Test
    public void getDeal_Id_Test() throws Exception {
        long dealId = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate))
                .getId();
        createdDealId = dealId;

        HSDeal deal = hubSpot.deal().getByID(dealId);

        assertEquals(dealId, deal.getId());
        assertEquals(testDealName, deal.getDealName());
    }

    @Test
    public void getDeal_Id_And_Properties_Test() throws Exception {
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

        assertEquals(dealId, deal.getId());
        assertEquals(testDealName, deal.getDealName());
    }

    @Test
    public void getDeal_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpot.deal().getByID(id));
        assertNull(hubSpot.deal().getByID(id));
    }


    @Test
    public void patch_phone_Deal_Test() throws Exception {
        String test_value = "new phone number";
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setDealName(test_value);
        HSDeal result = hubSpot.deal().patch(editDeal);

        assertEquals(editDeal.getDealName(), result.getDealName());
    }


    @Test
    public void patchDealIncorrectPredefinedFieldValue_Test() throws Exception {
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setDealStage("test invalid");

        exception.expect(HubSpotException.class);
        exception.expectMessage("");
        hubSpot.deal().patch(editDeal);
    }

    @Test
    public void patchDealMissedRequiredProperty_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value = "Test value 1";
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();
        deal.setProperty(test_property, test_value);
        HSDeal missedDeal = new HSDeal(deal.getDealName(),
                                                deal.getDealStage(),
                                                deal.getPipeline(),
                                                deal.getAmount(),
                                                deal.getCloseDate());

        exception.expect(HubSpotException.class);
        hubSpot.deal().patch(missedDeal);
    }

    @Test
    public void patchDealIncorrectProperty_Test() throws Exception {
        String test_property = "badpropertyz";
        String test_value = "Test value 1";
        HSDeal deal = hubSpot
                .deal()
                .create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate));
        createdDealId = deal.getId();

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setProperty(test_property, test_value);

        exception.expect(HubSpotException.class);
        hubSpot.deal().patch(editDeal);
    }

    @Test
    public void patchDeal_Bad_Stage_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, "wrong stage", testPipeline, testAmount, testCloseDate).setId(1);
        exception.expect(HubSpotException.class);
        hubSpot.deal().create(deal);
        createdDealId = deal.getId();
    }

    @Test
    public void patchDeal_Not_Found_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate).setId(-777);

        HSDeal editDeal = new HSDeal();
        editDeal.setId(deal.getId());
        editDeal.setDealName(deal.getDealName());

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");

        hubSpot.deal().patch(editDeal);
    }

    @Test
    public void deleteDeal_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal = hubSpot.deal().create(deal);
        createdDealId = deal.getId();

        hubSpot.deal().delete(deal);

        assertNull(hubSpot.deal().getByID(deal.getId()));

    }

    @Test
    public void deleteDeal_by_id_Test() throws Exception {
        HSDeal deal = new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testCloseDate);
        deal = hubSpot.deal().create(deal);
        createdDealId = deal.getId();

        hubSpot.deal().delete(deal.getId());

        assertNull(hubSpot.deal().getByID(deal.getId()));

    }

    @Test
    public void deleteDeal_No_ID_Test() throws Exception {
        HSDeal deal = new HSDeal().setDealName(testDealName);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("Deal ID must be provided"));
        hubSpot.deal().delete(deal);
    }


    @Test
    public void getDealListByIdAndProperties_Test() throws Exception {
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

            assertNotNull(deals);
            assertEquals(5, deals.size());

            assertEquals(deals.get(0).getId(), deals.get(0).getId());
            assertEquals(deals.get(1).getId(), deals.get(1).getId());
        } finally {
            hubSpot.deal().delete(deal.getId());
            hubSpot.deal().delete(deal2.getId());
            hubSpot.deal().delete(deal3.getId());
            hubSpot.deal().delete(deal4.getId());
            hubSpot.deal().delete(deal5.getId());
        }
    }

    @Test
    public void getAssociatedCompanies_Tests() throws Exception {
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

            assertNotNull(dealsCompanies);
            assertFalse(dealsCompanies.isEmpty());
            dealsCompanies.keySet().forEach(resDealId -> {
                assertEquals(dealsCompanies.get(resDealId).size(), savedDealsAndCompanies.get(resDealId).size());
                assertTrue(dealsCompanies.get(resDealId).containsAll(savedDealsAndCompanies.get(resDealId)));
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
    public void getAssociatedContacts_Tests() throws Exception {
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

            assertNotNull(dealsContacts);
            assertFalse(dealsContacts.isEmpty());
            dealsContacts.keySet().forEach(resDealId -> {
                assertEquals(dealsContacts.get(resDealId).size(), savedDealsAndContacts.get(resDealId).size());
                assertTrue(dealsContacts.get(resDealId).containsAll(savedDealsAndContacts.get(resDealId)));
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
    public void getAssociatedLineItems_Tests() throws Exception {
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

            assertNotNull(dealsLineItems);
            assertFalse(dealsLineItems.isEmpty());
            dealsLineItems.keySet().forEach(resDealId -> {
                assertEquals(dealsLineItems.get(resDealId).size(), savedDealsAndLineItems.get(resDealId).size());
                assertTrue(dealsLineItems.get(resDealId).containsAll(savedDealsAndLineItems.get(resDealId)));
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
