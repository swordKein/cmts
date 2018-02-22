package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;
import groovy.transform.Synchronized;

public interface LinkCrawlServiceImpl {
    /* 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장 */
    /* 서브페이지 수집 실패 이력도 리턴 */
    JsonObject getSubPages2(String prefix, String pageContent, JsonObject resultObject, ConfTarget reqInfo, String tags, String attrs
            , String postUrl) throws Exception;

    /*
        EX> daum_news
            http://search.daum.net/search?w=news&DA=PGD&cluster=y&p=61&q=%EC%98%81%ED%99%94+%EC%B1%84%EB%B9%84+%EB%A6%AC%EB%B7%B0
            paramQuery = q
            paramPaging = p
            paramDegree = 1
            tags = #clusterResultUL li .wrap_tit a
            attrs = href
         */
    @Synchronized
    JsonObject getSearchAndSubPages(String prefix, ConfTarget reqInfo, int pageLimit
            , String searchUrl, String paramQuery, String paramPaging, int pagingDegree, String tags, String attrs
            , String postUrl) throws Exception;
}
