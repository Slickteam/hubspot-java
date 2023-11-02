package fr.slickteam.hubspot.api.domain;
import kong.unirest.json.JSONObject;


public class HSAssocationTypeInput extends HSObject {
    private static final String CATEGORY = "associationCategory";
    private static final String HS_CATEGORY_INPUT = "HUBSPOT_DEFINED";
    private static final String ASSOCIATION_TYPE_ID = "associationTypeId";

    public HSAssocationTypeInput() {
    }

    public HSAssocationTypeInput setType(HSAssociationTypeEnum typeEnum) {
        this.setCategory();
        setAssociationTypeId(typeEnum.id);
        return this;
    }

    public void setAssociationTypeId(int typeId) {
        setProperty(ASSOCIATION_TYPE_ID, String.valueOf(typeId));
    }
    public void setCategory() {
        setProperty(CATEGORY, HS_CATEGORY_INPUT);
    }
    public JSONObject getJsonAssociationType (){
        return (JSONObject) new JSONObject(this).get("properties");
    }

}
