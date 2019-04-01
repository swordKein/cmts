package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.GoogleTransService;
import com.kthcorp.cmts.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OneCrawlService implements OneCrawlServiceImpl {
    @Autowired
    GoogleTransService googleTrans;


    private String getAwardOnePage(ConfTarget reqInfo, JsonObject resultObj, String descriptp_condition) throws Exception {
        String pageContent = "";

        JsonArray dest_fields = null;
        if(resultObj == null) {
            resultObj = new JsonObject();
        }

        dest_fields = (resultObj != null && resultObj.get("dest_fields") != null) ? (JsonArray) resultObj.get("dest_fields") : new JsonArray();

        if (reqInfo != null && reqInfo.getPresetList() != null) {
            //String pageContent = HttpClientUtil.reqGet(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null,null, "bypass");
            Map<String, Object> resultMap = HttpClientUtil.reqGetHtml(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null, null, "bypass");

            //String pageContent = "";
            String pageUri = "";
            if (resultMap != null) {
                if (resultMap.get("resultStr") != null) {
                    pageContent = resultMap.get("resultStr").toString();
                    //System.out.println("#resultStr:"+pageContent);
                    //resultObj.addProperty("pageContent", pageContent);
                }
                if (resultMap.get("resultUri") != null) {
                    pageUri = resultMap.get("resultUri").toString();
                    resultObj.addProperty("pageUri", pageUri);
                }
                //String title_movie = JsoupUtil.getTaggedValue(pageContent, ".tit_movie");
                //resultObj.addProperty("title_movie", title_movie);
            }
            for (ConfPreset ps : reqInfo.getPresetList()) {
                String descriptp = (ps.getDescriptp() != null ? ps.getDescriptp() : "");
                if (descriptp.contains(descriptp_condition)) {

                    String transContentStr = "";

                    //JsonObject res1 = new JsonObject();
                    //contentStr = JsoupUtil.getTaggedValueAll(pageContent, ps.getPs_tag());
                }
            }
        }
        return pageContent;
    }

    private List<String> getParsedAwards(List<String> awardList, String reqHtml) {
        if (!"".equals(reqHtml.trim())) {
            if (awardList == null) {
                awardList = new ArrayList();
            }

            //System.out.println("#reqHtml :: "+reqHtml);

            String contentHtml = JsoupUtil.getTaggedValueAllHtml(reqHtml, ".main_detail");

            Document doc = Jsoup.parse(contentHtml, "UTF-8");
            Elements summaryElms = doc.select(".main_detail");
            if (summaryElms != null && summaryElms.size() > 0) {

                for (Element elm : summaryElms) {
                    Elements tit_awards = elm.select(".tit_movie");
                    //String tit_award = tit_awards.text().toString();

                    for (int i=0; i<tit_awards.size(); i++) {
                        Elements info_prod = elm.select(".info_produce");
                        Elements acts = null;

                        String act = "";
                        acts = info_prod.eq(i).select(".list_produce").eq(0).select("dl").select("dt");
                        act = acts.text().toString();
                        act = act.trim();
                        //System.out.println("#acts :: " + acts.toString());

                        //System.out.println("#award compare :: " + tit_awards.get(i) + " :: " + act);

                        String tit_aw = tit_awards.get(i).text().toString().trim();
                        tit_aw = tit_aw.replace(",", ".");

                        if ("수상".equals(act)) {
                            awardList.add(tit_aw);
                        }

                        //System.out.println("#awardList:" + awardList.toString());
                    }
                }
            }
        }
        return awardList;
    }

    private int getPagingCnt(String pageContent) {
        Document doc2 = Jsoup.parse(pageContent, "UTF-8");
        Elements summaryElms2 = doc2.select(".paging_popcorn");
        //System.out.println("#summaryElms2 :: "+summaryElms2.toString());

        int cnt = 0;
        if (summaryElms2 != null && summaryElms2.size() > 0) {
            for (Element elm2 : summaryElms2) {
                Elements paging = elm2.select(".link_page");
                String pagingStrs = paging.text().toString();

                if(!"".equals(pagingStrs)) {
                    String pages[] = pagingStrs.split(" ");
                    cnt = pages.length;
                }
                System.out.println("#paging length:"+cnt);
            }
        }
        return cnt;
    }

    /* 주어진 ConfTarget & List<ConfPreset> 기준에 맞추어 해당 태그 대상 데이터 수집 */
    /* ImdbService.getSubItems 에서 파생 */
    @Override
    public JsonObject getSubItems(ConfTarget reqInfo, JsonObject resultObj, String descriptp_condition) throws Exception {

        JsonArray dest_fields = null;
        if(resultObj == null) {
            resultObj = new JsonObject();
        }

        dest_fields = (resultObj != null && resultObj.get("dest_fields") != null) ? (JsonArray) resultObj.get("dest_fields") : new JsonArray();

        if (reqInfo != null && reqInfo.getPresetList() != null) {
            //String pageContent = HttpClientUtil.reqGet(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null,null, "bypass");
            Map<String, Object> resultMap = HttpClientUtil.reqGetHtml(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null,null, "bypass");

            String pageContent = "";
            String pageUri = "";
            if (resultMap != null) {
                if (resultMap.get("resultStr") != null) {
                    pageContent = resultMap.get("resultStr").toString();
                    //System.out.println("#resultStr:"+pageContent);
                    //resultObj.addProperty("pageContent", pageContent);
                }
                if (resultMap.get("resultUri") != null) {
                    pageUri = resultMap.get("resultUri").toString();
                    resultObj.addProperty("pageUri", pageUri);
                }
                //String title_movie = JsoupUtil.getTaggedValue(pageContent, ".tit_movie");
                //resultObj.addProperty("title_movie", title_movie);
                if (reqInfo.getMovietitle() != null) resultObj.addProperty("title", reqInfo.getMovietitle());
                if (reqInfo.getMovieyear() != null) resultObj.addProperty("year", reqInfo.getMovieyear());
            }

            for (ConfPreset ps : reqInfo.getPresetList()) {
                String descriptp = (ps.getDescriptp() != null ? ps.getDescriptp() : "");
                if(descriptp.contains(descriptp_condition)) {
                    String contentStr = "";
                    String transContentStr = "";

                    //JsonObject res1 = new JsonObject();
                    contentStr = JsoupUtil.getTaggedValueAll(pageContent, ps.getPs_tag());
                    //System.out.println("#contentStr:"+contentStr);

                    // ps_type이 trans일 경우 구글 번역하여 필드명으로 저장한다. 원문은 orig_필드명으로 저장한다.
                    if ("trans".equals(ps.getPs_type())) {
                        transContentStr = googleTrans.getTransKoreanResult(contentStr);
                        resultObj.addProperty(ps.getDest_field(), transContentStr);
                        dest_fields.add(ps.getDest_field());

                        //res1.addProperty("orig_"+ps.getDest_field(), contentStr);
                        //dest_fields.add("orig_"+ps.getDest_field());
                    } else {
                        // 다음 영화 수상정보는 html 그대로 저장
                        // 다음 영화 수상정보는 페이징, 수상인 경우만 취득
                        List<String> awardList = new ArrayList();
                        JsonArray awardArr = new JsonArray(0);

                        if(ps.getDescriptp().contains("award")) {
                            String contentHtml = JsoupUtil.getTaggedValueAllHtml(pageContent, ps.getPs_tag());

                            /* 수상정보 수집조건 변경 19.03.27 by jaeyeon.hwang */
                            //System.out.println("#award :: html :: "+contentHtml);
                            awardList = this.getParsedAwards(awardList, contentHtml);
                            //System.out.println("#awardList 1st::"+awardList.toString());

                            // 페이징 적용
                            int cnt = this.getPagingCnt(pageContent);
                            String pageStr = "&page=";
                            for (int i=2; i <= cnt; i++) {
                                String pageno = String.valueOf(i);
                                reqInfo.setTg_url_param1("&page="+pageno);
                                String pageHtml = this.getAwardOnePage(reqInfo, resultObj, descriptp_condition);
                                if (!"".equals(pageHtml)) {
                                    awardList = this.getParsedAwards(awardList, pageHtml);
                                }
                            }

                            System.out.println("#awardList END::"+awardList.toString()+" : awardList.size::"+awardList.size());
                            Set<String> awardSet = null;
                            if (awardList != null && awardList.size() > 0) {
                                for(int i=0; i<awardList.size(); i++) {
                                    awardSet = StringUtil.prcAwardsStr(awardSet, awardList.get(i));
                                    //System.out.println("#awardSet processing:"+awardSet.toString());
                                }
                            } else {
                                awardSet = StringUtil.prcAwardsStr(awardSet, "");
                            }
                            //List<String> awardSetList = new ArrayList(awardSet);
                            //JsonArray awardSetJsonArr = new JsonArray(awardSetList);
                            if (awardSet != null) {
                                contentStr = awardSet.toString();
                            } else {
                                contentStr = "";
                            }

                            System.out.println("#AWARD CONTENT:"+contentStr);

                            if (resultObj.get(ps.getDest_field()) != null) resultObj.remove(ps.getDest_field());
                            //resultObj.addProperty(ps.getDest_field(), contentHtml);

                            //awardArr = JsonUtil.convertStringToJsonArrayWithDelemeter(contentStr, ",");
                            //resultObj.addProperty(ps.getDest_field(), contentStr);

                            List<String> awardlist = MapUtil.getConvertSetToStringArray(awardSet);
                            awardArr = JsonUtil.convertListToJsonArray(awardlist);
                            //resultObj.addProperty(ps.getDest_field(), awardArr.getAsString());
                            resultObj.add(ps.getDest_field(), awardArr);
                        } else {
                            if (resultObj.get(ps.getDest_field()) != null) resultObj.remove(ps.getDest_field());
                            resultObj.addProperty(ps.getDest_field(), contentStr);
                        }
                        dest_fields.add(ps.getDest_field());
                    }
                }
            }
            //if (resultObj.get("contents") != null) resultObj.remove("contents");
            if (resultObj.get("dest_fields") != null) resultObj.remove("dest_fields");

            //resultObj.add("contents", contentObj);
            resultObj.add("dest_fields", dest_fields);
        }
        return resultObj;
    }
}
