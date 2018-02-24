package com.kthcorp.cmts.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WordCountUtilExamTest {

    /*
    @Test
    public void test_getES() throws Exception {
            List<String> list = Arrays.asList(
                    "hello", "bye", "ciao", "bye", "ciao");
            Map<String, Integer> counts = list.parallelStream().
                    collect(Collectors.toConcurrentMap(
                            w -> w, w -> 1, Integer::sum));
            System.out.println(counts);

        //String result = RestUtil.getES("{\"data\":\"아버지가방에들어가신다\"}");
        String result = RestUtil.getES("{\"analyzer\":\"korean\",\"text\":\"아버지가방에들어가신다\"}");
        System.out.println("#RESULT:"+result);
    }
    */
}