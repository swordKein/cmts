package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfPresetOrig;
import com.kthcorp.cmts.model.ConfPsMappingOrig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Repository
public interface ConfPsMappingOrigMapper {

    @Transactional
    public int insConfPsMappingOrig(ConfPsMappingOrig req);
}
