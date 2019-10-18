package com.kthcorp.cmts.controller;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.service.*;
import com.kthcorp.cmts.service.crawl.*;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.HttpClientUtil;
import groovy.transform.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Controller
@Conditional(CheckCrawlProfiles.class)
public class CrawlController {

	private static final Logger logger = LoggerFactory.getLogger(CrawlController.class);

	@Value("${cmts.collector.naverblog.search_url}")
	private String naverblog_url;
	@Value("${cmts.collector.daumnews.search_url}")
	private String daumnews_url;
	@Value("${cmts.collector.navernews.search_url}")
	private String navernews_url;
	@Value("${cmts.admin.page_size}")
	private Integer pageSize;

	@Autowired
	private ConfTargetService confTargetService;
	@Autowired
	private ImdbService imdbService;
	@Autowired
	private NaverblogService naverblogService;
	@Autowired
	private DaumblogService daumblogService;
	@Autowired
	private NaverMovieService naverMovieService;
	@Autowired
	private DaumMovieService daumMovieService;
	@Autowired
	private CollectService collectService;
	@Autowired
	private NavernewsService navernewsService;
	@Autowired
	private DaumnewsService daumnewsService;
	@Autowired
	private Cine21MovieService cine21MovieService;

	@RequestMapping(value="/crawl/byprefix", method=RequestMethod.GET)
	@ResponseBody
	@Synchronized
	public String crawl(Model model
			, @RequestParam(value="prefix", required=true) String prefix
			, @RequestParam(value="movietitle", required=true) String movietitle
			, @RequestParam(value="movieyear", required=false, defaultValue = "") String movieyear
			, @RequestParam(value="moviedirector", required=false, defaultValue = "") String moviedirector
			, @RequestParam(value="ccubetype", required=false, defaultValue = "") String ccubetype
	) {

		logger.debug("#/crawl/byprefix ::"+prefix+" by movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);
		JsonObject result = new JsonObject();

		// 주어진 prefix에 해당하는 conf_target & conf_preset list를 조회하여
		// 실제 대상 사이트 crawl
		ConfTarget x = new ConfTarget();
		x.setTg_url(prefix);
		ConfTarget reqInfo = confTargetService.getTargetListByPrefix(x);
		String param1 = reqInfo.getParam1();
		//CcubeSeries 의 경우 검색어에서 영화 제거, 감독 제거
		if(ccubetype.contains("CcubeSeries")) {
			param1 = param1.replace("영화","");
			param1 = param1.replace("#moviedirector", "");
			param1 = param1.trim();
			reqInfo.setParam1(param1);
			logger.info("#/crawl/byprefix convert search_keyword:"+param1+" for CcubeSeries");
		}

		reqInfo.setMovietitle(movietitle);
		reqInfo.setMovieyear(movieyear);
		reqInfo.setMoviedirector(moviedirector);

		if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }

		if(!"DAUM_MOVIE".equals(prefix)) {
			if (param1.contains("#movieyear") && !"".equals(movieyear)) {
				param1 = param1.replace("#movieyear", movieyear);


			} else {
				param1 = param1.replace("#movieyear", "");
			}
		} else {
			if (param1.contains("#movieyear") && !"".equals(movieyear)) {
				param1 = param1.replace("#movieyear", "");
				String param2 = "&searchYear="+movieyear;
				reqInfo.setParam2(param2);
			} else {
				param1 = param1.replace("#movieyear", "");
			}
		}

		/* 19.03.27 다음 영화 수집 기준 변경 , 영화명 연도 */
		//if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
		//	param1 = param1.replace("#moviedirector", moviedirector);
		//} else {
			param1 = param1.replace("#moviedirector", "");
		//}

		reqInfo.setParam1(param1);

		try {
			switch (prefix) {
				case "GOOGLE_SEARCH_IMDB":
					result = imdbService.getMovie(reqInfo);
					break;
				case "NAVER_BLOG":
					result = naverblogService.getSearchBlog(reqInfo);
					break;
				case "DAUM_BLOG":
					result = daumblogService.getSearchBlog(reqInfo);
					break;
				case "NAVER_MOVIE":
					result = daumMovieService.getContents(prefix, reqInfo);
					break;
				case "DAUM_MOVIE":
					result = daumMovieService.getContents(prefix, reqInfo);
					break;
				case "NAVER_NEWS":
					result = navernewsService.getSearchNews(reqInfo);
					break;
				case "DAUM_NEWS":
					result = daumnewsService.getSearchNews(reqInfo);
					break;
				case "CINE21_MOVIE":
					result = cine21MovieService.getSearchCine21(reqInfo);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new JsonObject();
			result.add("result", null);
			result.addProperty("rt_code", "ERROR");
			result.addProperty("rt_msg", (e.getCause() != null) ? e.getCause().toString() : "unknown error!");
		}

		if (result != null && result.get("rt_code") == null) {
			result.addProperty("rt_code","OK");
			result.addProperty("rt_msg","SUCCESS");
		}

		return result.toString();
	}


	@RequestMapping(value="/crawl/test", method=RequestMethod.GET)
	@ResponseBody
	@Synchronized
	public String crawl_test(Model model
			, @RequestParam(value="prefix", required=true) String prefix
			, @RequestParam(value="movietitle", required=true) String movietitle
			, @RequestParam(value="movieyear", required=false, defaultValue = "") String movieyear
			, @RequestParam(value="moviedirector", required=false, defaultValue = "") String moviedirector
	) {

		/* for test */
		//movietitle="블라인드";
		//movieyear="2011";

		logger.debug("#/crawl/test ::"+prefix+" by movietitle:"+movietitle+"/movieyear:"+movieyear+"/moviedirector:"+moviedirector);
		JsonObject result = new JsonObject();

		// 주어진 prefix에 해당하는 conf_target & conf_preset list를 조회하여
		// 실제 대상 사이트 crawl
		ConfTarget x = new ConfTarget();
		x.setTg_url(prefix);
		ConfTarget reqInfo = confTargetService.getTargetListByPrefix(x);
		String param1 = reqInfo.getParam1();

		reqInfo.setMovietitle(movietitle);
		reqInfo.setMovieyear(movieyear);
		reqInfo.setMoviedirector(moviedirector);

		if (!"".equals(movietitle)) { param1 = param1.replace("#movietitle", movietitle); }

		if(!"DAUM_MOVIE".equals(prefix)) {
			if (param1.contains("#movieyear") && !"".equals(movieyear)) {
				param1 = param1.replace("#movieyear", movieyear);
			} else {
				param1 = param1.replace("#movieyear", "");
			}
		} else {
			if (param1.contains("#movieyear") && !"".equals(movieyear)) {
				param1 = param1.replace("#movieyear", "");
				String param2 = "&searchYear="+movieyear;
				reqInfo.setParam2(param2);
			} else {
				param1 = param1.replace("#movieyear", "");
			}
		}

		if (param1.contains("moviedirector") && !"".equals(moviedirector.trim())) {
			param1 = param1.replace("#moviedirector", moviedirector);
		} else {
			param1 = param1.replace("#moviedirector", "");
		}

		reqInfo.setParam1(param1);

		try {
			switch (prefix) {
				case "GOOGLE_SEARCH_IMDB":
					result = collectService.step03_ByHtml(reqInfo);
					break;
				case "NAVER_BLOG":
					result = collectService.step03_ByHtml(reqInfo);
					break;
				case "DAUM_BLOG":
					result = collectService.step03_ByHtml(reqInfo);
					break;
				case "NAVER_MOVIE":
					result = collectService.step03_ByHtml(reqInfo);
					break;
				case "DAUM_MOVIE":
					result = collectService.step03_ByHtml(reqInfo);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = new JsonObject();
			result.add("result", null);
			result.addProperty("rt_code", "ERROR");
			result.addProperty("rt_msg", (e.getCause() != null) ? e.getCause().toString() : "unknown error!");
		}

		if (result != null && result.get("rt_code") == null) {
			result.addProperty("rt_code","OK");
			result.addProperty("rt_msg","SUCCESS");
		}

		return result.toString();
	}
}