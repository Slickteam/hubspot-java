package fr.slickteam.hubspot.api.domain;

import java.util.List;

/**
 * The type Paged hs company list.
 */
public class PagedHSContactList {

    private List<HSContact> contacts;

    private String after;

    /**
     * Instantiates a new Paged hs contact list.
     *
     * @param contacts the contacts
     * @param after     the after
     */
    public PagedHSContactList(List<HSContact> contacts, String after) {
        this.contacts = contacts;
        this.after = after;
    }

    /**
     * Gets companies.
     *
     * @return the companies
     */
    public List<HSContact> getContacts() {
        return contacts;
    }

    /**
     * Sets companies.
     *
     * @param contacts the contacts
     */
    public void setContacts(List<HSContact> contacts) {
        this.contacts = contacts;
    }

    /**
     * Gets after.
     *
     * @return the after
     */
    public String getAfter() {
        return after;
    }

    /**
     * Sets after.
     *
     * @param after the after
     */
    public void setAfter(String after) {
        this.after = after;
    }
}
