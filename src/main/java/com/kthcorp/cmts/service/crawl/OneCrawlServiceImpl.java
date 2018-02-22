package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

public interface OneCrawlServiceImpl {
    /* 주어진 ConfTarget & List<ConfPreset> 기준에 맞추어 해당 태그 대상 데이터 수집 */
    /* ImdbService.getSubItems 에서 파생 */
    JsonObject getSubItems(ConfTarget reqInfo, JsonObject resultObj, String type) throws Exception;
}
