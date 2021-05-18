import React, {useEffect} from 'react';
import {Modal, Table, Switch} from 'antd';
import {useSelector, IUserModel, useDispatch, IProjectModel, fetchAllProject, getUserGroupProject, removeUserGroupProject, addUserGroupProject} from 'umi';

const ManageUserGroupProject = ({
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
    const { groupProject, projects } = useSelector(({ user: { groupProject }, project: { projects } }: {user: IUserModel, project: IProjectModel}) => ({ groupProject, projects }))

    useEffect(() => {
        if (id !== 0) {
            dispatch(getUserGroupProject(id));
        }
    }, [id]);

    useEffect(() => {
        dispatch(fetchAllProject());
    }, []);

    const renderedProjects = projects.map(project => ({ ...project, belong: groupProject.some(({id}) => id === project.id) }));
    return (
        <Modal visible={visible} width='50vw' onCancel={onCancel} footer={null} title={`${title} - 成员管理`}>
            <Table
                size="small"
                columns={[
                    {title: '名称', dataIndex: 'name'},
                    {title: '是否隶属', dataIndex: 'belong', render: (belong: boolean, { id: pid }) => <>
                        <Switch checked={belong} checkedChildren='隶属' unCheckedChildren="不隶属" onChange={() => {
                            if (belong) {
                                dispatch(removeUserGroupProject({ id, pid }));
                            }
                            else {
                                dispatch(addUserGroupProject({ id, pid }));
                            }
                        }} />
                    </>}
                ]}
                dataSource={renderedProjects} />
        </Modal>
    )
}

export default ManageUserGroupProject;
