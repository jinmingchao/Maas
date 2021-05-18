import React, {useEffect, useState} from 'react';
import {getInstanceDetail} from '@/service/instance';
import {Modal, Descriptions, Space} from 'antd';

const InstanceDetail = ({ sn, onClose }: { sn: string, onClose: () => void }) => {
    const initialDetail: any = {};
    const [detail, setDetail] = useState(initialDetail);

    useEffect(() => {
        if (sn !== '') {
            getInstanceDetail(sn).then(({ data }) => setDetail(data));
        }
        else {
            setDetail({});
        }
    }, [sn]);

    const mapper: { [key: string]: any } = {
        'boundMac1': { title: 'Bond MAC 1' },
        'boundMac2': { title: 'Bond MAC 2' },
        'boundType': { title: 'Bond类型' },
        'company': { title: '设备厂商' },
        'cpuCoreCount': { title: '设备厂商' },
        'dhcpIp': { title: '装机网IP' },
        'diskCapacitySum': { title: '硬盘总容量（MB）' },
        'diskCount': { title: '硬盘个数' },
        'modelName': { title: '类型编号' },
        'gatewayIp': { title: '网关IP' },
        'hostname': { title: '主机名称' },
        'innerIp': { title: '业务IP' },
        'netmask': { title: '子网掩码' },
        'oobIp': { title: '带外IP' },
        'oobUsername': { title: '带外账户' },
        'vlanId': { title: 'VLAN ID' },
        'status': { title: '装机状态' },
        'raid': { title: 'RAID' },
    }

    const cpuMapper: { [key: string]: any } = {
        'Model': { title: 'CPU型号' },
        'Core': { title: 'CPU核数' },
    }

    return (
        <Modal width="80vw" visible={sn !== ''} onCancel={onClose} footer={null} title={`设备SN:${sn}详细信息`}>
            <Space direction="vertical" style={{ width: '100%' }}>
                <Descriptions title="详细信息" bordered size="small">
                    {(() => {
                        const result: { key: string, title: string, value: string }[] = [];
                        for (const key in detail) {
                            if (typeof mapper[key] !== 'undefined') {
                                result.push({
                                    key,
                                    title: mapper[key]['title'],
                                    value: detail[key]
                                })
                            }
                        }

                        return result.map(({ key, title, value }) => <Descriptions.Item key={key} label={title}>{value}</Descriptions.Item>);
                    })()}
                </Descriptions>

                <Descriptions title="CPU" bordered size="small">
                    {(() => {
                        if (typeof detail['cpu'] === 'undefined') {
                            return null;
                        }

                        const result: { key: string, title: string, value: string }[] = [];
                        const cpuDetail = JSON.parse(detail['cpu']);
                        for (const key in cpuDetail) {
                            if (typeof cpuMapper[key] !== 'undefined') {
                                result.push({
                                    key,
                                    title: cpuMapper[key]['title'],
                                    value: cpuDetail[key]
                                })
                            }
                        }

                        return result.map(({ key, title, value }) => <Descriptions.Item key={key} label={title}>{value}</Descriptions.Item>);
                    })()}
                </Descriptions>

                <Descriptions title="硬盘" bordered column={2} size="small">
                    {(() => {
                        if (typeof detail['disk'] === 'undefined') {
                            return null;
                        }

                        const result: { key: string, title: string, value: string }[] = [];
                        const diskDetail = JSON.parse(detail['disk']);
                        diskDetail.forEach((item: any) => {
                            result.push({
                                key: 'Name',
                                title: '名称',
                                value: item['Name']
                            });
                            result.push({
                                key: 'Size',
                                title: '容量',
                                value: item['Size']
                            })
                        })

                        return result.map(({ key, title, value }) => <Descriptions.Item key={key} label={title}>{value}</Descriptions.Item>);
                    })()}
                </Descriptions>

                <Descriptions title="内存" bordered column={2} size="small">
                    {(() => {
                        if (typeof detail['memory'] === 'undefined') {
                            return null;
                        }

                        const result: { key: string, title: string, value: string }[] = [];
                        const memoryDetail = JSON.parse(detail['memory']);
                        memoryDetail.forEach((item: any) => {
                            result.push({
                                key: 'Name',
                                title: '名称',
                                value: item['Name']
                            });
                            result.push({
                                key: 'Size',
                                title: '容量',
                                value: item['Size']
                            })
                        })

                        return result.map(({ key, title, value }) => <Descriptions.Item key={key} label={title}>{value}</Descriptions.Item>);
                    })()}
                </Descriptions>

                <Descriptions title="网卡" bordered column={5} size="small">
                    {(() => {
                        if (typeof detail['nic'] === 'undefined') {
                            return null;
                        }

                        const result: { key: string, title: string, value: string }[] = [];
                        const nicDetail = JSON.parse(detail['nic']);
                        nicDetail.forEach((item: any) => {
                            result.push({
                                key: 'Name',
                                title: '名称',
                                value: item['Name']
                            });
                            result.push({
                                key: 'Status',
                                title: '状态',
                                value: item['Status']
                            });
                            result.push({
                                key: 'Speed',
                                title: '网络速率',
                                value: item['Speed']
                            });
                            result.push({
                                key: 'Mac',
                                title: 'MAC',
                                value: item['Mac']
                            });
                            result.push({
                                key: 'Ip',
                                title: 'IP',
                                value: item['Ip']
                            });
                        })

                        return result.map(({ key, title, value }) => <Descriptions.Item key={key} label={title}>{value}</Descriptions.Item>);
                    })()}
                </Descriptions>

                <Descriptions title="网卡驱动" bordered column={1} size="small">
                    {(() => {
                        if (typeof detail['nicDevice'] === 'undefined') {
                            return null;
                        }

                        const result: { key: string, title: string, value: string }[] = [];
                        const nicDetail = detail['nicDevice'].split('\n');
                        nicDetail.forEach((item: any) => {
                            result.push({
                                key: 'Name',
                                title: '网卡驱动',
                                value: item
                            });
                        })

                        return result.map(({ key, title, value }) => <Descriptions.Item key={key} label={title}>{value}</Descriptions.Item>);
                    })()}
                </Descriptions>
            </Space>
        </Modal>
    )
};

export default InstanceDetail;
