package fr.slickteam.hubspot.api.utils;

/**
 * The enum HubSpot search operator.
 * <a href="https://developers.hubspot.com/docs/guides/api/crm/search#filter-search-results">HubSpot documentation</a>
 *
 */
public enum HubSpotSearchOperator {
    /**
     * Lower Than operator
     */
    LT,
    /**
     * Lower or Equal operator
     */
    LTE,
    /**
     * Greater Than operator
     */
    GT,
    /**
     * Greater or Equal operator
     */
    GTE,
    /**
     * Equals operator
     */
    EQ,
    /**
     * Not Equals operator
     */
    NEQ,
    /**
     * Between operator
     */
    BETWEEN,
    /**
     * IN values operator
     */
    IN,
    /**
     * NOT IN values operator
     */
    NOT_IN,
    /**
     * Has property operator
     */
    HAS_PROPERTY,
    /**
     * Not has property operator
     */
    NOT_HAS_PROPERTY,
    /**
     * Contains token operator
     */
    CONTAINS_TOKEN,
    /**
     * Not contains token operator
     */
    NOT_CONTAINS_TOKEN
}
