import {defineConfig} from 'umi';

export default defineConfig({
    nodeModulesTransform: {
        type: 'none',
    },
    routes: [
        {path: '/', component: '@/pages/home/index'},
        {path: '/home', component: '@/pages/home/index'},
        {path: '/template/hardware-template', component: '@/pages/template/hardware-template'},
        {path: '/template/hardware', component: '@/pages/template/hardware'},
        {path: '/template/pxe', component: '@/pages/template/pxe'},
        {path: '/template/operation-system', component: '@/pages/template/operation-system'},
        {path: '/area/ip-pool/', component: '@/pages/area/pool'},
        {path: '/area/net-area/', component: '@/pages/area/net'},
        {path: '/area/area-zone/', component: '@/pages/area/area'},
        {path: '/instance/list', component: '@/pages/instance/list'},
        {path: '/instance/manage', component: '@/pages/area/net'},
        {path: '/log/setup', component: '@/pages/history/setup'},
        {path: '/log/instance', component: '@/pages/history/instance'},
        {path: '/project/list', component: '@/pages/project/list'},
        {path: '/stat/instance', component: '@/pages/stat/instance'},
        {path: '/user/info', component: '@/pages/user/user-permission-admin'},
        {path: '/user/permission', component: '@/pages/user/permission-admin'},
        {path: '/user/resource-group', component: '@/pages/user/resource-group'},
        {path: '/user/group', component: '@/pages/user/group'},
    ],
    dva: {},
    dynamicImport: {},
    proxy: {
        '/api': {
            target: 'http://localhost:8080/cmdb/torn-mcloud',
            // target: 'http://192.168.19.145:8080/cmdb/torn-mcloud',
            changeOrigin: true
        },
        '/websocket': {
            target: 'ws://localhost:8080/cmdb/torn-mcloud/',
            changeOrigin: true
        }
    },
    publicPath: './',
    history: {
        type: 'hash'
    },
    scripts: [
        "window.ENV = '" + process.env.APP_ENV + "';"
    ],

});
