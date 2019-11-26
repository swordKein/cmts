package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.Items;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
//@Qualifier("TestDao")
public interface TestMapper {
    //@Select("SELECT NOW()")
    String getCurrentDateTime();

    ArrayList<Map> getVocList();
    int uptVocListById(Map<String, Object> reqMap);


    ArrayList<Map> getFaqList();
    int uptFaqListById(Map<String, Object> reqMap);

    int insYjItems(Map<String, Object> reqMap);

    int insYcDatas(Map<String, Object> reqMap);
    List<Map<String, Object>> getYcDatas1st();
    List<Map<String, Object>> getYcDatas2st();

    List<Items> getNoGenreItems();

    List<Map<String, Object>> getItemsForSubgenre();
    List<Map<String, Object>> getItemsForSubgenre2();

    List<Map<String, Object>> getItemsAndSubgenre();

    List<Map<String, Object>> getItemsStatRt();

    List<Map<String, Object>> getItemsForDaumAward();

    List<Map<String, Object>> getItemsForSearchKeywords();

    List<Map<String, Object>> cntItemsMetasForSubgenre(String mtype);

    List<Map<String, Object>> getRtItems0417();

    List<Map<String, Object>> getContentsAssetList();
    List<Map<String, Object>> getSeriesAssetList();

    int insContentsAsset(List<Map<String, Object>> req);
    int insSeriesAsset(List<Map<String, Object>> req);

    List<Map<String, Object>> getCcubeContentsAll();
    List<Map<String, Object>> getCcubeSeriesAll();

    List<Map<String, Object>> getCcubeContentsFT();
    List<Map<String, Object>> getCcubeSeriesFT();

    List<Map<String, Object>> getItemsAndAwardAll();
    List<Map<String, Object>> getItemsAndAwardAll2();

    List<Map<String, Object>> getDicKeywordsByType0(String type);
    List<Map<String, Object>> getItemsTagsMetasByType0(String type);

    List<Map<String, Object>> getContentsOrigItemsAll(Items req);
    Integer cntContentsOrigItemsAll();

    List<Map<String, Object>> getSeriesOrigItemsAll(Items req);
    Integer cntSeriesOrigItemsAll();



    List<Map<String, Object>> getContentsOrigItemsDay(Items req);
    Integer cntContentsOrigItemsDay();

    List<Map<String, Object>> getSeriesOrigItemsDay(Items req);
    Integer cntSeriesOrigItemsDay();



    List<Map<String, Object>> getContentsOrigItemsAll_100();
    List<Map<String, Object>> getSeriesOrigItemsAll_100();


    List<Map<String, Object>> getItemIdxByContentsAssetId(String asset);
    List<Map<String, Object>> getItemIdxBySeriesAssetId(String asset);

    int insDicResultTagKeywords(Map<String,Object> req);

    List<Map<String, Object>> getMetaKeywordsByMtype(Map<String, Object> req);
    List<Map<String, Object>> getMetasByMtype(Map<String, Object> req);


    List<Map<String, Object>> getAllContentsByMcid();

    List<Map<String, Object>> getMetaKeywordsByMtypeByLastTagidx(Map<String, Object> req);

    List<Map<String, Object>> getSearchKeywordAndCount(Map<String, Object> req);


    int insNonMappedItems(Map<String, Object> req);
    List<Map<String, Object>> getNonMappedItems();

    int insTestCidsForAsset(Map<String, Object> req);

    List<Map<String, Object>> getTestCidsForAsset();
    Map<String, Object> getItemidxTaggedByCid(Map<String, Object> reqMap);


    int cntContentsOrigItemsDayByDate(String date1);
    int cntSeriesOrigItemsDayByDate(String date1);
    List<Map<String, Object>> getContentsOrigItemsDayByDate(Items req);
    List<Map<String, Object>> getSeriesOrigItemsDayByDate(Items req);

    int getCountConfirmedItemsByMetas(Map<String, Object> req);

    int insCidsNon(Map<String, Object> req);

    int cntAwardIdx(Map<String, Object> req);
    List<Map<String,Object>> getAllAwards();

	List<Map<String, Object>> getTagsCountByType();

	List<Map<String,Object>> getRemoveManualDest();
}
