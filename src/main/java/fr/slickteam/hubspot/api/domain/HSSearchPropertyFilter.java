package fr.slickteam.hubspot.api.domain;

import fr.slickteam.hubspot.api.utils.HubSpotSearchOperator;

import java.util.List;

/**
 * Model class for Search Property Filter in search request
 */
public class HSSearchPropertyFilter {
    /**
     * Name of the property to filter on.
     */
    private final String propertyName;

    /**
     * Higher value for BETWEEN operator.
     */
    private final String highValue;

    /**
     * Value to filter on.
     */
    private final String value;

    /**
     * List of values to filter on (for IN or NOT IN operator).
     */
    private final List<String> values;

    /**
     * Operator to use for filtering.
     */
    private final HubSpotSearchOperator operator;

    /**
     * Constructs a new HSSearchPropertyFilter instance with the specified parameters.
     *
     * @param propertyName the name of the property to filter on
     * @param highValue the higher value for the BETWEEN operator
     * @param value the value to filter on
     * @param values the list of values to filter on, used for operators like IN or NOT IN
     * @param operator the operator to use for filtering
     */
    public HSSearchPropertyFilter(String propertyName, String highValue, String value, List<String> values, HubSpotSearchOperator operator) {
        this.propertyName = propertyName;
        this.highValue = highValue;
        this.value = value;
        this.values = values;
        this.operator = operator;
    }

    /**
     * Retrieves the name of the property to filter on.
     *
     * @return the name of the property being filtered
     */
    public String getPropertyName() {
        return propertyName;
    }
    /**
     * Retrieves the higher value used for the BETWEEN operator in filtering.
     *
     * @return the high value as a String
     */
    public String getHighValue() {
        return highValue;
    }
    /**
     * Retrieves the value to filter on.
     *
     * @return the value to filter on
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the list of values to filter on, typically used for operators like IN or NOT IN.
     *
     * @return a list of strings representing the values for use in filtering
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Retrieves the operator used for filtering in the search property filter.
     *
     * @return the operator used for filtering
     */
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
