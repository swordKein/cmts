package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;
import java.util.List;

@Alias("ConfTarget")
public class ConfTarget extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer tg_id;
    private String title;
    private String descript;
    private String tg_url;
    private String tg_url_param1;
    private String tg_url_param2;
    private String tg_url_param3;
    private Timestamp regdate;
    private String regid;
    private String stat;
    private String param1;
    private String param2;
    private Integer param3;
    private Double param4;
    private Timestamp param5;
    private List<ConfPreset> presetList;

    private String orderType;
    private String stattarget;

    private String is_fail;
    private String is_limit;
    private String is_manual;
    private Integer content_min1;
    private Integer content_max1;
    private Integer content_min2;
    private Integer content_max2;
    private Integer fail_count1;
    private Integer fail_count2;

    private String movietitle;
    private String moviedirector;
    private String movieyear;

    private String ccubetype;

    public String getCcubetype() {
        return ccubetype;
    }

    public void setCcubetype(String ccubetype) {
        this.ccubetype = ccubetype;
    }

    public String getMovieyear() {
        return movieyear;
    }

    public void setMovieyear(String movieyear) {
        this.movieyear = movieyear;
    }

    public String getMovietitle() {
        return movietitle;
    }

    public void setMovietitle(String movietitle) {
        this.movietitle = movietitle;
    }

    public String getMoviedirector() {
        return moviedirector;
    }

    public void setMoviedirector(String moviedirector) {
        this.moviedirector = moviedirector;
    }

    public Integer getTg_id() {
        return tg_id;
    }

    public void setTg_id(Integer tg_id) {
        this.tg_id = tg_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getTg_url() {
        return tg_url;
    }

    public void setTg_url(String tg_url) {
        this.tg_url = tg_url;
    }

    public String getTg_url_param1() {
        return tg_url_param1;
    }

    public void setTg_url_param1(String tg_url_param1) {
        this.tg_url_param1 = tg_url_param1;
    }

    public String getTg_url_param2() {
        return tg_url_param2;
    }

    public void setTg_url_param2(String tg_url_param2) {
        this.tg_url_param2 = tg_url_param2;
    }

    public String getTg_url_param3() {
        return tg_url_param3;
    }

    public void setTg_url_param3(String tg_url_param3) {
        this.tg_url_param3 = tg_url_param3;
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

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
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

    public List<ConfPreset> getPresetList() {
        return presetList;
    }

    public void setPresetList(List<ConfPreset> presetList) {
        this.presetList = presetList;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStattarget() {
        return stattarget;
    }

    public void setStattarget(String stattarget) {
        this.stattarget = stattarget;
    }

    public String getIs_fail() {
        return is_fail;
    }

    public void setIs_fail(String is_fail) {
        this.is_fail = is_fail;
    }

    public String getIs_limit() {
        return is_limit;
    }

    public void setIs_limit(String is_limit) {
        this.is_limit = is_limit;
    }

    public String getIs_manual() {
        return is_manual;
    }

    public void setIs_manual(String is_manual) {
        this.is_manual = is_manual;
    }

    public Integer getContent_min1() {
        return content_min1;
    }

    public void setContent_min1(Integer content_min1) {
        this.content_min1 = content_min1;
    }

    public Integer getContent_max1() {
        return content_max1;
    }

    public void setContent_max1(Integer content_max1) {
        this.content_max1 = content_max1;
    }

    public Integer getContent_min2() {
        return content_min2;
    }

    public void setContent_min2(Integer content_min2) {
        this.content_min2 = content_min2;
    }

    public Integer getContent_max2() {
        return content_max2;
    }

    public void setContent_max2(Integer content_max2) {
        this.content_max2 = content_max2;
    }

    public Integer getFail_count1() {
        return fail_count1;
    }

    public void setFail_count1(Integer fail_count1) {
        this.fail_count1 = fail_count1;
    }

    public Integer getFail_count2() {
        return fail_count2;
    }

    public void setFail_count2(Integer fail_count2) {
        this.fail_count2 = fail_count2;
    }
}