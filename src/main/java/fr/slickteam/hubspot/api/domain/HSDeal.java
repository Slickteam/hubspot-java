package fr.slickteam.hubspot.api.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * The type Hs deal.
 */
public class HSDeal extends HSObject {

    private static final String ID = "vid";
    private static final String DEAL_NAME = "dealname";
    private static final String DEAL_STAGE = "dealstage";
    private static final String PIPELINE = "pipeline";
    private static final String AMOUNT = "amount";
    private static final String CLOSE_DATE = "closedate";
    private static final String CREATED_DATE = "createdate";

    /**
     * Instantiates a new Hs deal.
     */
    public HSDeal() {
    }

    /**
     * Instantiates a new Hs deal.
     *
     * @param dealname  the dealname
     * @param dealstage the dealstage
     * @param pipeline  the pipeline
     * @param amount    the amount
     * @param closedate the closedate
     */
    public HSDeal(String dealname, String dealstage, String pipeline, BigDecimal amount, Instant closedate) {
        this.setDealName(dealname);
        this.setDealStage(dealstage);
        this.setPipeline(pipeline);
        this.setAmount(amount);
        this.setCloseDate(closedate);
    }


    /**
     * Instantiates a new Hs deal.
     *
     * @param dealname   the dealname
     * @param dealstage  the dealstage
     * @param pipeline   the pipeline
     * @param amount     the amount
     * @param properties the properties
     */
    public HSDeal(String dealname, String dealstage, String pipeline, BigDecimal amount, Map<String, String> properties) {
        super(properties);
        this.setDealName(dealname);
        this.setDealStage(dealstage);
        this.setPipeline(pipeline);
        this.setAmount(amount);
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
    public HSDeal setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    /**
     * Gets deal name.
     *
     * @return the deal name
     */
    public String getDealName() {
        return getProperty(DEAL_NAME);
    }

    /**
     * Sets deal name.
     *
     * @param dealname the dealname
     * @return the deal name
     */
    public HSDeal setDealName(String dealname) {
        setProperty(DEAL_NAME, dealname);
        return this;
    }

    /**
     * Gets deal stage.
     *
     * @return the deal stage
     */
    public String getDealStage() {
        return getProperty(DEAL_STAGE);
    }

    /**
     * Sets deal stage.
     *
     * @param dealstage the dealstage
     * @return the deal stage
     */
    public HSDeal setDealStage(String dealstage) {
        setProperty(DEAL_STAGE, dealstage);
        return this;
    }

    /**
     * Gets pipeline.
     *
     * @return the pipeline
     */
    public String getPipeline() {
        return getProperty(PIPELINE);
    }

    /**
     * Sets pipeline.
     *
     * @param pipeline the pipeline
     * @return the pipeline
     */
    public HSDeal setPipeline(String pipeline) {
        setProperty(PIPELINE, pipeline);
        return this;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return getBigDecimalProperty(AMOUNT);
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     * @return the amount
     */
    public HSDeal setAmount(BigDecimal amount) {
        setProperty(AMOUNT, amount.toString());
        return this;
    }

    /**
     * Gets close date.
     *
     * @return the close date
     */
    public Instant getCloseDate() { return getDateProperty(CLOSE_DATE); }

    /**
     * Sets close date.
     *
     * @param closedate the closedate
     * @return the close date
     */
    public HSDeal setCloseDate(Instant closedate) {
        setProperty(CLOSE_DATE, closedate.toString());
        return this;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public Instant getCreatedDate() { return getDateProperty(CREATED_DATE); }

    /**
     * Sets created date.
     *
     * @param createdDate the created date
     * @return the created date
     */
    public HSDeal setCreatedDate(Instant createdDate) {
        setProperty(CREATED_DATE, createdDate.toString());
        return this;
    }
}
