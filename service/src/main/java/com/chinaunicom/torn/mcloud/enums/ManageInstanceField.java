package com.chinaunicom.torn.mcloud.enums;

public enum ManageInstanceField {

    HARDWARE("hardware"),
    NET_AREA("netarea"),
    VLAN_ID("vlanid"),
    PROJECT_ID("project_id"),
    OOB_USERNAME("oob_username"),
    OOB_PASSWORD("oob_password"),

    OOB_IP("oob_ip"),

    SN("sn");

    private final String field;

    private ManageInstanceField(String field) {
        this.field = field;
    }

    public String getField() {
        return this.field;
    }
}
