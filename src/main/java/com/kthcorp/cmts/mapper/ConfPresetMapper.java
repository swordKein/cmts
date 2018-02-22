package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfPreset;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Repository
public interface ConfPresetMapper {

    @Transactional
    int insertConfPreset(ConfPreset req);
    @Transactional
    int uptConfPreset(ConfPreset req);
    @Transactional
    int delConfPreset(ConfPreset req);
}
