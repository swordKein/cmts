package com.kthcorp.cmts.service;

import com.kthcorp.cmts.util.DateUtils;
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
	public void testManualCcubeSftp() throws Exception{
		sftpService.manualCcubeSftp();
	}

	@Test
	public void test_date() throws Exception {
		System.out.println("#res::"+DateUtils.getTimeFromStr(DateUtils.getDateStr("2018062813")));
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

	@Test
	public void test_processDownloadMultipleXmlFileToDB_0426() throws Exception {
		String fileName = "C:\\Users\\wodus77\\Documents\\KTH_META\\03.구현\\CCUBE_입수_데이터\\CCUBE_DOWN_0426\\Content_2016051423.xml";
		int rt = sftpService.processDownloadMultipleXmlFileToDB(fileName);

	}

	@Test
	public void test_processDownloadMultipleXmlFileToDB2_0426() throws Exception {
		String fileName = "C:\\Users\\wodus77\\Documents\\KTH_META\\03.구현\\CCUBE_입수_데이터\\CCUBE_DOWN_0426\\Series_2016053100.xml";
		int rt = sftpService.processDownloadMultipleXmlFileToDB(fileName);

	}

	@Test
	public void test_uploadToPrismReq() {
		int rt = sftpService.uploadToPrismReq();
	}
	@Test
	public void test_pollingPrismSftp() {
		int rt = sftpService.pollingPrismSftp();
	}

}
