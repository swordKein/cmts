package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;

import java.util.List;

public interface RefineServiceImpl {

    /* 올레TV 메타 확장 - 정제 수행 STEP04~06
         */
    int ollehTvMetaRefineScheduleCheck() throws Exception;

    /* STEP04
             * SchedTrigger & targetList<ConfTarget> 통해 정제 스케쥴 50개 조회
            */
    List<SchedTrigger> step04();

    List<SchedTrigger> step04byScid(SchedTrigger req);

    /* STEP05
         * ConfTarget & List<ConfPreset> 을 통해 정제 대상 설정 조회
         * 조회 후 stat = P 로 변경
        */
    ConfTarget step05(ConfTarget req) throws Exception;

    /* STEP06 정제 작업 - sub
        */
    JsonObject step06sub(SchedTrigger req) throws Exception;
}
