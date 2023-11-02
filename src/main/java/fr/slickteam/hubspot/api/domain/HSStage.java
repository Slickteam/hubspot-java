package fr.slickteam.hubspot.api.domain;

import java.time.Instant;
import java.util.Map;

/**
 * The type Hs stage.
 */
public class HSStage extends HSObject{

    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String DISPLAY_ORDER = "displayOrder";
    private static final String METADATA = "metadata";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String ARCHIVED = "archived";

    /**
     * Instantiates a new Hs stage.
     */
    public HSStage() {
    }

    /**
     * Instantiates a new Hs stage.
     *
     * @param label        the label
     * @param displayOrder the display order
     * @param metadata     the metadata
     * @param createdAt    the created at
     * @param updatedAt    the updated at
     * @param archived     the archived
     */
    public HSStage(String label, int displayOrder, String metadata, Instant createdAt, Instant updatedAt, Boolean archived) {
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setMetadata(metadata);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    /**
     * Instantiates a new Hs stage.
     *
     * @param label        the label
     * @param displayOrder the display order
     * @param metadata     the metadata
     * @param createdAt    the created at
     * @param updatedAt    the updated at
     * @param archived     the archived
     * @param properties   the properties
     */
    public HSStage(String label, int displayOrder,String metadata, Instant createdAt, Instant updatedAt, Boolean archived, Map<String, String> properties) {
        super(properties);
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setMetadata(metadata);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
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
    public HSStage setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return getProperty(LABEL);
    }

    /**
     * Sets label.
     *
     * @param label the label
     * @return the label
     */
    public HSStage setLabel(String label) {
        setProperty(LABEL, label);
        return this;
    }

    /**
     * Gets display order.
     *
     * @return the display order
     */
    public int getDisplayOrder() {
        return (getIntProperty(DISPLAY_ORDER));
    }

    /**
     * Sets display order.
     *
     * @param displayOrder the display order
     * @return the display order
     */
    public HSStage setDisplayOrder(int displayOrder) {
        setProperty(DISPLAY_ORDER, Long.toString(displayOrder));
        return this;
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public String getMetadata() {
        return getProperty(METADATA);
    }

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     * @return the metadata
     */
    public HSStage setMetadata(String metadata) {
        setProperty(METADATA, metadata);
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
    public HSStage setCreatedAt(Instant createdAt) {
        setProperty(CREATED_AT, createdAt.toString());
        return this;
    }

    /**
     * Gets update at.
     *
     * @return the update at
     */
    public Instant getUpdateAt() {
        return getDateProperty(UPDATED_AT);
    }

    /**
     * Sets update at.
     *
     * @param updatedAt the updated at
     * @return the update at
     */
    public HSStage setUpdateAt(Instant updatedAt) {
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
    public HSStage setArchived(boolean archived) {
        setProperty(ARCHIVED, Boolean.toString(archived));
        return this;
    }


}
