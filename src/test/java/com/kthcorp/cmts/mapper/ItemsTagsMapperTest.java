package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ItemsMetas;
import com.kthcorp.cmts.model.ItemsTags;
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
public class ItemsTagsMapperTest {
    @Autowired
    private ItemsTagsMapper itemsTagsMapper;

    @Test
    @Rollback(false)
    public void test_getItemsTagsMetasByItemIdx() throws Exception {
        ItemsTags req = new ItemsTags();
        req.setIdx(1);
        req.setStat("S");

        List<ItemsTags> result = itemsTagsMapper.getItemsTagsMetasByItemIdx(req);
        System.out.println("#result:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getMaxTagsIdxByItemIdx() throws Exception {
        ItemsTags req = new ItemsTags();
        req.setIdx(1);
        int result = itemsTagsMapper.getMaxTagsIdxByItemIdx(req);

        System.out.println("#result:" + result);
    }


    @Test
    @Rollback(false)
    public void test_insItemsTagsKeys() throws Exception {
        ItemsTags req = new ItemsTags();
        req.setIdx(1);
        req.setTagidx(1);

        int result = itemsTagsMapper.insItemsTagsKeys(req);
        System.out.println("#result:" + result);
    }

    @Test
    @Rollback(false)
    public void test_uptItemsTagsKeysStat() throws Exception {
        ItemsTags req = new ItemsTags();
        req.setIdx(1);
        req.setTagidx(1);
        req.setStat("S");

        int result = itemsTagsMapper.uptItemsTagsKeysStat(req);
        System.out.println("#result:" + result);
    }

    @Test
    @Rollback(false)
    public void test_insItemsTagsMetas() throws Exception {
        ItemsTags req = new ItemsTags();
        req.setIdx(1);
        req.setTagidx(1);
        req.setMtype("test");
        req.setMeta("test meta");
        req.setRegid("sched");

        int result = itemsTagsMapper.insItemsTagsMetas(req);
        System.out.println("#result:" + result);
    }
}
