import React, {FC, useEffect} from "react";
import Base from '../_base';
import {Card, Table, Button} from "antd";
import {connect, useDispatch} from "@@/plugin-dva/exports";
import {fetchAllSetup, ISetupModel, ISetupItem, useHistory, IInfoModel, ISimpleArea} from "umi";


const component: FC<{selectedArea? :ISimpleArea, setup: ISetupModel}> = ({ selectedArea, setup }) => {
    const dispatch = useDispatch();
    const history = useHistory();

    useEffect(() => {
        if (typeof selectedArea === 'undefined') {
            return;
        }

        dispatch(fetchAllSetup(selectedArea.id));
    }, []);

    return (
        <Base  title={'装机批次记录'} keys={['history', 'batch-list']}>
            <div style={{scrollbarWidth: 'none'}}>
                <Card title={"装机批次记录"} bordered={false}
                      // extra={<Button type={'primary'} onClick={addNetArea}>新建</Button>}
                >
                    <Table
                        dataSource={setup.setupList.map((item, key) => ({...item, key}))}
                        columns={[
                            { title: 'ID', dataIndex: 'id' },
                            { title: '名称', dataIndex: 'name' },
                            { title: '操作', dataIndex: 'operator', render: (_: string, { id }: ISetupItem) => {
                                return <Button type="link" onClick={() => history.push(`/instance/list?batchId=${id}`)}>查看主机列表</Button>
                            }},
                        ]}
                        // onRow={(record: IPNetAreaItem) => ({onClick: () => selectNetArea(record) })}
                        // onHeaderRow={(record) => ({onClick: () => selectNetArea(record) })}
                    />
                </Card>
            </div>

        </Base>
    )
}

const Setup = connect(({ info: { selectedArea }, setup } : { info: IInfoModel, setup: ISetupModel })=>({ selectedArea, setup }))(component)
export default Setup;

