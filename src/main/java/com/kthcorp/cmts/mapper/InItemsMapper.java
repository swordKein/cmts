package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.InItems;
import com.kthcorp.cmts.model.Items;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InItemsMapper {
    List<InItems> get50ActiveInItems();

    int uptInItemsStat(InItems req);
}
