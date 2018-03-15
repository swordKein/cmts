package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("SchedTargetContent")
public class SchedTargetContent extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer stc_id;
    private Integer sc_id;
    private Integer tg_id;
    private Integer stmh_id;
    private Timestamp regdate;
    private String content;
    private String param1;
    private String param2;
    private Integer param3;
    private Double param4;
    private Timestamp param5;

    private Integer tcnt;
    private String tg_url;

    public String getTg_url() {
        return tg_url;
    }

    public void setTg_url(String tg_url) {
        this.tg_url = tg_url;
    }

    public Integer getTcnt() {
        return tcnt;
    }

    public void setTcnt(Integer tcnt) {
        this.tcnt = tcnt;
    }

    public Integer getStc_id() {
        return stc_id;
    }

    public void setStc_id(Integer stc_id) {
        this.stc_id = stc_id;
    }

    public Integer getSc_id() {
        return sc_id;
    }

    public void setSc_id(Integer sc_id) {
        this.sc_id = sc_id;
    }

    public Integer getTg_id() {
        return tg_id;
    }

    public void setTg_id(Integer tg_id) {
        this.tg_id = tg_id;
    }

    public Integer getStmh_id() {
        return stmh_id;
    }

    public void setStmh_id(Integer stmh_id) {
        this.stmh_id = stmh_id;
    }

    public Timestamp getRegdate() {
        return regdate;
    }

    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public Integer getParam3() {
        return param3;
    }

    public void setParam3(Integer param3) {
        this.param3 = param3;
    }

    public Double getParam4() {
        return param4;
    }

    public void setParam4(Double param4) {
        this.param4 = param4;
    }

    public Timestamp getParam5() {
        return param5;
    }

    public void setParam5(Timestamp param5) {
        this.param5 = param5;
    }
}