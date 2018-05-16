package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
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

}
