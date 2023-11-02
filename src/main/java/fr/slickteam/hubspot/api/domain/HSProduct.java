package fr.slickteam.hubspot.api.domain;

import java.math.BigDecimal;
import java.util.Map;

public class HSProduct extends HSObject {

    private static final String ID = "vid";
    private static final String DESCRIPTION = "description";
    private static final String COST_OF_GOODS_SOLD = "hs_cost_of_goods_sold";
    private static final String RECURRING_BILLING_PERIOD = "hs_recurring_billing_period";
    private static final String SKU = "hs_sku";
    private static final String NAME = "name";
    private static final String PRICE = "price";

    public HSProduct() {
    }

    public HSProduct(String name, String description, BigDecimal price, String recurringBillingPeriod) {
        setName(name);
        setDescription(description);
        setPrice(price);
        setRecurringBillingPeriod(recurringBillingPeriod);
    }

    public HSProduct(long id, String name, String description, BigDecimal price, String recurringBillingPeriod, Map<String, String> properties) {
        super(properties);
        setId(id);
        setName(name);
        setDescription(description);
        setPrice(price);
        setRecurringBillingPeriod(recurringBillingPeriod);
    }

    public long getId() {
        return getLongProperty(ID);
    }

    public HSProduct setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    public String getDescription() {
        return getProperty(DESCRIPTION);
    }

    public HSProduct setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return this;
    }

    public String getName() {
        return getProperty(NAME);
    }

    public HSProduct setName(String name) {
        setProperty(NAME, name);
        return this;
    }

    public BigDecimal getPrice() {
        return getBigDecimalProperty(PRICE);
    }

    public HSProduct setPrice(BigDecimal price) {
        setProperty(PRICE, price.toString());
        return this;
    }

    public BigDecimal getCostOfGoodsSold() {
        return getBigDecimalProperty(COST_OF_GOODS_SOLD);
    }

    public HSProduct setCostOfGoodsSold(BigDecimal costOfGoodsSold) {
        setProperty(COST_OF_GOODS_SOLD, costOfGoodsSold.toString());
        return this;
    }

    public String getRecurringBillingPeriod() {
        return getProperty(RECURRING_BILLING_PERIOD);
    }

    public HSProduct setRecurringBillingPeriod(String recurringBillingPeriod) {
        setProperty(RECURRING_BILLING_PERIOD, recurringBillingPeriod);
        return this;
    }

    public String getSku() {
        return getProperty(SKU);
    }

    public HSProduct setSku(String sku) {
        setProperty(SKU, sku);
        return this;
    }

}
