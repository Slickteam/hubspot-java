package fr.slickteam.hubspotApi.service;

import fr.slickteam.hubspotApi.domain.HSDeal;
import fr.slickteam.hubspotApi.domain.HSLineItem;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import fr.slickteam.hubspotApi.utils.HubSpotHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HSDealService {

    private final static String LINE_ITEM_URL = "/crm/v3/objects/line_items/";
    private final static String DEAL_URL = "/crm/v3/objects/deals/";
    private final static String SEARCH = "search/";

    private HttpService httpService;
    private HSService hsService;

    public HSDealService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    public HSDeal getByID(long id) throws HubSpotException {
        String url = DEAL_URL + id;
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

    public HSDeal create(HSDeal HSDeal) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(DEAL_URL, HSDeal.toJsonString());

        return parseDealData(jsonObject);
    }

    public HSDeal parseDealData(JSONObject jsonBody) {
        HSDeal deal = new HSDeal();

        deal.setId(jsonBody.getLong("id"));

        hsService.parseJSONData(jsonBody, deal);
        return deal;
    }

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

    public void delete(HSDeal deal) throws HubSpotException {
        delete(deal.getId());
    }

    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("Deal ID must be provided");
        }
        String url = DEAL_URL + id;

        httpService.deleteRequest(url);
    }

}
