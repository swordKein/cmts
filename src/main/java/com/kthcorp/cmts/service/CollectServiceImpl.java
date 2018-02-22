package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;

import java.util.List;

public interface CollectServiceImpl {

    int ollehTvMetaCollectScheduleCheck() throws Exception;

    int test_ollehTvMetaCollectScheduleCheck(SchedTrigger req) throws Exception;

    /* STEP 0-1
                 * SchedTrigger & targetList<ConfTarget> 통해 수집 스케쥴 50개 조회
                */
    List<SchedTrigger> step01() throws Exception;

    List<SchedTrigger> step01byScid(SchedTrigger req);

    /* STEP 0-2
             * ConfTarget & List<ConfPreset> 을 통해 수집 대상 설정 조회
            */
    ConfTarget step02(ConfTarget req) throws Exception;

    /* STEP 3imdb
         * tg_url:GOOGLE_SEARCH_IMDB 구글 검색 후 imdb.com 데이터 수집
        */
    JsonObject step03imdb(ConfTarget req) throws Exception;

    /* STEP 03 - crawl by html api /crawl/byprefix
    */
    JsonObject step03_ByHtml(ConfTarget req);

    /* STEP 3naverBlog
                 * tg_url:NAVER_BLOG 검색 후 검색 페이지의 일정 페이징 사이즈만큼 데이터 수집
                */
    JsonObject step03naverBlog(ConfTarget req) throws Exception;

    /* STEP 3daumBlog - sub
         * tg_url:DAUM_BLOG 검색 후 검색 페이지의 일정 페이징 사이즈만큼 데이터 수집
        */
    JsonObject step03daumBlog(ConfTarget req) throws Exception;

    /* STEP 3naverMovie - sub
         * tg_url:NAVER_MOVIE 검색 후 메타 데이터 수집
        */
    JsonObject step03naverMovie(ConfTarget req) throws Exception;

    /* STEP 3daumMovie - sub
         * tg_url:DAUM_MOVIE 검색 후 메타 데이터 수집
        */
    JsonObject step03daumMovie(ConfTarget req) throws Exception;
}
