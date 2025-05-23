package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.HubSpotOrdering;
import fr.slickteam.hubspot.api.utils.HubSpotSearchOperator;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static fr.slickteam.hubspot.api.utils.JsonUtils.getJsonInputList;
import static fr.slickteam.hubspot.api.utils.JsonUtils.getJsonProperties;
import static java.lang.System.Logger.Level.DEBUG;

/**
 * HubSpot Company Service
 * <p>
 * Service for managing HubSpot companies
 */
public class HSCompanyService {

    private static final System.Logger log = System.getLogger(HSCompanyService.class.getName());
    private static final String PAGING = "paging";
    private static final String LOG_PROPERTIES = " | properties : ";
    private static final String RESULTS = "results";
    private static final String NOT_FOUND = "Not Found";
    private final HttpService httpService;
    private final HSService hsService;
    private HSContactService contactService;
    private final HSAssociationService associationService;
    private final HSDealService dealService;
    private static final List<String> DEAL_PROPERTIES = List.of("dealname", "dealstage", "pipeline", "date_debut_contrat", "date_fin_contrat", "amount");
    private static final String COMPANY_URL_V3 = "/crm/v3/objects/companies/";
    private static final String COMPANY_URL_V4 = "/crm/v4/objects/companies/";
    private static final String BATCH = "batch/";
    private static final String READ = "read/";
    private static final String SEARCH = "search";


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

    /**
     * Sets contact service.
     *
     * @param contactService the contact service
     */
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
        log.log(DEBUG, "create - hsCompany : " + hsCompany);
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
        log.log(DEBUG, "addContact - contactId : " + contactId + " | companyId : " + companyId);
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
        log.log(DEBUG, "getByID - id : " + id);
        String url = COMPANY_URL_V3 + id;
        return getCompany(url);
    }

    private HSCompany getCompany(String url) throws HubSpotException {
        try {
            return parseCompanyData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals(NOT_FOUND)) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Get HubSpot a list of companies by id with properties.
     *
     * @param idList     - ID list of companies
     * @param properties - List of string properties as company name or deal description
     * @return a company list with properties
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> getCompanyListByIdAndProperties(List<Long> idList, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getCompanyListByIdAndProperties - idList : " + idList + LOG_PROPERTIES + properties);
        String formatProperties = getJsonProperties(properties);
        String formatIdList = getJsonInputList(idList);
        String associationProperties = "{\n" +
                "  \"properties\": [\n" + formatProperties +
                "   ],\n" +
                "  \"propertiesWithHistory\": [],\n" +
                "   \"inputs\": [\n" + formatIdList +
                "   ]\n" +
                "}";
        String url = COMPANY_URL_V3 + BATCH + READ;
        try {
            JSONObject response = (JSONObject) httpService.postRequest(url, associationProperties);
            JSONArray jsonList = response.optJSONArray(RESULTS);
            List<HSCompany> companies = new ArrayList<>(jsonList.length());
            for (int i = 0; i < jsonList.length(); i++) {
                companies.add(parseCompanyData(jsonList.optJSONObject(i)));
            }
            return companies;
        } catch (HubSpotException e) {
            if (e.getMessage().equals(NOT_FOUND)) {
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }

    /**
     * Get HubSpot company by its ID with list of properties.
     *
     * @param id         - ID of the company
     * @param properties - List of string properties as company name or deal description
     * @return the company with the selected properties
     * @throws HubSpotException - if HTTP call fails
     */
    public HSCompany getByIdAndProperties(long id, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getByIdAndProperties - id : " + id + LOG_PROPERTIES + properties);
        String propertiesUrl = String.join(",", properties);
        String url = COMPANY_URL_V3 + id + "?properties=" + propertiesUrl;
        return getCompany(url);
    }

    /**
     * Get HubSpot associated companies by company ID.
     *
     * @param companyId - ID of company
     * @return A list of associated companies with details
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSAssociatedCompany> getAssociatedCompanies(Long companyId) throws HubSpotException {
        log.log(DEBUG, "getAssociatedCompanies - companyId : " + companyId);
        List<JSONObject> associationList = associationService.getCompaniesToCompany(companyId);
        List<HSAssociatedCompany> associatedCompanies = new ArrayList<>();

        for (JSONObject JsonAssociation : associationList) {
            // Initiate associated company parameters
            HSCompany company = getByID((Long) JsonAssociation.get("toObjectId"));
            HSAssociationTypeOutput associationType = new HSAssociationTypeOutput();
            // Create association type from JSON Object
            JSONObject jsonAssociationType = ((JSONArray) JsonAssociation.get("associationTypes")).getJSONObject(0);
            associationType.setLabel((String) jsonAssociationType.get("label"));
            associationType.setTypeId((Integer) jsonAssociationType.get("typeId"));
            // Create associated company and add it to a list
            HSAssociatedCompany associatedCompany = new HSAssociatedCompany(associationType, company);
            associatedCompanies.add(associatedCompany);
        }

        return associatedCompanies;
    }

    /**
     * Get HubSpot associated companies by company ID.
     *
     * @param companyId  - ID of company
     * @param properties the properties
     * @return A list of associated companies with details
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSAssociatedCompany> getAssociatedCompanies(Long companyId, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getAssociatedCompanies - companyId : " + companyId);
        List<JSONObject> associationList = associationService.getCompaniesToCompany(companyId);
        List<HSAssociatedCompany> associatedCompanies = new ArrayList<>();

        for (JSONObject JsonAssociation : associationList) {
            // Initiate associated company parameters
            HSCompany company = getByIdAndProperties((Long) JsonAssociation.get("toObjectId"), properties);
            HSAssociationTypeOutput associationType = new HSAssociationTypeOutput();
            // Create association type from JSON Object
            JSONObject jsonAssociationType = ((JSONArray) JsonAssociation.get("associationTypes")).getJSONObject(0);
            associationType.setLabel((String) jsonAssociationType.get("label"));
            associationType.setTypeId((Integer) jsonAssociationType.get("typeId"));
            // Create associated company and add it to a list
            HSAssociatedCompany associatedCompany = new HSAssociatedCompany(associationType, company);
            associatedCompanies.add(associatedCompany);
        }

        return associatedCompanies;
    }

    /**
     * Get HubSpot associated contacts for a list of company ID.
     *
     * @param companyIds - IDs of companies
     * @return A map containing for each company ID a list of contact IDs
     * @throws HubSpotException - if HTTP call fails
     */
    public Map<Long, List<Long>> getAssociatedContacts(List<Long> companyIds) throws HubSpotException {
        log.log(DEBUG, "getAssociatedContacts - companyIds : " + companyIds);
        List<JSONObject> associationList = associationService.companiesToContacts(companyIds);
        Map<Long, List<Long>> associatedContacts = new HashMap<>();

        for (JSONObject associationsByCompany : associationList) {
            // Initiate associated company parameters
            Long companyId = Long.parseLong(associationsByCompany.getJSONObject("from").get("id").toString());
            JSONArray contacts = associationsByCompany.getJSONArray("to");

            List<Long> contactIds = new ArrayList<>();

            for (int i = 0, max = contacts.length(); i < max; i++) {
                contactIds.add(Long.parseLong(contacts.getJSONObject(i).get("toObjectId").toString()));
            }

            associatedContacts.put(companyId, contactIds);
        }

        return associatedContacts;
    }

    /**
     * Get HubSpot contacts for one company.
     *
     * @param companyId  - ID of company
     * @param properties the properties
     * @return A list of associated contacts
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSContact> getCompanyContacts(Long companyId, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getCompanyContacts - companyId : " + companyId + " ; properties : " + properties);
        List<Long> contactIdList = associationService.getCompanyContactIdList(companyId);
        return contactService.getContactListByIdAndProperties(contactIdList, properties);
    }

    /**
     * Get companies by their domain.
     *
     * @param domain - domain of companies
     * @return List of companies for the domain
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> getByDomain(String domain) throws HubSpotException {
        log.log(DEBUG, "getByDomain - domain : " + domain);
        List<HSCompany> companies = new ArrayList<>();
        String url = COMPANY_URL_V3 + domain;
        JSONArray jsonArray = (JSONArray) httpService.getRequest(url);

        for (int i = 0; i < jsonArray.length(); i++) {
            companies.add(parseCompanyData(jsonArray.optJSONObject(i)));
        }
        return companies;
    }

    /**
     * Get HubSpot companies with pagination and a list of properties.
     *
     * @param after      - paging cursor token of the last successfully read resource in HubSpot
     * @param limit      - size of the page
     * @param properties - List of string properties as company name or deal description
     * @return the page with the list of companies and the token for next page
     * @throws HubSpotException - if HTTP call fails
     */
    public PagedHSCompanyList getCompanies(String after, int limit, List<String> properties) throws HubSpotException {
        log.log(DEBUG, "getCompanies - after : " + after + " | limit : " + limit + LOG_PROPERTIES + properties);
        String propertiesUrl = String.join(",", properties);
        String url = COMPANY_URL_V3 + "?limit=" + limit + "&after=" + after + "&properties=" + propertiesUrl;

        try {
            JSONObject response = (JSONObject) httpService.getRequest(url);
            JSONArray jsonList = response.optJSONArray(RESULTS);
            List<HSCompany> companies = new ArrayList<>(jsonList.length());
            for (int i = 0; i < jsonList.length(); i++) {
                companies.add(parseCompanyData(jsonList.optJSONObject(i)));
            }
            String nextPageToken = null;
            if (response.has(PAGING) && ((JSONObject) response.get(PAGING)).has("next")) {
                nextPageToken = ((JSONObject) ((JSONObject) response.get(PAGING)).get("next")).getString("after");
            }
            return new PagedHSCompanyList(companies, nextPageToken);
        } catch (HubSpotException e) {
            if (e.getMessage().equals(NOT_FOUND)) {
                return new PagedHSCompanyList(Collections.emptyList(), "0");
            } else {
                throw e;
            }
        }
    }

    /**
     * Get HubSpot total number of companies (ie for pagination)
     *
     * @return the number of companies in HubSpot
     * @throws HubSpotException - if HTTP call fails
     */
    public Long getTotalNumberOfCompanies() throws HubSpotException {
        log.log(DEBUG, "getTotalNumberOfCompanies");
        String url = COMPANY_URL_V3 + SEARCH;
        String searchProperties = "{\n" +
                "  \"filterGroups\": [\n" +
                "    {\n" +
                "      \"filters\": [\n" +
                "        {\n" +
                "          \"propertyName\": \"hs_object_id\",\n" +
                "          \"operator\": \"HAS_PROPERTY\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"propertyName\": \"hs_parent_company_id\",\n" +
                "          \"operator\": \"HAS_PROPERTY\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"sorts\": [\n" +
                "    \"name\"\n" +
                "  ],\n" +
                "  \"properties\": [\n" +
                "    \"name\"\n" +
                "  ],\n" +
                "  \"limit\": 0,\n" +
                "  \"after\": 0\n" +
                "}";

        JSONObject response = (JSONObject) httpService.postRequest(url, searchProperties);
        return Long.parseLong(response.get("total") + "");
    }

    /**
     * Query HubSpot companies with default searchable properties : website, phone, name and domain
     *
     * @param input              - string to query in company website, phone, name and domain
     * @param responseProperties - list of properties to return
     * @param limit              - size of the page
     * @return a company list filtered
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> queryByDefaultSearchableProperties(String input, List<String> responseProperties, int limit) throws HubSpotException {
        log.log(DEBUG, "queryByDefaultSearchableProperties");
        String url = COMPANY_URL_V3 + SEARCH;

        String responsePropertiesList = responseProperties.stream()
                .map(property -> "    \"" + property + "\"")
                .collect(Collectors.joining(",\n"));

        String queryProperties = "{\n" +
                "  \"query\": \"" + input + "\"," +
                "  \"properties\": [\n" +
                responsePropertiesList +
                "  ],\n" +
                "  \"limit\": " + limit + ",\n" +
                "  \"after\": 0\n" +
                "}";

        return sendCompanySearchRequest(url, queryProperties);
    }

    /**
     * Search HubSpot companies filtered by properties
     *
     * @param propertiesAndValuesFilters - map of properties and values to filter
     * @param responseProperties         - list of properties to return
     * @param limit                      - size of the page
     * @return a company list filtered
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> searchFilteredByProperties(Map<String, String> propertiesAndValuesFilters,
                                                      List<String> responseProperties,
                                                      int limit) throws HubSpotException {
        log.log(DEBUG, "searchFilteredByProperties");
        return searchSortedFilteredByProperties(propertiesAndValuesFilters,
                responseProperties,
                List.of(new HSSortOrder("name", HubSpotOrdering.DESCENDING)),
                limit);
    }

    /**
     * Search HubSpot companies filtered by properties
     *
     * @param propertiesAndValuesFilters - map of properties and values to filter
     * @param responseProperties         - list of properties to return
     * @param sortOrders                 - list of sort orders to apply to the search results
     * @param limit                      - size of the page
     * @return a company list filtered
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> searchSortedFilteredByProperties(Map<String, String> propertiesAndValuesFilters,
                                                            List<String> responseProperties,
                                                            List<HSSortOrder> sortOrders,
                                                            int limit) throws HubSpotException {
        log.log(DEBUG, "searchSortedFilteredByProperties");

        List<HSSearchPropertyFilter> filtersPropertyList = propertiesAndValuesFilters.entrySet().stream()
                .map(entry -> new HSSearchPropertyFilter(entry.getKey(),
                        null,
                        entry.getValue(),
                        null,
                        HubSpotSearchOperator.EQ))
                .collect(Collectors.toList());

        return searchSortedFiltered(filtersPropertyList, responseProperties, sortOrders, limit);
    }

    /**
     * Search HubSpot companies filtered by properties
     *
     * @param propertiesFilters  - list of properties, operator and values to filter
     * @param responseProperties - list of properties to return
     * @param sortOrders         - list of sort orders to apply to the search results
     * @param limit              - size of the page
     * @return a company list filtered
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> searchSortedFiltered(List<HSSearchPropertyFilter> propertiesFilters,
                                                List<String> responseProperties,
                                                List<HSSortOrder> sortOrders,
                                                int limit) throws HubSpotException {
        log.log(DEBUG, "searchSortedFiltered");
        String url = COMPANY_URL_V3 + SEARCH;

        String filtersPropertyList = propertiesFilters.stream()
                .map(HSCompanyService::buildFilter)
                .collect(Collectors.joining(",\n"));

        String responsePropertiesList = responseProperties.stream()
                .map(property -> "    \"" + property + "\"")
                .collect(Collectors.joining(",\n"));

        String sorts = sortOrders.stream()
                .map(so -> "   {\n\"propertyName\" : \"" + so.getProperty() + "\",\n" +
                        "   \"direction\": \"" + so.getOrdering().name() + "\"}\n")
                .collect(Collectors.joining(",\n"));

        String filterGroupsProperties =
                "{\n" +
                        "  \"filterGroups\": [\n" + filtersPropertyList +
                        "  ],\n" +
                        "  \"sorts\": [\n" + sorts +
                        "  ],\n" +
                        "  \"properties\": [\n" + responsePropertiesList +
                        "  ],\n" +
                        "  \"limit\": " + limit + "\n" +
                        "}";

        return sendCompanySearchRequest(url, filterGroupsProperties);
    }

    private static String buildFilter(HSSearchPropertyFilter propertyFilter) {
        String filter = " {\n" +
                "      \"filters\": [\n" +
                "        {\n" +
                "          \"propertyName\": \"" + propertyFilter.getPropertyName() + "\",\n" +
                "          \"operator\": \"" + propertyFilter.getOperator().name() + "\",\n";

        switch (propertyFilter.getOperator()) {
            case IN:
            case NOT_IN:
                filter += "          \"values\": \"[" + propertyFilter.getValues()
                        .stream()
                        .map(val -> "\"" + val + "\"")
                        .collect(Collectors.joining(",")) + "]\"\n";
                break;
            case BETWEEN:
                filter += "          \"highValue\": \"" + propertyFilter.getHighValue() + "\",\n";
                filter += "          \"value\": \"" + propertyFilter.getValue() + "\"\n";
                break;
            case HAS_PROPERTY:
            case NOT_HAS_PROPERTY:
                filter += "          \"value\": \"\"\n";
                break;
            default:
                filter += "          \"value\": \"" + propertyFilter.getValue() + "\"\n";
                break;
        }

        filter += "        }" +
                "      ]\n" +
                "    }\n";
        return filter;
    }


    /**
     * Send HubSpot companies request with properties to query or filter
     *
     * @param url        - url to send the request
     * @param properties - properties to query or filter
     * @return a company list filtered
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSCompany> sendCompanySearchRequest(String url, String properties) throws HubSpotException {
        List<HSCompany> companies = Collections.emptyList();

        try {
            JSONObject response = (JSONObject) httpService.postRequest(url, properties);
            JSONArray jsonList = response.optJSONArray(RESULTS);
            companies = new ArrayList<>(jsonList.length());
            for (int i = 0; i < jsonList.length(); i++) {
                companies.add(parseCompanyData(jsonList.optJSONObject(i)));
            }
        } catch (HubSpotException e) {
            if (e.getMessage().equals(NOT_FOUND)) {
                return companies;
            } else {
                throw e;
            }
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
        log.log(DEBUG, "patch - company : " + company);
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
        log.log(DEBUG, "delete - company : " + company);
        delete(company.getId());
    }

    /**
     * Delete a company.
     *
     * @param id - ID of the company to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        log.log(DEBUG, "delete - id : " + id);
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
        log.log(DEBUG, "getDealIdList - companyId : " + companyId);
        String url = COMPANY_URL_V4 + companyId + "/associations/deals";
        try {
            return hsService.parseJsonObjectToIdList(url);
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
        log.log(DEBUG, "getDeals - companyId : " + companyId);
        List<HSDeal> companyDeals = new ArrayList<>();

        // Get associated deals id
        List<Long> dealIdList = getDealIdList(companyId);

        if (dealIdList.isEmpty()) {
            return Collections.emptyList();
        }

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
    public HSDeal getLastDeal(long companyId) throws HubSpotException {
        log.log(DEBUG, "getLastDeal - companyId : " + companyId);
        // Get associated deals
        List<HSDeal> dealList = getDeals(companyId);

        if (dealList == null || dealList.isEmpty()) {
            return null;
        }

        return dealList.stream()
                .max(Comparator.comparing(HSDeal::getCreatedDate))
                .orElse(null);
    }

    /**
     * Get HubSpot associated deals for a list of company ID.
     *
     * @param companyIds - IDs of companies
     * @return A map containing for each company ID a list of deal IDs
     * @throws HubSpotException - if HTTP call fails
     */
    public Map<Long, List<Long>> getAssociatedDealIds(List<Long> companyIds) throws HubSpotException {
        log.log(DEBUG, "getAssociatedDealIds - companyIds : " + companyIds);
        List<JSONObject> associationList = associationService.companiesToDeals(companyIds);
        Map<Long, List<Long>> associatedDeals = new HashMap<>();

        for (JSONObject associationsByCompany : associationList) {
            // Initiate associated company parameters
            Long companyId = Long.parseLong(associationsByCompany.getJSONObject("from").get("id").toString());
            JSONArray deals = associationsByCompany.getJSONArray("to");

            List<Long> dealIds = new ArrayList<>();

            for (int i = 0, max = deals.length(); i < max; i++) {
                dealIds.add(Long.parseLong(deals.getJSONObject(i).get("toObjectId").toString()));
            }

            associatedDeals.put(companyId, dealIds);
        }

        return associatedDeals;
    }

}
