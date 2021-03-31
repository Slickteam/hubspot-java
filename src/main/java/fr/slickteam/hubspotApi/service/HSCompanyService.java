package fr.slickteam.hubspotApi.service;

import fr.slickteam.hubspotApi.domain.HSCompany;
import fr.slickteam.hubspotApi.utils.HubSpotException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HSCompanyService {

    private final static String COMPANY_URL = "/crm/v3/objects/companies/";
    private HttpService httpService;
    private HSService hsService;

    public HSCompanyService(HttpService httpService) {
        this.httpService = httpService;
        hsService = new HSService(httpService);
    }

	public HSCompany create(HSCompany HSCompany) throws HubSpotException {
		JSONObject jsonObject = (JSONObject) httpService.postRequest(COMPANY_URL, HSCompany.toJsonString());
		HSCompany.setId(jsonObject.getLong("id"));
		return HSCompany;
    }

	public void addContact(long contactId, long companyId) throws HubSpotException {
		String url = "/companies/v2/companies/" + companyId + "/contacts/" + contactId;
		httpService.putRequest(url, "");
	}

	public HSCompany parseCompanyData(JSONObject jsonBody) {
		HSCompany company = new HSCompany();

		company.setId(jsonBody.getLong("id"));

		hsService.parseJSONData(jsonBody, company);
		return company;
	}

    public HSCompany getByID(long id) throws HubSpotException{
        String url = COMPANY_URL + id ;
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

	public List<HSCompany> getByDomain(String domain) throws HubSpotException {
		List<HSCompany> companies = new ArrayList<>();
		String url = COMPANY_URL + domain;
		JSONArray jsonArray = (JSONArray)httpService.getRequest(url);

		for (int i = 0; i < jsonArray.length(); i++) {
			companies.add(parseCompanyData(jsonArray.optJSONObject(i)));
		}
		return companies;
	}

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

    public void delete(HSCompany company) throws HubSpotException {
        delete(company.getId());
    }

    public void delete(Long id) throws HubSpotException {
        if (id == 0) {
            throw new HubSpotException("User ID must be provided");
        }
        String url = COMPANY_URL + id;

        httpService.deleteRequest(url);
    }
}
