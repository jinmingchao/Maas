import React, {FC, useState} from 'react';
import {Button, Modal, Form, Input, Radio, Select, Switch} from 'antd';
import { CloseOutlined, CheckOutlined } from '@ant-design/icons';
import {IPNetAreaItem, IPPoolItem} from "@/models/ippool";
import {useForm} from "antd/es/form/Form";


interface CollectionCreateFormProps {
    visible: boolean;
    onCreate: (values: any) => void;
    onCancel: () => void;
    cloud: IPPoolItem[];
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
    return (
        <Modal
            visible={visible}
            title="创建IP"
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
                initialValues={initValues}
                // preserve={false}
            >
                <Form.Item
                    label="id"
                    name="id"
                    style={{display:"none"}}
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

                <Form.Item label={"所属IP池"} name={'poolAsst'} rules={[{required: true, message: 'Please select your poolAsst!'}]}>
                    <Select>
                        {cloud.map(({id, name}) => <Select.Option key={name} value={id}>{name}</Select.Option>)}
                    </Select>
                </Form.Item>
                <Form.Item label={"是否可用"} name={'enabled'}>
                    <Switch
                        checkedChildren={<CheckOutlined />}
                        unCheckedChildren={<CloseOutlined />}
                     />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default CollectionCreateForm;
