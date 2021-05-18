import Axios, { AxiosResponse } from "axios";
import {IPPoolItem, IPNetAreaItem} from "umi";
import {ICloudItem, IPItem} from "@/models/ippool";

export const getAllArea = () => Axios.get(`api/area/fetch-cloud/list`)
export const postArea = (data: ICloudItem) => Axios.post(`api/area/post-area`, data)
export const updateArea = (data: ICloudItem) => Axios.put(`api/area/put-area`, data)
export const deleteArea = (id: string) => Axios.delete(`api/area/delete-area/${id}`)

export const getAllNetArea = () => Axios.get(`api/area/fetch-net/list`)
export const postNetArea = (data: IPNetAreaItem) => Axios.post(`api/area/post-net`, data)
export const updateNetArea = (data: IPNetAreaItem) => Axios.put(`api/area/put-net`, data)
export const deleteNetArea = (id: number) => Axios.delete(`api/area/delete-net/${id}`)
export const getNetArea = (areaId: string) => Axios.get(`api/area/fetch-net/area/${areaId}/list`)

export const getAllPool = () => Axios.get(`api/area/fetch-ippool/list`)
export const postPool = (data: IPPoolItem) => Axios.post(`api/area/post-ippool`, data)
export const updatePool = (data: IPPoolItem) => Axios.put(`api/area/put-ippool`, data)
export const deletePool = (id: number) => Axios.delete(`api/area/delete-ippool/${id}`)
export const getPool = (netAsst: string) => Axios.get(`api/area/fetch-ippool/net/${netAsst}/list`)
export const getPoolByArea = (areaId: string) => Axios.get(`api/area/fetch-ippool/area/${areaId}/list`)

export const getAllIPByPool = (poolId: number) => Axios.get(`api/area/fetch-ip/pool/${poolId}/list`)
export const getUsedIPByPool = (poolId: number) => Axios.get(`api/area/fetch-ip/pool/${poolId}/list/used`)
export const getUnusedIPByPool = (poolId: number) => Axios.get(`api/area/fetch-ip/pool/${poolId}/list/unused`)
export const updateIP = (data: IPItem) => Axios.put(`api/area/put-ip`, data)
export const batchUpdateIP = (data: any) => Axios.put(`api/area/put-ip/batch`, data)
export const deleteIP = (id: number) => Axios.delete(`api/area/delete-ip/${id}`)
export const postIP = (data: IPItem) => Axios.post(`api/area/post-ip`, data)


