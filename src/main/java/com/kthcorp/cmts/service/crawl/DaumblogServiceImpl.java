package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

public interface DaumblogServiceImpl {

    JsonObject getSearchBlog(ConfTarget reqInfo) throws Exception;
}
