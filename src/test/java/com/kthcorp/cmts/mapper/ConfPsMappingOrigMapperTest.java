package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfPresetOrig;
import com.kthcorp.cmts.model.ConfPsMappingOrig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ConfPsMappingOrigMapperTest {
    @Autowired
    private ConfPsMappingOrigMapper confPsMappingOrigMapper;

    //#TODO
    @Test
    @Rollback(false)
    public void test_insConfPsMappingOrig() throws Exception {

        ConfPsMappingOrig psm = null;

        for(int i=11; i<266; i++) {
            psm = new ConfPsMappingOrig();
            psm.setTg_id(6);
            psm.setPs_id(i);
            int rt = confPsMappingOrigMapper.insConfPsMappingOrig(psm);
            System.out.println("#result:"+rt);
        }

    }
}
