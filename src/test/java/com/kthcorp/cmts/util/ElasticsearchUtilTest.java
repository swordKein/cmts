package com.kthcorp.cmts.util;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import org.elasticsearch.common.xcontent.XContentFactory.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ElasticsearchUtilTest {
	@Value("${elasticsearch.url}")
	private String es_url;
	@Value("${elasticsearch.idx}")
	private String es_idx;

	@Test
	public void test_getSimpleWords() throws Exception {
		String result = ElasticsearchUtil.getSimpleWords(es_url + es_idx + "/_analyze?analyzer=korean&pretty", "데이터는하늘에도있고도없지");
		System.out.println("#result:" + result);

	}

	@Test
	public void test_getSimpleWords2() throws Exception {
		String result = ElasticsearchUtil.getSimpleWords(es_url + "testidx/items/_search", "정상화");
		System.out.println("#result:" + result);

	}

	@Test
	public void test_putData() throws Exception {
		Map<String, Object> newItem = new HashMap<String, Object>();
		newItem.put("keywords", "네 여보세요 안녕하십니까 KT 고객센터 박 영 네네 네 고객님 인터넷 위치 변경건으로 연락드렸는데 맞으십니까 예예 아 그러세요 고객님 위치변경을 하게되면 저희 엔지니어 내방해서 처리하기 때문에<br>부가세 포함해서 *****원이 다음달 근데요 이게<br>어느정도 사람에 유지를 좀 해주셔야 되는거 아니에요 TV 밑에 다 바로 북계 놓으셔서 까지고 TV다이 바로 밑으로 떨어트리는데도 그게 안 되면은 그게 저희가 돈을 드려야 되는거에요<br>이 부분은 엔지니어 방문하게끔 이게 출장비나 그럼 천천히 아니 근데 거기서 잘못하신거 잖아요 어느 정도는 위치변경을 가능하게 해주셔야지 무슨 딱 통화도 아주 무슨<br>그렇게 해놓고 가가지고 제가 불편한거잖아요<br>그럼 이전번호 아니지 알았어요 원래는 다 길게 해주시는데 어떻게 TV 바로 밑에다가 해놓으셔가지고 TV다이 바로 밑에 애가 자꾸만 만져가지고 그 내리는데 다 안되는 거는 그거는 거기서 해주셔야 되는거 아니에요<br>죄송합니다만 저희가 이 부분 설치하는 부분은 선을 열개 하진 않습니다 제가 깔끔하게 해달라고 하시는 분들이 계셔가지고 저는 그러면 한 적 없는데요 네 그런 부분으로 이제 는 저희는 선을 어느저 이력 이렇게 하는게 아니라 딱 맞춰서 해드려요<br>그 제가 그 원하지 않았다고요<br>저희 기본으로는 인제는 선을 딱 맞춰서 해 드리는 부분입니다<br>까 열개 막 해드린 부분이 아니라 기본으로 저희가 이제 선을 아니 그러니깐요 딱 맞춰서 해 주시는 거 맞는 건데 제가 다른데 옮기는것도 아니고 TV 아이 위에 있는거를 바닥으로 내리는데 도 안된다니깐요<br>그 정도 선도 안해주시면 어떻게 하겠다는 거야 도대체 애가 만져가지고 바닥으로 내리는데도 안되는데<br>어 근데 이 부분으로는 저희가 접수 한번 해드릴 수 있는 부분이 아니라서요<br>이렇게 위치 변경건은 아니 이정도 아니 위치 변경도 아니에요 거기에서 잠깐 닫아 번호만 내리는데 그게 얼마나 돼요 삼십센치 도 안되는 그 정도도 안되게 해놓으시면은<br>도대체 뭐 옮기란 오늘 전혀 움직이지도 못하고 게 있는데 그럼 에*가자꾸 만져가지고 전산이 자꾸만 빠져 가지고 이게 지금 TV도 뜯었다 않게 낫다 그러고 있는데 그런거는 그러면 고장신고 내야돼요<br>애가 자꾸만 만져가지고 한달후에 그냥 고장신고하면 되나요 그러면<br>지금 이게 지금 고장이 나고 있어요 그래서 그러는거에요 지금 TV 시청이나 이런 부분 안된다면 그냥 지금 안되는 거에요 그것도 지금 고객님 네 네 근데 지금 고객님 말씀대로 지금 이 부분은 위치로 이렇게 변경하는<br>그러면은 고장신고해주세요 왜냐면은 TV가 지금 제가 뽀뽀뽀 보는데 그것도 지금 *월달 꺼졌다<br>그것도 안되거든요 그래서 제가 그 전원 껐다가 키고 애가 자꾸 만지니까 그러면은 이게 자꾸 고장난거 같으니까 제가 자꾸 밑으로 내려주는 건데 그러면 고장인고 해야지 뭐 어떻게 하겠어요 그러면<br>댁내접수는 취소하고 TV 시청이 안된다고 지금 고장 접수하시는거 맞으신가요<br>네 고장 접수 해주세요 그러면 TV 시청이 지금 어느부분이 안되십니다 그니까 개통이 피쳐폰 그 채널있잖아요 그거를 제가 지금 정규 정액제로 해서 하거든요<br>근데 그게 어저께 그 버튼도 안눌러지고<br>그럼 아예 전원을 컴퓨 그 TV 그 뒤에 있잖아요 모뎀 모뎀 전원 다 끄고 나서 다시해야겠 삭제 셋팅이 되면서 켜져요 그렇게 지금 스피커폰만 안 된다는 거세요 다른 뭐  ****원 지금 다 돼<br>님<br>다른것도 그러면은 만약에 그거를 꺼졌다가 다시킬때 안 켜질때 그래서 TV다 그럼 고객님께서는 그래가 안터져 신청시 끊긴다는 말씀이신가요<br>시정지 끊기는게 아니라 TV를 껐다가 다시켜지지도 않는다고<br>꺼져서 시청할려고<br>고 하면은 예 드리고 예 모뎀이 모뎀자체가 다시켜지지가 않아요 그래서 그 모뎀을 또 다시전 원 다 그 코드 다 빼고 다시뜨거든요 그러세요 애가 만져서 그러는거 아닌가 싶어가지고 지금 받아고 내리는거에요<br>그러신데 이부분으로 제가 AS 접수해드릴텐데요<br>네 껐다가 다시켰을 때 이게 재부팅이 안된다는 같습니다 네 재부팅도 않는데요 라는 말씀이시고요 고객님 예 네 그럼 제가 몇가지만 확인한 다음에 TV AS 접수해드릴텐데요 가입자분 성함하고<br>고객님 설치 주소 건물명하고 동호수 말씀해주시겠습니까 가입자는 저기 관식 이고요 경기도 광주시 초원읍 네<br>**월이요 네 충의로요 네 어 그게 길네 ** 다시*네 나온 팰리스 네 ***동 ***호요<br>저희 엔지니어가 내방해서 고객님께 연락을 드릴텐데요 지금 연락드린 이 번호로 연락드려도 되겠습니까 네네 네 혹시 다른 추가 연락처 있으신가요 고객님 아니요 없어요<br>네 방문시간 한번 확인해<br>네 지금 방문시간은 오늘 오후 네시에서 *시  사 이 가능한데 괜찮으십니까 네 그러세요 그럼 고객님 AS 요청하시 AS 뿐만 아니라 사용하는 KT 모든 상품에 대해 KT 엔지니어가 점검할<br>매장 미리 연락드리고 방문하도록 하겠습니다 네 알겠습니다 접수는 취소 처리 하도록 하겠습니다 네 감사합니다 KT 박 영 칠<br>");

		String result = ElasticsearchUtil.putData(es_url + "testidx/item/2", newItem);
		System.out.println("#result:" + result);

	}

	@Test
	public void test_bulkIndexing() throws Exception {
		TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("14.63.170.72"), 9300));

		BulkRequestBuilder bulkRequest = client.prepareBulk();

// either use client#prepare, or use Requests# to directly build index/delete requests
		bulkRequest.add(client.prepareIndex("testidx", "item", "3")
				.setSource(jsonBuilder()
						.startObject()
						.field("keywords", "정상화 고객센터")
						.endObject()
				)
		);
/*
		bulkRequest.add(client.prepareIndex("twitter", "tweet", "2")
				.setSource(jsonBuilder()
						.startObject()
						.field("user", "kimchy")
						.field("postDate", new Date())
						.field("message", "another post")
						.endObject()
				)
		);
*/
		BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			// process failures by iterating through each bulk response item
		}

		System.out.println("#bulkResponse:"+bulkResponse.toString());
		client.close();

	}


	@Test
	public void testFileWrite() {
		try {
			FileUtils.transTestIdxData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetFile() {
		try {
			String res = FileUtils.getTestIdxData();
			System.out.println("#result:"+res);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testGetFileMap() {
		try {
			List<Map<String, Object>> resMap = FileUtils.getTestIdxDataMap();
			System.out.println("#resultMap:"+resMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_bulkIndexingAll() throws Exception {
		TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("14.63.170.72"), 9300));

		IndicesAdminClient indicesAdminClient = client.admin().indices();

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		//String jsonSource = FileUtils.getTestIdxData();

		List<Map<String,Object>> resMap = null;
		try {
			resMap = FileUtils.getTestIdxDataMap();
			//System.out.println("#resultMap:"+resMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(Map<String, Object> obj : resMap) {
			bulkRequest.add(client.prepareIndex("testidx8", "item", obj.get("id").toString())
					.setSource(obj.get("jsonSource").toString()));
		}

		BulkResponse bulkResponse = bulkRequest.get();

		if (bulkResponse.hasFailures()) {
			System.out.println("#fail:"+bulkResponse.getItems().toString());
			 //process failures by iterating through each bulk response item
		}

		System.out.println("#bulkResponse:"+bulkResponse.toString());

		client.close();

	}

	@Test
	public void test_fileIndexing() throws Exception {

		// The name of the file to open.
		String fileName = "D:\\TADATA";

		// This will reference one line at a time
		String line = null;


		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader =
					new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader =
					new BufferedReader(fileReader);

			int lineCnt = 0;
			List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

			while((line = bufferedReader.readLine()) != null && lineCnt < 6) {
				//System.out.println(line);
				String[] lineOrig = line.split("\\|");
				String id = lineOrig[0];
				String keywords = lineOrig[1];
				String date = lineOrig[4];

				//System.out.println("#id:"+id);
				//System.out.println("#keywords:"+keywords.substring(0,40));
				//System.out.println("#date:"+date);
				Map<String, Object> newMap = new HashMap<String, Object>();
				newMap.put("id", id);
				newMap.put("keywords", CommonUtil.removeTex(keywords));

				resultMapArr.add(newMap);

				lineCnt++;
			}

			// Always close files.
			bufferedReader.close();

			String jsonSource = JsonUtil.convertListMapToJsonArrayString(resultMapArr);
			System.out.println("#jsonSource:"+jsonSource);

		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" +
							fileName + "'");
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '"
							+ fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	}



	@Test
	public void test_file_reading_and_word_freq() throws Exception {

		// The name of the file to open.
		String fileName = "D:\\TADATA";

		// This will reference one line at a time
		String line = null;


		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader =
					new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader =
					new BufferedReader(fileReader);

			int lineCnt = 0;
			int checkCnt = 0;
			List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

			String contentAll = "";
			Map<String, Integer> map = new HashMap<String, Integer>();

			while((line = bufferedReader.readLine()) != null && lineCnt < 60000) {
				//System.out.println(line);
				String[] lineOrig = line.split("\\|");
				String id = lineOrig[0];
				String keywords_orig = lineOrig[1];
				String date = lineOrig[4];

				//System.out.println("#id:"+id);
				//System.out.println("#keywords:"+keywords.substring(0,40));
				//System.out.println("#date:"+date);
				//Map<String, Object> newMap = new HashMap<String, Object>();
				//newMap.put("id", id);
				//newMap.put("keywords", CommonUtil.removeTex(keywords));

				//resultMapArr.add(newMap);
				if (keywords_orig.contains("인터넷")) {
					String tag = SeunjeonUtil.getSimpleWords2Str(keywords_orig, null);
					tag = CommonUtil.removeTex(tag);
					tag = tag.trim();
					//System.out.println("#tag:" + tag);
					//contentAll = contentAll + " " + tag;
					// 동일 Map에 지속적인 Word-freq 저장
					map = WordFreqUtil.getWordCountsMapByMap(map, tag);
					checkCnt++;
				}
				lineCnt++;
				if (lineCnt % 2000 == 0) System.out.println("#lineCnt:"+lineCnt);
			}
			System.out.println("#checkCnt:"+checkCnt);
			// Always close files.
			bufferedReader.close();

			//Map<String, Integer> result = WordFreqUtil.getWordCountsMap(contentAll);
			Map<String, Integer> result2 = MapUtil.sortByValue(map);
			//System.out.println("#result2:"+result2.toString());
			/* map 300개 프린트 */
			MapUtil.printMapBySize(result2, 300);
			//String jsonSource = JsonUtil.convertListMapToJsonArrayString(resultMapArr);
			//System.out.println("#jsonSource:"+jsonSource);

		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" +
							fileName + "'");
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '"
							+ fileName + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}
	}
}
