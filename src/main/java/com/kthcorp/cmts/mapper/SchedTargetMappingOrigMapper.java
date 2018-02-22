package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTargetMappingHist;
import com.kthcorp.cmts.model.SchedTargetMappingOrig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface SchedTargetMappingOrigMapper {
    List<SchedTargetMappingOrig> getSchedTargetMappingOrigList(SchedTargetMappingOrig req);
}
