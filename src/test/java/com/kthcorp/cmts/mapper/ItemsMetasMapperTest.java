package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.CcubeSeries;
import com.kthcorp.cmts.model.ItemsMetasMapping;
import com.kthcorp.cmts.model.ItemsMetas;
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
public class ItemsMetasMapperTest {
    @Autowired
    private ItemsMetasMapper itemsMetasMapper;

    @Test
    @Rollback(false)
    public void test_getItemsMetas() throws Exception {
        ItemsMetas req = new ItemsMetas();
        req.setIdx(1);
        req.setMtype("plot");
        ItemsMetas result = itemsMetasMapper.getItemsMetas(req);
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getItemsMetasByIdx() throws Exception {
        ItemsMetas req = new ItemsMetas();
        req.setIdx(1);
        List<ItemsMetas> result = itemsMetasMapper.getItemsMetasByIdx(req);
        System.out.println("#iresult:" + result.toString());
    }


    @Test
    @Rollback(false)
    public void test_insItemsMetas() throws Exception {
        ItemsMetas req = new ItemsMetas();
        req.setIdx(1);
        req.setMtype("plot");
        req.setMeta("줄거리1");
        req.setRegid("ghkdwo77");
        //int result = itemsMetasMapper.insItemsMetas(req);
        //System.out.println("#iresult:" + result);
    }

}
