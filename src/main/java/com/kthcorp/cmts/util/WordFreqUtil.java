package com.kthcorp.cmts.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class WordFreqUtil {
    public static void countWordsFromFile(final Path file) {
        try {
            Arrays.stream(new String(Files.readAllBytes(file), StandardCharsets.UTF_8).split("\\W+"))
                    .collect(Collectors.groupingBy(Function.<String>identity(), TreeMap::new, counting())).entrySet()
                    .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void countWords(String req) {
        try {
        Arrays.stream(req.split(" "))
                .collect(Collectors.groupingBy(Function.<String>identity(), TreeMap::new, counting())).entrySet()
                .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> getWordCountsMap(String str) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String[] strings = null;

        if(!"".equals(str)) strings = str.split(" ");

        if(strings != null) {
            for (String s : strings) {

                if (!map.containsKey(s)) {  // first time we've seen this string
                    map.put(s, 1);
                } else {
                    int count = map.get(s);
                    map.put(s, count + 1);
                }
            }
        }
        return map;
    }



    public static Map<String, Integer> getWordCountsMapByMap(Map<String, Integer> map, String str) {
        //Map<String, Integer> map = new HashMap<String, Integer>();
        String[] strings = null;

        if(!"".equals(str)) strings = str.split(" ");

        if(strings != null) {
            for (String s : strings) {

                if (!map.containsKey(s)) {  // first time we've seen this string
                    map.put(s, 1);
                } else {
                    int count = map.get(s);
                    map.put(s, count + 1);
                }
            }
        }
        return map;
    }

    public static Map<String, Double> getWordCountsMap2(String str) {
        Map<String, Double> map = new HashMap<String, Double>();
        String[] strings = null;

        if(!"".equals(str)) strings = str.split(" ");

        if(strings != null) {
            for (String s : strings) {

                if (!map.containsKey(s)) {  // first time we've seen this string
                    map.put(s, 1.0);
                } else {
                    Double count = (Double) map.get(s);
                    map.put(s, count + 1.0);
                }
            }
        }
        return map;
    }
}
