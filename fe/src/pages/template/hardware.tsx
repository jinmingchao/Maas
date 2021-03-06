import React, {FC, useState, useEffect} from 'react';
import Base from '../_base';
import {IInfoModel, connect, IInfoHardware, useDispatch, ISimpleArea, syncAreaInfo, IProjectModel, IProjectItem, fetchAllProject, appendProjectHardware, selectArea} from 'umi';
import {Table, PageHeader, Button, Tag, Select, Popconfirm, Modal, Card, List, Form, Row, Col, Input, Divider} from 'antd';
import {
    RetweetOutlined
} from '@ant-design/icons';
import { SearchOutlined } from '@ant-design/icons';
import {putHardwareEnabled, getHardwareTplField} from '@/service/info';
import AddHardware from './hardware-add';

const page: FC<{ selectedArea?: ISimpleArea, hardware: IInfoHardware[], projects: IProjectItem[] }> = ({ selectedArea, hardware, projects }) => {
    const dispatch = useDispatch();

    const initialSelectedHardwareState: IInfoHardware[] = [];
    const [selectedHardware, setSelectedHardware] = useState(initialSelectedHardwareState);

    const initialSelectedProject: any = undefined;
    const [selectedProject, setSelectedProject] = useState(initialSelectedProject);

    const initialDetailState: {visible: boolean, payload: string} = {visible: false, payload: '[{"Name": "", "Data": []}]'}
    const [detailState, setDetailState] = useState(initialDetailState);

    const initialSearchState: { keyword: string[] } =  { keyword: [] };
    const [searchState, setSearchState] = useState(initialSearchState);
    const [searchForm] = Form.useForm();

    const initialShowAllState: boolean = false;
    const [showAllState, setShowAllState] = useState(initialShowAllState);

    const initialEditState: { visible: boolean, id?: number, name?: string, company?: string, originData?: string, tpl?:string } = { visible: false }
    const [editState, setEditState] = useState(initialEditState);
    const initialCloneState: { visible: boolean, id?: number, name?: string, company?: string, originData?: string, tpl?:string } = { visible: false }
    const [cloneState, setCloneState] = useState(initialCloneState);

    useEffect(() => {
        dispatch(fetchAllProject());
    }, []);

    if (!showAllState) {
        hardware = hardware.filter(({ enabled }) => enabled);
    }

    hardware = hardware
        .filter(({ name }) => searchState.keyword.length === 0 || searchState.keyword.some(keyword => name.indexOf(keyword) >= 0));
    return (
        <Base title="????????????" keys={[ 'template', 'template-hardware' ]}>
            <Modal title="??????????????????" width={800} visible={detailState.visible} closable footer={null} onCancel={() => setDetailState({visible: false, payload: '[{"Name": "", "Data": []}]'})}>
                {(() => {
                    const detail: any[] = JSON.parse(detailState.payload);
                    if (typeof detail === 'undefined') {
                        return null;
                    }

                    return detail.map(({ Name, Data }) => <Card title={Name}>
                                          <List itemLayout="horizontal" dataSource={Data as any[]} renderItem={({ Name, Value }) => <List.Item><List.Item.Meta title={Name} description={Value} /></List.Item>} />
                                      </Card>);
                })()}
            </Modal>

            <AddHardware {...editState} edit onClose={() => setEditState(initialEditState)} />
            <AddHardware {...cloneState} clone onClose={() => setCloneState(initialCloneState)} />

            <Card>
                <PageHeader title="????????????"
                    extra={[
                        <Button key="flush" onClick={() => dispatch(syncAreaInfo((selectedArea as any).id))} icon={<RetweetOutlined />} />,
                        <Popconfirm
                            visible={typeof selectedProject !== 'undefined'}
                            title="????????????????????????????????????????????????????????????"
                            onCancel={() => setSelectedProject(undefined)}
                            onConfirm={() => {
                                dispatch(appendProjectHardware({
                                    projId: selectedProject,
                                    hardwareId: selectedHardware.map(({ maasId }) => maasId),
                                    cb: () => dispatch(syncAreaInfo((selectedArea as any).id))
                                }));
                                setSelectedProject(undefined);
                            }}>

                            <Select
                                style={{width: 450}}
                                value={selectedProject}
                                onSelect={value => {
                                    if (typeof value === 'number') {
                                        setSelectedProject(value);
                                    }
                                }}
                                placeholder="?????????????????????????????????">
                                {projects.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
                            </Select>
                        </Popconfirm>,
                        
                        <Divider type="vertical" />,

                        <Button onClick={() => setShowAllState(!showAllState)}>{showAllState ? '??????????????????' : '????????????'}</Button>
                    ]} />

                <Form form={searchForm} onFinish={({ keyword }) => {
                    if (keyword === '') {
                        setSearchState({ keyword: [] });
                    }
                    else {
                        setSearchState({ keyword: keyword.split(/,|\n|;/) });
                    }
                }}>
                    <Row>
                        <Col offset={6} span={12}> <Form.Item name="keyword"><Input.TextArea placeholder="????????????????????????????????????????????????????????????????????????????????????" autoSize={{ minRows: 1 }} /></Form.Item> </Col>
                        <Col span={6}> <Button style={{margin: 0}} icon={<SearchOutlined />} htmlType="submit">??????</Button> </Col>
                    </Row>
                </Form>
            </Card>

            <Card>
                <Table
                    size="small"
                    rowSelection={{
                        onChange: (_, selectedRow) => setSelectedHardware(selectedRow)
                    }}
                    columns={[
                        { title: 'ID', dataIndex: 'id' },
                        { title: '??????', dataIndex: 'name' },
                        { title: '????????????', dataIndex: 'projects', render: (_: any, { projects }: IInfoHardware) => {
                            return projects.map(({ name }) => <Tag key={name}>{name}</Tag>)
                        }},
                        { title: '??????', dataIndex: 'payload', render: (payload: string, hardware: IInfoHardware) => {
                            return <>
                                <Button type="link" onClick={() => setDetailState({ visible: true, payload })}>????????????</Button>
                                <Button type="link" onClick={() => putHardwareEnabled(hardware.maasId, !hardware.enabled).then(() => dispatch(selectArea((selectedArea as any).id)))}>{hardware.enabled ? '??????' : '??????'}</Button>
                                <Button type="link" onClick={() => {
                                    getHardwareTplField(hardware.maasId).then(({ data }) => {
                                        if (data.tpl === '') {
                                            data.tpl = '[]';
                                        }
                                        setEditState({
                                            id: hardware.maasId,
                                            visible: true,
                                            company: data.company,
                                            name: data.name,
                                            tpl: data.tpl.replaceAll('\n', ''),
                                            originData: payload
                                        });
                                    })
                                }}>??????</Button>
                                <Button type="link" onClick={() => {
                                    getHardwareTplField(hardware.maasId).then(({ data }) => {
                                        if (data.tpl === '') {
                                            data.tpl = '[]';
                                        }
                                        setCloneState({
                                            id: hardware.maasId,
                                            visible: true,
                                            company: data.company,
                                            name: data.name,
                                            tpl: data.tpl.replaceAll('\n', ''),
                                            originData: payload
                                        });
                                    })
                                }}>??????</Button>
                            </>
                        }}
                    ].map(item => ({ ...item, key: item.dataIndex }))}
                    dataSource={hardware.map(item => ({ key: item.id, ...item }))} />
            </Card>
        </Base>
    )
}

const Page = connect(({ info: { selectedArea, hardware }, project: { projects } }: { info: IInfoModel, project: IProjectModel }) => ({ selectedArea, hardware, projects }))(page);
export default Page;
