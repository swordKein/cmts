package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;
import org.springframework.scheduling.annotation.Async;

public interface StepServiceImpl {

    void run(String... strings) throws Exception;

    public String getTest();

    // 수집 스케쥴의 stat를 P로 업데이트 한다
    int uptSchedTriggerForCollectStep02(SchedTrigger sched);
    // 수집 ConfTarget 설정의 stat를 P로 업데이트 한다
    int uptConfTargetBeforeCollectProcess(ConfTarget tg) throws Exception;
    // 수집 완료 후 stat 업데이트
    int uptSchedTriggerProgsAfterCollectTargetOneProcess(int sc_id, String statTarget) throws Exception;



    // 정제 스케쥴의 stat를 P로 업데이트 한다
    int uptSchedTriggerForRefineStep05(SchedTrigger sched);
    // 정제 ConfTarget 설정의 stat를 P로 업데이트 한다
    int uptConfTargetBeforeRefineProcess(ConfTarget tg) throws Exception;
    // 정제 완료 후 stat 업데이트
    int uptSchedTriggerProgsAfterRefineTargetOneProcess (int sc_id, String statTarget) throws Exception;


    // 분석 스케쥴의 stat를 P로 업데이트 한다
    int uptSchedTriggerForAnalyzeStep07(SchedTrigger sched);
    // 분석 ConfTarget 설정의 stat를 P로 업데이트 한다
    int uptConfTargetBeforeAnalyzeProcess(ConfTarget tg) throws Exception;
    // 분석 완료 후 stat 업데이트
    int uptSchedTriggerProgsAfterAnalyzeTargetOneProcess(int sc_id, String statTarget) throws Exception;
}
