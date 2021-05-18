import React, {FC, useState} from 'react';
import Base from '../_base';
import {IInfoModel, connect, IInfoOperationSystem, useDispatch, syncAreaInfo, ISimpleArea, selectArea, } from 'umi';
import {Table, PageHeader, Button, Modal, Card, List, Form, Row, Col, Input, Divider} from 'antd';
import {
    RetweetOutlined,
    SearchOutlined
} from '@ant-design/icons';
import {putOperationSystemEnabled} from '@/service/info';
import OperationSystemAdd from './operation-system-add';

const page: FC<{ selectedArea?: ISimpleArea, operationSystem: IInfoOperationSystem[] }> = ({ selectedArea, operationSystem }) => {
    const dispatch = useDispatch();

    const initialDetailState: {visible: boolean, payload: string} = {visible: false, payload: '[{"Name": "", "Data": []}]'}
    const [detailState, setDetailState] = useState(initialDetailState);

    const initialSearchState: { keyword: string[] } =  { keyword: [] };
    const [searchState, setSearchState] = useState(initialSearchState);
    const [searchForm] = Form.useForm();

    const initialShowAllState: boolean = false;
    const [showAllState, setShowAllState] = useState(initialShowAllState);

    const initialEdit: { visible: boolean, id?: number, name?: string, content?: string } = { visible: false }
    const [editVisible, setEditVisible] = useState(initialEdit);

    if (!showAllState) {
        operationSystem = operationSystem.filter(({ enabled }) => enabled);
    }

    operationSystem = operationSystem
        .filter(({ name }) => searchState.keyword.length === 0 || searchState.keyword.some(keyword => name.indexOf(keyword) >= 0));
    return (
        <Base title="PXE" keys={[ 'template', 'template-operation-system' ]}>
            <OperationSystemAdd {...editVisible} onClose={() => setEditVisible(initialEdit)} />
            <Card>
                <PageHeader title="操作系统"
                    extra={[
                        <Button key="flush" onClick={() => dispatch(syncAreaInfo((selectedArea as any).id))} icon={<RetweetOutlined />} />,
                            
                        <Divider type="vertical" />,

                        <Button onClick={() => setShowAllState(!showAllState)}>{showAllState ? '忽略失效典配' : '显示全部'}</Button>,

                        <Button onClick={() => setEditVisible({ visible: true })}>添加操作系统配置</Button>
                    ]} />

                <Modal title="操作系统详情" width={800} visible={detailState.visible} closable footer={null} onCancel={() => setDetailState({visible: false, payload: '[{"Name": "", "Data": []}]'})}>
                    {(() => {
                        return <Card>
                            <List itemLayout="horizontal" dataSource={[detailState.payload]} renderItem={detail => <List.Item><List.Item.Meta title="操作系统配置详情" description={detail.split('\n').map((item, id) => <code key={id}>{item}<br /></code>)} /></List.Item>} />
                        </Card>
                    })()}
                </Modal>

                <Form form={searchForm} onFinish={({ keyword }) => {
                    if (keyword === '') {
                        setSearchState({ keyword: [] });
                    }
                    else {
                        setSearchState({ keyword: keyword.split(/,|\n|;/) });
                    }
                }}>
                    <Row>
                        <Col offset={6} span={12}> <Form.Item name="keyword"><Input.TextArea placeholder="请输入操作系统名称进行筛选，多条请以回车、逗号、分号分隔" autoSize={{ minRows: 1 }} /></Form.Item> </Col>
                        <Col span={6}> <Button style={{margin: 0}} icon={<SearchOutlined />} htmlType="submit">查询</Button> </Col>
                    </Row>
                </Form>
            </Card>

            <Card>
                <Table columns={[
                    { title: 'ID', dataIndex: 'id' },
                    { title: '名称', dataIndex: 'name' },
                    { title: '详情', dataIndex: 'payload', render: (payload: string, operationSystem: IInfoOperationSystem) => {
                        return <>
                            <Button type="link" onClick={() => setDetailState({ visible: true, payload })}>查看详情</Button>
                            <Button type="link" onClick={() => putOperationSystemEnabled(operationSystem.maasId, !operationSystem.enabled).then(() => dispatch(selectArea((selectedArea as any).id)))}>{operationSystem.enabled ? '隐藏' : '显示'}</Button>
                            <Button type="link" onClick={() => setEditVisible({ visible: true, id: operationSystem.id, name: operationSystem.name, content: payload })}>编辑</Button>
                        </>
                    }}
                ].map(item => ({ ...item, key: item.dataIndex }))}
                dataSource={operationSystem.map(item => ({ key: item.id, ...item }))} />
            </Card>
        </Base>
    )
}

const Page = connect(({ info: { selectedArea, operationSystem } }: { info: IInfoModel }) => ({ selectedArea, operationSystem }))(page);
export default Page;
