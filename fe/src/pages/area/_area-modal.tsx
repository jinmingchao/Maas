import React, {FC, useState} from 'react';
import {Button, Modal, Form, Input, Radio, Select, Switch, DatePicker} from 'antd';
import { CloseOutlined, CheckOutlined } from '@ant-design/icons';
import {ISimpleArea} from "umi";
import moment from 'moment';

interface CollectionCreateFormProps {
    visible: boolean;
    onCreate: (values: any) => void;
    onCancel: () => void;
    initValues: any;
}

const CollectionCreateForm: FC<CollectionCreateFormProps> = ({visible, onCreate, onCancel, initValues }) => {
    const [form] = Form.useForm();
    const layout = {
        labelCol: {span: 8},
        wrapperCol: {span: 8},
    };
    const tailLayout = {
        wrapperCol: {offset: 8, span: 16},
    };
    let switchValue;
    if (initValues) {
        form.setFieldsValue(initValues)
        switchValue = initValues.enabled
    } else {
        switchValue = false;
    }
    return (
        <Modal
            visible={visible}
            title="创建Area"
            okText="提交"
            cancelText="取消"
            onCancel={onCancel}
            // destroyOnClose={true}
            onOk={() => {
                form
                    .validateFields()
                    .then(values => {
                        form.resetFields();
                        onCreate(values);
                    })
                    .catch(info => {
                        console.log('Validate Failed:', info);
                    })
            }}
        >
                {/*{...layout}*/}
            <Form
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 14 }}
                form={form}
                layout="horizontal"
                name="form_in_modal"
                // initialValues={initValues}
                // preserve={false}
            >
                <Form.Item
                    label="ID"
                    name="id"
                    rules={[{required: true, message: 'Please input your ID!'}]}
                    // style={{display:"none"}}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="名称"
                    name="name"
                    rules={[{required: true, message: 'Please input your name!'}]}
                >
                    <Input/>
                </Form.Item>

                <Form.Item
                    label="域名"
                    name="host"
                    rules={[{required: true, message: 'Please input your host!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="用户名"
                    name="username"
                    rules={[{required: true, message: 'Please input your username!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="密码"
                    name="password"
                    rules={[{required: true, message: 'Please input your password!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="默认网络id"
                    name="defaultCloudbootNetworkId"
                    rules={[{required: true, message: 'Please input your netWorkId!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="同步发现新设备时间"
                    name="syncInstanceInterval"
                    rules={[{required: true, message: 'Please input your syncInstanceInterval!'}]}
                >
                    {/*<DatePicker*/}
                    {/*  format="YYYY-MM-DD HH:mm:ss"*/}
                    {/*  // disabledDate={disabledDate}*/}
                    {/*  // disabledTime={disabledDateTime}*/}
                    {/*  showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}*/}
                    {/*/>*/}
                    <Input />
                </Form.Item>

                <Form.Item label={"是否可用"} name={'enabled'}>
                    <Switch
                        checkedChildren={<CheckOutlined />}
                        unCheckedChildren={<CloseOutlined />}
                        defaultChecked={switchValue}
                    />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default CollectionCreateForm;
