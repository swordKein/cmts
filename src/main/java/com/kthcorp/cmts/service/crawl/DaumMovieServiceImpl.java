package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

import java.util.Map;

public interface DaumMovieServiceImpl {

    String getSearchedMovieUrl(String reqStr);

    JsonObject getSearchedDaumMovie(String reqStr);

    JsonArray getSearchWebItemsForNaverMovie(String reqStr) throws Exception;

    JsonArray getSearchWebItemsForDaumMovie(String reqStr, String movieTitle, String movieYear) throws Exception;

    JsonObject getSearchWebItemsForDaumMovieV3(String reqUrl, String movieTitle, String movieYear) throws Exception;

    JsonObject getOneItemFromSearchedPage(ConfTarget reqInfo);

    JsonObject getContents(String prefix, ConfTarget reqInfo) throws Exception;

    Map<String, Object> getDaumMovieItem(String title, String year);

    JsonObject getOneMovieFromDaumSearch(String title, String year);
}
