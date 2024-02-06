package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSLineItem;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static fr.slickteam.hubspot.api.utils.JsonUtils.getJsonInputList;
import static fr.slickteam.hubspot.api.utils.JsonUtils.getJsonProperties;
import static java.lang.System.Logger.Level.DEBUG;

/**
 * HubSpot LineItem Service
 * <p>
 * Service for managing HubSpot line items
 */
public class HSLineItemService {

    private static final System.Logger log = System.getLogger(HSLineItemService.class.getName());

    private static final String LOG_PROPERTIES = " | properties : ";
    private static final String RESULTS = "results";

    private static final String LINE_ITEM_URL = "/crm/v3/objects/line_items/";
    private static final String BATCH = "batch/";
    private static final String READ = "read/";
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
        log.log(DEBUG, "getByID - id : " + id);
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
        log.log(DEBUG, "create - hsLineItem : " + hsLineItem);
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
        log.log(DEBUG, "patch - lineItem : " + lineItem);

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
        log.log(DEBUG, "delete - lineItem : " + lineItem);
        delete(lineItem.getId());
    }

    /**
     * Delete a line item.
     *
     * @param id - ID of the line item to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        log.log(DEBUG, "delete - id : " + id);
        if (id == 0) {
            throw new HubSpotException("Line item ID must be provided");
        }
        String url = LINE_ITEM_URL + id;

        httpService.deleteRequest(url);
    }

    /**
     * Get HubSpot a list of line items by id with properties.
     *
     * @param idList     - ID list of companies
     * @param properties - List of string properties as product id or name
     * @return a line item list with properties
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSLineItem> getLineItemListByIdAndProperties(List<Long> idList, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getLineItemListByIdAndProperties - idList : " + idList + LOG_PROPERTIES + properties);
        String formatProperties = getJsonProperties(properties);
        String formatIdList = getJsonInputList(idList);
        String associationProperties = "{\n" +
                                       "  \"properties\": [\n" + formatProperties +
                                       "   ],\n" +
                                       "  \"propertiesWithHistory\": [],\n" +
                                       "   \"inputs\": [\n" + formatIdList +
                                       "   ]\n" +
                                       "}";
        String url = LINE_ITEM_URL + BATCH + READ;
        try {
            JSONObject response = (JSONObject) httpService.postRequest(url, associationProperties);
            JSONArray jsonList = response.optJSONArray(RESULTS);
            List<HSLineItem> lineItems = new ArrayList<>(jsonList.length());
            for (int i = 0; i < jsonList.length(); i++) {
                lineItems.add(parseLineItemData(jsonList.optJSONObject(i)));
            }
            return lineItems;
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }
}
