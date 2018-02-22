package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

public interface ImdbServiceImpl {

    public JsonObject getMovie(ConfTarget reqInfo) throws Exception;
}
