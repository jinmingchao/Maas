import React, {FC, useEffect, useRef, useState} from 'react';
import {Button, List, Modal, Popconfirm, Select, Table, Tag} from 'antd';
import {connect, useDispatch, fetchAllRolePermissionAction} from 'umi';
import {
    deletePolicyAction, deleteRoleAction,
    deleteRoleForUserAction, fetchAllRoleAction,
    fetchUsersForRoleAction,
    IAddRoleForUserModel,
    IPermissionModel,
    IRoleModel,
    putRoleForUserAction
} from "@/models/permission";
import {IUserInfo, IUserModel} from "@/models/user";

const {Option} = Select;

const roleColumns = [
    {title: 'index', dataIndex: 'id', key: 'id'},
    {title: '角色/用户', dataIndex: 'sub', key: 'sub'},
    {title: '资源/资源组', dataIndex: 'obj', key: 'obj'},
    {title: '权限', dataIndex: 'act', key: 'act'},
    {title: '操作', dataIndex: 'action', key: 'action'},
];

const component: FC<{ rolesWithPer: IRoleModel[], roleForUser: string[], roleUsers: string[], users: IUserInfo[], roles: string[] }> =
    ({rolesWithPer, roleForUser, roleUsers, users, roles}) => {
        const dispatch = useDispatch();
        const selectedUser = useRef<string[]>([]);
        const [visible, setVisible] = useState<boolean>(false);
        const [currentRole, setCurrentRole] = useState<string>('');

        useEffect(() => {
            dispatch(fetchAllRolePermissionAction);
            dispatch(fetchAllRoleAction);
        }, []);

        const fetchUserForRole = (role: string) => {
            dispatch(fetchUsersForRoleAction(role));
            setCurrentRole(role);
            setVisible(true);
        }

        const handleCancel = () => setVisible(false);

        const handleChange = (value: any) => selectedUser.current = value;

        const handleDeletePolicy = (role: string) => dispatch(deleteRoleAction(role));

        const handleAddUserForRole = () => {
            Modal.confirm({
                title: '编辑用户',
                content: <Select
                    mode='multiple'
                    style={{width: '100%'}}
                    placeholder='请选择用户'
                    defaultValue={roleUsers}
                    onChange={(value) => handleChange(value)}
                >
                    {
                        users.map(({username}) => <Option key={username} value={username}>{username}</Option>)
                    }
                </Select>,
                onOk() {
                    setVisible(false);
                    roleUsers
                        .filter(user => !selectedUser.current.includes(user))
                        .forEach(user => {
                            const data: IAddRoleForUserModel = {
                                role: currentRole,
                                user: user
                            }
                            dispatch(deleteRoleForUserAction(data));
                        });
                    selectedUser.current
                        ?.filter(user => !roleUsers.includes(user))
                        .forEach(user => {
                            const data: IAddRoleForUserModel = {
                                role: currentRole,
                                user: user,
                            }
                            dispatch(putRoleForUserAction(data))
                        })
                },
            });
        }

        return (
            <>
                <Table
                    columns={roleColumns}
                    dataSource={rolesWithPer?.map((role, index) => ({
                        key: index,
                        id: index + 1,
                        ...role,
                        action: <>{roles.includes(role.sub) ?
                            <><Tag color="#108ee9"
                                   onClick={() => fetchUserForRole(role.sub)}>查看包含用户</Tag>&nbsp;&nbsp;</> : <></>}
                            <Popconfirm
                                title="确认删除此用户/角色吗?"
                                onConfirm={() => handleDeletePolicy(role.sub)}
                                okText="确认"
                                cancelText="取消">
                                   <Tag color="#108ee9">删除</Tag>
                            </Popconfirm>
                        </>
                    }))}
                />
                <Modal
                    title='用户列表'
                    visible={visible}
                    onOk={handleCancel}
                    onCancel={handleCancel}>
                    <Button type='primary' onClick={() => handleAddUserForRole()}>编辑用户</Button>
                    <List size="small"
                          bordered
                          dataSource={roleUsers}
                          renderItem={item => <List.Item>{item}</List.Item>}
                          style={{height: "50vh", overflowY: "scroll"}}/>
                </Modal>
            </>
        );
    };

const RoleAdmin = connect(({permission: {rolesWithPer, roleForUser, roleUsers, roles}, user: {users}}: { permission: IPermissionModel, user: IUserModel }) => ({
    rolesWithPer,
    roleForUser,
    roleUsers,
    users,
    roles
}))(component);
export default RoleAdmin;
