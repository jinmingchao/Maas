import React, {FC, useState, useEffect} from 'react';
import Base from '../_base';
import {Card, Select, Space, Button} from 'antd';
import {connect, IInfoModel, ISimpleArea, IProjectModel, IProjectItem, useDispatch, fetchAllProject} from 'umi';
import {getInstanceStat} from '@/service/instance';
import ExcelJS from 'exceljs';

const page: FC<{ area: ISimpleArea[], projects: IProjectItem[] }> = ({area, projects}) => {
    const dispatch = useDispatch();

    const initialAreaState: string[] = [];
    const [areaState, setAreaState] = useState(initialAreaState);

    const initialProjectState: number[] = [];
    const [projectState, setProjectState] = useState(initialProjectState);

    useEffect(() => {
        dispatch(fetchAllProject());
    }, []);

    return (
        <Base title="设备统计" keys={[ 'instance', 'instance-stat' ]}>
            <Card style={{width: '100%'}}>
                <Space direction="vertical" style={{width: '100%'}} size={20}>
                    <div style={{width: '100%'}}>
                        <label>筛选区域</label>
                        <Select mode="multiple" style={{width: '100%'}} placeholder="默认为全部区域" onChange={(value: string[]) => setAreaState(value)} value={areaState}>{area.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}</Select>
                    </div>

                    <div style={{width: '100%'}}>
                        <label>筛选建设项目</label>
                        <Select mode="multiple" style={{width: '100%'}} placeholder="默认为全部建设项目" onChange={(value: number[]) => setProjectState(value)} value={projectState}>{projects.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}</Select>
                    </div>

                    <div>
                        <Button onClick={() => {
                            getInstanceStat(areaState, projectState).then(({ data }) => {
                                const workbook = new ExcelJS.Workbook();
                                const sheet = workbook.addWorksheet('data');

                                // @ts-ignore
                                sheet.columns = [
                                    {header: 'SN', key: 'sn'},
                                    {header: '属地区域', key: 'area'},
                                    {header: '建设项目', key: 'project'},
                                    {header: '系统典配', key: 'hardware'},
                                    {header: 'PXE', key: 'pxe'},
                                    {header: '操作系统', key: 'operationSystem'},
                                    {header: '是否已经纳管', key: 'managed'},
                                    {header: '是否已经分配', key: 'distributed'},
                                    {header: '是否已经装机', key: 'installed'},
                                    {header: '装机成功次数', key: 'successCount'},
                                    {header: '装机失败次数', key: 'failedCount'},
                                ].map(item => ({...item, width: 32}))
                                sheet.addRows(data);

                                workbook.xlsx.writeBuffer().then(buffer => {
                                    const blob = new Blob([buffer.slice(0)], { type: 'application/octet-stream' });

                                    const alink = document.createElement('a');
                                    alink.href = URL.createObjectURL(blob);
                                    alink.download = '统计报表.xlsx';
                                    if (window.MouseEvent) {
                                        alink.dispatchEvent(new MouseEvent('click'));
                                    }
                                    else {
                                        const event = document.createEvent('MouseEvents');
                                        event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
                                        alink.dispatchEvent(event);
                                    }
                                })
                            })
                        }}>生成报表</Button>
                    </div>
                </Space>
            </Card>
        </Base>
    )
}

export default connect(({ info: { area }, project: { projects } }: { info: IInfoModel, project: IProjectModel }) => ({ area, projects }))(page);
