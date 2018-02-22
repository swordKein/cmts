package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

public interface PagingArrayCrawlServiceImpl {
    JsonObject getReplyItemsByPaging(String prefix, ConfTarget reqInfo, int limitCnt)  throws Exception;

    //JsonObject getMagazineItemsByPaging(String prefix, ConfTarget reqInfo, int limitCnt)  throws Exception;
}
