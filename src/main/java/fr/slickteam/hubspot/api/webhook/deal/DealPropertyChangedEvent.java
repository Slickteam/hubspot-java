package fr.slickteam.hubspot.api.webhook.deal;

import fr.slickteam.hubspot.api.webhook.HSWebHookObject;

import java.util.Map;

/**
 * Deal property changed event
 */
public class DealPropertyChangedEvent extends HSWebHookObject {

    /**
     * ID of the deal
     */
    private static final String OBJECT_ID = "objectId";
    /**
     * The property name
     */
    private static final String PROPERTY_NAME = "propertyName";
    /**
     * The property value
     */
    private static final String PROPERTY_VALUE = "propertyValue";

    /**
     * Instantiates a new Deal property changed event.
     */
    public DealPropertyChangedEvent() {
        super();
    }

    /**
     * Instantiates a new Deal property changed event.
     *
     * @param appId            the app id
     * @param eventId          the event id
     * @param subscriptionId   the subscription id
     * @param portalId         the portal id
     * @param occurredAt       the occurred at
     * @param subscriptionType the subscription type
     * @param attemptNumber    the attempt number
     * @param changeSource     the source of the change
     * @param objectId         the object id
     * @param propertyName     the property name
     * @param propertyValue    the property value
     */
    public DealPropertyChangedEvent(Long appId, Long eventId, Long subscriptionId, Long portalId, Long occurredAt,
                                    String subscriptionType, Long attemptNumber, String changeSource, Long objectId,
                                    String propertyName, String propertyValue) {
        super(appId, eventId, subscriptionId, portalId, occurredAt, subscriptionType, attemptNumber, changeSource);
        this.put(OBJECT_ID, objectId);
        this.put(PROPERTY_NAME, propertyName);
        this.put(PROPERTY_VALUE, propertyValue);
    }

    /**
     * Map from linked hash map hs web hook object for automatic mapping from Jackson in client.
     *
     * @param object - the object to map
     * @return the event
     */
    public static DealPropertyChangedEvent mapFromLinkedHashMap(Map<String, Object> object) {
        return new DealPropertyChangedEvent(
                Long.getLong(object.get(APP_ID) + ""),
                Long.getLong(object.get(EVENT_ID) + ""),
                Long.getLong(object.get(SUBSCRIPTION_ID) + ""),
                Long.getLong(object.get(PORTAL_ID) + ""),
                Long.getLong(object.get(OCCURRED_AT) + ""),
                (String) object.get(SUBSCRIPTION_TYPE),
                Long.getLong(object.get(ATTEMPT_NUMBER) + ""),
                (String) object.get(CHANGE_SOURCE),
                Long.getLong(object.get(OBJECT_ID) + ""),
                (String) object.get(PROPERTY_NAME),
                (String) object.get(PROPERTY_VALUE)
        );
    }

    /**
     * Gets object id.
     *
     * @return the object id
     */
    public Long getObjectId() {
        return this.get(OBJECT_ID) != null ? (Long) this.get(OBJECT_ID) : null;
    }

    /**
     * Sets object id.
     *
     * @param objectId the object id
     */
    public void setObjectId(Long objectId) {
        this.put(OBJECT_ID, objectId);
    }

    /**
     * Gets property name.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return (String) this.get(PROPERTY_NAME);
    }

    /**
     * Sets property name.
     *
     * @param propertyName the property name
     */
    public void setPropertyName(String propertyName) {
        this.put(PROPERTY_NAME, propertyName);
    }

    /**
     * Gets property value.
     *
     * @return the property value
     */
    public String getPropertyValue() {
        return (String) this.get(PROPERTY_VALUE);
    }

    /**
     * Sets property value.
     *
     * @param propertyValue the property value
     */
    public void setPropertyValue(String propertyValue) {
        this.put(PROPERTY_VALUE, propertyValue);
    }
}
