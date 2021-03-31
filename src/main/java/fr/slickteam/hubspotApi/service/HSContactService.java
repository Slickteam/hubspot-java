package fr.slickteam.hubspotApi.service;

import com.google.common.base.Strings;
import fr.slickteam.hubspotApi.domain.HSContact;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import org.json.JSONObject;

import java.util.Optional;

public class HSContactService {

    private final static String CONTACT_URL = "/crm/v3/objects/contacts/";
    public static final String ID_PROPERTY_EMAIL = "idProperty=email";
    public static final String PARAMETER_OPERATOR = "?";
    private final HttpService httpService;
    private final HSService hsService;

    public HSContactService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);

    }

    public HSContact getByID(long id) throws HubSpotException{
        String url = CONTACT_URL + id ;
        return getContact(url);
    }

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

    public HSContact create(HSContact HSContact) throws HubSpotException {
        if (Strings.isNullOrEmpty(HSContact.getEmail())) {
            throw new HubSpotException("User email must be provided");
        }

        JSONObject jsonObject = (JSONObject) httpService.postRequest(CONTACT_URL, HSContact.toJsonString());
        return parseContactData(jsonObject);
    }

    public HSContact patch(HSContact contact) throws HubSpotException {
        if (contact.getId() == 0) {
            throw new HubSpotException("User ID must be provided");
        }

        String url = CONTACT_URL + contact.getId();
        JSONObject jsonObject = (JSONObject) httpService.patchRequest(url, contact.toJsonString());
        return parseContactData(jsonObject);
    }


    public void delete(HSContact contact) throws HubSpotException {
        delete(contact.getId());
    }

    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("User ID must be provided");
        }
        String url = CONTACT_URL + id;

        httpService.deleteRequest(url);
    }

    public HSContact parseContactData(JSONObject jsonObject) {
        HSContact HSContact = new HSContact();
        HSContact.setId(jsonObject.getLong("id"));

        hsService.parseJSONData(jsonObject, HSContact);
        return HSContact;
    }
}
