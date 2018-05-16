package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;

import java.util.List;

public interface SchedTriggerServiceImpl {
    public int uptSchedTriggerComplete(SchedTrigger req);

    public int uptSchedTriggerOnlyStat(SchedTrigger req);

    int uptStoppedProcessingJobsStat();

    int processCollectHearbit() throws Exception;

    boolean checkActiveServerByServerid() throws Exception;

    int deleteSchedTargetContentOrigin(Integer itemidx);
}
