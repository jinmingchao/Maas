import React, {useReducer, useCallback, useEffect} from 'react';
import {Modal, Form, Select, Input, Button, Divider, Row, Col} from 'antd';
import { MinusCircleOutlined } from '@ant-design/icons'
import {IHardwareTemplate, useDispatch, createHardwareTemplate} from 'umi';

const AddHardwareTemplate = ({
    visible,
    onClose,

    id,
    name,
    company,
    tpl
}: {
    visible: boolean,
    onClose: () => void,

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
        enabled: boolean;
    }

    interface IConfigStatusItem {
        configItemKey: number;
        enabled: boolean;
        type: number;
        options: IConfigStatusItemSelectOption[];
    }

    interface IConfigStatus {
        configKey: number;
        enabled: boolean;
        configItem: IConfigStatusItem[]
    }

    const setConfigStatus = useCallback((state: IConfigStatus[], { type, payload, restore }: { type: 'addConfig' | 'delConfig' | 'addItem' | 'delItem' | 'addSelectItem' | 'delSelectItem' | 'changeType' | 'restore', payload?: number[], restore?: IConfigStatus[] }) => {
        const newlyState = [...state];

        switch (type) {
            case 'addConfig':
                return [...state, { configKey: state.length, enabled: true, configItem: [] }];

            case 'delConfig':
                if (typeof payload === 'undefined') return state;
                newlyState[payload[0]].enabled = false;
                return newlyState;

            case 'addItem': {
                if (typeof payload === 'undefined') return state;
                const configItem = newlyState[payload[0]].configItem;
                newlyState[payload[0]].configItem = [...configItem, { configItemKey: configItem.length, enabled: true, type: 1, options: [] }]
                return newlyState;
            }

            case 'delItem': {
                if (typeof payload === 'undefined') return state;
                newlyState[payload[0]].configItem[payload[1]].enabled = false;
                return newlyState;
            }

            case 'addSelectItem': {
                if (typeof payload === 'undefined') return state;
                const options = newlyState[payload[0]].configItem[payload[1]].options;
                newlyState[payload[0]].configItem[payload[1]].options = [...options, { optionKey: options.length, enabled: true }];
                return newlyState;
            }

            case 'delSelectItem': {
                if (typeof payload === 'undefined') return state;
                newlyState[payload[0]].configItem[payload[1]].options[payload[2]].enabled = false;
                return newlyState;
            }

            case 'changeType': {
                if (typeof payload === 'undefined') return state;
                newlyState[payload[0]].configItem[payload[1]].type = payload[2];
                return newlyState;
            }

            case 'restore': {
                if (typeof restore === 'undefined') return state;
                return restore;
            }
        }
    }, []);

    const initialConfigStatus: IConfigStatus[] = [];
    const [configStatus, dispatchConfigStatus] = useReducer(setConfigStatus, initialConfigStatus);

    useEffect(() => {
        if (typeof tpl === 'undefined') {
            return;
        }

        const data: any[] = JSON.parse(tpl);
        const editValue: any = { model: name, company };

        const restore: IConfigStatus[] = [];
        editValue['config'] = {};
        data.forEach(config => {
            const configKey = restore.length;
            restore.push({ configKey, enabled: true, configItem: [] });

            editValue['config'][configKey] = {};
            editValue['config'][configKey]['name'] = config['name'];

            editValue['config'][configKey]['item'] = {}
            const items: any[] = config['data'];
            items.forEach(item => {
                const configItemKey = restore[configKey].configItem.length;
                restore[configKey].configItem.push({
                    configItemKey,
                    enabled: true,
                    type: item['type'] === 'select' ? CONFIG_STATUS_ITEM_SELECT : CONFIG_STATUS_ITEM_INPUT,
                    options: []
                });

                editValue['config'][configKey]['item'][configItemKey] = {};
                editValue['config'][configKey]['item'][configItemKey]['name'] = item['name'];
                editValue['config'][configKey]['item'][configItemKey]['type'] = item['type'];

                if (item['type'] === 'select') {
                    const options: any[] = item['data'];
                        editValue['config'][configKey]['item'][configItemKey]['option'] = {};
                    options.forEach(option => {
                        const optionKey = restore[configKey].configItem[configItemKey].options.length;
                        restore[configKey].configItem[configItemKey].options.push({
                            optionKey,
                            enabled: true
                        });

                        editValue['config'][configKey]['item'][configItemKey]['option'][optionKey] = {};
                        editValue['config'][configKey]['item'][configItemKey]['option'][optionKey]['name'] = option['name'];
                        editValue['config'][configKey]['item'][configItemKey]['option'][optionKey]['value'] = option['value'];
                    })
                }
                else {
                    editValue['config'][configKey]['item'][configItemKey]['inputValue'] = item['tpl'];
                    editValue['config'][configKey]['item'][configItemKey]['defaultInputValue'] = item['input'];
                }
            });
        });

        dispatchConfigStatus({ type: 'restore', restore });

        form.setFieldsValue(editValue);
    }, [id]);

    const headPart = {
        labelCol: { span: 4 },
        wrapperCol: { span: 16 },
    }

    return (
        <Modal width="80wh" visible={visible} onCancel={onClose} footer={null} title="添加典配模板">
            <Form form={form} onFinish={entity => {
                const tpl: any[] = [];
                for (let configKey in entity['config']) {
                    const config = entity['config'][configKey];
                    const name: string = config['name'];
                    const data: any[] = [];

                    const items = config['item'];
                    for (let itemKey in items) {
                        const item = items[itemKey];
                        const name: string = item['name'];
                        const type: string = item['type'];

                        if (type === 'select') {
                            const options = item['option'];
                            const values: any[] = [];
                            for (let optionKey in options) {
                                const option = options[optionKey];
                                const name = option['name'];
                                const value = option['value'];
                                values.push({ name, value, checked: false });
                            }
                            data.push({ name, type, data: values, default: '', tpl: '' });
                        }
                        else if (type === 'input') {
                            data.push({ name, type, tpl: item['inputValue'], input: item['defaultInputValue'], default: '' });
                        }
                    }

                    tpl.push({name, data});
                }
                const hardwareTemplate: IHardwareTemplate = { id, name: entity['model'], company: entity['company'], tpl: JSON.stringify(tpl) };

                dispatch(createHardwareTemplate(hardwareTemplate));
                onClose();
            }}>
                <Form.Item name="company" label="分类" {...headPart}><Input /></Form.Item>
                <Form.Item name="model" label="系统典配默认名称" {...headPart}><Input /></Form.Item>

                {configStatus.filter(({ enabled }) => enabled).map(config => (
                    <div style={{ marginBottom: '8rem' }}>
                        <Divider>配置项</Divider>

                        <Row>
                            <Col span="8" offset="4">
                                <Form.Item label="配置名称" name={[ 'config', `${config.configKey}`, 'name' ]} {...headPart}><Input /></Form.Item>
                            </Col>
                            <Col span="1">
                                <MinusCircleOutlined style={{ lineHeight: '32px' }} onClick={() => dispatchConfigStatus({ type: 'delConfig', payload: [config.configKey] })} />
                            </Col>
                        </Row>

                        {config.configItem.filter(({ enabled }) => enabled).map(configItem => (
                            <div style={{ marginBottom: '4rem' }}>
                                <Row>
                                    <Col span="8" offset="4">
                                        <Form.Item label="配置项" name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'name' ]} {...headPart}><Input /></Form.Item>
                                    </Col>
                                    <Col span="7">
                                        <Form.Item label="配置项类型" name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'type' ]} {...headPart}>
                                            <Select onSelect={(value) => dispatchConfigStatus({ type: 'changeType', payload: [config.configKey, configItem.configItemKey, value === 'select' ? CONFIG_STATUS_ITEM_SELECT : CONFIG_STATUS_ITEM_INPUT] })}>
                                                <Select.Option value="select">下拉框</Select.Option>
                                                <Select.Option value="input">输入框</Select.Option>
                                            </Select>
                                        </Form.Item>
                                    </Col>
                                    <Col span="1">
                                        <MinusCircleOutlined style={{ lineHeight: '32px' }} onClick={() => dispatchConfigStatus({ type: 'delItem', payload: [config.configKey, configItem.configItemKey] })} />
                                    </Col>
                                </Row>

                                { configItem.type === CONFIG_STATUS_ITEM_INPUT
                                    ? <Row>
                                        <Col span="8" offset="4">
                                            <Form.Item label="执行脚本" name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'inputValue' ]} {...headPart}><Input /></Form.Item>
                                        </Col>
                                        <Col span="7">
                                            <Form.Item label="占位符默认值" name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'defaultInputValue' ]} {...headPart}><Input /></Form.Item>
                                        </Col>
                                    </Row>
                                    : <>
                                        <Row>
                                            <Col span={12} offset={6}>
                                                {configItem.options.filter(({ enabled }) => enabled).map(option => (
                                                    <div style={{ width: '100%' }}>
                                                        <Row>
                                                            <Col span={10}>
                                                                <Form.Item name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'option', `${option.optionKey}`, 'name' ]} label="名称"><Input /></Form.Item>
                                                            </Col>
                                                            <Col span={10} offset={2}>
                                                                <Form.Item name={[ 'config', `${config.configKey}`, 'item', `${configItem.configItemKey}`, 'option', `${option.optionKey}`, 'value' ]} label="执行脚本"><Input /></Form.Item>
                                                            </Col>
                                                            <Col span={1}>
                                                                <MinusCircleOutlined style={{ lineHeight: '32px', width: '32px', height: '32px' }} onClick={() => dispatchConfigStatus({ type: 'delSelectItem', payload: [config.configKey, configItem.configItemKey, option.optionKey] })} />
                                                            </Col>
                                                        </Row>
                                                    </div>
                                                ))}
                                            </Col>
                                        </Row>

                                        <Form.Item wrapperCol={{ offset: 10, span: 4 }}>
                                            <Button type="dashed" size="small" style={{width: '100%'}} onClick={() => { dispatchConfigStatus({ type: 'addSelectItem', payload: [config.configKey, configItem.configItemKey] }); }}>添加可选项</Button>
                                        </Form.Item>
                                    </>
                                }
                            </div>
                        ))}

                        <Form.Item wrapperCol={{ offset: 6, span: 12 }}>
                            <Button type="dashed" size="small" style={{width: '100%'}} onClick={() => { dispatchConfigStatus({ type: 'addItem', payload: [config.configKey] }); }}>添加配置项</Button>
                        </Form.Item>
                    </div>
                ))}

                <Form.Item wrapperCol={{ offset: 4, span: 16 }}>
                    <Divider />
                    <Button type="dashed" size="small" style={{width: '100%'}} onClick={() => dispatchConfigStatus({ type: 'addConfig' })}>添加配置</Button>
                </Form.Item>
                <Button htmlType="submit">提交</Button>
            </Form>
        </Modal>
    )
}

export default AddHardwareTemplate;
