package com.chinaunicom.torn.mcloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_user_oplog")
public class LogEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "op_what")
    private String what;
    @Column(name = "op_where")
    private String where;
    @Column(name = "op_when")
    private String when;
    @Column(name = "op_who")
    private String who;
    @Column(name = "op_why")
    private String why;
    @Column(name = "op_how")
    private String how;

    public Integer getId() {
        return id;
    }

    public String getHow() {
        return how;
    }

    public String getWho() {
        return who;
    }

    public String getWhy() {
        return why;
    }

    public String getWhat() {
        return what;
    }

    public String getWhen() {
        return when;
    }

    public String getWhere() {
        return where;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setHow(String how) {
        this.how = how;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setWhy(String why) {
        this.why = why;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
