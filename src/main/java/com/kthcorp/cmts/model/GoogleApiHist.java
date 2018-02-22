package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("GoogleApiHist")
public class GoogleApiHist extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer hidx;
    private String type;
    private String src_lang;
    private String src_txt;
    private Integer src_size;
    private String target_lang;
    private String target_txt;
    private String regmonth;

    private Timestamp regdate;
    private String regid;
    private String action;
    private Integer action_id;

    public Integer getHidx() {
        return hidx;
    }

    public void setHidx(Integer hidx) {
        this.hidx = hidx;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc_lang() {
        return src_lang;
    }

    public void setSrc_lang(String src_lang) {
        this.src_lang = src_lang;
    }

    public String getSrc_txt() {
        return src_txt;
    }

    public void setSrc_txt(String src_txt) {
        this.src_txt = src_txt;
    }

    public Integer getSrc_size() {
        return src_size;
    }

    public void setSrc_size(Integer src_size) {
        this.src_size = src_size;
    }

    public String getTarget_lang() {
        return target_lang;
    }

    public void setTarget_lang(String target_lang) {
        this.target_lang = target_lang;
    }

    public String getTarget_txt() {
        return target_txt;
    }

    public void setTarget_txt(String target_txt) {
        this.target_txt = target_txt;
    }

    public String getRegmonth() {
        return regmonth;
    }

    public void setRegmonth(String regmonth) {
        this.regmonth = regmonth;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getAction_id() {
        return action_id;
    }

    public void setAction_id(Integer action_id) {
        this.action_id = action_id;
    }
}