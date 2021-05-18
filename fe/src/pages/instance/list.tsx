import React, {FC, useEffect, useState, useReducer} from 'react';
import {IInstanceModel, ISimpleInstance, connect, IInfoModel, useDispatch, fetchInstances, ISimpleArea, manageInstances, IManageInstanceResult, IDistributeInstance, distributeInstances, installInstances, useHistory, IInfoHardware, IInfoPXE, IInfoOperationSystem, modifyInstanceTemplateInfo, IPNetAreaItem, IModifyNetInfo, modifyInstanceNetInfo, fetchInstancesByBatchId, IPPoolModel, IPPoolItem, resetInstance, nondistributeInstance, IModifyOobInfo, modifyOobInstance, fetchInstancesByInstalling } from 'umi';
import Base from '../_base';
import {Form, Table, Tag, PageHeader, Button, message, Select, notification, Divider, Drawer, Upload, Input, Popconfirm, Row, Col, Switch, Space, Tooltip, Dropdown, Menu, Card } from 'antd';
import InstanceBatchManagedForm from './_batch_managed_form';
import InstanceBatchInstallForm from './_batch_install_form';
import {
    FilterOutlined,
    SearchOutlined,
    InboxOutlined,
    RetweetOutlined,
    DownOutlined
} from '@ant-design/icons';
import NetInstanceForm from './_modify_instance_netinfo';
import {putPowerOn, putPowerOff, putRestartFromPXE, putRestartFromDisk} from '@/service/instance';
import XLSX from 'xlsx';
import {RcFile} from 'antd/lib/upload';
import OobForm from './_modify_oob_form';
import ExcelJS from 'exceljs';
import {IProjectModel, fetchAllProject, IProjectItem} from '@/models/project';
import {ICheckPermissionResult} from "@/models/permission";
import {syncInstance, cancelInstallInstance, removeInstance, setInstanceInstallSuccess, setInstanceInstallFailure, recoveryResetInstance, IInstallMessage} from "@/models/instance";
import InstanceSubmitCheckList from './_check_list';
import InstanceDetail from './_instance_detail';
import InstanceInstallHistory from './_install_history';

function elseValue<T>(value: any, defaultValue: T): T { return value ? value : defaultValue; }
const elseValueStringCompare = (a: any, b: any, key: string) => elseValue(a[key], '').localeCompare(elseValue(b[key], ''));
const elseValueNumberCompare = (a: any, b: any, key: string) => elseValue(a[key], 0) - elseValue(b[key], 0)

const page: FC<{
    instances: ISimpleInstance[],
    manageResult: IManageInstanceResult[],
    selectedArea?: ISimpleArea,
    area?: string,
    batchId?: number,
    hardware: IInfoHardware[],
    allHardware: IInfoHardware[],
    pxe: IInfoPXE[],
    allPxe: IInfoPXE[],
    operationSystem: IInfoOperationSystem[],
    allOperationSystem: IInfoOperationSystem[],
    net: IPNetAreaItem[],
    pool: IPPoolItem[],
    projects: IProjectItem[],
    buttonPermission: ICheckPermissionResult[],
}> = ({ instances: allInstances, selectedArea, batchId, area, hardware, allHardware, pxe, allPxe, operationSystem, allOperationSystem, net, pool, projects }) => {
    const history = useHistory();
    const dispatch = useDispatch();
    const [batchManageVisible, setBatchManageVisible] = useState(false);
    const [batchInstallVisible, setBatchInstallVisible] = useState(false);

    const initialSelectedInstanceState: ISimpleInstance[] = [];
    const [selectedInstance, setSelectedInstance] = useState(initialSelectedInstanceState);

    const initialSelectedInstanceNetState: { instance?: ISimpleInstance, visible: boolean } = { visible: false };
    const [netInstance, setNetInstance] = useState(initialSelectedInstanceNetState);

    const initialImportManageState: { visible: boolean, uploadVisible: boolean, payload: any } = { visible: false, uploadVisible: false, payload: [] };
    const [importManage, setImportManage] = useState(initialImportManageState);

    const initialImportSetupBatchState: { visible: boolean, uploadVisible: boolean, payload: any, name: string } = { visible: false, uploadVisible: false, payload: [], name: '' };
    const [importSetupBatch, setImportSetupBatch] = useState(initialImportSetupBatchState);

    const initialModifyOobState: { visible: boolean, instance?: ISimpleInstance } = { visible: false };
    const [modifyOobState, setModifyOobState] = useState(initialModifyOobState);

    const initialEasyModifyPopConfirmState: { visible: boolean, type: string, value: number, sn: string } = { visible: false, type: '', sn: '', value: 0 };
    const [easyModifyPopConfirm, setEasyModifyPopConfirm] = useState(initialEasyModifyPopConfirmState);

    const initialEasyBatchOperationPopConfirmState: { visible: boolean, type: string } = { visible: false, type: '' };
    const [easyBatchPopConfirm, setEasyBatchPopConfirm] = useState(initialEasyBatchOperationPopConfirmState);

    const initialSubmitCheckListState: { visible: boolean, columns: { header: string, key: string }[], data: any[], onSubmit?: (data: any[]) => void, checkItem?: (data: any) => { status: boolean, reason: string }, onDownload?: (instances: any[]) => void } = { visible: false, columns: [], data: [] };
    const [submitCheckListState, setSubmitCheckListState] = useState(initialSubmitCheckListState);

    const initialSearchState: { keyword: string[] } = { keyword: [] };
    const [searchState, setSearchState] = useState(initialSearchState);
    const [searchForm] = Form.useForm();

    const initialFlushSchedState: { handle?: any } = {};
    const [flushSched, setFlushSched] = useState(initialFlushSchedState);

    const initialFilterState: { visible: boolean, project?: IProjectItem[], hardware?: IInfoHardware[], netArea?: IPNetAreaItem[], companyName?: string[], statusTags?: string[] } = { visible: false };
    const [filterState, setFilterState] = useState(initialFilterState);

    const initialDetailState: string = '';
    const [detailState, setDetailState] = useState(initialDetailState);

    const initialInstallHistoryState: string = '';
    const [installHistoryState, setInstallHistoryState] = useState(initialInstallHistoryState);

    const [flushCount, dispatchFlushCount] = useReducer((state: number, _: any) => state + 1, 0);

    const netMapper = net.reduce((prev, curr) => {
        prev[curr.id] = curr.name;
        return prev;
    }, {} as any);

    let instances: ISimpleInstance[] = allInstances;
    let specBaseKey = 'instance-list';
    if (!!(history.location as any).query.batchId) {
        specBaseKey += '-batch';
    }
    else if (!!(history.location as any).query.nonmanaged) {
        instances = instances.filter(({ managed }) => !managed);
        specBaseKey += '-nonmanaged';
    }
    else if (!!(history.location as any).query.managed) {
        instances = instances.filter(({ managed }) => managed);
        specBaseKey += '-managed';
    }
    else if (!!(history.location as any).query.installable) {
        specBaseKey += '-installable';
    }

    console.log(instances);
    instances = instances
        .filter(({ projectId, hardwareId, netAreaId, company, tags }) =>
                (typeof filterState.project === 'undefined' || filterState.project.length == 0 ? true : filterState.project.map(({id}) => id).includes(projectId ? projectId : -1))
                &&
                (typeof filterState.hardware === 'undefined' || filterState.hardware.length == 0 ? true : filterState.hardware.map(({id}) => id).includes(hardwareId))
                &&
                (typeof filterState.netArea === 'undefined' || filterState.netArea.length == 0 ? true : filterState.netArea.map(({id}) => id).includes(netAreaId))
                &&
                (typeof filterState.companyName === 'undefined' || filterState.companyName.length == 0 ? true : filterState.companyName.includes(company))
                &&
                (typeof filterState.statusTags === 'undefined' || filterState.statusTags.length === 0 ? true : tags.some(({ content }) => filterState.statusTags?.includes(content)))
        )

    if (searchState.keyword.length !== 0) {
        instances = instances.filter(({ sn, oobIp, innerIp, hostname }) => searchState.keyword.some(keyword => [sn, oobIp, innerIp, hostname].includes(keyword)));
    }

    useEffect(() => {
        if (typeof selectedArea === 'undefined') {
            typeof area !== 'undefined' && dispatch(fetchInstances(undefined));
            return;
        }

        if (['instance-list-nonmanaged', 'instance-list-managed'].includes(specBaseKey)) {
            dispatch(fetchInstances(selectedArea?.id));
        }
        else if (['instance-list-batch'].includes(specBaseKey)) {
            dispatch(fetchInstancesByBatchId({ area: selectedArea.id, batchId: parseInt((history.location as any).query.batchId) }));
        }
        else if (['instance-list-installable'].includes(specBaseKey)) {
            dispatch(fetchInstancesByInstalling({ area: selectedArea.id }));
        }
    }, [selectedArea, specBaseKey, flushCount])

    useEffect(() => {
        setSelectedInstance([]);
    }, [selectedArea, specBaseKey]);

    useEffect(() => {
        if (typeof flushSched.handle === 'undefined') {
            setFlushSched({ handle: setInterval(flushCallback, 5000) });
        }
        dispatch(fetchAllProject());
        return () => {
            if (flushSched.handle) {
                clearInterval(flushSched.handle);
                setFlushSched({});
            }
        }
    }, []);

    // 下载Excel模板表
    const downloadExcel = (columns: any[], content: any, dataVaildation: (sheet: ExcelJS.Worksheet, metadata: ExcelJS.Worksheet) => void, name: string) => {

        const workbook = new ExcelJS.Workbook();
        const sheet = workbook.addWorksheet('user_view');
        const metadataSheet = workbook.addWorksheet('metadata');

        // @ts-ignore
        sheet.columns = columns.map(({ key }) => ({ key, header: key, width: 32 }));
        const humanHeaders: any = {};
        columns.forEach((item: any) => { humanHeaders[item['key']] = item['header'] });
        sheet.addRow(humanHeaders);
        sheet.addRows(content);

        dataVaildation(sheet, metadataSheet);

        workbook.xlsx.writeBuffer().then(buffer => {
            const blob = new Blob([buffer.slice(0)], { type: 'application/octet-stream' });

            const alink = document.createElement('a');
            alink.href = URL.createObjectURL(blob);
            alink.download = name;
            if (window.MouseEvent) {
                alink.dispatchEvent(new MouseEvent('click'));
            }
            else {
                const event = document.createEvent('MouseEvents');
                event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
                alink.dispatchEvent(event);
            }
        })
    }

    // 导入Excel表
    const importExcel = (file: RcFile, callback: (data: any, columnMapper: { [key: string]: number }) => void) => {
        const reader = new FileReader();
        reader.onload = e => {
            const data = e.target?.result;
            const workbook = XLSX.read(data, { type: 'binary' });
            const sheet = workbook.Sheets[workbook.SheetNames[0]];

            const jsonArr = XLSX.utils.sheet_to_json(sheet, { header: 1 });
            const columnMapper: { [key: string]: number } = {};
            (jsonArr[0] as string[]).forEach((value, index) => {
                columnMapper[value] = index;
            })
            callback(jsonArr.splice(2), columnMapper);
        }

        reader.readAsBinaryString(file);

        return false;
    }

    const flushCallback = () => {
        dispatchFlushCount('any');
    }

    // 根据tag将特定按钮置为不可用
    const disabledByTags = (keywords: string[]) => {
        const filteredInstance = instances.filter(({ sn }) => selectedInstance.map(({ sn }) => sn).includes(sn));

        return !filteredInstance.length
        || !filteredInstance.every(({tags}) => tags.some(({content}) => keywords.includes(content)))
        || (specBaseKey === 'instance-list-batch' && filteredInstance.some(({ setupId }) => setupId !== batchId));
    }

    // 归约整合当前设备的设备厂商
    const companys: string[] = [];
    instances.map(({ company }) => company).reduce((prev, curr) => { prev.add(curr); return prev; }, new Set<string>()).forEach((value) => companys.push(value));
    const filterStatus: string[] = [];
    const tagsCount: Map<string, number> = instances.map(({ tags }) => tags).reduce((prev, curr) => {
        curr.forEach(({ content }) => prev.has(content) ? prev.set(content, (prev.get(content) as number) + 1) : prev.set(content, 1));
        return prev;
    }, new Map<string, number>());
    tagsCount.forEach((_, key) => filterStatus.push(key));
    const tagsCountTag: { key: string, value: number }[] = [];
    tagsCount.forEach((value, key) => tagsCountTag.push({ key, value }));

    const managedExcelHeader: { header: string, key: string, optionals?: any, operation?: any }[] = [
        { header: '*SN',                 key: 'sn', optionals: { fixed: 'left'                                        }                         },
        { header: '建设项目名',          key: 'project_id', optionals: { render: (pid: number) => projects.find(({ id }) => id === pid)?.name } },
        { header: '*系统典配',           key: 'hardware', optionals: { render: (hid: number) => hardware.find(({ id   }) => id === hid)?.name } },
        { header: '*网络区域',           key: 'netarea', optionals: { render: (nid: number) => netMapper[nid]         }                         },
        { header: '*带外IP',              key: 'oob_ip'                                                                                         },
        { header: '*带外账号',            key: 'oob_username'                                                                                   },
        { header: '*带外密码',            key: 'oob_password'                                                                                   },
        { header: '数据中心',            key: 'place_dc'                                                                                        },
        { header: '楼宇',                key: 'place_building'                                                                                  },
        { header: '机房',                key: 'place_room'                                                                                      },
        { header: '机柜',                key: 'place_cabinet'                                                                                   },
        { header: '起始U位',             key: 'place_u'                                                                                         },
        { header: '占U位个数',           key: 'place_u_count'                                                                                   },
        { header: '资产归属',            key: 'belong_with'                                                                                     },
        { header: '性质资产',            key: 'asset_prop'                                                                                      },
        { header: '区域',                key: 'area'                                                                                            },
        { header: '网络架构',            key: 'netarch'                                                                                         },
        { header: '资源池类型',          key: 'resource_pool_type'                                                                              },
        { header: '接入云管平台',        key: 'cloud_type'                                                                                      },
        { header: '资源类型',            key: 'asset_type'                                                                                      },
        { header: '设备厂商',            key: 'company'                                                                                         },
        { header: '设备型号',            key: 'instance_model'                                                                                  },
        { header: '设备类型',            key: 'instance_type'                                                                                   },
        { header: '设备配置',            key: 'instance_prop'                                                                                   },
        { header: '网卡模式',            key: 'netcard_mod'                                                                                     },
        { header: '默认Raid配置',        key: 'default_raid'                                                                                    },
        { header: '应用名称',            key: 'app_name'                                                                                        },
        { header: '纳管状态',            key: 'manage_status'                                                                                   },
        { header: '分配状态',            key: 'distribute_status'                                                                               },
        { header: '特殊备注',            key: 'backup'                                                                                          },
        { header: '业务地址',            key: 'inner_ip'                                                                                        },
        { header: '掩码',                key: 'netmask'                                                                                         },
        { header: '网关',                key: 'gateway'                                                                                         },
        { header: 'vlan',                key: 'vlan'                                                                                            },
        { header: '业务交换机1机房',     key: 'app_switch1_room'                                                                                },
        { header: '业务交换机1机柜',     key: 'app_switch1_cabinet'                                                                             },
        { header: '业务交换机1设备名称', key: 'app_switch1_name'                                                                                },
        { header: '业务交换机1设备SN',   key: 'app_switch1_sn'                                                                                  },
        { header: '业务交换机1端口',     key: 'app_switch1_port'                                                                                },
        { header: '业务交换机2机房',     key: 'app_switch2_room'                                                                                },
        { header: '业务交换机2机柜',     key: 'app_switch2_cabinet'                                                                             },
        { header: '业务交换机2设备名称', key: 'app_switch2_name'                                                                                },
        { header: '业务交换机2设备SN',   key: 'app_switch2_sn'                                                                                  },
        { header: '业务交换机2端口',     key: 'app_switch2_port'                                                                                },
        { header: '管理交换机机房',      key: 'man_switch_room'                                                                                 },
        { header: '管理交换机机柜',      key: 'man_switch_cabinet'                                                                              },
        { header: '管理交换机设备名称',  key: 'man_switch_name'                                                                                 },
        { header: '管理交换机设备SN',    key: 'man_switch_sn'                                                                                   },
        { header: '管理交换机端口',      key: 'man_switch_port'                                                                                 },
        { header: 'mac1',                key: 'mac1'                                                                                            },
        { header: 'mac2',                key: 'mac2'                                                                                            },
    ];
    const checkManagedItem = ({ hardware: hid, netarea: nid }: any) => {
        if (!hardware.some(({id}) => id === hid)) {
            return { status: false, reason: '系统典配填写错误' };
        }
        if (!net.some(({id}) => id === nid)) {
            return { status: false, reason: '网络区域填写错误' };
        }
        return { status: true, reason: '' };
    }
    const managedMetadataSheet = (sheet: ExcelJS.Worksheet, metadata: ExcelJS.Worksheet) => {
        // 硬件典配枚举
        hardware.forEach(({ name }, idx) => metadata.getCell(`A${idx + 1}`).value = name);
        sheet.getColumn('hardware').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$A$1:$A$${hardware.length}`]
        });

        // 网络区域枚举
        net.forEach(({ name }, idx) => metadata.getCell(`B${idx + 1}`).value = name);
        sheet.getColumn('netarea').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$B$1:$B$${net.length}`]
        })

        // 项目名
        projects.forEach(({ name }, idx) => metadata.getCell(`C${idx + 1}`).value = name);
        sheet.getColumn('project_id').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$C$1:$C$${projects.length}`]
        })
    }

    const batchInstallExcelHeader: { header: string, key: string, optionals?: any, operation?: any }[] = [
        { header: '*SN',        key: 'sn', optionals: { fixed: 'left' }                                                                            },
        { header: '*PXE',       key: 'pxeId', optionals: { render: (pid: number) => pxe.find(({id}) => id === pid)?.name }                         },
        { header: '*操作系统',  key: 'operationSystemId', optionals: { render: (oid: number) => operationSystem.find(({id}) => id === oid)?.name } },
        { header: '*主机名称',  key: 'hostname'                                                                                                    },
        { header: '*IP池名称',  key: 'ippool', optionals: { render: (pid: number) => pool.find(({id}) => id === pid)?.name }                       },
        { header: '*业务IP',    key: 'innerIp'                                                                                                     },
        { header: 'Bond MAC 1', key: 'boundMac1', operation: { map: (val: string) => typeof val === 'undefined' ? '' : val.toUpperCase() }         },
        { header: 'Bond MAC 2', key: 'boundMac2', operation: { map: (val: string) => typeof val === 'undefined' ? '' : val.toUpperCase() }         },
        { header: 'Bond类型',   key: 'boundType'                                                                                                   },
    ]
    const checkBatchInstallItem = ({ sn, innerIp, pxeId, operationSystemId, ippool, boundMac1, boundMac2 }: any) => {
        if (!/^(\d{1,3}\.){3}\d{1,3}$/.test(innerIp)) {
            return { status: false, reason: '业务IP格式错误' }
        }
        if (!pxe.some(({ id }) => id === pxeId)) {
            return { status: false, reason: 'PXE填写错误' }
        }
        if (!operationSystem.some(({ id }) => id === operationSystemId)) {
            return { status: false, reason: '操作系统填写错误' }
        }
        if (!pool.some(({ id }) => id === ippool)) {
            return { status: false, reason: 'IP池填写错误' }
        }

        if (!!!boundMac1 && !!!boundMac2) {
            return { status: true, reason: '' };
        }

        const instance: ISimpleInstance | undefined = instances.find(({ sn: instanceSn }) => instanceSn === sn);
        if (instance) {
            if (!!instance.nic) {
                const nicJson: { Name: string, Mac: string }[] = JSON.parse(instance.nic);
                
                return {
                    status: /^([a-fA-F0-9]{2}:){5}[a-fA-F0-9]{2}$/.test(boundMac1)
                    && /^([a-fA-F0-9]{2}:){5}[a-fA-F0-9]{2}$/.test(boundMac2)
                    && nicJson.some(({ Mac }) => Mac.toUpperCase() === boundMac1.toUpperCase())
                    && nicJson.some(({ Mac }) => Mac.toUpperCase() === boundMac2.toUpperCase()),
                    reason: 'Bond MAC填写错误'
                };
            }

            return { status: false, reason: '网卡MAC信息未捕获' }
        }
        return { status: false, reason: '未找到该主机' }
    };

    const batchInstallMetadataSheet = (sheet: ExcelJS.Worksheet, metadata: ExcelJS.Worksheet) => {
        // PXE
        pxe.forEach(({ name }, idx) => metadata.getCell(`A${idx + 1}`).value = name);
        sheet.getColumn('pxeId').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$A$1:$A$${pxe.length}`]
        });

        // 操作系统
        operationSystem.forEach(({ name }, idx) => metadata.getCell(`B${idx + 1}`).value = name);
        sheet.getColumn('operationSystemId').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$B$1:$B$${operationSystem.length}`]
        });

        // IP池
        pool.forEach(({ name }, idx) => metadata.getCell(`C${idx + 1}`).value = name);
        sheet.getColumn('ippool').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$C$1:$C$${pool.length}`]
        });
    }

    const exportInstanceExcelHeader: { header: string, key: string, optionals?: any, operation?: any }[] = [
        { header: 'SN', key: 'sn' },
        { header: '主机名', key: 'hostname' },
        { header: '装机网IP', key: 'dhcpIp' },
        { header: '管理IP', key: 'oobIp' },
        { header: '业务IP', key: 'innerIp' },
        { header: '网络区域', key: 'netAreaId' },
        { header: '系统典配', key: 'hardwareId' },
        { header: 'PXE', key: 'pxeId' },
        { header: '操作系统', key: 'operationSystemId' },
        { header: '建设项目名', key: 'project_id' } 
    ]

    const filterTags = [
        ...(typeof filterState.project === 'undefined' ? [] : filterState.project.map(({name}) => name)),
        ...(typeof filterState.hardware === 'undefined' ? [] : filterState.hardware.map(({name}) => name)),
        ...(typeof filterState.netArea === 'undefined' ? [] : filterState.netArea.map(({name}) => name)),
        ...(typeof filterState.companyName === 'undefined' ? [] : filterState.companyName),
        ...(typeof filterState.statusTags === 'undefined' ? [] : filterState.statusTags),
    ].map(name => <Tag key={name}>{name}</Tag>)

    return (
        <Base
            title="设备管理"
            keys={specBaseKey === 'instance-list-batch' ? ['history', 'batch-list'] : [ 'instance', specBaseKey ]}
            breadcrumb={(() => {
                if (specBaseKey === 'instance-list-nonmanaged') {
                    return [{ title: '未纳管列表', path: '/instance/list?nonmanaged=1' }]
                }
                else if (specBaseKey === 'instance-list-managed') {
                    return [{ title: '已纳管列表', path: '/instance/list?managed=1' }]
                }
                else if (specBaseKey === 'instance-list-installable') {
                    return [{ title: '装机列表', path: '/instance/list?installable=1' }]
                }
                else if (specBaseKey === 'instance-list-batch') {
                    return [{ title: '装机批次记录', path: '/log/setup' }, { title: '批次设备', path: `${history.location.pathname}${history.location.search}` }]
                }
            })()}>


            {batchManageVisible && <InstanceBatchManagedForm
                visible={batchManageVisible}
                onOk={entity => {
                    if (typeof selectedArea === 'undefined') {
                        message.error('未选择区域');
                        return;
                    }
                    if (typeof entity.hardware === 'undefined') {
                        message.error('批量纳管需填写信息不完整');
                        return;
                    }

                    setSubmitCheckListState({
                        visible: true,
                        columns: managedExcelHeader,
                        data: selectedInstance.map(({ sn }) => ({ ...entity, sn })),
                        checkItem: checkManagedItem,
                        onDownload: (instances: any[]) => downloadExcel([
                            ...managedExcelHeader,
                            { header: '错误原因', key: '_checkStatusReason' }
                        ], instances.map((instance: any) => ({
                            ...instance,
                            'project_id': projects.find(({id}) => id === instance['project_id'])?.name,
                            'hardware': hardware.find(({id}) => id === instance['hardware'])?.name,
                            'netarea': net.find(({id}) => id === instance['netarea'])?.name
                        })), managedMetadataSheet, '检查清单.xlsx'),
                        onSubmit: instances => {
                            dispatch(manageInstances({ area: (selectedArea as any).id, instances, cb: () => dispatchFlushCount('any') }))

                            message.info('已提交纳管');
                            setBatchManageVisible(false);
                            setSubmitCheckListState(initialSubmitCheckListState);
                        }
                    });

                }}
                onCancel={() => setBatchManageVisible(false)} /> }

            {batchInstallVisible && <InstanceBatchInstallForm
                visible={batchInstallVisible}
                instances={selectedInstance}
                onOk={entity => {
                    const instances: IDistributeInstance[] = [];
                    for (let sn in entity.instances) {
                        instances.push({
                            sn,
                            areaId: (selectedArea as any).id as string,
                            hostname: entity.instances[sn]['hostname'],
                            ippool: entity.instances[sn]['ippool'],
                            innerIp: entity.instances[sn]['inner_ip'],
                            pxeId: entity['pxe'],
                            operationSystemId: entity['operation_system'],
                            boundMac1: entity.instances[sn]['bound_mac1'],
                            boundMac2: entity.instances[sn]['bound_mac2'],
                            boundType: entity.instances[sn]['bound_type']
                        })
                    }

                    setSubmitCheckListState({
                        visible: true,
                        columns: batchInstallExcelHeader,
                        data: instances,
                        checkItem: checkBatchInstallItem,
                        onDownload: (instances: any[]) => downloadExcel([
                            ...batchInstallExcelHeader,
                            { header: '错误原因', key: '_checkStatusReason' }
                        ], instances.map(instance => ({
                            ...instance,
                            'pxeId': pxe.find(({id}) => id === instance.pxeId)?.name,
                            'ippool': pool.find(({id}) => id === instance.ippool)?.name,
                            'operationSystemId': operationSystem.find(({id}) => id === instance.operationSystemId)?.name
                        })), batchInstallMetadataSheet, '检查清单.xlsx'),
                        onSubmit: instances => {
                            dispatch(distributeInstances({
                                area: (selectedArea as any).id,
                                name: entity['setup_name'],
                                instances,
                                cb: () => dispatchFlushCount('any')
                            }));
                            message.info('已提交装机批次');
                            setBatchInstallVisible(false);
                            setSubmitCheckListState(initialSubmitCheckListState);
                        }
                    });

                }}
                onCancel={() => setBatchInstallVisible(false)} /> }

            {netInstance.visible && <NetInstanceForm
                visible={netInstance.visible}
                disabled={!netInstance.instance?.tags.some(({ content }) => ['装机核验通过', '待装机'].includes(content))}
                onOk={entity => {
                    const modify: IModifyNetInfo = {
                        areaId: entity['net_area'],
                        hostname: entity['hostname'],
                        ippool: entity['ippool'],
                        innerIp: entity['inner_ip'],
                        boundMac1: entity['bound_mac1'],
                        boundMac2: entity['bound_mac2'],
                        boundType: entity['bound_type']
                    }

                    dispatch(modifyInstanceNetInfo({ area: (selectedArea as any).id, sn: (netInstance.instance as any).sn, modify, cb: () => dispatchFlushCount('any') }));
                    message.info('已提交修改网络信息');
                    setNetInstance({ visible: false });
                }}
                instance={netInstance.instance}
                onCancel={() => setNetInstance({ visible: false })} />}

            {modifyOobState.visible && <OobForm
                visible={modifyOobState.visible}
                username={modifyOobState.instance?.oobUsername}
                onOk={entity => {
                    const modify: IModifyOobInfo = {
                        username: entity['username'],
                        password: entity['password']
                    }
                    dispatch(modifyOobInstance({ area: (selectedArea as any).id, sn: (modifyOobState.instance as any).sn, modify, cb: () => dispatchFlushCount('any') }));
                    message.info('已提交修改管理账户');
                    setModifyOobState({ visible: false });
                }}
                onCancel={() => setModifyOobState({ visible: false })} />}

            {importManage.visible && <Drawer
                visible={importManage.visible}
                onClose={() => setImportManage({ visible: false, uploadVisible: true, payload: [] })}
                width={400}
                title="上传纳管表"
                closable>
                {importManage.uploadVisible
                ? <div style={{ height: 550 }}>
                    <Upload.Dragger beforeUpload={file => importExcel(file, (data, mapper) => {
                        const instances = data.filter((line: any) => typeof line[0] !== 'undefined').map((line: any) => {
                            const instance: { [key: string]: any } = {};
                            for (let key in mapper) {
                                instance[key] = line[mapper[key]];
                            }
                            instance['project_id'] = projects.find(({ name }) => name === instance['project_id'])?.id;
                            instance['hardware'] = hardware.find(({ name }) => name === instance['hardware'])?.id;
                            instance['netarea'] = net.find(({ name }) => name === instance['netarea'])?.id;

                            return instance;
                        });

                        setSubmitCheckListState({
                            visible: true,
                            columns: managedExcelHeader,
                            data: instances,
                            checkItem: checkManagedItem,
                            onDownload: (instances: any[]) => downloadExcel([
                                ...managedExcelHeader,
                                { header: '错误原因', key: '_checkStatusReason' }
                            ], instances.map((instance: any) => ({
                                ...instance,
                                'project_id': projects.find(({id}) => id === instance['project_id'])?.name,
                                'hardware': hardware.find(({id}) => id === instance['hardware'])?.name,
                                'netarea': net.find(({id}) => id === instance['netarea'])?.name
                            })), managedMetadataSheet, '检查清单.xlsx'),
                            onSubmit: instances => {
                                dispatch(manageInstances({ area: (selectedArea as any).id, instances, cb: () => dispatchFlushCount('any') }))

                                message.info('已提交纳管');
                                setImportManage({ visible: false, uploadVisible: true, payload: [] });
                                setSubmitCheckListState(initialSubmitCheckListState);
                            }
                        });
                    })}>
                        <p style={{ fontSize: 270, color: 'darkblue' }}><InboxOutlined /></p>
                        <p>点击此处或拖拽纳管Excel表到该区域实现上传。</p>
                    </Upload.Dragger>
                </div>
                : <></>}
            </Drawer>}

            {importSetupBatch.visible && <Drawer
                visible={importSetupBatch.visible}
                onClose={() => setImportSetupBatch({ visible: false, uploadVisible: true, payload: [], name: '' })}
                width={400}
                title="上传创建批次表"
                closable>
                <Input placeholder="请输入批次名称" value={importSetupBatch.name} onChange={e => setImportSetupBatch({ ...importSetupBatch, name: e.target.value })} />
                {importSetupBatch.uploadVisible
                ? <div style={{ height: 550 }}>
                    <Upload.Dragger beforeUpload={file => {
                        if (importSetupBatch.name === '') {
                            message.error('尚未输入批次名称，请先输入批次名称。');
                            return false;
                        }

                        return importExcel(file, (data, mapper) => {
                            const instances = data.filter((line: any) => typeof line[0] !== 'undefined').map((line: any) => {
                                const instance: { [key: string]: any } = {};
                                for (let key in mapper) {
                                    instance[key] = line[mapper[key]];
                                }

                                instance['areaId'] = selectedArea?.id;
                                instance['pxeId'] = pxe.find(({ name }) => name === instance['pxeId'])?.id;
                                instance['operationSystemId'] = operationSystem.find(({ name }) => name === instance['operationSystemId'])?.id;
                                instance['ippool'] = pool.find(({ name }) => name === instance['ippool'])?.id;

                                return instance;
                            });

                            setSubmitCheckListState({
                                visible: true,
                                columns: batchInstallExcelHeader,
                                data: instances,
                                checkItem: checkBatchInstallItem,
                                onDownload: (instances: any[]) => downloadExcel([
                                    ...batchInstallExcelHeader,
                                    { header: '错误原因', key: '_checkStatusReason' }
                                ], instances.map((instance: any) => ({
                                    ...instance,
                                    'pxeId': pxe.find(({id}) => id === instance.pxeId)?.name,
                                    'ippool': pool.find(({id}) => id === instance.ippool)?.name,
                                    'operationSystemId': operationSystem.find(({id}) => id === instance.operationSystemId)?.name
                                })), batchInstallMetadataSheet, '检查清单.xlsx'),
                                onSubmit: instances => {
                                    dispatch(distributeInstances({
                                        area: (selectedArea as any).id,
                                        name: importSetupBatch.name,
                                        instances,
                                        cb: () => dispatchFlushCount('any')
                                    }));
                                    message.info('已提交装机批次');
                                    setImportSetupBatch({ visible: false, uploadVisible: true, payload: [], name: '' });
                                    setSubmitCheckListState(initialSubmitCheckListState);
                                }
                            });
                        });
                    }}>
                        <p style={{ fontSize: 270, color: 'darkblue' }}><InboxOutlined /></p>
                        <p>点击此处或拖拽创建批次Excel表到该区域实现上传。</p>
                    </Upload.Dragger>
                </div>
                : <></>}
            </Drawer>}

            {filterState.visible && <Drawer
                visible={filterState.visible}
                onClose={() => setFilterState({ ...filterState, visible: false })}
                width={400}
                title="主机筛选"
                closable>
                <Space direction="vertical" style={{width: '100%'}}>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>建设项目: </label>
                        <Select
                            mode="multiple"
                            placeholder="请选择筛选的建设项目"
                            style={{width: '100%'}}
                            onChange={(values: number[]) => setFilterState({ ...filterState, project: projects.filter(({ id }) => values.includes(id)).concat(values.some(id => id === -1) ? [{id: -1, name: '无隶属项目', description: '', createdAt: ''}] : []) })}
                            value={filterState.project?.map(({ id }) => id)}>{projects.map(({ name, id }) => <Select.Option value={id} key={id}>{name}</Select.Option>).concat([<Select.Option value={-1} key={-1}>无隶属项目</Select.Option>])}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>系统典配: </label>
                        <Select
                            mode="multiple"
                            placeholder="请选择筛选的系统典配"
                            style={{width: '100%'}}
                            onChange={(values: number[]) => setFilterState({ ...filterState, hardware: hardware.filter(({ id }) => values.includes(id)) })}
                            value={filterState.hardware?.map(({ id }) => id)}>{hardware.map(({ name, id }) => <Select.Option value={id} key={id}>{name}</Select.Option>)}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>网络区域: </label>
                        <Select
                            mode="multiple"
                            placeholder="请选择筛选的网络区域"
                            style={{width: '100%'}}
                            onChange={(values: number[]) => setFilterState({ ...filterState, netArea: net.filter(({ id }) => values.includes(id)) })}
                            value={filterState.netArea?.map(({ id }) => id)}>{net.map(({ name, id }) => <Select.Option value={id} key={id}>{name}</Select.Option>)}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>设备厂商: </label>
                        <Select
                            mode="multiple"
                            placeholder="请选择筛选的设备厂商"
                            style={{width: '100%'}}
                            onChange={(values: string[]) => setFilterState({ ...filterState, companyName: companys.filter(company => values.includes(company)) })}
                            value={filterState.companyName}>{companys.map(company => <Select.Option value={company} key={company}>{company}</Select.Option>)}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>状态筛选: </label>
                        <Select
                            mode="multiple"
                            placeholder="请选择筛选的主机状态"
                            style={{width: '100%'}}
                            onChange={(values: string[]) => setFilterState({ ...filterState, statusTags: filterStatus.filter(status => values.includes(status)) })}
                            value={filterState.statusTags}>{filterStatus.map(status => <Select.Option value={status} key={status}>{status}</Select.Option>)}</Select>
                    </div>
                </Space>
            </Drawer>}

            {submitCheckListState.visible && <InstanceSubmitCheckList {...submitCheckListState} onCancel={() => setSubmitCheckListState(initialSubmitCheckListState)} />}

            {detailState !== '' && <InstanceDetail sn={detailState} onClose={() => setDetailState(initialDetailState)} />}

            {installHistoryState !== '' && <InstanceInstallHistory sn={installHistoryState} onClose={() => setInstallHistoryState(initialInstallHistoryState)} />}

            <Card>
                <PageHeader
                    title="设备列表"
                    extra={[
                        <Switch key="flush-sche"
                            checkedChildren="开启定时刷新"
                            unCheckedChildren="关闭定时刷新"
                            checked={typeof flushSched.handle !== 'undefined'}
                            onChange={checked => {
                                if (checked) {
                                    setFlushSched({ handle: setInterval(flushCallback, 5000) });
                                }
                                else if (typeof flushSched.handle !== 'undefined') {
                                    clearInterval(flushSched.handle);
                                    setFlushSched({});
                                }
                            }}
                        />,
                        <Button key="flush" onClick={() => flushCallback()} icon={<RetweetOutlined />} />,

                        <Button key="sync" onClick={() => dispatch(syncInstance({ area: selectedArea ? selectedArea.id : '', cb: () => dispatchFlushCount('any') }))}>手动同步</Button>,

                        <Divider type="vertical" key="divider-3"></Divider>,

                        <Dropdown key="dropdown" overlay={
                            <Menu>
                                <Menu.ItemGroup title="管理操作">
                                    <Menu.Item>
                                        <Button key="manage"
                                            size="small" type="text"
                                            disabled={disabledByTags(['未纳管', '可分配', '待装机'])}
                                            onClick={() => {
                                                setBatchManageVisible(true);

                                                clearInterval(flushSched.handle); setFlushSched({});
                                            }}>批量纳管</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button key="batch"
                                            size="small" type="text"
                                            disabled={disabledByTags(['可分配'])}
                                            onClick={() => {
                                                setBatchInstallVisible(true);

                                                clearInterval(flushSched.handle); setFlushSched({});
                                            }}>创建装机批次</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Popconfirm
                                            title="是否将选中的主机退出当前所在批次？"
                                            key="back"
                                            visible={easyBatchPopConfirm.visible && easyBatchPopConfirm.type === 'back'}
                                            onConfirm={() => {
                                                const sn: string[] = selectedInstance.map(({ sn }) => sn);
                                                dispatch(nondistributeInstance({ area: (selectedArea as any).id, sn, cb: () => dispatchFlushCount('any') }));

                                                setEasyBatchPopConfirm(initialEasyModifyPopConfirmState);
                                            }}
                                            onCancel={() => setEasyBatchPopConfirm(initialEasyModifyPopConfirmState)}>
                                            <Button 
                                                size="small" type="text"
                                                disabled={disabledByTags(['可编辑', '已装机', '装机核验通过', '待装机', '装机成功', '装机失败'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'back' })}>退出当前批次</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup title="设置设备状态">
                                    <Menu.Item>
                                        <Popconfirm
                                            title="是否将选中的主机编辑典配？"
                                            key="reset"
                                            visible={easyBatchPopConfirm.visible && easyBatchPopConfirm.type === 'reset'}
                                            onConfirm={() => {
                                                const sn: string[] = selectedInstance.map(({ sn }) => sn);
                                                dispatch(resetInstance({ area: (selectedArea as any).id, sn, cb: () => dispatchFlushCount('any') }));

                                                setEasyBatchPopConfirm(initialEasyModifyPopConfirmState);
                                            }}
                                            onCancel={() => setEasyBatchPopConfirm(initialEasyModifyPopConfirmState)}>
                                            <Button
                                                size="small" type="text"
                                                disabled={disabledByTags(['装机成功', '装机失败'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'reset' })}>编辑典配</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="是否取消选中的主机编辑？"
                                            key="reset"
                                            visible={easyBatchPopConfirm.visible && easyBatchPopConfirm.type === 'recovery_reset'}
                                            onConfirm={() => {
                                                const sn: string[] = selectedInstance.map(({ sn }) => sn);
                                                dispatch(recoveryResetInstance({ area: (selectedArea as any).id, sn, cb: () => dispatchFlushCount('any') }));

                                                setEasyBatchPopConfirm(initialEasyModifyPopConfirmState);
                                            }}
                                            onCancel={() => setEasyBatchPopConfirm(initialEasyModifyPopConfirmState)}>
                                            <Button
                                                size="small" type="text"
                                                disabled={disabledByTags(['可编辑'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'recovery_reset' })}>取消编辑</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="是否将选中的主机标记为装机成功？"
                                            key="reset"
                                            visible={easyBatchPopConfirm.visible && easyBatchPopConfirm.type === 'set_install_success'}
                                            onConfirm={() => {
                                                const sn: string[] = selectedInstance.map(({ sn }) => sn);
                                                dispatch(setInstanceInstallSuccess({ area: (selectedArea as any).id, sn, cb: () => dispatchFlushCount('any') }));

                                                setEasyBatchPopConfirm(initialEasyModifyPopConfirmState);
                                            }}
                                            onCancel={() => setEasyBatchPopConfirm(initialEasyModifyPopConfirmState)}>
                                            <Button
                                                size="small" type="text"
                                                disabled={disabledByTags(['待装机', '可编辑', '装机失败'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'set_install_success' })}>设定为装机成功</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="是否将选中的主机标记为装机失败？"
                                            key="reset"
                                            visible={easyBatchPopConfirm.visible && easyBatchPopConfirm.type === 'set_install_failure'}
                                            onConfirm={() => {
                                                const sn: string[] = selectedInstance.map(({ sn }) => sn);
                                                dispatch(setInstanceInstallFailure({ area: (selectedArea as any).id, sn, cb: () => dispatchFlushCount('any') }));

                                                setEasyBatchPopConfirm(initialEasyModifyPopConfirmState);
                                            }}
                                            onCancel={() => setEasyBatchPopConfirm(initialEasyModifyPopConfirmState)}>
                                            <Button
                                                size="small" type="text"
                                                disabled={disabledByTags(['待装机', '可编辑', '装机成功'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'set_install_failure' })}>设定为装机失败</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup title="装机操作">
                                    <Menu.Item>
                                        <Popconfirm
                                            title="是否确认批量装机？"
                                            key="install"
                                            visible={easyBatchPopConfirm.visible && easyBatchPopConfirm.type === 'install'}
                                            onConfirm={() => {
                                                const sn: string[] = selectedInstance.map(({ sn }) => sn);
                                                dispatch(installInstances({ area: (selectedArea as any).id, sn, cb: () => dispatchFlushCount('any') }));

                                                setEasyBatchPopConfirm(initialEasyModifyPopConfirmState);
                                            }}
                                            onCancel={() => setEasyBatchPopConfirm(initialEasyModifyPopConfirmState)}>
                                            <Button
                                                size="small" type="text"
                                                disabled={disabledByTags(['可编辑', '装机核验通过'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'install' })}>批量装机</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="是否将选中的主机取消装机？"
                                            key="cancel"
                                            visible={easyBatchPopConfirm.visible && easyBatchPopConfirm.type === 'cancel'}
                                            onConfirm={() => {
                                                const sn: string[] = selectedInstance.map(({ sn }) => sn);
                                                dispatch(cancelInstallInstance({ area: (selectedArea as any).id, sn, cb: () => dispatchFlushCount('any') }));

                                                setEasyBatchPopConfirm(initialEasyModifyPopConfirmState);
                                            }}
                                            onCancel={() => setEasyBatchPopConfirm(initialEasyModifyPopConfirmState)}>
                                            <Button 
                                                size="small" type="text"
                                                disabled={disabledByTags(['安装中'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'cancel' })}>取消装机</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup title="上传操作">
                                    <Menu.Item>
                                        <Button size="small" type="text" key="uploadManageExcel" onClick={() => setImportManage({ visible: true, uploadVisible: true, payload: [] })}>上传纳管表</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="text" key="uploadBatchInstallExcel" onClick={() => setImportSetupBatch({ visible: true, uploadVisible: true, payload: [], name: '' })}>上传装机批次表</Button>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                            </Menu>}>
                            <Button>设置与操作 <DownOutlined /> </Button>
                        </Dropdown>,
                    ]}/>

                <Form form={searchForm} onFinish={({ keyword }) => {
                    if (keyword === '') {
                        setSearchState({ keyword: [] });
                    }
                    else {
                        setSearchState({ keyword: keyword.split(/,|\n|;/) });
                    }

                    setSelectedInstance([]);
                }}>
                    <Row>
                        <Col offset={6} span={12}>
                            <Form.Item name="keyword"><Input.TextArea placeholder="请输入SN、业务IP或管理IP进行筛选，多条请以回车、逗号、分号分隔" autoSize={{ minRows: 1 }} /></Form.Item>

                            <div style={{ marginBottom: '.85rem' }}>
                                {filterTags.length !== 0 && <label style={{ fontSize: '.85rem', color: 'gray' }}>筛选条件：</label>}
                                {filterTags}
                            </div>
                        </Col>
                        <Col span={6}>
                            <Space>
                                <Button style={{margin: 0}} icon={<SearchOutlined />} htmlType="submit">查询</Button>
                                <Button style={{margin: 0}} icon={<FilterOutlined />} onClick={() => setFilterState({ ...filterState, visible: true })} />
                            </Space>
                        </Col>
                    </Row>
                </Form>
            </Card>

            <Table
                style={{ overflow: 'visible' }}
                title={() => <Space>
                    <Button size="small" onClick={() => setSelectedInstance(instances)}>跨页全选</Button>
                    <Button size="small" onClick={() => setSelectedInstance([])}>全部清空</Button>
                    {tagsCountTag.map(({ value, key }) => <Tag key={key}>{key}: {value}</Tag>)}
                </Space>}
                rowSelection={{
                    onChange: (_, selectedRow) => setSelectedInstance(selectedRow),
                    selectedRowKeys: selectedInstance.map(({ sn }) => sn),
                }}
                size="small"
                scroll={{ x: true }}
                pagination={{ pageSizeOptions: [ '10', '20', '50', '100', '200', '500' ], defaultPageSize: 20, showTotal: (total: number) => `当前实例总数 ${total}` }}
                columns={[
                    { title: 'SN', dataIndex: 'sn', fixed: true, sorter: (a: ISimpleInstance, b: ISimpleInstance) => a.sn.localeCompare(b.sn), render: (sn: string, instance: ISimpleInstance) => <span
                        onClick={() => {
                            if (selectedInstance.map(({ sn }) => sn).includes(sn)) {
                                setSelectedInstance(selectedInstance.reduce((prev, curr) => sn === curr.sn ? prev : [...prev, curr], [] as ISimpleInstance[]));
                            }
                            else {
                                setSelectedInstance([...selectedInstance, instance]);
                            }
                    }}>{sn}</span> },
                    { title: '管理IP', dataIndex: 'oobIp', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueStringCompare(a, b, 'oobIp') },
                    { title: '装机网IP', dataIndex: 'dhcpIp', sorter: (a: ISimpleInstance, b:ISimpleInstance) => elseValueStringCompare(a, b, 'dhcpIp') },
                    { title: '业务IP', dataIndex: 'innerIp', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueStringCompare(a, b, 'innerIp') },
                    { title: '最后更新时间', dataIndex: 'lastModifyAt', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueStringCompare(a, b, 'lastModifyAt') },
                    { title: '网络区域', dataIndex: 'netAreaId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'netAreaId'),
                        render: (_: any, instance: ISimpleInstance) => {
                        return instance.netAreaId && <>{netMapper[instance.netAreaId]}</>
                    }},
                    { title: '状态', dataIndex: 'status', render: (_: any, { sn, setupId, tags, netAreaId, hostname, ippool, innerIp, boundMac1, boundMac2, boundType }: ISimpleInstance) => {
                        return (
                            <>
                                {tags.map(({ color, content }, key) => <Tag key={key} color={color}>{content}</Tag>)}
                                {tags.some(({ content }) => content === '待装机') && false && <Button size="small" type="link" onClick={() => {
                                        const modify: IModifyNetInfo = {
                                            areaId: netAreaId,
                                            hostname,
                                            ippool,
                                            innerIp,
                                            boundMac1,
                                            boundMac2,
                                            boundType
                                        }

                                        dispatch(modifyInstanceNetInfo({ area: (selectedArea as any).id, sn , modify, cb: () => dispatchFlushCount('any') }));
                                    }}>重新检查</Button>}
                                {specBaseKey === 'instance-list-batch' && setupId !== batchId && <Tag color="gold">已不属于该装机批次</Tag>}
                            </>
                        )
                    }},
                    { title: '系统典配', dataIndex: 'hardwareId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'hardwareId'),
                        render: (text: number, {sn, tags, setupId}: ISimpleInstance) => text &&
                        <Tooltip title={allHardware.find(({ id }) => id === text)?.name}>
                            <Popconfirm
                                visible={easyModifyPopConfirm.visible && sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'hardware'}
                                title="是否确认修改?"
                                onConfirm={() => {
                                    dispatch(modifyInstanceTemplateInfo({
                                        area: (selectedArea as any).id,
                                        type: easyModifyPopConfirm.type,
                                        sn: easyModifyPopConfirm.sn,
                                        value: easyModifyPopConfirm.value,
                                        cb: () => dispatchFlushCount('any')
                                    }));

                                    setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' });
                                }}
                                onCancel={() => setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' })}>

                                <Select
                                    style={{ width: 180 }}
                                    dropdownMatchSelectWidth={false}
                                    onSelect={value => {
                                        setEasyModifyPopConfirm({ visible: true, type: 'hardware', value: value as number, sn });
                                    }}
                                    value={hardware.find(({ id }) => id === text) ? text : allHardware.find(({ id }) => id === text)?.name}
                                    disabled={(specBaseKey === 'instance-list-batch' && setupId !== batchId) || !tags.some(({ content }) => ['可编辑', '装机核验通过', '可分配', '待装机'].includes(content))}
                                    bordered={false}>
                                    {hardware.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
                                </Select>
                            </Popconfirm>
                        </Tooltip>
                    },
                    { title: 'PXE', dataIndex: 'pxeId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'pxeId'),
                        render: (text: number, {sn, tags, setupId}: ISimpleInstance) => text &&
                        <Tooltip title={pxe.find(({ id }) => id === text)?.name}>
                            <Popconfirm
                                visible={easyModifyPopConfirm.visible && sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'pxe'}
                                title="是否确认修改?"
                                onConfirm={() => {
                                    dispatch(modifyInstanceTemplateInfo({
                                        area: (selectedArea as any).id,
                                        type: easyModifyPopConfirm.type,
                                        sn: easyModifyPopConfirm.sn,
                                        value: easyModifyPopConfirm.value,
                                        cb: () => dispatchFlushCount('any')
                                    }));
                                    setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' });
                                }}
                                onCancel={() => setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' })}>

                                <Select
                                    style={{ width: 180 }}
                                    dropdownMatchSelectWidth={false}
                                    onSelect={value => {
                                        setEasyModifyPopConfirm({ visible: true, type: 'pxe', value: value as number, sn });
                                    }}
                                    value={pxe.find(({ id }) => id === text) ? text : allPxe.find(({ id }) => id === text)?.name}
                                    disabled={(specBaseKey === 'instance-list-batch' && setupId !== batchId) || !tags.some(({ content }) => ['可编辑', '装机核验通过', '可分配', '待装机'].includes(content))}
                                    bordered={false}>
                                    {pxe.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
                                </Select>
                            </Popconfirm>
                        </Tooltip>
                    },
                    { title: '操作系统', dataIndex: 'operationSystemId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'operationSystemId'),
                        render: (text: number, {sn, tags, setupId}: ISimpleInstance) => text &&
                        <Tooltip title={pxe.find(({ id }) => id === text)?.name}>
                            <Popconfirm
                                visible={easyModifyPopConfirm.visible && sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'operation_system'}
                                title="是否确认修改?"
                                onConfirm={() => {
                                    dispatch(modifyInstanceTemplateInfo({
                                        area: (selectedArea as any).id,
                                        type: easyModifyPopConfirm.type,
                                        sn: easyModifyPopConfirm.sn,
                                        value: easyModifyPopConfirm.value,
                                        cb: () => dispatchFlushCount('any')
                                    }));
                                    setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' });
                                }}
                                onCancel={() => setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' })}>

                                <Select
                                    style={{ width: 180 }}
                                    dropdownMatchSelectWidth={false}
                                    onSelect={value => {
                                        setEasyModifyPopConfirm({ visible: true, type: 'operation_system', value: value as number, sn });
                                    }}
                                    value={operationSystem.find(({ id }) => id === text) ? text : allOperationSystem.find(({ id }) => id === text)?.name}
                                    disabled={(specBaseKey === 'instance-list-batch' && setupId !== batchId) || !tags.some(({ content }) => ['可编辑', '装机核验通过', '可分配', '待装机'].includes(content))}
                                    bordered={false}>
                                    {operationSystem.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
                                </Select>
                            </Popconfirm>
                        </Tooltip>
                    },
                    { title: '隶属建设项目', dataIndex: 'projectId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'projectId'),
                        render: (text: number, {sn}: ISimpleInstance) => <Tooltip title={projects.find(({ id }) => id === text)?.name}>
                            <Popconfirm
                                visible={easyModifyPopConfirm.visible && sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'project'}
                                title="是否确认修改?"
                                onConfirm={() => {
                                    dispatch(modifyInstanceTemplateInfo({
                                        area: (selectedArea as any).id,
                                        type: easyModifyPopConfirm.type,
                                        sn: easyModifyPopConfirm.sn,
                                        value: easyModifyPopConfirm.value,
                                        cb: () => dispatchFlushCount('any')
                                    }));
                                    setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' });
                                }}
                                onCancel={() => setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' })}>

                                <Select
                                    style={{ width: 180 }}
                                    dropdownMatchSelectWidth={false}
                                    onSelect={value => {
                                        setEasyModifyPopConfirm({ visible: true, type: 'project', value, sn });
                                    }}
                                    value={text}
                                    bordered={false}>
                                    {projects.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
                                </Select>
                            </Popconfirm>
                        </Tooltip>
                    },
                    { title: '操作', dataIndex: 'operation', render: (_: any, instance: ISimpleInstance) => {
                        const menu = (
                            <Menu>
                                <Menu.ItemGroup>
                                    <Menu.Item>
                                        <Button size="small" type="link" disabled={specBaseKey === 'instance-list-batch' && instance.setupId !== batchId}
                                        onClick={() => {
                                            setModifyOobState({ visible: true, instance })

                                            clearInterval(flushSched.handle); setFlushSched({});
                                        }}>编辑管理账户</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="link" disabled={ (specBaseKey === 'instance-list-batch' && instance.setupId !== batchId) }
                                        onClick={() => {
                                            setNetInstance({ visible: true, instance })

                                            clearInterval(flushSched.handle); setFlushSched({});
                                        }}>编辑网络配置</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="link"
                                        onClick={() => setDetailState(instance.sn) }>详细信息</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="link"
                                        onClick={() => setInstallHistoryState(instance.sn) }>装机记录</Button>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup>
                                    <Menu.Item>
                                        <Popconfirm
                                            visible={easyModifyPopConfirm.visible && instance.sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'delete_instance'}
                                            title="是否确认删除?"
                                            onConfirm={() => {
                                                dispatch(removeInstance({
                                                    sn: easyModifyPopConfirm.sn,
                                                    cb: () => dispatchFlushCount('any')
                                                }));
                                                setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' });
                                            }}
                                            onCancel={() => setEasyModifyPopConfirm({ visible: false, type: '', value: 0, sn: '' })}>
                                            <Button size="small" type="link" disabled={ (specBaseKey === 'instance-list-batch' && instance.setupId !== batchId) }
                                            onClick={() => {
                                                setEasyModifyPopConfirm({ visible: true, type: 'delete_instance', value: 0, sn: instance.sn });
                                            }}>删除</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>
                            </Menu>
                        );

                        return (
                            <>
                                <Dropdown overlay={menu}>
                                    <Button type="link" size="small">操作</Button>
                                </Dropdown>
                            </>
                        ) 
                    }},
                ].map(item => ({ ...item, key: item.dataIndex }))}
                dataSource={instances.map(instance => ({ ...instance, key: instance.sn }))}
                footer={() => {
                    return (
                        <>
                            <Button size="small"
                                disabled={!disabledByTags(['安装成功'])}
                                onClick={() => {
                                    putPowerOn((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                        const description: string[] = [];
                                        (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                        if (description.length === 0) {
                                            notification.open(({ message: '操作成功' }))
                                        }
                                        else {
                                            notification.open({
                                                message: '存在未成功提交的操作请求',
                                                description: description.join(','),
                                                duration: null
                                            })
                                        }
                                    })
                                }}>开机</Button>
                            <Button size="small"
                                disabled={!disabledByTags(['安装成功'])}
                                onClick={() => {
                                    putPowerOff((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                        const description: string[] = [];
                                        (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                        if (description.length === 0) {
                                            notification.open(({ message: '操作成功' }))
                                        }
                                        else {
                                            notification.open({
                                                message: '存在未成功提交的操作请求',
                                                description: description.join(','),
                                                duration: null
                                            })
                                        }
                                    })
                                }}>关机</Button>
                            <Button size="small"
                                disabled={!disabledByTags(['安装成功'])}
                                onClick={() => {
                                putRestartFromPXE((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                    const description: string[] = [];
                                    (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                    if (description.length === 0) {
                                        notification.open(({ message: '操作成功' }))
                                    }
                                    else {
                                        notification.open({
                                            message: '存在未成功提交的操作请求',
                                            description: description.join(','),
                                            duration: null
                                        })
                                    }
                                })
                            }}>从PXE重启</Button>
                            <Button size="small"
                                disabled={!disabledByTags(['安装成功'])}
                                onClick={() => {
                                    putRestartFromDisk((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                        const description: string[] = [];
                                        (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                        if (description.length === 0) {
                                            notification.open(({ message: '操作成功' }))
                                        }
                                        else {
                                            notification.open({
                                                message: '存在未成功提交的操作请求',
                                                description: description.join(','),
                                                duration: null
                                            })
                                        }
                                    })
                                }}>从硬盘重启</Button>

                            <Divider type="vertical"></Divider>

                            <Button size="small" onClick={() => downloadExcel(managedExcelHeader.map(({ header, key }) => ({ header, key })), selectedInstance.map(({ sn, netAreaId, hardwareName }) => ({
                                'sn': sn,
                                'hardware': hardwareName,
                                'netarea': netMapper[netAreaId],
                            })), managedMetadataSheet, '纳管表.xlsx')}>下载纳管Excel表</Button>

                            <Button size="small" onClick={() => downloadExcel(batchInstallExcelHeader, selectedInstance.map(({ sn, pxeName, operationSystemName, hostname }) => ({
                                'sn': sn,
                                'pxeId': pxeName,
                                'operationSystemId': operationSystemName,
                                'hostname': hostname,
                            })), batchInstallMetadataSheet, '创建装机批次表.xlsx')}>下载创建批次Excel表</Button>

                            <Button size="small" onClick={() => downloadExcel(exportInstanceExcelHeader, selectedInstance.map(({ sn, hostname, dhcpIp, oobIp, innerIp, netAreaId, hardwareName, pxeName, operationSystemName, projectName }) => ({
                                'sn': sn,
                                'dhcpIp': dhcpIp,
                                'hostname': hostname,
                                'oobIp': oobIp,
                                'innerIp': innerIp,
                                'netAreaId': netMapper[netAreaId],
                                'hardwareId': hardwareName,
                                'pxeId': pxeName,
                                'operationSystemId': operationSystemName,
                                'project_id': projectName,
                            })), () => {}, '设备列表.xlsx')}>导出设备Excel表</Button>
                        </>
                    )
                }}/>
        </Base>
    )
}

const Page = connect(({
    info: { net, selectedArea, hardware, operationSystem, pxe },
    instance: { buttonPermission, instances, batchId, area, manageResult },
    ippool: { pool },
    project: { projects }
}: { info: IInfoModel, instance: IInstanceModel, ippool: IPPoolModel, project: IProjectModel }) => ({
    selectedArea,
    net,
    hardware: hardware.filter(({ enabled }) => enabled),
    allHardware: hardware,
    operationSystem: operationSystem.filter(({ enabled }) => enabled),
    allOperationSystem: operationSystem,
    pxe: pxe.filter(({ enabled }) => enabled),
    allPxe: pxe,
    instances,
    batchId,
    area,
    manageResult,
    pool,
    projects,
    buttonPermission
}))(page);
export default Page;
