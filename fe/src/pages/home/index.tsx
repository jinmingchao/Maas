import React, { FC, useRef, useState } from 'react';
import Base from '../_base';
import {useHistory} from 'umi';


const page: FC<any > = ({}) => {
    const history = useHistory();
    history.push('/instance/list?managed=1');

    return (
        <Base title={'首页'} keys={['home']}>
            <div style={{scrollbarWidth: 'none'}}>

            </div>
        </Base>
    );
};

const Home = page;
export default Home;
