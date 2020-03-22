package com.codepros.prohub.model;

public class Unit {
    private String propId; // unique ID for property
    private String tenantId; // unique ID for tenant user
    private String unitName; // building address

    public Unit(){ }

    public Unit(String propId, String tenantId, String unitName) {
        this.propId = propId;
        this.tenantId = tenantId;
        this.unitName = unitName;
    }

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
