package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTargetMappingHist;
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
public class SchedTargetMappingHistMapperTest {

    @Autowired
    private SchedTargetMappingHistMapper schedTargetMappingHistMapper;

    @Test
    public void test_insSchedTargetMappingHist() throws Exception{
    SchedTargetMappingHist req = new SchedTargetMappingHist();
    req.setSc_id(48);
    req.setTg_id(1);
    req.setType("C");
    req.setStat("S");
    req.setRt_code("OK");
    req.setRt_msg("SUCCESS");
    req.setSummary("summary is ok");

    //int rs = schedTargetMappingHistMapper.insSchedTargetMappingHist(req);

    //System.out.println("#RS:"+rs);

    SchedTargetMappingHist reqH = new SchedTargetMappingHist();
    reqH.setPageNo(1);
    reqH.setPageSize(10);

    List<SchedTargetMappingHist> result = schedTargetMappingHistMapper.getSchedTargetMappingHistList(reqH);
    //System.out.println("#Result:"+result);
    }

    @Test
    public void test_getSchedTargetMappingHistList() {

        SchedTargetMappingHist reqH = new SchedTargetMappingHist();
        reqH.setPageNo(1);
        reqH.setPageSize(10);
        reqH.setSc_id(1);

        List<SchedTargetMappingHist> result = schedTargetMappingHistMapper.getSchedTargetMappingHistList(reqH);
        System.out.println("#Result:"+result);
        System.out.println("#Result size:"+result.size());
    }


    @Test
    public void test_getSchedTargetMappingHistListForLast() {

        SchedTargetMappingHist reqH = new SchedTargetMappingHist();
        reqH.setPageNo(1);
        reqH.setPageSize(10);
        reqH.setSc_id(1);

        List<SchedTargetMappingHist> result = schedTargetMappingHistMapper.getSchedTargetMappingHistListForLast(reqH);
        System.out.println("#Result:"+result);
        System.out.println("#Result size:"+result.size());
    }


    @Test
    public void test_getSchedTargetMappingHistListForLast2() {

        SchedTargetMappingHist reqH = new SchedTargetMappingHist();
        reqH.setType("C");
        reqH.setSc_id(44);

        List<SchedTargetMappingHist> result = schedTargetMappingHistMapper.getSchedTargetMappingHistListForLast2(reqH);
        System.out.println("#Result:"+result);
        System.out.println("#Result size:"+result.size());
    }

}
