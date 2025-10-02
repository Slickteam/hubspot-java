package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSDeal;
import fr.slickteam.hubspot.api.domain.HSLineItem;
import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.service.HSLineItemService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class HSLineItemServiceIT {

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

    @BeforeEach
    void setUp() throws HubSpotException, IOException {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        HSObject object = new HSObject();
        object.setProperty("price", testAmount.toString());
        object = hubSpot.hsService().createHSObject("/crm/v3/objects/products", object);

        testProductId = object.getProperty("hs_object_id");
    }

    @AfterEach
    void tearDown() throws Exception {
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
    void createLineItem_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);
        lineItem = hubSpot.lineItem().create(lineItem);

        createdLineItemId = lineItem.getId();

        assertThat(lineItem.getId()).isNotZero();
        assertThat(hubSpot.lineItem().getByID(lineItem.getId()).getHsProductId()).isEqualTo(lineItem.getHsProductId());
    }

    @Test
    void createLineItem_NetworkError_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);

        HSLineItemService mockHSLineItemService = mock(HSLineItemService.class);
        doThrow(new HubSpotException("Network error")).when(mockHSLineItemService).create(lineItem);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.lineItem()).thenReturn(mockHSLineItemService);
        assertThatThrownBy(() -> mockHubSpot.lineItem().create(lineItem))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void getLineItem_Id_Test() throws Exception {
        long lineItemId = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity))
                .getId();
        createdLineItemId = lineItemId;

        HSLineItem lineItem = hubSpot.lineItem().getByID(lineItemId);

        assertThat(lineItem.getId()).isEqualTo(lineItemId);
        assertThat(lineItem.getHsProductId()).isEqualTo(testProductId);
    }

    @Test
    void getLineItem_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertThat(hubSpot.lineItem().getByID(id)).isNull();
    }


    @Test
    void patch_quantity_LineItem_Test() throws Exception {
        long testValue = 50;
        HSLineItem lineItem = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();

        HSLineItem editLineItem = new HSLineItem();
        editLineItem.setId(lineItem.getId());
        editLineItem.setQuantity(testValue);
        HSLineItem result = hubSpot.lineItem().patch(editLineItem);

        assertThat(result.getHsProductId()).isEqualTo(editLineItem.getHsProductId());
    }


    @Test
    void patchLineItemIncorrectPredefinedFieldValue_Test() throws Exception {
        HSLineItem lineItem = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();

        HSLineItem editLineItem = new HSLineItem();
        editLineItem.setId(lineItem.getId());
        editLineItem.setHsProductId("wrong product id");

        assertThatThrownBy(() -> hubSpot.lineItem().patch(editLineItem))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("");
    }

    @Test
    void patchLineItemMissedRequiredProperty_Test() throws Exception {
        String testProperty = "linkedinbio";
        String testValue = "Test value 1";
        HSLineItem lineItem = hubSpot
                .lineItem()
                .create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();
        HSLineItem missedLineItem = new HSLineItem(lineItem.getHsProductId(),
                                                   lineItem.getQuantity());

        missedLineItem.setProperty(testProperty, testValue);

        assertThatThrownBy(() -> hubSpot.lineItem().patch(missedLineItem))
            .isInstanceOf(HubSpotException.class);
    }

    @Test
    void patchLineItem_Not_Found_Test() {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity).setId(-777);


        HSLineItem editLineItem = new HSLineItem();
        editLineItem.setId(lineItem.getId());
        editLineItem.setHsProductId(lineItem.getHsProductId());

        assertThatThrownBy(() -> hubSpot.lineItem().patch(editLineItem))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void deleteLineItem_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);
        lineItem = hubSpot.lineItem().create(lineItem);
        createdLineItemId = lineItem.getId();

        hubSpot.lineItem().delete(lineItem);

        assertThat(hubSpot.lineItem().getByID(lineItem.getId())).isNull();

    }

    @Test
    void deleteLineItem_by_id_Test() throws Exception {
        HSLineItem lineItem = new HSLineItem(testProductId, testQuantity);
        lineItem = hubSpot.lineItem().create(lineItem);
        createdLineItemId = lineItem.getId();

        hubSpot.lineItem().delete(lineItem.getId());

        assertThat(hubSpot.lineItem().getByID(lineItem.getId())).isNull();

    }

    @Test
    void deleteLineItem_No_ID_Test() {
        HSLineItem lineItem = new HSLineItem().setHsProductId(testProductId);

        assertThatThrownBy(() -> hubSpot.lineItem().delete(lineItem))
            .isInstanceOf(HubSpotException.class)
            .hasMessageContaining("Line item ID must be provided");
    }

    @Test
    void getHSLineItemsForHSDeal_success() throws Exception {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));
        createdDealId = deal.getId();
        HSLineItem lineItem = hubSpot.lineItem().create(new HSLineItem(testProductId, testQuantity));
        createdLineItemId = lineItem.getId();

        hubSpot.association().dealToLineItem(deal.getId(), lineItem.getId());

        sleep(8000);
        List<HSLineItem> results = hubSpot.deal().getHSLineItemsForHSDeal(deal);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getHsProductId()).isEqualTo(lineItem.getHsProductId());
        assertThat(results.get(0).getId()).isEqualTo(lineItem.getId());
        assertThat(results.get(0).getQuantity()).isEqualTo(lineItem.getQuantity());
    }


    @Test
    void getHSLineItemsForHSDeal_without_line_items_success() throws Exception {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));
        createdDealId = deal.getId();

        List<HSLineItem> results = hubSpot.deal().getHSLineItemsForHSDeal(deal);

        assertThat(results).isEmpty();
    }
}
