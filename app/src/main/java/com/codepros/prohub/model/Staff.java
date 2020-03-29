package com.codepros.prohub.model;

public class Staff {
    private String staffId;
    private String propId;
    private String name;
    private String phoneNum;
    private String address;
    private String postalCode;
    private String city;
    private String province;
    private String email;
    private String role;
    private String ImgUrl;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }



public Staff(){}
    public Staff( String staffId,String propId, String name, String phoneNum, String address, String postalCode, String city,String province, String email, String role, String imgUrl) {
        this.staffId=staffId;
        this.propId = propId;
        this.name = name;
        this.phoneNum = phoneNum;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.email = email;
        this.role = role;
        this.ImgUrl = imgUrl;
        this.province=province;
    }


    public String getPropId() {
        return propId;
    }

    public void setPropId(String staffPropId) {
        this.propId = staffPropId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStaffId() {
        return staffId;
    }
    public void setStaffId(String staffId) {
        this.staffId=staffId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


//    public String getPhone(){
//
//        String phone=phoneNum.substring(2,phoneNum.length());
//        return phone;
//    }




}
