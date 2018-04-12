package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicAddWordsHist;
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
public class DicAddWordsHistMapperTest {
    @Autowired
    private DicAddWordsHistMapper dicAddWordsHistMapper;

    @Test
    @Rollback(false)
    public void test_getDicAddWordsHist() throws Exception {
        List<DicAddWordsHist> result = dicAddWordsHistMapper.getDicAddWordsHist();
        System.out.println("#iresult:" + result.toString());
    }

    @Value("${property.serverid}")
    private String serverid;

    @Test
    @Rollback(false)
    public void test_insDicAddWordsHist() throws Exception {
        DicAddWordsHist req = new DicAddWordsHist();
        req.setType("use");
        req.setDic_idx(1);
        req.setRegid(serverid);
        req.setAction("do adding keyword by word");
        req.setAction_id(11);

        int result = dicAddWordsHistMapper.insDicAddWordsHist(req);
        System.out.println("#iresult:" + result + "/result.hidx:"+req.getHidx());
    }
}
