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

    @Value("${cmts.property.serverid}")
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
        List<CcubeContent> reqCcubeContents = ccubeService.get50ActiveCcubeContents();
        logger.info("#SCHEDULE checkInItems.get50ActiveCcubeContents: req-size:"+reqCcubeContents.size());
        int rt = 0;

        for (CcubeContent ccont : reqCcubeContents) {
            rt = this.copyCcubeContentToItems(ccont);
            logger.info("#SCHEDULE checkInItems:Copy Ccube_Contents to Items Result:"+rt);

            int rtupt = 0;
            if (rt > 0) {
                ccont.setStat("S");
            } else {
                ccont.setStat("F");
            }
            rtupt = ccubeService.uptCcubeContentStat(ccont);
        }
        return rt;
    }

    @Override
    public int processCcubeSeries() {
        List<CcubeSeries> reqCcubeSeries = ccubeService.get50ActiveCcubeSeries();
        logger.info("#SCHEDULE checkInItems.get50ActiveCcubeSeries: req-size:"+reqCcubeSeries.size());
        int rt = 0;
        for (CcubeSeries cser : reqCcubeSeries) {
            rt = this.copyCcubeSeriesToItems(cser);
            logger.info("#SCHEDULE checkInItems:Copy Ccube_Series to Items Result:"+rt);

            int rtupt = 0;
            if (rt > 0) {
                cser.setStat("S");
            } else {
                cser.setStat("F");
            }
            rtupt = ccubeService.uptCcubeSeriesStat(cser);
        }
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

        try {
            Items item = new Items();
            item.setType("CcubeContent");
            item.setCid((req.getContent_id() != null) ? req.getContent_id() : "0");
            item.setDirector((req.getDirector() != null) ? req.getDirector() : "");
            item.setYear((req.getYear() != null) ? req.getYear() : "");
            item.setTitle(req.getPurity_title());
            item.setTitle1(req.getContent_title());
            item.setTitle2(req.getEng_title());
            item.setStat("Y");
            item.setRegid("sched");

            rtitem =  this.insItems(item);

            if (rtitem > 0) {
                CcubeKeys reqKey = new CcubeKeys();
                reqKey.setContent_id(req.getContent_id());
                reqKey.setMaster_content_id((req.getMaster_content_id() != null) ? req.getMaster_content_id() : "0");
                reqKey.setSeries_id("0");
                reqKey.setKmrb_id((req.getKmrb_id() != null) ? req.getKmrb_id() : "0");
                reqKey.setPurity_title(req.getPurity_title());
                reqKey.setItemidx(rtitem);
                int rtkey = ccubeService.insCcubeKeys(reqKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtitem;
    }

    @Override
    @Transactional
    public int copyCcubeSeriesToItems(CcubeSeries req) {
        int rtitem = 0;

        try {
            Items item = new Items();
            item.setType("CcubeSeries");
            item.setCid((req.getSeries_id() != null) ? req.getSeries_id() : "0");
            item.setDirector((req.getDirector() != null) ? req.getDirector() : "");
            item.setYear((req.getYear() != null) ? req.getYear() : "");
            item.setTitle(req.getPurity_title());
            item.setTitle1(req.getSeries_nm());
            item.setTitle2(req.getEng_title());
            item.setStat("Y");
            item.setRegid("sched");

            rtitem =  this.insItems(item);

            if (rtitem > 0) {
                CcubeKeys reqKey = new CcubeKeys();
                reqKey.setContent_id("0");
                reqKey.setMaster_content_id("0");
                reqKey.setSeries_id(req.getSeries_id());
                reqKey.setKmrb_id("0");
                reqKey.setPurity_title(req.getPurity_title());
                reqKey.setItemidx(rtitem);
                int rtkey = ccubeService.insCcubeKeys(reqKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtitem;
    }

    @Override
    public List<InItems> get50ActiveInItems() {
        return inItemsMapper.get50ActiveInItems();
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

                if(rt > 0) {
                    if(req.getType().equals("C")) {
                        req.setStat("RC");
                    } else if (req.getType().equals("R")) {
                        req.setStat("RR");
                    } else if (req.getType().equals("S")) {
                        req.setStat("RA");
                    }

                    rt = itemsMapper.insItemsStat(req);
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
                String toStat = req.getStat();
                String itemsIdxs[] = req.getItemsIdxs().trim().split(",");
                for (String idx : itemsIdxs) {
                    req.setIdx(Integer.parseInt(idx));
                    int sc_id = itemsMapper.getScidByItemIdxAndType(req);

                    if (sc_id > 0) {
                        req.setSc_id(sc_id);
                        req.setStat(toStat);
                        rt = itemsMapper.uptSchedTriggerStatByScid(req);

                        if (rt > 0) {
                            if (req.getType().equals("C")) {
                                req.setStat("RC");
                            } else if (req.getType().equals("R")) {
                                req.setStat("RR");
                            } else if (req.getType().equals("S")) {
                                req.setStat("RA");
                            }

                            rt = itemsMapper.insItemsStat(req);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rt;
    }
}
