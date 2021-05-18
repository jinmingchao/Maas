import React, {FC, useEffect} from 'react';
import {Modal, Form, Input } from 'antd';
import {connect} from 'umi';

const {Item} = Form;

const component: FC<{
    visible: boolean,
    username?: string,
    onCancel: () => void,
    onOk: (entity: any) => void,
}> = ({ visible, username, onCancel, onOk, }) => {
    const [form] = Form.useForm();

    useEffect(() => {
        form.setFieldsValue({
            "username": username,
            "password": ''
        });
    })

    return (
        <Modal title="修改带外账户" visible={visible} onCancel={onCancel} onOk={() => form.submit()} width={800}>
            <Form labelCol={{span: 4}} wrapperCol={{span: 20}} form={form} onFinish={entity => onOk(entity)}>
                <Item label="账号" name="username"><Input /></Item>
                <Item label="密码" name="password"><Input.Password /></Item>
            </Form>
        </Modal>
    );
}

const OobForm = connect()(component);
export default OobForm;
