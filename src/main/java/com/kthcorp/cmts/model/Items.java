package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.type.Alias;

@Alias("Items")
public class Items extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer idx;
    private String type;
    private String cid;
    private String title;
    private String title1;
    private String title2;
    private Timestamp regdate;
    private Timestamp regdate1;
    private Timestamp regdate2;
    private String regid;
    private String stat;
    private String director;
    private String year;

    private String duration;
    private int tagcnt;

    private String searchType;
    private String searchStat;
    private String searchSdate;
    private String searchEdate;
    private String searchKeyword;
    private String searchParts;
    private List<String> searchPartsArr;
    private String searchTitleYn;
    private String searchDirectorYn;

    /* for ccube */
    private String content_id;
    private String series_id;

    private Integer cnt;
    private Integer sc_id;
    private String itemsIdxs;

    private Timestamp procdate;

    public Timestamp getProcdate() {
        return procdate;
    }

    public void setProcdate(Timestamp procdate) {
        this.procdate = procdate;
    }

    public String getSearchDirectorYn() {
        return searchDirectorYn;
    }

    public void setSearchDirectorYn(String searchDirectorYn) {
        this.searchDirectorYn = searchDirectorYn;
    }

    public String getSearchTitleYn() {
        return searchTitleYn;
    }

    public void setSearchTitleYn(String searchTitleYn) {
        this.searchTitleYn = searchTitleYn;
    }

    public String getItemsIdxs() {
        return itemsIdxs;
    }

    public void setItemsIdxs(String itemsIdxs) {
        this.itemsIdxs = itemsIdxs;
    }

    public Integer getSc_id() {
        return sc_id;
    }

    public void setSc_id(Integer sc_id) {
        this.sc_id = sc_id;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public List<String> getSearchPartsArr() {
        return searchPartsArr;
    }

    public void setSearchPartsArr(List<String> searchPartsArr) {
        this.searchPartsArr = searchPartsArr;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getSeries_id() {
        return series_id;
    }

    public void setSeries_id(String series_id) {
        this.series_id = series_id;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchStat() {
        return searchStat;
    }

    public void setSearchStat(String searchStat) {
        this.searchStat = searchStat;
    }

    public String getSearchSdate() {
        return searchSdate;
    }

    public void setSearchSdate(String searchSdate) {
        this.searchSdate = searchSdate;
    }

    public String getSearchEdate() {
        return searchEdate;
    }

    public void setSearchEdate(String searchEdate) {
        this.searchEdate = searchEdate;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSearchParts() {
        return searchParts;
    }

    public void setSearchParts(String searchParts) {
        this.searchParts = searchParts;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getTagcnt() {
        return tagcnt;
    }

    public void setTagcnt(int tagcnt) {
        this.tagcnt = tagcnt;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<SchedTrigger> getSchedList() {
        return schedList;
    }

    public void setSchedList(List<SchedTrigger> schedList) {
        this.schedList = schedList;
    }

    private List<ItemsMetas> metaList;
    private List<SchedTrigger> schedList;


    public List<ItemsMetas> getMetaList() {
        return metaList;
    }

    public void setMetaList(List<ItemsMetas> metaList) {
        this.metaList = metaList;
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public Timestamp getRegdate() {
        return regdate;
    }

    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }

    public Timestamp getRegdate1() {
        return regdate1;
    }

    public void setRegdate1(Timestamp regdate1) {
        this.regdate1 = regdate1;
    }

    public Timestamp getRegdate2() {
        return regdate2;
    }

    public void setRegdate2(Timestamp regdate2) {
        this.regdate2 = regdate2;
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
}