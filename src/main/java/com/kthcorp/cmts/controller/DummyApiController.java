package com.kthcorp.cmts.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.service.AdminService;
import com.kthcorp.cmts.service.DicService;
import com.kthcorp.cmts.service.ItemsService;
import com.kthcorp.cmts.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
//@RequestMapping(value = {"", "/dummy"})
@Conditional(CheckAdminProfiles.class)
public class DummyApiController {
	private static final Logger logger = LoggerFactory.getLogger(DummyApiController.class);

	@Autowired
	private AdminService adminService;
	@Autowired
	private DicService dicService;
	@Autowired
	private ItemsService itemsService;

	// #1
	@RequestMapping(value = "/dummy/auth/hash", method = RequestMethod.GET)
	@ResponseBody
	public String get__auth_hash(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "authkey", required = false, defaultValue = "authkey") String authkey
	) {
		logger.debug("#/auth/hash get");

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");
		result_all.addProperty("RESULT", "HASH_TEST_00000000");

		return result_all.toString();
	}

	// #2
	@RequestMapping(value = "/dummy/auth/user/list", method = RequestMethod.GET)
	@ResponseBody
	public String get__auth_user_list(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
	) {
		logger.debug("#/auth/user/list get");

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonArray result1 = new JsonArray();
		JsonObject new1 = new JsonObject();
		new1.addProperty("USERID", "test1");
		new1.addProperty("GRANT", "ADMIN");
		new1.addProperty("REGDATE", "2017-12-31 00:00:00");
		result1.add(new1);
		JsonObject new2 = new JsonObject();
		new2.addProperty("USERID", "test2");
		new2.addProperty("GRANT", "ADMIN");
		new2.addProperty("REGDATE", "2017-12-31 00:00:00");
		result1.add(new2);

		result_all.add("RESULT", result1);

		return result_all.toString();
	}

	// #3
	@RequestMapping(value = "/dummy/auth/user/add", method = RequestMethod.POST)
	@ResponseBody
	public String post__auth_user_add(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "target_userid", required = false, defaultValue = "new_user") String target_userid
			, @RequestParam(value = "target_grant", required = false, defaultValue = "STAT") String target_grant
			, @RequestParam(value = "target_company", required = false, defaultValue = "") String target_company
			, @RequestParam(value = "target_name", required = false, defaultValue = "") String target_name
	) {
		logger.debug("#/auth/user/add input userid:" + target_userid);


		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		return result_all.toString();
	}

	// #4
	@RequestMapping(value = "/dummy/auth/user/login", method = RequestMethod.POST)
	@ResponseBody
	public String post__auth_user_login(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "userid", required = false, defaultValue = "userid") String userid
	) {
		logger.debug("#/auth/user/login input userid:" + userid);

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();
		result.addProperty("HASH", "HASH_TEST_00000001");
		result.addProperty("GRANT", "ADMIN");

		result_all.add("RESULT", result);

		return result_all.toString();
	}

	// #5
	@RequestMapping(value = "/dummy/dash/list", method = RequestMethod.GET)
	@ResponseBody
	public String get__dash_list(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
	) {
		logger.debug("#/dash/list get");

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();

		JsonObject list_stat = new JsonObject();
		list_stat.addProperty("COUNT_INSERTED", 20200);
		list_stat.addProperty("COUNT_INSERT_TAGGED", 10100);
		list_stat.addProperty("COUNT_START_COLLECT", 140);
		list_stat.addProperty("COUNT_COLLECTED", 130);
		list_stat.addProperty("COUNT_START_ANALYZE", 135);
		list_stat.addProperty("COUNT_ANALYZED", 135);
		list_stat.addProperty("COUNT_START_TAG", 135);
		list_stat.addProperty("COUNT_TAGGED", 10);
		result.add("LIST_STAT", list_stat);

		JsonObject list_sum = new JsonObject();
		list_sum.addProperty("COUNT_READY", 135);
		list_sum.addProperty("COUNT_FAIL_COLLECT", 10);
		list_sum.addProperty("COUNT_FAIL_ANALYZE", 0);
		list_sum.addProperty("COUNT_READY_TAG", 125);
		result.add("LIST_SUMMARY", list_sum);

		JsonObject list_ratio = new JsonObject();
		list_ratio.addProperty("RATIO_ALL_TAG", 170);
		list_ratio.addProperty("RATIO_COLLECT", 135);
		list_ratio.addProperty("RATIO_ANALYZE", 130);
		list_ratio.addProperty("RATIO_TAG", 10);
		result.add("LIST_RATIO", list_ratio);

		JsonObject list_graph_weekly = new JsonObject();
		JsonArray list_caption = new JsonArray();
		list_caption.add("W6");
		list_caption.add("W5");
		list_caption.add("W4");
		list_caption.add("W3");
		list_caption.add("W2");
		list_caption.add("W1");
		list_graph_weekly.add("LIST_CAPTION", list_caption);
		JsonArray list_count_inserted = new JsonArray();
		list_count_inserted.add(0);
		list_count_inserted.add(0);
		list_count_inserted.add(0);
		list_count_inserted.add(0);
		list_count_inserted.add(0);
		list_count_inserted.add(0);
		list_graph_weekly.add("LIST_COUNT_INSERTED", list_count_inserted);
		JsonArray list_count_collected = new JsonArray();
		list_count_collected.add(0);
		list_count_collected.add(0);
		list_count_collected.add(0);
		list_count_collected.add(0);
		list_count_collected.add(0);
		list_count_collected.add(0);
		list_graph_weekly.add("LIST_COUNT_COLLECTED", list_count_collected);
		JsonArray list_count_analyzed = new JsonArray();
		list_count_analyzed.add(0);
		list_count_analyzed.add(0);
		list_count_analyzed.add(0);
		list_count_analyzed.add(0);
		list_count_analyzed.add(0);
		list_count_analyzed.add(0);
		list_graph_weekly.add("LIST_COUNT_ANALYZED", list_count_analyzed);
		JsonArray list_count_tagged = new JsonArray();
		list_count_tagged.add(0);
		list_count_tagged.add(0);
		list_count_tagged.add(0);
		list_count_tagged.add(0);
		list_count_tagged.add(0);
		list_count_tagged.add(0);
		list_graph_weekly.add("LIST_COUNT_TAGGED", list_count_tagged);
		result.add("LIST_GRAPH_WEEKLY", list_graph_weekly);


		JsonObject list_graph_daily = new JsonObject();
		JsonArray dlist_caption = new JsonArray();
		dlist_caption.add("D7");
		dlist_caption.add("D6");
		dlist_caption.add("D5");
		dlist_caption.add("D4");
		dlist_caption.add("D3");
		dlist_caption.add("D2");
		dlist_caption.add("D1");
		list_graph_daily.add("LIST_CAPTION", dlist_caption);
		JsonArray dlist_count_inserted = new JsonArray();
		dlist_count_inserted.add(95);
		dlist_count_inserted.add(84);
		dlist_count_inserted.add(66);
		dlist_count_inserted.add(42);
		dlist_count_inserted.add(36);
		dlist_count_inserted.add(98);
		dlist_count_inserted.add(74);
		list_graph_daily.add("LIST_COUNT_INSERTED", dlist_count_inserted);
		JsonArray dlist_count_collected = new JsonArray();
		dlist_count_collected.add(93);
		dlist_count_collected.add(82);
		dlist_count_collected.add(63);
		dlist_count_collected.add(40);
		dlist_count_collected.add(35);
		dlist_count_collected.add(96);
		dlist_count_collected.add(72);
		list_graph_daily.add("LIST_COUNT_COLLECTED", dlist_count_collected);
		JsonArray dlist_count_analyzed = new JsonArray();
		dlist_count_analyzed.add(82);
		dlist_count_analyzed.add(82);
		dlist_count_analyzed.add(62);
		dlist_count_analyzed.add(32);
		dlist_count_analyzed.add(24);
		dlist_count_analyzed.add(80);
		dlist_count_analyzed.add(70);
		list_graph_daily.add("LIST_COUNT_ANALYZED", dlist_count_analyzed);
		JsonArray dlist_count_tagged = new JsonArray();
		dlist_count_tagged.add(25);
		dlist_count_tagged.add(33);
		dlist_count_tagged.add(58);
		dlist_count_tagged.add(18);
		dlist_count_tagged.add(22);
		dlist_count_tagged.add(75);
		dlist_count_tagged.add(60);
		list_graph_daily.add("LIST_COUNT_TAGGED", dlist_count_tagged);
		result.add("LIST_GRAPH_DAILY", list_graph_daily);

		result_all.add("RESULT", result);

		return result_all.toString();
	}


	// #6
	@RequestMapping(value = "/dummy/item/list", method = RequestMethod.POST)
	@ResponseBody
	public String post__item_list(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "pagesize", required = false, defaultValue = "0") String spagesize
			, @RequestParam(value = "pageno", required = false, defaultValue = "0") String spageno

			, @RequestParam(value = "searchtype", required = false, defaultValue = "") String searchtype
			, @RequestParam(value = "searchstat", required = false, defaultValue = "") String searchstat
			, @RequestParam(value = "searchsdate", required = false, defaultValue = "") String searchsdate
			, @RequestParam(value = "searchedate", required = false, defaultValue = "") String searchedate
			, @RequestParam(value = "saarchkeyword", required = false, defaultValue = "") String saarchkeyword
			, @RequestParam(value = "searchparts", required = false, defaultValue = "") String searchparts
	) {
		logger.debug("#/item/list get");

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();

		result.addProperty("PAGESIZE", 20);
		result.addProperty("MAXPAGE", 6);
		result.addProperty("PAGENO", 1);

		JsonArray list_paging = new JsonArray();
		list_paging.add(1);
		list_paging.add(2);
		list_paging.add(3);
		list_paging.add(4);
		list_paging.add(5);
		result.add("LIST_PAGING", list_paging);

		result.addProperty("SEARCHTYPE", "ALL");
		result.addProperty("SEARCHSTAT", "ALL");
		result.addProperty("SEARCHSDATE", "");
		result.addProperty("SEARCHEDATE", "");
		result.addProperty("SEARCHKEYWORD", "");
		result.addProperty("SEARCHPARTARRAY", "[]");

		JsonObject stat_search = new JsonObject();
		stat_search.addProperty("COUNT_ALL", 80);
		stat_search.addProperty("COUNT_FAIL_COLLECT", 10);
		stat_search.addProperty("COUNT_FAIL_ANALYZE", 3);
		stat_search.addProperty("COUNT_READY_TAG", 20);
		stat_search.addProperty("COUNT_TAGGED", 53);
		result.add("COUNTS_SEARCH", stat_search);

		JsonArray list_items = new JsonArray();
		JsonObject n1 = new JsonObject();
		n1.addProperty("TITLE", "라이언 일병 구하기");
		n1.addProperty("CID", "06900004510000125");
		n1.addProperty("TYPE", "OTH");
		n1.addProperty("CNT_TAG", 1);
		n1.addProperty("REGDATE", "2017-12-23");
		n1.addProperty("PROCDATE", "2017-12-24");
		n1.addProperty("STAT", "RT");
		list_items.add(n1);
		JsonObject n2 = new JsonObject();
		n2.addProperty("TITLE", "1724 기방난동사건");
		n2.addProperty("CID", "03900002310000007");
		n2.addProperty("TYPE", "KOR");
		n2.addProperty("CNT_TAG", 1);
		n2.addProperty("REGDATE", "2017-12-25");
		n2.addProperty("PROCDATE", "2017-12-26");
		n2.addProperty("STAT", "FC");
		list_items.add(n2);
		result.add("LIST_ITEMS", list_items);

		result_all.add("RESULT", result);


		return result_all.toString();
	}

	// #7
	@RequestMapping(value = "/dummy/item/upt/one", method = RequestMethod.POST)
	@ResponseBody
	public String post__item_upt_one(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "0") String itemid
	) {
		logger.debug("#/item/upt/one input itemid:" + itemid);

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");


		return result_all.toString();
	}


	// #8
	@RequestMapping(value = "/dummy/item/upt/array", method = RequestMethod.POST)
	@ResponseBody
	public String post__item_upt_array(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "items", required = false, defaultValue = "") String items
			, @RequestParam(value = "target_type", required = false, defaultValue = "") String target_type
	) {
		logger.debug("#/item/upt/one input items:" + items + "/target_type:" + target_type);

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");


		return result_all.toString();
	}


	// #9
	@RequestMapping(value = "/dummy/pop/movie", method = RequestMethod.GET)
	@ResponseBody
	public String get__pop_movie(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "0") String itemid
	) {
		logger.debug("#/pop/movie get for itemid:"+itemid);

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();
		result.addProperty("TITLE", "라이언 일병 구하기");
		result.addProperty("OTITLE", "Saving Private Ryan, 1998");
		result.addProperty("SERIESYN", "N");
		result.addProperty("YEAR", "1998-09-12");
		result.addProperty("DIRECTOR", "스티븐 스필버그");
		result.addProperty("ACTOR", "톰 행크스");
		result.addProperty("GENRE", "전쟁,액션,드라마");
		result.addProperty("PLOT", "1944년 6월 6일 노르망디 상륙 작전. 병사들은 죽을 고비를 넘기고 임무를 완수하지만, 실종된 유일한 생존자 막내 라이언 일병을 구하는 임무를 맡는다. 그들은 과연 라이언 일병 한 명의 생명이 그들 여덟 명의 생명보다 더 가치가 있는 것인지 혼란에 빠진다.");

		result_all.add("RESULT", result);

		return result_all.toString();
	}



	// #10
	@RequestMapping(value = "/dummy/pop/meta", method = RequestMethod.GET)
	@ResponseBody
	public String get__pop_meta(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "0") String itemid
	) {
		logger.debug("#/pop/movie get for itemid:"+itemid);

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();
		result.addProperty("DURATION", "6m");

		JsonArray metaswhen = new JsonArray();

		JsonObject when2 = new JsonObject();
		when2.addProperty("word", "2차세계대전");
		when2.addProperty("type", "dup");
		when2.addProperty("ratio", 7.8);
		metaswhen.add(when2);
		JsonObject when1 = new JsonObject();
		when1.addProperty("word", "1944년");
		when1.addProperty("type", "new");
		when1.addProperty("ratio", 6.5);
		metaswhen.add(when1);
		JsonObject when3 = new JsonObject();
		when3.addProperty("word", "20세기");
		when3.addProperty("type", "new");
		when3.addProperty("ratio", 4.3);
		metaswhen.add(when3);
		result.add("METASWHEN", metaswhen);

		JsonArray metaswhere = new JsonArray();
		JsonObject where1 = new JsonObject();
		where1.addProperty("word", "노르망디");
		where1.addProperty("type", "new");
		where1.addProperty("ratio", 8.7);
		metaswhere.add(where1);
		where1 = new JsonObject();
		where1.addProperty("word", "오마하해변");
		where1.addProperty("type", "dup");
		where1.addProperty("ratio", 6.1);
		metaswhere.add(where1);
		where1 = new JsonObject();
		where1.addProperty("word", "프랑스");
		where1.addProperty("type", "new");
		where1.addProperty("ratio", 3.4);
		metaswhere.add(where1);
		where1 = new JsonObject();
		where1.addProperty("word", "라멜");
		where1.addProperty("type", "new");
		where1.addProperty("ratio", 3.1);
		metaswhere.add(where1);
		where1 = new JsonObject();
		where1.addProperty("word", "다리");
		where1.addProperty("type", "new");
		where1.addProperty("ratio", 3.0);
		metaswhere.add(where1);
		result.add("METASWHERE", metaswhere);

		JsonArray metaswhat = new JsonArray();
		JsonObject what1 = new JsonObject();
		what1.addProperty("word", "전쟁");
		what1.addProperty("type", "dup");
		what1.addProperty("ratio", 7.3);
		metaswhat.add(what1);
		JsonObject what2 = new JsonObject();
		what2.addProperty("word", "실화");
		what2.addProperty("type", "new");
		what2.addProperty("ratio", 6.5);
		metaswhat.add(what2);
		what2 = new JsonObject();
		what2.addProperty("word", "전우애");
		what2.addProperty("type", "new");
		what2.addProperty("ratio", 6.2);
		metaswhat.add(what2);
		what2 = new JsonObject();
		what2.addProperty("word", "노르망디상륙작전");
		what2.addProperty("type", "new");
		what2.addProperty("ratio", 5.9);
		metaswhat.add(what2);
		what2 = new JsonObject();
		what2.addProperty("word", "임무");
		what2.addProperty("type", "dup");
		what2.addProperty("ratio", 4.2);
		metaswhat.add(what2);
		what2 = new JsonObject();
		what2.addProperty("word", "작전");
		what2.addProperty("type", "new");
		what2.addProperty("ratio", 3.7);
		metaswhat.add(what2);
		what2 = new JsonObject();
		what2.addProperty("word", "생명");
		what2.addProperty("type", "new");
		what2.addProperty("ratio", 2.1);
		metaswhat.add(what2);
		result.add("METASWHAT", metaswhat);

		JsonArray metaswho = new JsonArray();
		JsonObject who1 = new JsonObject();
		who1.addProperty("word", "병사");
		who1.addProperty("type", "new");
		who1.addProperty("ratio", 7.4);
		metaswho.add(who1);
		who1 = new JsonObject();
		who1.addProperty("word", "일병");
		who1.addProperty("type", "dup");
		who1.addProperty("ratio", 7.2);
		metaswho.add(who1);
		who1 = new JsonObject();
		who1.addProperty("word", "대위");
		who1.addProperty("type", "new");
		who1.addProperty("ratio", 5.1);
		metaswho.add(who1);
		who1 = new JsonObject();
		who1.addProperty("word", "미군");
		who1.addProperty("type", "ext");
		who1.addProperty("ratio", 5.0);
		metaswho.add(who1);
		who1 = new JsonObject();
		who1.addProperty("word", "독일군");
		who1.addProperty("type", "ext");
		who1.addProperty("ratio", 4.9);
		metaswho.add(who1);
		who1 = new JsonObject();
		who1.addProperty("word", "형제");
		who1.addProperty("type", "new");
		who1.addProperty("ratio", 2.5);
		metaswho.add(who1);
		who1 = new JsonObject();
		who1.addProperty("word", "막내");
		who1.addProperty("type", "new");
		who1.addProperty("ratio", 2.1);
		metaswho.add(who1);
		who1 = new JsonObject();
		who1.addProperty("word", "부상병");
		who1.addProperty("type", "new");
		who1.addProperty("ratio", 2.1);
		metaswho.add(who1);
		result.add("METASWHO", metaswho);

		JsonArray metasemotion = new JsonArray();
		JsonObject emotion1 = new JsonObject();
		emotion1.addProperty("word", "감동적");
		emotion1.addProperty("type", "ext");
		emotion1.addProperty("ratio", 5.3);
		metasemotion.add(emotion1);
		emotion1 = new JsonObject();
		emotion1.addProperty("word", "충격적");
		emotion1.addProperty("type", "new");
		emotion1.addProperty("ratio", 4.3);
		metasemotion.add(emotion1);
		emotion1 = new JsonObject();
		emotion1.addProperty("word", "사실적");
		emotion1.addProperty("type", "new");
		emotion1.addProperty("ratio", 4.2);
		metasemotion.add(emotion1);
		emotion1 = new JsonObject();
		emotion1.addProperty("word", "참혹한");
		emotion1.addProperty("type", "new");
		emotion1.addProperty("ratio", 2.5);
		metasemotion.add(emotion1);
		result.add("METASEMOTION", metasemotion);

		JsonArray list_not_mapped = new JsonArray();

		JsonObject not1 = new JsonObject();
		not1.addProperty("word", "공수부대");
		not1.addProperty("type", "new");
		not1.addProperty("ratio", 3.7);
		list_not_mapped.add(not1);
		JsonObject not2 = new JsonObject();
		not2.addProperty("word", "낙하산");
		not2.addProperty("type", "new");
		not2.addProperty("ratio", 3.2);
		list_not_mapped.add(not2);
		result.add("LIST_NOT_MAPPED", list_not_mapped);

		JsonArray words_genre = new JsonArray();
		words_genre.add("감동적인");
		words_genre.add("사실적인");
		words_genre.add("짜릿한");
		words_genre.add("낭만적인");
		words_genre.add("감상적인");
		result.add("WORDS_GENRE", words_genre);

		JsonArray words_sns = new JsonArray();
		words_sns.add("대박");
		words_sns.add("압권");
		words_sns.add("걸작");
		words_sns.add("참혹한");
		words_sns.add("허무한");
		result.add("WORDS_SNS", words_sns);

		JsonArray words_assoc = new JsonArray();
		words_assoc.add("감명적인");
		words_assoc.add("감탄하는");
		words_assoc.add("감격스러운");
		words_assoc.add("낭만적인");
		words_assoc.add("감미로운");
		result.add("WORDS_ASSOC", words_assoc);

		JsonArray list_subgenre = new JsonArray();
		list_subgenre.add("실화 바탕 드라마");
		list_subgenre.add("밀리터리 드라마");
		list_subgenre.add("블락버스터");
		list_subgenre.add("전쟁액션모험");
		result.add("LIST_SUBGENRE", list_subgenre);

		JsonArray list_searchkeywords = new JsonArray();
		list_searchkeywords.add("노르망디");
		list_searchkeywords.add("2차세계대전");
		list_searchkeywords.add("전쟁");
		result.add("LIST_SEARCHKEYWORDS", list_searchkeywords);

		JsonArray list_reco_target = new JsonArray();
		//list_reco_target.add("다시 군대가는 꿈을 꾼 당신에게");
		result.add("LIST_RECO_TARGET", list_reco_target);

		JsonArray list_reco_situation = new JsonArray();
		result.add("LIST_RECO_SITUATION", list_reco_situation);

		result_all.add("RESULT", result);

		return result_all.toString();
	}


	// #11
	@RequestMapping(value = "/dummy/pop/award", method = RequestMethod.GET)
	@ResponseBody
	public String get__pop_award(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "0") String itemid
	) {
		logger.debug("#/pop/award get for itemid:"+itemid);

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();
		result.addProperty("AWARD", "\"<dl class=\\\"list_produce\\\"> \\n <dt>\\n  수상\\n </dt> \\n <dd> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=2611\\\" class=\\\"link_person #click\\\">스티븐 스필버그</a> (감독상) </span> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=1916\\\" class=\\\"link_person #click\\\">야누즈 카민스키</a> (촬영상) </span> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">음향상</span> </span> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">편집상</span> </span> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">음향편집상</span> </span> \\n </dd> \\n</dl>\\n<dl class=\\\"list_produce\\\"> \\n <dt>\\n  후보\\n </dt> \\n <dd> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">작품상</span> </span> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=516\\\" class=\\\"link_person #click\\\">톰 행크스</a> (남우주연상) </span> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=25095\\\" class=\\\"link_person #click\\\">로버트 로뎃</a> (각본상) </span> \\n </dd> \\n</dl>\"");

		result_all.add("RESULT", result);

		return result_all.toString();
	}


	// #12
	@RequestMapping(value = "/dummy/pop/c_cube", method = RequestMethod.GET)
	@ResponseBody
	public String get__pop_c_cube(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "0") String itemid
	) {
		logger.debug("#/pop/c_cube get for itemid:"+itemid);

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();
		result.addProperty("SERIES_ID", "");
		result.addProperty("SERIES_NM", "");
		result.addProperty("MASTER_CONTENT_ID", "1002442168");
		result.addProperty("CONTENT_ID", "10024421680001");
		result.addProperty("PURITY_TITLE", "소림배구");
		result.addProperty("CONTENT_TITLE", "소림 배구단");
		result.addProperty("ENG_TITLE", "Beach Spike!");
		result.addProperty("TITLE_BRIEF", "소림배구");
		result.addProperty("DIRECTOR", "토니 탕");
		result.addProperty("YEAR", "2011");
		result.addProperty("ACTORS_DISPLAY", "주수나,부영");
		result.addProperty("COUNTRY_OF_ORIGIN", "HKG");
		result.addProperty("SAD_CTGRY_NM", "영화 > 외국영화");
		result.addProperty("DOMESTIC_RELEASE_DATE", "2015-04-09");
		result.addProperty("KT_RATING", "15세 이상");
		result.addProperty("KMRB_ID", "20156354");

		result_all.add("RESULT", result);

		return result_all.toString();
	}


	//#13
	@RequestMapping(value = "/dummy/pop/cine21", method = RequestMethod.GET)
	@ResponseBody
	public String get__pop_cine21(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "0") String itemid
	) {
		logger.debug("#/pop/cine21 get for itemid:"+itemid);

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();

		JsonArray words_cine21_arr = new JsonArray();
		words_cine21_arr.add("노르망디");
		words_cine21_arr.add("병사");
		words_cine21_arr.add("일병");
		words_cine21_arr.add("임무");
		words_cine21_arr.add("2차시계대전");
		result.add("WQRDS_CINE21", words_cine21_arr);


		result_all.add("RESULT", result);

		return result_all.toString();
	}


	// #14
	@RequestMapping(value = "/dummy/pop/meta/upt/array", method = RequestMethod.POST)
	@ResponseBody
	public String post__pop_meta_upt_array(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "items", required = false, defaultValue = "") String items
			, @RequestParam(value = "duration", required = false, defaultValue = "") String duration
	) {
		logger.debug("#/pop/meta/upt/array input items:" + items + "/duration:" + duration);

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");


		return result_all.toString();
	}


	// #15
	@RequestMapping(value = "/dummy/pop/meta/restore", method = RequestMethod.POST)
	@ResponseBody
	public String post__pop_meta_restore(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "") String itemid
	) {
		logger.debug("#/pop/meta/restore input itemid:" + itemid);

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");


		return result_all.toString();
	}



	// #16
	@RequestMapping(value = "/dummy/pop/meta/uptstat", method = RequestMethod.POST)
	@ResponseBody
	public String post__pop_meta_uptstat(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "itemid", required = false, defaultValue = "") String itemid
			, @RequestParam(value = "stat", required = false, defaultValue = "") String stat
	) {
		logger.debug("#/pop/meta/uptstat input itemid:" + itemid+"/stat:"+stat);

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");


		return result_all.toString();
	}



	//#17
	@RequestMapping(value = "/dummy/dic/list", method = RequestMethod.GET)
	@ResponseBody
	public String get__dic_list(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "type", required = false, defaultValue = "WHEN") String type
			, @RequestParam(value = "pagesize", required = false, defaultValue = "0") String spagesize
			, @RequestParam(value = "pageno", required = false, defaultValue = "0") String spageno
	) {
		logger.debug("#/dic/list get for type:"+type);

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();
		result.addProperty("MAXPAGE", 5);
		result.addProperty("TYPE", "WHEN");
		result.addProperty("PAGESIZE", 500);
		result.addProperty("PAGENO", 1);

		JsonArray list_paging = new JsonArray();
		list_paging.add(1);
		list_paging.add(2);
		list_paging.add(3);
		list_paging.add(4);
		list_paging.add(5);
		result.add("LIST_PAGING", list_paging);

		JsonArray list_words = new JsonArray();
		list_words.add("아카데미");
		list_words.add("21세기");
		list_words.add("실화");
		list_words.add("전우애");
		result.add("LIST_WORDS", list_words);


		result_all.add("RESULT", result);

		return result_all.toString();
	}



	// #18
	@RequestMapping(value = "/dummy/dic/upt/array", method = RequestMethod.POST)
	@ResponseBody
	public String post__dic_upt_array(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "items", required = false, defaultValue = "") String items
	) {
		logger.debug("#/dic/upt/array input items:" + items);

		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");


		return result_all.toString();
	}



	// #19
	@RequestMapping(value = "/dummy/auth/user/mod", method = RequestMethod.POST)
	@ResponseBody
	public String post__auth_user_mod(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "target_userid", required = false, defaultValue = "new_user") String target_userid
			, @RequestParam(value = "target_grant", required = false, defaultValue = "STAT") String target_grant
			, @RequestParam(value = "target_company", required = false, defaultValue = "") String target_company
			, @RequestParam(value = "target_name", required = false, defaultValue = "") String target_name
	) {
		logger.debug("#/auth/user/mod input userid:" + target_userid);


		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		return result_all.toString();
	}


	// #20
	@RequestMapping(value = "/dummy/auth/user/del", method = RequestMethod.POST)
	@ResponseBody
	public String post__auth_user_del(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "target_userid", required = false, defaultValue = "new_user") String target_userid
	) {
		logger.debug("#/auth/user/del input userid:" + target_userid);


		JsonObject result_all = new JsonObject();
		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		return result_all.toString();
	}

	//#21
	@RequestMapping(value = "/dummy/social", method = RequestMethod.GET)
	@ResponseBody
	public String get__social(Model model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
	) {
		logger.debug("#/social get");

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();

		JsonArray words_instagram = new JsonArray();
		words_instagram.add("꾼");
		words_instagram.add("저스티스 리그");
		words_instagram.add("범죄도시");
		words_instagram.add("기억의 밤");
		words_instagram.add("오리엔트 특급 살인");
		words_instagram.add("버킷리스트: 죽기 전에 꼭 하고 싶은 것들");
		words_instagram.add("침묵");
		words_instagram.add("반드시 잡는다");
		words_instagram.add("러빙 빈센트");
		words_instagram.add("토르: 라그나로크");

		result.add("WORDS_INSTAGRAM", words_instagram);

		JsonObject graph_instagram = new JsonObject();
		JsonArray captions = new JsonArray();
		captions.add("D-13"); captions.add("D-12"); captions.add("D-11");
		captions.add("D-10"); captions.add("D-9"); captions.add("D-8"); captions.add("D-7"); captions.add("D-6");
		captions.add("D-5"); captions.add("D-4"); captions.add("D-3"); captions.add("D-2"); captions.add("D-1");
		graph_instagram.add("CAPTIONS", captions);
		JsonArray item1 = new JsonArray();
		item1.add(3);  item1.add(4); item1.add(5);
		item1.add(6);  item1.add(3); item1.add(1); item1.add(2); item1.add(1);
		item1.add(1);  item1.add(2); item1.add(1); item1.add(1); item1.add(1);
		graph_instagram.add("ITEM01", item1);
		JsonArray item2 = new JsonArray();
		item2.add(2);  item2.add(3); item2.add(2);
		item2.add(2);  item2.add(3); item2.add(2); item2.add(2); item2.add(2);
		item2.add(2);  item2.add(3); item2.add(2); item2.add(2); item2.add(2);
		graph_instagram.add("ITEM02", item2);
		JsonArray item3 = new JsonArray();
		item3.add(3);  item3.add(1); item3.add(3);
		item3.add(3);  item3.add(1); item3.add(3); item3.add(3); item3.add(3);
		item3.add(3);  item3.add(1); item3.add(3); item3.add(3); item3.add(3);
		graph_instagram.add("ITEM03", item3);
		JsonArray item4 = new JsonArray();
		item4.add(10);  item4.add(9); item4.add(7);
		item4.add(4);  item4.add(5); item4.add(6); item4.add(3); item4.add(2);
		item4.add(2);  item4.add(1); item4.add(3); item4.add(4); item4.add(4);
		graph_instagram.add("ITEM04", item4);
		JsonArray item5 = new JsonArray();
		item5.add(10);  item5.add(9); item5.add(7);
		item5.add(4);  item5.add(5); item5.add(6); item5.add(3); item5.add(2);
		item5.add(2);  item5.add(1); item5.add(3); item5.add(4); item5.add(4);
		graph_instagram.add("ITEM05", item5);
		JsonArray item6 = new JsonArray();
		item6.add(10);  item6.add(9); item6.add(7);
		item6.add(4);  item6.add(5); item6.add(6); item6.add(3); item6.add(2);
		item6.add(2);  item6.add(1); item6.add(3); item6.add(4); item6.add(4);
		graph_instagram.add("ITEM06", item6);
		JsonArray item7 = new JsonArray();
		item7.add(10);  item7.add(9); item7.add(7);
		item7.add(4);  item7.add(5); item7.add(6); item7.add(3); item7.add(2);
		item7.add(2);  item7.add(1); item7.add(3); item7.add(4); item7.add(4);
		graph_instagram.add("ITEM07", item7);
		JsonArray item8 = new JsonArray();
		item8.add(10);  item8.add(9); item8.add(7);
		item8.add(4);  item8.add(5); item8.add(6); item8.add(3); item8.add(2);
		item8.add(2);  item8.add(1); item8.add(3); item8.add(4); item8.add(4);
		graph_instagram.add("ITEM08", item8);
		JsonArray item9 = new JsonArray();
		item9.add(10);  item9.add(9); item9.add(7);
		item9.add(4);  item9.add(5); item9.add(6); item9.add(3); item9.add(2);
		item9.add(2);  item9.add(1); item9.add(3); item9.add(4); item9.add(4);
		graph_instagram.add("ITEM09", item9);
		JsonArray item10 = new JsonArray();
		item10.add(10);  item10.add(9); item10.add(7);
		item10.add(4);  item10.add(5); item10.add(6); item10.add(3); item10.add(2);
		item10.add(2);  item10.add(1); item10.add(3); item10.add(4); item10.add(4);
		graph_instagram.add("ITEM10", item10);

		result.add("GRAPH_INSTAGRAM", graph_instagram);

		JsonArray words_twitter = new JsonArray();
		words_twitter.add("꾼");
		words_twitter.add("저스티스 리그");
		words_twitter.add("범죄도시");
		words_twitter.add("기억의 밤");
		words_twitter.add("오리엔트 특급 살인");
		words_twitter.add("버킷리스트: 죽기 전에 꼭 하고 싶은 것들");
		words_twitter.add("침묵");
		words_twitter.add("반드시 잡는다");
		words_twitter.add("러빙 빈센트");
		words_twitter.add("토르: 라그나로크");
		result.add("WORDS_TWITTER", words_twitter);

		result.add("GRAPH_TWITTER", graph_instagram);


		result_all.add("RESULT", result);

		return result_all.toString();
	}




	// #22
	@RequestMapping(value = "/dummy/stat/list", method = RequestMethod.POST)
	@ResponseBody
	public String post__stat_list(Map<String, Object> model
			, @RequestParam(value = "custid", required = false, defaultValue = "ollehmeta") String custid
			, @RequestParam(value = "hash", required = false, defaultValue = "hash") String hash
			, @RequestParam(value = "pagesize", required = false, defaultValue = "0") String spagesize
			, @RequestParam(value = "pageno", required = false, defaultValue = "0") String spageno

			, @RequestParam(value = "searchstat", required = false, defaultValue = "") String searchstat
			, @RequestParam(value = "searchsdate", required = false, defaultValue = "") String searchsdate
			, @RequestParam(value = "searchedate", required = false, defaultValue = "") String searchedate
	) {
		logger.debug("#/stat/list get");

		JsonObject result_all = new JsonObject();

		result_all.addProperty("RT_CODE", 1);
		result_all.addProperty("RT_MSG", "SUCCESS");

		JsonObject result = new JsonObject();
		result.addProperty("PAGESIZE", 20);
		result.addProperty("MAXPAGE", 6);
		result.addProperty("PAGENO", 1);

		JsonArray list_paging = new JsonArray();
		list_paging.add(1);
		list_paging.add(2);
		list_paging.add(3);
		list_paging.add(4);
		list_paging.add(5);
		result.add("LIST_PAGING", list_paging);

		result.addProperty("SEARCHSTAT", "ALL");
		result.addProperty("SEARCHSDATE", "");
		result.addProperty("SEARCHEDATE", "");


		JsonObject stat_search = new JsonObject();
		stat_search.addProperty("COUNT_IN", 80);
		stat_search.addProperty("COUNT_SC", 70);
		stat_search.addProperty("COUNT_FC", 10);
		stat_search.addProperty("COUNT_SA", 65);
		stat_search.addProperty("COUNT_FA", 5);
		stat_search.addProperty("COUNT_ST", 62);
		stat_search.addProperty("COUNT_RT", 2);
		stat_search.addProperty("COUNT_FT", 1);
		result.add("COUNTS_STAT", stat_search);

		JsonArray list_items = new JsonArray();
		JsonObject n1 = new JsonObject();
		n1.addProperty("TITLE", "피나 3D");
		n1.addProperty("CID", "000001");
		n1.addProperty("TYPE", "OTH");
		n1.addProperty("CNT_TAG", 1);
		n1.addProperty("REGDATE", "2017-12-23");
		n1.addProperty("PROCDATE", "2017-12-24");
		n1.addProperty("STAT", "RT");
		n1.addProperty("CNT_IN", 1);
		n1.addProperty("CNT_COL", 1);
		n1.addProperty("CNT_ANA", 1);
		n1.addProperty("CNT_TAG2", 1);
		list_items.add(n1);
		JsonObject n2 = new JsonObject();
		n2.addProperty("TITLE", "1724 기방난동사건");
		n2.addProperty("CID", "000036");
		n2.addProperty("TYPE", "KOR");
		n2.addProperty("CNT_TAG", 0);
		n2.addProperty("REGDATE", "2017-12-25");
		n2.addProperty("PROCDATE", "2017-12-26");
		n2.addProperty("STAT", "FC");
		n2.addProperty("CNT_IN", 1);
		n2.addProperty("CNT_COL", 1);
		n2.addProperty("CNT_ANA", 0);
		n2.addProperty("CNT_TAG2", 0);
		list_items.add(n2);
		result.add("LIST_ITEMS", list_items);

		result_all.add("RESULT", result);


		return result_all.toString();
	}
}