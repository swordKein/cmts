package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.MetaKeywordMappingMapper;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.ItemsContent;
import com.kthcorp.cmts.model.MetaKeywordMapping;
import com.kthcorp.cmts.model.SchedTrigger;
import com.kthcorp.cmts.util.MapUtil;
import com.kthcorp.cmts.util.StringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AdminServiceTest {
	@Autowired
	private AdminService adminService;

	@Test
	public void test_uptConfTarget() throws Exception{
		ConfTarget req = new ConfTarget();
		req.setTg_id(14);
		req.setTitle("test444");
		int result = adminService.uptConfTarget(req);
		System.out.println("#Result:"+result);
	}

}
