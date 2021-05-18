package com.chinaunicom.torn.mcloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.config.LoginRedirectConfig;
import com.chinaunicom.torn.mcloud.dao.ProjectDao;
import com.chinaunicom.torn.mcloud.dao.UserGroupDao;
import com.chinaunicom.torn.mcloud.dao.UserGroupProjectDao;
import com.chinaunicom.torn.mcloud.dao.WorkGroupDao;
import com.chinaunicom.torn.mcloud.entity.ProjectEntity;
import com.chinaunicom.torn.mcloud.entity.UserGroupEntity;
import com.chinaunicom.torn.mcloud.entity.UserGroupProjectEntity;
import com.chinaunicom.torn.mcloud.entity.UserInfoEntity;
import com.chinaunicom.torn.mcloud.entity.WorkGroupEntity;
import com.chinaunicom.torn.mcloud.service.UserService;
import com.google.common.collect.Lists;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private LoginRedirectConfig loginConfig;

    @Autowired
    private WorkGroupDao workGroupDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private UserGroupProjectDao userGroupProjectDao;

    @Autowired
    private ProjectDao projectDao;

    @Override
    public List<UserInfoEntity> getAllUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        if (this.loginConfig.getDev()) {
            List<String> userList = Lists.newArrayList("Alice", "Bob", "Cindy", "Dale", "Eric");
            return userList
                    .stream()
                    .map(user -> {
                        UserInfoEntity tempEntity = new UserInfoEntity();
                        tempEntity.setUsername(user);
                        tempEntity.setEmail(user+"@chinaunicom.cn");
                        tempEntity.setName(user);
                        tempEntity.setPhone("18500660110");
                        return tempEntity;
                    })
                    .collect(Collectors.toList());
        }

        String token = "";
        if (session.getAttribute("BKToken") != null) {
            token = session.getAttribute("BKToken").toString();
        }

        CloseableHttpClient client = HttpClients.custom().build();
        HttpGet getMethod = new HttpGet(String.format("%sapi/v2/get_all_users?%s=%s", this.loginConfig.getApi(), this.loginConfig.getCookieKey(), token));

        try {
            CloseableHttpResponse response = client.execute(getMethod);

            String responseString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            JSONObject responseJson = JSONObject.parseObject(responseString);

            return responseJson.getJSONArray("data").toJavaList(UserInfoEntity.class);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public void addGroup(String name) {
        WorkGroupEntity entity = new WorkGroupEntity();
        entity.setName(name);

        this.workGroupDao.saveAndFlush(entity);
    }

    @Override
    public void deleteGroup(Integer id) {
        this.workGroupDao.deleteById(id);
    }

    @Override
    public List<WorkGroupEntity> getAllGroup() {
        return this.workGroupDao.findAll();
    }

    @Override
    public List<String> getGroupMemeber(Integer id) {
        UserGroupEntity filter = new UserGroupEntity();
        filter.setGroupId(id);

        return this.userGroupDao.findAll(Example.of(filter)).stream().map(e -> e.getUsername()).collect(Collectors.toList());
    }

    @Override
    public List<WorkGroupEntity> getUserBelongGroup(String username) {
        UserGroupEntity filter = new UserGroupEntity();
        filter.setUsername(username);

        Set<Integer> groupId = this.userGroupDao.findAll(Example.of(filter)).stream().map(e -> e.getGroupId()).collect(Collectors.toSet());

        return this.workGroupDao.findAllById(groupId);
    }

    @Override
    public void deleteGroupMember(Integer id, String username) {
        UserGroupEntity filter = new UserGroupEntity();
        filter.setGroupId(id);
        filter.setUsername(username);

        Optional<UserGroupEntity> deleteEntity = this.userGroupDao.findOne(Example.of(filter));
        if (deleteEntity.isPresent()) {
            this.userGroupDao.deleteById(deleteEntity.get().getId());
        }
    }

    @Override
    public void addGroupMemeber(Integer id, String username) {
        UserGroupEntity entity = new UserGroupEntity();
        entity.setGroupId(id);
        entity.setUsername(username);

        if (this.userGroupDao.exists(Example.of(entity))) {
            return;
        }

        this.userGroupDao.saveAndFlush(entity);
    }

    @Override
    public boolean existGroupMemeber(Integer id, String username) {
        UserGroupEntity entity = new UserGroupEntity();
        entity.setGroupId(id);
        entity.setUsername(username);

        return this.userGroupDao.exists(Example.of(entity));
    }

    @Override
    public List<ProjectEntity> getGroupProject(Integer id) {
        UserGroupProjectEntity filter = new UserGroupProjectEntity();
        filter.setGroupId(id);

        List<Integer> projectId = this.userGroupProjectDao.findAll(Example.of(filter)).stream().mapToInt(e -> e.getProjectId()).boxed().collect(Collectors.toList());
        return this.projectDao.findAllById(projectId);
    }

    @Override
    public List<ProjectEntity> getUserProject(String username) {
        UserGroupProjectEntity filter = new UserGroupProjectEntity();
        filter.setUsername(username);

        Set<Integer> projectId = this.userGroupProjectDao.findAll(Example.of(filter)).stream().mapToInt(e -> e.getProjectId()).boxed().collect(Collectors.toSet());

        this.getUserBelongGroup(username).forEach(group -> {
            UserGroupProjectEntity subFilter = new UserGroupProjectEntity();
            subFilter.setGroupId(group.getId());

            Set<Integer> subProjectId = this.userGroupProjectDao.findAll(Example.of(subFilter)).stream().mapToInt(e -> e.getProjectId()).boxed().collect(Collectors.toSet());

            projectId.addAll(subProjectId);
        });

        return this.projectDao.findAllById(projectId);
    }

    @Override
    public void addGroupProject(Integer id, Integer pid) {
        UserGroupProjectEntity entity = new UserGroupProjectEntity();
        entity.setGroupId(id);
        entity.setProjectId(pid);

        if (this.userGroupProjectDao.exists(Example.of(entity))) {
            return;
        }

        this.userGroupProjectDao.saveAndFlush(entity);
    }

    @Override
    public void deleteGroupProject(Integer id, Integer pid) {
        UserGroupProjectEntity entity = new UserGroupProjectEntity();
        entity.setGroupId(id);
        entity.setProjectId(pid);

        Optional<UserGroupProjectEntity> deleteEntity = this.userGroupProjectDao.findOne(Example.of(entity));
        if (deleteEntity.isPresent()) {
            this.userGroupProjectDao.deleteById(deleteEntity.get().getId());
        }
    }
}
