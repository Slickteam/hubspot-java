package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSDeal;
import fr.slickteam.hubspot.api.service.HSDealService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HSDealServiceIT {


    private final String testDealName = "Test deal";
    private final String testDealStage = "qualifiedtobuy";
    private final String testPipeline = "default";
    private final LocalDateTime testCloseDate = LocalDateTime.now();
    private final BigDecimal testAmount = BigDecimal.valueOf(50);
    private Long createdDealId;


    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }


    @After
    public void tearDown() throws Exception {
        if (createdDealId != null) {
            hubSpot.deal().delete(createdDealId);
        }
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
}
