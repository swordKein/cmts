package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

public interface NaverMovieServiceImpl {

    JsonArray getSearchWebItemsForNaverMovie(String reqStr) throws Exception;

    JsonArray getSearchWebItemsForDaumMovie(String reqStr) throws Exception;

    JsonObject getContents(String prefix, ConfTarget reqInfo) throws Exception;
}
