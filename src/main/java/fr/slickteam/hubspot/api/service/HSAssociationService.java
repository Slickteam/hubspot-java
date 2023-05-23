package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * HSAssociationService - HubSpot association service
 * Service to associate hubspot objects.
 */
public class HSAssociationService {
    private static class BasePath {
        public static final String V3 = "/crm/v3/objects/";
        public static final String V4 = "/crm/v4/objects/";
    }

    private static final String CONTACT = "contact/";
    private static final String CONTACTS = "contacts/";
    private static final String COMPANIES = "companies/";
    private static final String DEAL = "deal/";
    private static final String LINE_ITEM = "line_item/";

    private static final String DEAL_TO_LINE_ITEM = "deal_to_line_item";
    private static final String DEAL_TO_CONTACT = "deal_to_contact";
    private static final String CONTACT_TO_COMPANY = "contact_to_company";


    private static final String ASSOCIATION = "associations/";

    private final HttpService httpService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSAssociationService(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * Associate a contact and a company
     *
     * @param contactId - ID of the contact to link
     * @param companyId - ID of the company to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void contactToCompany(long contactId, long companyId) throws HubSpotException {
        String url =
                BasePath.V3 + CONTACTS + contactId + "/" + ASSOCIATION + COMPANIES + companyId + "/" + CONTACT_TO_COMPANY;

        httpService.putRequest(url);
    }

    /**
     * Associate a contact and a company
     *
     * @param contactId - ID of the contact to link
     * @throws HubSpotException - if HTTP call fails
     */
    public List<String> contactToCompanyIdList(long contactId) throws HubSpotException {
        String url =
                BasePath.V4 + CONTACTS + contactId + "/" + ASSOCIATION + COMPANIES;
        return getCompanyIdList(url);
    }

    private List<String> getCompanyIdList(String url) throws HubSpotException {
        try {
            return companiesDataToIdList((JSONArray) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Parse company json List from HubSpot API response to id list
     *
     * @param jsonArray - company array from HubSpot API response
     * @return a list of companies id
     */
    public List<String> companiesDataToIdList(JSONArray jsonArray) {
        List<String> companyIdList = new ArrayList<>();
        for (Object company : jsonArray) {
            JSONObject jsonCompany = (JSONObject) company;
            long companyId = jsonCompany.getLong("id");
            companyIdList.add(Long.toString(companyId));
        }
        return companyIdList;
    }

    /**
     * Associate a deal and a contact
     *
     * @param dealId - ID of the deal to link
     * @param contactId - ID of the contact to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void dealToContact(long dealId, long contactId) throws HubSpotException {
        String url =
                BasePath.V3 + DEAL + dealId + "/" + ASSOCIATION + CONTACT + contactId + "/" + DEAL_TO_CONTACT;

        httpService.putRequest(url);
    }

    /**
     * Associate a deal and a lineItem
     *
     * @param dealId - ID of the deal to link
     * @param lineItemId - ID of the lineItem to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void dealToLineItem(long dealId, long lineItemId) throws HubSpotException {
        String url =
                BasePath.V3 + DEAL + dealId + "/" + ASSOCIATION + LINE_ITEM + lineItemId + "/" + DEAL_TO_LINE_ITEM;

        httpService.putRequest(url);
    }
}
