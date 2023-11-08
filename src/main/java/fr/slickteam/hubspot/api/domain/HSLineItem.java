package fr.slickteam.hubspot.api.domain;

import java.util.Map;

/**
 * The type Hs line item.
 */
public class HSLineItem extends HSObject {

    private static final String ID = "vid";
    private static final String HS_PRODUCT_ID = "hs_product_id";
    private static final String QUANTITY = "quantity";

    /**
     * Instantiates a new Hs line item.
     */
    public HSLineItem() {
    }

    /**
     * Instantiates a new Hs line item.
     *
     * @param hsProductId the hs product id
     * @param quantity    the quantity
     */
    public HSLineItem(String hsProductId, long quantity) {
        this.setHsProductId(hsProductId);
        this.setQuantity(quantity);
    }

    /**
     * Instantiates a new Hs line item.
     *
     * @param id          the id
     * @param hsProductId the hs product id
     * @param quantity    the quantity
     * @param properties  the properties
     */
    public HSLineItem(long id, String hsProductId, long quantity,
                      Map<String, String> properties) {
        super(properties);
        this.setHsProductId(hsProductId);
        this.setQuantity(quantity);
    }


    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return getLongProperty(ID);
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public HSLineItem setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    /**
     * Gets hs product id.
     *
     * @return the hs product id
     */
    public String getHsProductId() {
        return getProperty(HS_PRODUCT_ID);
    }

    /**
     * Sets hs product id.
     *
     * @param hsProductId the hs product id
     * @return the hs product id
     */
    public HSLineItem setHsProductId(String hsProductId) {
        setProperty(HS_PRODUCT_ID, hsProductId);
        return this;
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    public long getQuantity() {
        return getLongProperty(QUANTITY);
    }

    /**
     * Sets quantity.
     *
     * @param quantity the quantity
     * @return the quantity
     */
    public HSLineItem setQuantity(long quantity) {
        setProperty(QUANTITY, Long.toString(quantity));
        return this;
    }

}
