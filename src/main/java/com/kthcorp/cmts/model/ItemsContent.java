package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;
import java.util.List;

@Alias("ItemsContent")
public class ItemsContent extends Paging {
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
    private String content;
    private String keyword;
    private int rate;
    private String cine21key;
    private int cine21keySize;
    private String matchKeywords;
    private String notMatchKeywords;
    private boolean matchYn;
    private List<String> cine21Keywords;
    private List<String> metaKeywords;
    private List<String> allKeywords;
    private String contentAll;
    private int sc_id;

    public int getSc_id() {
        return sc_id;
    }

    public void setSc_id(int sc_id) {
        this.sc_id = sc_id;
    }

    public String getContentAll() {
        return contentAll;
    }

    public void setContentAll(String contentAll) {
        this.contentAll = contentAll;
    }

    public List<String> getAllKeywords() {
        return allKeywords;
    }

    public void setAllKeywords(List<String> allKeywords) {
        this.allKeywords = allKeywords;
    }

    public List<String> getCine21Keywords() {
        return cine21Keywords;
    }

    public void setCine21Keywords(List<String> cine21Keywords) {
        this.cine21Keywords = cine21Keywords;
    }

    public List<String> getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(List<String> metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public boolean isMatchYn() {
        return matchYn;
    }

    public void setMatchYn(boolean matchYn) {
        this.matchYn = matchYn;
    }

    public String getCine21key() {
        return cine21key;
    }

    public void setCine21key(String cine21key) {
        this.cine21key = cine21key;
    }

    public int getCine21keySize() {
        return cine21keySize;
    }

    public void setCine21keySize(int cine21keySize) {
        this.cine21keySize = cine21keySize;
    }

    public String getMatchKeywords() {
        return matchKeywords;
    }

    public void setMatchKeywords(String matchKeywords) {
        this.matchKeywords = matchKeywords;
    }

    public String getNotMatchKeywords() {
        return notMatchKeywords;
    }

    public void setNotMatchKeywords(String notMatchKeywords) {
        this.notMatchKeywords = notMatchKeywords;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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