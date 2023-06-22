package fr.slickteam.hubspot.api.domain;

import java.util.Map;

public class HSAssociationTypeOutput extends HSObject {
    private static final String TYPE_ID = "typeId";
    private static final String LABEL = "label";
    private static final String CATEGORY = "category";
    private static final String HS_CATEGORY_OUTPUT = "HUBSPOT_DEFINED";
    private static final String PARENT_TYPE_ID = "14";
    private static final String PARENT_LABEL = "Parent Company";
    private static final String CHILD_TYPE_ID = "13";
    private static final String CHILD_LABEL = "Child Company";

    public HSAssociationTypeOutput() {
    }

    public HSAssociationTypeOutput(String typeId, String label, Map<String, String> properties) {
        super(properties);
        this.setTypeId(typeId);
        this.setLabel(label);
        this.setCategory();
    }

    public HSAssociationTypeOutput setType(HSAssociationTypeEnum typeEnum) {
        if (typeEnum == HSAssociationTypeEnum.PARENT) {
            setProperty(TYPE_ID, PARENT_TYPE_ID);
            setProperty(LABEL, PARENT_LABEL);
        } else {
            setProperty(TYPE_ID, CHILD_TYPE_ID);
            setProperty(LABEL, CHILD_LABEL);
        }
        this.setCategory();
        return this;
    }

    public void setTypeId(String typeId) {
        setProperty(TYPE_ID, typeId);
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