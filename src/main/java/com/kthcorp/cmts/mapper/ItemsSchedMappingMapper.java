package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.Items;
import com.kthcorp.cmts.model.ItemsSchedMapping;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ItemsSchedMappingMapper {
    int insItemsSchedMapping(ItemsSchedMapping req);

    int delItemsSchedMapping(ItemsSchedMapping req);

    int getItemIdxByScid(ItemsSchedMapping req);
}
