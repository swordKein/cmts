package com.kthcorp.cmts.util;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class MapUtil {

    public static void printMapBySize(Map<String, Integer> reqMap, int size) {
        int lineCnt = 0;
        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext() && lineCnt < size) {
            Map.Entry me = (Map.Entry) it.next();
            System.out.println("# "+lineCnt++ +" st map data:"+(me.getKey()+":"+me.getValue()));
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return -(o1.getValue()).compareTo( o2.getValue() );
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Map<String, Double> getSortedDescMapForDouble(Map<String, Double> reqMap) {
        Map<String, Double> metaValueMap = new HashMap<String, Double>();

        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            metaValueMap.put(me.getKey().toString(), (Double) me.getValue());
        }

        ValueComparator vc = new ValueComparator(metaValueMap);
        Map<String, Double> sortedMap = new TreeMap<String, Double>(vc);

        sortedMap.putAll(metaValueMap);

        return sortedMap;
    }


    public static Map<String, Object> getSortedascMapForStringKey(Map<String, Object> reqMap) {
        Map<String, Object> metaValueMap = new HashMap<String, Object>();
        TreeMap<String,Object> tm = new TreeMap<String,Object>(reqMap);

        Iterator<String> iteratorKey = tm.keySet( ).iterator( );   //키값 오름차순 정렬(기본)
        //Iterator<String> iteratorKey = tm.descendingKeySet().iterator(); //키값 내림차순 정렬

        return tm;
    }

    public static List<String> getConvertMapToStringArray(Map<String, Object> reqMap) {
        List<String> resultArr = new ArrayList();


        return resultArr;
    }


    public static JsonArray getListNotMapKeywords(Map<String, Double> reqMap) {
        JsonArray resultArr = new JsonArray();

        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            resultArr.add(me.getKey().toString());
        }

        return resultArr;
    }


    public static List<String> getKeysListFromMapByLimit(Map<String, Object> reqMap, int limit) {
        List<String> result = new ArrayList();

        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        int cnt = 0;
        while(it.hasNext() && cnt < limit) {
            Map.Entry me = (Map.Entry) it.next();

            if(me.getKey() != null) {
                String data = me.getKey().toString().trim();
                if(!"".equals(data) && !data.startsWith("_")) {
                    result.add(data);
                    cnt++;
                }
            }
        }

        return result;
    }


    public static Map<String, Double> getCuttedMapFromMapByLimit(Map<String, Double> reqMap, int maxCount) {
        Map<String, Double> result = new HashMap<String, Double>();
        try {
            int i=0;

            Set entrySet = reqMap.entrySet();
            Iterator it = entrySet.iterator();

            while(it.hasNext() && i < maxCount){
                Map.Entry me = (Map.Entry)it.next();

                result.put(me.getKey().toString(), (Double) me.getValue());
                //System.out.println("#getCuttedMapFromMapByLimit added "+i+" 'th key:"+me.getKey().toString());
                i++;
            }
        } catch (Exception e) { e.printStackTrace(); }

        return result;
    }


    public static JsonArray getCuttedMapPointsFromMapByLimit(Map<String, Double> reqMap, int maxCount) {
        Map<String, Double> result1 = new HashMap<String, Double>();
        JsonArray result = new JsonArray();

        try {
            int i=0;
            double sumFreq = 0.0;

            Set entrySet = reqMap.entrySet();
            Iterator it = entrySet.iterator();

            // 다운포인트 계산 ( freq / sumFreq * 10 )
            // 우선 freq 합산을 구한다.
            while(it.hasNext() && i < maxCount){
                Map.Entry me = (Map.Entry)it.next();
                //result1.put(me.getKey().toString(), (double) me.getValue());
                //System.out.println("#getCuttedMapFromMapByLimit added "+i+" 'th key:"+me.getKey().toString());
                i++;
                sumFreq += (double) me.getValue();
            }

            System.out.println("#sumFreq:"+sumFreq);

            i = 0;
            Set entrySet2 = reqMap.entrySet();
            Iterator it2 = entrySet2.iterator();
            while(it2.hasNext() && i < maxCount){
                Map.Entry me2 = (Map.Entry)it2.next();
                double curFreq = 0.0;
                curFreq = (me2.getValue() != null) ? (double) me2.getValue() : 0.0;
                double point = 0.0;
                point = curFreq / sumFreq * 10.0;

                JsonObject newItem = new JsonObject();
                newItem.addProperty("word", me2.getKey().toString());
                newItem.addProperty("ratio", point);
                newItem.addProperty("freq", curFreq);
                result.add(newItem);
                //System.out.println("#keywordPointArraymap.added"+newItem.toString());

                i++;
            }
        } catch (Exception e) { e.printStackTrace(); }

        return result;
    }
}