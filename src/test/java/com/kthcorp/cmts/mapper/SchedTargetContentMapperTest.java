package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTargetContent;
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
public class SchedTargetContentMapperTest {
    @Autowired
    private SchedTargetContentMapper schedTargetContentMapper;
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;


    @Test
    @Rollback(false)
    public void test_insSchedTargetContent() throws Exception{
        SchedTargetContent req = new SchedTargetContent();
        req.setSc_id(1);
        req.setTg_id(2);
        req.setStmh_id(2);
        req.setContent("테스트 컨텐츠");

        int rt = schedTargetContentMapper.insSchedTargetContent(req);

        SchedTargetContent reqss = new SchedTargetContent();
        reqss.setStc_id(1);

        List<SchedTargetContent> result = schedTargetContentMapper.getSchedTargetContentList(reqss);
        System.out.println("#Result:"+result);
    }

    @Test
    public void test_getSchedTargetContentList() {

        SchedTargetContent reqss = new SchedTargetContent();
        //reqss.setStc_id(1);
        //reqss.setSc_id(1);
        //reqss.setTg_id(2);
        reqss.setStmh_id(2);


        List<SchedTargetContent> result = schedTargetContentMapper.getSchedTargetContentList(reqss);
        System.out.println("#Result:"+result);
    }

}
