package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.HubSpotHelper;
import fr.slickteam.hubspot.api.domain.HSDeal;
import fr.slickteam.hubspot.api.domain.HSLineItem;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HubSpot Deal Service
 * <p>
 * Service for managing HubSpot deals
 */
public class HSDealService {

    private final static String LINE_ITEM_URL = "/crm/v3/objects/line_items/";
    private final static String DEAL_URL = "/crm/v3/objects/deals/";
    private final static String SEARCH = "search/";

    private final HttpService httpService;
    private final HSService hsService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSDealService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    /**
     * Get HubSpot deal by its ID.
     *
     * @param id - ID of the deal
     * @return the deal
     * @throws HubSpotException - if HTTP call fails
     */
    public HSDeal getByID(long id) throws HubSpotException {
        String url = DEAL_URL + id;
        return getDeal(url);
    }

    /**
     * Get HubSpot deal by its ID with list of properties.
     *
     * @param id - ID of the deal
     * @param properties - List of string properties as deal name or deal stage
     * @return the deal with the selected properties
     * @throws HubSpotException - if HTTP call fails
     */
    public HSDeal getByIdAndProperties(long id, List<String> properties) throws HubSpotException {
        String propertiesUrl = String.join(",", properties);
        String url = DEAL_URL + id + "?properties=" + propertiesUrl;
        return getDeal(url);
    }

    private HSDeal getDeal(String url) throws HubSpotException {
        try {
            return parseDealData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Get HubSpot LineItems for a deal.
     *
     * @param deal - HubSpot deal
     * @return list of HubSpot LineItems
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSLineItem> getHSLineItemsForHSDeal(HSDeal deal) throws HubSpotException {
        String filter = getFiltersLineItemsAssociatedWithDeal(deal);
        String url = LINE_ITEM_URL + SEARCH;
        try {
            JSONObject response = (JSONObject) httpService.postRequest(url, filter);
            return parseLineItemsData(response);
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    private List<HSLineItem> parseLineItemsData(JSONObject jsonObject) {
        List<HSLineItem> lineItems = new ArrayList<>();

        JSONArray jsonLineItems = jsonObject.getJSONArray("results");

        jsonLineItems.forEach(jsonLineItem -> lineItems.add(getLineItemFromJSONObject((JSONObject) jsonLineItem)));
        return lineItems;
    }

    private HSLineItem getLineItemFromJSONObject(JSONObject jsonLineItem) {
        HSLineItem hsLineItem = (HSLineItem) hsService.parseJSONData(jsonLineItem, new HSLineItem());
        hsLineItem.setId((jsonLineItem.getLong("id")));

        return hsLineItem;
    }

    private String getFiltersLineItemsAssociatedWithDeal(HSDeal deal) {
        Map<String, String> filterFields = new HashMap<>();

        filterFields.put("propertyName", "associations.deal");
        filterFields.put("operator", "EQ");
        filterFields.put("value", Long.toString(deal.getId()));

        return HubSpotHelper.mapFiltersToJson(filterFields).toString();
    }

    /**
     * Create a new deal
     *
     * @param hsDeal - deal to create
     * @return created deal
     * @throws HubSpotException - if HTTP call fails
     */
    public HSDeal create(HSDeal hsDeal) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(DEAL_URL, hsDeal.toJsonString());

        return parseDealData(jsonObject);
    }

    /**
     * Parse deal data from HubSpot API response
     *
     * @param jsonBody - body from HubSpot API response
     * @return the company
     */
    public HSDeal parseDealData(JSONObject jsonBody) {
        HSDeal deal = new HSDeal();

        deal.setId(jsonBody.getLong("id"));

        hsService.parseJSONData(jsonBody, deal);
        return deal;
    }

    /**
     * Patch a deal.
     *
     * @param deal - deal to update
     * @return Updated deal
     * @throws HubSpotException - if HTTP call fails
     */
    public HSDeal patch(HSDeal deal) throws HubSpotException {
        String url = DEAL_URL + deal.getId();

        String properties = deal.toJsonString();

        try {
            httpService.patchRequest(url, properties);
            return deal;
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update deal: " + deal + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a deal.
     *
     * @param deal - deal to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(HSDeal deal) throws HubSpotException {
        delete(deal.getId());
    }

    /**
     * Delete a deal.
     *
     * @param id - ID of the deal to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("Deal ID must be provided");
        }
        String url = DEAL_URL + id;

        httpService.deleteRequest(url);
    }

}
