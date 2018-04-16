package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTargetMapping;
import com.kthcorp.cmts.model.SchedTrigger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@Transactional
public class SchedTriggerMapperTest {
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;

    @Test
    public void test_getSchedTriggerList() throws Exception{
        List<SchedTrigger> result = schedTriggerMapper.getSchedTriggerList();
        System.out.println("#Result:"+result);
    }

    @Test
    public void test_getConfTargetListByScid() throws Exception{
        SchedTrigger req = new SchedTrigger();
        req.setSc_id(9319);
        List<ConfTarget> result = schedTriggerMapper.getConfTargetListByScid(req);
        System.out.println("#Result:"+result);
    }

    @Test
    public void test_getNewActive50CollectSchedTriggerList() throws Exception{
        SchedTrigger reqs = new SchedTrigger();
        reqs.setType("C");
        reqs.setStat("Y");

        List<SchedTrigger> result = schedTriggerMapper.get50ByTypeStatSchedTriggerList(reqs);
        System.out.println("#Result:"+result);
    }

    @Test
    public void test_getNewActive50AnalyzeSchedTriggerList() throws Exception{
        SchedTrigger req = new SchedTrigger();
        req.setType("A");
        req.setStat("Y");

        List<SchedTrigger> result = schedTriggerMapper.get50ByTypeStatSchedTriggerList(req);
        System.out.println("#Result:"+result);
    }

    @Test
    public void test_getSchedTriggerListByItemIdx() throws Exception{
        List<SchedTrigger> result = schedTriggerMapper.getSchedTriggerListByItemIdx(1);
        System.out.println("#Result:"+result.toString());
    }


    @Test
    public void test_getSchedTriggerListById() throws Exception{
        List<SchedTrigger> result = schedTriggerMapper.getSchedTriggerListById(1);
        System.out.println("#Result:"+result);
    }

    @Test
    @Rollback(false)
    public void test_uptSchedTriggerProgs() throws Exception{

        SchedTrigger req = new SchedTrigger();
        req.setSc_id(1);
        //req.setStat("S");
        req.setProgs(1);
        //int result = schedTriggerMapper.uptSchedTriggerProgs(req);
        //System.out.println("#Result:"+result);

        SchedTrigger result1 = schedTriggerMapper.getSchedTriggerById(1);
        int targetList_length = result1.getTargetList().size();
        int successList_length = 0;
        if (result1.getTargetList() != null && targetList_length > 0) {
            for (ConfTarget ct : result1.getTargetList()) {
                if(ct != null && ct.getStattarget() != null
                        && "S".equals(ct.getStattarget().toString())) {
                    successList_length++;
                }
            }
        }
        if (targetList_length == successList_length) {
            req = new SchedTrigger();
            req.setSc_id(1);
            req.setStat("S");
            req.setProgs(1);
            //int result2 = schedTriggerMapper.uptSchedTriggerProgs(req);
            //System.out.println("#Result upt:"+result2+" <<<<  by req:"+req.toString());
        }
        System.out.println("#Result:"+result1);
    }

    @Autowired
    private ConfTargetMapper confTargetMapper;

    @Test
    @Rollback(false)
    public void test_insSchedTriggerForStart() {
        SchedTrigger newReq = new SchedTrigger();
        newReq.setParent_sc_id(0);
        newReq.setType("C");
        newReq.setDescript("2테스트 수집");
        newReq.setRegid("ghkdwo77");

        // 먼저 sched_trigger에 등록
        //int rt = schedTriggerMapper.insSchedTriggerForStart(newReq);
        //System.out.println("# insert sc_id:"+newReq.getSc_id()+ "   result:"+rt);

        // sched_trigger에 등록한 sc_id 와 stat = 'P'인 conf_target 리스트를 조합하여 sched_target_mapping 등록

        ConfTarget req = new ConfTarget();
        req.setPageNo(1);
        req.setPageSize(10);
        req.setOrderType("ASC");
        req.setStat("P");

        List<ConfTarget> targetList = confTargetMapper.getTargetListByStat(req);
        for(ConfTarget ct : targetList) {
            SchedTargetMapping reqM = new SchedTargetMapping();
            reqM.setSc_id(newReq.getSc_id());
            reqM.setTg_id(ct.getTg_id());
            //int rtM = schedTriggerMapper.insSchedTargetMapping(reqM);
            //System.out.println("# insert targetMapping sc_id:"+newReq.getSc_id()+ "   result:"+rtM);
        }

        SchedTrigger result1 = schedTriggerMapper.getSchedTriggerById(newReq.getSc_id());
        System.out.println("# result:"+result1.toString());
    }

    @Test
    @Rollback(false)
    public void test_insSchedTriggerForRefine() {
        SchedTrigger newReq = new SchedTrigger();
        newReq.setParent_sc_id(18);
        newReq.setType("R");
        newReq.setDescript("2테스트 분석");
        newReq.setRegid("ghkdwo77");

        // 먼저 sched_trigger에 등록
        //int rt = schedTriggerMapper.insSchedTriggerForStart(newReq);
        //System.out.println("# insert sc_id:" + newReq.getSc_id() + "   result:" + rt);

        // sched_trigger에 등록한 sc_id 와 stat = 'P'인 conf_target 리스트를 조합하여 sched_target_mapping 등록

        ConfTarget req = new ConfTarget();
        req.setPageNo(1);
        req.setPageSize(10);
        req.setOrderType("ASC");
        req.setStat("P");

        List<ConfTarget> targetList = confTargetMapper.getTargetListByStat(req);
        for (ConfTarget ct : targetList) {
            SchedTargetMapping reqM = new SchedTargetMapping();
            reqM.setSc_id(newReq.getSc_id());
            reqM.setTg_id(ct.getTg_id());
            //int rtM = schedTriggerMapper.insSchedTargetMapping(reqM);
            //System.out.println("# insert targetMapping sc_id:" + newReq.getSc_id() + "   result:" + rtM);
        }

        SchedTrigger result1 = schedTriggerMapper.getSchedTriggerById(newReq.getSc_id());
        System.out.println("# result:" + result1.toString());
    }

    @Test
    public void test_getSchedTriggerById() {

        SchedTrigger result1 = schedTriggerMapper.getSchedTriggerById(1);
        System.out.println("# result:" + result1.toString());
    }


    @Test
    public void test_getParentSchedTargetMapping() {
        int sc_id = 1901;
        List<SchedTargetMapping> targetList = schedTriggerMapper.getParentSchedTargetMapping(sc_id);
        System.out.println("#Result:"+targetList.toString());
    }
}
