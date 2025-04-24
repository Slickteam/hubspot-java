package fr.slickteam.hubspot.api.domain;

import fr.slickteam.hubspot.api.utils.HubSpotSearchOperator;

import java.util.List;

/**
 * Model class for Search Property Filter in search request
 */
public class HSSearchPropertyFilter {
    private final String propertyName;
    private final String highValue;
    private final String value;
    private final List<String> values;
    private final HubSpotSearchOperator operator;

    public HSSearchPropertyFilter(String propertyName, String highValue, String value, List<String> values, HubSpotSearchOperator operator) {
        this.propertyName = propertyName;
        this.highValue = highValue;
        this.value = value;
        this.values = values;
        this.operator = operator;
    }

    public String getPropertyName() {
        return propertyName;
    }
    public String getHighValue() {
        return highValue;
    }
    public String getValue() {
        return value;
    }

    public List<String> getValues() {
        return values;
    }

    public HubSpotSearchOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "HSSearchPropertyFilter{" +
                "propertyName='" + propertyName + '\'' +
                ", highValue='" + highValue + '\'' +
                ", value='" + value + '\'' +
                ", values=" + values +
                ", operator=" + operator +
                '}';
    }
}
