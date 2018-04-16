package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicKeywords;
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
public class DicKeywordsMapperTest {
    @Autowired
    private DicKeywordsMapper dicKeywordsMapper;

    @Test
    @Rollback(false)
    public void test_getDicKeywords() throws Exception {
        DicKeywords req = new DicKeywords();
        req.setType("where");

        List<DicKeywords> result = dicKeywordsMapper.getDicKeywordsList(req);
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    public void test_countDicKeywrods() throws Exception {
        DicKeywords req = new DicKeywords();
        req.setKeyword("대통령");
        int cnt = dicKeywordsMapper.countDicKeywords(req);
        System.out.println("#cnt:"+cnt);

    }

    @Test
    @Rollback(false)
    public void test_insDicNotMapKeywrods() throws Exception {
        DicKeywords req = new DicKeywords();
        req.setKeyword("대통령");
        //int cnt = dicKeywordsMapper.insDicNotMapKeywords(req);
        //System.out.println("#cnt:"+cnt);
    }

    @Test
    public void test_getDicNotMapKeywords() throws Exception {
        List<DicKeywords> res = dicKeywordsMapper.getDicNotMapKeywords();
        System.out.println("#res:"+res.toString());
    }

    @Test
    public void test_getDicGenreKeywordsByGenre() throws Exception {
        System.out.println("#RESULT:"+dicKeywordsMapper.getDicGenreKeywordsByGenre("드라마"));
    }
}
