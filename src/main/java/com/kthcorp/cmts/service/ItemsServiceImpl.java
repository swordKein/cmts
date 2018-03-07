package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service
public interface ItemsServiceImpl {

    int checkInItems();

    int processInItems();

    int processCcubeContents();

    int processCcubeSeries();

    @Transactional
    int copyInItemsToItems(InItems req);

    @Transactional
    int copyCcubeContentToItems(CcubeContent req);

    @Transactional
    int copyCcubeSeriesToItems(CcubeSeries req);

    List<InItems> get50ActiveInItems();

    int insItemsHist(int itemIdx, String type, String stat, String title, String action_type, int action_id);

    int insItems(Items req);

    @Transactional
    int delItems(Items req);

    @Transactional
    int insItemsMetas(ItemsMetas req);

    @Transactional
    List<ItemsMetas> getItemsMetasByIdx(ItemsMetas req);

    @Transactional
    ItemsMetas getItemsMetas(ItemsMetas req);

    @Transactional
    int uptSchedTriggerStatByItemIdx(Items req);

    int insItemsStat(Items req);

    int insItemsStatOne(int itemIdx, String type, String stat);

    @Transactional
    int uptSchedTriggerStatByItemIdxArray(Items req);
}
