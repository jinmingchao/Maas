import React, {FC, useState} from 'react';
import {Button, Modal, Form, Input, Radio, Select, Switch} from 'antd';
import { CloseOutlined, CheckOutlined } from '@ant-design/icons';
import {IPNetAreaItem, IPPoolItem} from "@/models/ippool";
import {useForm} from "antd/es/form/Form";

interface Values {
    title: string;
    description: string;
    modifier: string;
}

interface CollectionCreateFormProps {
    visible: boolean;
    onCreate: (values: any) => void;
    onCancel: () => void;
    cloud: IPNetAreaItem[];
    initValues: any;
}

const CollectionCreateForm: FC<CollectionCreateFormProps> = ({visible, onCreate, onCancel, cloud, initValues }) => {
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
            title="创建IP池"
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
                    label="name"
                    name="id"
                    style={{display:"none"}}
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
                    label="IP范围"
                    name="cidr"
                    rules={[{required: true, message: 'Please input your cidr!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="子网掩码"
                    name="netmask"
                    rules={[{required: true, message: 'Please input your netmask!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="网关"
                    name="gatewayIp"
                    rules={[{required: true, message: 'Please input your gatewayIp!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    label="vlanId"
                    name="vlanId"
                    rules={[{required: true, message: 'Please input your vlanId!'}]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item label={"所属网络区域"} name={'netAsst'} rules={[{required: true, message: 'Please select your netAsst!'}]}>
                    <Select>
                        {cloud.map(({id, name}) => <Select.Option key={name} value={id}>{name}</Select.Option>)}
                    </Select>
                </Form.Item>
                <Form.Item label={"是否可用"} name='enabled'>
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
