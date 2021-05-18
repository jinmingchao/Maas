import React, {FC, useEffect, useState} from 'react';
import {connect, useDispatch, fetchAllRolePermissionAction} from 'umi';
import {IResourceGroupModel, putResourceGroupAction} from "@/models/permission";
import {Button, Form, Input, Modal} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";

const {Item} = Form;

const layout = {
    labelCol: {span: 6},
    wrapperCol: {span: 18},
};
const tailLayout = {
    wrapperCol: {offset: 6, span: 18},
};

const component: FC<any> = () => {
    const dispatch = useDispatch();
    const [createVisible, setCreateVisible] = useState<boolean>(false);
    const [tipsVisible, setTipsVisible] = useState<boolean>(false);

    const handleClickCreate = () => {
        setCreateVisible(true);
    };

    const handleCancel = () => {
        setCreateVisible(false);
    };

    const handleCreateOk = (values: { newResourceGroupName: string, newResourceGroupList: string }) => {
        const data: IResourceGroupModel = {
            resourceGroup: values.newResourceGroupName,
            resource: values.newResourceGroupList.split('\n').filter(r => r.replaceAll(' ', '').length > 0),
        }
        dispatch(putResourceGroupAction(data))
        setCreateVisible(false);
    };

    const showTips = () => {
        Modal.confirm({
            title: '资源命名规范',
            content: <><p>项目资源：PROJECT-[id]</p>
                <p>区域资源：AREA-[id]</p>
                <p>PXE资源：PXE-[id]</p>
                <p>区域资源：AREA-[id]</p>
                <p>典配资源：HARDWARE-[id]</p>
                <p>主机资源：[sn]</p>
            </>,
        });
    };

    useEffect(() => {
        dispatch(fetchAllRolePermissionAction)
    }, []);

    return (
        <>
            <Button type='primary' onClick={() => handleClickCreate()}>新增资源组</Button>
            <Modal
                title="新增资源组"
                visible={createVisible}
                onCancel={handleCancel}
                footer={null}
            >
                <Form
                    {...layout}
                    onFinish={handleCreateOk}>
                    <Item
                        label='资源组名称'
                        name='newResourceGroupName'
                        rules={[{required: true, message: '请输入资源组名称'}]}>
                        <Input/>
                    </Item>
                    <Item
                        label='资源列表'
                        name='newResourceGroupList'
                        rules={[{required: true, message: '请输入资源内容'}]}>
                        <Input.TextArea
                            rows={6}
                            placeholder='请输入资源内容，多个请换行'/>
                    </Item>
                    <Item {...tailLayout}>
                        <a onClick={showTips}><QuestionCircleOutlined/> 资源命名规范</a>
                    </Item>
                    <Item {...tailLayout}>
                        <Button type='primary' htmlType='submit'>
                            提交
                        </Button>
                    </Item>
                </Form>
            </Modal>
        </>
    );
};

const CreateResourceGroup = connect()(component);
export default CreateResourceGroup;
