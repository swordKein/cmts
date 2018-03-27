package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CcubeMapperTest {
    @Autowired
    private CcubeMapper ccubeMapper;

    @Test
    public void test_get50ActiveCcubeContents() throws Exception {
        List<CcubeContent> result = ccubeMapper.get50ActiveCcubeContents();
        System.out.println("#RESULT:"+result.toString());
    }

    @Test
    public void test_get50ActiveCcubeSeries() throws Exception {
        List<CcubeSeries> result = ccubeMapper.get50ActiveCcubeSeries();
        System.out.println("#RESULT:"+result.toString());
    }

    @Test
    public void test_getCcubeContentsByCid() throws Exception {
        CcubeContent req = new CcubeContent();
        req.setContent_id("1");
        CcubeContent result = ccubeMapper.getCcubeContentByCid(req);
        System.out.println("#RESULT:"+result.toString());
    }

    @Test
    public void test_getCcubeSeriesById() throws Exception {
        CcubeSeries req = new CcubeSeries();
        req.setSeries_id("1");
        CcubeSeries result = ccubeMapper.getCcubeSeriesById(req);
        System.out.println("#RESULT:"+result.toString());
    }

    @Test
    public void test_getCcubeItemIdx() throws Exception {
        CcubeKeys req = new CcubeKeys();
        req.setPurity_title("1");
        req.setContent_id("1");

        int result = ccubeMapper.getCcubeItemIdx(req);
        System.out.println("#RESULT:"+result);
    }

    @Test
    public void test_getCcubeKeys() throws Exception {
        CcubeKeys result = ccubeMapper.getCcubeKeys(10406);
        System.out.println("#result:"+result.toString());
    }

    @Test
    @Rollback(false)
    public void test_insertCcubeOutput() throws Exception {
        Map<String, Object> req = new HashMap();
        req.put("idx", 9473);
        req.put("stat", "Y");
        req.put("regid", "ghkdwo77");
        int rt = ccubeMapper.insCcubeOutput(req);

        Items reqi = new Items();
        reqi.setType("CcubeContent");
        reqi.setPageSize(50);
        reqi.setPageNo(1);

        System.out.println("#RES:"+ccubeMapper.getCcubeOutputListStandby(reqi));
    }

}
