package com.chinaunicom.torn.mcloud.message;

import com.chinaunicom.torn.mcloud.entity.ProjectEntity;

import java.util.Date;
import java.util.List;

public class BatchIPMessage {

    private List<Integer> ipList;
    private boolean enabled;

    public List<Integer> getIpList() {
        return ipList;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setIpList(List<Integer> ipList) {
        this.ipList = ipList;
    }
}
