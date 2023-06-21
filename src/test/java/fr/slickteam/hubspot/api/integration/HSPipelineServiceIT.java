package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.*;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.*;

public class HSPipelineServiceIT {

    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }
    @Test
    public void getPipelines_Test() throws HubSpotException {
        List<HSPipeline> pipelines = hubSpot.pipeline().getPipelines();

        // Assert
        Assert.assertNotNull(pipelines);
        pipelines.forEach(Assert::assertNotNull);
    }
    @Test
    public void getPipelineById_Test() throws HubSpotException {
        HSPipeline pipeline = hubSpot.pipeline().getPipelineById(3770417);
        Assert.assertNotNull(pipeline);
    }

    @Test
    public void getStageById_Test() throws HubSpotException {
        HSStage stage = hubSpot.pipeline().getStageById(3770417, 3770419);
        Assert.assertNotNull(stage);
    }

}
