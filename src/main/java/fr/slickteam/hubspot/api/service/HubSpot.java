package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.utils.HubSpotProperties;

import java.io.IOException;

/**
 * The type Hub spot.
 */
public class HubSpot {

    private final HttpService httpService;

    /**
     * Instantiates a new Hub spot.
     *
     * @param properties the properties
     * @throws IOException the io exception
     */
    public HubSpot(HubSpotProperties properties) throws IOException {

        httpService = new HttpService(properties);
    }

    /**
     * Company hs company service.
     *
     * @return the hs company service
     */
    public HSCompanyService company() {
        HSContactService contactService = new HSContactService(httpService);
        HSCompanyService companyService = new HSCompanyService(httpService);
        companyService.setContactService(contactService);
        return companyService;
    }

    /**
     * Contact hs contact service.
     *
     * @return the hs contact service
     */
    public HSContactService contact() {
        HSCompanyService companyService = new HSCompanyService(httpService);
        HSContactService contactService = new HSContactService(httpService);
        contactService.setCompanyService(companyService);
        return contactService;
    }

    /**
     * Deal hs deal service.
     *
     * @return the hs deal service
     */
    public HSDealService deal() {
        return new HSDealService(httpService);
    }

    /**
     * Pipeline hs pipeline service.
     *
     * @return the hs pipeline service
     */
    public HSPipelineService pipeline() {
        return new HSPipelineService(httpService);
    }

    /**
     * Line item hs line item service.
     *
     * @return the hs line item service
     */
    public HSLineItemService lineItem() {
        return new HSLineItemService(httpService);
    }

    /**
     * Quote hs quote service.
     *
     * @return the hs quote service
     */
    public HSQuoteService quote() {
        return new HSQuoteService(httpService);
    }

    /**
     * Service hs service.
     *
     * @return the hs service
     */
    public HSService hsService() {
        return new HSService(httpService);
    }

    /**
     * Association hs association service.
     *
     * @return the hs association service
     */
    public HSAssociationService association() {
        return new HSAssociationService(httpService);
    }

    /**
     * Product hs product service.
     *
     * @return the hs product service
     */
    public HSProductService product() {
        return new HSProductService(httpService);
    }

    /**
     * Stage hs stage service.
     *
     * @return the hs stage service
     */
    public HSStageService stage() {
        return new HSStageService(httpService);
    }
}
