import {IRoleModel, IAddRoleForUserModel, ICheckMultiPermissionModel, IResourceGroupModel} from '@/models/permission';
import Axios from 'axios';

// export const getAllUser = () => Axios.get('api/permission/users');
export const addUserPermission = (payload: IRoleModel) => Axios.put('api/permission/user/policy', payload);

export const getUsersForRole = (role: string) => Axios.get(`api/permission/users/${role}`);

export const getAllRole = () => Axios.get('api/permission/roles');

export const deleteRole = (role: string) => Axios.delete(`api/permission/role/del/${role}`);

export const getAllRoleAndPermission = () => Axios.get('api/permission/role/per');

export const getOneRoleAndPermission = (role: string) => Axios.get(`api/permission/role/per/${role}`);

export const getRoleForUser = (user: string) => Axios.get(`api/permission/user/role/${user}`);

export const addRoleForUser = (payload: IAddRoleForUserModel) => Axios.put('api/permission/user/per', payload);

export const delRoleForUser = (payload: IAddRoleForUserModel) => Axios.delete('api/permission/user/per', {params: payload});

export const addRolePermission = (payload: IRoleModel) => Axios.put('api/permission/role/per', payload);

export const delRolePermission = (payload: IRoleModel) => Axios.delete('api/permission/role/per', {params: payload});

// resource
export const getResourceGroupList = () => Axios.get('api/permission/resource');

export const getResource = (resourceGroup: string) => Axios.get(`api/permission/resource/${resourceGroup}`);

export const addResourceGroup = (payload: IResourceGroupModel) => Axios.put('api/permission/resource', payload);

export const delResource = (payload: IResourceGroupModel) => Axios.delete('api/permission/resource', {params: payload});

export const delResourceGroup = (resourceGroup: string) => Axios.delete(`api/permission/resource/del/${resourceGroup}`);
// check permission
export const checkMultiPermission = (payload: ICheckMultiPermissionModel) => Axios.post('api/permission/check-multi-permission', payload);
