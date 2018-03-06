package com.kthcorp.cmts.service;

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

    JsonObject getItemsMetasByItemIdx(int itemIdx);

    List<String> getNaverKindWordsByList(List<String> keywordList, int limit) throws Exception;

    List<String> getNaverKindWords(String keyword, List<String> origArr) throws Exception;

    JsonObject getItemsMetasByItemIdxForInsert(int itemIdx);

    JsonObject getItemsMetasByItemIdxForUpdate(int itemIdx);

    JsonObject getItemsMetasDupByItemIdx(JsonObject resultObj, int itemIdx);

    int restorePrevTag(int itemIdx);

    JsonObject getArraysByTypeFromInputItems(String items);

    int changeMetasArraysByTypeFromInputItems(int itemid, String items, String duration);

    List<ItemsTags> getYjTagsMetasByItemidx(ItemsTags req);

    ItemsTags getLastTagCntInfo(Integer itemid);

    List<ItemsTags> getTagCntInfo(Integer itemid);
}
