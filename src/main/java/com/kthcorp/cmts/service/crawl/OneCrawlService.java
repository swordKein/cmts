package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.GoogleTransService;
import com.kthcorp.cmts.util.HttpClientUtil;
import com.kthcorp.cmts.util.JsoupUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OneCrawlService implements OneCrawlServiceImpl {
    @Autowired
    GoogleTransService googleTrans;

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
                        if(ps.getDescriptp().contains("award")) {
                            String contentHtml = JsoupUtil.getTaggedValueAllHtml(pageContent, ps.getPs_tag());

                            if (resultObj.get(ps.getDest_field()) != null) resultObj.remove(ps.getDest_field());
                            resultObj.addProperty(ps.getDest_field(), contentHtml);
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
