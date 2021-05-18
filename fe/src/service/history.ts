import Axios from "axios";


export const getAllLogSetUp = () => Axios.get(`api/history/setup/list`)
export const getAllLogInstanceSetUp = () => Axios.get(`api/history/setup/instance/list`)
