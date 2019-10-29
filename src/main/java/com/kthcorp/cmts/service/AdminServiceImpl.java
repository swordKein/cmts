package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.*;

import java.util.List;

public interface AdminServiceImpl {
    List<Items> getItemsList(Items req);

    Integer countItems(Items req);

    Items getItemsByIdx(Items req);

    List<SchedTrigger> getSchedTriggerListByItemIdx(int idx);

    List<SchedTargetMappingHist> getSchedTriggerListBySc_id(SchedTargetMappingHist req);

    List<SchedTargetMappingHist> getSchedTriggerHistListAll(SchedTargetMappingHist req);

    List<ConfTargetOrig> getConfTargetOrigList(ConfTargetOrig req);

    List<ConfTarget> getConfTargetList(ConfTarget req);

    int uptSchedTriggerRetry(SchedTrigger req);

    int uptConfTarget(ConfTarget req);

    int delConfTarget(ConfTarget req);

    int uptConfPreset(ConfPreset req);

    int delConfPreset(ConfPreset req);

    List<MovieCine21> getMovieCine21(MovieCine21 req);

    int cntMovieCine21(MovieCine21 req);

    List<ManualChange> getManualJobHist(ManualChange req);

    int cntManualJobHist();

	String getDicKeywordsListDownload(String type);
}
