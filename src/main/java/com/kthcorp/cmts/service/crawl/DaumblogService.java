package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.DicService;
import com.kthcorp.cmts.service.GoogleSearchService;
import com.kthcorp.cmts.service.GoogleTransService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DaumblogService implements DaumblogServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(DaumblogService.class);

    @Autowired
    GoogleSearchService googleSearch;
    @Autowired
    GoogleTransService googleTrans;
    @Autowired
    private DicService dicService;
    @Autowired
    LinkCrawlService linkCrawlService;

    @Value("${cmts.collector.daumblog.search_url}")
    private String daumblog_url;

    @Value("${cmts.collector.daumblog.post_url}")
    private String daumblog_post_url;

    @Value("${cmts.collector.daumblog.paging_limit}")
    private int daumblog_search_page_limit;

    //@Value("${cmts.collector.daumblog.min_content_size}")
    //private int daumblog_min_content_size;

    //@Value("${cmts.collector.daumblog.max_content_size}")
    //private int daumblog_max_content_size;


    public JsonObject getSearchBlog(ConfTarget reqInfo) throws Exception {
        return linkCrawlService.getSearchAndSubPages("DAUM_BLOG", reqInfo, daumblog_search_page_limit
                , daumblog_url, "q", "page", 10
                ,  ".wrap_cont .cont_inner .wrap_tit a", "href", daumblog_post_url);
    }
}
