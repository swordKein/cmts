package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AnalyzeServiceImpl {

    int ollehTvMetaAnalyzeScheduleCheck() throws Exception;

    /* for test */
    List<SchedTrigger> step07byScid(SchedTrigger req);

    /* test */
    int test_ollehTvMetaAnalyzeScheduleCheck(SchedTrigger req1) throws Exception;

    /* STEP07
         * SchedTrigger & targetList<ConfTarget> 통해 수집 스케쥴 50개 조회
        */
    List<SchedTrigger> step07() throws Exception;

    /* STEP08
     * ConfTarget & List<ConfPreset> 을 통해 수집 대상 설정 조회
    */
    ConfTarget step08(ConfTarget req) throws Exception;

    /* STEP09 분석 sub - sub1 type별 분류 키워드 조회

     */
    Map<String, Object> step09_sub_01_getKeywordsByType(
            List<HashMap<String, Object>> resultMap
            , HashMap<String, Object> keywordResult
            , HashMap<String, Object> joinResult
            , HashMap<String, Double> keywordMapList);

    /* STEP09 분석 sub - sub2 type별 메타-태그 조회

     */
    Map<String, Object> step09_sub_02_getMetaByType(
            List<HashMap<String, Object>> resultMap
            , HashMap<String, Object> metaResult
            , HashMap<String, Object> joinResult
            , HashMap<String, Double> keywordMapList);

    /* STEP09 분석 작업 - sub
                    */
    JsonObject step09sub(SchedTrigger req);
}
