package com.chinaunicom.torn.mcloud.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.chinaunicom.torn.mcloud.dao.HistorySetupInstanceDao;
import com.chinaunicom.torn.mcloud.dao.InstanceDao;
import com.chinaunicom.torn.mcloud.dao.InstanceInstallOpLogDao;
import com.chinaunicom.torn.mcloud.dao.SetupDao;
import com.chinaunicom.torn.mcloud.dao.SetupInstanceDao;
import com.chinaunicom.torn.mcloud.entity.BatchInstallEntity;
import com.chinaunicom.torn.mcloud.entity.CloudbootTokenEntity;
import com.chinaunicom.torn.mcloud.entity.HistorySetupInstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstallInstanceEntity;
import com.chinaunicom.torn.mcloud.entity.InstanceEntity;
import com.chinaunicom.torn.mcloud.entity.LogEntityFactory;
import com.chinaunicom.torn.mcloud.entity.SetupEntity;
import com.chinaunicom.torn.mcloud.entity.SetupInstanceEntity;
import com.chinaunicom.torn.mcloud.enums.InstanceInstallOpType;
import com.chinaunicom.torn.mcloud.enums.LogHow;
import com.chinaunicom.torn.mcloud.enums.ServiceRole;
import com.chinaunicom.torn.mcloud.message.InstallResultMessage;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootAddDeviceInstancePayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootOperationPayload;
import com.chinaunicom.torn.mcloud.rpc.cloudboot.CloudbootResultStatusInfo;
import com.chinaunicom.torn.mcloud.service.AreaZoneService;
import com.chinaunicom.torn.mcloud.service.AuthenticationService;
import com.chinaunicom.torn.mcloud.service.BaremetalService;
import com.chinaunicom.torn.mcloud.service.InstallService;
import com.chinaunicom.torn.mcloud.service.InstallingService;
import com.chinaunicom.torn.mcloud.service.LoggerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class InstallServiceImpl implements InstallService {

    private static final LogEntityFactory logFactory = new LogEntityFactory(InstanceServiceImpl.class, ServiceRole.CALLER.name());

    private LinkedBlockingQueue<BatchInstallEntity> queue;
    private Thread thread; 
    private Runnable runner;

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private SetupDao setupDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private AreaZoneService areaZoneService;

    @Autowired
    private BaremetalService baremetalService;

    @Autowired
    private SetupInstanceDao setupInstanceDao;

    @Autowired
    private HistorySetupInstanceDao historySetupInstanceDao;

    @Autowired
    private InstallingService installingService;

    @Autowired
    private AreaZoneService poolService;

    @Autowired
    private InstanceInstallOpLogDao instanceInstallOpLogDao;

    public InstallServiceImpl() {
        this.queue = new LinkedBlockingQueue<>();
        final InstallServiceImpl installServiceSelf = this;
        this.runner = new Runnable() {

            @Override
            public void run() {
                installServiceSelf.installCheckRun();
            }
        };
        this.thread = new Thread(this.runner);
        this.thread.start();
    }


    @Override
    public void createBatch(String name, String areaId, List<InstallInstanceEntity> instances) throws InterruptedException {
        // ????????????
        SetupEntity setupEntity = new SetupEntity(name, areaId);
        this.setupDao.saveAndFlush(setupEntity);

        this.loggerService.info(logFactory.product()
                .how(LogHow.CALL).what(String.format("create batch: %s", name)).build());

        // ??????????????????
        this.setupInstanceDao.saveAll(instances
                .stream()
                .map(instance -> new SetupInstanceEntity(setupEntity.getId(), instance.getSn()))
                .collect(Collectors.toList()));
        this.setupInstanceDao.flush();

        this.historySetupInstanceDao.saveAll(instances
                .stream()
                .map(instance -> new HistorySetupInstanceEntity(setupEntity.getId(), instance.getSn(), "???????????????????????????????????????"))
                .collect(Collectors.toList()));
        this.historySetupInstanceDao.flush();

        // ????????????????????????
        this.asyncCheck(setupEntity.getId(), instances);
    }

    @Override
    public List<InstallResultMessage> install(List<String> sn) {
        // ??????????????????????????????????????????????????????????????????
        Map<String, List<CloudbootAddDeviceInstancePayload>> mapper = new HashMap<>();// key: areaId value: list<CloudbootAddDeviceInstancePayload>

        this.instanceDao.findAllById(sn)
            .stream()
            .filter(instance -> {
                if (!instance.getInstallable()) {
                    this.loggerService.info(logFactory.product()
                            .how(LogHow.CALL).what("uninstallable, ignore instance sn: " + instance.getSn() + " area: " + instance.getAreaId()).build());
                    instance.setStatus("????????????");
                    this.instanceDao.saveAndFlush(instance);//??????instance?????????????????????
                }
                return instance.getInstallable();
            })//????????????installable == true???
            .forEach(instance -> {
                if (!mapper.containsKey(instance.getAreaId())) {
                    this.loggerService.info(logFactory.product()
                            .how(LogHow.CALL).what("install newly area").build());
                    mapper.put(instance.getAreaId(), new LinkedList<>());
                }

                this.loggerService.info(logFactory.product()
                        .how(LogHow.CALL).what("install newly instance sn: " + instance.getSn() + " area: " + instance.getAreaId()).build());
                mapper.get(instance.getAreaId()).add(new CloudbootAddDeviceInstancePayload(instance));
            });

        // ????????????????????????????????????
        List<InstallResultMessage> result = new ArrayList<>(mapper.size());
        mapper.forEach((areaKey, payloads) -> {
            Optional<CloudbootTokenEntity> token = this.authenticationService.getCloudbootToken(areaKey);
            if (!token.isPresent()) {

                this.loggerService.info(logFactory.product()
                        .how(LogHow.CALL).what("token not exist").build());
                return;
            }

            this.loggerService.info(logFactory.product()
                    .how(LogHow.CALL).what("setup metal instance").build());

            // ?????????????????? ?????????????????????
            payloads.forEach(payload -> {
                Optional<InstanceEntity> instance = this.instanceDao.findById(payload.getSn());
                List<CloudbootOperationPayload> restartPayload = new ArrayList<>(1);//????????????operationPayload??????,?????????sn,oobip,oob_un,oob_pwd
                restartPayload.add(instance.get().generateCloudbootOperationPayload());
                CloudbootResultStatusInfo restartStatus = this.baremetalService.restartFromPXE(restartPayload, token.get());//????????????
                if (!restartStatus.getStatus().equals("success")) {
                    if (instance.isPresent()) {//?????????????????????
                        instance.get().setInstallable(true);
                        instance.get().setInstalled(true);
                        instance.get().setStatus("????????????");

                        this.instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog("system", InstanceInstallOpType.INSTALL_FAILURE));
                        this.instanceDao.saveAndFlush(instance.get());//??????????????????
                    }
                    result.add(new InstallResultMessage(areaKey, restartStatus));//????????????????????????

                    return;
                }

                List<CloudbootAddDeviceInstancePayload> singlePayload = new ArrayList<>(1);
                singlePayload.add(payload);

                CloudbootResultStatusInfo resultStatusInfo = this.baremetalService.setupMetalInstance(singlePayload, token.get());
                if (!resultStatusInfo.getStatus().equals("success")) {
                    if (instance.isPresent()) {
                        instance.get().setInstallable(true);
                        instance.get().setInstalled(true);
                        instance.get().setStatus("????????????");

                        this.instanceInstallOpLogDao.saveAndFlush(instance.get().generateOpLog("system", InstanceInstallOpType.INSTALL_FAILURE));
                        this.instanceDao.saveAndFlush(instance.get());
                    }
                }

                result.add(new InstallResultMessage(areaKey, resultStatusInfo));//????????????????????????
            });
            
            List<InstanceEntity> installed = this.instanceDao.findAllById(payloads.stream().map(payload -> payload.getSn()).collect(Collectors.toList()));
            installed.forEach(installedInstance -> {
                // ????????????????????????????????????????????????
                //installedInstance.setInstallable(false);
                installedInstance.setStatus("????????????");

                this.installingService.addInstance(installedInstance);//??????
            });
            this.instanceDao.saveAll(installed);
            this.instanceDao.flush();

            // ????????????????????????
            //CloudbootResultStatusInfo resultStatusInfo = this.baremetalService.setupMetalInstance(payloads, token.get());

            //if (!resultStatusInfo.getStatus().equals("success")) {
                //List<InstanceEntity> failedInstance = this.instanceDao.findAllById(payloads.stream().map(payload -> payload.getSn()).collect(Collectors.toList()));
                //installed.forEach(installedInstance -> {
                    //installedInstance.setInstallable(true);
                    //installedInstance.setInstalled(true);
                    //installedInstance.setStatus("????????????");

                    //this.instanceInstallOpLogDao.saveAndFlush(installedInstance.generateOpLog("system", InstanceInstallOpType.INSTALL_FAILURE));
                //});
                //this.instanceDao.saveAll(failedInstance);
                //this.instanceDao.flush();
            //}

            //result.add(new InstallResultMessage(areaKey, resultStatusInfo));
        });

        return result;
    }

    public void asyncCheck(Integer batchId, List<InstallInstanceEntity> instances) throws InterruptedException {
        // ??????????????????????????????vlanid?????????
        this.areaZoneService.ipPoolFillInstallInstance(instances);

        BatchInstallEntity installEntity = new BatchInstallEntity();
        installEntity.setBatchId(batchId);
        installEntity.setInstances(instances);

        this.queue.put(installEntity);
    }

    private void installCheckRun() {
        for ( ;; ) {
            try {
                // ????????????????????????????????????????????????
                BatchInstallEntity installEntity = this.queue.take();
                this.loggerService.info(logFactory.product()
                        .how(LogHow.TASK_QUEUE).what(String.format("process batch check install %d", installEntity.getBatchId())).build());

                // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????Area??????
                installEntity.getInstances().forEach(entity -> {
                    this.loggerService.info(logFactory.product()
                            .how(LogHow.TASK_QUEUE).what(String.format("check instance sn: [%s]", entity.getSn())).build());

                    Optional<InstanceEntity> instance = this.instanceDao.findById(entity.getSn());
                    if (!this.authenticationService.getCloudbootToken(entity.getAreaId()).isPresent()) {
                        this.loggerService.error(logFactory.product()
                                .how(LogHow.CALL).what(String.format("install sn: %s failed", entity.getSn())).why("Token not exist").build());

                        instance.get().setStatus("??????Token??????");
                        this.instanceDao.saveAndFlush(instance.get());
                        return;
                    }

                    instance.get().setInstallable(this.checkInstanceInstallable(instance, entity));

                    if (this.checkInstallEntity(instance, entity)) {
                        this.distribute(installEntity.getBatchId(), instance.get(), entity);
                    }

                    this.instanceDao.saveAndFlush(instance.get());
                });
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Boolean checkInstanceInstallable(Optional<InstanceEntity> instance, InstallInstanceEntity entity) {
        if (!this.poolService.getIpUsable(entity.getIppool(), entity.getInnerIp(), instance.get().getSn())) {
            this.loggerService.warn(logFactory.product()
                    .what("check instance installable failed sn: " + instance.get().getSn()).build());
            instance.get().setStatus("??????IP??????");
            return false;
        }
        this.poolService.setUsedIp(entity.getIppool(), entity.getInnerIp(), instance.get().getSn());

        return true;
    }

    private boolean checkInstallEntity(Optional<InstanceEntity> instance, InstallInstanceEntity entity) {
        if (!instance.isPresent()) {
            this.loggerService.warn(logFactory.product()
                    .how(LogHow.TASK_QUEUE).what(String.format("install check instance sn: %s failed", entity.getSn())).why("instance not exist").build());
            return false;
        }
        if (!instance.get().getDiscovery() || !instance.get().getManaged()) {
            this.loggerService.warn(logFactory.product()
                    .how(LogHow.TASK_QUEUE).what(String.format("install check instance sn: %s failed", entity.getSn())).why("not discovery or managed").build());
            instance.get().setStatus("????????????");
            return false;
        }
        if (instance.get().getDistributed() && !entity.getModified()) {
            this.loggerService.warn(logFactory.product()
                    .how(LogHow.TASK_QUEUE).what(String.format("install check instance sn: %s failed", entity.getSn())).why("distributed").build());
            instance.get().setStatus("??????????????????");
            return false;
        }

        return true;
    }

    private void distribute(Integer setupId, InstanceEntity instance, InstallInstanceEntity entity) {
        instance.transferSetup(setupId, entity);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        this.thread.interrupt();
    }

    @Override
    public Set<String> getBatchSNSet(Integer batchId) {
        SetupInstanceEntity filter = new SetupInstanceEntity();
        filter.setSetupId(batchId);

        return this.setupInstanceDao.findAll(Example.of(filter))
            .stream()
            .map(rel -> rel.getSn())
            .collect(Collectors.toSet());
    }

    @Override
    public SetupDao getSetupDao() {
        return this.setupDao;
    }
}
