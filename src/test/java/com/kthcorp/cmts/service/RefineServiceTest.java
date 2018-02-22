package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;
import com.kthcorp.cmts.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RefineServiceTest {
	@Autowired
	private RefineService refineService;

	@Test
	public void test_ollehTvMetaRefineScheduleCheck() throws Exception{
		int result = refineService.ollehTvMetaRefineScheduleCheck();
		System.out.println("#Result:"+result);
	}

	@Test
	public void testAA() throws Exception {
		String a = "k//";
		a = a.replaceAll("//*", "/");
		System.out.println("a:"+a);
	}
	
	@Test
	public void test_step04() throws Exception{
		List<SchedTrigger> result = refineService.step04();

		System.out.println("#Result:"+result);
	}

	@Test
	public void test_step05() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setTg_id(1);
		ConfTarget result = refineService.step05(req);
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_step06sub() throws Exception{
		List<SchedTrigger> request = refineService.step04();
		System.out.println("#Request:"+request.toString());

		JsonObject res = refineService.step06sub(request.get(0));

		System.out.println("#Result:"+res.toString());
		System.out.println("#Result.result:"+res.get("result"));
	}

	@Test
	public void test_step06() throws Exception{
		List<SchedTrigger> result = refineService.step04();

		if (result != null) {
			JsonObject res = refineService.run_step06(result.get(0), result.get(0).getSc_id(), result.get(0).getType(), result.get(0).getTcnt());

			System.out.println("#Result:" + res.toString());
			System.out.println("#Result.content:" + res.get("result"));
		}
	}

}
