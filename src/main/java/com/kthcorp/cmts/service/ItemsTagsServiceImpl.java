package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//@Service
public interface ItemsTagsServiceImpl {

    List<ItemsTags> getItemsTagsMetasByItemIdx(ItemsTags req);

    ItemsTags getItemsTagsMetasByItemIdxAndMtype(ItemsTags req);

    int getCurrTagsIdxOld(int itemIdx);

    int getCurrTagsIdxReady(int itemIdx);

    int getCurrTagsIdxForInsert(int itemIdx);

    int getCurrTagsIdxForSuccess(int itemIdx);

    int getMaxTagsIdxByItemIdx(ItemsTags req);

    int cntConfirmedTags(ItemsTags req);

    int insItemsTagsKeys(ItemsTags req);

    int uptItemsTagsKeysStat(ItemsTags req);

    int insItemsTagsMetas(ItemsTags req);

    JsonObject getItemsTagsMetasAll_bak(int itemIdx);

    //JsonObject getItemsMetasByItemIdx(int itemIdx);

    JsonObject getItemsMetasByIdx(int itemIdx, List<String> origTypes, String getStat);

    JsonObject getItemsMetasByItemIdx(int itemIdx, boolean isColorCode) throws Exception;

    JsonObject getAwardObject(int itemIdx, JsonObject resultObj2) throws Exception;

    JsonObject getWordsAssoc(int itemIdx, JsonObject resultObj2);

    JsonObject getWordsGenre(int itemIdx, JsonObject resultObj2);

    JsonObject getSubgenres(int itemIdx, JsonObject resultObj2);

    JsonObject getSubgenresString(int itemIdx, JsonObject resultObj2);

    JsonObject getSubgenresStringForJson(int itemIdx, JsonObject resultObj2);

    List<String> getGenreWordsListByGenre(String genre);

    List<String> getNaverKindWordsByList(List<String> keywordList, int limit) throws Exception;

    List<String> getNaverKindWords(String keyword, List<String> origArr) throws Exception;

    JsonObject getItemsMetasByItemIdxForInsert(int itemIdx);

    //JsonObject getItemsMetasByItemIdxForUpdate(int itemIdx);

    //JsonObject getItemsMetasDupByItemIdx(JsonObject resultObj, int itemIdx);

    JsonObject getItemsMetasByItemIdxForUpdate(int itemIdx, List<String> origTypes);

    JsonObject getItemsMetasDupByItemIdx(JsonObject resultObj, int itemIdx, boolean isColorCode);

    int restorePrevTag(int itemIdx);

    JsonObject getArraysByTypeFromInputItems(String items);

    int changeMetasArraysByTypeFromInputItems(int itemid, String items, String duration, String sendnow);

    List<ItemsTags> getYjTagsMetasByItemidx(ItemsTags req);

    ItemsTags getLastTagCntInfo(Integer itemid);

    ItemsTags getLastTagSuccessInfo(Integer itemid);

    List<ItemsTags> getTagCntInfo(Integer itemid);

    List<ItemsTags> getSuccessTagidxListDesc(Integer itemid);

    void processManualTagsMetasChange(String target_mtype, String from_keyword, String to_keyword, String action);

    ManualChange getManualJobHistLastOne();

    int insManualJobHist(ManualChange req);

    int uptManualJobHist(ManualChange req);

    int delItemsMetasAward(int itemIdx);

    JsonArray getMetaSubgenre(Integer itemid, String reqJsonObjStr) throws Exception;

    JsonArray getMixedSubgenre2(Integer itemid) throws Exception;
}
