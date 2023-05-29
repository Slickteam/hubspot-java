package fr.slickteam.hubspot.api.domain;
import kong.unirest.json.JSONObject;


public class HSAssocationTypeInput extends HSObject {
    private static final String CATEGORY = "associationCategory";
    private static final String HS_CATEGORY_INPUT = "HUBSPOT_DEFINED";
    private static final String ASSOCIATION_TYPE_ID = "associationTypeId";
    private static final String CHILD_TYPE_ID = "13";
    private static final String PARENT_TYPE_ID = "14";

    public HSAssocationTypeInput() {
    }

    public HSAssocationTypeInput setType(HSAssociationTypeEnum typeEnum) {
        this.setCategory();
        if (typeEnum == HSAssociationTypeEnum.PARENT) {
            setProperty(ASSOCIATION_TYPE_ID, PARENT_TYPE_ID);
        } else {
            setProperty(ASSOCIATION_TYPE_ID, CHILD_TYPE_ID);
        }
        return this;
    }

    public void setAssociationTypeId(String typeId) {
        setProperty(ASSOCIATION_TYPE_ID, typeId);
    }
    public void setCategory() {
        setProperty(CATEGORY, HS_CATEGORY_INPUT);
    }

    public JSONObject getJsonAssociationType (){
        return (JSONObject) new JSONObject(this).get("properties");
    }

}
