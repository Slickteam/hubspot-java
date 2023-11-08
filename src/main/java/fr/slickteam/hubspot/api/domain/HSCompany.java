package fr.slickteam.hubspot.api.domain;

import java.util.Map;

/**
 * Model class for HubSpot Company
 * <p>
 * Author: dlunev
 * Date: 4/26/16 11:34 AM
 */
public class HSCompany extends HSObject {

    private static final String ID = "vid";
    private static final String NAME = "name";
    private static final String PHONE_NUMBER = "phone";
    private static final String ADDRESS = "address";
    private static final String POSTAL_CODE = "zip";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String DESCRIPTION = "description";
    private static final String WEBSITE = "website";

    /**
     * Instantiates a new Hs company.
     */
    public HSCompany() {
    }

    /**
     * Instantiates a new Hs company.
     *
     * @param name        the name
     * @param phoneNumber the phone number
     * @param address     the address
     * @param postalCode  the postal code
     * @param city        the city
     * @param country     the country
     */
    public HSCompany(String name, String phoneNumber, String address, String postalCode, String city, String country) {
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setAddress(address);
        this.setPostalCode(postalCode);
        this.setCity(city);
        this.setCountry(country);
    }

    /**
     * Instantiates a new Hs company.
     *
     * @param name        the name
     * @param phoneNumber the phone number
     * @param address     the address
     * @param postalCode  the postal code
     * @param city        the city
     * @param country     the country
     * @param description the description
     * @param website     the website
     */
    public HSCompany(String name, String phoneNumber, String address, String postalCode, String city, String country, String description, String website) {
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setAddress(address);
        this.setPostalCode(postalCode);
        this.setCity(city);
        this.setCountry(country);
        this.setDescription(description);
        this.setWebsite(website);
    }

    /**
     * Instantiates a new Hs company.
     *
     * @param name        the name
     * @param phoneNumber the phone number
     * @param address     the address
     * @param postalCode  the postal code
     * @param city        the city
     * @param country     the country
     * @param description the description
     * @param website     the website
     * @param properties  the properties
     */
    public HSCompany(String name, String phoneNumber, String address, String postalCode, String city, String country, String description, String website, Map<String, String> properties) {
        super(properties);
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setAddress(address);
        this.setPostalCode(postalCode);
        this.setCity(city);
        this.setCountry(country);
        this.setDescription(description);
        this.setWebsite(website);
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
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return getProperty(ADDRESS);
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        setProperty(ADDRESS, address);
    }

    /**
     * Gets postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return getProperty(POSTAL_CODE);
    }

    /**
     * Sets postal code.
     *
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        setProperty(POSTAL_CODE, postalCode);
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return getProperty(CITY);
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        setProperty(CITY, city);
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return getProperty(COUNTRY);
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        setProperty(COUNTRY, country);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return getProperty(NAME);
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        setProperty(NAME, name);
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
    public HSCompany setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;    }

    /**
     * Gets website.
     *
     * @return the website
     */
    public String getWebsite() {
        return getProperty(WEBSITE);
    }

    /**
     * Sets website.
     *
     * @param website the website
     */
    public void setWebsite(String website) {
        setProperty(WEBSITE, website);
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return getProperty(DESCRIPTION);
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        setProperty(DESCRIPTION, description);
    }

}

