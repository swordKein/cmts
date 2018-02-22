package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTriggerHist;
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
public class SchedTriggerHistMapperTest {

    @Autowired
    private SchedTriggerHistMapper schedTriggerHistMapper;



    @Test
    public void test_insSchedTriggerHist() throws Exception{
    SchedTriggerHist req = new SchedTriggerHist();
    req.setSc_id(1);
    req.setType("C");
    req.setStat("S");
    req.setRt_code("OK");
    req.setRt_msg("SUCCESS");
    req.setSummary("summary is ok");

    int rs = schedTriggerHistMapper.insSchedTriggerHist(req);

    System.out.println("#RS:"+rs);

    SchedTriggerHist reqH = new SchedTriggerHist();
    reqH.setPageNo(1);
    reqH.setPageSize(10);

    List<SchedTriggerHist> result = schedTriggerHistMapper.getSchedTriggerHistList(reqH);
    System.out.println("#Result:"+result);
    }
}
