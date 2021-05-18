import React from 'react';
import {Modal, Table, Tag, PageHeader, Button} from 'antd';

const InstanceSubmitCheckList = ({
    visible,
    onCancel,
    onSubmit,
    onDownload,
    checkItem,
    columns,
    data
}: {
    visible: boolean,
    onCancel: () => void,
    onSubmit?: (filteredData: any[]) => void,
    onDownload?: (instances: any[]) => void,
    checkItem?: (item: any) => { status: boolean, reason: string },
    columns: { header: string, key: string, optionals?: any, operation?: any }[]
    data: any[]
}) => {
    const validData: any[] = [];
    const visibleData = data.map(item => {
        const _checkStatus: { status: boolean, reason: string } = checkItem ? checkItem(item) : { status: true, reason: '' };
        if (_checkStatus.status) {
            validData.push(item);
        }
        
        const result = { ...item, _checkStatus, _checkStatusReason: _checkStatus.reason };

        for (const resultKey in result) {
            columns.forEach(({ key, operation }) => {

                if (key === resultKey && operation && operation.map) {
                    result[key] = operation.map(result[key]);
                }
            })
        }

        return result;
    });

    return (
        <Modal
            zIndex={9999}
            width="100vw"
            visible={visible}
            closable
            onCancel={() => onCancel()}
            onOk={() => onSubmit && onSubmit(validData)}>
            <PageHeader
                title="确认清单"
                extra={[
                    <Button size="middle" onClick={() => onDownload && onDownload(visibleData)}>下载清单</Button>
                ]} />
            <Table
                size="small"
                scroll={{ x: '100vw' }}
                columns={[{
                    fixed: true,
                    title: '核验结果',
                    dataIndex: '_checkStatus',
                    width: 150,
                    render: ({ status, reason }: { status: boolean, reason: string }) => status ? <Tag color="green">核验通过</Tag> : <Tag>{reason}</Tag>
                }].concat(columns.map(({ header, key, optionals }) => ({ ...optionals, width: 200, title: header, dataIndex: key, key })))}
                dataSource={visibleData} />
        </Modal>
    );
}

export default InstanceSubmitCheckList;
