import Axios from "axios";

export const getAllSetupBatch = (areaId: string) => Axios.get(`api/setup/batch/${areaId}`)
