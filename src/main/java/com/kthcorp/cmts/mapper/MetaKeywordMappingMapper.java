package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.MetaKeywordMapping;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MetaKeywordMappingMapper {
    List<MetaKeywordMapping> getMetaKeywordMappingList(MetaKeywordMapping req);
    List<MetaKeywordMapping> getMetaKeywordMappingListAll();
    List<String> getMetaTypes();

    int insMetaKeywordMapping(MetaKeywordMapping req);
    int uptMetaKeywordMapping(MetaKeywordMapping req);
    int delMetaKeywordMapping(MetaKeywordMapping del);
}
