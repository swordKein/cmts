package com.kthcorp.cmts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyzeService implements AnalyzeServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(AnalyzeService.class);

    //@Autowired
    //private ConfTargetMapper confTargetMapper;
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;
    @Autowired
    private DicPumsaWordsMapper dicPumsaWordsMapper;
    @Autowired
    private SchedTargetMappingHistMapper schedTargetMappingHistMapper;
    @Autowired
    private SchedTargetContentMapper schedTargetContentMapper;
    @Autowired
    private MetaKeywordMappingMapper metaKeywordMappingMapper;
    @Autowired
    private StepService stepService;
    @Autowired
    private DicService dicService;
    @Autowired
    private SchedTriggerHistMapper schedTriggerHistMapper;
    @Autowired
    private DicKeywordsMapper dicKeywordsMapper;
    @Autowired
    private ItemsSchedMappingMapper itemsSchedMappingMapper;
    @Autowired
    private ItemsTagsService itemsTagsService;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private ApiService apiService;

    /* 올레TV 메타 확장 - 분석 수행 STEP07~09
     */
    @Override
    public int ollehTvMetaAnalyzeScheduleCheck() throws Exception {
        logger.info("#ollehTvMetaAnalyzeScheduleCheck start");
        int rtcode = 0;
        // STEP07 : 분석대상 중 등록일 상위 50개를 읽어옴
        List<SchedTrigger> schedList = this.step07();

        if (schedList != null && schedList.size() > 0) {
            logger.info("#STEP:07:: schedList:" + schedList.toString());
            logger.info("#STEP:08:: searching analyze-target ConfTarget by schedList! count:" + schedList.size());
            for (SchedTrigger sched : schedList) {
                String rt_stat = "F";

                // STEP08 진행 전 스케쥴 stat = P 로 변경
                if (sched != null) {
                    int sc_id = sched.getSc_id();
                    int itemIdx = sched.getItemIdx();
                    String type = sched.getType();
                    int tcnt = (sched.getTcnt() != null) ? sched.getTcnt() : 0;
                    String movietitle = sched.getMovietitle();
                    System.out.println("#STEP08 before STEP09 update tcnt:: from:"+sched.getTcnt());
                    //tcnt = tcnt + 1;
                    sched.setTcnt(tcnt);

                    //items_hist에 등록 for 통계
                    int rthist = itemsService.insItemsHist(itemIdx, "analyze", "R", movietitle, "START_ANALYZE", sc_id);

                    //System.out.println("## uptSchedTriggerForAnalyzeStep07 params:"+sched.toString());
                    // STEP08 실행 전 sched_trigger의 stat를 P로 업데이트
                    int rts = stepService.uptSchedTriggerForAnalyzeStep07(sched);

                    // STEP08 : 가져온 리스트에서 개별 타겟리스트의 설정에 따라 수집 파라미터 조회 bypass
                    //ConfTarget tg = this.step08(target);

                    // STEP09 : 분석 처리 후 히스토리 저장
                    // STEP09 : 조회된 설정에 맞는 Analyze 분리 시작 - ThreadPool로 전달
                    logger.info("#STEP:09:: analyze start! by SchedTrigger info!");
                    JsonObject resultAnalyze = run_step09(sched, sc_id, type, sched.getTcnt());

                    // SchedTrigger 에 대한 히스토리 저장
                    SchedTriggerHist reqSth = new SchedTriggerHist();
                    reqSth.setSc_id(sc_id);
                    reqSth.setType(sched.getType());
                    reqSth.setTcnt(tcnt);

                    if (resultAnalyze != null && resultAnalyze.get("rt_code") != null && "OK".equals(resultAnalyze.get("rt_code").getAsString())) {
                        reqSth.setStat("S");
                        reqSth.setRt_code("OK");
                        reqSth.setRt_msg("SUCCESS");
                        rtcode = 1;
                        rt_stat = "S";
                    } else {
                        reqSth.setStat("F");
                        reqSth.setRt_code("FAIL");
                        reqSth.setRt_msg("FAIL");
                        rtcode = -1;
                        rt_stat = "F";
                    }

                    System.out.println("#STEP:09:: analyze sched history writing! by "+reqSth.toString());
                    int rt1 = stepService.uptSchedTriggerProgsAfterAnalyzeTargetOneProcess(sc_id, rt_stat);

                    //items_hist에 등록 for 통계
                    rthist = itemsService.insItemsHist(itemIdx, "analyze", rt_stat, movietitle, "END_ANALYZE", sc_id);

                    // items_stat 저장
                    //int itemIdx = sched.getItemIdx();
                    int rt_it_stat = itemsService.insItemsStatOne(itemIdx, "A", rt_stat);

                    logger.debug("#STEP:09:: analyze sched history writing! by "+reqSth.toString());
                    // 스케쥴 이력 저장
                    int rtsth = schedTriggerHistMapper.insSchedTriggerHist(reqSth);
                }
            }
        } else {
            logger.info("#ollehTvMetaAnalyzeScheduleCheck end! schedule list is empty!");
        }
        return rtcode;
    }

    /* for test */
    @Override
    public List<SchedTrigger> step07byScid(SchedTrigger req) {

        System.out.println("#STEP07 for Collect test! by sc_id:"+req.getSc_id());
        List<SchedTrigger> result = new ArrayList();
        SchedTrigger item1 = schedTriggerMapper.getSchedTriggerOneByScid(req);

        result.add(item1);
        List<SchedTrigger> result2 = new ArrayList<SchedTrigger>();
        for(SchedTrigger st : result) {
            // 스케쥴과 영화와 컨텐츠 연결하는 서브 쿼리 실행

            SchedTargetContent reqC = new SchedTargetContent();
            reqC.setSc_id(st.getParent_sc_id());
            SchedTargetContent contentOne = schedTargetContentMapper.getSchedTargetContentLastOne(reqC);
            st.setContentOne(contentOne);
            result2.add(st);
            //System.out.println("#STEP07 for Analyze get schedTargetContent::"+contentList.toString());
        }
        System.out.println("#STEP07 for Analyze! result:"+result2);
        return result2;
    }

    /* test */
    @Override
    public int test_ollehTvMetaAnalyzeScheduleCheck(SchedTrigger req1) throws Exception {
        logger.info("#ollehTvMetaAnalyzeScheduleCheck start");
        int rtcode = 0;
        // STEP07 : 분석대상 중 등록일 상위 50개를 읽어옴
        List<SchedTrigger> schedList = this.step07byScid(req1);

        if (schedList != null && schedList.size() > 0) {
            logger.info("#STEP:07:: schedList:" + schedList.toString());
            logger.info("#STEP:08:: searching analyze-target ConfTarget by schedList! count:" + schedList.size());
            for (SchedTrigger sched : schedList) {
                String rt_stat = "F";

                // STEP08 진행 전 스케쥴 stat = P 로 변경
                if (sched != null) {
                    int sc_id = sched.getSc_id();
                    String type = sched.getType();
                    int tcnt = (sched.getTcnt() != null) ? sched.getTcnt() : 0;
                    System.out.println("#STEP08 before STEP09 update tcnt:: from:"+sched.getTcnt());
                    //tcnt = tcnt + 1;
                    sched.setTcnt(tcnt);

                    //System.out.println("## uptSchedTriggerForAnalyzeStep07 params:"+sched.toString());
                    // STEP08 실행 전 sched_trigger의 stat를 P로 업데이트
                    int rts = stepService.uptSchedTriggerForAnalyzeStep07(sched);

                    // STEP08 : 가져온 리스트에서 개별 타겟리스트의 설정에 따라 수집 파라미터 조회 bypass
                    //ConfTarget tg = this.step08(target);

                    // STEP09 : 분석 처리 후 히스토리 저장
                    // STEP09 : 조회된 설정에 맞는 Analyze 분리 시작 - ThreadPool로 전달
                    logger.info("#STEP:09:: analyze start! by SchedTrigger info!");
                    JsonObject resultAnalyze = run_step09(sched, sc_id, type, sched.getTcnt());

                    // SchedTrigger 에 대한 히스토리 저장
                    SchedTriggerHist reqSth = new SchedTriggerHist();
                    reqSth.setSc_id(sc_id);
                    reqSth.setType(sched.getType());
                    reqSth.setTcnt(tcnt);

                    if (resultAnalyze != null && resultAnalyze.get("rt_code") != null && "OK".equals(resultAnalyze.get("rt_code").getAsString())) {
                        reqSth.setStat("S");
                        reqSth.setRt_code("OK");
                        reqSth.setRt_msg("SUCCESS");
                        rtcode = 1;
                        rt_stat = "S";
                    } else {
                        reqSth.setStat("F");
                        reqSth.setRt_code("FAIL");
                        reqSth.setRt_msg("FAIL");
                        rtcode = -1;
                        rt_stat = "F";
                    }

                    System.out.println("#STEP:09:: analyze sched history writing! by "+reqSth.toString());
                    int rt1 = stepService.uptSchedTriggerProgsAfterAnalyzeTargetOneProcess(sc_id, rt_stat);

                    logger.debug("#STEP:09:: analyze sched history writing! by "+reqSth.toString());
                    // 스케쥴 이력 저장
                    int rtsth = schedTriggerHistMapper.insSchedTriggerHist(reqSth);
                }
            }
        } else {
            logger.info("#ollehTvMetaAnalyzeScheduleCheck end! schedule list is empty!");
        }
        return rtcode;
    }

    /*** 분석 작업 STEP07 ~ 09 ***/
    /* STEP07
     * SchedTrigger & targetList<ConfTarget> 통해 분석 스케쥴 50개 조회
    */
    @Override
    public List<SchedTrigger> step07() {

        System.out.println("#STEP07 for Analyze!");
        SchedTrigger req = new SchedTrigger();
        req.setType("A");
        req.setStat("Y");

        List<SchedTrigger> result = schedTriggerMapper.get50ByTypeStatSchedTriggerList(req);
        List<SchedTrigger> result2 = new ArrayList<SchedTrigger>();
        for(SchedTrigger st : result) {
            // 스케쥴과 영화와 컨텐츠 연결하는 서브 쿼리 실행

            SchedTargetContent reqC = new SchedTargetContent();
            reqC.setSc_id(st.getParent_sc_id());
            SchedTargetContent contentOne = schedTargetContentMapper.getSchedTargetContentLastOne(reqC);
            st.setContentOne(contentOne);
            result2.add(st);
            //System.out.println("#STEP07 for Analyze get schedTargetContent::"+contentList.toString());
        }
        System.out.println("#STEP07 for Analyze! result:"+result2);
        return result2;
    }


    /* STEP08
     * 분석 대상 설정 조회는 bypass
     * 조회 후 stat = P 로 변경
    */
    @Override
    public ConfTarget step08(ConfTarget req) throws Exception {
        ConfTarget result = null;
        /*
        if (req != null && req.getTg_id() != null) {

            result = confTargetMapper.getConfTargetById(req);
            logger.info("#STEP:08:: target: req.tg_id:"+req.getTg_id().toString()+" :: getConfTargetById:"+result.toString());
        } else {
            if (req!=null) {
                logger.info("#STEP:08::fail target is empty!");
            } else {
                logger.info("#STEP:08::fail target tg_url is empty!");
            }
        }
        */
        return result;
    }

    /* STEP09 분석 작업 - sub1 일부
    * 주어진 KeywordMapList와 DIcKeywords 대조하여 결과 MapList 리턴
     */
    public Map<String, Object> getListMappingKeywords(
                                HashMap<String, Object> resultMap
                                , HashMap<String, Double> keywordMapList
                                , List<DicKeywords> dicKeywordList ) {
        if (resultMap == null) resultMap = new HashMap<String, Object>();

        // keyword 매핑 된 결과리스트, 기존 결과물이 있으면 이어 붙임
        List<HashMap<String, Object>> KeywordList = new ArrayList<>();

        // keyword 매핑 된 결과리스트, 다운사이징 ratio 포함
        List<HashMap<String, Object>> keywordRatioList = new ArrayList();

        // keyword 미분류 키워드 리스트, 기존 결과물이 있으면 이어 붙임
        Map<String, Double> notTaggedKeywordList = (HashMap<String, Double>) keywordMapList.clone();
        if (resultMap.get("notTaggedKeywordList") != null) {
            notTaggedKeywordList = (Map<String, Double>) resultMap.get("notTaggedKeywordList");
        }
        if (notTaggedKeywordList == null) notTaggedKeywordList = new HashMap<String, Double>();

        //double sumFreq = 0.0;

        for (DicKeywords KeywordOne : dicKeywordList) {
            //System.out.println("#STEP09-1-sub::KeywordOne:: "+KeywordOne.toString());
            if (KeywordOne.getKeyword() != null) {
                String Keyword = KeywordOne.getKeyword().trim();
                double Ratio = ((KeywordOne.getRatio() != null) ? KeywordOne.getRatio() : 0.0);

                Set entrySet = keywordMapList.entrySet();
                Iterator lt = entrySet.iterator();

                while(lt.hasNext()){
                    Map.Entry key1 = (Map.Entry)lt.next();
                    String tmpKey = key1.getKey().toString();
                    Double tmpValue = (Double) key1.getValue();
                    //System.out.println("#STEP09-1-sub::getListMappingKeywords:: "+tmpKey+" vs "+Keyword);

                    // 키워드 매핑과 현재 키워드가 동일하면 태그에 freq * ratio 값을 매핑하여 더해준다
                    if(tmpKey.trim().equals(Keyword)) {
                        HashMap<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap.put("Keyword", tmpKey.trim());
                        tmpMap.put("KeywordFreq", tmpValue);
                        //tmpMap.put("Keyord", Keyword);
                        tmpMap.put("Ratio", Ratio);

                        //System.out.println("#KeyList.add:"+tmpMap.toString());
                        KeywordList.add(tmpMap);
                        notTaggedKeywordList.remove(tmpKey.trim());
                        //sumFreq += tmpValue;
                    }
                }
            }
        }
        //return KeyList;
        resultMap.put("mappingKeywordList", KeywordList);

        resultMap.put("notTaggedKeywordList", notTaggedKeywordList);

/*
        for (HashMap<String, Object> tmpMap2 : KeywordList) {
            double freq1 = (double) tmpMap2.get("KeywordFreq");
            double ratio1 = (double) freq1 / (double) sumFreq * 10;

            HashMap<String, Object> newMap = new HashMap<String, Object>();
            newMap.put("word", tmpMap2.get("Keyword"));
            newMap.put("freq", tmpMap2.get("KeywordFreq"));
            newMap.put("ratio", ratio1);
            keywordRatioList.add(newMap);
        }

        resultMap.put("mappingKeywordRatioList", keywordRatioList);
*/
        return resultMap;
    }

    /* STEP09 분석 작업 - sub1 일부
    * Keyword매핑 리스트에서 각 키워드 별 value를 계산한 SortedMap으로 리턴한다.
     */
    public Map<String, Double> getKeywordValueMap(List<HashMap<String,Object>> KeyList) {
        HashMap<String, Double> ValueMap = new HashMap<String, Double>();
        //double sumFreq = 0.0;

        for (HashMap<String, Object> item : KeyList) {
            String tmpKeyword = (String) item.get("Keyword");
            double tmpFreq = (Double) item.get("KeywordFreq");
            double tmpRatio = (Double) item.get("Ratio");
            //System.out.println("#getKeywordValueMap try::  Keyword:"+tmpKeyword+"  //  freq:"+tmpFreq);

            double allValue = 0.0;

            if (ValueMap.get(tmpKeyword) != null) {
                allValue = (Double) ValueMap.get(tmpKeyword);
            }

            //allValue = allValue + (tmpFreq * tmpRatio);
            allValue = tmpFreq;
            //sumFreq += allValue;

            ValueMap.put(tmpKeyword, allValue);
        }
        //System.out.println("#res:" + ValueMap.toString());

        ValueComparator vc = new ValueComparator(ValueMap);
        Map<String, Double> sortedMap = new TreeMap<String, Double>(vc);

        sortedMap.putAll(ValueMap);
        //sortedMap.put("SUMFREQ", sumFreq);

        return sortedMap;
    }

    /* STEP09 분석 작업 - sub2 일부
    * 주어진 KeywordMapList와 metaKeywordMapping 대조하여 결과 MapList 리턴
     */
    public Map<String, Object> getListMappingMetaKeywords(
                                                    HashMap<String, Double> keywordMapList
                                                    , List<MetaKeywordMapping> metaKeywordList    ) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        // 매타태그 - keyword 매핑 된 결과리스트
        List<HashMap<String, Object>> metaMappingKeywordList = new ArrayList<>();

        // 매타태그 - keyword 미분류 키워드 리스트
        HashMap<String, Double> notTaggedKeywordList = (HashMap<String, Double>) keywordMapList.clone();

        // 메타태그 - value 매핑 맵 결과리스트
        //HashMap<String, Integer> metaValueMap = new HashMap<String, Integer>();

        for (MetaKeywordMapping meta : metaKeywordList) {
            if (meta.getKeyword() != null && meta.getTag() != null && meta.getRatio() != null) {
                String metaTag = meta.getTag();
                String metaKeyword = meta.getKeyword();
                double metaRatio = meta.getRatio();
                //metaValueMap.put(metaTag, 0);

                Set entrySet = keywordMapList.entrySet();
                Iterator lt = entrySet.iterator();

                while(lt.hasNext()){
                    Map.Entry key1 = (Map.Entry)lt.next();
                    String tmpKey = key1.getKey().toString();
                    Double tmpValue = (Double) key1.getValue();

                    // 메타-키워드 매핑과 현재 키워드가 동일하면 태그에 freq * ratio 값을 매핑하여 더해준다
                    if(tmpKey.equals(metaKeyword)) {
                        HashMap<String, Object> tmpMap = new HashMap<String, Object>();
                        tmpMap.put("keyword", tmpKey);
                        tmpMap.put("keywordFreq", tmpValue);
                        tmpMap.put("metaKeyword", metaKeyword);
                        tmpMap.put("metaTag", metaTag);
                        tmpMap.put("metaRatio", metaRatio);

                        System.out.println("#metaMappingKeyList.add:"+tmpMap.toString());
                        metaMappingKeywordList.add(tmpMap);
                        notTaggedKeywordList.remove(tmpKey);
                    }
                }
            }
        }
        //return metaMappingKeyList;
        resultMap.put("metaMappingKeywordList", metaMappingKeywordList);
        resultMap.put("notMetaTaggedKeywordList", notTaggedKeywordList);
        return resultMap;
    }

    /* STEP09 분석 작업 - sub2 일부
    * 메타태그-Keyword매핑 리스트에서 각 매타태그 별 value를 계산한 SortedMap으로 리턴한다.
     */
    public Map<String, Double> getMetaTagValueMap(List<HashMap<String,Object>> metaMappingKeyList) {
        HashMap<String, Double> metaValueMap = new HashMap<String, Double>();
        for (HashMap<String, Object> item : metaMappingKeyList) {
            String tmpMetaTag = (String) item.get("metaTag");
            double tmpFreq = (Double) item.get("keywordFreq");
            double tmpRatio = (Double) item.get("metaRatio");

            double allValue = 0.0;

            if (metaValueMap.get(tmpMetaTag) != null) {
                allValue = (Double) metaValueMap.get(tmpMetaTag);
            }

            allValue = allValue + (tmpFreq * tmpRatio);

            metaValueMap.put(tmpMetaTag, allValue);
        }
        //System.out.println("#res:" + metaValueMap.toString());

        ValueComparator vc = new ValueComparator(metaValueMap);
        Map<String, Double> sortedMap = new TreeMap<String, Double>(vc);

        sortedMap.putAll(metaValueMap);

        return sortedMap;
    }

    /* dicKeywords 사전 Tag 리스트 */
    private List<String> getDicTypes() {
        List<String> dicTypes = new ArrayList();
        dicTypes.add("EMOTION");
        dicTypes.add("WHAT");
        dicTypes.add("WHEN");
        dicTypes.add("WHERE");
        dicTypes.add("WHO");
        return dicTypes;
    }

    /* STEP09 분석 sub - sub1 type별 분류 키워드 조회

     */
    @Override
    public Map<String, Object> step09_sub_01_getKeywordsByType(
            List<HashMap<String, Object>> resultMap
            , HashMap<String, Object> keywordResult
            , HashMap<String, Object> joinResult
            , HashMap<String, Double> keywordMapList) {
        Map<String, Object> resultMapObj = new HashMap<String, Object>();
        if (resultMap == null) resultMap = new ArrayList<HashMap<String,Object>>();

        // 메타태그-키워드 매핑 type 조회
        //List<String> keywordTypes = dicKeywordsMapper.getKeywordTypes();
        List<String> keywordTypes = this.getDicTypes();
        //System.out.println("#STEP09-1-sub:: keywordTypes:"+keywordTypes.toString());

        HashMap<String, Object> keywordMappingResult = null;
        Map<String, Double> notKeywordMappingResult = null;
        JsonArray notMapKeywords = null;
        Map<String, Double> keywordValuesMap = null;

        HashMap<String, Map<String, Double>> keywordValuesMapList = new HashMap<String, Map<String, Double>>();
        List<HashMap<String,Object>> mappingKeywordRatioList = null;

        JsonObject keywordValuesMapByType = null;

        for(String keywordType : keywordTypes) {
            HashMap<String,Object> tmpMap = new HashMap<String,Object>();

            // [타입별] 키워드 매핑 사전 리스트 조회
            DicKeywords reqKeyword = new DicKeywords();
            //reqMeta.setType("genre");
            reqKeyword.setType(keywordType);
            List<DicKeywords> dicKeywordList = dicKeywordsMapper.getDicKeywordsList(reqKeyword);

            // [타입별] keyword 매핑 된 결과리스트 && 미분류 키워드 리스트
            keywordMappingResult = (HashMap<String, Object>) getListMappingKeywords(keywordMappingResult, keywordMapList, dicKeywordList);
            List<HashMap<String,Object>> mappingKeywordList = null ;
            Map<String, Double> notKeywordMappingResultOrig = null;

            if (keywordMappingResult != null) {
                //System.out.println("# keywordMappingResult:"+keywordMappingResult.toString());

                if (keywordMappingResult.get("mappingKeywordList") != null) {
                    mappingKeywordList = (List<HashMap<String, Object>>) keywordMappingResult.get("mappingKeywordList");
                    //System.out.println("#mappingKeywordList:"+mappingKeywordList.toString());
                }
                if (keywordMappingResult.get("notTaggedKeywordList") != null) {
                    notKeywordMappingResultOrig = (HashMap<String, Double>) keywordMappingResult.get("notTaggedKeywordList");
                }
                /*
                if (keywordMappingResult.get("mappingKeywordRatioList") != null) {
                    mappingKeywordRatioList = (List<HashMap<String, Object>>) keywordMappingResult.get("mappingKeywordRatioList");
                    System.out.println("#mappingKeywordRatioList:"+mappingKeywordRatioList.toString());
                }
                */

            }
            //System.out.println("#mappingKeywordList:"+mappingKeywordList.toString());
            tmpMap.put("mappingKeywordList", mappingKeywordList);

            // [타입별] 매핑 키워드 리스트를 freq 역순으로 정렬  /  미분류 키워드 리스트를 freq 역순 정렬
            keywordValuesMap = getKeywordValueMap(mappingKeywordList);
            //System.out.println("#keywordValuesMap:"+keywordValuesMap.toString());
            //tmpMap.put("keywordValuesMap", keywordValuesMap);

            // sumFreq 계산을 위해 keywordValuesMap을 list에 보관
            keywordValuesMapList.put(keywordType, keywordValuesMap);


            // [장르] 미뷴류 키워드 리스트 freq 역순 정렬 후 100개만 잘라서 보관
            Map<String, Double> notKeywordMappingResult3 = MapUtil.getSortedDescMapForDouble(notKeywordMappingResultOrig);
            Map<String, Double> notKeywordMappingResult2 = MapUtil.getCuttedMapFromMapByLimit(notKeywordMappingResult3, 100);
            notKeywordMappingResult = MapUtil.getSortedDescMapForDouble(notKeywordMappingResult2);
            notMapKeywords = MapUtil.getListNotMapKeywords(notKeywordMappingResult);
            //tmpMap.put("notTaggedKeywordList", notTaggedKeywordList);

            // [장르] 메타태그 valueMap에서 주어진 개수만큼 KEY만 뽑아서 결과로 사용
            String keyResult = StringUtil.getKeySringFromMapByCount(keywordValuesMap, 5);
            tmpMap.put("result", keyResult);

            resultMap.add(tmpMap);

            keywordResult.put(keywordType, keyResult);
            joinResult.put(keywordType, keyResult);
        }

        // ITEMS_TAGS_META 저장을 위해 TYPE별 키워드 리스트 10개씩 끊어서 point 계산한 jsonArray의 JsonObject를 리턴
        // 5개로 변경 2018.03.19
        JsonObject keywordPointArraysObj = getKeywordPointArraysObj(keywordValuesMapList, keywordTypes);
        System.out.println("#keywordPointArraysObj:"+keywordPointArraysObj.toString());

        resultMapObj.put("resultMap", resultMap);
        resultMapObj.put("keywordResult", keywordResult);
        resultMapObj.put("result", joinResult);
        resultMapObj.put("notKeywordMappingResult", notKeywordMappingResult);
        resultMapObj.put("notMapKeywords", notMapKeywords);
        resultMapObj.put("keywordPointArraysObj", keywordPointArraysObj);

        return resultMapObj;
    }

    /*
    ITEMS_TAGS_META 저장을 위해 TYPE별 키워드 리스트 10개씩 끊어서 point 계산한 jsonArray의 JsonObject를 리턴
     */
    private JsonObject getKeywordPointArraysObj(HashMap<String, Map<String, Double>> keywordValuesMapList, List<String> keywordTypes) {
        JsonObject result = new JsonObject();

        if (keywordValuesMapList != null) {

            // 다운포인트 구현을 위해 매핑된 전체 키워드의 freq를 합산한다.
            double sumFreq = 0.0;
            double numWords = 0.0;

            for (String keywordType : keywordTypes) {
                if (keywordValuesMapList.get(keywordType) != null) {
                    // Type별 freq 역순 정렬 후 10개만 잘라서 보관
                    // 5개로 변경 2018.03.19
                    Map<String, Double> keyMap3 = MapUtil.getSortedDescMapForDouble(keywordValuesMapList.get(keywordType));
                    JsonArray keyMap2 = MapUtil.getCuttedMapPointsFromMapByLimit(keyMap3, 5);

                    //System.out.println("#keywordPointArrayMap:"+keyMap2.toString());
                    result.add(keywordType, keyMap2);
                }
            }
        }
        return result;
    }

    /* STEP09 분석 sub - sub2 type별 메타-태그 조회

     */
    @Override
    public Map<String, Object> step09_sub_02_getMetaByType(
            List<HashMap<String, Object>> resultMap
            , HashMap<String, Object> metaResult
            , HashMap<String, Object> joinResult
            , HashMap<String, Double> keywordMapList) {
        Map<String, Object> resultMapObj = new HashMap<String, Object>();
        if (resultMap == null) resultMap = new ArrayList<HashMap<String,Object>>();

        // 메타태그-키워드 매핑 type 조회
        List<String> metaTypes = metaKeywordMappingMapper.getMetaTypes();

        for(String metaType : metaTypes) {
            HashMap<String,Object> tmpMap = new HashMap<String,Object>();

            // [장르] 메타태그-키워드 매핑 사전 리스트 조회
            MetaKeywordMapping reqMeta = new MetaKeywordMapping();
            //reqMeta.setType("genre");
            reqMeta.setType(metaType);
            List<MetaKeywordMapping> metaKeywordList = metaKeywordMappingMapper.getMetaKeywordMappingList(reqMeta);

            // [장르] 매타태그-keyword 매핑 된 결과리스트 && 미분류 키워드 리스트
            HashMap<String, Object> metaMappingResult = (HashMap<String, Object>) getListMappingMetaKeywords(keywordMapList, metaKeywordList);
            List<HashMap<String,Object>> metaMappingKeywordList = null ;
            HashMap<String, Double> notTaggedKeywordListOrig = null;
            if (metaMappingResult != null) {
                if (metaMappingResult.get("metaMappingKeywordList") != null) {
                    metaMappingKeywordList = (List<HashMap<String, Object>>) metaMappingResult.get("metaMappingKeywordList");
                }
                if (metaMappingResult.get("notMetaTaggedKeywordList") != null) {
                    notTaggedKeywordListOrig = (HashMap<String, Double>) metaMappingResult.get("notMetaTaggedKeywordList");
                }
            }
            //System.out.println("#result:"+metaMappingKeyList.toString());
            tmpMap.put("metaMappingKeywordList", metaMappingKeywordList);

            // [장르] 메타태그-키워드 리스트를 freq 역순으로 정렬  /  미분류 키워드 리스트를 freq 역순 정렬
            Map<String, Double> metaValuesMap = getMetaTagValueMap(metaMappingKeywordList);
            //System.out.println("#res:"+metaValuesMap.toString());
            tmpMap.put("metaValuesMap", metaValuesMap);

            // [장르] 미뷴류 키워드 리스트 freq 역순 정렬 후 50개만 잘라서 보관
            Map<String, Double> notTaggedKeywordList3 = MapUtil.getSortedDescMapForDouble(notTaggedKeywordListOrig);
            Map<String, Double> notTaggedKeywordList2 = MapUtil.getCuttedMapFromMapByLimit(notTaggedKeywordList3, 50);
            Map<String, Double> notTaggedKeywordList = MapUtil.getSortedDescMapForDouble(notTaggedKeywordList2);
            tmpMap.put("notMetaTaggedKeywordList", notTaggedKeywordList);

            // [장르] 메타태그 valueMap에서 주어진 개수만큼 KEY만 뽑아서 결과로 사용
            String keyResult = StringUtil.getKeySringFromMapByCount(metaValuesMap, 3);
            tmpMap.put("result", keyResult);

            resultMap.add(tmpMap);

            metaResult.put(metaType, keyResult);
            joinResult.put(metaType, keyResult);
        }

        resultMapObj.put("resultMap", resultMap);
        resultMapObj.put("metaResult", metaResult);

        if (resultMapObj.get("result") != null) resultMapObj.remove("result");
        resultMapObj.put("result", joinResult);

        return resultMapObj;
    }

    /* STEP09 분석 작업 - sub
    */
    @Override
    public JsonObject step09sub(SchedTrigger req) {
        JsonObject result = null;

        if(req != null && req.getContentOne() != null) {
            logger.info("#STEP:09:: get start! getContentOne: getStmh_id:" + req.getContentOne().getStmh_id());
            try {
                String content = (req.getContentOne().getContent() != null) ? (req.getContentOne().getContent()) : "";
                System.out.println("#STEP:09:: getContentOne.size:"+content.length());

                // 버리는 문자열/특수기호 제거
                //content = CommonUtil.removeTex(content);

                // 결과 처리를 위한 map
                List< HashMap<String,Object>> resultMap = null;
                HashMap<String,Object> metaResult = null;
                HashMap<String,Object> keywordResult = null;
                HashMap<String,Object> joinResult = null;
                JsonArray notMapKeywords = null;
                Map<String, Double> keywordValuesMap = null;
                JsonObject keywordPointArraysObj = null;

                Map<String, Double> notKeywordMappingResult = null;

                // 컨텐츠를 읽어와서 List<HashMap<String, Double>> 형태로 변환
                HashMap<String, Double> keywordMapList = StringUtil.getMapArrayFromStringSeperatedComma(content);
                //System.out.println("#STEP:09:: keywordMapList:"+keywordMapList.toString());

                // 메타태그-키워드 매핑 type별 조회 결과 리턴
                Map<String, Object> resultKeywordMap = step09_sub_01_getKeywordsByType(resultMap,  new HashMap<String,Object>()
                                                                                    ,  new HashMap<String,Object>(), keywordMapList);
                //System.out.println("#STEP:09:: resultKeywordMappingMap:"+resultKeywordMap.toString());

                if (resultKeywordMap != null) {
                    if(resultKeywordMap.get("resultMap") != null) resultMap = (List<HashMap<String, Object>>) resultKeywordMap.get("resultMap");
                    if(resultKeywordMap.get("keywordResult") != null) keywordResult = (HashMap<String, Object>) resultKeywordMap.get("keywordResult");
                    if(resultKeywordMap.get("result") != null) joinResult = (HashMap<String, Object>) resultKeywordMap.get("result");
                    if(resultKeywordMap.get("notKeywordMappingResult") != null) notKeywordMappingResult = (Map<String, Double>) resultKeywordMap.get("notKeywordMappingResult");
                    if(resultKeywordMap.get("notMapKeywords") != null) notMapKeywords = (JsonArray) resultKeywordMap.get("notMapKeywords");
                    if(resultKeywordMap.get("keywordValuesMap") != null) keywordValuesMap = (Map<String, Double>) resultKeywordMap.get("keywordValuesMap");
                    if(resultKeywordMap.get("keywordPointArraysObj") != null) keywordPointArraysObj = (JsonObject ) resultKeywordMap.get("keywordPointArraysObj");
                }

                // 메타태그-키워드 매핑 type별 조회 결과 리턴
                /*
                Map<String, Object> resultMetaMap = step09_sub_02_getMetaByType(resultMap,  new HashMap<String,Object>()
                                                                                    , joinResult, keywordMapList);
                if (resultMetaMap != null) {
                    if(resultMetaMap.get("resultMap") != null) resultMap = (List<HashMap<String, Object>>) resultMetaMap.get("resultMap");
                    if(resultMetaMap.get("metaResult") != null) metaResult = (HashMap<String, Object>) resultMetaMap.get("metaResult");
                    if(resultKeywordMap.get("result") != null) joinResult = (HashMap<String, Object>) resultKeywordMap.get("result");
                }

                System.out.println("#STEP09: resultMap:"+resultMap.toString());

                ObjectMapper objectMapper = new ObjectMapper();
                String metaResultStr = objectMapper.writeValueAsString(metaResult);
                */
                // 결과 저장을 위해  추가 to sched_target_content
                joinResult.put("keywordResult", keywordResult);
                joinResult.put("notKeywordMappingResult",notKeywordMappingResult);
                joinResult.put("keywordValuesMap", keywordValuesMap);
                //joinResult.put("keywordPointArraysObj", keywordPointArraysObj);
                System.out.println("#STEP09:sub::result:"+joinResult.toString());

                String resultMapJsonArr = new Gson().toJson(resultMap);
                String metaResultJsonObj = new Gson().toJson(metaResult);
                String keywordResultJsonObj = new Gson().toJson(keywordResult);
                String joinResultJsonObj = new Gson().toJson(joinResult);
                String notKeywordMappingResultObj = new Gson().toJson(notKeywordMappingResult);
                JsonParser jsonParser = new JsonParser();
                JsonObject notKeywordMappingObj = (JsonObject) jsonParser.parse(notKeywordMappingResultObj);

                // 결과 처리
                result = new JsonObject();
                result.addProperty("rt_code","OK");
                result.addProperty("rt_msg","SUCCESS");
                result.addProperty("resultMap", resultMapJsonArr);
                //result.addProperty("result", metaResultJsonObj);
                result.addProperty("result", joinResultJsonObj);
                result.addProperty("metaResult", metaResultJsonObj);
                result.addProperty("keywordResult", keywordResultJsonObj);
                result.add("notKeywordMappingResult", notKeywordMappingObj);
                result.add("notMapKeywords", notMapKeywords);
                result.add("keywordPointArraysObj", keywordPointArraysObj);
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonObject());
                result.addProperty("rt_code", "FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));

                e.printStackTrace();
            }

            logger.info("#STEP:09:: end! url:" + req.getSc_id());
        } else {
            result = new JsonObject();
            result.add("result", new JsonObject());
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg","STEP09 get start fail! params error!");
            logger.info("#STEP:09:: get start fail! params error!:"+req.toString());
        }

        return result;
    }

    /* STEP09 분석 수행 - main
     * SchedTrigger.contentList 를 읽어들여서 META_KEYWORD_MAPPING과 대조하여 TAG 추출한다
    */
    public JsonObject run_step09(SchedTrigger sched, int sc_id, String type, int tcnt) {
        String statTarget = "F";
        String content = "";

        JsonObject resultAnalyze = null;
        JsonArray notMapKeywords = null;
        JsonObject notMapKeywordList = null;

        try {
            System.out.println("#STEP:09: analyze start for sc_id:"+sc_id);
            // #TODO ste09sub
            resultAnalyze = this.step09sub(sched);

            //결과 코드를 만든다.
            int resultContentSize = 0;
            String contentAll = "";

            SchedTargetMappingHist reqHist = null;
            if (resultAnalyze != null && resultAnalyze.get("result") != null) {
                resultContentSize = resultAnalyze.get("result").getAsString().length();

                // 분석 컨텐츠의 사이즈가 100 미만일 경우 실패처리
                if (resultContentSize > 100) statTarget = "S";
                contentAll = resultAnalyze.get("result").getAsString();

                notMapKeywords = (JsonArray) resultAnalyze.get("notMapKeywords");
                notMapKeywordList = (JsonObject) resultAnalyze.get("notKeywordMappingResult");
                //System.out.println("#STEP09: notMapKeywordsMap:"+notMapKeywords.toString());
                //JsonObject histObj = (JsonObject) resultAnalyze.get("result");
                //resultAnalyze.remove("result");

                /*
                JsonObject newHistSummary = new JsonObject();
                newHistSummary.addProperty("rt_code", resultAnalyze.get("rt_code").getAsString());
                newHistSummary.addProperty("rt_msg", resultAnalyze.get("rt_msg").getAsString());

                newHistSummary.addProperty("result", resultAnalyze.get("result").getAsString());
                String wordsAndPumsa = resultAnalyze.get("wordsAndPumsa").getAsString();
                int resultSize = resultAnalyze.get("result").getAsString().length();
                int trimSize = 7900 - resultSize;
                newHistSummary.addProperty("wordsAndPumsa", (wordsAndPumsa != null) ? CommonUtil.removeTex(wordsAndPumsa.toString()).substring(0,trimSize) : "");
                */

                // (수정) 수집 결과를 sched_target_mapping_hist 에 저장
                reqHist = new SchedTargetMappingHist();
                reqHist.setSc_id(sc_id);
                reqHist.setType(type);
                reqHist.setTcnt(tcnt);
                reqHist.setTg_id(0);
                if (resultAnalyze != null && resultAnalyze.get("rt_code") != null)
                    reqHist.setRt_code(resultAnalyze.get("rt_code").getAsString());
                if (resultAnalyze != null && resultAnalyze.get("rt_msg") != null)
                    reqHist.setRt_msg(resultAnalyze.get("rt_msg").getAsString());
                reqHist.setStat(statTarget);
                //#TODO 용량 문제로 일시 제외
                //reqHist.setSummary(resultAnalyze.toString());
            }
            System.out.println("#STEP09 sc_id:"+sc_id+" insSchedTargetMappingHist from data:"+reqHist.toString());
            int rt = schedTargetMappingHistMapper.insSchedTargetMappingHist(reqHist);
            System.out.println("#STEP09 sc_id:"+sc_id+" insSchedTargetMappingHist result:"+rt+"/stmh_id:"+reqHist.getStmh_id());

            if (rt > 0 && reqHist.getStmh_id() != null) {
                // (수정) 수집 컨텐츠를 conf_target_content 에 저장
                SchedTargetContent reqCont = new SchedTargetContent();
                reqCont.setSc_id(sc_id);
                reqCont.setTg_id(0);
                reqCont.setStmh_id(reqHist.getStmh_id());
                reqCont.setContent(contentAll);

                logger.debug("#STEP09 sc_id:"+sc_id+" insSchedTargetContent from data:"+reqCont.toString());
                int rt2 = schedTargetContentMapper.insSchedTargetContent(reqCont);
                System.out.println("#STEP09 sc_id:"+sc_id+" insSchedTargetContent result:"+rt2);
            }

            // 미분류 키워드를 dic_notmap_keywords 에 등록, dic_keywords 사전에 등록된 키워드는 제외
            int rtinmk = dicService.insNotMapKeywords(notMapKeywords);

            // 미분류 키워드를 ITEMS_TAGS_METAS에 LIST_NOT_MAPPED mtype으로 저장
            int rtitmnm = insItemsTagsMetaFromNotMappedKeywordsObj(notMapKeywordList, sc_id);

            // TYPE별 키워드리스트+포인트+Freq를 ITEMS_TAGS_METAS에 저장
            if (resultAnalyze.get("keywordPointArraysObj") != null) {
                JsonObject keywordPointArraysObj = (JsonObject) resultAnalyze.get("keywordPointArraysObj");

                int rtinsmeta = insItemsTagsMetaFromPointArraysObj(keywordPointArraysObj, sc_id);
            }

            // SNS감성어를 ITEMS_TAGS_METAS에 WORDS_SNS type으로 저장
            //JsonObject resultRefineObj = resultRefine.get("result").getAsJsonObject();
            //if (resultRefineObj != null) {

            String movietitle = sched.getMovietitle();
            if (!"".equals(movietitle)) {
                JsonArray wordsSnsArray = wordsSnsArray = apiService.getSnsKeywords(movietitle);
                if (wordsSnsArray == null) wordsSnsArray = new JsonArray();

                int rtws = insItemsTagsMetaFromWordsSns(wordsSnsArray, sc_id);
            }

            //}

        } catch (Exception e) {
            statTarget = "F";

            SchedTargetMappingHist reqHist = new SchedTargetMappingHist();
            reqHist.setSc_id(sc_id);
            reqHist.setType(type);
            reqHist.setTcnt(tcnt);
            reqHist.setTg_id(0);
            if (resultAnalyze != null && resultAnalyze.get("rt_code") != null) reqHist.setRt_code(resultAnalyze.get("rt_code").getAsString());
            if (resultAnalyze != null && resultAnalyze.get("rt_msg") != null) {
                reqHist.setRt_msg(resultAnalyze.get("rt_msg").getAsString()
                        + "_" + (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));
            }
            reqHist.setStat(statTarget);
            //reqHist.setContent(content);

            System.out.println("#STEP09 sc_id:"+sc_id+" fail to SchedTargetMappingHist data:"+reqHist.toString());

            e.printStackTrace();
            return resultAnalyze;
        }
        return resultAnalyze;
    }

    private int insItemsTagsMetaFromNotMappedKeywordsObj(JsonObject notmappedkeywordsObj, int sc_id) {
        int rt = 0;

        Map<String, Double> notmapKeywordList = new HashMap<String, Double>();
        JsonArray result = new JsonArray();

        if (notmappedkeywordsObj != null) {
            int itemIdx = 0;
            int maxTagidx = 0;
            try {
                // 일단 sc_id를 통해 itemIdx를 취득
                ItemsSchedMapping reqIsm = new ItemsSchedMapping();
                reqIsm.setSc_id(sc_id);
                itemIdx = itemsSchedMappingMapper.getItemIdxByScid(reqIsm);

                // items_tags_keys를 통해 itemIdx - tagIdx 를 최신으로 추가
                maxTagidx = itemsTagsService.getCurrTagsIdxForInsert(itemIdx);

                //  ITEMS_TAGS_META에 LIST_NOT_MAPPED mtype으로 insert
                //System.out.println("#notmappedkeywordsObj:"+notmappedkeywordsObj.toString());

                Iterator<String> keysItr = notmappedkeywordsObj.keySet().iterator();
                while (keysItr.hasNext()) {
                    String key1 = keysItr.next();
                    JsonElement value = notmappedkeywordsObj.get(key1);
                    double value1 = value.getAsDouble();

                    JsonObject newItem = new JsonObject();
                    newItem.addProperty("word", key1);
                    newItem.addProperty("ratio", value1);
                    newItem.addProperty("type", "new");
                    result.add(newItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //System.out.println("#notmappedKeywordsArray:"+result.toString());
            rt = saveItemsTagsMetas(result.toString(), itemIdx, maxTagidx, "LIST_NOT_MAPPED");

        }

        return rt;
    }

    private int insItemsTagsMetaFromPointArraysObj(JsonObject keywordPointArraysObj, int sc_id) {
        int rt = 0;

        Map<String, Double> allKeywordList = new HashMap<String, Double>();

        if (keywordPointArraysObj != null) {
            // 일단 sc_id를 통해 itemIdx를 취득
            ItemsSchedMapping reqIsm = new ItemsSchedMapping();
            reqIsm.setSc_id(sc_id);
            int itemIdx = itemsSchedMappingMapper.getItemIdxByScid(reqIsm);

            // items_tags_keys를 통해 itemIdx - tagIdx 를 최신으로 추가
            int maxTagidx = itemsTagsService.getCurrTagsIdxForInsert(itemIdx);

            // 메타태그-키워드 매핑 type 조회
            //List<String> keywordTypes = dicKeywordsMapper.getKeywordTypes();
            List<String> keywordTypes = this.getDicTypes();

            // TYPE별 키워드-포인트-freq Arrays Object를 각각 Array로 구분하여 ITEMS_TAGS_META에 mtype 구분으로 insert
            //System.out.println("#keywordPointArraysObj2:"+keywordPointArraysObj.toString());
            for (String keytype : keywordTypes) {
                if (keywordPointArraysObj.get(keytype) != null) {
                    //System.out.println("#keywordPointArray:" + keywordPointArraysObj.get(keytype).toString());
                    // 타입 별 freq 상위 키워드 10개씩 jsonArray 저장
                    int resitm1 = saveItemsTagsMetas(keywordPointArraysObj.get(keytype).toString(), itemIdx, maxTagidx, "METAS"+keytype);
                }
                JsonArray curArr = (JsonArray) keywordPointArraysObj.get(keytype);

                for (JsonElement je : curArr) {
                    JsonObject jo = (JsonObject) je;

                    String tmpKey = jo.get("word").getAsString();
                    Double tmpValue = jo.get("freq").getAsDouble();
                    allKeywordList.put(tmpKey, tmpValue);
                }
            }

            Map<String, Double> searchKeyword3 = MapUtil.getSortedDescMapForDouble(allKeywordList);
            Map<String, Double> searchKeyword2 = MapUtil.getCuttedMapFromMapByLimit(searchKeyword3, 3);
            Map<String, Double> searchKeyword1 = MapUtil.getSortedDescMapForDouble(searchKeyword2);
            JsonArray searchKeyword  = MapUtil.getListNotMapKeywords(searchKeyword1);
            //System.out.println("#searchKeyword:"+searchKeyword.toString());

            // 검색 키워드 jsonArray mtype:LIST_SEARCHKEYWORDS 저장
            int resitm2 = saveItemsTagsMetas(searchKeyword.toString(), itemIdx, maxTagidx, "LIST_SEARCHKEYWORDS");
        }

        return rt;
    }

    private int insItemsTagsMetaFromWordsSns(JsonArray wordsSnsArr, int sc_id) {
        int rt = 0;

        if (wordsSnsArr != null) {
            int itemIdx = 0;
            int maxTagidx = 0;
            try {
                // 일단 sc_id를 통해 itemIdx를 취득
                ItemsSchedMapping reqIsm = new ItemsSchedMapping();
                reqIsm.setSc_id(sc_id);
                itemIdx = itemsSchedMappingMapper.getItemIdxByScid(reqIsm);

                // items_tags_keys를 통해 itemIdx - tagIdx 를 최신으로 추가
                maxTagidx = itemsTagsService.getCurrTagsIdxForInsert(itemIdx);
            } catch (Exception e) {
                e.printStackTrace();
            }

            rt = saveItemsTagsMetas(wordsSnsArr.toString(), itemIdx, maxTagidx, "WORDS_SNS");
        }

        return rt;
    }

    private int saveItemsTagsMetas(String metas, int itemIdx, int tagidx, String mtype) {
        ItemsTags itm = new ItemsTags();
        itm.setIdx(itemIdx);
        itm.setTagidx(tagidx);
        itm.setMtype(mtype);
        itm.setMeta(metas);
        int itm1 = itemsTagsService.insItemsTagsMetas(itm);

        return itm1;
    }
}
