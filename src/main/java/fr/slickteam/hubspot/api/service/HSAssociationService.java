package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSAssocationTypeInput;
import fr.slickteam.hubspot.api.domain.HSAssociationTypeEnum;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.Logger.Level.DEBUG;

/**
 * HSAssociationService - HubSpot association service
 * Service to associate hubspot objects.
 */
public class HSAssociationService {

    private static final System.Logger log = System.getLogger(HSAssociationService.class.getName());

    private static class BasePath {
        /**
         * The constant V3.
         */
        public static final String V3 = "/crm/v3/objects/";
        /**
         * The constant V3_ASSOCIATION.
         */
        public static final String V3_ASSOCIATION = "/crm/v3/associations/";
        /**
         * The constant V4_OBJECT.
         */
        public static final String V4_OBJECT = "/crm/v4/objects/";
        /**
         * The constant V4_ASSOCIATION.
         */
        public static final String V4_ASSOCIATION = "/crm/v4/associations/";
    }

    private static final String CONTACT = "contact/";
    private static final String CONTACTS = "contacts/";
    private static final String COMPANIES = "companies/";
    private static final String COMPANY = "company/";
    private static final String DEAL = "deal/";
    private static final String DEALS = "deals/";
    private static final String LINE_ITEM = "line_item/";
    private static final String LINE_ITEMS = "line_items/";
    private static final String QUOTES = "quotes/";
    private static final String DEAL_TO_LINE_ITEM = "deal_to_line_item";
    private static final String DEAL_TO_CONTACT = "deal_to_contact";
    private static final String DEAL_TO_COMPANY = "deal_to_company";
    private static final String ASSOCIATION = "associations/";
    private static final String ARCHIVE = "archive/";
    private static final String BATCH = "batch/";
    private static final String CREATE = "create/";
    private static final String READ = "read";
    private final HttpService httpService;
    private final HSService hsService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSAssociationService(HttpService httpService) {
        this.httpService = httpService;
        this.hsService = new HSService(httpService);
    }

    /**
     * Associate a contact and a company
     *
     * @param contactId - ID of the contact to link
     * @param companyId - ID of the company to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void contactToCompany(long contactId, long companyId) throws HubSpotException {
        log.log(DEBUG, "contactToCompany - contactId : " + contactId + " | companyId : " + companyId);
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" +
                "    {\n" +
                "      \"from\": {\n" +
                "        \"id\": \"" + contactId + "\"\n" +
                "      },\n" +
                "      \"to\": {\n" +
                "        \"id\": \"" + companyId + "\"\n" +
                "      },\n" +
                "      \"types\": [\n" +
                "         {\n" +
                "            \"associationCategory\": \"HUBSPOT_DEFINED\",\n" +
                "            \"associationTypeId\": 279\n" +
                "         }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String url =
                BasePath.V4_ASSOCIATION + CONTACTS + COMPANIES + BATCH + CREATE;
        httpService.postRequest(url, associationProperties);
    }

    /**
     * Associate a contact and a company list
     *
     * @param contactId     - ID of the contact to link
     * @param companyIdList - ID of the company to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void contactToCompanyList(long contactId, List<Long> companyIdList) throws HubSpotException {
        log.log(DEBUG, "contactToCompanyList - contactId : " + contactId + " | companyIdList : " + companyIdList);
        String inputProperties = getContactToCompanyInputList(contactId, companyIdList);
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" + inputProperties +
                "  ]\n" +
                "}";
        String url =
                BasePath.V4_ASSOCIATION + CONTACTS + COMPANIES + BATCH + CREATE;
        httpService.postRequest(url, associationProperties);
    }

    private String getContactToCompanyInputList(long contactId, List<Long> companyIdList) {
        log.log(DEBUG, "getContactToCompanyInputList - contactId : " + contactId + " | companyIdList : " + companyIdList);
        StringBuilder stringBuilder = new StringBuilder();

        int lastIndex = companyIdList.size() - 1;
        int index = 0;

        for (long companyId : companyIdList) {
            stringBuilder.append("    {\n")
                    .append("      \"from\": {\n")
                    .append("        \"id\": \"").append(contactId).append("\"\n")
                    .append("      },\n")
                    .append("      \"to\": {\n")
                    .append("        \"id\": \"").append(companyId).append("\"\n")
                    .append("      },\n")
                    .append("      \"types\": [\n")
                    .append("         {\n")
                    .append("            \"associationCategory\": \"HUBSPOT_DEFINED\",\n")
                    .append("            \"associationTypeId\": 279\n")
                    .append("         }\n")
                    .append("      ]\n")
                    .append("    }");

            if (index != lastIndex) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n");
            }
            index++;
        }
        return stringBuilder.toString();
    }

    /**
     * Associate a company and a company
     *
     * @param companyId           - ID of the compagny to link
     * @param associatedCompanyId - ID of the Associated company to link
     * @param typeEnum            - Association type for the company associated
     * @throws HubSpotException - if HTTP call fails
     */
    public void companyToCompany(long companyId, long associatedCompanyId, HSAssociationTypeEnum typeEnum) throws HubSpotException {
        log.log(DEBUG, "companyToCompany - companyId : " + companyId + " | associatedCompanyId : " + associatedCompanyId +
                " | type : " + typeEnum.name());
        HSAssocationTypeInput assocationTypeInput = new HSAssocationTypeInput().setType(typeEnum);
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" +
                "    {\n" +
                "      \"from\": {\n" +
                "        \"id\": \"" + companyId + "\"\n" +
                "      },\n" +
                "      \"to\": {\n" +
                "        \"id\": \"" + associatedCompanyId + "\"\n" +
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
     * Associate a company and a company child list
     *
     * @param companyId               - ID of the compagny to link
     * @param associatedCompanyIdList - ID List of the Associated companies to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void companyToChildCompanyList(long companyId, List<Long> associatedCompanyIdList) throws HubSpotException {
        log.log(DEBUG, "companyToChildCompanyList - companyId : " + companyId + " | associatedCompanyIdList : " + associatedCompanyIdList);
        String inputProperties = getCompanyToChildCompanyInputList(companyId, associatedCompanyIdList);
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" + inputProperties +
                "  ]\n" +
                "}";
        String url =
                BasePath.V4_ASSOCIATION + COMPANIES + COMPANIES + BATCH + CREATE;
        httpService.postRequest(url, associationProperties);
    }

    private String getCompanyToChildCompanyInputList(long companyId, List<Long> companyIdList) {
        log.log(DEBUG, "getCompanyToChildCompanyInputList - companyId : " + companyId + " | companyIdList : " + companyIdList);
        HSAssocationTypeInput assocationTypeInput = new HSAssocationTypeInput().setType(HSAssociationTypeEnum.CHILD);
        StringBuilder stringBuilder = new StringBuilder();

        int lastIndex = companyIdList.size() - 1;
        int index = 0;

        for (long associatedCompanyId : companyIdList) {
            stringBuilder.append("    {\n")
                    .append("      \"from\": {\n")
                    .append("        \"id\": \"").append(companyId).append("\"\n")
                    .append("      },\n")
                    .append("      \"to\": {\n")
                    .append("        \"id\": \"").append(associatedCompanyId).append("\"\n")
                    .append("      },\n")
                    .append("      \"types\": [\n" + assocationTypeInput.getJsonAssociationType())
                    .append("      ]\n")
                    .append("    }");

            if (index != lastIndex) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n");
            }
            index++;
        }
        return stringBuilder.toString();
    }

    /**
     * Remove associate between a contact and a company
     *
     * @param contactId - ID of the contact
     * @param companyId - ID of the company
     * @throws HubSpotException - if HTTP call fails
     */
    public void removeContactToCompany(long contactId, long companyId) throws HubSpotException {
        log.log(DEBUG, "removeContactToCompany - contactId : " + contactId + " | companyId : " + companyId);
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" +
                "    {\n" +
                "      \"from\": {\n" +
                "        \"id\": \"" + contactId + "\"\n" +
                "      },\n" +
                "      \"to\": {\n" +
                "        \"id\": \"" + companyId + "\"\n" +
                "      },\n" +
                "      \"type\": \"contact_to_company\"" +
                "    }\n" +
                "  ]\n" +
                "}";
        String url = BasePath.V3_ASSOCIATION + CONTACTS + COMPANIES + BATCH + ARCHIVE;
        httpService.postRequest(url, associationProperties);
    }


    /**
     * Remove associate between a contact and a list of companies
     *
     * @param contactId     - ID of the contact
     * @param companyIdList - ID list of the companies
     * @throws HubSpotException - if HTTP call fails
     */
    public void removeContactToCompanyList(long contactId, List<Long> companyIdList) throws HubSpotException {
        log.log(DEBUG, "removeContactToCompanyList - contactId : " + contactId + " | companyIdList : " + companyIdList);
        String inputProperties = getRemoveInputList(contactId, companyIdList, "contact_to_company");
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" + inputProperties +
                "  ]\n" +
                "}";
        String url = BasePath.V3_ASSOCIATION + CONTACTS + COMPANIES + BATCH + ARCHIVE;
        httpService.postRequest(url, associationProperties);
    }


    /**
     * Remove associate between a company and a list of children companies
     *
     * @param companyId     - ID of the contact
     * @param companyIdList - ID list of the companies
     * @throws HubSpotException - if HTTP call fails
     */
    public void removeCompanyToChildCompanyList(long companyId, List<Long> companyIdList) throws HubSpotException {
        log.log(DEBUG, "removeCompanyToChildCompanyList - companyId : " + companyId + " | companyIdList : " + companyIdList);
        String inputProperties = getRemoveInputList(companyId, companyIdList, "company_to_company");
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" + inputProperties +
                "  ]\n" +
                "}";
        String url = BasePath.V3_ASSOCIATION + COMPANIES + COMPANIES + BATCH + ARCHIVE;
        httpService.postRequest(url, associationProperties);
    }

    private String getRemoveInputList(long contactId, List<Long> companyIdList, String associationType) {
        log.log(DEBUG, "getRemoveInputList - contactId : " + contactId + " | companyIdList : " + companyIdList +
                " | associationType : " + associationType);
        StringBuilder stringBuilder = new StringBuilder();

        int lastIndex = companyIdList.size() - 1;
        int index = 0;

        for (long companyId : companyIdList) {
            stringBuilder.append("    {\n")
                    .append("      \"from\": {\n")
                    .append("        \"id\": \"").append(contactId).append("\"\n")
                    .append("      },\n")
                    .append("      \"to\": {\n")
                    .append("        \"id\": \"").append(companyId).append("\"\n")
                    .append("      },\n")
                    .append("      \"type\": \"").append(associationType).append("\"\n")
                    .append("    }");

            if (index != lastIndex) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n");
            }
            index++;
        }
        return stringBuilder.toString();
    }

    /**
     * Get companies associated to a contact
     *
     * @param contactId - ID of the contact to link
     * @return the contact company id list
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getContactCompanyIdList(long contactId) throws HubSpotException {
        log.log(DEBUG, "getContactCompanyIdList - contactId : " + contactId);
        String url =
                BasePath.V4_OBJECT + CONTACTS + contactId + "/" + ASSOCIATION + COMPANIES;
        try {
            return hsService.parseJsonObjectToIdList(url);
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
     * @return the company contact id list
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getCompanyContactIdList(long companyId) throws HubSpotException {
        log.log(DEBUG, "getCompanyContactIdList - companyId : " + companyId);
        String url =
                BasePath.V4_OBJECT + COMPANIES + companyId + "/" + ASSOCIATION + CONTACTS;
        try {
            return hsService.parseJsonObjectToIdList(url);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No associated contact found for this company")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Get all companies ids associated to a company
     *
     * @param companyId - ID of the company
     * @return the companies to company
     * @throws HubSpotException - if HTTP call fails
     */
    public List<JSONObject> getCompaniesToCompany(long companyId) throws HubSpotException {
        log.log(DEBUG, "getCompaniesToCompany - companyId : " + companyId);
        String url =
                BasePath.V4_OBJECT + COMPANIES + companyId + "/" + ASSOCIATION + COMPANIES;
        try {
            return hsService.parseJsonResultToList(url);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No company associated found for this company")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Get business to contact associations for a list of businesses
     *
     * @param companyIds - ID of the compagny to link
     * @return the list
     * @throws HubSpotException - if HTTP call fails
     */
    public List<JSONObject> companiesToContacts(List<Long> companyIds) throws HubSpotException {
        log.log(DEBUG, "companiesToContacts - companyIds : " + companyIds);
        StringBuilder companyIdList = new StringBuilder("{\n" +
                "  \"inputs\": [\n");
        companyIds.forEach(id -> companyIdList.append("    {\n")
                .append("      \"id\": \"").append(id).append("\"\n")
                .append("    },\n"));
        String postBody = companyIdList.substring(0, companyIdList.length() - 2) + "]\n" + "}";
        String url =
                BasePath.V4_ASSOCIATION + COMPANY + CONTACT + BATCH + READ;
        try {
            return hsService.parsePostJsonResultToList(url, postBody);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No contact associated found for these companies")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Associate a deal and a contact
     *
     * @param dealId    - ID of the deal to link
     * @param contactId - ID of the contact to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void dealToContact(long dealId, long contactId) throws HubSpotException {
        log.log(DEBUG, "dealToContact - dealId : " + dealId + " | contactId : " + contactId);
        String url =
                BasePath.V3 + DEAL + dealId + "/" + ASSOCIATION + CONTACT + contactId + "/" + DEAL_TO_CONTACT;

        httpService.putRequest(url);
    }


    /**
     * Associate a deal and a company
     *
     * @param dealId    - ID of the deal to link
     * @param companyId - ID of the company to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void dealToCompany(long dealId, long companyId) throws HubSpotException {
        log.log(DEBUG, "dealToCompany - dealId : " + dealId + " | companyId : " + companyId);
        String url =
                BasePath.V3 + DEAL + dealId + "/" + ASSOCIATION + COMPANY + companyId + "/" + DEAL_TO_COMPANY;

        httpService.putRequest(url);
    }

    /**
     * Associate a deal and a lineItem
     *
     * @param dealId     - ID of the deal to link
     * @param lineItemId - ID of the lineItem to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void dealToLineItem(long dealId, long lineItemId) throws HubSpotException {
        log.log(DEBUG, "dealToLineItem - dealId : " + dealId + " | lineItemId : " + lineItemId);
        String url =
                BasePath.V3 + DEAL + dealId + "/" + ASSOCIATION + LINE_ITEM + lineItemId + "/" + DEAL_TO_LINE_ITEM;

        httpService.putRequest(url);
    }

    /**
     * Get all companies IDs associated to a deal
     *
     * @param dealId - ID of the deal
     * @return the list of company IDs associated to the deal
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getCompanieIdsToDeal(long dealId) throws HubSpotException {
        log.log(DEBUG, "getCompaniesToDeal - dealId : " + dealId);
        String url =
                BasePath.V4_OBJECT + DEALS + dealId + "/" + ASSOCIATION + COMPANIES;
        try {
            return hsService.parseJsonObjectToIdList(url);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No associated company found for this deal")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Get all contacts IDs associated to a deal
     *
     * @param dealId - ID of the deal
     * @return the contact IDs associated to the deal
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getContactIdsToDeal(long dealId) throws HubSpotException {
        log.log(DEBUG, "getContactsToDeal - dealId : " + dealId);
        String url =
                BasePath.V4_OBJECT + DEALS + dealId + "/" + ASSOCIATION + CONTACTS;
        try {
            return hsService.parseJsonObjectToIdList(url);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No contact associated found for this deal")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Get all line items IDs associated to a deal
     *
     * @param dealId - ID of the deal
     * @return the line item IDs associated to the deal
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getLineItemIdsToDeal(long dealId) throws HubSpotException {
        log.log(DEBUG, "getLineItemsToDeal - dealId : " + dealId);
        String url =
                BasePath.V4_OBJECT + DEALS + dealId + "/" + ASSOCIATION + LINE_ITEMS;
        try {
            return hsService.parseJsonObjectToIdList(url);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No line item associated found for this deal")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Get all quotes IDs associated to a deal
     *
     * @param dealId - ID of the deal
     * @return the quote IDs associated to the deal
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getQuoteIdsToDeal(long dealId) throws HubSpotException {
        log.log(DEBUG, "getQuotesToDeal - dealId : " + dealId);
        String url =
                BasePath.V4_OBJECT + DEALS + dealId + "/" + ASSOCIATION + QUOTES;
        try {
            return hsService.parseJsonObjectToIdList(url);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("No quote associated found for this deal")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

}
