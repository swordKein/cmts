package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfPsMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ConfPsMappingMapperTest {
    @Autowired
    private ConfPsMappingMapper confPsMappingMapper;

    @Test
    @Rollback(false)
    public void test_insConfPsMapping() throws Exception {

        ConfPsMapping psm = null;

        for(int i=12; i<267; i++) {
            psm = new ConfPsMapping();
            psm.setTg_id(6);
            psm.setPs_id(i);
            //int rt = confPsMappingMapper.insConfPsMapping(psm);
            //System.out.println("#result:"+rt);
        }

    }
}
