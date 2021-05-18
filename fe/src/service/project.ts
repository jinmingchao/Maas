import {ICreateProjectMessage} from 'umi';
import Axios from 'axios';

export const postCreateProject = (proj: ICreateProjectMessage) => Axios.post('api/project', proj);

export const putModifyProject = (id: number, proj: ICreateProjectMessage) => Axios.put(`api/project/${id}`, proj);

export const getProjects = () => Axios.get('api/project');

export const postHardwareBelongProject = (projId: number, hardwareId: number[]) => Axios.post(`api/project/${projId}/hardware`, hardwareId);
