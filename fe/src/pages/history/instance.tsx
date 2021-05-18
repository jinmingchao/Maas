import React, {FC, useEffect} from "react";
import Base from '../_base';
import {Card, Table} from "antd";
import {connect, useDispatch} from "@@/plugin-dva/exports";
import { IHistoryModel } from "umi";


const component: FC<{history: IHistoryModel}> = ({ history}) => {
    const column = [
        {title: 'id', dataIndex: 'id', key: 'id' },
        { title: '主机名', dataIndex: 'hostName', key: 'hostName' },
        { title: '域名', dataIndex: 'host', key: 'host' },
        { title: '当前状态', dataIndex: 'status', key: 'status' },
        { title: '装机开始时间', dataIndex: 'createTime', key: 'createTime' },
        { title: '装机完成时间', dataIndex: 'finishTime', key: 'finishTime' },
        { title: '装机失败时间', dataIndex: 'errorTime', key: 'errorTime' },
        { title: '操作用户', dataIndex: 'who', key: 'who' },
        { title: '失败原因', dataIndex: 'why', key: 'why' },
        { title: '所属批次', dataIndex: 'batchAsst', key: 'batchAsst' },
    ]
    const dispatch = useDispatch();


    return (
        <Base  title={'单台主机装机记录'} keys={['history', 'instance-manage']}>
            <div style={{scrollbarWidth: 'none'}}>
                <Card title={"单台主机装机记录"} bordered={false}
                      // extra={<Button type={'primary'} onClick={addNetArea}>新建</Button>}
                >
                    <Table
                        dataSource={history.histories.map((item, key) => ({...item, key}))}
                        columns={column}
                        // onRow={(record: IPNetAreaItem) => ({onClick: () => selectNetArea(record) })}
                        // onHeaderRow={(record) => ({onClick: () => selectNetArea(record) })}
                    />
                </Card>
            </div>

        </Base>
    )
}

const Setup = connect(({ history } : { history: IHistoryModel})=>({ history }))(component)
export default Setup;

