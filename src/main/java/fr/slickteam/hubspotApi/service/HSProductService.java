package fr.slickteam.hubspotApi.service;

import fr.slickteam.hubspotApi.domain.HSProduct;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import org.json.JSONObject;

public class HSProductService {

    private final static String PRODUCT_URL = "/crm/v3/objects/products/";
    private final static String SEARCH = "search/";

    private HttpService httpService;
    private HSService hsService;

    public HSProductService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    public HSProduct getByID(long id) throws HubSpotException {
        String url = PRODUCT_URL + id;
        return getProduct(url);
    }

    public HSProduct create(HSProduct product) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(PRODUCT_URL, product.toJsonString());

        return parseProductData(jsonObject);
    }

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

    public void delete(HSProduct product) throws HubSpotException {
        delete(product.getId());
    }

    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("Product ID must be provided");
        }
        String url = PRODUCT_URL + id;

        httpService.deleteRequest(url);
    }

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
