package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSCompany;
import fr.slickteam.hubspot.api.domain.HSObject;
import fr.slickteam.hubspot.api.domain.HSPipeline;
import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HSPipelineService {
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
    public HSPipelineService(HttpService httpService) {
        this.httpService = httpService;
        this.hsService = new HSService(httpService);
    }

    /**
     * Get HubSpot pipelines.
     *
     * @return All pipelines
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSPipeline> getPipelines() throws HubSpotException {
        List<HSPipeline> pipelines = new ArrayList<>();
        String url = PIPELINE_URL + DEALS;
        List<JSONObject> response = hsService.parseJsonResultToList(url);
        for (JSONObject jsonObject: response) {
            pipelines.add(parsePipelineData(jsonObject));
        }
        return pipelines;
    }

    /**
     * Get HubSpot pipeline by its ID.
     *
     * @param pipelineId - ID of pipeline
     * @return A pipeline
     * @throws HubSpotException - if HTTP call fails
     */
    public HSPipeline getPipelineById(long pipelineId) throws HubSpotException {
        HSPipeline pipeline = new HSPipeline();
        String url = PIPELINE_URL + DEALS + pipelineId;
        try {
            return parsePipelineData((JSONObject) httpService.getRequest(url));
        } catch (HubSpotException e) {
            if (e.getMessage().equals("Not Found")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    /**
     * Get HubSpot pipeline stage by its ID.
     *
     * @param pipelineId - ID of pipeline
     * @param stageId - ID of stage searched
     * @return A pipeline stage
     * @throws HubSpotException - if HTTP call fails
     */
    public HSStage getStageById(long pipelineId, long stageId) throws HubSpotException {
        HSStage stage = new HSStage();
        String url = PIPELINE_URL + DEALS + pipelineId + "/" + STAGES + stageId;
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
        stage.setId(jsonBody.getLong("id"));
        hsService.parseJSONData(jsonBody, stage);
        return stage;
    }

    /**
     * Parse pipeline data from HubSpot API response
     *
     * @param jsonObject - body from HubSpot API response
     * @return The pipeline
     */
    public HSPipeline parsePipelineData(JSONObject jsonObject) {
        HSPipeline pipeline = new HSPipeline();
//        pipeline.setId(jsonObject.getLong("id"));
//        pipeline.setLabel(jsonObject.getString("label"));
//        pipeline.setDisplayOrder(jsonObject.getInt("displayOrder"));
//        pipeline.setStages(jsonObject.getString("stages"));
//        pipeline.setCreatedAt(Instant.parse(jsonObject.getString("createdAt")));
        pipeline.setId(jsonObject.getLong("id"));
        hsService.parseJSONData(jsonObject, pipeline);
        return pipeline;
    }

}
