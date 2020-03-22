package com.codepros.prohub.model;

public class Form {

    public String formName;
    public String url;

    public Form() {}

    public Form(String formName, String url) {
        this.formName = formName;
        this.url = url;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
