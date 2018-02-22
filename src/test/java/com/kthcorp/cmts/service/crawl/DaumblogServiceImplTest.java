package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DaumblogServiceImplTest {

	//@MockBean
	@Autowired
	private DaumblogServiceImpl daumblogService;

	@Test
	public void test_getSearchBlog() throws Exception {
		ConfTarget reqInfo = new ConfTarget();
		reqInfo.setTg_id(3);
		reqInfo.setTg_url("DAUM_BLOG");
		reqInfo.setParam1("영화 토끼와 거북이 1924 줄거리");
		reqInfo.setIs_fail("Y");
		reqInfo.setIs_manual("N");
		reqInfo.setContent_min2(1000);
		reqInfo.setContent_max2(150000);
		reqInfo.setFail_count2(5);


		List<ConfPreset> pslist = new ArrayList<ConfPreset>();
		ConfPreset ps = new ConfPreset();
		ps = new ConfPreset();
		ps.setDest_field("daumblog1");
		ps.setDescriptp("daumblog1");
		ps.setPs_tag("#cContent #contentDiv");
		pslist.add(ps);

		ps = new ConfPreset();
		ps.setDest_field("daumblog");
		ps.setDescriptp("daumblog");
		ps.setPs_tag(".post-view");

		pslist.add(ps);

		ps = new ConfPreset();
		ps.setDest_field("daumblog_tistory");
		ps.setDescriptp("daumblog_tistory");
		ps.setPs_tag(".article_body");
		pslist.add(ps);

		ps = new ConfPreset();
		ps.setDest_field("daumblog_tistory2");
		ps.setDescriptp("daumblog_tistory2");
		ps.setPs_tag(".article");
		pslist.add(ps);

		reqInfo.setPresetList(pslist);

		JsonObject result = daumblogService.getSearchBlog(reqInfo);

		JsonArray resultArr = (JsonArray) result.get("result");
		for (JsonElement je : resultArr) {
			System.out.println("#Result:"+je.toString());
		}


		System.out.println("#All Result:"+result.toString());
		JsonArray failResultArr = (JsonArray) result.get("failResultArr");
		for (JsonElement je : failResultArr) {
			System.out.println("#failResult:"+je.toString());
		}
		System.out.println("#All ConfPresetList.size:"+pslist.size());

		JsonArray resultArr2 = (JsonArray) result.get("resultArr");
		System.out.println("#All result.size:"+resultArr2.size());
	}
}
