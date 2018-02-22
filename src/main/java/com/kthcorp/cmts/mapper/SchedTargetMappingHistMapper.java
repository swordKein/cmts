package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTargetMappingHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface SchedTargetMappingHistMapper {

    List<SchedTargetMappingHist> getSchedTargetMappingHistList(SchedTargetMappingHist req);

    // 최종 수집(현재) 이력 조회 by sc_id
    List<SchedTargetMappingHist> getSchedTargetMappingHistListForLast(SchedTargetMappingHist req);
    // 최종 정제,분석 (현재) 이력 조회 by sc_id
    List<SchedTargetMappingHist> getSchedTargetMappingHistListForLast2(SchedTargetMappingHist req);

    // 수집,정제,분석 이력 모두 조회 by sc_id
    List<SchedTargetMappingHist> getSchedTargetMappingHistListAll(SchedTargetMappingHist req);

    @Transactional
    int insSchedTargetMappingHist(SchedTargetMappingHist req);

}
