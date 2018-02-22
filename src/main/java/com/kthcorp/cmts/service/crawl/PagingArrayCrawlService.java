package com.kthcorp.cmts.service.crawl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.GoogleTransService;
import com.kthcorp.cmts.util.HttpClientUtil;
import com.kthcorp.cmts.util.JsonUtil;
import com.kthcorp.cmts.util.JsoupUtil;
import groovy.transform.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PagingArrayCrawlService implements PagingArrayCrawlServiceImpl {

    @Autowired
    LinkCrawlService linkCrawlService;
    @Autowired
    GoogleTransService googleTransService;

    @Value("${cmts.collector.navermovie.reply_url}")
    private String navermovie_reply_url;

    @Value("${cmts.collector.navermovie.reply_paging_limit}")
    private Integer navermovie_reply_paging_limit;

    @Value("${cmts.collector.navermovie.reply_limit_count}")
    private Integer navermovie_reply_limit_count;

    @Value("${cmts.collector.daummovie.magazine_url}")
    private String daummovie_magazine_url;

    @Value("${cmts.collector.daummovie.magazine_limit_count}")
    private Integer daummovie_magazine_limit_count;

    @Override
    public JsonObject getReplyItemsByPaging(String prefix, ConfTarget reqInfo, int limitCnt)  throws Exception {
        JsonObject resultObj = new JsonObject();
        JsonArray resultArr2 = new JsonArray();

        if(limitCnt == 0) limitCnt = navermovie_reply_limit_count;
        int pageLimit = navermovie_reply_paging_limit;

        int collectCnt = 0;
        int pageNo = 1;
        while (collectCnt < limitCnt && pageNo <= pageLimit) {
            resultObj = getPaging(prefix, resultObj, reqInfo
                        , navermovie_reply_url, "code", "page", 1, pageNo);

            JsonArray resultArr = null;
            JsonArray failResultArr = null;
            if (resultObj != null) {
                if (resultObj.get("resultArr") != null) resultArr = (JsonArray) resultObj.get("resultArr");
            } else {
                resultObj = new JsonObject();
            }

            if (resultArr != null) {
                System.out.println("#PagingReply:"+prefix+"::getPaging Page:"+pageNo+"'s result size:"+resultArr.size());
                for (JsonElement je : resultArr) {
                    if (collectCnt < limitCnt) {
                        resultArr2.add(je);
                        collectCnt ++;
                        //System.out.println("#PagingReply:"+prefix+"::getPaging collectCnt:"+collectCnt+"  /   resultArr2.add:"+je.toString());
                    }
                }
            }

            pageNo++;

            System.out.println("#limitCnt : "+limitCnt+"    /    collectCnt : " + collectCnt);
            System.out.println("#resultArr2: size:"+resultArr2.size());
        }

        resultObj.add("resultArr", resultArr2);

        return resultObj;
    }

    /*
    @Override
    public JsonObject getMagazineItemsByPaging(String prefix, ConfTarget reqInfo, int limitCnt)  throws Exception {
        JsonObject resultObj = new JsonObject();
        JsonArray resultArr2 = new JsonArray();

        if (limitCnt == 0) limitCnt = daummovie_magazine_limit_count;
        int pageLimit = daummovie_magazine_paging_limit;

        int collectCnt = 0;
        int pageNo = 1;
        while (collectCnt < limitCnt && pageNo <= pageLimit) {
                //http://movie.daum.net/search/main?searchText=
                // %ED%86%A0%EB%A5%B4%20:%20%EB%9D%BC%EA%B7%B8%EB%82%98%EB%A1%9C%ED%81%AC
                // &returnUrl=http://movie.daum.net/moviedb/main?movieId=93694#searchType=magazine&page=1

                String addParams = "?searchText="+ URLEncoder.encode(reqInfo.getParam1(), "UTF-8")
                                    + "&returnUrl=http://movie.daum.net/moviedb/main?movieId=";
                System.out.println("#addParams:"+addParams.toString());

                resultObj = getPaging(prefix, resultObj, reqInfo
                        , daummovie_magazine_url, addParams, "page", 1, pageNo);


            JsonArray resultArr = null;
            JsonArray failResultArr = null;
            if (resultObj != null) {
                if (resultObj.get("resultArr") != null) resultArr = (JsonArray) resultObj.get("resultArr");
            } else {
                resultObj = new JsonObject();
            }

            if (resultArr != null) {
                System.out.println("#PagingReply:"+prefix+"::getPaging Page:"+pageNo+"'s result size:"+resultArr.size());
                for (JsonElement je : resultArr) {
                    if (collectCnt < limitCnt) {
                        resultArr2.add(je);
                        collectCnt ++;
                        //System.out.println("#PagingReply:"+prefix+"::getPaging collectCnt:"+collectCnt+"  /   resultArr2.add:"+je.toString());
                    }
                }
            }

            pageNo++;

            System.out.println("#limitCnt : "+limitCnt+"    /    collectCnt : " + collectCnt);
            System.out.println("#resultArr2: size:"+resultArr2.size());
        }

        resultObj.add("resultArr", resultArr2);

        return resultObj;
    }
    */

    private JsonObject getPaging(String prefix, JsonObject resultObject, ConfTarget reqInfo
            , String searchUrl, String paramQuery, String paramPaging, int pagingDegree, int pageNo
            ) {

        //JsonObject resultObj = nullCheckJsonResult(resultObject);
        JsonObject resultObj = null;

        try {
            Map<String, Object> reqparams = new HashMap<String, Object>();
            //reqparams.put("reqPage", "option.page.currentPage");
            reqparams.put("reqPage", paramPaging);
            reqparams.put("reqPageNo", ((pageNo - 1) * pagingDegree) + 1);

            String reqQuery = "";
            if (!"".equals(reqInfo.getParam1())) reqQuery = URLEncoder.encode(reqInfo.getParam1().toString(), "UTF-8");
            //reqparams.put("reqQuery", "option.keyword");
            reqparams.put("reqQuery", paramQuery);
            reqparams.put("reqQueryString", reqQuery);

            String pageContent = HttpClientUtil.reqGet(navermovie_reply_url, "", null, reqparams, "");
            //System.out.println("#pageContent:"+pageContent);

            resultObj = getSubPages2(prefix, pageContent, resultObject, reqInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultObj;
    }


    private JsonObject getSubPages2(String prefix, String pageContent, JsonObject resultObject, ConfTarget reqInfo) throws Exception {
        //JsonObject resultObj = nullCheckJsonResult(resultObject);
        JsonObject resultObj = new JsonObject();

        JsonArray dest_fields = new JsonArray();
        for (ConfPreset ps : reqInfo.getPresetList()) {
            String descriptp = (ps.getDescriptp() != null ? ps.getDescriptp() : "");
            // reply가 아닌 경우 아이템 수집
            if (descriptp.contains("reply")) {
                List<String> allContentArray = null;
                if (resultObject != null && resultObject.get(ps.getDest_field()) != null) {
                    JsonArray resContentArray = (JsonArray) resultObject.get(ps.getDest_field());
                    allContentArray = JsonUtil.convertJsonArrayToList(resContentArray);
                } else {
                    allContentArray = new ArrayList<String>();
                }

                System.out.println("#resultObj.get(ps.getDest_field:"+allContentArray.toString());

                List<String> contentArray = JsoupUtil.getTaggedValueArray(pageContent, ps.getPs_tag());
                if (contentArray != null) {
                    for(String s : contentArray) {
                        allContentArray.add(s);
                    }
                }

                resultObj.add(ps.getDest_field(), JsonUtil.convertListToJsonArray(allContentArray));
                dest_fields.add(ps.getDest_field());
                //resultObj.add("dest_fields", dest_fields);
            }
            //resultObj.add("contents", resultArr);
            resultObj.add("dest_fields", dest_fields);
            System.out.println("#replyObject process:"+resultObj.toString());
        }

        //resultArr.add(pageContent);
        //resultObj.add("resultArr", resultArr);

        return resultObj;
    }


    private JsonObject nullCheckJsonResult(JsonObject resultObject) {
        JsonObject resultObj = null;
        JsonArray resultArr = null;
        JsonArray failResultArr = null;
        //JsonObject replyObj = null;

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

            //if (resultObject.get("reply") != null) {
            //    replyObj = (JsonObject) resultObject.get("reply");
            //} else {
            //    replyObj = new JsonObject();
            //}
            //resultObj.add("reply", replyObj);
        } else {
            resultObj = new JsonObject();
            resultArr = new JsonArray();
            failResultArr = new JsonArray();
            //replyObj = new JsonObject();
            resultObj.add("resultArr", resultArr);
            resultObj.add("failResultArr", failResultArr);
            //resultObj.add("reply", resultObj);
        }

        return resultObj;
    }
}
