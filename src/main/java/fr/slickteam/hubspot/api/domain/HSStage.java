package fr.slickteam.hubspot.api.domain;

import java.time.Instant;
import java.util.Map;

public class HSStage extends HSObject{

    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String DISPLAY_ORDER = "displayOrder";
    private static final String METADATA = "metadata";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String ARCHIVED = "archived";

    public HSStage() {
    }

    public HSStage(String label, int displayOrder, String metadata, Instant createdAt, Instant updatedAt, Boolean archived) {
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setMetadata(metadata);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    public HSStage(String label, int displayOrder,String metadata, Instant createdAt, Instant updatedAt, Boolean archived, Map<String, String> properties) {
        super(properties);
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setMetadata(metadata);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    public long getId() {
        return getLongProperty(ID);
    }

    public HSStage setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    public String getLabel() {
        return getProperty(LABEL);
    }

    public HSStage setLabel(String label) {
        setProperty(LABEL, label);
        return this;
    }

    public int getDisplayOrder() {
        return (getIntProperty(DISPLAY_ORDER));
    }

    public HSStage setDisplayOrder(int displayOrder) {
        setProperty(DISPLAY_ORDER, Long.toString(displayOrder));
        return this;
    }

    public String getMetadata() {
        return getProperty(METADATA);
    }

    public HSStage setMetadata(String metadata) {
        setProperty(METADATA, metadata);
        return this;
    }

    public Instant getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    public HSStage setCreatedAt(Instant createdAt) {
        setProperty(CREATED_AT, createdAt.toString());
        return this;
    }

    public Instant getUpdateAt() {
        return getDateProperty(UPDATED_AT);
    }

    public HSStage setUpdateAt(Instant updatedAt) {
        setProperty(UPDATED_AT, updatedAt.toString());
        return this;
    }

    public boolean getArchived() {
        return getBooleanProperty(ARCHIVED);
    }

    public HSStage setArchived(boolean archived) {
        setProperty(ARCHIVED, Boolean.toString(archived));
        return this;
    }


}
