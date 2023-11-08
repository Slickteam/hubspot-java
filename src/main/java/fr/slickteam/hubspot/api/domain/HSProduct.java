package fr.slickteam.hubspot.api.domain;

import java.math.BigDecimal;
import java.util.Map;

/**
 * The type Hs product.
 */
public class HSProduct extends HSObject {

    private static final String ID = "vid";
    private static final String DESCRIPTION = "description";
    private static final String COST_OF_GOODS_SOLD = "hs_cost_of_goods_sold";
    private static final String RECURRING_BILLING_PERIOD = "hs_recurring_billing_period";
    private static final String SKU = "hs_sku";
    private static final String NAME = "name";
    private static final String PRICE = "price";

    /**
     * Instantiates a new Hs product.
     */
    public HSProduct() {
    }

    /**
     * Instantiates a new Hs product.
     *
     * @param name                   the name
     * @param description            the description
     * @param price                  the price
     * @param recurringBillingPeriod the recurring billing period
     */
    public HSProduct(String name, String description, BigDecimal price, String recurringBillingPeriod) {
        setName(name);
        setDescription(description);
        setPrice(price);
        setRecurringBillingPeriod(recurringBillingPeriod);
    }

    /**
     * Instantiates a new Hs product.
     *
     * @param id                     the id
     * @param name                   the name
     * @param description            the description
     * @param price                  the price
     * @param recurringBillingPeriod the recurring billing period
     * @param properties             the properties
     */
    public HSProduct(long id, String name, String description, BigDecimal price, String recurringBillingPeriod, Map<String, String> properties) {
        super(properties);
        setId(id);
        setName(name);
        setDescription(description);
        setPrice(price);
        setRecurringBillingPeriod(recurringBillingPeriod);
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
    public HSProduct setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return getProperty(DESCRIPTION);
    }

    /**
     * Sets description.
     *
     * @param description the description
     * @return the description
     */
    public HSProduct setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return getProperty(NAME);
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public HSProduct setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public BigDecimal getPrice() {
        return getBigDecimalProperty(PRICE);
    }

    /**
     * Sets price.
     *
     * @param price the price
     * @return the price
     */
    public HSProduct setPrice(BigDecimal price) {
        setProperty(PRICE, price.toString());
        return this;
    }

    /**
     * Gets cost of goods sold.
     *
     * @return the cost of goods sold
     */
    public BigDecimal getCostOfGoodsSold() {
        return getBigDecimalProperty(COST_OF_GOODS_SOLD);
    }

    /**
     * Sets cost of goods sold.
     *
     * @param costOfGoodsSold the cost of goods sold
     * @return the cost of goods sold
     */
    public HSProduct setCostOfGoodsSold(BigDecimal costOfGoodsSold) {
        setProperty(COST_OF_GOODS_SOLD, costOfGoodsSold.toString());
        return this;
    }

    /**
     * Gets recurring billing period.
     *
     * @return the recurring billing period
     */
    public String getRecurringBillingPeriod() {
        return getProperty(RECURRING_BILLING_PERIOD);
    }

    /**
     * Sets recurring billing period.
     *
     * @param recurringBillingPeriod the recurring billing period
     * @return the recurring billing period
     */
    public HSProduct setRecurringBillingPeriod(String recurringBillingPeriod) {
        setProperty(RECURRING_BILLING_PERIOD, recurringBillingPeriod);
        return this;
    }

    /**
     * Gets sku.
     *
     * @return the sku
     */
    public String getSku() {
        return getProperty(SKU);
    }

    /**
     * Sets sku.
     *
     * @param sku the sku
     * @return the sku
     */
    public HSProduct setSku(String sku) {
        setProperty(SKU, sku);
        return this;
    }

}
