package com.codepros.prohub.model;

public class User {
    private String firstname;
    private String lastname;
    private String phone;
    private String password;
    private String role;

    public User() {
    }

    public User(String firstname, String lastname, String phone, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.password = password;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean authentication(String password){
        return this.password.equals(password);
    }
}
