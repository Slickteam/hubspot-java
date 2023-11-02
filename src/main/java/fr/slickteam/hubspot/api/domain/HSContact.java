package fr.slickteam.hubspot.api.domain;

import java.util.Map;

/**
 * The type Hs contact.
 */
public class HSContact extends HSObject {

    private static final String ID = "vid";
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final String EMAIL = "email";
    private static final String PHONE_NUMBER = "phone";
    private static final String LIFE_CYCLE_STAGE = "lifecyclestage";

    /**
     * Instantiates a new Hs contact.
     */
    public HSContact() {
    }

    /**
     * Instantiates a new Hs contact.
     *
     * @param email       the email
     * @param firstname   the firstname
     * @param lastname    the lastname
     * @param phoneNumber the phone number
     * @param lifeStage   the life stage
     */
    public HSContact(String email, String firstname, String lastname, String phoneNumber, String lifeStage) {
        this.setEmail(email);
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhoneNumber(phoneNumber);
        this.setLifeCycleStage(lifeStage);
    }

    /**
     * Instantiates a new Hs contact.
     *
     * @param id          the id
     * @param email       the email
     * @param firstname   the firstname
     * @param lastname    the lastname
     * @param phoneNumber the phone number
     * @param lifeStage   the life stage
     * @param properties  the properties
     */
    public HSContact(long id, String email, String firstname, String lastname, String phoneNumber, String lifeStage, Map<String, String> properties) {
        super(properties);
        this.setId(id);
        this.setEmail(email);
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhoneNumber(phoneNumber);
        this.setLifeCycleStage(lifeStage);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return getLongProperty(ID);
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public HSContact setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return getProperty(EMAIL);
    }

    /**
     * Sets email.
     *
     * @param email the email
     * @return the email
     */
    public HSContact setEmail(String email) {
        setProperty(EMAIL, email);
        return this;
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return getProperty(PHONE_NUMBER);
    }

    /**
     * Sets phone number.
     *
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        setProperty(PHONE_NUMBER, phoneNumber);
    }

    /**
     * Gets firstname.
     *
     * @return the firstname
     */
    public String getFirstname() {
        return getProperty(FIRST_NAME);
    }

    /**
     * Sets firstname.
     *
     * @param firstname the firstname
     * @return the firstname
     */
    public HSContact setFirstname(String firstname) {
        setProperty(FIRST_NAME, firstname);
        return this;
    }

    /**
     * Gets lastname.
     *
     * @return the lastname
     */
    public String getLastname() {
        return getProperty(LAST_NAME);
    }

    /**
     * Sets lastname.
     *
     * @param lastname the lastname
     * @return the lastname
     */
    public HSContact setLastname(String lastname) {
        setProperty(LAST_NAME, lastname);
        return this;
    }

    /**
     * Gets life cycle stage.
     *
     * @return the life cycle stage
     */
    public String getLifeCycleStage() {
        return getProperty(LIFE_CYCLE_STAGE);
    }

    /**
     * Sets life cycle stage.
     *
     * @param lifeCycleStage the life cycle stage
     * @return the life cycle stage
     */
    public HSContact setLifeCycleStage(String lifeCycleStage) {
        setProperty(LIFE_CYCLE_STAGE, lifeCycleStage);
        return this;
    }
}
