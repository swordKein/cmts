package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.ItemsTags;
import com.kthcorp.cmts.util.JsonUtil;
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
		JsonObject result = itemsTagsService.getItemsMetasByItemIdx(9023, false);
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

		String req = "";
		req = "[{\"meta\":\"압도적인1\",\"type\":\"emotion\",\"target_meta\":\"압도적인1\",\"action\":\"del\"},{\"meta\":\"압도적인1\",\"type\":\"emotion\",\"target_meta\":\"압도적인1\",\"action\":\"add\"}]";
		//req = "[{\"meta\":\"압도적인1\",\"type\":\"emotion\",\"target_meta\":\"압도적인1\",\"action\":\"add\"},{\"meta\":\"압도적인1\",\"type\":\"emotion\",\"target_meta\":\"압도적인1\",\"action\":\"del\"}]";
		req = "[{\"meta\":\"압도적인3\",\"type\":\"emotion\",\"target_meta\":\"압도적인3\",\"action\":\"add\"},{\"meta\":\"압도적인3\",\"type\":\"emotion\",\"target_meta\":\"압도적인4\",\"action\":\"mod\"}]";


		int  result = itemsTagsService.changeMetasArraysByTypeFromInputItems(1, req, "", "Y", "userId");	//userId : 로그인 중인 사용자정보 저장
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

	@Test
	public void test_restorePrevTag() throws Exception {
		itemsTagsService.restorePrevTag(2855);
	}

	@Test
	public void test_getMetaSubgenre() throws Exception {
		String reqStr = "[{\"type\":\"what\", \"meta\":\"감동\"}, {\"type\":\"emotion\", \"meta\":\"감동적인\"}" +
				", {\"type\":\"emotion\", \"meta\":\"인간적인\"}, {\"type\":\"what\", \"meta\":\"사랑\"}" +
				", {\"type\":\"what\", \"meta\":\"교훈\"}, {\"type\":\"what\", \"meta\":\"가족애\"}" +
				"]";


		reqStr = "[{\"type\":\"who\", \"meta\":\"갱스터\"}, {\"type\":\"what\", \"meta\":\"범죄\"}" +
				", {\"type\":\"what\", \"meta\":\"폭력\"}, {\"type\":\"metawho\", \"meta\":\"깡패\"}" +
				", {\"type\":\"metawho\", \"meta\":\"히어로\"}, {\"type\":\"who\", \"meta\":\"좀비\"}, {\"type\":\"who\", \"meta\":\"살인마\"}]";


		JsonArray resultArr = itemsTagsService.getMetaSubgenre(13260, reqStr);

		System.out.println("#RESULT:"+resultArr.toString());
	}

	@Test
	public void getItemsTagsMetasStringByItemIdx() throws Exception {
		ItemsTags it = new ItemsTags();
		it.setIdx(20);
		it.setStat("S");
		String reqStr = itemsTagsService.getItemsTagsMetasStringByItemIdx(it);
		System.out.println(reqStr);
		System.out.println(itemsTagsService.getMetaSubgenre(13260, reqStr));
	}

	@Test
	public void getWordArrStringFromItemsTagsMetasIdxAndMtype() throws Exception {
		ItemsTags it = new ItemsTags();
		it.setIdx(20);
		it.setMtype("LIST_SUBGENRE");
		it.setStat("S");
		System.out.println(itemsTagsService.tmp_getWordArrStringFromItemsTagsMetasIdxAndMtype(it));
	}

	@Test
	public void test_getSubgenres() throws Exception {
		JsonObject result = itemsTagsService.getSubgenres(13260, new JsonObject());
		System.out.println("#RES:"+result.toString());
	}

	@Test
	public void test_getMixedSubgenre2() throws Exception {
		System.out.println("#RES:"+itemsTagsService.getMixedSubgenre2(13260));
	}

	@Test
	public void test_targetMetas() throws Exception {
		JsonArray destArr = null;
		JsonArray destArr2 = null;

		String ogarr = "[{\"word\":\"복제인간\",\"type\":\"\",\"ratio\":0.0},{\"word\":\"클론\",\"type\":\"\",\"ratio\":0.0},{\"word\":\"특수요원\",\"type\":\"\",\"ratio\":0.0},{\"word\":\"동료\",\"type\":\"\",\"ratio\":0.0}]";
		String tgarr = "[{\"meta\":\"블레이드\",\"type\":\"who\",\"target_meta\":\"블레이드\",\"action\":\"del\"},\n" +
				"\t{\"meta\":\"인간들\",\"type\":\"who\",\"target_meta\":\"인간들\",\"action\":\"del\"},\n" +
				"\t{\"meta\":\"리플리컨트\",\"type\":\"who\",\"target_meta\":\"리플리컨트\",\"action\":\"del\"},\n" +
				"\t{\"meta\":\"박사\",\"type\":\"who\",\"target_meta\":\"박사\",\"action\":\"mod\"},\n" +
				"\t{\"meta\":\"경찰\",\"type\":\"who\",\"target_meta\":\"경찰\",\"action\":\"del\"},\n" +
				"\t{\"meta\":\"복제인간\",\"type\":\"who\",\"target_meta\":\"복제인간1\",\"action\":\"mod\"},\n" +
				"\t{\"meta\":\"경찰\",\"type\":\"who\",\"target_meta\":\"경찰\",\"action\":\"add\"}]";

		String ogarr1 = "[{\"word\":\"복제인간1\",\"type\":\"\",\"ratio\":0.0},{\"word\":\"복제인간2\",\"type\":\"\",\"ratio\":0.0}]";
		String tgarr1 = "[{\"meta\":\"복제인간1\",\"type\":\"who\",\"target_meta\":\"복제인간2\",\"action\":\"mod\"},\n" +
				"\t{\"meta\":\"복제인간2\",\"type\":\"who\",\"target_meta\":\"복제인간2\",\"action\":\"del\"}]";


		JsonArray origMetaArr = JsonUtil.getJsonArray(ogarr1);
		JsonArray changeMetaArr = JsonUtil.getJsonArray(tgarr1);

		destArr = itemsTagsService.getTargetMetasArray("METASWHO", origMetaArr, changeMetaArr);
		//destArr2 = itemsTagsService.getRemoveDupTargetMetasArrayOnlyString(destArr);
		System.out.println("#ELOG.destArr(String): datas::" + destArr.toString());

	}
}
