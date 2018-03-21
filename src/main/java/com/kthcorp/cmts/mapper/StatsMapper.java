package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.InItems;
import com.kthcorp.cmts.model.Stats;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StatsMapper {
    int top_countInItems();
    int top_countEndTagged();
    int top_countCollecting();
    int top_countCollected();
    int top_countAnalyzing();
    int top_countAnalyzed();
    int top_countTagging();
    int top_countTagged();

    int mid_countReady();
    int mid_countFailCollect();
    int mid_countFailAnalyze();
    int mid_countReadyTagging();
    //mid_down_stats

    //down_stats
    List<Stats> down_countsInserted(Stats req);
    List<Stats> down_countsCollected(Stats req);
    List<Stats> down_countsAnalyzed(Stats req);
    List<Stats> down_countsTagged(Stats req);

    //stat page
    int getCountInsertedDaily(Stats req);
    List<Stats> getCountItemsHistByType(Stats req);
    List<Stats> getCountsItemsStatByStat(Stats req);
}
