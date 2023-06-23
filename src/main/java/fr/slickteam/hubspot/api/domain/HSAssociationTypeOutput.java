package fr.slickteam.hubspot.api.domain;

import java.util.Map;

public class HSAssociationTypeOutput extends HSObject {
    private static final String TYPE_ID = "typeId";
    private static final String LABEL = "label";
    private static final String CATEGORY = "category";
    private static final String HS_CATEGORY_OUTPUT = "HUBSPOT_DEFINED";

    public HSAssociationTypeOutput() {
    }

    public HSAssociationTypeOutput(int typeId, String label, Map<String, String> properties) {
        super(properties);
        this.setTypeId(typeId);
        this.setLabel(label);
        this.setCategory();
    }

    public HSAssociationTypeOutput setType(HSAssociationTypeEnum typeEnum) {
        setTypeId(typeEnum.id);
        setLabel(typeEnum.label);
        this.setCategory();
        return this;
    }

    public void setTypeId(int typeId) {
        setProperty(TYPE_ID, String.valueOf(typeId));
    }

    public void setLabel(String label) {
        setProperty(LABEL, label);
    }

    public void setCategory() {
        setProperty(CATEGORY, HS_CATEGORY_OUTPUT);
    }

    public Integer getTypeId() {
        return Integer.valueOf(getProperty(TYPE_ID));
    }

    public String getLabel() {
        return getProperty(LABEL);
    }
}