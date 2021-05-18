import { DvaModelBuilder, actionCreatorFactory } from 'dva-model-creator';
import {getProjects, postCreateProject, postHardwareBelongProject, putModifyProject} from '@/service/project';

export interface ICreateProjectMessage {
    name: string;
    description: string;
}

export interface IProjectItem {
    id: number;
    name: string;
    description: string;
    createdAt: string;
}

export interface IProjectModel {
    projects: IProjectItem[]
};

const initialState: IProjectModel = {
    projects: []
};

const creator = actionCreatorFactory('project');

export const fetchAllProject = creator('fetchAllProject');
export const modifyProject = creator<{id: number, payload: ICreateProjectMessage}>('modifyProject');
export const createProject = creator<ICreateProjectMessage>('createProject');
export const appendProjectHardware = creator<{ projId: number, hardwareId: number[], cb?: () => void }>('appentProjectHardware');

const reduceProjects = creator<IProjectItem[]>('reduceProjects');

const model = new DvaModelBuilder(initialState, 'project')
.case(reduceProjects, (state, projects) => ({ ...state, projects }))
.takeEvery(fetchAllProject, function *(_, { call, put }) {
    const { data } = yield call(getProjects);

    yield put(reduceProjects(data));
})
.takeEvery(createProject, function *(payload, { call, put }) {
    yield call(postCreateProject, payload);

    yield put(fetchAllProject());
})
.takeEvery(appendProjectHardware, function *({ projId, hardwareId, cb }, { call }) {
    yield call(postHardwareBelongProject, projId, hardwareId);

    cb && cb();

})
.takeEvery(modifyProject, function *({ id, payload }, { call, put }) {
    yield call(putModifyProject, id, payload);

    yield put(fetchAllProject());
})
.build();

// @ts-ignore
export default { namespace: 'project', ...model };
