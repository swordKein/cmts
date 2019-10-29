package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

public interface Cine21MovieServiceImpl {

    JsonObject getSearchCine21(ConfTarget reqInfo) throws Exception;
}
