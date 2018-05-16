package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.ConfTargetMapper;
import com.kthcorp.cmts.mapper.SchedTargetContentMapper;
import com.kthcorp.cmts.mapper.SchedTriggerMapper;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchedTriggerService implements SchedTriggerServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(SchedTriggerService.class);

    @Value("${property.serverid}")
    private String serverid;

    @Autowired
    private SchedTriggerMapper schedTriggerMapper;
    @Autowired
    private SchedTargetContentMapper schedTargetContentMapper;

    @Override
    public int uptSchedTriggerComplete(SchedTrigger req) {
        int rtcode = 0;
        if (req != null) {
            req.setProgs(1);
            rtcode = schedTriggerMapper.uptSchedTriggerProgs(req);
        }
        return rtcode;
    }

    @Override
    public int uptSchedTriggerOnlyStat(SchedTrigger req) {
        int rtcode = 0;
        if (req != null) {
            rtcode = schedTriggerMapper.uptSchedTriggerProgs(req);
        }
        return rtcode;
    }

    @Autowired
    private Environment environment;
    @Override
    @PostConstruct
    public int uptStoppedProcessingJobsStat() {
        int rt = 0;

        String[] prs = this.environment.getActiveProfiles();
        String activeProfile = "";
        for(String pr: prs) {
            System.out.println("#Spring-Boot ACTIVE PROFILE:" + pr.toString());
            activeProfile = pr;
        }
        if("dev".equals(activeProfile)) {
            rt = schedTriggerMapper.uptStoppedProcessingJobsStat();;
        }
        return rt;
    }

    @Override
    public int processCollectHearbit() throws Exception {
        int rt = 0;

        Map<String, Object> statCollect = schedTriggerMapper.getCollectorActive();
        Map<String, Object> reqMap = new HashMap();
        reqMap.put("stype", "collector");
        reqMap.put("serverid", serverid);
        if (statCollect == null) {
            rt = schedTriggerMapper.insCollectorActive(reqMap);
            logger.info("#SERVER.Collector stat is DEAD: new serverid::"+serverid+" rt:"+rt);
        } else {
            if (statCollect.get("serverid") != null && serverid.equals(statCollect.get("serverid"))) {
                rt = schedTriggerMapper.uptCollectorActive(reqMap);
                logger.info("#SERVER.Collector stat is ALIVE: serverid::"+serverid+" rt:"+rt);
            } else {
                logger.info("#SERVER.Collector stat is not Active: active serverid is ::"
                        + statCollect.get("serverid") + ". but.this serverid:" + serverid + " rt:" + rt);
            }
        }

        return rt;
    }

    @Override
    public boolean checkActiveServerByServerid() throws Exception {
        boolean isAlive = false;

        Map<String, Object> statCollect = schedTriggerMapper.getCollectorActive();
        if (statCollect != null && statCollect.get("serverid") != null && statCollect.get("serverid").equals(serverid)) {
            logger.info("#SERVER.Collector stat is ALIVE: serverid::"+serverid);
            isAlive = true;
        }
        return isAlive;
    }

    @Override
    public int deleteSchedTargetContentOrigin(Integer itemidx) {
        return schedTargetContentMapper.deleteSchedTargetContentOrigin(itemidx);
    }

}
