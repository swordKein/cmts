package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.Stats;

import java.util.List;

//@Service
public interface StatsServiceImpl {

    JsonObject getStatsForDash();

    int getCountInsertedDaily(Stats req);

    List<Stats> getCountItemsHistByType(Stats req);

    JsonObject getStatsList(int pageSize, int pageno, String searchSdate, String searchEdate, String searchStat);

    JsonObject getCountsForStat(Stats reqSt);
}
