package com.kthcorp.cmts.util;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public static void printMapAll(Map<String, Long> reqMap) {
        int lineCnt = 0;
        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            System.out.println((me.getKey()+","+me.getValue()));
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

    public static List<String> getSortedDescStringArrayForDouble(Map<String, Double> reqMap, int limit) {
        Map<String, Double> metaValueMap = new HashMap<String, Double>();
        List<String> resultArr = new ArrayList();

        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            metaValueMap.put(me.getKey().toString(), (Double) me.getValue());
        }

        ValueComparator vc = new ValueComparator(metaValueMap);
        Map<String, Double> sortedMap = new TreeMap<String, Double>(vc);

        sortedMap.putAll(metaValueMap);

        Set entrySet2 = sortedMap.entrySet();
        Iterator it2 = entrySet2.iterator();
        int lineCnt = 0;
        while(it2.hasNext() && lineCnt < limit) {
            Map.Entry me2 = (Map.Entry) it2.next();
            resultArr.add(me2.getKey().toString());
            lineCnt++;
        }

        return resultArr;
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

    public static Map<String, Double> getParamDoubleMapFromJsonArrayByTag(JsonArray origArr, String labelTag, String valueTag) {
        Map<String, Double> resultMap = null;
        if (origArr != null && !"".equals(labelTag) && !"".equals(valueTag)) {
            resultMap = new HashMap();

            for(JsonElement je : origArr) {
                JsonObject jo = (JsonObject) je;
                if (jo.get(labelTag) != null && jo.get(valueTag) != null) {
                    String key = jo.get(labelTag).getAsString();
                    Double value = jo.get(valueTag).getAsDouble();
                    resultMap.put(key, value);
                }
            }
        }
        return resultMap;
    }


    public static Map<String, Double> getParamDoubleMapMutexedRatio(Map<String, Double> origMap, Double ratio) {
        Map<String, Double> resultMap = null;

        if (ratio > 0.0) {
            if (origMap != null) {
                resultMap = new HashMap();

                Set entrySet = origMap.entrySet();
                Iterator it = entrySet.iterator();

                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    //System.out.println("# "+lineCnt++ +" st map data:"+(me.getKey()+":"+me.getValue()));
                    String key = (String) me.getKey();
                    Double value = (Double) me.getValue();
                    value = value * ratio;

                    //System.out.println("# " +" st map data:"+(me.getKey()+":"+me.getValue()+":"+value));

                    resultMap.put(key, value);
                }
            }
        } else {
            resultMap = origMap;
        }

        return resultMap;
    }

    public static Map<String, Double> getAppendedMapAndParamDouble(Map<String, Double> origMap, Map<String, Double> reqMap) {
        //int lineCnt = 0;
        if (origMap == null) origMap = new HashMap<String, Double>();

        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            //System.out.println("# "+lineCnt++ +" st map data:"+(me.getKey()+":"+me.getValue()));
            String key = (String) me.getKey();
            Double value = (Double) me.getValue();

            Double valueOrig = 0.0;
            if(origMap.get(key) != null) {
                valueOrig = (Double) origMap.get(key);
                value = value + valueOrig;
            }

            origMap.put(key, value);
        }
        return origMap;
    }


    public static List<String> getListFromMapByTag(List<Map<String, Object>> reqMapArr, String tag, int limit) {
        List<String> result = new ArrayList();

        int lineCnt = 0;
        for(Map<String, Object> reqMap : reqMapArr) {
            if (reqMap != null && reqMap.get(tag) != null) {
                String value = reqMap.get(tag).toString();
                if (!"".equals(value) && lineCnt < limit) {
                    result.add(value);
                }

            }
            lineCnt++;
        }

        return result;
    }


    public static List<String> getListFromMapByTag2(List<Map<String, Object>> reqMapArr, List<String> days, int limit) {
        List<String> result = new ArrayList();

        int lineCnt = 0;
        for(Map<String, Object> reqMap : reqMapArr) {
            if (reqMap != null && reqMap.get("rank") != null) {
                String value = reqMap.get("rank").toString();
                if (lineCnt < limit ) {
                    System.out.println("#ELOG.compare value & date1:: rank:"+value+"/date1:"+reqMap.get("date1").toString()+"/before:"+days.get(lineCnt));

                    if (!"".equals(value) && reqMap.get("date1").toString().equals(days.get(lineCnt))) {
                        result.add(value);
                    } else {
                        result.add("0");
                    }
                }
            }
            lineCnt++;
        }

        return result;
    }

    public static Set<String> getSetFromStringArray(String[] reqArr) {
        Set<String> result = null;
        if (reqArr != null && reqArr.length > 0) {
            result = new HashSet();
            for (String s1 : reqArr) {
                for (String s2 : reqArr) {
                    String ds = s1 + "___" + s2;
                    result.add(ds);
                }
            }
        }
        return result;
    }

    public static Set<String> getNoDupSetFromStringArray(String[] reqArr) {
        Set<String> result = null;
        if (reqArr != null && reqArr.length > 0) {
            result = new HashSet();
            for (String s1 : reqArr) {
                for (String s2 : reqArr) {
                    List<String> newDsArr = new ArrayList();
                    newDsArr.add(s1);
                    newDsArr.add(s2);
                    String ds = StringUtil.getSortedStringStrsAddSeperator(newDsArr, "___");
                    result.add(ds);
                }
            }
        }
        return result;
    }

    public static Set<String> getNoDupSetFromStringArrayOne(String[] reqArr) {
        Set<String> result = null;
        if (reqArr != null && reqArr.length > 0) {
            result = new HashSet();
            List<String> newDsArr = new ArrayList();
            newDsArr.add(reqArr[0]);
            newDsArr.add(reqArr[1]);
            String ds = StringUtil.getSortedStringStrsAddSeperator(newDsArr, "___");
            result.add(ds);
        }
        return result;
    }

    public static Set<String> getNoDupSetFromStringArrayAddTag(List<String> reqArr, String origin) {
        Set<String> result = null;
        if (reqArr != null && reqArr.size() > 0) {
            result = new HashSet();
            for(String gen : reqArr) {
                List<String> newDsArr = new ArrayList();
                newDsArr.add(gen);
                newDsArr.add(origin);
                String ds = StringUtil.getSortedStringStrsAddSeperator(newDsArr, "___");
                result.add(ds);
            }
        }
        return result;
    }


    public static Map convertObjectToMap(Object obj){
        Map map = new HashMap();
        Field[] fields = obj.getClass().getDeclaredFields();
        for(int i=0; i <fields.length; i++){
            fields[i].setAccessible(true);
            try{
                map.put(fields[i].getName(), fields[i].get(obj));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }


    public static Object convertMapToObject(Map<String,Object> map,Object obj){
        String keyAttribute = null;
        String setMethodString = "set";
        String methodString = null;
        Iterator itr = map.keySet().iterator();

        while(itr.hasNext()){
            keyAttribute = (String) itr.next();
            methodString = setMethodString+keyAttribute.substring(0,1).toUpperCase()+keyAttribute.substring(1);
            Method[] methods = obj.getClass().getDeclaredMethods();
            for(int i=0;i<methods.length;i++){
                if(methodString.equals(methods[i].getName())){
                    try{
                        methods[i].invoke(obj, map.get(keyAttribute));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return obj;
    }

    public static Map<String, Object> removeMapByMap(Map<String, Object> reqMap, Map<String, Object> keyMap) {
        try {
            if (reqMap != null && keyMap != null) {
                Set entrySet = keyMap.entrySet();
                Iterator it = entrySet.iterator();

                while(it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    String key = (String) me.getKey();
                    reqMap.remove(key);
                }
            }
        } catch (Exception e) {

        }

        return reqMap;
    }
}