import {DvaModelBuilder, actionCreatorFactory} from 'dva-model-creator';
import {
    getInstances,
    postManageInstances,
    postDistribute,
    postInstall,
    putModifyInstanceHardware,
    putModifyInstancePXE,
    putModifyInstanceOperationSystem,
    putModifyInstanceProject,
    putModifyNetInfo,
    getInstancesByBatch,
    putInstancesReset,
    putInstancesNondistribute,
    putModifyOob,
    getInstancesByInstalling,
    postInstanceSync,
    postCancelInstallingInstance,
    deleteInstance,
    putInstancesInstallSuccess,
    putInstancesInstallFailure,
    putInstancesRecoveryReset
} from '@/service/instance';
import {notification} from 'antd';
import {checkMultiPermission} from "@/service/permission";
import {ICheckMultiPermissionModel, ICheckPermissionResult} from "@/models/permission";

export interface ISimpleInstanceTag {
    color?: string;
    content: string;
}

export interface ISimpleInstance {
    sn: string;
    tags: ISimpleInstanceTag[];
    setupId: number;
    company: string;
    modelName: string;
    cpuCount: number;
    diskCapacitySum: number;
    memoryCapacitySum: number;
    dhcpIp: string;
    innerIp: string;
    netmask: string;
    oobIp: string;
    hardwareId: number;
    netAreaId: number;
    projectId: number;
    distributeable: boolean;
    installable: boolean;
    installed: boolean;

    managed: boolean;
    distributed: boolean;

    pxeName: string;
    operationSystemName: string;
    hardwareName: string;
    projectName: string;

    hostname: string;
    ippool: number;
    boundMac1: string;
    boundMac2: string;
    boundType: string;

    lastModifyAt: string;

    oobUsername: string;
    nic: string;
}

export interface IManageInstanceResult {
    sn: string;
    status: boolean;
}

export interface IDistributeInstance {
    areaId: string;
    sn: string;
    hostname: string;
    ippool: number;
    innerIp: string;
    pxeId: number;
    operationSystemId: number;
    boundMac1: string;
    boundMac2: string;
    boundType: string;
}

export interface IDistribute {
    name: string;
    areaId: string;
    instances: IDistributeInstance[];
}

export interface IInstallMessage {
    areaKey: string;
    status: string;
    message: string;
};

export interface IModifyNetInfo {
    areaId: number;
    hostname: string;
    ippool: number;
    innerIp: string;
    boundMac1: string;
    boundMac2: string;
    boundType: string;
}

export interface IModifyOobInfo {
    username: string;
    password: string;
}

export interface IInstanceModel {
    batchId?: number;
    installing: boolean;
    area?: string;
    instances: ISimpleInstance[];
    manageResult: IManageInstanceResult[];
    buttonPermission: ICheckPermissionResult[];
}

const creator = actionCreatorFactory('instance');

export const fetchInstances = creator<string | undefined>('fetchInstances');
export const fetchInstancesByBatchId = creator<{area?: string, batchId: number}>('fetchInstancesByBatchId');
export const fetchInstancesByInstalling = creator<{area?: string}>('fetchInstancesByInstalling');
export const manageInstances = creator<{ area: string, instances: any[], cb?: () => void }>('manageInstances');
export const distributeInstances = creator<{ area: string, name: string, instances: IDistributeInstance[], cb?: () => void }>('distributeInstances');
export const installInstances = creator<{ area: string, sn: string[], cb?: () => void }>('installInstances');
export const modifyInstanceTemplateInfo = creator<{ area: string, type: string, sn: string, value: number, cb?: () => void }>('modifyInstanceTemplateInfo');
export const modifyInstanceNetInfo = creator<{ area: string, sn: string, modify: IModifyNetInfo, cb?: () => void }>('modifyInstanceNetInfo');
export const resetInstance = creator<{ area: string, sn: string[], cb?: () => void }>('resetInstance');
export const recoveryResetInstance = creator<{ area: string, sn: string[], cb?: () => void }>('recoveryResetInstance');
export const setInstanceInstallSuccess = creator<{ area: string, sn: string[], cb?: () => void }>('setInstanceInstallSuccess');
export const setInstanceInstallFailure = creator<{ area: string, sn: string[], cb?: () => void }>('setInstanceInstallFailure');
export const nondistributeInstance = creator<{ area: string, sn: string[], cb?: () => void }>('nondistributeInstance');
export const cancelInstallInstance = creator<{ area: string, sn: string[], cb?: () => void }>('cancelInstallInstance');
export const modifyOobInstance = creator<{ area: string, sn: string, modify: IModifyOobInfo, cb?: () => void }>('modifyOobInstance');
export const postCheckMultiPermissionAction = creator<ICheckMultiPermissionModel>('putCheckMultiPermissionAction');
export const syncInstance = creator<{ area: string, cb?: () => void }>('syncInstances');
export const removeInstance = creator<{ sn: string, cb?: () => void }>('deleteInstance');

const reduceInstances = creator<{ instances: ISimpleInstance[], area?: string, batchId?: number, installing?: boolean }>('reduceInstances');
const reduceManageResult = creator<IManageInstanceResult[]>('reduceManageResult');
const postCheckMultiPermissionReduce = creator<ICheckPermissionResult[]>('putCheckMultiPermissionReduce');

const initialState: IInstanceModel = {
    installing: false,
    instances: [],
    manageResult: [],
    buttonPermission: [],
}

const model = new DvaModelBuilder(initialState, 'instance')
.takeEvery(postCheckMultiPermissionAction, function* (payload, {put, call}) {
    const {data} = yield call(checkMultiPermission, payload);
    yield put(postCheckMultiPermissionReduce(data));
})
    .case(postCheckMultiPermissionReduce, ((state, buttonPermission) => ({
        ...state,
        buttonPermission
    })))
.case(reduceInstances, (state, { instances, area, batchId, installing }) => ({
    ...state,
    area,
    instances,
    batchId,
    installing: (typeof installing === 'undefined' ? false : installing)
}))
.case(reduceManageResult, (state, manageResult) => ({ ...state, manageResult }))
.takeEvery(fetchInstances, function *(area, { call, put }) {
    if (typeof area === 'undefined' || area === '') {
        yield put(reduceInstances({ instances: [] }));
        return;
    }

    const { data: instances } = yield call(getInstances, area);
    yield put(reduceInstances({ area, instances }));
})
.takeEvery(fetchInstancesByBatchId, function *({ area, batchId }, { call, put }) {
    if (typeof area === 'undefined' || area === '') {
        yield put(reduceInstances({ instances: [] }));
        return;
    }

    const { data: instances } = yield call(getInstancesByBatch, area, batchId);
    yield put(reduceInstances({ batchId, area, instances }));
})
.takeEvery(fetchInstancesByInstalling, function *({ area }, { call, put }) {
    if (typeof area === 'undefined' || area === '') {
        yield put(reduceInstances({ instances: [] }));
        return;
    }

    const { data: instances } = yield call(getInstancesByInstalling, area);
    yield put(reduceInstances({ area, instances, installing: true }));
})
.takeEvery(manageInstances, function *({ area, instances, cb }, { call, put }) {
    if (typeof area === 'undefined' || area === '') {
        return;
    }

    const { data } = yield call(postManageInstances, area, instances);

    yield put(reduceManageResult(data));

    cb && cb();
})
.takeEvery(distributeInstances, function *({ area, name, instances, cb }, { call }) {
    if (typeof area === 'undefined' || area === '') {
        return;
    }

    const payload: IDistribute = { name, areaId: area, instances };
    yield call(postDistribute, payload);

    cb && cb();
})
.takeEvery(installInstances, function *({ sn, cb }, { call }) {
    const { data } = yield call(postInstall, sn);

    const description: string[] = [];
    (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
    if (description.length === 0) {
        notification.open({
            message: '成功',
            description: '装机请求提交成功',
            duration: null
        });
    }
    else {
        notification.open({
            message: '存在未成功提交的装机请求',
            description: description.join(','),
            duration: null
        })
    }

    cb && cb();
})
.takeEvery(modifyInstanceTemplateInfo, function *({ type, sn, value, cb }, { call }) {
    if (type === 'hardware') {
        yield call(putModifyInstanceHardware, sn, value);
    }
    else if (type === 'pxe') {
        yield call(putModifyInstancePXE, sn, value);
    }
    else if (type === 'operation_system') {
        yield call(putModifyInstanceOperationSystem, sn, value);
    }
    else if (type === 'project') {
        yield call(putModifyInstanceProject, sn, value);
    }

    cb && cb();
})
.takeEvery(modifyInstanceNetInfo, function *({ sn, modify, cb }, { call }) {
    yield call(putModifyNetInfo, sn, modify);

    cb && cb();
})
.takeEvery(resetInstance, function *({ area, sn, cb }, { call }) {
    yield call(putInstancesReset, area, sn);

    cb && cb();
})
.takeEvery(recoveryResetInstance, function *({ area, sn, cb }, { call }) {
    yield call(putInstancesRecoveryReset, area, sn);

    cb && cb;
})
.takeEvery(setInstanceInstallSuccess, function *({ area, sn, cb }, { call }) {
    yield call(putInstancesInstallSuccess, area, sn);

    cb && cb();
})
.takeEvery(setInstanceInstallFailure, function *({ area, sn, cb }, { call }) {
    yield call(putInstancesInstallFailure, area, sn);

    cb && cb();
})
.takeEvery(nondistributeInstance, function *({ area, sn, cb }, { call }) {
    yield call(putInstancesNondistribute, area, sn);

    cb && cb();
})
.takeEvery(cancelInstallInstance, function *({ area, sn, cb }, { call }) {
    yield call(postCancelInstallingInstance, area, sn);

    cb && cb();
})
.takeEvery(modifyOobInstance, function *({ sn, modify, cb }, { call }) {
    yield call(putModifyOob, sn, modify);

    cb && cb();
})
.takeEvery(syncInstance, function *({ area, cb }, { call }) {
    yield call(postInstanceSync, area);

    cb && cb();
})
.takeEvery(removeInstance, function *({ sn, cb }, { call }) {
    yield call(deleteInstance, sn);

    cb && cb();
})
.build();

// @ts-ignore
export default {namespace: 'instance', ...model};
