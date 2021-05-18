import React, {useEffect, useState} from 'react';
import {getInstanceInstallHistory} from '@/service/instance';
import {Modal, Table} from 'antd';
import {useSelector, IInfoModel, IInfoHardware, IInfoOperationSystem, IInfoPXE, IPNetAreaItem} from 'umi';

interface IInstanceInstallHistory {
    id: number;
    opType: string;
    batchId: number;
    createdAt: string;
    gatewayIp: string;
    hardwareId: number;
    hostname: string;
    innerIp: string;
    netAreaId: number;
    netmask: string;
    pxeId: number;
    sn: string;
    systemId: number;
    user: string;
    vlanId: number;
}

const InstanceInstallHistory = ({ sn, onClose }: { sn: string, onClose: () => void }) => {
    const initialDetail: IInstanceInstallHistory[] = [];
    const [detail, setDetail] = useState(initialDetail);

    useEffect(() => {
        if (sn !== '') {
            getInstanceInstallHistory(sn).then(({ data }) => setDetail(data));
        }
        else {
            setDetail([]);
        }
    }, [sn]);

    const {
        hardware,
        operationSystem,
        pxe,
        net
    }: {
        hardware: IInfoHardware[],
        operationSystem: IInfoOperationSystem[],
        pxe: IInfoPXE[],
        net: IPNetAreaItem[]
    } = useSelector(({
        info: { hardware, operationSystem, pxe, net }
    }: {
        info: IInfoModel
    }) => ({ hardware, operationSystem, pxe, net }));

    return (
        <Modal width="80vw" visible={sn !== ''} onCancel={onClose} footer={null} title={`设备装机记录`}>
            <Table
                columns={[
                    { title: 'SN', dataIndex: 'sn' },
                    { title: '操作类型', dataIndex: 'opType' },
                    { title: '操作时间', dataIndex: 'createdAt' },
                    { title: '操作人', dataIndex: 'user' },
                    { title: '网络区域', dataIndex: 'netAreaId', render: (text: number) => text && net.find(({ id }) => id === text)?.name },
                    { title: '网关', dataIndex: 'gatewayIp' },
                    { title: '业务IP', dataIndex: 'innerIp' },
                    { title: '子网掩码', dataIndex: 'netmask' },
                    { title: 'VLAN ID', dataIndex: 'vlanId' },
                    { title: '主机名', dataIndex: 'hostname' },
                    { title: 'PXE', dataIndex: 'pxeId', render: (text: number) => text && pxe.find(({ id }) => id === text)?.name },
                    { title: '操作系统', dataIndex: 'systemId', render: (text: number) => text && operationSystem.find(({ id }) => id === text)?.name },
                    { title: '系统典配', dataIndex: 'hardwareId', render: (text: number) => text && hardware.find(({ id }) => id === text)?.name },
                ].map((item, key) => ({...item, key}))}
                dataSource={detail.sort((a: IInstanceInstallHistory, b: IInstanceInstallHistory) => b.id - a.id)}
            />
        </Modal>
    )
};

export default InstanceInstallHistory;
