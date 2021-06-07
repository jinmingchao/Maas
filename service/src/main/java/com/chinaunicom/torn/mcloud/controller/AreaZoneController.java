package com.chinaunicom.torn.mcloud.controller;

import com.chinaunicom.torn.mcloud.entity.CloudbootAreaEntity;
import com.chinaunicom.torn.mcloud.entity.IPNetAreaEntity;
import com.chinaunicom.torn.mcloud.entity.IPPoolAreaEntity;
import com.chinaunicom.torn.mcloud.entity.IPTableEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.message.BatchIPMessage;
import com.chinaunicom.torn.mcloud.service.AreaZoneService;
import com.chinaunicom.torn.mcloud.service.LoggerService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import java.util.List;

@Api(value="AreaZoneController", tags = ("AZ管理"))
@RestController
@RequestMapping("api/area/")
public class AreaZoneController {
    private static LogEntityFactory logFactory = new LogEntityFactory(AreaZoneController.class);

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private AreaZoneService poolService;

    @ApiOperation(value = "获取所有 ")
    @GetMapping("fetch-cloud/list")
    public List<CloudbootAreaEntity> fetchAllCloud() {
        return this.poolService.fetchAllCloud();
    }

    @ApiOperation(value = "创建cloudArea* ")
    @PostMapping(path="post-area", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createCloudArea(@RequestBody CloudbootAreaEntity area) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/post-area").build());

        this.poolService.createCloudArea(area);
        return "ok";
    }

    @ApiOperation(value = "更新cloudArea*")
    @PutMapping(path="/put-area", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String updateCloudArea(@RequestBody CloudbootAreaEntity area) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/put-area").build());

        this.poolService.updateCloudArea(area);
        return "ok";
    }

    @ApiOperation(value = "删除cloudArea* ")
    @DeleteMapping(path="/delete-area/{areaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String deleteCloudArea(@PathVariable String areaId) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/delete-area/" + areaId).build());

        CloudbootAreaEntity area = new CloudbootAreaEntity();
        area.setId(areaId);
        this.poolService.deleteCloudArea(area);
        return "ok";
    }

    @ApiOperation(value = "获取所有的 网络区域 *")
    @ApiParam(name="areaId", value = "area id", required = true)
    @GetMapping("/fetch-net/list")
    public List<IPNetAreaEntity> fetchAllNetArea() {
        return this.poolService.fetchAllNetArea();
    }

    @ApiOperation(value = "根据areaId获取关联的 网络区域* ")
    @ApiParam(name="areaId", value = "area id", required = true)
    @GetMapping("/fetch-net/area/{areaId}/list")
    public List<IPNetAreaEntity> fetchNetArea(@PathVariable(name = "areaId") String areaId) {
        CloudbootAreaEntity cloud = new CloudbootAreaEntity();
        cloud.setId(areaId);
        return this.poolService.fetchNetArea(cloud);
    }

    @ApiOperation(value = "创建网络区域* ")
    @PostMapping(path="/post-net", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createNetArea(@RequestBody IPNetAreaEntity net) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/post-net/").build());

        this.poolService.createNetArea(net);
        return "ok";
    }

    @ApiOperation(value = "更新网络区域*")
    @PutMapping(path="/put-net", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String updateNetArea(@RequestBody IPNetAreaEntity net) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/put-net/").build());

        this.poolService.updateNetArea(net);
        return "ok";
    }

    @ApiOperation(value = "删除网络区域* ")
    @DeleteMapping(path="/delete-net/{netId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String deleteNetArea(@PathVariable Integer netId) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/delete-net/" + netId).build());

        IPNetAreaEntity net = new IPNetAreaEntity();
        net.setId(netId);
        this.poolService.deleteNetArea(net);
        return "ok";
    }

    @ApiOperation(value = "获取所有的 ip 池* ")
    @GetMapping("/fetch-ippool/list")
    public List<IPPoolAreaEntity> fetchAllPool() {
        return this.poolService.fetchAllPool();
    }

    @ApiOperation(value = "根据cloud area id 获取关联的 ip 池* ")
    @ApiParam(name="areaId", value = "areaId", required = true)
    @GetMapping("/fetch-ippool/area/{areaId}/list")
    public List<IPPoolAreaEntity> fetchPoolByArea(@PathVariable(name = "areaId") String areaId) {
        CloudbootAreaEntity area = new CloudbootAreaEntity();
        area.setId(areaId);
        return this.poolService.fetchPoolByArea(area);
    }

    @ApiOperation(value = "根据网络区域 id 获取关联的 ip 池* ")
    @ApiParam(name="netAsst", value = "cloudBoot id", required = true)
    @GetMapping("/fetch-ippool/net/{netAsst}/list")
    public List<IPPoolAreaEntity> fetchPoolByNet(@PathVariable(name = "netAsst") Integer netAsst) {
        IPNetAreaEntity net = new IPNetAreaEntity();
        net.setId(netAsst);
        return this.poolService.fetchPool(net);
    }

    @ApiOperation(value = "根据 ip池的id 获取 IP 列表* ")
    @ApiParam(name="poolId", value = "ip池id", required = true)
    @GetMapping("/fetch-ip/pool/{poolId}/list")
    public List<IPTableEntity> fetchIP(@PathVariable(name = "poolId") Integer poolId) {
        IPPoolAreaEntity pool = new IPPoolAreaEntity();
        pool.setId(poolId);
        return this.poolService.fetchIP(pool);
    }

    @ApiOperation(value = "根据 ip池的id 获取 已使用IP 列表* ")
    @ApiParam(name="poolId", value = "ip池id", required = true)
    @GetMapping("/fetch-ip/pool/{poolId}/list/used")
    public List<IPTableEntity> fetchUsedIP(@PathVariable(name = "poolId") Integer poolId) {
        IPPoolAreaEntity pool = new IPPoolAreaEntity();
        pool.setId(poolId);
        return this.poolService.fetchUsedIP(pool);
    }

    @ApiOperation(value = "根据 ip池的id 获取 未使用IP 列表* ")
    @ApiParam(name="poolId", value = "ip池id", required = true)
    @GetMapping("/fetch-ip/pool/{poolId}/list/unused")
    public List<IPTableEntity> fetchUnusedIP(@PathVariable(name = "poolId") Integer poolId) {
        IPPoolAreaEntity pool = new IPPoolAreaEntity();
        pool.setId(poolId);
        return this.poolService.fetchUnusedIP(pool);
    }

    @ApiOperation(value = "创建ip池* ")
    @PostMapping(path="/post-ippool", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createPool(@RequestBody IPPoolAreaEntity pool) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/post-ippool").build());

        this.poolService.createPool(pool);
        return "ok";
    }

    @ApiOperation(value = "更新ip池* ")
    @PutMapping(path="/put-ippool", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String updatePool(@RequestBody IPPoolAreaEntity pool) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/put-ippool").build());

        this.poolService.updatePool(pool);
        return "ok";
    }

    @ApiOperation(value = "删除ip池* ")
    @DeleteMapping(path="/delete-ippool/{poolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String deletePool(@PathVariable Integer poolId) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/delete-ippool").build());

        IPPoolAreaEntity pool = new IPPoolAreaEntity();
        pool.setId(poolId);
        this.poolService.deletePool(pool);
        return "ok";
    }

    @ApiOperation(value = "使用一个ip* ")
    @ApiParam(name="ipId", value = "ip id", required = true)
    @GetMapping("/set-ip/ip/{ipId}/enabled")
    public String setUseIP(@PathVariable(name = "ipId") Integer ipId) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/set-ip/ip/" + ipId + "/enabled").build());

        IPTableEntity ip = new IPTableEntity();
        ip.setId(ipId);
        this.poolService.setUsedIP(ip, "");
        return "ok";
    }

    @ApiOperation(value = "创建ip* ")
    @PostMapping(path="/post-ip", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createIP(@RequestBody IPTableEntity ip) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/post-ip").build());

        this.poolService.createIP(ip);
        return "ok";
    }

    @ApiOperation(value = "更新ip* ")
    @PutMapping(path="/put-ip", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String updateIP(@RequestBody IPTableEntity ip) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/put-ip").build());

        this.poolService.updateIP(ip);
        return "ok";
    }

    @ApiOperation(value = "批量更新ip* ")
    @PutMapping(path="/put-ip/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String batchUpdateIP(@RequestBody BatchIPMessage ip) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/put-ip/batch").build());

        System.out.println("---------");
        System.out.println(ip);
        this.poolService.batchUpdateIP(ip);
        return "ok";
    }

    @ApiOperation(value = "删除ip* ")
    @DeleteMapping(path="/delete-ip/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String deleteIP(@PathVariable Integer id) {
        this.loggerService.operationLog(AreaZoneController.logFactory.product().how("webapi").what("/api/area/delete-ip/" + id).build());

        IPTableEntity ip = new IPTableEntity();
        ip.setId(id);
        this.poolService.deleteIP(ip);
        return "ok";
    }
}
