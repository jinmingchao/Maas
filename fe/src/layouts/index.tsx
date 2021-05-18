import React from 'react';

const BasicLayout: React.FC = props => {


    return (
        <div className={styles.normal}>
        <h1 className={styles.title}>Yay! Welcome to umi!</h1>
        {props.children}
        </div>
    );
};

export default BasicLayout;
