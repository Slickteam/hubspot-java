package fr.slickteam.hubspot.api.domain;

/**
 * Model class for Associated Company
 */
public class HSAssociatedCompany {
    private HSAssociationTypeOutput associationType;
    private HSCompany company;


    /**
     * Instantiates a new Hs associated company.
     */
    public HSAssociatedCompany() {
    }

    /**
     * Instantiates a new Hs associated company.
     *
     * @param typeEnum the type enum
     * @param company  the company
     */
    public HSAssociatedCompany(HSAssociationTypeEnum typeEnum, HSCompany company) {
        setAssociationType(typeEnum);
        this.company = company;
    }

    /**
     * Instantiates a new Hs associated company.
     *
     * @param associationType the association type
     * @param company         the company
     */
    public HSAssociatedCompany(HSAssociationTypeOutput associationType, HSCompany company) {
        this.associationType = associationType;
        this.company = company;
    }

    /**
     * Gets association type.
     *
     * @return the association type
     */
    public HSAssociationTypeOutput getAssociationType() {
        return this.associationType;
    }

    /**
     * Sets association type.
     *
     * @param typeEnum the type enum
     */
    public void setAssociationType(HSAssociationTypeEnum typeEnum) {
        this.associationType = new HSAssociationTypeOutput().setType(typeEnum);
    }

    /**
     * Gets company.
     *
     * @return the company
     */
    public HSCompany getCompany() {
        return company;
    }

    /**
     * Sets company.
     *
     * @param company the company
     */
    public void setCompany(HSCompany company) {
        this.company = company;
    }
}

