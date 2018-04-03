package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
    int insCcubeSeries(CcubeSeries req);

    List<CcubeContent> getCcubeContentByYjid();
    List<CcubeContent> getCcubeContentByYjidNot();

    List<Map<String,Object>> getCcubeOutputList50();
    List<Map<String,Object>> getCcubeOutputListStandby(Items req);
    int cntCcubeOutputListStandby(Items req);

    int insCcubeOutput(Map<String,Object> req);
    int uptCcubeOutputStat(Map<String,Object> req);
    int insCcubeOutputHist(Map<String,Object> req);
}
