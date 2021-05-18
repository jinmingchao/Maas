import Axios from 'axios';

export const getAllUser = () => Axios.get('api/user')

export const fetchWhoami = () => Axios.get('api/user/whoami');

export const logout = () => Axios.get('api/user/logout');

export const getAllGroup = () => Axios.get('api/user/group');

export const addGroup = (name: string) => Axios.post(`api/user/group/${name}`);

export const removeGroup = (id: number) => Axios.delete(`api/user/group/${id}`);

export const getGroupMemeber = (id: number) => Axios.get(`api/user/group/${id}/member`)

export const removeGroupMemeber = (id: number, username: string) => Axios.delete(`api/user/group/${id}/${username}`);

export const addGroupMemeber = (id: number, username: string) => Axios.post(`api/user/group/${id}/${username}`);

export const getGroupProject = (id: number) => Axios.get(`api/user/group/${id}/project`);

export const addGroupProject = (id: number, pid: number) => Axios.post(`api/user/group/${id}/project/${pid}`);

export const removeGroupProject = (id: number, pid: number) => Axios.delete(`api/user/group/${id}/project/${pid}`);
