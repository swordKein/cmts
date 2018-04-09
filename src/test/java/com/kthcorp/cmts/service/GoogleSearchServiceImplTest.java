package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GoogleSearchServiceImplTest {

	//@MockBean
	@Autowired
	private GoogleSearchService googleSerchService;

	@Test
	public void testGoogleSearchServiceTest() throws Exception {
		String result = googleSerchService.getSearchItems("blind", "shin gyun hyun", "2017");
		System.out.println("#Result:"+result);
	}

	@Test
	public void testGoogleSearchServiceTest2() throws Exception {
		JsonArray result = googleSerchService.getSearchItems2("blind", "shin gyun hyun", "2011");
		//System.out.println("#Result:"+result);
	}


	@Test
	public void test_getFirstSearchedItem() throws Exception {
		JsonObject result = googleSerchService.getFirstSearchedMovieItem("blind", "shin gyun hyun", "2017");
		System.out.println("#Result:"+result);
	}

}
