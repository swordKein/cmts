package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.kthcorp.cmts.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service
public interface DicServiceImpl {
    List<DicFilterWords> getDicFilterWords();
    int insDicFilterWords(DicFilterWords req);
    List<DicFilterWordsHist> getDicFilterWordsHist();
    int insDicFilterWordsHist(DicFilterWordsHist req);
    String filterByDicFilterWords(String req, int req_id) throws Exception ;
    List<String> filterListByDicFilterWords(List<String> req, int req_id);

    //// 단건 제외어 필터 적용
    //String filterByDicFilterWords(String req, int req_id) throws Exception;

    List<DicNotuseWords> getDicNotuseWords();
    int insDicNotuseWords(DicNotuseWords req);
    List<DicNotuseWordsHist> getDicNotuseWordsHist();
    int insDicNotuseWordsHist(DicNotuseWordsHist req);


    // 단건 불용어 사전에 의해 제외 처리
    String filterByDicNotuseWords(List<DicNotuseWords> dic, String req, int req_id) throws Exception;

    List<String> filterListByDicNotuseWords(List<String> req, int req_id);


    // 대체어 사전 조회
    List<DicChangeWords> getDicChangeWords();
    // 대체어 사전에 등록
    int insDicChangeWords(DicChangeWords req);
    // 대체어 사전 이력 리스트 조회
    List<DicChangeWordsHist> getDicChangeWordsHist();
    // 대체어 사전 이력 등록
    int insDicChangeWordsHist(DicChangeWordsHist req);

    // 단건 대체어 사전에 의해 제외 처리
    String filterByDicChangeWords(List<DicChangeWords> dic, String req, int req_id) throws Exception;

    // 리스트 대체어 사전에 의해 제외 처리
    List<String> filterListByDicChangeWords(List<String> req, int req_id);


    // 1글자 제외 로직 반영
    List<String> filterListByLengthUnder2byte(List<String> req);

    // 추가어 사전 조회
    List<DicAddWords> getDicAddWords();
    // 추가어 사전에 등록
    int insDicAddWords(DicAddWords req);
    // 추가어 사전 이력 리스트 조회
    List<DicAddWordsHist> getDicAddWordsHist();
    // 추가어 사전 이력 등록
    int insDicAddWordsHist(DicAddWordsHist req);

    // 단건 추가어 사전에 의해 제외 처리
    Double filterByDicAddWords(List<DicAddWords> dic, String req, Double reqFreq, int req_id) throws Exception;

    // 리스트 추가어 사전에 의해 제외 처리
    HashMap<String, Double> filterListByDicAddWords(Map<String, Double> reqMap, int req_id);

    List<String> getMetaTypes();

    List<MetaKeywordMapping> getMetaKeywordMappingListAll();

    List<String> getTagTypes();

    List<DicKeywords> getDicKeywordsListAll();

    // 키워드-매핑 사전
    // 키워드-매핑 사전 조회
    List<DicKeywords> getDicKeywords(DicKeywords req);

    // 키워드-매핑 사전 등록 등록
    int insDicKeywords(DicKeywords req);

    // 미분류 키워드 사전 등록
    @Transactional
    int insDicNotMapKeywords(DicKeywords req);

    int insNotMapKeywords(JsonArray reqArr);

    //int countItems(String type);

    int countItems(String type, String keyword);

    //JsonArray getDicKeywordsByType(String type, int pageSize, int pageno);

    JsonArray getDicKeywordsByType(String type, String keyword, int pageSize, int pageno);

    int modifyDicsByTypesFromArrayList(String items);

    List<DicKeywords> getRankWordsByGenreAndType(DicKeywords req);

    List<String> getKeywordTypes();

    List<String> getMixedGenreArrayFromGenre(String genre, String mtype);
}
