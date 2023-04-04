package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.domain.HSProduct;
import kong.unirest.json.JSONObject;

/**
 * HubSpot Product Service
 * <p>
 * Service for managing HubSpot products
 */
public class HSProductService {

    private final static String PRODUCT_URL = "/crm/v3/objects/products/";

    private final HttpService httpService;
    private final HSService hsService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSProductService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    /**
     * Get HubSpot product by its ID.
     *
     * @param id - ID of the product
     * @return the product
     * @throws HubSpotException - if HTTP call fails
     */
    public HSProduct getByID(long id) throws HubSpotException {
        String url = PRODUCT_URL + id;
        return getProduct(url);
    }

    /**
     * Create a new product
     *
     * @param product - product to create
     * @return created product
     * @throws HubSpotException - if HTTP call fails
     */
    public HSProduct create(HSProduct product) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(PRODUCT_URL, product.toJsonString());

        return parseProductData(jsonObject);
    }

    /**
     * Patch a product.
     *
     * @param product - product to update
     * @return Updated product
     * @throws HubSpotException - if HTTP call fails
     */
    public HSProduct patch(HSProduct product) throws HubSpotException {
        String url = PRODUCT_URL + product.getId();

        String properties = product.toJsonString();

        try {
            httpService.patchRequest(url, properties);
            return product;
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update product: " + product + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a product.
     *
     * @param product - product to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(HSProduct product) throws HubSpotException {
        delete(product.getId());
    }

    /**
     * Delete a product.
     *
     * @param id - ID of the product to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("Product ID must be provided");
        }
        String url = PRODUCT_URL + id;

        httpService.deleteRequest(url);
    }

    /**
     * Parse product data from HubSpot API response
     *
     * @param jsonBody - body from HubSpot API response
     * @return the company
     */
    public HSProduct parseProductData(JSONObject jsonBody) {
        HSProduct product = new HSProduct();

        product.setId(jsonBody.getLong("id"));

        hsService.parseJSONData(jsonBody, product);
        return product;
    }

    private HSProduct getProduct(String url) throws HubSpotException {
        try {
            return parseProductData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

}
