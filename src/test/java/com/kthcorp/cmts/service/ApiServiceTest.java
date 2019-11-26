package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.Items;
import com.kthcorp.cmts.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ApiServiceTest {
	@Autowired
	private ApiService apiService;
	@Autowired
	private RelKnowledgeService relKnowledgeService;
	@Autowired
	private DicService dicService;

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
		
		JsonObject result = apiService.getDicKeywordsByType("WHO", "", "new", 10, 11);
		//JsonObject result = apiService.getDicKeywordsByType("WHO", "", "abc", 10, 11);
		System.out.println("#Result:" + result.toString());
	}

	@Test
	public void test_searchItemsPaging() throws Exception {
		Items req = new Items();

		//JsonObject result = apiService.getItemsSearch(50, 1, "", ""
		//		, "", "", "최근", "title,METASWHEN");


		//JsonObject result = apiService.getItemsSearch(50, 1, "ALL", "ALL"
		//		, "2018-02-03", "2019-05-15", "모두 다 따를 것이다", "title");

		JsonObject result = apiService.getItemsSearch(50, 1, "ALL", ""
				, "", "", "프렌즈 시즌 4", "title");

		System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_getMovieInfoByIdx() {
		System.out.println(apiService.getMovieInfoByIdx(19968));
	}

	@Test
	public void test_getSnsKeywords() throws Exception {
		JsonArray result = apiService.getSnsKeywords("마더!");
		System.out.println("#RESULT:"+result.toString());
	}

	@Test
	public void test_getGenres() throws Exception {
		System.out.println("#RESULT:"+apiService.getFilteredGenre("블랙코미디 무협"));
	}

	@Test
	public void test_getSnsTopKeywords() throws Exception {
		JsonObject resultObj = apiService.getSnsTopKeywords();
		System.out.println("#RES:"+resultObj.toString());
	}

	@Test
	public void test_processSnsTopKeywordsByDateSched() throws Exception {
		apiService.processSnsTopKeywordsByDateSched();

	}

	@Test
	public void test_getSnsTopWordsAndGraph() throws Exception {
		JsonObject result = apiService.getSnsTopWordsAndGraph();
		System.out.println("#RES:"+result.toString());
	}

	@Test
	public void test_getResultSnsMap() throws Exception {
		List<String> result = apiService.getResultSnsMapByTag("twitter", "20180314", "word");
		System.out.println("#RES:"+result.toString());
	}

	@Test
	public void fileReadAndSaveTest() throws Exception {
		MultipartFile uploadfile = FileUtils.convertFileToMultipart("c:\\upload\\","DIC_KEYWORDS_WHEN_20191126_0811.csv");
		String result = apiService.returnStringFromMultiPartFileForDIC(uploadfile,"WHEN") ;
		System.out.println("#RES:"+result.toString());

		String result2 = dicService.uploadDicFile(result,"WHEN");
		System.out.println("#RES2:"+result2.toString());

	}
}
