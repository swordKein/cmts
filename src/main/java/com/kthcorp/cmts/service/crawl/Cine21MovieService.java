package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Cine21MovieService implements Cine21MovieServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(Cine21MovieService.class);

    @Autowired
    LinkCrawlService linkCrawlService;

    @Value("${cmts.collector.cine21.search_url}")
    private String cine21_search_url;

    @Override
    @Synchronized
    public JsonObject getSearchCine21(ConfTarget reqInfo) throws Exception {

        String orig_title = reqInfo.getParam1();

        Map<String, Object> paramMap = new HashMap();
        paramMap.put("q", orig_title);
        String toUrl = cine21_search_url;

        String title = orig_title;
        String link = "";

        Map<String, Object> result1 = HttpClientUtil.reqGetHtml(toUrl
                , "", Charset.forName("utf-8"), paramMap, "bypass");
        System.out.println("#HttpClient:test returned string:" + result1.toString());

        JsonObject result = null;
        //List<String> result2 = JsoupUtil.getTaggedValueArray(result1.get("resultStr").toString(), "#container #content #cbody .search_list_1 li p a");
        //System.out.println("#getTaggedValue:"+result2);

        Document doc = Jsoup.parse(result1.get("resultStr").toString(), "UTF-8");
        Elements summaryElms = doc.select(".culm2_area .mov_list");
        //System.out.println("## result Array:: "+summaryElms.toString());
        if (summaryElms != null && summaryElms.size() > 0) {
            int cnt = 0;
            for (Element elm : summaryElms) {
                Elements titles = elm.select("li");
                Elements items = elm.select("a");

                //System.out.println("## array All-ITEM:: "+elm.toString());
                //System.out.println("## array ITEM:title:: "+titles.text().toString());
                //System.out.println("## array ITEM:link:: "+items.attr("href").toString());

                if (cnt < 1) {
                    result = new JsonObject();
                    title = titles.text().toString();
                    link = items.attr("href").toString();
                    if (!"".equals(link)) link = "http://www.cine21.com" + link;

                    result.addProperty("movie_title", title);
                    result.addProperty("pageUrl", link);
                }
                cnt++;
            }
        }

        if (link != "") {
            Map<String, Object> result2 = HttpClientUtil.reqGetHtml(link
                    , "", Charset.forName("utf-8"), null, "");
            System.out.println("#HttpClient:test returned string:" + result1.toString());

            List<String> result3 = JsoupUtil.getTaggedValueArray(result2.get("resultStr").toString()
                    , "#content");
            System.out.println("#result3:" + result3);
            if (result3 != null && result3.size() > 0) {
                JsonArray contentArr = new JsonArray();
                for (String rs : result3) {
                    contentArr.add(rs);
                }
                result.add("contents", contentArr);
                JsonObject metaObj = new JsonObject();
                metaObj.addProperty("content", contentArr.getAsString());
                result.add("metas", metaObj);
                JsonArray dest_fields = new JsonArray();
                dest_fields.add("content");
                result.add("dest_fields",dest_fields);
                result.addProperty("rt_code","OK");
                result.addProperty("rt_msg", "SUCCESS");
            }

        }

        if (result == null) {
            result = new JsonObject();
            result.addProperty("movie_title",title);
            result.addProperty("pageUrl","");
            result.add("content", new JsonArray());
            result.add("metas", new JsonObject());
            result.addProperty("rt_code","FAIL");
            result.addProperty("rt_msg", "FAIL");
        }

        System.out.println("#result:" + result.toString());

        return result;
    }

    /*
    SELECT * FROM metatagdb.conf_target;
    SELECT * FROM metatagdb.conf_ps_mapping where tg_id = 41;
    SELECT * FROM metatagdb.conf_preset;

    update conf_target set stat = 'D' where tg_id != 41;

    insert into conf_preset (ps_id, ps_tag, ps_type, dest_field, regdate, regid, priority, descriptp)
    values (400, '#content', 'content', 'content', now(), 'ghkdwo77', 200, 'cine21 content');

    insert into conf_target (tg_id, title, descript, tg_url, regdate, regid, param1, param3, stat, is_fail, is_limit, is_manual, content_min1, content_max1, content_min2, content_max2)
    values (41, 'cine21 content collect', 'www.cine21.com collecting', 'CINE21_MOVIE', now(), 'ghkdwo77', '#movietitle', 41, 'Y', 'N', 'Y', 'N', 500, 20000, 2000, 50000);

    insert into conf_ps_mapping (tg_id, ps_id) values (41, 400);




     */
}
