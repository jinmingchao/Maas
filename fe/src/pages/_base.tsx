import React, { FC, useEffect } from 'react';
import { Breadcrumb, Layout } from 'antd';
import { useHistory } from 'react-router-dom';
import { Dispatch, connect, IInfoModel, ISimpleArea, useDispatch, selectArea } from 'umi';
import CommonMenu from './_menu'
import CommonPageHeader from './_page-header'

const { Content } = Layout;

export interface IBaseBreadcrumbItem {
    title: string;
    path: string;
}

export interface IBaseComponentProps {
    title: string,
    keys: string[],
    dispatch?: Dispatch,
    breadcrumb?: IBaseBreadcrumbItem[],
    selectedArea?: ISimpleArea,
    children: React.ReactNode
}

const BaseComponent: FC<IBaseComponentProps> = ({ title, keys, breadcrumb, selectedArea, children }) => {
    const history = useHistory();
    const dispatch = useDispatch();

    useEffect(() => {
        if (typeof selectedArea === 'undefined') {
            const area = window.sessionStorage.getItem('default_select_area');
            if (area) {
                dispatch(selectArea(area));
            }
        }
    }, []);

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <CommonMenu keys={keys} />
            <Layout>
                <CommonPageHeader title={title} />
                <Content style={{ margin: '0 16px' }}>
                    <Breadcrumb style={{ padding: 10, cursor: 'pointer' }}>
                        {breadcrumb && breadcrumb.map(({ title, path }, index) => (
                            <Breadcrumb.Item key={index} onClick={() => history.push(path)}>{title}</Breadcrumb.Item>))}
                    </Breadcrumb>

                    <div style={{ minHeight: 'calc(100vh - 170px)' }}>
                        {children}
                    </div>
                </Content>
            </Layout>
        </Layout>
    );
};

const Base = connect(({ info: { selectedArea } }: { info: IInfoModel }) => ({ selectedArea }))(BaseComponent);

export default Base;
