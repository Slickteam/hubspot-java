package fr.slickteam.hubspot.api.domain;

import java.time.Instant;
import java.util.Map;

/**
 * The type Hs quote.
 */
public class HSQuote extends HSObject {

    private static final String ID = "vid";
    private static final String HS_OBJECT_ID = "hs_object_id";
    private static final String HS_CREATEDATE = "hs_createdate";
    private static final String HS_LASTMODIFIEDDATE = "hs_lastmodifieddate";
    private static final String HS_PDF_DOWNLOAD_LINK = "hs_pdf_download_link";

    /**
     * Instantiates a new Hs quote.
     */
    public HSQuote() {
    }

    /**
     * Instantiates a new Hs quote.
     *
     * @param hsObjectId     the hs object id
     * @param hsCreateDate      the hs create date
     * @param hsLastmodifiedDate     the hs last modified date
     * @param hsPdfDownloadLink     the hs pdf download link
     */

    public HSQuote(String hsObjectId, Instant hsCreateDate, Instant hsLastmodifiedDate, String hsPdfDownloadLink) {
        this.setHsObjectId(hsObjectId);
        this.setHsCreateDate(hsCreateDate);
        this.setHsLastmodifiedDate(hsLastmodifiedDate);
        this.setHsPdfDownloadLink(hsPdfDownloadLink);
    }

    /**
     * Instantiates a new Hs quote.
     *
     * @param id          the id
     * @param hsObjectId     the hs object id
     * @param hsCreateDate      the hs create date
     * @param hsLastmodifiedDate     the hs last modified date
     * @param hsPdfDownloadLink     the hs pdf download link
     * @param properties  the properties
     */

    public HSQuote(long id, String hsObjectId, Instant hsCreateDate, Instant hsLastmodifiedDate, String hsPdfDownloadLink,
                   Map<String, String> properties) {
        super(properties);
        this.setHsObjectId(hsObjectId);
        this.setHsCreateDate(hsCreateDate);
        this.setHsLastmodifiedDate(hsLastmodifiedDate);
        this.setHsPdfDownloadLink(hsPdfDownloadLink);
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
    public HSQuote setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    /**
     * Gets hs object id.
     *
     * @return the hs object id
     */
    public String getHsObjectId() {
        return getProperty(HS_OBJECT_ID);
    }

    /**
     * Sets hs object id.
     *
     * @param hsObjectId the hs object id
     * @return the hs object id
     */
    public HSQuote setHsObjectId(String hsObjectId) {
        setProperty(HS_OBJECT_ID, hsObjectId);
        return this;
    }

    /**
     * Gets hs create date.
     *
     * @return the hs create date
     */
    public Instant getHsCreateDate() {
        return Instant.parse(getProperty(HS_CREATEDATE));
    }

    /**
     * Sets hs create date.
     *
     * @param hsCreateDate the hs create date
     * @return the hs create date
     */
    public HSQuote setHsCreateDate(Instant hsCreateDate) {
        setProperty(HS_CREATEDATE, hsCreateDate.toString());
        return this;
    }

    /**
     * Gets hs lastmodified date.
     *
     * @return the hs lastmodified date
     */
    public Instant getHsLastmodifiedDate() {
        return Instant.parse(getProperty(HS_LASTMODIFIEDDATE));
    }

    /**
     * Sets hs lastmodified date.
     *
     * @param hsLastmodifiedDate the hs lastmodified date
     * @return the hs lastmodified date
     */
    public HSQuote setHsLastmodifiedDate(Instant hsLastmodifiedDate) {
        setProperty(HS_LASTMODIFIEDDATE, hsLastmodifiedDate.toString());
        return this;
    }

    /**
     * Gets hs pdf download link.
     *
     * @return the hs pdf download link
     */
    public String getHsPdfDownloadLink() {
        return getProperty(HS_PDF_DOWNLOAD_LINK);
    }

    /**
     * Sets hs pdf download link.
     *
     * @param hsPdfDownloadLink the hs pdf download link
     * @return the hs pdf download link
     */
    public HSQuote setHsPdfDownloadLink(String hsPdfDownloadLink) {
        setProperty(HS_PDF_DOWNLOAD_LINK, hsPdfDownloadLink);
        return this;
    }

}
