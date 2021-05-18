import {DvaModelBuilder, actionCreatorFactory} from 'dva-model-creator';
import {getAllUser, addGroup, getAllGroup, removeGroup, getGroupMemeber, addGroupMemeber, removeGroupMemeber, getGroupProject, addGroupProject, removeGroupProject} from '@/service/user';
import {notification} from 'antd';
import {IProjectItem} from 'umi';

export interface IUserInfo {
    qq: string;
    phone: string;
    wechat: string;
    language: string;
    timeZone: string;
    username: string;
    email: string;
    name: string;
    role: number;
}

export interface IUserDict {
    [key: string]: IUserInfo
}

export interface IUserGroup {
    id: number;
    name: string;
}

export interface IUserModel {
    users: IUserInfo[];
    usersDict: IUserDict;
    groups: IUserGroup[];
    groupMemeber: string[];
    groupProject: IProjectItem[];
}

const initialState: IUserModel = {
    users: [],
    usersDict: {},
    groups: [],
    groupMemeber: [],
    groupProject: []
}

const creator = actionCreatorFactory('user');

const usersReduce = creator<IUserInfo[]>('usersReduce');

const userGroupReduce = creator<IUserGroup[]>('userGroupReduce');
const userGroupMemeberReduce = creator<string[]>('userGroupMemeberReduce');
const userGroupProjectReduce = creator<IProjectItem[]>('userGroupProjectReduce');

export const usersAction = creator('usersAction');

export const addUserGroup = creator<string>('addUserGroup');
export const getUserGroup = creator('getUserGroup');
export const removeUserGroup = creator<number>('removeUserGroup');
export const getUserGroupMemeber = creator<number>('getUserGroupMemeber');
export const addUserGroupMemeber = creator<{id: number, username: string}>('addUserGroupMemeber');
export const removeUserGroupMemeber = creator<{id: number, username: string}>('removeUserGroupMemeber');
export const getUserGroupProject = creator<number>('getUserGroupProject');
export const addUserGroupProject = creator<{id: number, pid: number }>('addUserGroupProject');
export const removeUserGroupProject = creator<{id: number, pid: number}>('removeUserGroupProject');

const model = new DvaModelBuilder(initialState, 'user')
    .subscript(({ dispatch }) => {
        dispatch(usersAction());
    })
    .case(usersReduce, (state, users) => {
        const usersDict: IUserDict = users.reduce((prev, curr) => {
            const next: IUserDict = { ...prev };
            next[curr.username] = curr;
            return next;
        }, {});

        return { ...state, users, usersDict };
    })
    .case(userGroupReduce, (state, groups) => ({ ...state, groups }))
    .case(userGroupMemeberReduce, (state, groupMemeber) => ({ ...state, groupMemeber }))
    .case(userGroupProjectReduce, (state, groupProject) => ({ ...state, groupProject }))
    .takeEvery(usersAction, function *(_: any, { call, put }) {
        const { data } = yield call(getAllUser);

        yield put(usersReduce(data));
    })
    .takeEvery(addUserGroup, function *(name, { call, put }) {
        const { data } = yield call(addGroup, name);
        
        if (data as boolean) {
            notification.success({ message: '添加成功' });
        }
        else {
            notification.error({ message: '添加失败' });
        }

        yield put(getUserGroup());
    })
    .takeEvery(removeUserGroup, function *(id, { call, put }) {
        const {data} = yield call(removeGroup, id);
        
        if (data as boolean) {
            notification.success({ message: '删除成功' });
        }
        else {
            notification.error({ message: '删除失败' });
        }

        yield put(getUserGroup());
    })
    .takeEvery(getUserGroup, function *(_: void, { call, put }) {
        const { data } = yield call(getAllGroup);

        yield put(userGroupReduce(data));
    })
    .takeEvery(getUserGroupMemeber, function *(id, { call, put }) {
        const { data } = yield call(getGroupMemeber, id);

        yield put(userGroupMemeberReduce(data));
    })
    .takeEvery(addUserGroupMemeber, function *({ id, username }, { call, put }) {
        yield call(addGroupMemeber, id, username);

        yield put(getUserGroupMemeber(id));
    })
    .takeEvery(removeUserGroupMemeber, function *({ id, username }, { call, put }) {
        yield call(removeGroupMemeber, id, username);

        yield put(getUserGroupMemeber(id));
    })
    .takeEvery(getUserGroupProject, function *(id, { call, put }) {
        const { data } = yield call(getGroupProject, id);

        yield put(userGroupProjectReduce(data));
    })
    .takeEvery(addUserGroupProject, function *({ id, pid }, { call, put }) {
        const { data } = yield call(addGroupProject, id, pid);
        
        if (data as boolean) {
            notification.success({ message: '添加成功' });
        }
        else {
            notification.error({ message: '添加失败' });
        }

        yield put(getUserGroupProject(id));
    })
    .takeEvery(removeUserGroupProject, function *({ id, pid }, { call, put }) {
        const { data } = yield call(removeGroupProject, id, pid);
        
        if (data as boolean) {
            notification.success({ message: '删除成功' });
        }
        else {
            notification.error({ message: '删除失败' });
        }

        yield put(getUserGroupProject(id));
    })
    .build();

// @ts-ignore
export default { namespace: 'user', ...model };
