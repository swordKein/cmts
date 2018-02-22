package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService implements AdminServiceImpl {
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;
    @Autowired
    private SchedTargetMappingHistMapper schedTargetMappingHistMapper;
    @Autowired
    private ConfTargetOrigMapper confTargetOrigMapper;
    @Autowired
    private ConfTargetMapper confTargetMapper;
    @Autowired
    private ConfPsMappingMapper confPsMappingMapper;
    @Autowired
    private ConfPresetMapper confPresetMapper;
    @Autowired
    private MovieCine21Mapper movieCine21Mapper;

    @Override
    public List<Items> getItemsList(Items req) {
        List<Items> result = itemsMapper.getPagedItems(req);
        System.out.println("#AdminService.getItemsList result:"+result.toString());

        return result;
    }

    @Override
    public Integer countItems(Items req) {
        return itemsMapper.countItems(req);
    }

    @Override
    public Items getItemsByIdx(Items req) {
        Items result = itemsMapper.getItemsByIdx(req);
        System.out.println("#AdminService.getItemsByIdx result:"+result.toString());

        return result;
    }

    @Override
    public List<SchedTrigger> getSchedTriggerListByItemIdx(int idx) {
        List<SchedTrigger> result = schedTriggerMapper.getSchedTriggerListByItemIdx(idx);
        return result;
    }

    @Override
    public List<SchedTargetMappingHist> getSchedTriggerListBySc_id(SchedTargetMappingHist req) {
        List<SchedTargetMappingHist> result = null;
        if (req != null && req.getType() != null) {
            switch(req.getType()) {
                case "C" :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListForLast(req);
                    break;
                // 수집이 아닌 경우 history는 tg_id = 0인 상태이므로 쿼리를 달리한다.
                default :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListForLast2(req);
                    break;
            }
        }
        return result;
    }

    @Override
    public List<SchedTargetMappingHist> getSchedTriggerHistListAll(SchedTargetMappingHist req) {
        List<SchedTargetMappingHist> result = null;
        if (req != null && req.getType() != null) {
            switch(req.getType()) {
                case "C" :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListAll(req);
                    break;
                //
                default :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListAll(req);
                    break;
            }
        }
        return result;
    }

    @Override
    public List<ConfTargetOrig> getConfTargetOrigList(ConfTargetOrig req) {
        return confTargetOrigMapper.getTargetListActivePage(req);
    }

    @Override
    public List<ConfTarget> getConfTargetList(ConfTarget req) {
        return confTargetMapper.getTargetListActivePage(req);
    }

    @Override
    public int uptSchedTriggerRetry(SchedTrigger req) { return schedTriggerMapper.uptSchedTriggerRetry(req); }

    @Override
    public int uptConfTarget(ConfTarget req) {
        return confTargetMapper.uptConfTarget(req);
    }

    @Override
    public int delConfTarget(ConfTarget req) {
        /*
        SchedTargetMapping reqStm = new SchedTargetMapping();
        reqStm.setTg_id(req.getTg_id());
        int resStm = schedTriggerMapper.delSchedTargetMapping(reqStm);

        ConfPsMapping reqCsm = new ConfPsMapping();
        reqCsm.setTg_id(req.getTg_id());
        int resCsm = confPsMappingMapper.delConfPsMapping(reqCsm);

        int res = confTargetMapper.delConfTarget(req);
        */
        req.setStat("D");
        int res = confTargetMapper.uptTargetStat(req);
        return res;
    }

    @Override
    public int uptConfPreset(ConfPreset req) {
        return confPresetMapper.uptConfPreset(req);
    }

    @Override
    public int delConfPreset(ConfPreset req) {
        ConfPsMapping reqCpm = new ConfPsMapping();
        reqCpm.setPs_id(req.getPs_id());
        int resCpm = confPsMappingMapper.delConfPsMappingByPsId(reqCpm);

        //int res = confPresetMapper.delConfPreset(req);
        return resCpm;
    }

    @Override
    public List<MovieCine21> getMovieCine21(MovieCine21 req) {
        return movieCine21Mapper.getMovieCine21(req);
    }
    @Override
    public int cntMovieCine21(MovieCine21 req) {
        return movieCine21Mapper.cntMovieCine21(req);
    }


}
