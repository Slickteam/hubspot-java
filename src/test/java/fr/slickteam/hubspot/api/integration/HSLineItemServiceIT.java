package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSDeal;
import fr.slickteam.hubspot.api.domain.HSLineItem;
import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.service.HSLineItemService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HSLineItemServiceIT {

    private final String testDealName = "Test deal";
    private final String testDealStage = "qualifiedtobuy";
    private final String testPipeline = "default";
    private final BigDecimal testAmount = BigDecimal.valueOf(75);
    private final Instant testClosedate = Instant.now();
    private String testProductId = null;
    private final long testQuantity = 1;
    private Long createdLineItemId;
    private Long createdDealId;

    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws HubSpotException, IOException {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        HSObject object = new HSObject();
        object.setProperty("price", testAmount.toString());
        object = hubSpot.hsService().createHSObject("/crm/v3/objects/products", object);

        testProductId = object.getProperty("hs_object_id");
    }

    @After
    public void tearDown() throws Exception {
        if (createdLineItemId != null) {
            hubSpot.lineItem().delete(createdLineItemId);
        }
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
    public void createLineItem_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);
        lineItem = hubSpot.lineItem().create(lineItem);

        createdLineItemId = lineItem.getId();

        assertNotEquals(0L, lineItem.getId());
        assertEquals(lineItem.getHsProductId(), hubSpot.lineItem().getByID(lineItem.getId()).getHsProductId());
    }

    @Test
    public void createLineItem_NetworkError_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);

        HSLineItemService mockHSLineItemService = mock(HSLineItemService.class);
        doThrow(new HubSpotException("Network error")).when(mockHSLineItemService).create(lineItem);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.lineItem()).thenReturn(mockHSLineItemService);
        exception.expect(HubSpotException.class);
        mockHubSpot.lineItem().create(lineItem);
    }

    @Test
    public void getLineItem_Id_Test() throws Exception {
        long lineItemId = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity))
                .getId();
        createdLineItemId = lineItemId;

        HSLineItem lineItem = hubSpot.lineItem().getByID(lineItemId);

        assertEquals(lineItemId, lineItem.getId());
        assertEquals(testProductId, lineItem.getHsProductId());
    }

    @Test
    public void getLineItem_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpot.lineItem().getByID(id));
    }


    @Test
    public void patch_quantity_LineItem_Test() throws Exception {
        long test_value = 50;
        HSLineItem lineItem = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();

        HSLineItem editLineItem = new HSLineItem();
        editLineItem.setId(lineItem.getId());
        editLineItem.setQuantity(test_value);
        HSLineItem result = hubSpot.lineItem().patch(editLineItem);

        assertEquals(editLineItem.getHsProductId(), result.getHsProductId());
    }


    @Test
    public void patchLineItemIncorrectPredefinedFieldValue_Test() throws Exception {
        HSLineItem lineItem = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();

        HSLineItem editLineItem = new HSLineItem();
        editLineItem.setId(lineItem.getId());
        editLineItem.setHsProductId("wrong product id");

        exception.expect(HubSpotException.class);
        exception.expectMessage("");
        hubSpot.lineItem().patch(editLineItem);
    }

    @Test
    public void patchLineItemMissedRequiredProperty_Test() throws Exception {
        String test_property = "linkedinbio";
        String test_value = "Test value 1";
        HSLineItem lineItem = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();
        HSLineItem missedLineItem = new HSLineItem(lineItem.getHsProductId(),
                                                   lineItem.getQuantity());

        missedLineItem.setProperty(test_property, test_value);

        exception.expect(HubSpotException.class);
        hubSpot.lineItem().patch(missedLineItem);
    }

    @Test
    public void patchLineItem_Not_Found_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity).setId(-777);


        HSLineItem editLineItem = new HSLineItem();
        editLineItem.setId(lineItem.getId());
        editLineItem.setHsProductId(lineItem.getHsProductId());

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");

        hubSpot.lineItem().patch(editLineItem);
    }

    @Test
    public void deleteLineItem_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);
        lineItem = hubSpot.lineItem().create(lineItem);
        createdLineItemId = lineItem.getId();

        hubSpot.lineItem().delete(lineItem);

        assertNull(hubSpot.lineItem().getByID(lineItem.getId()));

    }

    @Test
    public void deleteLineItem_by_id_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);
        lineItem = hubSpot.lineItem().create(lineItem);
        createdLineItemId = lineItem.getId();

        hubSpot.lineItem().delete(lineItem.getId());

        assertNull(hubSpot.lineItem().getByID(lineItem.getId()));

    }

    @Test
    public void deleteLineItem_No_ID_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem().setHsProductId(testProductId);

        exception.expect(HubSpotException.class);
        exception.expectMessage(StringContains.containsString("Line item ID must be provided"));
        hubSpot.lineItem().delete(lineItem);
    }

    @Test
    public void getHSLineItemsForHSDeal_success() throws Exception {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));
        createdDealId = deal.getId();
        HSLineItem lineItem = hubSpot.lineItem().create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();

        hubSpot.association().dealToLineItem(deal.getId(), lineItem.getId());

        sleep(5000);
        List<HSLineItem> results = hubSpot.deal().getHSLineItemsForHSDeal(deal);

        assertFalse(results.isEmpty());
        assertEquals(lineItem.getHsProductId(), results.get(0).getHsProductId());
        assertEquals(lineItem.getId(), results.get(0).getId());
        assertEquals(lineItem.getQuantity(), results.get(0).getQuantity());
    }


    @Test
    public void getHSLineItemsForHSDeal_without_line_items_success() throws Exception {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));
        createdDealId = deal.getId();

        List<HSLineItem> results = hubSpot.deal().getHSLineItemsForHSDeal(deal);

        assertEquals(0, results.size());
    }
}
