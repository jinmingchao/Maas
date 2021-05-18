import React, {useState, useEffect} from 'react';
import Base from '../_base';
import {Card, PageHeader, Button, Table} from 'antd';
import AddUserGroup from './_add_usergroup';
import {useDispatch, addUserGroup, getUserGroup, useSelector, IUserModel, IUserGroup, removeUserGroup, usersAction} from 'umi';
import ManageUserGroupMemeber from './_manage_usergroup_memeber';
import ManageUserGroupProject from './_manage_usergroup_project';

export default () => {
    const dispatch = useDispatch();

    const [addUserGroupVisible, setAddUserGroupVisible] = useState(false);
    const initialUserGroupMemeber = { visible: false, title: '', id: 0 };
    const [manageUserGroupMemeberVisible, setManageUserGroupMemeber] = useState(initialUserGroupMemeber);
    const initialUserGroupProject = { visible: false, title: '', id: 0 };
    const [manageUserGroupProjectVisible, setManageUserGroupProject] = useState(initialUserGroupProject);

    const { groups } = useSelector(({user: { groups }}: {user: IUserModel}) => ({ groups }))

    useEffect(() => {
        dispatch(getUserGroup());
        dispatch(usersAction());
    }, [])

    return (
        <Base title={'用户组'} keys={[ 'user', 'group' ]}>
            <AddUserGroup
                visible={addUserGroupVisible}
                onCancel={() => setAddUserGroupVisible(false)}
                onSubmit={({name}) => dispatch(addUserGroup(name))} />
            <ManageUserGroupMemeber
                visible={manageUserGroupMemeberVisible.visible}
                id={manageUserGroupMemeberVisible.id}
                title={manageUserGroupMemeberVisible.title}
                onCancel={() => setManageUserGroupMemeber(initialUserGroupMemeber)} />
            <ManageUserGroupProject
                visible={manageUserGroupProjectVisible.visible}
                id={manageUserGroupProjectVisible.id}
                title={manageUserGroupProjectVisible.title}
                onCancel={() => setManageUserGroupProject(initialUserGroupProject)} />

            <Card>
                <PageHeader
                    title="用户组"
                    extra={[
                        <Button key="add" onClick={() => setAddUserGroupVisible(true)}>添加用户组</Button>
                    ]} />
            </Card>

            <Card>
                <Table
                    size="small"
                    columns={[
                        {title: 'ID', dataIndex: 'id'},
                        {title: '名称', dataIndex: 'name'},
                        {title: '操作', dataIndex: 'operation', render: (_: any, { id, name }: IUserGroup) => (
                            <>
                                <Button size="small" type="link" onClick={() => dispatch(removeUserGroup(id))}>删除</Button>
                                <Button size="small" type="link" onClick={() => setManageUserGroupMemeber({ visible: true, title: name, id })}>成员管理</Button>
                                <Button size="small" type="link" onClick={() => setManageUserGroupProject({ visible: true, title: name, id })}>项目管理</Button>
                            </>
                        )},
                    ]}
                    dataSource={groups} />
            </Card>
        </Base>
    )
}
