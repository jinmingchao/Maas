import React, {FC, useEffect, useRef, useState} from 'react';
import {Button, Card, Drawer, Form, Input, Modal, Select, Space, Switch, Table, Tag} from "antd";
import { useHistory, connect, deleteIPAction, postIPAction,  IPPoolModel,updateIPAction, fetchIPAction, useDispatch, IPItem, IPoolInfo, batchUpdateIPAction } from 'umi'
import ModalComponent from './_pool-ip-modal'
import {IPPoolItem} from "@/models/ippool";
import { CheckCircleOutlined, SearchOutlined  } from '@ant-design/icons'
import {IInfoModel} from "@/models/info";
import { CloseOutlined, CheckOutlined } from '@ant-design/icons';
import Highlighter from 'react-highlight-words';
import XLSX from 'xlsx';
import ExcelJS from 'exceljs';



const page: FC<{pool: IPPoolItem[], ipList: IPItem[], poolInfo: IPoolInfo, selectedPool: IPPoolItem } > = (
    { pool, poolInfo, ipList, selectedPool }) => {
    const history = useHistory();
    const dispatch = useDispatch();

    const searchInput = useRef(null);
    const [form] = Form.useForm();

    const getColumnSearchProps = (dataIndex: string) => ({
        filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
            <div style={{ padding: 8 }}>
                <Input
                  ref={searchInput}
                  placeholder={`Search ${dataIndex}`}
                  value={selectedKeys[0]}
                  onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
                  onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
                  style={{ width: 188, marginBottom: 8, display: 'block' }}
                />
                <Space>
                  <Button
                    type="primary"
                    onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
                    icon={<SearchOutlined />}
                    size="small"
                    style={{ width: 90 }}
                  >
                    Search
                  </Button>
                  <Button onClick={() => handleReset(clearFilters)} size="small" style={{ width: 90 }}>
                    Reset
                  </Button>
                </Space>
            </div>
        ),
        filterIcon: filtered => <SearchOutlined style={{ color: filtered ? '#1890ff' : undefined }} />,
        onFilter: (value: string, record: any) =>
            record[dataIndex]
                ? record[dataIndex].toString().toLowerCase().includes(value.toLowerCase())
                : '',
        onFilterDropdownVisibleChange: (visible: boolean) => {
            if (visible) {
                setTimeout(() => searchInput.current?.select(), 100);
            }
        },
        render: text =>
          useSearchedColumn === dataIndex ? (
            <Highlighter
              highlightStyle={{ backgroundColor: '#ffc069', padding: 0 }}
              searchWords={[useSearchText]}
              autoEscape
              textToHighlight={text ? text.toString() : ''}
            />
          ) : (
            text
          ),

    })
    const column = [
        { title: 'id', dataIndex: 'id', key: 'id' },
        { title: '域名', dataIndex: 'host', key: 'host', ...getColumnSearchProps('host')
        },
        // { title: '所属ip池', dataIndex: 'poolAsst', key: 'poolAsst' },
        { title: '是否可用', dataIndex: 'enabled', key: 'enabled' ,
            render: (text: any, record: IPItem) => {
                // if (record.enabled) {
                //     return <Tag icon={<CheckCircleOutlined />} color="success"></Tag>
                // } return <Tag icon={<CloseCircleOutlined  />} color="default"></Tag>
                return <Switch
                    // defaultChecked={record.enabled}
                    checked={record.enabled}
                    checkedChildren={<CheckOutlined />}
                    unCheckedChildren={<CloseOutlined />}
                    onChange={(value) => onChange(record, value)}
                    disabled={hasSelected}
                 />
            },
            filters: [
                  {
                    text: '可用',
                    value: true,
                  },
                  {
                    text: '不可用',
                    value: false,
                  },
            ],
            filterMultiple: false,
            onFilter: (value, record) => record.enabled === value,
            sorter: (a, b) => a.enabled.toString().length - b.enabled.toString().length,
        },
        { title: '操作', dataIndex: '', key: 'x',
            render: (text:any, record: IPItem, index: any) => <div>
                {/*<Button type={'primary'} onClick={() => editIP(record)}>编辑</Button>*/}
                <Button type={'primary'} danger onClick={() => deleteIP(record)} disabled={hasSelected}>删除</Button>
            </div>,
        }
    ]


    // const drawIPVisible: boolean = poolInfo['drawIPVisible'] || false;
    // const [useDrawerVisible, setDrawerVisible] = useState(drawIPVisible);



    const handleSearch = (selectedKeys, confirm, dataIndex) => {
        confirm();
        setSearchText(selectedKeys[0])
        setSearchedColumn(dataIndex)
    };

    const handleReset = clearFilters => {
        clearFilters();
        setSearchText('')
    };


    const searchText: string = poolInfo['searchText'] || '';
    const [useSearchText, setSearchText] = useState(searchText);

    const searchedColumn: string = poolInfo['searchedColumn'] || '';
    const [useSearchedColumn, setSearchedColumn] = useState(searchedColumn);

    const modalAddVisible: boolean = poolInfo['modalAddVisible'] || false;
    const [useModalAddVisible, setModalAddVisible] = useState(modalAddVisible);

    const modalEditVisible: boolean = poolInfo['modalEditVisible'] || false;
    const [useModalEditVisible, setModalEditVisible] = useState(modalEditVisible);

    const modalDelVisible: boolean = poolInfo['modalDelVisible'] || false;
    const [useModalDelVisible, setModalDelVisible] = useState(modalDelVisible);

    const modalBatchUseVisible: boolean = poolInfo['modalBatchUseVisible'] || false;
    const [useModalBatchUseVisible, setModalBatchUseVisible] = useState(modalBatchUseVisible);

    const batchUseValue: boolean = poolInfo['batchUseValue'] || false;
    const [useBatchUseValue, setBatchUseValue] = useState(batchUseValue);

    const modalBatchClearVisible: boolean = poolInfo['modalBatchClearVisible'] || false;
    const [useModalBatchClearVisible, setModalBatchClearVisible] = useState(modalBatchClearVisible);

    const editForm: any = poolInfo['editForm'] || {};
    const [useEditForm, setEditForm] = useState(editForm);

    const delId: number = poolInfo['delId'] || {};
    const [useDelId, setDelId] = useState(delId);

    const selectedRowKeys: number[] = poolInfo['selectedRowKeys'] || [];
    const [useRowKeys, setRowKeys] = useState(selectedRowKeys);


    const selectVisible: boolean = poolInfo['selectVisible'] || false;
    const [useSelectVisible, setSelectVisible] = useState(selectVisible);

    const selectValue: boolean = poolInfo['selectValue'] || false;
    const [useSelectValue, setSelectValue] = useState(selectValue);

    const editIP = (record: IPItem) => {
        // console.log(record)
        setEditForm(record);
        setModalEditVisible(true)
    }

    const onChange = async (record: IPItem, value: boolean) => {
        record.enabled = value;
        setSelectVisible(true)
        setSelectValue(value)
        dispatch(updateIPAction(record))
        await sleep(1000);
        dispatch(fetchIPAction(selectedPool.id))
        // setEditForm(record);
        // setModalEditVisible(true)
        setSelectVisible(false)
    }

    const addIP = () => {
        setModalAddVisible(true)
    }

    const deleteIP = (record: IPItem) => {
        setModalDelVisible(true)
        setDelId(record.id)
    }

    const onSubmit = async (values:IPItem) => {
        dispatch(postIPAction(values))
        setModalAddVisible(false)
        await sleep(1000);
        dispatch(fetchIPAction(selectedPool.id))
    }

    const onUpdate = async (values:IPItem) => {
        dispatch(updateIPAction(values))
        await sleep(1000);
        dispatch(fetchIPAction(selectedPool.id))
        setModalEditVisible(false)
    }
    const sleep = (ms:any) => new Promise(resolve => setTimeout(resolve, ms));

    const onDelete = async () => {
        dispatch(deleteIPAction(useDelId))
        await sleep(1000);
        dispatch(fetchIPAction(selectedPool.id))
        setModalDelVisible(false)
    }

    const oncancel = () => {
        setModalAddVisible(false)
    }

    const onCancelEdit = () => {
        setEditForm(null);
        setModalEditVisible(false)
    }

    const onDeleteCancel = () => {
        setDelId(0)
        setModalDelVisible(false)
    }

    const selectOnChange = (selectedRowKeys: any) => {
        setRowKeys(selectedRowKeys)
    }

    const rowSelection = {
        selectedRowKeys: useRowKeys,
        onChange: selectOnChange,
    };

    const hasSelected = useRowKeys.length > 0;

    const batchDel = () => {
        console.log(useRowKeys)
    }

    const onBatchChange = async (value: boolean) => {
        setBatchUseValue(value)
        setModalBatchUseVisible(true)
    }

    const onModalBatchUse = async () => {
        dispatch(batchUpdateIPAction({ipList: useRowKeys, enabled: useBatchUseValue, poolId: selectedPool.id}))
        setModalBatchUseVisible(false)
        setRowKeys(selectedRowKeys)
    }

    const onModalBatchCancel = () => {
        setModalBatchUseVisible(false)
    }

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
    const poolMapper = pool.reduce((prev, curr) => {
        prev[curr.id] = curr.name;
        return prev;
    }, {} as any);

    const enabledMapper: { [key: string]: string } = {
        'true': '是',
        'false': '否'
    }

    const managedExcelHeader: { header: string, key: string, optionals?: any }[] = [
        { header: 'id',                  key: 'id', optionals: { fixed: 'left' } },
        { header: '域名',                key: 'host' },
        { header: '是否可用',                key: 'enabled', optionals: { render: (enabled: boolean) => enabledMapper[enabled.toString()] } },
        { header: '所属IP池',            key: 'poolAsst', optionals: { render: (nid: number) => poolMapper[nid] } },
    ];

    const managedMetadataSheet = (sheet: ExcelJS.Worksheet, metadata: ExcelJS.Worksheet) => {
        // 硬件典配枚举
        [true, false].forEach((item , idx) => metadata.getCell(`A${idx + 1}`).value = enabledMapper[item.toString()]);
        sheet.getColumn('enabled').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$A$1:$A$${2}`]
        });

        // IP池枚举
        pool.forEach(({ name }, idx) => metadata.getCell(`B${idx + 1}`).value = name);
        sheet.getColumn('poolAsst').eachCell({includeEmpty: true}, cell => cell.dataValidation = {
            type: 'list',
            allowBlank: true,
            formulae: [`=metadata!$B$1:$B$${pool.length}`]
        })

    }

    return (
            <div style={{scrollbarWidth: 'none'}}>
                <Card title={"IP列表"} bordered={false}
                      extra={<Button type={'primary'} onClick={addIP}>新建</Button>}
                >
                    <div style={{ marginBottom: 16 }}>
                        <span>当前IP池： <Tag color="blue">{selectedPool.name}</Tag></span>
                        <span>可用IP数： <Tag  color="green">{ipList.filter(item => item.enabled).length}</Tag></span>
                        <span>已使用IP数： <Tag color="magenta">{ipList.filter(item => !item.enabled).length}</Tag></span>
                         <Button type="primary" size={'small'}  onClick={()=>onBatchChange(false)} disabled={!hasSelected} style={{marginLeft: 20}} >
                            批量使用
                        </Button>
                        <Button type="primary" size={'small'}  onClick={()=>onBatchChange(true)} disabled={!hasSelected} style={{marginLeft: 20}} >
                            批量释放
                        </Button>
                        {/*<Button type="primary" danger onClick={batchDel} disabled={!hasSelected} style={{marginLeft: 20}} >*/}
                        {/*    删除*/}
                        {/*</Button>*/}
                    </div>
                    <Table
                        size={'small'}
                        dataSource={ipList.map((item, key) => ({...item, key:item.id}))}
                        columns={column}
                        rowSelection={
                            rowSelection
                        }
                    />
                    <div>
                        <Button size="small" onClick={() => downloadExcel(managedExcelHeader.map(({ header, key }) => ({ header, key })), ipList.filter( ipItem => useRowKeys.includes(ipItem.id)).map(({ id, host,enabled, poolAsst }) => ({
                                'id': id,
                                'host': host,
                                'enabled': enabledMapper[enabled.toString()],
                                'poolAsst': poolMapper[poolAsst],
                            })), managedMetadataSheet, 'IP列表.xlsx')}
                        >下载IPExcel表</Button>
                    </div>
                </Card>
                <ModalComponent initValues={null} visible={useModalAddVisible} onCancel={oncancel} onCreate={onSubmit} cloud={pool}/>
                <ModalComponent initValues={useEditForm} visible={useModalEditVisible} onCancel={onCancelEdit} onCreate={onUpdate} cloud={pool}/>
                <Modal
                  title="删除"
                  visible={useModalDelVisible}
                  onOk={onDelete}
                  onCancel={onDeleteCancel}
                >
                  <p>确认删除？</p>
                </Modal>

                <Modal visible={useModalBatchUseVisible}
                       title={"是否执行批量操作"}
                       onOk={onModalBatchUse}
                       onCancel={onModalBatchCancel}
                >
                    <Table
                        dataSource={ipList.filter(ip => useRowKeys.includes(ip.id)).map((item, key) => ({...item, key:item.id}))}
                        columns={[
                            { title: 'id', dataIndex: 'id', key: 'id' },
                            { title: '域名', dataIndex: 'host', key: 'host'},
                        ]}
                        size={'small'}
                    />
                </Modal>
            </div>
    );
};

const CenterPage = connect((
        { ippool: { pool, poolInfo, ipList} , info: { net, selectedArea }}: { ippool: IPPoolModel, info: IInfoModel  }
    )=>(
        { pool, poolInfo, ipList, net, selectedArea }
    ))(page);
export default CenterPage;
