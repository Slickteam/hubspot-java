package fr.slickteam.hubspot.api.domain.assocation;

import fr.slickteam.hubspot.api.domain.HSObject;

import java.util.Map;

public class HSAssociationTypeInput extends HSObject {

    public enum TypeEnum {PARENT, ENFANT}
    private static final String TYPE_ID = "typeId";
    private static final String LABEL = "label";
    private static final String PARENT_TYPE_ID = "14";
    private static final String PARENT_LABEL = "Child Company";
    private static final String CHILD_TYPE_ID = "13";
    private static final String CHILD_LABEL = "Parent Company";

    public HSAssociationTypeInput() {
    }

    public HSAssociationTypeInput(String typeId, String label, Map<String, String> properties) {
        super(properties);
        this.setTypeId(typeId);
        this.setLabel(label);
    }

    public HSAssociationTypeInput setType(TypeEnum typeEnum) {
        if (typeEnum == TypeEnum.PARENT) {
            setProperty(TYPE_ID, PARENT_TYPE_ID);
            setProperty(LABEL, PARENT_LABEL);
        } else {
            setProperty(TYPE_ID, CHILD_TYPE_ID);
            setProperty(LABEL, CHILD_LABEL);
        }
        return this;
    }

    public void setTypeId(String typeId) {
        setProperty(TYPE_ID, typeId);
    }
    public void setLabel(String label) {
        setProperty(LABEL, label);
    }
    public Integer getTypeId() {
        return Integer.valueOf(getProperty(TYPE_ID));
    }

    public String getLabel() {
        return getProperty(LABEL);
    }
}