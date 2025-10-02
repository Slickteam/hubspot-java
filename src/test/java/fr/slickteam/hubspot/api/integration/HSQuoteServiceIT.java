package fr.slickteam.hubspot.api.integration;

import fr.slickteam.hubspot.api.domain.HSQuote;
import fr.slickteam.hubspot.api.service.HSQuoteService;
import fr.slickteam.hubspot.api.service.HubSpot;
import fr.slickteam.hubspot.api.utils.Helper;
import fr.slickteam.hubspot.api.utils.HubSpotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;


import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled("Token issue")
class HSQuoteServiceIT {

    private final String testTitle = "test title";
    private final Instant testExpirationDate = Instant.now();
    private Long createdQuoteId;


    private HubSpot hubSpot;

    @BeforeEach
    void setUp() throws Exception {
        hubSpot = new HubSpot(Helper.provideHubspotProperties());
    }


    @AfterEach
    void tearDown() throws Exception {
        if (createdQuoteId != null) {
            hubSpot.quote().delete(createdQuoteId);
        }
        // add sleep to avoid "Too many requests" error
        sleep(100);
    }


    @org.junit.jupiter.api.Test
    void createQuote_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("hs_title", testTitle);
        quote.setProperty("hs_expiration_date", testExpirationDate.toString());
        quote = hubSpot.quote().create(quote);

        createdQuoteId = quote.getId();

        assertThat(quote.getId()).isNotZero();
    }

    @org.junit.jupiter.api.Test
    void createQuote_NetworkError_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("hs_title", testTitle);
        quote.setProperty("hs_expiration_date", testExpirationDate.toString());
        HSQuoteService mockHSQuoteService = mock(HSQuoteService.class);
        Mockito.doThrow(new HubSpotException("Network error")).when(mockHSQuoteService).create(quote);
        HubSpot mockHubSpot = mock(HubSpot.class);
        when(mockHubSpot.quote()).thenReturn(mockHSQuoteService);
        assertThatThrownBy(() -> mockHubSpot.quote().create(quote))
            .isInstanceOf(HubSpotException.class);
    }


    @org.junit.jupiter.api.Test
    void createQuoteIncorrectProperty_Test() {
        HSQuote quote = new HSQuote();
        quote.setProperty("badpropertyz", "Test value 1");
        quote.setProperty("hs_expiration_date", "2023-12-10");

        assertThatThrownBy(() -> hubSpot.quote().create(quote))
            .isInstanceOf(HubSpotException.class);
    }

    @org.junit.jupiter.api.Test
    void getQuote_Id_Test() throws Exception {
        HSQuote newQuote = new HSQuote();
        newQuote.setProperty("hs_title", testTitle);
        newQuote.setProperty("hs_expiration_date", testExpirationDate.toString());
        newQuote = hubSpot.quote().create(newQuote);
        HSQuote responseQuote = hubSpot.quote().getByID(newQuote.getId());

        createdQuoteId = newQuote.getId();
        assertThat(responseQuote.getId()).isEqualTo(newQuote.getId());
    }

    @org.junit.jupiter.api.Test
    void getQuote_Id_And_Properties_Test() throws Exception {
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

        assertThat(responseQuote.getId()).isEqualTo(quoteId);
        assertThat(responseQuote.getProperties()).containsEntry("hs_title", testTitle);
    }

    @org.junit.jupiter.api.Test
    void getQuote_Id_Not_Found_Test() throws Exception {
        long id = -777;
        assertThat(hubSpot.quote().getByID(id)).isNull();
        assertThat(hubSpot.quote().getByID(id)).isNull();
    }


    @org.junit.jupiter.api.Test
    void patch_status_quote_Test() throws Exception {
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

        assertThat(result.getProperties().get("hs_status")).isEqualTo(editedQuote.getProperties().get("hs_status"));
    }

    @org.junit.jupiter.api.Test
    void deleteDeal_by_id_Test() throws Exception {
        HSQuote quote = new HSQuote();
        quote.setProperty("hs_title", testTitle);
        quote.setProperty("hs_expiration_date", testExpirationDate.toString());

        quote = hubSpot
                .quote()
                .create(quote);
        createdQuoteId = quote.getId();

        hubSpot.quote().delete(quote.getId());

        assertThat(hubSpot.quote().getByID(quote.getId())).isNull();
    }

}
