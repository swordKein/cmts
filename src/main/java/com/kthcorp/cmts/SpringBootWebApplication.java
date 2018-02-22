package com.kthcorp.cmts;

import com.kthcorp.cmts.job.config.JobScheduleProperties;
import com.kthcorp.cmts.service.AnalyzeService;
import com.kthcorp.cmts.service.CollectService;
import com.kthcorp.cmts.service.RefineService;
import com.kthcorp.cmts.util.DateUtils;
import com.kthcorp.cmts.util.pool.concurrent.mgr.GenericTaskThreadPoolExecutor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.sql.Date;
import javax.servlet.Filter;

@SpringBootApplication
//@EnableScheduling
@Configuration
@EnableAutoConfiguration
@ComponentScan
@MapperScan("com.kthcorp.cmts")
@EnableConfigurationProperties(value = {JobScheduleProperties.class})
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringBootWebApplication {
	private int maxUploadSizeInMb = 10 * 1024 * 1024; // 10 MB

	@Value("${spring.static.resource.location}")
	private String UPLOAD_DIR;

	//@Autowired
	//private ApplicationContext appContext;
	@Autowired private CollectService collectService;
	@Autowired private RefineService refineService;
	@Autowired private AnalyzeService analyzeService;

	// ThreadPool 설정 활성화 코드
	public static GenericTaskThreadPoolExecutor threadPool;


	@Bean
	public GenericTaskThreadPoolExecutor genericTaskThreadPoolExecutor() {
		threadPool = new GenericTaskThreadPoolExecutor(core_pool_size, max_pool_size, keep_alive_seconds);
		return threadPool;
	}

	@Bean
	public GenericTaskThreadPoolExecutor getGenericTaskThreadPoolExecutor() {
		return this.threadPool;
	}

	@Value("${threadpool.core_pool_size}")
	public int core_pool_size;
	@Value("${threadpool.max_pool_size}")
	public int max_pool_size;
	@Value("${threadpool.keep_alive_seconds}")
	public long keep_alive_seconds;

	/*
	@Scheduled(fixedRate = 2000)
	public void Step01_03_CollectServiceJob() {
		int rt = 0;
		try {
			rt = collectService.ollehTvMetaCollectScheduleCheck();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("#MLOG schedule:CollectService" + DateUtils.getLocalDateTime() + "/rt:"+rt);
	}
	*/


	/*	*/
	/*
	implements CommandLineRunner
	@Override
	public void run(String... strings) throws Exception {
		String[] beans = appContext.getBeanDefinitionNames();
		Arrays.sort(beans);
		for (String bean : beans) {
			System.out.println(bean);
		}

	}
	*/

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootWebApplication.class, args);
	}

	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
		return new StringHttpMessageConverter(Charset.forName("UTF-8"));
	}

	/*
	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}
	*/

	/*
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**")
				.addResourceLocations(
						"file:///C:/Sambhav/Installations/workspace/demo-staticresource/ext-resources/")
				.setCachePeriod(0);
	}
	*/
	@Bean
	WebMvcConfigurer configurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addResourceHandlers (ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/upload/**")
						.addResourceLocations("file:///"+UPLOAD_DIR)
						.setCachePeriod(0);
			}
		};
	}
}