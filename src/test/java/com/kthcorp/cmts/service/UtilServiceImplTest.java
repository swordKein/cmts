package com.kthcorp.cmts.service;

import com.kthcorp.cmts.SpringBootWebApplication;
import com.kthcorp.cmts.util.FileUtils;
import com.kthcorp.cmts.util.pool.concurrent.mgr.GenericTaskThreadPoolExecutor;
import com.kthcorp.cmts.util.pool.concurrent.monitor.TaskPoolMonitorThread;
import com.kthcorp.cmts.util.pool.concurrent.task.JobTask;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.GenericTaskArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.kthcorp.cmts.SpringBootWebApplication.threadPool;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
		//(classes = UtilServiceImpl.class)
@WebAppConfiguration
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestDatabase
//@EnableAutoConfiguration
//@ComponentScan(basePackageClasses=TestServiceImpl.class)
//@EntityScan(basePackageClasses=TestDao.class)
//@MybatisTest
//@ContextConfiguration(locations = {"file:src/main/resources/application.yml","file:src/main/resources/threadpool.xml_"})
//@ContextConfiguration(locations = {"file:src/main/resources/application.yml"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class UtilServiceImplTest {

	//@MockBean
	@Autowired
	private UtilService utilService;
	//@Autowired
	//private StepService stepService;

	@Test
	public void testUtilServiceTest() throws Exception{
		//String result = utilService.getStr();
		//System.out.println("#Result:"+result);
	}

	@Autowired
	private CollectServiceImpl collectService;

	@Test
	public void test_xxx() throws Exception {

		//System.out.println("#Context:"+ context.getId().toString());
		//ApplicationContext context = new AnnotationConfigApplicationContext(SpringBootWebApplication.class);
		//System.out.println("#Context:"+ c.getId().toString());
		//MainService bean = (MainService) context.getBean(String.valueOf(mainService));
		//CollectService bean = context.getBean(CollectService.class);

		//bean.ollehTvMetaCollectScheduleCheck();

		//ThreadPoolTaskExecutor t = context.getBean(ThreadPoolTaskExecutor.class);
		//t.shutdown();
/**/
	}


	@Test
	public void test_Generic() {

		GenericTaskArgument taskargument = new GenericTaskArgument();
		taskargument.setTargetObject(collectService);
		//taskargument.setMethodName("runMainService");
		taskargument.setMethodName("step01");

		JobTask JobTask = new JobTask(taskargument,Thread.currentThread().getName()+"_JobTask");

		TaskPoolMonitorThread threadMon = new TaskPoolMonitorThread(threadPool);
		Thread monitor = new Thread(threadMon);
		monitor.start();

		try {

			threadPool.execute(JobTask);
			System.out.println("#1ruuning process:"+threadPool.isAvailAbleExecutionResource());
			Thread.sleep(300);

			monitor = new Thread(threadMon);
			monitor.start();

			threadPool.execute(JobTask);

			System.out.println("#2ruuning process:"+threadPool.isAvailAbleExecutionResource());

			Thread.sleep(3000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			monitor = null;
		}
	}




	@Autowired
	private TestServiceImpl testService=null;

	@Value("${spring.static.resource.location}")
	private String UPLOAD_DIR;

	@Test
	public void test_run() throws Exception {
		MultipartFile file1 = FileUtils.convertFileToMultipart("E:\\upload\\", "test_csv.txt");
		//List<MultipartFile> files1 = new ArrayList<MultipartFile>();
		//files1.add(file1);
		//FileUtils.saveUploadedFiles(files1, UPLOAD_DIR);
		//List<Object> paramObj = new ArrayList<Object>();
		//paramObj.add(file1);
		int rt = utilService.runJobTask(testService, "processFileToNlpsResult", file1);
		Thread.sleep(10000);
	}


	@Test
	public void test_Generic2() {

		GenericTaskArgument taskargument = new GenericTaskArgument();
		taskargument.setTargetObject(testService);
		//taskargument.setMethodName("runMainService");
		taskargument.setMethodName("processFileToNlpsResult");
		//taskargument.setMethodName("prcFileUpload");
		//taskargument.setMethodName("runJobTask");

		// 파라미터 타입 세팅
		Class<?>[] parmeterTypes={MultipartFile.class};
		taskargument.setParameterTypes(parmeterTypes);
		// 파라미터 세팅
		MultipartFile file1 = FileUtils.convertFileToMultipart("E:\\upload\\", "test_csv.txt");
		Object[] parameters={file1};
		taskargument.setParameters(parameters);

		JobTask JobTask = new JobTask(taskargument,Thread.currentThread().getName()+"_JobTask");

		TaskPoolMonitorThread threadMon = new TaskPoolMonitorThread(threadPool);
		Thread monitor = new Thread(threadMon);
		monitor.start();

		try {

			threadPool.execute(JobTask);
			System.out.println("#1ruuning process:"+threadPool.isAvailAbleExecutionResource());
			Thread.sleep(300);
//
//			monitor = new Thread(threadMon);
//			monitor.start();
//
//			threadPool.execute(JobTask);
//
//			System.out.println("#2ruuning process:"+threadPool.isAvailAbleExecutionResource());
//
			Thread.sleep(300000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			monitor = null;
		}
	}
}
