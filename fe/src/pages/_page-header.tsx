import React, {FC} from 'react';
import {Avatar, Dropdown, Menu, PageHeader} from 'antd';
import {useAccess} from 'umi';
import {DownOutlined, LogoutOutlined, UserOutlined} from '@ant-design/icons';

const userMenu = (
    <Menu>
        <Menu.Item>
            <a target="_self" rel="noopener noreferrer" href='/cmdb/torn-mcloud/api/user/logout'>
                <LogoutOutlined/>&nbsp;&nbsp;Logout
            </a>
        </Menu.Item>
    </Menu>
);

const CommonPageHeaderComponent: FC<{ title: string }> = ({title}) => {
    const {user} = useAccess();
    return (<PageHeader
        title={<span style={{color: '#ffffff'}}>裸金属服务管理控制台</span>}
        subTitle={<span style={{color: '#ffffff'}}>{title}</span>}
        style={{background: 'rgb(62, 180, 255)'}}
        extra={[
            <Dropdown key='dropDown' overlay={userMenu}>
                <a style={{
                    color: 'white',
                }}
                   target="_blank"
                   className="ant-dropdown-link" onClick={e => e.preventDefault()}>
                    <Avatar size="small" icon={<UserOutlined/>}/>{user} <DownOutlined/>
                </a>
            </Dropdown>
        ]}
    />)
};

const CommonPageHeader = CommonPageHeaderComponent;

export default CommonPageHeader;
