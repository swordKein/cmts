package com.kthcorp.cmts.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.service.crawl.*;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.JsonUtil;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CollectService implements CollectServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    @Autowired
    private ConfTargetMapper confTargetMapper;
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;
    @Autowired
    private SchedTargetMappingHistMapper schedTargetMappingHistMapper;
    @Autowired
    private SchedTargetMappingOrigMapper schedTargetMappingOrigMapper;
    @Autowired
    private ImdbService imdbService;
    @Autowired
    private NaverblogService naverBlogService;
    @Autowired
    private NavernewsService navernewsService;
    @Autowired
    private DaumblogService daumBlogService;
    @Autowired
    private DaumnewsService daumNewsService;
    @Autowired
    private NaverMovieService naverMovieService;
    @Autowired
    private SchedTargetContentMapper schedTargetContentMapper;
    @Autowired
    private StepService stepService;
    @Autowired
    private SchedTriggerHistMapper schedTriggerHistMapper;
    @Autowired
    private ItemsSchedMappingMapper itemsSchedMappingMapper;
    @Autowired
    private ItemsService itemsService;

    @Value("${property.serverid}")
    private String serverid;
    @Value("${property.crawl_api_url}")
    private String crawl_api_url;
    @Value("${cmts.property.collect_fail_limit}")
    private Integer collect_fail_limit;

    // 전체 실행 JOB 개수
    public static int runningJobCount = 0;
    // 수짒 시 포함글 방지를 위한 필터단어 set
    public static HashSet<String> filterWords = new HashSet();

    /* 올레TV 메타 확장 - 수집 수행
     */
    @Override
    public int ollehTvMetaCollectScheduleCheck() throws Exception {
        logger.info("#ollehTvMetaCollectScheduleCheck start");
        int rtcode = 0;
        // STEP01 : 수집대상 중 등록일 상위 50개를 읽어옴
        List<SchedTrigger> schedList = this.step01();

        if (schedList != null && schedList.size() > 0) {
            logger.info("#STEP:01:: schedList:" + schedList.toString());
            logger.info("#STEP:02:: searching ConfTarget by schedList! count:" + schedList.size());
            for (SchedTrigger sched : schedList) {
                // STEP02 진행 전 스케쥴 stat = P 로 변경
                if (sched != null && sched.getTargetList() != null) {
                    String rt_stat = "F";
                    String rt_code = "";
                    String rt_mesg = "";
                    String movietitle = sched.getMovietitle();

                    int failCount = 0;
                    int sc_id = sched.getSc_id();
                    int itemIdx = sched.getItemIdx();
                    String type = sched.getType();

                    System.out.println("## uptSchedTriggerForCollectStep02 params:"+sched.toString());

                    int tcnt = (sched.getTcnt() != null) ? sched.getTcnt() : 0;
                    System.out.println("#STEP02 before STEP03 update tcnt:: from:"+sched.getTcnt());
                    //tcnt = tcnt + 1;
                    sched.setTcnt(tcnt);

                    //items_hist에 등록 for 통계
                    int rthist = itemsService.insItemsHist(itemIdx, "collect", "R", movietitle, "START_COLLECT", sc_id);

                    int collectedCount = 0;
                    int rts = stepService.uptSchedTriggerForCollectStep02(sched);
                    // STEP02 : 가져온 리스트에서 개별 타겟리스트의 설정에 따라 수집 파라미터 조회
                    for (ConfTarget target : sched.getTargetList()) {
                        // STEP03 진행 전 수집설정 stat = P로 변경
                        // (추가) 진행 회차를 기록한다 tcnt + 1
                        //int rt0 = stepService.uptConfTargetBeforeCollectProcess(target);
                        //System.out.println("#STEP02 process TG:"+target.toString());

                        ConfTarget tg = this.step02(target);
                        //System.out.println("#STEP02 process to TG:"+tg.toString());

                        tg.setMovietitle(target.getMovietitle());
                        tg.setMoviedirector((target.getMoviedirector() != null) ? target.getMoviedirector() : "");
                        tg.setMovieyear((target.getMovieyear() != null) ? target.getMovieyear() : "" );
                        // STEP03 : 조회된 설정에 맞는 컬렉션 분리 시작 - ThreadPool로 전달
                        logger.info("#STEP:03:: collecting start many destinations! by Target info!");
                        // STEP03 : 수집 처리 후 히스토리 저장, contentAll 통합 저장
                        JsonObject resultCollect = null;
                        String param1 = "";

                        try {
                            //영화 제목이 있는 경우만 수집 시행
                            System.out.println("#################################################################################");
                            logger.info("#STEP:03:: before_params:" + tg.getTg_url() + "/key:"+tg.getParam1()
                                    +"/movietitle:"+ ((tg.getMovietitle() != null) ? tg.getMovietitle() : "")
                                    +"/movieyear:"+ ((tg.getMovieyear() != null) ? tg.getMovieyear() : "")
                                    +"/moviedirector:"+ ((tg.getMoviedirector() != null) ? tg.getMoviedirector() : ""));
                            System.out.println("#################################################################################");
                            param1 = tg.getParam1();
                            movietitle = tg.getMovietitle() != null ? tg.getMovietitle() : "";
                            String moviedirector = tg.getMoviedirector() != null ? tg.getMoviedirector() : "";
                            String movieyear = tg.getMovieyear() != null ? tg.getMovieyear() : "";
                            if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }
                            if (param1.contains("#movieyear") && !"".equals(movieyear)) {
                                param1 = param1.replace("#movieyear", movieyear);
                            } else {
                                param1 = param1.replace("#movieyear", "");
                            }
                            if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
                                param1 = param1.replace("#moviedirector", moviedirector);
                            } else {
                                param1 = param1.replace("#moviedirector", "");
                            }




                            if (!"".equals(movietitle)) {
                                resultCollect = run_step03(tg, sc_id, type, tcnt, sched.getCountry_of_origin());
                                logger.info("####CollectService.run_stap03 resultCollect:" + resultCollect.toString());
                            }




                            if (resultCollect != null && resultCollect.get("rt_code") != null
                                    && "OK".equals(resultCollect.get("rt_code").getAsString())
                                    && resultCollect.get("resultSize").getAsInt() > 100) {
                                System.out.println("#STEP:03:: collection sub Job's return code:"+resultCollect.get("rt_code"));

                                if(resultCollect.get("collectCnt") != null) {
                                    collectedCount += resultCollect.get("collectCnt").getAsInt();

                                    System.out.println("#STEP:03: collectCnt:"+ resultCollect.get("collectCnt"));
                                    System.out.println("#STEP:03: collectedCount:"+ collectedCount);
                                }

                                /* metas는 metas테이블 등록 후 items_metas_mapping에 매핑정보 등록 */
                                if (resultCollect.get("metas") != null) {
                                    //System.out.println("#metas inserting! by metas:"+resultCollect.get("metas").toString());
                                    System.out.println("#metas inserting! by metas: by itemIdx:"+itemIdx);
                                    JsonObject metasObj = (JsonObject) resultCollect.get("metas");
                                    JsonArray metaDestFields = (JsonArray) metasObj.get("dest_fields");
                                    for (JsonElement je : metaDestFields) {
                                        String dest = je.getAsString().trim();
                                        String meta = metasObj.get(dest).getAsString();
                                        if (!"award".equals(dest)) meta = CommonUtil.removeTex(meta);
                                        System.out.println("## insert! itemIdx:"+itemIdx+ " metas:"+dest+" data::"+metasObj.get(dest));


                                        /* 다음영화 수상정보 취득 저장 막음 2018.05.14 */
                                        if (!"award".equals(dest)) {
                                            ItemsMetas newMeta = new ItemsMetas();
                                            newMeta.setIdx(itemIdx);
                                            newMeta.setMtype(dest);
                                            newMeta.setMeta(meta);
                                            int rtItm = itemsService.insItemsMetas(newMeta);
                                        }
                                    }
                                }

                                rt_stat = "S";

                            }

                            System.out.println("#MLOG.collect:sc_id:"+sc_id+"/collectFailCheck::collectedCount:"+collectedCount);

                            int progs = (sched.getProgs() != null ? sched.getProgs() : 0);

                            if (collectedCount < collect_fail_limit) {
                                if (progs < 1) {
                                    //기본 수집 스케쥴이 실패할 경우 rank=2의 스케쥴을 추가하여 sched_target_mapping에 등록한다.
                                    SchedTargetMappingOrig getOrig = new SchedTargetMappingOrig();
                                    getOrig.setType("C");
                                    getOrig.setRank(2);
                                    List<SchedTargetMappingOrig> origSchedList = schedTargetMappingOrigMapper.getSchedTargetMappingOrigList(getOrig);
                                    if (origSchedList != null) {
                                        int rtStm = 0;
                                        for (SchedTargetMappingOrig stmo : origSchedList) {
                                            SchedTargetMapping newStm = new SchedTargetMapping();
                                            newStm.setSc_id(sc_id);
                                            newStm.setTg_id(stmo.getTg_id());
                                            try {
                                                rtStm = schedTriggerMapper.insSchedTargetMapping(newStm);
                                            } catch (Exception e) {}
                                        }
                                    }

                                    // 재처리를 위해 상태코드 변경
                                    sched.setProgs(1);
                                    sched.setStat("Y");
                                    int rtu = schedTriggerMapper.uptSchedTriggerProgs(sched);

                                    rtcode = -2;
                                    rt_stat = "R";
                                } else {
                                    rtcode = -1;
                                    rt_stat = "F";
                                }
                            } else {
                                rtcode = 1;
                                rt_stat = "S";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //target.setStat(rt_stat);

                        try {

                            // 스케쥴-수집 매핑의 이력을 저장한다.
                            SchedTargetMappingHist reqStmh = new SchedTargetMappingHist();
                            reqStmh.setSc_id(sc_id);
                            reqStmh.setType(type);
                            reqStmh.setTcnt(tcnt);
                            reqStmh.setTg_id(target.getTg_id());
                            reqStmh.setStat(rt_stat);
                            reqStmh.setParam1(param1);
                            if (resultCollect != null && resultCollect.get("rt_code") != null)
                                reqStmh.setRt_code(resultCollect.get("rt_code").getAsString());
                            if (resultCollect != null && resultCollect.get("rt_msg") != null)
                                reqStmh.setRt_msg(resultCollect.get("rt_msg").getAsString());
                            // 용량문제로 제외
                            //reqStmh.setSummary(resultCollect.toString());

                            int rt_tmp = schedTargetMappingHistMapper.insSchedTargetMappingHist(reqStmh);

                            // items_stat 저장
                            int rt_it_stat = itemsService.insItemsStatOne(itemIdx, "C", rt_stat);

                            int rt = 0;

                            // 스케쥴-수집 매핑 이력 저장결과 ID를 차용하여 스케쥴-타겟-컨텐츠 저장
                            if (reqStmh != null && reqStmh.getSc_id() != null) {
                                SchedTargetContent reqCont = new SchedTargetContent();
                                reqCont.setSc_id(sc_id);
                                reqCont.setTg_id(tg.getTg_id());
                                reqCont.setStmh_id(reqStmh.getStmh_id());
                                if (resultCollect != null && resultCollect.get("contentAll") != null) {
                                    reqCont.setContent(resultCollect.get("contentAll").getAsString());
                                }

                                logger.debug("#STEP03 result:"+sc_id+"/"+tg.getTg_id()+"/ insSchedTargetContent start");
                                int rt2 = schedTargetContentMapper.insSchedTargetContent(reqCont);

                                System.out.println("#STEP03 result:"+sc_id+"/"+tg.getTg_id()+"/ insSchedTargetContent result:"+rt2);
                                reqCont = null;
                                resultCollect = null;
                             }

                            } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    // SchedTrigger 에 대한 히스토리 저장
                    SchedTriggerHist reqSth = new SchedTriggerHist();
                    reqSth.setSc_id(sc_id);
                    reqSth.setType(sched.getType());
                    reqSth.setTcnt(tcnt);

                    System.out.println("#STEP03 collectedCount:"+collectedCount);

                    //if (failCount > 0 && collectedCount < 20) {
                    if (collectedCount < collect_fail_limit) {
                        reqSth.setStat("F");
                        reqSth.setRt_code("FAIL");
                        reqSth.setRt_msg("FAIL");
                        rtcode = -1;
                        rt_stat = "S";
                    } else {
                        reqSth.setStat("S");
                        reqSth.setRt_code("OK");
                        reqSth.setRt_msg("SUCCESS");
                        rtcode = 1;
                        rt_stat = "S";
                    }

                    //ConfTarget tg = new ConfTarget();

                    logger.debug("#STEP:03:: collecting sched history writing! by sc_id:"+reqSth.getSc_id()
                    +"/type:"+reqSth.getType()+"/stat:"+reqSth.getStat()+"/rt_code:"+reqSth.getStat());

                    int rt1 = stepService.uptSchedTriggerProgsAfterCollectTargetOneProcess(sc_id, rt_stat);

                    //logger.debug("#STEP:03:: collecting sched history writing! by "+reqSth.toString());
                    // 수집 스케쥴 이력 저장
                    int rtsth = schedTriggerHistMapper.insSchedTriggerHist(reqSth);


                    if("S".equals(rt_stat) || "F".equals(rt_stat)) {

                        //items_hist에 등록 for 통계
                        rthist = itemsService.insItemsHist(itemIdx, "collect", rt_stat, movietitle, "END_COLLECT", sc_id);


                        // 수집 스케쥴 종료 후 성공일 경우 분석 스케쥴 등록
                        SchedTrigger newReq = new SchedTrigger();
                        newReq.setParent_sc_id(sc_id);
                        newReq.setType("R");
                        newReq.setDescript(sched.getDescript() + " 정제");
                        newReq.setRegid(serverid);

                        // 등록 전 기존 분석 스케쥴이 있으면 stat = O 로 업데이트하여 중복 등록을 방지 added 2017-12-01 by jy.hwang
                        SchedTrigger oldRefineSched = schedTriggerMapper.getSchedTriggerOne(newReq);

                        int retryRt = 0;
                        // 이미 등록된 분석 스케쥴이 있으면 stat를 Y로 변경하여 재처리
                        if (oldRefineSched != null) {
                            //System.out.println("# getSchedTriggerOne result:"+oldRefineSched.toString());
                            logger.info("#CollectService.result for RefineService:: sc_id:"+oldRefineSched.getSc_id()
                                    +"/type:"+oldRefineSched.getType()
                                    +"/stat:"+oldRefineSched.getStat()
                                    +"/parent_sc_id:"+oldRefineSched.getParent_sc_id()
                            );

                            retryRt = schedTriggerMapper.uptOldSchedTriggerRetry(newReq);
                            retryRt++;
                            //System.out.println("# uptSchedTriggerRetry for R result:"+retryRt);
                        }
                        // 등록된 분석 스케쥴이 없으면 신규 등록
                        if (retryRt < 2) {
                            // 등록 전 기존 분석 스케쥴의 stat = "O"로 변경
                            // setStat는 Old처리에만 사용
                            newReq.setStat("S");
                            int rto = schedTriggerMapper.uptSchedTriggerOldItemsByTypeStat(newReq);

                            // 먼저 sched_trigger에 등록
                            System.out.println("# insert Sched_trigger for R:"+newReq.toString());
                            int rt = schedTriggerMapper.insSchedTriggerForStart(newReq);
                            System.out.println("# insert sc_id:" + newReq.getSc_id() + "   result:" + rt);

                            // items_sched_mapping에 등록
                            ItemsSchedMapping newISM = new ItemsSchedMapping();
                            newISM.setIdx(itemIdx);
                            newISM.setSc_id(newReq.getSc_id());
                            int rtISM = itemsSchedMappingMapper.insItemsSchedMapping(newISM);

                            // sched_trigger에 등록한 sc_id 와 stat = 'Y'인 conf_target 리스트를 조합하여 sched_target_mapping 등록

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
                            if (result1 != null) System.out.println("# getSchedTriggerById for R result:" + result1.toString());
                        }
                    }
                }


            }
        } else {
            logger.info("#ollehTvMetaCollectScheduleCheck end! schedule list is empty!");
        }
        return rtcode;
    }

    @Override
    public int test_ollehTvMetaCollectScheduleCheck(SchedTrigger req1) throws Exception {
        logger.info("#ollehTvMetaCollectScheduleCheck start");
        int rtcode = 0;
        // STEP01 : 수집대상 중 등록일 상위 50개를 읽어옴
        List<SchedTrigger> schedList = this.step01byScid(req1);

        if (schedList != null && schedList.size() > 0) {
            logger.info("#STEP:01:: schedList:" + schedList.toString());
            logger.info("#STEP:02:: searching ConfTarget by schedList! count:" + schedList.size());
            for (SchedTrigger sched : schedList) {
                // STEP02 진행 전 스케쥴 stat = P 로 변경
                if (sched != null && sched.getTargetList() != null) {
                    String rt_stat = "F";
                    String rt_code = "";
                    String rt_mesg = "";

                    int failCount = 0;
                    int sc_id = sched.getSc_id();
                    int itemIdx = sched.getItemIdx();
                    String type = sched.getType();
                    String movietitle = sched.getMovietitle();

                    //items_hist에 등록 for 통계
                    int rthist = itemsService.insItemsHist(itemIdx, "collect", "R", movietitle, "START_COLLECT", sc_id);

                    System.out.println("## uptSchedTriggerForCollectStep02 params:"+sched.toString());

                    int tcnt = (sched.getTcnt() != null) ? sched.getTcnt() : 0;
                    System.out.println("#STEP02 before STEP03 update tcnt:: from:"+sched.getTcnt());
                    //tcnt = tcnt + 1;
                    sched.setTcnt(tcnt);

                    int collectedCount = 0;
                    int rts = stepService.uptSchedTriggerForCollectStep02(sched);
                    // STEP02 : 가져온 리스트에서 개별 타겟리스트의 설정에 따라 수집 파라미터 조회
                    for (ConfTarget target : sched.getTargetList()) {
                        // STEP03 진행 전 수집설정 stat = P로 변경
                        // (추가) 진행 회차를 기록한다 tcnt + 1
                        //int rt0 = stepService.uptConfTargetBeforeCollectProcess(target);
                        //System.out.println("#STEP02 process TG:"+target.toString());

                        ConfTarget tg = this.step02(target);
                        //System.out.println("#STEP02 process to TG:"+tg.toString());

                        tg.setMovietitle(target.getMovietitle());
                        tg.setMoviedirector((target.getMoviedirector() != null) ? target.getMoviedirector() : "");
                        tg.setMovieyear((target.getMovieyear() != null) ? target.getMovieyear() : "" );
                        // STEP03 : 조회된 설정에 맞는 컬렉션 분리 시작 - ThreadPool로 전달
                        logger.info("#STEP:03:: collecting start many destinations! by Target info!");
                        // STEP03 : 수집 처리 후 히스토리 저장, contentAll 통합 저장
                        JsonObject resultCollect = null;
                        String param1 = "";

                        try {
                            //영화 제목이 있는 경우만 수집 시행
                            System.out.println("#################################################################################");
                            logger.info("#STEP:03:: before_params:" + tg.getTg_url() + "/key:"+tg.getParam1()
                                    +"/movietitle:"+ ((tg.getMovietitle() != null) ? tg.getMovietitle() : "")
                                    +"/movieyear:"+ ((tg.getMovieyear() != null) ? tg.getMovieyear() : "")
                                    +"/moviedirector:"+ ((tg.getMoviedirector() != null) ? tg.getMoviedirector() : ""));
                            System.out.println("#################################################################################");
                            param1 = tg.getParam1();
                            movietitle = tg.getMovietitle() != null ? tg.getMovietitle() : "";
                            String moviedirector = tg.getMoviedirector() != null ? tg.getMoviedirector() : "";
                            String movieyear = tg.getMovieyear() != null ? tg.getMovieyear() : "";
                            if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }
                            if (param1.contains("movieyear") && !"".equals(movieyear)) {
                                param1 = param1.replace("#movieyear", movieyear);
                            } else {
                                param1 = param1.replace("#movieyear", "");
                            }
                            if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
                                param1 = param1.replace("#moviedirector", moviedirector);
                            }




                            if (target.getMovietitle() != null && !"".equals(target.getMovietitle().trim())) {
                                resultCollect = run_step03(tg, sc_id, type, tcnt, sched.getCountry_of_origin());
                                System.out.println("#run_stap03 resultCollect:" + resultCollect.toString());
                            }




                            if (resultCollect != null && resultCollect.get("rt_code") != null
                                    && "OK".equals(resultCollect.get("rt_code").getAsString())
                                    && resultCollect.get("resultSize").getAsInt() > 100) {
                                System.out.println("#STEP:03:: collection sub Job's return code:"+resultCollect.get("rt_code"));

                                if(resultCollect.get("collectCnt") != null) {
                                    collectedCount += resultCollect.get("collectCnt").getAsInt();

                                    System.out.println("#STEP:03: collectCnt:"+ resultCollect.get("collectCnt"));
                                    System.out.println("#STEP:03: collectedCount:"+ collectedCount);
                                }

                                /* metas는 metas테이블 등록 후 items_metas_mapping에 매핑정보 등록 */
                                if (resultCollect.get("metas") != null) {
                                    //System.out.println("#metas inserting! by metas:"+resultCollect.get("metas").toString());
                                    System.out.println("#metas inserting! by metas: by itemIdx:"+itemIdx);
                                    JsonObject metasObj = (JsonObject) resultCollect.get("metas");
                                    JsonArray metaDestFields = (JsonArray) metasObj.get("dest_fields");
                                    for (JsonElement je : metaDestFields) {
                                        String dest = je.getAsString().trim();
                                        String meta = metasObj.get(dest).getAsString();
                                        meta = CommonUtil.removeTex(meta);
                                        System.out.println("## insert! itemIdx:"+itemIdx+ " metas:"+dest+" data::"+metasObj.get(dest));

                                        ItemsMetas newMeta = new ItemsMetas();
                                        newMeta.setIdx(itemIdx);
                                        newMeta.setMtype(dest);
                                        newMeta.setMeta(meta);
                                        int rtItm = itemsService.insItemsMetas(newMeta);
                                    }
                                }

                                rt_stat = "S";
                            } else {
                                //System.out.println("#STEP:03:: collection sub Job's return code:"+resultCollect.get("rt_code"));
                                rt_stat = "F";
                                failCount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //target.setStat(rt_stat);

                        try {

                            // 스케쥴-수집 매핑의 이력을 저장한다.
                            SchedTargetMappingHist reqStmh = new SchedTargetMappingHist();
                            reqStmh.setSc_id(sc_id);
                            reqStmh.setType(type);
                            reqStmh.setTcnt(tcnt);
                            reqStmh.setTg_id(target.getTg_id());
                            reqStmh.setStat(rt_stat);
                            reqStmh.setParam1(param1);
                            if (resultCollect != null && resultCollect.get("rt_code") != null)
                                reqStmh.setRt_code(resultCollect.get("rt_code").getAsString());
                            if (resultCollect != null && resultCollect.get("rt_msg") != null)
                                reqStmh.setRt_msg(resultCollect.get("rt_msg").getAsString());
                            // 용량문제로 일시 제외
                            //reqStmh.setSummary(resultCollect.toString());

                            int rt_tmp = schedTargetMappingHistMapper.insSchedTargetMappingHist(reqStmh);

                            // items_stat 저장
                            int rt_it_stat = itemsService.insItemsStatOne(itemIdx, "C", rt_stat);

                            int rt = 0;

                            // 스케쥴-수집 매핑 이력 저장결과 ID를 차용하여 스케쥴-타겟-컨텐츠 저장
                            if (reqStmh != null && reqStmh.getSc_id() != null) {
                                SchedTargetContent reqCont = new SchedTargetContent();
                                reqCont.setSc_id(sc_id);
                                reqCont.setTg_id(tg.getTg_id());
                                reqCont.setStmh_id(reqStmh.getStmh_id());
                                if (resultCollect != null && resultCollect.get("contentAll") != null) {
                                    reqCont.setContent(resultCollect.get("contentAll").getAsString());
                                }

                                logger.debug("#STEP03 result:"+sc_id+"/"+tg.getTg_id()+"/ insSchedTargetContent start");
                                int rt2 = schedTargetContentMapper.insSchedTargetContent(reqCont);
                                System.out.println("#STEP03 result:"+sc_id+"/"+tg.getTg_id()+"/ insSchedTargetContent result:"+rt2);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    // SchedTrigger 에 대한 히스토리 저장
                    SchedTriggerHist reqSth = new SchedTriggerHist();
                    reqSth.setSc_id(sc_id);
                    reqSth.setType(sched.getType());
                    reqSth.setTcnt(tcnt);

                    System.out.println("#STEP03 collectedCount:"+collectedCount);

                    /*
                    if (failCount > 0 && collectedCount < 10) {
                        reqSth.setStat("F");
                        reqSth.setRt_code("FAIL");
                        reqSth.setRt_msg("FAIL");
                        rtcode = -1;
                        rt_stat = "F";
                    } else {
                        reqSth.setStat("S");
                        reqSth.setRt_code("OK");
                        reqSth.setRt_msg("SUCCESS");
                        rtcode = 1;
                        rt_stat = "S";
                    }
                    */
                    reqSth.setStat("S");
                    reqSth.setRt_code("OK");
                    reqSth.setRt_msg("SUCCESS");
                    rtcode = 1;
                    rt_stat = "S";

                    //ConfTarget tg = new ConfTarget();

                    logger.debug("#STEP:03:: collecting sched history writing! by sc_id:"+reqSth.getSc_id()
                            +"/type:"+reqSth.getType()+"/stat:"+reqSth.getStat()+"/rt_code:"+reqSth.getStat());

                    int rt1 = stepService.uptSchedTriggerProgsAfterCollectTargetOneProcess(sc_id, rt_stat);

                    //logger.debug("#STEP:03:: collecting sched history writing! by "+reqSth.toString());
                    // 수집 스케쥴 이력 저장
                    int rtsth = schedTriggerHistMapper.insSchedTriggerHist(reqSth);


                    if("S".equals(rt_stat) || "F".equals(rt_stat)) {
                        //items_hist에 등록 for 통계
                        rthist = itemsService.insItemsHist(itemIdx, "collect", rt_stat, movietitle, "END_COLLECT", sc_id);

                        // 수집 스케쥴 종료 후 성공일 경우 분석 스케쥴 등록
                        SchedTrigger newReq = new SchedTrigger();
                        newReq.setParent_sc_id(sc_id);
                        newReq.setType("R");
                        newReq.setDescript(sched.getDescript() + " 정제");
                        newReq.setRegid(serverid);

                        // 등록 전 기존 분석 스케쥴이 있으면 stat = O 로 업데이트하여 중복 등록을 방지 added 2017-12-01 by jy.hwang
                        SchedTrigger oldRefineSched = schedTriggerMapper.getSchedTriggerOne(newReq);

                        int retryRt = 0;
                        // 이미 등록된 분석 스케쥴이 있으면 stat를 Y로 변경하여 재처리
                        if (oldRefineSched != null) {
                            logger.info("#CollectService.result for RefineService:: sc_id:"+oldRefineSched.getSc_id()
                                            +"/type:"+oldRefineSched.getType()
                                            +"/stat:"+oldRefineSched.getStat()
                                            +"/parent_sc_id:"+oldRefineSched.getParent_sc_id());

                            retryRt = schedTriggerMapper.uptOldSchedTriggerRetry(newReq);
                            retryRt++;
                            System.out.println("# uptSchedTriggerRetry for R result:"+retryRt);
                        }
                        // 등록된 분석 스케쥴이 없으면 신규 등록
                        if (retryRt < 2) {
                            // 등록 전 기존 분석 스케쥴의 stat = "O"로 변경
                            // setStat는 Old처리에만 사용
                            newReq.setStat("S");
                            int rto = schedTriggerMapper.uptSchedTriggerOldItemsByTypeStat(newReq);

                            // 먼저 sched_trigger에 등록
                            System.out.println("# insert Sched_trigger for R:"+newReq.toString());
                            int rt = schedTriggerMapper.insSchedTriggerForStart(newReq);
                            System.out.println("# insert sc_id:" + newReq.getSc_id() + "   result:" + rt);

                            // items_sched_mapping에 등록
                            ItemsSchedMapping newISM = new ItemsSchedMapping();
                            newISM.setIdx(itemIdx);
                            newISM.setSc_id(newReq.getSc_id());
                            int rtISM = itemsSchedMappingMapper.insItemsSchedMapping(newISM);

                            // sched_trigger에 등록한 sc_id 와 stat = 'Y'인 conf_target 리스트를 조합하여 sched_target_mapping 등록

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
                            if (result1 != null) System.out.println("# getSchedTriggerById for R result:" + result1.toString());
                        }
                    }
                }


            }
        } else {
            logger.info("#ollehTvMetaCollectScheduleCheck end! schedule list is empty!");
        }
        return rtcode;
    }

    /* before STEP 1
    * 영화 입수 시 수집 스케쥴 등록 후 conf_target 의 stat = 'P'인 항목과 매핑하여 SCHED_TARGET_MAPPING에 등록
     */
    public int insSchedTriggerForStart() {
        int rtcode = 0;
        SchedTrigger newReq = new SchedTrigger();
        newReq.setParent_sc_id(0);
        newReq.setType("C");
        newReq.setDescript("2테스트 수집");
        newReq.setRegid("ghkdwo77");

        // 먼저 sched_trigger에 등록
        rtcode = schedTriggerMapper.insSchedTriggerForStart(newReq);
        System.out.println("# insert sc_id:"+newReq.getSc_id()+ "   result:"+rtcode);

        // sched_trigger에 등록한 sc_id 와 stat = 'P'인 conf_target 리스트를 조합하여 sched_target_mapping 등록
        ConfTarget req = new ConfTarget();
        req.setPageNo(1);
        req.setPageSize(10);
        req.setOrderType("ASC");
        req.setStat("Y");

        List<ConfTarget> targetList = confTargetMapper.getTargetListByStat(req);
        for(ConfTarget ct : targetList) {
            SchedTargetMapping reqM = new SchedTargetMapping();
            reqM.setSc_id(newReq.getSc_id());
            reqM.setTg_id(ct.getTg_id());
            rtcode = schedTriggerMapper.insSchedTargetMapping(reqM);
            System.out.println("# insert targetMapping sc_id:"+newReq.getSc_id()+ "   result:"+rtcode);
        }

        SchedTrigger result1 = schedTriggerMapper.getSchedTriggerById(newReq.getSc_id());
        System.out.println("# result:"+result1.toString());
        return rtcode;
    }


    /* after STEP 3
    * 수집 종료 후 정제 스케쥴 등록
     */



    /* STEP 1
     * SchedTrigger & targetList<ConfTarget> 통해 수집 스케쥴 50개 조회
    */
    @Override
    public List<SchedTrigger> step01() {

        System.out.println("#STEP01 for Collect!");
        SchedTrigger req = new SchedTrigger();
        req.setType("C");
        req.setStat("Y");

        List<SchedTrigger> result = schedTriggerMapper.get50ByTypeStatSchedTriggerList(req);
        List<SchedTrigger> result2 = new ArrayList();
        for(SchedTrigger st : result) {
            List<ConfTarget> targetList = schedTriggerMapper.getConfTargetListByScid(st);
            st.setTargetList(targetList);
            result2.add(st);
        }
        System.out.println("#STEP01 for Collect! result:"+result2);
        return result2;
    }

    @Override
    public List<SchedTrigger> step01byScid(SchedTrigger req) {

        System.out.println("#STEP01 for Collect test! by sc_id:"+req.getSc_id());
        List<SchedTrigger> result = new ArrayList();
        SchedTrigger item1 = schedTriggerMapper.getSchedTriggerOneByScid(req);
        List<SchedTrigger> result2 = new ArrayList();
        List<ConfTarget> targetList = schedTriggerMapper.getConfTargetListByScid(item1);
        item1.setTargetList(targetList);
        result.add(item1);
        System.out.println("#STEP01 for Collect! result:"+result);
        return result;
    }

    /* STEP 2
     * ConfTarget & List<ConfPreset> 을 통해 수집 대상 설정 조회
     * 조회 후 stat = P 로 변경
    */
    @Override
    public ConfTarget step02(ConfTarget req) throws Exception {
        ConfTarget result = null;

        if (req != null && req.getTg_id() != null) {

            result = confTargetMapper.getConfTargetById(req);
            if (result != null && result.getPresetList() != null) {
                logger.info("#STEP:02:: target: req.tg_id:" + req.getTg_id().toString()
                        + "/URL:"+req.getTg_url()
                        + "/PARAM1:"+req.getTg_url_param1());

                List<ConfPreset> psetList = result.getPresetList();
                for (ConfPreset ps : psetList) {
                    logger.info("#STEP:02:: target-preset: req.tg_id:" + req.getTg_id().toString()
                            +"/req.ps_id:"+ps.getPs_id()
                            +"/dest_field:"+ps.getDest_field()
                            +"/ps_tag:"+ps.getPs_tag()
                            +"/descriptp:"+ps.getDescriptp());
                }
            }
        } else {
            if (req!=null) {
                logger.info("#STEP:02::fail target is empty!");
            } else {
                logger.info("#STEP:02::fail target tg_url is empty!");
            }
        }
        return result;
    }

    /* STEP 3imdb - main
     * tg_url:GOOGLE_SEARCH_IMDB 구글 검색 후 imdb.com 데이터 수집

    public JsonObject run_step03imdb(ConfTarget tg) {
        String statTarget = "F";
        //String content = "";

        JsonObject imdb = null;
        try {
            imdb = this.step03imdb(tg);
            //결과 코드를 만든다.
            int resultContentSize = 0;
            JsonArray resultArr = null;
            if (imdb != null && imdb.get("result") != null) {
                resultArr = (JsonArray) imdb.get("result");

                // web collect 실행 시 추출한 dest_fields에 해당하는 컨텐츠를 모두 합해서 저장한다
                //JsonElement destElm =  imdb.get("dest_fields");
                //Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                //ArrayList<String> dest_fields =  new Gson().fromJson(destElm, listType);
                //System.out.println("### dest_fields:"+dest_fields);

                //for(JsonElement je : resultArr) {
                //    JsonObject jo = (JsonObject) je;
                //    if(jo.get("value") != null) resultContentSize += jo.get("value").getAsString().length();
                //    System.out.println("##### resultArr.element:"+jo.getAsString());
                //}
                String contentAll = JsonUtil.getStringFromJsonArraysValues(resultArr);
                resultContentSize = contentAll.length();

                if (resultContentSize > 100) statTarget = "S";
                // 수집 결과를 conf_target_hist 에 저장
                ConfTargetHist reqHist = new ConfTargetHist();
                reqHist.setTg_id(tg.getTg_id());
                if (imdb != null && imdb.get("rt_code") != null) reqHist.setRt_code(imdb.get("rt_code").getAsString());
                if (imdb != null && imdb.get("rt_msg") != null) reqHist.setRt_msg(imdb.get("rt_msg").getAsString());
                reqHist.setStat(statTarget);
                reqHist.setSummary(imdb.toString());

                System.out.println("#STEP03imdb insConfTargetHist from data:"+reqHist.toString());
                int rt = confTargetHistMapper.insConfTargetHist(reqHist);
                System.out.println("#STEP03imdb insConfTargetHist result:"+rt+"/tgh_id:"+reqHist.getTgh_id());

                if (rt > 0 && reqHist.getTgh_id() != null) {
                    // 수집 컨텐츠를 conf_target_content 에 저장
                    ConfTargetContent reqCont = new ConfTargetContent();
                    reqCont.setTg_id(tg.getTg_id());
                    reqCont.setTgh_id(reqHist.getTgh_id());
                    reqCont.setContent(contentAll);

                    System.out.println("#STEP03imdb insConfHistContent from data:"+reqCont.toString());
                    int rt2 = confTargetContentMapper.insConfTargetContent(reqCont);
                    System.out.println("#STEP03imdb insConfHistContent result:"+rt2);
                }

            }

        } catch (Exception e) {
            statTarget = "F";

            ConfTargetHist reqHist = new ConfTargetHist();
            reqHist.setTg_id(tg.getTg_id());
            if (imdb != null && imdb.get("rt_code") != null) reqHist.setRt_code(imdb.get("rt_code").getAsString());
            if (imdb != null && imdb.get("rt_msg") != null) {
                reqHist.setRt_msg(imdb.get("rt_msg").getAsString()
                    + "_" + (e.getCause() != null ? e.getCause().toString() : "")
                    + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));
            }
            reqHist.setStat(statTarget);
            //reqHist.setContent(content);

            System.out.println("#STEP03imdb insConfTargetHist-fail from data:"+reqHist.toString());
            confTargetHistMapper.insConfTargetHist(reqHist);

            e.printStackTrace();
        }
        return imdb;
    }
    */

    /* STEP 3imdb - sub
     * tg_url:GOOGLE_SEARCH_IMDB 구글 검색 후 imdb.com 데이터 수집
     * tg_url:NAVER_BLOG 네이버 블로그 검색 후 서브 링크 데이터 수집
    */
    @Override
    public JsonObject step03imdb(ConfTarget req) {
        JsonObject result = null;
        if(req != null && req.getTg_url() != null && req.getParam1() != null) {
            String param1 = req.getParam1();
            String movietitle = req.getMovietitle() != null ? req.getMovietitle() : "";
            String moviedirector = req.getMoviedirector() != null ? req.getMoviedirector() : "";
            String movieyear = req.getMovieyear() != null ? req.getMovieyear() : "";
            if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }
            if (param1.contains("#movieyear") && !"".equals(movieyear)) {
                param1 = param1.replace("#movieyear", movieyear);
            } else {
                param1 = param1.replace("#movieyear", "");
            }
            if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
                param1 = param1.replace("#moviedirector", moviedirector);
            } else {
                param1 = param1.replace("#moviedirector", "");
            }
            req.setParam1(param1);

            logger.info("#STEP:03:: get start! url:" + req.getTg_url() + "/key:" + req.getParam1()+"/key2:"
                    +param1+"/movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);

            int collectCnt = 0;
            try {
                result = imdbService.getMovie(req);
                String rt_code = "F";
                String rt_msg = "FAIL";
                if (result != null && result.get("contents") != null) {
                    JsonArray resultContentArray = (JsonArray) result.get("contents");
                    JsonObject contentObj = (JsonObject) resultContentArray.get(0);

                    if (contentObj.get("plot") != null) {
                        String plotStr = contentObj.get("plot").getAsString();
                        if ("FAIL".equals(plotStr)) {
                            rt_code = "FAIL";
                            rt_msg = "FAIL";
                        } else if ("TRANS_FAIL".equals(plotStr)) {
                            rt_code = "FAIL";
                            rt_msg = plotStr;
                        } else if ("TRANS_LIMIT".equals(plotStr)) {
                            rt_code = "FAIL";
                            rt_msg = plotStr;
                        } else {
                            rt_code = "OK";
                            rt_msg = "SUCCESS";
                            collectCnt = 1;
                        }
                    } else {
                        rt_code = "FAIL";
                        rt_msg = "CONTENT_NULL";
                    }
                } else {
                    rt_code = "FAIL";
                    rt_msg = "CONTENTS_NULL";
                }
                result.addProperty("rt_code",rt_code);
                result.addProperty("rt_msg",rt_msg);
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonArray());
                result.addProperty("rt_code", "FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));

                e.printStackTrace();
            }

            logger.info("#STEP:03:: get end! url:" + req.getTg_url() + "/key:" + req.getParam1());
        } else {
            result = new JsonObject();
            result.add("result", new JsonArray());
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg","IMDB get start fail! params error!");
            logger.info("#STEP:03:: get start fail! params error!:"+req.toString());
        }
        return result;
    }

    /* STEP 03 - crawl by html api /crawl/byprefix
    */
    @Override
    public JsonObject step03_ByHtml(ConfTarget req) {
        JsonObject result = null;

        String prefix = "";

        if(req != null && req.getTg_url() != null && req.getParam1() != null) {
            prefix = req.getTg_url();
            String param1 = req.getParam1();
            String movietitle = req.getMovietitle() != null ? req.getMovietitle() : "";
            String moviedirector = req.getMoviedirector() != null ? req.getMoviedirector() : "";
            String movieyear = req.getMovieyear() != null ? req.getMovieyear() : "";
            String ccubetype = req.getCcubetype() != null ? req.getCcubetype() : "";


            int collectCnt = 0;
            try {
                logger.info("#STEP:03:: crawl start! url:+"+crawl_api_url+" ::" + req.getTg_url() + "/pararm1:" + req.getParam1()
                        +"/movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);

                String jsonStr = Jsoup.connect(crawl_api_url)
                        .timeout(70000)
                        .userAgent("Mozilla")
                        .ignoreContentType(true)
                        .data("prefix", prefix)
                        .data("movietitle", movietitle)
                        .data("movieyear", movieyear)
                        .data("moviedirector", moviedirector)
                        .data("ccubetype", ccubetype)
                        .execute().body();

                result = new Gson().fromJson(jsonStr, JsonObject.class);

                System.out.println("##### crawl/prefix result::print::"+result.toString());

                String rt_code = "F";
                String rt_msg = "FAIL";
                if (result != null
                        && ( result.get("contents") != null || result.get("resultArr") != null)) {

                    //System.out.println("#resultObj:::"+result.toString());

                    /*
                    IMDB : contents (JsonArray)
                    NAVER_BLOG : resultArr (JsonArray)
                     */
                    JsonArray resultContentArray = (JsonArray) result.get("contents");
                    if (resultContentArray == null) resultContentArray = (JsonArray) result.get("resultArr");
                    if (resultContentArray.size() > 0) {
                        rt_code = "OK";
                        rt_msg = "SUCCESS";
                    }

                    try {
                        JsonObject contentObj = (JsonObject) resultContentArray.get(0);

                        if (contentObj != null && contentObj.get("plot") != null) {
                            String plotStr = contentObj.get("plot").getAsString();
                            if ("FAIL".equals(plotStr)) {
                                rt_code = "FAIL";
                                rt_msg = "FAIL";
                            } else if ("TRANS_FAIL".equals(plotStr)) {
                                rt_code = "FAIL";
                                rt_msg = plotStr;
                            } else if ("TRANS_LIMIT".equals(plotStr)) {
                                rt_code = "FAIL";
                                rt_msg = plotStr;
                            }
                        } else {
                            if (!"OK".equals(rt_code)) {
                                rt_code = "FAIL";
                                rt_msg = "CONTENT_NULL";
                            }
                        }
                    } catch (Exception e) { }
                } else if (result != null && result.get("contentsObj") != null) {
                    JsonArray contentsObjArr = result.get("contentsObj").getAsJsonArray();
                    if (contentsObjArr.size() > 0) {
                        rt_code = "OK";
                        rt_msg = "SUCCESS";
                    }

                    result.add("contents", contentsObjArr);
                }
                result.addProperty("rt_code",rt_code);
                result.addProperty("rt_msg",rt_msg);
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonArray());
                result.addProperty("rt_code", "FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));

                e.printStackTrace();
            }

            logger.info("#STEP:03:: get end! url:" + req.getTg_url() + "/key:" + req.getParam1());
        } else {
            result = new JsonObject();
            result.add("result", new JsonArray());
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg",prefix+" crawl fail! params error!");
            logger.info("#STEP:03:: crawl fail! params error!:"+req.toString());
        }
        return result;
    }

    /* STEP 3 - main
     * tg_url:STEP 3 - 데이터 수집 이후 conf_target_history와 conf_target_content를 저장
     * conuntry_of_origin :: CcubeContent, CcubeContentK, CcubeSeries, CcubeSeriesK
    */
    public JsonObject run_step03(ConfTarget tg, int sc_id, String type, int tcnt, String country_of_origin) {
        String statTarget = "F";
        String content = "";

        JsonObject resultCollect = null;
        try {
            // CcubeSeries의 경우 검색어에서 영화, 감독명을 빼기 위해 ccubetype에 type을 저장
            tg.setCcubetype(country_of_origin);

            switch (tg.getTg_url()) {
                case "GOOGLE_SEARCH_IMDB":
                    if (!country_of_origin.endsWith("K")) {
                        logger.info("#STEP:03:imdb:: collecting start GOOGLE_SEARCH_IMDB: by sc_id:" + sc_id);
                        resultCollect = this.step03_ByHtml(tg);
                    } else {
                        logger.info("#STEP:03:imdb:: skip-collecting GOOGLE_SEARCH_IMDB: by sc_id:" + sc_id +" caused country_of_origin:"+country_of_origin);
                    }

                    break;
                case "NAVER_BLOG":
                    logger.info("#STEP:03:naver_blog:: collecting start NAVER_BLOG: by sc_id:" + sc_id);
                    resultCollect = this.step03_ByHtml(tg);

                    break;
                case "DAUM_BLOG":
                    logger.info("#STEP:03:daum_blog:: collecting start DAUM_BLOG: by sc_id:" + sc_id);
                    resultCollect = this.step03_ByHtml(tg);

                    break;
                case "NAVER_MOVIE":
                    logger.info("#STEP:03:naver_movie:: collecting start NAVER_MOVIE: by sc_id:" + sc_id);
                    resultCollect = this.step03_ByHtml(tg);
                    //System.out.println("#resultCollect:"+resultCollect.toString());

                    break;
                case "DAUM_MOVIE":
                    logger.info("#STEP:03:daum_movie:: collecting start DAUM_MOVIE: by sc_id:" + sc_id);
                    resultCollect = this.step03_ByHtml(tg);

                    break;
                case "NAVER_NEWS":
                    logger.info("#STEP:03:daum_movie:: collecting start NAVER_NEWS: by sc_id:" + sc_id);
                    //resultCollect = navernewsService.getSearchNews(tg);
                    resultCollect = this.step03_ByHtml(tg);

                    break;
                case "DAUM_NEWS":
                    logger.info("#STEP:03:daum_movie:: collecting start DAUM_NEWS: by sc_id:" + sc_id);
                    //resultCollect = daumNewsService.getSearchNews(tg);
                    resultCollect = this.step03_ByHtml(tg);

                    break;

                case "CINE21_MOVIE":
                    logger.info("#STEP:03:cine21_movie:: collecting start CINE21_MOVIE: by sc_id:" + sc_id);
                    //resultCollect = daumNewsService.getSearchNews(tg);
                    resultCollect = this.step03_ByHtml(tg);

                    break;

            }
            //결과 코드를 만든다.
            int resultContentSize = 0;
            JsonArray resultArr = null;
            String contentAll = "";
            SchedTargetMappingHist reqHist = null;
            System.out.println("##### resultCollect result:::" + resultCollect.toString());


            if (resultCollect != null && (resultCollect.get("result") != null
                    || resultCollect.get("contents") != null)) {
                //System.out.println("#resultObj:::"+resultCollect.toString());
                if (resultCollect.get("contents") != null) {
                    JsonArray contentArr = (JsonArray) resultCollect.get("contents");
                    //System.out.println("#contentArr1:"+contentArr.toString());

                    contentAll = JsonUtil.getStringFromJsonArraysValues2(contentArr);
                    //System.out.println("#contentArr2 contentAll:"+contentAll.toString());
                } else {
                    resultArr = (JsonArray) resultCollect.get("result");
                    contentAll = JsonUtil.getStringFromJsonArraysValues(resultArr);

                    // 네이버 블로그의 경우 obj:[array:[obj;array:[obj]]] 형태 이므로 1 depth 더 내려간 array를 발췌하여 content 통합
                    // 예시   resultObj:[array[sub_url:'http:...', contents:[ obj:["plot":"txt"       ], [ ]                    ], array[].....]

                    if (resultArr != null && resultArr.size() > 0 && resultArr.get(0) != null) {
                            //&& ((JsonObject) resultArr.get(0)).get("contents") != null) {
                        JsonArray tmpJarr = JsonUtil.getListFromJsonArray(resultArr);
                        contentAll = JsonUtil.getStringFromJsonArraysValues(tmpJarr);
                    }
                }

                resultContentSize = contentAll.length();
                resultCollect.addProperty("resultSize", resultContentSize);
                resultCollect.addProperty("contentAll", contentAll);
            } else {
                resultCollect = new JsonObject();
                resultCollect.addProperty("rt_code","FAIL");
                resultCollect.addProperty("rt_msg","Result-Collect is null!");
            }
        } catch (Exception e) {
            resultCollect = new JsonObject();
            resultCollect.addProperty("rt_code","FAIL");
            resultCollect.addProperty("rt_msg","Result-Collect get Exception");
            e.printStackTrace();
        }
        return resultCollect;
    }


    public JsonObject run_step03_orig(ConfTarget tg, int sc_id, String type, int tcnt) {
        String statTarget = "F";
        String content = "";

        JsonObject resultCollect = null;
        try {
            switch (tg.getTg_url()) {
                case "GOOGLE_SEARCH_IMDB":
                    System.out.println("#STEP:03:imdb:: collecting start GOOGLE_SEARCH_IMDB: by sc_id:" + sc_id);
                    resultCollect = this.step03imdb(tg);

                    break;
                case "NAVER_BLOG":
                    System.out.println("#STEP:03:naver_blog:: collecting start NAVER_BLOG: by sc_id:" + sc_id);
                    resultCollect = this.step03naverBlog(tg);

                    break;
                case "DAUM_BLOG":
                    System.out.println("#STEP:03:daum_blog:: collecting start DAUM_BLOG: by sc_id:" + sc_id);
                    resultCollect = this.step03daumBlog(tg);

                    break;
                case "NAVER_MOVIE":
                    System.out.println("#STEP:03:naver_movie:: collecting start NAVER_MOVIE: by sc_id:" + sc_id);
                    resultCollect = this.step03naverMovie(tg);
                    //System.out.println("#resultCollect:"+resultCollect.toString());

                    break;
                case "DAUM_MOVIE":
                    System.out.println("#STEP:03:daum_movie:: collecting start DAUM_MOVIE: by sc_id:" + sc_id);
                    resultCollect = this.step03daumMovie(tg);

                    break;
            }
            //결과 코드를 만든다.
            int resultContentSize = 0;
            JsonArray resultArr = null;
            String contentAll = "";
            SchedTargetMappingHist reqHist = null;
            //System.out.println("#resultCollect result:::" + resultCollect.toString());

            if (resultCollect != null && (resultCollect.get("result") != null
                    || resultCollect.get("contents") != null)) {
                if (resultCollect.get("contents") != null) {
                    JsonArray contentArr = (JsonArray) resultCollect.get("contents");
                    //System.out.println("#contentArr1:"+contentArr.toString());

                    contentAll = JsonUtil.getStringFromJsonArraysValues2(contentArr);
                    //System.out.println("#contentArr2 contentAll:"+contentAll.toString());
                } else {
                    resultArr = (JsonArray) resultCollect.get("result");
                    contentAll = JsonUtil.getStringFromJsonArraysValues(resultArr);

                    // 네이버 블로그의 경우 obj:[array:[obj;array:[obj]]] 형태 이므로 1 depth 더 내려간 array를 발췌하여 content 통합
                    // 예시   resultObj:[array[sub_url:'http:...', contents:[ obj:["plot":"txt"       ], [ ]                    ], array[].....]
                    if (resultArr != null && resultArr.get(0) != null && ((JsonObject) resultArr.get(0)).get("contents") != null) {
                        JsonArray tmpJarr = JsonUtil.getListFromJsonArray(resultArr);
                        contentAll = JsonUtil.getStringFromJsonArraysValues(tmpJarr);
                    }
                }

                resultContentSize = contentAll.length();
                resultCollect.addProperty("resultSize", resultContentSize);
                resultCollect.addProperty("contentAll", contentAll);
            } else {
                resultCollect = new JsonObject();
                resultCollect.addProperty("rt_code","FAIL");
                resultCollect.addProperty("rt_msg","Result-Collect is null!");
            }
        } catch (Exception e) {
            resultCollect = new JsonObject();
            resultCollect.addProperty("rt_code","FAIL");
            resultCollect.addProperty("rt_msg","Result-Collect get Exception");
            e.printStackTrace();
        }
        return resultCollect;
    }


    /* STEP 3naverBlog - sub
     * tg_url:NAVER_BLOG 검색 후 검색 페이지의 일정 페이징 사이즈만큼 데이터 수집
    */
    @Override
    public JsonObject step03naverBlog(ConfTarget req) throws Exception {
        JsonObject result = null;
        if(req != null && req.getTg_url() != null && req.getParam1() != null) {
            String param1 = req.getParam1();
            String movietitle = req.getMovietitle() != null ? req.getMovietitle() : "";
            String moviedirector = req.getMoviedirector() != null ? req.getMoviedirector() : "";
            String movieyear = req.getMovieyear() != null ? req.getMovieyear() : "";
            if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }
            if (param1.contains("#movieyear") && !"".equals(movieyear)) {
                param1 = param1.replace("#movieyear", movieyear);
            } else {
                param1 = param1.replace("#movieyear", "");
            }
            if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
                param1 = param1.replace("#moviedirector", moviedirector);
            } else {
                param1 = param1.replace("#moviedirector", "");
            }
            req.setParam1(param1);

            System.out.println("#################################################################################");
            logger.info("#STEP:03:: getNaverBlog start! url:" + req.getTg_url() + "/key:" + req.getParam1()+"/key2:"
                    +param1+"/movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);
            System.out.println("#################################################################################");

            int rt_count = 0;
            try {
                result = naverBlogService.getSearchBlog(req);

                result.addProperty("rt_code","OK");
                result.addProperty("rt_msg","SUCCESS");
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonArray());
                result.addProperty("rt_code","FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));
                e.printStackTrace();
            }
            logger.info("#STEP:03:: getNaverBlog end! url:" + req.getTg_url() + "/key:" + req.getParam1());
        } else {
            result = new JsonObject();
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg","getNaverBlog fail! params error!");
            logger.info("#STEP:03:: getNaverBlog fail! params error!:"+req.toString());
        }
        return result;
    }

    /* STEP 3daumBlog - sub
     * tg_url:DAUM_BLOG 검색 후 검색 페이지의 일정 페이징 사이즈만큼 데이터 수집
    */
    @Override
    public JsonObject step03daumBlog(ConfTarget req) throws Exception {
        JsonObject result = null;
        if(req != null && req.getTg_url() != null && req.getParam1() != null) {
            String param1 = req.getParam1();
            String movietitle = req.getMovietitle() != null ? req.getMovietitle() : "";
            String moviedirector = req.getMoviedirector() != null ? req.getMoviedirector() : "";
            String movieyear = req.getMovieyear() != null ? req.getMovieyear() : "";
            if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }
            if (param1.contains("#movieyear") && !"".equals(movieyear)) {
                param1 = param1.replace("#movieyear", movieyear);
            } else {
                param1 = param1.replace("#movieyear", "");
            }
            if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
                param1 = param1.replace("#moviedirector", moviedirector);
            } else {
                param1 = param1.replace("#moviedirector", "");
            }
            req.setParam1(param1);

            logger.info("#STEP:03:: getDaumBlog start! url:" + req.getTg_url() + "/key:" + req.getParam1()+"/key2:"
                    +param1+"/movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);
            try {
                result = daumBlogService.getSearchBlog(req);

                result.addProperty("rt_code","OK");
                result.addProperty("rt_msg","SUCCESS");
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonArray());
                result.addProperty("rt_code","FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));
                e.printStackTrace();
            }
            logger.info("#STEP:03:: getDaumBlog end! url:" + req.getTg_url() + "/key:" + req.getParam1());
        } else {
            result = new JsonObject();
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg","getDaumBlog fail! params error!");
            logger.info("#STEP:03:: getDaumBlog fail! params error!:"+req.toString());
        }
        return result;
    }

    /* STEP 3naverMovie - sub
     * tg_url:NAVER_MOVIE 검색 후 메타 데이터 수집
    */
    @Override
    public JsonObject step03naverMovie(ConfTarget req) throws Exception {
        JsonObject result = null;
        if(req != null && req.getTg_url() != null && req.getParam1() != null) {
            String param1 = req.getParam1();
            String movietitle = req.getMovietitle() != null ? req.getMovietitle() : "";
            String moviedirector = req.getMoviedirector() != null ? req.getMoviedirector() : "";
            String movieyear = req.getMovieyear() != null ? req.getMovieyear() : "";
            if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }
            if (param1.contains("#movieyear") && !"".equals(movieyear)) {
                param1 = param1.replace("#movieyear", movieyear);
            } else {
                param1 = param1.replace("#movieyear", "");
            }
            if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
                param1 = param1.replace("#moviedirector", moviedirector);
            } else {
                param1 = param1.replace("#moviedirector", "");
            }
            req.setParam1(param1);

            logger.info("#STEP:03:: getNaverMovie start! url:" + req.getTg_url() + "/key:" + req.getParam1()+"/key2:"
                    +param1+"/movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);
            try {
                result = naverMovieService.getContents("NAVER_MOVIE", req);

                result.addProperty("rt_code","OK");
                result.addProperty("rt_msg","SUCCESS");
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonArray());
                result.addProperty("rt_code","FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));
                e.printStackTrace();
            }
            logger.info("#STEP:03:: getNaverMovie end! url:" + req.getTg_url() + "/key:" + req.getParam1());
        } else {
            result = new JsonObject();
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg","getNaverMovie fail! params error!");
            logger.info("#STEP:03:: getNaverMovie fail! params error!:"+req.toString());
        }
        return result;
    }

    /* STEP 3daumMovie - sub
     * tg_url:DAUM_MOVIE 검색 후 메타 데이터 수집
    */
    @Override
    public JsonObject step03daumMovie(ConfTarget req) throws Exception {
        JsonObject result = null;
        if(req != null && req.getTg_url() != null && req.getParam1() != null) {
            String param1 = req.getParam1();
            String movietitle = req.getMovietitle() != null ? req.getMovietitle() : "";
            String moviedirector = req.getMoviedirector() != null ? req.getMoviedirector() : "";
            String movieyear = req.getMovieyear() != null ? req.getMovieyear() : "";
            if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }
            if (param1.contains("#movieyear") && !"".equals(movieyear)) {
                param1 = param1.replace("#movieyear", movieyear);
            } else {
                param1 = param1.replace("#movieyear", "");
            }
            if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
                param1 = param1.replace("#moviedirector", moviedirector);
            } else {
                param1 = param1.replace("#moviedirector", "");
            }
            req.setParam1(param1);

            logger.info("#STEP:03:: getDaumMovie start! url:" + req.getTg_url() + "/key:" + req.getParam1()+"/key2:"
                    +param1+"/movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);
            try {
                result = naverMovieService.getContents("DAUM_MOVIE", req);

                result.addProperty("rt_code","OK");
                result.addProperty("rt_msg","SUCCESS");
            } catch (Exception e) {
                result = new JsonObject();
                result.add("result", new JsonArray());
                result.addProperty("rt_code","FAIL");
                result.addProperty("rt_msg",  (e.getCause() != null ? e.getCause().toString() : "")
                        + "_" + (e.getMessage() != null ? e.getMessage().toString() : ""));
                e.printStackTrace();
            }
            logger.info("#STEP:03:: getDaumMovie end! url:" + req.getTg_url() + "/key:" + req.getParam1());
        } else {
            result = new JsonObject();
            result.addProperty("rt_code","FAIL_PARAM");
            result.addProperty("rt_msg","getDaumMovie fail! params error!");
            logger.info("#STEP:03:: getDaumMovie fail! params error!:"+req.toString());
        }
        return result;
    }
}
