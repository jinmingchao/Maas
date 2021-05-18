import Axios from 'axios';
import {IInfoForm, IHardwareTemplate, ICloudbootAddHardware, } from 'umi';

export const getAllArea = () => Axios.get('api/info/area');

export const getHardware = (areaKey: string) => Axios.get(`api/info/${areaKey}/hardware`);
export const putHardwareEnabled = (id: number, enabled: boolean) => Axios.put(`api/info/hardware/${id}/switch/${enabled}`);

export const getPXE = (areaKey: string) => Axios.get(`api/info/${areaKey}/pxe`);
export const putPXEEnabled = (id: number, enabled: boolean) => Axios.put(`api/info/pxe/${id}/switch/${enabled}`);
export const postAddPXE = (areaKey: string, payload: IInfoForm) => Axios.post(`api/info/${areaKey}/pxe`, payload);
export const putUpdatePXE = (areaKey: string, id: number, payload: IInfoForm) => Axios.put(`api/info/${areaKey}/pxe/${id}`, payload);

export const getOperationSystem = (areaKey: string) => Axios.get(`api/info/${areaKey}/operation_system`);
export const putOperationSystemEnabled = (id: number, enabled: boolean) => Axios.put(`api/info/operation_system/${id}/switch/${enabled}`);
export const postAddOperationSystem = (areaKey: string, payload: IInfoForm) => Axios.post(`api/info/${areaKey}/operation_system`, payload);
export const putUpdateOperationSystem = (areaKey: string, id: number, payload: IInfoForm) => Axios.put(`api/info/${areaKey}/operation_system/${id}`, payload);

export const putSyncInfo = (areaKey: string) => Axios.put(`api/info/${areaKey}/sync`);

export const getHardwareTemplates = () => Axios.get('api/info/hardware_template');
export const postCreateHardwareTemplate = (ht: IHardwareTemplate) => Axios.post(`api/info/hardware_template`, ht);
export const deleteHardwareTemplate = (id: number) => Axios.delete(`api/info/hardware_template/${id}`);
export const postAddHardware = (areaKey: string, payload: ICloudbootAddHardware) => Axios.post(`api/info/${areaKey}/hardware`, payload);
export const getHardwareTplField = (id: number) => Axios.get(`api/info/hardware/${id}/template`)
export const putUpdateHardware = (id: number, payload: ICloudbootAddHardware) => Axios.put(`api/info/hardware/${id}`, payload);
