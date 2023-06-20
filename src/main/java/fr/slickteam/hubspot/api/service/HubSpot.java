package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.utils.HubSpotProperties;

import java.io.IOException;

public class HubSpot {

    private final HttpService httpService;

    public HubSpot(HubSpotProperties properties) throws IOException {

        httpService = new HttpService(properties);
    }

    public HSCompanyService company() {
        HSContactService contactService = new HSContactService(httpService);
        HSCompanyService companyService = new HSCompanyService(httpService);
        companyService.setContactService(contactService);
        return companyService;
    }

    public HSContactService contact() {
        HSCompanyService companyService = new HSCompanyService(httpService);
        HSContactService contactService = new HSContactService(httpService);
        contactService.setCompanyService(companyService);
        return contactService;
    }
    public HSDealService deal() {
        return new HSDealService(httpService);
    }

    public HSPipelineService pipeline() {return new HSPipelineService(httpService);}

    public HSLineItemService lineItem() {
        return new HSLineItemService(httpService);
    }

    public HSService hsService() {
        return new HSService(httpService);
    }

    public HSAssociationService association() {
        return new HSAssociationService(httpService);
    }

    public HSProductService product() {
        return new HSProductService(httpService);
    }

}
