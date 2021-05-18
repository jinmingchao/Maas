package com.chinaunicom.torn.mcloud.rpc.cloudboot;

import com.alibaba.fastjson.annotation.JSONField;

public class CloudbootPageablePayload {

    public final static Integer MAX_LIMIT = 999999999;

    @JSONField(name = "Limit")
    private Integer limit;    
    @JSONField(name = "Offset")
    private Integer offset;

    public static CloudbootPageablePayload build() {
        return CloudbootPageablePayload.build(CloudbootPageablePayload.MAX_LIMIT);
    }

    public static CloudbootPageablePayload build(Integer limit) {
        return CloudbootPageablePayload.build(0, limit);
    }

    public static CloudbootPageablePayload build(Integer offset, Integer limit) {
        CloudbootPageablePayload payload = new CloudbootPageablePayload();

        payload.setOffset(offset);
        payload.setLimit(limit);

        return payload;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
