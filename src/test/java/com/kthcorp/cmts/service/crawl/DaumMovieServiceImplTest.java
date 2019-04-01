package com.kthcorp.cmts.service.crawl;

import com.google.gson.JsonArray;
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
import java.util.Map;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DaumMovieServiceImplTest {

	//@MockBean
	@Autowired
	private DaumMovieServiceImpl daumMovieService;

	@Test
	public void test_getOneItemFromSearchedPage() throws Exception {
		String resStr = "어벤저스 2012";
		ConfTarget reqInfo = new ConfTarget();
		List<ConfPreset> psList = new ArrayList<ConfPreset>();

		ConfPreset ps1 = new ConfPreset();
		/*
		ps1.setPs_type("content meta");
		ps1.setPs_tag(".desc_movie");
		ps1.setDest_field("plot");
		ps1.setDescriptp("daummovie_plot");
		psList.add(ps1);
		*/

		ps1 = new ConfPreset();
		ps1.setPs_type("meta");
		ps1.setPs_tag(".main_detail");
		ps1.setDest_field("award");
		ps1.setDescriptp("daummovie_award");
		psList.add(ps1);
		/*
		ps1 = new ConfPreset();
		ps1.setPs_type("ready");
		ps1.setPs_tag(".section_view");
		ps1.setDest_field("magazine");
		ps1.setDescriptp("daummovie_magazine");
		psList.add(ps1);
		*/

		reqInfo.setPresetList(psList);

		reqInfo.setTg_url("DAUM_MOVIE");
		reqInfo.setParam1("어벤저스");

		reqInfo.setMovietitle("어벤저스");
		reqInfo.setMovieyear("2012");
		JsonObject resultObj= daumMovieService.getOneItemFromSearchedPage(reqInfo);
	}


	@Test
	public void test_getContents() throws Exception {
		ConfTarget reqInfo = new ConfTarget();
		List<ConfPreset> psList = new ArrayList<ConfPreset>();

		ConfPreset ps1 = new ConfPreset();
		/*
		ps1.setPs_type("content meta");
		ps1.setPs_tag(".desc_movie");
		ps1.setDest_field("plot");
		ps1.setDescriptp("daummovie_plot");
		psList.add(ps1);
		*/

		ps1 = new ConfPreset();
		ps1.setPs_type("meta");
		ps1.setPs_tag(".main_detail");
		ps1.setDest_field("award");
		ps1.setDescriptp("daummovie_award");
		psList.add(ps1);
		/*
		ps1 = new ConfPreset();
		ps1.setPs_type("ready");
		ps1.setPs_tag(".section_view");
		ps1.setDest_field("magazine");
		ps1.setDescriptp("daummovie_magazine");
		psList.add(ps1);
		*/

		reqInfo.setPresetList(psList);

		reqInfo.setTg_url("DAUM_MOVIE");
		reqInfo.setParam1("333");
		reqInfo.setMovietitle("로마");
		reqInfo.setMovieyear("2017");

		JsonObject result = daumMovieService.getContents("DAUM_MOVIE", reqInfo);
		System.out.println("#Result:"+result.toString());

		//JsonObject reply = (JsonObject) result.get("reply");
		//System.out.println("#Result replay_contents:"+reply.get("reply_text").toString());

		//JsonObject contentsObj = (JsonObject) result.get("contents");
		//System.out.println("#Result contents:"+contentsObj.toString());
		//JsonObject metas = (JsonObject) result.get("metas");
		//System.out.println("#Result metas:"+metas.toString());

		//System.out.println("#Result dest_fields:"+result.get("dest_fields").toString());


		//System.out.println("#Result metas:"+result.get("metas").toString());

		//JsonObject metasObj = (JsonObject) result.get("metas");
		//System.out.println("#Result metas:"+metasObj.get("award").getAsString());
	}



	@Test
	public void test_getDaumMovieItem() throws Exception {
		Map<String,Object> result = daumMovieService.getDaumMovieItem("어벤져스", "2012");
		System.out.println("#RESULT: resultStr:"+result.toString());
	}

	@Test
	public void test_getOneMovieFromDaumSearch() throws Exception {
		JsonObject result = daumMovieService.getOneMovieFromDaumSearch("어벤져스", "2012");
		System.out.println("#RESULT: resultStr:"+result.toString());
	}

}
