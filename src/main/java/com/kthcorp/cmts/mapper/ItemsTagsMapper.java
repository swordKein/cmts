package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ItemsMetas;
import com.kthcorp.cmts.model.ItemsTags;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ItemsTagsMapper {
    List<ItemsTags> getItemsTagsMetasByItemIdx(ItemsTags req);
    ItemsTags getItemsTagsMetasByItemIdxAndMtype(ItemsTags req);
    int getMaxTagsIdxByItemIdx(ItemsTags req);
    int cntConfirmedTags(ItemsTags req);

    int insItemsTagsKeys(ItemsTags req);
    int uptItemsTagsKeysStat(ItemsTags req);

    int insItemsTagsMetas(ItemsTags req);
    List<ItemsTags> getYjTagsMetasByItemidx(ItemsTags req);
    ItemsTags getLastTagCntInfo(Integer req);
    List<ItemsTags> getTagCntInfo(Integer req);

    int cntSearchTagsMetasByMtypeAndKeyword(ItemsTags req);
    List<ItemsTags> getSearchTagsMetasByMtypeAndKeywordPaging(ItemsTags req);
    int uptItemsTagsByManual(ItemsTags req);
}
