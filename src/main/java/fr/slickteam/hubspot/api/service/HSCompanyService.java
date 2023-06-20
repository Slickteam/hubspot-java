package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * HubSpot Company Service
 * <p>
 * Service for managing HubSpot companies
 */
public class HSCompanyService {
    private final HttpService httpService;
    private final HSService hsService;
    private final HSAssociationService associationService;
    private final HSDealService dealService;
    private static final List<String> DEAL_PROPERTIES = List.of("dealname", "dealstage", "pipeline", "date_debut_contrat", "date_fin_contrat", "amount");
    private static final String COMPANY_URL_V3 = "/crm/v3/objects/companies/";
    private static final String COMPANY_URL_V4 = "/crm/v4/objects/companies/";

    private HSContactService contactService;


    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSCompanyService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
        associationService = new HSAssociationService(httpService);
        dealService = new HSDealService(httpService);
    }

    public void setContactService(HSContactService contactService) {
        this.contactService = contactService;
    }


    /**
     * Create a new company
     *
     * @param hsCompany - company to create
     * @return created company
     * @throws HubSpotException - if HTTP call fails
     */
    public HSCompany create(HSCompany hsCompany) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(COMPANY_URL_V3, hsCompany.toJsonString());
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
        String url = COMPANY_URL_V3 + id;
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
     * Get HubSpot company by its ID.
     *
     * @param companyId - ID of company
     * @return A list of associated companies with details
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSAssociatedCompany> getAssociatedCompanies(Long companyId) throws HubSpotException {
        List<JSONObject> associationList = associationService.getCompaniesToCompany(companyId);
        List<HSAssociatedCompany> associatedCompanies = new ArrayList<>();

        for (JSONObject JsonAssociation : associationList) {
            // Initiate associated company parameters
            HSCompany company = getByID((Long) JsonAssociation.get("toObjectId"));
            HSAssociationTypeOutput associationType = new HSAssociationTypeOutput();
            // Create association type from JSON Object
            JSONObject jsonAssociationType = ((JSONArray) JsonAssociation.get("associationTypes")).getJSONObject(0);
            associationType.setLabel((String) jsonAssociationType.get("label"));
            associationType.setTypeId(String.valueOf(jsonAssociationType.get("typeId")));
            // Create associated company and add it to a list
            HSAssociatedCompany associatedCompany = new HSAssociatedCompany(associationType, company);
            associatedCompanies.add(associatedCompany);
        }

        return associatedCompanies;
    }

    /**
     * Get HubSpot contacts for one company.
     *
     * @param companyId - ID of company
     * @return A list of associated contacts
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSContact> getCompanyContacts(Long companyId) throws HubSpotException {
        List<Long> contactIdList = associationService.getCompanyContactIdList(companyId);
        return contactIdList.stream()
                .map(this::getContactById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private HSContact getContactById(Long contactId) {
        try {
            return contactService.getByID(contactId);
        } catch (HubSpotException e) {
            return null;
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
        String url = COMPANY_URL_V3 + domain;
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
        String url = COMPANY_URL_V3 + company.getId();
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
        String url = COMPANY_URL_V3 + id;

        httpService.deleteRequest(url);
    }

    /**
     * Get associated deals id.
     *
     * @param companyId - ID of company
     * @return List of deals long id
     * @throws HubSpotException - if HTTP call fails
     */
    public List<Long> getDealIdList(Long companyId) throws HubSpotException {
        String url = COMPANY_URL_V4 + companyId + "/associations/deals";
        try {
            return hsService.parseJsonArrayToIdList(url);
        } catch (HubSpotException e) {
            throw new HubSpotException("Cannot get company's deals. Company id : " + companyId + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Get associated deals.
     *
     * @param companyId - ID of company
     * @return List of deals for the company
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSDeal> getDeals(Long companyId) throws HubSpotException {
        List<HSDeal> companyDeals = new ArrayList<>();

        // Get associated deals id
        List<Long> dealIdList = getDealIdList(companyId);

        // Get details for each deal id
        for (Long dealId : dealIdList) {
            companyDeals.add(dealService.getByIdAndProperties(dealId, DEAL_PROPERTIES));
        }
        return companyDeals;
    }

    /**
     * Get last associated deal.
     *
     * @param companyId - ID of company
     * @return The last company deal
     * @throws HubSpotException - if HTTP call fails
     */
    public HSDeal getLastDeal(Long companyId) throws HubSpotException {
        // Get associated deals id
        List<Long> dealIdList = getDealIdList(companyId);
        Long maxId = Collections.max(dealIdList);

        // Get details for each deal id
        return dealService.getByIdAndProperties(maxId, DEAL_PROPERTIES);
    }

}
