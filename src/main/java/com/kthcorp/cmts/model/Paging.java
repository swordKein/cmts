package com.kthcorp.cmts.model;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("Paging")
public class Paging implements Serializable {
    private int pageNo;
    private int pageSize;
    private int startIdx;
    private int offsetIdx;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartIdx() {
        return (pageNo-1) * pageSize;
    }

    public int getOffsetIdx() {
        return (pageNo-1) * pageSize;
    }

    /*public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }
    */
}
