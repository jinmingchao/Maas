import { DvaModelBuilder, actionCreatorFactory } from 'dva-model-creator';
import {getAllArea, getHardware, getPXE, getOperationSystem, putSyncInfo, getHardwareTemplates, postCreateHardwareTemplate, deleteHardwareTemplate} from '@/service/info';
import {getNetArea} from "@/service/ippool";
import { fetchNetAreaAction, fetchPoolAction, IPNetAreaItem} from "@/models/ippool";
import { fetchAllSetup } from "umi";

export interface ISimpleArea {
    id: string;
    name: string;
}

export interface ISimpleHardwareProject {
    id: number;
    name: string;
}

export interface IInfoHardware {
    id: number;
    maasId: number;
    name: string;
    projects: ISimpleHardwareProject[];
    enabled: boolean;
}

export interface IInfoPXE {
    id: number;
    maasId: number;
    name: string;
    enabled: boolean;
}

export interface IInfoOperationSystem {
    id: number;
    maasId: number;
    name: string;
    enabled: boolean;
}

export interface IHardwareTemplate {
    id?: number;
    company: string;
    name: string;
    tpl: string;
}

export interface IInfoForm {
    name: string;
    content: string;
}

export interface IInfoModel {
    area: ISimpleArea[];
    hardware: IInfoHardware[];
    pxe: IInfoPXE[];
    operationSystem: IInfoOperationSystem[];
    selectedArea?: ISimpleArea;
    net: IPNetAreaItem[];

    hardwareTemplate: IHardwareTemplate[];
}

export interface ICloudbootAddHardware {
    company: string;
    modelName: string;
    data: string;
    isSystemAdd: string;
    tpl?: string;
}

const creator = actionCreatorFactory('info');

export const fetchArea = creator('fetchArea');
export const selectArea = creator<string>('selectArea');
export const syncAreaInfo = creator<string>('syncArea');

export const fetchHardwareTemplate = creator('fetchHardwareTemplate');
export const createHardwareTemplate = creator<IHardwareTemplate>('createHardwareTemplate');
export const removeHardwareTemplate = creator<number>('removeHardwareTemplate');

const reduceArea = creator<ISimpleArea[]>('reduceArea');
const reduceSelectArea = creator<{
    area: string,
    hardware: IInfoHardware[],
    pxe: IInfoPXE[],
    operationSystem: IInfoOperationSystem[]
    net: IPNetAreaItem[],

}>('reduceSelectArea');
const reduceHardwareTemplate = creator<IHardwareTemplate[]>('reduceHardwareTemplate');

const initialState: IInfoModel = {
    area: [],
    hardware: [],
    pxe: [],
    operationSystem: [],
    net: [],
    hardwareTemplate: []
}

const model = new DvaModelBuilder(initialState, 'info')
.subscript(({dispatch}) => dispatch(fetchArea()))
.case(reduceArea, (state, area) => ({ ...state, area }))
.case(reduceSelectArea, (state, { area, hardware, pxe, operationSystem, net }) => {
    window.sessionStorage.setItem('default_select_area', area);

    return {
        ...state,
        selectedArea: state.area.find(({ id }) => id === area),
        hardware, pxe, operationSystem, net
    }
})
.case(reduceHardwareTemplate, (state, hardwareTemplate) => ({ ...state, hardwareTemplate }))
.takeEvery(fetchArea, function *(_: any, { call, put }) {
    const { data } = yield call(getAllArea);
    yield put(reduceArea(data));
})
.takeEvery(selectArea, function *(area, { call, put }) {

    const { data: hardware } = yield call(getHardware, area);
    const { data: pxe } = yield call(getPXE, area);
    const { data: operationSystem } = yield call(getOperationSystem, area);
    const { data: net } = yield call(getNetArea, area);

    yield put(reduceSelectArea({ area, hardware, pxe, operationSystem, net }));
    yield put(fetchNetAreaAction(area))
    yield put(fetchPoolAction(area))
    yield put(fetchAllSetup(area))
})
.takeEvery(syncAreaInfo, function *(area, { call, put }) {

    yield call(putSyncInfo, area);

    yield put(selectArea(area));
})
.takeEvery(fetchHardwareTemplate, function *(_: void, { call, put }) {
    const { data } = yield call(getHardwareTemplates);
    yield put(reduceHardwareTemplate(data));
})
.takeEvery(createHardwareTemplate, function *(payload, { call, put }) {
    yield call(postCreateHardwareTemplate, payload);

    yield put(fetchHardwareTemplate);
})
.takeEvery(removeHardwareTemplate, function *(payload, { call, put }) {
    yield call(deleteHardwareTemplate, payload);

    yield put(fetchHardwareTemplate);
})
.build();

// @ts-ignore
export default { namespace: 'info', ...model };
