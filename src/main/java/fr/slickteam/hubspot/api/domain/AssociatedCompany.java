package fr.slickteam.hubspot.api.domain;

/**
 * Model class for Associated Company
 */
public class AssociatedCompany {

    public enum CompanyAssociationType {
        CHILD_COMPANY, PARENT_COMPANY
    }
    private CompanyAssociationType associationType;
    private HSCompany company;


    public AssociatedCompany() {
    }

    public AssociatedCompany(String associationType, HSCompany company) {
        this.setAssociationType(associationType);
        this.company = company;
    }

    public String getAssociationType() {
        return this.associationType.toString();
    }

    public void setAssociationType(String stringType) {
        this.associationType = CompanyAssociationType.valueOf(stringType);
    }

    public HSCompany getCompany() {
        return company;
    }

    public void setCompany(HSCompany company) {
        this.company = company;
    }
}

