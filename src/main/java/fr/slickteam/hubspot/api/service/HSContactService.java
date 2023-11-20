package fr.slickteam.hubspot.api.service;

import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.domain.HSContact;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.Logger.Level.DEBUG;

/**
 * HubSpot Contact Service
 * <p>
 * Service for managing HubSpot contacts
 */
public class HSContactService {

    private static final System.Logger log = System.getLogger(HSContactService.class.getName());

    private static final String CONTACT_URL = "/crm/v3/objects/contacts/";
    /**
     * The constant ID_PROPERTY_EMAIL.
     */
    public static final String ID_PROPERTY_EMAIL = "idProperty=email";
    /**
     * The constant PARAMETER_OPERATOR.
     */
    public static final String PARAMETER_OPERATOR = "?";
    private final HttpService httpService;
    private final HSService hsService;
    private final HSAssociationService associationService;
    private HSCompanyService companyService;
    private static final String BATCH = "batch/";
    private static final String READ = "read/";
    private static final String SEARCH = "search";

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSContactService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
        associationService = new HSAssociationService(httpService);
    }

    /**
     * Sets company service.
     *
     * @param companyService the company service
     */
    public void setCompanyService(HSCompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Get HubSpot contact by its ID.
     *
     * @param id - ID of the contact
     * @return the contact
     * @throws HubSpotException - if HTTP call fails
     */
    public HSContact getByID(long id) throws HubSpotException {
        log.log(DEBUG, "getByID - id : " + id);
        String url = CONTACT_URL + id;
        return getContact(url);
    }

    /**
     * Get HubSpot contact by its ID with list of properties.
     *
     * @param id         - ID of the company
     * @param properties - List of string properties as contact name or email
     * @return the contact with the selected properties
     * @throws HubSpotException - if HTTP call fails
     */
    public HSContact getByIdAndProperties(long id, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getByIdAndProperties - id : " + id + " | properties : " + properties);
        String propertiesUrl = String.join(",", properties);
        String url = CONTACT_URL + id + "?properties=" + propertiesUrl;
        return getContact(url);
    }

    /**
     * Get HubSpot a list of contact by id with properties.
     *
     * @param idList     - ID list of companies
     * @param properties - List of string properties as company name or deal description
     * @return a company list with properties
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSContact> getContactListByIdAndProperties(List<Long> idList, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getContactListByIdAndProperties - idList : " + idList + " | properties : " + properties);
        String formatProperties = getJsonProperties(properties);
        String formatIdList = getJsonInputList(idList);
        String associationProperties = "{\n" +
                "  \"properties\": [\n" + formatProperties +
                "   ],\n" +
                "  \"propertiesWithHistory\": [],\n" +
                "   \"inputs\": [\n" + formatIdList +
                "   ]\n" +
                "}";
        String url = CONTACT_URL + BATCH + READ;
        try {
            JSONObject response = (JSONObject) httpService.postRequest(url, associationProperties);
            JSONArray jsonList = response.optJSONArray("results");
            List<HSContact> contacts = new ArrayList<>(jsonList.length());
            for (int i = 0; i < jsonList.length(); i++) {
                contacts.add(parseContactData(jsonList.optJSONObject(i)));
            }
            return contacts;
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    private String getJsonProperties(List<String> properties) {
        StringJoiner stringJoiner = new StringJoiner(",\n", "", "\n");
        for (String property : properties) {
            stringJoiner.add("\"" + property + "\"");
        }
        return stringJoiner.toString();
    }

    private String getJsonInputList(List<Long> idList) {
        StringBuilder stringBuilder = new StringBuilder();

        int lastIndex = idList.size() - 1;
        int index = 0;

        for (long companyId : idList) {
            stringBuilder.append(" { \"id\": \"").append(companyId).append("\" }");
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
     * Get HubSpot contact by its email.
     *
     * @param email - Email of the contact
     * @return the contact
     * @throws HubSpotException - if HTTP call fails
     */
    public Optional<HSContact> getByEmail(String email) throws HubSpotException {
        log.log(DEBUG, "getByEmail - email : " + email);
        String url = CONTACT_URL + email + PARAMETER_OPERATOR + ID_PROPERTY_EMAIL;
        return Optional.ofNullable(getContact(url));
    }

    private HSContact getContact(String url) throws HubSpotException {
        try {
            return parseContactData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Get Contact Companies.
     *
     * @param contactId - ID of the contact
     * @return the list of companies associated to the contact
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> getContactCompanies(Long contactId) throws HubSpotException {
        log.log(DEBUG, "getContactCompanies - contactId : " + contactId);
        List<Long> companyIds = associationService.getContactCompanyIdList(contactId);

        return companyService.getCompanyListByIdAndProperties(companyIds, Collections.emptyList());
    }

    /**
     * Get Contact Companies with properties.
     *
     * @param contactId  - ID of the contact
     * @param properties - List of string properties as company name or deal description
     * @return the list of companies associated to the contact
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> getContactCompanies(Long contactId, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getContactCompanies - contactId : " + contactId + " ; properties : " + properties);
        List<Long> companyIds = associationService.getContactCompanyIdList(contactId);

        return companyService.getCompanyListByIdAndProperties(companyIds, properties);
    }

    /**
     * Create a new contact
     *
     * @param hsContact - contact to create
     * @return created contact
     * @throws HubSpotException - if HTTP call fails
     */
    public HSContact create(HSContact hsContact) throws HubSpotException {
        log.log(DEBUG, "create - hsContact : " + hsContact);
        if (Strings.isNullOrEmpty(hsContact.getEmail())) {
            throw new HubSpotException("User email must be provided");
        }

        JSONObject jsonObject = (JSONObject) httpService.postRequest(CONTACT_URL, hsContact.toJsonString());
        return parseContactData(jsonObject);
    }

    /**
     * Patch a contact.
     *
     * @param contact - contact to update
     * @return Updated contact
     * @throws HubSpotException - if HTTP call fails
     */
    public HSContact patch(HSContact contact) throws HubSpotException {
        log.log(DEBUG, "patch - contact : " + contact);
        if (contact.getId() == 0) {
            throw new HubSpotException("User ID must be provided");
        }

        String url = CONTACT_URL + contact.getId();
        JSONObject jsonObject = (JSONObject) httpService.patchRequest(url, contact.toJsonString());
        return parseContactData(jsonObject);
    }

    /**
     * Delete a contact.
     *
     * @param contact - contact to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(HSContact contact) throws HubSpotException {
        log.log(DEBUG, "delete - contact : " + contact);
        delete(contact.getId());
    }

    /**
     * Delete a contact.
     *
     * @param id - ID of the contact to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        log.log(DEBUG, "delete - id : " + id);
        if (id == 0) {
            throw new HubSpotException("Contact ID must be provided");
        }
        String url = CONTACT_URL + id;

        httpService.deleteRequest(url);
    }

    /**
     * Parse contact data from HubSpot API response
     *
     * @param jsonObject - body from HubSpot API response
     * @return the contact
     */
    public HSContact parseContactData(JSONObject jsonObject) {
        HSContact hsContact = new HSContact();
        hsContact.setId(jsonObject.getLong("id"));

        hsService.parseJSONData(jsonObject, hsContact);
        return hsContact;
    }

    /**
     * Query HubSpot contact with default searchable properties : firstname,lastname,email,
     * phone,hs_additional_emails, fax, mobilephone, company, hs_marketable_until_renewal
     *
     * @param input      - string to query
     * @param responseProperties - list of properties to return
     * @param limit      - size of the page
     * @return  a contact list filtered
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSContact> queryByDefaultSearchableProperties(String input, List<String> responseProperties, int limit) throws HubSpotException {
        log.log(DEBUG, "queryByDefaultSearchableProperties");
        String url = CONTACT_URL + SEARCH;

        String responsePropertiesList = responseProperties.stream()
                .map(property -> "    \"" + property + "\"")
                .collect(Collectors.joining(",\n"));

        String queryProperties = "{\n" +
                "  \"query\": \""+ input +"\"," +
                "  \"properties\": [\n" +
                responsePropertiesList +
                "  ],\n" +
                "  \"limit\": "+ limit +",\n" +
                "  \"after\": 0\n" +
                "}";

        return sendContactSearchRequest(url, queryProperties);
    }


    /**
     * Send Hubspot contact search HTTP request
     *
     * @param url      - url of the request
     * @param properties - list of properties to query
     * @return  a contact list filtered
     * @throws HubSpotException - if HTTP call fails
     */

    public List<HSContact> sendContactSearchRequest(String url, String properties) throws HubSpotException {
        List<HSContact> contacts = Collections.emptyList();

        try {
            JSONObject response = (JSONObject) httpService.postRequest(url, properties);
            JSONArray jsonList = response.optJSONArray("results");
            contacts = new ArrayList<>(jsonList.length());
            for (int i = 0; i < jsonList.length(); i++) {
                contacts.add(parseContactData(jsonList.optJSONObject(i)));
            }
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return contacts;
            } else {
                throw e;
            }
        }
        return contacts;
    }

    /**
     * Search HubSpot contacts filtered by properties
     *
     * @param propertiesAndValuesFilters - map of properties and values to filter
     * @param responseProperties - list of properties to return
     * @param limit - size of the page
     * @return  a contact list filtered
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSContact> searchFilteredByProperties(Map<String, String> propertiesAndValuesFilters, List<String> responseProperties, int limit) throws HubSpotException {
        log.log(DEBUG, "searchFilteredByProperties");
        String url = CONTACT_URL + SEARCH;

        String filtersPropertyList = propertiesAndValuesFilters.entrySet().stream()
                .map(entry ->
                        " {\n" +
                                "      \"filters\": [\n" +
                                "        {\n" +
                                "          \"propertyName\": \"" + entry.getKey() + "\",\n" +
                                "          \"value\": \"" + entry.getValue() + "\",\n" +
                                "          \"operator\": \"EQ\"\n" +
                                "        }" +
                                "      ]\n" +
                                "    }\n"
                )
                .collect(Collectors.joining(",\n"));

        String responsePropertiesList = responseProperties.stream()
                .map(property -> "    \"" + property + "\"")
                .collect(Collectors.joining(",\n"));

        String filterGroupsProperties =
                "{\n" +
                        "  \"filterGroups\": [\n" +
                        filtersPropertyList +
                        "  ],\n" +
                        "  \"sorts\": [\n" +
                        "    \"lastname\"\n" +
                        "  ],\n" +
                        "  \"properties\": [\n" +
                        responsePropertiesList +
                        "  ],\n" +
                        "  \"limit\": "+ limit +"\n" +
                        "}";

        return sendContactSearchRequest(url, filterGroupsProperties);
    }
}
