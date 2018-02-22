package com.kthcorp.cmts.util;

import org.bitbucket.eunjeon.seunjeon.*;
import scala.Array;
import scala.collection.mutable.WrappedArray;

import java.util.*;

public class SeunjeonUtil {

    // 형태소 분석 후 단어 취득
    public static ArrayList<ArrayList<String>> getSimpleWords0(String req, List<String> removeType) throws Exception {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        try {
            //for (Eojeol eojeol : Analyzer.parseJava(req)) {
                for (LNode node : Analyzer.parseJava(req)) {
                    //System.out.println(node+"/"+node.morpheme().poses());
                    WrappedArray<String> nt = node.morpheme().copy$default$5();
                    //ArrayList<String> nt1 = nt;
                    List<String> nt1 = ScalaUtil.convert(nt);
                    //ArrayList<String> nt2 = new ArrayList<String>(nt1);
                    //System.out.println("#WORDx: nt1:" + nt1 + "/nt2:" + nt2);

                    if (nt1 != null && nt1.size() > 4) {
                        //System.out.println("#WORD::" + nt2 + "/" + nt2.get(0) + " : " + nt2.get(3) + " : " + node.morpheme().cost() + " : " + node.morpheme().copy$default$7());
                        System.out.println("#WORD::"+nt1.toString());

                        ArrayList<String> newItem = new ArrayList();
                        for (String s : nt1) {
                            newItem.add(s);
                        }
                        result.add(newItem);
                    }
                    //System.out.println(node);
                }
          //  }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 형태소 분석 후 단어 취득
    public static void getSimpleWords(String req, List<String> removeType) throws Exception {
        for (Eojeol eojeol : Analyzer.parseEojeolJava(req)) {
            for (LNode node : eojeol.nodesJava()) {
                //System.out.println(node+"/"+node.morpheme().poses());
                WrappedArray<String> nt = node.morpheme().copy$default$5();
                //Iterable<String> nt1 = ScalaUtil.convert(nt);
                System.out.println("#node::"+node.toString());
                System.out.println("#WORD::"+ nt + " : " + node.morpheme().cost() + " : " + node.morpheme().copy$default$7());

                //System.out.println(node);
            }
        }
    }


    public static String getSimpleWords2Str(String req, List<String> removeType) throws Exception {
        ArrayList<ArrayList<String>> resultArr = getSimpleWords2(req, removeType);
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        String res = "";

        ArrayList<String> s1 = new ArrayList<String>();
        s1.add("NNG||NNP");
        s1.add("NNG||NNP&&NNG||NNP");
        s1.add("NNG||NNP&&NNG||NNP&&NNG||NNP");

        result = SeunjeonUtil.getArrayWordsForFindClass(resultArr, s1);

        for (ArrayList<String> cur : result) {
            res += " " + cur.get(3);
        }
        return res;
    }

    public static String getSimpleWords2StrForMatchClass2(String req, List<String> removeType) throws Exception {
        ArrayList<ArrayList<String>> resultArr = getSimpleWords2(req, removeType);
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        String res = "";

        ArrayList<String> s1 = new ArrayList<String>();
        s1.add("NNG||NNP");
        s1.add("NNG||NNP&&NNG||NNP");
        s1.add("NNG||NNP&&NNG||NNP&&NNG||NNP");

        result = SeunjeonUtil.getArrayWordsForMatchClass2(resultArr, s1);

        for (ArrayList<String> cur : result) {
            res += " " + cur.get(3);
        }
        return res;
    }

    // 형태소 분석 후 단어 취득
    public static ArrayList<ArrayList<String>> getSimpleWords2(String req, List<String> removeType) throws Exception {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        try {
            for (Eojeol eojeol : Analyzer.parseEojeolJava(req)) {
                for (LNode node : eojeol.nodesJava()) {
                    //System.out.println(node+"/"+node.morpheme().poses());
                    String key = node.morpheme().copy$default$1();
                    WrappedArray<String> nt = node.morpheme().copy$default$5();
                    //ArrayList<String> nt1 = nt;
                    List<String> nt1 = ScalaUtil.convert(nt);
                    //ArrayList<String> nt2 = new ArrayList<String>(nt1);
                    //System.out.println("#WORDx: nt1:" + nt1 + "/nt2:" + nt2);

                    // SN이 마스킹 되는 문제로 key를 꺼내어서 Array4번째에 치환해 준다.
                    if (nt1 != null && nt1.size() > 4) {
                        ArrayList<String> nt2 = new ArrayList<String>();
                        int i = 1;
                        for (String s : nt1) {
                            if (i == 4) {
                                nt2.add(key);
                            } else {
                                nt2.add(s);
                            }
                            i++;
                        }
                        result.add(nt2);
                    }
                    /*
                    if (nt2 != null && nt2.size() > 4) {
                        System.out.println("#WORD::"+key+" || " + nt2 + "/" + nt2.get(0) + " : " + nt2.get(3) + " : " + node.morpheme().cost() + " : " + node.morpheme().copy$default$7());

                        result.add(nt2);
                    }
                    */
                    //System.out.println(node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    // 형태소 룰셋 정리하여 ArrayList 로 리턴
    public static ArrayList<String> getListForRule(ArrayList<String> s1) {

        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> items = null;

        HashSet<ArrayList<String>> tagsMap = new HashSet<ArrayList<String>>();
        ArrayList<String> tags = null;

        if (s1 != null && s1.size() > 0) {
            String[] splitted_cond = null;
            for (String cond : s1) {
                int cnt = 0;
                tags = new ArrayList<String>();
                if(cond.length() > 0 && !cond.contains("&&")) { cond += "&&"; }

                if(cond.contains("&&")) {
                    splitted_cond = cond.split("\\&\\&");
                    items = new ArrayList<String>();
                    for (String cond_one_orig : splitted_cond) {
                        items.add(cond_one_orig);
                        System.out.println("#item:"+cond_one_orig);
                    }

                    // AA||BB||CC&&XX||YY => AA_XX, AA_YY, BB_XX, BB_YY, CC_XX, CC_YY
                    for (int point=0; point < items.size(); point++) {
                        // AA|BB
                        if (items.get(point).contains("||")) {
                            //AA
                            //BB
                            String[] condition = items.get(point).split("\\|\\|");
                            for (int pt_cond = 0; pt_cond < condition.length; pt_cond++) {
                                if (point == 0) {
                                    tags.add(condition[pt_cond]);
                                } else {
                                    //tags.set(point, tags.get(point)+"_"+one_cond);
                                    for (int point_tag = 0; point_tag < tags.size(); point_tag++) {
                                        System.out.println("## setA tags.get("+point_tag+") : "+ tags.get(point_tag));
                                        tags.set(point_tag, tags.get(point_tag)+"_"+condition[pt_cond]);
                                        System.out.println("## setA tags.set end("+point_tag+", "+ tags.get(point_tag)+")");
                                    }
                                }
                            }
                        } else {
                            if (!"".equals(items.get(point))) {
                                if (point == 0) {
                                    tags.add(items.get(point));
                                } else {
                                    for (int point_tag = 0; point_tag < tags.size(); point_tag++) {
                                        tags.set(point_tag, tags.get(point_tag)+"_"+items.get(point));
                                        System.out.println("## setB tags.set("+point_tag+", "+ tags.get(point_tag)+"_"+items.get(point));
                                    }
                                }
                            }
                        }

                        System.out.println("#cnt:"+cnt+" ### item:"+items.get(point) + "  ### tags:"+tags.toString());
                        cnt++;


                    }
                }
                tagsMap.add(tags);
            }
        }


        System.out.println("#tagsMap:"+tagsMap.toString());

        for(String s : tags) {
            System.out.println("#getList.condition tag:"+s);
        }
        Iterator iterator = tagsMap.iterator();
        while (iterator.hasNext()) {
            System.out.println("## tagsMap.one:"+iterator.next());
        }

        return result;
    }

    // 형태소 조회 결과에 주어진 품사리스트에 포함된 문자열을 추가하여 리턴
    public static ArrayList<ArrayList<String>> getArrayWordsForFindClass(ArrayList<ArrayList<String>> origArr, ArrayList<String> classArr) {
        ArrayList<ArrayList<String>> resultArr = new ArrayList<ArrayList<String>>();

        ArrayList<String> findTags = SeunjeonUtil.getListForRule2(classArr);

        String lv = "";
        String cv = "";
        String lw = "";
        String ww = "";
        String lww = "";

        ArrayList<String> newResult = null;

        boolean isAdded = false;
        for (ArrayList<String> ls : origArr) {
            resultArr.add(ls);
            //System.out.println("#current param:"+ls);
            if(ls != null && ls.get(0) != null && ls.get(3) != null) {
                if (lv.equals(ls.get(0))) {
                    if (lv.equals("")) {
                        cv = ls.get(0);
                    } else {
                        cv = cv + "_" + ls.get(0);
                    }
                } else {
                    if (lv.equals("")) {
                        cv = ls.get(0);
                    } else {
                        cv = lv + "_" + ls.get(0);
                    }
                }

                boolean existMatch = false;
                for(String tag : findTags) {
                    if (cv.equals(tag)) {
                        existMatch = true;
                        if (ww.equals("")) {
                            ww += lw + ls.get(3);
                        } else {
                            ww += ls.get(3);
                        }
                    }
                    //System.out.println("#333###### lv:"+lv+"        ### ww:"+ww+ "         ##### "+cv + " #### "+tag);
					/*
					else {
						//System.out.println("#WW:" + ww);
						System.out.println("#333###### lv:"+lv+"        ### ww:"+ww+ "         ##### "+cv + " #### "+tag);
						ww = "";
					}
					*/
                }
                if(!existMatch && ww.equals(lww)) ww = "";

                if(existMatch && !"".equals(ww)) {
					/*newResult = new ArrayList<String>();
					newResult.add(cv);
					newResult.add("_");
					newResult.add("_");
					newResult.add(ww);
					newResult.add("_");
					newResult.add("_");
					newResult.add("_");
					newResult.add("_");
					result.add(nweResult);
					*/
                    String newStr = cv+" _ _ "+ww+" _ _ _ _";
                    ArrayList<String> newItem = new ArrayList<String>();
                    newItem.addAll(Arrays.asList(newStr.split(" ")));
                    resultArr.add(newItem);
                }

                //System.out.println("####### lv:"+lv+"     ### lww:"+lww+"   ### ww:"+ww+ "         ##### "+cv);

                lv = ls.get(0);
                lw = ls.get(3);
                lww = ww;
            }
        }

        return resultArr;
    }



    // 형태소 조회 결과에 주어진 품사리스트에 포함된 문자열 "만" Only 추가하여 리턴
    public static ArrayList<ArrayList<String>> getArrayWordsForMatchClass(ArrayList<ArrayList<String>> origArr, ArrayList<String> classArr) {
        ArrayList<ArrayList<String>> resultArr = new ArrayList<ArrayList<String>>();

        ArrayList<String> findTags = SeunjeonUtil.getListForRule2(classArr);
        System.out.println("#Find Class Array:"+findTags);

        String lv = "";
        String cv = "";
        String lw = "";
        String ww = "";
        String lww = "";

        ArrayList<String> newResult = null;

        for (ArrayList<String> ls : origArr) {
            //resultArr.add(ls);
            ww = "";
            //System.out.println("#current param:"+ls);
            if(ls != null && ls.get(0) != null && ls.get(3) != null) {
                for(String tag : findTags) {
                    if (ls.get(0).equals(tag)) {
                        ww = ls.get(3);
                    }
                }
                if(!"".equals(ww)) {
					/*newResult = new ArrayList<String>();
					newResult.add(cv);
					newResult.add("_");
					newResult.add("_");
					newResult.add(ww);
					newResult.add("_");
					newResult.add("_");
					newResult.add("_");
					newResult.add("_");
					result.add(nweResult);
					*/
                    //System.out.println("33####### lv:"+lv+"     ### lww:"+lww+"   ### ww:"+ww+ "         ##### "+cv);
                    String newStr = ls.get(0)+" _ _ "+ww+" _ _ _ _";
                    ArrayList<String> newItem = new ArrayList<String>();
                    newItem.addAll(Arrays.asList(newStr.split(" ")));
                    resultArr.add(newItem);
                }

                //System.out.println("44####### lv:"+lv+"     ### lww:"+lww+"   ### ww:"+ww+ "         ##### "+cv);

                lv = ls.get(0);
                lw = ls.get(3);
                lww = ww;
            }
        }

        return resultArr;
    }

    // 형태소 조회 결과에 주어진 품사리스트에 포함된 문자열(만) 추가하여 리턴2
    public static ArrayList<ArrayList<String>> getArrayWordsForMatchClass2(ArrayList<ArrayList<String>> origArr, ArrayList<String> classArr) {
        ArrayList<ArrayList<String>> resultArr = new ArrayList<ArrayList<String>>();

        ArrayList<String> findTags = SeunjeonUtil.getListForRule2(classArr);

        //System.out.println("#SeunjeonUtil findTags:"+findTags.toString());

        String lv = "";
        String lvv = "";
        String cv = "";
        String lw = "";
        String cw = "";
        String lww = "";


        for (ArrayList<String> ls : origArr) {
            //resultArr.add(ls);
            //System.out.println("#current param:"+ls);
            if(ls != null && ls.get(0) != null && ls.get(3) != null && !"".equals(ls.get(3))) {
                cv = ls.get(0);
                cw = ls.get(3);

                lv = lv + "_" + cv;
                if(!"".equals(lw.trim())) {
                    //lw = lw + "_" + ls.get(3);
                    lw = lw + ls.get(3);
                } else {
                    lw = lw + ls.get(3);
                }

                lvv = lvv + "_" + cv;
                if(!"".equals(lww.trim())) {
                    //lww = lww + "_" + ls.get(3);
                    lww = lww + ls.get(3);
                } else {
                    lww = lww + ls.get(3);
                }

                boolean existMatch = false;
                for(String tag : findTags) {

                    //System.out.println("# find matchClassWords for tag:"+tag+"     || cv:"+cv+"/cw:"+cw+"   || lv:"+lv+"/lw:"+lw + "   ||  lvv:"+lvv+"/lww:"+lww);
                    String newStr = "";
                    if (cv.equals(tag)) {
                        existMatch = true;
                        newStr = cv + " _ _ " + cw + " _ _ _ _";
                        //System.out.println("#### CV.existMatch:" + existMatch + " | cv:" + cv + "/tag:" + tag + "/word:" + cw);


                        ArrayList<String> newItem = new ArrayList<String>();
                        newItem.addAll(Arrays.asList(newStr.split(" ")));

                        //System.out.println("##SeunjeonUtil getArrayWordsForMatchClass2 add New-Item2:" + newItem.toString());

                        resultArr.add(newItem);
                        existMatch = false;
                    }
                    if (lv.equals(tag) && lv.contains("_")
                            && !lv.contains("MMD_NNG")
                            && !lv.contains("MMD_NNB")
                            ) {
                        existMatch = true;
                        newStr = lv + " _ _ " + lw.trim() + " _ _ _ _";
                        //System.out.println("#### LV.existMatch:" + existMatch + " | lv:" + lv + "/tag:" + tag + "/word:" + lw);


                        ArrayList<String> newItem = new ArrayList<String>();
                        newItem.addAll(Arrays.asList(newStr.split(" ")));
                        //for(String s : newStr.split(" ")) {
                        //    newItem.add(s.replace("1", " "));
                        //}

                        //System.out.println("##SeunjeonUtil getArrayWordsForMatchClass2 add New-Item2:" + newItem.toString());

                        resultArr.add(newItem);
                        existMatch = false;
                    }
                    if (lvv.equals(tag) && lvv.contains("_")
                            && !lvv.contains("MMD_NNG")
                            && !lvv.contains("MMD_NNB")
                            ) {
                        existMatch = true;
                        newStr = lvv + " _ _ " + lww.trim()  + " _ _ _ _";
                        //System.out.println("#### LVV.existMatch:" + existMatch + " | lvv:" + lvv + "/tag:" + tag + "/word:" + lww);


                        ArrayList<String> newItem = new ArrayList<String>();
                        newItem.addAll(Arrays.asList(newStr.split(" ")));
                        //for(String s : newStr.split(" ")) {
                        //    newItem.add(s.replace("1", " "));
                        //}
                        //System.out.println("##SeunjeonUtil getArrayWordsForMatchClass2 add New-Item2:" + newItem.toString());

                        resultArr.add(newItem);
                        existMatch = false;
                    }
                }

                lvv = lv;
                lww = lw;
                lv = cv;
                lw = cw;
            }
        }

        return resultArr;
    }

    public static ArrayList<String> getMutexArrays(ArrayList<String> origArr, ArrayList<ArrayList<String>> mutexArrays) {
        ArrayList<String> newArray = null;

        if(mutexArrays != null) {
            for(ArrayList<String> arr : mutexArrays) {
                //System.out.println("#ITEM_MUTEXT_ARRAY from:"+newArray);
                newArray = getMutexArray(newArray, arr);
                //System.out.println("#ITEM_MUTEXT_ARRAY"+arr+"  >>  "+newArray);
            }
        }
        return newArray;
    }

    public static ArrayList<String> getMutexArray(ArrayList<String> origArr, ArrayList<String> mutexArr) {
        ArrayList<String> newArray = new ArrayList<String>();;
        if(mutexArr != null){
            if(origArr != null) {
                String sumTxt = "";
                for (String orig : origArr) {
                    for (String mutexTxt : mutexArr) {
                        sumTxt = orig + "_" + mutexTxt;
                        newArray.add(sumTxt);
                    }
                }
            } else {
                for (String addTxt : mutexArr) {
                    newArray.add(addTxt);
                }
            }
        }
        return newArray;
    }


    // 형태소 룰셋 정리하여 ArrayList 로 리턴
    public static ArrayList<String> getListForRule2(ArrayList<String> s1) {

        ArrayList<String> result = null;

        //HashSet<ArrayList<String>> tagsMap = new HashSet<ArrayList<String>>();
        ArrayList<ArrayList<String>> item_array = null;
        ArrayList<ArrayList<ArrayList<String>>> item_arrays = new ArrayList<ArrayList<ArrayList<String>>>();

        if (s1 != null && s1.size() > 0) {
            String[] splitted_cond = null;
            ArrayList<String> elems = null;
            for (String cond : s1) {
                if(cond.length() > 0 && !cond.contains("&&")) { cond += "&&"; }

                if (cond.contains("&&")) {
                    item_array = new ArrayList<ArrayList<String>>();
                    splitted_cond = cond.split("\\&\\&");
                    for (String cond_one_orig : splitted_cond) {
                        elems = new ArrayList<String>();
                        if (cond_one_orig.contains("||")) {
                            String[] splitted_elms = cond_one_orig.split("\\|\\|");
                            for (String s : splitted_elms) {
                                elems.add(s);
                            }
                        } else {
                            elems = new ArrayList<String>();
                            elems.add(cond_one_orig);
                        }

                        item_array.add(elems);
                    }
                    item_arrays.add(item_array);
                    //System.out.println("### item-array:" + item_array.toString());
                    //System.out.println("### item-arrays:" + item_arrays.toString());
                }
            }
        }

        for (ArrayList<ArrayList<String>> item_array1 : item_arrays) {
                //System.out.println("#ITEM_ARR:" + item_array1.toString());
                ArrayList<String> tmpArr = getMutexArrays(result, item_array1);
                result = setArrayToOneArray(result, tmpArr);
        }

        return result;
    }

    public static ArrayList<String> setArrayToOneArray(ArrayList<String> origArr, ArrayList<String> reqArr) {
        if (origArr == null) origArr = new ArrayList<String>();
        for (String str : reqArr) {
            origArr.add(str);
        }
        return origArr;
    }

    // 형태소 분석
    public static void parse1(String req) throws Exception {
        for (LNode node : Analyzer.parseJava(req)) {
            //System.out.println(node);

            System.out.println(node.morpheme().surface());

        }
    }

    // 어절 분석
    public static void parse2(String req) throws Exception {
        for (Eojeol eojeol : Analyzer.parseEojeolJava("아버지가방에들어가신다.")) {
            System.out.println(eojeol);
            for (LNode node : eojeol.nodesJava()) {
                System.out.println(node);
            }
        }
}

    /**
     * 사용자 사전 추가
     * surface,cost
     *   surface: 단어명. '+' 로 복합명사를 구성할 수 있다.
     *           '+'문자 자체를 사전에 등록하기 위해서는 '\+'로 입력. 예를 들어 'C\+\+'
     *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
     */
    public static void setUserDict(String req) {
        Analyzer.setUserDict(Arrays.asList("덕후", "버카충,-100", "낄끼+빠빠,-100").iterator());
        for (LNode node : Analyzer.parseJava("덕후냄새가 난다.")) {
            System.out.println(node);
        }
    }

    // 활용어 원형
    public static void getOrig(String req) {
        for (LNode node : Analyzer.parseJava("빨라짐")) {
            for (LNode node2 : node.deInflectJava()) {
                System.out.println(node2);
            }
        }
    }
        // 복합명사 분해
    public static void parse3(String req) {
        for (LNode node : Analyzer.parseJava("낄끼빠빠")) {
            System.out.println(node);   // 낄끼빠빠
            for (LNode node2: node.deCompoundJava()) {
                System.out.println(node2);  // 낄끼+빠빠
            }
        }
    }
}