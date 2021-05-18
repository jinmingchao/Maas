import { DvaModelBuilder, actionCreatorFactory } from 'dva-model-creator';



export interface  IBaseModel {
    collapseBase: boolean;
}

const initialState: IBaseModel = {
    collapseBase: false,
}


const creator = actionCreatorFactory('base');


export const collapseBaseAction = creator<boolean>('collapseBaseAction');
const collapseBaseReduce = creator<boolean>('collapseBaseReduce');


const model = new DvaModelBuilder(initialState, 'base')
    .takeEvery(collapseBaseAction, function* (payload, { put }) {
        yield put(collapseBaseReduce(payload));
    })
    .case(collapseBaseReduce, (state, collapseBase) => ({ ...state, collapseBase }))
    .build();



export default { namespace: 'base', ...model }
