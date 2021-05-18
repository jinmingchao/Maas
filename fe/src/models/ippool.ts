import { DvaModelBuilder, actionCreatorFactory } from 'dva-model-creator';
import {
    getAllIPByPool,
    getAllPool,
    postPool,
    updatePool,
    deletePool,
    getAllNetArea,
    postNetArea,
    updateNetArea,
    deleteNetArea,
    postIP,
    updateIP,
    deleteIP,
    getNetArea,
    getAllArea,
    updateArea,
    postArea,
    deleteArea,
    getPool,
    getPoolByArea,
    batchUpdateIP
} from '@/service/ippool'
import {fetchArea, selectArea} from "@/models/info";
import {useDispatch} from "@@/plugin-dva/exports";

export interface ICloudItem {
    id: string;
    name: string;
    host: string;
    username: string;
    password: string;
    defaultCloudbootNetworkId: number;
    syncInstanceInterval: number;
    enabled: boolean;

}

export interface IPNetAreaItem {
    id: number;
    name: string;
    comment: string;
    areaId: string;
    enabled: boolean;
}

export interface IPPoolItem {
    id: number;
    name: string;
    cidr: string;
    netAsst: string;
    netmask: string;
    gatewayIp: string;
    enabled: boolean;
}

export interface IPItem {
    id: number;
    host: string;
    poolAsst: number;
    enabled: boolean;
}
export interface IPoolInfo {
    [key: string]: any,
}

export interface IPPoolModel {
    cloud: ICloudItem[];
    pool: IPPoolItem[];
    ipList: IPItem[];
    poolInfo: IPoolInfo;
    netArea: IPNetAreaItem[];
    result: string;
}

const initialState: IPPoolModel = {
    cloud: [],
    pool: [],
    ipList: [],
    poolInfo: {},
    netArea: [],
    result: '',
}

const creator = actionCreatorFactory('ippool');

export const fetchCloudAction = creator<any>('fetchCloudAction');
const fetchCloudReduce = creator<ICloudItem[]>('fetchCloudReduce');

export const postAreaAction = creator<ICloudItem>('postAreaAction');
const postAreaReduce = creator<string>('postAreaReduce');

export const updateAreaAction = creator<ICloudItem>('updateAreaAction');
const updateAreaReduce = creator<string>('updateAreaReduce');

export const deleteAreaAction = creator<string>('deleteAreaAction');
const deleteAreaReduce = creator<string>('deleteAreaReduce');


export const fetchAllNetAreaAction = creator<any>('fetchAllNetAreaAction');
const fetchAllNetAreaReduce = creator<IPNetAreaItem[]>('fetchAllNetAreaReduce');

export const fetchNetAreaAction = creator<any>('fetchNetAreaAction');
const fetchNetAreaReduce = creator<IPNetAreaItem[]>('fetchNetAreaReduce');

export const postNetAreaAction = creator<{netArea: IPNetAreaItem, area: string}>('postNetAreaAction');
const postNetAreaReduce = creator<string>('postNetAreaReduce');

export const updateNetAreaAction = creator<IPNetAreaItem>('updateNetAreaAction');
const updateNetAreaReduce = creator<string>('updateNetAreaReduce');

export const deleteNetAreaAction = creator<number>('deleteNetAreaAction');
const deleteNetAreaReduce = creator<string>('deleteNetAreaReduce');

export const fetchAllPoolAction = creator<any>('fetchAllPoolAction');
const fetchAllPoolReduce = creator<IPPoolItem[]>('fetchAllPoolReduce');

export const fetchPoolAction = creator<string>('fetchPoolAction');
const fetchPoolReduce = creator<IPPoolItem[]>('fetchPoolReduce');

export const postPoolAction = creator<IPPoolItem>('postPoolAction');
const postPoolReduce = creator<string>('postPoolReduce');

export const updatePoolAction = creator<IPPoolItem>('updatePoolAction');
const updatePoolReduce = creator<string>('updatePoolReduce');

export const deletePoolAction = creator<number>('deletePoolAction');
const deletePoolReduce = creator<string>('deletePoolReduce');

export const fetchIPAction = creator<number>('fetchIPAction');
const fetchIPReduce = creator<IPItem[]>('fetchIPReduce');

export const postIPAction = creator<IPItem>('postIPAction');
const postIPReduce = creator<string>('postIPReduce');

export const updateIPAction = creator<IPItem>('updateIPAction');
const updateIPReduce = creator<string>('updateIPReduce');

export const batchUpdateIPAction = creator<{ipList: number[], enabled: boolean, poolId: number}>('batchUpdateIPAction');
const batchUpdateIPReduce = creator<string>('batchUpdateIPReduce');

export const deleteIPAction = creator<number>('deleteIPAction');
const deleteIPReduce = creator<string>('deleteIPReduce');


const model = new DvaModelBuilder(initialState, 'ippool')
    .takeEvery(fetchCloudAction, function* (_, { put, call }) {
        const { data } = yield call(getAllArea);
        yield put(fetchCloudReduce(data));
    })
    .case(fetchCloudReduce, (state, cloud) => ({ ...state, cloud }))
    .takeEvery(postAreaAction, function* (payload, {put, call}) {
        const { data } = yield call(postArea, payload)
        yield put(postAreaReduce(data))
        yield put(fetchArea());
    })
    .case(postAreaReduce, (state, result) => ({ ...state, result }))
    .takeEvery(updateAreaAction, function* (payload, { put, call }) {
        const { data } = yield call(updateArea, payload)
        yield put(updateAreaReduce(data))
        yield put(fetchArea());
    })
    .case(updateAreaReduce, (state, result) => ({ ...state, result }))
    .takeEvery(deleteAreaAction, function* (payload, { put, call }) {
        const { data } = yield call(deleteArea, payload)
        yield put(deleteAreaReduce(data))
        yield put(fetchArea());
    })
    .case(deleteAreaReduce, (state, result) => ({ ...state, result }))
    .takeEvery(fetchAllNetAreaAction, function* (_, { put, call }) {
        const { data } = yield call(getAllNetArea)
        yield put(fetchAllNetAreaReduce(data))
    })
    .case(fetchAllNetAreaReduce, (state, netArea) => ({ ...state, netArea }))
    .takeEvery(fetchNetAreaAction, function* (payload, { put, call }) {
        const { data } = yield call(getNetArea, payload)
        yield put(fetchNetAreaReduce(data))
    })
    .case(fetchNetAreaReduce, (state, netArea) => ({ ...state, netArea }))
    .takeEvery(postNetAreaAction, function* ({netArea, area}, {put, call}) {
        const { data } = yield call(postNetArea, netArea)
        yield put(postNetAreaReduce(data))
        yield put(selectArea(area))
    })
    .case(postNetAreaReduce, (state, result) => ({ ...state, result }))
    .takeEvery(updateNetAreaAction, function* (payload, { put, call }) {
        const { data } = yield call(updateNetArea, payload)
        yield put(updateNetAreaReduce(data))
    })
    .case(updateNetAreaReduce, (state, result) => ({ ...state, result }))
    .takeEvery(deleteNetAreaAction, function* (payload, { put, call }) {
        const { data } = yield call(deleteNetArea, payload)
        yield put(deleteNetAreaReduce(data))
    })
    .case(deleteNetAreaReduce, (state, result) => ({ ...state, result }))
    .takeEvery(fetchAllPoolAction, function* (payload, { put, call }) {
        const { data } = yield call(getAllPool)
        yield put(fetchAllPoolReduce(data))
    })
    .case(fetchAllPoolReduce, (state, pool) => ({ ...state, pool }))
    .takeEvery(fetchPoolAction, function* (payload, { put, call }) {
        const { data } = yield call(getPoolByArea, payload)
        yield put(fetchPoolReduce(data))
    })
    .case(fetchPoolReduce, (state, pool) => ({ ...state, pool }))
    .takeEvery(postPoolAction, function* (payload, {put, call}) {
        const { data } = yield call(postPool, payload)
        yield put(postPoolReduce(data))
    })
    .case(postPoolReduce, (state, result) => ({ ...state, result }))
    .takeEvery(updatePoolAction, function* (payload, { put, call }) {
        const { data } = yield call(updatePool, payload)
        yield put(updatePoolReduce(data))
    })
    .case(updatePoolReduce, (state, result) => ({ ...state, result }))
    .takeEvery(deletePoolAction, function* (payload, { put, call }) {
        const { data } = yield call(deletePool, payload)
        yield put(deletePoolReduce(data))
    })
    .case(deletePoolReduce, (state, result) => ({ ...state, result }))
    .takeEvery(fetchIPAction, function* (payload, { put, call }) {
        console.log(payload)
        const { data } = yield call(getAllIPByPool, payload);
        yield put(fetchIPReduce(data));
    })
    .case(fetchIPReduce, (state, ipList) => ({ ...state, ipList }))

    .takeEvery(postIPAction, function* (payload, {put, call}) {
        const { data } = yield call(postIP, payload)
        yield put(postIPReduce(data))
    })
    .case(postIPReduce, (state, result) => ({ ...state, result }))
    .takeEvery(updateIPAction, function* (payload, { put, call }) {
        const { data } = yield call(updateIP, payload)
        yield put(updateIPReduce(data))
        yield put(fetchIPAction(payload.poolAsst));
    })
    .case(updateIPReduce, (state, result) => ({ ...state, result }))
    .takeEvery(batchUpdateIPAction, function* ({ipList, poolId, enabled}, { put, call }) {
        const { data } = yield call(batchUpdateIP, {ipList, enabled})
        yield put(batchUpdateIPReduce(data))
        const ipData = (yield call(getAllIPByPool, poolId)).data;
        yield put(fetchIPReduce(ipData));
    })
    .case(batchUpdateIPReduce, (state, result) => ({ ...state, result }))
    .takeEvery(deleteIPAction, function* (payload, { put, call }) {
        const { data } = yield call(deleteIP, payload)
        yield put(deleteIPReduce(data))
    })
    .case(deleteIPReduce, (state, result) => ({ ...state, result }))
    .build();



export default { namespace: 'ippool', ...model }
