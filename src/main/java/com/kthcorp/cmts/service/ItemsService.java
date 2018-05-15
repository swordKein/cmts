package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ItemsService implements ItemsServiceImpl {
    static Logger logger = LoggerFactory.getLogger(ItemsService.class);

    @Value("${property.serverid}")
    private String serverid;

    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsMetasMapper itemsMetasMapper;
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;
    @Autowired
    private ItemsSchedMappingMapper itemsSchedMappingMapper;
    @Autowired
    private SchedTargetMappingOrigMapper schedTargetMappingOrigMapper;
    @Autowired
    private InItemsMapper inItemsMapper;
    @Autowired
    private CcubeService ccubeService;
    @Autowired
    private ItemsTagsService itemsTagsService;
    @Autowired
    private ItemsHistMapper itemsHistMapper;

    @Override
    public int checkInItems() {
        int rt = 0;
        /* in_items manual insert movie */
        rt = processInItems();

        /* ccube_contents sftp inserted */
        rt = processCcubeContents();

        /* ccube_series sftp inserted */
        rt = processCcubeSeries();

        return rt;
    }

    @Override
    public int processInItems() {
        List<InItems> reqItems = this.get50ActiveInItems();
        logger.info("#SCHEDULE checkInItems.get50ActiveInItems: req-size:"+reqItems.size());

        int rt = 0;
        for (InItems ins : reqItems) {
            rt = this.copyInItemsToItems(ins);
            logger.info("#SCHEDULE checkInItems:Copy In-Item to Items Result:"+rt);

            int rtupt = 0;
            if (rt > 0) {
                ins.setStat("S");
            } else {
                ins.setStat("F");
            }
            rtupt = inItemsMapper.uptInItemsStat(ins);
        }
        return rt;
    }

    @Override
    public int processCcubeContents() {
        int rt = 0;

        //for(int i=0; i<20; i++) {
            List<CcubeContent> reqCcubeContents = ccubeService.get50ActiveCcubeContents();
            logger.info("#SCHEDULE checkInItems.get50ActiveCcubeContents: req-size:" + reqCcubeContents.size());

            for (CcubeContent ccont : reqCcubeContents) {
                //logger.info("#SCHEDULE checkInItems:req datas:"+ccont.toString());
                rt = this.copyCcubeContentToItems(ccont);
                logger.info("#SCHEDULE checkInItems:Copy Ccube_Contents to Items Result:" + rt);

                int rtupt = 0;
                if (rt > 0) {
                    ccont.setStat("S");
                } else {
                    ccont.setStat("F");
                }
                rtupt = ccubeService.uptCcubeContentStat(ccont);
            }
        //}
        return rt;
    }

    @Override
    public int processCcubeSeries() {
        int rt = 0;

        //for(int i=0; i<20; i++) {
            List<CcubeSeries> reqCcubeSeries = ccubeService.get50ActiveCcubeSeries();
            logger.info("#SCHEDULE checkInItems.get50ActiveCcubeSeries: req-size:" + reqCcubeSeries.size());

            for (CcubeSeries cser : reqCcubeSeries) {
                rt = this.copyCcubeSeriesToItems(cser);
                logger.info("#SCHEDULE checkInItems:Copy Ccube_Series to Items Result:" + rt);

                int rtupt = 0;
                if (rt > 0) {
                    cser.setStat("S");
                } else {
                    cser.setStat("F");
                }
                rtupt = ccubeService.uptCcubeSeriesStat(cser);
            }
        //}
        return rt;
    }

    @Override
    @Transactional
    public int copyInItemsToItems(InItems req) {
        int rtitem = 0;
        try {
            Items item = new Items();
            item.setType(req.getType());
            item.setCid((req.getCid() != null) ? req.getCid() : "0");
            item.setDirector((req.getDirector() != null) ? req.getDirector() : "");
            item.setYear((req.getYear() != null) ? req.getYear() : "");
            item.setTitle(req.getContent_title());
            item.setTitle1(req.getPurity_title());
            item.setTitle2(req.getTitleshort());
            item.setStat("Y");
            item.setRegid("sched");

            rtitem =  this.insItems(item);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtitem;
    }

    @Override
    @Transactional
    public int copyCcubeContentToItems(CcubeContent req) {
        int rtitem = 0;
        int itemIdx = 0;

        try {
            String type = "CcubeContent";
            if (req.getCountry_of_origin() != null && "KOR".equals(req.getCountry_of_origin().trim())) {
                type = "CcubeContentK";
            }

            // 중복방지로직, cid , title, director, year 순으로 대조
            CcubeKeys reqCk = new CcubeKeys();
            //if (req.getPurity_title() != null) reqCk.setPurity_title(req.getPurity_title().trim());
            if (req.getContent_id() != null) reqCk.setContent_id(req.getContent_id().trim());
            String title = (req.getPurity_title() != null && !"".equals(req.getPurity_title())) ? req.getPurity_title() : req.getContent_title();
            reqCk.setPurity_title(title);
            reqCk.setYear(req.getYear());
            reqCk.setDirector(req.getDirector());

            int oldItemIdx = ccubeService.getCcubeItemIdx(reqCk);
            if (oldItemIdx > 0) {
                itemIdx = oldItemIdx;
                System.out.println("### item Exists:: itemIdx:"+itemIdx+"  by  cid:"+reqCk.getContent_id()+"/title:"+title+"/year:"+reqCk.getYear()+"/director:"+reqCk.getDirector());

                // 2018.05.15
                // 기존 itemIdx가 있을 경우 ccube_keys에 키 등록
                CcubeKeys reqKey = new CcubeKeys();
                reqKey.setContent_id(req.getContent_id());
                reqKey.setMaster_content_id((req.getMaster_content_id() != null) ? req.getMaster_content_id() : "0");
                reqKey.setSeries_id("0");
                reqKey.setKmrb_id((req.getKmrb_id() != null) ? req.getKmrb_id() : "0");
                reqKey.setPurity_title(title);
                reqKey.setYear(req.getYear());
                reqKey.setDirector(req.getDirector());

                reqKey.setItemidx(oldItemIdx);
                int rtkeyei = ccubeService.insCcubeKeys(reqKey);
            }

            if (oldItemIdx < 1) {
                Items item = new Items();
                item.setType(type);
                item.setCid((req.getContent_id() != null) ? req.getContent_id() : "0");
                item.setDirector((req.getDirector() != null) ? req.getDirector() : "");
                item.setYear((req.getYear() != null) ? req.getYear() : "");
                item.setTitle((req.getPurity_title() != null && !"".equals(req.getPurity_title())) ? req.getPurity_title() : req.getContent_title());
                item.setTitle1(req.getContent_title());
                item.setTitle2(req.getEng_title());
                item.setStat("Y");
                item.setRegid("sched");

                rtitem = this.insItems(item);

                // 2018.04.20
                // 신규 itemIdx로 등록된 경우에도 ccube_keys에 키 등록
                CcubeKeys reqKey = new CcubeKeys();
                reqKey.setContent_id(req.getContent_id());
                reqKey.setMaster_content_id((req.getMaster_content_id() != null) ? req.getMaster_content_id() : "0");
                reqKey.setSeries_id("0");
                reqKey.setKmrb_id((req.getKmrb_id() != null) ? req.getKmrb_id() : "0");
                reqKey.setPurity_title((req.getPurity_title() != null && !"".equals(req.getPurity_title())) ? req.getPurity_title() : req.getContent_title());
                reqKey.setYear(req.getYear());
                reqKey.setDirector(req.getDirector());

                reqKey.setItemidx(rtitem);
                int rtkeyi = ccubeService.insCcubeKeys(reqKey);
            }

            if (rtitem > 0) {
                itemIdx = rtitem;
            }


            if (itemIdx > 0) {
                /* insert items_tags_keys by yj_items_out2 */
                /* 영진위 기준 수동 추출물이 있는 경우 items_tags_metas가 없을 때 승인완료 처리하면서 meta를 채워넣는다 */
                int tagsIdx = itemsTagsService.getCurrTagsIdxForInsert(itemIdx);
                if (tagsIdx < 1) {
                    ItemsTags reqit = new ItemsTags();
                    reqit.setIdx(itemIdx);
                    reqit.setTagidx(tagsIdx);
                    req.setStat("Y");
                    /* itemidx 로 yj_tags_metas 내역을 조회 */
                    List<ItemsTags> metasArr = itemsTagsService.getYjTagsMetasByItemidx(reqit);
                    int insertedCnt = 0;
                    if (metasArr != null) {
                        for (ItemsTags ym : metasArr) {
                            /* 기존 승인완료된 metas가 있는지 조회 */
                            ItemsTags tagsMetas = itemsTagsService.getItemsTagsMetasByItemIdxAndMtype(reqit);
                            boolean exist_Items_tags_metas = false;
                            if (tagsMetas != null && tagsMetas.getIdx() != null) {
                                exist_Items_tags_metas = true;
                            }
                            /* 승인된 metas가 존재하지 않으면 ym -> items_tags_metas로 등록 */
                            if (!exist_Items_tags_metas) {
                                ym.setIdx(itemIdx);
                                ym.setTagidx(tagsIdx);
                                int rtitm = itemsTagsService.insItemsTagsMetas(ym);
                                if (rtitm > 0) {
                                    insertedCnt++;
                                }
                            }
                        }
                    }
                    /* items_tags_metas에 등록된 건이 있는 경우 승인완료 처리 */
                    /* items_tags_keys(stat=S) ,  items_stat (stat=ST) , items (tagcnt++)*/
                    if (insertedCnt > 0) {
                        reqit.setStat("S");
                        int rt1 = itemsTagsService.uptItemsTagsKeysStat(reqit);
                        Items req1 = new Items();
                        req1.setIdx(itemIdx);
                        req1.setStat("ST");
                        int rt2 = itemsMapper.insItemsStat(req1);
                        int rt3 = itemsMapper.uptItemsTagcnt(req1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemIdx;
    }

    @Override
    @Transactional
    public int copyCcubeSeriesToItems(CcubeSeries req) {
        int rtitem = 0;
        int itemIdx = 0;

        try {
            String type = "CcubeSeries";
            if (req.getCountry_of_origin() != null && "KOR".equals(req.getCountry_of_origin().trim())) {
                type = "CcubeSeriesK";
            }

            // 중복방지로직, 기존 series_id,  추가 타이틀,연도,감독 순으로 대조
            CcubeKeys reqCk = new CcubeKeys();
            //if (req.getPurity_title() != null) reqCk.setPurity_title(req.getPurity_title().trim());
            if (req.getSeries_id() != null) reqCk.setSeries_id(req.getSeries_id().trim());
            if (req.getSeries_nm() != null) reqCk.setPurity_title(req.getSeries_nm().trim());
            reqCk.setYear(req.getYear());
            reqCk.setDirector(req.getDirector());

            int oldItemIdx = ccubeService.getCcubeItemIdx(reqCk);
            if (oldItemIdx > 0) {
                itemIdx = oldItemIdx;

                /*
                2018.05.15
                기존 itemIdx 있는 경우 ccube_keys 등록
                 */
                CcubeKeys reqKey = new CcubeKeys();
                reqKey.setContent_id("0");
                reqKey.setMaster_content_id("0");
                reqKey.setSeries_id(req.getSeries_id());
                reqKey.setKmrb_id("0");
                reqKey.setPurity_title(req.getSeries_nm().trim());
                reqKey.setItemidx(itemIdx);
                int rtkeyei = ccubeService.insCcubeKeys(reqKey);
            }

            if (oldItemIdx < 1) {
                Items item = new Items();
                item.setType(type);
                item.setCid((req.getSeries_id() != null) ? req.getSeries_id() : "0");
                item.setDirector((req.getDirector() != null) ? req.getDirector() : "");
                item.setYear((req.getYear() != null) ? req.getYear() : "");
                item.setTitle(req.getPurity_title());
                item.setTitle1(req.getSeries_nm());
                item.setTitle2(req.getEng_title());
                item.setStat("Y");
                item.setRegid("sched");

                rtitem = this.insItems(item);
                if (rtitem > 0) {
                    itemIdx = rtitem;
                    CcubeKeys reqKey = new CcubeKeys();
                    reqKey.setContent_id("0");
                    reqKey.setMaster_content_id("0");
                    reqKey.setSeries_id(req.getSeries_id());
                    reqKey.setKmrb_id("0");
                    reqKey.setPurity_title(req.getPurity_title());
                    reqKey.setItemidx(rtitem);
                    int rtkeyi = ccubeService.insCcubeKeys(reqKey);
                }
            }

            if (itemIdx > 0) {
                int tagsIdx = itemsTagsService.getCurrTagsIdxForInsert(itemIdx);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemIdx;
    }

    @Override
    public List<InItems> get50ActiveInItems() {
        return inItemsMapper.get50ActiveInItems();
    }

    @Override
    public int insItemsHist(int itemIdx, String type, String stat, String title, String action_type, int action_id) {
        ItemsHist req = new ItemsHist();
        req.setType(type);
        req.setIdx(itemIdx);
        req.setStat(stat);
        req.setTitle(title);
        req.setAction(action_type);
        req.setAction_id(action_id);
        req.setRegid(serverid);

        int rt = itemsHistMapper.insItemsHist(req);

        return rt;
    }

    @Override
    @Transactional
    public int insItems(Items req) {

        int rtItem =  itemsMapper.insItems(req);

        int newItemIdx = 0;

        if (rtItem > 0) {
            newItemIdx = req.getIdx();

            SchedTrigger newReq = new SchedTrigger();
            newReq.setParent_sc_id(0);
            newReq.setType("C");
            newReq.setDescript(req.getTitle()+ " 수집");
            newReq.setRegid(req.getRegid());

            //sched_trigger에 등록
            int rtSched = schedTriggerMapper.insSchedTriggerForStart(newReq);

            int newSc_id = newReq.getSc_id();
            System.out.println("#insItem insert sc_id:"+newReq.getSc_id()+ "   result:"+rtSched);
            if (newSc_id > 0) {
                //items_hist에 등록 for 통계
                int rthist = this.insItemsHist(newItemIdx, "items", "S", req.getTitle(), "IN_ITEMS", newItemIdx);

                //item_sched_mapping에 등록
                ItemsSchedMapping newISM = new ItemsSchedMapping();
                newISM.setIdx(newItemIdx);
                newISM.setSc_id(newSc_id);
                int rtISM = itemsSchedMappingMapper.insItemsSchedMapping(newISM);

                //기본 수집 스케쥴을 읽어서 sched_target_mapping에 등록한다.
                SchedTargetMappingOrig getOrig = new SchedTargetMappingOrig();
                getOrig.setType("C");
                getOrig.setRank(1);
                List<SchedTargetMappingOrig> origSchedList = schedTargetMappingOrigMapper.getSchedTargetMappingOrigList(getOrig);
                if (origSchedList != null) {
                    for (SchedTargetMappingOrig stmo : origSchedList) {
                        SchedTargetMapping newStm = new SchedTargetMapping();
                        newStm.setSc_id(newSc_id);
                        newStm.setTg_id(stmo.getTg_id());
                        int rtStm = schedTriggerMapper.insSchedTargetMapping(newStm);
                    }
                }
            }
        }
        return newItemIdx;
    }

    @Override
    @Transactional
    public int delItems(Items req) {
        int rt = 0;
        if (req != null) {
            ItemsSchedMapping reqISM = new ItemsSchedMapping();
            reqISM.setIdx(req.getIdx());
            rt = itemsSchedMappingMapper.delItemsSchedMapping(reqISM);
            rt = itemsMapper.delItems(req);
        }
        return rt;
    }

    @Override
    @Transactional
    public int insItemsMetas(ItemsMetas req) {
        if (req.getRegid() == null || "".equals(req.getRegid())) req.setRegid(serverid);
        return itemsMetasMapper.insItemsMetas(req);
    }

    @Override
    @Transactional
    public List<ItemsMetas> getItemsMetasByIdx(ItemsMetas req) {
        return itemsMetasMapper.getItemsMetasByIdx(req);
    }

    @Override
    @Transactional
    public ItemsMetas getItemsMetas(ItemsMetas req) {
        return itemsMetasMapper.getItemsMetas(req);
    }

    @Override
    @Transactional
    public int uptSchedTriggerStatByItemIdx(Items req) {
        int rt = 0;

        if (req != null) {
            int sc_id = itemsMapper.getScidByItemIdxAndType(req);

            if (sc_id > 0) {
                req.setSc_id(sc_id);
                rt = itemsMapper.uptSchedTriggerStatByScid(req);

                String histType = "";
                String histAction = "";
                if(rt > 0) {
                    if(req.getType().equals("C")) {
                        req.setStat("RC");
                        histType = "re_collect";
                        histAction = "RE_COLLECT";
                    } else if (req.getType().equals("A")) {
                        req.setStat("RR");
                        histType = "re_refine";
                        histAction = "RE_REFINE";
                    } else if (req.getType().equals("S")) {
                        req.setStat("RA");
                        histType = "re_analyze";
                        histAction = "RE_ANALYZE";
                    }

                    rt = itemsMapper.insItemsStat(req);

                    //items_hist에 등록 for 통계
                    Items itemInfo = this.getItemsByIdx(req);
                    String movietitle = "";
                    System.out.println("#RERTY action ITEMS_HIST by idx:"+req.getIdx()+" /type:"+req.getType()
                            +" /histType:"+histType+" /histAction:"+histAction);

                    movietitle = (itemInfo != null && itemInfo.getTitle() != null) ? itemInfo.getTitle().trim() : "";
                    int rthist = this.insItemsHist(req.getIdx(), histType, "UPT", movietitle, histAction, sc_id);

                }
            }
        }
        return rt;
    }

    @Override
    public int insItemsStat(Items req) {
        int rt = 0;
        if (req != null) {
            rt = itemsMapper.insItemsStat(req);

            int rtregdate = itemsMapper.uptItemsRegdate(req);
        }
        return rt;
    }
    @Override
    public int insItemsStatOne(int itemIdx, String type, String stat) {
        Items req = new Items();
        req.setIdx(itemIdx);
        String sstat = stat+type;
        if (sstat.equals("SA")) sstat = "RT";

        req.setStat(sstat);

        int rt = 0;
        if (req != null) {
            rt = itemsMapper.insItemsStat(req);
        }
        return rt;
    }

    @Override
    @Transactional
    public int uptSchedTriggerStatByItemIdxArray(Items req) {
        int rt = 0;
        if (req != null && req.getItemsIdxs() != null && !"".equals(req.getItemsIdxs())) {
            try {
                String toStat = req.getType();
                String itemsIdxs[] = req.getItemsIdxs().trim().split(",");
                for (String idx : itemsIdxs) {
                    req.setIdx(Integer.parseInt(idx));
                    switch(toStat) {
                        case "C": case "A": case "S":
                            int sc_id = itemsMapper.getScidByItemIdxAndType(req);

                            if (sc_id > 0) {
                                req.setSc_id(sc_id);
                                String toStat1 = toStat;
                                if(!"A".equals(toStat)) toStat1 = "R";
                                req.setStat("Y");
                                rt = itemsMapper.uptSchedTriggerStatByScid(req);

                                if (rt > 0) {
                                    if (req.getType().equals("C")) {
                                        req.setStat("RC");
                                    } else if (req.getType().equals("A")) {
                                        req.setStat("RR");
                                    } else if (req.getType().equals("S")) {
                                        req.setStat("RA");
                                    }

                                    rt = itemsMapper.insItemsStat(req);
                                }
                            }
                            break;
                        case "FT": case "RT":
                            if(!"".equals(idx)) {
                                Items reqIt = new Items();
                                reqIt.setIdx(Integer.parseInt(idx));
                                reqIt.setStat(toStat);
                                rt = this.insItemsStat(reqIt);
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rt;
    }

    @Override
    public Items getItemsByIdx(Items req) {
        return itemsMapper.getItemsByIdx(req);
    }

    @Override
    public int uptItemsTagcnt(Items req) {
        return itemsMapper.uptItemsTagcnt(req);
    }

    @Override
    public Items getItemInfoOne(int idx) {
        Items item = null;
        if (idx > 0) {
            item = itemsMapper.getItemsInfoByIdx(idx);
            ItemsMetas reqIm = new ItemsMetas();
            reqIm.setIdx(idx);
            List<ItemsMetas> metasList = itemsMetasMapper.getItemsMetasByIdx(reqIm);
            item.setMetaList(metasList);

            ItemsTags reqIt = new ItemsTags();
            reqIt.setIdx(idx);
            reqIt.setStat("S");
            List<ItemsTags> tagsList = itemsTagsService.getItemsTagsMetasByItemIdx(reqIt);
            item.setTagsMetasList(tagsList);
        }

        return item;
    }
}
