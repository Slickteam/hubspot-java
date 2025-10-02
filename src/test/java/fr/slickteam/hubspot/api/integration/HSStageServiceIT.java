package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSPipeline;
import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HSStageServiceIT {

    private HubSpot hubSpot;

    @BeforeEach
    void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }

    @Disabled("Required scopes missing")
    @org.junit.jupiter.api.Test
    void createStage_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        HSStage stage = getNewTestStage(pipeline.getId());

        assertThat(stage.getId()).isNotNull();
        assertThat(stage.getId()).isNotEqualTo("");
        hubSpot.pipeline().delete(pipeline.getId());
    }

    @Disabled("Required scopes missing")
    @org.junit.jupiter.api.Test
    void deleteStage_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        HSStage stage = getNewTestStage(pipeline.getId());

        hubSpot.stage().delete(pipeline.getId(), stage);

        assertThat(hubSpot.stage().getStageById(pipeline.getId(), stage.getId())).isNull();
        hubSpot.pipeline().delete(pipeline.getId());
    }

    @org.junit.jupiter.api.Test
    void getStageById_Test() throws Exception {
        List<HSPipeline> pipelines = hubSpot.pipeline().getPipelines();
        HSPipeline pipelineTest = findPipelineWithStage(pipelines);
        HSStage stageTest = pipelineTest.getStages().get(0);
        HSStage findStage = hubSpot.stage().getStageById(pipelineTest.getId(), stageTest.getId());
        assertThat(findStage).isNotNull();
        assertThat(findStage.getId()).isEqualTo(stageTest.getId());
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

    private HSStage getNewTestStage(String pipelineId) throws HubSpotException {
        return hubSpot.stage().create(pipelineId, new HSStage());
    }

    private HSPipeline getNewTestPipeline() throws HubSpotException {
        return hubSpot.pipeline().create(new HSPipeline());
    }
}
