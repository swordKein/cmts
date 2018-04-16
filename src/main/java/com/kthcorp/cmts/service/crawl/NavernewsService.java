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
public class NavernewsService implements NavernewsServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(NavernewsService.class);

    @Autowired
    GoogleSearchService googleSearch;
    @Autowired
    GoogleTransService googleTrans;
    @Autowired
    private DicService dicService;
    @Autowired
    LinkCrawlService linkCrawlService;

    @Value("${cmts.collector.navernews.search_url}")
    private String navernews_url;

    @Value("${cmts.collector.navernews.paging_limit}")
    private int navernews_search_page_limit;

    @Override
    @Synchronized
    public JsonObject getSearchNews(ConfTarget reqInfo) throws Exception {
        return linkCrawlService.getSearchAndSubPages("NAVER_NEWS", reqInfo, navernews_search_page_limit
                , navernews_url, "query", "start", 10
                , ".news .type01 li dt a", "href", "");
    }



    /*

    //@Value("${cmts.collector.naverblog.min_content_size}")
    //private int naverblog_min_content_size;

    //@Value("${cmts.collector.naverblog.max_content_size}")
    //private int naverblog_max_content_size;


    // 주어진 ConfTarget & List<ConfPreset> 기준에 맞추어 해당 태그 대상 데이터 수집
    private JsonObject getSubItems(ConfTarget reqInfo, JsonObject result) throws Exception {
        if (reqInfo != null && reqInfo.getPresetList() != null) {
            String pageContent = HttpClientUtil.reqGet(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null,null, "");
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
            //subLinkUrl = naverblog_post_url + blogIdList.get(idx) + "&logNo=" + blogLognoList.get(idx);
            // subLink 추출 필요
            subLinkUrl = "";
            System.out.println("#SubPageList url>"+subLinkUrl);

            subContent = HttpClientUtil.reqGet(subLinkUrl, "", null,null, "");
            //System.out.println("#result for id>"+subLinkUrl+" subContent:"+subContent.toString());

            for(ConfPreset ps : reqInfo.getPresetList()) {
                //String subContentTxt = JsoupUtil.getTaggedValue(subContent, ".post-view");
                String subContentTxt = JsoupUtil.getTaggedValue(subContent, ps.getPs_tag());
                subContentTxt = CommonUtil.removeTex(subContentTxt);
                subContentTxt = dicService.filterByDicFilterWords(subContentTxt, reqInfo.getTg_id());

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
                    //subUrl = naverblog_post_url + blogId + "&logNo="+logNo;
                    //subUrl 발췌 필요
                    subUrl = "";
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
                    //subUrl = naverblog_post_url + blogId + "&logNo="+logNo;
                    //subUrl 발췌 필요
                    subUrl = "";
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

    // 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장
    private JsonArray getSubPages(String pageContent, JsonArray resultArr, ConfTarget reqInfo) throws Exception {

        //List<String> blogIdList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=blogId]", "value");
        //List<String> blogLognoList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=logNo]", "value");

        List<String> searchedBlogList = JsoupUtil.getTaggedLinkArray(pageContent, ".news .type01 li dt a", "href");
        System.out.println("#NaverNewsService.getSubPages:"+searchedBlogList.toString());

        // 검색결과 배열로 subUrl 배열 취득
        List<String> subUrlArray = getSubPagesUrlArray(searchedBlogList);

        String subContent = "";
        String subLinkUrl = "";
        JsonObject jObj = null;

        List<ConfPreset> presetList = reqInfo.getPresetList();

        //for (int idx=0; idx<blogIdList.size(); idx++) {
        for (int idx=0; idx<subUrlArray.size(); idx++) {
            //subLinkUrl = naverblog_post_url + blogIdList.get(idx) + "&logNo=" + blogLognoList.get(idx);
            //subLinkUrl = naverblog_post_url + blogIdList.get(idx);
            subLinkUrl = subUrlArray.get(idx);
            System.out.println("#SubPageList url:#" + subLinkUrl);

            // ancmtsjs 사용하는 웹페이지는 일단 제외 ( 한국일보 )
            // if (!subLinkUrl.contains("star.hankooki")) {
                try {

                    // ConfPreset 에 dest_charset 이 지정되 있을 경우 subLink 연동 시 target_charset 지정해서 연결
                    Charset dest_charset = null;
                    for(ConfPreset ps : presetList) {
                        if(ps.getDescriptp() != null && subLinkUrl.contains(ps.getDescriptp())) {
                            if(ps.getDest_charset() != null
                                &&
                                ("UTF-8".equals(ps.getDest_charset()) || "EUC-KR".equals(ps.getDest_charset()))
                            )
                            dest_charset = Charset.forName(ps.getDest_charset());
                        }
                    }

                    subContent = "";
                    subContent = HttpClientUtil.reqGet(subLinkUrl, "", dest_charset,null, "");

                    //System.out.println("#SubContent:" + subContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("#result for id>"+subLinkUrl+" subContent:"+subContent.toString());

                jObj = new JsonObject();
                jObj.addProperty("sub_url", subLinkUrl);

                JsonArray dest_fields = new JsonArray();
                JsonArray jArr = new JsonArray();

                for (ConfPreset ps : reqInfo.getPresetList()) {
                    //String subContentTxt = JsoupUtil.getTaggedValue(subContent, ".post-view");
                    String subContentTxt = JsoupUtil.getTaggedValue(subContent, ps.getPs_tag());
                    subContentTxt = CommonUtil.removeTex(subContentTxt);
                    logger.debug("#MLOG NAVER_NEWS getSubPages by reqInfo:" + reqInfo.toString());
                    subContentTxt = dicService.filterByDicFilterWords(subContentTxt, reqInfo.getTg_id());

                    //System.out.println("#subPages:subContentTxt:"+subContentTxt);

                    if (// 2차 시도 min_max 체크
                         subContentTxt.length() > reqInfo.getContent_min2() && subContentTxt.length() < reqInfo.getContent_max2()
                            ) {
                        //서브페이지 아이템 ps_tag에 맞추어 array로 수집
                        JsonObject subJobj = new JsonObject();
                        subJobj.addProperty(ps.getDest_field(), subContentTxt);
                        jArr.add(subJobj);
                        dest_fields.add(ps.getDest_field());

                    } else {
                        //기준 컨텐츠 사이즈 (min ~ max) 내에 해당하지 않으면 공백으로 저장
                        //logger.info("#MLOG Collect:naverBlog id:"+reqInfo.getTg_id()+" filtered by content size:"+subContentTxt.length());
                        //JsonObject subJobj = new JsonObject();
                        //subJobj.addProperty(ps.getDest_field(), "");
                        //jArr.add(subJobj);
                        //dest_fields.add(ps.getDest_field());
                    }

                }
                if(jArr != null && jArr.size() > 0) {
                    jObj.add("result", jArr);
                    jObj.add("dest_fields", dest_fields);
                    resultArr.add(jObj);
                }
            //}
        }
        return resultArr;
    }

    private String getSubContentByCharset(String subLinkUrl, List<ConfPreset> presetList) {
        String subContent = "";
        try {
            // ConfPreset 에 dest_charset 이 지정되 있을 경우 subLink 연동 시 target_charset 지정해서 연결
            Charset dest_charset = null;
            for(ConfPreset ps : presetList) {
                if(ps.getDescriptp() != null && subLinkUrl.contains(ps.getDescriptp().toString())) {
                    if(ps.getDest_charset() != null
                            &&
                            ("UTF-8".equals(ps.getDest_charset()) || "EUC-KR".equals(ps.getDest_charset()))
                            )
                        dest_charset = Charset.forName(ps.getDest_charset());
                        //System.out.println("#NaverNews  surl:"+subLinkUrl+" // eurl:"+ps.getDescriptp()+"   // setDestCharset:"+dest_charset.toString());
                }
            }

            subContent = "";
            subContent = HttpClientUtil.reqGet(subLinkUrl, "", dest_charset,null, "");

            //System.out.println("#SubContent:" + subContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("#result for id>"+subLinkUrl+" subContent:"+subContent.toString());
        return subContent;
    }

    // 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장
    // 서브페이지 수집 실패에 대응하기 위해 JsonObject 형태로 리턴
    private JsonObject getSubPages2(String pageContent, JsonObject resultObject, ConfTarget reqInfo) throws Exception {
        //List<String> blogIdList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=blogId]", "value");
        //List<String> blogLognoList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=logNo]", "value");
        JsonObject resultObj = new JsonObject();
        JsonArray resultArr = (JsonArray) resultObject.get("resultArr");
        JsonArray failResultArr = (JsonArray) resultObject.get("failResultArr");

        List<String> searchedBlogList = JsoupUtil.getTaggedLinkArray(pageContent, ".news .type01 li dt a", "href");
        System.out.println("#NaverNewsService.getSubPages:"+searchedBlogList.toString());

        // 검색결과 배열로 subUrl 배열 취득
        List<String> subUrlArray = getSubPagesUrlArray(searchedBlogList);

        String subContent = "";
        String subLinkUrl = "";
        JsonObject jObj = null;

        List<ConfPreset> presetList = reqInfo.getPresetList();

        //for (int idx=0; idx<blogIdList.size(); idx++) {
        for (int idx=0; idx<subUrlArray.size(); idx++) {
            boolean is_collected = false;
            boolean is_defined = false;
            boolean is_crashed = false;
            boolean is_filtered = false;

            //subLinkUrl = naverblog_post_url + blogIdList.get(idx) + "&logNo=" + blogLognoList.get(idx);
            //subLinkUrl = naverblog_post_url + blogIdList.get(idx);
            subLinkUrl = subUrlArray.get(idx);
            System.out.println("#SubPageList url:#" + subLinkUrl);

            // ConfPreset 리스트에서 dest_charset 지정여부를 파악하여 서브페이지 컨텐츠 http로 가져온다
            subContent = getSubContentByCharset(subLinkUrl, presetList);

            // subPage의 내용이 <script>location.href="........."</script> 일 경우 예외 조항 처리
            if (subContent.trim().length() < 200) {
                if (subContent.trim().startsWith("<script") && subContent.trim().endsWith("script>")
                    && subContent.contains("location.href=")) {
                    String toUrl = subContent.trim().replace("<script>", "");
                    toUrl = toUrl.replace("<script type='text/javascript'>", "");
                    toUrl = toUrl.replace("</script>", "");
                    toUrl = toUrl.replace("\"", "");
                    toUrl = toUrl.replace("\'", "");
                    toUrl = toUrl.replace(";", "");
                    toUrl = toUrl.replace("location.href=", "");

                    toUrl = toUrl.trim();
                    if(toUrl.startsWith("/")) {
                        String dns[] = subLinkUrl.split("/");
                        if (dns != null && dns.length > 2) {
                            toUrl = dns[0] + "/" + dns[1] + "/"+ dns[2] + toUrl;
                        }
                    }
                    subContent = HttpClientUtil.reqGet(toUrl,"",null, null, "");
                    System.out.println("#NaverNews.Redirection toURL:"+toUrl);
                    //System.out.println("#NaverNews.Redirection URL result:"+subContent);
                }
            }

            // subLinkUrl에 대한 각 속성 초기값 설정
            jObj = new JsonObject();
            jObj.addProperty("sub_url", subLinkUrl);
            JsonArray jArr = new JsonArray();
            JsonArray dest_fields =  new JsonArray();
            String subContentTxt = "";

            // subContent가 조회되지 않을 경우 failResultArr 에 subUrl 추가, 사유는 null
            if (!"".equals(subContent.trim())) {
                for (ConfPreset ps : reqInfo.getPresetList()) {
                    //System.out.println("#URL check:: result:"+subLinkUrl.contains(ps.getDescriptp().toString())+  "  "+ps.getDescriptp()+"   vs   "+subLinkUrl);

                    // URL 대조
                    if (ps.getDescriptp() != null && subLinkUrl.contains(ps.getDescriptp().toString())) {
                        is_defined = true;

                        if (!ps.getPs_tag().contains("|")) {
                            subContentTxt = JsoupUtil.getTaggedValue(subContent, ps.getPs_tag());
                        } else {
                            String[] psTags = ps.getPs_tag().toString().split("\\|");
                            subContentTxt = JsoupUtil.getTaggedAttr(subContent, psTags[0], psTags[1]);
                        }
                        subContentTxt = CommonUtil.removeTex(subContentTxt);

                        logger.debug("#MLOG NAVER_NEWS getSubPages by reqInfo:" + reqInfo.toString());
                        //System.out.println("#MLOG NAVER_NEWS getSubPage content:"+subContentTxt);

                        subContentTxt = dicService.filterByDicFilterWords(subContentTxt, reqInfo.getTg_id());

                        //System.out.println("#subPages:subContentTxt:"+subContentTxt);
                        if (!subContentTxt.contains("�")) {
                            if (
                                // 2차 시도 min_max 체크
                                    subContentTxt.length() > reqInfo.getContent_min2() && subContentTxt.length() < reqInfo.getContent_max2()
                                    ) {
                                //서브페이지 아이템 ps_tag에 맞추어 array로 수집
                                JsonObject subJobj = new JsonObject();
                                subJobj.addProperty(ps.getDest_field(), subContentTxt);
                                jArr.add(subJobj);
                                dest_fields.add(ps.getDest_field());

                                is_collected = true;
                            }
                        } else {
                            //System.out.println("#SpecialCharactor � is exist! content:"+subContentTxt);
                            is_crashed = true;
                        }
                    }
                } // for END

                if(jArr != null && jArr.size() > 0) {
                    jObj.add("result", jArr);
                    jObj.add("dest_fields", dest_fields);
                    resultArr.add(jObj);
                }

                if(!is_collected) {
                    if (!is_defined) {
                        failResultArr.add("not_defined___" + subLinkUrl);
                    } else if(is_crashed) {
                        //failResultArr.add("crashed___"+subLinkUrl+"___"+subContentTxt.substring(0, (subContentTxt.length() > 100) ? 99 : subContentTxt.length()));
                        failResultArr.add("crashed___"+subLinkUrl+"___"+subContentTxt);
                    } else {
                        failResultArr.add("filtered___"+subLinkUrl);
                    }
                }
            } else {
                failResultArr.add("null___" + subLinkUrl);

            }
        }
        resultObj.remove("resultArr");
        resultObj.remove("failResultArr");

        resultObj.add("resultArr", resultArr);
        resultObj.add("failResultArr", failResultArr);

        return resultObj;
    }

    private JsonObject nullCheckJsonResult(JsonObject resultObject) {
        JsonObject resultObj = null;
        JsonArray resultArr = null;
        JsonArray failResultArr = null;

        if(resultObj == null) resultObj = new JsonObject();

        if (resultObject != null) {
            if (resultObject.get("resultArr") != null) {
                resultArr = (JsonArray) resultObject.get("resultArr");
            } else {
                resultArr = new JsonArray();
            }
            resultObj.add("resultArr", resultArr);

            if (resultObject.get("failResultArr") != null) {
                failResultArr = (JsonArray) resultObject.get("failResultArr");
            } else {
                failResultArr = new JsonArray();
            }
            resultObj.add("failResultArr", failResultArr);
        } else {
            resultObj = new JsonObject();
            resultArr = new JsonArray();
            failResultArr = new JsonArray();
            resultObj.add("resultArr", resultArr);
            resultObj.add("failResultArr", failResultArr);
        }

        return resultObj;
    }

    private synchronized JsonObject getSearchNewsPaging(JsonObject resultObject, ConfTarget reqInfo
            , int pageNo, String stDate, String edDate) throws Exception {
        JsonObject resultObj = nullCheckJsonResult(resultObject);

        if(reqInfo != null && reqInfo.getParam1() != null) {
            JsonArray resultArr = (JsonArray) resultObj.get("resultArr");
            JsonArray failResultArr = (JsonArray) resultObj.get("failResultArr");

            //String reqPageno = Integer.toString(pageNo);
            Map<String, Object> reqparams = new HashMap<String, Object>();
            //reqparams.put("reqPage", "option.page.currentPage");
            reqparams.put("reqPage", "start");
            reqparams.put("reqPageNo", ((pageNo-1)*10) + 1);

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
                //네이버 뉴스 서브 URL 발췌 필요
               // String navernews_url = "";

                String pageContent = HttpClientUtil.reqGet(navernews_url, "", null,reqparams, "");
                //System.out.println("#Navernews getPageContent:"+pageContent);

                resultObj = getSubPages2(pageContent, resultObj, reqInfo);
                System.out.println("#getSearchNewsPaging:result resultObj:"+resultObj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return resultObj;
    }


    @Override
    @Synchronized
    public JsonObject getSearchNews(ConfTarget reqInfo) throws Exception {
        JsonObject resultObj = null;
        JsonArray resultArr2 = new JsonArray();

        //NAVER_NEWS tg_url 세팅 필요
        //if(!reqInfo.getTg_url().equals("NAVER_NEWS")) throw new Exception("Conf_Preset tg_url is not NAVER BLOG!");

        int limitCnt = 0;
        if (reqInfo.getIs_fail() != null && "Y".equals(reqInfo.getIs_fail())) {
            limitCnt = limitCnt + reqInfo.getFail_count2();
        } else {
            limitCnt = limitCnt + reqInfo.getFail_count1();
        }

        JsonArray failResult = new JsonArray();
        int collectCnt = 0;
        int pageNo = 1;
        while (collectCnt < limitCnt && pageNo <= navernews_search_page_limit) {

            resultObj = getSearchNewsPaging(null, reqInfo, pageNo, "", "");

            JsonArray resultArr = null;
            JsonArray failResultArr = null;
            if (resultObj != null) {
                if (resultObj.get("resultArr") != null) resultArr = (JsonArray) resultObj.get("resultArr");
                if (resultObj.get("failResultArr") != null) failResultArr = (JsonArray) resultObj.get("failResultArr");
            }

            if (resultArr != null) {
                System.out.println("#Navernews getSearchNewsPaging "+pageNo+"'s result size:"+resultArr.size());
                for (JsonElement je : resultArr) {
                    if (collectCnt < limitCnt) {
                        resultArr2.add(je);
                        collectCnt ++;
                        System.out.println("#Navernew collectCnt:"+collectCnt+"  /   resultArr2.add:"+je.toString());
                    }
                }
            }

            if (failResultArr != null) {
                for (JsonElement je : failResultArr) {
                    failResult.add(je);
                }
            }

            pageNo++;

            System.out.println("#limitCnt : "+limitCnt+"    /    collectCnt : " + collectCnt);
            System.out.println("#resultArr2: size:"+resultArr2.size()+"   data:"+resultArr2.toString());
            System.out.println("#failResultArr: size:"+failResult.size()+"   data:"+failResult.toString());
        }

        resultObj.add("result", resultArr2);
        resultObj.add("failResultArr", failResult);
        //System.out.println("#getNaverNews::"+result.toString());

        logger.info("#getNaverNews resultObj::" + resultObj.toString());
        return resultObj;
    }
    */
}
