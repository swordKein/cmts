package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicFilterWords;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.MapUtil;
import com.kthcorp.cmts.util.SeunjeonUtil;
import com.kthcorp.cmts.util.WordFreqUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class DicFilterWordsMapperTest {
    @Autowired
    private DicFilterWordsMapper dicFilterWordsMapper;

    @Test
    @Rollback(false)
    public void test_getDicFilterWords() throws Exception {
        List<DicFilterWords> result = dicFilterWordsMapper.getDicFilterWords();
        System.out.println("#iresult:" + result.toString());
    }
}
