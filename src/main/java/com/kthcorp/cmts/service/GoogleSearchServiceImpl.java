package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;

import java.util.List;

public interface GoogleSearchServiceImpl {

    public JsonObject getFirstSearchedMovieItem(String title, String producer, String year);
    public String getSearchItems(String title, String producer, String year);

    JsonArray getOneItemFromSearchedList(ConfTarget reqInfo);

    public JsonArray getSearchItems2(String title, String producer, String year);
    public JsonArray getSearchItems3(ConfTarget reqInfo);
}
