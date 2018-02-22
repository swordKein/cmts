package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("NlpProgs")
public class NlpProgs {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer idx;
    private String filename;
    private Long filelines;
    private String filecharset;
    private Long linecnt;
    private String stat;
    private Timestamp regdate;
    private String outfilename;
    private Double progs;

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFilelines() {
        return filelines;
    }

    public void setFilelines(Long filelines) {
        this.filelines = filelines;
    }

    public String getFilecharset() {
        return filecharset;
    }

    public void setFilecharset(String filecharset) {
        this.filecharset = filecharset;
    }

    public Long getLinecnt() {
        return linecnt;
    }

    public void setLinecnt(Long linecnt) {
        this.linecnt = linecnt;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public Timestamp getRegdate() {
        return regdate;
    }

    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }

    public String getOutfilename() {
        return outfilename;
    }

    public void setOutfilename(String outfilename) {
        this.outfilename = outfilename;
    }

    public Double getProgs() {
        return progs;
    }

    public void setProgs(Double progs) {
        this.progs = progs;
    }
}