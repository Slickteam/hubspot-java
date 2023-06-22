package fr.slickteam.hubspot.api.domain;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HSPipeline extends HSObject{

    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String DISPLAY_ORDER = "displayOrder";
    private static final String STAGES = "stages";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String ARCHIVED = "archived";

    public HSPipeline() {
    }

    public HSPipeline(String label, int displayOrder, List<HSStage> stages, Instant createdAt, Instant updatedAt, boolean archived) {
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setStages(stages);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    public HSPipeline(String label, int displayOrder, List<HSStage> stages, Instant createdAt, Instant updatedAt, boolean archived, Map<String, String> properties) {
        super(properties);
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setStages(stages);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    public long getId() {
        return getLongProperty(ID);
    }

    public HSPipeline setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    public String getLabel() {
        return getProperty(LABEL);
    }

    public HSPipeline setLabel(String label) {
        setProperty(LABEL, label);
        return this;
    }

    public int getDisplayOrder() {
        return (getIntProperty(DISPLAY_ORDER));
    }

    public HSPipeline setDisplayOrder(int displayOrder) {
        setProperty(DISPLAY_ORDER, Long.toString(displayOrder));
        return this;
    }

    public List<HSStage> getStages() {
        JSONArray jsonArray = new JSONArray(getProperty(STAGES));
        return getStagesProperties(jsonArray);
    }

    private List<HSStage> getStagesProperties(JSONArray jsonArray) {
        List<HSStage> stages = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            HSStage stage = new HSStage();
            JSONObject jsonStage = jsonArray.getJSONObject(i);
            stage.setId(Long.parseLong((String) jsonStage.get(ID)));
            stage.setLabel((String) jsonStage.get(LABEL));
            stage.setDisplayOrder(((int) jsonStage.get(DISPLAY_ORDER)));
            stage.setMetadata(String.valueOf(jsonStage.get("metadata")));
            stage.setCreatedAt(Instant.parse((String) jsonStage.get(CREATED_AT)));
            stage.setUpdateAt(Instant.parse((String) jsonStage.get(UPDATED_AT)));
            stage.setArchived((Boolean) jsonStage.get(ARCHIVED));
            stages.add(stage);
        }
        return stages;
    }

    public HSPipeline setStages(List<HSStage> stages) {
        setProperty(STAGES, String.valueOf(stages));
        return this;
    }

    public Instant getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    public HSPipeline setCreatedAt(Instant createdAt) {
        setProperty(CREATED_AT, createdAt.toString());
        return this;
    }

    public Instant getUpdateAt() {
        return getDateProperty(UPDATED_AT);
    }

    public HSPipeline setUpdateAt(Instant updatedAt) {
        setProperty(UPDATED_AT, updatedAt.toString());
        return this;
    }

    public boolean getArchived() {
        return getBooleanProperty(ARCHIVED);
    }

    public HSPipeline setArchived(boolean archived) {
        setProperty(ARCHIVED, Boolean.toString(archived));
        return this;
    }
}
