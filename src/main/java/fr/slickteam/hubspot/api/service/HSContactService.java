package fr.slickteam.hubspot.api.service;

import com.google.common.base.Strings;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSContact;
import kong.unirest.json.JSONObject;

import java.util.Optional;

/**
 * HubSpot Contact Service
 * <p>
 * Service for managing HubSpot contacts
 */
public class HSContactService {

    private final static String CONTACT_URL = "/crm/v3/objects/contacts/";
    public static final String ID_PROPERTY_EMAIL = "idProperty=email";
    public static final String PARAMETER_OPERATOR = "?";
    private final HttpService httpService;
    private final HSService hsService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSContactService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);

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
        HSContact HSContact = new HSContact();
        HSContact.setId(jsonObject.getLong("id"));

        hsService.parseJSONData(jsonObject, HSContact);
        return HSContact;
    }
}
