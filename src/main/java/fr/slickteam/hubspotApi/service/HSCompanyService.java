package fr.slickteam.hubspotApi.service;

import fr.slickteam.hubspotApi.domain.HSCompany;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * HubSpot Company Service
 * <p>
 * Service for managing HubSpot companies
 */
public class HSCompanyService {

    private final static String COMPANY_URL = "/crm/v3/objects/companies/";
    private final HttpService httpService;
    private final HSService hsService;

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSCompanyService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

    /**
     * Create a new company
     *
     * @param hsCompany - company to create
     * @return created company
     * @throws HubSpotException - if HTTP call fails
     */
    public HSCompany create(HSCompany hsCompany) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(COMPANY_URL, hsCompany.toJsonString());
        hsCompany.setId(jsonObject.getLong("id"));
        return hsCompany;
    }

    /**
     * Add a contact to a company
     *
     * @param contactId - ID of the contact to add
     * @param companyId - ID of the company
     * @throws HubSpotException - if HTTP call fails
     */
    public void addContact(long contactId, long companyId) throws HubSpotException {
        String url = "/companies/v2/companies/" + companyId + "/contacts/" + contactId;
        httpService.putRequest(url, "");
    }

    /**
     * Parse company data from HubSpot API response
     *
     * @param jsonBody - body from HubSpot API response
     * @return the company
     */
    public HSCompany parseCompanyData(JSONObject jsonBody) {
        HSCompany company = new HSCompany();

        company.setId(jsonBody.getLong("id"));

        hsService.parseJSONData(jsonBody, company);
        return company;
    }

    /**
     * Get HubSpot company by its ID.
     *
     * @param id - ID of the company
     * @return the company
     * @throws HubSpotException - if HTTP call fails
     */
    public HSCompany getByID(long id) throws HubSpotException {
        String url = COMPANY_URL + id;
        return getCompany(url);
    }

    private HSCompany getCompany(String url) throws HubSpotException {
        try {
            return parseCompanyData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Get companies by their domain.
     *
     * @param domain - domain of companies
     * @return List of companies for the domain
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> getByDomain(String domain) throws HubSpotException {
        List<HSCompany> companies = new ArrayList<>();
        String url = COMPANY_URL + domain;
        JSONArray jsonArray = (JSONArray) httpService.getRequest(url);

        for (int i = 0; i < jsonArray.length(); i++) {
            companies.add(parseCompanyData(jsonArray.optJSONObject(i)));
        }
        return companies;
    }

    /**
     * Patch a company.
     *
     * @param company - company to update
     * @return Updated company
     * @throws HubSpotException - if HTTP call fails
     */
    public HSCompany patch(HSCompany company) throws HubSpotException {
        String url = COMPANY_URL + company.getId();
        String properties = company.toJsonString();

        try {
            httpService.patchRequest(url, properties);
            return company;
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot update company: " + company + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a company.
     *
     * @param company - company to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(HSCompany company) throws HubSpotException {
        delete(company.getId());
    }

    /**
     * Delete a company.
     *
     * @param id - ID of the company to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("Company ID must be provided");
        }
        String url = COMPANY_URL + id;

        httpService.deleteRequest(url);
    }
}
