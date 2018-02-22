package com.kthcorp.cmts.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = TestServiceImpl.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class})
//@MapperScan(value={"com.kthcorp.cmts"})
public class TestDaoTest {

	@Autowired
	private TestDao testDao;
	//@Autowired
	//private TestServiceImpl testService;

	@Test
	public void testMapperTest() throws Exception{
		String result = testDao.getCurrentDateTime2();
		System.out.println("#Result:"+result);
	}
}
