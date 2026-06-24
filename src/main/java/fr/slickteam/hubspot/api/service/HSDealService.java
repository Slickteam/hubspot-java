package fr.slickteam.hubspot.api.service;

import tools.jackson.databind.JsonNode;
import fr.slickteam.hubspot.api.domain.HSDeal;
import fr.slickteam.hubspot.api.domain.HSLineItem;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.HubSpotHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.slickteam.hubspot.api.utils.JsonUtils.getJsonInputList;
import static fr.slickteam.hubspot.api.utils.JsonUtils.getJsonProperties;
import static java.lang.System.Logger.Level.DEBUG;

/**
 * HubSpot Deal Service
 * <p>
 * Service for managing HubSpot deals
 */
public class HSDealService {

    private static final System.Logger log = System.getLogger(HSDealService.class.getName());

    private static final String LINE_ITEM_URL = "/crm/objects/2026-03/line_items/";
    private static final String DEAL_URL = "/crm/objects/2026-03/deals/";
    private static final String BATCH = "batch/";
    private static final String READ = "read/";
    private static final String SEARCH = "search/";
    private static final String RESULTS = "results";
    public static final String NOT_FOUND = "Not Found";
    public static final String TO_OBJECT_ID = "toObjectId";

    private final HttpService httpService;
    private final HSService hsService;
    private final HSAssociationService associationService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSDealService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
        associationService = new HSAssociationService(httpService);
    }

    /**
     * Get HubSpot deal by its ID.
     *
     * @param id - ID of the deal
     * @return the deal
     * @throws HubSpotException - if HTTP call fails
     */
    public HSDeal getByID(long id) throws HubSpotException {
        log.log(DEBUG, "getByID - id : " + id);
        String url = DEAL_URL + id;
        return getDeal(url);
    }

    /**
     * Get HubSpot deal by its ID with list of properties.
     *
     * @param id         - ID of the deal
     * @param properties - List of string properties as deal name or deal stage
     * @return the deal with the selected properties
     * @throws HubSpotException - if HTTP call fails
     */
    public HSDeal getByIdAndProperties(long id, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getByIdAndProperties - id : " + id + " | properties : " + properties);
        String propertiesUrl = String.join(",", properties);
        String url = DEAL_URL + id + "?properties=" + propertiesUrl;
        return getDeal(url);
    }

    private HSDeal getDeal(String url) throws HubSpotException {
        try {
            return parseDealData((JsonNode) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals(NOT_FOUND)) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Get HubSpot a list of deals by id with properties.
     *
     * @param idList     - ID list of deals
     * @param properties - List of string properties as deal name or deal stage
     * @return a deal list with properties
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSDeal> getDealListByIdAndProperties(List<Long> idList, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getDealListByIdAndProperties - idList : " + idList + " | properties : " + properties);
        String formatProperties = getJsonProperties(properties);
        String formatIdList = getJsonInputList(idList);
        String dealProperties = "{\n" +
                                "  \"properties\": [\n" + formatProperties +
                                "   ],\n" +
                                "  \"propertiesWithHistory\": [],\n" +
                                "   \"inputs\": [\n" + formatIdList +
                                "   ]\n" +
                                "}";
        String url = DEAL_URL + BATCH + READ;
        try {
            JsonNode response = (JsonNode) httpService.postRequest(url, dealProperties);
            JsonNode jsonList = response.path(RESULTS);
            List<HSDeal> deals = new ArrayList<>(jsonList.size());
            for (int i = 0; i < jsonList.size(); i++) {
                deals.add(parseDealData(jsonList.path(i)));
            }
            return deals;
        } catch (HubSpotException e) {
            if (e.getMessage().equals(NOT_FOUND)) {
                return new ArrayList<>();
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
        log.log(DEBUG, "getHSLineItemsForHSDeal - deal : " + deal);
        String filter = getFiltersLineItemsAssociatedWithDeal(deal);
        String url = LINE_ITEM_URL + SEARCH;
        try {
            JsonNode response = (JsonNode) httpService.postRequest(url, filter);
            return parseLineItemsData(response);
        } catch (HubSpotException e) {
            if (e.getMessage().equals(NOT_FOUND)) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    private List<HSLineItem> parseLineItemsData(JsonNode jsonNode) {
        List<HSLineItem> lineItems = new ArrayList<>();

        JsonNode jsonLineItems = jsonNode.path(RESULTS);

        for (JsonNode jsonLineItem : jsonLineItems) {
            lineItems.add(getLineItemFromJsonNode(jsonLineItem));
        }
        return lineItems;
    }

    private HSLineItem getLineItemFromJsonNode(JsonNode jsonLineItem) {
        HSLineItem hsLineItem = (HSLineItem) hsService.parseJSONData(jsonLineItem, new HSLineItem());
        hsLineItem.setId(jsonLineItem.path("id").asLong());

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
        log.log(DEBUG, "create - hsDeal : " + hsDeal);
        JsonNode jsonNode = (JsonNode) httpService.postRequest(DEAL_URL, hsDeal.toJsonString());

        return parseDealData(jsonNode);
    }

    /**
     * Parse deal data from HubSpot API response
     *
     * @param jsonBody - body from HubSpot API response
     * @return the company
     */
    public HSDeal parseDealData(JsonNode jsonBody) {
        HSDeal deal = new HSDeal();

        deal.setId(jsonBody.path("id").asLong());

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
        log.log(DEBUG, "patch - deal : " + deal);
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
        log.log(DEBUG, "delete - deal : " + deal);
        delete(deal.getId());
    }

    /**
     * Delete a deal.
     *
     * @param id - ID of the deal to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        log.log(DEBUG, "delete - id : " + id);
        if (id == 0) {
            throw new HubSpotException("Deal ID must be provided");
        }
        String url = DEAL_URL + id;

        httpService.deleteRequest(url);
    }

    /**
     * Get HubSpot associated companies for a list of deal IDs.
     *
     * @param dealIds - IDs of deals
     * @return A map containing for each deal ID a list of company IDs
     * @throws HubSpotException - if HTTP call fails
     */
    public Map<Long, List<Long>> getAssociatedCompanyIds(List<Long> dealIds) throws HubSpotException {
        log.log(DEBUG, "getAssociatedCompanyIds - dealIds : " + dealIds);
        List<JsonNode> associationList = associationService.dealsToCompanies(dealIds);
        Map<Long, List<Long>> associatedDeals = new HashMap<>();

        for (JsonNode associationsByDeal : associationList) {
            // Initiate associated deal parameters
            Long dealId = associationsByDeal.path("from").path("id").asLong();
            JsonNode companies = associationsByDeal.path("to");

            List<Long> companyIds = new ArrayList<>();

            for (int i = 0; i < companies.size(); i++) {
                companyIds.add(companies.path(i).path(TO_OBJECT_ID).asLong());
            }

            associatedDeals.put(dealId, companyIds);
        }

        return associatedDeals;
    }

    /**
     * Get HubSpot associated contacts for a list of deal IDs.
     *
     * @param dealIds - IDs of deals
     * @return A map containing for each deal ID a list of contact IDs
     * @throws HubSpotException - if HTTP call fails
     */
    public Map<Long, List<Long>> getAssociatedContactIds(List<Long> dealIds) throws HubSpotException {
        log.log(DEBUG, "getAssociatedContactIds - dealIds : " + dealIds);
        List<JsonNode> associationList = associationService.dealsToContacts(dealIds);
        Map<Long, List<Long>> associatedDeals = new HashMap<>();

        for (JsonNode associationsByDeal : associationList) {
            // Initiate associated deal parameters
            Long dealId = associationsByDeal.path("from").path("id").asLong();
            JsonNode contacts = associationsByDeal.path("to");

            List<Long> contactIds = new ArrayList<>();

            for (int i = 0; i < contacts.size(); i++) {
                contactIds.add(contacts.path(i).path(TO_OBJECT_ID).asLong());
            }

            associatedDeals.put(dealId, contactIds);
        }

        return associatedDeals;
    }

    /**
     * Get HubSpot associated line items for a list of deal IDs.
     *
     * @param dealIds - IDs of deals
     * @return A map containing for each deal ID a list of line item IDs
     * @throws HubSpotException - if HTTP call fails
     */
    public Map<Long, List<Long>> getAssociatedLineItemIds(List<Long> dealIds) throws HubSpotException {
        log.log(DEBUG, "getAssociatedLineItemIds - dealIds : " + dealIds);
        List<JsonNode> associationList = associationService.dealsToLineItems(dealIds);
        Map<Long, List<Long>> associatedDeals = new HashMap<>();

        for (JsonNode associationsByDeal : associationList) {
            // Initiate associated deal parameters
            Long dealId = associationsByDeal.path("from").path("id").asLong();
            JsonNode lineItems = associationsByDeal.path("to");

            List<Long> lineItemIds = new ArrayList<>();

            for (int i = 0; i < lineItems.size(); i++) {
                lineItemIds.add(lineItems.path(i).path(TO_OBJECT_ID).asLong());
            }

            associatedDeals.put(dealId, lineItemIds);
        }

        return associatedDeals;
    }

}
