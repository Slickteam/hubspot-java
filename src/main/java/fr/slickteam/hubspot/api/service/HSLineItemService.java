package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSLineItem;
import kong.unirest.json.JSONObject;

/**
 * HubSpot LineItem Service
 * <p>
 * Service for managing HubSpot line items
 */
public class HSLineItemService {

    private final static String LINE_ITEM_URL = "/crm/v3/objects/line_items/";
    private final HttpService httpService;
    private final HSService hsService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSLineItemService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    /**
     * Get HubSpot line item by its ID.
     *
     * @param id - ID of the line item
     * @return the line item
     * @throws HubSpotException - if HTTP call fails
     */
    public HSLineItem getByID(long id) throws HubSpotException {
        String url = LINE_ITEM_URL + id;
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

    /**
     * Create a new line item
     *
     * @param hsLineItem - line item to create
     * @return created line item
     * @throws HubSpotException - if HTTP call fails
     */
    public HSLineItem create(HSLineItem hsLineItem) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(LINE_ITEM_URL, hsLineItem.toJsonString());

        return parseLineItemData(jsonObject);
    }

    /**
     * Parse line item data from HubSpot API response
     *
     * @param jsonBody - body from HubSpot API response
     * @return the company
     */
    public HSLineItem parseLineItemData(JSONObject jsonBody) {
        HSLineItem line_items = new HSLineItem();

        line_items.setId(jsonBody.getLong("id"));

        hsService.parseJSONData(jsonBody, line_items);
        return line_items;
    }

    /**
     * Patch a line item.
     *
     * @param lineItem - line item to update
     * @return Updated line item
     * @throws HubSpotException - if HTTP call fails
     */
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

    /**
     * Delete a line item.
     *
     * @param lineItem - line item to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(HSLineItem lineItem) throws HubSpotException {
        delete(lineItem.getId());
    }

    /**
     * Delete a line item.
     *
     * @param id - ID of the line item to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("Line item ID must be provided");
        }
        String url = LINE_ITEM_URL + id;

        httpService.deleteRequest(url);
    }
}
