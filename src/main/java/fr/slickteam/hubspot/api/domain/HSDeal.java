package fr.slickteam.hubspot.api.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public class HSDeal extends HSObject {

    private static final String ID = "vid";
    private static final String DEAL_NAME = "dealname";
    private static final String DEAL_STAGE = "dealstage";
    private static final String PIPELINE = "pipeline";
    private static final String AMOUNT = "amount";
    private static final String CLOSE_DATE = "closedate";

    public HSDeal() {
    }

    public HSDeal(String dealname, String dealstage, String pipeline, BigDecimal amount, Instant closedate) {
        this.setDealName(dealname);
        this.setDealStage(dealstage);
        this.setPipeline(pipeline);
        this.setAmount(amount);
        this.setCloseDate(closedate);
    }

    public HSDeal(long id, String dealname, String dealstage, String pipeline, BigDecimal amount, Map<String, String> properties, Instant closedate) {
        super(properties);
        this.setId(id);
        this.setDealName(dealname);
        this.setDealStage(dealstage);
        this.setPipeline(pipeline);
        this.setAmount(amount);
        this.setCloseDate(closedate);
    }

    public long getId() {
        return getLongProperty(ID);
    }

    public HSDeal setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    public String getDealName() {
        return getProperty(DEAL_NAME);
    }

    public HSDeal setDealName(String dealname) {
        setProperty(DEAL_NAME, dealname);
        return this;
    }

    public String getDealStage() {
        return getProperty(DEAL_STAGE);
    }

    public HSDeal setDealStage(String dealstage) {
        setProperty(DEAL_STAGE, dealstage);
        return this;
    }

    public String getPipeline() {
        return getProperty(PIPELINE);
    }

    public HSDeal setPipeline(String pipeline) {
        setProperty(PIPELINE, pipeline);
        return this;
    }

    public BigDecimal getAmount() {
        return getBigDecimalProperty(AMOUNT);
    }

    public HSDeal setAmount(BigDecimal amount) {
        setProperty(AMOUNT, amount.toString());
        return this;
    }

    public Instant getCloseDate() { return getDateProperty(CLOSE_DATE); }

    public HSDeal setCloseDate(Instant closedate) {
        setProperty(CLOSE_DATE, closedate.toString());
        return this;
    }
}
