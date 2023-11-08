package fr.slickteam.hubspot.api.service;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.Logger.Level.DEBUG;

/**
 * The type Hs pipeline service.
 */
public class HSPipelineService {

    private static final System.Logger log = System.getLogger(HSPipelineService.class.getName());
    private final HttpService httpService;
    private final HSService hsService;
    private static final String PIPELINE_URL = "/crm/v3/pipelines/";
    private static final String DEALS = "deals/";

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
     * Create a new pipeline
     *
     * @param hsPipeline - pipeline to create
     * @return created pipeline
     * @throws HubSpotException - if HTTP call fails
     */
    public HSPipeline create(HSPipeline hsPipeline) throws HubSpotException {
        log.log(DEBUG, "create - hsPipeline : " + hsPipeline);
        JSONObject jsonObject = (JSONObject) httpService.postRequest(PIPELINE_URL + DEALS, hsPipeline.toJsonString());
        hsPipeline.setId(jsonObject.getLong("id"));
        return hsPipeline;
    }

    /**
     * Delete a pipeline.
     *
     * @param pipeline - pipeline to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(HSPipeline pipeline) throws HubSpotException {
        log.log(DEBUG, "delete - pipeline : " + pipeline);
        delete(pipeline.getId());
    }

    /**
     * Delete a pipeline.
     *
     * @param id - ID of the pipeline to delete
     * @throws HubSpotException - if HTTP call fails
     */
    public void delete(Long id) throws HubSpotException {
        log.log(DEBUG, "delete - id : " + id);
        if (id == 0) {
            throw new HubSpotException("Pipeline ID must be provided");
        }
        String url = PIPELINE_URL + DEALS + id;

        httpService.deleteRequest(url);
    }

    /**
     * Get HubSpot pipelines.
     *
     * @return All pipelines
     * @throws HubSpotException - if HTTP call fails
     */
    public List<HSPipeline> getPipelines() throws HubSpotException {
        log.log(DEBUG, "getPipelines");
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
        log.log(DEBUG, "getPipelineById - pipelineId : " + pipelineId);
        String url = PIPELINE_URL + DEALS + pipelineId;
        return getPipeline(url);
    }

    /**
     * Parse pipeline data from HubSpot API response
     *
     * @param jsonObject - body from HubSpot API response
     * @return The pipeline
     */
    public HSPipeline parsePipelineData(JSONObject jsonObject) {
        HSPipeline pipeline = new HSPipeline();
        hsService.parseJSONData(jsonObject, pipeline);
        return pipeline;
    }


    private HSPipeline getPipeline(String url) throws HubSpotException {
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

}
