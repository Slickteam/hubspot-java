package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSAssocationTypeInput;
import fr.slickteam.hubspot.api.domain.HSAssociationTypeEnum;
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
        public static final String V4_OBJECT = "/crm/v4/objects/";
        public static final String V4_ASSOCIATION = "/crm/v4/associations/";
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
    private static final String BATCH = "batch/";
    private static final String CREATE = "create/";
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
     * @param compagnyId - ID of the compagny to link
     * @param associatedCompanyId - ID of the Associated company to link
     * @param typeEnum - Association type from company to associated company
     * @throws HubSpotException - if HTTP call fails
     */
    public void companyToCompany(long compagnyId, long associatedCompanyId, HSAssociationTypeEnum typeEnum) throws HubSpotException {
        HSAssocationTypeInput assocationTypeInput = new HSAssocationTypeInput().setType(typeEnum);
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" +
                "    {\n" +
                "      \"from\": {\n" +
                "        \"id\": \""+compagnyId+"\"\n" +
                "      },\n" +
                "      \"to\": {\n" +
                "        \"id\": \""+associatedCompanyId+"\"\n" +
                "      },\n" +
                "      \"types\": [\n" + assocationTypeInput.getJsonAssociationType() +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String url =
                BasePath.V4_ASSOCIATION + COMPANIES + COMPANIES + BATCH + CREATE;
        httpService.postRequest(url, associationProperties);
    }

    /**
     * Get companies associated to a contact
     *
     * @param contactId - ID of the contact to link
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getContactCompanyIdList(long contactId) throws HubSpotException {
        String url =
                BasePath.V4_OBJECT + CONTACTS + contactId + "/" + ASSOCIATION + COMPANIES;
        try {
            return parseJsonArrayToIdList((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No associated companies found for this user")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Get contacts associated to a company
     *
     * @param companyId - ID of the company to link
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getCompanyContactIdList(long companyId) throws HubSpotException {
        String url =
                BasePath.V4_OBJECT + COMPANIES + companyId + "/" + ASSOCIATION + CONTACTS;
        try {
            return parseJsonArrayToIdList((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No associated contact found for this company")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Parse a Json array to a list of long Ids
     **/
    private List<Long> parseJsonArrayToIdList(JSONObject jsonObject) {
        JSONArray results = (JSONArray) jsonObject.get("results");
        List<Long> idList = new ArrayList<>();
        for (Object result : results) {
            JSONObject resultObj = (JSONObject) result;
            Long id = Long.valueOf(resultObj.get("toObjectId").toString());
            idList.add(id);
        }
        return idList;
    }


    /**
     * Get all companies ids associated to a company
     *
     * @param companyId - ID of the company
     * @throws HubSpotException - if HTTP call fails
     */
    public List<JSONObject> getCompaniesToCompany(long companyId) throws HubSpotException {
        String url =
                BasePath.V4_OBJECT + COMPANIES + companyId + "/" + ASSOCIATION + COMPANIES;
        try {
            return parseJsonResultToList((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No company associated found for this company")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }
    public List<JSONObject> parseJsonResultToList (JSONObject jsonObject) {
        JSONArray results = (JSONArray) jsonObject.get("results");
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (Object result : results) {
            JSONObject resultObj = (JSONObject) result;
            jsonObjectList.add(resultObj);
        }
        return jsonObjectList;
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
