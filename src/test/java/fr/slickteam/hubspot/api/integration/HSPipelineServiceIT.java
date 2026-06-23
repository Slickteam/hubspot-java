package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

class HSPipelineServiceIT {

    private String createdPipelineId;
    private HubSpot hubSpot;

    @BeforeEach
    void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (createdPipelineId != null) {
            hubSpot.pipeline().delete(createdPipelineId);
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }

    @Disabled("Required scopes missing")
    @Test
    void createPipeline_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        pipeline = hubSpot.pipeline().create(pipeline);
        createdPipelineId = pipeline.getId();

        assertThat(pipeline.getId()).isNotNull();
        assertThat(pipeline.getId()).isNotEqualTo("");
    }

    @Disabled("Required scopes missing")
    @Test
    void deletePipeline_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        createdPipelineId = pipeline.getId();

        hubSpot.pipeline().delete(pipeline);

        assertThat(hubSpot.pipeline().getPipelineById(pipeline.getId())).isNull();

    }

    @Test
    void getPipelines_Test() throws Exception {
        List<HSPipeline> pipelines = hubSpot.pipeline().getPipelines();

        assertThat(pipelines).isNotNull();
        pipelines.forEach(pipeline -> assertThat(pipeline).isNotNull());
    }

    @Test
    void getPipelineById_Test() throws Exception {
        List<HSPipeline> pipelines = hubSpot.pipeline().getPipelines();
        HSPipeline pipelineTest = findPipelineWithStage(pipelines);
        HSPipeline findPipeline = hubSpot.pipeline().getPipelineById(pipelineTest.getId());
        assertThat(findPipeline).isNotNull();
        assertThat(findPipeline.getId()).isEqualTo(pipelineTest.getId());
    }

    private HSPipeline getNewTestPipeline() throws HubSpotException {
        return hubSpot.pipeline().create(new HSPipeline());
    }

    public HSPipeline findPipelineWithStage(List<HSPipeline> pipelineList) {
        Optional<HSPipeline> firstPipeline = pipelineList.stream()
                .filter(pipeline -> {
                    try {
                        return !pipeline.getStages().isEmpty();
                    } catch (HubSpotException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst();

        return firstPipeline.orElse(null);
    }

}
