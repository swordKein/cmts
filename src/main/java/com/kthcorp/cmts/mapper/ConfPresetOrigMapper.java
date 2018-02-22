package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfPresetOrig;
import com.kthcorp.cmts.model.ConfTarget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface ConfPresetOrigMapper {

    @Transactional
    public int insertConfPresetOrig(ConfPresetOrig req);
}
