package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.*;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class ApiService implements ApiServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    @Value("${property.serverid}")
    private String serverid;
    @Value("${cmts.property.sns_api_url}")
    private String sns_api_url;
    @Value("${cmts.property.sns_stat_url}")
    private String sns_stat_url;
    @Value("${property.crawl_sns_topwords_url}")
    private String crawl_sns_topwords_url;
    @Value("${cmts.property.naver_kordic_url}")
    private String naver_kordic_url;
    @Value("${cmts.property.coll_naver_kordic_url}")
    private String coll_naver_kordic_url;

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
    @Autowired
    private SnsMapper snsMapper;

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
            case -88 :
                rtmsg = "Job is running!";
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

    @Override
    public JsonObject getAwardArrInfoByIdx(int itemIdx) throws Exception {
        ArrayList<String> origTypes = new ArrayList<String>();
        origTypes.add("AWARD");

        JsonObject resultObj = getItemsMetasArrByIdx(itemIdx, origTypes);
        return resultObj;
    }

    private JsonObject getItemsMetasArrByIdx(int itemIdx, ArrayList<String> origTypes) throws Exception {
        JsonObject resultObj = new JsonObject();

        ItemsMetas req = new ItemsMetas();
        req.setIdx(itemIdx);
        List<ItemsMetas> result = itemsMetasMapper.getItemsMetasByIdx(req);

        if (result != null && result.size() > 0) {
            for (ItemsMetas im : result) {
                if(im != null && im.getMtype() != null && im.getMeta() != null) {
                    for(String ot : origTypes) {
                        if(ot.equals(im.getMtype().toUpperCase())) {
                            String thisMeta = im.getMeta();
                            JsonArray thisArr = null;
                            if (!"".equals(thisMeta)) {
                                thisArr = JsonUtil.getJsonArray(thisMeta);
                            } else {
                                thisArr = new JsonArray();
                            }
                            resultObj.add(im.getMtype().toUpperCase(), thisArr);
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

                String master_cid = "";
                List<Map<String, Object>> mcidObj = ccubeMapper.getSortedItemsByMCID(ckeys.getMaster_content_id());
                if (mcidObj != null && mcidObj.size() > 0) {
                    master_cid = (String) mcidObj.get(0).get("content_id");
                    reqcon.setContent_id(master_cid);
                    CcubeContent ccon = ccubeMapper.getCcubeContentByCid(reqcon);
                    resultObj = convertCcubeContentToJsonObject(ccon);
                }

                if (resultObj == null) {
                    master_cid = ckeys.getContent_id();
                    reqcon.setContent_id(master_cid);

                    CcubeContent ccon = ccubeMapper.getCcubeContentByCid(reqcon);
                    resultObj = convertCcubeContentToJsonObject(ccon);
                }

            }
        }

        ItemsMetas req = new ItemsMetas();
        req.setIdx(itemIdx);
        List<ItemsMetas> result = itemsMetasMapper.getItemsMetasByIdx(req);
        //System.out.println("#ELOG.getItemsMetasByIdx by idx:"+itemIdx+"/result:"+result.toString());

        if (result != null && result.size() > 0) {
            for (ItemsMetas im : result) {
                if (im != null && im.getMtype() != null && im.getMeta() != null) {
                    if("PLOT".equals(im.getMtype().toUpperCase())) {
                        resultObj.addProperty(im.getMtype().toUpperCase(), im.getMeta());
                    } else if ("GENRE".equals(im.getMtype().toUpperCase())) {
                        String tmpMeta = im.getMeta();
                        System.out.println("#ELOG.getMetas GENRE:"+tmpMeta);
                        tmpMeta = this.getFilteredGenre(tmpMeta);
                        System.out.println("#ELOG.getMetas FILTERED-GENRE:"+tmpMeta);
                        resultObj.addProperty(im.getMtype().toUpperCase(), tmpMeta);
                    }
                }
            }
        }

        resultObj = setEmptyMovieInfo(resultObj, origTypes);

        return resultObj;
    }

    @Override
    public String getFilteredGenre(String origGenre) {
        String result = "";
        if (!"".equals(origGenre.trim())) {
            List<String> dicGenres = new ArrayList();
            dicGenres.add("드라마");
            dicGenres.add("판타지");
            dicGenres.add("서부");
            dicGenres.add("공포");
            dicGenres.add("멜로/로맨스");
            dicGenres.add("모험");
            dicGenres.add("스릴러");
            dicGenres.add("느와르");
            dicGenres.add("컬트");
            dicGenres.add("다큐멘터리");
            dicGenres.add("코미디");
            dicGenres.add("가족");
            dicGenres.add("미스터리");
            dicGenres.add("전쟁");
            dicGenres.add("애니메이션");
            dicGenres.add("범죄");
            dicGenres.add("뮤지컬");
            dicGenres.add("SF");
            dicGenres.add("액션");
            dicGenres.add("무협");
            dicGenres.add("에로");
            dicGenres.add("서스펜스");
            dicGenres.add("서사");
            dicGenres.add("블랙코미디");
            dicGenres.add("실험");
            dicGenres.add("공연실황");

            List<String> genres = new ArrayList<>();
            for (String dic : dicGenres) {
                if (origGenre.contains(dic)) {
                    genres.add(dic);
                }
            }
            if (genres.size() > 0) {
                result = genres.toString();
                result = result.replace("[","");
                result = result.replace("]","");
            }
        }
        return result;
    }

    private JsonObject setEmptyMovieInfo(JsonObject reqObj, ArrayList<String> origTypes) {
        // 빠진 type은 공백이라도 채워준다
        if(reqObj != null) {
            for(String type : origTypes) {
                if(reqObj.get(type) == null) {
                    reqObj.addProperty(type, "");
                }
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
    public JsonObject getDicKeywordsByType(String type, String keyword, String orderby, int pageSize, int pageno) {
        //if(pageSize < 1 || pageSize > 200) pageSize = 200;
    	if(pageSize < 1) pageSize = 200;	//검색어 자동완성에서도 쓰고 있어서 200 이상으로도 되어야 함

        JsonObject result = new JsonObject();
        int countItems = dicService.countItems(type, keyword);
        System.out.println("#COUNT_BY_TYPE:: type:"+type+ " / count:"+countItems);

        JsonArray list_words = dicService.getDicKeywordsByType(type, keyword, orderby, pageSize, pageno);	//권재일 추가 07.31 5-1
        System.out.println("#LIST_WORDS:"+list_words.toString());

        int maxPage = countItems / pageSize + 1;

        Map<String, Object> listPaging = CommonUtil.getPaginationJump(countItems, pageSize, pageno, 20);
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
    public String getChangedMtypes(String searchParts) {
        searchParts = searchParts.replace("when","METASWHEN");
        searchParts = searchParts.replace("where","METASWHERE");
        searchParts = searchParts.replace("who","METASWHO");
        searchParts = searchParts.replace("what","METASWHAT");
        searchParts = searchParts.replace("emotion","METASEMOTION");
        searchParts = searchParts.replace("character","METASCHARACTER");

        searchParts = searchParts.replace("keyword", "LIST_SEARCHKEYWORDS");
        searchParts = searchParts.replace("subgenre", "LIST_SUBGENRE");
        searchParts = searchParts.replace("recotarget", "LIST_RECO_TARGET");
        searchParts = searchParts.replace("recositu", "LIST_RECO_SITUATION");

        return searchParts;
    }

    private Items getSearchPartsOptions(Items reqIt, String ps) {
        List<String> searchTagsArr = reqIt.getSearchTagsArr();
        if (searchTagsArr == null) searchTagsArr = new ArrayList();

        List<String> searchMetasArr = reqIt.getSearchMetasArr();
        if (searchMetasArr == null) searchMetasArr = new ArrayList();

        if(ps.trim().equals("title")) {
            reqIt.setSearchTitleYn("Y");
        } else if (ps.trim().equals("director")) {
            reqIt.setSearchDirectorYn("Y");
        } else if (ps.trim().equals("actor")) {
            reqIt.setSearchActorsYn("Y");
        } else if (ps.trim().contains("METAS")
                || ps.trim().contains("LIST")
                ) {
            searchTagsArr.add(ps.trim());

            reqIt.setSearchTagsArr(searchTagsArr);
            reqIt.setSearchTagsYn("Y");
        } else if (ps.trim().equals("plot")
                || ps.trim().equals("genre")
                || ps.trim().equals("award")
                ) {
            searchMetasArr.add(ps.trim());

            reqIt.setSearchMetasArr(searchMetasArr);
            reqIt.setSearchMetasYn("Y");
        }
        return reqIt;
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
        if ("".equals(searchKeyword.trim())) searchParts = "";

        searchParts = getChangedMtypes(searchParts);

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
        //시간은 쿼리식에서 붙어서 여기서는 빼버림
        if ("".equals(searchSdate) || "".equals(searchEdate)) {
        	newSdate = "2017-01-01"; newEdate = "2025-12-31";
        } else {
            newSdate = searchSdate;
            newEdate = searchEdate;
        }
        reqIt.setSearchSdate(newSdate);
        reqIt.setSearchEdate(newEdate);

        if (!"".equals(searchKeyword)) reqIt.setSearchKeyword(searchKeyword);

        if (!"".equals(searchParts)) {
            List<String> searchPartsArr = null;
            String sp[] = searchParts.trim().split(",");
            if (searchParts.contains(",")) {
                for (String ps : sp) {
                    // searchParts중 1개에 대해 검색옵션 설정
                    reqIt = getSearchPartsOptions(reqIt, ps);
                }
            } else {
                // searchParts중 1개에 대해 검색옵션 설정
                reqIt = getSearchPartsOptions(reqIt, searchParts);
            }
            reqIt.setSearchParts(searchParts);
        }

        //int countItems = itemsMapper.countItems(reqIt);
        //System.out.println("#ELOG.searchItems:: req:"+reqIt.toString());
        int countItems = itemsMapper.countItemsPaging(reqIt);
        int countAll = itemsMapper.countItemsAll();

        System.out.println("#COUNT_SEARCH_ITEMS:: / count:"+countItems);

        List<Items> list_items = itemsMapper.searchItemsPaging(reqIt);
        List<Items> list_items2 = new ArrayList();
        for(Items one : list_items) {
            if (one != null && one.getSeries_id() != null && !"0".equals(one.getSeries_id())) {
                one.setContent_id(one.getSeries_id());
                one.setCid(one.getSeries_id());
            }
            list_items2.add(one);
        }
        JsonArray listItems = getListItemsFromArray(list_items2);

        //System.out.println("#LIST_ITEMS:"+list_items.toString());

        int maxPage = countItems / pageSize + 1;

        Map<String, Object> listPaging = CommonUtil.getPaginationJump(countItems, pageSize, pageno, 10);
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

        JsonObject countsSearch = getCountSearch(countAll, reqIt);
//        countsSearch.addProperty("COUNT_ALL", countAll);
        countsSearch.addProperty("COUNT_ALL", countItems);

        result.add("COUNTS_SEARCH", countsSearch);

        result.addProperty("PAGESIZE", pageSize);
        result.addProperty("PAGENO", pageno);
        result.add("LIST_PAGING", listPageArr);
        result.add("LIST_PAGING_ACTIVE", listActiveArr);
        result.add("LIST_ITEMS", listItems);

        return result;
    }

    @Override
    public JsonArray getListItemsFromArray(List<Items> itemsList) {
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
                newItem.addProperty("REGID", tm.getRegid());

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
        int fail_analyze = 0;

        if (listStatCnts != null) {
            for(Items lt : listStatCnts) {
                if(lt.getStat() != null) {
                    switch(lt.getStat()) {
                        case "FC":
                            resultObj.addProperty("COUNT_FAIL_COLLECT", lt.getCnt());
                            break;
                        case "FA":
                            fail_analyze += lt.getCnt();
                            break;
                        case "FR":
                            fail_analyze += lt.getCnt();
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

        resultObj.addProperty("COUNT_FAIL_ANALYZE", fail_analyze);

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

        String toType = type;
        // 분석 재처리의 경우 추출 재처리로 변경 처리
        if("A".equals(type)) toType = "R";
        itm.setType(toType);
        itm.setStat(stat);

        rt = itemsService.uptSchedTriggerStatByItemIdx(itm);

        return rt;
    }

    @Override
    public JsonArray getSnsKeywords(String title) throws Exception {
        JsonArray result = null;

        JsonArray childList1 = null;
        JsonArray childList2 = null;

        title = title.trim().replace(" ","");
        title = CommonUtil.removeAllSpec(title);
        title = CommonUtil.removeTex(title);
        title =  URLEncoder.encode(title, "utf-8");

        if (!"".equals(title)) {
            result = new JsonArray();

            String target1 = "twitter";
            String edate = DateUtils.getLocalDate();
            String sdate = DateUtils.calculateDate(Calendar.YEAR, -1, edate);

            String sns_api_url_dest1 = sns_api_url.replace("#SDATE", sdate);
            sns_api_url_dest1 = sns_api_url_dest1.replace("#EDATE", edate);
            sns_api_url_dest1 = sns_api_url_dest1.replace("#TARGET", target1);
            sns_api_url_dest1 = sns_api_url_dest1.replace("#TITLE", title);
            Map<String, Object> resultMap1 = HttpClientUtil.reqGetHtml(sns_api_url_dest1, null, null,null, "bypass");

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

        result = getDuppedKeywordsArrayCutted(childList1, childList2, 10);

        return result;
    }

    private JsonArray getDuppedKeywordsArrayCutted(JsonArray childList1, JsonArray childList2, int limit) {
        JsonArray result = new JsonArray();
        Map<String, Double> addedMap = new HashMap();

        if (childList1 != null) {
            Map<String, Double> map1 = MapUtil.getParamDoubleMapFromJsonArrayByTag(childList1, "label", "score");
            addedMap = MapUtil.getAppendedMapAndParamDouble(addedMap, map1);
        }

        if (childList2 != null) {
            Map<String, Double> map2 = MapUtil.getParamDoubleMapFromJsonArrayByTag(childList2, "label", "score");
            addedMap = MapUtil.getAppendedMapAndParamDouble(addedMap, map2);
        }
        //System.out.println("#TMP.addedMap:"+addedMap.toString());
        Map<String, Double> sortedMap = MapUtil.getSortedDescMapForDouble(addedMap);
        //System.out.println("#TMP.sortedMap:"+sortedMap.toString());

        Map<String, Double> cuttedSortedMap = MapUtil.getCuttedMapFromMapByLimit(sortedMap, 10);
        Map<String, Double> cuttedSortedMap2 = MapUtil.getSortedDescMapForDouble(cuttedSortedMap);
        //System.out.println("#TMP.cuttedSortedMap:"+cuttedSortedMap2.toString());

        result = MapUtil.getListNotMapKeywords(cuttedSortedMap2);

        return result;
    }

    @Override
    public JsonObject getSnsTopKeywords() throws Exception {
        JsonObject resultObj = null;

        String target1 = "twitter";
        String ndate = DateUtils.getLocalDate();
        resultObj = this.getSnsTopKeywords(resultObj, target1, ndate);

        ndate = DateUtils.calculateDate(Calendar.DATE, -1, ndate);
        resultObj = this.getSnsTopKeywords(resultObj, target1, ndate);

        target1 = "insta";
        ndate = DateUtils.getLocalDate();
        resultObj = this.getSnsTopKeywords(resultObj, target1, ndate);

        ndate = DateUtils.calculateDate(Calendar.DATE, -1, ndate);
        resultObj = this.getSnsTopKeywords(resultObj, target1, ndate);

        return resultObj;
    }

    private JsonObject getSnsTopKeywords(JsonObject resultObj, String target1, String ndate) throws Exception {
        if (resultObj == null) resultObj = new JsonObject();

        JsonArray childList1 = new JsonArray();
        // -1 : 하루전, 이틀전 수집
        String date1 = DateUtils.calculateDate(Calendar.DATE, -1, ndate);
        String fieldStr = target1 + "_" + date1;

        String sns_stat_url_dest1 = sns_stat_url.replace("#SDATE", date1);
        sns_stat_url_dest1 = sns_stat_url_dest1.replace("#EDATE", date1);
        sns_stat_url_dest1 = sns_stat_url_dest1.replace("#TARGET", target1);

        Map<String, Object> resultMap1 = HttpClientUtil.reqGetHtml(sns_stat_url_dest1, null, null,null, "bypass");

        if (resultMap1 != null && resultMap1.get("resultStr") != null) {
            //System.out.println("#ELOG.resultMap:"+resultMap1.toString());

            String result1 = resultMap1.get("resultStr").toString();
            childList1 = (JsonArray) JsonUtil.getJsonArray(result1);
            //System.out.println("#ELOG:sns_stat:"+target1+":: childList1:"+childList1.toString());
        }

        resultObj.add(fieldStr, childList1);
        return resultObj;
    }

    @Override
    public int processSnsTopKeywordsByDateSched() {
        int rt = 0;
        try {
            Map<String, Object> resultMap1 = HttpClientUtil.reqGetHtml(crawl_sns_topwords_url, null, null,null, "bypass");
            if (resultMap1 != null && resultMap1.get("resultStr") != null) {
                //System.out.println("#ELOG.getAPI_result::"+resultMap1.toString());
                String result1 = resultMap1.get("resultStr").toString();
                JsonObject resultStr  = (JsonObject) JsonUtil.getJsonObject(result1);
                //System.out.println("#ELOG.resultStrObj::"+resultStr.toString());

                JsonObject resultObj = (JsonObject) resultStr.get("RESULT");
                System.out.println("#ELOG.resultObj::"+resultObj.toString());


                Iterator<String> keysItr = resultObj.keySet().iterator();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    JsonArray value = (JsonArray) resultObj.get(key);
                    System.out.println("#ELOG.loop::key:" + key + "/datas::" + value.toString());

                    if (!"".equals(key)) {
                        String[] keys = key.split("_");
                        if (keys.length > 1 && value != null) {
                            Map<String, Object> reqMap = new HashMap();
                            reqMap.put("target", keys[0]);
                            reqMap.put("date1", keys[1]);
                            reqMap.put("meta", value.toString());
                            reqMap.put("regid", serverid);
                            int rti = snsMapper.insSnsTopWords(reqMap);

                            int rtd = snsMapper.delSnsTopWords2(reqMap);
                            int rank = 1;
                            for (JsonElement je : value) {
                                JsonObject jo = (JsonObject) je;
                                String word = jo.get("keyword").getAsString();
                                System.out.println("#ELOG target:"+keys[0]+"/word:"+word+"/rank:"+rank);
                                reqMap.put("word", word);
                                reqMap.put("rank", rank);
                                int rti2 = snsMapper.insSnsTopWords2(reqMap);
                                rank++;
                            }
                        }
                    }
                }
                rt = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            rt = -1;
        }

        return rt;
    }

    @Override
    public List<String> getResultSnsMapByTag(String target, String date1, String tag) {
        List<String> result = null;

        Map<String, Object> reqMap = new HashMap();
        reqMap.put("target", target);
        reqMap.put("sdate", date1);
        reqMap.put("edate", date1);
        List<Map<String, Object>> resultMapArr = snsMapper.getSnsTopWords2Rank(reqMap);
        //System.out.println("#ELOG.getResultSnsMap::resultMap::"+resultMap.toString());

        if (resultMapArr != null && resultMapArr.size() > 0) {
            result = new ArrayList();
            for (Map<String, Object> resultMap : resultMapArr) {
                if (resultMap != null && resultMap.get(tag) != null) {
                    try {
                        String word = resultMap.get(tag).toString();
                        result.add(word);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

    private List<String> getSnsTopWordsListByTarget(String target, Map<String, Object> wordsMap) {
        List<String> resultArr = null;
        if (wordsMap != null) {
            resultArr = new ArrayList();

        }
        return resultArr;
    }

    private List<String> getBeforeDays(String date1, int limit) {
        List<String> daysArr = new ArrayList();
        for (int i=0; i<limit; i++) {
            int bday = - (limit - i) + 1;

            String todate = DateUtils.calculateDate(Calendar.DATE, bday, date1);
            daysArr.add(todate);

           // System.out.println("#ELOG.add beforeDays:"+todate);
        }
        return daysArr;
    }

    private List<String> getBeforeDaysWithDash(String date1, int limit) {
        List<String> daysArr = new ArrayList();
        for (int i=0; i<limit; i++) {
            int bday = - (limit - i) + 1;

            String todate = DateUtils.calculateDateWithDash(Calendar.DATE, bday, date1);
            daysArr.add(todate);

            // System.out.println("#ELOG.add beforeDays:"+todate);
        }
        return daysArr;
    }

    private JsonObject getRankArrayByTarget(String target, List<String> wordsArr1) {
        //List<String> result = new ArrayList<>();
        String date1 = snsMapper.getMaxDateStr();

        //List<String> wordsArr1 = this.getResultSnsMapByTag(target, date1, "word");

        String edate = date1;
        String sdate = DateUtils.calculateDate(Calendar.DATE, -5, date1);

        List<String> beforeDays = this.getBeforeDays(date1, 5);
        List<String> beforeDaysWithDash = this.getBeforeDaysWithDash(date1, 5);

        Map<String, Object> reqMap = new HashMap();
        reqMap.put("target", target);
        reqMap.put("sdate", sdate);
        reqMap.put("edate", edate);

        JsonObject graphs = new JsonObject();
        // caption 에 날짜 표기를 위해 Object에 날짜 array 추가  added 18.04.24
        JsonArray daysArr = JsonUtil.convertListToJsonArray(beforeDaysWithDash);
        graphs.add("days", daysArr);

        int itemCnt = 1;
        for (String word : wordsArr1) {
            if (!"".equals(word)) {
                reqMap.put("word", word);
                List<Map<String, Object>> ranksMapArr = snsMapper.getSnsTopWords2RankByWord(reqMap);
                //System.out.println("#ELOG.ranksMappArr:"+ranksMapArr.toString());
                //result = MapUtil.getListFromMapByTag2(ranksMapArr, this.getBeforeDays(date1, 5), 5);
                JsonArray rankArr = this.getRanksArrFromWordsMap(ranksMapArr, beforeDays);
                String itemTag = "ITEM" + String.format("%02d",itemCnt);
                graphs.add(itemTag, rankArr);
                //System.out.println("#ELOG.rankArr:"+rankArr.toString());
            }

            itemCnt++;
        }
        return graphs;
    }

    private JsonArray getRanksArrFromWordsMap(List<Map<String, Object>> ranksMapArr, List<String> beforeDays) {
        JsonArray graphs = new JsonArray();

        Map<String, Object> nmap = new HashMap();
        for (Map<String, Object> rm : ranksMapArr) {
            if (rm != null && rm.get("date1") != null && rm.get("rank") != null) {
                String date1 = rm.get("date1").toString();
                String rank = rm.get("rank").toString();
                nmap.put(date1, rank);
            }
        }

        Set entrySet = nmap.entrySet();
        Iterator it = entrySet.iterator();

        for (String ds : beforeDays) {
            if(nmap.get(ds) != null) {
                int rankno = 0;
                try {
                    rankno = Integer.parseInt(nmap.get(ds).toString());
                } catch (Exception e) {}
                graphs.add(rankno);
            } else {
                graphs.add(0);
            }
        }

        return graphs;
    }


    @Override
    public JsonObject getSnsTopWordsAndGraph() throws Exception {
        JsonObject result = new JsonObject();

        //String ndate = DateUtils.getLocalDate();
        //String date1 = DateUtils.calculateDate(Calendar.DATE, -1, ndate);
        String date1 = snsMapper.getMaxDateStr();

        // rank for Twitter
        String targetTwitter = "twitter";
        List<String> wordsArrTwitter = this.getResultSnsMapByTag(targetTwitter, date1, "word");
        JsonObject graph_twitter = this.getRankArrayByTarget(targetTwitter, wordsArrTwitter);

        // rank for Instagram
        String targetInsta = "insta";
        List<String> wordsArrInsta = this.getResultSnsMapByTag(targetInsta, date1, "word");
        JsonObject graph_insta = this.getRankArrayByTarget(targetInsta, wordsArrInsta);
        System.out.println("#graph_insta:"+graph_insta.toString());

        JsonArray words_instagram = JsonUtil.convertListToJsonArray(wordsArrInsta);
        result.add("WORDS_INSTAGRAM", words_instagram);

        //JsonArray captions = new JsonArray();
        //captions.add("D-5"); captions.add("D-4"); captions.add("D-3"); captions.add("D-2"); captions.add("D-1");
        JsonArray captions = null;
        if (graph_insta != null && graph_insta.get("days") != null) {
            captions = graph_insta.get("days").getAsJsonArray();
        } else if (graph_twitter != null && graph_twitter.get("days") != null) {
            captions = graph_twitter.get("days").getAsJsonArray();
        } else {
            captions = new JsonArray();
            captions.add("D-5"); captions.add("D-4"); captions.add("D-3"); captions.add("D-2"); captions.add("D-1");
        }

        graph_insta.add("CAPTIONS", captions);

        result.add("GRAPH_INSTAGRAM", graph_insta);


        JsonArray words_twitter = JsonUtil.convertListToJsonArray(wordsArrTwitter);
        result.add("WORDS_TWITTER", words_twitter);

        graph_twitter.add("CAPTIONS", captions);

        result.add("GRAPH_TWITTER", graph_twitter);

        return result;
    }

    @SuppressWarnings("unused")
    public String getNaverKordicResultNull(String keyword) {
        return "";
    }

    //@HystrixCommand(fallbackMethod = "getNaverKordicResultNull")
    @Override
    public String getNaverKordicResult(String keyword) throws Exception {
        String result = "";

        try {
            String reqUrl = naver_kordic_url;
            reqUrl = reqUrl.replace("#KEYWORD", URLEncoder.encode(keyword,"utf-8"));

            Map<String, Object> resultMap2 = HttpClientUtil.reqGetHtml(reqUrl, null
                    , Charset.forName("utf-8"),null, "bypass");

            if (resultMap2 != null && resultMap2.get("resultStr") != null) {
                result = resultMap2.get("resultStr").toString();
            }
        } catch (Exception e) {
            logger.error("/naver/kordic ERROR:"+e.toString());
            e.printStackTrace();
        }
        return result;
    }

    //@HystrixCommand(fallbackMethod = "getNaverKordicResultNull")
    @Override
    public String getCollNaverKordicResult(String keyword) throws Exception {
        String result = "";

        try {
            String reqUrl = coll_naver_kordic_url;
            reqUrl = reqUrl.replace("#KEYWORD", URLEncoder.encode(keyword,"utf-8"));

            Map<String, Object> resultMap2 = HttpClientUtil.reqGetHtml(reqUrl, null
                    , Charset.forName("utf-8"),null, "bypass");

            if (resultMap2 != null && resultMap2.get("resultStr") != null) {
                result = resultMap2.get("resultStr").toString();
            }
        } catch (Exception e) {
            logger.error("/naver/kordic ERROR:"+e.toString());
            e.printStackTrace();
        }
        return result;
    }

    //mcid로 동일 컨텐츠 검색
	public JsonObject getItemListSameMcid(Integer itemid) {
		// TODO Auto-generated method stub
		
		JsonObject result = new JsonObject();		
		Items reqIt = new Items();
		
		reqIt.setIdx(itemid);
		
		/*
		//from getItemsSearch
        List<Items> list_items = itemsMapper.searchItemsPaging(reqIt);
        List<Items> list_items2 = new ArrayList();
        for(Items one : list_items) {
            if (one != null && one.getSeries_id() != null && !"0".equals(one.getSeries_id())) {
                one.setContent_id(one.getSeries_id());
                one.setCid(one.getSeries_id());
            }
            list_items2.add(one);
        }
        JsonArray listItems = getListItemsFromArray(list_items2);
		*/
		
		List<Items> list_items = itemsMapper.getItemListSameMcid(reqIt);
        List<Items> list_items2 = new ArrayList();
        for(Items one : list_items) {
            if (one != null && one.getSeries_id() != null && !"0".equals(one.getSeries_id())) {
                one.setContent_id(one.getSeries_id());
                one.setCid(one.getSeries_id());
            }
            list_items2.add(one);
        }
		JsonArray listItems = getListItemsFromArray(list_items2);
		
		result.add("LIST_ITEMS", listItems);
		
		return result;
	}

}
