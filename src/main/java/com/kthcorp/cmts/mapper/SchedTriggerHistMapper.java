package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTriggerHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface SchedTriggerHistMapper {

    public List<SchedTriggerHist> getSchedTriggerHistList(SchedTriggerHist req);

    @Transactional
    public int insSchedTriggerHist(SchedTriggerHist req);

}
