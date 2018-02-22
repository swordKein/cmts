package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.sql.Timestamp;
import java.util.List;

public class SchedTrigger extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
    private Integer sc_id;
    private String type;
    private Integer tcnt;
    private String descript;
    private Timestamp regdate;
    private String regid;
    private String stat;
    private Integer progs;
    private String param1;
    private String param2;
    private Integer param3;
    private Double param4;
    private Timestamp param5;
    private List<ConfTarget> targetList;
    private SchedTargetContent contentOne;
    private List<SchedTargetContent> contentList;
    private Integer parent_sc_id;

    private Integer itemIdx;

    public Integer getItemIdx() {
        return itemIdx;
    }

    public void setItemIdx(Integer itemIdx) {
        this.itemIdx = itemIdx;
    }

    public SchedTargetContent getContentOne() {
        return contentOne;
    }

    public void setContentOne(SchedTargetContent contentOne) {
        this.contentOne = contentOne;
    }

    public Integer getParent_sc_id() {
        return parent_sc_id;
    }

    public void setParent_sc_id(Integer parent_sc_id) {
        this.parent_sc_id = parent_sc_id;
    }

    public Integer getTcnt() {
        return tcnt;
    }

    public void setTcnt(Integer tcnt) {
        this.tcnt = tcnt;
    }

    public List<SchedTargetContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<SchedTargetContent> contentList) {
        this.contentList = contentList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSc_id() {
        return sc_id;
    }

    public void setSc_id(Integer sc_id) {
        this.sc_id = sc_id;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public Timestamp getRegdate() {
        return regdate;
    }

    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public Integer getProgs() {
        return progs;
    }

    public void setProgs(Integer progs) {
        this.progs = progs;
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

    public List<ConfTarget> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<ConfTarget> targetList) {
        this.targetList = targetList;
    }
}