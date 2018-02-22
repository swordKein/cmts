package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface GoogleTransServiceImpl {
    public String getTransKoreanResultWeb(String reqTxt) throws Exception;

    public String getTransKoreanResult(String reqTxt) throws Exception ;
}
