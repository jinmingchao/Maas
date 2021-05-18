import React, {FC, useEffect, useState} from 'react';
import Base from '../_base';
import {connect, useDispatch, fetchAllProject, IProjectModel, IProjectItem, createProject, ICreateProjectMessage, modifyProject} from 'umi';
import {Table, PageHeader, Button, Form, Input, Modal, Row, Col} from 'antd';
import { SearchOutlined } from '@ant-design/icons'

const page: FC<{ projects: IProjectItem[] }> = ({ projects }) => {
    const dispatch = useDispatch();

    const [addProjectVisible, setAddProjectVisible] = useState(false);
    const [form] = Form.useForm();

    const initialModifyProjectState: { visible: boolean, id?: number, payload?: ICreateProjectMessage } = { visible: false };
    const [modifyProjectState, setModifyProjectState] = useState(initialModifyProjectState);
    const [modifyForm] = Form.useForm();

    const initialSearchState: { keyword: string[] } =  { keyword: [] };
    const [searchState, setSearchState] = useState(initialSearchState);
    const [searchForm] = Form.useForm();

    useEffect(() => {
        dispatch(fetchAllProject());
    }, []);
    
    projects = projects
        .filter(({ name }) => searchState.keyword.length === 0 || searchState.keyword.some(keyword => name.indexOf(keyword) >= 0));
    return (
        <Base title="建设项目列表" keys={[ 'project', 'project-list' ]}>
            <PageHeader title="建设项目列表"
                extra={[
                    <Button key="create" onClick={() => setAddProjectVisible(true)}>创建建设项目</Button>
                ]} />

            <Modal
                title="创建建设项目"
                visible={addProjectVisible}
                onCancel={() => setAddProjectVisible(false)}
                onOk={() => form.submit()}
                width={800}>

                <Form labelCol={{span: 4}} wrapperCol={{span: 20}} form={form} onFinish={entity => {
                    dispatch(createProject(entity));
                    setAddProjectVisible(false);
                }}>
                    <Form.Item label="建设项目全称" name="name"><Input /></Form.Item>
                    <Form.Item label="描述" name="description"><Input /></Form.Item>
                </Form>
            </Modal>

            <Modal
                title="修改建设项目"
                visible={modifyProjectState.visible}
                onCancel={() => setModifyProjectState({ visible: false })}
                onOk={() => modifyForm.submit()}
                width={800}>

                <Form labelCol={{span: 4}} wrapperCol={{span: 20}} form={modifyForm} onFinish={payload => {
                    dispatch(modifyProject({ id: (modifyProjectState as any).id, payload }));
                    setModifyProjectState({ visible: false });
                }}>
                    <Form.Item label="建设项目全称" name="name"><Input /></Form.Item>
                    <Form.Item label="描述" name="description"><Input /></Form.Item>
                </Form>
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
                    <Col offset={6} span={12}> <Form.Item name="keyword"><Input.TextArea placeholder="请输入项目名称进行筛选，多条请以回车、逗号、分号分隔" autoSize={{ minRows: 1 }} /></Form.Item> </Col>
                    <Col span={6}> <Button style={{margin: 0}} icon={<SearchOutlined />} htmlType="submit">查询</Button> </Col>
                </Row>
            </Form>

            <Table columns={[
                { title: 'ID', dataIndex: 'id' },
                { title: '名称', dataIndex: 'name' },
                { title: '描述', dataIndex: 'description' },
                { title: '操作', dataIndex: 'operation', render: (_: any, { id, name, description }: IProjectItem) => {
                    return <Button type="link" onClick={() => {
                        modifyForm.setFieldsValue({ name, description });
                        setModifyProjectState({ visible: true, id, payload: { name, description }});
                    }}>编辑</Button>
                }},
            ].map(item => ({ ...item, key: item.dataIndex }))}
            dataSource={projects.map(item => ({ key: item.id, ...item }))} />
        </Base>
    )
}

const Page = connect(({ project: { projects } }: { project: IProjectModel }) => ({ projects }))(page);
export default Page;
