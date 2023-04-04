package fr.slickteam.hubspot.api.domain;

import java.util.Map;

public class HSContact extends HSObject {

    private static final String ID = "vid";
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final String EMAIL = "email";
    private static final String PHONE_NUMBER = "phone";
    private static final String LIFE_CYCLE_STAGE = "lifecyclestage";

    public HSContact() {
    }

    public HSContact(String email, String firstname, String lastname, String phoneNumber, String lifeStage) {
        this.setEmail(email);
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhoneNumber(phoneNumber);
        this.setLifeCycleStage(lifeStage);
    }

    public HSContact(long id, String email, String firstname, String lastname, String phoneNumber, String lifeStage, Map<String, String> properties) {
        super(properties);
        this.setId(id);
        this.setEmail(email);
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhoneNumber(phoneNumber);
        this.setLifeCycleStage(lifeStage);
    }

    public long getId() {
        return getLongProperty(ID);
    }

    public HSContact setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

    public String getEmail() {
        return getProperty(EMAIL);
    }

    public HSContact setEmail(String email) {
        setProperty(EMAIL, email);
        return this;
    }

    public String getPhoneNumber() {
        return getProperty(PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        setProperty(PHONE_NUMBER, phoneNumber);
    }

    public String getFirstname() {
        return getProperty(FIRST_NAME);
    }

    public HSContact setFirstname(String firstname) {
        setProperty(FIRST_NAME, firstname);
        return this;
    }

    public String getLastname() {
        return getProperty(LAST_NAME);
    }

    public HSContact setLastname(String lastname) {
        setProperty(LAST_NAME, lastname);
        return this;
    }

    public String getLifeCycleStage() {
        return getProperty(LIFE_CYCLE_STAGE);
    }

    public HSContact setLifeCycleStage(String lifeCycleStage) {
        setProperty(LIFE_CYCLE_STAGE, lifeCycleStage);
        return this;
    }
}
