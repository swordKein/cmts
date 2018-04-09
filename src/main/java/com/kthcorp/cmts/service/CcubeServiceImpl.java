package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.AuthUser;
import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.CcubeKeys;
import com.kthcorp.cmts.model.CcubeSeries;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    int processCcubeOutputToJson();

    int processCcubeOutputToJsonByType(String type);

    @Transactional
    int processCcubeSeriesOutputToJsonTest();
}
