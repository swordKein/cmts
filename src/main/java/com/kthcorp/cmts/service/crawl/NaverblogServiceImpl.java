package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

public interface NaverblogServiceImpl {

    JsonObject getSearchBlog(ConfTarget reqInfo) throws Exception;
}
