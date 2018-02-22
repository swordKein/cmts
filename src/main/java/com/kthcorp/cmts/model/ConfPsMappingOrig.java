package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

@Alias("ConfPsMappingOrig")
public class ConfPsMappingOrig {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private Integer tg_id;
    private Integer ps_id;

    public Integer getTg_id() {
        return tg_id;
    }

    public void setTg_id(Integer tg_id) {
        this.tg_id = tg_id;
    }

    public Integer getPs_id() {
        return ps_id;
    }

    public void setPs_id(Integer ps_id) {
        this.ps_id = ps_id;
    }

}
