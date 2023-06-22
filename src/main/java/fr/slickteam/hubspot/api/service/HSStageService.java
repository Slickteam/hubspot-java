package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONObject;

public class HSStageService {
    private final HttpService httpService;
    private final HSService hsService;
    private static final String PIPELINE_URL = "/crm/v3/pipelines/";
    private static final String DEALS = "deals/";
    private static final String STAGES = "stages/";

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSStageService(HttpService httpService) {
        this.httpService = httpService;
        this.hsService = new HSService(httpService);
    }

    /**
     * Create a new stage
     *
     * @param hsStage - stage to create
     * @return created stage
     * @throws HubSpotException - if HTTP call fails
     */
    public HSStage create(long pipelineId, HSStage hsStage) throws HubSpotException {
        JSONObject jsonObject = (JSONObject) httpService.postRequest(PIPELINE_URL + DEALS + pipelineId + "/" + STAGES, hsStage.toJsonString());
        hsStage.setId(jsonObject.getLong("id"));
        return hsStage;
    }

    /**
     * Delete a stage.
     *
     * @param stage - stage to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(long pipelineId, HSStage stage) throws HubSpotException {
        delete(pipelineId, stage.getId());
    }

    /**
     * Delete a stage.
     *
     * @param pipelineId - ID of the stage's pipeline
     * @param stageId - ID of the stage to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(long pipelineId, long stageId) throws HubSpotException {
        if (pipelineId == 0) {
            throw new HubSpotException("Pipeline ID must be provided");
        }
        if (stageId == 0) {
            throw new HubSpotException("Stage ID must be provided");
        }
        String url = PIPELINE_URL + DEALS + pipelineId + "/" + STAGES + stageId;

        httpService.deleteRequest(url);
    }

    /**
     * Get HubSpot pipeline stage by its ID.
     *
     * @param pipelineId - ID of pipeline
     * @param stageId    - ID of stage searched
     * @return A pipeline stage
     * @throws HubSpotException - if HTTP call fails
     */
    public HSStage getStageById(long pipelineId, long stageId) throws HubSpotException {
        String url = PIPELINE_URL + DEALS + pipelineId + "/" + STAGES + stageId;
        return getStage(url);
    }

    private HSStage getStage(String url) throws HubSpotException {
        try {
            return parseStageData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Parse stage data from HubSpot API response
     *
     * @param jsonBody - body from HubSpot API response
     * @return a stage
     */
    public HSStage parseStageData(JSONObject jsonBody) {
        HSStage stage = new HSStage();
        hsService.parseJSONData(jsonBody, stage);
        return stage;
    }

}
