import React, {FC} from 'react';
import {connect} from 'umi';
import Base from '../_base';
import {IRoleModel} from "@/models/permission";
import RoleAdmin from "@/pages/user/_role_admin";
import CreatePolicy from "@/pages/user/_create-policy";

const page: FC<{ users: string[], rolesWithPer: IRoleModel[], roleForUser: string[] }> = ({users, rolesWithPer, roleForUser}) => {
    return (
        <Base title={'权限管理'} keys={['user', 'permission-admin']}>
            <div style={{scrollbarWidth: 'none'}}>
                <CreatePolicy />
                <RoleAdmin/>
            </div>
        </Base>
    );
};

const PermissionAdmin = connect(() => ({}))(page);
export default PermissionAdmin;
