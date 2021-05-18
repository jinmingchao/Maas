import React, {FC, useEffect, useState} from 'react';
import Base from '../_base';
import {connect, useDispatch} from 'umi';
import {
    deleteRoleForUserAction,
    fetchAllRoleAction, fetchRoleForUserAction,
    IAddRoleForUserModel,
    IPermissionModel,
    putRoleForUserAction
} from '@/models/permission';
import {Card, Modal, Select, Table} from 'antd';
import {IUserInfo, IUserModel} from "@/models/user";

const {Option} = Select;

const columns = [
    {
        title: '用户名',
        dataIndex: 'username',
        key: 'username',
    },
    {
        title: '中文名',
        dataIndex: 'name',
        key: 'name',
    },
    {
        title: '联系电话',
        dataIndex: 'phone',
        key: 'phone',
    },
    {
        title: '常用邮箱',
        dataIndex: 'email',
        key: 'email',
    },
    {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
    }
];

const page: FC<{ users: IUserInfo[], roles: string[], roleForUser: string[] }> = ({users, roles, roleForUser}) => {
    const dispatch = useDispatch();
    const [visible, setVisible] = useState(false);
    const [selectedRole, setRoleForUser] = useState<string[]>();
    const [curUser, setCurUser] = useState<string>('');

    useEffect(() => {
        dispatch(fetchAllRoleAction);
    }, []);

    useEffect(() => {
        setRoleForUser(roleForUser);
    }, [roleForUser]);

    const getRoleForUser = (user: string) => {
        dispatch(fetchRoleForUserAction(user));
        setCurUser(user);
        setVisible(true);
    };

    const onClickModalClose = () => setVisible(false);

    const onClickOk = () => {
        roleForUser.filter(role => !selectedRole?.includes(role)).forEach(role => {
            const info: IAddRoleForUserModel = {
                user: curUser,
                role: role
            };
            dispatch(deleteRoleForUserAction(info));
        });
        selectedRole?.filter(role => !roleForUser.includes(role))
            ?.forEach((role) => {
                const info: IAddRoleForUserModel = {
                    user: curUser,
                    role: role,
                }
                dispatch(putRoleForUserAction(info))
            })
        setVisible(false);
    };

    const onRoleForUserChange = (value: string[]) => setRoleForUser(value);

    return (
        <Base title="基本信息" keys={['user', 'info']}>
            <div style={{display: 'flex', flexDirection: 'row'}}>
                <Card title="用户列表" style={{width: '100%'}}>
                    <Table dataSource={users.map((user, index) => ({
                        ...user,
                        key: index,
                        action: <pre><a onClick={() => getRoleForUser(user.username)}>角色管理</a></pre>
                    }))} columns={columns}/>
                </Card>
            </div>
            <Modal
                title="运行结果"
                visible={visible}
                onOk={onClickOk}
                onCancel={onClickModalClose}

            >
                <Select
                    mode="multiple"
                    allowClear
                    style={{width: '100%'}}
                    placeholder="Please select role for user"
                    value={selectedRole}
                    onChange={onRoleForUserChange}
                >
                    {roles.map((role, id) => (
                        <Option key={role} value={role}>{role}</Option>
                    ))}
                </Select>
            </Modal>
        </Base>
    );
};

const UserPermissionAdmin = connect(({permission: {roles, roleForUser}, user: {users}}: { permission: IPermissionModel, user: IUserModel }) => ({
    roles,
    roleForUser,
    users
}))(page);
export default UserPermissionAdmin;
