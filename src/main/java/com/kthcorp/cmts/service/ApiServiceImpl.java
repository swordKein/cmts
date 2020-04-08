package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.AuthUser;
import com.kthcorp.cmts.model.Items;
import org.springframework.web.multipart.MultipartFile;

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

    JsonObject getAwardArrInfoByIdx(int itemIdx) throws Exception;

    String getFilteredGenre(String origGenre);

    JsonObject getCine21DatasByIdx(int itemIdx);

    JsonObject getCine21Datas(String title);

    //JsonObject getDicKeywordsByType(String type, int pageSize, int pageno);

    JsonObject getDicKeywordsByType(String type, String keyword, String orderby, int pageSize, int pageno);	

    String getChangedMtypes(String searchParts);


    JsonObject getItemsReSearch(
            int pageSize, int pageno
            , String searchType
            , String searchStat
            , String searchSdate
            , String searchEdate
            , String searchKeyword
            , String searchParts
            , String resultSearch
            , String[] precondition
            , String _precondition
    );

    JsonObject getItemsSearch(
            int pageSize, int pageno
            , String searchType
            , String searchStat
            , String searchSdate
            , String searchEdate
            , String searchKeyword
            , String searchParts
            , String resultSearch
            , String precondition
    );

    JsonArray getListItemsFromArray(List<Items> itemsList);

    int uptSchedTriggerStatByItemIdxAndType(int itemIdx, String type, String stat);

    JsonArray getSnsKeywords(String title) throws Exception;

    JsonObject getSnsTopKeywords() throws Exception;

    int processSnsTopKeywordsByDateSched();

    List<String> getResultSnsMapByTag(String target, String date1, String tag);

    JsonObject getSnsTopWordsAndGraph() throws Exception;

    String getNaverKordicResult(String keyword) throws Exception;

    String getCollNaverKordicResult(String keyword) throws Exception;
    
    //mcid로 동일 컨텐츠 검색
    public JsonObject getItemListSameMcid(Integer itemid);

    String returnStringFromMultiPartFile(MultipartFile uploadfile, String strType);

    String returnStringFromMultiPartFileForDIC(MultipartFile uploadfile, String strType);
}
