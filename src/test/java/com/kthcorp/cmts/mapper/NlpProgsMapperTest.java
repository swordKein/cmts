package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.NlpProgs;
import com.kthcorp.cmts.model.TestVO;
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
@Transactional
public class NlpProgsMapperTest {
    @Autowired
    private NlpProgsMapper nlpProgsMapper;

    @Test
    @Rollback(false)
    public void test_getTest1() throws Exception {
        TestVO req = new TestVO();
        req.setMin(100);
        req.setMax(400);

        List<TestVO> result = nlpProgsMapper.getTest1(req);
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getNlpProgs() throws Exception {
        List<NlpProgs> result = nlpProgsMapper.getNlpProgs();
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_insNlpProgs() throws Exception {
        NlpProgs req = new NlpProgs();
        req.setFilename("filename1.txt");
        req.setFilelines((long) 3);
        req.setFilecharset("euc-kr");
        req.setOutfilename("filename_out_2017.txt");
        req.setStat("Y");

        //int result = nlpProgsMapper.insNlpProgs(req);
        //System.out.println("#iresult:" + result);
        System.out.println("#inserted idx:" + req.getIdx());
    }

    @Test
    @Rollback(false)
    public void test_uptNlpProgs() throws Exception {
        NlpProgs req = new NlpProgs();
        req.setIdx(1);
        req.setLinecnt((long) 2);
        //int result = nlpProgsMapper.uptNlpProgs(req);
        //System.out.println("#result:"+result);


        List<NlpProgs> result2 = nlpProgsMapper.getNlpProgs();
        System.out.println("#iresult2:" + result2.toString());
    }
}
