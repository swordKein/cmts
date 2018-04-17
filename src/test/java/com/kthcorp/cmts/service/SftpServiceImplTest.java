package com.kthcorp.cmts.service;

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
public class SftpServiceImplTest {

	@Autowired
	private SftpService sftpService;

	@Test
	public void testPollingCcubeSftp() throws Exception{
		sftpService.pollingCcubeSftp();
	}

	@Test
	public void test_processDownloadMultipleXmlFileToDB() throws Exception {
		String fileName = "D:\\upload\\Content_2017120100.xml";
		int rt = sftpService.processDownloadMultipleXmlFileToDB(fileName);


		String fileName2 = "D:\\upload\\Content_2018010100.xml";
		int rt2 = sftpService.processDownloadMultipleXmlFileToDB(fileName2);

	}

	@Test
	public void test_processDownloadMultipleXmlFileToDB2() throws Exception {
		String fileName = "D:\\upload\\Series_2017120100.xml";
		int rt = sftpService.processDownloadMultipleXmlFileToDB(fileName);


		String fileName2 = "D:\\upload\\Series_2018010100.xml";
		int rt2 = sftpService.processDownloadMultipleXmlFileToDB(fileName2);

	}
}
