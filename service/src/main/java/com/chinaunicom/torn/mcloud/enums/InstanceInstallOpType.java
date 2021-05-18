package com.chinaunicom.torn.mcloud.enums;

public enum InstanceInstallOpType {

    MANAGED("managed"),
    CREATE_BATCH("create_batch"),
    INSTALL("install"),
    INSTALL_SUCCESS("install_success"),
    INSTALL_FAILURE("install_failure"),
    RESET("reset"),
    UPDATE("update"),
    BACK("back"),
    CANCEL("cancel");

    private final String name;

    private InstanceInstallOpType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
