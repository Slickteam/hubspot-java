package fr.slickteam.hubspot.api.domain;

import java.util.List;

/**
 * The type Paged hs company list.
 */
public class PagedHSCompanyList {

    private List<HSCompany> companies;

    private String after;

    /**
     * Instantiates a new Paged hs company list.
     *
     * @param companies the companies
     * @param after     the after
     */
    public PagedHSCompanyList(List<HSCompany> companies, String after) {
        this.companies = companies;
        this.after = after;
    }

    /**
     * Gets companies.
     *
     * @return the companies
     */
    public List<HSCompany> getCompanies() {
        return companies;
    }

    /**
     * Sets companies.
     *
     * @param companies the companies
     */
    public void setCompanies(List<HSCompany> companies) {
        this.companies = companies;
    }

    /**
     * Gets after.
     *
     * @return the after
     */
    public String getAfter() {
        return after;
    }

    /**
     * Sets after.
     *
     * @param after the after
     */
    public void setAfter(String after) {
        this.after = after;
    }
}
