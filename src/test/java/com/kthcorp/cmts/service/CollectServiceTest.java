package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;
import com.kthcorp.cmts.util.JsonUtil;
import com.kthcorp.cmts.util.StringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CollectServiceTest {
	@Autowired
	private CollectService collectService;

	@Test
	public void test_ollehTvMetaScheduleCheck() throws Exception{
		int result = collectService.ollehTvMetaCollectScheduleCheck();
		System.out.println("#Result:"+result);
	}

	@Test
	public void testAA() throws Exception {
		String a = "k//";
		a = a.replaceAll("//*", "/");
		System.out.println("a:"+a);
	}
	@Test
	public void testRe() throws Exception {
		String a = "aa #movietitle";
		a = a.replace("#movietitle", "test");
		System.out.println("a:"+a);
	}
	@Test
	public void test_step01() throws Exception{
		List<SchedTrigger> result = collectService.step01();
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_step01byScid() throws Exception{
		SchedTrigger req = new SchedTrigger();
		req.setSc_id(12);
		List<SchedTrigger> result = collectService.step01byScid(req);
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_step01_02_03() throws Exception {
		SchedTrigger req = new SchedTrigger();
		req.setSc_id(5857);
		int result = collectService.test_ollehTvMetaCollectScheduleCheck(req);
		//System.out.println("#Result:"+result);
	}


	@Test
	public void test_step02() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setTg_id(1);
		ConfTarget result = collectService.step02(req);
		System.out.println("#Result:"+result);
	}
	@Test
	public void test_run_step03imdb() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setTg_id(2);

		ConfTarget tg = collectService.step02(req);

		//String result = collectService.run_step03imdb(tg);
		JsonObject result = collectService.run_step03(tg, 705, "C", 1, "SK");
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_json() throws Exception{
		JsonArray req = new JsonArray();
		JsonObject jobj = null;
		jobj = new JsonObject();
		jobj.addProperty("A","aaa");
		req.add(jobj);
		jobj = new JsonObject();
		jobj.addProperty("B","bbb");
		req.add(jobj);

		//ArrayList<String> res = JsonUtil.getListFromJsonArrayOnlyValue(req);
		String res = JsonUtil.getStringFromJsonArraysValues(req);
		System.out.println("#result:"+res.toString());
	}


	@Test
	public void test_step03naverBlog() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setTg_id(2);

		ConfTarget tg = collectService.step02(req);
		//System.out.println("#Result: step02:"+tg.toString());

		JsonObject result = collectService.step03naverBlog(tg);

		System.out.println("#RESULT:"+result.toString());

		JsonArray resultArr = (JsonArray) result.get("result");
		for (JsonElement res : resultArr) {
			JsonObject res1 = (JsonObject) res;
			System.out.println("#ResArr:" + res1);
		}
		/**/
	}


	@Test
	public void test_run_step03naverBlog() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setTg_id(2);

		ConfTarget tg = collectService.step02(req);
		//System.out.println("#Result: step02:"+tg.toString());

		JsonObject result = collectService.run_step03(tg, 2, "C", 1, "SK");

		System.out.println("#RESULT:"+result.toString());
	}
}
