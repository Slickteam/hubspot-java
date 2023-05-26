package fr.slickteam.hubspot.api.domain.assocation;

import fr.slickteam.hubspot.api.domain.HSCompany;

/**
 * Model class for Associated Company
 */
public class HSAssociatedCompany {
    private HSAssociationTypeInput associationType;
    private HSCompany company;


    public HSAssociatedCompany() {
    }

    public HSAssociatedCompany(HSAssociationTypeInput.TypeEnum typeEnum, HSCompany company) {
        setAssociationType(typeEnum);
        this.company = company;
    }

    public HSAssociatedCompany(HSAssociationTypeInput associationType, HSCompany company) {
        this.associationType = associationType;
        this.company = company;
    }

    public HSAssociationTypeInput getAssociationType() {
        return this.associationType;
    }

    public void setAssociationType(HSAssociationTypeInput.TypeEnum typeEnum) {
        HSAssociationTypeInput createdAssociationType =new HSAssociationTypeInput().setType(typeEnum);
        this.associationType = createdAssociationType;
    }

    public HSCompany getCompany() {
        return company;
    }

    public void setCompany(HSCompany company) {
        this.company = company;
    }
}

