package fr.slickteam.hubspotApi.service;

import fr.slickteam.hubspotApi.utils.HubSpotException;

public class HSAssociationService {

    private static final String BASE_PATH = "/crm/v3/objects/";
    private static final String CONTACT = "contact/";
    private static final String CONTACTS = "contacts/";
    private static final String COMPANIES = "companies/";
    private static final String DEAL = "deal/";
    private static final String LINE_ITEM = "line_item/";

    private static final String DEAL_TO_LINE_ITEM = "deal_to_line_item";
    private static final String DEAL_TO_CONTACT = "deal_to_contact";
    private static final String CONTACT_TO_COMPANY = "contact_to_company";

    private static final String ASSOCIATION = "associations/";

    private HttpService httpService;
    private HSService hsService;

    public HSAssociationService(HttpService httpService) {
        this.httpService = httpService;
        this.hsService = new HSService(httpService);
    }

    public void contactToCompany(long contactId, long companyId) throws HubSpotException {
        String url =
                BASE_PATH + CONTACTS + contactId + "/" + ASSOCIATION + COMPANIES + companyId + "/" + CONTACT_TO_COMPANY;

        httpService.putRequest(url);
    }

    public void dealToContact(long dealId, long contactId) throws HubSpotException {
        String url =
                BASE_PATH + DEAL + dealId + "/" + ASSOCIATION + CONTACT + contactId + "/" + DEAL_TO_CONTACT;

        httpService.putRequest(url);
    }

    public void dealToLineItem(long dealId, long lineItemId) throws HubSpotException {
        String url =
                BASE_PATH + DEAL + dealId + "/" + ASSOCIATION + LINE_ITEM + lineItemId + "/" + DEAL_TO_LINE_ITEM;

        httpService.putRequest(url);
    }
}
