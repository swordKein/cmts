package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.SpringBootWebApplication;
import com.kthcorp.cmts.mapper.StatsMapper;
import com.kthcorp.cmts.model.Stats;
import com.kthcorp.cmts.util.DateUtils;
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
        int count_ready = statsMapper.mid_countReady();
        int count_fail_collect = statsMapper.mid_countFailCollect();
        int count_fail_analyze = statsMapper.mid_countFailAnalyze();
        int count_ready_tag = statsMapper.mid_countReadyTagging();
        //int count_ready_tag = count_inserted- count_endTagged;
        listSummary.addProperty("COUNT_READY", count_ready + count_ready_tag);
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
}
