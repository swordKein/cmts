package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.DicService;
import com.kthcorp.cmts.service.GoogleSearchService;
import com.kthcorp.cmts.service.GoogleTransService;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.HttpClientUtil;
import com.kthcorp.cmts.util.JsoupUtil;
import groovy.transform.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DaumnewsService implements DaumnewsServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(DaumnewsService.class);

    @Autowired
    LinkCrawlService linkCrawlService;

    @Value("${cmts.collector.daumnews.search_url}")
    private String daumnews_url;

    @Value("${cmts.collector.daumnews.paging_limit}")
    private int daumnews_search_page_limit;

    @Override
    @Synchronized
    public JsonObject getSearchNews(ConfTarget reqInfo) throws Exception {
        return linkCrawlService.getSearchAndSubPages("DAUM_NEWS", reqInfo, daumnews_search_page_limit
                , daumnews_url, "q", "p", 1
                , "#clusterResultUL li .wrap_tit a", "href", "");
    }

}
