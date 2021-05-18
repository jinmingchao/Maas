import React, {FC, useEffect, useRef, useState} from 'react';
import Base from '../_base';
import {Button, Card, Drawer, Form, Modal, Select, Table, Tag} from "antd";
import { useHistory, connect, ICloudItem, deleteAreaAction,  IPPoolModel,fetchCloudAction, ISimpleArea, useDispatch, updateAreaAction, IPoolInfo, postAreaAction } from 'umi'
import ModalComponent from './_area-modal'
import { CheckCircleOutlined, CloseCircleOutlined  } from '@ant-design/icons'
import {fetchArea, IInfoModel} from "@/models/info";


const page: FC<{cloud: ICloudItem[], poolInfo: IPoolInfo, area: ISimpleArea[], selectedArea?: ISimpleArea } > = (
    { cloud, poolInfo, area, selectedArea }) => {
    const history = useHistory();
    const dispatch = useDispatch();

    const [form] = Form.useForm();
    const column = [
        { title: 'id', dataIndex: 'id', key: 'id' },
        { title: '名称', dataIndex: 'name', key: 'name' },
        { title: '域名', dataIndex: 'host', key: 'host' },
        { title: '用户', dataIndex: 'username', key: 'username' },
        // { title: '密码', dataIndex: 'password', key: 'password' },
        { title: '默认网络id', dataIndex: 'defaultCloudbootNetworkId', key: 'defaultCloudbootNetworkId' },
        { title: '同步发现新设备时间', dataIndex: 'syncInstanceInterval', key: 'syncInstanceInterval' },
        { title: '是否可用', dataIndex: 'enabled', key: 'enabled',
            render: (text: any, record: ICloudItem) => {
                if (record.enabled) {
                    return <Tag icon={<CheckCircleOutlined />} color="success"></Tag>
                } return <Tag icon={<CloseCircleOutlined  />} color="default"></Tag>
            }
        },
        { title: '操作', dataIndex: '', key: 'x',
            render: (text:any, record: ICloudItem, index: any) => <div>
                {/*<Button type={'primary'} onClick={() =>selectNetArea(record)}>查看IP</Button>*/}
                <Button type={'primary'} onClick={() => editNetArea(record)}>编辑</Button>
                <Button type={'primary'} danger onClick={() => deleteNetArea(record)}>删除</Button>
            </div>,
        }
    ]


    // const drawIPVisible: boolean = poolInfo['drawIPVisible'] || false;
    // const [useDrawerVisible, setDrawerVisible] = useState(drawIPVisible);

    const modalAddVisible: boolean = poolInfo['modalAddVisible'] || false;
    const [useModalAddVisible, setModalAddVisible] = useState(modalAddVisible);

    const modalEditVisible: boolean = poolInfo['modalEditVisible'] || false;
    const [useModalEditVisible, setModalEditVisible] = useState(modalEditVisible);

    const modalDelVisible: boolean = poolInfo['modalDelVisible'] || false;
    const [useModalDelVisible, setModalDelVisible] = useState(modalDelVisible);

    const editForm: any = poolInfo['editForm'] || {};
    const [useEditForm, setEditForm] = useState(editForm);

    const delId: string = poolInfo['areaId'] || {};
    const [useDelId, setDelId] = useState(delId);

    useEffect(() => {
        dispatch(fetchCloudAction({}))
    }, []);


    const editNetArea = (record: ICloudItem) => {
        // console.log(record)
        setEditForm(record);
        setModalEditVisible(true)
    }

    const addNetArea = () => {
        setModalAddVisible(true)
    }

    const deleteNetArea = (record: ICloudItem) => {
        // console.log(record)
        setModalDelVisible(true)
        setDelId(record.id)
    }

    const sleep = (ms:any) => new Promise(resolve => setTimeout(resolve, ms));

    const onSubmit = async (values:ICloudItem) => {
        // console.log(values)
        // console.log(values)
        dispatch(postAreaAction(values))
        setModalAddVisible(false)
          // console.log("I'm going to sleep for 1 second.");
        await sleep(1000);
        dispatch(fetchCloudAction({}))
        dispatch(fetchArea())
          // console.log('I woke up after 1 second.');
        // window.location.reload()
    }

    const onUpdate = async (values:ICloudItem) => {
        // console.log(values)
        dispatch(updateAreaAction(values))
        setModalEditVisible(false)
        await sleep(1000);
        dispatch(fetchCloudAction({}))
        // window.location.reload()
    }

    const onDelete = async () => {
        dispatch(deleteAreaAction(useDelId))
        setModalDelVisible(false)
        await sleep(1000);
        dispatch(fetchCloudAction({}))
        dispatch(fetchArea())
        // window.location.reload()
    }

    const oncancel = () => {
        setModalAddVisible(false)

    }

    const onCancelEdit = () => {
        setEditForm(null);
        setModalEditVisible(false)
        // window.location.reload()
        // const [form] = Form.useForm();
    }

    const onDeleteCancel = () => {
        setDelId('NULL')
        setModalDelVisible(false)
    }

    return (
        <Base title={'区域管理'} keys={['area', 'area-zone']} >
            <div style={{scrollbarWidth: 'none'}}>
                <Card title={"区域管理"} bordered={false}
                      extra={<Button type={'primary'} onClick={addNetArea}>新建</Button>}
                >
                    <Table
                        size="small"
                        dataSource={cloud.map((item, key) => ({...item, key}))}
                        columns={column}
                        // onRow={(record: IPNetAreaItem) => ({onClick: () => selectNetArea(record) })}
                        // onHeaderRow={(record) => ({onClick: () => selectNetArea(record) })}
                    />
                </Card>
            </div>
            <ModalComponent initValues={null} visible={useModalAddVisible} onCancel={oncancel} onCreate={onSubmit} />
            <ModalComponent initValues={useEditForm} visible={useModalEditVisible} onCancel={onCancelEdit} onCreate={onUpdate} />
            <Modal
              title="删除"
              visible={useModalDelVisible}
              onOk={onDelete}
              onCancel={onDeleteCancel}
            >
              <p>确认删除？</p>
            </Modal>


        </Base>
    );
};

const CenterPage = connect((
        { ippool: { cloud, poolInfo}, info: { area, selectedArea  } }: { ippool: IPPoolModel, info: IInfoModel }
    )=>(
        { cloud, poolInfo, area, selectedArea }
    ))(page);
export default CenterPage;
