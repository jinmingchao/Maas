import React, {FC, ReactElement, useEffect} from 'react';
import {Layout, Menu, Select} from 'antd';
import {
    HddFilled,
    HddOutlined,
    DesktopOutlined,
    HomeOutlined,
    ClusterOutlined,
    HistoryOutlined,
    DashboardOutlined,
    CarryOutOutlined,
    CodeOutlined,
    IdcardOutlined,
    TeamOutlined,
    RocketOutlined,
} from '@ant-design/icons';
import {useHistory, Dispatch, useDispatch, IInfoModel, ISimpleArea, selectArea} from 'umi';
import {collapseBaseAction, IBaseModel} from "@/models/base";
import {connect} from "@@/plugin-dva/exports";
import {fetchIPAction, fetchNetAreaAction, fetchPoolAction} from "@/models/ippool";
import {useAccess} from "@@/plugin-access/access";
import {IPermissionModel, postCheckMultiPermissionAction, ICheckPermissionResult} from "@/models/permission";

const {Sider} = Layout
const {Item, SubMenu} = Menu;

interface IMenuItem {
    key: string;
    path: string;
    title: string;
    icon: ReactElement;
}

interface IMenuGroup {
    key: string;
    title: string;
    icon: ReactElement;

    items?: IMenuItem[],
    groups?: IMenuGroup[];
}

const menuItems: IMenuItem[] = [
    {key: 'home', icon: <HomeOutlined/>, title: '首页', path: '/home'},
];

const menuGroups: IMenuGroup[] = [
    {
        key: 'area', icon: <CarryOutOutlined/>, title: '区域管理',
        items: [
            {key: 'area-zone', icon: <HddOutlined/>, title: '区域管理', path: '/area/area-zone'},
            {key: 'net-area', icon: <HddOutlined/>, title: '网络区域', path: '/area/net-area'},
            {key: 'ip-pool', icon: <HddOutlined/>, title: 'IP池管理', path: '/area/ip-pool'},
        ]
    },
    {
        key: 'project', icon: <CarryOutOutlined/>, title: '建设项目管理',
        items: [
            {key: 'project-list', icon: <HddOutlined/>, title: '项目列表', path: '/project/list'},
        ]
    },
    {
        key: 'template', icon: <CarryOutOutlined/>, title: '典配管理',
        items: [
            {key: 'template-hardware-template', icon: <CarryOutOutlined/>, title: '系统典配模板', path: '/template/hardware-template'},
            {key: 'template-hardware', icon: <CarryOutOutlined/>, title: '系统典配', path: '/template/hardware'},
            {key: 'template-pxe', icon: <CarryOutOutlined/>, title: 'PXE', path: '/template/pxe'},
            {key: 'template-operation-system', icon: <CarryOutOutlined/>, title: '操作系统', path: '/template/operation-system'},
        ]
    },
    {
        key: 'instance', icon: <CarryOutOutlined/>, title: '设备管理',
        items: [
            {
                key: 'instance-list-nonmanaged',
                icon: <CarryOutOutlined/>,
                title: '未纳管列表',
                path: '/instance/list?nonmanaged=1'
            },
            {key: 'instance-list-managed', icon: <CarryOutOutlined/>, title: '已纳管列表', path: '/instance/list?managed=1'},
            {
                key: 'instance-list-installable',
                icon: <CarryOutOutlined/>,
                title: '装机列表',
                path: '/instance/list?installable=1'
            },
            {key: 'instance-stat', icon: <CarryOutOutlined/>, title: '设备统计', path: '/stat/instance'},
            //{ key: 'instance-manage', icon: <CarryOutOutlined />, title: '网络拓扑', path: '/instance/manage' },
            //{ key: 'instance-manage', icon: <CarryOutOutlined />, title: '机房位置', path: '/instance/manage' },
        ]
    },
    {
        key: 'history', icon: <CarryOutOutlined/>, title: '装机记录',
        items: [
            {key: 'batch-list', icon: <CarryOutOutlined/>, title: '装机批次记录', path: '/log/setup'},
            // {key: 'instance-manage', icon: <CarryOutOutlined/>, title: '单台主机装机记录', path: '/log/instance'},
        ]
    },
    {
        key: 'user', icon: <TeamOutlined/>, title: '用户管理',
        items: [
            {key: 'info', icon: <IdcardOutlined/>, title: '基本信息', path: '/user/info'},
            // {key: 'instance-batch-list2', icon: <CarryOutOutlined/>, title: '访问证书', path: '/user/info'},
            // {key: 'instance-batch-list3', icon: <CarryOutOutlined/>, title: '口令管理', path: '/user/info'},
            {key: 'permission-admin', icon: <HddFilled/>, title: '权限管理', path: '/user/permission'},
            {key: 'resource-group', icon: <RocketOutlined/>, title: '资源组管理', path: '/user/resource-group'},
            {key: 'group', icon: <RocketOutlined/>, title: '用户组管理', path: '/user/group'},
        ]
    },
]

export interface ICommonMenuComponentProps {
    keys: string[],
    collapseBase: boolean,
    area: ISimpleArea[],
    selectedArea?: ISimpleArea
}

const CommonMenuComponent: FC<ICommonMenuComponentProps> = ({keys, collapseBase, area, selectedArea}) => {
    const history = useHistory();
    const dispatch = useDispatch();
    const {user, roles} = useAccess();

    const menuClickHandle = (path: string) => ({key}: any) => {
        history.push(path);
    };

    const renderItem = ({key, path, title, icon}: IMenuItem): ReactElement => {
        const noDisplay = ['/area/area-zone']
        if (noDisplay.includes(path) && !(user === 'admin' || roles.includes("ADMIN"))) {
            return <></>
        }
        return <Item key={key} icon={icon} onClick={menuClickHandle(path)}>{title}</Item>

    };

    const renderGroup = (items: IMenuItem[] = [], groups: IMenuGroup[] = []): ReactElement[] =>
        items.map(item => renderItem(item))
            .concat(groups.map((({key, title, icon, items, groups}) => {
                let res: any[] = []
                if (key === 'user' && !(user === 'admin' || roles.includes("ADMIN"))) {
                    return <></>
                }
                res = renderGroup(items, groups);
                return <SubMenu key={key} icon={icon} title={title}>{res}</SubMenu>
            })));

    return (
        <Sider collapsible
               collapsed={collapseBase}
               onCollapse={collapsed => dispatch(collapseBaseAction(collapsed))}>
            <img src={require('../assets/logo.png')} style={{width: '100%', marginBottom: 50}} alt={"中国联通"}/>
            <Select
                style={{width: '100%', textAlign: 'center', color: 'rgba(255, 255, 255, .65)'}}
                bordered={false}
                value={selectedArea?.id}
                placeholder="请选择装机区域"
                onSelect={value => {
                    dispatch(selectArea(value))
                    dispatch(fetchPoolAction(value))
                    dispatch(fetchNetAreaAction(value))
                }}>
                {area.map(({id, name}, key) => <Select.Option key={key} value={id}
                                                              style={{textAlign: 'center'}}>{name}</Select.Option>)}
            </Select>
            <Menu theme="dark" selectedKeys={keys} defaultOpenKeys={keys} mode="inline">
                {renderGroup(menuItems, menuGroups)}
            </Menu>
        </Sider>
    );
}

const CommonMenu = connect(({
                                base: {collapseBase},
                                info: {area, selectedArea}
                            }: { base: IBaseModel, info: IInfoModel }) => ({
    collapseBase,
    area,
    selectedArea
}))(CommonMenuComponent);

export default CommonMenu;
