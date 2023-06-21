package fr.slickteam.hubspot.api.domain;

import java.time.Instant;
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

    public HSPipeline(String label, int displayOrder, String stages, Instant createdAt, Instant updatedAt, boolean archived) {
        this.setLabel(label);
        this.setDisplayOrder(displayOrder);
        this.setStages(stages);
        this.setCreatedAt(createdAt);
        this.setUpdateAt(updatedAt);
        this.setArchived(archived);
    }

    public HSPipeline(String label, int displayOrder, String stages, Instant createdAt, Instant updatedAt, boolean archived, Map<String, String> properties) {
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

    public String getStages() {
        // TODO:
//        List<HSStage> stages = new ArrayList<>();
//        JSONArray jsonArray = new JSONArray(getProperty(STAGES));
//
//        ArrayList<String> arrayList = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            HSStage stage = new HSStage();
//            JSONObject jsonProperties = this.getJSONObject(STAGES);
//            String stageString = jsonArray.getString(i);
//            stage.set
//            arrayList.add(jsonArray.getString(i));
//        }
        return getProperty(STAGES);
    }

    public HSPipeline setStages(String stages) {
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
