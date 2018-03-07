package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.Items;
import com.kthcorp.cmts.model.ItemsContent;
import com.kthcorp.cmts.model.ItemsHist;
import com.kthcorp.cmts.model.ItemsTags;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ItemsHistMapper {
    int insItemsHist(ItemsHist req);
}
