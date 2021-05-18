package com.chinaunicom.torn.mcloud.entity;

public class PolicyEntity {

    private String sub;
    private String obj;
    private String act;

    public PolicyEntity(String sub, String obj, String act) {
        this.sub = sub;
        this.obj = obj;
        this.act = act;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getSub() {
        return sub;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getObj() {
        return obj;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getAct() {
        return act;
    }

    @Override
    public String toString() {
        return "Policy [sub=" + sub + ", obj=" + obj + ", act=" + act + "]";
    }
}
