package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicChangeWordsHist;
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
public class DicChangeWordsHistMapperTest {
    @Autowired
    private DicChangeWordsHistMapper dicChangeWordsHistMapper;

    @Test
    @Rollback(false)
    public void test_getDicChangeWordsHist() throws Exception {
        List<DicChangeWordsHist> result = dicChangeWordsHistMapper.getDicChangeWordsHist();
        System.out.println("#iresult:" + result.toString());
    }

    @Value("${property.serverid}")
    private String serverid;

    @Test
    @Rollback(false)
    public void test_insDicChangeWordsHist() throws Exception {
        DicChangeWordsHist req = new DicChangeWordsHist();
        req.setType("use");
        req.setDic_idx(1);
        req.setRegid(serverid);
        req.setAction("do changing keyword by word");
        req.setAction_id(11);

        int result = dicChangeWordsHistMapper.insDicChangeWordsHist(req);
        System.out.println("#iresult:" + result + "/result.hidx:"+req.getHidx());
    }
}
