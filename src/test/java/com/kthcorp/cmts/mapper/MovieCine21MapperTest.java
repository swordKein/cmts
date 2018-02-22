package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.InItems;
import com.kthcorp.cmts.model.MovieCine21;
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
public class MovieCine21MapperTest {
    @Autowired
    private MovieCine21Mapper movieCine21Mapper;

    @Test
    @Rollback(false)
    public void test_insMovieCine21List() throws Exception {
        MovieCine21 req1 = new MovieCine21();
        req1.setMovieId(11);
        MovieCine21 req2 = new MovieCine21();
        req2.setMovieId(21);

        List<MovieCine21> movieList = new ArrayList<MovieCine21>();
        movieList.add(req1);
        movieList.add(req2);

        Map<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("list", movieList);

        int result = movieCine21Mapper.insMovieCine21List(reqMap);
        System.out.println("#iresult:" + result);
    }

    @Test
    @Rollback(false)
    public void test_cntMovieCine21() throws Exception {
        MovieCine21 req = new MovieCine21();
        req.setMovieNm("대부");

        int result = movieCine21Mapper.cntMovieCine21(req);
        System.out.println("#iresult:" + result);
    }

    @Test
    @Rollback(false)
    public void test_getMovieCine21() throws Exception {
        MovieCine21 req = new MovieCine21();
        req.setPageNo(1);
        req.setPageSize(20);
        List<MovieCine21> result = movieCine21Mapper.getMovieCine21(req);
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_insMovieCine21() throws Exception {
        MovieCine21 req = new MovieCine21();
        req.setMovieId(1);

        int result = movieCine21Mapper.insMovieCine21(req);
        System.out.println("#iresult:" + result);
    }

}
