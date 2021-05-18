import React, {FC, useEffect} from 'react';
import {Modal, Form, Select, Divider, Input} from 'antd';
import {connect, IInfoModel, IInfoPXE, IInfoOperationSystem, ISimpleInstance, IPNetAreaItem, IPPoolModel, IPPoolItem} from 'umi';

const {Item} = Form;

const component: FC<{
    visible: boolean,
    onCancel: () => void,
    onOk: (entity: any) => void,
    pxe: IInfoPXE[],
    operationSystem: IInfoOperationSystem[],
    net: IPNetAreaItem[],
    instances: ISimpleInstance[],
    pool: IPPoolItem[]
}> = ({ visible, onCancel, onOk, instances, pxe, operationSystem, net, pool }) => {
    const [form] = Form.useForm();

    useEffect(() => {
        form.resetFields();
    }, [instances]);

    return (
        <Modal title="创建批次" visible={visible} onCancel={onCancel} onOk={() => form.submit()} width={800}>
            <Form labelCol={{span: 4}} wrapperCol={{span: 20}} form={form} onFinish={entity => onOk(entity)}>
                <Item label="批次名称" name="setup_name"><Input /></Item>
                <Item label="PXE" name="pxe" required>
                    <Select>{pxe.map(({ id, name }) => (<Select.Option key={id} value={id}>{name}</Select.Option>))}</Select>
                </Item>
                <Item label="操作系统" name="operation_system" required>
                    <Select>{operationSystem.map(({ id, name }) => (<Select.Option key={id} value={id}>{name}</Select.Option>))}</Select>
                </Item>
                <Divider>各主机详细配置</Divider>
                <Form.List name="instances" children={() => instances.filter(({ distributeable }) => distributeable).map(({ sn, netAreaId }) => {
                        const netArea = net.find(({ id }) => netAreaId === id);
                        return (
                            <>
                            <h5>{sn} - 网络区域：{netArea?.name}</h5>
                            <Form.List name={sn} children={() => [
                                <Item key="sn" hidden name="sn" initialValue={sn}><Input /></Item>,
                                <Item key="hostname" label="主机名" name="hostname" rules={[{ required: true, message: '请输入主机名' }]}><Input /></Item>,
                                <Item key="ippool" label="IP池" name="ippool" rules={[{ required: true, message: '请选择IP池' }]}>
                                    <Select>{pool.filter(({ netAsst }) => parseInt(netAsst) === netAreaId).map(({ id, name, gatewayIp, netmask }) => <Select.Option key={id} value={id}>{name} (网关：{gatewayIp}, 掩码: {netmask})</Select.Option>)}</Select>
                                </Item>,
                                <Item key="inner_ip" label="业务IP" name="inner_ip" rules={[{ required: true, message: '请输入业务IP' }]}><Input /></Item>,
                                <Item key="bound_type" label="Bond类型" name="bound_type"><Input /></Item>,
                                <Item key="bound_mac1" label="Bond MAC 1" name="bound_mac1" rules={[{ required: false, message: '请填写Bond MAC地址' }]}><Input /></Item>,
                                <Item key="bound_mac2" label="Bond MAC 2" name="bound_mac2" rules={[{ required: false, message: '请填写Bond MAC地址' }]}><Input /></Item>,
                            ]} />
                            </>
                        );
                    })} />
            </Form>
        </Modal>
    );
}

const InstanceBatchInstallForm = connect(({ info: { net, pxe, operationSystem }, ippool: { pool } }: { info: IInfoModel, ippool: IPPoolModel }) => ({
    net,
    pxe: pxe.filter(({ enabled }) => enabled),
    operationSystem: operationSystem.filter(({ enabled }) => enabled),
    pool
}))(component);
export default InstanceBatchInstallForm;
