package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("CcubeKeys")
public class CcubeKeys extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer cidx;
    private Integer itemidx;
    private String master_content_id;
    private String content_id;
    private String series_id;
    private String kmrb_id;
    private String purity_title;

    public Integer getCidx() {
        return cidx;
    }

    public void setCidx(Integer cidx) {
        this.cidx = cidx;
    }

    public String getKmrb_id() {
        return kmrb_id;
    }

    public void setKmrb_id(String kmrb_id) {
        this.kmrb_id = kmrb_id;
    }

    public Integer getItemidx() {
        return itemidx;
    }

    public void setItemidx(Integer itemidx) {
        this.itemidx = itemidx;
    }

    public String getMaster_content_id() {
        return master_content_id;
    }

    public void setMaster_content_id(String master_content_id) {
        this.master_content_id = master_content_id;
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

    public String getPurity_title() {
        return purity_title;
    }

    public void setPurity_title(String purity_title) {
        this.purity_title = purity_title;
    }
}