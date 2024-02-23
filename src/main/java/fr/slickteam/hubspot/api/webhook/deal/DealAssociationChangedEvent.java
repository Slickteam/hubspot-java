package fr.slickteam.hubspot.api.webhook.deal;

import fr.slickteam.hubspot.api.webhook.HSWebHookObject;

import java.util.Map;

/**
 * Deal association changed event
 */
public class DealAssociationChangedEvent extends HSWebHookObject {
    /**
     * The type of association
     */
    private static final String ASSOCIATION_TYPE = "associationType";
    /**
     * The ID of the object that the association was changed on
     */
    private static final String FROM_OBJECT_ID = "fromObjectId";
    /**
     * The ID of the object that the association was changed to
     */
    private static final String TO_OBJECT_ID = "toObjectId";
    /**
     * Was the association removed ?
     */
    private static final String ASSOCIATION_REMOVED = "associationRemoved";
    /**
     * Is the association primary ?
     */
    private static final String IS_PRIMARY_ASSOCIATION = "isPrimaryAssociation";

    /**
     * Instantiates a new Deal association changed event.
     */
    public DealAssociationChangedEvent() {
        super();
    }

    /**
     * Instantiates a new Deal association changed event.
     *
     * @param appId                the app id
     * @param eventId              the event id
     * @param subscriptionId       the subscription id
     * @param portalId             the portal id
     * @param occurredAt           the occurred at
     * @param subscriptionType     the subscription type
     * @param attemptNumber        the attempt number
     * @param changeSource         the change source
     * @param associationType      the association type
     * @param fromObjectId         the from object id
     * @param toObjectId           the to object id
     * @param associationRemoved   the association removed
     * @param isPrimaryAssociation the is primary association
     */
    public DealAssociationChangedEvent(Long appId, Long eventId, Long subscriptionId, Long portalId, Long occurredAt,
                                       String subscriptionType, Long attemptNumber, String changeSource,
                                       String associationType, Long fromObjectId, Long toObjectId,
                                       boolean associationRemoved, boolean isPrimaryAssociation) {
        super(appId, eventId, subscriptionId, portalId, occurredAt, subscriptionType, attemptNumber, changeSource);
        this.put(ASSOCIATION_TYPE, associationType);
        this.put(FROM_OBJECT_ID, fromObjectId);
        this.put(TO_OBJECT_ID, toObjectId);
        this.put(ASSOCIATION_REMOVED, associationRemoved);
        this.put(IS_PRIMARY_ASSOCIATION, isPrimaryAssociation);
    }

    /**
     * Map from linked hash map hs web hook object for automatic mapping from Jackson in client.
     *
     * @param object - the object to map
     * @return the event
     */
    public static DealAssociationChangedEvent mapFromLinkedHashMap(Map<String, Object> object) {
        return new DealAssociationChangedEvent(
                Long.parseLong(object.get(APP_ID).toString()),
                Long.parseLong(object.get(EVENT_ID).toString()),
                Long.parseLong(object.get(SUBSCRIPTION_ID).toString()),
                Long.parseLong(object.get(PORTAL_ID).toString()),
                Long.parseLong(object.get(OCCURRED_AT).toString()),
                (String) object.get(SUBSCRIPTION_TYPE),
                Long.parseLong(object.get(ATTEMPT_NUMBER).toString()),
                (String) object.get(CHANGE_SOURCE),
                (String) object.get(ASSOCIATION_TYPE),
                Long.parseLong(object.get(FROM_OBJECT_ID).toString()),
                Long.parseLong(object.get(TO_OBJECT_ID).toString()),
                (boolean) object.get(ASSOCIATION_REMOVED),
                (boolean) object.get(IS_PRIMARY_ASSOCIATION)
        );
    }

    /**
     * Gets association type.
     *
     * @return the association type
     */
    public String getAssociationType() {
        return (String) this.get(ASSOCIATION_TYPE);
    }

    /**
     * Sets association type.
     *
     * @param associationType the association type
     */
    public void setAssociationType(String associationType) {
        this.put(ASSOCIATION_TYPE, associationType);
    }

    /**
     * Gets from object id.
     *
     * @return the from object id
     */
    public Long getFromObjectId() {
        return this.get(FROM_OBJECT_ID) != null ? (Long) this.get(FROM_OBJECT_ID) : null;
    }

    /**
     * Sets from object id.
     *
     * @param fromObjectId the from object id
     */
    public void setFromObjectId(Long fromObjectId) {
        this.put(FROM_OBJECT_ID, fromObjectId);
    }

    /**
     * Gets to object id.
     *
     * @return the to object id
     */
    public Long getToObjectId() {
        return this.get(TO_OBJECT_ID) != null ? (Long) this.get(TO_OBJECT_ID) : null;
    }

    /**
     * Sets to object id.
     *
     * @param toObjectId the to object id
     */
    public void setToObjectId(Long toObjectId) {
        this.put(TO_OBJECT_ID, toObjectId);
    }

    /**
     * Is association removed boolean.
     *
     * @return the boolean
     */
    public boolean isAssociationRemoved() {
        return (boolean) this.get(ASSOCIATION_REMOVED);
    }

    /**
     * Sets association removed.
     *
     * @param associationRemoved the association removed
     */
    public void setAssociationRemoved(boolean associationRemoved) {
        this.put(ASSOCIATION_REMOVED, associationRemoved);
    }

    /**
     * Is primary association boolean.
     *
     * @return the boolean
     */
    public boolean isPrimaryAssociation() {
        return (boolean) this.get(IS_PRIMARY_ASSOCIATION);
    }

    /**
     * Sets primary association.
     *
     * @param primaryAssociation the primary association
     */
    public void setPrimaryAssociation(boolean primaryAssociation) {
        this.put(IS_PRIMARY_ASSOCIATION, primaryAssociation);
    }
}
