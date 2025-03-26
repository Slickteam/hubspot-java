package fr.slickteam.hubspot.api.webhook.contact;

import fr.slickteam.hubspot.api.webhook.HSWebHookObject;

import java.util.Map;

/**
 * Contact delete for privacy event
 */
public class ContactDeleteForPrivacyEvent extends HSWebHookObject {

    /**
     * ID of the contact
     */
    private static final String OBJECT_ID = "objectId";


    /**
     * Instantiates a new Contact delete for privacy event.
     */
    public ContactDeleteForPrivacyEvent() {
        super();
    }

    /**
     * Instantiates a new Contact delete for privacy event.
     *
     * @param appId            the app id
     * @param eventId          the event id
     * @param subscriptionId   the subscription id
     * @param portalId         the portal id
     * @param occurredAt       the occurred at
     * @param subscriptionType the subscription type
     * @param attemptNumber    the attempt number
     * @param objectId         the object id
     * @param changeSource     the change source
     */
    public ContactDeleteForPrivacyEvent(Long appId, Long eventId, Long subscriptionId, Long portalId, Long occurredAt,
                                        String subscriptionType, Long attemptNumber, Long objectId, String changeSource) {
        super(appId, eventId, subscriptionId, portalId, occurredAt, subscriptionType, attemptNumber, changeSource);
        this.put(OBJECT_ID, objectId);
    }

    /**
     * Map from linked hash map hs web hook object for automatic mapping from Jackson in client.
     *
     * @param object - the object to map
     * @return the event
     */
    public static ContactDeleteForPrivacyEvent mapFromLinkedHashMap(Map<String, Object> object) {
        return new ContactDeleteForPrivacyEvent(
                Long.parseLong(object.get(APP_ID).toString()),
                Long.parseLong(object.get(EVENT_ID).toString()),
                Long.parseLong(object.get(SUBSCRIPTION_ID).toString()),
                Long.parseLong(object.get(PORTAL_ID).toString()),
                Long.parseLong(object.get(OCCURRED_AT).toString()),
                (String) object.get(SUBSCRIPTION_TYPE),
                Long.parseLong(object.get(ATTEMPT_NUMBER).toString()),
                Long.parseLong(object.get(OBJECT_ID).toString()),
                (String) object.get(CHANGE_SOURCE)
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

}
