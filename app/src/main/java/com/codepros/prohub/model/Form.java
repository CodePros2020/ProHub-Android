package com.codepros.prohub.model;

public class Form {
    private String formId;
    private String propId;
    private String formTitle;
    private String contentUrl;

    public Form() {
    }

    public Form(String formId, String propId, String formTitle, String contentUrl) {
        this.formId = formId;
        this.propId = propId;
        this.formTitle = formTitle;
        this.contentUrl = contentUrl;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}

//     public String formName;
//     public String url;

//     public Form() {}

//     public Form(String formName, String url) {
//         this.formName = formName;
//         this.url = url;
//     }

//     public String getFormName() {
//         return formName;
//     }

//     public void setFormName(String formName) {
//         this.formName = formName;
//     }

//     public String getUrl() {
//         return url;
//     }

//     public void setUrl(String url) {
//         this.url = url;
//     }
// }
