package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.pool.concurrent.mgr.GenericTaskThreadPoolExecutor;
import com.kthcorp.cmts.util.pool.concurrent.monitor.TaskPoolMonitorThread;
import com.kthcorp.cmts.util.pool.concurrent.task.JobTask;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.GenericTaskArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
		//(classes = UtilServiceImpl.class)
@WebAppConfiguration
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestDatabase
//@EnableAutoConfiguration
//@ComponentScan(basePackageClasses=TestServiceImpl.class)
//@EntityScan(basePackageClasses=TestDao.class)
//@MybatisTest
//@ContextConfiguration(locations = {"file:src/main/resources/application.yml","file:src/main/resources/threadpool.xml_"})
//@ContextConfiguration(locations = {"file:src/main/resources/application.yml"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DicServiceTest {

	@Autowired
	private DicService dicService;

	@Test
	public void test_getDicFilterWords() {
		List<DicFilterWords> result = dicService.getDicFilterWords();
		System.out.println("#RESLT:"+result.toString());
	}


	@Test
	public void test_insDicFilterWords() {
		DicFilterWords req = new DicFilterWords();
		req.setWord("외모리뷰");
		req.setRegid("wodus77");

		int result = dicService.insDicFilterWords(req);
		System.out.println("#insert RESULT:"+result);

		List<DicFilterWords> result2 = dicService.getDicFilterWords();
		System.out.println("#RESLT:"+result2.toString());
	}

	@Test
	public void test_contain() {
		String s = "PGA TOUR US";
		String k = "PGA TOUR";
		System.out.println(" == ::"+s.contains(s));
		System.out.println(" > ::"+s.contains(k));
		System.out.println(" < ::"+k.contains(s));
	}

	@Test
	public void test_getDicFilterWordsHist() {
		List<DicFilterWordsHist> result = dicService.getDicFilterWordsHist();
		System.out.println("#RESLT:"+result.toString());
	}


	@Test
	public void test_getDicNotuseWords() {
		List<DicNotuseWords> result = dicService.getDicNotuseWords();
		System.out.println("#RESLT:"+result.toString());
	}


	@Test
	public void test_insDicNotuseWords() {
		DicNotuseWords req = new DicNotuseWords();
		req.setWord("외모리뷰");
		req.setRegid("wodus77");

		//int result = dicService.insDicNotuseWords(req);
		//System.out.println("#insert RESULT:"+result);

		List<DicNotuseWords> result2 = dicService.getDicNotuseWords();
		System.out.println("#RESLT:"+result2.toString());
	}


	@Test
	public void test_getDicNotuseWordsHist() {
		List<DicNotuseWordsHist> result = dicService.getDicNotuseWordsHist();
		System.out.println("#RESLT:"+result.toString());
	}



	@Test
	public void test_getDicChangeWords() {
		List<DicChangeWords> result = dicService.getDicChangeWords();
		System.out.println("#RESLT:"+result.toString());
	}


	@Test
	public void test_insDicChangeWords() {
		DicChangeWords req = new DicChangeWords();
		req.setWord("외모리뷰");
		req.setWordto("외모비교");
		req.setRegid("wodus77");

		int result = dicService.insDicChangeWords(req);
		System.out.println("#insert RESULT:"+result);

		List<DicChangeWords> result2 = dicService.getDicChangeWords();
		System.out.println("#RESLT:"+result2.toString());
	}

	@Test
	public void test_filterListByDicChangeWords() {
		List<String> req = new ArrayList<String>();
		req.add("경찰");
		req.add("경찰");
		req.add("경찰서");

		List<String> res = dicService.filterListByDicChangeWords(req, 1);
		System.out.println("#res:"+res.toString());
	}

	@Test
	public void test_getDicChangeWordsHist() {
		List<DicChangeWordsHist> result = dicService.getDicChangeWordsHist();
		System.out.println("#RESLT:"+result.toString());
	}




	@Test
	public void test_getDicAddWords() {
		List<DicAddWords> result = dicService.getDicAddWords();
		System.out.println("#RESLT:"+result.toString());
	}


	@Test
	public void test_insDicAddWords() {
		DicAddWords req = new DicAddWords();
		req.setWord("포돌이");
		req.setFreq(2.0);
		req.setRegid("ghkdwo77");

		int result = dicService.insDicAddWords(req);
		System.out.println("#insert RESULT:"+result);

		List<DicAddWords> result2 = dicService.getDicAddWords();
		System.out.println("#RESLT:"+result2.toString());
	}

	@Test
	public void test_filterListByDicAddWords() {
		Map<String, Double> reqMap = new HashMap<String, Double>();
		reqMap.put("경찰", 20.0);
		reqMap.put("포돌이", 30.0);

		Map<String, Double> res = dicService.filterListByDicAddWords(reqMap, 1);
		System.out.println("#res:"+res.toString());
	}

	@Test
	public void test_getDicAddWordsHist() {
		List<DicAddWordsHist> result = dicService.getDicAddWordsHist();
		System.out.println("#RESLT:"+result.toString());
	}

	@Test
	public void test_insDicKeywords() {
		DicKeywords req = new DicKeywords();
		req.setType("WHAT");
		req.setKeyword("외모리뷰");
		req.setRegid("ghkdwo77");

		int result = dicService.insDicKeywords(req);
		System.out.println("#insert RESULT:"+result);

		List<DicKeywords> result2 = dicService.getDicKeywords(req);
		System.out.println("#RESLT:"+result2.toString());
	}

	@Test
	public void test_modifyDicsByTypesFromArrayList() throws Exception {
		String items = "[\n" +
				"{\"word\":\"\",\"target_type\":\"NOTUSE\", \"target_word\":\"1920년대\", \"action\":\"NOTUSE\"},\n" +
				"{\"word\":\"1920년대\",\"target_type\":\"NOTUSE\", \"target_word\":\"1920년대1\", \"action\":\"mod\"},\n" +
				"{\"word\":\"1920년대1\",\"target_type\":\"NOTUSE\", \"target_word\":\"\", \"action\":\"del\"}\n" +
				"]\n";
		int rt = dicService.modifyDicsByTypesFromArrayList(items);
		System.out.println("#result:"+rt);

	}
}
