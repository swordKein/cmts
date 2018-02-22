package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("ConfTargetContent")
public class ConfTargetContent extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer tc_id;
    private Integer tg_id;
    private Integer tgh_id;
    private Timestamp regdate;
    private String content;
    private String param1;
    private String param2;
    private Integer param3;
    private Double param4;
    private Timestamp param5;

    public Integer getTgh_id() {
        return tgh_id;
    }

    public void setTgh_id(Integer tgh_id) {
        this.tgh_id = tgh_id;
    }

    public Integer getTc_id() {
        return tc_id;
    }

    public void setTc_id(Integer tc_id) {
        this.tc_id = tc_id;
    }

    public Integer getTg_id() {
        return tg_id;
    }

    public void setTg_id(Integer tg_id) {
        this.tg_id = tg_id;
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