package com.codepros.prohub.model;

public class Property {

    private int propID; // unique ID for property
    private String phone; // this is users phone number (?)
    private String name; // building name
    private String address; // building address

    public Property(){
    }

    public Property(String name, String address){
        this.name = name;
        this.address = address;
    }

    public int getPropID() {
        return this.propID;
    }

    public void setPropID(int propID) {
        this.propID = propID;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
