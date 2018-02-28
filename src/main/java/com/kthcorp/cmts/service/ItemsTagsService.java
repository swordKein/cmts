package com.kthcorp.cmts.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ItemsTagsService implements ItemsTagsServiceImpl {
    static Logger logger = LoggerFactory.getLogger(ItemsTagsService.class);

    @Value("${cmts.property.serverid}")
    private String serverid;

    @Autowired
    private ItemsTagsMapper itemsTagsMapper;
    @Autowired
    private ItemsMetasMapper itemsMetasMapper;
    @Autowired
    private ItemsMapper itemsMapper;

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

            // 승인완료된 tagidx가 없으면 0으로 리턴
            if (tagidx2 == 0) {
                // 승인완료된 tagidx 개수가 > 0 이면 1 증가
                int confirmedTagCnt = this.cntConfirmedTags(req2);
                System.out.println("#confirmedTagCnt :"+confirmedTagCnt);
                if (confirmedTagCnt > 0) {
                    tagidx = 1;
                } else {
                    tagidx = 0;
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

    private JsonObject getItemsMetasByIdx(int itemIdx, ArrayList<String> origTypes) {
        JsonObject result = new JsonObject();

        ItemsTags itReq = new ItemsTags();
        itReq.setIdx(itemIdx);
        itReq.setStat("Y");

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
    public JsonObject getItemsMetasByItemIdx(int itemIdx) {
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

        JsonObject resultObj = getItemsMetasByIdx(itemIdx, origTypes);

        JsonObject resultObj2 = getItemsMetasDupByItemIdx(resultObj, itemIdx);
        return resultObj2;
    }

    private JsonObject setEmptyMetas(JsonObject reqObj, ArrayList<String> origTypes) {
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
    public JsonObject getItemsMetasDupByItemIdx(JsonObject resultObj, int itemIdx) {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");

        JsonObject oldResultObj = getOldItemsMetasByIdx(itemIdx, origTypes);

        System.out.println("#oldItemsMetas:" + oldResultObj.toString());

        for (String type : origTypes) {
            JsonArray oldTypesArr = (JsonArray) oldResultObj.get(type);
            JsonArray newTypesArr = (JsonArray) resultObj.get(type);
            JsonArray typeResultArr = getCombinedJsonArray(oldTypesArr, newTypesArr);

            System.out.println("#typeResultArr:"+typeResultArr.toString());

            resultObj.remove(type);
            resultObj.add(type, typeResultArr);
        }

        return resultObj;
    }

    private JsonArray getCombinedJsonArray(JsonArray oldArr, JsonArray newArr) {
        JsonArray resultArr = new JsonArray();

        for (JsonElement oje : oldArr) {
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
                    newItem.addProperty("type", "dup");
                    newItem.addProperty("ratio", njo.get("ratio").getAsDouble());
                    resultArr.add(newItem);
                    isMatch = true;
                }
            }
            if(!isMatch) {
                // OLD:NEW 같은 것이 없으면 type=est 로 저장
                JsonObject newItem = new JsonObject();
                newItem.addProperty("word", oldWord);
                newItem.addProperty("type", "ext");
                newItem.addProperty("ratio", ojo.get("ratio").getAsDouble());
                resultArr.add(newItem);
            }
        }

        for(JsonElement njje : newArr) {
            JsonObject njjo = (JsonObject) njje;
            String nj_word = njjo.get("word").getAsString();

            boolean isExist = false;
            for(JsonElement re : resultArr) {
                JsonObject ro = (JsonObject) re;
                String re_word = ro.get("word").getAsString();
                if (nj_word.equals(re_word)) {
                    isExist = true;
                }
            }
            if(!isExist) {
                // NEW:RESLT 같은 것이 없으면 type=new 로 저장
                //System.out.println("#njjo:"+njjo.toString());

                JsonObject newItem = new JsonObject();
                newItem.addProperty("word", nj_word);
                newItem.addProperty("type", "new");
                newItem.addProperty("ratio", njjo.get("ratio").getAsDouble());
                resultArr.add(newItem);
            }
        }

        return resultArr;
    }

    @Override
    public int restorePrevTag(int itemIdx) {
        int rt = 0;
        if (itemIdx > 0) {
            // S -> Y
            ItemsTags reqO = new ItemsTags();
            reqO.setIdx(itemIdx);
            reqO.setStat("S");
            int oldTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqO);

            // Y -> C
            ItemsTags reqC = new ItemsTags();
            reqC.setIdx(itemIdx);
            reqC.setStat("Y");
            int curTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqC);


            // S -> Y
            if (oldTagIdx > 0) {
                ItemsTags reqOO = new ItemsTags();
                reqOO.setIdx(itemIdx);
                reqOO.setStat("Y");
                reqOO.setTagidx(oldTagIdx);
                rt = itemsTagsMapper.uptItemsTagsKeysStat(reqOO);
            }

            // Y -> C
            // curTagIdx는 C로 승인취소 처리
            if (curTagIdx > 0) {
                ItemsTags reqCC = new ItemsTags();
                reqCC.setIdx(itemIdx);
                reqCC.setStat("C");
                reqCC.setTagidx(curTagIdx);
                rt = itemsTagsMapper.uptItemsTagsKeysStat(reqCC);
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
            JsonElement tradeElement = parser.parse(items);
            JsonArray reqArr = tradeElement.getAsJsonArray();

            Set<String> typesArr = new HashSet<String>();

            if (reqArr != null) {
                //System.out.println("#reqArr:"+reqArr.toString());

                for (JsonElement je : reqArr) {
                    JsonObject nitem = (JsonObject) je;

                    if (nitem.get("type") != null) {
                        String tmpMapArrayName = String.valueOf(nitem.get("type").getAsString());
                        typesArr.add(String.valueOf(nitem.get("type").getAsString()));

                        List<Map<String, Object>> tmpArr = (List<Map<String, Object>>) tmpMapArrays.get(tmpMapArrayName);
                        if (tmpArr == null) tmpArr = new ArrayList();

                        Map<String, Object> newItem = new HashMap();
                        newItem.put("type", nitem.get("type").getAsString().toUpperCase());
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


    @Override
    public int changeMetasArraysByTypeFromInputItems(int itemid, String items, String duration) {
        int rt = 0;
        int curTagIdx = this.getCurrTagsIdxForInsert(itemid);

        try {
            JsonObject origMetasArraysByType = this.getItemsMetasByItemIdx(itemid);
            JsonObject actionItemsArraysByType = this.getArraysByTypeFromInputItems(items);
            //System.out.println("#actionItemsArraysByType:"+actionItemsArraysByType.toString());

            /* get action TYPE to Arrays */
            JsonArray typesArr = null;
            if (actionItemsArraysByType.get("typesArr") != null) typesArr = (JsonArray) actionItemsArraysByType.get("typesArr");
            //System.out.println("#typesArr:"+typesArr.toString());

            for (JsonElement atype1 : typesArr) {
                String atype = atype1.getAsString();
                atype = atype.replace("\"","");

                JsonArray origMetaArr = null;
                JsonArray changeMetaArr = null;
                if (origMetasArraysByType.get(atype) != null) {
                    origMetaArr = (JsonArray) origMetasArraysByType.get(atype);
                    System.out.println("#Change type("+atype+") orig meta datas::"+origMetaArr);
                }

                if (actionItemsArraysByType.get(atype) != null) {
                    changeMetaArr = (JsonArray) actionItemsArraysByType.get(atype);
                    System.out.println("#Change type("+atype+") changing meta datas to::"+changeMetaArr);
                }

                if(changeMetaArr != null) {
                    /* get meta data for saving */
                    JsonArray destArr = this.getTargetMetasArray(atype, origMetaArr, changeMetaArr);
                    String destMeta = destArr.toString();

                    System.out.println("##DestArr cause changed for type:"+atype+" :: "+destArr.toString());

                    /* 기존 메타와 추가 액션아이템들이 반영된 TYPE(ex> METASWHEN) 별 메타JsonArray가 준비되면 현재 tagIdx를 기준으로 업데이트 */
                    ItemsTags reqMeta = new ItemsTags();
                    reqMeta.setIdx(itemid);
                    reqMeta.setTagidx(curTagIdx);
                    reqMeta.setMtype(atype);
                    reqMeta.setMeta(destMeta);

                    rt = this.insItemsTagsMetas(reqMeta);

                }
            }

            /* 해당 items_tags_keys 를 승인으로 업데이트 한다 */
            ItemsTags reqConfirm = new ItemsTags();
            reqConfirm.setIdx(itemid);
            reqConfirm.setTagidx(curTagIdx);
            reqConfirm.setStat("S");
            int rts = this.uptItemsTagsKeysStat(reqConfirm);

            /* 해당 items_stat 를 승인으로 업데이트 한다 */
            Items reqIt = new Items();
            reqIt.setIdx(itemid);
            reqIt.setStat("ST");
            int rti = itemsMapper.insItemsStat(reqIt);

            /* 해당 items 정보를 변경한다.  tagcnt++,  duration */
            if(!"".equals(duration)) reqIt.setDuration(duration);
            int rtu = itemsMapper.uptItemsTagcnt(reqIt);
            rt = 1;

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

    public JsonArray changeTargetMetasArray(String toAction, JsonObject jObj, JsonArray origArray) {
        JsonArray resultArr = null;

        try {
            if (jObj != null && origArray != null) {
                String asWord = "";
                String toWord = "";

                resultArr = new JsonArray();
                switch (toAction) {
                    case "add" :
                        toWord = jObj.get("target_meta").getAsString().trim();

                        for (JsonElement je : origArray) {
                            JsonObject jo = (JsonObject) je;
                            JsonObject newobj = new JsonObject();
                            newobj.addProperty("word", jo.get("word").getAsString());
                            newobj.addProperty("type", jo.get("type").getAsString());
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
                                newobj2.addProperty("type", jo.get("type").getAsString());
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
                                newobj3.addProperty("type", jo.get("type").getAsString());
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

    @Override
    public List<ItemsTags> getYjTagsMetasByItemidx(ItemsTags req) {
        return itemsTagsMapper.getYjTagsMetasByItemidx(req);
    }
}
