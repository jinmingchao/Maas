import React, {useEffect} from 'react';
import {Modal, Form, Input, Button, notification} from 'antd';
import {useSelector, IInfoModel, IInfoForm, useDispatch, syncAreaInfo} from 'umi';
import {postAddOperationSystem, putUpdateOperationSystem} from '@/service/info';

const operationSystemAdd = ({
    visible,
    onClose,
    id,
    name,
    content
}: {
    visible: boolean,
    onClose: () => void,
    id?: number,
    name?: string,
    content?: string
}) => {
    const { selectedArea } = useSelector(({ info: { selectedArea } }: { info: IInfoModel }) => ({ selectedArea }));
    const [form] = Form.useForm();
    const dispatch = useDispatch();

    useEffect(() => {
        form.setFieldsValue({ name, content });
    }, [id]);

    return (
        <Modal width="80vw" visible={visible} onCancel={onClose} footer={null} title={id ? '更改操作系统配置' : '添加操作系统配置'}>
            <Form form={form} onFinish={(entity: IInfoForm) => {
                if (id) {
                    putUpdateOperationSystem((selectedArea as any).id, id, entity).then(() => {
                        dispatch(syncAreaInfo((selectedArea as any).id));
                        notification.info({ message: '更新成功', description: '更新成功', duration: null  });
                    })
                }
                else {
                    postAddOperationSystem((selectedArea as any).id, entity).then(() => {
                        dispatch(syncAreaInfo((selectedArea as any).id));
                        notification.info({ message: '添加成功', description: '添加成功', duration: null });
                    })
                }
            }}>
                <Form.Item label="名称" name="name"><Input /></Form.Item>
                <Form.Item label="内容" name="content"><Input.TextArea rows={32} /></Form.Item>

                <Button htmlType="submit">提交</Button>
            </Form>
        </Modal>
    )
}

const OperationSystemAdd = operationSystemAdd;
export default OperationSystemAdd;
