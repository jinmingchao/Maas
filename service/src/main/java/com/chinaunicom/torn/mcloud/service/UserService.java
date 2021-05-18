package com.chinaunicom.torn.mcloud.service;

import com.chinaunicom.torn.mcloud.entity.ProjectEntity;
import com.chinaunicom.torn.mcloud.entity.UserInfoEntity;
import com.chinaunicom.torn.mcloud.entity.WorkGroupEntity;

import java.util.List;

public interface UserService {
    List<UserInfoEntity> getAllUser();

    String getCurrentUser();

    void addGroup(String name);

    void deleteGroup(Integer id);

    List<WorkGroupEntity> getAllGroup();

    List<String> getGroupMemeber(Integer id);

    List<WorkGroupEntity> getUserBelongGroup(String username);

    void deleteGroupMember(Integer id, String username);

    void addGroupMemeber(Integer id, String username);

    boolean existGroupMemeber(Integer id, String username);

    List<ProjectEntity> getGroupProject(Integer id);

    List<ProjectEntity> getUserProject(String username);

    void addGroupProject(Integer id, Integer pid);

    void deleteGroupProject(Integer id, Integer pid);
}
