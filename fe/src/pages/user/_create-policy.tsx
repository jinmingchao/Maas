import React, {FC, useEffect, useState} from 'react';
import {Button, Col, Form, Input, Modal, Row, Select} from 'antd';
import {connect, useDispatch} from 'umi';
import {
    fetchAllResourceGroupAction,
    IPermissionModel,
    IRoleModel,
    putPolicyAction, putUserPolicyAction,
} from "@/models/permission";
import {IUserInfo, IUserModel} from "@/models/user";

const {Item} = Form;
const {Option} = Select;

const layout = {
    labelCol: {span: 6},
    wrapperCol: {span: 18},
};
const tailLayout = {
    wrapperCol: {offset: 6, span: 18},
};

const component: FC<{ resourceGroupList: string[], users: IUserInfo[] }> = ({resourceGroupList, users}) => {
    const dispatch = useDispatch();
    const [visible, setVisible] = useState<boolean>(false);
    const [sub, setSub] = useState<string>('role');
    const [objType, setObjType] = useState<string>('resourceGroup');

    useEffect(() => {
        dispatch(fetchAllResourceGroupAction);
    }, [])

    const handleCancel = () => setVisible(false);

    const handleAddPolicy = (values: IRoleModel) => {
        values = {
            sub: values.sub,
            obj: values.obj,
            act: values.act,
        }

        if (sub.match('role')) {
            dispatch(putPolicyAction(values));
        } else if (sub.match('user')) {
            dispatch(putUserPolicyAction(values));
        }
        setVisible(false);
    }

    const handleSubSelect = (value: string) => setSub(value);
    const handleObjTypeSelect = (value: string) => setObjType(value);

    const selectSubType = (
        <Select defaultValue="role" className="select-before" onSelect={handleSubSelect}>
            <Option key='user' value='user'>用户</Option>
            <Option key='role' value='role'>角色</Option>
        </Select>
    );

    const selectResourceBefore = (
        <Select defaultValue="resourceGroup" className="select-before" onSelect={handleObjTypeSelect}>
            <Option key='resource' value='resource'>资源</Option>
            <Option key='resourceGroup' value='resourceGroup'>资源组</Option>
        </Select>
    );

    const selectResourceGroup = (
        <Select>
            {resourceGroupList.map(group =>
                <Option key={group} value={group}>{group}</Option>)}
        </Select>
    );

    const selectUser = (
        <Select>
            {users.map(user =>
                <Option key={user.username} value={user.username}>{user.username}</Option>)}
        </Select>
    );

    return (
        <>
            <Button type="primary" onClick={() => setVisible(true)}>新增权限策略</Button>
            <Modal
                title='新增权限策略'
                visible={visible}
                onCancel={handleCancel}
                footer={null}
            >
                <Form
                    {...layout}
                    onFinish={handleAddPolicy}>
                    <Item label="角色/用户" name='subType' rules={[{required: true}]} initialValue='1'>
                        <Row>
                            <Col span={6}>{selectSubType}</Col>
                            <Col span={18}>
                                <Item noStyle name='sub' rules={[{required: true, message: '请输入用户/角色'}]}>
                                {sub.match('user')
                                    ? selectUser
                                    : <Input/>}
                                </Item>
                            </Col>
                        </Row>
                    </Item>
                    <Item label='资源组/资源' name='resourceType' rules={[{required: true}]} initialValue='1'>
                        <Row>
                            <Col span={6}>{selectResourceBefore}</Col>
                            <Col span={18}>
                                <Item noStyle name='obj' rules={[{required: true, message: '请输入资源组/资源'}]}>
                                    {objType.match('resourceGroup')
                                        ? selectResourceGroup
                                        : <Input/>}
                                </Item>
                            </Col>
                        </Row>
                    </Item>
                    <Item label='权限' name='act' rules={[{required: true, message: '请选择权限'}]}>
                        <Select>
                            {
                                ['read', 'write', 'execute'].map(act =>
                                    <Option value={act} key={act}>{act}</Option>
                                )
                            }
                        </Select>
                    </Item>
                    <Item {...tailLayout}>
                        <Button type='primary' htmlType="submit">添加</Button>
                    </Item>
                </Form>
            </Modal>
        </>
    );
};

const CreatePolicy = connect(({permission: {resourceGroupList}, user: {users}}: { permission: IPermissionModel, user: IUserModel }) => ({
    resourceGroupList,
    users
}))(component);
export default CreatePolicy;
