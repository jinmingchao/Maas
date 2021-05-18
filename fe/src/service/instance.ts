import Axios from 'axios';
import {IDistribute, IModifyNetInfo, IModifyOobInfo} from 'umi';

export const getInstances = (area: string) => Axios.get(`api/instance/${area}`);

export const getInstancesByBatch = (area: string, batchId: number) => Axios.get(`api/instance/${area}?batchId=${batchId}`);

export const getInstancesByInstalling = (area: string) => Axios.get(`api/instance/${area}?installing=true`);

export const getInstanceHistories = (sn: string) => Axios.get(`api/instance/${sn}/history`);

export const postManageInstances = (area: string, instances: any[]) => Axios.post(`api/instance/${area}`, instances);

export const postDistribute = (data: IDistribute) => Axios.post('api/setup/distribute', data);

export const postInstall = (data: string[]) => Axios.post('api/setup/install', data);

export const putModifyInstanceHardware = (sn: string, id: number) => Axios.put(`api/instance/${sn}/hardware/${id}`);

export const putModifyInstancePXE = (sn: string, id: number) => Axios.put(`api/instance/${sn}/pxe/${id}`);

export const putModifyInstanceOperationSystem = (sn: string, id: number) => Axios.put(`api/instance/${sn}/operation_system/${id}`);

export const putModifyInstanceProject = (sn: string, id: number) => Axios.put(`api/instance/${sn}/project/${id}`);

export const putModifyNetInfo = (sn: string, info: IModifyNetInfo) => Axios.put(`api/instance/${sn}/net_info`, info);

export const putModifyOob = (sn: string, info: IModifyOobInfo) => Axios.put(`api/instance/${sn}/oob`, info);

export const putPowerOn = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/op/power_on`, sn);

export const putPowerOff = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/op/power_off`, sn);

export const putRestartFromDisk = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/op/restart_from_disk`, sn);

export const putRestartFromPXE = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/op/restart_from_pxe`, sn);

export const putInstancesReset = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/reset`, sn);

export const putInstancesRecoveryReset = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/recovery_reset`, sn);

export const putInstancesInstallSuccess = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/set_install_success`, sn);

export const putInstancesInstallFailure = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/set_install_failure`, sn);

export const putInstancesNondistribute = (area: string, sn: string[]) => Axios.put(`api/instance/${area}/nondistribute`, sn);

export const getInstanceStat = (areaId: string[], projectId: number[]) => Axios.post(`api/instance/stat`, { areaId, projectId });

export const postInstanceSync = (areaId: string) => Axios.post(`api/instance/${areaId}/sync`);

export const getInstanceDetail = (sn: string) => Axios.get(`api/instance/${sn}/detail`);

export const postCancelInstallingInstance = (areaId: string, sn: string[]) => Axios.post(`api/instance/${areaId}/cancel`, sn);

export const getInstanceInstallHistory = (sn: string) => Axios.get(`api/instance/${sn}/install_history`);

export const deleteInstance = (sn: string) => Axios.delete(`api/instance/${sn}`);
