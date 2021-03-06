package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicPumsaWords;
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
public class DicPumsaWordsMapperTest {
    @Autowired
    private DicPumsaWordsMapper dicPumsaWordsMapper;

    @Test
    @Rollback(false)
    public void test_getDicPumsaWords() throws Exception {
        List<DicPumsaWords> result = dicPumsaWordsMapper.getDicPumsaWords();
        System.out.println("#iresult:" + result.toString());
    }
}
