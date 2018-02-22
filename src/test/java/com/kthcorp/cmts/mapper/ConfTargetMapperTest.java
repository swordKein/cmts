package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.SpringBootWebApplication;
//import com.kthcorp.cmts.config.WebConfig;
import com.kthcorp.cmts.controller.ConfTargetController;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTargetMapping;
import com.kthcorp.cmts.repository.TestDao;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;
import java.util.List;

//@SpringBootTest(classes = TestServiceImpl.class)
//@Configuration
//@ComponentScan(basePackages = "com.kthcorp.cmts"
//		, excludeFilters = @ComponentScan.Filter(value = ConfTargetController.class, type = FilterType.ANNOTATION)
//)
//@ComponentScan

		//, excludeFilters = {
		//@ComponentScan.Filter(Configuration.class), @ComponentScan.Filter(ConfTargetController.class) })
//@WebAppConfiguration
//@MapperScan(value={"com.kthcorp.cmts.mapper"})
//@Import({
//		WebConfig.class
//})
//@ContextConfiguration
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class})
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SpringBootWebApplication.class)
//@WebIntegrationTest @FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@ActiveProfiles(profiles = "local")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ConfTargetMapperTest {
	@Autowired
	private ConfTargetMapper confTargetMapper;
	//@Autowired
	//private TestServiceImpl testService;

	@Test
	public void testGetAll() throws Exception{
		List<ConfTarget> result = confTargetMapper.getAll();
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_getTargetListActivePage() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setPageNo(1);
		req.setPageSize(10);

		List<ConfTarget> result = confTargetMapper.getTargetListActivePage(req);
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_getTargetListByStat() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setPageNo(1);
		req.setPageSize(10);
		req.setOrderType("ASC");
		req.setStat("P");

		List<ConfTarget> result = confTargetMapper.getTargetListByStat(req);
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_getTargetListActiveFirst10() throws Exception{
		List<ConfTarget> result = confTargetMapper.getTargetListActiveFirst10();
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_getTargetListByPrefix() throws Exception{
		ConfTarget req = new ConfTarget();
		//req.setTg_url("GOOGLE_SEARCH_IMDB");
		req.setTg_url("NAVER_BLOG");
		ConfTarget result = confTargetMapper.getTargetListByPrefix(req);
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_uptTargetStat() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setTg_id(19);
		req.setStat("P");
		int rs = confTargetMapper.uptTargetStat(req);

		System.out.println("#RS:"+rs);

		List<ConfTarget> result = confTargetMapper.getTargetListActiveFirst10();
		System.out.println("#Result:"+result);
	}



	@Test
	@Rollback(false)
	public void test_insertConfTarget() throws Exception {
		ConfTarget req = new ConfTarget();
		req.setTitle("naver_news collect");
		req.setDescript("news.naver.com searching");
		req.setTg_url("NAVER_NEWS");
		req.setTg_url_param1("");
		req.setRegid("ghkdwo77");
		req.setStat("Y");
		req.setParam1("영화 #movietitle 리뷰");
		req.setIs_fail("N");
		req.setIs_limit("Y");
		req.setIs_manual("N");
		req.setContent_min1(1000);
		req.setContent_max1(20000);
		req.setContent_min2(2000);
		req.setContent_max2(20000);
		req.setFail_count1(10);
		req.setFail_count2(20);

		int rt = confTargetMapper.insConfTarget(req);
		System.out.println("#result code:"+rt);
		System.out.println("#result tg_id:"+req.getTg_id());
	}


	@Test
	@Rollback(false)
	public void test_insertConfTarget_daumnews() throws Exception {
		ConfTarget req = new ConfTarget();
		req.setTitle("daum_news collect");
		req.setDescript("news.daum.net searching");
		req.setTg_url("DAUM_NEWS");
		req.setTg_url_param1("");
		req.setRegid("ghkdwo77");
		req.setStat("Y");
		req.setParam1("영화 #movietitle 리뷰");
		req.setIs_fail("N");
		req.setIs_limit("Y");
		req.setIs_manual("N");
		req.setContent_min1(1000);
		req.setContent_max1(20000);
		req.setContent_min2(2000);
		req.setContent_max2(20000);
		req.setFail_count1(10);
		req.setFail_count2(20);

		int rt = confTargetMapper.insConfTarget(req);
		System.out.println("#result code:"+rt);
		System.out.println("#result tg_id:"+req.getTg_id());
	}


	@Test
	@Rollback(false)
	public void test_insertConfTarget_navermovie() throws Exception {
		ConfTarget req = new ConfTarget();
		req.setTitle("naver_movie collect");
		req.setDescript("movie.naver.com collecting");
		req.setTg_url("NAVER_MOVIE");
		req.setTg_url_param1("");
		req.setRegid("ghkdwo77");
		req.setStat("Y");
		req.setParam1("site:movie.naver.com #movietitle");
		req.setIs_fail("N");
		req.setIs_limit("Y");
		req.setIs_manual("N");
		req.setContent_min1(1000);
		req.setContent_max1(20000);
		req.setContent_min2(2000);
		req.setContent_max2(20000);
		req.setFail_count1(10);
		req.setFail_count2(20);

		int rt = confTargetMapper.insConfTarget(req);
		System.out.println("#result code:"+rt);
		System.out.println("#result tg_id:"+req.getTg_id());
	}



	@Test
	@Rollback(false)
	public void test_insertConfTarget_daummovie() throws Exception {
		ConfTarget req = new ConfTarget();
		req.setTitle("daum_movie collect");
		req.setDescript("movie.daum.net collecting");
		req.setTg_url("DAUM_MOVIE");
		req.setTg_url_param1("");
		req.setRegid("ghkdwo77");
		req.setStat("Y");
		req.setParam1("site://movie.daum.net/moviedb #movietitle");
		req.setIs_fail("N");
		req.setIs_limit("Y");
		req.setIs_manual("N");
		req.setContent_min1(1000);
		req.setContent_max1(20000);
		req.setContent_min2(2000);
		req.setContent_max2(20000);
		req.setFail_count1(10);
		req.setFail_count2(20);

		int rt = confTargetMapper.insConfTarget(req);
		System.out.println("#result code:"+rt);
		System.out.println("#result tg_id:"+req.getTg_id());
	}


}
