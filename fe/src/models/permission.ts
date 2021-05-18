import {DvaModelBuilder, actionCreatorFactory} from "dva-model-creator";
import {message} from "antd";
import {
    checkMultiPermission,
    getAllRoleAndPermission,
    getResourceGroupList,
    addResourceGroup,
    getResource,
    getRoleForUser,
    getUsersForRole,
    addRolePermission,
    addRoleForUser,
    delRoleForUser,
    getAllRole, delResource, addUserPermission, delRolePermission, deleteRole, delResourceGroup
} from '@/service/permission';

export interface IAddRoleForUserModel {
    user: string,
    role: string
}

export interface IResourceGroupModel {
    resourceGroup: string;
    resource: string | string[];
}

export interface ICheckMultiPermissionModel {
    sub: string,
    objs: string[],
    act: string
}

export interface IRoleModel {
    sub: string,
    obj: string,
    act: string
}

export interface IMenuInfo {
    key: string,
    icon: string,
    title: string,
    path: string,
    fatherKey: string,
    level: string,
}

export interface IMenuItem {
    key: string;
    path: string;
    title: string;
    icon: string;
}

export interface IMenuGroup {
    key: string;
    title: string;
    icon: string;

    items?: IMenuItem[];
    groups?: IMenuGroup[];
}

export interface IMenuModel {
    menuItems: IMenuItem[];
    menuGroups: IMenuGroup[];
}

export interface ICheckPermissionResult {
    obj: string,
    hasPermission: boolean
}

export interface IPutResourceGroupResult {
    groupName: string,
    resource: string,
    result: boolean,
}

export interface IPermissionModel {
    roleUsers: string[],
    rolesWithPer: IRoleModel[],
    roleForUser: string[],
    roles: string[],
    menu: IMenuModel,
    resourceGroupList: string[],
    resourceList: string[],
    checkPermissionResult: ICheckPermissionResult[],
}

const initialState: IPermissionModel = {
    rolesWithPer: [],
    roleUsers: [],
    roles: [],
    roleForUser: [],
    menu: {
        menuItems: [],
        menuGroups: [],
    },
    resourceGroupList: [],
    resourceList: [],
    checkPermissionResult: []
};

const creator = actionCreatorFactory('permission');

export const fetchUsersForRoleAction = creator<string>('fetchUsersForRoleAction');
const fetchUsersForRoleReduce = creator<string[]>('fetchUsersForRoleReduce');

export const fetchAllRolePermissionAction = creator<any>('fetchAllRolePermissionAction');
const fetchAllRolePermissionReduce = creator<IRoleModel[]>('fetchAllRolePermissionReduce');

export const postCheckMultiPermissionAction = creator<any>('postCheckMultiPermissionAction');
const postCheckMultiPermissionReduce = creator<ICheckPermissionResult[]>('postCheckMultiPermissionReduce');

export const fetchAllResourceGroupAction = creator<string[]>('fetchAllResourceGroupAction');
const fetchAllResourceGroupReduce = creator<string[]>('fetchAllResourceGroupReduce');

export const fetchOneResourceGroupListAction = creator<any>('fetchOneResourceGroupListAction');
const fetchOneResourceGroupListReduce = creator<string[]>('fetchOneResourceGroupListReduce');

export const putResourceGroupAction = creator<IResourceGroupModel>('putResourceGroupAction');

export const deleteResourceAction = creator<IResourceGroupModel>('deleteResourceAction');

export const putPolicyAction = creator<IRoleModel>('putPolicyAction');

export const deletePolicyAction = creator<IRoleModel>('deletePolicyAction');

export const putRoleForUserAction = creator<IAddRoleForUserModel>('putRoleForUserAction');

export const deleteRoleForUserAction = creator<IAddRoleForUserModel>('deleteRoleForUserAction');

export const fetchAllRoleAction = creator<any>('fetchAllRoleAction');
const fetchAllRoleReduce = creator<string[]>('fetchAllRoleReduce');

export const fetchRoleForUserAction = creator<any>('fetchRoleForUserAction');
const fetchRoleForUserReduce = creator<string[]>('fetchRoleForUserReduce');

export const putUserPolicyAction = creator<IRoleModel>('putUserPolicyAction');

export const deleteRoleAction = creator<string>('deleteRoleAction');

export const deleteResourceGroupAction = creator<string>('deleteResourceGroupAction');

const model = new DvaModelBuilder(initialState, 'permission')
    .takeEvery(putUserPolicyAction, function* (payload, {put, call}) {
        const {data} = yield call(addUserPermission, payload);
        data ? message.success('用户权限添加成功') : message.error('用户权限添加失败');
        yield put(fetchAllRolePermissionAction);
    })
    .takeEvery(fetchRoleForUserAction, function* (payload, {put, call}) {
        const {data} = yield call(getRoleForUser, payload);
        yield put(fetchRoleForUserReduce(data));
    })
    .case(fetchRoleForUserReduce, ((state, roleForUser) => ({
        ...state,
        roleForUser,
    })))
    .takeEvery(deleteRoleForUserAction, function* (payload, {put, call}) {
        const {data} = yield call(delRoleForUser, payload);
        data
            ? message.success('角色：' + payload.role + ' 删除用户 ' + payload.user + ' 成功')
            : message.error('角色：' + payload.role + ' 删除用户 ' + payload.user + ' 失败');
    })
    .takeEvery(putRoleForUserAction, function* (payload, {put, call}) {
        const {data} = yield call(addRoleForUser, payload);
        data
            ? message.success('角色：' + payload.role + ' 添加用户' + payload.user + '成功')
            : message.error('角色：' + payload.role + ' 添加用户' + payload.user + '失败');
    })
    .takeEvery(putPolicyAction, function* (payload, {put, call}) {
        const {data} = yield call(addRolePermission, payload);
        data ? message.info('角色权限添加成功') : message.error('角色权限添加失败');
        const info = yield call(getAllRoleAndPermission);
        yield put(fetchAllRolePermissionReduce(info.data));
        yield put(fetchAllRoleAction);
    })
    .takeEvery(deletePolicyAction, function* (payload, {put, call}) {
        const {data} = yield call(delRolePermission, payload);
        data ? message.info('权限删除成功') : message.error('权限删除失败');
        yield put(fetchAllRolePermissionAction);
    })
    .takeEvery(deleteRoleAction, function* (payload, {put, call}) {
        const {data} = yield call(deleteRole, payload);
        data ? message.info('删除成功') : message.error('删除失败');
        yield put(fetchAllRolePermissionAction);
    })
    .takeEvery(fetchUsersForRoleAction, function* (role, {put, call}) {
        const {data} = yield call(getUsersForRole, role);
        yield put(fetchUsersForRoleReduce(data));
    })
    .case(fetchUsersForRoleReduce, ((state, roleUsers) => ({
        ...state,
        roleUsers,
    })))
    .takeEvery(fetchAllRolePermissionAction, function* (_, {put, call}) {
        const {data} = yield call(getAllRoleAndPermission);
        yield put(fetchAllRolePermissionReduce(data));
    })
    .case(fetchAllRolePermissionReduce, (state, rolesWithPer) => ({
        ...state,
        rolesWithPer,
    }))
    .takeEvery(postCheckMultiPermissionAction, function* (payload, {put, call}) {
        const {data} = yield call(checkMultiPermission, payload);
        yield put(postCheckMultiPermissionReduce(data));
    })
    .case(postCheckMultiPermissionReduce, ((state, checkPermissionResult) => ({
        ...state,
        checkPermissionResult
    })))
    .takeEvery(fetchAllResourceGroupAction, function* (_, {put, call}) {
        const {data} = yield call(getResourceGroupList);
        yield put(fetchAllResourceGroupReduce(data));
    })
    .case(fetchAllResourceGroupReduce, ((state, resourceGroupList) => ({
        ...state,
        resourceGroupList
    })))
    .takeEvery(fetchOneResourceGroupListAction, function* (payload, {put, call}) {
        const {data} = yield call(getResource, payload);
        yield put(fetchOneResourceGroupListReduce(data));
    })
    .case(fetchOneResourceGroupListReduce, ((state, resourceList) => ({
        ...state,
        resourceList
    })))
    .takeEvery(putResourceGroupAction, function* (payload, {put, call}) {
        const {data}: { data: IPutResourceGroupResult[] } = yield call(addResourceGroup, payload);
        data.forEach(res => res.result
            ? message.success('资源组：' + res.groupName + '中，资源：' + res.resource + ' 添加成功。')
            : message.error('资源组：' + res.groupName + '中，资源：' + res.resource + ' 添加失败。')
        );
        yield put(fetchAllResourceGroupAction);
    })
    .takeEvery(deleteResourceAction, function* (payload, {put, call}) {
        const {data} = yield call(delResource, payload);
        if (data) {
            message.success(payload.resource + ' 删除成功');
            yield put(fetchOneResourceGroupListAction(payload.resourceGroup));
        } else {
            message.error(payload.resource + ' 删除失败')
        }

    })
    .takeEvery(deleteResourceGroupAction, function* (payload, {put, call}) {
        const {data} = yield call(delResourceGroup, payload);
        data ? message.success("删除成功") : message.error("删除失败");
        yield put(fetchAllResourceGroupAction);
    })
    .takeEvery(fetchAllRoleAction, function* (_, {put, call}) {
        const {data} = yield call(getAllRole);
        yield put(fetchAllRoleReduce(data));
    })
    .case(fetchAllRoleReduce, (((state, roles) => ({
        ...state,
        roles,
    }))))
    .build();

// @ts-ignore
export default {namespace: 'permission', ...model};
