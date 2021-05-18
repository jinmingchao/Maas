package com.chinaunicom.torn.mcloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.component.SecurityUtil;
import com.chinaunicom.torn.mcloud.entity.AddResourceResEntity;
import com.chinaunicom.torn.mcloud.entity.CasbinHasPermissionEntity;
import com.chinaunicom.torn.mcloud.entity.PolicyEntity;
import com.chinaunicom.torn.mcloud.service.PermissionService;
import com.chinaunicom.torn.mcloud.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(value = "PermissionController", tags = ("权限管理"))
@RestController
@RequestMapping("api/permission/")
public class PermissionController {
    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "获取所有角色")
    @GetMapping("/roles")
    public List<String> getRoles() {
        return this.permissionService.getRoles();
    }

    @ApiOperation(value = "获取某角色的用户")
    @ApiParam(name = "role", value = "role id", required = true)
    @GetMapping("/users/{role}")
    public List<String> getUsersForRole(@PathVariable(name = "role") String role) {
        return this.permissionService.getUserForRole(role);
    }

    @ApiOperation(value = "获取所有角色权限")
    @GetMapping("/role/per")
    public List<PolicyEntity> getAllRolePolicy() {
        return this.permissionService.getAllRolePolicy()
                .stream()
                .map(policy -> new PolicyEntity(policy.get(0), policy.get(1), policy.get(2)))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "给角色添加权限")
    @PutMapping("/role/per")
    public Boolean addPer(@RequestBody String data) {
        JSONObject body = JSONObject.parseObject(data);
        return this.permissionService.addRolePolicy(new PolicyEntity(body.getString("sub"), body.getString("obj"),
                body.getString("act")));
    }

    @ApiOperation(value = "给用户添加权限")
    @PutMapping("/user/policy")
    public Boolean addUserPer(@RequestBody String data) {
        JSONObject body = JSONObject.parseObject(data);
        return this.permissionService.addUserPolicy(new PolicyEntity(body.getString("sub"), body.getString("obj"),
                body.getString("act")));
    }

    @ApiOperation(value = "删除角色的权限")
    @DeleteMapping("/role/per")
    public Boolean deletePer(
            @RequestParam String sub,
            @RequestParam String obj,
            @RequestParam String act) {
        return this.permissionService.removeRolePolicy(new PolicyEntity(sub, obj, act));
    }

    @ApiOperation(value = "删除某个角色")
    @ApiParam(name = "role", value = "role id", required = true)
    @DeleteMapping("/role/del/{role}")
    public Boolean deleteRole(@PathVariable(name = "role") String role) {
        return this.permissionService.delRoles(role);
    }

    @GetMapping("/role/per/{role}")
    public List<List<String>> getPerForRole(@PathVariable(name = "role") String role) {
        return this.permissionService.getPerForRole(role);
    }

    @PutMapping("/user/per")
    public Boolean addRoleForUser(@RequestBody String data) {
        JSONObject body = JSONObject.parseObject(data);
        return this.permissionService.addRoleForUser(body.getString("user"), body.getString("role"));
    }

    @DeleteMapping("/user/per")
    public Boolean delRoleForUser(@RequestParam(name = "user") String user, @RequestParam(name = "role") String role) {
        return this.permissionService.delRoleForUser(user, role);
    }

//    @GetMapping("/role/per/{role}")
//    public List<List<String>> getPerForRole(@PathVariable(name = "role") String role) {
//        return this.permissionService.getPerForRole(role);
//    }

    @GetMapping("/user/per/{user}}")
    public List<PolicyEntity> getPerForUser(@PathVariable(name = "user") String user) {
        return this.permissionService.getPerForUser(user);
    }

    @GetMapping("/user/role/{user}")
    public List<String> getRoleForUser(@PathVariable(name = "user") String user) {
        return this.permissionService.getRolesForUser(user);
    }

    @PostMapping("/check-permission")
    public Boolean checkPermission(@RequestBody String data) {
        JSONObject body = JSONObject.parseObject(data);
        return this.permissionService.checkPermission(new PolicyEntity(body.getString("sub"),
                body.getString("obj"), body.getString("act")));
    }

    @PostMapping("/check-multi-permission")
    public List<CasbinHasPermissionEntity> checkMultiPermission(@RequestBody String data) {
        JSONObject body = JSONObject.parseObject(data);
        return body.getJSONArray("objs")
                .stream()
                .map(policy ->
                        new CasbinHasPermissionEntity(
                                policy.toString(),
                                this.permissionService.checkPermission(
                                        new PolicyEntity(
                                                this.userService.getCurrentUser(),
                                                policy.toString(),
                                                body.getString("act")))
                        )).collect(Collectors.toList());
    }

    @ApiOperation(value = "添加资源组资源")
    @PutMapping("/resource")
    public List<AddResourceResEntity> addResourceRole(@RequestBody String data) {
        JSONObject body = JSONObject.parseObject(data);
        String resourceGroup = body.getString("resourceGroup");
        return body.getJSONArray("resource")
                .stream()
                .map(resource ->
                        new AddResourceResEntity(resourceGroup,
                                resource.toString(),
                                this.permissionService.addResourceRole(resource.toString(), resourceGroup)))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "删除资源组资源")
    @DeleteMapping("/resource")
    public boolean delResourceFromGroup(
            @RequestParam(name = "resource") String resource,
            @RequestParam(name = "resourceGroup") String resourceGroup) {
        return this.permissionService.delResourceFromGroup(resource, resourceGroup);
    }

    @ApiOperation(value = "删除资源组")
    @DeleteMapping("/resource/del/{resourceRole}")
    public Boolean deleteResourceGroup(@PathVariable(name = "resourceRole") String resourceRole) {
        return this.permissionService.delResourceGroup(resourceRole);
    }

    @GetMapping("/resource")
    public List<String> getResourceRole() {
        return this.permissionService.getResourceRole();
    }

    @GetMapping("/resource/{resourceRole}")
    public List<String> getResourceForResourceRole(@PathVariable(name = "resourceRole") String resourceRole) {
        return this.permissionService.getResourceForResourceRole(resourceRole);
    }
}
