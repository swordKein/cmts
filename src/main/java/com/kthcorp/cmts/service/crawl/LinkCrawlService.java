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
import com.kthcorp.cmts.util.StringUtil;
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
public class LinkCrawlService implements LinkCrawlServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(LinkCrawlService.class);

    @Autowired
    GoogleSearchService googleSearch;
    @Autowired
    GoogleTransService googleTrans;
    @Autowired
    private DicService dicService;

    /* 주어진 ConfTarget & List<ConfPreset> 기준에 맞추어 해당 태그 대상 데이터 수집
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
    */

    /* 블로그 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장
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
    */

    // 블로그 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 sub_url을 발췌
    public List<String> getSubPagesUrlArray(String postUrl, List<String> reqArr) {
        List<String> result = new ArrayList<String>();
        if(reqArr != null) {
            for(String s : reqArr) {
                //System.out.println("#getSubPagesUrl. reqUrl:"+s);
                String blogId = "";
                String logNo = "";
                String subUrl = "";

                if(s.contains("blog.naver.com")) {
                        String[] s2 = s.split("logNo=");
                        if (s2 != null && s2[1] != null) {
                            logNo = s2[1];
                        }
                        String[] t1 = s.split("\\?");
                        if (t1 != null && t1[0] != null) {
                            String[] t2 = t1[0].split("/");
//                        for(String t : t2) {
//                            System.out.print(" ##"+t);
//                        }
                            if (t2 != null && t2[3] != null) {
                                blogId = t2[3];
                            }
                        }
                        subUrl = postUrl + blogId + "&logNo=" + logNo;
                        //System.out.println("#subUrl. blog.naver.com:" + subUrl);
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
                    subUrl = postUrl + blogId + "&logNo="+logNo;
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

    /* 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장
    private JsonArray getSubPages(String pageContent, JsonArray resultArr, ConfTarget reqInfo) throws Exception {

        //List<String> blogIdList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=blogId]", "value");
        //List<String> blogLognoList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=logNo]", "value");

        List<String> searchedBlogList = JsoupUtil.getTaggedLinkArray(pageContent, ".news .type01 li dt a", "href");
        System.out.println("#search.getSubPages:"+searchedBlogList.toString());

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
                    logger.debug("#MLOG getSubPages by reqInfo:" + reqInfo.toString());
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
    */

    private Map<String, Object> getSubContentByCharset(String subLinkUrl, List<ConfPreset> presetList) {
        Map<String,Object> subContentMap = null;

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
                    //System.out.println("#DaumNews  surl:"+subLinkUrl+" // eurl:"+ps.getDescriptp()+"   // setDestCharset:"+dest_charset.toString());
                }
            }

            subContentMap = HttpClientUtil.reqGetHtml(subLinkUrl, "", dest_charset,null, "");

            //System.out.println("#subContentMap:" + subContentMap.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("#result for id>"+subLinkUrl+" subContent:"+subContent.toString());
        return subContentMap;
    }

    /* 검색 페이지에서 각 서브페이지 url을 발췌하여 각 페이지의 해당 테그 내용을 발췌 dest_field 로 저장 */
    /* 서브페이지 수집 실패 이력도 리턴 */
    @Override
    public JsonObject getSubPages2(String prefix, String pageContent, JsonObject resultObject, ConfTarget reqInfo, String tags, String attrs
            , String postUrl) throws Exception {
        //List<String> blogIdList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=blogId]", "value");
        //List<String> blogLognoList = JsoupUtil.getTaggedLinkArray(pageContent, ".list_type_1 input[name=logNo]", "value");
        JsonObject resultObj = new JsonObject();
        JsonArray resultArr = (JsonArray) resultObject.get("resultArr");
        JsonArray failResultArr = (JsonArray) resultObject.get("failResultArr");

        //List<String> searchedBlogList = JsoupUtil.getTaggedLinkArray(pageContent, "#clusterResultUL li .wrap_tit a", "href");

        List<String> searchedBlogList = JsoupUtil.getTaggedLinkArray(pageContent, tags, attrs);
        System.out.println("#LinkCrawlService:"+prefix+"::search.getSubPages2:"+searchedBlogList.toString());

        // 검색결과 배열로 subUrl 배열 취득
        List<String> subUrlArray = getSubPagesUrlArray(postUrl, searchedBlogList);

        System.out.println("#LinkCrawlService:"+prefix+"::search.getSubPagesUrlArray:"+subUrlArray.toString());

        Map<String, Object> subContentMap = null;
        String subContent = "";
        String subContentUri = "";
        String subContentCharset = "";

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
            System.out.println("#LinkCrawlService: getSubPages2:"+prefix+"::SubPageList url:#" + subLinkUrl);

            /* ConfPreset 리스트에서 dest_charset 지정여부를 파악하여 서브페이지 컨텐츠 http로 가져온다 */
            subContentMap = getSubContentByCharset(subLinkUrl, presetList);
            if (subContentMap != null) {
                subContent = subContentMap.get("resultStr").toString();
                subContentUri = subContentMap.get("resultUri").toString();
                subContentCharset = subContentMap.get("resultCharset").toString();
                System.out.println("#LinkCrawlService:"+prefix+"::SubPageList result-charset:"+subContentCharset);
            }

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
                    subContentMap = HttpClientUtil.reqGetHtml(toUrl,"",null, null, "");
                    if (subContentMap != null) {
                        subContent = subContentMap.get("resultStr").toString();
                        subContentUri = subContentMap.get("resultUri").toString();
                        subContentCharset = subContentMap.get("resultCharset").toString();
                    }
                    System.out.println("#LinkCrawlService:"+prefix+"::subPage.Redirection toURL:"+toUrl);
                    //System.out.println("#DaumNews.Redirection URL result:"+subContent);
                }
            }

            // subPage의 내용이 <frame srcf="/_blog........."</frame> 일 경우 예외 조항 처리 for 다음 블로그
            if (subContent.trim().length() < 2000) {
                if (subContent.trim().contains("<frame src") && subContent.contains("frame>")) {
                    String toUrl = subContent.substring(subContent.indexOf("<frame src")+12, subContent.indexOf("</frame>"));
                    toUrl = toUrl.substring(0, toUrl.indexOf("\""));
                    toUrl = toUrl.replace("&amp;", "&");

                    //String toUrl = subContent.trim().replace("<script>", "");

                    toUrl = toUrl.trim();
                    if(toUrl.startsWith("/")) {
                        String dns[] = subLinkUrl.split("/");
                        if (dns != null && dns.length > 2) {
                            toUrl = dns[0] + "/" + dns[1] + "/"+ dns[2] + toUrl;
                        }
                    }

                    toUrl = toUrl.replace("/_blog/BlogTypeMain.do", "/_blog/hdn/ArticleContentsView.do");
                    toUrl = toUrl.replace("/_blog/BlogTypeView.do", "/_blog/hdn/ArticleContentsView.do");
                    toUrl = toUrl.replace("&admin=","&looping=0&longOpen=");
                    subContentUri = toUrl;
                    //subContent = " daumblog ";

                    System.out.println("#LinkCrawlService:"+prefix+"::subPage.Redirection toURL:"+toUrl);

                    subContentMap = HttpClientUtil.reqGetHtml(toUrl,"",null, null, "");
                    if (subContentMap != null) {
                        subContent = subContentMap.get("resultStr").toString();
                        subContentUri = subContentMap.get("resultUri").toString();
                        subContentCharset = subContentMap.get("resultCharset").toString();
                    }
                    //System.out.println("#Daumblog.Redirection URL result:"+subContent);
                }
            }

            // subLinkUrl에 대한 각 속성 초기값 설정
            jObj = new JsonObject();
            //jObj.addProperty("sub_url", subLinkUrl);
            jObj.addProperty("sub_url", subContentUri);
            JsonArray jArr = new JsonArray();
            JsonArray dest_fields =  new JsonArray();
            String subContentTxt = "";

            //System.out.println("#LinkCrawlService:"+prefix+"::reqInfo.getPresetList():"+reqInfo.getPresetList().toString());

            // subContentUri 의 문자열을 갖는 psList를 별도로 생성한다
            List<ConfPreset> subPsList = new ArrayList<ConfPreset>();
            for (ConfPreset p : reqInfo.getPresetList()) {
                if (!prefix.endsWith(("_BLOG"))) {
                    if (p.getDescriptp() != null && subContentUri.contains(p.getDescriptp())) {
                        subPsList.add(p);
                    }
                } else {
                    subPsList.add(p);
                }
            }
            //System.out.println("#LinkCrawlService:"+prefix+"::subPsList:"+subPsList.toString());

            // subContent가 조회되지 않을 경우 failResultArr 에 subUrl 추가, 사유는 null
            if (!"".equals(subContent.trim())) {
                for (ConfPreset ps : subPsList) {
                    System.out.println("#subPsList:"+subPsList.toString());

                    String dp = (ps.getDescriptp().toString() != null) ? ps.getDescriptp().toString() : "";
                    System.out.println("#URL check:: result:"
                            + subLinkUrl.contains(dp)+  "  "
                            + ps.getDescriptp()
                            + "   vs   "+subLinkUrl);

                    // URL 대조
                    //if (ps.getDescriptp() != null && subLinkUrl.contains(ps.getDescriptp().toString())) {
                    //if (ps.getDescriptp() != null && subContentUri.contains(ps.getDescriptp().toString())) {
                        is_defined = true;

                        System.out.println("#LinkCrawlService:"+prefix+"::subContent-charset:"+subContentCharset+"   vs  ps.Dest_charset:"+ps.getDest_charset());

                        // charset이 다르면 페이지를 한번 더 읽어옴
                        if(ps.getDest_charset() != null && !"".equals(ps.getDest_charset())) {
                            if (!subContentCharset.equals(ps.getDest_charset())) {
                                subContentMap = getSubContentByCharset(subContentUri, presetList);
                                if (subContentMap != null) {
                                    subContent = subContentMap.get("resultStr").toString();
                                    subContentUri = subContentMap.get("resultUri").toString();
                                    subContentCharset = subContentMap.get("resultCharset").toString();
                                    System.out.println("#LinkCrawlService:"+prefix+"::SubPageList result-charset2:" + subContentCharset);
                                }
                            }
                        }

                        if (!ps.getPs_tag().contains("|")) {
                            subContentTxt = JsoupUtil.getTaggedValueAll(subContent, ps.getPs_tag());
                        } else {
                            String[] psTags = ps.getPs_tag().toString().split("\\|");
                            subContentTxt = JsoupUtil.getTaggedAttr(subContent, psTags[0], psTags[1]);
                        }
                        subContentTxt = CommonUtil.removeTex(subContentTxt);
                        subContentTxt = CommonUtil.removeAllSpec(subContentTxt);

                        if (reqInfo != null) {
                            logger.debug("#LinkCrawlService:" + prefix + ":: search getSubPages by reqTG_INFO:"
                                    + reqInfo.getTg_id()+"/"+reqInfo.getTg_url()+"/"+reqInfo.getParam1()+"/"+reqInfo.getRegdate());
                            for (ConfPreset p : reqInfo.getPresetList()) {
                                logger.debug("#    LinkCrawlService:" + prefix + ":: search getSubPages by reqTG_INFO - Preset:"
                                        + p.getPs_id()+"/"+p.getPs_tag()+"/"+p.getPs_type()+"/"+p.getDest_charset()+"/"+p.getDescriptp());
                            }
                        }
                        //System.out.println("#MLOG DAUM_BLOG getSubPage content:"+subContentTxt);

                        subContentTxt = dicService.filterByDicFilterWords(subContentTxt, reqInfo.getTg_id());

                        System.out.println("#MLOG "+prefix+" getSubPage after filter content:"+subContentTxt);

                        //System.out.println("#subPages:subContentTxt:"+subContentTxt);
                        if (!"".equals(subContentTxt)) {
                            if (StringUtil.countOccurrences(subContentTxt, '�') < 10) {
                                // 2차 시도 min_max 체크
                                System.out.println("#MLOG "+prefix+" getSubPage is_limit:"+reqInfo.getIs_limit()+"/min:"
                                        +reqInfo.getContent_min2()+"/max:"+reqInfo.getContent_max2());
                                if (
                                        (reqInfo.getIs_limit() != null && "Y".equals(reqInfo.getIs_limit())
                                          && ( subContentTxt.length() > reqInfo.getContent_min2()
                                                && subContentTxt.length() < reqInfo.getContent_max2() ))
                                        || (reqInfo.getIs_limit() == null || "N".equals(reqInfo.getIs_limit()))
                                 ) {
                                    //서브페이지 아이템 ps_tag에 맞추어 array로 수집
                                    JsonObject subJobj = new JsonObject();
                                    subJobj.addProperty(ps.getDest_field(), subContentTxt);
                                    jArr.add(subJobj);
                                    dest_fields.add(ps.getDest_field());

                                    is_collected = true;
                                    break;
                                } else {
                                    is_filtered = true;
                                    System.out.println("#MLOG "+prefix+" getSubPage fail! cause by length filtered. length:"+subContentTxt.length());
                                }
                            } else {
                                //System.out.println("#LinkCrawlService:"+prefix+"::SpecialCharactor � is exist! content:"+subContentTxt);
                                is_crashed = true;
                                System.out.println("#MLOG "+prefix+" getSubPage fail! cause by crashed.");
                            }
                        } else {
                            is_filtered = true;
                            System.out.println("#MLOG "+prefix+" getSubPage fail! cause by null.");
                        }
                    //}
                } // for END

                if(jArr != null && jArr.size() > 0) {
                    jObj.add("contents", jArr);
                    jObj.add("dest_fields", dest_fields);
                    resultArr.add(jObj);
                } else {
                    if (!is_defined) {
                        failResultArr.add("not_defined___" + subContentUri);
                    } else if (is_crashed) {
                        //failResultArr.add("crashed___"+subLinkUrl+"___"+subContentTxt.substring(0, (subContentTxt.length() > 100) ? 99 : subContentTxt.length()));
                        failResultArr.add("crashed___" + subContentUri + "___" + subContentTxt);
                    } else if (is_filtered) {
                        failResultArr.add("filtered___" + subContentUri);
                    }
                }
            } else {
                failResultArr.add("null___" + subContentUri);

            }
        }
        resultObj.remove("resultArr");
        resultObj.remove("failResultArr");

        resultObj.add("resultArr", resultArr);
        resultObj.add("failResultArr", failResultArr);
        resultObj.addProperty("prefix", prefix);

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

    private synchronized JsonObject getSearchNewsPaging(String prefix, JsonObject resultObject, ConfTarget reqInfo
            , String searchUrl, String paramQuery, String paramPaging, int pagingDegree, int pageNo, String stDate, String edDate, String tags, String attrs
            , String postUrl) throws Exception {
        JsonObject resultObj = nullCheckJsonResult(resultObject);

        if(reqInfo != null && reqInfo.getParam1() != null) {
            JsonArray resultArr = (JsonArray) resultObj.get("resultArr");
            JsonArray failResultArr = (JsonArray) resultObj.get("failResultArr");

            //String reqPageno = Integer.toString(pageNo);
            Map<String, Object> reqparams = new HashMap<String, Object>();
            //reqparams.put("reqPage", "option.page.currentPage");
            reqparams.put("reqPage", paramPaging);
            reqparams.put("reqPageNo", ((pageNo-1)*pagingDegree) + 1);

            String reqQuery = "";
            if (!"".equals(reqInfo.getParam1())) reqQuery = URLEncoder.encode(reqInfo.getParam1().toString(), "UTF-8");
            //reqparams.put("reqQuery", "option.keyword");
            reqparams.put("reqQuery", paramQuery);
            reqparams.put("reqQueryString", reqQuery);

            /*
            if (!"".equals(stDate)) {
                reqparams.put("reqSt", "option.startDate");
                reqparams.put("reqStDate", stDate);
            }
            if (!"".equals(edDate)) {
                reqparams.put("reqEd", "option.endDate");
                reqparams.put("reqEdDate", edDate);
            }
            */
            try {
               // String navernews_url = "";

                //String pageContent = HttpClientUtil.reqGet(daumnews_url, "", null,reqparams, "");
                String pageContent = HttpClientUtil.reqGet(searchUrl, "", null,reqparams, "");
                //System.out.println("#SearchUrl getPageContent:"+pageContent);

                resultObj = getSubPages2(prefix, pageContent, resultObj, reqInfo, tags, attrs, postUrl);
                System.out.println("#LinkCrawlService:"+prefix+"::getSearch Paging:result resultObj:"+resultObj.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return resultObj;
    }


    @Override
    @Synchronized
    public JsonObject getSearchAndSubPages(String prefix, ConfTarget reqInfo, int pageLimit
            , String searchUrl, String paramQuery, String paramPaging, int pagingDegree, String tags, String attrs
            , String postUrl) throws Exception {
        JsonObject resultObj = null;
        JsonArray resultArr2 = new JsonArray();

        // tg_url 세팅 필요
        //if(!reqInfo.getTg_url().equals("DAUM_NEWS")) throw new Exception("Conf_Preset tg_url is not NAVER BLOG!");

        int limitCnt = 0;
        if (reqInfo.getIs_fail() != null && "Y".equals(reqInfo.getIs_fail())) {
            limitCnt = limitCnt + reqInfo.getFail_count2();
        } else {
            limitCnt = limitCnt + reqInfo.getFail_count1();
        }

        JsonArray failResult = new JsonArray();
        int collectCnt = 0;
        int pageNo = 1;
        while (collectCnt < limitCnt && pageNo <= pageLimit) {

            resultObj = getSearchNewsPaging(prefix, null, reqInfo, searchUrl, paramQuery, paramPaging
                    , pagingDegree, pageNo,"", "", tags, attrs, postUrl);

            JsonArray resultArr = null;
            JsonArray failResultArr = null;
            if (resultObj != null) {
                if (resultObj.get("resultArr") != null) resultArr = (JsonArray) resultObj.get("resultArr");
                if (resultObj.get("failResultArr") != null) failResultArr = (JsonArray) resultObj.get("failResultArr");
            }

            if (resultArr != null) {
                System.out.println("#LinkCrawlService:"+prefix+"::getSearchAndSubPages getSearch Paging "+pageNo+"'s result size:"+resultArr.size());
                for (JsonElement je : resultArr) {
                    if (collectCnt < limitCnt) {
                        resultArr2.add(je);
                        collectCnt ++;
                        //System.out.println("#LinkCrawlService:"+prefix+"::getSearchAndSubPages collectCnt:"+collectCnt+"  /   resultArr2.add:"+je.toString());
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
            System.out.println("#resultArr2: size:"+resultArr2.size());
            System.out.println("#failResultArr: size:"+failResult.size());
        }

        if(resultObj != null) {
            if (resultObj.get("result") != null) resultObj.remove("result");
            if (resultObj.get("failResultArr") != null) resultObj.remove("failResultArr");
        }
        resultObj.add("result", resultArr2);
        resultObj.addProperty("collectCnt",collectCnt);
        resultObj.add("failResultArr", failResult);
        //System.out.println("#LinkCrawlService:"+prefix+"::getSearchUrl::"+result.toString());

        //logger.info("#LinkCrawlService:"+prefix+"::getSearchAndSubPages resultObj::" + resultObj.toString());
        return resultObj;
    }

}
