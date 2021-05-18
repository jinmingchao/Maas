import React, {useEffect, useReducer, useCallback} from 'react';
import Modal from 'antd/lib/modal/Modal';
import {Form, Select, Input, Divider, Button, notification, Checkbox, Space} from 'antd';
import {useDispatch, IInfoModel, syncAreaInfo, useSelector, ICloudbootAddHardware} from 'umi';
import {postAddHardware, putUpdateHardware} from '@/service/info';

const AddHardware = ({
    visible,
    onClose,

    edit,
    clone,
    originData,
    id,
    name,
    company,
    tpl
} : {
    visible: boolean,
    onClose: () => void,

    edit?: boolean,
    clone?: boolean,
    originData?: string,
    id?: number,
    name?: string,
    company?: string,
    tpl?: string,
}) => {
    const dispatch = useDispatch();
    const [form] = Form.useForm();

    const CONFIG_STATUS_ITEM_SELECT: number = 2;
    const CONFIG_STATUS_ITEM_INPUT: number = 3;

    interface IConfigStatusItemSelectOption {
        optionKey: number;
        name: string;
        value: string;
    }

    interface IConfigStatusItem {
        configItemKey: number;
        type: number;
        name: string;
        options: IConfigStatusItemSelectOption[];
    }

    interface IConfigStatus {
        configKey: number;
        name: string;
        configItem: IConfigStatusItem[];
    }

    const setConfigStatus = useCallback((_: IConfigStatus[], action: IConfigStatus[]) => {
        return action;
    }, [])

    const initialConfigStatus: IConfigStatus[] = [];
    const [configStatus, dispatchConfigStatus] = useReducer(setConfigStatus, initialConfigStatus);

    useEffect(() => {
        if (typeof tpl === 'undefined') {
            return;
        }

        let editOriginValue: any[] = [];
        if (originData) {
            editOriginValue = JSON.parse(originData);
        }
        const data: any[] = JSON.parse(tpl);
        const editValue: any = { model: name, company };

        const restore: IConfigStatus[] = [];
        editValue['config'] = {};
        data.forEach(config => {
            const configKey = restore.length;
            restore.push({ configKey, configItem: [], name: config['name'] });

            let editOriginItemValue: any[] = editOriginValue.find(({ Name }) => Name === config['name'])?.Data;
            if (!editOriginItemValue) {
                editOriginItemValue = [];
            }

            const items: any[] = config['data'];
            editValue['config'][configKey] = {};
            editValue['config'][configKey]['name'] = config['name'];
            editValue['config'][configKey]['item'] = {};
            items.forEach(item => {
                const configItemKey = restore[configKey].configItem.length;
                restore[configKey].configItem.push({
                    configItemKey,
                    name: item['name'],
                    type: item['type'] === 'select' ? CONFIG_STATUS_ITEM_SELECT : CONFIG_STATUS_ITEM_INPUT,
                    options: []
                });

                editValue['config'][configKey]['item'][configItemKey] = {};
                editValue['config'][configKey]['item'][configItemKey]['name'] = item['name'];
                if (item['type']=== 'select') {
                    const options: any[] = item['data'];
                    options.forEach(option => {
                        const optionKey = restore[configKey].configItem[configItemKey].options.length;
                        restore[configKey].configItem[configItemKey].options.push({
                            optionKey,
                            name: option['name'],
                            value: option['value']
                        });
                    })

                    if (editOriginItemValue.length !== 0) {
                        console.log(editOriginItemValue);
                        editValue['config'][configKey]['item'][configItemKey]['value'] = editOriginItemValue.find(({ Name }) => Name === item['name'])?.Value;
                    }
                }
                else {
                    if (editOriginItemValue.length === 0) {
                        editValue['config'][configKey]['item'][configItemKey]['value'] = (item['tpl'] as string).replaceAll('<{##}>', item['input']);
                    }
                    else {
                        editValue['config'][configKey]['item'][configItemKey]['value'] = editOriginItemValue.find(({ Name }) => Name === item['name'])?.Value;
                    }
                }
            });
        });

        dispatchConfigStatus(restore);
        form.setFieldsValue(editValue);
    }, [id]);

    const headPart = {
        labelCol: { span: 4 },
        wrapperCol: { span: 16 },
    }

    const { area, selectedArea } = useSelector(({ info: { area, selectedArea } }: { info: IInfoModel }) => ({ area, selectedArea }));

    return (
        <Modal width="80wh" visible={visible} onCancel={onClose} footer={null} title={edit ? '编辑典配' : (clone ? '克隆典配' : '添加典配')}>
            <Form form={form} onFinish={entity => {
                const content: any[] = [];
                for (let configKey in entity['config']) {
                    const config = entity['config'][configKey];
                    const contentConfig: any = {
                        Name: config['name'],
                        Data: []
                    }

                    for (let itemKey in config['item']) {
                        const item = config['item'][itemKey];
                        contentConfig['Data'].push({
                            Name: item['name'],
                            Value: item['value']
                        })
                    }

                    content.push(contentConfig);
                }

                const payload: ICloudbootAddHardware = {
                    company: entity['company'],
                    modelName: entity['model'],
                    data: JSON.stringify(content),
                    isSystemAdd: 'No',
                    tpl
                };

                if ((edit || clone) && id) {
                    if (edit) {
                        putUpdateHardware(id, payload).then(({ data: { status, message } }) => {
                            dispatch(syncAreaInfo((selectedArea as any).id));
                            notification.info({ message: status, description: message, duration: null });
                        });
                    }
                    else {
                        if (entity.target) {
                            entity.target.forEach((areaKey: string) => {
                                postAddHardware(areaKey, payload).then(({ data: { status, message } }) => {
                                    dispatch(syncAreaInfo((selectedArea as any).id));
                                    notification.info({ message: status, description: message, duration: null });
                                });
                            });
                        }
                    }
                }
                else {
                    if (entity.target) {
                        entity.target.forEach((areaKey: string) => {
                            postAddHardware(areaKey, payload).then(({ data: { status, message } }) => {
                                dispatch(syncAreaInfo((selectedArea as any).id));
                                notification.info({ message: status, description: message, duration: null });
                            });
                        });
                    }
                }

            }}>
                <Form.Item name="company" label="分类" {...headPart}><Input /></Form.Item>
                <Form.Item name="model" label="系统典配名称" {...headPart}><Input /></Form.Item>

                {configStatus.map(config => (
                    <div style={{ marginBottom: '8rem' }}>
                        <Divider>配置项 - {config.name}</Divider>
                        <Form.Item name={[ 'config', `${config.configKey}`, 'name' ]} hidden><Input /></Form.Item>

                        {config.configItem.map(configItem => (
                            <div style={{ marginBottom: '4rem' }}>
                                <Form.Item name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'name' ]} hidden><Input /></Form.Item>
                                <Form.Item name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'value' ]} label={configItem.name} {...headPart}>
                                {configItem.type === CONFIG_STATUS_ITEM_SELECT
                                ? (
                                    <Select>
                                        {configItem.options.map(option => (
                                            <Select.Option value={option.value}>[ {option.name} ] {option.value}</Select.Option>
                                        ))}
                                    </Select>
                                )
                                : (
                                    <Input />
                                )}
                                </Form.Item>
                            </div>
                        ))}
                    </div>
                ))}

                {!edit && 
                    <Form.Item name="target" label="属地分配" {...headPart}>
                        <Checkbox.Group>
                            <Space>
                                {area.map(singleArea => <Checkbox value={singleArea.id}>{singleArea.name}</Checkbox>)}
                            </Space>
                        </Checkbox.Group>
                    </Form.Item>
                }

                <Button htmlType="submit">提交</Button>
            </Form>
        </Modal>
    )
}

export default AddHardware;
