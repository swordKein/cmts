package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.CcubeKeys;
import com.kthcorp.cmts.model.CcubeSeries;
import com.kthcorp.cmts.model.ItemsMetas;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CcubeMapper {
    List<CcubeContent> get50ActiveCcubeContents();
    int uptCcubeContentStat(CcubeContent req);
    List<CcubeSeries> get50ActiveCcubeSeries();
    int uptCcubeSeriesStat(CcubeSeries req);

    CcubeContent getCcubeContentByCid(CcubeContent req);
    CcubeSeries getCcubeSeriesById(CcubeSeries req);

    int getCcubeItemIdx(CcubeKeys req);
    int getCcubeKeysIdx(CcubeKeys req);
    int insCcubeKeys(CcubeKeys req);

    CcubeKeys getCcubeKeys(int idx);

    int insCcubeContent(CcubeContent req);

    List<CcubeContent> getCcubeContentByYjid();
    List<CcubeContent> getCcubeContentByYjidNot();
}
