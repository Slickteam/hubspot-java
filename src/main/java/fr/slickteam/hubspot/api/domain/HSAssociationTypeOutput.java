package fr.slickteam.hubspot.api.domain;

import java.util.Map;

/**
 * The type Hs association type output.
 */
public class HSAssociationTypeOutput extends HSObject {
    private static final String TYPE_ID = "typeId";
    private static final String LABEL = "label";
    private static final String CATEGORY = "category";
    private static final String HS_CATEGORY_OUTPUT = "HUBSPOT_DEFINED";

    /**
     * Instantiates a new Hs association type output.
     */
    public HSAssociationTypeOutput() {
    }

    /**
     * Instantiates a new Hs association type output.
     *
     * @param typeId     the type id
     * @param label      the label
     * @param properties the properties
     */
    public HSAssociationTypeOutput(int typeId, String label, Map<String, String> properties) {
        super(properties);
        this.setTypeId(typeId);
        this.setLabel(label);
        this.setCategory();
    }

    /**
     * Sets type.
     *
     * @param typeEnum the type enum
     * @return the type
     */
    public HSAssociationTypeOutput setType(HSAssociationTypeEnum typeEnum) {
        setTypeId(typeEnum.id);
        setLabel(typeEnum.label);
        this.setCategory();
        return this;
    }

    /**
     * Sets type id.
     *
     * @param typeId the type id
     */
    public void setTypeId(int typeId) {
        setProperty(TYPE_ID, String.valueOf(typeId));
    }

    /**
     * Sets label.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        setProperty(LABEL, label);
    }

    /**
     * Sets category.
     */
    public void setCategory() {
        setProperty(CATEGORY, HS_CATEGORY_OUTPUT);
    }

    /**
     * Gets type id.
     *
     * @return the type id
     */
    public Integer getTypeId() {
        return Integer.valueOf(getProperty(TYPE_ID));
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return getProperty(LABEL);
    }
}