package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("ManualChange")
public class ManualChange extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private int hidx;
    private String target_mtype;
    private String from_keyword;
    private String to_keyword;
    private String action;
    private Timestamp regdate;
    private String stat;
    private int cnt;
    private Timestamp enddate;

    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Timestamp getEnddate() {
        return enddate;
    }

    public void setEnddate(Timestamp enddate) {
        this.enddate = enddate;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getHidx() {
        return hidx;
    }

    public void setHidx(int hidx) {
        this.hidx = hidx;
    }

    public String getTarget_mtype() {
        return target_mtype;
    }

    public void setTarget_mtype(String target_mtype) {
        this.target_mtype = target_mtype;
    }

    public String getFrom_keyword() {
        return from_keyword;
    }

    public void setFrom_keyword(String from_keyword) {
        this.from_keyword = from_keyword;
    }

    public String getTo_keyword() {
        return to_keyword;
    }

    public void setTo_keyword(String to_keyword) {
        this.to_keyword = to_keyword;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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
}