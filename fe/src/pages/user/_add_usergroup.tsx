import React, {useEffect} from 'react';
import {Modal, Form, Input, Button} from 'antd';

const AddUserGroup = ({
    visible,
    onCancel,
    onSubmit,
}: {
    visible: boolean
    onCancel: () => void,
    onSubmit: (entity: any) => void
}) => {

    const headPart = {
        labelCol: { span: 4 },
        wrapperCol: { span: 16 },
    }

    const [form] = Form.useForm();

    useEffect(() => {
        form.resetFields();
    }, []);

    return (
        <Modal visible={visible} onCancel={onCancel} footer={null} title="添加用户组">
            <Form form={form} onFinish={entity => { onSubmit(entity); onCancel(); }}>
                <Form.Item name="name" label="用户组名称" {...headPart}>
                    <Input />
                </Form.Item>

                <Form.Item {...headPart}>
                    <Button type="primary" htmlType="submit">提交</Button>
                </Form.Item>
            </Form>
        </Modal>
    );
}

export default AddUserGroup;
