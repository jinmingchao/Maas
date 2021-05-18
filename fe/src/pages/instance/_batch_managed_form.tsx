import React, {FC, useEffect} from 'react';
import {connect, IInfoModel, IInfoHardware, IPNetAreaItem, useDispatch, fetchAllProject, IProjectModel, IProjectItem} from 'umi';
import {Form, Modal, Select, Input, Divider} from 'antd';

const {Item} = Form;

const component: FC<{
    visible: boolean,
    onCancel: () => void,
    onOk: (entity: any) => void,
    hardware: IInfoHardware[],
    net: IPNetAreaItem[],
    projects: IProjectItem[]
}> = ({ visible, onCancel, onOk, hardware, net, projects }) => {
    const [form] = Form.useForm();
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(fetchAllProject());
    }, [])

    return (
        <Modal title="批量纳管" visible={visible} onCancel={onCancel} onOk={() => form.submit()} width={800}>
            <Form labelCol={{span: 4}} wrapperCol={{span: 20}} form={form} onFinish={entity => onOk(entity)}>
                <Item label="硬件典配" name="hardware" rules={[{ required: true, message: '请选择硬件典配' }]}>
                    <Select>{hardware.map(({id, name}) => <Select.Option key={id} value={id}>{name}</Select.Option>)}</Select>
                </Item>
                <Item label="网络区域" name="netarea" rules={[{ required: true, message: '请选择网络区域' }]}>
                    <Select>{net.map(({id, name}) => <Select.Option key={id} value={id}>{name}</Select.Option>)}</Select>
                </Item>
                <Item label="带外IP" name="oob_ip"><Input /></Item>
                <Item label="带外账号" name="oob_username" rules={[{ required: true, message: '请填写带外账号' }]}><Input /></Item>
                <Item label="带外密码" name="oob_password" rules={[{ required: true, message: '请填写带外密码' }]}><Input.Password /></Item>

                <Divider>物理位置</Divider>

                <Item label="数据中心" name="place_dc"><Input /></Item>
                <Item label="楼宇" name="place_building"><Input /></Item>
                <Item label="机房" name="place_room"><Input /></Item>
                <Item label="机柜" name="place_cabinet"><Input /></Item>
                <Item label="U位" name="place_u"><Input /></Item>
                <Item label="占U位个数" name="place_u_count"><Input /></Item>

                <Divider>项目</Divider>

                <Item label="项目名" name="project_id">
                    <Select>{projects.map(({ name, id }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}</Select>
                </Item>
                <Item label="资产归属" name="belong_with"><Input /></Item>
            </Form>
        </Modal>
    )
}

const InstanceBatchManagedForm = connect(({ info: { hardware, net }, project: { projects } }: { info: IInfoModel, project: IProjectModel }) => ({
    hardware: hardware.filter(({enabled}) => enabled),
    net,
    projects }))(component);
export default InstanceBatchManagedForm;
