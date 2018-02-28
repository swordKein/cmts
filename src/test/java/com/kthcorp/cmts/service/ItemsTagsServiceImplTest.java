package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ItemsTagsServiceImplTest {

	//@MockBean
	@Autowired
	private ItemsTagsService itemsTagsService;

	@Test
	public void test_getItemsTagsMetasAll() throws Exception{
		JsonObject result = itemsTagsService.getItemsMetasByItemIdxForInsert(1);
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

		String req = "[{\"meta\":\"압도적인\",\"type\":\"emotion\",\"target_meta\":\"압도적인\",\"action\":\"del\"}]";

		int  result = itemsTagsService.changeMetasArraysByTypeFromInputItems(1, req, "");
		System.out.println("#Result:"+result);
	}
}
