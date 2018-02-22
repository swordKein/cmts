package com.kthcorp.cmts.service;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.LanguageListOption;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kthcorp.cmts.mapper.GoogleApiHistMapper;
import com.kthcorp.cmts.model.GoogleApiHist;
import com.kthcorp.cmts.util.DateUtils;
import com.kthcorp.cmts.util.HttpClientUtil;
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
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@Service
public class GoogleTransService implements GoogleTransServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(GoogleTransService.class);

    @Value("${keys.google.key}")
    private String oauth_key;
    @Value("${cmts.property.serverid}")
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
            System.out.println("#GOOGLE.TRANS "+reqTxt.length()+" chars getTransKoraen wait 2000ms!");
            Thread.sleep(2000);
            try {
                String dt = detectLanguage(reqTxt);
                result = getTransResult(reqTxt, "en", "ko");
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    System.out.println("#GOOGLE.TRANS "+reqTxt.length()+" chars getTransKoraen wait 25000ms!");
                    Thread.sleep(25000);
                    result = getTransResult(reqTxt, "en", "ko");
                } catch (Exception e2) {
                    e2.printStackTrace();
                    try {
                        System.out.println("#GOOGLE.TRANS "+reqTxt.length()+" chars getTransKoraen wait 35000ms!");
                        Thread.sleep(100000);
                        result = getTransResult(reqTxt, "en", "ko");
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    private String getTransResultOld(String reqTxt, String to_lang) throws Exception {
        String result = "";
/*
        final TranslateRequestInitializer KEY_INITIALIZER = new TranslateRequestInitializer(oauth_key);

        // Set up the HTTP transport and JSON factory
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // set up translate
        final Translate translate = new Translate.Builder(httpTransport, jsonFactory, null)
                .setApplicationName("My Project")
                .setTranslateRequestInitializer(KEY_INITIALIZER)
                .build();

        // list languages
        {
            //System.out.println(translate.languages().list().execute());
            // output: {"languages":[{"language":"af"},{"language":"ar"},{"language":"az"},{"language":"be"},{"language":"bg"},{"language":"bn"},{"language":"bs"},{"language":"ca"},{"language":"ceb"},{"language":"cs"},{"language":"cy"},{"language":"da"},{"language":"de"},{"language":"el"},{"language":"en"},{"language":"eo"},{"language":"es"},{"language":"et"},{"language":"eu"},{"language":"fa"},{"language":"fi"},{"language":"fr"},{"language":"ga"},{"language":"gl"},{"language":"gu"},{"language":"ha"},{"language":"hi"},{"language":"hmn"},{"language":"hr"},{"language":"ht"},{"language":"hu"},{"language":"hy"},{"language":"id"},{"language":"ig"},{"language":"is"},{"language":"it"},{"language":"iw"},{"language":"ja"},{"language":"jw"},{"language":"ka"},{"language":"kk"},{"language":"km"},{"language":"kn"},{"language":"ko"},{"language":"la"},{"language":"lo"},{"language":"lt"},{"language":"lv"},{"language":"mg"},{"language":"mi"},{"language":"mk"},{"language":"ml"},{"language":"mn"},{"language":"mr"},{"language":"ms"},{"language":"mt"},{"language":"my"},{"language":"ne"},{"language":"nl"},{"language":"no"},{"language":"ny"},{"language":"pa"},{"language":"pl"},{"language":"pt"},{"language":"ro"},{"language":"ru"},{"language":"si"},{"language":"sk"},{"language":"sl"},{"language":"so"},{"language":"sq"},{"language":"sr"},{"language":"st"},{"language":"su"},{"language":"sv"},{"language":"sw"},{"language":"ta"},{"language":"te"},{"language":"tg"},{"language":"th"},{"language":"tl"},{"language":"tr"},{"language":"uk"},{"language":"ur"},{"language":"uz"},{"language":"vi"},{"language":"yi"},{"language":"yo"},{"language":"zh"},{"language":"zh-TW"},{"language":"zu"}]}
        }

        // translate
        {
            final ImmutableList<String> phrasesToTranslate = ImmutableList.<String>builder().add(reqTxt).build();
            // perform
            TranslationsListResponse res = translate.translations().list(phrasesToTranslate, to_lang).execute();

            // output: {"translations":[{"detectedSourceLanguage":"en","translatedText":"Bonjour le monde"},{"detectedSourceLanguage":"en","translatedText":"Où puis-je promener mon chien"}]}


            System.out.println("#GOOGLE.TRANS: all-result::"+res.toString());
            System.out.println("#GOOGLE.TRANS: "+reqTxt.length()+" Char's Translated Text:"+res.getTranslations().get(0).getTranslatedText());
            if (res != null && res.size() > 0) result =  res.getTranslations().get(0).getTranslatedText();
        }

*/
        return result;
    }

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
}
