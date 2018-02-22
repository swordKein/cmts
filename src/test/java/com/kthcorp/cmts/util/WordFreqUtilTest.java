package com.kthcorp.cmts.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllBytes;
import static java.util.stream.Collectors.counting;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WordFreqUtilTest {

    @Test
    public void test1() {
        WordFreqUtil.countWords("아버지 아버지 아버지 방 방 들어가 아버지 신다 신다 신다 신다");
        //System.out.println("#Result:"+result.toString());
    }

    @Test
    public void test2() {
        //for(int i=0; i<10000;i++) {
            Map<String, Integer> result = WordFreqUtil.getWordCountsMap("아버지 아버지 아버지 방 방 들어가 아버지 신다 신다 신다 신다");
            Map<String, Integer> result2 = MapUtil.sortByValue(result);
            //Map<String, Integer> result3 = new TreeMap(Collections.reverseOrder());
            //result3.putAll(result2);
            System.out.println("#Result:" + result2.toString());
        //}
    }

    @Test
    public void test3() {
        //for(int i=0; i<10000;i++) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result = WordFreqUtil.getWordCountsMapByMap(result, "아버지 아버지 아버지 방 방 들어가 아버지 신다 신다 신다 신다");
        result = WordFreqUtil.getWordCountsMapByMap(result, "들어가 들어가");
        Map<String, Integer> result2 = MapUtil.sortByValue(result);
        //Map<String, Integer> result3 = new TreeMap(Collections.reverseOrder());
        //result3.putAll(result2);
        System.out.println("#Result:" + result2.toString());
        //}
    }
}