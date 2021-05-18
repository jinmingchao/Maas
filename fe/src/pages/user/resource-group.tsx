import React, {FC, useEffect, useRef, useState} from 'react';
import Base from '../_base';
import {connect, useDispatch} from 'umi';
import {
    deleteResourceAction,
    deleteResourceGroupAction,
    fetchAllResourceGroupAction,
    fetchOneResourceGroupListAction,
    IPermissionModel,
    IResourceGroupModel,
    putResourceGroupAction
} from '@/models/permission';
import {Breadcrumb, Button, Col, Popconfirm, Input, List, Modal, Row, Table, Tag} from 'antd';
import CreateResourceGroup from "@/pages/user/_create-resource-group";
import {CloseOutlined} from '@ant-design/icons';

const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
    },
    {
        title: '资源组',
        dataIndex: 'resource',
        key: 'resource',
    },
    {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
    },
];

const page: FC<{ resourceGroupList: string[], resourceList: string[] }> = ({resourceGroupList, resourceList}) => {
    const dispatch = useDispatch();
    const [visible, setVisible] = useState<boolean>(false);
    const [curResourceGroup, setCurResourceGroup] = useState<string>('');
    const addOneResourceString = useRef<string>('');

    useEffect(() => {
        dispatch(fetchAllResourceGroupAction);
    }, []);

    const getResourceForGroup = (resourceGroup: string) => {
        dispatch(fetchOneResourceGroupListAction(resourceGroup));
        setCurResourceGroup(resourceGroup);
        setVisible(true);
    };


    const handleCancel = () => {
        setVisible(false);
    };

    const delResource = (resource: string, resourceGroup: string) => {
        const info = {
            resource: resource,
            resourceGroup: resourceGroup,
        }
        dispatch(deleteResourceAction(info));
    };

    const handleDeleteGroup = (group: string) => dispatch(deleteResourceGroupAction(group));
    const handleAddOneResource = () => {
        addOneResourceString.current = '';
        Modal.confirm({
            title: '添加资源',
            content: <Input.TextArea placeholder='请输入要添加的资源，多个资源请换行输入。'
                                     onChange={e => addOneResourceString.current = e.target.value}/>,
            onOk() {
                const data: IResourceGroupModel = {
                    resourceGroup: curResourceGroup,
                    resource: addOneResourceString.current.split('\n').filter(r => r.replaceAll(' ', '').length > 0),
                };
                dispatch(putResourceGroupAction(data));
                setVisible(false);
            },
        });
    };

    // const onRoleForUserChange = (value: string[]) => setRoleForUser(value);

    return (
        <Base title={'资源组管理'} keys={['user', 'resource-group']}>
            <div style={{scrollbarWidth: 'none'}}>
                <CreateResourceGroup/>
                <Table dataSource={resourceGroupList.map((resourceGroup, index) => ({
                    key: index,
                    id: index + 1,
                    resource: resourceGroup,
                    action: <span>
                        <Tag color="#108ee9" onClick={() => getResourceForGroup(resourceGroup)}>查看组内资源</Tag>&nbsp;&nbsp;
                        <Popconfirm
                            title="确认删除此资源组吗?"
                            onConfirm={() => handleDeleteGroup(resourceGroup)}
                            okText="确认"
                            cancelText="取消">
                            <Tag color="#108ee9">删除</Tag></Popconfirm>
                    </span>,
                }))} columns={columns}/>
                <Modal
                    title="资源列表"
                    visible={visible}
                    onOk={handleCancel}
                    onCancel={handleCancel}
                >
                    <Row>
                        <Col span={8}>
                            <Breadcrumb>
                                <Breadcrumb.Item>
                                    资源组
                                </Breadcrumb.Item>
                                <Breadcrumb.Item>
                                    {curResourceGroup}
                                </Breadcrumb.Item>
                            </Breadcrumb>
                        </Col>
                        <Col span={5} offset={11}>
                            <Button type='primary' onClick={() => handleAddOneResource()}>新增资源</Button>
                        </Col>
                    </Row>
                    <List
                        size="small"
                        header={<div>资源列表</div>}
                        bordered
                        dataSource={resourceList}
                        renderItem={item =>
                            <List.Item
                                actions={[<a onClick={() => delResource(item, curResourceGroup)}><CloseOutlined/></a>]}>
                                {item}
                            </List.Item>}
                        style={{height: "50vh", overflowY: "scroll"}}
                    />
                </Modal>

            </div>
        </Base>
    );
};

const ResourceGroup = connect(({permission: {resourceGroupList, resourceList}}: { permission: IPermissionModel }) => ({
    resourceGroupList,
    resourceList,
}))(page);
export default ResourceGroup;
