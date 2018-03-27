package com.kthcorp.cmts.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.ItemsMapper;
import com.kthcorp.cmts.mapper.TestMapper;
import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.Items;
import com.kthcorp.cmts.model.ItemsContent;
import com.kthcorp.cmts.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
//(classes = TestServiceImpl.class)
//@WebAppConfiguration
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestDatabase
//@EnableAutoConfiguration
//@ComponentScan(basePackageClasses=TestServiceImpl.class)
//@EntityScan(basePackageClasses=TestDao.class)
//@MybatisTest
public class TestServiceTestImpl {

	//@MockBean
	@Autowired
	private TestService testService;
	@Autowired
	private ItemsMapper itemsMapper;
	@Autowired
	private TestMapper testMapper;

	@Test
	public void test_insMovieCine21List() throws Exception {
		int rt = testService.insMovieCine21List();
		System.out.println("#result:"+rt);
	}

	@Test
	public void testServiceTest1() throws Exception{
		testService.getTest1();
		//System.out.println("#Result:"+result);
	}

	@Test
	public void testServiceTest() throws Exception{
		String result = testService.getTest();
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_progs_nlp() throws Exception {
		MultipartFile file1 = FileUtils.convertFileToMultipart("E:\\upload\\", "test_csv.txt");
		testService.processFileToNlpsResult(file1);
	}

	@Test
	public void test_getsome() throws Exception {
		MultipartFile file1 = FileUtils.convertFileToMultipart("E:\\upload\\", "test_csv.txt");
		Map<String, Object> result = testService.processFileToNlpsSome(file1, 50);
		System.out.println("#result:"+result.toString());
	}


	@Test
	public void test_getItemsContent() throws Exception {
		List<ItemsContent> result = testService.getItemsContent();
		System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_getItemsContentProc() throws Exception {
		List<ItemsContent> result = testService.getItemsContentProc();
		System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_getItemsContentProc2() throws Exception {
		List<ItemsContent> result = testService.getItemsContentProc2();
		System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_writeCine21Csv() throws Exception {
		testService.writeCine21Csv();
		//System.out.println("#result:"+result.toString());
	}

	@Test
	public void test__insert_from_loadDataCine21Words() throws Exception {
		testService.insert_from_loadDataCine21Words();
	}

	@Test
	public void test__insert_from_loadDataCine21_metas() throws Exception {
		testService.insert_from_loadDataCine21_metas();
	}

	@Test
	public void test_loadYjDatas() throws Exception {
		List<Map<String,Object>> resultMap = testService.loadYjDatas("E:\\yj_datas.txt");
		System.out.println("#result:"+resultMap.toString());
	}

	@Test
	public void test_insYjItems() throws Exception {
		int rt = testService.insYjItems();
	}

	@Test
	public void test_getItemsCine21Second520() {
		List<ItemsContent> resultMap = testService.getItemsCine21Second520();
		System.out.println("#result:"+resultMap.toString());
	}

	@Test
	public void test_writeCine21Csv2_520() throws Exception {
		testService.writeCine21Csv2_520();
		//System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_getItemsCine21SecondFor10AndContentall() {
		List<ItemsContent> resultMap = testService.getItemsCine21SecondFor10AndContentall();
		System.out.println("#result:"+resultMap.toString());
	}

	@Test
	public void test_writeCine21Csv2_520SecondFor10AndContentall() throws Exception {
		testService.writeCine21Csv2_520SecondFor10AndContentall();
		//System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_getItemsYj01by01() {
		List<ItemsContent> resultMap = testService.getItemsYj01by01();
		System.out.println("#resultMap:" + resultMap.size());

		List<List<ItemsContent>> resultArr = testService.getSeperatedContent(resultMap, 50);
		System.out.println("#resultArr:" + resultArr.size());

		int cnt = 1;
		for (List<ItemsContent> res : resultArr) {
			System.out.println("#result:" + res.size());
			int x = 0;
			for (ItemsContent rr : res) {
				//if (x < 5) {
					//System.out.println("#title:"+rr.getTitle() + "	");
					//System.out.println("#key:"+rr.getAllKeywords());
					//System.out.println("##input data:"+rr.toString());

				//}
				x++;
			}
			System.out.println("");
			//System.out.println("#RESULT:"+ resultArr.toString());
			System.out.println("# "+cnt+ "'s resultArr.size:" + res.size());

			cnt++;
		}

		System.out.println("#resultMap:" + resultMap.size());
	}

	@Test
	public void test_writeYj01by01() throws Exception {
		testService.writeYj01by01();
		//System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_insDicNotUseWords_phase1() throws Exception {
		testService.insDicNotUseWords_phase1();
	}

	@Test
	public void test_getItemsCine21() throws Exception {
		List<ItemsContent> result = testService.getItemsCine21();
		System.out.println("#RESULT:"+result.toString());
	}

	@Test
	public void test_writeCine21_1659() throws Exception {
		testService.writeCine21();
		//System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_writeCine21_1600_sorted() throws Exception {
		testService.writeCine21_1600_sorted();
		//System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_loadCcubeMoviesDatas() throws Exception {
		testService.loadCcubeMoviesDatas();
	}

	@Test
	public void test_insCcubeMoviesDatas() throws Exception {
		List<CcubeContent> reqList = testService.loadCcubeMoviesDatas();
		testService.insCcubeMovies(reqList);
	}

	@Test
	public void test_getCcubeContents() throws Exception {
		List<CcubeContent> reqList = testService.getCcubeContents();
	}

	@Test
	public void test_writeCcubeContents_phase1_24123() throws Exception {
		testService.writeCcubeContents_phase1_24123();

	}

	@Test
	public void test_write_yjdatas_1st_20180208_sorted() throws Exception {
		testService.write_yjdatas_1st_20180208_sorted();
	}

	@Test
	public void test_getYcDatas1stFromTable() {
		List<List<Map<String, Object>>> result = testService.getYcDatas1stFromTable();


		int lineCnt = 1;
		for(List<Map<String, Object>> ic : result) {
			//if (lineCnt > 1) break;

			List<String> cidArr = new ArrayList();
			List<String> titlesArr = new ArrayList();
			List<Map<String, Object>> sortedMap = new ArrayList();

			System.out.println("#lineMap:"+ic.toString());
			System.out.println("#lineMap.size:"+ic.size());

			for(Map<String, Object> lineMap : ic) {
				Map<String, Object> newItem = new HashMap();

				titlesArr.add((String) lineMap.get("title"));
				cidArr.add((String) lineMap.get("content_id"));

				String tmpWords = (String) lineMap.get("words");
				tmpWords = tmpWords.replace("{","").replace("}","");
				String wordsArr[] = tmpWords.split(",");
				for (String w : wordsArr) {
					String ww = w.trim();
					String www[] = ww.split("=");
					if (www != null && www.length > 1) {
						newItem.put(www[0].trim(), www[1].trim());
					}
				}

				System.out.println("#newMap:"+newItem.toString());
				sortedMap.add(newItem);

			}

			List<String> list2 = testService.processResultListToRowsFiltered3(sortedMap, titlesArr, cidArr);

			System.out.println("list2:: result.size:"+list2.size() + "  datas::"+list2.toString());

			lineCnt++;
		}


		System.out.println("#result.size:"+result.size());
	}

	@Test
	public void test_write_ycDatas_1st_20180208_from_table() throws Exception {
		testService.write_ycDatas_1st_20180208_from_table();
	}

	@Test
	public void test_write_yjdatas_2st_20180220_sorted() throws Exception {
		testService.write_yjdatas_2st_20180220_sorted();
	}

	@Test
	public void test_write_ycDatas_2st_20180220_from_table() throws Exception {
		testService.write_ycDatas_2st_20180220_from_table();
	}



	@Test
	public void test_getItemsYj04() {
		List<ItemsContent> resultMap = testService.getItemsYj04();
		System.out.println("#resultMap:" + resultMap.size());
		//System.out.println("#:"+resultMap.toString());

		List<List<ItemsContent>> resultArr = testService.getSeperatedContent(resultMap, 50);
		System.out.println("#resultArr:" + resultArr.size());

		int cnt = 1;
		for (List<ItemsContent> res : resultArr) {
			System.out.println("#result:" + res.size());
			int x = 0;
			for (ItemsContent rr : res) {
				//if (x < 5) {
				//System.out.println("#title:"+rr.getTitle() + "	");
				//System.out.println("#key:"+rr.getAllKeywords());
				//System.out.println("##input data:"+rr.toString());

				//}
				System.out.println("##::"+rr.getTitle());
				//System.out.println("##::"+rr.toString());
				x++;
			}
			System.out.println("");
			//System.out.println("#RESULT:"+ resultArr.toString());
			System.out.println("# "+cnt+ "'s resultArr.size:" + res.size());

			cnt++;
		}

		System.out.println("#resultMap:" + resultMap.size());
	}

	@Test
	public void test_writeYj04() throws Exception {
		testService.writeYj04();
		//System.out.println("#result:"+result.toString());
	}

	@Test
	public void test_getYcDatas1st2() throws Exception {
		List<Map<String, Object>> result = testService.getYcDatas1st2();
		System.out.println("#RESULT.size:"+result.size());
	}

	@Test
	public void test_getYcDatas2st2() throws Exception {
		List<Map<String, Object>> result = testService.getYcDatas2st2();
		System.out.println("#RESULT.size:"+result.size());
	}

	@Test
	public void test_insDicKeywords__FromYcDatas1st1() throws Exception {
		testService.insDicKeywords__FromYcDatas1st1();
		//System.out.println("#RESULT.size:"+result.size());
	}
	@Test
	public void test_insDicKeywords__FromYcDatas1st2() throws Exception {
		testService.insDicKeywords__FromYcDatas1st2();
		//System.out.println("#RESULT.size:"+result.size());
	}

	@Test
	public void test_loadCcubeMoviesDatas_0226() throws Exception {
		testService.loadCcubeMoviesDatas0226();
	}

	@Test
	public void test_insCcubeMoviesDatas_0226() throws Exception {
		List<CcubeContent> reqList = testService.loadCcubeMoviesDatas0226();
		testService.insCcubeMovies0226(reqList);
	}

	@Test
	public void test_loadCcubeMoviesDatas_0226_2() throws Exception {
		testService.loadCcubeMoviesDatas0226_2();
	}
	@Test
	public void test_insCcubeMoviesDatas_0226_2() throws Exception {
		List<CcubeContent> reqList = testService.loadCcubeMoviesDatas0226_2();
		testService.insCcubeMovies0226(reqList);
	}

	@Test
	public void test_writeDicEmo0227() throws Exception {
		testService.writeDicEmo0227();

	}

	@Test
	public void test_processCalFreqFromDicKeywords() throws Exception {
		testService.processCalFreqFromDicKeywords();
	}

	@Test
	public void test_writeRankOfDicKeywordByFreq1() throws Exception {
		testService.writeRankOfDicKeywordByFreq1();
	}

}
