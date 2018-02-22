package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTargetMappingOrig;
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
public class SchedTargetMappingOrigMapperTest {

    @Autowired
    private SchedTargetMappingOrigMapper schedTargetMappingOrigMapper;

    @Test
    public void test_insSchedTargetMappingOrig() throws Exception{
    SchedTargetMappingOrig req = new SchedTargetMappingOrig();
    req.setType("C");
    //req.setRank(1);

    List<SchedTargetMappingOrig> result = schedTargetMappingOrigMapper.getSchedTargetMappingOrigList(req);

    System.out.println("#RS:"+result.toString());
    }

}
