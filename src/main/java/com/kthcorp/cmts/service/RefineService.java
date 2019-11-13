package com.kthcorp.cmts.service;

import com.google.gson.*;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RefineService implements RefineServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(RefineService.class);

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
    private StepService stepService;
    @Autowired
    private DicService dicService;
    @Autowired
    private SchedTriggerHistMapper schedTriggerHistMapper;
    @Autowired
    private ConfTargetMapper confTargetMapper;
    @Autowired
    private ItemsSchedMappingMapper itemsSchedMappingMapper;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private ApiService apiService;

    @Value("${property.serverid}")
    private String serverid;

    /* 올레TV 메타 확장 - 정제 수행 STEP04~06
     */
    @Override
    public int ollehTvMetaRefineScheduleCheck() throws Exception {
        logger.info("#ollehTvMetaRefineScheduleCheck start");
        int rtcode = 0;
        // STEP04 : 정제대상 중 등록일 상위 50개를 읽어옴
        List<SchedTrigger> schedList = this.step04();

        if (schedList != null && schedList.size() > 0) {
            logger.info("#STEP:04:: schedList:" + schedList.toString());
            logger.info("#STEP:05:: searching refine-target ConfTarget by schedList! count:" + schedList.size());
            for (SchedTrigger sched : schedList) {
                String rt_stat = "F";

                // STEP05 진행 전 스케쥴 stat = P 로 변경
                if (sched != null) {
                    int sc_id = sched.getSc_id();
                    int itemIdx = sched.getItemIdx();
                    String type = sched.getType();
                    int tcnt = (sched.getTcnt() != null) ? sched.getTcnt() : 0;
                    String movietitle = sched.getMovietitle();
                    System.out.println("#STEP05 before STEP06 update tcnt:: from:"+sched.getTcnt());
                    //tcnt = tcnt + 1;
                    sched.setTcnt(tcnt);

                    //items_hist에 등록 for 통계
                    int rthist = itemsService.insItemsHist(itemIdx, "refine", "R", movietitle, "START_REFINE", sc_id);

                    //System.out.println("## uptSchedTriggerForCollectStep02 params:"+sched.toString());
                    // STEP06 실행 전 sched_trigger의 stat를 P로 업데이트
                    int rts = stepService.uptSchedTriggerForRefineStep05(sched);

                    // STEP05 : 가져온 리스트에서 개별 타겟리스트의 설정에 따라 수집 파라미터 조회 bypass
                    //ConfTarget tg = this.step05(target);

                    // STEP06 : 정제 처리 후 히스토리 저장
                    // STEP06 : 조회된 설정에 맞는 Refine 분리 시작 - ThreadPool로 전달
                    logger.info("#STEP:06:: refine start! by SchedTrigger info!");
                    JsonObject resultRefine = run_step06(sched, sc_id, type, sched.getTcnt());

                    // SchedTrigger 에 대한 히스토리 저장
                    SchedTriggerHist reqSth = new SchedTriggerHist();
                    reqSth.setSc_id(sc_id);
                    reqSth.setType(sched.getType());
                    reqSth.setTcnt(tcnt);

                    if (resultRefine != null && resultRefine.get("rt_code") != null && "OK".equals(resultRefine.get("rt_code").getAsString())) {
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

                    System.out.println("#STEP:06:: refine sched history writing! by "+reqSth.toString());
                    int rt1 = stepService.uptSchedTriggerProgsAfterRefineTargetOneProcess(sc_id, rt_stat);

                    //items_hist에 등록 for 통계
                    rthist = itemsService.insItemsHist(itemIdx, "refine", rt_stat, movietitle, "END_REFINE", sc_id);

                    logger.debug("#STEP:06:: refine sched history writing! by "+reqSth.toString());
                    // 수집 스케쥴 이력 저장
                    int rtsth = schedTriggerHistMapper.insSchedTriggerHist(reqSth);

                    // items_stat 저장
                    int rt_it_stat = itemsService.insItemsStatOne(itemIdx, "R", rt_stat);

                    if("S".equals(rt_stat)) {
                        // 추출 스케쥴 종료 후 성공일 경우 분석 스케쥴 등록
                        SchedTrigger newReq = new SchedTrigger();
                        newReq.setParent_sc_id(sc_id);
                        newReq.setType("A");
                        newReq.setDescript(sched.getDescript() + " 분석");
                        newReq.setRegid(serverid);

                        // 등록 전 기존 분석 스케쥴이 있으면 stat = Y 로 업데이트하여 중복 등록을 방지 added 2017-12-01 by jy.hwang
                        SchedTrigger oldRefineSched = schedTriggerMapper.getSchedTriggerOne(newReq);
                        int retryRt = 0;
                        if (oldRefineSched != null) {
                            retryRt = schedTriggerMapper.uptOldSchedTriggerRetry(newReq);
                            retryRt++;
                        }
                        if (retryRt < 2) {
                            // 등록 전 기존 분석 스케쥴의 stat = "O"로 변경
                            // setStat는 Old처리에만 사용
                            newReq.setStat("S");
                            int rto = schedTriggerMapper.uptSchedTriggerOldItemsByTypeStat(newReq);

                            // 먼저 sched_trigger에 등록
                            int rt = schedTriggerMapper.insSchedTriggerForStart(newReq);
                            System.out.println("# insert sc_id:" + newReq.getSc_id() + "   result:" + rt);

                            // items_sched_mapping에 등록
                            ItemsSchedMapping newISM = new ItemsSchedMapping();
                            newISM.setIdx(itemIdx);
                            newISM.setSc_id(newReq.getSc_id());
                            int rtISM = itemsSchedMappingMapper.insItemsSchedMapping(newISM);

                            // sched_trigger에 등록한 sc_id 와 stat = 'P'인 conf_target 리스트를 조합하여 sched_target_mapping 등록

                            ConfTarget req = new ConfTarget();
                            req.setPageNo(1);
                            req.setPageSize(20);
                            req.setOrderType("ASC");
                            req.setStat("Y");

                            List<SchedTargetMapping> targetList = schedTriggerMapper.getParentSchedTargetMapping(sc_id);
                            for (SchedTargetMapping stm : targetList) {
                                SchedTargetMapping reqM = new SchedTargetMapping();
                                reqM.setSc_id(newReq.getSc_id());
                                reqM.setTg_id(stm.getTg_id());
                                int rtM = schedTriggerMapper.insSchedTargetMapping(reqM);
                                System.out.println("# insert targetMapping sc_id:" + newReq.getSc_id()+" - "+stm.getTg_id() + "   result:" + rtM);
                            }

                            SchedTrigger result1 = schedTriggerMapper.getSchedTriggerById(newReq.getSc_id());
                            if (result1 != null) System.out.println("# getSchedTriggerById for A result:" + result1.toString());
                        }
                    }
                }
            }
        } else {
            logger.info("#ollehTvMetaRefineScheduleCheck end! schedule list is empty!");
        }
        return rtcode;
    }

    /*** 정제 작업 STEP04 ~ 06 ***/
    /* STEP04
     * SchedTrigger & targetList<ConfTarget> 통해 정제 스케쥴 50개 조회
    */
    @Override
    public List<SchedTrigger> step04() {

        System.out.println("#STEP04 for Refine!");
        SchedTrigger req = new SchedTrigger();
        req.setType("R");
        req.setStat("Y");

        List<SchedTrigger> result = schedTriggerMapper.get50ByTypeStatSchedTriggerList(req);
        List<SchedTrigger> result2 = new ArrayList<SchedTrigger>();
        for(SchedTrigger st : result) {
            // 스케쥴과 영화와 컨텐츠 연결하는 서브 쿼리 실행

            SchedTargetContent reqC = new SchedTargetContent();
            reqC.setSc_id(st.getParent_sc_id());
            reqC.setTcnt(st.getTcnt());
            List<SchedTargetContent> contentList = schedTargetContentMapper.getSchedTargetContentList(reqC);
            st.setContentList(contentList);
            result2.add(st);
            //System.out.println("#STEP04 for Refine! get schedTargetContent::"+contentList.toString());
        }
        System.out.println("#STEP04 for Refine! result:"+result2);
        return result2;
    }

    @Override
    public List<SchedTrigger> step04byScid(SchedTrigger req) {
        System.out.println("#STEP04 for Refine test! by sc_id:" + req.getSc_id());
        SchedTrigger item1 = schedTriggerMapper.getSchedTriggerOneByScid(req);

        List<SchedTrigger> result2 = new ArrayList<SchedTrigger>();


        SchedTargetContent reqC = new SchedTargetContent();
        reqC.setSc_id(req.getParent_sc_id());
        //reqC.setTcnt(req.getTcnt());
        List<SchedTargetContent> contentList = schedTargetContentMapper.getSchedTargetContentList(reqC);
        item1.setContentList(contentList);
        result2.add(item1);

        System.out.println("#STEP04 for Refine test! result:"+result2.toString());
        return result2;
    }

    /* STEP05
     * 정제 대상 설정 조회는 bypass
     * 조회 후 stat = P 로 변경
    */
    @Override
    public ConfTarget step05(ConfTarget req) throws Exception {
        ConfTarget result = null;
        /*
        if (req != null && req.getTg_id() != null) {

            result = confTargetMapper.getConfTargetById(req);
            logger.info("#STEP:05:: target: req.tg_id:"+req.getTg_id().toString()+" :: getConfTargetById:"+result.toString());
        } else {
            if (req!=null) {
                logger.info("#STEP:05::fail target is empty!");
            } else {
                logger.info("#STEP:05::fail target tg_url is empty!");
            }
        }
        */
        return result;
    }

    // 네이버 영화 가중치, CINE21 영화 가중치 추가
    private Map<String, Double> getAddedWordsFreqArrays(Map<String, Double> origMap, String subContentTxt, String tg_url) throws Exception {
        if (origMap == null) origMap = new HashMap();
        Map<String, Double> resultWordFreq = WordFreqUtil.getWordCountsMap2(subContentTxt);
        logger.debug("#ELOG.resultWordFreq orig:"+tg_url+"/ size:"+resultWordFreq.size()+"/datas::"+resultWordFreq.toString());

        Double mutexRatio = 0.0;
        if (tg_url.equals("NAVER_MOVIE") || tg_url.equals("CINE21_MOVIE")) mutexRatio = 500.0;

        Map<String, Double> resultWordFreqMutexByRatio = MapUtil.getParamDoubleMapMutexedRatio(resultWordFreq, mutexRatio);
        logger.debug("#ELOG.resultWordFreq:"+tg_url+"/ mutex: size:"+resultWordFreqMutexByRatio.size()+"/datas::"+resultWordFreqMutexByRatio.toString());

        Map<String, Double> resultMap = MapUtil.getAppendedMapAndParamDouble(origMap, resultWordFreqMutexByRatio);

        return resultMap;
    }

    /* STEP06 정제 작업 - sub
    */
    @Override
    public JsonObject step06sub(SchedTrigger req) throws Exception {
        JsonObject result = null;

        if(req != null && req.getContentList() != null) {
            logger.info("#STEP:06:: get start! contentList.size:" + req.getContentList().size());
            try {
                String content = "";
                ArrayList<String> s1 = null;
                Map<String, Double> resultWordFreq = null;

                // 형태소 분석 전 품사 추가대상 조회
                List<DicPumsaWords> pumsaList = dicPumsaWordsMapper.getDicPumsaWords();
                if (pumsaList != null) {
                    s1 = new ArrayList<String>();
                    for (DicPumsaWords dp : pumsaList) {
                        s1.add(dp.getWord());
                    }
                }

                for(SchedTargetContent sc : req.getContentList()) {
                    if (sc.getContent() != null && !"".equals(sc.getContent())) {
                        //content = content + " " + sc.getContent();
                        content = sc.getContent().trim();
                        String tg_url = (sc.getTg_url() != null) ? sc.getTg_url().trim() : "";
                        logger.info("#STEP:06:: SRC:"+tg_url+"/content.size:" + content.length());

                        // 버리는 문자열/특수기호 제거
                        //content = CommonUtil.removeTex(content);

                        // 형태소 분석 결과 중첩 modified at 2018.03.15
                        ArrayList<ArrayList<String>> wordsList = SeunjeonUtil.getSimpleWords2(content, null);
                        //System.out.println("#ELOG.wordsList.result:"+wordsList.toString());
                        content = "";

                        // 추가 품사 대상에 대해 형태소 분석 결과에 ADD
                        ArrayList<ArrayList<String>> resultArr = SeunjeonUtil.getArrayWordsForMatchClass2(wordsList, s1);
                        wordsList = null;
                        //String wordsAndPumsa = new Gson().toJson(resultArr);
                        //System.out.println("#STEP06: getArrayWordsForMatchClass2.result:"+resultArr.toString());

                        // 문자열 Array로 변형
                        List<String> subArray = new ArrayList();
                        for(List<String> ls : resultArr) {
                            if (ls != null && ls.get(3) != null) subArray.add(ls.get(3));
                        }

                        // 불용어 처리
                        List<String> subArray2 = dicService.filterListByDicNotuseWords(subArray, req.getSc_id());
                        subArray = null;

                        // 대체어 처리
                        List<String> subArray3 = dicService.filterListByDicChangeWords(subArray2, req.getSc_id());
                        subArray2 = null;

                        // 1글자 제외 로직 적용 added 2018-01-17
                        // _ 로 시작하면 2개품사 조합 중 첫째 단어가 공백인 경우 - 제외
                        List<String> subArray4 = dicService.filterListByLengthUnder2byte(subArray3);
                        subArray3 = null;

                        // 문자열 Array를 1개의 String으로 취합
                        String subContentTxt = StringUtil.getStringFromList(subArray4);
                        subArray4 = null;

                        // 버리는 문자열/특수기호 제거
                        subContentTxt = CommonUtil.removeTex(subContentTxt);

                        // WordFreq 추출 , 가중치 추가
                        //resultWordFreq = WordFreqUtil.getWordCountsMap2(subContentTxt);
                        resultWordFreq = this.getAddedWordsFreqArrays(resultWordFreq, subContentTxt, tg_url);

                        subContentTxt = "";
                    }
                }
                logger.debug("#ELOG.DEST.resultWordFreq orig:size:"+resultWordFreq.size()+"::datas::"+resultWordFreq.toString());

                // 추가어 처리 , WordFreq 변경
                Map<String, Double> resultAddedWordFreq = dicService.filterListByDicAddWords(resultWordFreq, req.getSc_id());
                resultWordFreq = null;

                // WordFreq 랭킹 산정
                Map<String, Double> resultWordFreqRank = MapUtil.sortByValue(resultAddedWordFreq);
                resultAddedWordFreq = null;
                //String resultFreqRank = new Gson(resultWordFreqRank);

                //result = imdbService.getMovie(req);
                result = new JsonObject();
                result.addProperty("rt_code","OK");
                result.addProperty("rt_msg","SUCCESS");
                //result.addProperty("wordsAndPumsa", resultArr.toString());
                //result.addProperty("subContent", subContentTxt);
                result.addProperty("result", String.valueOf(resultWordFreqRank));
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonArray());
                result.addProperty("rt_code", "FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));

                e.printStackTrace();
            }

            logger.info("#STEP:06:: end! url:" + req.getSc_id());
        } else {
            result = new JsonObject();
            result.add("result", new JsonArray());
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg","STEP06 get start fail! params error!");
            logger.info("#STEP:06:: get start fail! params error!:"+req.toString());
        }

        return result;
    }

    /* STEP06 정제 수행 - main
     * SchedTrigger.contentList 를 읽어들여서 형태소 분석 후 WordFreq 랭킹을 저장한다
    */
    public JsonObject run_step06(SchedTrigger sched, int sc_id, String type, int tcnt) {
        String statTarget = "F";
        String content = "";

        JsonObject resultRefine = null;
        try {
            System.out.println("#STEP:06: Refine start for sc_id:"+sc_id);
            resultRefine = this.step06sub(sched);

            //결과 코드를 만든다.
            int resultContentSize = 0;
            String contentAll = "";

            System.out.println("#STEP06 resultRefine::"+resultRefine.get("result").toString());

            SchedTargetMappingHist reqHist = null;
            if (resultRefine != null && resultRefine.get("result") != null) {
                resultContentSize = resultRefine.get("result").getAsString().length();

                // 정제 컨텐츠의 사이즈가 100 미만일 경우 실패처리
                if (resultContentSize > 100) {
                    statTarget = "S";
                } else {
                    statTarget = "F";
                }

                //System.out.println("#ELOG.resultRefine:: datas::"+resultRefine.toString());

                contentAll = resultRefine.get("result").getAsString();

                //JsonObject histObj = (JsonObject) resultRefine.get("result");
                //resultRefine.remove("result");

                /*
                JsonObject newHistSummary = new JsonObject();
                newHistSummary.addProperty("rt_code", resultRefine.get("rt_code").getAsString());
                newHistSummary.addProperty("rt_msg", resultRefine.get("rt_msg").getAsString());

                newHistSummary.addProperty("result", resultRefine.get("result").getAsString());
                String wordsAndPumsa = resultRefine.get("wordsAndPumsa").getAsString();
                int resultSize = resultRefine.get("result").getAsString().length();
                int trimSize = 7900 - resultSize;
                newHistSummary.addProperty("wordsAndPumsa", (wordsAndPumsa != null) ? CommonUtil.removeTex(wordsAndPumsa.toString()).substring(0,trimSize) : "");
                */

                // (수정) 수집 결과를 sched_target_mapping_hist 에 저장
                reqHist = new SchedTargetMappingHist();
                reqHist.setSc_id(sc_id);
                reqHist.setType(type);
                reqHist.setTcnt(tcnt);
                reqHist.setTg_id(0);
                if (resultRefine != null && resultRefine.get("rt_code") != null)
                    reqHist.setRt_code(resultRefine.get("rt_code").getAsString());
                if (resultRefine != null && resultRefine.get("rt_msg") != null)
                    reqHist.setRt_msg(resultRefine.get("rt_msg").getAsString());
                reqHist.setStat(statTarget);
                // 용량문제로 제외
                //reqHist.setSummary(resultRefine.toString());
            }
            System.out.println("#STEP06 sc_id:"+sc_id+" insSchedTargetMappingHist from data:"+reqHist.toString());
            int rt = schedTargetMappingHistMapper.insSchedTargetMappingHist(reqHist);
            System.out.println("#STEP06 sc_id:"+sc_id+" insSchedTargetMappingHist result:"+rt+"/stmh_id:"+reqHist.getStmh_id());

            if (rt > 0 && reqHist.getStmh_id() != null) {
                // (수정) 수집 컨텐츠를 conf_target_content 에 저장
                SchedTargetContent reqCont = new SchedTargetContent();
                reqCont.setSc_id(sc_id);
                reqCont.setTg_id(0);
                reqCont.setStmh_id(reqHist.getStmh_id());
                reqCont.setContent(contentAll);

                logger.debug("#STEP06 sc_id:"+sc_id+" insSchedTargetContent from data:"+reqCont.toString());
                int rt2 = schedTargetContentMapper.insSchedTargetContent(reqCont);
                System.out.println("#STEP06 sc_id:"+sc_id+" insSchedTargetContent result:"+rt2);
            }


        } catch (Exception e) {
            e.printStackTrace();

            statTarget = "F";

            SchedTargetMappingHist reqHist = new SchedTargetMappingHist();
            reqHist.setSc_id(sc_id);
            reqHist.setType(type);
            reqHist.setTcnt(tcnt);
            reqHist.setTg_id(0);
            if (resultRefine != null && resultRefine.get("rt_code") != null) reqHist.setRt_code(resultRefine.get("rt_code").getAsString());
            if (resultRefine != null && resultRefine.get("rt_msg") != null) {
                reqHist.setRt_msg(resultRefine.get("rt_msg").getAsString()
                        + "_" + (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));
            }
            reqHist.setStat(statTarget);
            //reqHist.setContent(content);

            System.out.println("#STEP06 sc_id:"+sc_id+" fail to SchedTargetMappingHist data:"+reqHist.toString());



            return resultRefine;
        }
        return resultRefine;
    }
}
