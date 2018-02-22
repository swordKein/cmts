package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.DicService;
import com.kthcorp.cmts.service.GoogleSearchService;
import com.kthcorp.cmts.service.GoogleTransService;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.HttpClientUtil;
import com.kthcorp.cmts.util.JsoupUtil;
import com.kthcorp.cmts.util.StringUtil;
import groovy.transform.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NaverblogService implements NaverblogServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(NaverblogService.class);

    @Autowired
    GoogleSearchService googleSearch;
    @Autowired
    GoogleTransService googleTrans;
    @Autowired
    private DicService dicService;
    @Autowired
    LinkCrawlService linkCrawlService;

    @Value("${cmts.collector.naverblog.search_url}")
    private String naverblog_url;

    @Value("${cmts.collector.naverblog.post_url}")
    private String naverblog_post_url;

    @Value("${cmts.collector.naverblog.paging_limit}")
    private int naverblog_search_page_limit;

    //@Value("${cmts.collector.naverblog.min_content_size}")
    //private int naverblog_min_content_size;

    //@Value("${cmts.collector.naverblog.max_content_size}")
    //private int naverblog_max_content_size;


    public JsonObject getSearchBlog(ConfTarget reqInfo) throws Exception {
        JsonObject result = linkCrawlService.getSearchAndSubPages("NAVER_BLOG", reqInfo, naverblog_search_page_limit
                , naverblog_url, "query", "start", 10
                ,  ".blog .type01 .sh_blog_top .sh_blog_title", "href", naverblog_post_url);

        //System.out.println("#NaverblogService.result::"+result.toString());
        //System.out.println("#NaverblogService.result.size::"+result.get("collectCnt").getAsInt());

        return result;
    }

    /*

    // 주어진 ConfTarget & List<ConfPreset> 기준에 맞추어 해당 태그 대상 데이터 수집
    private JsonObject getSubItems(ConfTarget reqInfo, JsonObject result) throws Exception {
        if (reqInfo != null && reqInfo.getPresetList() != null) {
            String pageContent = HttpClientUtil.reqGet(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null, null, "");
            String tmp = "";
            String trans_tmp = "";
            for (ConfPreset ps : reqInfo.getPresetList()) {
                tmp = JsoupUtil.getTaggedValue(pageContent, ps.getPs_tag());
                result.addProperty(ps.getDest_field(), tmp);
                if ("trans".equals(ps.getPs_type())) {
                    trans_tmp = googleTrans.getTransKoreanResult(tmp);
                    result.addProperty("trans_"+ps.getDest_field(), trans_tmp);
                }
            }
        }
        return result;
    }

    // 블로그 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장
    private JsonObject getSubPages_orig(String pageContent, JsonObject resultObj, ConfTarget reqInfo) throws Exception {
        JsonObject jObj = null;
        JsonArray resultArr = new JsonArray();

        List<String> blogIdList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=blogId]", "value");
        List<String> blogLognoList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=logNo]", "value");

        String subContent = "";
        String subLinkUrl = "";
        for (int idx=0; idx<blogIdList.size(); idx++) {
            subLinkUrl = naverblog_post_url + blogIdList.get(idx) + "&logNo=" + blogLognoList.get(idx);
            System.out.println("#SubPageList url>"+subLinkUrl);

            subContent = HttpClientUtil.reqGet(subLinkUrl, "", null, null, "");
            //System.out.println("#result for id>"+subLinkUrl+" subContent:"+subContent.toString());

            for(ConfPreset ps : reqInfo.getPresetList()) {
                //String subContentTxt = JsoupUtil.getTaggedValue(subContent, ".post-view");
                String subContentTxt = JsoupUtil.getTaggedValue(subContent, ps.getPs_tag());
                subContentTxt = CommonUtil.removeTex(subContentTxt);
                subContentTxt = dicService.filterByDicFilterWords(subContentTxt, reqInfo.getTg_id());

                //if (subContentTxt.length() > naverblog_min_content_size && subContentTxt.length() < naverblog_max_content_size) {
                 if (
                         // 1차 시도 min_max 체크
                         (reqInfo.getIs_fail() != null && "N".equals(reqInfo.getIs_fail())
                         && subContentTxt.length() > reqInfo.getContent_min1() && subContentTxt.length() < reqInfo.getContent_max1())
                     ||
                        // 2차 시도 min_max 체크
                        (reqInfo.getIs_fail() != null && "Y".equals(reqInfo.getIs_fail())
                        && subContentTxt.length() > reqInfo.getContent_min2() && subContentTxt.length() < reqInfo.getContent_max2())
                     ) {

                    jObj = new JsonObject();
                    jObj.addProperty("sub_url", subLinkUrl);
                    jObj.addProperty(ps.getDest_field(), subContentTxt);

                    //System.out.println("#SubContent for link>"+subLinkUrl+" subContentTxt:"+subContentTxt.toString());
                    resultArr.add(jObj);
                }
            }
            resultObj.add("result", resultArr);
        }

        return resultObj;
    }


    // 블로그 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 sub_url을 발췌
    public List<String> getSubPagesUrlArray(List<String> reqArr) {
        List<String> result = new ArrayList<String>();
        if(reqArr != null) {
            for(String s : reqArr) {
                //System.out.println("#getSubPagesUrl. reqUrl:"+s);
                String blogId = "";
                String logNo = "";
                String subUrl = "";

                if(s.contains("blog.naver.com")) {
                    String[] s2 = s.split("logNo=");
                    if(s2 != null && s2[1] != null) {
                        logNo = s2[1];
                    }
                    String[] t1 = s.split("\\?");
                    if (t1 != null && t1[0] != null) {
                        String[] t2 = t1[0].split("/");
//                        for(String t : t2) {
//                            System.out.print(" ##"+t);
//                        }
                        if(t2 != null && t2[3] != null) {
                            blogId = t2[3];
                        }
                    }
                    subUrl = naverblog_post_url + blogId + "&logNo="+logNo;
                } else if(s.contains("blog.me")) {
                    String[] s2 = s.split("/");
                    if(s2 != null && s2[2] != null && s2[3] != null) {
                        logNo = s2[3];

                        //System.out.println("##s2[0]:"+s2[0]+"/s2[2]:"+s2[2]);
                        String[] t1 = s2[2].split("\\.");
                        if (t1 != null && t1[0] != null) {
                            blogId = t1[0];
                        }
                    }
                    subUrl = naverblog_post_url + blogId + "&logNo="+logNo;
                } else if (s.contains("tistory.com")) {
                    subUrl = s;
                } else {

                    //String[] s2 = s.split("/");

                    //if ( s2 != null && s2[3] != null) {
                    //    logNo = s2[3];
                    //    String[] t2 = s2[2].split("\\.");
                    //    if (t2 != null && t2[0] != null) {
                    //        blogId = t2[0];
                    //    }
                    //}
                    //subUrl = naverblog_post_url + blogId + "&logNo="+logNo;

                    subUrl = s;
                }

                //String subUrl = naverblog_post_url + blogId + "&logNo="+logNo;
                //System.out.println("#NaverblogService.getSubPagesUrlArray subUrl:"+subUrl);
                result.add(subUrl);
            }
        }
        return result;
    }

    // 블로그 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장
    private JsonArray getSubPages(String pageContent, JsonArray resultArr, ConfTarget reqInfo) throws Exception {

        //List<String> blogIdList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=blogId]", "value");
        //List<String> blogLognoList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=logNo]", "value");

        List<String> searchedBlogList = JsoupUtil.getTaggedLinkArray(pageContent, ".blog .type01 .sh_blog_top .sh_blog_title", "href");
        //System.out.println("#NaverBlogService.getSubPages:"+searchedBlogList.toString());

        // 검색결과 배열로 subUrl 배열 취득
        List<String> subUrlArray = getSubPagesUrlArray(searchedBlogList);

        String subContent = "";
        String subLinkUrl = "";
        JsonObject jObj = null;


        //for (int idx=0; idx<blogIdList.size(); idx++) {
        for (int idx=0; idx<subUrlArray.size(); idx++) {
            //subLinkUrl = naverblog_post_url + blogIdList.get(idx) + "&logNo=" + blogLognoList.get(idx);
            //subLinkUrl = naverblog_post_url + blogIdList.get(idx);
            subLinkUrl = subUrlArray.get(idx);
            System.out.println("#SubPageList url:#"+subLinkUrl);
            try {
                subContent = "";
                subContent = HttpClientUtil.reqGet(subLinkUrl, "", null, null, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println("#result for id>"+subLinkUrl+" subContent:"+subContent.toString());

            jObj = new JsonObject();
            jObj.addProperty("sub_url", subLinkUrl);

            JsonArray dest_fields = new JsonArray();
            JsonArray jArr = new JsonArray();

            for(ConfPreset ps : reqInfo.getPresetList()) {
                //String subContentTxt = JsoupUtil.getTaggedValue(subContent, ".post-view");
                String subContentTxt = JsoupUtil.getTaggedValue(subContent, ps.getPs_tag());
                subContentTxt = CommonUtil.removeTex(subContentTxt);
                logger.debug("#MLOG NAVER_BLOG getSubPages by reqInfo:"+reqInfo.toString());
                subContentTxt = dicService.filterByDicFilterWords(subContentTxt, reqInfo.getTg_id());

                //System.out.println("#subPages:subContentTxt:"+subContentTxt);

                //if (subContentTxt.length() > naverblog_min_content_size && subContentTxt.length() < naverblog_max_content_size) {
                if (
                        // 1차 시도 min_max 체크
                        (reqInfo.getIs_fail() != null && "N".equals(reqInfo.getIs_fail())
                        && subContentTxt.length() > reqInfo.getContent_min1() && subContentTxt.length() < reqInfo.getContent_max1())
                    ||
                        // 2차 시도 min_max 체크
                        (reqInfo.getIs_fail() != null && "Y".equals(reqInfo.getIs_fail())
                        && subContentTxt.length() > reqInfo.getContent_min2() && subContentTxt.length() < reqInfo.getContent_max2())
                 ) {
                //서브페이지 아이템 ps_tag에 맞추어 array로 수집
                    JsonObject subJobj = new JsonObject();
                    subJobj.addProperty(ps.getDest_field(), subContentTxt);
                    jArr.add(subJobj);
                    dest_fields.add(ps.getDest_field());
                } else {
                    //기준 컨텐츠 사이즈 (min ~ max) 내에 해당하지 않으면 공백으로 저장
                    logger.info("#MLOG Collect:naverBlog id:"+reqInfo.getTg_id()+" filtered by content size:"+subContentTxt.length());
                    JsonObject subJobj = new JsonObject();
                    subJobj.addProperty(ps.getDest_field(), "");
                    jArr.add(subJobj);
                    dest_fields.add(ps.getDest_field());
                }

            }
            jObj.add("result", jArr);
            jObj.add("dest_fields", dest_fields);
            resultArr.add(jObj);
        }

        return resultArr;
    }

    private synchronized JsonArray getSearchBlogPaging(JsonArray resultArr, ConfTarget reqInfo
            , int pageNo, String stDate, String edDate) throws Exception {

        if(reqInfo != null && reqInfo.getParam1() != null) {
            if (resultArr == null) resultArr = new JsonArray();

            //String reqPageno = Integer.toString(pageNo);

            Map<String, Object> reqparams = new HashMap<String, Object>();
            //reqparams.put("reqPage", "option.page.currentPage");
            reqparams.put("reqPage", "start");
            reqparams.put("reqPageNo", (pageNo*10) + 1);

            String reqQuery = "";
            if (!"".equals(reqInfo.getParam1())) reqQuery = URLEncoder.encode(reqInfo.getParam1().toString(), "UTF-8");
            //reqparams.put("reqQuery", "option.keyword");
            reqparams.put("reqQuery", "query");
            reqparams.put("reqQueryString", reqQuery);

            //
            //if (!"".equals(stDate)) {
            //    reqparams.put("reqSt", "option.startDate");
            //    reqparams.put("reqStDate", stDate);
            //}
            //if (!"".equals(edDate)) {
            //    reqparams.put("reqEd", "option.endDate");
            //    reqparams.put("reqEdDate", edDate);
            //}

            try {
                String pageContent = HttpClientUtil.reqGet(naverblog_url, "", null, reqparams, "");
                resultArr = getSubPages(pageContent, resultArr, reqInfo);
                //System.out.println("#result JsonArray:"+result.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return resultArr;
    }


    @Override
    @Synchronized
    public JsonObject getSearchBlog(ConfTarget reqInfo) throws Exception {
        JsonObject resultObj = new JsonObject();
        JsonArray resultArr = new JsonArray();

        if(!reqInfo.getTg_url().equals("NAVER_BLOG")) throw new Exception("Conf_Preset tg_url is not NAVER BLOG!");

        for(int i=1; i<=naverblog_search_page_limit; i++) {
            resultArr = getSearchBlogPaging(resultArr, reqInfo, i, "", "");
        }
        resultObj.add("result", resultArr);
        //System.out.println("#getNaverBlog::"+result.toString());

        logger.info("#getNaverBlog resultObj::" + resultObj.toString());
        return resultObj;
    }


    private JsonObject getMovieOne(String title, String producer, String year, ConfTarget reqInfo) {
        JsonObject result = new JsonObject();
        result.addProperty("title", title);
        result.addProperty("producer", producer);
        result.addProperty("year", year);

        try {
            JsonArray jArr = googleSearch.getSearchItems2(title, producer, year);

            if (jArr != null && jArr.size() > 0) {
                //System.out.println(">> items::" + jObj.toString());
                JsonObject obj = (JsonObject) jArr.get(0);

                String reqUrl = "http://"+obj.get("link").toString();
                reqUrl = CommonUtil.removeTex(reqUrl);
                String addUrl = "plotsummary?ref_=tt_stry_pl";
                String resultStr = HttpClientUtil.reqGet(reqUrl, addUrl, null, null, "");

                String plot = "";
                result.addProperty("plot", plot);
                String transPlot = googleTrans.getTransKoreanResult(plot);
                result.addProperty("transPlot", transPlot);

            }
        } catch (Exception pe) {
            pe.printStackTrace();
        }

        System.out.println("#getNaverBlog::"+result.toString());
        return result;
    }
    */
}
