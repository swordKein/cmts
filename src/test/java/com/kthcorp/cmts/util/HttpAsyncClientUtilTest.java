package com.kthcorp.cmts.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class HttpAsyncClientUtilTest {

    @Test
    public void testHttpAsyncClientUtilTest() throws Exception {

//    	HttpAsyncClientUtil.reqGet("https://section.blog.naver.com/Search/Post.nhn?keyword=%EC%88%98%EC%A7%80");
        //HttpAsyncClientUtil.reqGet("https://www.google.com");
        HttpAsyncClientUtil.reqGet("https://www.google.co.kr/search?q=movie%20blind&sourceid=chrome&ie=UTF-8", "", null);

    }
}