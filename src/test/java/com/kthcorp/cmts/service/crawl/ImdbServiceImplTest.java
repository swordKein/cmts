package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ConfPreset;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.GoogleSearchService;
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
public class ImdbServiceImplTest {

	//@MockBean
	@Autowired
	private ImdbServiceImpl imdbService;

	@Test
	public void test_getMovie() throws Exception {
		ConfTarget reqInfo = new ConfTarget();
		List<ConfPreset> psList = new ArrayList<ConfPreset>();
		ConfPreset ps1 = new ConfPreset();
		ps1.setPs_tag("#plot-summaries-content");
		ps1.setDest_field("plot");
		ps1.setPs_type("trans");
		psList.add(ps1);
		reqInfo.setPresetList(psList);

		reqInfo.setTg_url("GOOGLE_SEARCH_IMDB");
		reqInfo.setTg_url_param1("/plotsummary?ref_=tt_stry_pl");
		reqInfo.setParam1("site:imdb.com/title/ blind in 2011 director shin gyu hyun");

		JsonObject result = imdbService.getMovie(reqInfo);
		System.out.println("#Result:"+result.toString());
	}
}
