package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTargetMapping;
import com.kthcorp.cmts.model.SchedTrigger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SchedTriggerMapper {

    List<SchedTrigger> getSchedTriggerList();

    List<SchedTrigger> get50ByTypeStatSchedTriggerList(SchedTrigger req);
    List<SchedTrigger> getSchedTriggerListById(int sc_id);
    SchedTrigger getSchedTriggerById(int sc_id);
    //List<SchedTrigger> getTargetListByStat(ConfTarget req);
    //List<SchedTrigger> getTargetListActiveFirst10();
    SchedTrigger getSchedTriggerOne(SchedTrigger req);

    @Transactional
    int uptSchedTriggerProgs(SchedTrigger req);

    @Transactional
    int uptSchedTriggerOldItemsByTypeStat(SchedTrigger req);
    @Transactional
    int uptOldSchedTriggerRetry(SchedTrigger req);

    @Transactional
    int insSchedTriggerForStart(SchedTrigger req);

    @Transactional
    int insSchedTargetMapping(SchedTargetMapping req);
    @Transactional
    int delSchedTargetMapping(SchedTargetMapping req);

    List<SchedTrigger> getSchedTriggerListByItemIdx(int idx);

    @Transactional
    int uptSchedTriggerRetry(SchedTrigger req);


    List<SchedTargetMapping> getParentSchedTargetMapping(Integer sc_id);

    SchedTrigger getSchedTriggerOneByScid(SchedTrigger req);

    List<ConfTarget> getConfTargetListByScid(SchedTrigger req);

    int uptStoppedProcessingJobsStat();

    Map<String,Object> getCollectorActive();
    int insCollectorActive(Map<String, Object> req);
    int uptCollectorActive(Map<String, Object> req);
}
