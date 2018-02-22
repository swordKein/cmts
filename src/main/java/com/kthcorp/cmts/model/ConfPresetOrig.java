package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("ConfPresetOrig")
public class ConfPresetOrig {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer ps_id;

    private Integer priority;
    private String ps_tag;
    private String dest_field;
    private Timestamp regdate;
    private String regid;
    private String ps_add_url;
    private String ps_type;
    private String descriptp;
    private String dest_charset;

    public String getDest_charset() {
        return dest_charset;
    }

    public void setDest_charset(String dest_charset) {
        this.dest_charset = dest_charset;
    }

    public String getDescriptp() {
        return descriptp;
    }

    public void setDescriptp(String descriptp) {
        this.descriptp = descriptp;
    }

    public String getPs_type() {
        return ps_type;
    }

    public void setPs_type(String ps_type) {
        this.ps_type = ps_type;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPs_id() {
        return ps_id;
    }

    public void setPs_id(Integer ps_id) {
        this.ps_id = ps_id;
    }

    public String getPs_tag() {
        return ps_tag;
    }

    public void setPs_tag(String ps_tag) {
        this.ps_tag = ps_tag;
    }

    public String getDest_field() {
        return dest_field;
    }

    public void setDest_field(String dest_field) {
        this.dest_field = dest_field;
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

    public String getPs_add_url() {
        return ps_add_url;
    }

    public void setPs_add_url(String ps_add_url) {
        this.ps_add_url = ps_add_url;
    }
}