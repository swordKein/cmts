package com.kthcorp.cmts.controller;

import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.service.ConfTargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@Conditional(CheckAdminProfiles.class)
public class ConfTargetController {

	private static final Logger logger = LoggerFactory.getLogger(ConfTargetController.class);

	@Autowired
	private ConfTargetService confTargetService;

	@RequestMapping("/targetList")
	public ModelAndView getTargetList(Map<String, Object> model) {

		logger.debug("#LOCATION {}", "targetList");

		//String r1 = testService.getTest();
		List<ConfTarget> targetList = confTargetService.getTargetListActiveFirst10();
		logger.debug("#Search Result:"+targetList.toString());
		//System.out.println("#Search Result:"+targetList.toString());

		ModelAndView mv = new ModelAndView("targetList");
		mv.addObject("targetList", targetList);
		return mv;
	}

}