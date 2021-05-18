package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.*;

@Entity
@Table(name = "tb_ip_pool_area")
public class IPPoolAreaEntity {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "host")
    private String cidr;
    @Column(name = "net_asst")
    private Integer netAsst;
    @Column(name = "netmask")
    private String netmask;
    @Column(name = "gateway_ip")
    private String gatewayIp;
    @Column(name = "vlan_id")
    private Integer vlanId;
    @Column(name = "enabled")
    private boolean enabled;

    public Integer getVlanId() {
        return vlanId;
    }

    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public Integer getNetAsst() {
        return netAsst;
    }

    public Integer getId() {
        return id;
    }

    public String getCidr() {
        return cidr;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setNetAsst(Integer netAsst) {
        this.netAsst = netAsst;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "pool{id: "+id+",name："+name+",cidr："+cidr+",netAsst："+netAsst+",enabled："+enabled+"}";
    }

}
