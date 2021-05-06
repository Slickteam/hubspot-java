package fr.slickteam.hubspotApi.service;

import fr.slickteam.hubspotApi.utils.HubSpotHelper;
import fr.slickteam.hubspotApi.utils.HubSpotProperties;

import java.io.IOException;
import java.util.Properties;

public class HubSpot {

    private final HttpService httpService;

    public HubSpot(HubSpotProperties properties) throws IOException {

        httpService = new HttpService(properties);
    }

    public HSCompanyService company() {
        return new HSCompanyService(httpService);
    }

    public HSContactService contact() {
        return new HSContactService(httpService);
    }

    public HSDealService deal() {
        return new HSDealService(httpService);
    }

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
