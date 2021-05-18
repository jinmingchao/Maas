import React, {FC, useEffect} from 'react';
import {Modal, Form, Select, Input } from 'antd';
import {connect, IInfoModel, ISimpleInstance, IPNetAreaItem, IPPoolModel, IPPoolItem} from 'umi';

const {Item} = Form;

const component: FC<{
    visible: boolean,
    onCancel: () => void,
    onOk: (entity: any) => void,
    net: IPNetAreaItem[],
    instance?: ISimpleInstance,
    pool: IPPoolItem[],
    disabled: boolean
}> = ({ visible, onCancel, onOk, instance, net, pool, disabled }) => {
    const [form] = Form.useForm();

    useEffect(() => {
        form.setFieldsValue({
            "net_area": instance?.netAreaId,
            "hostname": instance?.hostname,
            "ippool": instance?.ippool,
            'inner_ip': instance?.innerIp,
            "bound_mac1": instance?.boundMac1,
            "bound_mac2": instance?.boundMac2,
            "bound_type": instance?.boundType,
        });
    })

    return (
        <Modal title="编辑网络配置" visible={visible} onCancel={onCancel} onOk={() => !disabled && form.submit()} width={800}>
            <Form labelCol={{span: 4}} wrapperCol={{span: 20}} form={form} onFinish={entity => onOk(entity)}>
                <Item label="网络区域" name="net_area"><Select disabled={disabled}>{net.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}</Select></Item>
                <Item label="主机名" name="hostname"><Input disabled={disabled} /></Item>
                <Item label="IP池" name="ippool">
                    <Select disabled={disabled}>{pool.map(({ id, name, gatewayIp, netmask }) => <Select.Option key={id} value={id}>{name} (网关：{gatewayIp}, 掩码: {netmask})</Select.Option>)}</Select>
                </Item>
                <Item label="业务IP" name="inner_ip"><Input disabled={disabled} /></Item>
                <Item key="bound_type" label="Bond类型" name="bound_type"><Input disabled={disabled} /></Item>,
                <Item key="bound_mac1" label="Bond MAC 1" name="bound_mac1"><Input disabled={disabled} /></Item>
                <Item key="bound_mac2" label="Bond MAC 2" name="bound_mac2"><Input disabled={disabled} /></Item>
            </Form>
        </Modal>
    );
}

const NetInstanceForm = connect(({ info: { net }, ippool: { pool } }: { info: IInfoModel, ippool: IPPoolModel }) => ({ net, pool }))(component);
export default NetInstanceForm;
