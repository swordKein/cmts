package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.GoogleApiHist;
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
public class GoogleApiHistMapperTest {
    @Autowired
    private GoogleApiHistMapper googleApiHistMapper;

    @Test
    @Rollback(false)
    public void test_getGoogleApiHist() throws Exception {
        List<GoogleApiHist> result = googleApiHistMapper.getGoogleApiHist();
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getGoogleTransSumSizeByMonth() throws Exception {
        GoogleApiHist req = new GoogleApiHist();
        req.setRegmonth("201711");
        GoogleApiHist result = googleApiHistMapper.getGoogleTransSumSizeByMonth(req);
        System.out.println("#iresult:" + result.getSrc_size().toString());
    }


    @Value("${cmts.property.serverid}")
    private String serverid;

    @Test
    @Rollback(false)
    public void test_insGoogleApiHist() throws Exception {
        GoogleApiHist req = new GoogleApiHist();
        req.setType("trans");
        req.setSrc_lang("en");
        req.setSrc_txt("test");
        req.setSrc_size(4);
        req.setTarget_lang("kr");
        req.setTarget_txt("테스트");
        req.setRegmonth("201711");
        req.setRegid(serverid);
        req.setAction("google_translate");
        req.setAction_id(11);

        int result = googleApiHistMapper.insGoogleApiHist(req);
        System.out.println("#iresult:" + result + "/result.hidx:"+req.getHidx());
    }
}
