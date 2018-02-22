package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.Items;
import com.kthcorp.cmts.model.ItemsSchedMapping;
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
public class ItemsSchedMappingMapperTest {
    @Autowired
    private ItemsSchedMappingMapper itemsSchedMappingMapper;

    @Test
    @Rollback(true)
    public void test_getItemsSchedMapping() throws Exception {
        ItemsSchedMapping req = new ItemsSchedMapping();
        req.setIdx(15);
        req.setSc_id(1);
        int result = itemsSchedMappingMapper.insItemsSchedMapping(req);
        System.out.println("#iresult:" + result);
    }

    @Test
    public void test_getItemIdxByScid() {
        ItemsSchedMapping reqism = new ItemsSchedMapping();
        reqism.setSc_id(29465);
        int itemIdx = itemsSchedMappingMapper.getItemIdxByScid(reqism);
        System.out.println("#result:"+itemIdx);
    }
}
