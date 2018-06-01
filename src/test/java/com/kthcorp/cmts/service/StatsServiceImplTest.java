package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.Stats;
import com.kthcorp.cmts.util.FileUtils;
import com.kthcorp.cmts.util.pool.concurrent.monitor.TaskPoolMonitorThread;
import com.kthcorp.cmts.util.pool.concurrent.task.JobTask;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.GenericTaskArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import static com.kthcorp.cmts.SpringBootWebApplication.threadPool;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class StatsServiceImplTest {

	//@MockBean
	@Autowired
	private StatsService statsService;

	@Test
	public void test_getStatsForDash() throws Exception{
		JsonObject result = statsService.getStatsForDash();
		System.out.println("#Result:"+result.toString());
	}

	@Test
	public void test_getCountInsertedDaily() throws Exception {
		Stats req = new Stats();
		req.setSdate("2018-02-26");
		req.setEdate("2018-02-28");

		System.out.println("#RESULT:"+statsService.getCountInsertedDaily(req));
	}

	@Test
	public void test_getCountItemsHistByType() throws Exception {
		Stats req = new Stats();
		req.setSdate("2018-02-26");
		req.setEdate("2018-02-28");

		System.out.println("#RESULT:"+statsService.getCountItemsHistByType(req));
	}

	@Test
	public void test_getStatsList() throws Exception {
		System.out.println("#RESULT:"+statsService.getStatsList(20, 1, "2018-05-17", "2018-05-17", "RT"));
	}
}
