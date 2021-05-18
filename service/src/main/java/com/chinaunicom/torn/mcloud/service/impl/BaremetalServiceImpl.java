package com.chinaunicom.torn.mcloud.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAPIEnum;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddDeviceInstancePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddHardwarePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddOperationSystemPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddPXEPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootCancelInstallPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDeletePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDeviceInstanceInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDiscoveryInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootDiscoveryPageablePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootFetchDevicePageablePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootHardwareInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootHardwarePageablePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootLoginPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootNetworkInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootOperationPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootPageablePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootPxeInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootSystemInfo;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdateHardwarePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdateOperationSystemPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootUpdatePXEPayload;
import com.chinaunicom.torn.mcloud.service.BaremetalService;
import com.chinaunicom.torn.mcloud.service.LoggerService;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaremetalServiceImpl implements BaremetalService {

    private static LogEntityFactory logFactory = new LogEntityFactory(BaremetalServiceImpl.class);

    @Autowired
    private LoggerService logService;

    @Override
    public Optional<CloudbootTokenEntity> loginCloudboot(String host, String username, String password) {
        try {
            HttpPost post = this.buildCloudbootPost(host, CloudbootAPIEnum.LOGIN, new CloudbootLoginPayload(username, password));

            CloseableHttpResponse response = HttpClients.createDefault().execute(post);
            String resultString = EntityUtils.toString(response.getEntity());
            JSONObject result = JSONObject.parseObject(resultString);

            CloudbootTokenEntity entity = new CloudbootTokenEntity();
            entity.setHost(host);
            entity.setToken(result.getJSONObject("Content").getString("AccessToken"));
            Header[] headers = response.getHeaders("Set-Cookie");
            if (headers.length == 0) {
                return Optional.empty();
            }
            entity.setCookie(headers[0].getValue());

            return Optional.of(entity);
        }
        catch (Exception ex) {
            ex.printStackTrace();

            this.logService.error(BaremetalServiceImpl.logFactory.product()
                    .who(ServiceRole.PROMOTER).how(LogHow.CALL).what("RPC Cloudboot login API failed").why(ex.getMessage()).build());

            return Optional.empty();
        }
    }

    @Override
    public List<CloudbootPxeInfo> getPxeInfos(CloudbootTokenEntity token) {
        return this.listResult(
                CloudbootAPIEnum.PXE_LIST, token, CloudbootPageablePayload.build(),
                ServiceRole.CALLER.toString(), CloudbootPxeInfo.class);
    }

    @Override
    public List<CloudbootSystemInfo> getSystemInfos(CloudbootTokenEntity token) {
        return this.listResult(
                CloudbootAPIEnum.SYSTEM_LIST, token, CloudbootPageablePayload.build(),
                ServiceRole.CALLER.toString(), CloudbootSystemInfo.class);
    }

    @Override
    public List<CloudbootHardwareInfo> getHardwareInfos(CloudbootTokenEntity token) {
        return this.listResult(
                CloudbootAPIEnum.HARDWARE_LIST, token, CloudbootHardwarePageablePayload.build(),
                ServiceRole.CALLER.toString(), CloudbootHardwareInfo.class);
    }

    @Override
    public List<CloudbootDiscoveryInfo> getDiscoveryInfos(CloudbootTokenEntity token) {
        return this.listResult(
                CloudbootAPIEnum.DISCOVERY_INSTANCE_LIST, token, CloudbootDiscoveryPageablePayload.build(),
                ServiceRole.CALLER.toString(), CloudbootDiscoveryInfo.class);
    }

    @Override
    public List<CloudbootDeviceInstanceInfo> getDeviceInstanceInfos(CloudbootTokenEntity token) {
        List<CloudbootDeviceInstanceInfo> result = this.listResult(
                CloudbootAPIEnum.DEVICE_INSTANCE_LIST, token, CloudbootFetchDevicePageablePayload.build(),
                ServiceRole.CALLER.toString(), CloudbootDeviceInstanceInfo.class);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<CloudbootDeviceInstanceInfo> getDeviceInstanceInfos(CloudbootTokenEntity token, Set<String> snList) {
        CloudbootFetchDevicePageablePayload payload = CloudbootFetchDevicePageablePayload.build();
        payload.setKeyword(String.join("\n", snList));

        List<CloudbootDeviceInstanceInfo> result = this.listResult(
                CloudbootAPIEnum.DEVICE_INSTANCE_LIST, token, payload,
                ServiceRole.CALLER.toString(), CloudbootDeviceInstanceInfo.class);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<CloudbootNetworkInfo> getNetworkInfos(CloudbootTokenEntity token) {
        return this.listResult(
                CloudbootAPIEnum.NETWORK_LIST, token, CloudbootPageablePayload.build(),
                ServiceRole.CALLER.toString(), CloudbootNetworkInfo.class);
    }

    @Override
    public CloudbootResultStatusInfo setupMetalInstance(List<CloudbootAddDeviceInstancePayload> payloads, CloudbootTokenEntity token) {
        Map<Integer, CloudbootNetworkInfo> networkInfoMapper = this.getNetworkInfos(token)
            .stream()
            .reduce(new HashMap<Integer, CloudbootNetworkInfo>(),
                    (prev, curr) -> {
                        prev.put(curr.getId(), curr);
                        return prev;
                    },
                    (a, b) -> null);

        payloads.forEach(payload -> {
            payload.setUserId(0);
            long dhcpIp = 0L;
            int off = 24;
            for (String area : payload.getIp().split("\\.")) {
                dhcpIp += Integer.parseInt(area) << off;
                off -= 8;
            }

            final long finalDhcpIp = dhcpIp;
            Pattern networkPattern = Pattern.compile("^(?<network>(\\d+\\.){3}(\\d+))/(?<networklen>\\d+)$");
            networkInfoMapper.forEach((id, network) -> {
                if (payload.getNetworkId() != null) {
                    return;
                }

                Matcher matcher = networkPattern.matcher(network.getNetwork());
                if (!matcher.matches()) {
                    return;
                }

                String cidrIp = matcher.group("network");

                long cidr = 0L;
                int cidrOff = 24;
                for (String area : cidrIp.split("\\.")) {
                    cidr += Integer.parseInt(area) << cidrOff;
                    cidrOff -= 8;
                }

                long mask = 0L;
                cidrOff = 24;
                for (String area : network.getNetmask().split("\\.")) {
                    mask += Integer.parseInt(area) << cidrOff;
                    cidrOff -= 8;
                }

                if ((finalDhcpIp & mask) == cidr) {
                    payload.setNetworkId(id);
                    payload.setNetwork(network.getNetwork());
                }
            });

            if (payload.getNetworkId() == null && token.getDefaultCloudbootNetworkId() != null) {
                payload.setNetworkId(token.getDefaultCloudbootNetworkId());
                if (networkInfoMapper.containsKey(payload.getNetworkId())) {
                    payload.setNetwork(networkInfoMapper.get(payload.getNetworkId()).getNetwork());
                }
            }
        });

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.BATCH_ADD_DEVICE_INSTANCE, token, payloads,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);

        if (status.isPresent()) {
            if (!status.get().getStatus().equals("success")) {
                this.logService.error(BaremetalServiceImpl.logFactory.product()
                        .how(LogHow.CALL).what(String.format("HTTP RPC batchAdd failed")).why(status.get().getMessage()).build());
            }

            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo powerOn(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token) {
        return this.operation(CloudbootAPIEnum.BATCH_POWER_ON, operations, token);
    }

    @Override
    public CloudbootResultStatusInfo powerOff(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token) {
        return this.operation(CloudbootAPIEnum.BATCH_POWER_OFF, operations, token);
    }

    @Override
    public CloudbootResultStatusInfo restart(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token) {
        return this.operation(CloudbootAPIEnum.BATCH_RESTART, operations, token);
    }

    @Override
    public CloudbootResultStatusInfo deleteInstance(List<CloudbootDeletePayload> operations, CloudbootTokenEntity token) {
        operations.forEach(operation -> {
            operation.setAccessToken(token.getToken());
            operation.setUserId(0);
        });

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.BATCH_DELETE_DEVICE_INSTANCE, token, operations,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);

        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo restartFromPXE(List<CloudbootOperationPayload> operations, CloudbootTokenEntity token) {
        return this.operation(CloudbootAPIEnum.BATCH_RESTART_FROM_PXE, operations, token);
    }

    @Override
    public CloudbootResultStatusInfo cancelInstall(List<CloudbootCancelInstallPayload> payloads, CloudbootTokenEntity token) {
        payloads.forEach(payload -> {
            payload.setAccessToken(token.getToken());
            payload.setUserId(0);
        });

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.BATCH_CANCEL_INSTALL, token, payloads,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);
        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo addPxe(CloudbootAddPXEPayload payload, CloudbootTokenEntity token) {
        payload.setAccessToken(token.getToken());

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.ADD_PXE, token, payload,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);
        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo addOperationSystem(CloudbootAddOperationSystemPayload payload, CloudbootTokenEntity token) {
        payload.setAccessToken(token.getToken());

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.ADD_OPERATION_SYSTEM, token, payload,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);
        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo updatePxe(CloudbootUpdatePXEPayload payload, CloudbootTokenEntity token) {
        payload.setAccessToken(token.getToken());

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.UPDATE_PXE, token, payload,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);
        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo updateOperationSystem(CloudbootUpdateOperationSystemPayload payload, CloudbootTokenEntity token) {
        payload.setAccessToken(token.getToken());

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.UPDATE_OPERATION_SYSTEM, token, payload,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);
        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo addHardware(CloudbootAddHardwarePayload payload, CloudbootTokenEntity token) {
        payload.setAccessToken(token.getToken());

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.ADD_HARDWARE, token, payload,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);
        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    @Override
    public CloudbootResultStatusInfo updateHardware(CloudbootUpdateHardwarePayload payload, CloudbootTokenEntity token) {
        payload.setAccessToken(token.getToken());

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                CloudbootAPIEnum.UPDATE_HARDWARE, token, payload,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);
        if (status.isPresent()) {
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            return internalErrorStatus;
        }
    }

    private CloudbootResultStatusInfo operation(CloudbootAPIEnum api, List<CloudbootOperationPayload> operations, CloudbootTokenEntity token) {
        operations.forEach(operation -> {
            operation.setAccessToken(token.getToken());
            operation.setUserId(0);
        });

        this.logService.info(BaremetalServiceImpl.logFactory.product()
                .how(LogHow.CALL).what(String.format("HTTP RPC operation: %s", api.name())).build());

        Optional<CloudbootResultStatusInfo> status = this.singleResult(
                api, token, operations,
                ServiceRole.CALLER.toString(), CloudbootResultStatusInfo.class);

        if (status.isPresent()) {

            this.logService.info(BaremetalServiceImpl.logFactory.product()
                    .how(LogHow.CALL).what(String.format("HTTP RPC operation: %s result: %s", api.name(), status.get().getMessage())).build());
            return status.get();
        }
        else {
            CloudbootResultStatusInfo internalErrorStatus = new CloudbootResultStatusInfo();

            internalErrorStatus.setStatus("error");
            internalErrorStatus.setMessage("mcloud internal service error");

            this.logService.info(BaremetalServiceImpl.logFactory.product()
                    .how(LogHow.CALL).what(String.format("HTTP RPC operation: %s failed", api.name())).why(internalErrorStatus.getMessage()).build());

            return internalErrorStatus;
        }
    }

    private <T, R> Optional<T> singleResult(CloudbootAPIEnum api, CloudbootTokenEntity token, R payload, String who, Class<T> clazz) {
        try {
            return Optional.of(JSONObject.parseObject(this.execute(api, token, payload).toJSONString(), clazz));
        }
        catch (Exception ex) {
            ex.printStackTrace();

            this.logService.error(BaremetalServiceImpl.logFactory.product()
                    .who(who).how(LogHow.CALL).what(String.format("HTTP POST url: %s failed", api.url(token.getHost()))).why(ex.getMessage()).build());

            return Optional.empty();
        }
    }

    private <T> List<T> listResult(CloudbootAPIEnum api, CloudbootTokenEntity token, CloudbootPageablePayload pageable, String who, Class<T> clazz) {
        try {
            JSONArray content = this.execute(api, token, pageable).getJSONObject("Content").getJSONArray("list");
            if (content != null) {
                return content.toJavaList(clazz);
            }
            else {
                return new ArrayList<>(0);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();

            this.logService.error(BaremetalServiceImpl.logFactory.product()
                    .who(who).how(LogHow.CALL).what(String.format("HTTP POST url: %s failed", api.url(token.getHost()))).why(ex.getMessage()).build());

            return new ArrayList<>(0);
        }
    }

    private <T> JSONObject execute(CloudbootAPIEnum api, CloudbootTokenEntity token, T payload) throws IOException {
        HttpPost post = this.buildCloudbootPost(token.getHost(), api, payload);

        post.setHeader("Cookie", token.getCookie());
        post.setHeader("osinstallAuthAccessToken", token.getToken());
        post.setHeader("AccessToken", token.getToken());

        CloseableHttpResponse response = HttpClients.createDefault().execute(post);
        String resultString = EntityUtils.toString(response.getEntity());

        return JSONObject.parseObject(resultString);
    }

    private <T> HttpPost buildCloudbootPost(String host, CloudbootAPIEnum api, T payload) throws UnsupportedEncodingException {
        String url = api.url(host);
        String serializedPayload = JSONObject.toJSONString(payload);
        HttpPost result = new HttpPost(url);
        result.setHeader("Content-Type", "application/json;charset=utf-8");

        result.setEntity(new StringEntity(serializedPayload, "utf-8"));

        this.logService.info(BaremetalServiceImpl.logFactory.product()
                .how(LogHow.CALL).what(String.format("HTTP POST url: %s, payload: %s", url, serializedPayload)).build());

        return result;
    }
}
