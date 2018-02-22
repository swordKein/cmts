package com.kthcorp.cmts.controller;

import com.kthcorp.cmts.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class MovieController {
	private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

	@Autowired
	private TestService testService;

	@RequestMapping("/mv")
	public String welcome(Map<String, Object> model) {

		logger.debug("Movie {}", "testing");

		//String r1 = testService.getTest();
		String r2 = testService.getTest();
		System.out.println("#DB result:"+r2);
		model.put("message", "Upload Test");
		return "movie";
	}

}