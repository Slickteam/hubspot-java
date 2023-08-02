package fr.slickteam.hubspot.api.service;

import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSContact;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * HubSpot Contact Service
 * <p>
 * Service for managing HubSpot contacts
 */
public class HSContactService {

    private static final String CONTACT_URL = "/crm/v3/objects/contacts/";
    public static final String ID_PROPERTY_EMAIL = "idProperty=email";
    public static final String PARAMETER_OPERATOR = "?";
    private final HttpService httpService;
    private final HSService hsService;
    private final HSAssociationService associationService;
    private HSCompanyService companyService;
    private static final String BATCH = "batch/";
    private static final String READ = "read/";

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService        - HTTP service for HubSpot API
     */
    public HSContactService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
        associationService = new HSAssociationService(httpService);
    }

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
        String url = CONTACT_URL + id;
        return getContact(url);
    }

    /**
     * Get HubSpot contact by its ID with list of properties.
     *
     * @param id - ID of the company
     * @param properties - List of string properties as contact name or email
     * @return the contact with the selected properties
     * @throws HubSpotException - if HTTP call fails
     */
    public HSContact getByIdAndProperties(long id, List<String> properties) throws HubSpotException {
        String propertiesUrl = String.join(",", properties);
        String url = CONTACT_URL + id + "?properties=" + propertiesUrl;
        return getContact(url);
    }

    /**
     * Get HubSpot a list of companies by id with properties.
     *
     * @param idList     - ID list of companies
     * @param properties - List of string properties as company name or deal description
     * @return a company list with properties
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSContact> getContactListByIdAndProperties(List<Long> idList, List<String> properties) throws HubSpotException {
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
        List<Long> companyIds = associationService.getContactCompanyIdList(contactId);

        return companyIds.stream()
                .map(this::getHsCompany)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private HSCompany getHsCompany(Long companyId) {
        try {
            return companyService.getByID(companyId);
        } catch (HubSpotException e) {
            return null;
        }
    }

    /**
     * Create a new contact
     *
     * @param hsContact - contact to create
     * @return created contact
     * @throws HubSpotException - if HTTP call fails
     */
    public HSContact create(HSContact hsContact) throws HubSpotException {
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
        delete(contact.getId());
    }

    /**
     * Delete a contact.
     *
     * @param id - ID of the contact to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
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
}
