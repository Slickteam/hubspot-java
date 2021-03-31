package fr.slickteam.hubspotApi.domain;

import java.util.Map;

/**
 * Author: dlunev
 * Date: 4/26/16 11:34 AM
 */
public class HSCompany extends HSObject {

    private static String ID = "vid";
    private static String NAME = "name";
    private static String PHONE_NUMBER = "phone";
    private static String ADDRESS = "address";
    private static String POSTAL_CODE = "zip";
    private static String CITY = "city";
    private static String COUNTRY = "country";

    public HSCompany() {
    }

    public HSCompany(String name, String phoneNumber, String address, String postalCode, String city, String country) {
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setAddress(address);
        this.setPostalCode(postalCode);
        this.setCity(city);
        this.setCountry(country);
    }

    public HSCompany(String name, String phoneNumber, String address, String postalCode, String city, String country, Map<String, String> properties) {
        super(properties);
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setAddress(address);
        this.setPostalCode(postalCode);
        this.setCity(city);
        this.setCountry(country);
    }

    public String getPhoneNumber() {
        return getProperty(PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        setProperty(PHONE_NUMBER, phoneNumber);
    }

    public String getAddress() {
        return getProperty(ADDRESS);
    }

    public void setAddress(String address) {
        setProperty(ADDRESS, address);
    }

    public String getPostalCode() {
        return getProperty(POSTAL_CODE);
    }

    public void setPostalCode(String postalCode) {
        setProperty(POSTAL_CODE, postalCode);
    }

    public String getCity() {
        return getProperty(CITY);
    }

    public void setCity(String city) {
        setProperty(CITY, city);
    }

    public String getCountry() {
        return getProperty(COUNTRY);
    }

    public void setCountry(String country) {
        setProperty(COUNTRY, country);
    }

    public String getName() {
        return getProperty(NAME);
    }

    public void setName(String name) {
        setProperty(NAME, name);
    }

    public long getId() {
        return getLongProperty(ID);
    }

    public HSCompany setId(long id) {
        setProperty(ID, Long.toString(id));
        return this;
    }

}

