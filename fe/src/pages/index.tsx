import React from 'react';
import styles from './index.less';
import Axios from 'axios';

Axios.defaults.withCredentials = true;

export default () => {
  return (
    <div>
      <h1 className={styles.title}>Page index</h1>
    </div>
  );
}
