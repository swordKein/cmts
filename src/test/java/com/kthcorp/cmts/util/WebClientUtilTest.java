package com.kthcorp.cmts.util;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WebClientUtilTest {

    @Test
    public void test_getWebPage() throws Exception {
        String reqUrl = "https://search.daum.net/search?q=%EC%96%B4%EB%B2%A4%EC%A0%80%EC%8A%A4&w=tot&DA=S43";
        String result = WebClientUtil.getWebPage(reqUrl);
    }
}