package fr.slickteam.hubspot.api.integration;
import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class HSAssociationIT {
    private Long createdContactId;
    private List<Long> createdCompanyIds = new ArrayList<>();
    private Long createdAssociatedCompanyId;
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
    private final Instant testClosedate = Instant.now();
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
        for (long createdCompanyId : createdCompanyIds) {
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
        createdContactId = contact.getId();
        createdCompanyIds.add(company.getId());

        hubSpot.association().contactToCompany(createdContactId, company.getId());
    }

    @Test
    public void associate_contact_to_company_bad_company_id_exception() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                                                                   testPhoneNumber,
                                                                   testAddress,
                                                                   testZip,
                                                                   testCity,
                                                                   testCountry));

        createdCompanyIds.add(company.getId());

        exception.expect(HubSpotException.class);
        exception.expectMessage("internal error");
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
        exception.expectMessage("internal error");
        hubSpot.association().contactToCompany(contact.getId(), -777);
    }

    @Test
    public void associate_contact_to_companyList_success() throws HubSpotException {
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                testFirstname,
                testLastname,
                testPhoneNumber,
                testLifeCycleStage));

        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        HSCompany company2 = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdContactId = contact.getId();
        createdCompanyIds.add(company.getId());
        createdCompanyIds.add(company2.getId());

        hubSpot.association().contactToCompanyList(createdContactId, createdCompanyIds);
    }

    @Test
    public void associate_company_to_company_success() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));
        HSCompany associatedCompany = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdCompanyIds.add(company.getId());
        createdAssociatedCompanyId = associatedCompany.getId();
        hubSpot.association().companyToCompany(company.getId(), createdAssociatedCompanyId, HSAssociationTypeEnum.PARENT);
    }

    @Test
    public void associate_company_to_company_bad_company_id_exception() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));
        HSCompany associatedCompany = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdCompanyIds.add(company.getId());
        createdAssociatedCompanyId = associatedCompany.getId();
        exception.expect(HubSpotException.class);
        hubSpot.association().companyToCompany(-777, createdAssociatedCompanyId, HSAssociationTypeEnum.PARENT);
    }

    @Test
    public void associate_company_to_childCompanyList_success() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));
        HSCompany associatedCompany1 = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));
        HSCompany associatedCompany2 = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdCompanyIds.add(company.getId());
        createdCompanyIds.add(associatedCompany1.getId());
        createdCompanyIds.add(associatedCompany2.getId());

        List<Long> associatedCompanyIdList = new ArrayList<>();
        associatedCompanyIdList.add(associatedCompany1.getId());
        associatedCompanyIdList.add(associatedCompany2.getId());
        hubSpot.association().companyToChildCompanyList(company.getId(), associatedCompanyIdList);
    }

    @Test
    public void remove_association_company_to_childCompanyList_success() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));
        HSCompany associatedCompany1 = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));
        HSCompany associatedCompany2 = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdCompanyIds.add(company.getId());
        createdCompanyIds.add(associatedCompany1.getId());
        createdCompanyIds.add(associatedCompany2.getId());
        List<Long> associatedCompanyIdList = new ArrayList<>();
        associatedCompanyIdList.add(associatedCompany1.getId());
        associatedCompanyIdList.add(associatedCompany2.getId());
        hubSpot.association().companyToChildCompanyList(company.getId(), associatedCompanyIdList);
        hubSpot.association().removeCompanyToChildCompanyList(company.getId(), associatedCompanyIdList);
    }


    @Test
    public void remove_association_contact_to_company_success() throws HubSpotException {
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                testFirstname,
                testLastname,
                testPhoneNumber,
                testLifeCycleStage));
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdContactId = contact.getId();
        createdCompanyIds.add(company.getId());

        hubSpot.association().contactToCompany(contact.getId(), company.getId());
        hubSpot.association().removeContactToCompany(createdContactId, company.getId());
    }

    @Test
    public void remove_association_contact_to_company_list_success() throws HubSpotException {
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                testFirstname,
                testLastname,
                testPhoneNumber,
                testLifeCycleStage));

        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        HSCompany company2 = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdContactId = contact.getId();
        createdCompanyIds.add(company.getId());
        createdCompanyIds.add(company2.getId());

        hubSpot.association().contactToCompany(createdContactId, company.getId());
        hubSpot.association().contactToCompany(createdContactId, company2.getId());
        hubSpot.association().removeContactToCompanyList(createdContactId, createdCompanyIds);
    }


    @Test
    public void associate_get_contact_company_id_list_success() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdCompanyIds.add(company.getId());

        hubSpot.association().getCompanyContactIdList(company.getId());
    }

    @Test
    public void associate_get_companies_to_company_success() throws HubSpotException {
        HSCompany company = hubSpot.company().create(new HSCompany(testEmail1,
                testPhoneNumber,
                testAddress,
                testZip,
                testCity,
                testCountry));

        createdCompanyIds.add(company.getId());

        hubSpot.association().getCompaniesToCompany(company.getId());
    }

    @Test
    public void associate_deal_to_contact_success() throws HubSpotException {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));
        HSContact contact = hubSpot.contact().create(new HSContact(testEmail1,
                                                                   testFirstname,
                                                                   testLastname,
                                                                   testPhoneNumber,
                                                                   testLifeCycleStage));

        createdDealId = deal.getId();
        createdContactId = contact.getId();

        hubSpot.association().dealToContact(createdDealId, createdContactId);
    }

    @Test
    public void associate_deal_to_contact_bad_company_id_exception() throws HubSpotException {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));

        createdDealId = deal.getId();

        exception.expect(HubSpotException.class);
        exception.expectMessage("Not Found");
        hubSpot.association().dealToContact(createdDealId, -777);
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
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));
        HSLineItem lineItem = hubSpot.lineItem().create(new HSLineItem(testProductId, testQuantity));

        createdDealId = deal.getId();
        createdLineItemId = lineItem.getId();

        hubSpot.association().dealToLineItem(deal.getId(), lineItem.getId());
    }

    @Test
    public void associate_deal_to_line_item_bad_company_id_exception() throws HubSpotException {
        HSDeal deal = hubSpot.deal().create(new HSDeal(testDealName, testDealStage, testPipeline, testAmount, testClosedate));

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
