package com.chinaunicom.torn.mcloud.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.entity.PolicyEntity;
import com.chinaunicom.torn.mcloud.entity.ProjectCloudbootHardwareEntity;
import com.chinaunicom.torn.mcloud.entity.ProjectEntity;
import com.chinaunicom.torn.mcloud.message.CreateProjectMessage;
import com.chinaunicom.torn.mcloud.service.LoggerService;
import com.chinaunicom.torn.mcloud.service.PermissionService;
import com.chinaunicom.torn.mcloud.service.ProjectService;

import com.chinaunicom.torn.mcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/project")
public class ProjectController {
    private static LogEntityFactory logFactory = new LogEntityFactory(ProjectController.class);

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public String createProject(@RequestBody CreateProjectMessage message) {
        this.loggerService.operationLog(ProjectController.logFactory.product().how("webapi").what("create project[" + message.getName() + "]: /api/project").build());

        ProjectEntity curProject = this.projectService.getProjectDao().saveAndFlush(message.generateProjectEntity());
        this.permissionService.addUserPolicy(
                new PolicyEntity(
                        this.userService.getCurrentUser(),
                        "PROJECT-" + curProject.getId(),
                        "read"));
        return "{}";
    }

    @PutMapping(path = "/{PROJECT_ID}")
    public String modifyProject(@PathVariable(name = "PROJECT_ID") Integer projectId, @RequestBody CreateProjectMessage message) {
        this.loggerService.operationLog(ProjectController.logFactory.product().how("webapi").what("modify project [" + message.getName() + "]: /api/project/" + projectId).build());

        Optional<ProjectEntity> entity = this.projectService.getProjectDao().findById(projectId);
        if (!entity.isPresent()) {
            return "{}";
        }

        entity.get().setName(message.getName());
        entity.get().setDescription(message.getDescription());

        this.projectService.getProjectDao().saveAndFlush(entity.get());
        return "{}";
    }

    @GetMapping
    public List<ProjectEntity> getProjects() {
        return this.projectService.getProjectDao().findAll()
                .stream()
//                .filter(project ->
//                        this.permissionService.checkPermission(
//                                new PolicyEntity(
//                                        this.userService.getCurrentUser(),
//                                        "PROJECT-" + project.getId(),
//                                        "read"
//                                )))
                .sorted((o1, o2) -> o2.getId().compareTo(o1.getId()))
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/{PROJ_ID}/hardware")
    public String appendHardware(@PathVariable(name = "PROJ_ID") Integer projId, @RequestBody List<Integer> hardwareId) {

        this.projectService.getProjectCloudbootHardwareDao().saveAll(hardwareId
                .stream()
                .map(id -> {
                    ProjectCloudbootHardwareEntity entity = new ProjectCloudbootHardwareEntity(projId, id);

                    Optional<ProjectCloudbootHardwareEntity> existed = this.projectService.getProjectCloudbootHardwareDao().findOne(Example.of(entity));
                    if (existed.isPresent()) {
                        return existed.get();
                    }

                    return entity;
                })
                .collect(Collectors.toList()));
        this.projectService.getProjectCloudbootHardwareDao().flush();

        return "{}";
    }
}
