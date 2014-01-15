package com.oddprints.checkout;

public class Address {
    private String recipientName;
    private String address1;
    private String address2;
    private String townOrCity;
    private String stateOrCounty;
    private String postalOrZipCode;
    private String countryCode;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getTownOrCity() {
        return townOrCity;
    }

    public void setTownOrCity(String townOrCity) {
        this.townOrCity = townOrCity;
    }

    public String getStateOrCounty() {
        return stateOrCounty;
    }

    public void setStateOrCounty(String stateOrCounty) {
        this.stateOrCounty = stateOrCounty;
    }

    public String getPostalOrZipCode() {
        return postalOrZipCode;
    }

    public void setPostalOrZipCode(String postalOrZipCode) {
        this.postalOrZipCode = postalOrZipCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
