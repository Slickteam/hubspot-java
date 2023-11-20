package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSQuote;
import fr.slickteam.hubspot.api.service.HSQuoteService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;


import static java.lang.Thread.sleep;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore("Token issue")
public class HSQuoteServiceIT {

    private final String testTitle = "test title";
    private final Instant testExpirationDate = Instant.now();
    private final Instant testCreatedDate = Instant.now();
    private final Instant testLastModifiedDate = Instant.now();
    private final String testPdfDownloadLink = "download link";
    private String testObjectId = null;
    private Long createdQuoteId;


    private HubSpot hubSpot;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }


    @After
    public void tearDown() throws Exception {
        if (createdQuoteId != null) {
            hubSpot.quote().delete(createdQuoteId);
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }


    @Test
    public void createQuote_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("hs_title", testTitle);
        quote.setProperty("hs_expiration_date", testExpirationDate.toString());
        quote = hubSpot.quote().create(quote);

        createdQuoteId = quote.getId();

        assertNotEquals(0L, quote.getId());
    }

    @Test
    public void createQuote_NetworkError_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("hs_title", testTitle);
        quote.setProperty("hs_expiration_date", testExpirationDate.toString());
        HSQuoteService mockHSQuoteService = mock(HSQuoteService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSQuoteService).create(quote);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.quote()).thenReturn(mockHSQuoteService);
        exception.expect(HubSpotException.class);
        mockHubSpot.quote().create(quote);
    }


    @Test
    public void createQuoteIncorrectProperty_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("badpropertyz", "Test value 1");
        quote.setProperty("hs_expiration_date", "2023-12-10");

        exception.expect(HubSpotException.class);
        hubSpot.quote().create(quote);
    }

    @Test
    public void getQuote_Id_Test() throws Exception {
        HSQuote newQuote = new HSQuote();
        newQuote.setProperty("hs_title", testTitle);
        newQuote.setProperty("hs_expiration_date", testExpirationDate.toString());
        newQuote = hubSpot.quote().create(newQuote);
        HSQuote responseQuote = hubSpot.quote().getByID(newQuote.getId());

        createdQuoteId = newQuote.getId();
        assertEquals(newQuote.getId(), responseQuote.getId());
    }

    @Test
    public void getQuote_Id_And_Properties_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("hs_title", testTitle);
        quote.setProperty("hs_expiration_date", testExpirationDate.toString());

        List<String> properties = Arrays.asList("hs_title","hs_created_by_user_id");
        long quoteId = hubSpot
                .quote()
                .create(quote)
                .getId();
        createdQuoteId = quoteId;

        HSQuote responseQuote = hubSpot.quote().getByIdAndProperties(quoteId, properties);

        assertEquals(quoteId, responseQuote.getId());
        assertEquals(testTitle, responseQuote.getProperties().get("hs_title"));
    }

    @Test
    public void getQuote_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertNull(hubSpot.quote().getByID(id));
        assertNull(hubSpot.quote().getByID(id));
    }


    @Test
    public void patch_status_quote_Test() throws Exception {
        HSQuote newQuote = new HSQuote();
        newQuote.setProperty("hs_title", testTitle);
        newQuote.setProperty("hs_expiration_date", testExpirationDate.toString());

        newQuote = hubSpot
                .quote()
                .create(newQuote);
        createdQuoteId = newQuote.getId();

        HSQuote editedQuote = new HSQuote();
        editedQuote.setProperty("hs_status", "DRAFT");
        editedQuote.setProperty("hs_language", "fr");

        editedQuote.setId(newQuote.getId());

        HSQuote result = hubSpot.quote().patch(editedQuote);

        assertEquals(editedQuote.getProperties().get("hs_status"), result.getProperties().get("hs_status"));
    }

    @Test
    public void deleteDeal_by_id_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("hs_title", testTitle);
        quote.setProperty("hs_expiration_date", testExpirationDate.toString());

        quote = hubSpot
                .quote()
                .create(quote);
        createdQuoteId = quote.getId();

        hubSpot.quote().delete(quote.getId());

        assertNull(hubSpot.quote().getByID(quote.getId()));
    }

}
