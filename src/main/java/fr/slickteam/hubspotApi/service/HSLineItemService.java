package fr.slickteam.hubspotApi.service;

import fr.slickteam.hubspotApi.domain.HSLineItem;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import org.json.JSONObject;


public class HSLineItemService {

    private final static String LINE_ITEM_URL = "/crm/v3/objects/line_items/";
    private HttpService httpService;
    private HSService hsService;

    public HSLineItemService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    public HSLineItem getByID(long id) throws HubSpotException{
        String url = LINE_ITEM_URL + id ;
        return getLineItem(url);
    }

    private HSLineItem getLineItem(String url) throws HubSpotException {
        try {
            return parseLineItemData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public HSLineItem create(HSLineItem HSLineItem) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(LINE_ITEM_URL, HSLineItem.toJsonString());

        return parseLineItemData(jsonObject);
    }

    public HSLineItem parseLineItemData(JSONObject jsonBody) {
        HSLineItem line_items = new HSLineItem();

        line_items.setId(jsonBody.getLong("id"));

        hsService.parseJSONData(jsonBody, line_items);
        return line_items;
    }

    public HSLineItem patch(HSLineItem lineItem) throws HubSpotException {

        String url = LINE_ITEM_URL + lineItem.getId();

        String properties = lineItem.toJsonString();

        try {
            httpService.patchRequest(url, properties);
            return lineItem;
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update line item: " + lineItem + ". Reason: " + e.getMessage(), e);
        }
    }

    public void delete(HSLineItem lineItem) throws HubSpotException {
        delete(lineItem.getId());
    }

    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("Line item ID must be provided");
        }
        String url = LINE_ITEM_URL + id;

        httpService.deleteRequest(url);
    }
}
