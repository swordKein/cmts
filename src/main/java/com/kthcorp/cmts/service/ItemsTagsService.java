package com.kthcorp.cmts.service;

import com.google.gson.*;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class ItemsTagsService implements ItemsTagsServiceImpl {
    static Logger logger = LoggerFactory.getLogger(ItemsTagsService.class);

    public static boolean jobRuuningStat = false;

    @Value("${property.serverid}")
    private String serverid;
    @Value("${property.crawl_naver_kordic_url}")
    private String crawl_naver_kordic_url;

    @Value("${cmts.property.es_cut_point}")
    private Double es_cut_point;

    @Autowired
    private ItemsTagsMapper itemsTagsMapper;
    @Autowired
    private ItemsMetasMapper itemsMetasMapper;
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private DicKeywordsMapper dicKeywordsMapper;
    @Autowired
    private DicService dicService;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private CcubeService ccubeService;
    @Autowired
    private SchedTriggerService schedTriggerService;

    @Autowired
    private ManualJobHistMapper manualJobHistMapper;

    @Override
    public List<ItemsTags> getItemsTagsMetasByItemIdx(ItemsTags req) {
        return itemsTagsMapper.getItemsTagsMetasByItemIdx(req);
    }

    @Override
    public String getItemsTagsMetasStringByItemIdx(ItemsTags req) throws Exception {
        String result = "";

        JsonArray resultArr = new JsonArray();
        List<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHENERA");
        origTypes.add("METASWHENHIS");
        origTypes.add("METASWHENETC");

        origTypes.add("METASWHERE");
        origTypes.add("METASWHERENATION");
        origTypes.add("METASWHERECITY");
        origTypes.add("METASWHERESIGHTS");
        origTypes.add("METASWHEREETC");

        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");

        origTypes.add("METASINT");

        List<ItemsTags> itemsTags = itemsTagsMapper.getItemsTagsMetasByItemIdx(req);

        for(ItemsTags ta : itemsTags) {
            String mtype = ta.getMtype().toUpperCase();
            String mtype2 = mtype.replace("METAS","").toLowerCase();

            String meta = "";
            for (String otype : origTypes) {
                if (mtype.equals(otype)) {
                    meta = ta.getMeta().trim();
                    JsonArray metaArr = null;
                    if (!"".equals(meta)) {
                        metaArr = JsonUtil.getJsonArray(meta);
                        if (metaArr != null && metaArr.size() > 0) {
                            JsonObject newJo = null;
                            for (JsonElement je : metaArr) {
                                JsonObject jo = (JsonObject) je;
                                //System.out.println("#ELOG jo:"+jo.toString());
                                newJo = new JsonObject();
                                newJo.addProperty("type", mtype2);
                                newJo.addProperty("meta", jo.get("word").getAsString());
                                resultArr.add(newJo);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("#ELOG resultArr:"+resultArr.toString());
        result = resultArr.toString();

        return result;
    }


    @Override
    public String getWordArrStringFromItemsTagsMetasIdxAndMtype(ItemsTags req) throws Exception {
        String result = "";

        JsonArray resultArr = new JsonArray();
        String reqMtype = req.getMtype();

        List<ItemsTags> itemsTags = itemsTagsMapper.getItemsTagsMetasByItemIdx(req);

        for(ItemsTags ta : itemsTags) {
            String mtype = ta.getMtype().toUpperCase();
            String mtype2 = mtype.replace("METAS","").toLowerCase();

            String meta = "";
            if (mtype.equals(reqMtype)) {
                meta = ta.getMeta().trim();
                JsonArray metaArr = null;
                if (!"".equals(meta)) {
                    metaArr = JsonUtil.getJsonArray(meta);
                    if (metaArr != null && metaArr.size() > 0) {

                        for (JsonElement je : metaArr) {
                            JsonObject jo = (JsonObject) je;
                            String ns = jo.get("word").getAsString();
                            resultArr.add(ns);
                        }
                    }
                }
            }
        }

        //System.out.println("#ELOG resultArr:"+resultArr.toString());
        result = resultArr.toString();

        return result;
    }

    @Override
    public String tmp_getWordArrStringFromItemsTagsMetasIdxAndMtype(ItemsTags req) throws Exception {
        String result = "";

        JsonArray resultArr = new JsonArray();
        String reqMtype = req.getMtype();

        List<ItemsTags> itemsTags = itemsTagsMapper.tmp_getItemsTagsMetasByItemIdx(req);

        for(ItemsTags ta : itemsTags) {
            String mtype = ta.getMtype().toUpperCase();
            String mtype2 = mtype.replace("METAS","").toLowerCase();

            String meta = "";
            if (mtype.equals(reqMtype)) {
                meta = ta.getMeta().trim();
                JsonArray metaArr = null;
                if (!"".equals(meta)) {
                    metaArr = JsonUtil.getJsonArray(meta);
                    if (metaArr != null && metaArr.size() > 0) {

                        for (JsonElement je : metaArr) {
                            JsonObject jo = (JsonObject) je;
                            String ns = jo.get("word").getAsString();
                            resultArr.add(ns);
                        }
                    }
                }
            }
        }

        //System.out.println("#ELOG resultArr:"+resultArr.toString());
        result = resultArr.toString();

        return result;
    }

    @Override
    public ItemsTags getItemsTagsMetasByItemIdxAndMtype(ItemsTags req) {
        return itemsTagsMapper.getItemsTagsMetasByItemIdxAndMtype(req);
    }

    @Override
    public int getCurrTagsIdxOld(int itemIdx) {
        ItemsTags req = new ItemsTags();
        req.setIdx(itemIdx);
        req.setStat("S");
        int tagidx = this.getMaxTagsIdxByItemIdx(req);

        return tagidx;
    }
    @Override
    public int getCurrTagsIdxReady(int itemIdx) {
        ItemsTags req = new ItemsTags();
        req.setIdx(itemIdx);
        req.setStat("Y");
        int tagidx = this.getMaxTagsIdxByItemIdx(req);

        return tagidx;
    }

    @Override
    public int getCurrTagsIdxForInsert(int itemIdx) {
        /*
        1. 泥섏쓬 �벑濡앺븷 寃쎌슦 = 0
        2. �듅�씤�맂 嫄댁씠 �엳怨� �닔�젙�븷 寃쎌슦 = last tagIdx
        3. �듅�씤�맂 嫄댁씠 �뾾怨� �닔�젙�븷 寃쎌슦 = last tagIdx
         */
        ItemsTags req = new ItemsTags();
        req.setIdx(itemIdx);
        req.setStat("Y");
        int tagidx = this.getMaxTagsIdxByItemIdx(req);

        if (tagidx == 0) {
            // �듅�씤�셿猷뚮맂 tagidx媛� �엳�뒗吏� �솗�씤
            ItemsTags req2 = new ItemsTags();
            req2.setIdx(itemIdx);
            req2.setStat("S");
            int tagidx2 = this.getMaxTagsIdxByItemIdx(req2);
            //System.out.println("#confirmed tagidx2:"+tagidx2);

            // �듅�씤�셿猷뚮맂 tagidx2媛� �뾾�쑝硫� �듅�씤誘몄셿猷뚯씤 tagidx瑜� 由ы꽩
            if (tagidx2 == 0) {
                // �듅�씤�셿猷뚮맂 tagidx 媛쒖닔媛� > 0 �씠硫� 1 利앷�
                int confirmedTagCnt = this.cntConfirmedTags(req2);
                System.out.println("#confirmedTagCnt :"+confirmedTagCnt);
                if (confirmedTagCnt > 0) {
                    tagidx = 1;
                }
                // �듅�씤�셿猷뚮맂 tagidx媛� �엳�쑝硫� 1 利앷��븯�뿬 �떊洹쒓굔�쑝濡� ���옣
            } else if (tagidx2 > 0) {
                tagidx = tagidx2 + 1;
            }

            req.setTagidx(tagidx);
            int rt0 = this.insItemsTagsKeys(req);
        }

        return tagidx;
    }

    @Override
    public int getCurrTagsIdxForSuccess(int itemIdx) {
        ItemsTags req = new ItemsTags();
        req.setIdx(itemIdx);
        req.setStat("S");

        int tagidx = this.getMaxTagsIdxByItemIdx(req);

        return tagidx;
    }

    @Override
    public int getMaxTagsIdxByItemIdx(ItemsTags req) {
        return itemsTagsMapper.getMaxTagsIdxByItemIdx(req);
    }

    @Override
    public int cntConfirmedTags(ItemsTags req) {
        return itemsTagsMapper.cntConfirmedTags(req);
    }

    @Override
    public int insItemsTagsKeys(ItemsTags req) {
        return itemsTagsMapper.insItemsTagsKeys(req);
    }

    @Override
    public int uptItemsTagsKeysStat(ItemsTags req) {
        return itemsTagsMapper.uptItemsTagsKeysStat(req);
    }

    @Override
    public int insItemsTagsMetas(ItemsTags req) {
        if (req != null && req.getRegid() == null) req.setRegid(serverid);
        return itemsTagsMapper.insItemsTagsMetas(req);
    }

    @Override
    public JsonObject getItemsTagsMetasAll_bak(int itemIdx) {
        JsonObject result = new JsonObject();
        if (itemIdx > 0) {
            Items item = itemsMapper.getItemsInfoByIdx(itemIdx);
            if (item != null) {
                String duration = (item.getDuration() != null) ? item.getDuration() : "";
                result.addProperty("DURATION", duration);
                System.out.println("#Duration:"+duration);


                ItemsTags itReq = new ItemsTags();
                itReq.setIdx(itemIdx);
                itReq.setStat("Y");
                List<ItemsTags> listMetas = this.getItemsTagsMetasByItemIdx(itReq);
                //System.out.println("#listMetas"+listMetas.toString());
                for(ItemsTags it : listMetas) {
                    if (it.getMeta() != null) {
                        //System.out.println("#getMeta() : " + it.getMeta().toString());
                        JsonParser jsonParser = new JsonParser();
                        //JsonArray metas = new Gson().fromJson(it.getMeta(), new TypeToken<List<MetasType>>(){}.getType());
                        JsonArray metas = (JsonArray) jsonParser.parse(it.getMeta());

                        //System.out.println("#metas JsonArray : " + metas.toString());
                        result.add(it.getMtype(), metas);
                    } else {
                        result.add(it.getMtype(), new JsonArray());
                    }
                }
            }
        }

        return result;
    }

    @Override
    public JsonObject getItemsMetasByIdx(int itemIdx, List<String> origTypes, String getStat) {
        JsonObject result = new JsonObject();

        ItemsTags itReq = new ItemsTags();
        itReq.setIdx(itemIdx);
        itReq.setStat(getStat);

        List<ItemsTags> metasList = this.getItemsTagsMetasByItemIdx(itReq);
        //System.out.println("#listMetas"+listMetas.toString());

        if (metasList != null && metasList.size() > 0) {
            for (ItemsTags it : metasList) {
                if(it != null && it.getMtype() != null && it.getMeta() != null) {
                    for(String ot : origTypes) {
                        if(ot.equals(it.getMtype().toUpperCase())) {
                            JsonParser jsonParser = new JsonParser();
                            JsonArray metas = (JsonArray) jsonParser.parse(it.getMeta());

                            //System.out.println("#metas JsonArray : " + metas.toString());
                            JsonArray metas2 = new JsonArray();
                            if (metas != null && metas.size() > 0) {
                                if (it.getMtype().toUpperCase().contains("SUBGENRE")) {
                                    // 留덉�留� �젣�쇅 ���긽 �븘�꽣 �쟻�슜
                                    for (JsonElement je : metas) {
                                        JsonObject jo1 = (JsonObject) je;
                                        boolean isValid = StringUtil.filterLastTagValid(jo1);

                                        if (isValid) {
                                            //System.out.println("#ELOG metas jo: added:"+jo1.toString());
                                            metas2.add(jo1);
                                        }
                                    }
                                    result.add(it.getMtype(), metas2);
                                    //System.out.println("#ELOG metas2:"+metas2.toString()+" by mtype:"+it.getMtype());
                                } else {
                                    result.add(it.getMtype(), metas);
                                    //System.out.println("#ELOG metas1:"+metas.toString()+" by mtype:"+it.getMtype());
                                }
                            }
                        }
                    }
                }
            }
        }

        result.addProperty("DURATION", getItemsDuration(itemIdx));
        JsonObject result2 = setEmptyMetas(result, origTypes);

        return result2;
    }


    private JsonObject getOldItemsMetasByIdx(int itemIdx, ArrayList<String> origTypes) {
        JsonObject result = new JsonObject();

        ItemsTags itReq = new ItemsTags();
        itReq.setIdx(itemIdx);
        itReq.setStat("S");

        List<ItemsTags> metasList = this.getItemsTagsMetasByItemIdx(itReq);
        //System.out.println("#metasList"+metasList.toString());

        if (metasList != null && metasList.size() > 0) {
            for (ItemsTags it : metasList) {
                if(it != null && it.getMtype() != null && it.getMeta() != null) {
                    for(String ot : origTypes) {
                        if(ot.equals(it.getMtype().toUpperCase())) {
                            JsonParser jsonParser = new JsonParser();
                            JsonArray metas = (JsonArray) jsonParser.parse(it.getMeta());

                            //System.out.println("#metas JsonArray : " + metas.toString());
                            result.add(it.getMtype(), metas);
                        }
                    }
                }
            }
        }

        result.addProperty("DURATION", getItemsDuration(itemIdx));
        result = setEmptyMetas(result, origTypes);

        return result;
    }

    private String getItemsDuration(int itemIdx) {
        String duration = "";
        if (itemIdx > 0) {
            Items item = itemsMapper.getItemsInfoByIdx(itemIdx);
            if (item != null) {
                duration = (item.getDuration() != null) ? item.getDuration() : "6m";
                System.out.println("#Duration:" + duration);
            }
        }
        return duration;
    }

    @Override
    public JsonObject getItemsMetasByItemIdx(int itemIdx, boolean isColorCode) throws Exception {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHENERA");
        origTypes.add("METASWHENHIS");
        origTypes.add("METASWHENETC");

        origTypes.add("METASWHERE");
        origTypes.add("METASWHERENATION");
        origTypes.add("METASWHERECITY");
        origTypes.add("METASWHERESIGHTS");
        origTypes.add("METASWHEREETC");

        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");

        origTypes.add("METASINT");


        origTypes.add("METASCHARACTER");
        origTypes.add("LIST_NOT_MAPPED");
        origTypes.add("WORDS_GENRE");
        origTypes.add("WORDS_SNS");
        origTypes.add("WORDS_ASSOC");
        origTypes.add("LIST_SUBGENRE");
        origTypes.add("LIST_SEARCHKEYWORDS");
        origTypes.add("LIST_RECO_TARGET");
        origTypes.add("LIST_RECO_SITUATION");

        JsonObject resultObj = getItemsMetasByIdx(itemIdx, origTypes, "Y");
//        System.out.println("#ELOG.getItemsMetasByItemIdx:: old.datas::"+resultObj.toString());
//        logger.debug("[********] 1 resultObj.toString() = " + resultObj.toString() + "\n\n");

        JsonObject resultObj2 = getItemsMetasDupByItemIdx(resultObj, itemIdx, isColorCode);
//        System.out.println("#ELOG.getItemsMetasByItemIdx:: dupCheck.datas::"+resultObj2.toString());
//        logger.debug("[********] 2 resultObj2.toString() = " + resultObj2.toString() + "\n\n");

        /* WORDS_ASSOC 媛먯꽦�쑀�쓽�뼱 - �꽕�씠踰꾩궗�쟾 */
        resultObj2 = getWordsAssoc(itemIdx, resultObj2);
//        logger.debug("[********] 3 resultObj2.toString() = " + resultObj2.toString() + "\n\n");

//        System.out.println("#ELOG.getItemsMetasByItemIdx:: after.wordsAssoc.datas::"+resultObj2.toString());

        /* WORDS_GENRE */
        resultObj2 = getWordsGenre(itemIdx, resultObj2);
//        System.out.println("#ELOG.getItemsMetasByItemIdx:: after.wordsGenre.datas::"+resultObj2.toString());
//        logger.debug("[********] 4 resultObj2.toString() = " + resultObj2.toString() + "\n\n");


        /* WORDS_SNS */
        // resultObj2 = getWordsSns(itemIdx, resultObj2);

        /* LIST_SUBGENRE */
        if (resultObj2 != null && resultObj2.get("LIST_SUBGENRE") == null) {
            resultObj2 = getSubgenres(itemIdx, resultObj2);
        }
//        logger.debug("[********] 5 resultObj2.toString() = " + resultObj2.toString() + "\n\n");

        /* LIST_AWARD */
        resultObj2 = getAwardObject(itemIdx, resultObj2);
//        logger.debug("[********] 6 resultObj2.toString() = " + resultObj2.toString() + "\n\n");

        return resultObj2;
    }

    @Override
    public JsonObject getAwardObject(int itemIdx, JsonObject resultObj2) throws Exception {

        JsonObject metaAwardObj = apiService.getAwardArrInfoByIdx(itemIdx);
        System.out.println("#itemsTagsService.getAwardObject :: "+metaAwardObj.toString());

        JsonArray awardArr = null;
        if (metaAwardObj != null && metaAwardObj.get("AWARD") != null) {
            System.out.println("#award obj :"+ metaAwardObj.get("award"));
            JsonElement awardArrElm = metaAwardObj.get("AWARD");
            if (awardArrElm != null){
                String awardArrStr = String.valueOf(awardArrElm);

                System.out.println("#AWARD str :" + awardArrStr);
                if (!"".equals(awardArrStr) && !"\"\"".equals(awardArrStr) && !"null".equals(awardArrStr)) {
                    awardArr = (JsonArray) awardArrElm;

                    System.out.println("#awardArrElm obj :"+ awardArrElm.toString() + " => awardArr:"+awardArr.toString());
                } else {
                    awardArr = new JsonArray();
                }
            } else {
                awardArr = new JsonArray();
            }
        } else {
            awardArr = new JsonArray();
        }

        resultObj2.add("LIST_AWARD", awardArr);
        System.out.println("#itemsTagsService.getAwardObject:: resultObj2.awardArr:"+awardArr.toString());
        System.out.println("#itemsTagsService.getAwardObject:: resultObj2:"+resultObj2.toString());
        return resultObj2;
    }

    private JsonObject getWordsSns(int itemIdx, JsonObject resultObj2) {
        Items reqIt = new Items();
        reqIt.setIdx(itemIdx);
        Items itemInfo = itemsService.getItemsByIdx(reqIt);
        System.out.println("#ELOG.getWordsSns.getItemInfo:"+itemInfo.toString());

        if (itemInfo != null && itemInfo.getTitle() != null) {
            String movietitle = itemInfo.getTitle().trim();
            if (!"".equals(movietitle)) {
                System.out.println("#ELOG.movieTitle:"+movietitle);
                try {
                    JsonArray result = apiService.getSnsKeywords(movietitle);
                    if(result == null) result = new JsonArray();
                    if (resultObj2.get("WORDS_SNS") != null) resultObj2.remove("WORDS_SNS");

                    resultObj2.add("WORDS_SNS", result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resultObj2;
    }

    @Override
    public JsonObject getWordsAssoc(int itemIdx, JsonObject resultObj2) {
        if (resultObj2 != null) {
            if (resultObj2.get("METASEMOTION") != null) {
                JsonArray emotionArr = (JsonArray) resultObj2.get("METASEMOTION");
                //System.out.println("#ELOG.emotionArr:"+emotionArr.toString());

                if (emotionArr != null && emotionArr.size() > 0) {
                    List<String> emoKeys = JsonUtil.convertJsonArrayToListByLabel(emotionArr, "word");
                    //System.out.println("#ELOG.emoKeys:"+emoKeys.toString());

                    List<String> emoKindKeys = null;
                    try {
                        emoKindKeys = this.getNaverKindWordsByList(emoKeys, 10);
                        if (emoKindKeys != null && emoKindKeys.size() > 0) {
                            JsonArray newEmoKindArr = JsonUtil.convertListToJsonArray(emoKindKeys);
                            if (newEmoKindArr != null && newEmoKindArr.size() > 0) {
                                System.out.println("#ELOG.getItemsMetasByIdx:"+itemIdx+"/WORDS_ASSOC"+newEmoKindArr.toString());

                                if (resultObj2.get("WORDS_ASSOC") != null) {
                                    resultObj2.remove("WORDS_ASSOC");
                                }
                                resultObj2.add("WORDS_ASSOC", newEmoKindArr);
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }

            }
        }
        return resultObj2;
    }

    @Override
    public JsonObject getWordsGenre(int itemIdx, JsonObject resultObj2) {
        String movieGenre = this.getMovieGenreFromCcubeContents(itemIdx);
        System.out.println("#ELOG.movieGenre:"+movieGenre);
        if (!"".equals(movieGenre)) {
            List<String> listGenreWords = this.getGenreWordsListByGenre(movieGenre);
            if (listGenreWords != null) {
                JsonArray newListGenreWords = JsonUtil.convertListToJsonArray(listGenreWords);
                System.out.println("#ELOG.getItemMetaByIdx:"+itemIdx+"/WORDS_GENRE:"+newListGenreWords.toString());
                if (resultObj2.get("WORDS_GENRE") != null) {
                    resultObj2.remove("WORDS_GENRE");
                }
                resultObj2.add("WORDS_GENRE", newListGenreWords);
            }
        }
        return resultObj2;
    }

    @Override
    public JsonObject getSubgenres(int itemIdx, JsonObject resultObj2) {
        String subGenreMix = this.getMovieSubGenreData(itemIdx);
        System.out.println("#ELOG.subGenreMix:"+subGenreMix);
        if (!"".equals(subGenreMix)) {
            JsonArray listSubGenres = new JsonArray();
            String[] arrSubGenres = subGenreMix.split(",");
            for (String as : arrSubGenres) {

                // last sub_genre filter
                String as2 = as.trim();
                as2 = StringUtil.filterGenres(as2);

                if(!"".equals(as2)) {
                    JsonObject newItem = new JsonObject();
                    newItem.addProperty("type", "");
                    newItem.addProperty("ratio", "0.0");
                    newItem.addProperty("word", as2);
                    listSubGenres.add(newItem);
                }
            }

            System.out.println("#ELOG.getItemMetaByIdx:"+itemIdx+"/LIST_SUBGENRE:"+listSubGenres.toString());
            if (resultObj2.get("LIST_SUBGENRE") != null) {
                resultObj2.remove("LIST_SUBGENRE");
            }

            resultObj2.add("LIST_SUBGENRE", listSubGenres);

        }
        return resultObj2;
    }

    /*
        LIST_SUBGENRE -> META_SUBGENRE
     */
    @Override
    public JsonObject getSubgenresString(int itemIdx, JsonObject resultObj2) {
        resultObj2 = this.getSubgenres(itemIdx, resultObj2);
        //System.out.println("#first subgenre:"+resultObj2.toString());

        Set subgenreArr = new HashSet();
        String subgenreStr = "";

        //System.out.println("#ELOG itemsTagsMetas ALL:"+resultObj2.toString());

        if (resultObj2 != null && resultObj2.get("LIST_SUBGENRE") != null) {
            System.out.println("#ELOG jsonArray for LIST_SUBGENRE:"+resultObj2.get("LIST_SUBGENRE").toString());

            JsonArray listSubgenres = resultObj2.get("LIST_SUBGENRE").getAsJsonArray();
            resultObj2.remove("LIST_SUBGENRE");
            if (listSubgenres != null && listSubgenres.size() > 0) {
                for (JsonElement je : listSubgenres) {
                    JsonObject jo = (JsonObject) je;
                    if (jo != null && jo.get("word") != null) {
                        String as = jo.get("word").getAsString();

                        // last sub_genre filter
                        as = StringUtil.filterLastGenre(as);

                        if (!"".equals(as.trim())) {
                            subgenreArr.add(as);
                        }
                    }
                }
                if (subgenreArr != null) {
                    subgenreStr = subgenreArr.toString();
                    subgenreStr = StringUtil.removeBracket(subgenreStr);
                }
                resultObj2.addProperty("META_SUBGENRE", subgenreStr);
            }
        }
        return resultObj2;
    }

    @Override
    public JsonObject getSubgenresStringForJson(int itemIdx, JsonObject resultObj2) {
        //resultObj2 = this.getSubgenres(itemIdx, resultObj2);
        Set subgenreArr = new HashSet();
        String subgenreStr = "";

        if (resultObj2 != null && resultObj2.get("LIST_SUBGENRE") != null) {
            JsonArray listSubgenres = resultObj2.get("LIST_SUBGENRE").getAsJsonArray();
            resultObj2.remove("LIST_SUBGENRE");
            if (listSubgenres != null && listSubgenres.size() > 0) {
                for (JsonElement je : listSubgenres) {
                    JsonObject jo = (JsonObject) je;
                    if (jo != null && jo.get("word") != null) {
                        String as = jo.get("word").getAsString();
                        if (!"".equals(as.trim())) {
                            subgenreArr.add(as);
                        }
                    }
                }
                if (subgenreArr != null) {
                    subgenreStr = subgenreArr.toString();
                    subgenreStr = StringUtil.removeBracket(subgenreStr);
                }
                resultObj2.addProperty("META_SUBGENRE", subgenreStr);
            }
        }
        return resultObj2;
    }


    @Override
    public String getMovieGenreFromCcubeContents(int itemIdx) {
        ItemsMetas req = new ItemsMetas();
        req.setIdx(itemIdx);
        List<ItemsMetas> resultMovieMeta = itemsMetasMapper.getItemsMetasByIdx(req);
        String movieGenre = "";
        if (resultMovieMeta != null) {
            for (ItemsMetas mm : resultMovieMeta) {
                //System.out.println("#TMP::mm:"+mm.toString());
                if (mm != null && mm.getMtype() != null && "genre".equals(mm.getMtype()) && mm.getMeta() != null) {
                    movieGenre = mm.getMeta();
                }
            }
        }
        return movieGenre;
    }

    private String getMovieSubGenreData(int itemIdx) {
        ItemsMetas req = new ItemsMetas();
        req.setIdx(itemIdx);
        List<ItemsMetas> resultMovieMeta = itemsMetasMapper.getItemsMetasByIdx(req);
        String result = "";
        if (resultMovieMeta != null) {
            for (ItemsMetas mm : resultMovieMeta) {
                //System.out.println("#TMP::mm:"+mm.toString());
                if (mm != null && mm.getMtype() != null && "subgenreMix2".equals(mm.getMtype()) && mm.getMeta() != null) {
                    System.out.println("#ELOG mtype:"+mm.getMtype()+ "  / meta:"+mm.getMeta());

                    //result = mm.getMeta();
                    String resultGenreMix = (StringUtil.filterSubgenreMix(mm.getMeta()));
                    result = resultGenreMix;
                }
                if (mm != null && mm.getMtype() != null && "subgenreOrgin2".equals(mm.getMtype()) && mm.getMeta() != null) {
                    if (!"".equals(result)) {
                        result = result + ", ";
                    }
                    result += mm.getMeta();
                }
            }
        }
        return result;
    }

    private List<DicGenreWords> getDicGenreKeywordsOneGenre(String genre, List<DicGenreWords> origArr) {
        List<DicGenreWords> dicGenreKeys = dicKeywordsMapper.getDicGenreKeywordsByGenre(genre);
        if (origArr == null) origArr = new ArrayList<DicGenreWords>();

        for(DicGenreWords dgw : dicGenreKeys) {
            origArr.add(dgw);
        }

        return origArr;
    }

    @Override
    public List<String> getGenreWordsListByGenre(String genre) {
        List<String> result = null;
        List<DicGenreWords> dicGenreKeys = new ArrayList();
        if (genre.trim().contains(" ")) {
            String genres[] = genre.trim().split(" ");
            for (String genreOne : genres) {
                dicGenreKeys = this.getDicGenreKeywordsOneGenre(genreOne, dicGenreKeys);
            }
        } else {
            dicGenreKeys = dicKeywordsMapper.getDicGenreKeywordsByGenre(genre);
        }

        if (dicGenreKeys != null) {
            result = new ArrayList();

            for (DicGenreWords dw : dicGenreKeys) {
                String ts = dw.getWord();
                result.add(ts);
            }
        }

        return result;
    }

    @Override
    public List<String> getNaverKindWordsByList(List<String> keywordList, int limit) throws Exception {
        List<String> result = new ArrayList();
        if (keywordList != null && keywordList.size() > 0) {

            int cnt = 0;
            for(String key : keywordList) {
                if (cnt < limit) {
                    result = this.getNaverKindWords(key, result);
                    cnt = result.size();
                }
            }
        }

        result = StringUtil.getCuttedArrayByLimit(result, limit);

        return result;
    }

    @Override
    public List<String> getNaverKindWords(String keyword, List<String> origArr) throws Exception {
        String reqUrl = crawl_naver_kordic_url;
        reqUrl = reqUrl.replace("#KEYWORD", URLEncoder.encode(keyword, "utf-8"));

        Map<String, Object> resultMap2 = HttpClientUtil.reqGetHtml(reqUrl, null
                , Charset.forName("utf-8"),null, "bypass");

        if (resultMap2 != null && resultMap2.get("resultStr") != null) {
            String result2 = resultMap2.get("resultStr").toString();
            //System.out.println("#ELOG:getNaverKindWords result:"+result2);
            String result22 = "";
            if (!"".equals(result2.trim())) {
                result22 = JsoupUtil.getTaggedValueAll(result2, ".syn .syno");
                result22 = CommonUtil.removeNumber(result22.trim());
                if (!"".equals(result22) && !"_FAIL".equals(result22)) {
                    String[] result3 = result22.split(" ");
                    if (result3 != null && result3.length > 0) {

                        for (String rs : result3) {
                            origArr.add(rs);
                        }
                    }
                }
            }
        }

        return origArr;
    }

    @Override
    public JsonObject getItemsMetasByItemIdxForInsert(int itemIdx) {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
        origTypes.add("LIST_NOT_MAPPED");
        origTypes.add("WORDS_GENRE");
        origTypes.add("WORDS_SNS");
        origTypes.add("WORDS_ASSOC");
        origTypes.add("LIST_SUBGENRE");
        origTypes.add("LIST_SEARCHKEYWORDS");
        origTypes.add("LIST_RECO_TARGET");
        origTypes.add("LIST_RECO_SITUATION");

        JsonObject resultObj = getItemsMetasByIdx(itemIdx, origTypes, "Y");

        JsonObject resultObj2 = getItemsMetasDupByItemIdx(resultObj, itemIdx, true);
        return resultObj2;
    }

    @Override
    public JsonObject getItemsMetasByItemIdxForUpdate(int itemIdx, List<String> origTypes) {
        JsonObject resultObj = getItemsMetasByIdx(itemIdx, origTypes, "S");

        //JsonObject resultObj2 = getItemsMetasDupByItemIdx(resultObj, itemIdx);
        return resultObj;
    }

    private JsonObject setEmptyMetas(JsonObject reqObj, List<String> origTypes) {
        // 鍮좎쭊 type�� 怨듬갚�씠�씪�룄 梨꾩썙以��떎
        if(reqObj != null) {
            for(String type : origTypes) {
                if(reqObj.get(type) == null) {
                    reqObj.add(type, new JsonArray());
                    //System.out.println("#reqObj add new JsonArray:"+type);
                } else {
                    //System.out.println("#reqObj:"+reqObj.get(type).toString());
                }
            }
        }

        return reqObj;
    }

    @Override
    public JsonObject getItemsMetasDupByItemIdx(JsonObject resultObj, int itemIdx, boolean isColorCode) {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHENERA");
        origTypes.add("METASWHENHIS");
        origTypes.add("METASWHENETC");

        origTypes.add("METASWHERE");
        origTypes.add("METASWHERENATION");
        origTypes.add("METASWHERECITY");
        origTypes.add("METASWHERESIGHTS");
        origTypes.add("METASWHEREETC");

        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
        origTypes.add("METASCHARACTER");

        origTypes.add("METASINT");

        origTypes.add("LIST_NOT_MAPPED");
        origTypes.add("WORDS_GENRE");
        origTypes.add("WORDS_SNS");
        origTypes.add("WORDS_ASSOC");
        origTypes.add("LIST_SUBGENRE");
        origTypes.add("LIST_SEARCHKEYWORDS");
        origTypes.add("LIST_RECO_TARGET");
        origTypes.add("LIST_RECO_SITUATION");

        JsonObject oldResultObj = getOldItemsMetasByIdx(itemIdx, origTypes);
        logger.debug("[********] 11 itemIdx = " + itemIdx);
        logger.debug("[********] 11 origTypes = " + origTypes);
        logger.debug("[********] 11 oldResultObj = " + oldResultObj);
        //System.out.println("#oldItemsMetas:" + oldResultObj.toString());

        for (String type : origTypes) {
            logger.debug("\n\n[********] 12 type = " + type);
            JsonArray oldTypesArr = null;
            if (oldResultObj != null && oldResultObj.get(type) != null) oldTypesArr = (JsonArray) oldResultObj.get(type);
            JsonArray newTypesArr = null;
            if (resultObj != null && resultObj.get(type) != null) newTypesArr = (JsonArray) resultObj.get(type);

            JsonArray typeResultArr1 = getCombinedJsonArray(type, oldTypesArr, newTypesArr, isColorCode);
            System.out.println("#TLOG:resultMetasCheckDup for type("+type+") data:"+typeResultArr1.toString());
            logger.debug("[********] 13 typeResultArr1.toString() = " + typeResultArr1.toString());

            JsonArray typeResultArr = null;
            if(!"LIST_SEARCHKEYWORDS".equals(type) && !"WORDS_SNS".equals(type)) {
                //&& !"LIST_SUBGENRE".equals(atype)) {
                typeResultArr = this.getRemoveDupTargetMetasArray(typeResultArr1);
                //System.out.println("#ELOG.destArr(JsonObject): datas::"+destArr2.toString());
                logger.debug("[********] 14_1 typeResultArr.toString() = " + typeResultArr.toString());
            } else {
                typeResultArr = this.getRemoveDupTargetMetasArrayOnlyString(typeResultArr1);
                //System.out.println("#ELOG.destArr(String): datas::"+destArr2.toString());
//                logger.debug("[********] 14_2 typeResultArr.toString() = " + typeResultArr.toString());
            }

            if ("LIST_NOT_MAPPED".equals(type)) {
                JsonArray sortedJsonArray = JsonUtil.getSortedJsonArray(typeResultArr, "word", "ratio", "type", 100);
                if (sortedJsonArray != null && sortedJsonArray.size() > 0) typeResultArr = sortedJsonArray;
            }

            if("LIST_SUBGENRE".equals(type)) {
                JsonArray listSubgenre2 = new JsonArray();
                if (typeResultArr != null && typeResultArr.size() > 0) {
                    // 留덉�留� �젣�쇅 ���긽 �븘�꽣 �쟻�슜
                    for (JsonElement je : typeResultArr) {
                        JsonObject jo1 = (JsonObject) je;
                        boolean isValid = StringUtil.filterLastTagValid(jo1);

                        if (isValid) {
                            //System.out.println("#ELOG metas jo: added:"+jo1.toString());
                            listSubgenre2.add(jo1);
                        }
                    }
                }
                typeResultArr = null;
                //typeResultArr = new JsonArray();
                typeResultArr = listSubgenre2;
            }

//            logger.debug("[********] 15 resultObj.toString() = " + resultObj.toString());
            if (resultObj.get(type) != null) resultObj.remove(type);
            resultObj.add(type, typeResultArr);
//            logger.debug("[********] 16 resultObj.toString() = " + resultObj.toString());
        }

        return resultObj;
    }

    private JsonArray getCombinedJsonArray(String type, JsonArray oldArr, JsonArray newArr, boolean isColorCode) {
        //System.out.println("#TLOG:oldArr:"+oldArr.toString());
        if("LIST_NOT_MAPPED".equals(type)) {
            isColorCode = false;
        }

        JsonArray resultArr = new JsonArray();

        System.out.println("#MLOG.getCombinedJsonArray req.type:"+type);
        if ((type.contains("METAS") || type.contains("LIST")) && !type.equals("LIST_SEARCHKEYWORDS")) {
            for (JsonElement oje : oldArr) {
                //System.out.println("#TOLOG:oje:" + oje.toString());
                try {
                    JsonObject ojo = (JsonObject) oje;
                    String oldWord = ojo.get("word").getAsString();

                    boolean isMatch = false;
                    for (JsonElement nje : newArr) {
                        JsonObject njo = (JsonObject) nje;
                        String newWord = njo.get("word").getAsString();
                        if (oldWord.equals(newWord)) {
                            // OLD:NEW 媛숈� 寃껋씠 �엳�쑝硫� type=dup 濡� ���옣
                            //System.out.println("#njo:"+njo.toString());

                            JsonObject newItem = new JsonObject();
                            newItem.addProperty("word", newWord);
                            //if ("get".equals(when)) {
                            if (isColorCode) {
                                newItem.addProperty("type", "dup");
                            } else {
                                newItem.addProperty("type", "");
                            }
                            //} else {
                            //    newItem.addProperty("type", "ext");
                            //}
                            newItem.addProperty("ratio", njo.get("ratio").getAsDouble());
                            resultArr.add(newItem);
                            isMatch = true;
                        }
                    }
                    if (!isMatch) {
                        // OLD:NEW 媛숈� 寃껋씠 �뾾�쑝硫� type=ext 濡� ���옣
                        JsonObject newItem = new JsonObject();
                        newItem.addProperty("word", oldWord);
                        //if ("get".equals(when)) {
                        //newItem.addProperty("type", "ext");
                        if (isColorCode) {
                            newItem.addProperty("type", "ext");
                        } else {
                            newItem.addProperty("type", "");
                        }
                        //} else {
                        //    newItem.addProperty("type", "new");
                        //}
                        newItem.addProperty("ratio", ojo.get("ratio").getAsDouble());
                        resultArr.add(newItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("#ERR:getCombinedJsonArray error! oldArr(oje):"+oje.toString());
                }
            }

            for (JsonElement njje : newArr) {
                try {
                    JsonObject njjo = (JsonObject) njje;
                    String nj_word = njjo.get("word").getAsString();

                    boolean isExist = false;
                    for (JsonElement re : resultArr) {
                        JsonObject ro = (JsonObject) re;
                        String re_word = ro.get("word").getAsString();
                        if (nj_word.equals(re_word)) {
                            isExist = true;
                        }
                    }
                    if (!isExist) {
                        // NEW:RESLT 媛숈� 寃껋씠 �뾾�쑝硫� type=new 濡� ���옣
                        //System.out.println("#njjo:"+njjo.toString());

                        JsonObject newItem = new JsonObject();
                        newItem.addProperty("word", nj_word);
                        //newItem.addProperty("type", "new");
                        if (isColorCode) {
                            newItem.addProperty("type", "new");
                        } else {
                            newItem.addProperty("type", "");
                        }
                        newItem.addProperty("ratio", njjo.get("ratio").getAsDouble());
                        resultArr.add(newItem);
                    }
                } catch (Exception e) {
                    logger.error("#ERR.getCombinedJsonArray error! type:"+type+" / newArr(njje):"+njje.toString());
                    e.printStackTrace();
                }
            }
        } else {
            /* 寃��깋�뼱�뒗 �떒�닚 JsonArray */
            //System.out.println("#TLOG:LIST_SEARCHKEYWORDS :: oldArr:"+oldArr.toString() + "/newArr:"+newArr.toString());
            for (JsonElement je : oldArr) {
                String os = je.getAsString();
                resultArr.add(os);
            }
            for (JsonElement nje : newArr) {
                //System.out.println("#ELOG type:"+type+" / nje:"+nje);
                String ns = nje.getAsString();
                ns = ns.trim();
                ns = ns.replace("\"","");
                resultArr.add(ns);
            }
        }
        return resultArr;
    }

    @Override
    public int restorePrevTag(int itemIdx) {
        int rt = 0;
        if (itemIdx > 0) {
            /*
            // S -> C
            ItemsTags reqO = new ItemsTags();
            reqO.setIdx(itemIdx);
            reqO.setStat("S");
            int oldTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqO);

            // Y -> D
            ItemsTags reqC = new ItemsTags();
            reqC.setIdx(itemIdx);
            reqC.setStat("Y");
            int curTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqC);


            // S -> C
            if (oldTagIdx > 0) {
                ItemsTags reqOO = new ItemsTags();
                reqOO.setIdx(itemIdx);
                reqOO.setStat("C");
                reqOO.setTagidx(oldTagIdx);
                int rt1 = itemsTagsMapper.uptItemsTagsKeysStat(reqOO);
                rt += rt1;
            }

            // Y -> D
            // curTagIdx�뒗 C濡� �듅�씤痍⑥냼 泥섎━
            if (curTagIdx > 0) {
                ItemsTags reqCC = new ItemsTags();
                reqCC.setIdx(itemIdx);
                reqCC.setStat("D");
                reqCC.setTagidx(curTagIdx);
                int rt2 = itemsTagsMapper.uptItemsTagsKeysStat(reqCC);
                rt += rt2;
            }
            */

            // 理쒖큹 �듅�씤 李⑥닔
            ItemsTags reqO = new ItemsTags();
            reqO.setIdx(itemIdx);
            reqO.setStat("S");
            int firstTaggedIdx = itemsTagsMapper.getMinTagsIdxByItemIdx(reqO);

            // 理쒖큹 �듅�씤 李⑥닔�� 留덉�留� 李⑥닔 (�듅�씤 �뿬遺� 臾닿�) 媛� �떎瑜� 寃쎌슦留�  留덉�留� 李⑥닔 �궘�젣
            ItemsTags req1 = new ItemsTags();
            req1.setIdx(itemIdx);
            int maxTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(req1);
            if (maxTagIdx != firstTaggedIdx) {
                req1.setTagidx(maxTagIdx);
                rt = itemsTagsMapper.delItemsTagsKeys(req1);
            }

            int curTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqO);

            if (rt > 0) {
                Items reqIt = new Items();
                reqIt.setIdx(itemIdx);
                //int rtx = itemsMapper.uptItemsTagcntMinus(reqIt);

                //items_hist�뿉 �벑濡� for �넻怨�
                Items itemInfo = itemsService.getItemsByIdx(reqIt);
                String movietitle = "";
                movietitle = (itemInfo != null && itemInfo.getTitle() != null) ? itemInfo.getTitle().trim() : "";
                int rthist = itemsService.insItemsHist(itemIdx, "meta", "RECV", movietitle, "RESTORE_META", curTagIdx);

                // items �쓽 tagcnt瑜� 留덉�留� tagidx 濡� �닔�젙
                //int maxTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqO);
                reqIt.setTagcnt(curTagIdx);
                //reqIt.setRegid(userId);		//userId : 로그인 중인 사용자정보 저장
                int rtu = itemsService.uptItemsTagcnt(reqIt);
            }
        }
        return rt;
    }

    @Override
    public JsonObject getArraysByTypeFromInputItems(String items) {
        JsonObject resultObj = null;
        Map<String, Object> tmpMapArrays = new HashMap();

        try {
            resultObj = new JsonObject();
            JsonParser parser = new JsonParser();
            //JsonArray reqArr = (JsonArray) parser.parse(items);
            JsonElement tradeElement = null;
            try {
                tradeElement = parser.parse(items);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("#MLOG /pop/meta/upt/array items parse error! items:"+items);
            }
            JsonArray reqArr = tradeElement.getAsJsonArray();

            Set<String> typesArr = new HashSet<String>();

            if (reqArr != null) {
                //System.out.println("#reqArr:"+reqArr.toString());

                for (JsonElement je : reqArr) {
                    JsonObject nitem = (JsonObject) je;

                    if (nitem.get("type") != null) {
                        String tmpMapArrayName = String.valueOf(nitem.get("type").getAsString().toUpperCase());
                        String metasTypes = "WHEN,WHENERA,WHENHIS,WHENETC, WHERE,WHERENATION,WHERECITY,WHERESIGHTS,WHEREETC, WHAT, WHO, EMOTION, CHARACTER,INT";
                        if(metasTypes.contains(tmpMapArrayName)) {
                            tmpMapArrayName = "METAS" + tmpMapArrayName;
                        }
                        // ui�뿉�꽌�뒗 listsearchkeywords �삎�깭濡� 怨듬갚�뾾�씠 �쟾�넚�맖
                        if(tmpMapArrayName.startsWith("LIST")) {
                            tmpMapArrayName = tmpMapArrayName.replace("LIST", "LIST_");

                            // LIST_NOT_MAPPED는 LIST__NOT_MAPPED가 되어버려서 __를 _로 수정
                            tmpMapArrayName = tmpMapArrayName.replace("LIST__", "LIST_");
                        }
                        if(tmpMapArrayName.contains("RECO")) {
                            tmpMapArrayName = tmpMapArrayName.replace("RECO", "RECO_");
                        }

                        typesArr.add(tmpMapArrayName);

                        List<Map<String, Object>> tmpArr = (List<Map<String, Object>>) tmpMapArrays.get(tmpMapArrayName);
                        if (tmpArr == null) tmpArr = new ArrayList();

                        Map<String, Object> newItem = new HashMap();
                        newItem.put("type", tmpMapArrayName);
                        newItem.put("meta", nitem.get("meta").getAsString());
                        newItem.put("target_meta", nitem.get("target_meta").getAsString());
                        newItem.put("action", nitem.get("action").getAsString());
                        tmpArr.add(newItem);

                        if (tmpMapArrays.get(tmpMapArrayName) != null) {
                            tmpMapArrays.remove(tmpMapArrayName);
                        }
                        tmpMapArrays.put(tmpMapArrayName, tmpArr);

                        if (tmpMapArrays.get("typesArr") != null) {
                            tmpMapArrays.remove("typesArr");
                        }
                        tmpMapArrays.put("typesArr", typesArr);
                    }
                }

                System.out.println("#tmpMapArrays::" + tmpMapArrays.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(tmpMapArrays != null) {
            resultObj = new Gson().toJsonTree(tmpMapArrays).getAsJsonObject();
        }
        return resultObj;
    }

    public Integer processMetaObjectByTypes(JsonObject origMetasArraysByType, JsonObject actionItemsArraysByType, JsonArray typesArr
            , int itemid, int curTagIdx, String userId) {	//userId : 로그인 중인 사용자정보 저장
        int rt = 0;
        //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  " + DateUtils.getLocalDateTime3() + " ItemsTagsService.processMetaObjectByTypes start");
        //System.out.println(" - origMetasArraysByType = " + origMetasArraysByType.toString());
        //System.out.println(" - actionItemsArraysByType = " + actionItemsArraysByType.toString());
        //System.out.println(" - typesArr = " + typesArr.toString());
        //System.out.println(" - itemid = " + itemid);
        //System.out.println(" - curTagIdx = " + curTagIdx);
        //System.out.println(" - userId = " + userId);	//userId : 로그인 중인 사용자정보 저장

        /* ���엯 蹂� �븸�뀡 �븘�씠�뀥 以� add,mod�뒗 �궗�쟾�뿉 異붽� */
        ArrayList<String> dicTypes = new ArrayList<String>();
        dicTypes.add("METASWHEN");
        dicTypes.add("METASWHENERA");
        dicTypes.add("METASWHENHIS");
        dicTypes.add("METASWHENETC");

        dicTypes.add("METASWHERE");
        dicTypes.add("METASWHERENATION");
        dicTypes.add("METASWHERECITY");
        dicTypes.add("METASWHERESIGHTS");
        dicTypes.add("METASWHEREETC");

        dicTypes.add("METASWHO");
        dicTypes.add("METASWHAT");
        dicTypes.add("METASEMOTION");
        dicTypes.add("METASCHARACTER");


        dicTypes.add("METASINT");

        //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes metaDic start");
        for (String dicType : dicTypes) {
            JsonArray dicActionArr = null;
            if (actionItemsArraysByType.get(dicType) != null) {
                dicActionArr = (JsonArray) actionItemsArraysByType.get(dicType);
                System.out.println("#TLOG:dicActionArr:"+dicActionArr.toString());

                for (JsonElement je : dicActionArr) {
                    JsonObject jo = (JsonObject) je;

                    if(jo != null && jo.get("action") != null
                            && jo.get("meta") != null && !"".equals(jo.get("meta").getAsString().trim())
                            && jo.get("meta").getAsString().trim().length() > 0
                    ) {
                        //System.out.println("#TLOG:putDicKeyword:: jo:"+jo.toString());
                        DicKeywords newKey = new DicKeywords();

                        switch(jo.get("action").getAsString()) {
                            case "add":
                                newKey.setType(dicType.replace("METAS", ""));
                                newKey.setKeyword(jo.get("meta").getAsString());
                                newKey.setRatio(0.0);
                                int rtd = dicService.insDicKeywords(newKey);
                                break;
                            case "mod" :
                                newKey.setType(dicType.replace("METAS", ""));
                                newKey.setKeyword(jo.get("meta").getAsString());
                                newKey.setToword(jo.get("target_meta").getAsString());
                                newKey.setRatio(0.0);
                                int rtd2 = dicService.insDicKeywords(newKey);
                                break;
                        }
                    }
                }
            }
        }
        //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes metaDic end");

        //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for start");
        if (typesArr != null && origMetasArraysByType != null && actionItemsArraysByType != null) {
            System.out.println("#ELOG.origMetasArraysByType:"+origMetasArraysByType.toString());

            //for (JsonElement atype1 : typesArr) {
            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes setEmptyMetas start");
            JsonObject origMetasArraysByType2 = setEmptyMetas(origMetasArraysByType, this.getOrigTypes());
            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes setEmptyMetas end");

            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for start");
            for (String atype : this.getOrigTypes()) {
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " start");
                //String atype = atype1.getAsString();
                atype = atype.replace("\"", "");
                atype = atype.toUpperCase();

                JsonArray origMetaArr = null;
                JsonArray changeMetaArr = null;
                String destMeta = "";

                if (origMetasArraysByType2.get(atype) != null) {
//                    System.out.println("#Change type(" + atype + ") orig meta::"+origMetasArraysByType2.toString());
                    origMetaArr = (JsonArray) origMetasArraysByType2.get(atype);
//                    System.out.println("#Change type(" + atype + ") orig meta datas::" + origMetaArr);
                }
                logger.debug("[********] 14 JsonArray origMetaArr = " + origMetaArr.toString());

                if (actionItemsArraysByType.get(atype) != null) {
                    changeMetaArr = (JsonArray) actionItemsArraysByType.get(atype);
//                    System.out.println("#Change type(" + atype + ") changing meta datas to::" + changeMetaArr);
                }

                if (changeMetaArr != null) {
                    //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " - changeMetaArr not null");
                    /* get meta data for saving */
                    JsonArray destArr = null;
                    JsonArray destArr2 = null;
                    if(!"LIST_SEARCHKEYWORDS".equals(atype) && !"WORDS_SNS".equals(atype)
                            && !"LIST_AWARD".equals(atype)
                    ) {
                        //&& !"LIST_SUBGENRE".equals(atype)) {
                        destArr = this.getTargetMetasArray(atype, origMetaArr, changeMetaArr);
                        destArr2 = this.getRemoveDupTargetMetasArray(destArr);
                        System.out.println("#ELOG.destArr(JsonObject): datas::"+destArr2.toString());
                    } else {
                        //destArr = this.getTargetMetasArrayOnlyString(atype, origMetaArr, changeMetaArr);
                        //destArr2 = this.getRemoveDupTargetMetasArrayOnlyString(destArr);
                        //System.out.println("#ELOG.destArr(String): datas::"+destArr2.toString());

                        //2019.11.21 origMetaArr를 string array 에서 json array로 해야 한다. {"word":"테스트키워드1","ratio":0.0}
                        System.out.println(1);
                        if(origMetaArr.size()>0) {
                            if(origMetaArr.get(0).isJsonPrimitive()) {
                                JsonArray tmpJsonArray = new JsonArray();
                                for(JsonElement tmpJe : origMetaArr) {
                                    JsonObject tmpJsonObject = new JsonObject();
                                    tmpJsonObject.addProperty("word", tmpJe.getAsString());
                                    tmpJsonObject.addProperty("ratio", "0.0");
                                    tmpJsonArray.add(tmpJsonObject);
                                }

                                origMetaArr = tmpJsonArray;
                            }
                        }

                        destArr = this.getTargetMetasArrayOnlyString(atype, origMetaArr, changeMetaArr);
                        destArr2 = this.getRemoveDupTargetMetasArrayOnlyString(destArr);
                        System.out.println("#ELOG.destArr(String): datas::"+destArr2.toString());
                    }
                    if (destArr != null) {
                        destMeta = destArr2.toString();

                        System.out.println("#MLOG DestArr cause changed for type:" + atype + " :: " + destMeta.toString());

                        /* 湲곗〈 硫뷀��� 異붽� �븸�뀡�븘�씠�뀥�뱾�씠 諛섏쁺�맂 TYPE(ex> METASWHEN) 蹂� 硫뷀�JsonArray媛� 以�鍮꾨릺硫� �쁽�옱 tagIdx瑜� 湲곗��쑝濡� �뾽�뜲�씠�듃 */
                        if (!atype.toUpperCase().contains("AWARD")) {
                            ItemsTags reqMeta = new ItemsTags();
                            reqMeta.setIdx(itemid);
                            reqMeta.setTagidx(curTagIdx);
                            reqMeta.setMtype(atype);
                            reqMeta.setMeta(destMeta);
                            reqMeta.setRegid(userId);	//userId : 로그인 중인 사용자정보 저장

                            //System.out.println("#MLOG change insItemsTagsMetas data:"+reqMeta.toString());

                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " insItemsTagsMetas before : mtype,meta = " + atype+","+destMeta);

                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " insItemsTagsMetas before : reqMeta = " + reqMeta);
                            rt = this.insItemsTagsMetas(reqMeta);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " " + " after : rt = " + rt);
                        } else {
                            // AWARD�쓽 寃쎌슦 items_metas�뿉 湲곗〈硫뷀�瑜� �쑀吏��븳�떎 18.05.15
                            ItemsMetas reqM = new ItemsMetas();
                            reqM.setIdx(itemid);
                            reqM.setMtype("award");
                            reqM.setMeta(destMeta);
                            reqM.setRegid(serverid);

                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " insItemsTagsMetas before : mtype,meta = " + atype+","+destMeta);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " insItemsTagsMetas before : reqM = " + reqM);
                            rt = itemsMetasMapper.insItemsMetas(reqM);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " insItemsTagsMetas after : rt = " + rt);
                            System.out.println("#insItemsMetas for AWARD1:"+reqM.toString());
                        }
                    } else {
                        System.out.println("#MLOG DestArr null for type:" + atype);
                    }
                } else {
                    //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " - changeMetaArr null");
                    if (origMetaArr != null) {
                        destMeta = origMetaArr.toString();
                        /* 湲곗〈 硫뷀��뿉�꽌 異붽� �븸�뀡 �븘�씠�뀥�뱾�씠 �뾾�뒗 寃쎌슦 湲곗〈 硫뷀� 洹몃�濡� �쁽�옱 tagIdx�뿉 �뾽�뜲�씠�듃 */
                        if (!atype.toUpperCase().contains("AWARD")) {
                            ItemsTags reqMeta = new ItemsTags();
                            reqMeta.setIdx(itemid);
                            reqMeta.setTagidx(curTagIdx);
                            reqMeta.setMtype(atype);	//MTYPE
                            reqMeta.setMeta(destMeta);	//통째로 META 컬럼에 저장
                            reqMeta.setRegid(userId);	//userId : 로그인 중인 사용자정보 저장

                            //System.out.println("#MLOG uptItemsTagsMetas data:"+reqMeta.toString());
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes 메타 반복문 : atype = " + atype + " insItemsTagsMetas before : mtype,meta = " + atype+","+destMeta);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes 메타 반복문 : atype = " + atype + " insItemsTagsMetas before : reqMeta = " + reqMeta);
                            rt = this.insItemsTagsMetas(reqMeta);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes 메타 반복문 : atype = " + atype + " insItemsTagsMetas after : rt = " + rt);
                        } else {
                            // AWARD�쓽 寃쎌슦 items_metas�뿉 湲곗〈硫뷀�瑜� �쑀吏��븳�떎 18.05.15
                            ItemsMetas reqM = new ItemsMetas();
                            reqM.setIdx(itemid);
                            reqM.setMtype("award");
                            reqM.setMeta(destMeta);
                            reqM.setRegid(serverid);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes 메타 반복문 : atype = " + atype + " insItemsTagsMetas before : mtype,meta = " + atype+","+destMeta);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes 메타 반복문 : atype = " + atype + " insItemsTagsMetas before : reqM = " + reqM);
                            rt = itemsMetasMapper.insItemsMetas(reqM);
                            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes 메타 반복문 : atype = " + atype + " insItemsTagsMetas after : rt = " + rt);

                            //System.out.println("#insItemsMetas for AWARD2:"+reqM.toString());
                        }
                    }
                }
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for : atype = " + atype + " end");
            }
            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for end");
        }
        //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.processMetaObjectByTypes meta for end");
        return rt;
    }

    private List<String> getOrigTypes() {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHENERA");
        origTypes.add("METASWHENHIS");
        origTypes.add("METASWHENETC");

        origTypes.add("METASWHERE");
        origTypes.add("METASWHERENATION");
        origTypes.add("METASWHERECITY");
        origTypes.add("METASWHERESIGHTS");
        origTypes.add("METASWHEREETC");

        origTypes.add("METASINT");

        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
        origTypes.add("METASCHARACTER");
        origTypes.add("LIST_NOT_MAPPED");
        //origTypes.add("WORDS_GENRE");
        //origTypes.add("WORDS_SNS");
        //origTypes.add("WORDS_ASSOC");
        origTypes.add("LIST_SUBGENRE");
        origTypes.add("LIST_SEARCHKEYWORDS");
        origTypes.add("LIST_RECO_TARGET");
        origTypes.add("LIST_RECO_SITUATION");

        origTypes.add("LIST_AWARD");

        origTypes.add("METASINT");

        /* ���엯 蹂� �븸�뀡 �븘�씠�뀥 以� add,mod�뒗 �궗�쟾�뿉 異붽�
        ArrayList<String> dicTypes = new ArrayList<String>();
        dicTypes.add("METASWHEN");
        dicTypes.add("METASWHERE");
        dicTypes.add("METASWHO");
        dicTypes.add("METASWHAT");
        dicTypes.add("METASEMOTION");
        dicTypes.add("METASCHARACTER"); */
        return origTypes;
    }

    @Override
    public int changeMetasArraysByTypeFromInputItems (int itemid, String items, String duration, String sendnow, String userId) {	//userId : 로그인 중인 사용자정보 저장

        //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems 시작");
        //System.out.println(" - itemid = " + itemid);
        //System.out.println(" - items = " + items);
        //System.out.println(" - duration = " + duration);
        //System.out.println(" - sendnow = " + sendnow);
        //System.out.println(" - userId = " + userId);	//userId : 로그인 중인 사용자정보 저장

        int rt = 0;
        //int curTagIdx = this.getCurrTagsIdxForInsert(itemid);
        //int curTagIdx = this.getCurrTagsIdxForSuccess(itemid);
        List<ItemsTags> tagIdxArr = this.getTagCntInfo(itemid);
        //List<ItemsTags> tagIdxArr = this.getSuccessTagidxListDesc(itemid);
        int curTagIdx = 0;
        if (tagIdxArr != null && tagIdxArr.size() > 0) {
            curTagIdx = tagIdxArr.get(0).getTagidx();
        }
        System.out.println("#MLOG /pop/meta/upt/array curTagIdx:"+curTagIdx);

        try {
            ItemsTags lastTag = this.getLastTagSuccessInfo(itemid);
            if (lastTag != null) {
                System.out.println("#lastTag:" + lastTag.toString());
            }
            //else {
            //    lastTag = this.getLastTagCntInfo(itemid);
            //    System.out.println("#lastTag:" + lastTag.toString());
            //}

            Items reqIt = null;

            /* get action TYPE to Arrays */
            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - input asfasfd = " + items);
            JsonObject actionItemsArraysByType = this.getArraysByTypeFromInputItems(items);
            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - output asfasfd = " + actionItemsArraysByType.toString());
            System.out.println("#actionItemsArraysByType:"+actionItemsArraysByType.toString());
            JsonArray typesArr = null;
            if (actionItemsArraysByType.get("typesArr") != null)
                typesArr = (JsonArray) actionItemsArraysByType.get("typesArr");
            //System.out.println("#typesArr:"+typesArr.toString());

            int tagcnt = 0;

            /* 기승인된 메타가 없을 경우 (최근 tagIdx가 미승인인 경우) 메타수정 후 상태변경, 승인 처리한다 */
            if(lastTag != null && !"S".equals(lastTag.getStat())) {
            	//System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - no gaseungin metas");

            	//System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - getItemsMetasByItemIdx bf");
            	JsonObject origMetasArraysByType = this.getItemsMetasByItemIdx(itemid, false);
            	//System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - getItemsMetasByItemIdx af : origMetasArraysByType = " + origMetasArraysByType.toString());

                // AWARD 泥섎━瑜� �쐞�빐 ITEMS_METAS�뿉�꽌 �씫�뼱�꽌 援ъ“泥댁뿉 異붽�
            	//System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - getAwardObject bf(call) : AWARD ~~~");

                origMetasArraysByType = this.getAwardObject(itemid, origMetasArraysByType);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - getAwardObject af : origMetasArraysByType = " + origMetasArraysByType.toString());

                System.out.println("#origMetasArraysByType:"+origMetasArraysByType.toString());

                /* action_item이 있는 경우 타입별 meta 수정 */
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes before(호출) action_item이 있는 경우 타입별 meta 수정");
                int rtm = this.processMetaObjectByTypes(origMetasArraysByType, actionItemsArraysByType, typesArr, itemid, curTagIdx, userId);	//userId : 로그인 중인 사용자정보 저장
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes after : rtm = " + rtm);
                //System.out.println(" - origMetasArraysByType " + origMetasArraysByType.toString());
                //System.out.println(" - actionItemsArraysByType " + actionItemsArraysByType.toString());
                //System.out.println(" - typesArr " + typesArr);
                //System.out.println(" - itemid " + itemid);
                //System.out.println(" - curTagIdx " + curTagIdx);

                /* 해당 items_tags_keys 를 승인으로 업데이트 한다 */
                ItemsTags reqConfirm = new ItemsTags();
                reqConfirm.setIdx(itemid);
                reqConfirm.setTagidx(curTagIdx);
                reqConfirm.setStat("S");
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - uptItemsTagsKeysStat before");
                int rts = this.uptItemsTagsKeysStat(reqConfirm);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - uptItemsTagsKeysStat after : rts = " + rts);

                /* 해당 items_stat 를 승인으로 업데이트 한다 */
                reqIt = new Items();
                reqIt.setIdx(itemid);
                reqIt.setStat("ST");
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat before");
                int rti = itemsMapper.insItemsStat(reqIt);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat after : rti = " + rti);

                rt = 1;

                tagcnt = curTagIdx + 1;
            } else {
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems -   added 2018.04.11");
                /* 이전 상태 코드 확인하여 승인완료(ST)가 아닌 경우 승인완료로 처리  added 2018.04.11 */
                String oldItemsStat = itemsMapper.getItemsStatByIdx(itemid);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - oldItemsStat " + oldItemsStat);
                if (!"ST".equals(oldItemsStat)) {
                     /* 해당 items_tags_keys 를 승인으로 업데이트 한다
                    ItemsTags reqConfirm = new ItemsTags();
                    reqConfirm.setIdx(itemid);
                    reqConfirm.setTagidx(curTagIdx);
                    reqConfirm.setStat("S");
                    int rts = this.uptItemsTagsKeysStat(reqConfirm);*/

                    /* 해당 items_stat 를 승인으로 업데이트 한다 */
                    reqIt = new Items();
                    reqIt.setIdx(itemid);
                    reqIt.setStat("ST");
                    //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat before");
                    int rti = itemsMapper.insItemsStat(reqIt);
                    //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat after : rti = " + rti);
                } else {
                    /* 기승인된 메타가 있을 경우 tagidx를 신규 생성한다..  18.05.16 */
                    curTagIdx =  this.getCurrTagsIdxForInsert(itemid);
                    //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - curTagIdx " + curTagIdx);

                    /* 해당 items_tags_keys 를 승인으로 업데이트 한다
                    ItemsTags reqConfirm = new ItemsTags();
                    reqConfirm.setIdx(itemid);
                    reqConfirm.setTagidx(curTagIdx);
                    reqConfirm.setStat("S");
                    int rts = this.uptItemsTagsKeysStat(reqConfirm);*/

                    /* 해당 items의 tagcnt를 최종 tagidx로 수정한다 */
                    // 아래 마지막 라인 참조
                    /* 해당 items_stat 를 승인으로 업데이트 한다 */
                    reqIt = new Items();
                    reqIt.setIdx(itemid);
                    reqIt.setStat("ST");
                    //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat before");
                    int rti = itemsMapper.insItemsStat(reqIt);
                    //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat after : rti = " + rti);
                }

                /* 기승인된 메타가 있을 경우, items_tags_metas 만 수정한다 */
                JsonObject origMetasArraysByType = this.getItemsMetasByItemIdxForUpdate(itemid, this.getOrigTypes());

                // AWARD 처리를 위해 ITEMS_METAS에서 읽어서 구조체에 추가
                origMetasArraysByType = this.getAwardObject(itemid, origMetasArraysByType);

                System.out.println("#origMetasArraysByType:"+origMetasArraysByType.toString());

                /* action_item이 있는 경우 타입별 meta 수정 */
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes before");
                int rtm = this.processMetaObjectByTypes(origMetasArraysByType, actionItemsArraysByType, typesArr, itemid, curTagIdx, userId);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes after : rtm = " + rtm);
                //System.out.println(" - origMetasArraysByType " + origMetasArraysByType.toString());
                //System.out.println(" - actionItemsArraysByType " + actionItemsArraysByType.toString());
                //System.out.println(" - typesArr " + typesArr);
                //System.out.println(" - itemid " + itemid);
                //System.out.println(" - curTagIdx " + curTagIdx);

                rt = 1;

                tagcnt = curTagIdx;
            }

            System.out.println("#ELOG./pop/meta/upt/array rt:"+rt);

            //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction befare");
            if (rt > 0) {
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0");
                /* 해당 items_tags_keys 를 승인으로 업데이트 한다  */
                ItemsTags reqConfirm = new ItemsTags();
                reqConfirm.setIdx(itemid);
                reqConfirm.setTagidx(curTagIdx);
                reqConfirm.setStat("S");
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - uptItemsTagsKeysStat before");
                //System.out.println(" - itemid = " + itemid);
                //System.out.println(" - curTagIdx = " + curTagIdx);
                //System.out.println(" - stat = S");
                int rts = this.uptItemsTagsKeysStat(reqConfirm);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - uptItemsTagsKeysStat after : rts = " + rts);
                //System.out.println(" - itemid = " + itemid);
                //System.out.println(" - curTagIdx = " + curTagIdx);
                //System.out.println(" - stat = S");

                //items_hist에 등록 for 통계
                reqIt = new Items();
                reqIt.setIdx(itemid);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - getItemsByIdx before");
                Items itemInfo = itemsService.getItemsByIdx(reqIt);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - getItemsByIdx after : itemInfo = " + itemInfo.toString());
                String movietitle = "";
                movietitle = (itemInfo != null && itemInfo.getTitle() != null) ? itemInfo.getTitle().trim() : "";
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - insItemsHist before");
                int rthist = itemsService.insItemsHist(itemid, "meta", "S", movietitle, "CONFIRM_META", itemid);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - insItemsHist after : rthist = " + rthist);

                // TagCnt 1 증가 // 일단 업데이트 구문으로 해결
                //int oldTagCnt = itemInfo.getTagcnt();
                //int newTagCnt = oldTagCnt + 1;
                //reqIt.setTagcnt(newTagCnt);

                /* 해당 items 정보를 변경한다.  tagcnt++,  duration */
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - target items change");
                if (!"".equals(duration)) reqIt.setDuration(duration);
                reqIt.setTagcnt(tagcnt);
                reqIt.setRegid(userId);		//userId : 로그인 중인 사용자정보 저장
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - uptItemsTagcnt before");
                int rtu = itemsService.uptItemsTagcnt(reqIt);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - uptItemsTagcnt after : rtu = " + rtu);
                logger.info("#MLOG:uptItemsTagcnt for itemIdx:"+itemid);

                /* �빐�떦 items�쓽 sched_target_content �썝臾몄쓣 紐⑤몢 �궘�젣�븳�떎  18.05.16 */
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - deleteSchedTargetContentOrigin before");
                int rtd = schedTriggerService.deleteSchedTargetContentOrigin(itemid);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - deleteSchedTargetContentOrigin after : rtd = " + rtd);
            }

            if ("Y".equals(sendnow.toUpperCase())) {
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction sendnow=Y");
                Map<String,Object> reqCcube = new HashMap();
                reqCcube.put("idx", itemid);
                reqCcube.put("regid", serverid);
                System.out.println(" - itemid = " + itemid);
                System.out.println(" - serverid = " + serverid);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - insCcubeOutput before");
                int rtc = ccubeService.insCcubeOutput(reqCcube);
                //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems - saveAction rt > 0 - insCcubeOutput after : rtc = " + rtc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("[********] " + DateUtils.getLocalDateTime3() + "  ItemsTagsService.changeMetasArraysByTypeFromInputItems end : rt = " + rt);
        return rt;
    }

    public JsonArray getTargetMetasArray(String type, JsonArray origArray, JsonArray changeArray) {
        try {

            for (JsonElement je : changeArray) {
                JsonObject jo = (JsonObject) je;

                String toAction = jo.get("action").getAsString();
                origArray = changeTargetMetasArray(toAction, jo, origArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return origArray;
    }

    public JsonArray getRemoveDupTargetMetasArray(JsonArray origArray) {
        logger.debug("[********] 14 getRemoveDupTargetMetasArray origArray.toString() = " + origArray.toString());
        Map<String, Object> wordSet = new HashMap();
        JsonArray resultArr = null;

        if (origArray != null) {
            try {
                resultArr = new JsonArray();;

                for (JsonElement je : origArray) {
                    JsonObject jo = (JsonObject) je;
                    logger.debug("[********] 14    0 jo.toString() = " + jo.toString());

                    String word1 = jo.get("word").getAsString();
                    word1 = word1.trim();

                    // Map의 key에 word를 등록하여 중복 제거
                    if (!"".equals(word1) && wordSet.get(word1) == null) {
                        logger.debug("[********] 14    1 jo.toString() = " + jo.toString());
                        resultArr.add(jo);
                        wordSet.put(word1, "exist");
                    } else {
                        logger.debug("[********] 14    2 jo.toString() = " + jo.toString());
                        System.out.println("#ELOG.metaChange skip by Duplicated word:"+word1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.debug("[********] 14 getRemoveDupTargetMetasArray wordSet.toString() = " + wordSet.toString());
        logger.debug("[********] 14 getRemoveDupTargetMetasArray resultArr.toString() = " + resultArr.toString());

        List<JsonObject>  resultArrList = new ArrayList<JsonObject>();
        for (JsonElement je : resultArr) {
            JsonObject jo = (JsonObject) je;
            logger.debug("[********] 14 getRemoveDupTargetMetasArray resultArr.toString() = " + jo.toString());

            resultArrList.add(jo);
        }

//        Collections.reverse(resultArrList);
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(resultArrList).getAsJsonArray();

        return myCustomArray;
    }


    public JsonArray getTargetMetasArrayOnlyString(String type, JsonArray origArray, JsonArray changeArray) {
        try {
            for (JsonElement je : changeArray) {
                JsonObject jo = (JsonObject) je;

                System.out.println("#origArry:"+origArray.toString()+" /#changeArray:"+changeArray.toString());
                String toAction = jo.get("action").getAsString();
                origArray = changeTargetMetasArrayOnlyString(toAction, jo, origArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return origArray;
    }

    public JsonArray getRemoveDupTargetMetasArrayOnlyString(JsonArray origArray) {
        logger.debug("[********] 14 getRemoveDupTargetMetasArrayOnlyString origArray.toString() = " + origArray.toString());
        JsonArray resultArr = null;
        Map<String, Object> wordSet = new HashMap();

        List<String> origStrArr = null;

        if (origArray != null) {
            try {
                //origStrArr = JsonUtil.convertJsonArrayToListByLabel(origArray, "target_meta");	//2019.11.14 - 넘어오는게 텍스트만 있어서 에러남    2019.11.22 target_meta 말고 word가 있음
                if(origArray.toString().indexOf("\"target_meta\"")<0) {		//target_meta가 없음
                    //origStrArr = JsonUtil.convertJsonArrayToList(origArray);
                    if(origArray.toString().indexOf("\"word\"")<0) {		//word도 없음
                        origStrArr = JsonUtil.convertJsonArrayToList(origArray);
                    }else {		//word는 있음
                        origStrArr = JsonUtil.convertJsonArrayToListByLabel(origArray, "word");
                    }
                }else {	//target_meta 있음
                    origStrArr = JsonUtil.convertJsonArrayToListByLabel(origArray, "target_meta");
                }

                logger.debug("[********] 14    0 origStrArr.toString() = " + origStrArr.toString());
                resultArr = new JsonArray();

                for (String word1 : origStrArr) {
                    logger.debug("[********] 14    1 word1 = " + word1);
                    word1 = word1.trim();

                    // Map�쓽 key�뿉 word瑜� �벑濡앺븯�뿬 以묐났 �젣嫄�
                    if (!"".equals(word1) && wordSet.get(word1) == null) {
                        logger.debug("[********] 14    2 word1 = " + word1);
                        resultArr.add(word1);
                        wordSet.put(word1, "exist");
                    } else {
                        logger.debug("[********] 14    3 word1 = " + word1);
                        System.out.println("#ELOG.metaChange skip by Duplicated word:"+word1);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.debug("[********] 14 getRemoveDupTargetMetasArray wordSet.toString() = " + wordSet.toString());
        logger.debug("[********] 14 getRemoveDupTargetMetasArray resultArr.toString() = " + resultArr.toString());
        return resultArr;
    }

    public JsonArray changeTargetMetasArray(String toAction, JsonObject jObj, JsonArray origArray) {
        JsonArray resultArr = null;

        try {
            if (jObj != null && origArray != null) {
                //String asWord = "";
                String toWord = "";

                resultArr = new JsonArray();
                switch (toAction) {
                    case "add" :
                        toWord = jObj.get("target_meta").getAsString().trim();


                        JsonObject newobj = new JsonObject();
                        newobj.addProperty("word", toWord);
                        newobj.addProperty("type", "new");
                        newobj.addProperty("ratio", 0.0);
                        resultArr.add(newobj);
                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            newobj = new JsonObject();
                            newobj.addProperty("word", jo.get("word").getAsString());
                            newobj.addProperty("type", (jo.get("type") != null) ? jo.get("type").getAsString() : "");
                            newobj.addProperty("ratio", jo.get("ratio").getAsDouble());
                            resultArr.add(newobj);
                        }
                        break;

                    case "mod":
                        String fromWord = jObj.get("meta").getAsString().trim();
                        toWord = jObj.get("target_meta").getAsString().trim();

                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            if(jo.get("word").getAsString().trim().equals(fromWord)) {
                                JsonObject newobj2 = new JsonObject();
                                newobj2.addProperty("word", toWord);
                                newobj2.addProperty("type", "chg");
                                newobj2.addProperty("ratio", jo.get("ratio").getAsDouble());
                                resultArr.add(newobj2);
                            } else {
                                JsonObject newobj2 = new JsonObject();
                                newobj2.addProperty("word", jo.get("word").getAsString());
                                newobj2.addProperty("type", (jo.get("type") != null) ? jo.get("type").getAsString() : "");
                                newobj2.addProperty("ratio", jo.get("ratio").getAsDouble());
                                resultArr.add(newobj2);
                            }
                        }
                        break;

                    case "del":
                        fromWord = jObj.get("meta").getAsString().trim();
                        //toWord = jObj.get("target_meta").getAsString().trim();

                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            if(!jo.get("word").getAsString().trim().equals(fromWord)) {
                                JsonObject newobj3 = new JsonObject();
                                newobj3.addProperty("word", jo.get("word").getAsString());
                                newobj3.addProperty("type", (jo.get("type") != null) ? jo.get("type").getAsString() : "");
                                newobj3.addProperty("ratio", jo.get("ratio").getAsDouble());
                                resultArr.add(newobj3);
                            }
                        }
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultArr;
    }

    public JsonArray changeTargetMetasArrayOnlyString(String toAction, JsonObject jObj, JsonArray origArray) {
        JsonArray resultArr = null;

        try {
            if (jObj != null && origArray != null) {
                //String asWord = "";
                String fromWord = jObj.get("meta").getAsString().trim();
                String toWord = jObj.get("target_meta").getAsString().trim();

                resultArr = new JsonArray();
                switch (toAction) {
                    case "add" :
                        boolean isExist = false;
                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            String word = jo.get("word").getAsString();
                            word = word.trim();
                            String toword = jObj.get("target_meta").getAsString();
                            toword = toword.trim();
                            //System.out.println("Action:"+toAction+"/word:"+word);
                            if (word.equals(toword)) {
                                isExist = true;
                            }
                            resultArr.add(jo);
                        }
                        if (!isExist) {
                            JsonObject jo = new JsonObject();
                            jo.addProperty("word", toWord);
                            jo.addProperty("ratio",0.0);
                            resultArr.add(jo);
                        }

                        break;

                    case "mod":
                        System.out.print("Orig:"+origArray.toString());
                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            String word = jo.get("word").getAsString();
                            word = word.trim();

                            //System.out.println("Action:"+toAction+"/word:"+word+"/toword:"+toWord);
                            if (word.equals(fromWord)) {
                                JsonObject jo_new = new JsonObject();
                                jo_new.addProperty("word", toWord);
                                jo_new.addProperty("ratio", 0.0);

                                resultArr.add(jo_new);
                            } else {
                                resultArr.add(jo);
                            }
                        }

                        break;

                    case "del":
                        System.out.println("Orig:"+origArray.toString());
                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            String word = jo.get("word").getAsString();
                            word = word.trim();
                            String toword = jObj.get("target_meta").getAsString();
                            toword = toword.trim();
                            //System.out.println("Action:"+toAction+"/word:"+word);
                            if (!word.equals(toword)) {
                                resultArr.add(jo);
                            }
                        }

                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultArr;
    }

    @Override
    public List<ItemsTags> getYjTagsMetasByItemidx(ItemsTags req) {
        return itemsTagsMapper.getYjTagsMetasByItemidx(req);
    }

    @Override
    public ItemsTags getLastTagCntInfo(Integer itemid) {
        return itemsTagsMapper.getLastTagCntInfo(itemid);
    }

    @Override
    public ItemsTags getLastTagSuccessInfo(Integer itemid) {
        return itemsTagsMapper.getLastTagSuccessInfo(itemid);
    }

    @Override
    public List<ItemsTags> getTagCntInfo(Integer itemid) {
        return itemsTagsMapper.getTagCntInfo(itemid);
    }

    @Override
    public List<ItemsTags> getSuccessTagidxListDesc(Integer itemid) {
        return itemsTagsMapper.getSuccessTagList(itemid);
    }

    @Override
    @Async
    @Transactional
    public void processManualTagsMetasChange(String target_mtype, String from_keyword, String to_keyword, String action) {
        int rt = 0;
        //target_mtype = apiService.getChangedMtypes(target_mtype);

        System.out.println("#ELOG.start processManualTagsMetasChange :flag:"+jobRuuningStat);
        if (!"".equals(target_mtype) && !"".equals(from_keyword) && !"".equals(to_keyword) && !"".equals(action)) {
            if(jobRuuningStat == false) {
                jobRuuningStat = true;
                try {
                    // running job 以묐났諛⑹�瑜� �쐞�빐 �씠�젰 ���옣
                    ManualChange reqm = new ManualChange();
                    reqm.setTarget_mtype(target_mtype);
                    reqm.setFrom_keyword(from_keyword);
                    reqm.setTo_keyword(to_keyword);
                    reqm.setAction(action);
                    int rtma = this.insManualJobHist(reqm);

                    ItemsTags req = new ItemsTags();
                    req.setMtype(target_mtype);

                    switch (action) {
                        case "add":
                            // 議고쉶�뒗 �쟾泥� �깭洹몃찓��
                            // �씫�뼱�삩 硫뷀� 援ъ“泥댁뿉 �떊洹� �궎�썙�뱶 異붽�
                            // from_keyword 媛� �엳�쓣 寃쎌슦 �빐�떦 �궎�썙�뱶媛� �엳�뒗 row留� ���긽�쑝濡� �꽑�젙
                            if (!from_keyword.equals(to_keyword)) {
                                req.setKeyword(from_keyword);
                            }
                            break;
                        case "mod":
                            // 議고쉶�뒗 mtype, from_keyword瑜� 湲곗��쑝濡�
                            req.setKeyword(from_keyword);
                            // �씫�뼱�삩 硫뷀� 援ъ“泥댁뿉�꽌 湲곗〈 �궎�썙�뱶瑜� to_keyword濡� 蹂�寃�
                            break;
                        case "del":
                            // 議고쉶�뒗 mtype, from_keyword瑜� 湲곗��쑝濡�
                            req.setKeyword(from_keyword);
                            // �씫�뼱�삩 硫뷀� 援ъ“泥댁뿉�꽌 湲곗〈 �궎�썙�뱶瑜� 李얠븘 �젣嫄�
                            break;
                    }

                    // 蹂�寃� ���긽 議고쉶
                    int cnt_all = itemsTagsMapper.cntSearchTagsMetasByMtypeAndKeyword(req);
                    System.out.println("#ELOG.searchTagsCount:" + cnt_all);

                    int pageSize = 50;
                    req.setPageSize(pageSize);
                    if (cnt_all > 0) {
                        int maxPage = cnt_all / pageSize + 1;
                        System.out.println("#ELOG.maxPage:" + maxPage);

                        JsonArray changeMetaArr = new JsonArray();
                        JsonObject newChangeObj = new JsonObject();
                        newChangeObj.addProperty("type", target_mtype);
                        newChangeObj.addProperty("meta", from_keyword);
                        newChangeObj.addProperty("target_meta", to_keyword);
                        newChangeObj.addProperty("action", action);
                        changeMetaArr.add(newChangeObj);

                        for (int pageno = 1; pageno <= maxPage; pageno++) {
                            req.setPageNo(pageno);
                            List<ItemsTags> resCur = itemsTagsMapper.getSearchTagsMetasByMtypeAndKeywordPaging(req);
                            //System.out.println("#ELOG.searchedItemsTags by mtype:"+target_mtype+"/pageno:"+pageno+"/datas::"+resCur.toString());
                            for (ItemsTags itag : resCur) {
                                // ���옣 ���긽 �깮�꽦
                                String origMeta = itag.getMeta();
                                if (origMeta != null && !"".equals(origMeta) && !"[]".equals(origMeta)) {
                                    JsonParser jsonParser = new JsonParser();
                                    JsonArray origMetaArr = (JsonArray) jsonParser.parse(origMeta);
                                    System.out.println("#ELOG.searchedItemsTags by mtype:" + target_mtype + "/pageno:" + pageno + "/origMetaArr::" + origMetaArr.toString());

                                    JsonArray destArr = null;
                                    JsonArray destArr2 = null;

                                    if (target_mtype.contains("METAS") || target_mtype.equals("LIST_NOT_MAPPED")) {
                                        destArr = this.getTargetMetasArray(target_mtype, origMetaArr, changeMetaArr);
                                        destArr2 = this.getRemoveDupTargetMetasArray(destArr);
                                        System.out.println("#ELOG.destArr(JsonObject): datas::" + destArr2.toString());

                                    } else {
                                        destArr = this.getTargetMetasArrayOnlyString(target_mtype, origMetaArr, changeMetaArr);
                                        destArr2 = this.getRemoveDupTargetMetasArrayOnlyString(destArr);
                                        System.out.println("#ELOG.destArr(String): datas::" + destArr2.toString());
                                    }

                                    if (destArr2 != null) {
                                        // idx, tagidx, mtype �뿉 留욎텛�뼱 meta �뾽�뜲�씠�듃
                                        itag.setMeta(destArr2.toString());
                                        rt = itemsTagsMapper.uptItemsTagsByManual(itag);
                                    }
                                }
                            }

                        }
                    }

                    // �궗�쟾�뿉�룄 諛섏쁺 �옉�뾽
                    if (rt > 0) {
                        int rtd = this.changeDicKeywordsForManual(target_mtype, from_keyword, to_keyword, action);
                    }

                    Thread.sleep(3000);

                    // running job 以묐났諛⑹�瑜� �쐞�빐 �씠�젰 ���옣 stat = S
                    ManualChange histOneOld = this.getManualJobHistLastOne();
                    ManualChange histOne = this.getManualJobHistLastOne();
                    histOne.setHidx(histOneOld.getHidx());
                    histOne.setStat("S");
                    histOne.setCnt(cnt_all);

                    rtma = this.uptManualJobHist(histOne);

                    jobRuuningStat = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ManualChange histOne = this.getManualJobHistLastOne();
                logger.info("#CLOG:jobRunning! skip manualChange:type:"+target_mtype+"/from:"+from_keyword+"/to:"+to_keyword+"/action:"+action);
                // + "     ||    "
                // + "runningJob::type:"+histOne.getTo_keyword()+"/from:"+histOne.getFrom_keyword()+"/to:"+histOne.getTo_keyword()+"/action:"+histOne.getAction());
            }
        }
        //return rt;
    }

    private int changeDicKeywordsForManual(String target_mtype, String from_keyword, String to_keyword, String action) throws Exception {
        int rt = 0;

        // �궗�쟾�뿉�룄 諛섏쁺 �옉�뾽
        if (!"".equals(target_mtype) && !"".equals(to_keyword) && !"".equals(action)) {
            DicKeywords newKey = new DicKeywords();

            switch (action) {
                case "add":
                    newKey.setType(target_mtype.replace("METAS", ""));
                    newKey.setKeyword(to_keyword);
                    newKey.setRatio(0.0);
                    rt = dicService.insDicKeywords(newKey);
                    break;
                case "mod":
                    newKey.setType(target_mtype.replace("METAS", ""));
                    newKey.setKeyword(from_keyword);
                    newKey.setOldword(from_keyword);
                    newKey.setToword(to_keyword);
                    newKey.setRatio(0.0);
                    rt = dicService.insDicKeywords(newKey);
                    break;
                case "del":
                    newKey.setType(target_mtype.replace("METAS", ""));
//                    newKey.setKeyword(to_keyword);
                    newKey.setOldword(to_keyword);
                    newKey.setRatio(0.0);
                    rt = dicService.delDicKeywords(newKey);
                    break;
            }
        }

        return rt;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public ManualChange getManualJobHistLastOne() {
        return manualJobHistMapper.getManualJobHistLastOne();
    }
    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int insManualJobHist(ManualChange req) {
        return manualJobHistMapper.insManualJobHist(req);
    }
    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public int uptManualJobHist(ManualChange req) {

        return manualJobHistMapper.uptManualJobHist(req);
    }

    @Override
    public int delItemsMetasAward(int itemIdx) {
        ItemsMetas req = new ItemsMetas();
        req.setIdx(itemIdx);
        req.setMtype("award");
        int rt = itemsMetasMapper.delItemsMetas(req);
        return rt;
    }

    private String getMetasStringFromJsonObject(JsonObject resultObj, List<String> origTypes) {
        String result = "";

        List<String> resultArr = new ArrayList();
        JsonArray metaArr = null;
        if (resultObj != null && origTypes != null) {
            for(String type : origTypes) {
                String typeStr = type.replace("METAS","");
                if (resultObj.get(type) != null) {
                    metaArr = (JsonArray) resultObj.get(type);
                    //System.out.println("#metaArr:"+metaArr.toString());
                    JsonObject jo = null;
                    if(metaArr != null && metaArr.size() > 0) {
                        for(JsonElement je : metaArr) {
                            jo = (JsonObject) je;
                            String keyOne = (jo.get("word") != null) ? jo.get("word").toString() : "";
                            if((!"".equals(keyOne.trim()))) {
                                keyOne = keyOne.replace("\"","");
                                keyOne = keyOne.replace("\'","");
                                keyOne = typeStr +"___"+ keyOne.trim();
                                //System.out.println("#word:"+keyOne);
                                resultArr.add(keyOne);
                            }
                        }
                    }
                }
            }
        }

        if(resultArr != null && resultArr.size() > 0) {
            result = resultArr.toString();
            result = result.replace(",","");
            result = CommonUtil.removeBrackets(result);
        }
        return result;
    }


    private String getMetasStringFromJsonArray(JsonArray reqJsonArr, List<String> origTypes) {
        String result = "";

        Set<String> resultSet = null;
        if (reqJsonArr != null && origTypes != null) {
            resultSet = new HashSet<String>();

            for (JsonElement je : reqJsonArr) {
                JsonObject jo = (JsonObject) je;
                String type = "";
                String meta = "";
                String toMeta = "";
                if (jo != null) {
                    type = (jo.get("type") != null ? jo.get("type").toString() : "");
                    type = type.toUpperCase();
                    type = type.replace("\"","");
                    meta = (jo.get("meta") != null ? jo.get("meta").toString() : "");
                    meta = meta.replace("\"","");
                }
                if (!"".equals(type) && !"".equals(meta)) {
                    toMeta = type + "___" + meta;
                    resultSet.add(toMeta);
                }
            }

        }
        if (resultSet != null) {
            result = resultSet.toString();
            result = result.replace(", ", " ");
            result = result.replace(",", " ");
            result = CommonUtil.removeBrackets(result);
        }

        return result;
    }

    private static EsConfig esConfig = null;
    private static RestClient restClient = null;

    private JsonObject getSearchedEsData(String idxName, String fieldName, String reqStr) throws Exception {
        //String result = "";
        JsonObject result = new JsonObject();

        try {
            if (restClient == null) {
                esConfig = new EsConfig();
                System.out.println("##REST::ElasticSearch server:" + EsConfig.INSTANCE.getEs_host() + ":" + EsConfig.INSTANCE.getEs_port() + "//:request_param:" + reqStr);
                restClient = RestClient.builder(
                        new HttpHost(EsConfig.INSTANCE.getEs_host(), EsConfig.INSTANCE.getEs_port(), "http")).build();
            }
            //HttpEntity entity = new NStringEntity(reqStr, ContentType.APPLICATION_JSON);

            /*
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("keywords", reqStr);
            paramMap.put("pretty", "true");
            */
            HttpEntity entity = new NStringEntity(
                    "{\n" +
                            "    \"query\" : {\n" +
                            "    \"match\": { \""+fieldName+"\":\""+reqStr+"\"} \n" +
                            "} \n"+
                            "}",
                    ContentType.APPLICATION_JSON
            );
            Response response = restClient.performRequest(
                    "GET",
                    "/"+idxName+"/_search",
                    Collections.singletonMap("pretty", "true"),
                    entity
            );


            /*
            Response response = restClient.performRequest(
                    "GET",
                    "/"+idxName+"/_search",
                    paramMap
            );
            */

            //System.out.println(EntityUtils.toString(response.getEntity()));
            //result = response.getEntity().toString();
            String resultStr = EntityUtils.toString(response.getEntity());
            result = JsonUtil.getJsonObject(resultStr);

            //System.out.println("#REST::ElasticSearch Result:"+result.toString());

        } catch (Exception e) { e.printStackTrace(); }


        return result;
    }

    private JsonObject getEsTopWords(JsonObject reqObj) {
        JsonObject result = null;
        JsonArray words = null;
        if(reqObj != null) {
            result = new JsonObject();
            words = new JsonArray();

            JsonObject hitsObj = null;
            if(reqObj.get("hits") != null) hitsObj = (JsonObject) reqObj.get("hits");
            //System.out.println("#hits:"+hitsObj.toString());
            JsonArray hitsArr = null;
            if(hitsObj != null && hitsObj.get("hits") !=null) hitsArr = hitsObj.get("hits").getAsJsonArray();
            //System.out.println("#hitsArr:"+hitsArr.toString());
            int cnt = 0;
            for(JsonElement je : hitsArr) {
                if (cnt < 10) {
                    JsonObject jo = (JsonObject) je;
                    JsonObject jobj = null;
                    String wordOne = "";

                    double score = 0.0;
                    if (jo != null && jo.get("_score") != null) score = jo.get("_score").getAsDouble();
                    if (jo != null && jo.get("_source") != null) jobj = jo.get("_source").getAsJsonObject();
                    if (jobj != null && jobj.get("topic") != null) wordOne = jobj.get("topic").getAsString();
                    System.out.println("# score:"+score+"  /  word:"+wordOne);
                    JsonObject word1 = new JsonObject();
                    word1.addProperty("score", String.valueOf(score));
                    word1.addProperty("word", wordOne);
                    words.add(word1);
                } else {
                    break;
                }
                cnt++;
            }
            result.add("words", words);
        }

        return result;
    }


    private JsonObject getEsTopWordsWithPointCut(JsonObject reqObj, Double limitPoint) {
        JsonObject result = null;
        JsonArray words = null;
        if(reqObj != null) {
            result = new JsonObject();
            words = new JsonArray();

            JsonObject hitsObj = null;
            if(reqObj.get("hits") != null) hitsObj = (JsonObject) reqObj.get("hits");
            //System.out.println("#hits:"+hitsObj.toString());
            JsonArray hitsArr = null;
            if(hitsObj != null && hitsObj.get("hits") !=null) hitsArr = hitsObj.get("hits").getAsJsonArray();
            //System.out.println("#hitsArr:"+hitsArr.toString());
            int cnt = 0;
            for(JsonElement je : hitsArr) {
                if (cnt < 2) {
                    JsonObject jo = (JsonObject) je;
                    JsonObject jobj = null;
                    String wordOne = "";

                    double score = 0.0;
                    if (jo != null && jo.get("_score") != null) score = jo.get("_score").getAsDouble();
                    if (jo != null && jo.get("_source") != null) jobj = jo.get("_source").getAsJsonObject();
                    if (jobj != null && jobj.get("topic") != null) wordOne = jobj.get("topic").getAsString();
                    System.out.println("# score:"+score+"  /  word:"+wordOne);
                    if (score > limitPoint) {
                        JsonObject word1 = new JsonObject();
                        word1.addProperty("score", String.valueOf(score));
                        word1.addProperty("word", wordOne);
                        words.add(word1);
                    }
                } else {
                    break;
                }
                cnt++;
            }
            result.add("words", words);
        }

        return result;
    }

    private Set<String> getCombindEsAndGenre(Set<String> resultSet, String esReturnWord, String itemGenres) {
        if (resultSet == null) resultSet = new HashSet();

        if (!"".equals(esReturnWord) && !"".equals(itemGenres)) {

            if (itemGenres.trim().contains(" ")) {
                String genreR[] = itemGenres.trim().split(" ");
                for (String genre : genreR) {
                    resultSet.add(esReturnWord + "___" + genre);
                }
            } else {
                resultSet.add(esReturnWord + "___" + itemGenres);
            }
        }
        return resultSet;
    }

    @Override
    public JsonArray getMetaSubgenre(Integer itemid, String reqJsonObjStr) throws Exception {
        List<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");

        // request param 蹂�議� metawho -> who
        reqJsonObjStr = StringUtil.removeMetaTag(reqJsonObjStr);

        //long itemIdx0 = (long) 0;
        //int itemIdx = 0;
        JsonArray resultArr = this.getMixedSubgenre2(itemid);
        if (resultArr == null) resultArr = new JsonArray();

        int cnt = 0;
        JsonObject jo = null;
        String word = "";
        double score = 0.0;

        ItemsMetas newMeta = null;
        int intIdx = 0;
        int rtItm1 = -1;
        long longIdx = 0;
        if (itemid > 0 && !"".equals(reqJsonObjStr)) {

            try {

                // �엯�젰 JsonArray瑜� META_WORD �삎�깭�쓽 怨듬갚�쑝濡� 援щ텇�맂 1媛쒖쓽 臾몄옄�뿴濡� 移섑솚
                JsonArray reqArr = JsonUtil.getJsonArray(reqJsonObjStr);
                String reqStr = this.getMetasStringFromJsonArray(reqArr, origTypes);

                // �엯�젰 reqStr�쓣 �뿕�씪�뒪�떛 �꽌移섎�� �넻�빐 �쑀�궗�룄 �룊媛�
                JsonObject resultEs = null;
                //Set<String> esReturnArr = new HashSet<String>();
                List<String> esReturnArr = new ArrayList<>();

                String esReturnWord = "";
                String meta_single = "";
                String meta_genre = "";
                String esWords = "";
                JsonArray words = null;
                JsonObject hits = null;

                Set<String> metaSingleArr = new HashSet();
                Set<String> metaGenreArr = new HashSet();

                // ���옣�맂 �옣瑜대�� 媛��졇�삩�떎.
                ItemsMetas reqIm = new ItemsMetas();
                reqIm.setIdx(itemid);
                reqIm.setMtype("genre");
                ItemsMetas genreMetas = itemsMetasMapper.getItemsMetas(reqIm);
                String itemGenre = (genreMetas != null && genreMetas.getMeta() != null) ? genreMetas.getMeta() : "";
                System.out.println("#ELOG ORIG item's GENRE:"+itemGenre);

                // �떎瑜� 寃껉낵 �븘臾� �긽愿��뾾�씠 �듅�젙 �궎�썙�뱶媛� �엳�쑝硫� 寃곌낵�뿉 洹� �옣瑜대챸�쓣 異붽��븳�떎. dic_subgenre_genres�쓽 mtype=genre_word
                System.out.println("#ELOG genre_add_arr from reqStr:"+reqStr.toString());
                Set<String> genre_add_arr = dicService.getGenreAddByReqKeywords(reqStr, "genre_add");
                System.out.println("#ELOG GENRE_ADD by genre_add:"+genre_add_arr.toString());

                Set<String> combinedWordAndGenres = new HashSet();
                if (genre_add_arr != null) {
                    // meta_single怨� ��議고븯�뿬 寃곌낵�뿉 ���옣
                    for(String gs : genre_add_arr) {
                        metaSingleArr = dicService.getMetaSingleFromGenre(metaSingleArr, gs, "meta_single");
                    }
                    System.out.println("#ELOG metaSingleArr after genre_add:"+metaSingleArr.toString());

                    // meta_genre �� ��議고븯�뿬 寃곌낵�뿉 ���옣
                    for(String ga : genre_add_arr) {
                        combinedWordAndGenres = this.getCombindEsAndGenre(combinedWordAndGenres, ga, itemGenre);
                    }

                    //System.out.println("#ELOG combinedWordAndGenres by genre_add:"+combinedWordAndGenres.toString());
                    if (combinedWordAndGenres != null && combinedWordAndGenres.size() > 0) {
                        metaGenreArr = dicService.getMetaGenreFromGenre(null, combinedWordAndGenres, "meta_genre");
                        System.out.println("#ELOG metaGenreArr after genre_add:"+metaGenreArr.toString());
                    }
                }

                System.out.println("#requestEs for reqStr:" + reqStr);
                resultEs = getSearchedEsData("idx_subgenre", "keywords"
                        , reqStr);

                System.out.println("#resultEs:" + resultEs.toString());
                // 1李� ES response�뿉�꽌 10媛쒕�� �룷�씤�듃 �긽愿��뾾�씠 痍⑤뱷
                hits = this.getEsTopWords(resultEs);
                System.out.println("#resultEs.words top1::" + hits.toString());

                words = null;
                if (hits != null && hits.get("words") != null) {
                    words = hits.get("words").getAsJsonArray();
                    cnt = 0;
                    jo = null;
                    word = "";
                    score = 0.0;
                    for (JsonElement je : words) {
                        jo = (JsonObject) je;
                        word = "";
                        word = (jo.get("word") != null) ? jo.get("word").getAsString() : "";

                        //if (cnt == 0) {
                        score = (jo.get("score") != null) ? jo.get("score").getAsDouble() : 0.0;
                        if (score > es_cut_point) {
                            esReturnArr.add(word);
                        }
                        //}
                        cnt++;
                    }
                    esWords = hits.get("words").toString();
                }

                // ES 寃��깋 �썑 而룻룷�씤�듃 �넻怨� �븳 寃곌낵�뒗 1媛쒕쭔 痍⑤뱷
                if (esReturnArr != null && esReturnArr.size() > 0) {
                    esReturnWord = esReturnArr.get(0);
                }

                System.out.println("#esReturnWord:(cut " + es_cut_point + ")::" + esReturnWord + " / esWords:" + esWords);

                // 而룻룷�씤�듃 �넻怨쇳븳 ES 寃��깋 �넗�뵿 TOP 1�쓣 resultArr�뿉 ���옣
                if (!"".equals(esReturnWord)) {
                    if (resultArr == null) resultArr = new JsonArray();
                    /*
                    JsonObject newWord = new JsonObject();
                    newWord.addProperty("type", "");
                    newWord.addProperty("ratio", 0.0);
                    newWord.addProperty("word", esReturnWord);
                    resultArr.add(newWord);
                    */

                    // �넗�뵿 TOP 1 �쓣 dic_subgenre_genres �쓽 mtype=meta_single怨� ��議고븯�뿬 異붽��뼱 痍⑤뱷 �썑 resultArr�뿉 ���옣
                    metaSingleArr = dicService.getMetaSingleFromGenre(metaSingleArr, esReturnWord, "meta_single");
                    System.out.println("#ELOG metaSingleArr final ::"+metaSingleArr.toString());

                    if (metaSingleArr != null && metaSingleArr.size() > 0) {
                        for (String ms : metaSingleArr) {
                            meta_single = ms;
                            meta_single = StringUtil.removeBracket(meta_single);
                            System.out.println("#meta_single:" + meta_single);
                            if (!"".equals(meta_single)) {
                                JsonObject newWord2 = new JsonObject();
                                newWord2.addProperty("type", "");
                                newWord2.addProperty("ratio", 0.0);
                                newWord2.addProperty("word", meta_single);
                                resultArr.add(newWord2);
                            }
                        }
                    }

                    // �넗�뵿 TOP 1怨� genre瑜� dic_subgenre_genres �쓽 mtype=meta_genre �� ��議고븯�뿬 異붽��뼱 痍⑤뱷 �썑 resultArr�뿉 ���옣

                    // �옣瑜닿� �엳�쑝硫� ES�넗�뵿___�옣瑜�  議고빀�쑝濡� �궗�쟾怨� ��議고븯�뿬 由ъ뒪�듃 異붿텧
                    Set<String> combinedEsWordAndGenres = new HashSet();
                    if (!"".equals(itemGenre)) {
                        combinedEsWordAndGenres = this.getCombindEsAndGenre(combinedEsWordAndGenres, esReturnWord, itemGenre);
                        System.out.println("#combinedEsWordAndGenres:"+combinedEsWordAndGenres.toString());

                        metaGenreArr = dicService.getMetaGenreFromGenre(metaGenreArr, combinedEsWordAndGenres, "meta_genre");
                        System.out.println("#ELOG metaGenreArr final ::"+metaGenreArr.toString());

                        if (metaGenreArr != null && metaGenreArr.size() > 0) {
                            for (String meta_genre_one : metaGenreArr) {
                                resultArr.add(JsonUtil.getObjFromMatchedGenre(meta_genre_one));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JsonArray resultArr2 = new JsonArray();
        if (resultArr != null && resultArr.size() > 0) {
            // 留덉�留� �젣�쇅 ���긽 �븘�꽣 �쟻�슜
            for (JsonElement je : resultArr) {
                JsonObject jo1 = (JsonObject) je;
                boolean isValid = StringUtil.filterLastTagValid(jo1);

                if(isValid) {
                    System.out.println("#ELOG jo: added:"+jo1.toString());
                    resultArr2.add(jo1);
                }
            }
        }

        return resultArr2;
    }


    @Override
    public JsonArray getMixedSubgenre2(Integer itemid) throws Exception {
        JsonArray resultArr = null;

        if (itemid > 0) {
            resultArr = new JsonArray();
            Items itemInfo = itemsService.getItemsInfoByIdx(itemid);
            String cont_type = itemInfo.getType();

            String reqStr0 = "";
            String reqStr = "";
            if(itemInfo.getGenre() != null) reqStr0 = itemInfo.getGenre();
            if(itemInfo.getKt_rating() != null) reqStr = reqStr0 + " " + itemInfo.getKt_rating();
            System.out.println("#req str: genre/kt_rating::"+reqStr);

            String toMeta = "";
            if(!"".equals(reqStr)) {
                Set result = dicService.getMixedGenreArrayFromGenre(reqStr, "subgenre_filter");
                toMeta = result.toString();
                toMeta = CommonUtil.removeNationStr(toMeta);
                System.out.println("#toMeta:"+toMeta);
                if (cont_type.contains("CcubeSeries")) {
                    toMeta = toMeta.replace("�쁺�솕", "�떆由ъ쫰");
                }
            }

            String origin = "";
            if(itemInfo.getCorigin() != null) {
                origin = itemInfo.getCorigin();
            } else if(itemInfo.getSorigin() != null) {
                origin = itemInfo.getSorigin();
            }

            String toMetaOrigin = "";
            if (!"".equals(origin) && !"".equals(reqStr0)) {
                System.out.println("###REQ_STR2::origin:" + origin);
                Set resultNation = dicService.getMixedNationGenreArrayFromGenre(reqStr0, origin, "origin");
                System.out.println("#RESULT_NATION:" + resultNation.toString());
                toMetaOrigin = resultNation.toString();
                toMetaOrigin = CommonUtil.removeBrackets(toMetaOrigin);
                if (cont_type.contains("CcubeSeries")) {
                    toMetaOrigin = toMetaOrigin.replace("�쁺�솕", "�떆由ъ쫰");
                }
            }

            if(!"".equals(toMeta)) {
                //ItemsMetas newMeta = new ItemsMetas();
                //long longIdx = itemid;
                //int intIdx = (int) longIdx;
                //newMeta.setIdx(itemid);
                //newMeta.setMtype("subgenreMix2");
                //newMeta.setMeta(toMeta);
                //System.out.println("#save itemsMetas:" + newMeta.toString());
                //int rtItm = itemsService.insItemsMetas(newMeta);
                String[] toMetas = toMeta.split(", ");
                for (String toM : toMetas) {
                    resultArr.add(JsonUtil.getObjFromMatchedGenre(toM));
                }
            }

            if(!"".equals(toMetaOrigin)) {
                //ItemsMetas newMeta = new ItemsMetas();
                //long longIdx = (Long) nmap.get("idx");
                //int intIdx = (int) longIdx;
                //newMeta.setIdx(itemid);
                //newMeta.setMtype("subgenreOrgin2");
                //newMeta.setMeta(toMetaOrigin);
                //System.out.println("#save itemsMetas2:" + newMeta.toString());
                //int rtItm = itemsService.insItemsMetas(newMeta);
                String[] toMetaOs = toMetaOrigin.split(", ");
                for (String toM : toMetaOs) {
                    resultArr.add(JsonUtil.getObjFromMatchedGenre(toM));
                }
            }
        }

        return resultArr;
    }


    @Override
    public int insTmpCountKeyword(ItemsTags req) {
        return itemsTagsMapper.insTmpCountKeyword(req);
    }
}
