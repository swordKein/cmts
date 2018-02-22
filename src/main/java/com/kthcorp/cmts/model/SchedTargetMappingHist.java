package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;
import java.util.List;

@Alias("SchedTargetMappingHist")
public class SchedTargetMappingHist extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer stmh_id;
    private Integer sc_id;
    private Integer tg_id;
    private String type;
    private Integer tcnt;
    private Timestamp regdate;
    private String stat;
    private String rt_code;
    private String rt_msg;
    private String param1;
    private String param2;
    private Integer param3;
    private Double param4;
    private Timestamp param5;
    private String content;
    private String summary;

    private String title;
    private ConfTarget confTarget;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ConfTarget getConfTarget() {
        return confTarget;
    }

    public void setConfTarget(ConfTarget confTarget) {
        this.confTarget = confTarget;
    }

    public Integer getTcnt() {
        return tcnt;
    }

    public void setTcnt(Integer tcnt) {
        this.tcnt = tcnt;
    }

    private List<SchedTargetContent> contentList;

    public List<SchedTargetContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<SchedTargetContent> contentList) {
        this.contentList = contentList;
    }

    public Integer getStmh_id() {
        return stmh_id;
    }

    public void setStmh_id(Integer stmh_id) {
        this.stmh_id = stmh_id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getRegdate() {
        return regdate;
    }

    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getRt_code() {
        return rt_code;
    }

    public void setRt_code(String rt_code) {
        this.rt_code = rt_code;
    }

    public String getRt_msg() {
        return rt_msg;
    }

    public void setRt_msg(String rt_msg) {
        this.rt_msg = rt_msg;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}