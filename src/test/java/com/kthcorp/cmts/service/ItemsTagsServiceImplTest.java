package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ItemsTagsServiceImplTest {

	//@MockBean
	@Autowired
	private ItemsTagsService itemsTagsService;

	@Test
	public void test_getItemsMetasByItemIdx() throws Exception{
		JsonObject result = itemsTagsService.getItemsMetasByItemIdx(1, false);
		System.out.println("#Result:"+result.toString());
	}

	@Test
	public void test_getItemsTagsMetasAll() throws Exception{
		JsonObject result = itemsTagsService.getItemsMetasByItemIdxForInsert(2);
		System.out.println("#Result:"+result.toString());
	}

	@Test
	public void test_getArraysByTypeFromInputItems() {
		String req = "[{\"type\":\"METASWHEN\", \"meta\":\"\", \"target_meta\":\"테스트삽입\", \"action\":\"add\"} ,{\"type\":\"METASWHEN\", \"meta\":\"테스트삽입\", \"target_meta\":\"테스트삽입1\", \"action\":\"mod\"} ,{\"type\":\"METASWHEN\", \"meta\":\"테스트삽입\", \"target_meta\":\"\", \"action\":\"del\"} ,{\"type\":\"METASWHAT\", \"meta\":\"테스트삽입\", \"target_meta\":\"\", \"action\":\"del\"}]";

		JsonObject result = itemsTagsService.getArraysByTypeFromInputItems(req);
		System.out.println("#Result:"+result.toString());
	}

	@Test
	public void test_changeMetasArraysByTypeFromInputItems() {

		String req = "[{\"meta\":\"압도적인2\",\"type\":\"emotion\",\"target_meta\":\"압도적인1\",\"action\":\"mod\"}]";

		int  result = itemsTagsService.changeMetasArraysByTypeFromInputItems(1, req, "");
		System.out.println("#Result:"+result);
	}


	@Test
	public void test_getNaverKindWords() throws Exception {
		List<String> origArr = new ArrayList();
		origArr.add("1");
		List<String> result = itemsTagsService.getNaverKindWords("아름다운", origArr);
		System.out.println("#RESULT:"+result.toString());
	}

	@Test
	public void test_getNaverKindWordsByList() throws Exception {
		List<String> origArr = new ArrayList();
		origArr.add("아름다운");
		origArr.add("이쁜");
		origArr.add("소박한");
		origArr.add("사랑스러운");
		origArr.add("따뜻한");

		List<String> result = itemsTagsService.getNaverKindWordsByList(origArr, 10);
		System.out.println("#RESULT:"+result.toString());
	}

	@Test
	public void test_getGenreWordsListByGenre() throws Exception {
		String genres = "액션 드라마";
		List<String> result = itemsTagsService.getGenreWordsListByGenre(genres);
		System.out.println("#RESULT:"+result.toString());
	}

	@Test
	public void test_processManualTagsMetasChange() throws Exception {
		itemsTagsService.processManualTagsMetasChange(
				//"METASWHEN", "18세기","18세기","add"));
				//"METASWHEN", "1852","1852","add"));
				//"METASWHEN", "테스트","테스트","add"));
				//"METASWHEN", "1852","1852","add"));
				//"METASWHEN", "1852","18세기","mod"));
				//"METASWHEN", "테스트","테스트","del"));
				"METASWHEN", "3","3","add");

		Thread.sleep(10000);

	}
}
