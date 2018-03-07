package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.AuthUserMapper;
import com.kthcorp.cmts.mapper.CcubeMapper;
import com.kthcorp.cmts.mapper.ItemsMapper;
import com.kthcorp.cmts.mapper.ItemsMetasMapper;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class ApiService implements ApiServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    @Value("${cmts.property.serverid}")
    private String serverid;
    @Value("${cmts.property.sns_api_url}")
    private String sns_api_url;

    @Autowired
    private AuthUserMapper authUserMapper;
    @Autowired
    private ItemsMetasMapper itemsMetasMapper;
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private DicService dicService;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private CcubeMapper ccubeMapper;

    @Override
    public String getHashCode(String custid, String authkey) throws Exception {
        //String key = "sdjnfio2390dsvjklwwe90jf2";

        AES256Util aes256 = null;
        String encText = "";

        try {
            aes256 = new AES256Util(authkey);

            //String orgText = "OLLEH_META_";
            String orgText = custid + "_";
            String orgDate = DateUtils.getLocalDate2();
            orgText += orgDate;

            encText = aes256.aesEncode(orgText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encText;
    }

    @Override
    public int checkAuthByHashCode(String custid, String hash) throws Exception {
        return 1;
    }

    public int checkAuthByHashCode_bak(String custid, String hash) throws Exception {
        String authkey = "sdjnfio2390dsvjklwwe90jf2";
        if (hash.contains("%")) hash = URLDecoder.decode(hash, "UTF-8");

        int rtcode = -1;
        AES256Util aes256 = null;

        String decText = "";

        try {
            aes256 = new AES256Util(authkey);

            //String orgText = "OLLEH_META_";
            String orgText = custid + "_";
            String orgDate = DateUtils.getLocalDate2();
            orgText += orgDate;

            decText = aes256.aesDecode(hash);
            if (!"".equals(decText)) {
                String decTexts[] = decText.split("_");
                String decDate = decTexts[1];

                String origDate = DateUtils.getLocalDate2();

                logger.debug("#MLOG checkAuthByHashCode:: decHash:"+decText+" // decDate:"+decDate+" vs origDate:"+origDate);

                if(decDate.equals(origDate)) {
                    rtcode = 1;
                } else {
                    /* expired */
                    rtcode = -4;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            rtcode = -3;
        }

        return rtcode;
    }

    @Override
    public String getRtmsg(int rtcode) {
        String rtmsg = "";

        switch(rtcode) {
            case 1 :
                rtmsg = "SUCCESS";
                break;
            case  -1 :
                rtmsg = "Failuse!";
                break;
            case -2 :
                rtmsg = "Transaction error!";
                break;
            case -3 :
                rtmsg = "Permission not granted!";
                break;
            case -4 :
                rtmsg = "Token expired!";
                break;
            case -999 :
                rtmsg = "System error!";
                break;
        }
        return rtmsg;
    }

    @Override
    public List<AuthUser> getAuthUsers() {
        return authUserMapper.getAuthUsers();
    }
    @Override
    public AuthUser getAuthUserById(AuthUser req) {
        return authUserMapper.getAuthUserById(req);
    }
    @Override
    public int insAuthUser(AuthUser req) {
        if (req != null) req.setRegid(serverid);
        return authUserMapper.insAuthUser(req);
    }
    @Override
    public int uptAuthUser(AuthUser req) {
        if (req != null) req.setRegid(serverid);
        return authUserMapper.uptAuthUser(req);
    }
    @Override
    public int delAuthUser(AuthUser req) {
        if (req != null) req.setRegid(serverid);
        return authUserMapper.delAuthUser(req);
    }

    @Override
    public JsonObject getMovieInfoByIdx(int itemIdx) {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("TITLE");
        origTypes.add("OTITLE");
        origTypes.add("SERIESYN");
        origTypes.add("YEAR");
        origTypes.add("DIRECTOR");
        origTypes.add("ACTOR");
        origTypes.add("GENRE");
        origTypes.add("PLOT");

        //JsonObject resultObj = getItemsMetasByIdx(itemIdx, origTypes);
        JsonObject resultObj = getCcubeMetasByIdx(itemIdx, origTypes);
        return resultObj;
    }

    @Override
    public JsonObject getAwardInfoByIdx(int itemIdx) {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("AWARD");

        JsonObject resultObj = getItemsMetasByIdx(itemIdx, origTypes);
        return resultObj;
    }

    private JsonObject getItemsMetasByIdx(int itemIdx, ArrayList<String> origTypes) {
        JsonObject resultObj = new JsonObject();

        ItemsMetas req = new ItemsMetas();
        req.setIdx(itemIdx);
        List<ItemsMetas> result = itemsMetasMapper.getItemsMetasByIdx(req);

        if (result != null && result.size() > 0) {
            for (ItemsMetas im : result) {
                if(im != null && im.getMtype() != null && im.getMeta() != null) {
                    for(String ot : origTypes) {
                        if(ot.equals(im.getMtype().toUpperCase())) {
                            resultObj.addProperty(im.getMtype().toUpperCase(), im.getMeta());
                        }
                    }
                }
            }
        }

        resultObj = setEmptyMovieInfo(resultObj, origTypes);

        return resultObj;
    }

    private JsonObject convertCcubeSeriesToJsonObject(CcubeSeries cser) {
        JsonObject resultObj = null;

        if (cser != null) {
            resultObj = new JsonObject();
            if (cser.getPurity_title() != null) resultObj.addProperty("TITLE", cser.getPurity_title());
            if (cser.getEng_title() != null) resultObj.addProperty("OTITLE", cser.getEng_title());
            resultObj.addProperty("SERIESYN", "Y");
            if (cser.getYear() != null) resultObj.addProperty("YEAR", cser.getYear());
            if (cser.getDirector() != null) resultObj.addProperty("DIRECTOR", cser.getDirector());
            if (cser.getActors_display() != null) resultObj.addProperty("ACTOR", cser.getActors_display());
        }

        return resultObj;
    }

    private JsonObject convertCcubeContentToJsonObject(CcubeContent cser) {
        JsonObject resultObj = null;

        if (cser != null) {
            resultObj = new JsonObject();
            if (cser.getPurity_title() != null) resultObj.addProperty("TITLE", cser.getPurity_title());
            if (cser.getEng_title() != null) resultObj.addProperty("OTITLE", cser.getEng_title());
            resultObj.addProperty("SERIESYN", "N");
            if (cser.getYear() != null) resultObj.addProperty("YEAR", cser.getYear());
            if (cser.getDirector() != null) resultObj.addProperty("DIRECTOR", cser.getDirector());
            if (cser.getActors_display() != null) resultObj.addProperty("ACTOR", cser.getActors_display());
        }

        return resultObj;
    }

    private JsonObject getCcubeMetasByIdx(int itemIdx, ArrayList<String> origTypes) {
        JsonObject resultObj = null;

        CcubeKeys ckeys = ccubeMapper.getCcubeKeys(itemIdx);
        if (ckeys != null ) {
            if (ckeys.getSeries_id() != null && !"0".equals(ckeys.getSeries_id())) {
                CcubeSeries reqser = new CcubeSeries();
                reqser.setSeries_id(ckeys.getSeries_id());
                CcubeSeries cser = ccubeMapper.getCcubeSeriesById(reqser);
                if (cser != null) {
                    resultObj = convertCcubeSeriesToJsonObject(cser);
                }
            } else if (ckeys.getContent_id() != null && !"0".equals(ckeys.getContent_id())) {
                CcubeContent reqcon = new CcubeContent();
                reqcon.setContent_id(ckeys.getContent_id());
                CcubeContent ccon = ccubeMapper.getCcubeContentByCid(reqcon);
                if (ccon != null) {
                    resultObj = convertCcubeContentToJsonObject(ccon);
                }
            }
        }

        ItemsMetas req = new ItemsMetas();
        req.setIdx(itemIdx);
        List<ItemsMetas> result = itemsMetasMapper.getItemsMetasByIdx(req);
        if (result != null && result.size() > 0) {
            for (ItemsMetas im : result) {
                if (im != null && im.getMtype() != null && im.getMeta() != null) {
                    if("GENRE".equals(im.getMtype().toUpperCase()) || "PLOT".equals(im.getMtype().toUpperCase())) {
                        resultObj.addProperty(im.getMtype().toUpperCase(), im.getMeta());
                    }
                }
            }
        }

        resultObj = setEmptyMovieInfo(resultObj, origTypes);

        return resultObj;
    }

    private JsonObject setEmptyMovieInfo(JsonObject reqObj, ArrayList<String> origTypes) {
        // 빠진 type은 공백이라도 채워준다
        if(reqObj != null) {
            for(String type : origTypes) {
                if(reqObj.get(type) == null) reqObj.addProperty(type, "");
            }
        }

        return reqObj;
    }

    @Override
    public JsonObject getCine21DatasByIdx(int itemIdx) {
        JsonObject result = null;

        if (itemIdx > 0) {
            Items item = itemsMapper.getItemsInfoByIdx(itemIdx);
            if (item != null && item.getTitle() != null) {
                String title1 = item.getTitle().trim();
                title1 = title1.replace(" ", "");
                result = this.getCine21Datas(title1);
            }
        }

        return result;
    }

    @Override
    public JsonObject getCine21Datas(String title) {
        JsonObject result = null;
        if (!"".equals(title)) {
            ItemsContent res = itemsMapper.getMovieCine21ByTitle(title);
            if (res != null && res.getContent() != null) {
                result = new JsonObject();
                result.add("WORDS_CINE21", JsonUtil.convertStringToJsonArrayByComma(res.getContent()));
            }
        }

        if(result == null) {
            result = new JsonObject();
            result.add("WORDS_CINE21", new JsonArray());
        }

        return result;
    }

    @Override
    public JsonObject getDicKeywordsByType(String type, String keyword, int pageSize, int pageno) {
        if(pageSize < 1 || pageSize > 200) pageSize = 200;

        JsonObject result = new JsonObject();
        int countItems = dicService.countItems(type, keyword);
        System.out.println("#COUNT_BY_TYPE:: type:"+type+ " / count:"+countItems);

        JsonArray list_words = dicService.getDicKeywordsByType(type, keyword, pageSize, pageno);
        System.out.println("#LIST_WORDS:"+list_words.toString());

        int maxPage = countItems / pageSize + 1;

        Map<String, Object> listPaging = CommonUtil.getPagination(countItems, pageSize, pageno);
        List<String> listActive = null;
        List<Integer> listPage = null;
        if (listPaging != null) {
            listActive = (List<String>) listPaging.get("listActive");
            listPage = (List<Integer>) listPaging.get("listPage");
        }
        JsonArray listPageArr = JsonUtil.convertIntegerListToJsonArray(listPage);
        JsonArray listActiveArr = JsonUtil.convertListToJsonArray(listActive);

        result.addProperty("COUNT_ALL", countItems);
        result.addProperty("MAXPAGE", maxPage);
        result.addProperty("TYPE", type);
        result.addProperty("PAGESIZE", pageSize);
        result.addProperty("PAGENO", pageno);
        result.add("LIST_PAGING", listPageArr);
        result.add("LIST_ACTIVE", listActiveArr);
        result.add("LIST_WORDS", list_words);

        return result;
    }


    @Override
    public JsonObject getItemsSearch(
            int pageSize, int pageno
            , String searchType
            , String searchStat
            , String searchSdate
            , String searchEdate
            , String searchKeyword
            , String searchParts
    ) {
        if(pageSize < 1) pageSize = 50;

        JsonObject result = new JsonObject();

        Items reqIt = new Items();
        reqIt.setPageSize(pageSize);
        reqIt.setPageNo(pageno);

        String newType = "";
        if ("".equals(searchType)) {
            newType = "ALL";
        } else {
            newType = searchType;
        }
        reqIt.setSearchType(newType);

        String newStat = "";
        if ("".equals(searchStat)) {
            newStat = "ALL";
        } else {
            newStat = searchStat;
        }
        reqIt.setSearchStat(newStat);

        String newSdate = "";
        String newEdate = "";
        if ("".equals(searchSdate) || "".equals(searchEdate)) {
            newSdate = "2017-01-01 00:00:00"; newEdate = "2025-12-31 23:59:59";
        } else {
            newSdate = searchSdate + " 00:00:00";
            newEdate = searchEdate + " 23:59:59";
        }
        reqIt.setSearchSdate(newSdate);
        reqIt.setSearchEdate(newEdate);

        if (!"".equals(searchKeyword)) reqIt.setSearchKeyword(searchKeyword);

        if (!"".equals(searchParts)) {
            List<String> searchPartsArr = null;
            String sp[] = searchParts.trim().split(",");
            if (searchParts.contains(",")) {
                for (String ps : sp) {
                    if(ps.trim().equals("title")) {
                        reqIt.setSearchTitleYn("Y");
                    } else if (ps.trim().equals("genre")) {
                        reqIt.setSearchGenreYn("Y");
                    } else {
                        if (searchPartsArr == null) searchPartsArr = new ArrayList();
                        searchPartsArr.add(ps.trim());
                    }
                }
            } else {
                if(searchParts.trim().equals("title")) {
                    reqIt.setSearchTitleYn("Y");
                } else if (searchParts.trim().equals("genre")) {
                    reqIt.setSearchGenreYn("Y");
                } else {
                    if (searchPartsArr == null) searchPartsArr = new ArrayList();
                    searchPartsArr.add(searchParts.trim());
                }
            }
            reqIt.setSearchParts(searchParts);
            reqIt.setSearchPartsArr(searchPartsArr);
        }


        //int countItems = itemsMapper.countItems(reqIt);
        int countItems = itemsMapper.countItemsPaging(reqIt);

        System.out.println("#COUNT_SEARCH_ITEMS:: / count:"+countItems);

        List<Items> list_items = itemsMapper.searchItemsPaging(reqIt);
        JsonArray listItems = getListItemsFromArray(list_items);

        //System.out.println("#LIST_ITEMS:"+list_items.toString());

        int maxPage = countItems / pageSize + 1;

        Map<String, Object> listPaging = CommonUtil.getPagination(countItems, pageSize, pageno);
        List<String> listActive = null;
        List<Integer> listPage = null;
        if (listPaging != null) {
            listActive = (List<String>) listPaging.get("listActive");
            listPage = (List<Integer>) listPaging.get("listPage");
        }
        JsonArray listPageArr = JsonUtil.convertIntegerListToJsonArray(listPage);
        JsonArray listActiveArr = JsonUtil.convertListToJsonArray(listActive);

        result.addProperty("MAXPAGE", maxPage);
        result.addProperty("SEARCHTYPE", searchType);
        result.addProperty("SEARCHSTAT", searchStat);
        result.addProperty("SEARCHSDATE", searchSdate);
        result.addProperty("SEARCHEDATE", searchEdate);
        result.addProperty("SEARCHKEYWORD", searchKeyword);
        result.addProperty("SEARCHPARTS", searchParts);

        JsonObject countsSearch = getCountSearch(countItems, reqIt);
        countsSearch.addProperty("COUNT_ALL", countItems);


        result.add("COUNTS_SEARCH", countsSearch);

        result.addProperty("PAGESIZE", pageSize);
        result.addProperty("PAGENO", pageno);
        result.add("LIST_PAGING", listPageArr);
        result.add("LIST_PAGING_ACTIVE", listActiveArr);
        result.add("LIST_ITEMS", listItems);

        return result;
    }

    private JsonArray getListItemsFromArray(List<Items> itemsList) {
        JsonArray result = new JsonArray();
        if (itemsList != null && itemsList.size() > 0) {
            for (Items tm : itemsList) {
                JsonObject newItem = new JsonObject();
                newItem.addProperty("TITLE", tm.getTitle());
                newItem.addProperty("CID", (tm.getContent_id() != null ? tm.getContent_id() : ""));
                newItem.addProperty("TYPE", (tm.getType() != null ? tm.getType() : ""));
                newItem.addProperty("CNT_TAG", tm.getTagcnt());
                newItem.addProperty("REGDATE", (tm.getRegdate() != null ? tm.getRegdate().toString() : ""));
                newItem.addProperty("PROCDATE", (tm.getProcdate() != null ? tm.getProcdate().toString() : "" ));
                newItem.addProperty("STAT", (tm.getStat() != null ? tm.getStat() : ""));

                newItem.addProperty("ITEMID", tm.getIdx());

                result.add(newItem);
            }
        }
        return result;
    }

    private JsonObject getCountSearch(int countItems, Items reqIt) {
        ArrayList<String> origStats = new ArrayList();
        origStats.add("COUNT_FAIL_COLLECT");
        origStats.add("COUNT_FAIL_ANALYZE");
        origStats.add("COUNT_READY_TAG");
        origStats.add("COUNT_TAGGED");

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("COUNT_ALL", countItems);
        List<Items> listStatCnts = itemsMapper.countItemsPagingByStat(reqIt);
        if (listStatCnts != null) {
            for(Items lt : listStatCnts) {
                if(lt.getStat() != null) {
                    switch(lt.getStat()) {
                        case "FC":
                            resultObj.addProperty("COUNT_FAIL_COLLECT", lt.getCnt());
                            break;
                        case "FA":
                            resultObj.addProperty("COUNT_FAIL_ANALYZE", lt.getCnt());
                            break;
                        case "RT":
                            resultObj.addProperty("COUNT_READY_TAG", lt.getCnt());
                            break;
                        case "ST":
                            resultObj.addProperty("COUNT_TAGGED", lt.getCnt());
                            break;
                        case "NONE":
                            resultObj.addProperty("COUNT_NONE", lt.getCnt());
                            break;
                    }
                }
            }
        }

        JsonObject resultObj2 = setEmptyCountInfo(resultObj, origStats);
        return resultObj2;
    }

    private JsonObject setEmptyCountInfo(JsonObject reqObj, ArrayList<String> origTypes) {
        // 빠진 type은 0이라도 채워준다
        if(reqObj != null) {
            for(String type : origTypes) {
                if(reqObj.get(type) == null) reqObj.addProperty(type, 0);
            }
        }

        return reqObj;
    }

    @Override
    public int uptSchedTriggerStatByItemIdxAndType(int itemIdx, String type, String stat) {
        int rt = 0;
        Items itm = new Items();
        itm.setIdx(itemIdx);

        String toStat1 = type;
        if(!"A".equals(type)) toStat1 = "R";
        itm.setType(toStat1);
        itm.setStat(stat);

        rt = itemsService.uptSchedTriggerStatByItemIdx(itm);

        return rt;
    }

    @Override
    public JsonArray getSnsKeywords(String title) throws Exception {
        JsonArray result = null;
        if (!"".equals(title)) {
            result = new JsonArray();

            String target1 = "twitter";
            String edate = DateUtils.getLocalDate();
            String sdate = DateUtils.calculateDate(Calendar.MONTH, -1, edate);

            String sns_api_url_dest1 = sns_api_url.replace("#SDATE", sdate);
            sns_api_url_dest1 = sns_api_url_dest1.replace("#EDATE", edate);
            sns_api_url_dest1 = sns_api_url_dest1.replace("#TARGET", target1);
            sns_api_url_dest1 = sns_api_url_dest1.replace("#TITLE", title);
            Map<String, Object> resultMap1 = HttpClientUtil.reqGetHtml(sns_api_url_dest1, null, null,null, "bypass");
            JsonArray childList1 = null;
            if (resultMap1 != null && resultMap1.get("resultStr") != null) {
                String result1 = resultMap1.get("resultStr").toString();
                //System.out.println("#ELOG:sns_api:: result1:"+result1);
                JsonObject resultObj1 = JsonUtil.convertStringToJsonObject(result1);
                //System.out.println("#ELOG:sns_api:: result_jsonObj1:"+resultObj1.toString());
                if (resultObj1 != null && resultObj1.get("childList") != null) {
                    childList1 = (JsonArray) resultObj1.get("childList");
                    System.out.println("#ELOG:sns_api:: childList1:"+childList1.toString());
                }
            }

            String target2 = "insta";
            String sns_api_url_dest2 = sns_api_url.replace("#SDATE", sdate);
            sns_api_url_dest2 = sns_api_url_dest2.replace("#EDATE", edate);
            sns_api_url_dest2 = sns_api_url_dest2.replace("#TARGET", target2);
            sns_api_url_dest2 = sns_api_url_dest2.replace("#TITLE", title);
            JsonArray childList2 = null;
            Map<String, Object> resultMap2 = HttpClientUtil.reqGetHtml(sns_api_url_dest2, null, null,null, "bypass");
            if (resultMap2 != null && resultMap2.get("resultStr") != null) {
                String result2 = resultMap2.get("resultStr").toString();
                //System.out.println("#ELOG:sns_api:: result2:"+result2);
                JsonObject resultObj2= JsonUtil.convertStringToJsonObject(result2);
                //System.out.println("#ELOG:sns_api:: result_jsonObj2:"+resultObj2.toString());
                if (resultObj2 != null && resultObj2.get("childList") != null) {
                    childList2 = (JsonArray) resultObj2.get("childList");
                    System.out.println("#ELOG:sns_api:: childList2:"+childList2.toString());
                }
            }
        }


        //result = getDuppedKeywordsArrayCutted(childList1, childList2, 10);


        return result;
    }

    private JsonArray getDuppedKeywordsArrayCutted(JsonArray childList1, JsonArray childList2, int limit) {
        JsonArray result = new JsonArray();
        if (childList1 != null) {
            for (JsonElement je1 : childList1) {
                result.add(je1);
            }
        }
        if (childList2 != null) {

        }
        return result;
    }

}
