import { DvaModelBuilder, actionCreatorFactory } from 'dva-model-creator';
import {getAllSetupBatch} from '@/service/setup';

export interface ISetupItem {
    id: number;
    name: string;
    date: string;
    who: string;
    callback: string;
};

export interface ISetupModel {
    setupList: ISetupItem[]
};

const creator = actionCreatorFactory('setup');

export const fetchAllSetup = creator<string>('fetchAllSetup');

const reduceSetupList = creator<ISetupItem[]>('reduceSetupList');

const initialState: ISetupModel = {
    setupList: []
};

const model = new DvaModelBuilder(initialState, 'setup')
.case(reduceSetupList, (state, setupList) => ({ ...state, setupList }))
.takeEvery(fetchAllSetup, function *(areaId, { call, put }) {
    const { data } = yield call(getAllSetupBatch, areaId);

    yield put(reduceSetupList(data));
})
.build();

// @ts-ignore
export default { namespace: 'setup', ...model };
