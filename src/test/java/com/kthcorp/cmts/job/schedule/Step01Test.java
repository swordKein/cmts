package com.kthcorp.cmts.job.schedule;

import com.kthcorp.cmts.service.CollectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class Step01Test {
	@Autowired
	private CollectService collectService;

	@Test
	public void test_Step01() throws Exception {
		//String result = ElasticsearchUtil.getSimpleWords(es_url + es_idx, "데이터는하늘에도있고도없지");
		//System.out.println("#result:" + result);

	}
}
