package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("DicNotuseWords")
public class DicNotuseWords extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer idx;
    private String word;
    private String oldword;
    private Double freq;
    private Timestamp regdate;
    private String regid;
    private String orderby;	//권재일 추가 07.31 5-1
    private String fileName;	//권재일 추가 11.06

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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getFreq() {
        return freq;
    }

    public void setFreq(Double freq) {
        this.freq = freq;
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
    
    public String getOrderby() {
		return orderby;
	}
    
    public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
    
    public String getFileName() {
		return fileName;
	}
    
    public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}