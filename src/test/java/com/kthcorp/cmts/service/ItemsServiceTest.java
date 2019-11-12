package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.InItems;
import com.kthcorp.cmts.model.SchedTrigger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ItemsServiceTest {
	@Autowired
	private ItemsService itemsService;

	@Test
	public void test_copyInItemsToItems() throws Exception{
		List<InItems> reqItems = itemsService.get50ActiveInItems();
		System.out.println("#Request:"+reqItems.toString());

		int rt = 0;
		for (InItems ins : reqItems) {
			rt = 0;
			rt = itemsService.copyInItemsToItems(ins);
			System.out.println("#Copy In-Item to Items Result:"+rt);
		}

	}

	@Test
	public void test_processCcubeContents() throws Exception {
		//for(int i=0; i<200; i++) {
			int rt = itemsService.processCcubeContents();
			System.out.println("#result:" + rt);
		//}
	}

	@Test
	public void test_processCcubeSeries() throws Exception {
		//for(int i=0; i<200; i++) {
			int rt = itemsService.processCcubeSeries();
			System.out.println("#result:" + rt);
		//}
	}

	@Test
	public void test_getItemsInfoByIdx() throws Exception {
		System.out.println("#RES:"+itemsService.getItemsInfoByIdx(2855));
	}

	@Test
	public void test_copyCcubeContentToItems() throws Exception {
		CcubeContent req = new CcubeContent();
		req.setCountry_of_origin("KOR");
		req.setContent_id("111111222");
		req.setPurity_title("나랏말싸미");
		req.setYear("2019");
		req.setDirector("조철현");
		System.out.println("#RES:"+itemsService.copyCcubeContentToItems(req));
	}

	@Test
	public void test_copyCcubeContentToItems2() throws Exception {
		CcubeContent req = new CcubeContent();
		req.setCountry_of_origin("ENG");
		req.setContent_id("222222222");
		req.setPurity_title("어바웃타임");
		req.setYear("2013");
		req.setDirector("리차드 커티스");
		System.out.println("#RES:"+itemsService.copyCcubeContentToItems(req));
	}

	@Test
	public void test_copyCcubeContentToItems3() throws Exception {
		CcubeContent req = new CcubeContent();
		req.setCountry_of_origin("ENG");
		req.setContent_id("333333222");
		req.setPurity_title("분노의 질주:홉스&쇼");
		req.setYear("2019");
		req.setDirector("데이빗 레이치");
		System.out.println("#RES:"+itemsService.copyCcubeContentToItems(req));
	}

	@Test
	public void test_copyCcubeContentToItems4() throws Exception {
		CcubeContent req = new CcubeContent();
		req.setCountry_of_origin("KOR");
		req.setContent_id("444444222");
		req.setPurity_title("타짜: 원 아이드 잭");
		req.setYear("2019");
		req.setDirector("권오강");
		System.out.println("#RES:"+itemsService.copyCcubeContentToItems(req));
	}
}
