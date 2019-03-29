package com.kthcorp.cmts.service.crawl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.GoogleSearchService;
import com.kthcorp.cmts.service.GoogleTransService;
import com.kthcorp.cmts.util.CommonUtil;
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

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DaumMovieService implements DaumMovieServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(DaumMovieService.class);

    @Autowired
    GoogleSearchService googleSearch;
    @Autowired
    GoogleTransService googleTrans;
    @Autowired
    PagingArrayCrawlService pagingArrayCrawlService;
    @Autowired
    LinkCrawlService linkCrawlService;
    @Autowired
    OneCrawlService oneCrawlService;

    @Value("${cmts.collector.daummovie.search_url}")
    private String daummovie_search_url;
    @Value("${cmts.collector.daummovie.award_url}")
    private String daummovie_award_url;
    @Value("${cmts.collector.daummovie.magazine_url}")
    private String daummovie_magazine_url;
    @Value("${cmts.collector.daummovie.magazine_suburl}")
    private String daummovie_magazine_suburl;
    @Value("${cmts.collector.daummovie.magazine_limit_count}")
    private Integer daummovie_magazine_limit_count;

    @Override
    public String getSearchedMovieUrl(String reqStr) {
        return null;
    }

    @Override
    public JsonObject getSearchedDaumMovie(String reqStr) {
        JsonObject result = null;

        try {
            Document doc = Jsoup.parse(reqStr, "UTF-8");
            Elements summaryElms = doc.select(".daum_wrap #daumContent #movieEColl");
            System.out.println("## result Array:: "+summaryElms.toString());
            /*
            if (summaryElms != null && summaryElms.size() > 0) {
                JsonObject obj = null;
                for (Element elm : summaryElms) {
                    obj = new JsonObject();
                    Elements titles = elm.select("dl dt");
                    Elements items = elm.select("a");

                    String title = titles.text().toString();
                    String link = "";
                    link = items.attr("href").toString();
                    if (!"".equals(link)) link = "movie.naver.com" + link;

                    obj.addProperty("title", title);
                    obj.addProperty("link", link);

                    result.put("result",obj);
                }
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


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
        JsonArray contentsArr = new JsonArray();
        JsonObject metaObj = new JsonObject();
        JsonArray metaDestFields = new JsonArray();

        if (reqInfo != null && reqInfo.getPresetList() != null) {
            Map<String, Object> resultMap = null;

            resultMap = HttpClientUtil.reqGetHtml(reqInfo.getTg_url(), reqInfo.getTg_url_param1(), null,null, "bypass");

            String pageContent = "";
            String pageUri = "";
            if (resultMap != null) {
                if (resultMap.get("resultStr") != null) {
                    pageContent = resultMap.get("resultStr").toString();
                    //resultObj.addProperty("pageContent", pageContent);
                }
                if (resultMap.get("resultUri") != null) {
                    pageUri = resultMap.get("resultUri").toString();
                    resultObj.addProperty("pageUri", pageUri);
                }
                //String title_movie = JsoupUtil.getTaggedValue(pageContent, ".tit_movie");
                //if ("_FAIL".equals(title_movie)) title_movie = JsoupUtil.getTaggedValue(pageContent, ".h_movie a");
                //resultObj.addProperty("title_movie", title_movie);
            }

            JsonArray dest_fields = new JsonArray();
            for (ConfPreset ps : reqInfo.getPresetList()) {
                String descriptp = (ps.getDescriptp() != null ? ps.getDescriptp() : "");
                // reply가 아닌 경우 아이템 수집
                if (!descriptp.contains("reply") && !descriptp.contains("award")) {
                    String contentStr = "";

                    // meta는 별도로 모음, metas 객체에 dest_fields 로 식별
                    // contents는 별도로 모음, 식별자 제거
                    if (ps.getPs_type() != null && ps.getPs_type().contains("meta")) {
                        if (ps.getPs_type().contains("content")) {
                                contentStr = JsoupUtil.getTaggedValueAll(pageContent, ps.getPs_tag());
                                contentStr = CommonUtil.removeAllSpec(contentStr);
                                contentsArr.add(contentStr);
                                dest_fields.add(ps.getDest_field());


                                metaObj.addProperty(ps.getDest_field(), contentStr);
                                metaDestFields.add(ps.getDest_field());
                        } else if (ps.getPs_type().contains("genre")){
                            List<String> tmpStrArr = JsoupUtil.getTaggedValueArray(pageContent, ps.getPs_tag());
                            //contentStr = JsoupUtil.getTaggedValue(pageContent, ps.getPs_tag());
                            contentStr = tmpStrArr.get(0);
                            contentStr = CommonUtil.removeAllSpec(contentStr);
                            contentsArr.add(contentStr);
                            dest_fields.add(ps.getDest_field());


                            metaObj.addProperty(ps.getDest_field(), contentStr);
                            metaDestFields.add(ps.getDest_field());
                        } else {
                            contentStr = JsoupUtil.getTaggedValue(pageContent, ps.getPs_tag());
                            contentStr = CommonUtil.removeAllSpec(contentStr);
                            //contentsArr.add(contentStr);

                            metaObj.addProperty(ps.getDest_field(), contentStr);
                            metaDestFields.add(ps.getDest_field());
                        }
                    } else if (ps.getPs_type() != null && ps.getPs_type().contains("content")) {
                        if (!ps.getDest_field().contains("magazine")) {
                            contentStr = JsoupUtil.getTaggedValueAll(pageContent, ps.getPs_tag());
                            contentStr = CommonUtil.removeAllSpec(contentStr);
                            contentsArr.add(contentStr);
                            dest_fields.add(ps.getDest_field());
                        }
                    }
                    //resultArr.add(res1);
                }
            }
            metaObj.add("dest_fields", metaDestFields);
            resultObj.add("contents", contentsArr);
            resultObj.add("metas", metaObj);
            resultObj.add("dest_fields", dest_fields);
        }
        return resultObj;
    }

    @Override
    public JsonArray getSearchWebItemsForNaverMovie(String reqStr) throws Exception {
        JsonArray result = new JsonArray();

        Document doc = Jsoup.parse(reqStr,"UTF-8");
        Elements summaryElms = doc.select("#container #content #cbody .search_list_1 li");
        //System.out.println("## result Array:: "+summaryElms.toString());
        if (summaryElms != null && summaryElms.size() > 0) {
            JsonObject obj = null;
            for (Element elm : summaryElms) {
                obj = new JsonObject();
                Elements titles = elm.select("dl dt");
                Elements items = elm.select("a");

                //System.out.println("## array All-ITEM:: "+elm.toString());
                //System.out.println("## array ITEM:title:: "+titles.text().toString());
                //System.out.println("## array ITEM:link:: "+items.attr("href").toString());

                String title = titles.text().toString();
                String link = "";
                link = items.attr("href").toString();
                if (!"".equals(link)) link = "movie.naver.com"+link;

                obj.addProperty("title", title);
                obj.addProperty("link", link);

                result.add(obj);
            }
        }
        return result;
    }

    @Override
    public JsonArray getSearchWebItemsForDaumMovie(String reqUrl, String movieTitle, String movieYear) throws Exception {
        JsonArray result = new JsonArray();

        String jsonStr = Jsoup.connect(reqUrl)
                .timeout(4000)
                .userAgent("Mozilla")
                .ignoreContentType(true)
                .execute().body();

        JsonObject jobj = new Gson().fromJson(jsonStr, JsonObject.class);
        //System.out.println("#daumMovieSearch result::"+jobj.toString());

        JsonArray datas = (JsonArray) jobj.get("data");
        if (datas != null) {
            if (datas.size() > 1) {
                for (JsonElement je : datas) {
                    JsonObject jo = (JsonObject) je;
                    String title = jo.get("titleKo").getAsString();
                    title = CommonUtil.removeTex(title);
                    String movieId = jo.get("movieId").getAsString();
                    String link = "movie.daum.net/moviedb/main?movieId=";
                    link += movieId;

                    String prodYear = jo.get("prodYear").getAsString();

                    System.out.println("#get_title:"+movieTitle+"  /  get_prodYear:"+movieYear);
                    System.out.println("#daum_title:"+title+"  /  daum_prodYear:"+prodYear);
                    // 다음 검색 결과 중 영화 명과 영화 출시년도가 같을 경우 link 취득  added 2018.04.10
                    if (movieTitle.equals(title) && movieYear.equals(prodYear)) {
                        JsonObject newItem = new JsonObject();
                        newItem.addProperty("title", title);
                        newItem.addProperty("link", link);
                        result.add(newItem);
                        break;
                    }
                }
            }
            if (datas.size() > 0 && result.size() < 1) {
                // 다음 검색 결과 중 제목과 출시년도가 같은 건이 없을 경우 첫번째 링크 취득 addedd 2018.04.10
                // 다음 검색 결과가 1건일 경우 링크 취득 added 2018.04.10
                JsonObject jo = (JsonObject) datas.get(0);
                String title = jo.get("titleKo").getAsString();
                title = CommonUtil.removeTex(title);
                String movieId = jo.get("movieId").getAsString();
                String link = "movie.daum.net/moviedb/main?movieId=";
                link += movieId;

                String prodYear = jo.get("prodYear").getAsString();

                System.out.println("#get_title:"+movieTitle+"  /  get_prodYear:"+movieYear);
                System.out.println("#daum_title:"+title+"  /  daum_prodYear:"+prodYear);
                JsonObject newItem = new JsonObject();
                newItem.addProperty("title", title);
                newItem.addProperty("link", link);
                result.add(newItem);
            }
        }

        return result;
    }

    @Override
    public JsonObject getSearchWebItemsForDaumMovieV3(String reqUrl, String movieTitle, String movieYear) throws Exception {
        JsonObject result = new JsonObject();

        String jsonStr = Jsoup.connect(reqUrl)
                .timeout(4000)
                .userAgent("Mozilla")
                .ignoreContentType(true)
                .execute().body();

        System.out.println("#daumMovieSearch result::"+jsonStr);

        JsonObject jobj = new Gson().fromJson(jsonStr, JsonObject.class);
        //System.out.println("#daumMovieSearch result::"+jobj.toString());

        JsonArray datas = (JsonArray) jobj.get("data");
        if (datas != null) {
            if (datas.size() > 1) {
                for (JsonElement je : datas) {
                    JsonObject jo = (JsonObject) je;
                    String title = jo.get("titleKo").getAsString();
                    title = CommonUtil.removeTex(title);
                    String movieId = jo.get("movieId").getAsString();
                    String link = "movie.daum.net/moviedb/main?movieId=";
                    link += movieId;

                    String prodYear = jo.get("prodYear").getAsString();

                    System.out.println("#get_title:"+movieTitle+"  /  get_prodYear:"+movieYear);
                    System.out.println("#daum_title:"+title+"  /  daum_prodYear:"+prodYear);
                    // 다음 검색 결과 중 영화 명과 영화 출시년도가 같을 경우 link 취득  added 2018.04.10
                    if (movieTitle.equals(title) && movieYear.equals(prodYear)) {
                        JsonObject newItem = new JsonObject();
                        newItem.addProperty("title", title);
                        newItem.addProperty("link", link);
                        //result.add(newItem);
                        break;
                    }
                }
            }
            if (datas.size() > 0 && result.size() < 1) {
                // 다음 검색 결과 중 제목과 출시년도가 같은 건이 없을 경우 첫번째 링크 취득 addedd 2018.04.10
                // 다음 검색 결과가 1건일 경우 링크 취득 added 2018.04.10
                JsonObject jo = (JsonObject) datas.get(0);
                String title = jo.get("titleKo").getAsString();
                title = CommonUtil.removeTex(title);
                String movieId = jo.get("movieId").getAsString();
                String link = "movie.daum.net/moviedb/main?movieId=";
                link += movieId;

                String prodYear = jo.get("prodYear").getAsString();

                System.out.println("#get_title:"+movieTitle+"  /  get_prodYear:"+movieYear);
                System.out.println("#daum_title:"+title+"  /  daum_prodYear:"+prodYear);
                JsonObject newItem = new JsonObject();
                newItem.addProperty("title", title);
                newItem.addProperty("link", link);
                //result.add(newItem);
            }
        }

        return result;
    }

    @Override
    public JsonObject getOneItemFromSearchedPage(ConfTarget reqInfo) {
        JsonObject result = new JsonObject();

        String reqUrl = "";

    try {
            reqUrl = daummovie_search_url;
            reqUrl = reqUrl.replace("#searchTxt", URLEncoder.encode(reqInfo.getParam1().toString(), "UTF-8"));
            if(reqInfo.getParam2() != null) {
                reqUrl += reqInfo.getParam2();
            }
            //reqUrl = reqUrl + "#searchType=movie&page=1&sortType=acc";
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("#getMoviePortal request URI:"+reqUrl);

            String movieTitle = (reqInfo != null && reqInfo.getMovietitle() != null) ? reqInfo.getMovietitle() : "";
            String movieYear = (reqInfo != null && reqInfo.getMovieyear() != null) ? reqInfo.getMovieyear() : "";
            //result = getSearchWebItemsForDaumMovieV3(reqUrl, movieTitle, movieYear);
            result = this.getOneMovieFromDaumSearch(movieTitle, movieYear);


        } catch (Exception e) { e.printStackTrace(); }


        return result;
    }

    @Override
    public JsonObject getContents(String prefix, ConfTarget reqInfo) throws Exception {

        logger.info("daumMovieService.getContents by title:"+reqInfo.getMovietitle()+" / year:"+reqInfo.getMovieyear());

        JsonObject resultObj = new JsonObject();
        //resultObj.addProperty("title",reqInfo.getMovietitle());
        //resultObj.addProperty("year",reqInfo.getMovieyear());

        if (reqInfo != null) {
            //JsonArray jArr = googleSearch.getOneItemFromSearchedList(reqInfo);
            JsonObject jArr = this.getOneItemFromSearchedPage(reqInfo);

            if (jArr != null && jArr.size() > 0) {
                //System.out.println(">> items::" + jObj.toString());
                JsonObject obj = (JsonObject) jArr;
                //String titleGoogle = obj.get("title").toString();
                //resultObj.addProperty("title_google", CommonUtil.removeTex(titleGoogle));
                //String title_movie = (reqInfo.getMovietitle() != null) ? reqInfo.getMovietitle().toString() : "";
                //title_movie = CommonUtil.removeTex(title_movie);
                String title_movie = obj.get("title").toString();
                title_movie = CommonUtil.removeTex(title_movie);
                resultObj.addProperty("title_movie", title_movie);

                String sUrl = obj.get("link").toString();
                sUrl = CommonUtil.removeTex(sUrl);
                sUrl = sUrl.replaceAll("//*", "/");

                String reqUrl = "http://" + sUrl;
                reqInfo.setTg_url(reqUrl);
                System.out.println("#SURL:"+reqUrl);
                String[] tmpIds = reqUrl.split("=");
                System.out.println("#SURL - movieId ::"+tmpIds[1]);
                reqInfo.setParam1(tmpIds[1]);

                /* 설정에 따라 구글 검색결과에서 취득된 url을 검색 후 취득한 후 대상 tag를 수집한다 */
                //
                resultObj = this.getSubItems(reqInfo, resultObj);

                /* reply 배열 수집 */
                if (prefix.equals("DAUM_MOVIE")) {
                    // 다음영화 수상정보 수집 - 별도 URL
                    String awardUrl = daummovie_award_url + "?movieId=" + reqInfo.getParam1();
                    reqInfo.setTg_url(awardUrl);
                    JsonObject newAwardItem = new JsonObject();

                    newAwardItem = oneCrawlService.getSubItems(reqInfo, newAwardItem, "award");

                    System.out.println("#daum award data:" + newAwardItem.toString());
                    //resultObj.add("metas", newAwardItem);


                    JsonObject metasObj = (resultObj.get("metas") != null) ? (JsonObject) resultObj.get("metas") : new JsonObject();
                    JsonArray metasDestArr = (metasObj.get("dest_fields") != null) ? (JsonArray) metasObj.get("dest_fields") : new JsonArray();

                    if (newAwardItem != null && newAwardItem.get("dest_fields") != null) {
                        JsonArray newMetasDestArr = (newAwardItem.get("dest_fields") != null) ? (JsonArray) newAwardItem.get("dest_fields") : new JsonArray();

                        //System.out.println("#newAwardItem::"+newAwardItem.toString());

                        for (JsonElement je : newMetasDestArr) {
                            String dest = je.getAsString();
                            if(!"awardxxx".equals(dest)) {
                                String destStr = (newAwardItem.get(dest) != null ? newAwardItem.get(dest).getAsString() : "");
                                //destStr = CommonUtil.removeTex(destStr);
                                metasObj.addProperty(dest, destStr);
                            } else {
                                JsonArray destStr = (newAwardItem.get(dest) != null) ? (JsonArray) newAwardItem.get(dest) : null;
                                metasObj.add(dest, destStr);
                            }
                            metasDestArr.add(dest);
                        }
                    }
                    if (metasObj.get("dest_fields") != null) metasObj.remove("dest_fields");
                    metasObj.add("dest_fields", metasDestArr);
                    if (resultObj.get("metas") != null) resultObj.remove("metas");
                    resultObj.add("metas", metasObj);

                    // 다음영화 매거진 array 수집 - metas 에 추가
                    // 이후 metas를 1개의 string으로 묶어서 contents.megazine 항목으로 추가
                    title_movie = resultObj.get("title_movie").getAsString();

                    boolean getMagazineYn = false;
                    if (reqInfo != null && reqInfo.getPresetList() != null) {
                        for (ConfPreset p : reqInfo.getPresetList()) {
                            if ((p.getPs_type() != null && p.getDest_field() != null)
                                    && (p.getPs_type().contains("content") && p.getDest_field().contains("magazine"))) {
                                getMagazineYn = true;
                                break;
                            }
                        }
                    }

                    JsonObject pageObj = null;
                    JsonArray dataArr = null;
                    int magaCnt = 0;
                    JsonArray magaArr = new JsonArray();
                    if (getMagazineYn) {
                        String magUrl = daummovie_magazine_url + "&searchText=" + URLEncoder.encode(title_movie, "UTF-8");
                        //System.out.println("#magUrl:"+magUrl);

                        String pageContent = HttpClientUtil.reqGet(magUrl, null, null, null, "bypass");
                        pageObj = new Gson().fromJson(pageContent, JsonObject.class);
                        dataArr = (JsonArray) pageObj.get("data");
                        //System.out.println("#pageContent:"+pageObj.toString());

                        for (JsonElement e : dataArr) {
                            if (magaCnt < daummovie_magazine_limit_count) {
                                JsonObject o = (JsonObject) e;
                                if (o != null && o.get("samancoPublishKey") != null) {
                                    String magaAddUrl = o.get("samancoPublishKey").getAsString();
                                    reqInfo.setTg_url(daummovie_magazine_suburl);
                                    reqInfo.setTg_url_param1(magaAddUrl);
                                    JsonObject newSubItem = new JsonObject();
                                    newSubItem = oneCrawlService.getSubItems(reqInfo, newSubItem, "magazine");

                                    magaArr.add(newSubItem);
                                    //magaArr.add(newSubItem.get("contents").getAsString());
                                    //System.out.println("#get magazine subpage contents:"+newSubItem.toString());
                                }
                                //System.out.println("#pageContent.data:" + o.get("samancoPublishKey").toString());
                            }
                            magaCnt++;
                        }
                    }

                    //System.out.println("###sub magazine items array:"+magaArr.toString());

                    //다음영화 매거진을 분석대상인 contentsObj에 추가한다.
                    //System.out.println("#contents:"+ resultObj.toString());
                    //JsonObject contentObj = (resultObj != null && resultObj.get("contents") != null) ? (JsonObject) resultObj.get("contents") : new JsonObject();
                    JsonArray contentArr = (resultObj != null && resultObj.get("contents") != null) ? (JsonArray) resultObj.get("contents") : new JsonArray();

                    String maga_content = "";

                    if (magaArr != null && magaArr.size() > 0) {
                        JsonObject newMaga = new JsonObject();

                        //System.out.println("#magaArr:" + magaArr.toString());
                        for (JsonElement je : magaArr) {
                            JsonObject jo = (JsonObject) je;
                            System.out.println("## maga_content size:" + jo.get("magazine").getAsString().length());
                            maga_content = maga_content + " " + ((jo.get("magazine") != null) ?
                                    jo.get("magazine").getAsString()
                                    : "");
                        }
                        //System.out.println("## inside megazineObj ::" + jo.get("contents").toString());
                        //System.out.println("## inside megazineObj ::" + jo.get("dest_fields").toString());
                    }
                    if (!"".equals(maga_content)) {
                        contentArr.add(maga_content);
                        JsonArray dest_fields = null;
                        if (resultObj.get("dest_fields") != null) {
                            dest_fields = (JsonArray) resultObj.get("dest_fields");
                            dest_fields.add("magazine");
                            resultObj.remove("dest_fields");
                            resultObj.add("dest_fields", dest_fields);
                        } else {
                            dest_fields = new JsonArray();
                            dest_fields.add("magazine");
                            resultObj.add("dest_fields", dest_fields);
                        }
                        if (resultObj.get("contents") != null) resultObj.remove("contents");
                        resultObj.add("contents", contentArr);
                        //System.out.println("#ResultObj with magazine:"+resultObj.toString());
                    }
                }

                //resultObj.add("result", resultObj);
                resultObj.addProperty("rt_code", "OK");
                resultObj.addProperty("rt_msg", "SUCCESS");
            }

            //logger.info("#NaverMovieService.getContents resultObj::" + resultObj.toString());
        }

        if(resultObj != null && resultObj.get("pageContent") != null) resultObj.remove("pageContent");
        return resultObj;
    }


    /* 2019.03 다음 영화 수집 변경 */

    @Override
    public Map<String, Object> getDaumMovieItem(String title, String year) {
        Map<String, Object> resultMap = null;
        try {
            String reqUrl = daummovie_search_url;
            String stxt = title;
            if (!"".equals(year)) {
                stxt = title+" "+year;
            }
            stxt = URLEncoder.encode(stxt, String.valueOf(Charset.forName("utf-8")));

            reqUrl = reqUrl.replace("#searchTxt", stxt);

            resultMap = HttpClientUtil.reqGetHtml(reqUrl
                    ,"", Charset.forName("utf-8"), null, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public JsonObject getOneMovieFromDaumSearch(String title, String year) {
        JsonObject result = null;
        Map<String, Object> reqMap = this.getDaumMovieItem(title, year);

        if (reqMap != null && reqMap.get("resultStr") != null) {
            result = new JsonObject();

            String reqStr= reqMap.get("resultStr").toString();
            Document doc = Jsoup.parse(reqStr, "UTF-8");
            Elements summaryElms = doc.select(".daum_wrap #daumContent #movieEColl #movieTitle");
            //System.out.println("## result summaryElms:: "+summaryElms.toString());

            if (summaryElms != null && summaryElms.size() > 0) {
                JsonObject obj = null;
                for (Element elm : summaryElms) {
                    obj = new JsonObject();
                    Elements titles = elm.select(".tit_name");
                    Elements subtitles = elm.select(".tit_sub");

                    String title1 = titles.text().toString();
                    String subtitle1 = subtitles.text().toString();
                    if (!"".equals(subtitle1)) {
                        title1 += " " + subtitle1;
                    }
                    String link1 = titles.attr("href").toString();
                    link1 = link1.replace("http://","");
                    link1 = link1.replace("https://","");

                    String movieid = link1.replace("http://movie.daum.net/moviedb/main?movieId=", "");

                    result.addProperty("title", title1);
                    result.addProperty("link", link1);
                    result.addProperty("movieId", movieid);
                }
            }
        }
        return result;
    }
}
