package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.Items;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ApiServiceTest {
	@Autowired
	private ApiService apiService;

	@Test
	public void test_getMovieCine21ByTitle() throws Exception{
		JsonObject result = apiService.getCine21Datas("12몽키즈");
		System.out.println("#Result:"+result.toString());

	}

	@Test
	public void test_getDicKeywordsByType() throws Exception {
		//JsonObject result = apiService.getDicKeywordsByType("WHEN", "10",10, 1);
		//JsonObject result = apiService.getDicKeywordsByType("CHANGE", "재밌", 100, 1);
		//JsonObject result = apiService.getDicKeywordsByType("NOTUSE", "있",10, 2);
		//JsonObject result = apiService.getDicKeywordsByType("ADD", "인",10, 1);
		JsonObject result = apiService.getDicKeywordsByType("FILTER", "P",10, 1);
		System.out.println("#Result:" + result.toString());
	}

	@Test
	public void test_searchItemsPaging() throws Exception {
		Items req = new Items();

		//JsonObject result = apiService.getItemsSearch(50, 1, "", ""
		//		, "", "", "최근", "title,METASWHEN");

		JsonObject result = apiService.getItemsSearch(10, 1, "ALL", "ALL"
				, "2018-01-01", "2019-03-28", "마더!", "title");

		System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_getMovieInfoByIdx() {
		System.out.println(apiService.getMovieInfoByIdx(2));
	}

	@Test
	public void test_getSnsKeywords() throws Exception {
		JsonArray result = apiService.getSnsKeywords("신과함께");
		//System.out.println("#RESULT:"+result.toString());
	}

}
