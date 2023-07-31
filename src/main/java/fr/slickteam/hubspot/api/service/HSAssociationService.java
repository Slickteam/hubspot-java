package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSAssocationTypeInput;
import fr.slickteam.hubspot.api.domain.HSAssociationTypeEnum;
import fr.slickteam.hubspot.api.utils.HubSpotException;
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
        public static final String V3_ASSOCIATION = "/crm/v3/associations/";
        public static final String V4_OBJECT = "/crm/v4/objects/";
        public static final String V4_ASSOCIATION = "/crm/v4/associations/";
    }

    private static final String CONTACT = "contact/";
    private static final String CONTACTS = "contacts/";
    private static final String COMPANIES = "companies/";
    private static final String COMPANY = "company/";
    private static final String DEAL = "deal/";
    private static final String LINE_ITEM = "line_item/";
    private static final String DEAL_TO_LINE_ITEM = "deal_to_line_item";
    private static final String DEAL_TO_CONTACT = "deal_to_contact";
    private static final String DEAL_TO_COMPANY = "deal_to_company";
    private static final String ASSOCIATION = "associations/";
    private static final String ARCHIVE = "archive/";
    private static final String BATCH = "batch/";
    private static final String CREATE = "create/";
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
     * @param typeEnum            - Association type from company to associated company
     * @throws HubSpotException - if HTTP call fails
     */
    public void companyToCompany(long companyId, long associatedCompanyId, HSAssociationTypeEnum typeEnum) throws HubSpotException {
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
     * Remove associate between a contact and a company
     *
     * @param contactId - ID of the contact
     * @param companyId - ID of the company
     * @throws HubSpotException - if HTTP call fails
     */
    public void removeContactToCompany(long contactId, long companyId) throws HubSpotException {
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
        String inputProperties = getRemoveContactToCompanyInputList(contactId, companyIdList);
        String associationProperties = "{\n" +
                "  \"inputs\": [\n" + inputProperties +
                "  ]\n" +
                "}";
        String url = BasePath.V3_ASSOCIATION + CONTACTS + COMPANIES + BATCH + ARCHIVE;
        httpService.postRequest(url, associationProperties);
    }

    private String getRemoveContactToCompanyInputList(long contactId, List<Long> companyIdList) {
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
                    .append("      \"type\": \"contact_to_company\"\n")
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
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getContactCompanyIdList(long contactId) throws HubSpotException {
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
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getCompanyContactIdList(long companyId) throws HubSpotException {
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
     * @throws HubSpotException - if HTTP call fails
     */
    public List<JSONObject> getCompaniesToCompany(long companyId) throws HubSpotException {
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
     * Associate a deal and a contact
     *
     * @param dealId    - ID of the deal to link
     * @param contactId - ID of the contact to link
     * @throws HubSpotException - if HTTP call fails
     */
    public void dealToContact(long dealId, long contactId) throws HubSpotException {
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
        String url =
                BasePath.V3 + DEAL + dealId + "/" + ASSOCIATION + LINE_ITEM + lineItemId + "/" + DEAL_TO_LINE_ITEM;

        httpService.putRequest(url);
    }
}
