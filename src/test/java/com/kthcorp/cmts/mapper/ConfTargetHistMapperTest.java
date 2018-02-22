package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTargetHist;
import com.kthcorp.cmts.model.SchedTrigger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@Transactional
public class ConfTargetHistMapperTest {
    @Autowired
    private ConfTargetHistMapper confTargetHistMapper;

    /*
    @Test
    public void test_getConfTargetHistList() throws Exception{
        ConfTargetHist req = new ConfTargetHist();
        //req.setStat("Y");
        req.setPageNo(1);
        req.setPageSize(10);
        List<ConfTargetHist> result = confTargetHistMapper.getConfTargetHistList(req);
        System.out.println("#Result:"+result);
    }

    @Test
    @Rollback(false)
    public void test_insConfTargetHistList() throws Exception{
        ConfTargetHist req = new ConfTargetHist();
        req.setTg_id(1);
        req.setRt_code("OK");
        req.setRt_msg("SUCC");
        req.setStat("Y");
        //req.setContent("{cont}");

        int rt = confTargetHistMapper.insConfTargetHist(req);
        System.out.println("#isnert rt tgh_id:"+req.getTgh_id());
        List<ConfTargetHist> result = confTargetHistMapper.getConfTargetHistList(req);
        System.out.println("#Result:"+result);
    }
    */
}
