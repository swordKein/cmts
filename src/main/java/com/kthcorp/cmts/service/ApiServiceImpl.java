package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.AuthUser;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;

import java.util.List;

public interface ApiServiceImpl {
    String getHashCode(String custid, String authkey) throws Exception;


    int checkAuthByHashCode(String custid, String hash) throws Exception;

    String getRtmsg(int rtcode);

    List<AuthUser> getAuthUsers();

    AuthUser getAuthUserById(AuthUser req);

    int insAuthUser(AuthUser req);

    int uptAuthUser(AuthUser req);

    int delAuthUser(AuthUser req);

    JsonObject getMovieInfoByIdx(int itemIdx);

    JsonObject getAwardInfoByIdx(int itemIdx);

    JsonObject getCine21DatasByIdx(int itemIdx);

    JsonObject getCine21Datas(String title);

    //JsonObject getDicKeywordsByType(String type, int pageSize, int pageno);

    JsonObject getDicKeywordsByType(String type, String keyword, int pageSize, int pageno);

    JsonObject getItemsSearch(
            int pageSize, int pageno
            , String searchType
            , String searchStat
            , String searchSdate
            , String searchEdate
            , String searchKeyword
            , String searchParts
    );

    int uptSchedTriggerStatByItemIdxAndType(int itemIdx, String type, String stat);

    JsonArray getSnsKeywords(String title) throws Exception;
}
