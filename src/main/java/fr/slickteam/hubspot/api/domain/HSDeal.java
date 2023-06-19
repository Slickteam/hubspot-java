package fr.slickteam.hubspot.api.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class HSDeal extends HSObject {

    private static final String ID = "vid";
    private static final String DEAL_NAME = "dealname";
    private static final String DEAL_STAGE = "dealstage";
    private static final String PIPELINE = "pipeline";
    private static final String AMOUNT = "amount";
    private static final String CLOSE_DATE = "closedate";
    private static final String CONTRACT_START_DATE = "date_debut_contrat";
    private static final String CONTRACT_END_DATE = "date_fin_contrat";

    public HSDeal() {
    }

    public HSDeal(String dealname, String dealstage, String pipeline, BigDecimal amount, Instant closedate) {
        this.setDealName(dealname);
        this.setDealStage(dealstage);
        this.setPipeline(pipeline);
        this.setAmount(amount);
        this.setCloseDate(closedate);
    }

    public HSDeal(String dealname, String dealstage, String pipeline, BigDecimal amount, Instant closedate, Instant contractStartDate, Instant contractEndDate) {
        this.setDealName(dealname);
        this.setDealStage(dealstage);
        this.setPipeline(pipeline);
        this.setAmount(amount);
        this.setCloseDate(closedate);
        this.setContractStartDate(contractStartDate);
        this.setContractEndDate(contractEndDate);
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

    public Instant getContractStartDate() { return getDateProperty(CONTRACT_START_DATE); }

    public HSDeal setContractStartDate(Instant contractStartDate) {
        setProperty(CONTRACT_START_DATE, contractStartDate.toString());
        return this;
    }

    public Instant getContractEndDate() { return getDateProperty(CONTRACT_END_DATE); }

    public HSDeal setContractEndDate(Instant contractEndDate) {
        setProperty(CONTRACT_END_DATE, contractEndDate.toString());
        return this;
    }
}
