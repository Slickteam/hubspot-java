package fr.slickteam.hubspot.api.webhook;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base object for Webhook from HubSpot
 */
public class HSWebHookObject extends LinkedHashMap<String, Object> implements Serializable {

    /**
     * The private application id
     */
    protected static final String APP_ID = "appId";
    /**
     * The Event id
     */
    protected static final String EVENT_ID = "eventId";
    /**
     * The subscription id
     */
    protected static final String SUBSCRIPTION_ID = "subscriptionId";
    /**
     * The portal id
     */
    protected static final String PORTAL_ID = "portalId";
    /**
     * Timestamp of the event
     */
    protected static final String OCCURRED_AT = "occurredAt";
    /**
     * The subscription type (contact.creation, contact.deletion, ...)
     */
    protected static final String SUBSCRIPTION_TYPE = "subscriptionType";
    /**
     * The attempt number
     */
    protected static final String ATTEMPT_NUMBER = "attemptNumber";
    /**
     * The source of the change
     */
    protected static final String CHANGE_SOURCE = "changeSource";

    /**
     * Instantiates a new Hs web hook object.
     */
    public HSWebHookObject() {
        super();
    }

    /**
     * Instantiates a new Hs web hook object.
     *
     * @param appId            the app id
     * @param eventId          the event id
     * @param subscriptionId   the subscription id
     * @param portalId         the portal id
     * @param occurredAt       the occurred at
     * @param subscriptionType the subscription type
     * @param attemptNumber    the attempt number
     * @param changeSource     the change source
     */
    public HSWebHookObject(Long appId, Long eventId, Long subscriptionId, Long portalId, Long occurredAt,
                           String subscriptionType, Long attemptNumber, String changeSource) {
        super();
        this.put(APP_ID, appId);
        this.put(EVENT_ID, eventId);
        this.put(SUBSCRIPTION_ID, subscriptionId);
        this.put(PORTAL_ID, portalId);
        this.put(OCCURRED_AT, occurredAt);
        this.put(SUBSCRIPTION_TYPE, subscriptionType);
        this.put(ATTEMPT_NUMBER, attemptNumber);
        this.put(CHANGE_SOURCE, changeSource);
    }

    /**
     * Map from linked hash map hs web hook object for automatic mapping from Jackson in client.
     *
     * @param object - the object to map
     * @return the HSWebHookObject
     */
    public static HSWebHookObject mapFromLinkedHashMap(Map<String, Object> object) {
        return new HSWebHookObject(
                Long.parseLong(object.get(APP_ID).toString()),
                Long.parseLong(object.get(EVENT_ID).toString()),
                Long.parseLong(object.get(SUBSCRIPTION_ID).toString()),
                Long.parseLong(object.get(PORTAL_ID).toString()),
                Long.parseLong(object.get(OCCURRED_AT).toString()),
                (String) object.get(SUBSCRIPTION_TYPE),
                Long.parseLong(object.get(ATTEMPT_NUMBER).toString()),
                (String) object.get(CHANGE_SOURCE)
        );
    }

    /**
     * Gets app id.
     *
     * @return the app id
     */
    public Long getAppId() {
        return this.get(APP_ID) != null ? (Long) this.get(APP_ID) : null;
    }

    /**
     * Sets app id.
     *
     * @param appId the app id
     */
    public void setAppId(Long appId) {
        this.put(APP_ID, appId);
    }

    /**
     * Gets event id.
     *
     * @return the event id
     */
    public Long getEventId() {
        return this.get(EVENT_ID) != null ? (Long) this.get(EVENT_ID) : null;
    }

    /**
     * Sets event id.
     *
     * @param eventId the event id
     */
    public void setEventId(Long eventId) {
        this.put(EVENT_ID, eventId);
    }

    /**
     * Gets subscription id.
     *
     * @return the subscription id
     */
    public Long getSubscriptionId() {
        return this.get(SUBSCRIPTION_ID) != null ? (Long) this.get(SUBSCRIPTION_ID) : null;
    }

    /**
     * Sets subscription id.
     *
     * @param subscriptionId the subscription id
     */
    public void setSubscriptionId(Long subscriptionId) {
        this.put(SUBSCRIPTION_ID, subscriptionId);
    }

    /**
     * Gets portal id.
     *
     * @return the portal id
     */
    public Long getPortalId() {
        return this.get(PORTAL_ID) != null ? (Long) this.get(PORTAL_ID) : null;
    }

    /**
     * Sets portal id.
     *
     * @param portalId the portal id
     */
    public void setPortalId(Long portalId) {
        this.put(PORTAL_ID, portalId);
    }

    /**
     * Gets occurred at.
     *
     * @return the occurred at
     */
    public Long getOccurredAt() {
        return this.get(OCCURRED_AT) != null ? (Long) this.get(OCCURRED_AT) : null;
    }

    /**
     * Sets occurred at.
     *
     * @param occurredAt the occurred at
     */
    public void setOccurredAt(Long occurredAt) {
        this.put(OCCURRED_AT, occurredAt);
    }

    /**
     * Gets subscription type.
     *
     * @return the subscription type
     */
    public String getSubscriptionType() {
        return (String) this.get(SUBSCRIPTION_TYPE);
    }

    /**
     * Sets subscription type.
     *
     * @param subscriptionType the subscription type
     */
    public void setSubscriptionType(String subscriptionType) {
        this.put(SUBSCRIPTION_TYPE, subscriptionType);
    }

    /**
     * Gets attempt number.
     *
     * @return the attempt number
     */
    public Long getAttemptNumber() {
        return this.get(ATTEMPT_NUMBER) != null ? (Long) this.get(ATTEMPT_NUMBER) : null;
    }

    /**
     * Sets attempt number.
     *
     * @param attemptNumber the attempt number
     */
    public void setAttemptNumber(Long attemptNumber) {
        this.put(ATTEMPT_NUMBER, attemptNumber);
    }

    /**
     * Gets change source.
     *
     * @return the change source
     */
    public String getChangeSource() {
        return (String) this.get(CHANGE_SOURCE);
    }

    /**
     * Sets change source.
     *
     * @param changeSource the change source
     */
    public void setChangeSource(String changeSource) {
        this.put(CHANGE_SOURCE, changeSource);
    }
}
