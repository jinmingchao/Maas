package com.chinaunicom.torn.mcloud.service;

import java.util.List;
import com.chinaunicom.torn.mcloud.entity.PolicyEntity;

public interface PermissionService {
    /**
     * 添加角色权限
     *
     * @param policy
     * @return
     */
    boolean addRolePolicy(PolicyEntity policy);

    boolean addUserPolicy(PolicyEntity policy);

    /**
     * 删除角色权限
     *
     * @param policy
     * @return
     */
    boolean removeRolePolicy(PolicyEntity policy);

    /**
     * 获取所有角色权限
     *
     * @return
     */
    List<List<String>> getAllRolePolicy();

//    /**
//     * 获取角色权限
//     *
//     * @param user
//     * @return
//     */
    // List<List<String>> getPerForUser(String user);

    List<List<String>> getPerForRole(String role);

    /**
     * 获取所有角色
     *
     * @return
     */
    List<String> getRoles();

    /**
     * 获取拥有某角色的用户
     *
     * @param role
     * @return
     */
    List<String> getUserForRole(String role);

    /**
     * 删除角色
     *
     * @param role
     * @return
     */
    boolean delRoles(String role);

    /**
     * 添加用户角色
     *
     * @param user
     * @param role
     * @return
     */
    boolean addRoleForUser(String user, String role);

    /**
     * 删除用户角色
     *
     * @param user
     * @param role
     * @return
     */
    boolean delRoleForUser(String user, String role);

    /**
     * 获取用户角色
     *
     * @return
     */
    List<String> getRolesForUser(String user);

    List<PolicyEntity> getPerForUser(String user);

    boolean checkPermission(PolicyEntity policy);

    boolean addResourceRole(String sub, String obj);

    boolean delResourceFromGroup(String sub, String obj);

    List<String> getResourceRole();

    List<String> getResourceForResourceRole(String resourceRole);

    boolean delResourceGroup(String obj);
}
