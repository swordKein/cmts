package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.AuthUser;
import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.CcubeKeys;
import com.kthcorp.cmts.model.CcubeSeries;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface CcubeServiceImpl {

    List<CcubeContent> get50ActiveCcubeContents();

    int uptCcubeContentStat(CcubeContent req);

    List<CcubeSeries> get50ActiveCcubeSeries();

    int uptCcubeSeriesStat(CcubeSeries req);

    CcubeContent getCcubeContentByCid(CcubeContent req);

    CcubeSeries getCcubeSeriesById(CcubeSeries req);

    int getCcubeItemIdx(CcubeKeys req);

    int getCcubeCIdx(CcubeKeys req);

    int insCcubeKeys(CcubeKeys req);

    JsonObject getCcubeDatasByItemIdx(int itemIdx);

    JsonArray getJsonArrayForCcubeOutput(JsonArray contentsArr, String type, Map<String, Object> reqMap) throws Exception;

    JsonArray getJsonArrayForCcubeOutput_Orig(JsonArray contentsArr, String type, Map<String, Object> reqMap) throws Exception;

    JsonObject getJsonObjectForCcubeOutput(String type, Map<String, Object> reqMap) throws Exception;

    int processCcubeOutputToJson();

    int processCcubeOutputToJsonByType(String type);

    @Transactional
    List<Map<String, Object>> processCcubeOutputToMapListByType(String type);

    @Transactional
    int processCcubeSeriesOutputToJsonTest();

    int insCcubeContent(CcubeContent req);

    int insCcubeSeries(CcubeSeries req);

    int insCcubeContentManual(CcubeContent req);

    int insCcubeSeriesManual(CcubeSeries req);

    int insCcubeOutput(Map<String, Object> req);
}
