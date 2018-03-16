package com.kthcorp.cmts.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CommonUtilTest {

    @Test
    public void test_1() throws Exception {
        Map<String, Object> resultMap = CommonUtil.getPagination(221, 20, 11, 5);
        List<String> listActive = (List<String>) resultMap.get("listActive");
        List<Integer> listPage = (List<Integer>) resultMap.get("listPage");
        System.out.println("#RESULT:");
        for (int i=0; i<listPage.size(); i++) {
            System.out.println("## "+i+" 'th data:"+listPage.get(i)+" / "+listActive.get(i));
        }
    }
}