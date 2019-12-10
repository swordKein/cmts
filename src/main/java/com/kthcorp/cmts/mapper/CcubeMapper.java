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
    int getCcubeItemIdx2(CcubeKeys req);

    int getCcubeKeysIdx(CcubeKeys req);
    int insCcubeKeys(CcubeKeys req);

    CcubeKeys getCcubeKeys(int idx);

    int insCcubeContent(CcubeContent req);
    int insCcubeSeries(CcubeSeries req);

    int insCcubeContentOrig(CcubeContent req);
    int insCcubeSeriesOrig(CcubeSeries req);

    int insCcubeContentOrigManual(CcubeContent req);
    int insCcubeSeriesOrigManual(CcubeSeries req);

    List<CcubeContent> getCcubeContentByYjid();
    List<CcubeContent> getCcubeContentByYjidNot();

    List<Map<String,Object>> getCcubeOutputList50();
    List<Map<String,Object>> getCcubeOutputListStandby(Items req);
    int cntCcubeOutputListStandby(Items req);

    int insCcubeOutput(Map<String,Object> req);
    int uptCcubeOutputStat(Map<String,Object> req);
    int insCcubeOutputHist(Map<String,Object> req);

    List<Map<String,Object>> getCcubeOutputListSeriesAll();
    int cntCcubeOutputListSeriesAll();

    int cntCcubeKeysByCidOrSid(CcubeKeys req);

    String getSummaryFromCcube(Integer itemidx);

    List<Map<String,Object>> getMultipleItemsByMCID();
    List<Map<String,Object>> getSortedItemsByMCID(String master_content_id);
    int chgItemsForDisableDup(Long itemidx);
    int chgItemsStatForDisableDup(Long itemidx);
    List<Map<String,Object>> getSortedMCIDforNew(String master_content_id);

    int chgItemsStatForRT(Long itemIdx);

    List<Integer> getItemidxByMC_ID(String mcid);
    List<Integer> getItemidxBySERIES_ID(String series_id);
	CcubeKeys getCcubeKeys2(CcubeKeys ckParam);		//2019.12.06
}
