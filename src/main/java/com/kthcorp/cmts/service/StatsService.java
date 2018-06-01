package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.SpringBootWebApplication;
import com.kthcorp.cmts.mapper.ItemsMapper;
import com.kthcorp.cmts.mapper.StatsMapper;
import com.kthcorp.cmts.model.Items;
import com.kthcorp.cmts.model.Stats;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.DateUtils;
import com.kthcorp.cmts.util.JsonUtil;
import com.kthcorp.cmts.util.pool.concurrent.mgr.GenericTaskThreadPoolExecutor;
import com.kthcorp.cmts.util.pool.concurrent.task.JobTask;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.GenericTaskArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService implements StatsServiceImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StatsMapper statsMapper;
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ApiService apiService;

    @Override
    public JsonObject getStatsForDash() {
        JsonObject result = new JsonObject();

        int count_inserted = statsMapper.top_countInItems();
        int count_endTagged = statsMapper.top_countEndTagged();
        int count_start_collect = statsMapper.top_countCollecting();
        int count_collected = statsMapper.top_countCollected();
        int count_start_analyze = statsMapper.top_countAnalyzing();
        int count_analyzed = statsMapper.top_countAnalyzed();
        int count_start_tag = statsMapper.top_countTagging();
        int count_tagged = statsMapper.top_countTagged();
        JsonObject listStat = new JsonObject();
        listStat.addProperty("COUNT_INSERTED", count_inserted);
        listStat.addProperty("COUNT_INSERT_TAGGED", count_endTagged);
        listStat.addProperty("COUNT_START_COLLECT", count_start_collect);
        listStat.addProperty("COUNT_COLLECTED", count_collected);
        listStat.addProperty("COUNT_START_ANALYZE", count_start_analyze);
        listStat.addProperty("COUNT_ANALYZED", count_analyzed);
        listStat.addProperty("COUNT_START_TAG", count_start_tag);
        listStat.addProperty("COUNT_TAGGED", count_tagged);

        result.add("LIST_STAT", listStat);

        JsonObject listSummary = new JsonObject();
        //int count_ready = statsMapper.mid_countReady();

        int count_fail_collect = statsMapper.mid_countFailCollect();
        int count_fail_analyze = statsMapper.mid_countFailAnalyze();
        int count_ready_tag = statsMapper.mid_countReadyTagging();
        int count_ready = count_fail_collect + count_fail_analyze + count_ready_tag;
                //int count_ready_tag = count_inserted- count_endTagged;
        listSummary.addProperty("COUNT_READY", count_ready);
        listSummary.addProperty("COUNT_FAIL_COLLECT", count_fail_collect);
        listSummary.addProperty("COUNT_FAIL_ANALYZE", count_fail_analyze);
        listSummary.addProperty("COUNT_READY_TAG", count_ready_tag);
        result.add("LIST_SUMMARY", listSummary);

        JsonObject listRatio = new JsonObject();
        double ratio_all_tag = (double) count_tagged / (double) count_inserted * 100.0;
        double ratio_collect = (double) ( count_inserted - count_fail_collect ) / (double) count_inserted * 100.0;
        double ratio_analyze = (double) ( count_inserted - count_fail_analyze ) / (double) count_inserted * 100.0;
        //double ratio_tag = (double) count_tagged / (double) count_inserted * 100.0;

        double ratio_tag = (double) count_endTagged / (double) count_inserted * 100.0;
        listRatio.addProperty("RATIO_ALL_TAG", Double.toString(ratio_tag));
        //System.out.println("# "+count_tagged +" / "+count_inserted+ " = ratio_tag:"+ratio_tag);
        listRatio.addProperty("RATIO_COLLECT", ratio_collect);
        listRatio.addProperty("RATIO_ANALYZE", ratio_analyze);
        listRatio.addProperty("RATIO_TAG", ratio_tag);
        result.add("LIST_RATIO", listRatio);

        JsonObject listGrWeek = new JsonObject();
        JsonArray grw_cap = new JsonArray();
        grw_cap.add("W6");
        grw_cap.add("W5");
        grw_cap.add("W4");
        grw_cap.add("W3");
        grw_cap.add("W2");
        grw_cap.add("W1");
        listGrWeek.add("LIST_CATION", grw_cap);

        JsonArray grw_ins = new JsonArray();
        grw_ins.add(0);
        grw_ins.add(0);
        grw_ins.add(0);
        grw_ins.add(0);
        grw_ins.add(0);
        grw_ins.add(0);
        listGrWeek.add("LIST_COUNT_INSERTED", grw_ins);
        listGrWeek.add("LIST_COUNT_COLLECTED", grw_ins);
        listGrWeek.add("LIST_COUNT_ANALYZED", grw_ins);
        listGrWeek.add("LIST_COUNT_TAGGED", grw_ins);

        result.add("LIST_GRAAPH_WEEKLY", listGrWeek);

        JsonObject listGrDay = new JsonObject();
        JsonArray grd_cap = new JsonArray();
        grd_cap.add("D7"); grd_cap.add("D6"); grd_cap.add("D5"); grd_cap.add("D4"); grd_cap.add("D3"); grd_cap.add("D2"); grd_cap.add("D1");
        listGrDay.add("LIST_CAPTION", grd_cap);

        List<String> grDays = DateUtils.getArrayDateFromInput(-6);
        String sdate = grDays.get(0);
        String edate = grDays.get(grDays.size()-1);
        Stats reqStats = new Stats();
        reqStats.setSdate(sdate);
        reqStats.setEdate(edate);
        List<Stats> listCountInserted = statsMapper.down_countsInserted(reqStats);
        List<Stats> listCountCollected = statsMapper.down_countsCollected(reqStats);
        List<Stats> listCountAnalyzed = statsMapper.down_countsAnalyzed(reqStats);
        List<Stats> listCountTagged = statsMapper.down_countsTagged(reqStats);

        JsonArray listCountInsertedArr = getArrayByEmptyDay(listCountInserted, grDays);
        JsonArray listCountCollectedArr = getArrayByEmptyDay(listCountCollected, grDays);
        JsonArray listCountAnalyzedArr = getArrayByEmptyDay(listCountAnalyzed, grDays);
        JsonArray listCountTaggedArr = getArrayByEmptyDay(listCountTagged, grDays);
        listGrDay.add("LIST_COUNT_INSERTED", listCountInsertedArr);
        listGrDay.add("LIST_COUNT_COLLECTED", listCountCollectedArr);
        listGrDay.add("LIST_COUNT_ANALYZED", listCountAnalyzedArr);
        listGrDay.add("LIST_COUNT_TAGGED", listCountTaggedArr);
        result.add("LIST_GRAPH_DAILY", listGrDay);
        /*
  ,"LIST_GRAPH_DAILY":{
            "LIST_CAPTION":["D6","D5","D4","D3","D2","D1"]
    ,"LIST_COUNT_INSERTED":[10,7,6,4,8,7]
    ,"LIST_COUNT_COLLECTED":[9,7,4,4,8,6]
    ,"LIST_COUNT_ANALYZED":[9,7,4,4,8,6]
    ,"LIST_COUNT_TAGGED":[9,7,4,4,8,6]

        }
        */
  /*
  {
"RT_CODE":1,					RT_CODE : 리턴코드 :: 1(정상), -1(일반오류), -2(트랜잭션오류), -3(권한오류), -4(기간만료), -999(시스템오류)
"RT_MSG":"SUCCESS"
"RESULT": {
  "LIST_STAT":  {					상단 통계 항목 리스트
    "COUNT_INSERTED":200					  - 입수완료 건수
    ,"COUNT_INSERT_TAGGED":178					  - 태깅완료 건수
    ,"COUNT_START_COLLECT":182					  - 수집진행 건수
    ,"COUNT_COLLECTED":180					  - 수집완료 건수
    ,"COUNT_START_ANALYZE":179					  - 추출진행 건수
    ,"COUNT_ANALYZED":179					  - 추출완료 건수
    ,"COUNT_START_TAG":178					  - 승인진행 건수
    ,"COUNT_TAGGED":178					  - 승인완료 건수
  }
  ,"LIST_SUMMARY":{					중단 통계 항목 리스트
    "COUNT_READY":100					  - 처리대기 건수
    ,"COUNT_FAIL_COLLECT":12					  - 수집실패 건수
    ,"COUNT_FAIL_ANALYZE":0					  - 추출실패 건수
    ,"COUNT_READY_TAG":2					  - 승인대기 건수
  }
  ,"LIST_RATIO":{					좌측 하단 비율 리스트
    "RATIO_ALL_TAG":178					  - 태깅 비율
    ,"RATIO_COLLECT":95					  - 수집 비율
    ,"RATIO_ANALYZE":95					  - 추출 비율
    ,"RATIO_TAG":95					  - 승인 비율
  }
  ,"LIST_GRAPH_WEEKLY":{					우측하단 주별 그래프
    "LIST_CAPTION":["W6","W5","W4","W3","W2","W1"]				array	  - X축 타이틀 리스트
    ,"LIST_COUNT_INSERTED":[10,7,6,4,8,7]				array	  - 인입 건수 리스트
    ,"LIST_COUNT_COLLECTED":[9,7,4,4,8,6]				array	  - 수집 건수 리스트
    ,"LIST_COUNT_ANALYZED":[9,7,4,4,8,6]				array	  - 추출 건수 리스트
    ,"LIST_COUNT_TAGGED":[9,7,4,4,8,6]				array	  - 승인 건수 리스트
  }
  ,"LIST_GRAPH_DAILY":{					우측하단 일별 그래프
    "LIST_CAPTION":["D6","D5","D4","D3","D2","D1"]				array	  - X축 타이틀 리스트
    ,"LIST_COUNT_INSERTED":[10,7,6,4,8,7]				array	  - 인입 건수 리스트
    ,"LIST_COUNT_COLLECTED":[9,7,4,4,8,6]				array	  - 수집 건수 리스트
    ,"LIST_COUNT_ANALYZED":[9,7,4,4,8,6]				array	  - 추출 건수 리스트
    ,"LIST_COUNT_TAGGED":[9,7,4,4,8,6]				array	  - 승인 건수 리스트
  }
}
}

   */
        return result;
    }

    private JsonArray getArrayByEmptyDay(List<Stats> stats, List<String> days) {
        JsonArray result = new JsonArray();

        for (String sd : days) {
            boolean isHit = false;
            for (Stats s : stats) {
                if (s != null && s.getDate() != null
                        && sd.equals(s.getDate())) {
                    result.add(s.getCnt());
                    isHit = true;
                }
            }
            if (!isHit) {
                result.add(0);
            }
        }

        return result;
    }

    @Override
    public int getCountInsertedDaily(Stats req) {
        return statsMapper.getCountInsertedDaily(req);
    }
    @Override
    public List<Stats> getCountItemsHistByType(Stats req) {
        return statsMapper.getCountItemsHistByType(req);
    }

    @Override
    public JsonObject getStatsList(int pageSize, int pageno, String searchSdate, String searchEdate, String searchStat) {
        JsonObject result = new JsonObject();

        Items reqIt = new Items();
        reqIt.setPageSize(pageSize);
        reqIt.setPageNo(pageno);

        String newSdate = "";
        String nSdate = "";
        String newEdate = "";
        String nEdate = "";
        if ("".equals(searchSdate) || "".equals(searchEdate)) {
            newSdate = "2018-02-01 00:00:00"; newEdate = "2025-12-31 23:59:59";
            nSdate = "2018-02-01"; nEdate = "2025-12-31";
        } else {
            newSdate = searchSdate + " 00:00:00";
            newEdate = searchEdate + " 23:59:59";
            nSdate = searchSdate;
            nEdate = searchEdate;
        }
        reqIt.setSearchSdate(newSdate);
        reqIt.setSearchEdate(newEdate);
        //reqIt.setStat("ST");
        reqIt.setStat(searchStat);


        Stats reqSt = new Stats();
        reqSt.setSdate(nSdate);
        reqSt.setEdate(nEdate);

        //int countItems = itemsMapper.countItems(reqIt);
        //System.out.println("#ELOG.searchItems:: req:"+reqIt.toString());
        int countItems = itemsMapper.countItemsPaging(reqIt);
        int countAll = itemsMapper.countItemsAll();

        System.out.println("#COUNT_SEARCH_ITEMS:: / count:"+countItems);

        List<Items> list_items = itemsMapper.searchItemsPaging(reqIt);
        JsonArray listItems = apiService.getListItemsFromArray(list_items);
        //n1.addProperty("STAT", "RT");
        //n1.addProperty("CNT_IN", 1);
        //n1.addProperty("CNT_COL", 1);
        //n1.addProperty("CNT_ANA", 1);
        //n1.addProperty("CNT_TAG2", 1);

        System.out.println("#LIST_ITEMS:"+list_items.toString());

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
        result.addProperty("SEARCHSDATE", searchSdate);
        result.addProperty("SEARCHEDATE", searchEdate);

        //JsonObject countsSearch = apiService.getCountSearch(countAll, reqIt);
        //countsSearch.addProperty("COUNT_ALL", countAll);
        //result.add("COUNTS_SEARCH", countsSearch);

        //result.addProperty("PAGESIZE", pageSize);
        //result.addProperty("PAGENO", pageno);
        result.add("LIST_PAGING", listPageArr);
        result.add("LIST_PAGING_ACTIVE", listActiveArr);
        result.add("LIST_ITEMS", listItems);

        JsonObject stat_search = this.getCountsForStat(reqSt);
//        if (stat_search != null && stat_search.get("COUNT_IN") != null) {
//            stat_search.remove("COUNT_IN");
//            stat_search.addProperty("COUNT_IN", countItems);
//        }
        result.add("COUNTS_STAT", stat_search);

        result.add("LIST_ITEMS", listItems);

        return result;
    }

    private JsonObject getCountsForStat_old(Stats reqSt) {
        int count_in = statsMapper.getCountInsertedDaily(reqSt);
        int count_sc = 0;
        int count_fc = 0;
        int count_sa = 0;
        int count_fa = 0;
        List<Stats> listCounts1 = statsMapper.getCountItemsHistByType(reqSt);
        if (listCounts1 != null) {
            for (Stats s1 : listCounts1) {
                if (s1 != null && s1.getType() != null && s1.getStat() != null) {
                    String type = s1.getType();
                    String stat = s1.getStat();
                    int cnt = s1.getCnt();

                    if ("collect".equals(type)) {
                        if ("S".equals(stat)) {
                            count_sc += cnt;
                        } else if ("F".equals(stat)) {
                            count_fc += cnt;
                        }
                    } else if ("analyze".equals(type)) {
                        if ("S".equals(stat)) {
                            count_sa += cnt;
                        } else if ("F".equals(stat)) {
                            count_fa += cnt;
                        }
                    }
                }
            }
        }

        int count_st = 0;
        int count_rt = 0;
        int count_ft = 0;
        List<Stats> listCounts2 = statsMapper.getCountsItemsStatByStat(reqSt);
        if (listCounts2 != null) {
            for (Stats s2 : listCounts2) {
                if (s2 != null && s2.getStat() != null) {
                    String stat = s2.getStat();
                    int cnt = s2.getCnt();

                    if ("ST".equals(stat)) {
                        count_st += cnt;
                    } else if ("RT".equals(stat)) {
                        count_rt += cnt;
                    } else if ("FT".equals(stat)) {
                        count_ft += cnt;
                    } else if ("FR".equals(stat)) {
                        count_ft += cnt;
                    } else if ("FA".equals(stat)) {
                        count_ft += cnt;
                    } else if ("FC".equals(stat)) {
                        count_ft += cnt;
                    }
                }
            }
        }

        JsonObject stat_search = new JsonObject();
        stat_search.addProperty("COUNT_IN", count_in);
        stat_search.addProperty("COUNT_SC", count_sc);
        stat_search.addProperty("COUNT_FC", count_fc);
        stat_search.addProperty("COUNT_SA", count_sa);
        stat_search.addProperty("COUNT_FA", count_fa);
        stat_search.addProperty("COUNT_ST", count_st);
        stat_search.addProperty("COUNT_RT", count_rt);
        stat_search.addProperty("COUNT_FT", count_ft);

        return stat_search;
    }

    private JsonObject getCountsForStat(Stats reqSt) {
        int count_in = statsMapper.getCountInsertedDaily(reqSt);
        int count_sc = 0;
        int count_fc = 0;
        int count_sa = 0;
        int count_fa = 0;
        List<Stats> listCounts1 = statsMapper.getCountItemsHistByType(reqSt);
        if (listCounts1 != null) {
            for (Stats s1 : listCounts1) {
                if (s1 != null && s1.getType() != null && s1.getStat() != null) {
                    String type = s1.getType();
                    String stat = s1.getStat();
                    int cnt = s1.getCnt();

                    if ("collect".equals(type)) {
                        if ("S".equals(stat)) {
                            count_sc += cnt;
                        } else if ("F".equals(stat)) {
                            count_fc += cnt;
                        }
                    } else if ("analyze".equals(type)) {
                        if ("S".equals(stat)) {
                            count_sa += cnt;
                        } else if ("F".equals(stat)) {
                            count_fa += cnt;
                        }
                    }
                }
            }
        }

        int count_st = 0;
        int count_rt = 0;
        int count_ft = 0;
        List<Stats> listCounts2 = statsMapper.getCountsItemsStatByStat(reqSt);
        if (listCounts2 != null) {
            for (Stats s2 : listCounts2) {
                if (s2 != null && s2.getStat() != null) {
                    String stat = s2.getStat();
                    int cnt = s2.getCnt();

                    if ("ST".equals(stat)) {
                        count_st += cnt;
                    } else if ("RT".equals(stat)) {
                        count_rt += cnt;
                    } else if ("FT".equals(stat)) {
                        count_ft += cnt;
                    } else if ("FR".equals(stat)) {
                        count_ft += cnt;
                    } else if ("FA".equals(stat)) {
                        count_ft += cnt;
                    } else if ("FC".equals(stat)) {
                        count_ft += cnt;
                    }
                }
            }
        }

        List<Stats> listCountsUniq = statsMapper.getCountsItemsByStatUniq(reqSt);
        int count_sc_u = 0;
        int count_fc_u = 0;
        int count_sa_u = 0;
        int count_fa_u = 0;
        int count_st_u = 0;
        int count_rt_u = 0;
        int count_ft_u = 0;


        JsonObject stat_search = new JsonObject();
        stat_search.addProperty("COUNT_IN", count_in);
        stat_search.addProperty("COUNT_SC", count_sc);
        stat_search.addProperty("COUNT_FC", count_fc);
        stat_search.addProperty("COUNT_SA", count_sa);
        stat_search.addProperty("COUNT_FA", count_fa);
        stat_search.addProperty("COUNT_ST", count_st);
        stat_search.addProperty("COUNT_RT", count_rt);
        stat_search.addProperty("COUNT_FT", count_ft);

        return stat_search;
    }
}
