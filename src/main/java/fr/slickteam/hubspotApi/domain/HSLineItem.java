package fr.slickteam.hubspotApi.domain;

import java.util.Map;

public class HSLineItem extends HSObject {

    private static String ID = "vid";
    private static String HS_PRODUCT_ID = "hs_product_id";
    private static String QUANTITY = "quantity";

    public HSLineItem() {
    }

    public HSLineItem(String hsProductId, long quantity) {
        this.setHsProductId(hsProductId);
        this.setQuantity(quantity);
    }

    public HSLineItem(long id, String hsProductId, long quantity,
                      Map<String, String> properties) {
        super(properties);
        this.setHsProductId(hsProductId);
        this.setQuantity(quantity);
    }


    public long getId() {
        return getLongProperty(ID);
    }

    public HSLineItem setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    public String getHsProductId() {
        return getProperty(HS_PRODUCT_ID);
    }

    public HSLineItem setHsProductId(String hsProductId) {
        setProperty(HS_PRODUCT_ID, hsProductId);
        return this;
    }

    public long getQuantity() {
        return getLongProperty(QUANTITY);
    }

    public HSLineItem setQuantity(long quantity) {
        setProperty(QUANTITY, Long.toString(quantity));
        return this;
    }

}
