package com.kthcorp.cmts.service;

import com.google.gson.*;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.*;

@Service
public class ItemsTagsService implements ItemsTagsServiceImpl {
    static Logger logger = LoggerFactory.getLogger(ItemsTagsService.class);

    public static boolean jobRuuningStat = false;

    @Value("${cmts.property.serverid}")
    private String serverid;
    @Value("${cmts.property.naver_kordic_url}")
    private String naver_kordic_url;

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
    private ManualJobHistMapper manualJobHistMapper;

    @Override
    public List<ItemsTags> getItemsTagsMetasByItemIdx(ItemsTags req) {
        return itemsTagsMapper.getItemsTagsMetasByItemIdx(req);
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
        1. 처음 등록할 경우 = 0
        2. 승인된 건이 있고 수정할 경우 = last tagIdx
        3. 승인된 건이 없고 수정할 경우 = last tagIdx
         */
        ItemsTags req = new ItemsTags();
        req.setIdx(itemIdx);
        req.setStat("Y");
        int tagidx = this.getMaxTagsIdxByItemIdx(req);

        if (tagidx == 0) {
            // 승인완료된 tagidx가 있는지 확인
            ItemsTags req2 = new ItemsTags();
            req2.setIdx(itemIdx);
            req2.setStat("S");
            int tagidx2 = this.getMaxTagsIdxByItemIdx(req2);
            //System.out.println("#confirmed tagidx2:"+tagidx2);

            // 승인완료된 tagidx2가 없으면 승인미완료인 tagidx를 리턴
            if (tagidx2 == 0) {
                // 승인완료된 tagidx 개수가 > 0 이면 1 증가
                int confirmedTagCnt = this.cntConfirmedTags(req2);
                System.out.println("#confirmedTagCnt :"+confirmedTagCnt);
                if (confirmedTagCnt > 0) {
                    tagidx = 1;
                }
            // 승인완료된 tagidx가 있으면 1 증가하여 신규건으로 저장
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
                            result.add(it.getMtype(), metas);
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
    public JsonObject getItemsMetasByItemIdx(int itemIdx, boolean isColorCode) {
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
        //System.out.println("#ELOG.getItemsMetasByItemIdx:: old.datas::"+resultObj.toString());

        JsonObject resultObj2 = getItemsMetasDupByItemIdx(resultObj, itemIdx, isColorCode);
        System.out.println("#ELOG.getItemsMetasByItemIdx:: dupCheck.datas::"+resultObj2.toString());

        /* WORDS_ASSOC 감성유의어 - 네이버사전 */
        resultObj2 = getWordsAssoc(itemIdx, resultObj2);

        System.out.println("#ELOG.getItemsMetasByItemIdx:: after.wordsAssoc.datas::"+resultObj2.toString());

        /* WORDS_GENRE */
        resultObj2 = getWordsGenre(itemIdx, resultObj2);
        System.out.println("#ELOG.getItemsMetasByItemIdx:: after.wordsGenre.datas::"+resultObj2.toString());


        /* WORDS_SNS */
        // resultObj2 = getWordsSns(itemIdx, resultObj2);

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

    private JsonObject getWordsAssoc(int itemIdx, JsonObject resultObj2) {
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

    private JsonObject getWordsGenre(int itemIdx, JsonObject resultObj2) {
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

    private String getMovieGenreFromCcubeContents(int itemIdx) {
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
        String reqUrl = naver_kordic_url;
        reqUrl = reqUrl.replace("#KEYWORD", keyword);

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
        // 빠진 type은 공백이라도 채워준다
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
            if(!"LIST_SEARCHKEYWORDS".equals(type)) {
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
                            // OLD:NEW 같은 것이 있으면 type=dup 로 저장
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
                        // OLD:NEW 같은 것이 없으면 type=ext 로 저장
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
                        // NEW:RESLT 같은 것이 없으면 type=new 로 저장
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
            /* 검색어는 단순 JsonArray */
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
            // curTagIdx는 C로 승인취소 처리
            if (curTagIdx > 0) {
                ItemsTags reqCC = new ItemsTags();
                reqCC.setIdx(itemIdx);
                reqCC.setStat("D");
                reqCC.setTagidx(curTagIdx);
                int rt2 = itemsTagsMapper.uptItemsTagsKeysStat(reqCC);
                rt += rt2;
            }

            if (rt > 0) {
                Items reqIt = new Items();
                reqIt.setIdx(itemIdx);
                int rtx = itemsMapper.uptItemsTagcntMinus(reqIt);

                //items_hist에 등록 for 통계
                Items itemInfo = itemsService.getItemsByIdx(reqIt);
                String movietitle = "";
                movietitle = (itemInfo != null && itemInfo.getTitle() != null) ? itemInfo.getTitle().trim() : "";
                int rthist = itemsService.insItemsHist(itemIdx, "meta", "RECV", movietitle, "RESTORE_META", oldTagIdx);
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
                        // ui에서는 listsearchkeywords 형태로 공백없이 전송됨
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

        /* 타입 별 액션 아이템 중 add,mod는 사전에 추가 */
        ArrayList<String> dicTypes = new ArrayList<String>();
        dicTypes.add("METASWHEN");
        dicTypes.add("METASWHERE");
        dicTypes.add("METASWHO");
        dicTypes.add("METASWHAT");
        dicTypes.add("METASEMOTION");
        dicTypes.add("METASCHARACTER");
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

        if (typesArr != null && origMetasArraysByType != null && actionItemsArraysByType != null) {
            System.out.println("#ELOG.origMetasArraysByType:"+origMetasArraysByType.toString());

            //for (JsonElement atype1 : typesArr) {
            JsonObject origMetasArraysByType2 = setEmptyMetas(origMetasArraysByType, this.getOrigTypes());

            for (String atype : this.getOrigTypes()) {
                //String atype = atype1.getAsString();
                atype = atype.replace("\"", "");
                atype = atype.toUpperCase();

                JsonArray origMetaArr = null;
                JsonArray changeMetaArr = null;
                String destMeta = "";

                if (origMetasArraysByType2.get(atype) != null) {
                    origMetaArr = (JsonArray) origMetasArraysByType2.get(atype);
                    System.out.println("#Change type(" + atype + ") orig meta datas::" + origMetaArr);
                }

                if (actionItemsArraysByType.get(atype) != null) {
                    changeMetaArr = (JsonArray) actionItemsArraysByType.get(atype);
                    System.out.println("#Change type(" + atype + ") changing meta datas to::" + changeMetaArr);
                }

                if (changeMetaArr != null) {
                    /* get meta data for saving */
                    JsonArray destArr = null;
                    JsonArray destArr2 = null;
                    if(!"LIST_SEARCHKEYWORDS".equals(atype)) {
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

                        /* 기존 메타와 추가 액션아이템들이 반영된 TYPE(ex> METASWHEN) 별 메타JsonArray가 준비되면 현재 tagIdx를 기준으로 업데이트 */
                        ItemsTags reqMeta = new ItemsTags();
                        reqMeta.setIdx(itemid);
                        reqMeta.setTagidx(curTagIdx);
                        reqMeta.setMtype(atype);
                        reqMeta.setMeta(destMeta);

                        //System.out.println("#MLOG change insItemsTagsMetas data:"+reqMeta.toString());
                        rt = this.insItemsTagsMetas(reqMeta);
                    } else {
                        System.out.println("#MLOG DestArr null for type:" + atype);
                    }
                } else {
                    if (origMetaArr != null) {
                        destMeta = origMetaArr.toString();
                        /* 기존 메타에서 추가 액션 아이템들이 없는 경우 기존 메타 그대로 현재 tagIdx에 업데이트 */
                        ItemsTags reqMeta = new ItemsTags();
                        reqMeta.setIdx(itemid);
                        reqMeta.setTagidx(curTagIdx);
                        reqMeta.setMtype(atype);
                        reqMeta.setMeta(destMeta);

                        //System.out.println("#MLOG uptItemsTagsMetas data:"+reqMeta.toString());
                        rt = this.insItemsTagsMetas(reqMeta);
                    }
                }
            }
        }
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

        /* 타입 별 액션 아이템 중 add,mod는 사전에 추가
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
    public int changeMetasArraysByTypeFromInputItems (int itemid, String items, String duration) {
        int rt = 0;
        //int curTagIdx = this.getCurrTagsIdxForInsert(itemid);
        //int curTagIdx = this.getCurrTagsIdxForSuccess(itemid);
        List<ItemsTags> tagIdxArr = this.getTagCntInfo(itemid);
        int curTagIdx = 0;
        if (tagIdxArr != null && tagIdxArr.size() > 0) {
            curTagIdx = tagIdxArr.get(0).getTagidx();
        }
        System.out.println("#MLOG /pop/meta/upt/array curTagIdx:"+curTagIdx);

        try {
            ItemsTags lastTag = this.getLastTagCntInfo(itemid);
            Items reqIt = null;

            /* get action TYPE to Arrays */
            JsonObject actionItemsArraysByType = this.getArraysByTypeFromInputItems(items);
            System.out.println("#actionItemsArraysByType:"+actionItemsArraysByType.toString());
            JsonArray typesArr = null;
            if (actionItemsArraysByType.get("typesArr") != null)
                typesArr = (JsonArray) actionItemsArraysByType.get("typesArr");
            //System.out.println("#typesArr:"+typesArr.toString());

            System.out.println("#lastTag:"+lastTag.toString());
            /* 기승인된 메타가 없을 경우 (최근 tagIdx가 미승인인 경우) 메타수정 후 상태변경, 승인 처리한다 */
            if(lastTag != null && !"S".equals(lastTag.getStat())) {
                JsonObject origMetasArraysByType = this.getItemsMetasByItemIdx(itemid, false);
                System.out.println("#origMetasArraysByType:"+origMetasArraysByType.toString());

                /* action_item이 있는 경우 타입별 meta 수정 */
                int rtm = this.processMetaObjectByTypes(origMetasArraysByType, actionItemsArraysByType, typesArr, itemid, curTagIdx);

                /* 해당 items_tags_keys 를 승인으로 업데이트 한다 */
                ItemsTags reqConfirm = new ItemsTags();
                reqConfirm.setIdx(itemid);
                reqConfirm.setTagidx(curTagIdx);
                reqConfirm.setStat("S");
                int rts = this.uptItemsTagsKeysStat(reqConfirm);

                /* 해당 items_stat 를 승인으로 업데이트 한다 */
                reqIt = new Items();
                reqIt.setIdx(itemid);
                reqIt.setStat("ST");
                int rti = itemsMapper.insItemsStat(reqIt);

                rt = 1;
            } else {
                /* 기승인된 메타가 있을 경우, items_tags_metas 만 수정한다 */
                JsonObject origMetasArraysByType = this.getItemsMetasByItemIdxForUpdate(itemid, this.getOrigTypes());

                /* action_item이 있는 경우 타입별 meta 수정 */
                int rtm = this.processMetaObjectByTypes(origMetasArraysByType, actionItemsArraysByType, typesArr, itemid, curTagIdx);

                rt = 1;
            }

            System.out.println("#ELOG./pop/meta/upt/array rt:"+rt);

            if (rt > 0) {
                //items_hist에 등록 for 통계
                reqIt = new Items();
                reqIt.setIdx(itemid);
                Items itemInfo = itemsService.getItemsByIdx(reqIt);
                String movietitle = "";
                movietitle = (itemInfo != null && itemInfo.getTitle() != null) ? itemInfo.getTitle().trim() : "";
                int rthist = itemsService.insItemsHist(itemid, "meta", "S", movietitle, "CONFIRM_META", itemid);

                // TagCnt 1 증가 // 일단 업데이트 구문으로 해결
                //int oldTagCnt = itemInfo.getTagcnt();
                //int newTagCnt = oldTagCnt + 1;
                //reqIt.setTagcnt(newTagCnt);

                /* 해당 items 정보를 변경한다.  tagcnt++,  duration */
                if (!"".equals(duration)) reqIt.setDuration(duration);
                int rtu = itemsService.uptItemsTagcnt(reqIt);
                logger.info("#MLOG:uptItemsTagcnt for itemIdx:"+itemid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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

                    // Map의 key에 word를 등록하여 중복 제거
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

                    // Map의 key에 word를 등록하여 중복 제거
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
                        toWord = jObj.get("target_meta").getAsString().trim();

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
    public List<ItemsTags> getTagCntInfo(Integer itemid) {
        return itemsTagsMapper.getTagCntInfo(itemid);
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
                    // running job 중복방지를 위해 이력 저장
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
                            // 조회는 전체 태그메타
                            // 읽어온 메타 구조체에 신규 키워드 추가
                            // from_keyword 가 있을 경우 해당 키워드가 있는 row만 대상으로 선정
                            if (!from_keyword.equals(to_keyword)) {
                                req.setKeyword(from_keyword);
                            }
                            break;
                        case "mod":
                            // 조회는 mtype, from_keyword를 기준으로
                            req.setKeyword(from_keyword);
                            // 읽어온 메타 구조체에서 기존 키워드를 to_keyword로 변경
                            break;
                        case "del":
                            // 조회는 mtype, from_keyword를 기준으로
                            req.setKeyword(from_keyword);
                            // 읽어온 메타 구조체에서 기존 키워드를 찾아 제거
                            break;
                    }

                    // 변경 대상 조회
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
                                // 저장 대상 생성
                                // #TODO
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
                                        // idx, tagidx, mtype 에 맞추어 meta 업데이트
                                        itag.setMeta(destArr2.toString());
                                        rt = itemsTagsMapper.uptItemsTagsByManual(itag);
                                    }
                                }
                            }

                        }
                    }

                    // 사전에도 반영 작업
                    if (rt > 0) {
                        int rtd = this.changeDicKeywordsForManual(target_mtype, from_keyword, to_keyword, action);
                    }

                    Thread.sleep(3000);

                    // running job 중복방지를 위해 이력 저장 stat = S
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

            // 사전에도 반영 작업
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
}
