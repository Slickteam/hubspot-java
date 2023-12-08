package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSPipeline;
import fr.slickteam.hubspot.api.domain.HSStage;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class HSStageServiceIT {

    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }

    @Ignore("Required scopes missing")
    @Test
    public void createStage_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        HSStage stage = getNewTestStage(pipeline.getId());

        assertNotEquals(0, stage.getId());
        hubSpot.pipeline().delete(pipeline.getId());
    }

    @Ignore("Required scopes missing")
    @Test
    public void deleteStage_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        HSStage stage = getNewTestStage(pipeline.getId());

        hubSpot.stage().delete(pipeline.getId(), stage);

        assertNull(hubSpot.stage().getStageById(pipeline.getId(), stage.getId()));
        hubSpot.pipeline().delete(pipeline.getId());
    }

    @Test
    public void getStageById_Test() throws HubSpotException {
        List<HSPipeline> pipelines = hubSpot.pipeline().getPipelines();
        HSPipeline pipelineTest = findPipelineWithStage(pipelines);
        HSStage stageTest = pipelineTest.getStages().get(0);
        HSStage findStage = hubSpot.stage().getStageById(pipelineTest.getId(), stageTest.getId());
        Assert.assertNotNull(findStage);
        Assert.assertEquals(stageTest.getId(), findStage.getId());
    }

    public HSPipeline findPipelineWithStage(List<HSPipeline> pipelineList) {
        Optional<HSPipeline> firstPipeline = pipelineList.stream()
                .filter(pipeline -> !pipeline.getStages().isEmpty())
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
