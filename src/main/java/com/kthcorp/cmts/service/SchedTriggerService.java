package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.ConfTargetMapper;
import com.kthcorp.cmts.mapper.SchedTriggerMapper;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class SchedTriggerService implements SchedTriggerServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(SchedTriggerService.class);

    @Autowired
    private SchedTriggerMapper schedTriggerMapper;

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

}
