import React, {FC, useEffect, useRef, useState} from 'react';
import Base from '../_base';
import {Button, Card, Drawer, Form, Input, Modal, Select, Table, Tag} from "antd";
import { useHistory, connect, deletePoolAction,  IPPoolModel,fetchNetAreaAction, fetchPoolAction, useDispatch, updatePoolAction,fetchIPAction, IPItem, IPoolInfo, IPPoolItem, postPoolAction } from 'umi'
import ModalComponent from './_pool-modal'
import IpListComponent from './_pool-ip-list'
import {IPNetAreaItem} from "@/models/ippool";
import { CheckCircleOutlined, CloseCircleOutlined  } from '@ant-design/icons'
import {IInfoModel, ISimpleArea} from "@/models/info";



const page: FC<{pool: IPPoolItem[], ipList: IPItem[], poolInfo: IPoolInfo, netArea: IPNetAreaItem[], selectedArea: ISimpleArea }> = ({
    pool, ipList, poolInfo, netArea, selectedArea }) => {
    const history = useHistory();
    const dispatch = useDispatch();

    netArea.filter(({id, name}) => {})

    const [form] = Form.useForm();
    const column = [
        { title: 'id', dataIndex: 'id', key: 'id' },
        { title: '名称', dataIndex: 'name', key: 'name' },
        { title: 'IP范围', dataIndex: 'cidr', key: 'cidr' },
        { title: '子网掩码', dataIndex: 'netmask', key: 'netmask' },
        { title: '网关', dataIndex: 'gatewayIp', key: 'gatewayIp' },
        { title: 'vlanId', dataIndex: 'vlanId', key: 'vlanId' },
        { title: '所属网络区域', dataIndex: 'netAsst', key: 'netAsst',
            render: (text: any, record: IPPoolItem) => {
                let res;
                netArea.map(({id, name}) => {
                    console.log(id)
                    console.log(record.netAsst)
                    console.log(name)
                    if (id == parseInt(record.netAsst)) {
                        res = name;
                    }
                })
                return res;
            }
        },
        { title: '是否可用', dataIndex: 'enabled', key: 'enabled',
            render: (text: any, record: IPPoolItem) => {
                if (record.enabled) {
                    return <Tag icon={<CheckCircleOutlined />} color="success"></Tag>
                } return <Tag icon={<CloseCircleOutlined  />} color="default"></Tag>
            }

        },
        { title: '操作', dataIndex: '', key: 'x',
            render: (text:any, record: IPPoolItem, index: any) => <div>
                <Button type={'primary'} onClick={() =>selectPool(record)}>查看IP</Button>
                <Button type={'primary'} onClick={() => editPool(record)}>编辑</Button>
                <Button type={'primary'} danger onClick={() => deletePool(record)}>删除</Button>
            </div>,
        }
    ]


    const drawIPVisible: boolean = poolInfo['drawIPVisible'] || false;
    const [useDrawerVisible, setDrawerVisible] = useState(drawIPVisible);

    const modelAddVisible: boolean = poolInfo['modelAddVisible'] || false;
    const [useModelAddVisible, setModelAddVisible] = useState(modelAddVisible);

    const modelEditVisible: boolean = poolInfo['modelEditVisible'] || false;
    const [useModelEditVisible, setModelEditVisible] = useState(modelEditVisible);

    const modalDelVisible: boolean = poolInfo['modalDelVisible'] || false;
    const [useModalDelVisible, setModalDelVisible] = useState(modalDelVisible);

    const selectedPool: IPPoolItem = poolInfo['selectPool'] || 0;
    const [useSelectedPool, setSelectedPool] = useState(selectedPool);

    const editForm: any = poolInfo['editForm'] || {};
    const [useEditForm, setEditForm] = useState(editForm);

    const delId: number = poolInfo['delId'] || {};
    const [useDelId, setDelId] = useState(delId);

    // useEffect(() => {
    //     dispatch(fetchAllPoolAction({}));
    // }, []);

    const selectPool = (record: IPPoolItem) => {
        console.log(record)
        dispatch(fetchIPAction(record.id))
        setSelectedPool(record)
        setDrawerVisible(true)
    }

    const editPool = (record: IPPoolItem) => {
        // console.log(record)
        setEditForm(record);
        dispatch(fetchNetAreaAction(selectedArea.id))
        setModelEditVisible(true)
    }

    const addPool = () => {
        dispatch(fetchNetAreaAction(selectedArea.id))
        setModelAddVisible(true)
    }

    const deletePool = (record: IPPoolItem) => {
        // console.log(record)
        setModalDelVisible(true)
        setDelId(record.id)
    }

    // const layout = {
    //   labelCol: { span: 8 },
    //   wrapperCol: { span: 8 },
    // };
    // const tailLayout = {
    //   wrapperCol: { offset: 8, span: 16 },
    // };

    const handleDrawerClose = () => {
        setDrawerVisible(false)
    }

    const onSubmit = async (values:IPPoolItem) => {
        // console.log(values)
        dispatch(postPoolAction(values))
        setModelAddVisible(false)
        await sleep(1000);
        dispatch(fetchPoolAction(selectedArea.id))
    }

    const onUpdate = async (values:IPPoolItem) => {
        // console.log(values)
        dispatch(updatePoolAction(values))
        setModelEditVisible(false)
        await sleep(1000);
        dispatch(fetchPoolAction(selectedArea.id))
    }

    const onDelete = async () => {
        dispatch(deletePoolAction(useDelId))
        setModalDelVisible(false)
        await sleep(1000);
        dispatch(fetchPoolAction(selectedArea.id))
    }

    const sleep = (ms:any) => new Promise(resolve => setTimeout(resolve, ms));

    const oncancel = () => {
        setModelAddVisible(false)

    }

    const onCancelEdit = () => {
        setEditForm(null);
        setModelEditVisible(false)
        // window.location.reload()
        // const [form] = Form.useForm();
    }

    const onDeleteCancel = () => {
        setDelId(0)
        setModalDelVisible(false)

    }

    return (
        <Base title={'IP池管理'} keys={['area','ip-pool']} >
            <div style={{scrollbarWidth: 'none'}}>
                <Card title={"ip池管理"} bordered={false}
                      extra={<Button type={'primary'} onClick={addPool}>新建</Button>}
                >
                    <Table
                        size="small"
                        dataSource={pool.map((item, key) => ({...item, key}))}
                        columns={column}
                        // onRow={(record: IPPoolItem) => ({onClick: () => selectPool(record) })}
                        // onHeaderRow={(record) => ({onClick: () => selectPool(record) })}
                    />
                </Card>
            </div>
            <Drawer width={700} placement={'right'} visible={useDrawerVisible} onClose={handleDrawerClose}>
                <IpListComponent selectedPool={useSelectedPool}/>
            </Drawer>
            <ModalComponent initValues={null} visible={useModelAddVisible} onCancel={oncancel} onCreate={onSubmit} cloud={netArea}/>
            <ModalComponent initValues={useEditForm} visible={useModelEditVisible} onCancel={onCancelEdit} onCreate={onUpdate} cloud={netArea}/>
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

const PoolPage = connect((
        { ippool: { pool, ipList, netArea, poolInfo}, info: { net, selectedArea } }: { ippool: IPPoolModel, info: IInfoModel }
    )=>(
        { pool, ipList, poolInfo, netArea, net, selectedArea }
    ))(page);
export default PoolPage;
