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
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.util.*;

@Service
public class CcubeService implements CcubeServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    @Value("${property.serverid}")
    private String serverid;
    @Value("${spring.static.resource.location}")
    private String UPLOAD_DIR;
    @Value("${spring.static.resource.location}")
    private String WORK_DIR;

    @Autowired
    private CcubeMapper ccubeMapper;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private ItemsTagsService itemsTagsService;
    @Autowired
    private SftpService sftpService;
    @Autowired
    private DicService dicService;

    @Override
    public List<CcubeContent> get50ActiveCcubeContents() {
        return ccubeMapper.get50ActiveCcubeContents();
    }

    @Override
    public int uptCcubeContentStat(CcubeContent req) {
        return ccubeMapper.uptCcubeContentStat(req);
    }

    @Override
    public List<CcubeSeries> get50ActiveCcubeSeries() {
        return ccubeMapper.get50ActiveCcubeSeries();
    }

    @Override
    public int uptCcubeSeriesStat(CcubeSeries req) {
        return ccubeMapper.uptCcubeSeriesStat(req);
    }

    @Override
    public CcubeContent getCcubeContentByCid(CcubeContent req) {
        return ccubeMapper.getCcubeContentByCid(req);
    }

    @Override
    public CcubeSeries getCcubeSeriesById(CcubeSeries req) {
        return ccubeMapper.getCcubeSeriesById(req);
    }

    @Override
    public int getCcubeItemIdx(CcubeKeys req) {
        int itemIdx = 0;
        itemIdx = ccubeMapper.getCcubeItemIdx(req);
        // CID로 itemIdx 조회 후 타이틀/출시년도/감독 으로 재조회 (OR, AND 복합조건) 추가 2018.04.16
        if (itemIdx == 0) {
            if (req.getPurity_title() != null && !"".equals(req.getPurity_title())
                    && req.getYear() != null && !"".equals(req.getYear())
                    && req.getDirector() != null && !"".equals(req.getDirector())
            ) {
                itemIdx = ccubeMapper.getCcubeItemIdx2(req);
            }
        }
        return itemIdx;
    }

    @Override
    public int getCcubeCIdx(CcubeKeys req) {
        return ccubeMapper.getCcubeKeysIdx(req);
    }


    @Override
    public int insCcubeKeys(CcubeKeys req) {
        int result = 0;
        if (req != null) {
            if (req.getContent_id() != null && "".equals(req.getContent_id())) req.setContent_id("");
            if (req.getKmrb_id() != null && "".equals(req.getKmrb_id())) req.setKmrb_id("");
            if (req.getMaster_content_id() != null && "".equals(req.getMaster_content_id())) req.setMaster_content_id("");
            if (req.getSeries_id() != null && "".equals(req.getSeries_id())) req.setSeries_id("");

            int countCcubeKeys = ccubeMapper.cntCcubeKeysByCidOrSid(req);

            // cid/sid 중복 등록 방지 로직 추가  18.05.17
            if (countCcubeKeys < 1) {
                result = ccubeMapper.insCcubeKeys(req);
                if (result > 0 && req.getCidx() > 0) {
                    result = req.getCidx();
                }
            }
        }

        return result;
    }

    private String getEmptyCheck(Object req) {
        if (req == null) {
            return "";
        } else {
            return req.toString();
        }
    }

    @Override
    public JsonObject getCcubeDatasByItemIdx(int itemIdx) {
        JsonObject result = null;
        if (itemIdx > 0) {
            CcubeKeys ckey = ccubeMapper.getCcubeKeys(itemIdx);
            if(ckey != null) {
                if(ckey.getSeries_id() != null && !"0".equals(ckey.getSeries_id())) {
                    CcubeSeries ser = new CcubeSeries();
                    ser.setSeries_id(ckey.getSeries_id());
                    CcubeSeries curSer = ccubeMapper.getCcubeSeriesById(ser);
                    if (curSer != null) {
                        result = new JsonObject();
                        result.addProperty("SERIES_ID", curSer.getSeries_id());
                        result.addProperty("SERIES_NM", getEmptyCheck(curSer.getSeries_nm()));
                        result.addProperty("MASTER_CONTENT_ID", "");
                        result.addProperty("CONTENT_ID", "");
                        result.addProperty("PURITY_TITLE", getEmptyCheck(curSer.getPurity_title()));
                        result.addProperty("CONTENT_TITLE", "");
                        result.addProperty("ENG_TITLE", getEmptyCheck(curSer.getEng_title()));
                        result.addProperty("TITLE_BRIEF", "");
                        result.addProperty("DIRECTOR", getEmptyCheck(curSer.getDirector()));
                        result.addProperty("YEAR", getEmptyCheck(curSer.getYear()));
                        result.addProperty("ACTORS_DISPLAY", getEmptyCheck(curSer.getActors_display()));
                        result.addProperty("COUNTRY_OF_ORIGIN", getEmptyCheck(curSer.getCountry_of_origin()));
                        result.addProperty("SAD_CTGRY_NM", getEmptyCheck(curSer.getSad_ctgry_nm()));
                        result.addProperty("DOMESTIC_RELEASE_DATE", "");
                        result.addProperty("KT_RATING", getEmptyCheck(curSer.getKt_rating()));
                        result.addProperty("KMRB_ID", "");
                        result.addProperty("POSTER_URL", getEmptyCheck(curSer.getPoster_url()));
                    }
                } else if (result == null && ckey.getContent_id() != null && !"0".equals(ckey.getContent_id())) {
                    CcubeContent con = new CcubeContent();
                    con.setContent_id(ckey.getContent_id());
                    CcubeContent curCon = ccubeMapper.getCcubeContentByCid(con);
                    if (curCon != null) {
                        result = new JsonObject();
                        result.addProperty("SERIES_ID", "");
                        result.addProperty("SERIES_NM", "");
                        result.addProperty("MASTER_CONTENT_ID", getEmptyCheck(curCon.getMaster_content_id()));
                        result.addProperty("CONTENT_ID", getEmptyCheck(curCon.getContent_id()));
                        result.addProperty("PURITY_TITLE", getEmptyCheck(curCon.getPurity_title()));
                        result.addProperty("CONTENT_TITLE", getEmptyCheck(curCon.getContent_title()));
                        result.addProperty("ENG_TITLE", getEmptyCheck(curCon.getEng_title()));
                        result.addProperty("TITLE_BRIEF", getEmptyCheck(curCon.getTitle_brief()));
                        result.addProperty("DIRECTOR", getEmptyCheck(curCon.getDirector()));
                        result.addProperty("YEAR", getEmptyCheck(curCon.getYear()));
                        result.addProperty("ACTORS_DISPLAY", getEmptyCheck(curCon.getActors_display()));
                        result.addProperty("COUNTRY_OF_ORIGIN", getEmptyCheck(curCon.getCountry_of_origin()));
                        result.addProperty("SAD_CTGRY_NM", getEmptyCheck(curCon.getSad_ctgry_nm()));
                        result.addProperty("DOMESTIC_RELEASE_DATE", getEmptyCheck(curCon.getDomestic_release_date()));
                        result.addProperty("KT_RATING", getEmptyCheck(curCon.getKt_rating()));
                        result.addProperty("KMRB_ID", getEmptyCheck(curCon.getKmrb_id()));
                        result.addProperty("POSTER_URL", getEmptyCheck(curCon.getPoster_url()));
                    }
                }
            }
        }

        return result;
    }

    private Map<String, Object> getTagsMetasMap(List<ItemsTags> tagsMetasList) {
        Map<String,Object> resMap = new HashMap();
        for (ItemsTags it : tagsMetasList) {
            if (it != null && it.getMtype() != null && it.getMeta() != null) {
                resMap.put(it.getMtype(), it.getMeta());
            }
        }
        return resMap;
    }

    // LIST_ -> META_
    private JsonObject getTagsMetasObj(JsonObject origObj, List<ItemsTags> tagsMetasList) throws Exception {
        if(origObj == null) origObj = new JsonObject();

        List<String> origTypes = new ArrayList();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
        origTypes.add("LIST_SUBGENRE");
        origTypes.add("LIST_SEARCHKEYWORDS");
        origTypes.add("METASCHARACTER");
        origTypes.add("LIST_RECO_TARGET");
        origTypes.add("LIST_RECO_SITUATION");

        Map<String, Object> tagsMetasMap = this.getTagsMetasMap(tagsMetasList);

        for(String type : origTypes) {
            String mtype = type;
            mtype = mtype.replace("METAS", "META_");
            mtype = mtype.replace("LIST_", "META_");
            mtype = mtype.replace("SEARCHKEYWORDS", "SEARCH");

            if (tagsMetasMap != null && tagsMetasMap.get(type) != null) {
                String metasStr = tagsMetasMap.get(type).toString();
                //System.out.println("#MLOG.convert.type:"+type+" | metasStr:"+metasStr);

                List<String> tmpList = null;
                if ((type.contains("METAS") || type.contains("LIST")) && !type.equals("LIST_SEARCHKEYWORDS")) {
                    JsonArray tmpArr = JsonUtil.getJsonArray(metasStr);
                    tmpList = JsonUtil.getListFromJsonArrayByTag(tmpArr, "word", 100);
                } else {
                    tmpList = StringUtil.convertStringToListByComma(metasStr);
                    //System.out.println("#MLOG.tmpLIST:"+tmpList.toString());
                }
                // 문자열 List에서 각 메타 중 콤마를 제거
                tmpList = StringUtil.removeCharacterFromList(tmpList, ",");
                List<String> tmpList2 = null;
                // tag 최종 필터 적용
                if (tmpList != null && tmpList.size() > 0) {
                    tmpList2 = new ArrayList();
                    for (String ts : tmpList) {
                        String ts2 = StringUtil.filterLastGenre(ts);
                        if (!"".equals(ts2)) {
                            tmpList2.add((ts2));
                        }
                    }
                }

                // List를 구분자로 구분하여 String으로 치환, 현재 delimeter는 comma
                String meta = StringUtil.convertArrayStringToStringAddDelimeter(tmpList2, ",");

                // 각 meta의 최대 size는 연동규격에 맞추어 700byte에서 cut
                int limitSize = 699;

                if (meta.length() < limitSize) limitSize = meta.length();
                meta = meta.substring(0,limitSize);

                origObj.addProperty(mtype, meta);
                //System.out.println("#ELOG getTagsMetasObj.result:"+origObj.toString());
            }
        }

        return origObj;
    }

    public static Map<String, Object> insertedCidList;

    private boolean isExistCid(String cid) {
        boolean isExist = false;
        if (insertedCidList.get(cid) != null) {
            isExist = true;
        } else {
            insertedCidList.put(cid, null);
        }
        return isExist;
    }

    @Override
    public JsonArray getJsonArrayForCcubeOutput(JsonArray contentsArr, String type, Map<String, Object> reqMap) throws Exception {
        if (contentsArr == null) contentsArr = new JsonArray();
        if (insertedCidList == null) insertedCidList = new HashMap<String, Object>();

        if (reqMap != null) {
            int itemIdx = 0;
            String contentId = "";
            String masterId = "";
            String seriesId = "";
            if (reqMap.get("idx") != null) {
                String sIdx = String.valueOf(reqMap.get("idx"));
                itemIdx = Integer.parseInt(sIdx);
            }
            if (reqMap.get("content_id") != null) {
                contentId = reqMap.get("content_id").toString();
            }
            if (reqMap.get("master_content_id") != null) {
                masterId = reqMap.get("master_content_id").toString();
            }
            if (reqMap.get("series_id") != null) {
                seriesId = reqMap.get("series_id").toString();
            }
            if (itemIdx > 0) {
                Items itemInfo = itemsService.getItemInfoOne(itemIdx);
                //System.out.println("#MLOG: getContent::"+itemInfo.toString());
                if (itemInfo != null) {
                    JsonObject newItem = new JsonObject();
                    int limitSize = 199;

                    /* ##PAHSE #2 contentId or seriesId 기준으로 중복 제거 */
                    System.out.println("#series_Id :"+seriesId+" / dupcheck:"+isExistCid(seriesId));

                    if (!isExistCid(contentId) || !isExistCid(seriesId)) {

                        if(type.contains("CcubeContent")) {
                            newItem.addProperty("CONTENT_ID", contentId);
                            String title = itemInfo.getTitle();

                            if (title.length() < limitSize) limitSize = title.length();
                            title = title.substring(0,limitSize);

                            newItem.addProperty("META_CONTENT_TITLE", title);
                        } else if(type.contains("CcubeSeries")) {
                            newItem.addProperty("SERIES_ID", seriesId);
                            String title = itemInfo.getTitle();
                            if (title.length() < limitSize) limitSize = title.length();
                            title = title.substring(0,limitSize);

                            newItem.addProperty("META_SERIES_TITLE", title);
                        }

                        //System.out.println("#ELOG insertedCidList :: "+insertedCidList.toString());

                        // items_tags_metas를 읽어와서 Obj에 매핑
                        //System.out.println("#ELOG newItem-first:"+newItem.toString());
                        newItem = this.getTagsMetasObj(newItem, itemInfo.getTagsMetasList());
                        System.out.println("#ELOG newItem-second:"+newItem.toString());
                        //System.out.println("#ELOG getTagsMetasObj:"+newItem.toString());

                        // items_metas에서 award를 가져와서 Obj에 매핑
                        String awardStr = "";
                        if (itemInfo.getMetaList() != null) {
                            for (ItemsMetas im : itemInfo.getMetaList()) {
                                if (im != null && im.getMtype() != null && im.getMeta() != null
                                        && "award".equals(im.getMtype())) {
                                    awardStr = CommonUtil.removeLineFeed(im.getMeta().trim());
                                    awardStr = CommonUtil.removeTag(awardStr);
                                    awardStr = CommonUtil.removeAllSpec1(awardStr);
                                }
                            }
                        }
                        limitSize = 3999;
                        //limitSize = 699;
                        if (awardStr.length() < limitSize) limitSize = awardStr.length();
                        awardStr = awardStr.substring(0,limitSize);
                        awardStr = awardStr.replace("FAIL","");

                        newItem.addProperty("META_AWARD",awardStr);

                        /* META_SUBGENRE */
                        //newItem = itemsTagsService.getSubgenresString(itemIdx, newItem);

                        // output 연동규격에 맞추어 값이 없는 경우 공백으로 채워줌 added 18.04.24
                        List<String> origTypes = null;
                        if(type.contains("CcubeContent")) {
                            origTypes = this.getContentOutputMetaTypes();
                        } else if(type.contains("CcubeSeries")) {
                            origTypes = this.getSeriesOutputMetaTypes();
                        }
                        newItem = JsonUtil.setEmptyMetasAndReplaceComma(newItem, origTypes);


                        /* 임시
                        // 검색메타 삭제
                        if (newItem.get("META_SEARCH") != null) {
                            newItem.remove("META_SEARCH");
                            newItem.addProperty("META_SEARCH","");
                        }
                        */

                        contentsArr.add(newItem);
                        //System.out.println("#MLOG.contentsArr.add.newItem:"+newItem.toString());
                    }
                }

            }
        }
        return contentsArr;
    }

    private JsonArray getNewArrWithResultTags(JsonArray oldArr, String mtype) {
        JsonArray newArr = null;
        if (oldArr != null && oldArr.size() > 0 && !"".equals(mtype)) {
            for (JsonElement je : oldArr) {
                JsonObject jo = (JsonObject) je;

                System.out.println("# tmp jo::"+jo.toString());

                newArr.add(jo);
            }
        }

        return newArr;
    }

    private JsonObject getMetaTagsWithResultTag(JsonObject req) {
        List<String> origTypes = new ArrayList();
        origTypes.add("WHAT");
        origTypes.add("WHERE");
        origTypes.add("WHO");
        origTypes.add("WHEN");
        origTypes.add("EMOTION");

        System.out.println("## req JsonObject::"+req.toString());

        for (String otype : origTypes) {
            if (req.get("METAS"+otype) != null) {
                JsonArray thisArr = (JsonArray) req.get("METAS"+otype);

                JsonArray newArr = this.getNewArrWithResultTags(thisArr, otype);

                req.remove("METAS"+otype);
                req.add("METAS"+otype, newArr);
            }
        }

        return req;
    }

    private List<String> getContentOutputMetaTypes() {
        List<String> origTypes = new ArrayList();
        origTypes.add("CONTENT_ID");
        origTypes.add("META_CONTENT_TITLE");
        origTypes.add("META_WHEN");
        origTypes.add("META_WHERE");
        origTypes.add("META_WHAT");
        origTypes.add("META_WHO");
        origTypes.add("META_EMOTION");
        origTypes.add("META_SUBGENRE");
        origTypes.add("META_SEARCH");
        origTypes.add("META_CHARACTER");
        origTypes.add("META_RECO_TARGET");
        origTypes.add("META_RECO_SITUATION");
        origTypes.add("META_AWARD");

        return origTypes;
    }

    private List<String> getSeriesOutputMetaTypes() {
        List<String> origTypes = new ArrayList();
        origTypes.add("SERIES_ID");
        origTypes.add("META_SERIES_TITLE");
        origTypes.add("META_WHEN");
        origTypes.add("META_WHERE");
        origTypes.add("META_WHAT");
        origTypes.add("META_WHO");
        origTypes.add("META_EMOTION");
        origTypes.add("META_SUBGENRE");
        origTypes.add("META_SEARCH");
        origTypes.add("META_CHARACTER");
        origTypes.add("META_RECO_TARGET");
        origTypes.add("META_RECO_SITUATION");
        origTypes.add("META_AWARD");

        return origTypes;
    }

    @Override
    public int processCcubeOutputToJson() {
        int rt = 0;
        rt = this.processCcubeOutputToJsonByType("CcubeSeries");
        rt = this.processCcubeOutputToJsonByType("CcubeContent");

        return rt;
    }

    @Override
    @Transactional
    public int processCcubeOutputToJsonByType(String type) {
        int rt = 0;

        int pageSize = 20;
        Items req = new Items();
        req.setType(type);
        req.setPageSize(pageSize);

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        countAll = ccubeMapper.cntCcubeOutputListStandby(req);
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("TOTAL_COUNT", countAll);

        logger.info("#MLLOG:processCcubeOutput:: type:"+type+" / countAll:"+countAll);
        if(countAll > 0) {
            int pageAll = 0;
            if (countAll == 0) {
                pageAll = 1;
            } else {
                pageAll = countAll / pageSize + 1;
            }
            //System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;
            Map<Long, Integer> uptKeyAndTagCntList = new HashMap();

            try {
                for (int pno = 1; pno <= pageAll; pno++) {
                    req.setPageNo(pno);
                    reqItems = ccubeMapper.getCcubeOutputListStandby(req);
                    if (reqItems != null) {
                        logger.info("#SCHEDULE processCcubeOutputToJson.getCcubeOutputListStandby: type:" + type + " / pno:" + pno + " / items-size:" + reqItems.size());
                        for (Map<String, Object> ins : reqItems) {

                            logger.info("#SCHEDULE processCcubeOutputToJson.reqMap:"+ins.toString());
                            contents = this.getJsonArrayForCcubeOutput(contents, type, ins);

                            uptKeyAndTagCntList.put((Long) ins.get("hidx"), (Integer) ins.get("uptcnt"));
                            logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to json ContentsArr:" + contents.toString());
                        }
                    }

                }
                resultObj.add("CONTENTS", contents);
                logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to jsonObj:" + resultObj.toString());

                String fileNameContent = (type.startsWith("CcubeSeries") ? "METAS_SERIES_" : "METAS_MOVIE_");
                fileNameContent += DateUtils.getLocalDate("yyyyMMddHHmm") + ".json";

                int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultObj.toString(), UPLOAD_DIR, fileNameContent, "utf-8");
                logger.info("#SCHEDULE processCcubeOutputToJson file:" + UPLOAD_DIR + fileNameContent + " rt:" + rtFileC);
                if (rtFileC > 0) {
                    int rtUp = sftpService.uploadToCcube(WORK_DIR, fileNameContent);
                }

                rt = 1;
            } catch (Exception e) {
                rt = -3;
                logger.error("#ERROR:" + e);
            }

            System.out.println("#UPT stat:: from:" + uptKeyAndTagCntList.toString());

            /* update CCUBE_OUTPUT stat = S , uptcnt++ */
            Set entrySet = uptKeyAndTagCntList.entrySet();
            Iterator it = entrySet.iterator();

            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Long hidx = (Long) me.getKey();
                Integer nextUptCnt = (Integer) me.getValue() + 1;

                Map<String, Object> uptItem = new HashMap();
                uptItem.put("hidx", hidx);
                uptItem.put("uptcnt", nextUptCnt);
                uptItem.put("stat", "S");

                int rtupt = ccubeMapper.uptCcubeOutputStat(uptItem);
            }
        }
        return rt;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> processCcubeOutputToMapListByType(String type) {
        int rt = 0;
        List<Map<String, Object>> result = null;

        int pageSize = 20;
        Items req = new Items();
        req.setType(type);
        req.setPageSize(pageSize);

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        countAll = ccubeMapper.cntCcubeOutputListStandby(req);
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("TOTAL_COUNT", countAll);

        logger.info("#MLLOG:processCcubeOutput:: type:"+type+" / countAll:"+countAll);
        if(countAll > 0) {
            int pageAll = 0;
            if (countAll == 0) {
                pageAll = 1;
            } else {
                pageAll = countAll / pageSize + 1;
            }
            //System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;
            Map<Long, Integer> uptKeyAndTagCntList = new HashMap();

            try {
                for (int pno = 1; pno <= pageAll; pno++) {
                    req.setPageNo(pno);
                    reqItems = ccubeMapper.getCcubeOutputListStandby(req);
                    if (reqItems != null) {
                        logger.info("#SCHEDULE processCcubeOutputToJson.getCcubeOutputListStandby: type:" + type + " / pno:" + pno + " / items-size:" + reqItems.size());
                        for (Map<String, Object> ins : reqItems) {
                            contents = this.getJsonArrayForCcubeOutput(contents, type, ins);
                            uptKeyAndTagCntList.put((Long) ins.get("hidx"), (Integer) ins.get("uptcnt"));
                            //logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to json ContentsArr:" + contents.toString());
                        }
                    }

                }
                resultObj.add("CONTENTS", contents);
                logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to jsonObj:" + resultObj.toString());

                String fileNameContent = (type.startsWith("CcubeSeries") ? "META_SERIES_" : "META_MOVIE_");
                fileNameContent += DateUtils.getLocalDate("yyyyMMddHH") + ".json";

                int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultObj.toString(), UPLOAD_DIR, fileNameContent, "utf-8");
                logger.info("#SCHEDULE processCcubeOutputToJson file:" + UPLOAD_DIR + fileNameContent + " rt:" + rtFileC);
                int rtUp = sftpService.uploadToCcube(WORK_DIR, fileNameContent);

                rt = 1;
            } catch (Exception e) {
                rt = -3;
                logger.error("#ERROR:" + e);
            }

        }
        return result;
    }

    @Override
    @Transactional
    public int processCcubeSeriesOutputToJsonTest() {
        int rt = 0;
        String type = "CcubeSeries";

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        countAll = ccubeMapper.cntCcubeOutputListSeriesAll();
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("TOTAL_COUNT", countAll);

        logger.info("#MLLOG:processCcubeOutput:: countAll:"+countAll);
        if(countAll > 0) {
            //System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;
            Map<Long, Integer> uptKeyAndTagCntList = new HashMap();

            try {
                //for (int pno = 1; pno <= pageAll; pno++) {

                    reqItems = ccubeMapper.getCcubeOutputListSeriesAll();
                    if (reqItems != null) {
                        logger.info("#SCHEDULE processCcubeOutputToJson.getCcubeOutputListStandby:items-size:" + reqItems.size());
                        for (Map<String, Object> ins : reqItems) {
                            contents = this.getJsonArrayForCcubeOutput(contents, type, ins);
                            uptKeyAndTagCntList.put((Long) ins.get("hidx"), (Integer) ins.get("uptcnt"));
                            logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to json ContentsArr:" + contents.toString());
                        }
                    }

                //}
                resultObj.add("CONTENTS", contents);
                logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to jsonObj:" + resultObj.toString());

                String fileNameContent = (type.startsWith("CcubeSeries") ? "META_SERIES_" : "META_MOVIE_");
                fileNameContent += DateUtils.getLocalDate("yyyyMMddHH") + ".json";

                int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultObj.toString(), UPLOAD_DIR, fileNameContent, "utf-8");
                logger.info("#SCHEDULE processCcubeOutputToJson file:" + UPLOAD_DIR + fileNameContent + " rt:" + rtFileC);
                int rtUp = sftpService.uploadToCcube(WORK_DIR, fileNameContent);

                rt = 1;
            } catch (Exception e) {
                rt = -3;
                logger.error("#ERROR:" + e);
            }

            System.out.println("#UPT stat:: from:" + uptKeyAndTagCntList.toString());

            /* update CCUBE_OUTPUT stat = S , uptcnt++ */
            Set entrySet = uptKeyAndTagCntList.entrySet();
            Iterator it = entrySet.iterator();

            /*
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Long hidx = (Long) me.getKey();
                Integer nextUptCnt = (Integer) me.getValue() + 1;

                Map<String, Object> uptItem = new HashMap();
                uptItem.put("hidx", hidx);
                uptItem.put("uptcnt", nextUptCnt);
                uptItem.put("stat", "S");

                int rtupt = ccubeMapper.uptCcubeOutputStat(uptItem);
            }
            */
        }
        return rt;
    }

    @Override
    public int insCcubeContent(CcubeContent req) {
        int rt = 0;
        rt = ccubeMapper.insCcubeContentOrig(req);

        // CID 중복 제거 컨텐츠 등록
        rt = ccubeMapper.insCcubeContent(req);

        return rt;
    }
    @Override
    public int insCcubeSeries(CcubeSeries req) {
        int rt = 0;
        rt = ccubeMapper.insCcubeSeriesOrig(req);

        // SID 중복제거 시리즈 등록
        rt = ccubeMapper.insCcubeSeries(req);

        return rt;
    }

    @Override
    public int insCcubeContentManual(CcubeContent req) {
        int rt = 0;
        rt = ccubeMapper.insCcubeContentOrigManual(req);

        // CID 중복 제거 컨텐츠 등록
        //rt = ccubeMapper.insCcubeContent(req);

        return rt;
    }
    @Override
    public int insCcubeSeriesManual(CcubeSeries req) {
        int rt = 0;
        rt = ccubeMapper.insCcubeSeriesOrigManual(req);

        // SID 중복제거 시리즈 등록
        //rt = ccubeMapper.insCcubeSeries(req);

        return rt;
    }

    @Override
    public int insCcubeOutput(Map<String,Object> req) {
        return ccubeMapper.insCcubeOutput(req);
    }
}
