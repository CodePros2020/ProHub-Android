package com.codepros.prohub.model;

public class Unit {


    private String unitId;
    private String propId; // unique ID for property
    private String tenantId; // unique ID for tenant user
    private String unitName; // building address

    public Unit(){ }

    public Unit(String unitId,String propId, String tenantId, String unitName) {
        this.unitId=unitId;
        this.propId = propId;
        this.tenantId = tenantId;
        this.unitName = unitName;
    }
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
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
