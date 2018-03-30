package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.util.HttpClientUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleSearchService implements GoogleSearchServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(GoogleSearchService.class);

    @Value("${keys.google.key}")
    private String api_key;
    //@Value("${keys.google.cx}")
    //private String api_cx;

    @Override
    public JsonObject getFirstSearchedMovieItem (String title, String producer, String year) {

        JsonObject jObj = null;

        try {
            String reqStr = getSearchItems(title, producer, year);
            if (!"".equals(reqStr)) {
                JsonParser parser = new JsonParser();
                JsonObject fObj = (JsonObject) parser.parse(reqStr);
                JsonArray items = (JsonArray) fObj.getAsJsonArray("items");
                if (items != null && items.size() > 0) {
                    jObj = (JsonObject) items.get(0);
                }

                //System.out.println(">> items::" + jObj.get("link").toString());
            }
        } catch (Exception pe) {
            pe.printStackTrace();
        }
        return jObj;
    }

    @Override
    public String getSearchItems(String title, String producer, String year) {
        String result = "";

        //System.out.println("#KEY:"+api_key+"/CX:"+api_cx);
        String reqUrl = "https://www.googleapis.com/customsearch/v1";
        //String q = "site:imdb.com/title/ " + title + " in " + year + " directed by " + producer;
        String q = title + " in " + year + " directed by " + producer;

        try {

            Map<String, Object> reqParamMap = new HashMap<String, Object>();
            reqParamMap.put("key", api_key);
            //reqParamMap.put("cx", api_cx);

            q = java.net.URLEncoder.encode(q, "UTF-8");
            reqParamMap.put("q", q);

            result = HttpClientUtil.reqGet(reqUrl, "", null, reqParamMap, "bypass");
        } catch (Exception e) { e.printStackTrace(); }

        return result;
    }

    @Override
    public JsonArray getSearchItems3(ConfTarget reqInfo) {
        JsonArray result = new JsonArray();

        String reqUrl = "https://www.google.co.kr/search?q=";
        //String q = "site:imdb.com/title/ " + reqInfo.getParam1().replaceAll("//*","/");
        String q = reqInfo.getParam1().replaceAll("//*","/");


        try {
            reqUrl += java.net.URLEncoder.encode(q, "UTF-8");

            //System.out.println("#request URI:"+reqUrl);

            String resultStr = HttpClientUtil.reqGet(reqUrl, "", null,null, "bypass");

            result = getSearchWebItems(resultStr);

            if (result != null && result.get(0) != null) System.out.println("#GOOGLE_SEARCH_RESULT for IMDB jArr.get(0):"+result.get(0).toString());

        } catch (Exception e) { e.printStackTrace(); }


        return result;
    }


    @Override
    public JsonArray getOneItemFromSearchedList(ConfTarget reqInfo) {
        JsonArray result = new JsonArray();

        String reqUrl = "https://www.google.co.kr/search?q=";
        String q = reqInfo.getParam1().toString().replaceAll("//*","/");


        try {
            reqUrl += java.net.URLEncoder.encode(q, "UTF-8");

            //System.out.println("#request URI:"+reqUrl);

            String resultStr = HttpClientUtil.reqGet(reqUrl, "", null,null, "bypass");

            result = getSearchWebItems(resultStr);

            System.out.println("#result jArr.get(0):"+result.get(0).toString());

        } catch (Exception e) { e.printStackTrace(); }


        return result;
    }

    @Override
    public JsonArray getSearchItems2(String title, String producer, String year) {
        JsonArray result = new JsonArray();

        String reqUrl = "https://www.google.co.kr/search?q=";
        String q = "site:imdb.com/title/ " + title + " in " + year + " director " + producer;

        try {
            reqUrl += java.net.URLEncoder.encode(q, "UTF-8");
            //System.out.println("#request URI:"+reqUrl);

            String resultStr = HttpClientUtil.reqGet(reqUrl, "", null,null, "bypass");

            result = getSearchWebItems(resultStr);

            System.out.println("#result jArr.get(0):"+result.get(0).toString());

        } catch (Exception e) { e.printStackTrace(); }


        return result;
    }


    private JsonArray getSearchWebItems(String reqStr) throws Exception {
        JsonArray result = new JsonArray();

        Document doc = Jsoup.parse(reqStr,"UTF-8");
        Elements summaryElms = doc.select("body #res .g");
        //System.out.println("## result Array:: "+summaryElms.toString());
        if (summaryElms != null && summaryElms.size() > 0) {
            JsonObject obj = null;
            for (Element elm : summaryElms) {
                obj = new JsonObject();
                Elements titles = elm.select(".r");
                Elements links = elm.select(".r a");
                //Elements items = elm.select(".kv");

                //System.out.println("## array All-ITEM:: "+elm.toString());
                //System.out.println("## array ITEM:title:: "+titles.toString());
                //System.out.println("## array ITEM:link:: "+items.select("cite").text().toString());

                obj.addProperty("title", titles.text().toString());
                //String link = items.select("cite").text().toString();
                String link = links.attr("href").toString();
                link = link.replace("http://","");
                link = link.replace("https://","");

                System.out.println("# GOOGLE_SEARCH URL for IMDB :: title:"+titles.text()+"/url:"+link);
                obj.addProperty("link", link);

                result.add(obj);
            }
        }
        return result;
    }
}
