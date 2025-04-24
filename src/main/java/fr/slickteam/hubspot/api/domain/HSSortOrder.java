package fr.slickteam.hubspot.api.domain;

import fr.slickteam.hubspot.api.utils.HubSpotOrdering;

public class HSSortOrder {
    private final String property;
    private final HubSpotOrdering ordering;

    public HSSortOrder(String property, HubSpotOrdering ordering) {
        this.property = property;
        this.ordering = ordering;
    }

    public String getProperty() {
        return property;
    }
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
