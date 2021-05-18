package com.chinaunicom.torn.mcloud.controller;

import com.chinaunicom.torn.mcloud.component.SecurityUtil;
import com.chinaunicom.torn.mcloud.entity.CurrentUserEntity;
import com.chinaunicom.torn.mcloud.entity.ProjectEntity;
import com.chinaunicom.torn.mcloud.entity.UserInfoEntity;
import com.chinaunicom.torn.mcloud.entity.WorkGroupEntity;
import com.chinaunicom.torn.mcloud.service.PermissionService;
import com.chinaunicom.torn.mcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping
    public List<UserInfoEntity> getAllUser() {
        return this.userService.getAllUser();
    }

    @GetMapping(path = "/whoami")
    public CurrentUserEntity whoami() {
        String username = this.userService.getCurrentUser();
        List<String> currentUserRoles = this.permissionService.getRolesForUser(username);
        return new CurrentUserEntity(username, currentUserRoles);
    }

    @GetMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        this.securityUtil.logout();

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header("Location", "/cmdb/torn-mcloud/")
                .build();
    }

    @GetMapping(path = "/group")
    public List<WorkGroupEntity> getAllGroup() {
        return this.userService.getAllGroup();
    }

    @PostMapping(path = "/group/{NAME}")
    public Boolean addGroup(@PathVariable(name = "NAME") String name) {
        String operator = this.userService.getCurrentUser();

        if (operator.equals("admin")) {
            this.userService.addGroup(name);
            return true;
        }

        return false;
    }

    @DeleteMapping(path = "/group/{ID}")
    public Boolean removeGroup(@PathVariable(name = "ID") Integer id) {
        String operator = this.userService.getCurrentUser();

        if (operator.equals("admin")) {
            this.userService.deleteGroup(id);
            return true;
        }

        return false;
    }

    @GetMapping(path = "/group/{ID}/member")
    public List<String> getGroupMember(@PathVariable(name = "ID") Integer id) {
        return this.userService.getGroupMemeber(id);
    }

    @DeleteMapping(path = "/group/{ID}/{USERNAME}")
    public Boolean removeGroupMemeber(@PathVariable(name = "ID") Integer id, @PathVariable(name = "USERNAME") String username) {
        String operator = this.userService.getCurrentUser();

        if (operator.equals("admin") || this.userService.existGroupMemeber(id, operator)) {
            this.userService.deleteGroupMember(id, username);

            return true;
        }

        return false;
    }

    @PostMapping(path = "/group/{ID}/{USERNAME}")
    public Boolean addGroupMemeber(@PathVariable(name = "ID") Integer id, @PathVariable(name = "USERNAME") String username) {
        String operator = this.userService.getCurrentUser();

        if (operator.equals("admin") || this.userService.existGroupMemeber(id, operator)) {
            this.userService.addGroupMemeber(id, username);

            return true;
        }

        return false;
    }

    @GetMapping(path = "/group/{ID}/project")
    public List<ProjectEntity> getGroupProject(@PathVariable(name = "ID") Integer id) {
        return this.userService.getGroupProject(id);
    }

    @PostMapping(path = "/group/{ID}/project/{PID}")
    public Boolean addGroupProject(@PathVariable(name = "ID") Integer id, @PathVariable(name = "PID") Integer pid) {
        String operator = this.userService.getCurrentUser();

        if (operator.equals("admin")) {
            this.userService.addGroupProject(id, pid);

            return true;
        }

        return false;
    }

    @DeleteMapping(path = "/group/{ID}/project/{PID}")
    public Boolean deleteGroupProject(@PathVariable(name = "ID") Integer id, @PathVariable(name = "PID") Integer pid) {
        String operator = this.userService.getCurrentUser();

        if (operator.equals("admin")) {
            this.userService.deleteGroupProject(id, pid);

            return true;
        }

        return false;
    }

}
