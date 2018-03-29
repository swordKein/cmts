package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("DicKeywords")
public class DicKeywords extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer idx;
    private String type;
    private String keyword;
    private Double ratio;
    private Timestamp regdate;
    private String regid;

    private String oldword;
    private String toword;

    private Double freq1;
    private Double freq2;
    private String genre;

    private Integer cnt;

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Double getFreq1() {
        return freq1;
    }

    public void setFreq1(Double freq1) {
        this.freq1 = freq1;
    }

    public Double getFreq2() {
        return freq2;
    }

    public void setFreq2(Double freq2) {
        this.freq2 = freq2;
    }

    public String getToword() {
        return toword;
    }

    public void setToword(String toword) {
        this.toword = toword;
    }

    public String getOldword() {
        return oldword;
    }

    public void setOldword(String oldword) {
        this.oldword = oldword;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
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
}