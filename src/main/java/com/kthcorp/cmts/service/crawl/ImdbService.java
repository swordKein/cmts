package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.GoogleSearchService;
import com.kthcorp.cmts.service.GoogleTransService;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.HttpClientUtil;
import com.kthcorp.cmts.util.JsoupUtil;
import groovy.transform.Synchronized;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ImdbService implements ImdbServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(ImdbService.class);

    @Autowired
    GoogleSearchService googleSearch;
    @Autowired
    GoogleTransService googleTrans;

    private String getPlot(String reqStr) {
        String result = "";
        Document doc = Jsoup.parse(reqStr,"UTF-8");
        Elements summaryElms = doc.select("#plot-summaries-content");
        if (summaryElms != null && summaryElms.size() > 0) {
            Element summaryElm = summaryElms.get(0);
            if (summaryElm != null && summaryElm.text() != null) {
                System.out.println(">> items::" + summaryElm.text().toString());
                result = summaryElm.text().toString();
            }
        }
        return result;
    }

    /* 주어진 ConfTarget & List<ConfPreset> 기준에 맞추어 해당 태그 대상 데이터 수집 */
    private JsonObject getSubItems(ConfTarget reqInfo, JsonObject resultObj) throws Exception {
        if(resultObj == null) resultObj = new JsonObject();
        JsonArray resultArr = new JsonArray();

        if (reqInfo != null && reqInfo.getPresetList() != null) {
            String pageContent = HttpClientUtil.reqGet(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null,null, "bypass");

            JsonArray dest_fields = new JsonArray();
            for (ConfPreset ps : reqInfo.getPresetList()) {
                String contentStr = "";
                String transContentStr = "";

                JsonObject res1 = new JsonObject();
                contentStr = JsoupUtil.getTaggedValue(pageContent, ps.getPs_tag());

                // ps_type이 trans일 경우 구글 번역하여 필드명으로 저장한다. 원문은 orig_필드명으로 저장한다.
                if ("trans".equals(ps.getPs_type())) {
                    try {
                        transContentStr = googleTrans.getTransKoreanResult(contentStr);
                        //transContentStr = googleTrans.getTransResult(contentStr,"en","ko");
                    } catch (Exception e) {
                        e.printStackTrace();
                        transContentStr = "TRANS_FAIL";
                    } finally {
                        if (transContentStr.length() < 5) { transContentStr = "TRANS_FAIL"; }
                    }

                    res1.addProperty(ps.getDest_field(), transContentStr);
                    res1.addProperty("origin_"+ps.getDest_field(), contentStr);
                    dest_fields.add(ps.getDest_field());

                    //res1.addProperty("orig_"+ps.getDest_field(), contentStr);
                    //dest_fields.add("orig_"+ps.getDest_field());
                } else {
                    res1.addProperty(ps.getDest_field(), contentStr);
                    dest_fields.add(ps.getDest_field());
                }
                resultArr.add(res1);
            }
            resultObj.add("contents", resultArr);
            resultObj.add("dest_fields", dest_fields);
        }
        return resultObj;
    }

    @Override
    @Synchronized
    public JsonObject getMovie(ConfTarget reqInfo) throws Exception {
        logger.info("imdbService.getMovie by title:"+reqInfo.getParam1());

        JsonObject resultObj = new JsonObject();
        if (reqInfo != null) {
            JsonArray jArr = googleSearch.getSearchItems3(reqInfo);

            if (jArr != null && jArr.size() > 0) {
                //System.out.println(">> items::" + jObj.toString());
                JsonObject obj = (JsonObject) jArr.get(0);
                System.out.println("#jobj.get(0)::"+obj.toString());

                String titleGoogle = obj.get("title").toString();
                resultObj.addProperty("title_google", CommonUtil.removeTex(titleGoogle));

                String sUrl = obj.get("link").toString();
                sUrl = CommonUtil.removeTex(sUrl);
                //sUrl = sUrl.replaceAll("//*", "/");

                String reqUrl = sUrl;
                reqInfo.setTg_url(reqUrl);

                /* 설정에 따라 구글 검색결과에서 취득된 url을 검색 후 취득한 후 대상 tag를 수집한다 */
                resultObj = getSubItems(reqInfo, resultObj);
                //resultObj.add("result", result);
                //resultObj.addProperty("rt_code", "OK");
                //resultObj.addProperty("rt_msg", "SUCCESS");
            }

            logger.info("#getMovie resultObj::" + resultObj.toString());
        }
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
                String resultStr = HttpClientUtil.reqGet(reqUrl, addUrl, null, null,"bypass");

                String plot = getPlot(resultStr);
                result.addProperty("plot", plot);
                String transPlot = googleTrans.getTransKoreanResult(plot);
                result.addProperty("transPlot", transPlot);
                result.addProperty("originPlot", plot);

            }
        } catch (Exception pe) {
            pe.printStackTrace();
        }

        System.out.println("#getMovie::"+result.toString());
        return result;
    }
}
