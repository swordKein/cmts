package com.kthcorp.cmts.service.crawl;

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
public class NaverMovieServiceImplTest {

	//@MockBean
	@Autowired
	private NaverMovieServiceImpl naverMovieService;
	@Autowired
	private DaumMovieServiceImpl daumMovieService;

	@Test
	public void test_getContents() throws Exception {
		ConfTarget reqInfo = new ConfTarget();
		List<ConfPreset> psList = new ArrayList<ConfPreset>();
		ConfPreset ps1 = new ConfPreset();
		ps1.setPs_type("meta contents");
		ps1.setPs_tag(".story_area .con_tx");
		ps1.setDest_field("plot");
		ps1.setDescriptp("navermovie_plot");
		psList.add(ps1);

		ps1 = new ConfPreset();
		ps1.setPs_type("meta contents");
		ps1.setPs_tag(".making_note .con_tx");
		ps1.setDest_field("making_note");
		ps1.setDescriptp("navermovie_making_note");
		psList.add(ps1);

		ps1 = new ConfPreset();
		ps1.setPs_type("meta");
		ps1.setPs_tag(".score_result ul li .star_score em");
		ps1.setDest_field("star_score");
		ps1.setDescriptp("navermovie_reply");
		psList.add(ps1);

		ps1 = new ConfPreset();
		ps1.setPs_tag(".score_result ul li .score_reple p");
		ps1.setPs_type("meta");
		ps1.setDest_field("reply_text");
		ps1.setDescriptp("navermovie_reply");
		psList.add(ps1);

		/** new **/
		ps1 = new ConfPreset();
		ps1.setPs_tag(".h_movie a");
		ps1.setPs_type("meta");
		ps1.setDest_field("title_movie");
		ps1.setDescriptp("navermovie_title_movie");
		psList.add(ps1);

		ps1 = new ConfPreset();
		ps1.setPs_tag(".info_spec dd p span");
		ps1.setPs_type("meta genre");
		ps1.setDest_field("genre");
		ps1.setDescriptp("navermovie_genre");
		psList.add(ps1);

		reqInfo.setPresetList(psList);

		reqInfo.setTg_url("NAVER_MOVIE");
		reqInfo.setParam1("나랏말싸미");
		reqInfo.setParam1("타짜: 원 아이드 잭");

		JsonObject result = naverMovieService.getContents("NAVER_MOVIE", reqInfo);
		System.out.println("#Result:"+result.toString());

		JsonObject metas = (JsonObject) result.get("metas");
		System.out.println("#Result:"+ result.get("dest_fields").toString());

		//JsonObject reply = (JsonObject) result.get("reply");
		//System.out.println("#Result replay_contents:"+reply.get("reply_text").toString());
	}
}
