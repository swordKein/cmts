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
public class Cine21MovieServiceImplTest {

	//@MockBean
	@Autowired
	private Cine21MovieServiceImpl cine21MovieService;

	@Test
	public void test_getCine21Movie() throws Exception {
		ConfTarget reqInfo = new ConfTarget();
		reqInfo.setTg_id(11);
		reqInfo.setTg_url("CINE21");
		reqInfo.setParam1("타짜: 원 아이드 잭");
		reqInfo.setIs_fail("Y");
		reqInfo.setIs_manual("N");
		reqInfo.setContent_min2(2);
		reqInfo.setContent_max2(20000000);
		reqInfo.setFail_count2(10);

		ConfPreset ps = new ConfPreset();
		List<ConfPreset> pslist = new ArrayList<ConfPreset>();

		ps = new ConfPreset();
		ps.setDescriptp("/www.cine21.com/movie/info/?movie_id=52538");
		ps.setDest_field("story-area");
		ps.setPs_tag("#content #story-area");
		pslist.add(ps);

		reqInfo.setPresetList(pslist);

		JsonObject result = cine21MovieService.getSearchCine21(reqInfo);

		System.out.println("#All result:"+result);
	}
}
