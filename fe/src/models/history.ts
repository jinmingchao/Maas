import { DvaModelBuilder, actionCreatorFactory } from 'dva-model-creator';
import {getAllLogSetUp, getAllLogInstanceSetUp} from "@/service/history";

export interface IHistorySetupInstance {
    setupId: number;
    sn: string;
    createAt: string;
    detail: string;
}

export interface IHistoryModel {
    sn?: string;
    setupId?: number;
    histories: IHistorySetupInstance[];
}

const initialState: IHistoryModel = {
    histories: []
}

const creator = actionCreatorFactory('history');

const model = new DvaModelBuilder(initialState, 'history')
.build();


// @ts-ignore
export default { namespace: 'history', ...model }
