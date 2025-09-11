package fr.slickteam.hubspot.api.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import fr.slickteam.hubspot.api.utils.JsonUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Hs pipeline.
 */
public class HSPipeline extends HSObject {

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
        setProperty(LABEL, label);
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
    public List<HSStage> getStages() throws HubSpotException {
        JsonNode jsonNode = JsonUtils.parseJson(getProperty(STAGES));
        return getStagesProperties((ArrayNode) jsonNode);
    }

    /**
     * Sets stages.
     *
     * @param stages the stages
     * @return the stages
     */
    public HSPipeline setStages(List<HSStage> stages) {
        ArrayNode arrayNode = JsonUtils.createArrayNode();
        stages.forEach(stage -> arrayNode.add(stageToJson(stage)));
        setProperty(STAGES, arrayNode.toString());
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

    private List<HSStage> getStagesProperties(ArrayNode arrayNode) {
        List<HSStage> stages = new ArrayList<>();
        for (int i = 0; i < arrayNode.size(); i++) {
            HSStage stage = new HSStage();
            JsonNode jsonStage = arrayNode.get(i);
            stage.setId(jsonStage.path(ID).asText());
            stage.setLabel(jsonStage.path(LABEL).asText());
            stage.setDisplayOrder(jsonStage.path(DISPLAY_ORDER).asInt());
            stage.setMetadata(jsonStage.path("metadata").toString());
            stage.setCreatedAt(Instant.parse(jsonStage.path(CREATED_AT).asText()));
            stage.setUpdateAt(Instant.parse(jsonStage.path(UPDATED_AT).asText()));
            stage.setArchived(jsonStage.path(ARCHIVED).asBoolean());
            stages.add(stage);
        }
        return stages;
    }

    private ObjectNode stageToJson(HSStage stage) {
        ObjectNode jsonStage = JsonUtils.createObjectNode();
        jsonStage.put(ID, stage.getId());
        jsonStage.put(LABEL, stage.getLabel());
        jsonStage.put(DISPLAY_ORDER, stage.getDisplayOrder());
        jsonStage.put("metadata", stage.getMetadata());
        jsonStage.put(CREATED_AT, stage.getCreatedAt().toString());
        jsonStage.put(UPDATED_AT, stage.getUpdateAt().toString());
        jsonStage.put(ARCHIVED, stage.getArchived());
        return jsonStage;
    }

}