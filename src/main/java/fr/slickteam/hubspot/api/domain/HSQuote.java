package fr.slickteam.hubspot.api.domain;

import java.time.Instant;
import java.util.Map;

/**
 * The type Hs quote.
 */
public class HSQuote extends HSObject {

    private static final String ID = "vid";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String ARCHIVED = "archived";

    /**
     * Instantiates a new Hs quote.
     */
    public HSQuote() {
    }

    /**
     * Instantiates a new Hs quote.
     *
     * @param properties      the hs quote title
     */

    public HSQuote(Map<String, String> properties) {
        super(properties);
    }

    /**
     * Instantiates a new Hs quote.
     *
     * @param properties      the hs quote title
     * @param createdAt       the hs quote expiration date
     * @param updatedAt       the hs quote created date
     * @param archived        the hs quote last modified date
     */

    public HSQuote(Map<String, String> properties, Instant createdAt, Instant updatedAt, boolean archived) {
        super(properties);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
        this.setArchived(archived);
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
     * Gets created at.
     *
     * @return the created at
     */
    public Instant getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    /**
     * Sets created at.
     *
     * @param createdAt the created at
     * @return the created at
     */
    public HSQuote setCreatedAt(Instant createdAt) {
        setProperty(CREATED_AT, createdAt.toString());
        return this;
    }

    /**
     * Gets updated at.
     *
     * @return the updated at
     */
    public Instant getUpdatedAt() {
        return getDateProperty(UPDATED_AT);
    }

    /**
     * Sets updated at.
     *
     * @param updatedAt the updated at
     * @return the updated at
     */
    public HSQuote setUpdatedAt(Instant updatedAt) {
        setProperty(UPDATED_AT, updatedAt.toString());
        return this;
    }

    /**
     * Gets archived.
     *
     * @return the archived
     */
    public boolean getArchived() {
        return getBooleanProperty(ARCHIVED);
    }

    /**
     * Sets archived.
     *
     * @param archived the archived
     * @return the archived
     */
    public HSQuote setArchived(boolean archived) {
        setProperty(ARCHIVED, Boolean.toString(archived));
        return this;
    }



}
