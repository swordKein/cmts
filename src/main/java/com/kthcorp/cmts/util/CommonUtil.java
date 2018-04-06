package com.kthcorp.cmts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    /* Paging pagination 정리 */
    public static Map<String, Object> getPagination(Integer allCnt, Integer pageSize, Integer pageNo, int maxLimit) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<String> listActive = new ArrayList<String>();
        List<Integer> listPage = new ArrayList<Integer>();

        int limit = 1;
        int maxPage = allCnt / pageSize + 1;

        int startPage = pageNo - 2;
        if (startPage < 1) startPage = 1;

        int currPage = startPage;

        while (limit <= maxLimit && currPage <= maxPage) {
            if (currPage == pageNo) {
                listActive.add("active");
            } else {
                listActive.add("");
            }
            listPage.add(currPage);

            currPage++;
            limit++;
        }

        resultMap.put("listPage", listPage);
        resultMap.put("listActive", listActive);
        return resultMap;
    }

    /* Paging pagination 정리 10page 넥스트 10페이지 */
    public static Map<String, Object> getPaginationJump(Integer allCnt, Integer pageSize, Integer pageNo, int maxLimit) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<String> listActive = new ArrayList<String>();
        List<Integer> listPage = new ArrayList<Integer>();

        int limit = 1;
        int maxPage = allCnt / pageSize + 1;

        int maxGroup = maxPage / maxLimit  + 1;

        int groupNo = 1;
        groupNo = pageNo / maxLimit + 1;

        if(pageNo % maxLimit == 0) groupNo--;

        int startPage = (groupNo - 1) * maxLimit;
        /*
        System.out.println("#maxPage:"+maxPage);
        System.out.println("#maxGroup:"+maxGroup);
        System.out.println("#pageNo:"+pageNo);
        System.out.println("#groupNo:"+groupNo);
        System.out.println("#startPage:"+startPage);
        */
        if (startPage < 0) startPage = 0;

        int currPage = startPage;

        while (limit <= ((int) maxLimit + 2) && currPage <= maxPage) {
            //System.out.println("#COMP:: limit:"+limit+"    maxLimit+2:"+((int) maxLimit+2)+"    currPage:"+currPage+"    maxPage:"+maxPage);

            if (currPage == pageNo) {
                listActive.add("active");
            } else {
                listActive.add("");
            }
            listPage.add(currPage);

            currPage++;
            limit++;
        }

        resultMap.put("listPage", listPage);
        resultMap.put("listActive", listActive);
        return resultMap;
    }

    /* HTML Tag 제거 */
    public static String removeTag(String str){
        Matcher mat;
        try {
            // script 처리
            Pattern script = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>",Pattern.DOTALL);
            mat = script.matcher(str);
            str = mat.replaceAll(" ");
            // style 처리
            Pattern style = Pattern.compile("<style[^>]*>.*</style>",Pattern.DOTALL);
            mat = style.matcher(str);
            str = mat.replaceAll(" ");
            // tag 처리
            //Pattern tag = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
            Pattern tag = Pattern.compile("\\<[^\\>]*\\>");
            mat = tag.matcher(str);
            str = mat.replaceAll(" ");
            // ntag 처리
            Pattern ntag = Pattern.compile("<\\w+\\s+[^<]*\\s*>");
            mat = ntag.matcher(str);
            str = mat.replaceAll(" ");
            // entity ref 처리
            Pattern Eentity = Pattern.compile("&[^;]+;");
            mat = Eentity.matcher(str);
            str = mat.replaceAll(" ");
            // whitespace 처리
            Pattern wspace = Pattern.compile("\\s\\s+");
            mat = wspace.matcher(str);
            str = mat.replaceAll(" ");

            str = str.trim().replace("  ", " ");

        } catch (Exception e)
        {
            //e.printStackTrace();
            //
        }
        return removeTex(str) ;
    }

    public static String removeNumber(String str) {
        String tmpStr = str;

        tmpStr = tmpStr.replace("0","");
        tmpStr = tmpStr.replace("1","");
        tmpStr = tmpStr.replace("2","");
        tmpStr = tmpStr.replace("3","");
        tmpStr = tmpStr.replace("4","");
        tmpStr = tmpStr.replace("5","");
        tmpStr = tmpStr.replace("6","");
        tmpStr = tmpStr.replace("7","");
        tmpStr = tmpStr.replace("8","");
        tmpStr = tmpStr.replace("9","");
        return tmpStr;
    }

    public static String removeTex(String str) {
        /* 제외어 로직 추가함으로 삭제
         중앙일보 태그 삭제

        if(str.contains("PGA TOUR")) {
            System.out.println("#CommonUtil.removeText has destroy req-Str because text include PGA_TOUR!");
            str = "";
        }
        */

        String tmpStr = str;
        tmpStr = tmpStr.replace("'한국언론 뉴스허브'","");
        tmpStr = tmpStr.replace("뉴시스통신사.","");
        tmpStr = tmpStr.replace("무단전재-재배포 금지.","");
        tmpStr = tmpStr.replace("무단전재-재배포 금지","");
        tmpStr = tmpStr.replace("Copyright by JTBC,","");
        tmpStr = tmpStr.replace("DramaHouseJcontentHub Co., Ltd.", "");
        tmpStr = tmpStr.replace("All Rights Reserved.","");
        tmpStr = tmpStr.replace("<저작권자ⓒ   .>","");
        tmpStr = tmpStr.replace("저작권자ⓒ.","");
        tmpStr = tmpStr.replace("저작권자 © 뉴스1코리아,","");
        tmpStr = tmpStr.replace("저작권자 ⓒ","");
        tmpStr = tmpStr.replace("무단전재 및 재배포 금지.","");
        tmpStr = tmpStr.replace("무단전재 및 재배포 금지","");
        tmpStr = tmpStr.replace("ⓒ","");
        tmpStr = tmpStr.replace("ⓒ CBS 노컷뉴스(www.nocutnews.co.kr)","");
        tmpStr = tmpStr.replace("CBS 노컷뉴스(www.nocutnews.co.kr)","");
        tmpStr = tmpStr.replace("JTBC 핫클릭","");
        tmpStr = tmpStr.replace("DramaHouseJcontentHub Co., Ltd.","");
        tmpStr = tmpStr.replace("\n","");
        tmpStr = tmpStr.replace("\r","");
        tmpStr = tmpStr.replace("/뉴스1","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@newsis.com","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@hani.co.kr","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@hani.co","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@hani","");
        tmpStr = tmpStr.replace("기자 .","기자");
        tmpStr = tmpStr.replace("기자광고","기자");
        tmpStr = tmpStr.replace("기자고","기자");
        tmpStr = tmpStr.replace("기사 더보기 광고","");
        tmpStr = tmpStr.replace(" 광고$","");
        tmpStr = tmpStr.replace("기자","기자 ");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@kmib.co.kr","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@kmib.co","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@kmib","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@seoul.co.kr","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@seoul.co","");
        tmpStr = tmpStr.replaceAll("[a-z0-9]*@seoul","");

        tmpStr = tmpStr.replace("\\t"," ");
        tmpStr = tmpStr.replace("\\r", " ");
        tmpStr = tmpStr.replace("\\n", " ");
        tmpStr = tmpStr.replace("\\^", "");
        tmpStr = tmpStr.replace("\\", "");
        tmpStr = tmpStr.replace("\"", "");
        tmpStr = tmpStr.replace("\'", "");
        tmpStr = tmpStr.replace("\\‘", "");
        tmpStr = tmpStr.replace("\\’", "");
        tmpStr = tmpStr.replace(",", "");
        tmpStr = tmpStr.replace("●","");
        tmpStr = tmpStr.replace("▲","");
        tmpStr = tmpStr.replace("-","");
        tmpStr = tmpStr.replace("\\uD","");
        tmpStr = tmpStr.replace("*","");
        tmpStr = tmpStr.replace("combined", "");
        tmpStr = tmpStr.replace(" _,", "");
        tmpStr = tmpStr.replace("<br>", " ");
        tmpStr = tmpStr.replace("<b>", " ");
        tmpStr = tmpStr.replace("</b>", " ");
        tmpStr = tmpStr.replace("[", " ");
        tmpStr = tmpStr.replace("]", " ");
        tmpStr = tmpStr.replace("{", " ");
        tmpStr = tmpStr.replace("}", " ");
        return tmpStr;
    }

    public static String removeAllSpec(String reqStr) {
        String result = reqStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
        return result;
    }

    public static String removeAllSpec1(String reqStr) {
        String result = reqStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ");
        return result;
    }

    public static String removeLineFeed(String reqStr) {
        reqStr = reqStr.replace("\n","");
        reqStr = reqStr.replace("\r","");
        return reqStr;
    }

    public static String removeAllSpec2(String reqStr) {
        String result = reqStr.replaceAll("|", "");
        result = result.replace("select","");
        result = result.replace("from","");
        result = result.replace("insert","");
        result = result.replace("into","");
        result = result.replace("update","");
        result = result.replace("set","");
        result = result.replace("union","");
        result = result.replace("join","");
        result = result.replace("all","");
        result = result.replace("left","");
        result = result.replace("right","");
        result = result.replace("*","");
        result = result.replace("\\","");
        result = result.replace("(","");
        result = result.replace(")","");
        return result;
    }

    public static String filterByWordArray(ArrayList<String> wordsArray, String reqStr) {
        // 중앙일보 태그 삭제
        for(String filterword : wordsArray) {
            if (reqStr.contains(filterword)) {
                System.out.println("#CommonUtil.filterByWordArray has destroy req-Str because text include word:"+filterword+"!");
                reqStr = "";
                return reqStr;
            }
        }
        return reqStr;
    }

    public static String removeBrackets (String req) {
        req = req.replace("[","");
        req = req.replace("]", "");
        return req;
    }

    public static String removeNationStr(String req) {
        req = req.replace("아르메니아", "");
        req = req.replace("그리스", "");
        req = req.replace("아일랜드", "");
        req = req.replace("대만", "");
        req = req.replace("몽골", "");
        req = req.replace("말레이지아", "");
        req = req.replace("멕시코", "");
        req = req.replace("일본", "");
        req = req.replace("모로코", "");
        req = req.replace("노르웨이", "");

        req = req.replace("핀란드", "");
        req = req.replace("칠레", "");
        req = req.replace("아르헨티나", "");
        req = req.replace("뉴질랜드", "");
        req = req.replace("캐나다", "");
        req = req.replace("아프가니스탄", "");
        req = req.replace("스웨덴", "");
        req = req.replace("타지키스탄", "");

        req = req.replace("한국", "");
        req = req.replace("기니", "");
        req = req.replace("카자흐스탄", "");
        req = req.replace("벨기에", "");
        req = req.replace("영국", "");
        req = req.replace("이란", "");
        req = req.replace("러시아구", "");
        req = req.replace("덴마크", "");
        req = req.replace("홍콩", "");
        req = req.replace("라트비아", "");

        req = req.replace("튀니지", "");
        req = req.replace("미국", "");
        req = req.replace("폴란드", "");
        req = req.replace("중국", "");
        req = req.replace("터키", "");
        req = req.replace("필리핀", "");
        req = req.replace("스페인", "");
        req = req.replace("헝가리", "");
        req = req.replace("세네갈", "");
        req = req.replace("이집트", "");
        req = req.replace("루마니아", "");

        req = req.replace("스위스", "");
        req = req.replace("키르기스스탄", "");
        req = req.replace("체코", "");
        req = req.replace("이탈리아", "");
        req = req.replace("독일", "");
        req = req.replace("포르투갈", "");
        req = req.replace("페루", "");
        req = req.replace("독일구", "");
        req = req.replace("베트남", "");
        req = req.replace("인도", "");

        req = req.replace("프랑스", "");
        req = req.replace("오스트리아", "");
        req = req.replace("소련", "");
        req = req.replace("태국", "");
        req = req.replace("오스트레일리아", "");
        req = req.replace("인도네시아", "");
        req = req.replace("이스라엘", "");
        req = req.replace("러시아", "");
        req = req.replace("연방", "");
        req = req.replace("네덜란드", "");
        req = req.replace("쿠바", "");
        req = req.replace("서독", "");
        req = req.replace("구", "");
        req = req.replace("네시아", "");
        req = req.replace("[","");
        req = req.replace("]", "");

        return req;
    }



}
