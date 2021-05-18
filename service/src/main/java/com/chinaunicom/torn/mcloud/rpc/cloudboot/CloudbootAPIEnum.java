package com.chinaunicom.torn.mcloud.rpc.cloudboot;

public enum CloudbootAPIEnum {
    LOGIN("/api/osinstall/v1/user/login"),
    DISCOVERY_INSTANCE_LIST("/api/osinstall/v1/device/scan/list"),
    DEVICE_INSTANCE_LIST("/api/osinstall/v1/device/list"),

    BATCH_ADD_DEVICE_INSTANCE("/api/osinstall/v1/device/batchAdd"),
    BATCH_CANCEL_INSTALL("/api/osinstall/v1/device/batchCancelInstall"),

    BATCH_POWER_ON("/api/osinstall/v1/device/batchPowerOn"),
    BATCH_POWER_OFF("/api/osinstall/v1/device/batchPowerOff"),
    BATCH_RESTART("/api/osinstall/v1/device/batchReStart"),
    BATCH_RESTART_FROM_PXE("/api/osinstall/v1/device/batchStartFromPxe"),

    BATCH_DELETE_DEVICE_INSTANCE("/api/osinstall/v1/device/batchDelete"),

    ADD_PXE("/api/osinstall/v1/osConfig/add"),
    UPDATE_PXE("/api/osinstall/v1/osConfig/update"),
    ADD_OPERATION_SYSTEM("/api/osinstall/v1/systemConfig/add"),
    UPDATE_OPERATION_SYSTEM("/api/osinstall/v1/systemConfig/update"),
    ADD_HARDWARE("/api/osinstall/v1/hardware/add"),
    UPDATE_HARDWARE("/api/osinstall/v1/hardware/update"),

    NETWORK_LIST("/api/osinstall/v1/network/list"),
    PXE_LIST("/api/osinstall/v1/osConfig/list"),
    SYSTEM_LIST("/api/osinstall/v1/systemConfig/list"),
    HARDWARE_LIST("/api/osinstall/v1/hardware/list");

    private final String uri;

    private CloudbootAPIEnum(String uri) {
        this.uri = uri;
    }

    public String url(String host) {
        return String.format("%s%s", host, this.uri);
    }
};
