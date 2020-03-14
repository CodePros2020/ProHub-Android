package com.codepros.prohub.model;

public class Property {

    private int propID; // unique ID for property
    private String phone; // this is users phone number (?)
    private String name; // building name
    private String streetLine1; // building address
    private String streetLine2;
    private String city;
    private String province;
    private String postalCode;// Postal code (need validation)

    public Property(){
    }

    public Property(String name, String streetLine1, String streetLine2, String city, String province, String postalCode) {
        this.name = name;
        this.streetLine1 = streetLine1;
        this.streetLine2 = streetLine2;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }

    public int getPropID(){return propID;}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetLine1() {
        return streetLine1;
    }

    public void setStreetLine1(String streetLine1) {
        this.streetLine1 = streetLine1;
    }

    public String getStreetLine2() {
        return streetLine2;
    }

    public void setStreetLine2(String streetLine2) {
        this.streetLine2 = streetLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }






}
