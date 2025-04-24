package fr.slickteam.hubspot.api.domain;

import fr.slickteam.hubspot.api.utils.HubSpotOrdering;

/**
 * Model class for Sort Order parameter in search request
 */
public class HSSortOrder {
    /**
     * Name of the property to sort on.
     */
    private final String property;

    /**
     * Ordering to use for sorting.
     */
    private final HubSpotOrdering ordering;

    /**
     * Constructs an instance of HSSortOrder with the specified property and ordering.
     *
     * @param property the name of the property to sort on
     * @param ordering the ordering direction, either ASCENDING or DESCENDING
     */
    public HSSortOrder(String property, HubSpotOrdering ordering) {
        this.property = property;
        this.ordering = ordering;
    }

    /**
     * Retrieves the name of the property to sort on.
     *
     * @return the property name used for sorting
     */
    public String getProperty() {
        return property;
    }

    /**
     * Retrieves the ordering to use for sorting.
     *
     * @return the ordering used for sorting, either ASCENDING or DESCENDING.
     */
    public HubSpotOrdering getOrdering() {
        return ordering;
    }

    @Override
    public String toString() {
        return "HSSortOrder{" +
                "property='" + property + '\'' +
                ", ordering=" + ordering +
                '}';
    }
}
