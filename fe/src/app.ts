import { fetchWhoami } from './service/user'

export async function getInitialState() {
    return fetchWhoami();
}
