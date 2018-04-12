package com.kthcorp.cmts.service;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kthcorp.cmts.mapper.GoogleApiHistMapper;
import com.kthcorp.cmts.model.GoogleApiHist;
import com.kthcorp.cmts.service.crawl.ImdbService;
import com.kthcorp.cmts.util.DateUtils;
import com.kthcorp.cmts.util.HttpClientUtil;
import com.kthcorp.cmts.util.JsonUtil;
import com.kthcorp.cmts.util.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleTransService implements GoogleTransServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(GoogleTransService.class);

    @Value("${google.api.key}")
    private String api_key;
    @Value("${google.api.cx}")
    private String api_cx;
    @Value("${google.api.translate.url}")
    private String translate_url;

    @Value("${property.serverid}")
    private String serverid;
    @Value("${google.api.translate.limit_month_count}")
    private Integer google_trans_limit_month_count;

    @Autowired
    private GoogleApiHistMapper googleApiHistMapper;

    @Override
    public String getTransKoreanResultWeb(String reqTxt) throws Exception {

        String reqUrl = "https://translate.google.com/?tl=en#en/ko/";
        reqUrl += URLEncoder.encode(reqTxt, "UTF-8");

        String htmlResult = HttpClientUtil.reqGet(reqUrl, "", null,null, "");
        String result = JsoupUtil.getTaggedValueGoogleTrans(htmlResult, "#result_box");

        System.out.println("# returned string:"+result);
        return result;
    }

    @Override
    public String getTransKoreanResult(String reqTxt) throws Exception {
        // Google RateLimit check 10000c / 100s

        String result = "";
        if(reqTxt.length() > 10) {
            logger.info("#GOOGLE.TRANS "+reqTxt.length()+" 1st");
                    //+"chars getTransKoraen wait 2000ms!");
            //Thread.sleep(2000);
            try {
                //String dt = detectLanguage(reqTxt);
                result = getTransResult(reqTxt, "en", "ko");
                logger.info("#GOOGLE.TRANS "+reqTxt.length()+" to trans 1st result::"+result);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("#GOOGLE.TRANS error! :: "+e.toString());

                try {
                    logger.info("#GOOGLE.TRANS "+reqTxt.length()+" chars getTransKoraen wait 2000ms!");
                    Thread.sleep(2000);
                    result = getTransResult(reqTxt, "en", "ko");
                    logger.info("#GOOGLE.TRANS "+reqTxt.length()+" to trans 2nd result::"+result);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    logger.error("#GOOGLE.TRANS error! 2:: "+e2.toString());
                    try {
                        logger.info("#GOOGLE.TRANS "+reqTxt.length()+" chars getTransKoraen wait 4000ms!");
                        Thread.sleep(4000);
                        result = getTransResult(reqTxt, "en", "ko");
                        logger.info("#GOOGLE.TRANS "+reqTxt.length()+" to trans 3nd result::"+result);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        logger.error("#GOOGLE.TRANS error! 3:: "+e3.toString());
                    }
                }
            }
        }
        return result;
    }

    public String getTransResult(String q, String sourceLang, String targetLang) {
        String result = "";
        String reqUrl = translate_url;
        try {
            Map<String, Object> reqParamMap = new HashMap<String, Object>();
            reqParamMap.put("key", api_key);
            reqParamMap.put("cx", api_cx);

            q = java.net.URLEncoder.encode(q, "UTF-8");
            reqParamMap.put("q", q);
            reqParamMap.put("source", sourceLang);
            reqParamMap.put("target", targetLang);

            System.out.println("#GoogleSearchService reqUrl:"+reqUrl+"&& params:"+reqParamMap.toString());

            String result1 = HttpClientUtil.reqGet(reqUrl, "", null, reqParamMap, "bypass");
            //System.out.println("#result1:"+result1);

            if (result1 != null) {
                JsonObject jobj = JsonUtil.getJsonObject(result1);
                if (jobj != null && jobj.get("data") != null) {
                    JsonObject data = jobj.get("data").getAsJsonObject();
                    //System.out.println("#GOOGLE.TRANS :: data txt:" + data.toString());

                    if (data != null && data.get("translations") != null) {
                        JsonArray translations = data.get("translations").getAsJsonArray();
                        //System.out.println("#GOOGLE.TRANS :: translations txt:" + translations.toString());

                        if (translations != null && translations.size() > 0) {
                            JsonObject transTxt = (JsonObject) translations.get(0);
                            //System.out.println("#GOOGLE.TRANS :: transed txt:" + transTxt);

                            if (transTxt != null && transTxt.get("translatedText") != null) {
                                result = transTxt.get("translatedText").getAsString();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("#GOOGLE.TRANS error! ::"+e.toString());
            e.printStackTrace();
        }
        return result;
    }

    /*
    public static Translate createTranslateService() {
        return TranslateOptions.newBuilder().build().getService();
    }

    public String getTransResult(String sourceText, String sourceLang, String targetLang) {
        String result = "";
        Translate translate = createTranslateService();

        TranslateOption srcLang = TranslateOption.sourceLanguage(sourceLang);
        TranslateOption tgtLang = TranslateOption.targetLanguage(targetLang);

        // Use translate `model` parameter with `base` and `nmt` options.
        TranslateOption model = TranslateOption.model("nmt");

        // 구글 번역 API 사용양 제어를 위해 월별 총 글자수를 조회
        String reqMonth = DateUtils.getLocalMonth();
        GoogleApiHist reqMonthSize = new GoogleApiHist();
        reqMonthSize.setRegmonth(reqMonth);
        GoogleApiHist monthSum =  googleApiHistMapper.getGoogleTransSumSizeByMonth(reqMonthSize);
        int monthSumSize = 0;
        if (monthSum != null && monthSum.getSrc_size() != null) monthSumSize = monthSum.getSrc_size();

        // limit보다 작을 때 번역 시도
        Translation translation = null;
        GoogleApiHist reqHist = new GoogleApiHist();
        reqHist.setSrc_lang(sourceLang);
        reqHist.setSrc_txt(sourceText);
        reqHist.setSrc_size(sourceText.length());
        reqHist.setRegmonth(reqMonth);
        reqHist.setRegid(serverid);

        if (monthSumSize < google_trans_limit_month_count) {
            translation = translate.translate(sourceText, srcLang, tgtLang, model);
            System.out.println("Source::  Lang: "+sourceLang+", Text:"+sourceText);
            System.out.println("Translated:: Lang: "+targetLang+", Text:"+translation.getTranslatedText());

            reqHist.setType("trans");
            reqHist.setTarget_lang(targetLang);
            reqHist.setTarget_txt((translation != null && translation.getTranslatedText() != null) ? translation.getTranslatedText() : "");
            reqHist.setAction("google_translate");
        } else {
            reqHist.setType("trans_fail_limit");
            reqHist.setAction("google_translate_fail_by_limit:" + google_trans_limit_month_count + "/current:" + monthSumSize);
            result = "TRANS_LIMIT";
        }

        int resultHist = googleApiHistMapper.insGoogleApiHist(reqHist);

        if ("".equals(result) && translation != null && translation.getTranslatedText() != null) {
            result = translation.getTranslatedText();
        }

        return result;
    }

    public static String detectLanguage(String sourceText) {
        Translate translate = createTranslateService();
        List<Detection> detections = translate.detect(ImmutableList.of(sourceText));
        System.out.println("Language(s) detected:");
        for (Detection detection : detections) {
            System.out.println("Language(s) detected: lang:"+detection.toString());
        }
        return detections.toString();
    }
    */
}
