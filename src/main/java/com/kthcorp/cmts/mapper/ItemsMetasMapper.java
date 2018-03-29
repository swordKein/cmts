package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.CcubeSeries;
import com.kthcorp.cmts.model.ItemsMetas;
import com.kthcorp.cmts.model.ItemsMetasMapping;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ItemsMetasMapper {
    ItemsMetas getItemsMetas(ItemsMetas req);
    List<ItemsMetas> getItemsMetasByIdx(ItemsMetas req);

    List<ItemsMetas> getItemsMetasByMtype(ItemsMetas req);

    int insItemsMetas(ItemsMetas req);
}
