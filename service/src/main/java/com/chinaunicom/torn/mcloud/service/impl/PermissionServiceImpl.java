package com.chinaunicom.torn.mcloud.service.impl;

import com.chinaunicom.torn.mcloud.component.EnforcerFactory;
import com.chinaunicom.torn.mcloud.entity.AddResourceResEntity;
import com.chinaunicom.torn.mcloud.entity.PolicyEntity;
import com.chinaunicom.torn.mcloud.service.PermissionService;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final Enforcer enforcer = EnforcerFactory.getEnforcer();

    @Override
    public boolean addRolePolicy(PolicyEntity policy) {
        boolean addPer = this.enforcer.addPolicy(policy.getSub(), policy.getObj(), policy.getAct());
        boolean addRole;
        if (this.enforcer.hasRoleForUser("admin", policy.getSub())) {
            addRole = true;
        } else {
            addRole = this.enforcer.addRoleForUser("admin", policy.getSub());
        }
        return addPer && addRole;
    }

    @Override
    public boolean addUserPolicy(PolicyEntity policy) {
        return this.enforcer.addPolicy(policy.getSub(), policy.getObj(), policy.getAct());
    }

    @Override
    public boolean removeRolePolicy(PolicyEntity policy) {
        return this.enforcer.removePolicy(policy.getSub(), policy.getObj(), policy.getAct());
    }

    @Override
    public List<String> getRoles() {
        this.enforcer.getGroupingPolicy();
        return this.enforcer
                .getAllRoles()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserForRole(String role) {
        return this.enforcer.getUsersForRole(role);
    }

    @Override
    public boolean delRoles(String role) {
        this.enforcer.deleteRole(role);
        return true;
    }

    @Override
    public boolean addRoleForUser(String user, String role) {
        return this.enforcer.addRoleForUser(user, role);
    }

    @Override
    public boolean delRoleForUser(String user, String role) {
        return this.enforcer.deleteRoleForUser(user, role);
    }

    @Override
    public List<String> getRolesForUser(String user) {
        return this.enforcer.getRolesForUser(user);
    }

    @Override
    public List<List<String>> getPerForRole(String role) {
        return this.enforcer.getPermissionsForUser(role);
    }

//    @Override
//    public List<List<String>> getPerForRole(String role) {
//        return this.enforcer.getPermissionsForUser(role);
//    }

    @Override
    public List<List<String>> getAllRolePolicy() {
        return this.enforcer.getPolicy();
    }

    @Override
    public boolean addResourceRole(String sub, String obj) {
        return this.enforcer.addNamedGroupingPolicy("g2", sub, obj);
    }

    @Override
    public boolean delResourceFromGroup(String sub, String obj) {
        return this.enforcer.removeNamedGroupingPolicy("g2", sub, obj);
    }

    @Override
    public boolean delResourceGroup(String resourceRole) {
        return this.enforcer.removeFilteredNamedGroupingPolicy("g2", 1, resourceRole);
    }

    @Override
    public List<String> getResourceRole() {
        return this.enforcer.getAllNamedRoles("g2")
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getResourceForResourceRole(String resourceRole) {
        return this.enforcer.getFilteredNamedGroupingPolicy("g2", 1, resourceRole)
                .stream()
                .map(resources -> resources.get(0)).collect(Collectors.toList());
    }

    @Override
    public boolean checkPermission(PolicyEntity policy) {
        return this.enforcer.enforce(policy.getSub(), policy.getObj(), policy.getAct());
    }

    @Override
    public List<PolicyEntity> getPerForUser(String user) {

        List<PolicyEntity> res = new ArrayList<>();

        this.enforcer.getRolesForUser(user)
                .forEach(role ->
                        this.enforcer.getPermissionsForUser(role)
                                .forEach(rolePolicy ->
                                        res.add(new PolicyEntity(rolePolicy.get(0), rolePolicy.get(1), rolePolicy.get(2)))));
        return res;
    }
}
