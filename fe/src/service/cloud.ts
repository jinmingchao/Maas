import Axios, { AxiosResponse } from "axios";


export const getCloud = () => Axios.get(`api/ippool/fetch-cloud/list`)

