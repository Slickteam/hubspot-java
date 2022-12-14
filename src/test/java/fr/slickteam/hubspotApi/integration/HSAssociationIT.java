package fr.slickteam.hubspotApi.integration;

import fr.slickteam.hubspotApi.domain.*;
import fr.slickteam.hubspotApi.service.HubSpot;
import fr.slickteam.hubspotApi.utils.Helper;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.time.Instant;


public class HSAssociationIT {
    private Long createdContactId;
    private Long createdCompanyId;
    private Long createdDealId;
    private Long createdLineItemId;

    private String testEmail1;
    private final String testFirstname = "Testfristname";
    private final String testLastname = "Testlastname";
    private final String testPhoneNumber = "TestPhoneNumber";
    private final String testLifeCycleStage = "customer";

    private final String testAddress = "address";
    private final String testZip = "zip";
    private final String testCity = "city";
    private final String testCountry = "country";

    private final String testDealName = "Test deal";
    private final String testDealStage = "qualifiedtobuy";
    private final String testPipeline = "default";
    private final BigDecimal testAmount = BigDecimal.valueOf(50);

    private String testProductId = null;
    private final long testQuantity = 1;

    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
        HSObject object = new HSObject();
        object.setProperty("price", Long.toString(75));
        object = hubSpot.hsService().createHSObject("/crm/v3/objects/products", object);
        testProductId = object.getProperty("hs_object_id");

        testEmail1 = "test1" + Instant.now().getEpochSecond() + "@mail.com";
    }

    @After
    public void tearDown() throws Exception {
        if (createdContactId != null) {
            hubSpot.contact().delete(createdContactId);
        }
        if (createdCompanyId != null) {
            hubSpot.company().delete(createdCompanyId);
        }
        if (createdDealId != null) {
            hubSpot.deal().delete(createdDealId);
        }
        if (createdLineItemId != null) {
            hubSpot.lineItem().delete(createdLineItemId);
        }
        if (testProductId != null) {
            hubSpot.hsService().deleteHSObject("/crm/v3/objects/products/" + testProductId);
            testProductId = null;
        }
    }

    @Test
    public void associate_contact_to_company_success() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                                                                   testPhoneNumber,
                                                                   testAddress,
                                                                   testZip,
                                                                   testCity,
                                                                   testCountry));
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                                                                   testFirstname,
                                                                   testLastname,
                                                                   testPhoneNumber,
                                                                   testLifeCycleStage));

        createdCompanyId = company.getId();
        createdContactId = contact.getId();

        hubSpot.association().contactToCompany(contact.getId(), company.getId());
    }

    @Test
    public void associate_contact_to_company_bad_company_id_exception() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                                                                   testPhoneNumber,
                                                                   testAddress,
                                                                   testZip,
                                                                   testCity,
                                                                   testCountry));

        createdCompanyId = company.getId();

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.association().contactToCompany(-777, company.getId());
    }

    @Test
    public void associate_contact_to_company_bad_contact_id_exception() throws HubSpotException {
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                                                                   testFirstname,
                                                                   testLastname,
                                                                   testPhoneNumber,
                                                                   testLifeCycleStage));

        createdContactId = contact.getId();


        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.association().contactToCompany(contact.getId(), -777);
    }

    @Test
    public void associate_deal_to_contact_success() throws HubSpotException {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount));
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                                                                   testFirstname,
                                                                   testLastname,
                                                                   testPhoneNumber,
                                                                   testLifeCycleStage));

        createdDealId = deal.getId();
        createdContactId = contact.getId();

        hubSpot.association().dealToContact(deal.getId(), contact.getId());
    }

    @Test
    public void associate_deal_to_contact_bad_company_id_exception() throws HubSpotException {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount));

        createdDealId = deal.getId();

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.association().dealToContact(deal.getId(), -777);
    }

    @Test
    public void associate_deal_to_contact_bad_contact_id_exception() throws HubSpotException {
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                                                                   testFirstname,
                                                                   testLastname,
                                                                   testPhoneNumber,
                                                                   testLifeCycleStage));

        createdContactId = contact.getId();


        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.association().dealToContact(-777, contact.getId());
    }

    @Test
    public void associate_deal_to_line_item_success() throws HubSpotException {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount));
        HSLineItem lineItem = hubSpot.lineItem().create(new HSLineItem(testProductId, testQuantity));

        createdDealId = deal.getId();
        createdLineItemId = lineItem.getId();

        hubSpot.association().dealToLineItem(deal.getId(), lineItem.getId());
    }

    @Test
    public void associate_deal_to_line_item_bad_company_id_exception() throws HubSpotException {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount));

        createdDealId = deal.getId();

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.association().dealToLineItem(deal.getId(), -777);
    }

    @Test
    public void associate_deal_to_line_item_bad_contact_id_exception() throws HubSpotException {
        HSLineItem lineItem = hubSpot.lineItem().create(new HSLineItem(testProductId, testQuantity));


        createdLineItemId = lineItem.getId();


        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.association().dealToLineItem(-777, lineItem.getId());
    }
}
