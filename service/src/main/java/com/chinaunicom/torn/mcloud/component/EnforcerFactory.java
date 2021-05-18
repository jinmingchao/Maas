package com.chinaunicom.torn.mcloud.component;

import com.chinaunicom.torn.mcloud.entity.CasbinEnforcerEntity;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnforcerFactory implements InitializingBean {

    private static Enforcer enforcer;

    @Autowired
    private CasbinEnforcerEntity casbinConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        //从数据库读取策略
        JDBCAdapter jdbcAdapter = new JDBCAdapter(this.casbinConfig.getDriverClassName(),
                this.casbinConfig.getUrl(),
                this.casbinConfig.getUsername(),
                this.casbinConfig.getPassword());
        enforcer = new Enforcer(this.casbinConfig.getModelPath(), jdbcAdapter);
        enforcer.enableAutoSave(true);
        enforcer.loadPolicy(); //Load the policy from DB.
        enforcer.enableLog(false);
    }

    public static Enforcer getEnforcer() {
        return enforcer;
    }
}
