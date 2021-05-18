import React, {FC, useEffect, useRef, useState} from 'react';
import Base from '../_base';
import {Button, Card, Form, Modal, Table, Tag, Row, Col, Input} from "antd";
import { useHistory, connect, selectArea, deleteNetAreaAction,  IPPoolModel,fetchCloudAction, ISimpleArea, fetchNetAreaAction, useDispatch, updateNetAreaAction, IPoolInfo, IPNetAreaItem, postNetAreaAction } from 'umi'
import ModalComponent from './_net-modal'
import { CheckCircleOutlined, CloseCircleOutlined, SearchOutlined  } from '@ant-design/icons'
import {IInfoModel} from "@/models/info";


const page: FC<{netArea: IPNetAreaItem[], poolInfo: IPoolInfo, area: ISimpleArea[], selectedArea?: ISimpleArea } > = (
    { netArea, poolInfo, area, selectedArea }) => {
    const history = useHistory();
    const dispatch = useDispatch();

    const initialSearchState: { keyword: string[] } =  { keyword: [] };
    const [searchState, setSearchState] = useState(initialSearchState);
    const [searchForm] = Form.useForm();

    const [form] = Form.useForm();
    const column = [
        { title: 'id', dataIndex: 'id', key: 'id' },
        { title: '名称', dataIndex: 'name', key: 'name' },
        { title: '描述', dataIndex: 'comment', key: 'comment' },
        { title: '所属cloud区域', dataIndex: 'areaId', key: 'areaId' ,
            render: (text: any, record: IPNetAreaItem) => {
                return selectedArea?.name
            }
        },
        { title: '是否可用', dataIndex: 'enabled', key: 'enabled',
            render: (text: any, record: IPNetAreaItem) => {
                if (record.enabled) {
                    return <Tag icon={<CheckCircleOutlined />} color="success"></Tag>
                } return <Tag icon={<CloseCircleOutlined  />} color="default"></Tag>
            }
        },
        { title: '操作', dataIndex: '', key: 'x',
            render: (text:any, record: IPNetAreaItem, index: any) => <div>
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

    const delId: number = poolInfo['delId'] || {};
    const [useDelId, setDelId] = useState(delId);

    // useEffect(() => {
    //     if (typeof selectedArea === 'undefined') {
    //         typeof area !== 'undefined' && dispatch(fetchAllNetAreaAction(undefined));
    //         return;
    //     } else {
    //         dispatch(fetchNetAreaAction(selectedArea.id))
    //     }
    // }, []);


    const editNetArea = (record: IPNetAreaItem) => {
        console.log(record)
        setEditForm(record);
        setModalEditVisible(true)
    }

    const addNetArea = () => {
        setModalAddVisible(true)
        dispatch(fetchCloudAction({}))
    }

    const deleteNetArea = (record: IPNetAreaItem) => {
        // console.log(record)
        setModalDelVisible(true)
        setDelId(record.id)
    }
    const sleep = (ms:any) => new Promise(resolve => setTimeout(resolve, ms));

    const onSubmit = async (values:IPNetAreaItem) => {
        // console.log(values)
        dispatch(postNetAreaAction({ netArea: values , area: selectedArea.id}))
        setModalAddVisible(false)
        await sleep(1000);
        // window.location.reload()
        if (selectedArea) {
            dispatch(fetchNetAreaAction(selectedArea.id))
            dispatch(selectArea(selectedArea.id))
        }
    }

    const onUpdate = async (values:IPNetAreaItem) => {
        // console.log(values)
        dispatch(updateNetAreaAction(values))
        setModalEditVisible(false)
        // window.location.reload()
        await sleep(1000);
        if (selectedArea) {
            dispatch(fetchNetAreaAction(selectedArea.id))
        }
    }

    const onDelete = async () => {
        dispatch(deleteNetAreaAction(useDelId))
        setModalDelVisible(false)
        // window.location.reload()
        await sleep(1000);
        if (selectedArea) {
            dispatch(fetchNetAreaAction(selectedArea.id))
        }
    }

    const oncancel = () => {
        setModalAddVisible(false)
        if (selectedArea) {
            dispatch(fetchNetAreaAction(selectedArea.id))
        }

    }

    const onCancelEdit = () => {
        setEditForm(null);
        setModalEditVisible(false)
        // window.location.reload()
        // const [form] = Form.useForm();
        if (selectedArea) {
            dispatch(fetchNetAreaAction(selectedArea.id))
        }
    }

    const onDeleteCancel = () => {
        setDelId(0)
        setModalDelVisible(false)
        if (selectedArea) {
            dispatch(fetchNetAreaAction(selectedArea.id))
        }
    }

    netArea = netArea
        .filter(({ name }) => searchState.keyword.length === 0 || searchState.keyword.some(keyword => name.indexOf(keyword) >= 0));

    return (
        <Base title={'网络区域'} keys={['area', 'net-area']} >
            <div style={{scrollbarWidth: 'none'}}>
                <Card title={"网络区域"} bordered={false}
                      extra={<Button type={'primary'} onClick={addNetArea}>新建</Button>}>

                    <Form form={searchForm} onFinish={({ keyword }) => {
                        if (keyword === '') {
                            setSearchState({ keyword: [] });
                        }
                        else {
                            setSearchState({ keyword: keyword.split(/,|\n|;/) });
                        }
                    }}>
                        <Row>
                            <Col offset={6} span={12}> <Form.Item name="keyword"><Input.TextArea placeholder="请输入网络区域名称进行筛选，多条请以回车、逗号、分号分隔" autoSize={{ minRows: 1 }} /></Form.Item> </Col>
                            <Col span={6}> <Button style={{margin: 0}} icon={<SearchOutlined />} htmlType="submit">查询</Button> </Col>
                        </Row>
                    </Form>

                    <Table
                        size="small"
                        dataSource={netArea.map((item, key) => ({...item, key}))}
                        columns={column}
                        // onRow={(record: IPNetAreaItem) => ({onClick: () => selectNetArea(record) })}
                        // onHeaderRow={(record) => ({onClick: () => selectNetArea(record) })}
                    />
                </Card>
            </div>

            {/*<Drawer width={800} placement={'right'} visible={useDrawerVisible} onClose={handleDrawerClose}>*/}
            {/*    <IpListComponent/>*/}
            {/*</Drawer>*/}
            <ModalComponent initValues={null} visible={useModalAddVisible} onCancel={oncancel} onCreate={onSubmit} cloud={area}/>
            <ModalComponent initValues={useEditForm} visible={useModalEditVisible} onCancel={onCancelEdit} onCreate={onUpdate} cloud={area}/>
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
        { ippool: { netArea, poolInfo}, info: { area, selectedArea  } }: { ippool: IPPoolModel, info: IInfoModel }
    )=>(
        { netArea, poolInfo, area, selectedArea }
    ))(page);
export default CenterPage;
