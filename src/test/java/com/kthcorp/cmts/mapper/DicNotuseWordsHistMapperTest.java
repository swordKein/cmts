package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicNotuseWordsHist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Transactional
public class DicNotuseWordsHistMapperTest {
    @Autowired
    private DicNotuseWordsHistMapper dicNotuseWordsHistMapper;

    @Test
    @Rollback(false)
    public void test_getDicNotuseWordsHist() throws Exception {
        List<DicNotuseWordsHist> result = dicNotuseWordsHistMapper.getDicNotuseWordsHist();
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getDicNotuseWordsHistByReq() throws Exception {
        DicNotuseWordsHist req = new DicNotuseWordsHist();
        req.setRegid("cmts_cm01");
        req.setDic_idx(2);
        req.setAction_id(5);

        List<DicNotuseWordsHist> result = dicNotuseWordsHistMapper.getDicNotuseWordsHistByReq(req);
        System.out.println("# result:" + result.toString());
        System.out.println("# result size:"+ result.size());
    }

    @Value("${property.serverid}")
    private String serverid;

    @Test
    @Rollback(false)
    public void test_insDicNotuseWordsHist() throws Exception {
        DicNotuseWordsHist req = new DicNotuseWordsHist();
        req.setType("use");
        req.setDic_idx(2);
        req.setRegid(serverid);
        req.setAction("do block keyword by notuse word");
        req.setAction_id(11);

        int result = dicNotuseWordsHistMapper.insDicNotuseWordsHist(req);
        System.out.println("#iresult:" + result + "/result.hidx:"+req.getHidx());
    }
}
