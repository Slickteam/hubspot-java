package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSQuote;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.Logger.Level.DEBUG;

/**
 * HubSpot Quote Service
 * <p>
 * Service for managing HubSpot quotes
 */
public class HSQuoteService {

    private static final System.Logger log = System.getLogger(HSQuoteService.class.getName());
    private static final String QUOTE_URL = "/crm/v3/objects/quotes/";
    private static final String PATCH_URL = "/crm/v3/objects/quote/";

    private final HttpService httpService;
    private final HSService hsService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSQuoteService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    /**
     * Get HubSpot quote by its ID.
     *
     * @param id - ID of the quote
     * @return the quote
     * @throws HubSpotException - if HTTP call fails
     */
    public HSQuote getByID(long id) throws HubSpotException {
        log.log(DEBUG, "getByID - id : " + id);
        String url = QUOTE_URL + id;
        return getQuote(url);
    }

    /**
     * Get HubSpot quote by its ID with list of properties.
     *
     * @param id         - ID of the quote
     * @param properties - List of string properties as hs_status or hs_language
     * @return the quote with the selected properties
     * @throws HubSpotException - if HTTP call fails
     */
    public HSQuote getByIdAndProperties(long id, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getByIdAndProperties - id : " + id + " | properties : " + properties);
        String propertiesUrl = String.join(",", properties);
        String url = QUOTE_URL + id + "?properties=" + propertiesUrl;
        return getQuote(url);
    }

    /**
     * Get All HubSpot quote.
     *
     * @return a list of quotes
     * @param properties - List of string properties as hs_status or hs_language
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSQuote> getAllWithProperties(List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getAll - properties : " + properties);
        String propertiesUrl = String.join(",", properties);
        String url = QUOTE_URL + "?properties=" + propertiesUrl;
        try {
            return parseQuoteDataList((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    private List<HSQuote> parseQuoteDataList(JSONObject jsonObject) {
        List<HSQuote> quotes = new ArrayList<>();
        JSONArray jsonQuotes = jsonObject.getJSONArray("results");
        jsonQuotes.forEach(jsonQuote -> quotes.add(parseQuoteData((JSONObject) jsonQuote)));
        return quotes;
    }

    private HSQuote getQuote(String url) throws HubSpotException {
        try {
            return parseQuoteData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Create a new quote
     *
     * @param hsQuote - quote to create
     * @return created quote
     * @throws HubSpotException - if HTTP call fails
     */
    public HSQuote create(HSQuote hsQuote) throws HubSpotException {
        log.log(DEBUG, "create - hsQuote : " + hsQuote);
        JSONObject jsonObject = (JSONObject) httpService.postRequest(QUOTE_URL, hsQuote.toJsonString());

        return parseQuoteData(jsonObject);
    }

    /**
     * Parse quote data from HubSpot API response
     *
     * @param jsonBody - body from HubSpot API response
     * @return the company
     */
    public HSQuote parseQuoteData(JSONObject jsonBody) {
        HSQuote quote = new HSQuote();

        quote.setId(jsonBody.getLong("id"));

        hsService.parseJSONData(jsonBody, quote);
        return quote;
    }

    /**
     * Patch a quote.
     *
     * @param quote - quote to update
     * @return Updated quote
     * @throws HubSpotException - if HTTP call fails
     */
    public HSQuote patch(HSQuote quote) throws HubSpotException {
        log.log(DEBUG, "patch - quote : " + quote);
        String url = PATCH_URL + quote.getId();

        String properties = quote.toJsonString();

        try {
            httpService.patchRequest(url, properties);
            return quote;
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update quote: " + quote + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a quote.
     *
     * @param quote - quote to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(HSQuote quote) throws HubSpotException {
        log.log(DEBUG, "delete - quote : " + quote);
        delete(quote.getId());
    }

    /**
     * Delete a quote.
     *
     * @param id - ID of the quote to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        log.log(DEBUG, "delete - id : " + id);
        if (id == 0) {
            throw new HubSpotException("quote ID must be provided");
        }
        String url = QUOTE_URL + id;

        httpService.deleteRequest(url);
    }

}
