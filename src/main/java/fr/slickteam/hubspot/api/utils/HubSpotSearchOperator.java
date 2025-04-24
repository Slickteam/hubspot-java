package fr.slickteam.hubspot.api.utils;

/**
 * The enum HubSpot search operator.
 * <a href="https://developers.hubspot.com/docs/guides/api/crm/search#filter-search-results">HubSpot documentation</a>
 *
 */
public enum HubSpotSearchOperator {
    LT,
    LTE,
    GT,
    GTE,
    EQ,
    NEQ,
    BETWEEN,
    IN,
    NOT_IN,
    HAS_PROPERTY,
    NOT_HAS_PROPERTY,
    CONTAINS_TOKEN,
    NOT_CONTAINS_TOKEN
}
