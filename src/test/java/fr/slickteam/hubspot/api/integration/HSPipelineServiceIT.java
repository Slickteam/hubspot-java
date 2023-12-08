package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class HSPipelineServiceIT {

    private String createdPipelineId;
    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }

    @After
    public void tearDown() throws Exception {
        if (createdPipelineId != null) {
            hubSpot.pipeline().delete(createdPipelineId);
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }

    @Ignore("Required scopes missing")
    @Test
    public void createPipeline_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        pipeline = hubSpot.pipeline().create(pipeline);
        createdPipelineId = pipeline.getId();

        assertNotEquals(0, pipeline.getId());
    }

    @Ignore("Required scopes missing")
    @Test
    public void deletePipeline_Test() throws Exception {
        HSPipeline pipeline = getNewTestPipeline();
        createdPipelineId = pipeline.getId();

        hubSpot.pipeline().delete(pipeline);

        assertNull(hubSpot.pipeline().getPipelineById(pipeline.getId()));

    }

    @Test
    public void getPipelines_Test() throws HubSpotException {
        List<HSPipeline> pipelines = hubSpot.pipeline().getPipelines();

        Assert.assertNotNull(pipelines);
        pipelines.forEach(Assert::assertNotNull);
    }
    @Test
    public void getPipelineById_Test() throws HubSpotException {
        List<HSPipeline> pipelines = hubSpot.pipeline().getPipelines();
        HSPipeline pipelineTest = findPipelineWithStage(pipelines);
        HSPipeline findPipeline = hubSpot.pipeline().getPipelineById(pipelineTest.getId());
        Assert.assertNotNull(findPipeline);
        Assert.assertEquals(pipelineTest.getId(), findPipeline.getId());
    }

    private HSPipeline getNewTestPipeline() throws HubSpotException {
        return hubSpot.pipeline().create(new HSPipeline());
    }

    public HSPipeline findPipelineWithStage(List<HSPipeline> pipelineList) {
        Optional<HSPipeline> firstPipeline = pipelineList.stream()
                .filter(pipeline -> !pipeline.getStages().isEmpty())
                .findFirst();

        return firstPipeline.orElse(null);
    }

}
