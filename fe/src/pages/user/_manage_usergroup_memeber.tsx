import React, {useEffect} from 'react';
import {Modal, Table, Switch} from 'antd';
import {useSelector, IUserModel, useDispatch, getUserGroupMemeber, addUserGroupMemeber, removeUserGroupMemeber} from 'umi';

const ManageUserGroupMemeber = ({
    visible,
    id,
    onCancel,
    title,
}: {
    visible: boolean,
    id: number,
    onCancel: () => void,
    title: string
}) => {
    const dispatch = useDispatch();
    const { users, groupMemeber } = useSelector(({ user: { users, groupMemeber } }: {user: IUserModel}) => ({ users, groupMemeber }))

    useEffect(() => {
        if (id !== 0) {
            dispatch(getUserGroupMemeber(id));
        }
    }, [id]);

    const renderedUsers = users.map(user => ({ ...user, belong: groupMemeber.some(value => value === user.username) }));
    return (
        <Modal visible={visible} onCancel={onCancel} footer={null} title={`${title} - 成员管理`}>
            <Table
                size="small"
                columns={[
                    {title: '名称', dataIndex: 'name'},
                    {title: '是否隶属', dataIndex: 'belong', render: (belong: boolean, { username }) => <>
                        <Switch checked={belong} checkedChildren='隶属' unCheckedChildren="不隶属" onChange={() => {
                            if (belong) {
                                dispatch(removeUserGroupMemeber({ id, username }));
                            }
                            else {
                                dispatch(addUserGroupMemeber({ id, username }));
                            }
                        }} />
                    </>}
                ]}
                dataSource={renderedUsers} />
        </Modal>
    )
}

export default ManageUserGroupMemeber;
