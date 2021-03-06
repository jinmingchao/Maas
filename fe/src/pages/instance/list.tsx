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

    // ??????Excel?????????
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

    // ??????Excel???
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

    // ??????tag??????????????????????????????
    const disabledByTags = (keywords: string[]) => {
        const filteredInstance = instances.filter(({ sn }) => selectedInstance.map(({ sn }) => sn).includes(sn));

        return !filteredInstance.length
        || !filteredInstance.every(({tags}) => tags.some(({content}) => keywords.includes(content)))
        || (specBaseKey === 'instance-list-batch' && filteredInstance.some(({ setupId }) => setupId !== batchId));
    }

    // ???????????????????????????????????????
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
        { header: '???????????????',          key: 'project_id', optionals: { render: (pid: number) => projects.find(({ id }) => id === pid)?.name } },
        { header: '*????????????',           key: 'hardware', optionals: { render: (hid: number) => hardware.find(({ id   }) => id === hid)?.name } },
        { header: '*????????????',           key: 'netarea', optionals: { render: (nid: number) => netMapper[nid]         }                         },
        { header: '*??????IP',              key: 'oob_ip'                                                                                         },
        { header: '*????????????',            key: 'oob_username'                                                                                   },
        { header: '*????????????',            key: 'oob_password'                                                                                   },
        { header: '????????????',            key: 'place_dc'                                                                                        },
        { header: '??????',                key: 'place_building'                                                                                  },
        { header: '??????',                key: 'place_room'                                                                                      },
        { header: '??????',                key: 'place_cabinet'                                                                                   },
        { header: '??????U???',             key: 'place_u'                                                                                         },
        { header: '???U?????????',           key: 'place_u_count'                                                                                   },
        { header: '????????????',            key: 'belong_with'                                                                                     },
        { header: '????????????',            key: 'asset_prop'                                                                                      },
        { header: '??????',                key: 'area'                                                                                            },
        { header: '????????????',            key: 'netarch'                                                                                         },
        { header: '???????????????',          key: 'resource_pool_type'                                                                              },
        { header: '??????????????????',        key: 'cloud_type'                                                                                      },
        { header: '????????????',            key: 'asset_type'                                                                                      },
        { header: '????????????',            key: 'company'                                                                                         },
        { header: '????????????',            key: 'instance_model'                                                                                  },
        { header: '????????????',            key: 'instance_type'                                                                                   },
        { header: '????????????',            key: 'instance_prop'                                                                                   },
        { header: '????????????',            key: 'netcard_mod'                                                                                     },
        { header: '??????Raid??????',        key: 'default_raid'                                                                                    },
        { header: '????????????',            key: 'app_name'                                                                                        },
        { header: '????????????',            key: 'manage_status'                                                                                   },
        { header: '????????????',            key: 'distribute_status'                                                                               },
        { header: '????????????',            key: 'backup'                                                                                          },
        { header: '????????????',            key: 'inner_ip'                                                                                        },
        { header: '??????',                key: 'netmask'                                                                                         },
        { header: '??????',                key: 'gateway'                                                                                         },
        { header: 'vlan',                key: 'vlan'                                                                                            },
        { header: '???????????????1??????',     key: 'app_switch1_room'                                                                                },
        { header: '???????????????1??????',     key: 'app_switch1_cabinet'                                                                             },
        { header: '???????????????1????????????', key: 'app_switch1_name'                                                                                },
        { header: '???????????????1??????SN',   key: 'app_switch1_sn'                                                                                  },
        { header: '???????????????1??????',     key: 'app_switch1_port'                                                                                },
        { header: '???????????????2??????',     key: 'app_switch2_room'                                                                                },
        { header: '???????????????2??????',     key: 'app_switch2_cabinet'                                                                             },
        { header: '???????????????2????????????', key: 'app_switch2_name'                                                                                },
        { header: '???????????????2??????SN',   key: 'app_switch2_sn'                                                                                  },
        { header: '???????????????2??????',     key: 'app_switch2_port'                                                                                },
        { header: '?????????????????????',      key: 'man_switch_room'                                                                                 },
        { header: '?????????????????????',      key: 'man_switch_cabinet'                                                                              },
        { header: '???????????????????????????',  key: 'man_switch_name'                                                                                 },
        { header: '?????????????????????SN',    key: 'man_switch_sn'                                                                                   },
        { header: '?????????????????????',      key: 'man_switch_port'                                                                                 },
        { header: 'mac1',                key: 'mac1'                                                                                            },
        { header: 'mac2',                key: 'mac2'                                                                                            },
    ];
    const checkManagedItem = ({ hardware: hid, netarea: nid }: any) => {
        if (!hardware.some(({id}) => id === hid)) {
            return { status: false, reason: '????????????????????????' };
        }
        if (!net.some(({id}) => id === nid)) {
            return { status: false, reason: '????????????????????????' };
        }
        return { status: true, reason: '' };
    }
    const managedMetadataSheet = (sheet: ExcelJS.Worksheet, metadata: ExcelJS.Worksheet) => {
        // ??????????????????
        hardware.forEach(({ name }, idx) => metadata.getCell(`A${idx + 1}`).value = name);
        sheet.getColumn('hardware').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$A$1:$A$${hardware.length}`]
        });

        // ??????????????????
        net.forEach(({ name }, idx) => metadata.getCell(`B${idx + 1}`).value = name);
        sheet.getColumn('netarea').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$B$1:$B$${net.length}`]
        })

        // ?????????
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
        { header: '*????????????',  key: 'operationSystemId', optionals: { render: (oid: number) => operationSystem.find(({id}) => id === oid)?.name } },
        { header: '*????????????',  key: 'hostname'                                                                                                    },
        { header: '*IP?????????',  key: 'ippool', optionals: { render: (pid: number) => pool.find(({id}) => id === pid)?.name }                       },
        { header: '*??????IP',    key: 'innerIp'                                                                                                     },
        { header: 'Bond MAC 1', key: 'boundMac1', operation: { map: (val: string) => typeof val === 'undefined' ? '' : val.toUpperCase() }         },
        { header: 'Bond MAC 2', key: 'boundMac2', operation: { map: (val: string) => typeof val === 'undefined' ? '' : val.toUpperCase() }         },
        { header: 'Bond??????',   key: 'boundType'                                                                                                   },
    ]
    const checkBatchInstallItem = ({ sn, innerIp, pxeId, operationSystemId, ippool, boundMac1, boundMac2 }: any) => {
        if (!/^(\d{1,3}\.){3}\d{1,3}$/.test(innerIp)) {
            return { status: false, reason: '??????IP????????????' }
        }
        if (!pxe.some(({ id }) => id === pxeId)) {
            return { status: false, reason: 'PXE????????????' }
        }
        if (!operationSystem.some(({ id }) => id === operationSystemId)) {
            return { status: false, reason: '????????????????????????' }
        }
        if (!pool.some(({ id }) => id === ippool)) {
            return { status: false, reason: 'IP???????????????' }
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
                    reason: 'Bond MAC????????????'
                };
            }

            return { status: false, reason: '??????MAC???????????????' }
        }
        return { status: false, reason: '??????????????????' }
    };

    const batchInstallMetadataSheet = (sheet: ExcelJS.Worksheet, metadata: ExcelJS.Worksheet) => {
        // PXE
        pxe.forEach(({ name }, idx) => metadata.getCell(`A${idx + 1}`).value = name);
        sheet.getColumn('pxeId').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$A$1:$A$${pxe.length}`]
        });

        // ????????????
        operationSystem.forEach(({ name }, idx) => metadata.getCell(`B${idx + 1}`).value = name);
        sheet.getColumn('operationSystemId').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$B$1:$B$${operationSystem.length}`]
        });

        // IP???
        pool.forEach(({ name }, idx) => metadata.getCell(`C${idx + 1}`).value = name);
        sheet.getColumn('ippool').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$C$1:$C$${pool.length}`]
        });
    }

    const exportInstanceExcelHeader: { header: string, key: string, optionals?: any, operation?: any }[] = [
        { header: 'SN', key: 'sn' },
        { header: '?????????', key: 'hostname' },
        { header: '?????????IP', key: 'dhcpIp' },
        { header: '??????IP', key: 'oobIp' },
        { header: '??????IP', key: 'innerIp' },
        { header: '????????????', key: 'netAreaId' },
        { header: '????????????', key: 'hardwareId' },
        { header: 'PXE', key: 'pxeId' },
        { header: '????????????', key: 'operationSystemId' },
        { header: '???????????????', key: 'project_id' } 
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
            title="????????????"
            keys={specBaseKey === 'instance-list-batch' ? ['history', 'batch-list'] : [ 'instance', specBaseKey ]}
            breadcrumb={(() => {
                if (specBaseKey === 'instance-list-nonmanaged') {
                    return [{ title: '???????????????', path: '/instance/list?nonmanaged=1' }]
                }
                else if (specBaseKey === 'instance-list-managed') {
                    return [{ title: '???????????????', path: '/instance/list?managed=1' }]
                }
                else if (specBaseKey === 'instance-list-installable') {
                    return [{ title: '????????????', path: '/instance/list?installable=1' }]
                }
                else if (specBaseKey === 'instance-list-batch') {
                    return [{ title: '??????????????????', path: '/log/setup' }, { title: '????????????', path: `${history.location.pathname}${history.location.search}` }]
                }
            })()}>


            {batchManageVisible && <InstanceBatchManagedForm
                visible={batchManageVisible}
                onOk={entity => {
                    if (typeof selectedArea === 'undefined') {
                        message.error('???????????????');
                        return;
                    }
                    if (typeof entity.hardware === 'undefined') {
                        message.error('????????????????????????????????????');
                        return;
                    }

                    setSubmitCheckListState({
                        visible: true,
                        columns: managedExcelHeader,
                        data: selectedInstance.map(({ sn }) => ({ ...entity, sn })),
                        checkItem: checkManagedItem,
                        onDownload: (instances: any[]) => downloadExcel([
                            ...managedExcelHeader,
                            { header: '????????????', key: '_checkStatusReason' }
                        ], instances.map((instance: any) => ({
                            ...instance,
                            'project_id': projects.find(({id}) => id === instance['project_id'])?.name,
                            'hardware': hardware.find(({id}) => id === instance['hardware'])?.name,
                            'netarea': net.find(({id}) => id === instance['netarea'])?.name
                        })), managedMetadataSheet, '????????????.xlsx'),
                        onSubmit: instances => {
                            dispatch(manageInstances({ area: (selectedArea as any).id, instances, cb: () => dispatchFlushCount('any') }))

                            message.info('???????????????');
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
                            { header: '????????????', key: '_checkStatusReason' }
                        ], instances.map(instance => ({
                            ...instance,
                            'pxeId': pxe.find(({id}) => id === instance.pxeId)?.name,
                            'ippool': pool.find(({id}) => id === instance.ippool)?.name,
                            'operationSystemId': operationSystem.find(({id}) => id === instance.operationSystemId)?.name
                        })), batchInstallMetadataSheet, '????????????.xlsx'),
                        onSubmit: instances => {
                            dispatch(distributeInstances({
                                area: (selectedArea as any).id,
                                name: entity['setup_name'],
                                instances,
                                cb: () => dispatchFlushCount('any')
                            }));
                            message.info('?????????????????????');
                            setBatchInstallVisible(false);
                            setSubmitCheckListState(initialSubmitCheckListState);
                        }
                    });

                }}
                onCancel={() => setBatchInstallVisible(false)} /> }

            {netInstance.visible && <NetInstanceForm
                visible={netInstance.visible}
                disabled={!netInstance.instance?.tags.some(({ content }) => ['??????????????????', '?????????'].includes(content))}
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
                    message.info('???????????????????????????');
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
                    message.info('???????????????????????????');
                    setModifyOobState({ visible: false });
                }}
                onCancel={() => setModifyOobState({ visible: false })} />}

            {importManage.visible && <Drawer
                visible={importManage.visible}
                onClose={() => setImportManage({ visible: false, uploadVisible: true, payload: [] })}
                width={400}
                title="???????????????"
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
                                { header: '????????????', key: '_checkStatusReason' }
                            ], instances.map((instance: any) => ({
                                ...instance,
                                'project_id': projects.find(({id}) => id === instance['project_id'])?.name,
                                'hardware': hardware.find(({id}) => id === instance['hardware'])?.name,
                                'netarea': net.find(({id}) => id === instance['netarea'])?.name
                            })), managedMetadataSheet, '????????????.xlsx'),
                            onSubmit: instances => {
                                dispatch(manageInstances({ area: (selectedArea as any).id, instances, cb: () => dispatchFlushCount('any') }))

                                message.info('???????????????');
                                setImportManage({ visible: false, uploadVisible: true, payload: [] });
                                setSubmitCheckListState(initialSubmitCheckListState);
                            }
                        });
                    })}>
                        <p style={{ fontSize: 270, color: 'darkblue' }}><InboxOutlined /></p>
                        <p>???????????????????????????Excel??????????????????????????????</p>
                    </Upload.Dragger>
                </div>
                : <></>}
            </Drawer>}

            {importSetupBatch.visible && <Drawer
                visible={importSetupBatch.visible}
                onClose={() => setImportSetupBatch({ visible: false, uploadVisible: true, payload: [], name: '' })}
                width={400}
                title="?????????????????????"
                closable>
                <Input placeholder="?????????????????????" value={importSetupBatch.name} onChange={e => setImportSetupBatch({ ...importSetupBatch, name: e.target.value })} />
                {importSetupBatch.uploadVisible
                ? <div style={{ height: 550 }}>
                    <Upload.Dragger beforeUpload={file => {
                        if (importSetupBatch.name === '') {
                            message.error('??????????????????????????????????????????????????????');
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
                                    { header: '????????????', key: '_checkStatusReason' }
                                ], instances.map((instance: any) => ({
                                    ...instance,
                                    'pxeId': pxe.find(({id}) => id === instance.pxeId)?.name,
                                    'ippool': pool.find(({id}) => id === instance.ippool)?.name,
                                    'operationSystemId': operationSystem.find(({id}) => id === instance.operationSystemId)?.name
                                })), batchInstallMetadataSheet, '????????????.xlsx'),
                                onSubmit: instances => {
                                    dispatch(distributeInstances({
                                        area: (selectedArea as any).id,
                                        name: importSetupBatch.name,
                                        instances,
                                        cb: () => dispatchFlushCount('any')
                                    }));
                                    message.info('?????????????????????');
                                    setImportSetupBatch({ visible: false, uploadVisible: true, payload: [], name: '' });
                                    setSubmitCheckListState(initialSubmitCheckListState);
                                }
                            });
                        });
                    }}>
                        <p style={{ fontSize: 270, color: 'darkblue' }}><InboxOutlined /></p>
                        <p>?????????????????????????????????Excel??????????????????????????????</p>
                    </Upload.Dragger>
                </div>
                : <></>}
            </Drawer>}

            {filterState.visible && <Drawer
                visible={filterState.visible}
                onClose={() => setFilterState({ ...filterState, visible: false })}
                width={400}
                title="????????????"
                closable>
                <Space direction="vertical" style={{width: '100%'}}>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>????????????: </label>
                        <Select
                            mode="multiple"
                            placeholder="??????????????????????????????"
                            style={{width: '100%'}}
                            onChange={(values: number[]) => setFilterState({ ...filterState, project: projects.filter(({ id }) => values.includes(id)).concat(values.some(id => id === -1) ? [{id: -1, name: '???????????????', description: '', createdAt: ''}] : []) })}
                            value={filterState.project?.map(({ id }) => id)}>{projects.map(({ name, id }) => <Select.Option value={id} key={id}>{name}</Select.Option>).concat([<Select.Option value={-1} key={-1}>???????????????</Select.Option>])}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>????????????: </label>
                        <Select
                            mode="multiple"
                            placeholder="??????????????????????????????"
                            style={{width: '100%'}}
                            onChange={(values: number[]) => setFilterState({ ...filterState, hardware: hardware.filter(({ id }) => values.includes(id)) })}
                            value={filterState.hardware?.map(({ id }) => id)}>{hardware.map(({ name, id }) => <Select.Option value={id} key={id}>{name}</Select.Option>)}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>????????????: </label>
                        <Select
                            mode="multiple"
                            placeholder="??????????????????????????????"
                            style={{width: '100%'}}
                            onChange={(values: number[]) => setFilterState({ ...filterState, netArea: net.filter(({ id }) => values.includes(id)) })}
                            value={filterState.netArea?.map(({ id }) => id)}>{net.map(({ name, id }) => <Select.Option value={id} key={id}>{name}</Select.Option>)}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>????????????: </label>
                        <Select
                            mode="multiple"
                            placeholder="??????????????????????????????"
                            style={{width: '100%'}}
                            onChange={(values: string[]) => setFilterState({ ...filterState, companyName: companys.filter(company => values.includes(company)) })}
                            value={filterState.companyName}>{companys.map(company => <Select.Option value={company} key={company}>{company}</Select.Option>)}</Select>
                    </div>
                    <div style={{width: '100%'}}>
                        <label style={{lineHeight: '2rem'}}>????????????: </label>
                        <Select
                            mode="multiple"
                            placeholder="??????????????????????????????"
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
                    title="????????????"
                    extra={[
                        <Switch key="flush-sche"
                            checkedChildren="??????????????????"
                            unCheckedChildren="??????????????????"
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

                        <Button key="sync" onClick={() => dispatch(syncInstance({ area: selectedArea ? selectedArea.id : '', cb: () => dispatchFlushCount('any') }))}>????????????</Button>,

                        <Divider type="vertical" key="divider-3"></Divider>,

                        <Dropdown key="dropdown" overlay={
                            <Menu>
                                <Menu.ItemGroup title="????????????">
                                    <Menu.Item>
                                        <Button key="manage"
                                            size="small" type="text"
                                            disabled={disabledByTags(['?????????', '?????????', '?????????'])}
                                            onClick={() => {
                                                setBatchManageVisible(true);

                                                clearInterval(flushSched.handle); setFlushSched({});
                                            }}>????????????</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button key="batch"
                                            size="small" type="text"
                                            disabled={disabledByTags(['?????????'])}
                                            onClick={() => {
                                                setBatchInstallVisible(true);

                                                clearInterval(flushSched.handle); setFlushSched({});
                                            }}>??????????????????</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Popconfirm
                                            title="???????????????????????????????????????????????????"
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
                                                disabled={disabledByTags(['?????????', '?????????', '??????????????????', '?????????', '????????????', '????????????'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'back' })}>??????????????????</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup title="??????????????????">
                                    <Menu.Item>
                                        <Popconfirm
                                            title="???????????????????????????????????????"
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
                                                disabled={disabledByTags(['????????????', '????????????'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'reset' })}>????????????</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="????????????????????????????????????"
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
                                                disabled={disabledByTags(['?????????'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'recovery_reset' })}>????????????</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="????????????????????????????????????????????????"
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
                                                disabled={disabledByTags(['?????????', '?????????', '????????????'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'set_install_success' })}>?????????????????????</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="????????????????????????????????????????????????"
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
                                                disabled={disabledByTags(['?????????', '?????????', '????????????'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'set_install_failure' })}>?????????????????????</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup title="????????????">
                                    <Menu.Item>
                                        <Popconfirm
                                            title="???????????????????????????"
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
                                                disabled={disabledByTags(['?????????', '??????????????????'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'install' })}>????????????</Button>
                                        </Popconfirm>
                                    </Menu.Item>

                                    <Menu.Item>
                                        <Popconfirm
                                            title="???????????????????????????????????????"
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
                                                disabled={disabledByTags(['?????????'])}
                                                onClick={() => setEasyBatchPopConfirm({ visible: true, type: 'cancel' })}>????????????</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup title="????????????">
                                    <Menu.Item>
                                        <Button size="small" type="text" key="uploadManageExcel" onClick={() => setImportManage({ visible: true, uploadVisible: true, payload: [] })}>???????????????</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="text" key="uploadBatchInstallExcel" onClick={() => setImportSetupBatch({ visible: true, uploadVisible: true, payload: [], name: '' })}>?????????????????????</Button>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                            </Menu>}>
                            <Button>??????????????? <DownOutlined /> </Button>
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
                            <Form.Item name="keyword"><Input.TextArea placeholder="?????????SN?????????IP?????????IP?????????????????????????????????????????????????????????" autoSize={{ minRows: 1 }} /></Form.Item>

                            <div style={{ marginBottom: '.85rem' }}>
                                {filterTags.length !== 0 && <label style={{ fontSize: '.85rem', color: 'gray' }}>???????????????</label>}
                                {filterTags}
                            </div>
                        </Col>
                        <Col span={6}>
                            <Space>
                                <Button style={{margin: 0}} icon={<SearchOutlined />} htmlType="submit">??????</Button>
                                <Button style={{margin: 0}} icon={<FilterOutlined />} onClick={() => setFilterState({ ...filterState, visible: true })} />
                            </Space>
                        </Col>
                    </Row>
                </Form>
            </Card>

            <Table
                style={{ overflow: 'visible' }}
                title={() => <Space>
                    <Button size="small" onClick={() => setSelectedInstance(instances)}>????????????</Button>
                    <Button size="small" onClick={() => setSelectedInstance([])}>????????????</Button>
                    {tagsCountTag.map(({ value, key }) => <Tag key={key}>{key}: {value}</Tag>)}
                </Space>}
                rowSelection={{
                    onChange: (_, selectedRow) => setSelectedInstance(selectedRow),
                    selectedRowKeys: selectedInstance.map(({ sn }) => sn),
                }}
                size="small"
                scroll={{ x: true }}
                pagination={{ pageSizeOptions: [ '10', '20', '50', '100', '200', '500' ], defaultPageSize: 20, showTotal: (total: number) => `?????????????????? ${total}` }}
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
                    { title: '??????IP', dataIndex: 'oobIp', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueStringCompare(a, b, 'oobIp') },
                    { title: '?????????IP', dataIndex: 'dhcpIp', sorter: (a: ISimpleInstance, b:ISimpleInstance) => elseValueStringCompare(a, b, 'dhcpIp') },
                    { title: '??????IP', dataIndex: 'innerIp', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueStringCompare(a, b, 'innerIp') },
                    { title: '??????????????????', dataIndex: 'lastModifyAt', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueStringCompare(a, b, 'lastModifyAt') },
                    { title: '????????????', dataIndex: 'netAreaId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'netAreaId'),
                        render: (_: any, instance: ISimpleInstance) => {
                        return instance.netAreaId && <>{netMapper[instance.netAreaId]}</>
                    }},
                    { title: '??????', dataIndex: 'status', render: (_: any, { sn, setupId, tags, netAreaId, hostname, ippool, innerIp, boundMac1, boundMac2, boundType }: ISimpleInstance) => {
                        return (
                            <>
                                {tags.map(({ color, content }, key) => <Tag key={key} color={color}>{content}</Tag>)}
                                {tags.some(({ content }) => content === '?????????') && false && <Button size="small" type="link" onClick={() => {
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
                                    }}>????????????</Button>}
                                {specBaseKey === 'instance-list-batch' && setupId !== batchId && <Tag color="gold">???????????????????????????</Tag>}
                            </>
                        )
                    }},
                    { title: '????????????', dataIndex: 'hardwareId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'hardwareId'),
                        render: (text: number, {sn, tags, setupId}: ISimpleInstance) => text &&
                        <Tooltip title={allHardware.find(({ id }) => id === text)?.name}>
                            <Popconfirm
                                visible={easyModifyPopConfirm.visible && sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'hardware'}
                                title="???????????????????"
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
                                    disabled={(specBaseKey === 'instance-list-batch' && setupId !== batchId) || !tags.some(({ content }) => ['?????????', '??????????????????', '?????????', '?????????'].includes(content))}
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
                                title="???????????????????"
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
                                    disabled={(specBaseKey === 'instance-list-batch' && setupId !== batchId) || !tags.some(({ content }) => ['?????????', '??????????????????', '?????????', '?????????'].includes(content))}
                                    bordered={false}>
                                    {pxe.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
                                </Select>
                            </Popconfirm>
                        </Tooltip>
                    },
                    { title: '????????????', dataIndex: 'operationSystemId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'operationSystemId'),
                        render: (text: number, {sn, tags, setupId}: ISimpleInstance) => text &&
                        <Tooltip title={pxe.find(({ id }) => id === text)?.name}>
                            <Popconfirm
                                visible={easyModifyPopConfirm.visible && sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'operation_system'}
                                title="???????????????????"
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
                                    disabled={(specBaseKey === 'instance-list-batch' && setupId !== batchId) || !tags.some(({ content }) => ['?????????', '??????????????????', '?????????', '?????????'].includes(content))}
                                    bordered={false}>
                                    {operationSystem.map(({ id, name }) => <Select.Option key={id} value={id}>{name}</Select.Option>)}
                                </Select>
                            </Popconfirm>
                        </Tooltip>
                    },
                    { title: '??????????????????', dataIndex: 'projectId', sorter: (a: ISimpleInstance, b: ISimpleInstance) => elseValueNumberCompare(a, b, 'projectId'),
                        render: (text: number, {sn}: ISimpleInstance) => <Tooltip title={projects.find(({ id }) => id === text)?.name}>
                            <Popconfirm
                                visible={easyModifyPopConfirm.visible && sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'project'}
                                title="???????????????????"
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
                    { title: '??????', dataIndex: 'operation', render: (_: any, instance: ISimpleInstance) => {
                        const menu = (
                            <Menu>
                                <Menu.ItemGroup>
                                    <Menu.Item>
                                        <Button size="small" type="link" disabled={specBaseKey === 'instance-list-batch' && instance.setupId !== batchId}
                                        onClick={() => {
                                            setModifyOobState({ visible: true, instance })

                                            clearInterval(flushSched.handle); setFlushSched({});
                                        }}>??????????????????</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="link" disabled={ (specBaseKey === 'instance-list-batch' && instance.setupId !== batchId) }
                                        onClick={() => {
                                            setNetInstance({ visible: true, instance })

                                            clearInterval(flushSched.handle); setFlushSched({});
                                        }}>??????????????????</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="link"
                                        onClick={() => setDetailState(instance.sn) }>????????????</Button>
                                    </Menu.Item>
                                    <Menu.Item>
                                        <Button size="small" type="link"
                                        onClick={() => setInstallHistoryState(instance.sn) }>????????????</Button>
                                    </Menu.Item>
                                </Menu.ItemGroup>

                                <Menu.ItemGroup>
                                    <Menu.Item>
                                        <Popconfirm
                                            visible={easyModifyPopConfirm.visible && instance.sn === easyModifyPopConfirm.sn && easyModifyPopConfirm.type === 'delete_instance'}
                                            title="???????????????????"
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
                                            }}>??????</Button>
                                        </Popconfirm>
                                    </Menu.Item>
                                </Menu.ItemGroup>
                            </Menu>
                        );

                        return (
                            <>
                                <Dropdown overlay={menu}>
                                    <Button type="link" size="small">??????</Button>
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
                                disabled={!disabledByTags(['????????????'])}
                                onClick={() => {
                                    putPowerOn((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                        const description: string[] = [];
                                        (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                        if (description.length === 0) {
                                            notification.open(({ message: '????????????' }))
                                        }
                                        else {
                                            notification.open({
                                                message: '????????????????????????????????????',
                                                description: description.join(','),
                                                duration: null
                                            })
                                        }
                                    })
                                }}>??????</Button>
                            <Button size="small"
                                disabled={!disabledByTags(['????????????'])}
                                onClick={() => {
                                    putPowerOff((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                        const description: string[] = [];
                                        (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                        if (description.length === 0) {
                                            notification.open(({ message: '????????????' }))
                                        }
                                        else {
                                            notification.open({
                                                message: '????????????????????????????????????',
                                                description: description.join(','),
                                                duration: null
                                            })
                                        }
                                    })
                                }}>??????</Button>
                            <Button size="small"
                                disabled={!disabledByTags(['????????????'])}
                                onClick={() => {
                                putRestartFromPXE((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                    const description: string[] = [];
                                    (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                    if (description.length === 0) {
                                        notification.open(({ message: '????????????' }))
                                    }
                                    else {
                                        notification.open({
                                            message: '????????????????????????????????????',
                                            description: description.join(','),
                                            duration: null
                                        })
                                    }
                                })
                            }}>???PXE??????</Button>
                            <Button size="small"
                                disabled={!disabledByTags(['????????????'])}
                                onClick={() => {
                                    putRestartFromDisk((selectedArea as any).id, selectedInstance.map(({ sn }) => sn)).then(({ data })=> {
                                        const description: string[] = [];
                                        (data as IInstallMessage[]).forEach(({ status, message }) => status !== 'success' && description.push(message))
                                        if (description.length === 0) {
                                            notification.open(({ message: '????????????' }))
                                        }
                                        else {
                                            notification.open({
                                                message: '????????????????????????????????????',
                                                description: description.join(','),
                                                duration: null
                                            })
                                        }
                                    })
                                }}>???????????????</Button>

                            <Divider type="vertical"></Divider>

                            <Button size="small" onClick={() => downloadExcel(managedExcelHeader.map(({ header, key }) => ({ header, key })), selectedInstance.map(({ sn, netAreaId, hardwareName }) => ({
                                'sn': sn,
                                'hardware': hardwareName,
                                'netarea': netMapper[netAreaId],
                            })), managedMetadataSheet, '?????????.xlsx')}>????????????Excel???</Button>

                            <Button size="small" onClick={() => downloadExcel(batchInstallExcelHeader, selectedInstance.map(({ sn, pxeName, operationSystemName, hostname }) => ({
                                'sn': sn,
                                'pxeId': pxeName,
                                'operationSystemId': operationSystemName,
                                'hostname': hostname,
                            })), batchInstallMetadataSheet, '?????????????????????.xlsx')}>??????????????????Excel???</Button>

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
                            })), () => {}, '????????????.xlsx')}>????????????Excel???</Button>
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
