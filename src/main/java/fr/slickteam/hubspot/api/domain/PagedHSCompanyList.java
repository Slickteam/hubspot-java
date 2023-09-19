package fr.slickteam.hubspot.api.domain;

import java.util.List;

public class PagedHSCompanyList {

    private List<HSCompany> companies;

    private String after;

    public PagedHSCompanyList(List<HSCompany> companies, String after) {
        this.companies = companies;
        this.after = after;
    }

    public List<HSCompany> getCompanies() {
        return companies;
    }

    public void setCompanies(List<HSCompany> companies) {
        this.companies = companies;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
