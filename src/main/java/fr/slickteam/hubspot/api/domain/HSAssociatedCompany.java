package fr.slickteam.hubspot.api.domain;

/**
 * Model class for Associated Company
 */
public class HSAssociatedCompany {
    private HSAssociationTypeOutput associationType;
    private HSCompany company;


    public HSAssociatedCompany() {
    }

    public HSAssociatedCompany(HSAssociationTypeEnum typeEnum, HSCompany company) {
        setAssociationType(typeEnum);
        this.company = company;
    }

    public HSAssociatedCompany(HSAssociationTypeOutput associationType, HSCompany company) {
        this.associationType = associationType;
        this.company = company;
    }

    public HSAssociationTypeOutput getAssociationType() {
        return this.associationType;
    }

    public void setAssociationType(HSAssociationTypeEnum typeEnum) {
        HSAssociationTypeOutput createdAssociationType =new HSAssociationTypeOutput().setType(typeEnum);
        this.associationType = createdAssociationType;
    }

    public HSCompany getCompany() {
        return company;
    }

    public void setCompany(HSCompany company) {
        this.company = company;
    }
}

