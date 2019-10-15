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
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");

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

        //System.out.println("#ELOG resultArr:"+resultArr.toString());
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
<<<<<<< HEAD
        1. ï§£ì„ì“¬ ï¿½ë²‘æ¿¡ì•ºë¸· å¯ƒìŽŒìŠ¦ = 0
        2. ï¿½ë“…ï¿½ì”¤ï¿½ë§‚ å«„ëŒì”  ï¿½ì—³æ€¨ï¿½ ï¿½ë‹”ï¿½ì ™ï¿½ë¸· å¯ƒìŽŒìŠ¦ = last tagIdx
        3. ï¿½ë“…ï¿½ì”¤ï¿½ë§‚ å«„ëŒì”  ï¿½ë¾¾æ€¨ï¿½ ï¿½ë‹”ï¿½ì ™ï¿½ë¸· å¯ƒìŽŒìŠ¦ = last tagIdx
=======
        1. Ã³À½ µî·ÏÇÒ °æ¿ì = 0
        2. ½ÂÀÎµÈ °ÇÀÌ ÀÖ°í ¼öÁ¤ÇÒ °æ¿ì = last tagIdx
        3. ½ÂÀÎµÈ °ÇÀÌ ¾ø°í ¼öÁ¤ÇÒ °æ¿ì = last tagIdx
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
         */
        ItemsTags req = new ItemsTags();
        req.setIdx(itemIdx);
        req.setStat("Y");
        int tagidx = this.getMaxTagsIdxByItemIdx(req);

        if (tagidx == 0) {
<<<<<<< HEAD
            // ï¿½ë“…ï¿½ì”¤ï¿½ì…¿çŒ·ëš®ë§‚ tagidxåª›ï¿½ ï¿½ì—³ï¿½ë’—ï§žï¿½ ï¿½ì†—ï¿½ì”¤
=======
            // ½ÂÀÎ¿Ï·áµÈ tagidx°¡ ÀÖ´ÂÁö È®ÀÎ
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
            ItemsTags req2 = new ItemsTags();
            req2.setIdx(itemIdx);
            req2.setStat("S");
            int tagidx2 = this.getMaxTagsIdxByItemIdx(req2);
            //System.out.println("#confirmed tagidx2:"+tagidx2);

<<<<<<< HEAD
            // ï¿½ë“…ï¿½ì”¤ï¿½ì…¿çŒ·ëš®ë§‚ tagidx2åª›ï¿½ ï¿½ë¾¾ï¿½ì‘ï§Žï¿½ ï¿½ë“…ï¿½ì”¤èª˜ëª„ì…¿çŒ·ëš¯ì”¤ tagidxç‘œï¿½ ç”±Ñ‹ê½©
=======
            // ½ÂÀÎ¿Ï·áµÈ tagidx2°¡ ¾øÀ¸¸é ½ÂÀÎ¹Ì¿Ï·áÀÎ tagidx¸¦ ¸®ÅÏ
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
            if (tagidx2 == 0) {
<<<<<<< HEAD
                // ï¿½ë“…ï¿½ì”¤ï¿½ì…¿çŒ·ëš®ë§‚ tagidx åª›ì’–ë‹”åª›ï¿½ > 0 ï¿½ì” ï§Žï¿½ 1 ï§ì•·ï¿½
=======
                // ½ÂÀÎ¿Ï·áµÈ tagidx °³¼ö°¡ > 0 ÀÌ¸é 1 Áõ°¡
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                int confirmedTagCnt = this.cntConfirmedTags(req2);
                System.out.println("#confirmedTagCnt :"+confirmedTagCnt);
                if (confirmedTagCnt > 0) {
                    tagidx = 1;
                }
<<<<<<< HEAD
            // ï¿½ë“…ï¿½ì”¤ï¿½ì…¿çŒ·ëš®ë§‚ tagidxåª›ï¿½ ï¿½ì—³ï¿½ì‘ï§Žï¿½ 1 ï§ì•·ï¿½ï¿½ë¸¯ï¿½ë¿¬ ï¿½ë–Šæ´¹ì’“êµ”ï¿½ì‘æ¿¡ï¿½ ï¿½ï¿½ï¿½ì˜£
=======
            // ½ÂÀÎ¿Ï·áµÈ tagidx°¡ ÀÖÀ¸¸é 1 Áõ°¡ÇÏ¿© ½Å±Ô°ÇÀ¸·Î ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                                    // ï§ë‰ï¿½ï§ï¿½ ï¿½ì £ï¿½ì‡… ï¿½ï¿½ï¿½ê¸½ ï¿½ë¸˜ï¿½ê½£ ï¿½ìŸ»ï¿½ìŠœ
=======
                                    // ¸¶Áö¸· Á¦¿Ü ´ë»ó ÇÊÅÍ Àû¿ë
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
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
        System.out.println("#ELOG.getItemsMetasByItemIdx:: old.datas::"+resultObj.toString());

        JsonObject resultObj2 = getItemsMetasDupByItemIdx(resultObj, itemIdx, isColorCode);
        System.out.println("#ELOG.getItemsMetasByItemIdx:: dupCheck.datas::"+resultObj2.toString());

<<<<<<< HEAD
        /* WORDS_ASSOC åª›ë¨¯ê½¦ï¿½ì‘€ï¿½ì“½ï¿½ë¼± - ï¿½ê½•ï¿½ì” è¸°ê¾©ê¶—ï¿½ìŸ¾ */
=======
        /* WORDS_ASSOC °¨¼ºÀ¯ÀÇ¾î - ³×ÀÌ¹ö»çÀü */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
        resultObj2 = getWordsAssoc(itemIdx, resultObj2);

        System.out.println("#ELOG.getItemsMetasByItemIdx:: after.wordsAssoc.datas::"+resultObj2.toString());

        /* WORDS_GENRE */
        resultObj2 = getWordsGenre(itemIdx, resultObj2);
        System.out.println("#ELOG.getItemsMetasByItemIdx:: after.wordsGenre.datas::"+resultObj2.toString());


        /* WORDS_SNS */
        // resultObj2 = getWordsSns(itemIdx, resultObj2);

        /* LIST_SUBGENRE */
        if (resultObj2 != null && resultObj2.get("LIST_SUBGENRE") == null) {
            resultObj2 = getSubgenres(itemIdx, resultObj2);
        }

        /* LIST_AWARD */
        resultObj2 = getAwardObject(itemIdx, resultObj2);

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
<<<<<<< HEAD
        // é®ì¢Žì­Š typeï¿½ï¿½ æ€¨ë“¬ê°šï¿½ì” ï¿½ì”ªï¿½ë£„ ï§¢ê¾©ì™ä»¥ï¿½ï¿½ë–Ž
=======
        // ºüÁø typeÀº °ø¹éÀÌ¶óµµ Ã¤¿öÁØ´Ù
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
        origTypes.add("METASCHARACTER");
        origTypes.add("LIST_NOT_MAPPED");
        origTypes.add("WORDS_GENRE");
        origTypes.add("WORDS_SNS");
        origTypes.add("WORDS_ASSOC");
        origTypes.add("LIST_SUBGENRE");
        origTypes.add("LIST_SEARCHKEYWORDS");
        origTypes.add("LIST_RECO_TARGET");
        origTypes.add("LIST_RECO_SITUATION");

        JsonObject oldResultObj = getOldItemsMetasByIdx(itemIdx, origTypes);
        //System.out.println("#oldItemsMetas:" + oldResultObj.toString());

        for (String type : origTypes) {
            JsonArray oldTypesArr = null;
            if (oldResultObj != null && oldResultObj.get(type) != null) oldTypesArr = (JsonArray) oldResultObj.get(type);
            JsonArray newTypesArr = null;
            if (resultObj != null && resultObj.get(type) != null) newTypesArr = (JsonArray) resultObj.get(type);

            JsonArray typeResultArr1 = getCombinedJsonArray(type, oldTypesArr, newTypesArr, isColorCode);
            System.out.println("#TLOG:resultMetasCheckDup for type("+type+") data:"+typeResultArr1.toString());

            JsonArray typeResultArr = null;
            if(!"LIST_SEARCHKEYWORDS".equals(type) && !"WORDS_SNS".equals(type)) {
                //&& !"LIST_SUBGENRE".equals(atype)) {
                typeResultArr = this.getRemoveDupTargetMetasArray(typeResultArr1);
                //System.out.println("#ELOG.destArr(JsonObject): datas::"+destArr2.toString());
            } else {
                typeResultArr = this.getRemoveDupTargetMetasArrayOnlyString(typeResultArr1);
                //System.out.println("#ELOG.destArr(String): datas::"+destArr2.toString());
            }

            if ("LIST_NOT_MAPPED".equals(type)) {
                JsonArray sortedJsonArray = JsonUtil.getSortedJsonArray(typeResultArr, "word", "ratio", "type", 100);
                if (sortedJsonArray != null && sortedJsonArray.size() > 0) typeResultArr = sortedJsonArray;
            }

            if("LIST_SUBGENRE".equals(type)) {
                JsonArray listSubgenre2 = new JsonArray();
                if (typeResultArr != null && typeResultArr.size() > 0) {
<<<<<<< HEAD
                    // ï§ë‰ï¿½ï§ï¿½ ï¿½ì £ï¿½ì‡… ï¿½ï¿½ï¿½ê¸½ ï¿½ë¸˜ï¿½ê½£ ï¿½ìŸ»ï¿½ìŠœ
=======
                    // ¸¶Áö¸· Á¦¿Ü ´ë»ó ÇÊÅÍ Àû¿ë
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

            if (resultObj.get(type) != null) resultObj.remove(type);
            resultObj.add(type, typeResultArr);
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
<<<<<<< HEAD
                            // OLD:NEW åª›ìˆˆï¿½ å¯ƒê»‹ì”  ï¿½ì—³ï¿½ì‘ï§Žï¿½ type=dup æ¿¡ï¿½ ï¿½ï¿½ï¿½ì˜£
=======
                            // OLD:NEW °°Àº °ÍÀÌ ÀÖÀ¸¸é type=dup ·Î ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                        // OLD:NEW åª›ìˆˆï¿½ å¯ƒê»‹ì”  ï¿½ë¾¾ï¿½ì‘ï§Žï¿½ type=ext æ¿¡ï¿½ ï¿½ï¿½ï¿½ì˜£
=======
                        // OLD:NEW °°Àº °ÍÀÌ ¾øÀ¸¸é type=ext ·Î ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                        // NEW:RESLT åª›ìˆˆï¿½ å¯ƒê»‹ì”  ï¿½ë¾¾ï¿½ì‘ï§Žï¿½ type=new æ¿¡ï¿½ ï¿½ï¿½ï¿½ì˜£
=======
                        // NEW:RESLT °°Àº °ÍÀÌ ¾øÀ¸¸é type=new ·Î ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
            /* å¯ƒï¿½ï¿½ê¹‹ï¿½ë¼±ï¿½ë’— ï¿½ë–’ï¿½ë‹š JsonArray */
=======
            /* °Ë»ö¾î´Â ´Ü¼ø JsonArray */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
            // curTagIdxï¿½ë’— Cæ¿¡ï¿½ ï¿½ë“…ï¿½ì”¤ç—â‘¥ëƒ¼ ï§£ì„Žâ”
=======
            // curTagIdx´Â C·Î ½ÂÀÎÃë¼Ò Ã³¸®
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
            if (curTagIdx > 0) {
                ItemsTags reqCC = new ItemsTags();
                reqCC.setIdx(itemIdx);
                reqCC.setStat("D");
                reqCC.setTagidx(curTagIdx);
                int rt2 = itemsTagsMapper.uptItemsTagsKeysStat(reqCC);
                rt += rt2;
            }
            */

<<<<<<< HEAD
            // ï§¤ì’–í¹ ï¿½ë“…ï¿½ì”¤ ï§¡â‘¥ë‹”
=======
            // ÃÖÃÊ ½ÂÀÎ Â÷¼ö
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
            ItemsTags reqO = new ItemsTags();
            reqO.setIdx(itemIdx);
            reqO.setStat("S");
            int firstTaggedIdx = itemsTagsMapper.getMinTagsIdxByItemIdx(reqO);

<<<<<<< HEAD
            // ï§¤ì’–í¹ ï¿½ë“…ï¿½ì”¤ ï§¡â‘¥ë‹”ï¿½ï¿½ ï§ë‰ï¿½ï§ï¿½ ï§¡â‘¥ë‹” (ï¿½ë“…ï¿½ì”¤ ï¿½ë¿¬éºï¿½ è‡¾ë‹¿ï¿½) åª›ï¿½ ï¿½ë–Žç‘œï¿½ å¯ƒìŽŒìŠ¦ï§ï¿½  ï§ë‰ï¿½ï§ï¿½ ï§¡â‘¥ë‹” ï¿½ê¶˜ï¿½ì £
=======
            // ÃÖÃÊ ½ÂÀÎ Â÷¼ö¿Í ¸¶Áö¸· Â÷¼ö (½ÂÀÎ ¿©ºÎ ¹«°ü) °¡ ´Ù¸¥ °æ¿ì¸¸  ¸¶Áö¸· Â÷¼ö »èÁ¦
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

<<<<<<< HEAD
                //items_histï¿½ë¿‰ ï¿½ë²‘æ¿¡ï¿½ for ï¿½ë„»æ€¨ï¿½
=======
                //items_hist¿¡ µî·Ï for Åë°è
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                Items itemInfo = itemsService.getItemsByIdx(reqIt);
                String movietitle = "";
                movietitle = (itemInfo != null && itemInfo.getTitle() != null) ? itemInfo.getTitle().trim() : "";
                int rthist = itemsService.insItemsHist(itemIdx, "meta", "RECV", movietitle, "RESTORE_META", curTagIdx);

<<<<<<< HEAD
                // items ï¿½ì“½ tagcntç‘œï¿½ ï§ë‰ï¿½ï§ï¿½ tagidx æ¿¡ï¿½ ï¿½ë‹”ï¿½ì ™
=======
                // items ÀÇ tagcnt¸¦ ¸¶Áö¸· tagidx ·Î ¼öÁ¤
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                //int maxTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqO);
                reqIt.setTagcnt(curTagIdx);
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
                        String metasTypes = "WHEN, WHERE, WHAT, WHO, EMOTION, CHARACTER";
                        if(metasTypes.contains(tmpMapArrayName)) {
                            tmpMapArrayName = "METAS" + tmpMapArrayName;
                        }
<<<<<<< HEAD
                        // uiï¿½ë¿‰ï¿½ê½Œï¿½ë’— listsearchkeywords ï¿½ì‚Žï¿½ê¹­æ¿¡ï¿½ æ€¨ë“¬ê°šï¿½ë¾¾ï¿½ì”  ï¿½ìŸ¾ï¿½ë„šï¿½ë§–
=======
                        // ui¿¡¼­´Â listsearchkeywords ÇüÅÂ·Î °ø¹é¾øÀÌ Àü¼ÛµÊ
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                        if(tmpMapArrayName.startsWith("LIST")) {
                            tmpMapArrayName = tmpMapArrayName.replace("LIST", "LIST_");
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
            , int itemid, int curTagIdx) {
        int rt = 0;
<<<<<<< HEAD

        /* ï¿½ï¿½ï¿½ì—¯ è¹‚ï¿½ ï¿½ë¸¸ï¿½ë€¡ ï¿½ë¸˜ï¿½ì” ï¿½ë€¥ ä»¥ï¿½ add,modï¿½ë’— ï¿½ê¶—ï¿½ìŸ¾ï¿½ë¿‰ ç•°ë¶½ï¿½ */
=======
        System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ½ÃÀÛ");
        System.out.println(" - origMetasArraysByType = " + origMetasArraysByType.toString());
        System.out.println(" - actionItemsArraysByType = " + actionItemsArraysByType.toString());
        System.out.println(" - typesArr = " + typesArr.toString());
        System.out.println(" - itemid = " + itemid);
        System.out.println(" - curTagIdx = " + curTagIdx);
        
        /* Å¸ÀÔ º° ¾×¼Ç ¾ÆÀÌÅÛ Áß add,mod´Â »çÀü¿¡ Ãß°¡ */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
        ArrayList<String> dicTypes = new ArrayList<String>();
        dicTypes.add("METASWHEN");
        dicTypes.add("METASWHERE");
        dicTypes.add("METASWHO");
        dicTypes.add("METASWHAT");
        dicTypes.add("METASEMOTION");
        dicTypes.add("METASCHARACTER");

        System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸»çÀü ½ÃÀÛ");
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
        System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸»çÀü ³¡");
        
        System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® ½ÃÀÛ");
        if (typesArr != null && origMetasArraysByType != null && actionItemsArraysByType != null) {
            System.out.println("#ELOG.origMetasArraysByType:"+origMetasArraysByType.toString());

            //for (JsonElement atype1 : typesArr) {
            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes setEmptyMetas ½ÃÀÛ");
            JsonObject origMetasArraysByType2 = setEmptyMetas(origMetasArraysByType, this.getOrigTypes());
            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes setEmptyMetas ³¡");
            
            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® ½ÃÀÛ");
            for (String atype : this.getOrigTypes()) {
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " ½ÃÀÛ");
                //String atype = atype1.getAsString();
                atype = atype.replace("\"", "");
                atype = atype.toUpperCase();

                JsonArray origMetaArr = null;
                JsonArray changeMetaArr = null;
                String destMeta = "";

                if (origMetasArraysByType2.get(atype) != null) {
                    System.out.println("#Change type(" + atype + ") orig meta::"+origMetasArraysByType2.toString());
                    origMetaArr = (JsonArray) origMetasArraysByType2.get(atype);
                    System.out.println("#Change type(" + atype + ") orig meta datas::" + origMetaArr);
                }

                if (actionItemsArraysByType.get(atype) != null) {
                    changeMetaArr = (JsonArray) actionItemsArraysByType.get(atype);
                    System.out.println("#Change type(" + atype + ") changing meta datas to::" + changeMetaArr);
                }

                if (changeMetaArr != null) {
                	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " - changeMetaArr ³Î ¾Æ´Ô");
                	/* get meta data for saving */
                    JsonArray destArr = null;
                    JsonArray destArr2 = null;
                    if(!"LIST_SEARCHKEYWORDS".equals(atype) && !"WORDS_SNS".equals(atype) && !"LIST_AWARD".equals(atype)) {
                            //&& !"LIST_SUBGENRE".equals(atype)) {
                        destArr = this.getTargetMetasArray(atype, origMetaArr, changeMetaArr);
                        destArr2 = this.getRemoveDupTargetMetasArray(destArr);
                        System.out.println("#ELOG.destArr(JsonObject): datas::"+destArr2.toString());
                    } else {
                        destArr = this.getTargetMetasArrayOnlyString(atype, origMetaArr, changeMetaArr);
                        destArr2 = this.getRemoveDupTargetMetasArrayOnlyString(destArr);
                        System.out.println("#ELOG.destArr(String): datas::"+destArr2.toString());
                    }
                    if (destArr != null) {
                        destMeta = destArr2.toString();

                        System.out.println("#MLOG DestArr cause changed for type:" + atype + " :: " + destMeta.toString());

<<<<<<< HEAD
                        /* æ¹²ê³—ã€ˆ ï§Žë·€ï¿½ï¿½ï¿½ ç•°ë¶½ï¿½ ï¿½ë¸¸ï¿½ë€¡ï¿½ë¸˜ï¿½ì” ï¿½ë€¥ï¿½ë±¾ï¿½ì”  è«›ì„ìºï¿½ë§‚ TYPE(ex> METASWHEN) è¹‚ï¿½ ï§Žë·€ï¿½JsonArrayåª›ï¿½ ä»¥ï¿½é®ê¾¨ë¦ºï§Žï¿½ ï¿½ì½ï¿½ì˜± tagIdxç‘œï¿½ æ¹²ê³—ï¿½ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ */
=======
                        /* ±âÁ¸ ¸ÞÅ¸¿Í Ãß°¡ ¾×¼Ç¾ÆÀÌÅÛµéÀÌ ¹Ý¿µµÈ TYPE(ex> METASWHEN) º° ¸ÞÅ¸JsonArray°¡ ÁØºñµÇ¸é ÇöÀç tagIdx¸¦ ±âÁØÀ¸·Î ¾÷µ¥ÀÌÆ® */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                        if (!atype.toUpperCase().contains("AWARD")) {
                            ItemsTags reqMeta = new ItemsTags();
                            reqMeta.setIdx(itemid);
                            reqMeta.setTagidx(curTagIdx);
                            reqMeta.setMtype(atype);
                            reqMeta.setMeta(destMeta);

                            //System.out.println("#MLOG change insItemsTagsMetas data:"+reqMeta.toString());
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : mtype,meta = " + atype+","+destMeta);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : reqMeta = " + reqMeta);
                            rt = this.insItemsTagsMetas(reqMeta);//ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ® ÁÖ¼®´ÞÀ½!!!!
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ ÈÄ : rt = " + rt);
                        } else {
<<<<<<< HEAD
                            // AWARDï¿½ì“½ å¯ƒìŽŒìŠ¦ items_metasï¿½ë¿‰ æ¹²ê³—ã€ˆï§Žë·€ï¿½ç‘œï¿½ ï¿½ì‘€ï§žï¿½ï¿½ë¸³ï¿½ë–Ž 18.05.15
=======
                            // AWARDÀÇ °æ¿ì items_metas¿¡ ±âÁ¸¸ÞÅ¸¸¦ À¯ÁöÇÑ´Ù 18.05.15
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                            ItemsMetas reqM = new ItemsMetas();
                            reqM.setIdx(itemid);
                            reqM.setMtype("award");
                            reqM.setMeta(destMeta);
                            reqM.setRegid(serverid);

                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : mtype,meta = " + atype+","+destMeta);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : reqM = " + reqM);
                            rt = itemsMetasMapper.insItemsMetas(reqM);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ ÈÄ : rt = " + rt);
                            
                            System.out.println("#insItemsMetas for AWARD1:"+reqM.toString());
                        }
                    } else {
                        System.out.println("#MLOG DestArr null for type:" + atype);
                    }
                } else {
                	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " - changeMetaArr ³Î ¸ÂÀ½");
                    if (origMetaArr != null) {
                        destMeta = origMetaArr.toString();
<<<<<<< HEAD
                        /* æ¹²ê³—ã€ˆ ï§Žë·€ï¿½ï¿½ë¿‰ï¿½ê½Œ ç•°ë¶½ï¿½ ï¿½ë¸¸ï¿½ë€¡ ï¿½ë¸˜ï¿½ì” ï¿½ë€¥ï¿½ë±¾ï¿½ì”  ï¿½ë¾¾ï¿½ë’— å¯ƒìŽŒìŠ¦ æ¹²ê³—ã€ˆ ï§Žë·€ï¿½ æ´¹ëªƒï¿½æ¿¡ï¿½ ï¿½ì½ï¿½ì˜± tagIdxï¿½ë¿‰ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ */
=======
                        /* ±âÁ¸ ¸ÞÅ¸¿¡¼­ Ãß°¡ ¾×¼Ç ¾ÆÀÌÅÛµéÀÌ ¾ø´Â °æ¿ì ±âÁ¸ ¸ÞÅ¸ ±×´ë·Î ÇöÀç tagIdx¿¡ ¾÷µ¥ÀÌÆ® */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                        if (!atype.toUpperCase().contains("AWARD")) {
                            ItemsTags reqMeta = new ItemsTags();
                            reqMeta.setIdx(itemid);
                            reqMeta.setTagidx(curTagIdx);
                            reqMeta.setMtype(atype);	//MTYPE
                            reqMeta.setMeta(destMeta);	//ÅëÂ°·Î META ÄÃ·³¿¡ ÀúÀå

                            //System.out.println("#MLOG uptItemsTagsMetas data:"+reqMeta.toString());
                            
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : mtype,meta = " + atype+","+destMeta);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : reqMeta = " + reqMeta);
                            rt = this.insItemsTagsMetas(reqMeta);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ ÈÄ : rt = " + rt);
                        } else {
<<<<<<< HEAD
                            // AWARDï¿½ì“½ å¯ƒìŽŒìŠ¦ items_metasï¿½ë¿‰ æ¹²ê³—ã€ˆï§Žë·€ï¿½ç‘œï¿½ ï¿½ì‘€ï§žï¿½ï¿½ë¸³ï¿½ë–Ž 18.05.15
=======
                            // AWARDÀÇ °æ¿ì items_metas¿¡ ±âÁ¸¸ÞÅ¸¸¦ À¯ÁöÇÑ´Ù 18.05.15
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                            ItemsMetas reqM = new ItemsMetas();
                            reqM.setIdx(itemid);
                            reqM.setMtype("award");
                            reqM.setMeta(destMeta);
                            reqM.setRegid(serverid);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : mtype,meta = " + atype+","+destMeta);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ Àü : reqM = " + reqM);
                            rt = itemsMetasMapper.insItemsMetas(reqM);
                            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " insItemsTagsMetas µ¿ÀÛ ÈÄ : rt = " + rt);

                            System.out.println("#insItemsMetas for AWARD2:"+reqM.toString());
                        }
                    }
                }
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® : atype = " + atype + " ³¡");
            }
            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® ³¡");
        }
        System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸ÞÅ¸ ¹Ýº¹¹® ³¡");
        System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.processMetaObjectByTypes ¸®ÅÏ : rt = " + rt);
        return rt;
    }

    private List<String> getOrigTypes() {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHERE");
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

<<<<<<< HEAD
        /* ï¿½ï¿½ï¿½ì—¯ è¹‚ï¿½ ï¿½ë¸¸ï¿½ë€¡ ï¿½ë¸˜ï¿½ì” ï¿½ë€¥ ä»¥ï¿½ add,modï¿½ë’— ï¿½ê¶—ï¿½ìŸ¾ï¿½ë¿‰ ç•°ë¶½ï¿½
=======
        /* Å¸ÀÔ º° ¾×¼Ç ¾ÆÀÌÅÛ Áß add,mod´Â »çÀü¿¡ Ãß°¡
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
    public int changeMetasArraysByTypeFromInputItems (int itemid, String items, String duration, String sendnow) {
		System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems ½ÃÀÛ");
		System.out.println(" - itemid = " + itemid);
		System.out.println(" - items = " + items);
		System.out.println(" - duration = " + duration);
		System.out.println(" - sendnow = " + sendnow);
		
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
            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - input asfasfd = " + items);
            JsonObject actionItemsArraysByType = this.getArraysByTypeFromInputItems(items);
            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - output asfasfd = " + actionItemsArraysByType.toString());
            System.out.println("#actionItemsArraysByType:"+actionItemsArraysByType.toString());
            JsonArray typesArr = null;
            if (actionItemsArraysByType.get("typesArr") != null)
                typesArr = (JsonArray) actionItemsArraysByType.get("typesArr");
            //System.out.println("#typesArr:"+typesArr.toString());

            int tagcnt = 0;

<<<<<<< HEAD
            /* æ¹²ê³—ë“…ï¿½ì”¤ï¿½ë§‚ ï§Žë·€ï¿½åª›ï¿½ ï¿½ë¾¾ï¿½ì“£ å¯ƒìŽŒìŠ¦ (ï§¤ì’“ë  tagIdxåª›ï¿½ èª˜ëª„ë“…ï¿½ì”¤ï¿½ì”¤ å¯ƒìŽŒìŠ¦) ï§Žë·€ï¿½ï¿½ë‹”ï¿½ì ™ ï¿½ì‘ ï¿½ê¸½ï¿½ê¹­è¹‚ï¿½å¯ƒï¿½, ï¿½ë“…ï¿½ì”¤ ï§£ì„Žâ”ï¿½ë¸³ï¿½ë–Ž */
=======
            /* ±â½ÂÀÎµÈ ¸ÞÅ¸°¡ ¾øÀ» °æ¿ì (ÃÖ±Ù tagIdx°¡ ¹Ì½ÂÀÎÀÎ °æ¿ì) ¸ÞÅ¸¼öÁ¤ ÈÄ »óÅÂº¯°æ, ½ÂÀÎ Ã³¸®ÇÑ´Ù */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
            if(lastTag != null && !"S".equals(lastTag.getStat())) {
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ±â½ÂÀÎµÈ ¸ÞÅ¸°¡ ¾øÀ» °æ¿ì");
            	
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - getItemsMetasByItemIdx ½ÇÇà Àü");
            	JsonObject origMetasArraysByType = this.getItemsMetasByItemIdx(itemid, false);
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - getItemsMetasByItemIdx µ¿ÀÛ ÈÄ : origMetasArraysByType = " + origMetasArraysByType.toString());

<<<<<<< HEAD
                // AWARD ï§£ì„Žâ”ç‘œï¿½ ï¿½ìžï¿½ë¹ ITEMS_METASï¿½ë¿‰ï¿½ê½Œ ï¿½ì”«ï¿½ë¼±ï¿½ê½Œ æ´ÑŠâ€œï§£ëŒë¿‰ ç•°ë¶½ï¿½
=======
                // AWARD Ã³¸®¸¦ À§ÇØ ITEMS_METAS¿¡¼­ ÀÐ¾î¼­ ±¸Á¶Ã¼¿¡ Ãß°¡
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - getAwardObject µ¿ÀÛ Àü(È£Ãâ) : AWARD Ã³¸®¸¦ À§ÇØ ITEMS_METAS¿¡¼­ ÀÐ¾î¼­ ±¸Á¶Ã¼¿¡ Ãß°¡");
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                origMetasArraysByType = this.getAwardObject(itemid, origMetasArraysByType);
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - getAwardObject µ¿ÀÛ ÈÄ : origMetasArraysByType = " + origMetasArraysByType.toString());

                System.out.println("#origMetasArraysByType:"+origMetasArraysByType.toString());

<<<<<<< HEAD
                /* action_itemï¿½ì”  ï¿½ì—³ï¿½ë’— å¯ƒìŽŒìŠ¦ ï¿½ï¿½ï¿½ì—¯è¹‚ï¿½ meta ï¿½ë‹”ï¿½ì ™ */
=======
                /* action_itemÀÌ ÀÖ´Â °æ¿ì Å¸ÀÔº° meta ¼öÁ¤ */
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes µ¿ÀÛ Àü(È£Ãâ) action_itemÀÌ ÀÖ´Â °æ¿ì Å¸ÀÔº° meta ¼öÁ¤");
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                int rtm = this.processMetaObjectByTypes(origMetasArraysByType, actionItemsArraysByType, typesArr, itemid, curTagIdx);
<<<<<<< HEAD

                /* ï¿½ë¹ï¿½ë–¦ items_tags_keys ç‘œï¿½ ï¿½ë“…ï¿½ì”¤ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ ï¿½ë¸³ï¿½ë–Ž */
=======
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes µ¿ÀÛ ÈÄ : rtm = " + rtm);
            	System.out.println(" - origMetasArraysByType " + origMetasArraysByType.toString());
            	System.out.println(" - actionItemsArraysByType " + actionItemsArraysByType.toString());
            	System.out.println(" - typesArr " + typesArr);
            	System.out.println(" - itemid " + itemid);
            	System.out.println(" - curTagIdx " + curTagIdx);
            	
                /* ÇØ´ç items_tags_keys ¸¦ ½ÂÀÎÀ¸·Î ¾÷µ¥ÀÌÆ® ÇÑ´Ù */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                ItemsTags reqConfirm = new ItemsTags();
                reqConfirm.setIdx(itemid);
                reqConfirm.setTagidx(curTagIdx);
                reqConfirm.setStat("S");
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - uptItemsTagsKeysStat µ¿ÀÛ Àü");
                int rts = this.uptItemsTagsKeysStat(reqConfirm);
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - uptItemsTagsKeysStat µ¿ÀÛ ÈÄ : rts = " + rts);

<<<<<<< HEAD
                /* ï¿½ë¹ï¿½ë–¦ items_stat ç‘œï¿½ ï¿½ë“…ï¿½ì”¤ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ ï¿½ë¸³ï¿½ë–Ž */
=======
                /* ÇØ´ç items_stat ¸¦ ½ÂÀÎÀ¸·Î ¾÷µ¥ÀÌÆ® ÇÑ´Ù */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                reqIt = new Items();
                reqIt.setIdx(itemid);
                reqIt.setStat("ST");
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat µ¿ÀÛ Àü");
                int rti = itemsMapper.insItemsStat(reqIt);
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat µ¿ÀÛ ÈÄ : rti = " + rti);

                rt = 1;

                tagcnt = curTagIdx + 1;
            } else {
<<<<<<< HEAD
                /* ï¿½ì” ï¿½ìŸ¾ ï¿½ê¸½ï¿½ê¹­ è‚„ë¶¾ë±¶ ï¿½ì†—ï¿½ì”¤ï¿½ë¸¯ï¿½ë¿¬ ï¿½ë“…ï¿½ì”¤ï¿½ì…¿çŒ·ï¿½(ST)åª›ï¿½ ï¿½ë¸˜ï¿½ë•¶ å¯ƒìŽŒìŠ¦ ï¿½ë“…ï¿½ì”¤ï¿½ì…¿çŒ·ëš®ì¤ˆ ï§£ì„Žâ”  added 2018.04.11 */
=======
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀÌÀü »óÅÂ ÄÚµå È®ÀÎÇÏ¿© ½ÂÀÎ¿Ï·á(ST)°¡ ¾Æ´Ñ °æ¿ì ½ÂÀÎ¿Ï·á·Î Ã³¸®  added 2018.04.11");
                /* ÀÌÀü »óÅÂ ÄÚµå È®ÀÎÇÏ¿© ½ÂÀÎ¿Ï·á(ST)°¡ ¾Æ´Ñ °æ¿ì ½ÂÀÎ¿Ï·á·Î Ã³¸®  added 2018.04.11 */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                String oldItemsStat = itemsMapper.getItemsStatByIdx(itemid);
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - oldItemsStat " + oldItemsStat);
                if (!"ST".equals(oldItemsStat)) {
<<<<<<< HEAD
                    /* ï¿½ë¹ï¿½ë–¦ items_tags_keys ç‘œï¿½ ï¿½ë“…ï¿½ì”¤ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ ï¿½ë¸³ï¿½ë–Ž
=======
                    /* ÇØ´ç items_tags_keys ¸¦ ½ÂÀÎÀ¸·Î ¾÷µ¥ÀÌÆ® ÇÑ´Ù
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    ItemsTags reqConfirm = new ItemsTags();
                    reqConfirm.setIdx(itemid);
                    reqConfirm.setTagidx(curTagIdx);
                    reqConfirm.setStat("S");
                    int rts = this.uptItemsTagsKeysStat(reqConfirm);*/

<<<<<<< HEAD
                    /* ï¿½ë¹ï¿½ë–¦ items_stat ç‘œï¿½ ï¿½ë“…ï¿½ì”¤ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ ï¿½ë¸³ï¿½ë–Ž */
=======
                    /* ÇØ´ç items_stat ¸¦ ½ÂÀÎÀ¸·Î ¾÷µ¥ÀÌÆ® ÇÑ´Ù */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    reqIt = new Items();
                    reqIt.setIdx(itemid);
                    reqIt.setStat("ST");
                	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat µ¿ÀÛ Àü");
                    int rti = itemsMapper.insItemsStat(reqIt);
                	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat µ¿ÀÛ ÈÄ : rti = " + rti);
                } else {
<<<<<<< HEAD
                    /* æ¹²ê³—ë“…ï¿½ì”¤ï¿½ë§‚ ï§Žë·€ï¿½åª›ï¿½ ï¿½ì—³ï¿½ì“£ å¯ƒìŽŒìŠ¦ tagidxç‘œï¿½ ï¿½ë–Šæ´¹ï¿½ ï¿½ê¹®ï¿½ê½¦ï¿½ë¸³ï¿½ë–Ž..  18.05.16 */
=======
                    /* ±â½ÂÀÎµÈ ¸ÞÅ¸°¡ ÀÖÀ» °æ¿ì tagidx¸¦ ½Å±Ô »ý¼ºÇÑ´Ù..  18.05.16 */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    curTagIdx =  this.getCurrTagsIdxForInsert(itemid);
                	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - curTagIdx " + curTagIdx);

<<<<<<< HEAD
                    /* ï¿½ë¹ï¿½ë–¦ items_tags_keys ç‘œï¿½ ï¿½ë“…ï¿½ì”¤ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ ï¿½ë¸³ï¿½ë–Ž
=======
                    /* ÇØ´ç items_tags_keys ¸¦ ½ÂÀÎÀ¸·Î ¾÷µ¥ÀÌÆ® ÇÑ´Ù
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    ItemsTags reqConfirm = new ItemsTags();
                    reqConfirm.setIdx(itemid);
                    reqConfirm.setTagidx(curTagIdx);
                    reqConfirm.setStat("S");
                    int rts = this.uptItemsTagsKeysStat(reqConfirm);*/

<<<<<<< HEAD
                    /* ï¿½ë¹ï¿½ë–¦ itemsï¿½ì“½ tagcntç‘œï¿½ ï§¤ì’–ì¥Œ tagidxæ¿¡ï¿½ ï¿½ë‹”ï¿½ì ™ï¿½ë¸³ï¿½ë–Ž */
                    // ï¿½ë¸˜ï¿½ì˜’ ï§ë‰ï¿½ï§ï¿½ ï¿½ì”ªï¿½ì”¤ ï§¡ëª„â€œ
                    /* ï¿½ë¹ï¿½ë–¦ items_stat ç‘œï¿½ ï¿½ë“…ï¿½ì”¤ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ ï¿½ë¸³ï¿½ë–Ž */
=======
                    /* ÇØ´ç itemsÀÇ tagcnt¸¦ ÃÖÁ¾ tagidx·Î ¼öÁ¤ÇÑ´Ù */
                    // ¾Æ·¡ ¸¶Áö¸· ¶óÀÎ ÂüÁ¶
                    /* ÇØ´ç items_stat ¸¦ ½ÂÀÎÀ¸·Î ¾÷µ¥ÀÌÆ® ÇÑ´Ù */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    reqIt = new Items();
                    reqIt.setIdx(itemid);
                    reqIt.setStat("ST");
                	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat µ¿ÀÛ Àü");
                    int rti = itemsMapper.insItemsStat(reqIt);
                	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - itemsMapper.insItemsStat µ¿ÀÛ ÈÄ : rti = " + rti);
                }

<<<<<<< HEAD
                /* æ¹²ê³—ë“…ï¿½ì”¤ï¿½ë§‚ ï§Žë·€ï¿½åª›ï¿½ ï¿½ì—³ï¿½ì“£ å¯ƒìŽŒìŠ¦, items_tags_metas ï§ï¿½ ï¿½ë‹”ï¿½ì ™ï¿½ë¸³ï¿½ë–Ž */
=======
                /* ±â½ÂÀÎµÈ ¸ÞÅ¸°¡ ÀÖÀ» °æ¿ì, items_tags_metas ¸¸ ¼öÁ¤ÇÑ´Ù */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                JsonObject origMetasArraysByType = this.getItemsMetasByItemIdxForUpdate(itemid, this.getOrigTypes());

<<<<<<< HEAD
                // AWARD ï§£ì„Žâ”ç‘œï¿½ ï¿½ìžï¿½ë¹ ITEMS_METASï¿½ë¿‰ï¿½ê½Œ ï¿½ì”«ï¿½ë¼±ï¿½ê½Œ æ´ÑŠâ€œï§£ëŒë¿‰ ç•°ë¶½ï¿½
=======
                // AWARD Ã³¸®¸¦ À§ÇØ ITEMS_METAS¿¡¼­ ÀÐ¾î¼­ ±¸Á¶Ã¼¿¡ Ãß°¡
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                origMetasArraysByType = this.getAwardObject(itemid, origMetasArraysByType);

                System.out.println("#origMetasArraysByType:"+origMetasArraysByType.toString());

<<<<<<< HEAD
                /* action_itemï¿½ì”  ï¿½ì—³ï¿½ë’— å¯ƒìŽŒìŠ¦ ï¿½ï¿½ï¿½ì—¯è¹‚ï¿½ meta ï¿½ë‹”ï¿½ì ™ */
=======
                /* action_itemÀÌ ÀÖ´Â °æ¿ì Å¸ÀÔº° meta ¼öÁ¤ */
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes µ¿ÀÛ Àü");
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                int rtm = this.processMetaObjectByTypes(origMetasArraysByType, actionItemsArraysByType, typesArr, itemid, curTagIdx);
            	System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - processMetaObjectByTypes µ¿ÀÛ ÈÄ : rtm = " + rtm);
            	System.out.println(" - origMetasArraysByType " + origMetasArraysByType.toString());
            	System.out.println(" - actionItemsArraysByType " + actionItemsArraysByType.toString());
            	System.out.println(" - typesArr " + typesArr);
            	System.out.println(" - itemid " + itemid);
            	System.out.println(" - curTagIdx " + curTagIdx);

                rt = 1;

                tagcnt = curTagIdx;
            }

            System.out.println("#ELOG./pop/meta/upt/array rt:"+rt);

            System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç Á÷Àü");
            if (rt > 0) {
<<<<<<< HEAD
                /* ï¿½ë¹ï¿½ë–¦ items_tags_keys ç‘œï¿½ ï¿½ë“…ï¿½ì”¤ï¿½ì‘æ¿¡ï¿½ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ ï¿½ë¸³ï¿½ë–Ž  */
=======
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0");
                /* ÇØ´ç items_tags_keys ¸¦ ½ÂÀÎÀ¸·Î ¾÷µ¥ÀÌÆ® ÇÑ´Ù  */
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                ItemsTags reqConfirm = new ItemsTags();
                reqConfirm.setIdx(itemid);
                reqConfirm.setTagidx(curTagIdx);
                reqConfirm.setStat("S");
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - uptItemsTagsKeysStat ½ÇÇà Àü");
                System.out.println(" - itemid = " + itemid);
                System.out.println(" - curTagIdx = " + curTagIdx);
                System.out.println(" - stat = S");
                int rts = this.uptItemsTagsKeysStat(reqConfirm);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - uptItemsTagsKeysStat ½ÇÇà ÈÄ : rts = " + rts);
                System.out.println(" - itemid = " + itemid);
                System.out.println(" - curTagIdx = " + curTagIdx);
                System.out.println(" - stat = S");

<<<<<<< HEAD
                //items_histï¿½ë¿‰ ï¿½ë²‘æ¿¡ï¿½ for ï¿½ë„»æ€¨ï¿½
=======
                //items_hist¿¡ µî·Ï for Åë°è
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                reqIt = new Items();
                reqIt.setIdx(itemid);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - getItemsByIdx ½ÇÇà Àü");
                Items itemInfo = itemsService.getItemsByIdx(reqIt);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - getItemsByIdx ½ÇÇà ÈÄ : itemInfo = " + itemInfo.toString());
                String movietitle = "";
                movietitle = (itemInfo != null && itemInfo.getTitle() != null) ? itemInfo.getTitle().trim() : "";
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - insItemsHist ½ÇÇà Àü");
                int rthist = itemsService.insItemsHist(itemid, "meta", "S", movietitle, "CONFIRM_META", itemid);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - insItemsHist ½ÇÇà ÈÄ : rthist = " + rthist);

<<<<<<< HEAD
                // TagCnt 1 ï§ì•·ï¿½ // ï¿½ì”ªï¿½ë–’ ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ æ´Ñ‰Ð¦ï¿½ì‘æ¿¡ï¿½ ï¿½ë¹å¯ƒï¿½
=======
                // TagCnt 1 Áõ°¡ // ÀÏ´Ü ¾÷µ¥ÀÌÆ® ±¸¹®À¸·Î ÇØ°á
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                //int oldTagCnt = itemInfo.getTagcnt();
                //int newTagCnt = oldTagCnt + 1;
                //reqIt.setTagcnt(newTagCnt);

<<<<<<< HEAD
                /* ï¿½ë¹ï¿½ë–¦ items ï¿½ì ™è¹‚ëŒ€ï¿½ï¿½ è¹‚ï¿½å¯ƒì€ë¸³ï¿½ë–Ž.  tagcnt++,  duration */
=======
                /* ÇØ´ç items Á¤º¸¸¦ º¯°æÇÑ´Ù.  tagcnt++,  duration */
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - ÇØ´ç items Á¤º¸¸¦ º¯°æÇÑ´Ù");
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                if (!"".equals(duration)) reqIt.setDuration(duration);
                reqIt.setTagcnt(tagcnt);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - uptItemsTagcnt ½ÇÇà Àü");
                int rtu = itemsService.uptItemsTagcnt(reqIt);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - uptItemsTagcnt ½ÇÇà ÈÄ : rtu = " + rtu);
                logger.info("#MLOG:uptItemsTagcnt for itemIdx:"+itemid);

<<<<<<< HEAD
                /* ï¿½ë¹ï¿½ë–¦ itemsï¿½ì“½ sched_target_content ï¿½ìè‡¾ëª„ì“£ ï§â‘¤ëª¢ ï¿½ê¶˜ï¿½ì £ï¿½ë¸³ï¿½ë–Ž  18.05.16 */
=======
                /* ÇØ´ç itemsÀÇ sched_target_content ¿ø¹®À» ¸ðµÎ »èÁ¦ÇÑ´Ù  18.05.16 */
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - deleteSchedTargetContentOrigin ½ÇÇà Àü");
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                int rtd = schedTriggerService.deleteSchedTargetContentOrigin(itemid);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - deleteSchedTargetContentOrigin ½ÇÇà ÈÄ : rtd = " + rtd);
            }

            if ("Y".equals(sendnow.toUpperCase())) {
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç sendnow=Y");
                Map<String,Object> reqCcube = new HashMap();
                reqCcube.put("idx", itemid);
                reqCcube.put("regid", serverid);
                System.out.println(" - itemid = " + itemid);
                System.out.println(" - serverid = " + serverid);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - insCcubeOutput ½ÇÇà Àü");
                int rtc = ccubeService.insCcubeOutput(reqCcube);
                System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems - ÀúÀå¾×¼Ç rt > 0 - insCcubeOutput ½ÇÇà ÈÄ : rtc = " + rtc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

		System.out.println("[ÀúÀå½Ã ¸ÞÅ¸Å°¿öµå ´©¶ô Å×½ºÆ®] ItemsTagsService.changeMetasArraysByTypeFromInputItems ³¡ : rt = " + rt);
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
        Map<String, Object> wordSet = new HashMap();
        JsonArray resultArr = null;

        if (origArray != null) {
            try {
                resultArr = new JsonArray();;

                for (JsonElement je : origArray) {
                    JsonObject jo = (JsonObject) je;

                    String word1 = jo.get("word").getAsString();
                    word1 = word1.trim();

<<<<<<< HEAD
                    // Mapï¿½ì“½ keyï¿½ë¿‰ wordç‘œï¿½ ï¿½ë²‘æ¿¡ì•ºë¸¯ï¿½ë¿¬ ä»¥ë¬ë‚¬ ï¿½ì £å«„ï¿½
=======
                    // MapÀÇ key¿¡ word¸¦ µî·ÏÇÏ¿© Áßº¹ Á¦°Å
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    if (!"".equals(word1) && wordSet.get(word1) == null) {
                        resultArr.add(jo);
                        wordSet.put(word1, "exist");
                    } else {
                        System.out.println("#ELOG.metaChange skip by Duplicated word:"+word1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultArr;
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
        JsonArray resultArr = null;
        Map<String, Object> wordSet = new HashMap();

        List<String> origStrArr = null;

        if (origArray != null) {
            try {
                origStrArr = JsonUtil.convertJsonArrayToList(origArray);
                resultArr = new JsonArray();

                for (String word1 : origStrArr) {
                    word1 = word1.trim();

<<<<<<< HEAD
                    // Mapï¿½ì“½ keyï¿½ë¿‰ wordç‘œï¿½ ï¿½ë²‘æ¿¡ì•ºë¸¯ï¿½ë¿¬ ä»¥ë¬ë‚¬ ï¿½ì £å«„ï¿½
=======
                    // MapÀÇ key¿¡ word¸¦ µî·ÏÇÏ¿© Áßº¹ Á¦°Å
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    if (!"".equals(word1) && wordSet.get(word1) == null) {
                        resultArr.add(word1);
                        wordSet.put(word1, "exist");
                    } else {
                        System.out.println("#ELOG.metaChange skip by Duplicated word:"+word1);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            JsonObject newobj = new JsonObject();
                            newobj.addProperty("word", jo.get("word").getAsString());
                            newobj.addProperty("type", (jo.get("type") != null) ? jo.get("type").getAsString() : "");
                            newobj.addProperty("ratio", jo.get("ratio").getAsDouble());
                            resultArr.add(newobj);
                        }
                        JsonObject newobj = new JsonObject();
                        newobj.addProperty("word", toWord);
                        newobj.addProperty("type", "new");
                        newobj.addProperty("ratio", 0.0);
                        resultArr.add(newobj);
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
                String toWord = "";

                resultArr = new JsonArray();
                switch (toAction) {
                    case "add" :
                        toWord = jObj.get("target_meta").getAsString().trim();

                        List<String> tmpArr = JsonUtil.convertJsonArrayToList(origArray);

                        tmpArr.add(toWord);

                        resultArr = JsonUtil.convertListToJsonArray(tmpArr);
                        break;

                    case "mod":
                        String fromWord = jObj.get("meta").getAsString().trim();
                        toWord = jObj.get("target_meta").getAsString().trim();

                        List<String> tmpArrU = JsonUtil.convertJsonArrayToList(origArray);
                        List<String> newArrU = new ArrayList<String>();
                        for(String je : tmpArrU) {
                            if(!je.trim().equals(fromWord)) {
                                newArrU.add(je.trim());
                            } else {
                                newArrU.add(toWord);
                            }
                        }

                        resultArr = JsonUtil.convertListToJsonArray(newArrU);
                        break;

                    case "del":
                        fromWord = jObj.get("meta").getAsString().trim();

                        List<String> tmpArrD = JsonUtil.convertJsonArrayToList(origArray);
                        List<String> newArrD = new ArrayList<String>();
                        for(String je : tmpArrD) {
                            if(!je.trim().equals(fromWord)) {
                                newArrD.add(je.trim());
                            }
                        }
                        resultArr = JsonUtil.convertListToJsonArray(newArrD);

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
<<<<<<< HEAD
                    // running job ä»¥ë¬ë‚¬è«›â‘¹ï¿½ç‘œï¿½ ï¿½ìžï¿½ë¹ ï¿½ì” ï¿½ì ° ï¿½ï¿½ï¿½ì˜£
=======
                    // running job Áßº¹¹æÁö¸¦ À§ÇØ ÀÌ·Â ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                            // è­°ê³ ì‰¶ï¿½ë’— ï¿½ìŸ¾ï§£ï¿½ ï¿½ê¹­æ´¹ëªƒì°“ï¿½ï¿½
                            // ï¿½ì”«ï¿½ë¼±ï¿½ì‚© ï§Žë·€ï¿½ æ´ÑŠâ€œï§£ëŒë¿‰ ï¿½ë–Šæ´¹ï¿½ ï¿½ê¶Žï¿½ì™ï¿½ë±¶ ç•°ë¶½ï¿½
                            // from_keyword åª›ï¿½ ï¿½ì—³ï¿½ì“£ å¯ƒìŽŒìŠ¦ ï¿½ë¹ï¿½ë–¦ ï¿½ê¶Žï¿½ì™ï¿½ë±¶åª›ï¿½ ï¿½ì—³ï¿½ë’— rowï§ï¿½ ï¿½ï¿½ï¿½ê¸½ï¿½ì‘æ¿¡ï¿½ ï¿½ê½‘ï¿½ì ™
=======
                            // Á¶È¸´Â ÀüÃ¼ ÅÂ±×¸ÞÅ¸
                            // ÀÐ¾î¿Â ¸ÞÅ¸ ±¸Á¶Ã¼¿¡ ½Å±Ô Å°¿öµå Ãß°¡
                            // from_keyword °¡ ÀÖÀ» °æ¿ì ÇØ´ç Å°¿öµå°¡ ÀÖ´Â row¸¸ ´ë»óÀ¸·Î ¼±Á¤
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                            if (!from_keyword.equals(to_keyword)) {
                                req.setKeyword(from_keyword);
                            }
                            break;
                        case "mod":
<<<<<<< HEAD
                            // è­°ê³ ì‰¶ï¿½ë’— mtype, from_keywordç‘œï¿½ æ¹²ê³—ï¿½ï¿½ì‘æ¿¡ï¿½
=======
                            // Á¶È¸´Â mtype, from_keyword¸¦ ±âÁØÀ¸·Î
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                            req.setKeyword(from_keyword);
<<<<<<< HEAD
                            // ï¿½ì”«ï¿½ë¼±ï¿½ì‚© ï§Žë·€ï¿½ æ´ÑŠâ€œï§£ëŒë¿‰ï¿½ê½Œ æ¹²ê³—ã€ˆ ï¿½ê¶Žï¿½ì™ï¿½ë±¶ç‘œï¿½ to_keywordæ¿¡ï¿½ è¹‚ï¿½å¯ƒï¿½
=======
                            // ÀÐ¾î¿Â ¸ÞÅ¸ ±¸Á¶Ã¼¿¡¼­ ±âÁ¸ Å°¿öµå¸¦ to_keyword·Î º¯°æ
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                            break;
                        case "del":
<<<<<<< HEAD
                            // è­°ê³ ì‰¶ï¿½ë’— mtype, from_keywordç‘œï¿½ æ¹²ê³—ï¿½ï¿½ì‘æ¿¡ï¿½
=======
                            // Á¶È¸´Â mtype, from_keyword¸¦ ±âÁØÀ¸·Î
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                            req.setKeyword(from_keyword);
<<<<<<< HEAD
                            // ï¿½ì”«ï¿½ë¼±ï¿½ì‚© ï§Žë·€ï¿½ æ´ÑŠâ€œï§£ëŒë¿‰ï¿½ê½Œ æ¹²ê³—ã€ˆ ï¿½ê¶Žï¿½ì™ï¿½ë±¶ç‘œï¿½ ï§¡ì– ë¸˜ ï¿½ì £å«„ï¿½
=======
                            // ÀÐ¾î¿Â ¸ÞÅ¸ ±¸Á¶Ã¼¿¡¼­ ±âÁ¸ Å°¿öµå¸¦ Ã£¾Æ Á¦°Å
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                            break;
                    }

<<<<<<< HEAD
                    // è¹‚ï¿½å¯ƒï¿½ ï¿½ï¿½ï¿½ê¸½ è­°ê³ ì‰¶
=======
                    // º¯°æ ´ë»ó Á¶È¸
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                                // ï¿½ï¿½ï¿½ì˜£ ï¿½ï¿½ï¿½ê¸½ ï¿½ê¹®ï¿½ê½¦
=======
                                // ÀúÀå ´ë»ó »ý¼º
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                                        // idx, tagidx, mtype ï¿½ë¿‰ ï§ìšŽí…›ï¿½ë¼± meta ï¿½ë¾½ï¿½ëœ²ï¿½ì” ï¿½ë“ƒ
=======
                                        // idx, tagidx, mtype ¿¡ ¸ÂÃß¾î meta ¾÷µ¥ÀÌÆ®
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                                        itag.setMeta(destArr2.toString());
                                        rt = itemsTagsMapper.uptItemsTagsByManual(itag);
                                    }
                                }
                            }

                        }
                    }

<<<<<<< HEAD
                    // ï¿½ê¶—ï¿½ìŸ¾ï¿½ë¿‰ï¿½ë£„ è«›ì„ìº ï¿½ì˜‰ï¿½ë¾½
=======
                    // »çÀü¿¡µµ ¹Ý¿µ ÀÛ¾÷
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    if (rt > 0) {
                        int rtd = this.changeDicKeywordsForManual(target_mtype, from_keyword, to_keyword, action);
                    }

                    Thread.sleep(3000);

<<<<<<< HEAD
                    // running job ä»¥ë¬ë‚¬è«›â‘¹ï¿½ç‘œï¿½ ï¿½ìžï¿½ë¹ ï¿½ì” ï¿½ì ° ï¿½ï¿½ï¿½ì˜£ stat = S
=======
                    // running job Áßº¹¹æÁö¸¦ À§ÇØ ÀÌ·Â ÀúÀå stat = S
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

<<<<<<< HEAD
            // ï¿½ê¶—ï¿½ìŸ¾ï¿½ë¿‰ï¿½ë£„ è«›ì„ìº ï¿½ì˜‰ï¿½ë¾½
=======
            // »çÀü¿¡µµ ¹Ý¿µ ÀÛ¾÷
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

<<<<<<< HEAD
        // request param è¹‚ï¿½è­°ï¿½ metawho -> who
=======
        // request param º¯Á¶ metawho -> who
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

<<<<<<< HEAD
                // ï¿½ì—¯ï¿½ì ° JsonArrayç‘œï¿½ META_WORD ï¿½ì‚Žï¿½ê¹­ï¿½ì“½ æ€¨ë“¬ê°šï¿½ì‘æ¿¡ï¿½ æ´Ñ‰í…‡ï¿½ë§‚ 1åª›ì’–ì“½ è‡¾ëª„ì˜„ï¿½ë¿´æ¿¡ï¿½ ç§»ì„‘ì†š
=======
                // ÀÔ·Â JsonArray¸¦ META_WORD ÇüÅÂÀÇ °ø¹éÀ¸·Î ±¸ºÐµÈ 1°³ÀÇ ¹®ÀÚ¿­·Î Ä¡È¯
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                JsonArray reqArr = JsonUtil.getJsonArray(reqJsonObjStr);
                String reqStr = this.getMetasStringFromJsonArray(reqArr, origTypes);

<<<<<<< HEAD
                // ï¿½ì—¯ï¿½ì ° reqStrï¿½ì“£ ï¿½ë¿•ï¿½ì”ªï¿½ë’ªï¿½ë–› ï¿½ê½Œç§»ì„Žï¿½ï¿½ ï¿½ë„»ï¿½ë¹ ï¿½ì‘€ï¿½ê¶—ï¿½ë£„ ï¿½ë£Šåª›ï¿½
=======
                // ÀÔ·Â reqStrÀ» ¿¤¶ó½ºÆ½ ¼­Ä¡¸¦ ÅëÇØ À¯»çµµ Æò°¡
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

<<<<<<< HEAD
                // ï¿½ï¿½ï¿½ì˜£ï¿½ë§‚ ï¿½ì˜£ç‘œëŒ€ï¿½ï¿½ åª›ï¿½ï¿½ì¡‡ï¿½ì‚©ï¿½ë–Ž.
=======
                // ÀúÀåµÈ Àå¸£¸¦ °¡Á®¿Â´Ù.
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                ItemsMetas reqIm = new ItemsMetas();
                reqIm.setIdx(itemid);
                reqIm.setMtype("genre");
                ItemsMetas genreMetas = itemsMetasMapper.getItemsMetas(reqIm);
                String itemGenre = (genreMetas != null && genreMetas.getMeta() != null) ? genreMetas.getMeta() : "";
                System.out.println("#ELOG ORIG item's GENRE:"+itemGenre);

<<<<<<< HEAD
                // ï¿½ë–Žç‘œï¿½ å¯ƒê»‰ë‚µ ï¿½ë¸˜è‡¾ï¿½ ï¿½ê¸½æ„¿ï¿½ï¿½ë¾¾ï¿½ì”  ï¿½ë“…ï¿½ì ™ ï¿½ê¶Žï¿½ì™ï¿½ë±¶åª›ï¿½ ï¿½ì—³ï¿½ì‘ï§Žï¿½ å¯ƒê³Œë‚µï¿½ë¿‰ æ´¹ï¿½ ï¿½ì˜£ç‘œëŒ€ì±¸ï¿½ì“£ ç•°ë¶½ï¿½ï¿½ë¸³ï¿½ë–Ž. dic_subgenre_genresï¿½ì“½ mtype=genre_word
=======
                // ´Ù¸¥ °Í°ú ¾Æ¹« »ó°ü¾øÀÌ Æ¯Á¤ Å°¿öµå°¡ ÀÖÀ¸¸é °á°ú¿¡ ±× Àå¸£¸íÀ» Ãß°¡ÇÑ´Ù. dic_subgenre_genresÀÇ mtype=genre_word
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                System.out.println("#ELOG genre_add_arr from reqStr:"+reqStr.toString());
                Set<String> genre_add_arr = dicService.getGenreAddByReqKeywords(reqStr, "genre_add");
                System.out.println("#ELOG GENRE_ADD by genre_add:"+genre_add_arr.toString());

                Set<String> combinedWordAndGenres = new HashSet();
                if (genre_add_arr != null) {
<<<<<<< HEAD
                    // meta_singleæ€¨ï¿½ ï¿½ï¿½è­°ê³ ë¸¯ï¿½ë¿¬ å¯ƒê³Œë‚µï¿½ë¿‰ ï¿½ï¿½ï¿½ì˜£
=======
                    // meta_single°ú ´ëÁ¶ÇÏ¿© °á°ú¿¡ ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                    for(String gs : genre_add_arr) {
                        metaSingleArr = dicService.getMetaSingleFromGenre(metaSingleArr, gs, "meta_single");
                    }
                    System.out.println("#ELOG metaSingleArr after genre_add:"+metaSingleArr.toString());

<<<<<<< HEAD
                    // meta_genre ï¿½ï¿½ ï¿½ï¿½è­°ê³ ë¸¯ï¿½ë¿¬ å¯ƒê³Œë‚µï¿½ë¿‰ ï¿½ï¿½ï¿½ì˜£
=======
                    // meta_genre ¿Í ´ëÁ¶ÇÏ¿© °á°ú¿¡ ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                // 1ï§¡ï¿½ ES responseï¿½ë¿‰ï¿½ê½Œ 10åª›ì’•ï¿½ï¿½ ï¿½ë£·ï¿½ì”¤ï¿½ë“ƒ ï¿½ê¸½æ„¿ï¿½ï¿½ë¾¾ï¿½ì”  ç—â‘¤ë±·
=======
                // 1Â÷ ES response¿¡¼­ 10°³¸¦ Æ÷ÀÎÆ® »ó°ü¾øÀÌ Ãëµæ
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

<<<<<<< HEAD
                // ES å¯ƒï¿½ï¿½ê¹‹ ï¿½ì‘ è€Œë£»ë£·ï¿½ì”¤ï¿½ë“ƒ ï¿½ë„»æ€¨ï¿½ ï¿½ë¸³ å¯ƒê³Œë‚µï¿½ë’— 1åª›ì’•ì­” ç—â‘¤ë±·
=======
                // ES °Ë»ö ÈÄ ÄÆÆ÷ÀÎÆ® Åë°ú ÇÑ °á°ú´Â 1°³¸¸ Ãëµæ
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                if (esReturnArr != null && esReturnArr.size() > 0) {
                    esReturnWord = esReturnArr.get(0);
                }

                System.out.println("#esReturnWord:(cut " + es_cut_point + ")::" + esReturnWord + " / esWords:" + esWords);

<<<<<<< HEAD
                // è€Œë£»ë£·ï¿½ì”¤ï¿½ë“ƒ ï¿½ë„»æ€¨ì‡³ë¸³ ES å¯ƒï¿½ï¿½ê¹‹ ï¿½ë„—ï¿½ëµ¿ TOP 1ï¿½ì“£ resultArrï¿½ë¿‰ ï¿½ï¿½ï¿½ì˜£
=======
                // ÄÆÆ÷ÀÎÆ® Åë°úÇÑ ES °Ë»ö ÅäÇÈ TOP 1À» resultArr¿¡ ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
                if (!"".equals(esReturnWord)) {
                    if (resultArr == null) resultArr = new JsonArray();
                    /*
                    JsonObject newWord = new JsonObject();
                    newWord.addProperty("type", "");
                    newWord.addProperty("ratio", 0.0);
                    newWord.addProperty("word", esReturnWord);
                    resultArr.add(newWord);
                    */

<<<<<<< HEAD
                    // ï¿½ë„—ï¿½ëµ¿ TOP 1 ï¿½ì“£ dic_subgenre_genres ï¿½ì“½ mtype=meta_singleæ€¨ï¿½ ï¿½ï¿½è­°ê³ ë¸¯ï¿½ë¿¬ ç•°ë¶½ï¿½ï¿½ë¼± ç—â‘¤ë±· ï¿½ì‘ resultArrï¿½ë¿‰ ï¿½ï¿½ï¿½ì˜£
=======
                    // ÅäÇÈ TOP 1 À» dic_subgenre_genres ÀÇ mtype=meta_single°ú ´ëÁ¶ÇÏ¿© Ãß°¡¾î Ãëµæ ÈÄ resultArr¿¡ ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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

<<<<<<< HEAD
                    // ï¿½ë„—ï¿½ëµ¿ TOP 1æ€¨ï¿½ genreç‘œï¿½ dic_subgenre_genres ï¿½ì“½ mtype=meta_genre ï¿½ï¿½ ï¿½ï¿½è­°ê³ ë¸¯ï¿½ë¿¬ ç•°ë¶½ï¿½ï¿½ë¼± ç—â‘¤ë±· ï¿½ì‘ resultArrï¿½ë¿‰ ï¿½ï¿½ï¿½ì˜£
=======
                    // ÅäÇÈ TOP 1°ú genre¸¦ dic_subgenre_genres ÀÇ mtype=meta_genre ¿Í ´ëÁ¶ÇÏ¿© Ãß°¡¾î Ãëµæ ÈÄ resultArr¿¡ ÀúÀå
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git

<<<<<<< HEAD
                    // ï¿½ì˜£ç‘œë‹¿ï¿½ ï¿½ì—³ï¿½ì‘ï§Žï¿½ ESï¿½ë„—ï¿½ëµ¿___ï¿½ì˜£ç‘œï¿½  è­°ê³ ë¹€ï¿½ì‘æ¿¡ï¿½ ï¿½ê¶—ï¿½ìŸ¾æ€¨ï¿½ ï¿½ï¿½è­°ê³ ë¸¯ï¿½ë¿¬ ç”±ÑŠë’ªï¿½ë“ƒ ç•°ë¶¿í…§
=======
                    // Àå¸£°¡ ÀÖÀ¸¸é ESÅäÇÈ___Àå¸£  Á¶ÇÕÀ¸·Î »çÀü°ú ´ëÁ¶ÇÏ¿© ¸®½ºÆ® ÃßÃâ
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
            // ï§ë‰ï¿½ï§ï¿½ ï¿½ì £ï¿½ì‡… ï¿½ï¿½ï¿½ê¸½ ï¿½ë¸˜ï¿½ê½£ ï¿½ìŸ»ï¿½ìŠœ
=======
            // ¸¶Áö¸· Á¦¿Ü ´ë»ó ÇÊÅÍ Àû¿ë
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                    toMeta = toMeta.replace("ï¿½ìºï¿½ì†•", "ï¿½ë–†ç”±ÑŠì«°");
=======
                    toMeta = toMeta.replace("¿µÈ­", "½Ã¸®Áî");
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
<<<<<<< HEAD
                    toMetaOrigin = toMetaOrigin.replace("ï¿½ìºï¿½ì†•", "ï¿½ë–†ç”±ÑŠì«°");
=======
                    toMetaOrigin = toMetaOrigin.replace("¿µÈ­", "½Ã¸®Áî");
>>>>>>> branch 'cmts_20190322' of https://github.com/swordKein/cmts.git
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
