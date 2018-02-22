package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicFilterWords;
import com.kthcorp.cmts.model.DicFilterWordsHist;
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
public class DicFilterWordsHistMapperTest {
    @Autowired
    private DicFilterWordsHistMapper dicFilterWordsHistMapper;

    @Test
    @Rollback(false)
    public void test_getDicFilterWordsHist() throws Exception {
        List<DicFilterWordsHist> result = dicFilterWordsHistMapper.getDicFilterWordsHist();
        System.out.println("#iresult:" + result.toString());
    }

    @Value("${cmts.property.serverid}")
    private String serverid;

    @Test
    @Rollback(false)
    public void test_insDicFilterWordsHist() throws Exception {
        DicFilterWordsHist req = new DicFilterWordsHist();
        req.setType("use");
        req.setDic_idx(2);
        req.setRegid(serverid);
        req.setAction("do filtering content by word");
        req.setAction_id(11);

        int result = dicFilterWordsHistMapper.insDicFilterWordsHist(req);
        System.out.println("#iresult:" + result + "/result.hidx:"+req.getHidx());
    }
}
