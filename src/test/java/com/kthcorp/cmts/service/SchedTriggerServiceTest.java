package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.SchedTriggerMapper;
import com.kthcorp.cmts.model.SchedTrigger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
//@Transactional
public class SchedTriggerServiceTest {
	@Autowired
	private SchedTriggerService schedTriggerService;

	@Autowired
	private SchedTriggerMapper schedTriggerMapper;

	@Test
	public void test_uptSchedTriggerComplete() throws Exception{
		SchedTrigger req = new SchedTrigger();
		req.setSc_id(1);
		req.setStat("Y");
		int result = schedTriggerService.uptSchedTriggerComplete(req);
		System.out.println("#Result:"+result);


		List<SchedTrigger> result1 = schedTriggerMapper.getSchedTriggerListById(1);
		System.out.println("#Result:"+result1);
	}


	@Test
	public void test_uptSchedTriggerOnlyStat() throws Exception{
		SchedTrigger req = new SchedTrigger();
		req.setSc_id(1);
		req.setStat("Y");
		//req.setProgs(0);
		int result = schedTriggerService.uptSchedTriggerOnlyStat(req);
		System.out.println("#Result:"+result);


		List<SchedTrigger> result1 = schedTriggerMapper.getSchedTriggerListById(1);
		System.out.println("#Result:"+result1);
	}
}
