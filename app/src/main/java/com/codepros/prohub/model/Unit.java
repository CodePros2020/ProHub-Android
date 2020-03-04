package com.codepros.prohub.model;

public class Unit {
    private int unitId; // unique ID for unit
    private int propId; // unique ID for property
    private int tenantId; // unique ID for tenant user
    private String unitName; // building address

    public Unit(){
        ++unitId;
    }

    public Unit(int propId, int tenantId, String unitName) {
        ++unitId;
        this.propId = propId;
        this.tenantId = tenantId;
        this.unitName = unitName;
    }

    public int getUnitId() {
        return unitId;
    }

    public int getPropId() {
        return propId;
    }

    public void setPropId(int propId) {
        this.propId = propId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
