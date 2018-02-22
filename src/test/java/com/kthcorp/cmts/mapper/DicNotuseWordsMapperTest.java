package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicNotuseWords;
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
public class DicNotuseWordsMapperTest {
    @Autowired
    private DicNotuseWordsMapper dicNotuseWordsMapper;

    @Test
    @Rollback(false)
    public void test_getDicNotuseWords() throws Exception {
        DicNotuseWords req = new DicNotuseWords();
        req.setWord("외도");
        req.setRegid("ghkdwo77");

        int rt = dicNotuseWordsMapper.insDicNotuseWords(req);

        List<DicNotuseWords> result = dicNotuseWordsMapper.getDicNotuseWords();
        System.out.println("#iresult:" + result.toString());
    }
}
