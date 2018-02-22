package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfPsMapping;
import com.kthcorp.cmts.model.ConfPsMappingOrig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Repository
public interface ConfPsMappingMapper {

    @Transactional
    int insConfPsMapping(ConfPsMapping req);

    @Transactional
    int delConfPsMapping(ConfPsMapping req);

    @Transactional
    int delConfPsMappingByPsId(ConfPsMapping req);

}
