package fr.slickteam.hubspot.api.domain;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Hs pipeline.
 */
public class HSPipeline extends HSObject{

    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String DISPLAY_ORDER = "displayOrder";
    private static final String STAGES = "stages";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String ARCHIVED = "archived";

    /**
     * Instantiates a new Hs pipeline.
     */
    public HSPipeline() {
    }

    /**
     * Instantiates a new Hs pipeline.
     *
     * @param label        the label
     * @param displayOrder the display order
     * @param stages       the stages
     * @param createdAt    the created at
     * @param updatedAt    the updated at
     * @param archived     the archived
     */
    public HSPipeline(String label, int displayOrder, List<HSStage> stages, Instant createdAt, Instant updatedAt, boolean archived) {
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setStages(stages);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    /**
     * Instantiates a new Hs pipeline.
     *
     * @param label        the label
     * @param displayOrder the display order
     * @param stages       the stages
     * @param createdAt    the created at
     * @param updatedAt    the updated at
     * @param archived     the archived
     * @param properties   the properties
     */
    public HSPipeline(String label, int displayOrder, List<HSStage> stages, Instant createdAt, Instant updatedAt, boolean archived, Map<String, String> properties) {
        super(properties);
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setStages(stages);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return getProperty(ID);
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public HSPipeline setId(String id) {
        setProperty(ID, id);
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
    public HSPipeline setLabel(String label) {
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
    public HSPipeline setDisplayOrder(int displayOrder) {
        setProperty(DISPLAY_ORDER, Long.toString(displayOrder));
        return this;
    }

    /**
     * Gets stages.
     *
     * @return the stages
     */
    public List<HSStage> getStages() {
        JSONArray jsonArray = new JSONArray(getProperty(STAGES));
        return getStagesProperties(jsonArray);
    }

    /**
     * Sets stages.
     *
     * @param stages the stages
     * @return the stages
     */
    public HSPipeline setStages(List<HSStage> stages) {
        JSONArray jsonArray = new JSONArray();
        stages.forEach(stage -> jsonArray.put(stageToJson(stage)));
        setProperty(STAGES, jsonArray.toString());
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
    public HSPipeline setCreatedAt(Instant createdAt) {
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
    public HSPipeline setUpdateAt(Instant updatedAt) {
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
    public HSPipeline setArchived(boolean archived) {
        setProperty(ARCHIVED, Boolean.toString(archived));
        return this;
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

    private JSONObject stageToJson(HSStage stage) {
        JSONObject jsonStage = new JSONObject();
        jsonStage.put(ID, stage.getId()+"");
        jsonStage.put(LABEL, stage.getLabel());
        jsonStage.put(DISPLAY_ORDER, stage.getDisplayOrder());
        jsonStage.put("metadata", stage.getMetadata());
        jsonStage.put(CREATED_AT, stage.getCreatedAt()+"");
        jsonStage.put(UPDATED_AT, stage.getUpdateAt()+"");
        jsonStage.put(ARCHIVED, stage.getArchived());
        return jsonStage;
    }
}
