package fr.slickteam.hubspot.api.domain;
import kong.unirest.json.JSONObject;


/**
 * The type Hs assocation type input.
 */
public class HSAssocationTypeInput extends HSObject {
    private static final String CATEGORY = "associationCategory";
    private static final String HS_CATEGORY_INPUT = "HUBSPOT_DEFINED";
    private static final String ASSOCIATION_TYPE_ID = "associationTypeId";

    /**
     * Instantiates a new Hs assocation type input.
     */
    public HSAssocationTypeInput() {
    }

    /**
     * Sets type.
     *
     * @param typeEnum the type enum
     * @return the type
     */
    public HSAssocationTypeInput setType(HSAssociationTypeEnum typeEnum) {
        this.setCategory();
        setAssociationTypeId(typeEnum.id);
        return this;
    }

    /**
     * Sets association type id.
     *
     * @param typeId the type id
     */
    public void setAssociationTypeId(int typeId) {
        setProperty(ASSOCIATION_TYPE_ID, String.valueOf(typeId));
    }

    /**
     * Sets category.
     */
    public void setCategory() {
        setProperty(CATEGORY, HS_CATEGORY_INPUT);
    }

    /**
     * Get json association type json object.
     *
     * @return the json object
     */
    public JSONObject getJsonAssociationType (){
        return (JSONObject) new JSONObject(this).get("properties");
    }

}
