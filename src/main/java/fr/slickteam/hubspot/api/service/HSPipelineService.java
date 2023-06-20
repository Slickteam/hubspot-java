package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.HSPipeline;
import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HSPipelineService {
    private final HttpService httpService;
    private static final String PIPELINE_URL = "/crm/v3/objects/pipelines/";
    private static final String DEALS = "deals/";
    private static final String STAGES = "stages/";

    /**
     * Constructor with HTTPService injected
     *
     * @param httpService - HTTP service for HubSpot API
     */
    public HSPipelineService(HttpService httpService) {
        this.httpService = httpService;
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
        JSONObject jsonArray = (JSONObject) httpService.getRequest(url);
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
        JSONArray jsonArray = (JSONArray) httpService.getRequest(url);
        return pipeline;
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
        String url = PIPELINE_URL + DEALS + pipelineId + STAGES + stageId;
        JSONArray jsonArray = (JSONArray) httpService.getRequest(url);
        return stage;
    }

}
