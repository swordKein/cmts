package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.InItems;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CcubeServiceTest {
	@Autowired
	private CcubeService ccubeService;

	@Test
	public void test_getCcubeDatasByItemIdx() throws Exception{
		JsonObject result = ccubeService.getCcubeDatasByItemIdx(10410);
		System.out.println("#Result:"+result.toString());

	}
}
