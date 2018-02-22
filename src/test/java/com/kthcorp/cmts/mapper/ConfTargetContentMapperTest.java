package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTargetContent;
import com.kthcorp.cmts.model.ConfTargetHist;
import com.kthcorp.cmts.model.SchedTrigger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@Transactional
public class ConfTargetContentMapperTest {
    @Autowired
    private ConfTargetContentMapper confTargetContentMapper;
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;

    @Test
    public void test_getNewActive50CollectSchedTriggerList() throws Exception{
        SchedTrigger req = new SchedTrigger();
        req.setType("C");
        req.setStat("Y");

        List<SchedTrigger> result = schedTriggerMapper.get50ByTypeStatSchedTriggerList(req);
        System.out.println("#Result:"+result);
    }

    @Test
    public void test_insConfTargetContent() throws Exception{
        ConfTargetContent req = new ConfTargetContent();
        req.setTg_id(1);
        req.setContent("테스트 컨텐츠");

        int rt = confTargetContentMapper.insConfTargetContent(req);

        SchedTrigger reqs = new SchedTrigger();
        reqs.setType("C");
        reqs.setStat("Y");

        List<SchedTrigger> result = schedTriggerMapper.get50ByTypeStatSchedTriggerList(reqs);
        System.out.println("#Result:"+result);
    }

}
