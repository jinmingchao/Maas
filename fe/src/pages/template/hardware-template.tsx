import React, {useEffect, useState} from 'react';
import Base from '../_base';
import {useDispatch, fetchHardwareTemplate, useSelector, IInfoModel, IHardwareTemplate, removeHardwareTemplate} from 'umi';
import {Table, Card, PageHeader, Button} from 'antd';
import AddHardwareTemplate from './hardware-template-add';
import AddHardware from './hardware-add';

export default () => {
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(fetchHardwareTemplate());
    }, []);

    const { hardwareTemplate } = useSelector(({ info: { hardwareTemplate } }: { info: IInfoModel }) => ({ hardwareTemplate }))

    const initialEditState: { visible: boolean, id?: number, tpl?: string, name?: string, company?: string } = { visible: false }
    const [editState, setEditState] = useState(initialEditState);
    const initialAddTemplateState: { visible: boolean, id?: number, tpl?: string, name?: string, company?: string } = { visible: false }
    const [addTemplateState, setAddTemplateState] = useState(initialAddTemplateState);

    return (
        <Base title="系统典配模板" keys={[ 'template', 'template-hardware-template' ]}>
            <AddHardwareTemplate
                id={editState.id} tpl={editState.tpl} name={editState.name} company={editState.company}
                visible={editState.visible}
                onClose={() => setEditState(initialEditState)} />

            <AddHardware
                id={addTemplateState.id} tpl={addTemplateState.tpl} name={addTemplateState.name} company={addTemplateState.company}
                visible={addTemplateState.visible}
                onClose={() => setAddTemplateState(initialAddTemplateState)} />

            <Card>
                <PageHeader
                    title="系统典配模板"
                    extra={[
                        <Button key="add" onClick={() => setEditState({ visible: true })}>系统典配模板</Button>
                    ]} />
            </Card>
            <Card>
                <Table 
                    size="small"
                    columns={[
                        { title: 'ID', dataIndex: 'id' },
                        { title: '服务器厂商', dataIndex: 'company' },
                        { title: '名称', dataIndex: 'name' },
                        { title: '操作', dataIndex: 'operation', render: (_:any, {id, tpl, name, company}: IHardwareTemplate) => (
                            <>
                                <Button size="small" type="link" onClick={() => setAddTemplateState({ visible: true, id, tpl, name, company })}>生成系统典配</Button>
                                <Button size="small" type="link" onClick={() => setEditState({ visible: true, id, tpl, name, company })}>修改</Button>
                                <Button size="small" type="link" onClick={() => dispatch(removeHardwareTemplate(id as number))}>删除</Button>
                            </>
                        ) }
                    ]}
                    dataSource={hardwareTemplate} />
            </Card>
        </Base>
    )
}
