package com.kthcorp.cmts.util;

import com.google.gson.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllBytes;
import static java.util.stream.Collectors.counting;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class StringUtilTest {

    @Test
    public void findCountAlphabets() {
        String txt = "http://cofs.tistory.com?param1=a123&param2=한글&param3=개발자&param4=cofs";
        char[] txtChar = txt.toCharArray();
        for (int j = 0; j < txtChar.length; j++) {
            if (txtChar[j] >= '\uAC00' && txtChar[j] <= '\uD7A3') {
                String targetText = String.valueOf(txtChar[j]);
                try {
                    txt = txt.replace(targetText, URLEncoder.encode(targetText, "utf-8"));
                    System.out.println("txt:"+txt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void patternMatch(){
        String txt = " ab12345가나다";
        int cntEng = StringUtil.getCountAlphabets(txt);
        int cntKor = StringUtil.getCountKorean(txt);
        int cntNum = StringUtil.getCountNumber(txt);
        System.out.println("#Result:"+cntEng+" / "+cntKor+" / "+cntNum);

        double ratio = StringUtil.getRatioEngPerKor(txt);
        System.out.println("#Ratio:"+ratio);
    }

    @Test
    public void countPatternMatch() {
        String s = "<br>dddd<br>ddgggg<br>bbbbbttt<br>";
        Pattern p = Pattern.compile("[a-z]");

        Matcher m = p.matcher(s);
        int count  = 0;
        //int count2 = 0;
        for( int i = 0; m.find(i); i = m.end()){
            //if(count<4){
            count++;
            //count2=i;
            //}
        }
        System.out.println(count);
        //System.out.println(count+"  " + count2); //특정문자열(Pattern)의 갯수
        //System.out.println(s.substring(0,count2)); //특정 문자 갯수까지 출력하기

    }

    @Test
    public void countPatternMatch2() {
        String s = "<br>dddd<br>ddgggg<br>bbbbbttt<br>";
        Pattern p = Pattern.compile("<br>");

        Matcher m = p.matcher(s);
        int count  = 0;
        int count2 = 0;
        for( int i = 0; m.find(i); i = m.end()){
            if(count<4){
                count++;
                count2=i;
            }
        }
        System.out.println(count+"  " + count2); //특정문자열(Pattern)의 갯수
        System.out.println(s.substring(0,count2)); //특정 문자 갯수까지 출력하기

    }


    @Test
    public void test_parse_URL() {
        String logNo = "";
        String blogId = "";
        String s = "http://yonina.kr/110136929666";

        String[] s2 = s.split("/");

        if ( s2 != null && s2[3] != null) {
            logNo = s2[3];
            String[] t2 = s2[2].split("\\.");
            if (t2 != null && t2[0] != null) {
                blogId = t2[0];
            }
        }
        System.out.println("##"+s+"   ->   blogId:"+blogId+"  :: logNo:"+logNo);
    }


    @Test
    public void test_getMapArrayFromStringSeperatedComma() {
        String req = "";
        HashMap<String, Double> res = StringUtil.getMapArrayFromStringSeperatedComma(req);

    }

    @Test
    public void test_printJsonObject() {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("TOTAL_COUNT", 30);

        JsonArray jarr = new JsonArray();

        JsonObject con = new JsonObject();
        con.addProperty("TAGGING_ID", "10434");
        con.addProperty("MASTER_CONTENT_ID", "1002258534");
        con.addProperty("CONTENT_ID", "10022585340001");
        con.addProperty("CONTENT_TITLE", "겨울왕국");
        con.addProperty("META_GENRE", "애니메이션/어드벤처/가족");
        con.addProperty("META_WHEN", "겨울");
        con.addProperty("META_WHERE", "왕국");
        con.addProperty("META_WHAT", "가족");
        con.addProperty("META_WHO", "엘사 울라프");
        con.addProperty("META_EMOTION", "슬픔 신남");
        con.addProperty("META_SUBGENRE", "가족 애니메이션");
        con.addProperty("META_KEYWORD", "겨울왕국 겨울 엘사 울라프");
        con.addProperty("META_AWARD", "6회 천안영화제, 2015 초청 크리스 벅 외 1명 (상영작) 0회 충무로뮤지컬영화제, 2015 초청 크리스 벅 외 1명 (싱   CHIMFF) 38회 일본아카데미상, 2015 수상 외국작품상 86회 아카데미시상식, 2014 수상 크리스 벅, 제니퍼 리  장편애니메이션상) \"Let It Go\" (주제가상) 24회 유바리국제판타스틱영화제, 2014 수상 크리스 벅, 제니퍼 리 (관객상) 초청 크리스 벅, 제니퍼 리 (특별초청)");
        con.addProperty("META_PLOT", "얼어붙은 세상을 녹일 자매가 온다! 서로가 최고의 친구였던 자매 ‘엘사’와 ‘안나’. 하지만 언니 ‘엘사’에게는 하나뿐인 동생에게조차 말 못할 비밀이 있다. 모든 것을 얼려버리는 신비로운 힘이 바로 그것. ‘엘사’는 통제할 수 없는 자신의 힘이 두려워 왕국을 떠나고, 얼어버린 왕국의 저주를 풀기 위해 ‘안나’는 언니를 찾아 환상적인 여정을 떠나는데……");
        con.addProperty("META_CHARACTERS", "엘사");
        con.addProperty("META_RECOMMEND_TAG", "다시 군대가는 꿈을 꾸는 그대에게");
        con.addProperty("META_RECOMMEND_SITUATION", "25세 남자");

        jarr.add(con);

        JsonObject con2 = new JsonObject();
        con2.addProperty("meta","meta2");
        jarr.add(con2);

        //jobj.add("CONTENTS", jarr);

        //System.out.println("#RESLT:"+jobj.toString());
        System.out.println("#RESLT:"+jarr.toString());


    }

    @Test
    public void test__auth_hash() {
        JsonObject result = new JsonObject();

        result.addProperty("RT_CODE", 1);
        result.addProperty("RT_MSG", "SUCCESS");
        result.addProperty("RESULT", "3DS89FJK2LIFFIEF23");
        System.out.println("RESULT:" + result.toString());
    }

    @Test
    public void test__auth_user_list() {
        JsonObject result = new JsonObject();

        result.addProperty("RT_CODE", 1);
        result.addProperty("RT_MSG", "SUCCESS");

        JsonArray result1 = new JsonArray();
        JsonObject new1 = new JsonObject();
        new1.addProperty("USERID", "test1");
        new1.addProperty("GRANT", "ADMIN");
        new1.addProperty("REGDATE", "2017-12-31 00:00:00");
        result1.add(new1);
        JsonObject new2 = new JsonObject();
        new2.addProperty("USERID", "test2");
        new2.addProperty("GRANT", "ADMIN");
        new2.addProperty("REGDATE", "2017-12-31 00:00:00");
        result1.add(new2);

        result.add("RESULT", result1);
        System.out.println("RESULT:" + result.toString());
    }

    @Test
    public void test__common_post() {
        JsonObject result = new JsonObject();

        result.addProperty("RT_CODE", 1);
        result.addProperty("RT_MSG", "SUCCESS");
        System.out.println("RESULT:"+result.toString());
    }

    @Test
    public void test__dash_list() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();

        JsonObject list_stat = new JsonObject();
        list_stat.addProperty("COUNT_INSERTED", 200);
        list_stat.addProperty("COUNT_INSERT_TAGGED", 178);
        list_stat.addProperty("COUNT_START_COLLECT", 182);
        list_stat.addProperty("COUNT_COLLECTED",180);
        list_stat.addProperty("COUNT_START_ANALYZE", 179);
        list_stat.addProperty("COUNT_ANALYZED", 179);
        list_stat.addProperty("COUNT_START_TAG", 179);
        list_stat.addProperty("COUNT_TAGGED",178);
        result.add("LIST_STAT", list_stat);

        JsonObject list_sum = new JsonObject();
        list_sum.addProperty("COUNT_READY", 100);
        list_sum.addProperty("COUNT_FAIL_COLLECT", 12);
        list_sum.addProperty("COUNT_FAIL_ANALYZE", 0);
        list_sum.addProperty("COUNT_READY_TAG", 2);
        result.add("LIST_SUMMARY", list_sum);

        JsonObject list_ratio = new JsonObject();
        list_ratio.addProperty("RATIO_ALL_TAG", 178);
        list_ratio.addProperty("RATIO_COLLECT", 95);
        list_ratio.addProperty("RATIO_ANALYZE", 95);
        list_ratio.addProperty("RATIO_TAG", 95);
        result.add("LIST_RATIO", list_ratio);

        JsonObject list_graph_weekly = new JsonObject();
        JsonArray list_caption = new JsonArray();
        list_caption.add("W6");
        list_caption.add("W5");
        list_caption.add("W4");
        list_caption.add("W3");
        list_caption.add("W2");
        list_caption.add("W1");
        list_graph_weekly.add("LIST_CAPTION", list_caption);
        JsonArray list_count_inserted = new JsonArray();
        list_count_inserted.add(10);
        list_count_inserted.add(7);
        list_count_inserted.add(6);
        list_count_inserted.add(4);
        list_count_inserted.add(8);
        list_count_inserted.add(7);
        list_graph_weekly.add("LIST_COUNT_INSERTED", list_count_inserted);
        JsonArray list_count_collected = new JsonArray();
        list_count_collected.add(9);
        list_count_collected.add(7);
        list_count_collected.add(4);
        list_count_collected.add(4);
        list_count_collected.add(8);
        list_count_collected.add(6);
        list_graph_weekly.add("LIST_COUNT_COLLECTED", list_count_collected);
        JsonArray list_count_analyzed = new JsonArray();
        list_count_analyzed.add(9);
        list_count_analyzed.add(7);
        list_count_analyzed.add(4);
        list_count_analyzed.add(4);
        list_count_analyzed.add(8);
        list_count_analyzed.add(6);
        list_graph_weekly.add("LIST_COUNT_ANALYZED", list_count_analyzed);
        JsonArray list_count_tagged = new JsonArray();
        list_count_tagged.add(9);
        list_count_tagged.add(7);
        list_count_tagged.add(4);
        list_count_tagged.add(4);
        list_count_tagged.add(8);
        list_count_tagged.add(6);
        list_graph_weekly.add("LIST_COUNT_TAGGED", list_count_tagged);
        result.add("LIST_GRAPH_WEEKLY", list_graph_weekly);


        JsonObject list_graph_daily = new JsonObject();
        JsonArray dlist_caption = new JsonArray();
        dlist_caption.add("D6");
        dlist_caption.add("D5");
        dlist_caption.add("D4");
        dlist_caption.add("D3");
        dlist_caption.add("D2");
        dlist_caption.add("D1");
        list_graph_daily.add("LIST_CAPTION", dlist_caption);
        JsonArray dlist_count_inserted = new JsonArray();
        dlist_count_inserted.add(10);
        dlist_count_inserted.add(7);
        dlist_count_inserted.add(6);
        dlist_count_inserted.add(4);
        dlist_count_inserted.add(8);
        dlist_count_inserted.add(7);
        list_graph_daily.add("LIST_COUNT_INSERTED", dlist_count_inserted);
        JsonArray dlist_count_collected = new JsonArray();
        dlist_count_collected.add(9);
        dlist_count_collected.add(7);
        dlist_count_collected.add(4);
        dlist_count_collected.add(4);
        dlist_count_collected.add(8);
        dlist_count_collected.add(6);
        list_graph_daily.add("LIST_COUNT_COLLECTED", dlist_count_collected);
        JsonArray dlist_count_analyzed = new JsonArray();
        dlist_count_analyzed.add(9);
        dlist_count_analyzed.add(7);
        dlist_count_analyzed.add(4);
        dlist_count_analyzed.add(4);
        dlist_count_analyzed.add(8);
        dlist_count_analyzed.add(6);
        list_graph_daily.add("LIST_COUNT_ANALYZED", dlist_count_analyzed);
        JsonArray dlist_count_tagged = new JsonArray();
        dlist_count_tagged.add(9);
        dlist_count_tagged.add(7);
        dlist_count_tagged.add(4);
        dlist_count_tagged.add(4);
        dlist_count_tagged.add(8);
        dlist_count_tagged.add(6);
        list_graph_daily.add("LIST_COUNT_TAGGED", dlist_count_tagged);
        result.add("LIST_GRAPH_DAILY", list_graph_daily);

        result_all.add("RESULT", result);

        System.out.println("RESULT:"+result_all.toString());


    }


    @Test
    public void test_printJsonObject2() {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("TOTAL_COUNT", 30);

        JsonArray jarr = new JsonArray();

        JsonObject con = new JsonObject();
        con.addProperty("TAGGING_ID", "10434");
        con.addProperty("SERIES_ID", "1002258534");
        con.addProperty("SERIES_NM", "겨울왕국");
        con.addProperty("META_GENRE", "애니메이션/어드벤처/가족");
        con.addProperty("META_WHEN", "겨울");
        con.addProperty("META_WHERE", "왕국");
        con.addProperty("META_WHAT", "가족");
        con.addProperty("META_WHO", "엘사 울라프");
        con.addProperty("META_EMOTION", "슬픔 신남");
        con.addProperty("META_SUBGENRE", "가족 애니메이션");
        con.addProperty("META_KEYWORD", "겨울왕국 겨울 엘사 울라프");
        con.addProperty("META_AWARD", "6회 천안영화제, 2015 초청 크리스 벅 외 1명 (상영작) 0회 충무로뮤지컬영화제, 2015 초청 크리스 벅 외 1명 (싱   CHIMFF) 38회 일본아카데미상, 2015 수상 외국작품상 86회 아카데미시상식, 2014 수상 크리스 벅, 제니퍼 리  장편애니메이션상) \"Let It Go\" (주제가상) 24회 유바리국제판타스틱영화제, 2014 수상 크리스 벅, 제니퍼 리 (관객상) 초청 크리스 벅, 제니퍼 리 (특별초청)");
        con.addProperty("META_PLOT", "얼어붙은 세상을 녹일 자매가 온다! 서로가 최고의 친구였던 자매 ‘엘사’와 ‘안나’. 하지만 언니 ‘엘사’에게는 하나뿐인 동생에게조차 말 못할 비밀이 있다. 모든 것을 얼려버리는 신비로운 힘이 바로 그것. ‘엘사’는 통제할 수 없는 자신의 힘이 두려워 왕국을 떠나고, 얼어버린 왕국의 저주를 풀기 위해 ‘안나’는 언니를 찾아 환상적인 여정을 떠나는데……");
        con.addProperty("META_CHARACTERS", "엘사");
        con.addProperty("META_RECOMMEND_TAG", "다시 군대가는 꿈을 꾸는 그대에게");
        con.addProperty("META_RECOMMEND_SITUATION", "25세 남자");

        jarr.add(con);

        jobj.add("CONTENTS", jarr);

        System.out.println("#RESLT:"+jobj.toString());
    }


    @Test
    public void test__item_list() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();

        result.addProperty("PAGESIZE", 20);
        result.addProperty("MAXPAGE", 6);
        result.addProperty("PAGENO", 1);

        JsonArray list_paging = new JsonArray();
        list_paging.add(1);
        list_paging.add(2);
        list_paging.add(3);
        list_paging.add(4);
        list_paging.add(5);
        result.add("LIST_PAGING", list_paging);

        result.addProperty("SEARCHTYPE", "ALL");
        result.addProperty("SEARCHSTAT", "ALL");
        result.addProperty("SEARCHSDATE", "");
        result.addProperty("SEARCHEDATE", "");
        result.addProperty("SEARCHKEYWORD", "");
        result.addProperty("SEARCHPARTARRAY", "[]");

        JsonObject stat_search = new JsonObject();
        stat_search.addProperty("COUNT_ALL", 80);
        stat_search.addProperty("COUNT_FAIL_COLLECT", 10);
        stat_search.addProperty("COUNT_FAIL_ANALYZE", 3);
        stat_search.addProperty("COUNT_READY_TAG", 20);
        stat_search.addProperty("COUNT_TAGGED", 53);
        result.add("COUNTS_SEARCH", stat_search);

        JsonArray list_items = new JsonArray();
        JsonObject n1 = new JsonObject();
        n1.addProperty("TITLE", "피나 3D");
        n1.addProperty("CID", "000001");
        n1.addProperty("TYPE", "OTH");
        n1.addProperty("CNT_TAG", 1);
        n1.addProperty("REGDATE", "2017-12-23");
        n1.addProperty("PROCDATE", "2017-12-24");
        n1.addProperty("STAT", "RT");
        list_items.add(n1);
        JsonObject n2 = new JsonObject();
        n2.addProperty("TITLE", "1724 기방난동사건");
        n2.addProperty("CID", "000036");
        n2.addProperty("TYPE", "KOR");
        n2.addProperty("CNT_TAG", 1);
        n2.addProperty("REGDATE", "2017-12-25");
        n2.addProperty("PROCDATE", "2017-12-26");
        n2.addProperty("STAT", "FC");
        list_items.add(n2);
        result.add("LIST_ITEMS", list_items);

        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }


    @Test
    public void test__pop_movie() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();
        result.addProperty("TITLE", "라이언 일병 구하기");
        result.addProperty("OTITLE", "Saving Private Ryan");
        result.addProperty("SERIESYN", "N");
        result.addProperty("YEAR", "1998-09-12");
        result.addProperty("DIRECTOR", "스티븐 스필버그");
        result.addProperty("ACTOR", "톰 행크스");
        result.addProperty("GENRE", "드라마");
        result.addProperty("PLOT", "1944년 6월 6일 노르망디 상륙 작전. 병사들은 죽을 고비를 넘기고 임무를 완수하지만, 실종된 유일한 생존자 막내 라이언 일병을 구하는 임무를 맡는다. 그들은 과연 라이언 일병 한 명의 생명이 그들 여덟 명의 생명보다 더 가치가 있는 것인지 혼란에 빠진다.");

        result_all.add("RESULT", result);
        System.out.println("RESULT:" + result_all.toString());
    }

    @Test
    public void test__pop_meta() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();
        result.addProperty("DURATION", "6m");

        JsonArray metaswhen = new JsonArray();
        JsonObject when1 = new JsonObject();
        when1.addProperty("word", "20세기");
        when1.addProperty("type", "new");
        when1.addProperty("ratio", 7.3);
        metaswhen.add(when1);
        JsonObject when2 = new JsonObject();
        when2.addProperty("word", "2차세계대전");
        when2.addProperty("type", "dup");
        when2.addProperty("ratio", 6.3);
        metaswhen.add(when2);
        result.add("METASWHEN", metaswhen);

        JsonArray metaswhere = new JsonArray();
        JsonObject where1 = new JsonObject();
        where1.addProperty("word", "노르망디");
        where1.addProperty("type", "new");
        where1.addProperty("ratio", 7.3);
        metaswhere.add(where1);
        result.add("METASWHERE", metaswhere);

        JsonArray metaswhat = new JsonArray();
        JsonObject what1 = new JsonObject();
        what1.addProperty("word", "전쟁");
        what1.addProperty("type", "dup");
        what1.addProperty("ratio", 7.3);
        metaswhat.add(what1);
        JsonObject what2 = new JsonObject();
        what2.addProperty("word", "실화");
        what2.addProperty("type", "new");
        what2.addProperty("ratio", 6.5);
        metaswhat.add(what2);
        result.add("METASWHAT", metaswhat);

        JsonArray metaswho = new JsonArray();
        JsonObject who1 = new JsonObject();
        who1.addProperty("word", "병사");
        who1.addProperty("type", "new");
        who1.addProperty("ratio", 6.5);
        metaswho.add(who1);
        result.add("METASWHO", metaswho);

        JsonArray metasemotion = new JsonArray();
        JsonObject emotion1 = new JsonObject();
        emotion1.addProperty("word", "감동적인");
        emotion1.addProperty("type", "new");
        emotion1.addProperty("ratio", 6.7);
        metasemotion.add(emotion1);
        result.add("METASEMOTION", metasemotion);

        JsonArray list_not_mapped = new JsonArray();
        JsonObject not1 = new JsonObject();
        not1.addProperty("word", "상륙작전");
        not1.addProperty("type", "new");
        not1.addProperty("ratio", 3.7);
        list_not_mapped.add(not1);
        JsonObject not2 = new JsonObject();
        not2.addProperty("word", "공수부대");
        not2.addProperty("type", "new");
        not2.addProperty("ratio", 3.2);
        list_not_mapped.add(not2);
        result.add("LIST_NOT_MAPPED", list_not_mapped);

        JsonArray words_genre = new JsonArray();
        words_genre.add("감동적인");
        words_genre.add("극적인");
        result.add("WORDS_GENRE", words_genre);

        JsonArray words_sns = new JsonArray();
        words_sns.add("압권");
        words_sns.add("명작");
        words_sns.add("강추");
        result.add("WORDS_SNS", words_sns);

        JsonArray words_assoc = new JsonArray();
        words_assoc.add("감명적");
        result.add("WORDS_ASSOC", words_assoc);

        JsonArray list_subgenre = new JsonArray();
        list_subgenre.add("전쟁드라마");
        result.add("LIST_SUBGENRE", list_subgenre);

        JsonArray list_searchkeywords = new JsonArray();
        list_searchkeywords.add("노르망디");
        list_searchkeywords.add("2차세계대전");
        list_searchkeywords.add("전쟁");
        result.add("LIST_SEARCHKEYWORDS", list_searchkeywords);

        JsonArray list_reco_target = new JsonArray();
        list_reco_target.add("다시 군대가는 꿈을 꾼 당신에게");
        result.add("LIST_RECO_TARGET", list_reco_target);

        JsonArray list_reco_situation = new JsonArray();
        result.add("LIST_RECO_SITUATION", list_reco_situation);

        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }


    @Test
    public void test__pop_award() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();
        result.addProperty("AWARD", "\"<dl class=\\\"list_produce\\\"> \\n <dt>\\n  수상\\n </dt> \\n <dd> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=2611\\\" class=\\\"link_person #click\\\">스티븐 스필버그</a> (감독상) </span> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=1916\\\" class=\\\"link_person #click\\\">야누즈 카민스키</a> (촬영상) </span> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">음향상</span> </span> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">편집상</span> </span> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">음향편집상</span> </span> \\n </dd> \\n</dl>\\n<dl class=\\\"list_produce\\\"> \\n <dt>\\n  후보\\n </dt> \\n <dd> \\n  <span class=\\\"info_person\\\"> <span class=\\\"txt_award\\\">작품상</span> </span> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=516\\\" class=\\\"link_person #click\\\">톰 행크스</a> (남우주연상) </span> \\n  <span class=\\\"info_person\\\"> <a href=\\\"/person/main?personId=25095\\\" class=\\\"link_person #click\\\">로버트 로뎃</a> (각본상) </span> \\n </dd> \\n</dl>\"");
        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }


    @Test
    public void test__pop_cine21() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();

        JsonArray words_cine21_arr = new JsonArray();
        words_cine21_arr.add("노르망디");
        words_cine21_arr.add("병사");
        words_cine21_arr.add("일병");
        words_cine21_arr.add("임무");
        words_cine21_arr.add("2차시계대전");
        result.add("WQRDS_CINE21", words_cine21_arr);

        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }


    @Test
    public void test__pop_c_cube() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();
        result.addProperty("SERIES_ID", "");
        result.addProperty("SERIES_NM", "");
        result.addProperty("MASTER_CONTENT_ID", "1002442168");
        result.addProperty("CONTENT_ID", "10024421680001");
        result.addProperty("PURITY_TITLE", "소림배구");
        result.addProperty("CONTENT_TITLE", "소림 배구단");
        result.addProperty("ENG_TITLE", "Beach Spike!");
        result.addProperty("TITLE_BRIEF", "소림배구");
        result.addProperty("DIRECTOR", "토니 탕");
        result.addProperty("YEAR", "2011");
        result.addProperty("ACTORS_DISPLAY", "주수나,부영");
        result.addProperty("COUNTRY_OF_ORIGIN", "HKG");
        result.addProperty("SAD_CTGRY_NM", "영화 > 외국영화");
        result.addProperty("DOMESTIC_RELEASE_DATE", "2015-04-09");
        result.addProperty("KT_RATING", "15세 이상");
        result.addProperty("KMRB_ID", "20156354");

        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }




    @Test
    public void test__dic_list() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();
        result.addProperty("MAXPAGE", 5);
        result.addProperty("TYPE", "WHEN");
        result.addProperty("PAGESIZE", 500);
        result.addProperty("PAGENO", 1);

        JsonArray list_paging = new JsonArray();
        list_paging.add(1);
        list_paging.add(2);
        list_paging.add(3);
        list_paging.add(4);
        list_paging.add(5);
        result.add("LIST_PAGING", list_paging);

        JsonArray list_words = new JsonArray();
        list_words.add("아카데미");
        list_words.add("21세기");
        list_words.add("실화");
        list_words.add("전우애");
        result.add("LIST_WORDS", list_words);

        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }



    @Test
    public void test__social() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();

        JsonArray words_instagram = new JsonArray();
        words_instagram.add("타투");
        words_instagram.add("해피버스데이");
        words_instagram.add("스케치");
        result.add("WORDS_INSTAGRAM", words_instagram);

        JsonObject graph_instagram = new JsonObject();
        JsonArray captions = new JsonArray();
        captions.add("D-5"); captions.add("D-4"); captions.add("D-3"); captions.add("D-2"); captions.add("D-1");
        graph_instagram.add("CAPTIONS", captions);
        JsonArray item1 = new JsonArray();
        item1.add(1);  item1.add(2); item1.add(1); item1.add(1); item1.add(1);
        graph_instagram.add("ITEM01", item1);
        JsonArray item2 = new JsonArray();
        item2.add(2);  item2.add(3); item2.add(2); item2.add(2); item2.add(2);
        graph_instagram.add("ITEM02", item2);
        JsonArray item3 = new JsonArray();
        item3.add(3);  item3.add(1); item3.add(3); item3.add(3); item3.add(3);
        graph_instagram.add("ITEM03", item3);
        result.add("GRAPH_INSTAGRAM", graph_instagram);

        JsonArray words_twitter = new JsonArray();
        words_twitter.add("7호실");
        words_twitter.add("교환");
        words_twitter.add("토르");
        result.add("WORDS_TWITTER", words_twitter);

        result.add("GRAPH_TWITTER", graph_instagram);

        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }



    @Test
    public void test__stat_list() {
        JsonObject result_all = new JsonObject();

        result_all.addProperty("RT_CODE", 1);
        result_all.addProperty("RT_MSG", "SUCCESS");

        JsonObject result = new JsonObject();
        result.addProperty("PAGESIZE", 20);
        result.addProperty("MAXPAGE", 6);
        result.addProperty("PAGENO", 1);

        JsonArray list_paging = new JsonArray();
        list_paging.add(1);
        list_paging.add(2);
        list_paging.add(3);
        list_paging.add(4);
        list_paging.add(5);
        result.add("LIST_PAGING", list_paging);

        result.addProperty("SEARCHSTAT", "ALL");
        result.addProperty("SEARCHSDATE", "");
        result.addProperty("SEARCHEDATE", "");


        JsonObject stat_search = new JsonObject();
        stat_search.addProperty("COUNT_IN", 80);
        stat_search.addProperty("COUNT_SC", 70);
        stat_search.addProperty("COUNT_FC", 10);
        stat_search.addProperty("COUNT_SA", 65);
        stat_search.addProperty("COUNT_FA", 5);
        stat_search.addProperty("COUNT_ST", 62);
        stat_search.addProperty("COUNT_RT", 2);
        stat_search.addProperty("COUNT_FT", 1);
        result.add("COUNTS_STAT", stat_search);

        JsonArray list_items = new JsonArray();
        JsonObject n1 = new JsonObject();
        n1.addProperty("TITLE", "피나 3D");
        n1.addProperty("CID", "000001");
        n1.addProperty("TYPE", "OTH");
        n1.addProperty("CNT_TAG", 1);
        n1.addProperty("REGDATE", "2017-12-23");
        n1.addProperty("PROCDATE", "2017-12-24");
        n1.addProperty("STAT", "RT");
        n1.addProperty("CNT_IN", 1);
        n1.addProperty("CNT_COL", 1);
        n1.addProperty("CNT_ANA", 1);
        n1.addProperty("CNT_TAG2", 1);
        list_items.add(n1);
        JsonObject n2 = new JsonObject();
        n2.addProperty("TITLE", "1724 기방난동사건");
        n2.addProperty("CID", "000036");
        n2.addProperty("TYPE", "KOR");
        n2.addProperty("CNT_TAG", 0);
        n2.addProperty("REGDATE", "2017-12-25");
        n2.addProperty("PROCDATE", "2017-12-26");
        n2.addProperty("STAT", "FC");
        n2.addProperty("CNT_IN", 1);
        n2.addProperty("CNT_COL", 1);
        n2.addProperty("CNT_ANA", 0);
        n2.addProperty("CNT_TAG2", 0);
        list_items.add(n2);
        result.add("LIST_ITEMS", list_items);

        result_all.add("RESULT", result);

        System.out.println("RESULT:" + result_all.toString());
    }

    @Test
    public void test_getParsingStr() {
        String a = "<frame src=\"/_blog/sss&amp;\">test</frame>";
        String b = a.substring(a.indexOf("<frame src")+12, a.indexOf("</frame>"));
        b = b.substring(0, b.indexOf("\">"));
        b = b.replace("&amp;", "&");

        System.out.println("#a:"+b);

        //System.out.println("#b:"+b);
    }
    /*

    @Test
    public void test_regexp() {
        String req = "어렵고 힘들게 입을 엽니다 \uDB82\uDC52주님의 111 abc*";

        String result = "";

        //result = req.replaceAll("^[가-힣]*$}", "");
        //result = result.replaceAll("[[:]\\\\/?[*]]", "");
        result = req.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
        System.out.println("#result:"+result);

    }
    */

    /*
    @Test
    public void test_jsonStr() {
        String str = "{\"resultArr\":[{\"sub_url\":\"http://victoryshadow.tistory.com/5\",\"contents\":[{\"blog_tistory2\":\"영화 굿바이 싱글 내용 줄거리 결말 김혜수 마동석  싱글이여 안녕 영화 굿바이 싱글입니다 이번에 엄청난 예매율을 자랑하는 영화가 있다고 해서 바로 극장에 달려가서 영화를 보았어요 이 영화 같은 경우에는 김혜수씨가 주연으로 나오면서 정말 인기리에 상영중이라고 하는데요   요즘 한국 영화 같은 경우에는 여성배우가 설자리가 없다고 해요 그러다 보니까 여성 배우가 주연으로 나오는 영화 또한 많이 없는데요   이번에 이렇게 김혜수씨가 주연으로 나오는 영화를 볼 수 있어서 얼마나 좋았는지 몰라요 내용도 생각보다 굉장히 신박하더라고요 그래서 정말 재미있게 보았답니다   이 영화 같은 경우에는 김혜수씨가 다 지난 여배우로 나와요 하지만 그녀는 정작 이렇게 오랜세월 힘들게 일을 했지만 자기 편 하나 없다는 것에 정말 많이 힘들어하고 어느날 아이만큼은 자신의 편이 되줄거라고 생각하고 가짜로 임신한척을 하죠    그녀 뒤에는 중학생인데 임신을 한 아이가 있고 말이에요 그 아이는 아이를 낳고 김혜수에게 주는 목적으로 김혜수씨는 임신했다는 것으로 새롭게 다시 인기를 얻는 목적으로 아이를 낳을 동안 같이 살기로 하죠   존칭을 써가며 살지만 이후에는 서로 친해지면서 가까워지게 되는데요 김혜수씨가 점차 바빠지게 되니 멀어지게 되고 나중에는 결국에 아이 임신이 가짜였다는 사실도 밝혀지게 된답니다    그러다가 마지막에는 김혜수씨가 자신의 편이 존재하지 않는것이 아니라 자기 자신이 문제라는 것을 알게 되죠 그러면서 해피앤딩 많이 배울 수 있는 영화였고 또 힐링이 되는 영화였답니다 무엇보다 코미디적인 요소와 감동코드가 적절히 배합된듯해서 정말 좋았어요 공감sns신고 저작자표시비영리변경금지 영화 카테고리의 다른 글 영화 스타워즈 에피소드2 클론의 습격 줄거리 스포 내용0 20160725 한지민 정재영 영화 플랜맨 줄거리 결말 내용 느낀점0 20160721 김민준 영화 웨딩스캔들 줄거리 내용 결말0 20160720 려원 봉태규 영화 두얼굴의여친 줄거리 결말 내용0 20160719 영화 굿바이 싱글 내용 줄거리 결말 김혜수 마동석0 20160718 트랜스포머2 패자의 역습 영화 줄거리 내용 감상문0 20160715\"}],\"dest_fields\":[\"blog_tistory2\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=totoro2010&logNo=100105646813\",\"contents\":[{\"blog_post_view\":\" 굿 바이 Good  Bye   사랑한다는 말보다 아름다운 인사 굿 바이 도쿄에서 잘나가는 오케스트라 첼리스트인 다이고모토키 마사히로 1억이 넘는 돈을 대출해서 장만한 첼로로 열심히 연주해보지만 관객도 별로 없는 공연장 인기도 없고 유지하기도 힘들고 그렇게된 형편으로인해서 악단이 해체되고 갑작스럽게 백수 신세가되어버린다 아내 미카히로스에 료코과 함께 어머니가 남겨주신 유일한 재산인 집으로 이사를 가게된다이사온 집엔 어머니와 아버지의 흔적이 가득하다   일자리를 찾던 그는 우연히 연령무관 고수익 보장이라는 파격적인 조건의 여행 가이드 구인광고를 발견하고 두근두근 면접을 보러 간다 면접은 1분도 안되는 초스피드로 진행되고 바로 합격한 다이고 그러나 여행사인줄만 알았던 회사는 인생의 마지막 여행을 떠나는 사람들을 배웅하는 납관일을 하는 곳 도저히 할 수 없을 것같은 다이고는 갈등을 하는데 마침 이쿠에이야마자키 츠토무가 내미는 돈뭉치에 마음이 흔들려 버리고 하루 아침에 화려한 첼리스트에서 초보 납관도우미가 된 다이고  7살때 아버지가 다른 여자와 떠나버리게 되면서 아버지에 대한 미움과 원망을 가지고 살았던 자신의 모습을 떠올리는 다이고 어릴적 자신에게 처음 첼로를 안겨준 아버지의 모습을 떠올려보지만 아버지의 얼굴을 흐릿하게 떠오르지 않고 옛적에 쓰던 작은 첼로를 열어보는데 그 곳엔 아버지가 건내준 큰 돌멩이가 신문에 쌓여 함께 넣어져있다 그 돌멩이는 멀리 있는 사람에게 자신의 소식을 전하기 위해 돌멩이를 주고받으며 편지형식으로 서로 전했다는 이야기를 하며 아버지와 함께 나누었던 것이다 그때의 아버지의 얼굴은 아마도 기억하고 싶지 않은 이유로 지웠을 것이라 생각되어진다  언어가 없던 시절 사람들은 돌을 통해 자신의 마음을 상대방에게 전했다고 해  마음이 편안하면 부드러운 돌을 무슨 일이 있으면 울퉁불퉁한 돌을 주는 것이다  돌을 받은 사람은 부드러운 돌이면 안심하고 거친 돌이면 걱정을 했다고해  다이고 아버지  그리고 보면 다이고에게 건내주었던 돌은 울퉁불퉁한 돌이었고 다이고가 아버지에게 건낸 돌은 작고 둥글둥글한 돌이였다 모든 것이 낯설고 거북하지만 차츰 베테랑 납관사 이쿠에이가 정성스럽게 고인의 마지막을 배웅하는 모습에 찡한 감동을 배워간다 첫번째 일은 자살한 사람의 시신을 배웅해야하는일 첫 일인데 너무나 지독한 일이 걸려 구토에 시달리는 다이고   다음엔 극장으로 가라는 연락을 받고 달려단 다이고는 홍보 영상을 찍게되고 주인공으로 누워있는 시신 역할을 담당하게된다 이왕 시작한 일 열심히는 해보려고 하지만 시신역할이란 영 나중에 문제가 되지 않을까 걱정을 하지만 그를 안심시키는 이쿠에이 홍보영상물이기때문에 아무나 볼수 있는 것이 아니라고 그들 달래고 촬영에 들어간다   그리고 계속되어지는 일들을 통해 다이고는 납관이라는 일이 너무나 필요하고 신성한 것이라는 것을 깨닫게된다 그리고 오랫만에 돌아온 고향에선 다이고를 알아보는 사람들이 한둘씩 나타나고 그 중 오래된 목욕탕에서 그를 알아보는 친구의 어머니를 만나게된다 자신의 아버지가 집을 나갔을때도 사람들 앞에서는 울지않고 목욕탕 한구석에서 슬피 울던 아이였다며 잘해주라며 미카에게 당부한다   두번째 일로 가게된 곳에서 5분이 늦었다는 이유로 유가족들에게 쓴소리를 듣게되면서 시작된다 하지만 떠나는 사람을 배웅하는 모습을 보던 유가족들은 나중에 감사함을 표현하게된다  오늘 아내는 지금까지 제가 본 중에 가장 예뻤습니다 고맙습니다  그 일로 더 이일을 사랑하기 시작하게된 다이고 하지만 아내 미카히로스에 료코와 친구들은 다이고에게 당장 일을 그만두라고 반대를 하게된다 하지만 시간이 흐르면서 일에 대한매력을 느끼게 되면서그만두지않겠다고 말을 한다 그 일로 인해서 미카는 친정을 떠나버리고 홀로남겨진 다이고는 열심히 일을 하며 지내게 된다 그러던 어느날 부인이 친정으로 갔다는 이야기를 듣고 이쿠에이와 함께 식사를 하게된 다이고  맛있단 말이지 미안스럽게도  아마도 먼저 떠난자들에 대해 남겨진자들이 살아가며 느끼는 좋은것들에 대해 미안한 마음을 이렇게 표현한것은 아닐까하는 생각이 든다   나름대로 뿌듯함을 가지고 열심히 일하는 사이 미카는 다시 다이고의 곁으로 돌아온다 이유는 임신 다시 그 일을 그만 두라고 설득을 하게되는데 그날 갑자기 자주 들리던 단골 목욕탕의 주인 아주머니의 죽음 소식이 들리고 그 곳으로 가게된 다이고와 미카 다이고의 배웅모습을 지켜보며 감동을 받은 미카는 더이상 그를 말리지 않게된다  죽음은 문이야 문을 열고나가면 다음세상으로 가는거지 그래서 죽음은 문이라고 생각해   그리고 며칠이 지난 어느날 아버지에 사망 소식을 통보받게되는 다이고 아버지에 대한 원망으로 인해 절대로 가지 않겠다고 하지만 미카는 그래도 마지막 모습을 뵈야한다며 설득하려한다 갈등 속에 결국 다이고는 미카와 함께 아버지의 시신이 있는 바닷가 어느곳으로 찾아가게된다 그리고 아버지의 시신앞에서 아무렇지 않게 앉아 있는데 상조회사 직원들이 들어와 아버지의 시신을 마구잡이 식으로 나무 관에 넣는 모습에 그만 화가나서 자신이 하겠다는 말을 한다 그리고 직접 자신의 손으로 아버지의 시신을 배웅하게된다 아버지의 몸을 씻기던 중 아버지에 손을 펴는데 무엇인가 쥐고 있는 것인지 잘 펴지지 않고 결국 펴진 손안엔 자신이 7살때 아버지와 주고 받은 작은 돌멩이가 쥐어져 있음을 알게된다 눈물을 흘리며 아버지의 몸을 닦고 결국 아버지가 자신을 깊이 사랑하고 있었다는 것을 깨닫게 되는 다이고    이렇게 영화는 막을 내린다 영화는 잔잔하고 소박한 느낌을 주는 형식으로 그려져있다 그 안에서 너무나 평범하게 일어나는 것들을 마지막 떠나는 여행으로 비유하며 아름다운 마지막을 선사하고 있다  우리나라에서는 시신을 함부로 다루는 경향이 있다고 한다 벌거벗겨 마구잡이 식으로 닦는 무례한 행동들이 많은 가족이 있는 앞에서 행해진다고 들었는데 이 영화속에서는 돌아가신 분이지만 너무나 아름답게 마무리 하도록 도와주는 장면들이 나오고 있다 그리고 떠나는 자와 남겨지는 자들의 마지막 정리할 수 잇는 그리고 추억할 수 있는 시간들을 준다는게 참 아름답다고 여겨졌다 또한 직업에 대한 시각도 변해야 된다는 뻔한 생각을 해보지만 역시나편견에서 벗어나기란 힘들다는 결론이다\"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=blueyoung1203&logNo=220753020348\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 176 목록열기 스크랩 엮인글 영이의영화 영화굿바이싱글후기 김혜수마동석케미 굿바이싱글보고왔어요줄거리 2016 7 4 1141 URL 복사 본문 기타 기능 번역보기 영화굿바이싱글 후기줄거리 요즘 핫한 영화 영화굿바이싱글 보고왔어요 영화굿바이싱글 보면서 역시 김혜수 이 말이 절로 나오더라구요 김혜수 몸매도 얼굴도 연기도 여전했다 명품 그자체 솔직히 제가 김혜수 팬이어서 그러는게 아니라 굿바이싱글 김혜수가 아닌 다른 여배우가 주인공이었다면 결코 재밌지 않았을 것 같다는 생각이 들더라구요 김혜수의 유쾌통쾌한 코믹연기 덕분에 간만에 유쾌하고 즐겁게 영화를 본 것 같아요 개인적으로 CGV보다 메가박스가 더 좋은 일인 이날도 어김없이 동탄메가박스에서 굿바이싱글을 봤어요 동탄메가박스에서는 할인관람권을 매번 주는데좀 더 저렴하게 영화를 볼 수 있어 좋아요 동탄메가박스1주년기념이벤트 콤보3천원할인권 두개나 나왔어요 영화굿바이싱글을 보면서저녁시간이니 출출하니깐 팝콘보다는 동탄메가박스 바로 앞에 있는 핫도그집에서 핫도그를 사다가 먹었어요  불갈비핫도그  바베큐치킨핫도그  음료2개 가격 12000원  다 먹고나면 입이 심심할까봐 커널스팝콘도 샀어요 말도안돼 바가지다 커널스팝콘 가격 3000원 앞으로는 핫도그만 먹기로 핫도그는 영화시작전에 폭풍흡입 핫도그가 내용물이 막 떨어지거든요 잘먹어야해요 영화시작하면 깜깜하니깐 조금이나마 밝을때 편안하게 먹으면 좋잖아요 배도 고프니 아 얼마나 맛있던지 배고플때 먹으면 모든지 다 맛있는 것 같아요 영화굿바이싱글 줄거리 대한민국 대표 독거스타의 임신 스캔들이번엔 제대로 사고쳤다 온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받고 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데 대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지고 주연김혜수의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분서주 하는데 통제불능 여배우 그녀의 무모한 계획은 계속 될까 영화굿바이싱글 선 웃음 후 감동 영화굿바이싱글 후기 개인적으로 영화굿바이싱글 간만에 유쾌하고 즐겁게 본 영화였어요 굿바이싱글은 후반부에 살짝 루즈해지는 부분이 있었긴하지만 적당한 코미디와 부담스럽지 않은 메시지까지 포함되어있는 영화랍니다 김혜수와 마동석의 환상의 캐미가 돋보였던 영화 김혜수 마동석 연기 최고였어요 마동석은 이 영화에서 감칠맛을 더해주는 배우였어요 또한아역배우 김현수의 재발견 대박 더불어 김혜수의 여전한 미모와 몸매에 빠져들며 본 나 너무이쁘닷 이런 몸매로 하루만이라도 살아보고싶네요 굿바이싱글 본 뒤로 집에와서 먹고싶은 생각이 사라졌어여 여자아이는 임신으로 떳떳하지 못하고 꿈까지 내려놓았는데 잘못은 둘이했는데 왜 그 남자아이는떳떳하게 국가대표로 발탁되서 외국에 나갔다 라는 김혜수의 속시원한 대사가 생각이 나네요 후반부에 눈물이 고이더라구요 두 사람의 책임인데 왜 우리사회에서는 여자만 고개를 숙여야하고 따가운 눈총을 맞고 손가락질을 받아야하는지 영화굿바이싱글은 김혜수 민낯 막춤 등 역대급 코믹연기도 볼 수 있고 감동도 받는 그런 영화더라구요 임산부들 남자들보다는 여자들이 보면 좋은 영화일 것 같아요 7월엔 웃어라 저는 부모님도 보시라고 따로 예매해드렸거든요 아빠  너 마지막에 안울었어 슬퍼서 아빠는 울었다 아가씨보다 5배는 훨씬 재밌더라 아빠의 말에 공감 여러분들도 오랜만에 재밌고 따뜻한 메시지 담겨있는 영화보러 극장나들이 다녀오세요  어바웃영이의 포스팅은 제 돈주고 관람하고 제 개인적인 의견으로 작성한 후기입니다  태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 인쇄 댓글 151 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=jin7daks&logNo=220742769916\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 805 목록열기 스크랩 엮인글 영화 보고싶어요 영화기대를 한몸에 받고 있는 굿바이 싱글 줄거리 김혜수 코믹연기 2016 6 22 1005 URL 복사 본문 기타 기능 번역보기 안녕하세요 좋은아침입니다 에구 벌써 일주일의 반이라는 시간이 흘렀네요 시간이 왜이렇게 점점 빨리흐르는지 아무튼 이런 우울한 잡생각 떨쳐줄 재밌는 영화 오늘은 가져와봤는데요 바로 김혜수 마동석 주연의 굿바이 싱글 줄거리 정보 가져왔답니다 굿바이 싱글 감독 김태곤 출연 김혜수 마동석 김현수 개봉 2016 대한민국 상세보기 오는 6월29일 개봉을 앞둔 따끈따끈한 예정작인데요 TV에서 나오는 예고편만 봤는데 빵빵터지고 너무 기대가 되더라구요 조연에는 또 오해영으로 인기를 끌고있는 서현진과 우리의 아버지 김용건 그리고 당찬 아역 김현수까지 출연해서 관심을 한몸에 받고 있답니다 김혜수  저 예쁜 얼굴이랑 몸매로 드레스차려입고 집에서 아이스크림 퍼먹는 이 스틸컷이 왜이리 와닿던지 한물 간 스타가 뜨기위해 벌리는 해프닝을 그린 굿바이 싱글 줄거리 한번 알아볼까요  굿바이 싱글 줄거리  대한민국 대표 독거스타의 임신 스캔들 이번엔 제대로 사고쳤다 라면거 굿바이 싱글 줄거리 시작되는데요 한번 가 봅시다 굿바이 싱글 줄거리 온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받게 됩니다 굿바이 싱글 줄거리 그런 주연은 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데 대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지게 되고 주연의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분거주 하게 됩니다 통제불능 여배우 그녀의 무모한 계획은 계속 될까 라면서 굿바이 싱글 줄거리 끝이 납니다  톡톡튀는 연기와 코믹한 면으로 똘똘뭉쳤을 거 같은 이번 김혜수의 연기 평구 라는 이름으로 등장하는  마동석과 함께 재기발랄한 캐미를 터트릴거 같습니다 이러니 개봉전부터 난리가 났죠 당찬 연기를 보여줄거같은 김현수도 기대가 되고 서현진의 모습도 궁금해지니 이거이거 언제29일까지 기다릴까요 굿바이 싱글 줄거리 보니 궁금해서 참을수가 없습니다 아무튼 정말 재밌고 힘든일상 시원하게 풀어줄만한 영화라는것은 확실하니 가서 시원하게 웃고 오는것도 좋겠네요 그럼 오늘 준비한 영화소식 굿바이 싱글 줄거리 포스팅은 여기까지구요 행복한 수요일 되시길 그럼 예고편 두고 가겠습니당 굿바이 싱글 2차 예고편 태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 인쇄 댓글 1 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://reviewisgood.tistory.com/37\",\"contents\":[{\"blog_tistory2\":\"영화줄거리를 결말까지 초간단 요약합니다 결말 스포가 있으니 주의하세요 김혜수가 임산부가 된다는 영화포스터만으로 화제가 되고 있는 영화 160629에 개봉예정이고 현재 시사회를 진행했는데 대박이라는 평가까지는 없지만 별로라는 평도 그다지 보이지 않아 대체로 무난하다는 평가 초중반 코믹 후반 감동이 법칙이다시피한 한국 코미디영화의 공식에서 그다지 벗어나지 않는다는 점은 아쉽지만 주연들의 호연과 김혜수의 대체불가한 연기는 강점  줄거리를 소개하면 영화상의 김혜수도 연예인 그러나 영화상에서는 현재 나이가 들면서 인기는 떨어지고 찌라시나 스캔들 등에서나 볼 수 있는 배우가 된 상황 김혜수의 매니저 마동석은 이런 김혜수의 뒷치닥거리를 하느라 매번 고생한다 서현진은 마동석과 부부로 나오고 3아이의 엄마로 나옴 그렇게 장면이 많이 나오진 않음 이후 연하의 남친 곽시양과 결혼까지 생각했으나 곽시양이 양다리를 핀 것에 충격을 먹고 자신의편이 되줄아이를 결혼을 하지 않고 만들고자 한다 그런데 입양이나 임신이나 김혜수에게는 버거운 것이어서 고민하던 중 마침 임신한 여중생을 만나게 됨 여중생은 아버지도 외면해버려서 낙태를 생각하고 있었음 그래서 김혜수는 그 중학생에게 거액의 돈을 주는 대신 애가 나오면 김혜수의 아이가 되는 것으로 하기로 함 그래서 김혜수는 임신한 것처럼 연기를 하고 대중들에게도 임신한 것을 발표함 미혼스타였던 김혜수의 임신발표는 세간을 강타하는 핫소식이 되고 그로 인해 왕년의 스타였던 김혜수는 다시 인기를 한몸에 받는다 그리고 임신한 중학생은 김혜수와 같이 사는데 각자 삶의 어려움을 가지고 있던 둘은 점차 어려움을 서로 의지하는 사이가 된다 그러다가 김혜수가 임신한 것이 거짓임이 드러나고 코믹일색이었던 초중반부와 달리 슬픔을 자아내는 이야기들이 나온다 그리고 조금은 뻔하지만 뻔하지 않은 결말로 새로운 가족이탄생하는것으로 영화는 끝난다 영화 굿바이 싱글 등장인물흥행 간략분석은 링크클릭 httpreviewisgoodtistorycom50  읽으셨던 포스팅이 유익하셨으면 아래 공감버튼 눌러주시면 감사하겠습니다 로그인 필요없어요  공감sns신고 저작자표시비영리변경금지 영화 리뷰 카테고리의 다른 글 인디펜던스 데이 리써전스줄거리 간략요약결말스포O0 20160629 영화 비밀은 없다 줄거리 간략 요약결말스포O1 20160628 영화 굿바이 싱글 줄거리 간략요약결말스포 조금0 20160625 영화 동갑내기 과외하기 줄거리 간략 요약결말스포O0 20160623 영화 정글북 줄거리 간략 요약결말스포O0 20160620 특별수사 사형수의 편지 줄거리 간략 요약결말스포O0 20160618\"}],\"dest_fields\":[\"blog_tistory2\"]},{\"sub_url\":\"http://shoppingschool.tistory.com/123\",\"contents\":[{\"blog_tistory2\":\"굿바이싱글 결말  손익분기점  줄거리  미혼모 영화  오늘 정보는 영화 리뷰입니다 리뷰 영화는얼마 전에 보게 된 굿바이 싱글이라는 김혜수씨가 오랜만에 주연을 맡은 영화입니다 결론적으로 말씀드리면 필자는 꽤나 재미있게 봤습니다 특히 임산부 역할을 능청 맞게 연기하는 김혜수씨와 신스틸러로 활약하고 있는 마동석의 연기가 매우 인상 깊었습니다 혼자 봐도 꿀잼이고 여친과 봐도 재미있는 영화라 생각이 됩니다 개방은 6월 말에 했으며 극장에서는 이제 거의 스크린이 내려간 상태입니다 관람객 평점을 보시면 아시겠지만 전체적으로 누가봐도 재미있는 수준의 영화입니다 러닝타임은 2시간으로 짱짱하며대략 210만 정도입니다 굿바이 싱글의 손익분기점은 150만명입니다 비수기 시즌에 나와서 굉장히 선방했다고 볼 수 있습니다 요즘 나오는 영화의 경우 100만 넘기도 힘든 것이 사실입니다 제작진과 배우는 연기력을 논하기에 그 수준이 매우 높다는 것이 특징입니다 특히 김태곤 감독의 경우 족구왕으로 연출력을 인정받은 분이지요 김혜수와 마동석은 말할 필요도 없지요 미혼모로 나온 김현수의 연기도 나무랄 때가 없습니다 필자는 모르고 봤는데 또 오해영의 히어로인 서현진씨도 출연을 합니다 영화의 재미는 마동석과 김혜수의 티격태격하는 자잘한 상황과 개그에 있습니다 정말 마동석은 못하는 배역이 없는 것 같고 특히어찌 저 덩치로 저렇게 귀여울 수 있을까 하는 의구심마저 들게 합니다 앞으로 12년 이내에 마동석 단독 주연의 영화가 나올 것이라 확신합니다 이틀 전에 부산행을 봤는데 역시나  마블리는 기대를 져버리지 않았습니다 완전 씬을 씹어 먹는 몸과 연기를 보여줍니다 이 영화는 사실 이야기보다 배우들이 이끈 영화입니다 특히 극중감정기복이 심하고 통제불능인 톱배우 역할을 김혜수씨가 완벽하게 연기로 소화했습니다 실제인지 연기인지 헷갈릴 정도로 연기를 잘 했습니다 또한 우월한 몸매는 영화에 더욱 몰입하게 만듭니다  스포일러 내용 있음  굿바이 싱글 시나리오는 믿었던 남친이 바람을 피면서 세상에 자기 것이 없다고 한탄하던 그녀가세상에서 온전하게 자신의 것인 자식을 갖기로 하면서이를 실천하기 위해 병원을 찾게 됩니다 그러나  매우안타깝게도 그녀는 이미폐경이 와서임신이 불가능한 상태라는 사실을 알게 됩니다 이때 같은 병원에 온 고등학생 미혼모인 단지를 알게 되고 이 상황을 간댕이가 배 밖으로 나온 희대의 사기극인 가짜 임신을 계확히기 시작합니다 잘 진행되던 계획은 전 남친의 방문으로 탄로나면서 결국 수포로 돌아가고 당당한 싱글맘으로 대중을 마음을 훔쳤던 그녀는결국 전재산을 잃고바닥으로 추락합니다 그러나 다 잃을 것으로 생각했던 그녀에게 또 다른 자신의 것들이 생겨나는 것을 마지막으로 영화는 끝이 납니다 굿바이 싱글 평점입니다 대부분 잔잔한 감동과 소소한 웃음이 있는 영화라는 평이 많네요 필자 역시도이에 동감합니다 특히 마지막 장면에서 단지가 미혼모라는 이유로 차별하고 비하하는사람들의 날 선 목소리에 맞서 당당하게내뱉은김혜수의 대사가 굉장히 가슴에 와닿는 부분입니다 결론적으로 굿바이 싱글은 교훈감동유머 3박자를 골고루 갖춘 영화라 평할 수 있겠습니다 아직 보지 않으셨다면 꼭 보시기를 권합니다 sns신고 영화만화 카테고리의 다른 글 윤태호 웹툰  역사웹툰  스릴러웹툰  명작웹툰0 20160726 추천 만화책  만화 자칼  천추작가  암살 만화0 20160722 굿바이싱글 결말  손익분기점  줄거리  미혼모 영화0 20160722 사냥 결말  사냥해석  손익분기점  줄거리  안성기 정체3 20160721 외국 재난영화  조난영화  태풍영화 지진영화0 20160719\"}],\"dest_fields\":[\"blog_tistory2\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=iamthgml&logNo=220749415048\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 414 목록열기 스크랩 엮인글 영화 영화굿바이싱글김혜수마동석줄거리후기스포 2016 6 29 2213 URL 복사 본문 기타 기능 번역보기 스크랩 하실 때에는 댓글비밀댓글 달아주세요 스포주의줄거리 결말 있음 2016년 6월 29일 굿바이 싱글 보고 왔습니다 사냥 볼까 하다가 오늘은 왠지 보면서 웃을 수 있는 영화 보고 싶어서 굿바이싱글 선택 간단한 줄거리는 톱스타 고주연 맨날 스캔들을 달고 사는 연예인인데요 어린 배우랑 사귀다가 이 나쁜놈이 바람핌아오 그래서 큰 충격을 받고 영원히 내 편이 되어줄 사람을 찾다가 애를 만들겠다고 뜬금없엌 하지만 이 언니는 애를 만들 수 없어서 우연히 만난 단지의 애를 자기가 키우겠다고 얘기합니다 고아인 단지는 중학생이고 임신을 했죠 애를 지우려고 병원에 온 단지를 우연히 만난거죠 그렇게 자기 집에 단지를 데려다 놓고 열심히 태교합니다 그렇게 서로에게 정도 들죠 단지는 주연이에게 바람핀 전남친을 엿맥이라고 해서 아빠가 누군지는 모르겠지만 저격하는 투로 임신했다고 발표 임신했다고 밝히고 뜻밖에도 이미지는 좋아졌고 CF도 엄청 많이 들어오고 하지만 일을 하느라 단지를 못챙기죠 그렇게 시간은 흐르고 고아인 단지에게 아주 못된 언니가 있는데 언니가 이 사실을 알게돼서 언니가 주연이 돈을 뜯으려고 합니다  돈 안주면 신고할거라고 왜냐면 아기를 사는거니까요 뭐 이렇게저렇게 소송은 막지만 주연이는 임신했다고 거짓말 한 것을 들킵니다 그렇게 이미지는 완전 바닥으로 떨어지고 더 이상 배우생활을 할 수 없을 정도로 저거 거짓말이라는거밝혀낸거  바람핀 전연하남친 아오 진짜 아무튼 결국에는 단지는 아기 잘 낳고 주연이랑 단지랑 아기랑 행복하게 잘 살았다는 결말이  급 마무리 그냥 웃고싶어서 이 영화를 선택했는데 뜻밖의 감동이 이런건지 몰랐는V 김혜수배우님 이런연기는뭔가 연기가 아니라 진짜같아서 더 웃김 그리고마동석배우님의 하드캐리개웃김 저랑 같은 영화관에서 같이 보신 분들은 굉장히 리액션이 좋으셔서 많이 웃으시고 공감도 많이 하시면서 보시더라구요 저도재밌게 봤습니다 전연하남친 그리고 단지 임신시킨놈이 짜증났지만 재밌게 봤습니다 이상 하찮은 후기였습니다 항상 하찮음 태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 인쇄 댓글 2 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=freshfox90&logNo=120195008397\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 165 목록열기 스크랩 엮인글 죽기전에 봐야할 영화 굿바이 마이 프렌드 줄거리 감동적인영화 옛날 영화 2013 7 27 1955 httpblognavercomfreshfox90120195008397 번역하기 전용뷰어 보기 이 포스트를 보낸곳 1 죽기전에 봐야할 영화 굿바이 마이 프렌드 줄거리 감동적인영화  오늘 포스트는 1995년에 개봉한 감동적인영화 굿바이 마이 프렌드 입니다 어릴때 이 영화를 볼 때 상당히 슬프지만 감동적인 느낌을 받았었는데요 아직 안보신 분들을 위해 대략적인 줄거리를 적어보도록 하겠습니다   오늘 소개 할 죽기전에 봐야할 영화 굿바이 마이 프렌드는 두 친구에 관한 이야기 입니다 제목에 프렌드가 들어가니 당연히 예상하셨겠지만요   사실 이 영화의 원제는 The Cure 치유하다인데 한국으로 들어오면서 너무 제목이 밋밋한거 아닌가라는 생각에 영화 제목을 굿바이 마이 프렌드로 바꿔버린거죠   아무튼 대략적인 줄거리는 수혈 때문에 에이즈에 감염된 덱스터와 건강하고 의리있는 에릭의 치료약을 구하기 위한 여정이라는 내용인데요 독초의 약효를 알기위해서 집어먹는다던가 다른 동네에 치료제가 있다는 소식을 듣고 뗏목을 타고 간다던가 뭐 그런 내용들입니다   사실 제목을 굿바이 마이 프렌드라고 바꿔버려서 아 얘가 죽는 영화구나라는 생각 때문에 원제인 더 큐어 일때보다 뭔가 상상하는 재미가 떨어지는건 사실이었죠  하지만 이 영화가 제목을 굿바이 마이 프렌드로 바꿨음에도 불구하고 죽기전에 봐야할 영화로 선정이 된 이유는 덱스터의 엄마의 역할도 아주 컸다고 생각합니다   그건 그렇고 밴드 오브 브라더스 2부 퍼시픽을 보신 분들이라면 느꼈겠지만 덱스터 역으로 나오는 조셉 마젤로가 퍼시픽에서 유진 슬레지로 나왔다는거 어렴풋이 느껴지셨을거에요 잘컸죠 아주  사실 이친구 쥬라기공원에 꼬마남자 역으로도 나왔다는    하지만 굿바이 마이 프렌드 이후로는 그닥 크게 성공한 영화의 주연을 맡은적이 없다는게 참 안타깝죠 퍼시픽 같은 전쟁드라마야 워낙에 매니아층이 뚜렷하다보니 하지만 그래도 조셉 마젤로는 아직 젊으니까 83년생 이후로도 굿바이 마이 프렌드처럼 죽기전에 봐야할 영화를 찍을 수 있지 않을까 기대해봅니다  굿바이 마이 프랜드리뷰보기 감독 피터 호튼 출연 조셉 마젤로 브래드 렌프로 다이아나 스카  태그저장 취소 인쇄 댓글 2 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=atm____&logNo=220755195985\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 419 목록열기 스크랩 엮인글 영화와 공연 영화 굿바이 싱글 줄거리 김혜수 마동석 김현수  스포 있음 2016 7 6 1617 URL 복사 본문 기타 기능 번역보기 Posting by 지연 저번 주 주말 생각지도 않게 영화 굿바이 싱글을 보게 되었음 정글북 타잔은 너무 그냥 그럴 거 같고 사냥은 비추가 많아서 인디펜더스랑 굿바이 싱글 중 시간 맞는 거 보려고 했는데 마침 굿바이 싱글이 1시간도 안 남았길래 바로 예매 예매할 땐 모두 LGU멤버십으로 예매하도록 해요  대한민국 대표 독거 스타의 임신 스캔들 이번엔 제대로 사고 쳤다 온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받고 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데 대표 독거 스타의 임신 발표는 전 국민 스캔들로 일이 커지고 주연김혜수의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분서주 하는데 통제불능 여배우 그녀의 무모한 계획은 계속 될까 줄거리는 이러함  줄거리를 읽어 보니까 재미있을 거 같기도 하고 김혜수와 마동석 캐미가 기대되기도 하고 두 배우 모두 연기 갑아니오 인물은 크게 배우 고주연 스타일리스트 평구 그의 와이프 상미 외 자식들 주연 엔터테인먼트 대표 김대표 그리고 바람피운 연하남 스타 배우 지훈 당돌한 미술을 잘하는 흙 수저 중딩 단지 학교 대표로 뽑힐 정도의 수준임 요 정도 포스터에는 주연배우인 김혜수 마동석의 이름만 크게 써져있는데 내가 보기엔 극 중 단지역을 맡았던 아역 배우 김현수도 주연이니까 함께 크게 써져야 하는 게 아닌가 싶음  그그냥그렇다고 아무튼 줄거리 시작 꽤나 자극적으로 시작 시상식 바로 전 날 입술 필러를 맞아 집에서 시상식 드레스를 입고 풀메이크업을 한 채 TV로 시상식 시청중인 고주연 와 나 이장면 보고 첫번로 김혜수 몸매에 감탄 첫번째라는 말에 주목해야함 시작부터 감탄사가 나왔던 김혜수는 극중에서도 배우이기때문에 옷이 굉장히 화려했고 몸매 작살임 내 몸은 눈 감아 두번째 감탄신 휴 김혜수는 연하 남친의 아버지가 식사를 하자는 말에 기분이 좋아 요리는 못하지만 맛있는초밥을 만들어기분 좋게 소속사로 출근하는 장면임 저렇게 화려한 옷을 입고  역시 여배우 그런데    연하 남친에게 일반인 여친이 있다는 찌라시가 돌고 집으로 직접 찾아가 보니 연하 남친 집에는 어린 여친이 함께있었음 심지어 그 어린 여자는 고주연의 옷을 입고있었고 고주연이 지훈의 엄마 생일 선물로 지훈에게 준 목걸이를 차고있었음 나쁜쉐키 아무튼 배우 고주연은 연하 남친에게 아주 호되게 차이고 평구 집에서 쉬다 평구의 아이들이 엄마 상미에게 매달리고 엄마에게 일편단심인 모습을 보고 남자에게 차일 바에는 자식을 낳겠다 입양 하기로 맘 먹음 그렇지만 자격미달로 거절당하고 산부인과 검진하러 갔다가 폐경이라는 충격적이 사실을 알게됨 그렇게 충격을 안고 돌아가던 중 엘레베이터에서 임신한 중딩 단지를 만나게됨 단지가 어린나이에 산부인과에 오니 역시 사람들 시선이 곱지못했고 대놓고 혀를 차는 등 막말을 퍼부음 주연은 옆에서 듣다 단지 엄마라고  옆에서 보호하면서 단지와의 첫만남이 시작됨 그리고선 은밀하게 단지가 낳을 아이를 본인이 키울 작정을 짜게됨 굿바이싱글에서 만난 정봉이도 너무나 웃겼음 그리고는 단지와 계약을 하게됨 부모님은 안계시고 언니와 언니남자친구와 함께 살고있는데 단지는 돈이 필요했고 그 생활 속에서 벗어나고자 고주연과 계약하게 됨 계약 후 단지는 고주연 집으로 들어가 살게됨 고주연은 성심성의껏 단지 태교에 힘씀 식이요법부터 운동까지 산모수첩도 함께 작성  그러다 세번째 감탄씬 내몸 눈 감았지 처음엔 서로 의심하고 계약 관계였지만 함께 생활을 공유하고 마음을 공유하다보니 서로가 서로를 챙기게됨 여기서 단지를 임신시킨 그 새키는 골프 선수로 학교대표로 또 국가대표로 미국으로 대회에 참가하게 됨 단지가 그 아이에게 임신 사실을 알리니 내아이가 맞냐고 이야기 했다고 그래서 고주연이 복수하러 가자며 공항으로가서둘이 있을 때 빰한대 날리고 다른 골퍼들과 함께 단체사진 찍고 돌아오는 길 단지 너무 예쁘고 고주연도 너무 멋짐  이렇게 행복한 삶을 지내고 있는데 단지가 갑자기 언니도 복수해야하지 않겠냐고 고주연에게 물음 해야지 해야지 그럼 그렇게 고주연은 미혼으로 임신을 공개하여 큰 이슈가 된다 왜 여자만 숨어야하느냐며 뉴스까지 출연하면서 임신연기 시작 단지 복수를 위한 거짓 임신공개는 고주연의 이미지 상승을 시키며 짤렸던 드라마 주연 케스팅에 갖은 육아용품 CF행복한 이미지의 CF를 모두 섭렵하게됨 고주연은 아빠가 누구냐 이런 소리가 나올때마다 아이아빠의 이름은 언급하지 않지만 지훈이 아빠라는 기운을 풀풀 풍김 그리고 옆에 연하남 지훈은 점점 이미지 하락하게됨  꼬시다 내 이놈  바람둥이 그래도 지후훈 본인은 절대 아니라는 생각에 주연도 찾아가아니라고 한 마디만 해주면 된다고 아니지 않냐고 하자 주연은꺼져버리라며 외면 지훈은 스토커질을 하게됨 주연이 바빠지면서 단지는 집에 혼자 있는시간이 많아지고 배는 점점부르는데 병원도 혼자 다니게되고외출이 잦아짐 그러다 지훈에게 걸림 처음엔 조카라고 했지만 부른배를 들키는바람에 지훈이 이거다 싶어서 언론에 퍼뜨림 아래 컷은 주연이 임신연극 대국민사기가 이슈가 된 후 지훈의 모습임 얄미워 얄미워  그 이후 내용은 영화  보시면 될거같음 내 글수준으로 풀어 내기엔 역부족  절반만 스포 헷 내용 전개는 웃음코믹  시련  눈물감동의내용 전개로 진부하였지만 내용 자체가 진부하지만은 않았던거같음 가볍게 보기좋았던 영화고 조금은 속이 시원했던 영화 김혜수마동석은 믿고 보는 연기였고 아역인데도 감정을 잘 풀어넨 김현수에게도 박수를 그래서 내 별점은 10점만점에 75점정도 태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 인쇄 댓글 2 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]}],\"prefix\":\"NAVER_BLOG\",\"result\":[{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=godqhrm_m&logNo=221178540875\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 699 목록열기 스크랩 엮인글 영화 영화  굿바이 싱글 줄거리 및 후기 2018 1 5 1533 URL 복사 본문 기타 기능 번역보기 영화  굿바이 싱글 줄거리 및 후기 출처  네이버 줄거리 및 후기 출처  네이버 대한민국 대표 독거스타의 임신 스캔들 이번엔 제대로 사고쳤다 온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수  그러나 점차 내려가는 인기와 남자친구곽시양 의 공개적 배신에 충격을 받고 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 된다 영원한 내 편은 아기라고 생각하여 입양을 시도했으나 실패 하고 병원에 들렸다가 폐경이라는 사실을 알게 되고 거기서 임신한 학생김현수  을 만나게 된다 김혜수는 아기가 필요하고 김현수는 아기가 필요 없고 김혜수는 아기를 낳아 주면 자기가 키우겠다고 계약을 한다 학생을 임신시킨 남자는 국제대회도 나가는데 임산부라고 학생 미술대회 조차도 못 나가게 하는 사회를 보면서 너무 화가 나기도 하고 안타깝기도 하고 김혜수 와 김현수가 같이 살면서 정이 들게 되는데 미혼모라는 타이틀을 가지고 온갓CF 와 드라마가 들어오면서 점점 바빠지면서 임산부에게 소홀해지게 되고 결국 김현수의 언니가 이 사실을 알게되고 온국민이 이 사실을알게된다 엄청난 돈을 받고 김현수의 언니는 남친과 호주로떠나게되고 김혜수의 불알친구이자 스타일리스트인 마동석 과 소속사 식구들이 안절부절하며 뒷수습에 나선다 김현수는 혼자가되며 배우로서 이미지가추락한 김혜수는 과거를뒤돌아보며 김현수에게 감정이입되고 김현수를 찾아가 사과하며 김현수는 김혜수의 도움으로 출산을하게된다 김혜수는 단역배우로 활동하게되며 김현수와 딸과 함께 살며 끝난다 좋지 않은 리뷰들을 많이봤는데 엄마된지 얼마 안되서 임신과 출산에대해 공감가는부분도많았고 새로운 역할의 마동석도 좋았다 생각보다 아기를 가지고싶어도 못가지는사람들이 많은데 아기를 가졌다는것은 큰 축복인데 미혼모라는 이유로 따가운 시선을받아 안타까웠다 임신하면 호르몬변화 신체적변화가 많이일어나는데 주변사람들이 잘 도와줘서 모든 임산부들이 외롭고 우울하지 않았으면 좋겠다 태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 인쇄 댓글쓰기 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=lovethedays&logNo=220782519495\",\"contents\":[{\"blog_post_view\":\"영화 굿바이싱글 후기 줄거리가 약간포함되어 있습니다 올만에 쓰는 영화 후기 무슨내용인지도 몰랐고 김혜수랑 마동석이 부부로 나오는줄 알고 본영화인데 예상외로 너무 재미있었던 영화입니당 저의엄마가 김혜수를 무지무지 무진장 좋아하셔서 저도 좋아라 하는데 보게되서 내용도 모른채 그냥보게되었답니다 후기에 들어가기전에 김혜수언니의 몸매는후 진짜 죽입니당 우리엄마가 김혜수를좋아하는 이유가 영화 람보때 그렇게 이쁜사람이 나오는걸 처음봤다고 예뻐서 좋아한다는 우리엄마 김혜수 언니의 나이를 생각해보면  이 나이인데도 저몸매라니 정말 대단하지 않습니까 영화보면서 계속 감탄했답니당 마동석과 김혜수가 부부로 나오는줄알았는데 아니면 러브라인이겠거니 했는데 아예 아니더라구용 둘의 우정이 빛났던 영화 마동석은 극중 김혜수의 스타일리스트로 나옵니다 하지만 사고뭉치 안하무인 여배우 고주연김혜수의 개인 소속사라서 사실상 매니저 역할까지 다하면서 뒤에서 다 챙겨주는 역할이랍니다 고주연이 얼마나 안하무인이냐면 혼자서 시상식 당일에 입술 필러를 맞고 퉁퉁 부은채로 와서 결국 시상식은 못가게되고 나이는 많은데 띠동갑 이상인 어린 잘생긴 신인연예인남자들한테 잘해주고 자리 꽂아주고퍼줘서 결혼할려고 욕심부리는데 맨날 차이고  이번에는 마지막인줄알고 또 어린 신인 배우에게 정성을 쏟지만 결혼을 꿈꾸다 결국에는 또 남자가 항상 바람펴서 당하고 영원한자기 편은 없다는걸 깨닫게 돼서 늦게나마임신을하려 산부인과에 갔다가 청천벽력 같은소리만 듣고 나오게됩니당 그리고 거기에서 중학생인데 임신해서 중절수술을 받으려는 단지를 만나서 어차피 지울거면 자기에게 달라는딜을 하죠 불법이지만 어찌보면 안하무인 고주연이기에 가능한 거래인거같네요 아이는 단지가 낳지만 갑자기 애가생기는것도 그렇고 전 남자친구에게 복수도 하기위해 그 남자 사이에서 생긴거처럼 하지만미혼모로 낳을것처럼 발표를 하는데요 이 발표가인기가 다 죽어가던 한물간고주연에게 CF도 많이 들어오고 섭외도 많이 들어오게바꿉니다 덕분에 조연으로 캐스팅됐던 영화에서도 주연으로 바뀌죠 뒤따라오는 남자가 전남친분  안그래도 고주연과의 스캔들로 그리고 고주연이 꽂아준 영화에서 주연을 맡게되서 점점 인기를 얻고있는 전남친 공식 연애는 아니었지만 남들 다 알고있던 사이였기에 고주연의 아이 아빠가 전남친인걸로 당연시됩니다 그래서 이 임신으로 큰타격을 입은 이제 막 떠오르기 시작한 신인배우전남친는 영화에서도 하차하게 되고 여러가지 악재가 겹쳐서 이를 악물고 결국엔 고주연의 임신이 가짜인걸 밝혀내요 덜덜 다 밝혀진 후 고주연은 전남친의 명예훼손 및 다른사기 소송 CF 위약금 소송 등등 수십억에 달하는 돈을 물어줘야 할 위기가 되고 안그래도 고주연 하나만 있던 소속사는 고주연의 집을 팔아 소송들을 마무리 짓고 이제 해체하려고 합니당  자기 잘못을 모르고 끝까지 안하무인인 고주연에게 화가나서 화내는 이분 이름을 모르네요 아무튼 마동석의 부인으로 나오는데 참한 연기를 잘하고 나도 화가 같이나서 감정이입 되더라구요 그리고 영화보면서 제일 크게 느꼈던거 중학생인단지의 임신을 저 자신도 쯧쯧 이렇게 느꼈다는거죠 그런데 극중 김혜수언니가 단지에게 나쁜말과 안좋은시선을 보내는 학부모들에게 편을 들면서 얘 이렇게 만든놈은요 얘 이렇게 만든놈은 국가대표로 해외에 경기하러갔어요 근데 얘는 미술대회 참가하는것도 안되는거에요 왜요 하는데 와 찡 하는거에요 진짜 나는 왜 남자는 당연히 생각도안하고 저 아이에게만 안좋은 시선으로 바라봤는지 쿵 하더라구요 아무튼 정말 생각없이 봤는데 의외로 많은 교훈을 느끼게 해준 굿바이 싱글 후기 감동도 쪼꼼해서 울었고 기대 안하고 본 영화였는데 지루한 시간없이재미있게 잘봤답니당  굿바이 싱글 줄거리 결말 후기 끝 영화 굿바이싱글 후기\"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=letdie2307&logNo=220726265817\",\"contents\":[{\"blog_post_view\":\"굿바이 싱글 GOODBYE SINGLE 2015 개요 코미디드라마  119분  20160629 개봉 감독 김태곤 출연 김혜수 마동석 김현수곽시양서현진 등급 15세 관람가 제가 넘나 사랑하는 배우 혜수 언니의 새 영화 굿바이 싱글 이 6월 29일 개봉 이라고 합니다 끄앙 tvN 드라마 시그널에서 형사 차수현 역으로 시크한 카리스마와 풋풋한 뿅아리 신입 경찰의 매력을 동시에 보여주며 많은 팬들을설레게 만들었는데 이번에는 어떤 매력을 보여 주실지 넘나 기대 되고요  줄거리 대한민국 대표 독거스타의 임신 스캔들 이번엔 제대로 사고쳤다   어딜 데려다 놓건 뭘 입혀 놓던 일상이 화보인 언제나 품격 있는 대한민국 톱스타 고주연 김혜수  누가 봐도완벽하고 화려한 삶을 살아 가는 우주대스타로 보이지만 사실은 사고폭탄인 그녀  시상식 가는 날 입술에 필러 맞아서 입술이 닭똥집 되어 온 주연 때문에 마요미는 딥빡  세상에서 제일 기품 있고 예쁜 얼굴로 구수한쌍욕 시전하는 언니  훠우 그래도 내 눈엔 예쁘기만 할 듯   거기다 연애 허당까지 이 언니 연애를 글로 배웠다에 500원 검 그리고점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받고  영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데   수많은 취재진 앞에서 뜬금포로 임신 발표를  하고 만다  대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지고 주연김혜수의 불알친구이자 스타일리스트인평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분서주 하는데  통제불능 여배우 그녀의 무모한 계획은 계속 될까 줄거리만 봐도 유쾌 상쾌 통쾌 할 것만 같은 코미디 일 것 같지 않나요   베테랑에서 나 아트박스 사장인데 라는 대사 한 줄로 강렬한 임팩트를 선사 하신 마동석님과 오랫동안 내 마음 속 1등 배우인 혜수 언니 굿바이 싱글에서 둘이 러브라인은 아니지만 둘도 없는 친구사이로 케미도 은근 기대 되네요  믿고 보는 배우 김혜수 6월 29일 제가 달려가 꼭 보겠습니다  근데 소중한 여인가제 소식은 왜 아직 없는 것  \"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=choapick&logNo=220753310276\",\"contents\":[{\"blog_post_view\":\"굿바이싱글 줄거리  영화 굿바이싱글 줄거리 내용 후기 리뷰 굿바이 싱글 감독 김태곤 출연 김혜수 마동석 김현수 개봉 2016 한국 리뷰보기 이힝 주말엔 남친이랑 400일이었어요 그래서 남친님이 같이 영화관 가주심 영화관을 너무 싫어하셔서요 곡성 볼까하다가 김혜수 마동석 씨가 나온 영화 굿바이싱글을 보기로했어요 김혜수님은 오랜만에 작품인것같아요 마동석씨는 계속 주가가 오르고있죠 그럼 영화 굿바이싱글 줄거리 후기 리뷰 고고   저희는 메가박스 킨텍스점에서 영화봤어요 제 생각엔 일산에서는 메가박스 킨텍스점이 제일 깔끔하고 넓고 좋은것같아요 규모도 큰데 사람은 그렇게 많지않은게 장점이죠 메가박스 킨텍스점은 자가용 없이는 이용하기 좀 힘든게 그래서 사람들이 규모에 비해 좀 없는것같아요 킨텍스까지 가는 버스들이 많이 없거든요 가더라도 엄청 돌아감   영화 굿바이싱글 극중 김혜수 씨는 어릴때부터 데뷔해서 70년대를 풍미하다가 현재는 약 40살 여배우가 된 약간 퇴물느낌나는 조연급 여배우입니다  하지만 여전히 철없고 어린아이같아요 시상식 당일날 필러 맞아서 입술이 퉁퉁 붓는 필러 맞은것도 젊은 애인이랑 뽀뽀할때 푹신푹신한 느낌 주려고 맞은듯싶어요  기획사 식구들도 고민이 많아요 김혜수씨가 이제 늙고 인기가 점점 떨어져가자 젊은 신인을 키우려고 몰래 준비하는등 게다가 김혜수씨는 젊은 남자들을 꼬셔서 연예인으로 데뷔시켜주는  이상한 관념이 생긴상태에요 물론 의도하고 그런건 아니지만  굿바이싱글에서 김혜수씨의 애인으로 나오는 곽시양씨도 결국 김혜수를 이용만하고 젊은 여자랑 바람을 피워요  상처받은 김혜수는 온전한 자신만의 편을 만들기로 합니다 바로아이를 가지기로한거죠 처음엔 입양으로 알아보지만 조건이 까다로워서 입양이 어려웠어요   그래서 직접 낳기 위해서 평소 알던 산부인과 의사를 찾아갔는데 아 근데 저 의사 배우 안재홍씨 응팔이랑 영화 위대한소원에 나왔었죠 저랑 남친은 이분 등장하자마자 너무 웃었어요  아이를 가지고 싶었지만 나이40데 이른 폐경을 맞게되어서 아이를 가지지두 못하는 갓혜수찡 체념하던찰라 병원에서 혼자 낙태하러온 16살 중학생 김현수씨를 만나게됩니다  그리고 1억을 주기로하고 아이를 낳아서 김혜수가 키우기로하지요 어떻게 보면 완전 불법입니다 대리모도 아니고  그러다가 전 남친에게 복수를 하기위해 언론에다가 임신을 했다고 공개해버려요 처음엔 걱정했으나 위기가 오히려 기회가 됩니다  마치 영화 과속스캔들에서 차태현씨가 책임감 있는 아빠로 다시 자리매김했다면 김혜수씨도한 영화 굿바이싱글에서 책임감있는 미혼모로 거듭나게 됩니다 한순간에 대박배우로 다시 등극해요  출연진들은 모두 즐거운 나날을 보냅니다 기획사 식구들과 김혜수는 돈벌어서 좋구요 하지만 바빠짐과 동시에 점차 김현수씨에게 관심을 가져주지 못합니다  그리고 평소 좋아하던 국민 앵커 이성민씨와 단독 인터뷰도하고 이성민씨가 먼저 데이트제안도 걸어옵니다  인기는 점차 회복되는 수준을 넘어서서 그녀를 정상배우로 만들어줍니다 처음에 까였던 신사임당 주연으로 발탁되고 김혜수가 임신한 아이가 전 남자친구라고 거짓말을 하면서 전 남자친구는 모든 계약이 위반되어 위약금을 물며 도망다니는 신세로 전락합니다  이에 전 남자친구는 김혜수의 집까지 찾아와서 사정사정합니다 제발 자신의 아이가 아니라고 해달라고 하지만 김혜수는 매몰차게 거절하죠 게다가 멋모르고 전 남자친구를 집에들인 김현수한테 엄청 화를 냅니다  김혜수가 김현수에게 점차 관심을 잃어가고 자신의 일과 연애중인 이성민씨에게 홀딱 빠지면서 김현수씨는 혼자 임신과정을 겪게됩니다 마블리마동석씨 만 김현수를 챙기게되요 무슨 빨래 찢어버릴것처럼 생겼는데 집안일 하는거보니까 왜이렇게 귀엽나요  김혜수씨가 마동석씨에게 굿바이싱글 촬영하면서 별명을 지어줬다네요 마쁜이 마블리 마귀욤     한편 전 남자친구는 자신에게 김혜수의 조카라고 소개한 김현수씨가 조카가 아닌 사실을 깨닫게 됩니다 결국 언론에 사실을 밝히게됩니다 김혜수의 임신이 거짓이었다고 인기를 잃어가는 스타가 스캔들을 만들기위해 벌인 사기극이라고  한순간에 정신병자 수준으로 몰린 김혜수 그러나 여전히 정신 못차리고 자신의 편들에게조차 막대합니다 결국 자신의 편들조차 화가나서 나가버려요 그러다 자신의 사과 기자 회견에도 가지않고 김현수씨의 미술대회를 같이가서 미혼모들을 대변하는 멘트를 통해 약간 부활하는 듯한 뉘앙스를 보여주면서 영화 굿바이싱글은 결말을 맞이합니다 영화 후반에는 독립영화를 찍으면서 예전처럼 수십억짜리 초호화 저택에는 살지 못하지만 아담한집에서 좋아하는사람들과 맛있게 식사를 하면서 끝마쳐요 영화 굿바이싱글의 현재 관객수는 91만명정도네요 굿바이싱글은 막 거대한 스토리나 대작이 있는건 아니지만 소소하게 볼만해요 영화관 가실 일이 있다면 한번 보시는것도 좋아보이네요 이상 영화 굿바이싱글 줄거리 내용 후기 리뷰 였습니다 초아의 영화 굿바이싱글 평점     \"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=junenyoung&logNo=220737701111\",\"contents\":[{\"blog_post_view\":\"영화 굿바이싱글 줄거리 후기 리뷰 Goodbye Single 2016 코미디 드라마  119분  15세 관람가  6월 29일 개봉 예정인 김혜수 주연의 영화 굿바이싱글 톱스타의 임신 스캔들이라는 다소 자극적인 포스터 내용이 눈길을 끌어요 6월 29일 개봉 예정이지만 영화 시사회 당첨되서개봉전에 먼저 관람하고 왔어요 큰 기대없이 봐서 그런지 생각보다 재밌었어요 영화 굿바이싱글 줄거리와 후기 간단히 리뷰할께요 자세한 내용은 영화로 직접 확인하세요 한때는 정말 잘나갔던 여배우 고주연김혜수 시상식 당일에 필러를 맞고 올 정도로 대책없는 스타일이에요  그런 주연을 곁에서 항상 지켜주는 친구이자 스타일리스트 평구마동석 주연이 사고치면 항상 뒷수습하느라 고생하는 주연에게는 꼭 필요한 친구에요 건강상의 문제로 시상식 참여가 불가능하다며 상황을 해결해요 지나치게 티가나는 얼굴 때문에 시상식에는 불참하지만 평구가 만든 시상식용 드레스를 입고 집에서 시상식을 관람해요   시상식에서 신인배우상을 받은 지훈곽시양 강지훈은 요새잘나가는 신인배우이자 주연의 남자친구에요 주연 덕에 드라마 촬영기회도 생기고 신인상까지 받게 되었어요   나이차이가 많이 나는 지훈과 주연의 사이를 반대하는 지훈의 부모님 하지만지훈의 아버지가 주연을 보고싶어한다는 말을 전하자 결혼에 대한 기대감을 갖고 몹시 좋아해요 몇일 뒤 터진 지훈의 스캔들 속 여자는 주연이 아닌 다른 여자 알고보니 지훈은 자신보다 더 어린 여자와 바람을 피고 있었어요 한번쯤은 용서를 해주겠다는 주연과 달리 주연에게 별 마음이 없던 지훈 결국 둘은 헤어지게 되요 점차 내려가는 인기와 남자친구의 공개적인 배신에 충격을 받은 주연 진정한 자기편이 없다고 생각한 주연은 영원한 내편이 되어줄 아이를 만들어야겠다는 생각을 해요   입양도 생각해보고 인공수정도 해보려고 했지만 모두 실패 그러던 중 사고친 중학생 단지김현수를 알게되요 일종의 계약 끝에 남들 몰래 단지가 낳은 아이를 자신의 아이로 키우기로 한 주연 출산 후 두번다시 만나지 않겠다며 서로 거리를 두려고 하지만 시간을 보낼수록 가까워지는 단지와 주연 점점 서로의 아픔도 공유하고 위로하게 되요   남자친구였던 지훈에게 복수하고싶은 마음에 기자 들 앞에서 자신이 임신했다고 말해요   신문 방송 인터넷 등 어디서나 화제가 된 고주연의 임실사실 다양한 스캔들이 있었던 만큼 아이의 아버지가 누굴지에 대한 관심도 쏠려요 소속사에 상의 없이 기자 들에게 임신사실을 밝혀 소속사에서는 당황스럽기도 했지만 오히려 이 일이 전화위복이 될줄은 각종 분유 유아용품 광고가 물밀듯이 들어와요   이로 인해 제 2의 전성기를 누리게 된 고주연   뉴스까지 나와서 미혼모에 대한 생각까지 밝히게 되요 아이의 아빠로 추정되는 지훈은 드라마에서 하차하는 등 큰 타격을 입게 되요 결국 주연을 찾아가 자신의 애가 아니라고 말해달라고 하는데 과연 이 임신 스캔들은 어떻게 끝날것인지 자세한 내용은 영화로 직접 확인해보세요 미국 유학파 스타일리스트 평구 역의 마동석씨 영화에서는 스타일리스트라기보다 매니저의 역할을 더 많이 소화하시는거 같더라구요   다소 험학한 인상과 달리 귀여운 모습도 많이 보여주는 마요미 마블리 마동석   사실 영화 보기 전에는 김혜수씨랑 둘이 잘되는 스토리인줄 알았는데 서현진씨와 결혼한 사이로 나와요 요즘 대세녀인 서현진씨와 결혼한 마블리의 매력도 관람 포인트   톱스타이자 대표적인 골드미스 배우 주연 역을 맡은 김혜수씨 다른 배우가 이 역할을 맡았으면 어땠을지 상상이 안되요 영화 보는 내내 혜수언니의 모습에 감탄 김혜수씨의 카리스마는 이미 알고 있었지만 코믹한 연기도 잘 소화하고 사랑스러운 모습까지도 완벽 소화 실제로도 대표 골드미스지만 영화에서보다 더 부러운 삶을 살것만 같아요 영화보면서 머리쓰고 복잡한 생각할 필요 없이 보면서 그냥 즐길 수 있는 재밌는 코미디 영화 굿바이싱글 영화 후반에는 다소 뻔하긴 하지만 감동적인 부분도 있어요 진정한 내편이 있는지 한번쯤은 생각하게 만든 영화 굿바이싱글 올 여름 가볍에 웃으면서 즐길 수 있는 코미디 영화로 굿바이싱글은 어떠신가요 영화 굿바이싱글 리뷰 굿바이싱글 줄거리 후기 김혜수 마동석 김현수 곽시양 서현진 코미디영화 추천\"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=asdfghjkl004&logNo=220751627950\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 572 목록열기 스크랩 엮인글 영화 후기 코미디보다는 감동적이었던 코미디영화 굿바이싱글 후기 줄거리 2016 7 2 1227 URL 복사 본문 기타 기능 번역보기 개요 코미디 드라마  한국  119분  20160629 개봉 감독 김태곤 출연 김혜수주연 마동석평구 김현수단지 등급 국내 15세 관람가 김혜수만 보고 영화를 선택했습니다 김혜수가 선택한 코미디영화는 어떤 것일까  영화 굿바이싱글 줄거리 대한민국 대표 독거스타의 임신 스캔들 이번엔 제대로 사고쳤다  온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받고 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데  대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지고 주연김혜수의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분서주 하는데  통제불능 여배우 그녀의 무모한 계획은 계속 될까  영화 굿바이싱글 후기 혼밥혼술 등등 혼자 영화보기가 부끄럽지않고 자연스러운 세상 해시태그에서 혼자놀기 의 태그횟수도 꾸준히 증가하고 있다고 한다 요즘같이 싱글라이프가 대세인 이 시대에 오히려 혼자로 살만큼 살아본 톱스타 여배우 고주연의 내편만들기 프로젝트를 담은 영화이다 코미디 영화인데 영화의 호흡이 좀 느렸다 영화의 예고편을 보면 영화의 호흡이 좀 빨라 보이는데 이 영화는 좀 느린 편 줄거리를 보면 미혼모 이야기를 유쾌하게 했던 영화 과속스캔들 을 떠올리게 한다근데 10대 미혼모역할을 한 배우김현수의 입으로 미혼모를 돌보고 있었던 역할을 한 배우김혜수의 입으로 10대 미혼모가 받는 사회적 시선이나 미혼모의 입장의 대사들을 내뱉는 장면이 정말 감동적이었음 한국 영화의 정석인 코미디  감동 코드를 잘 따랐는데 코미디가 조금 약했던것 같다 생각보다 김혜수가 연기한 고주연이 너무 무대포무개념이라서 그런지 좀 짜증나서 웃기기보다는생략 결말은 다시 고주연이 톱스타의 자리로 갈줄 알았는데 오히려 그러지 않고 내편인 사람들과 함께하는 것을 보고 진정한 해피엔딩이었던 것 같음 저의 주관적인 별점은  세개반이용 이 영화는 많은 사람이 봤으면 좋겠다 태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 이 저작물은 아래 조건 만족 시 별도 허가 없이 사용 가능합니다 저작자 명시 필수 영리적 사용 불가 내용 변경 불가 인쇄 댓글 13 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=epsl1009&logNo=220753701258\",\"contents\":[{\"blog_post_view\":\"주관적 평점과 후기입니다 참고만 해주세요   굿바이 싱글 감독 김태곤 출연 김혜수 마동석 김현수 개봉 2016 한국 리뷰보기    정말 가볍게 볼 수 있는 영화 굿바이 싱글 가족 친구 연인 모두가 울고 웃으며 볼 수 있는 킬링타임용 영화가 아닐까 싶어요 결코 예고편이 전부가 아닌 영화 굿바이 싱글 아직 못보신 분들이라면 추천해요          제 주관적인 평점은 40점5점만점입니다    오랜만에 드라마코미디 영화가 잘 없는데 오랜만에 휴먼코미디 영화가 나왔어요 결코 예고편이 전부가 아닌 영화 굿바이 싱글  흔히들 예고편만 보고 뻔한 내용이겠지 라고 생각할 수 있는데 영화 굿바이 싱글은 울고 웃고 유쾌하고 재밌고 감동도 있는 그런 영화가 아닐까 싶어요  단독 주연으로 길다면 긴 영화 러닝타임을 혼자서 이끌어간 배우 김혜수의 연기에 다시 한번 놀랐습니다 물론 다른 배우들과의 호흡도 좋았지만 무엇보다 단한번도 영화의 몰입감이 방해되지 않았다는게 김혜수라는 배우의 저력을 보여줬다고 생각합니다  영화 굿바이싱글에서 다루고 있는 10대 임신과 미혼모 주제 결코 가볍게만은 다루지 않아서 좋았어요 영화 후반부 김혜수가 열변을 토하듯이 대사를 내뱉는 장면은 정말 뭉클하고 인상깊었어요bb  그 외 연출이나 다른 배우들의 연기나 BGM까지 무난무난했습니다 영화 굿바이 싱글 보러가실분들은 참고하세요    이번주에 영화 봉이 김선달도 개봉하면서 여름 극장가 킬링타임용 영화가 넘쳐나겠네요 그럼 전 봉이 김선달도 보고 와서 후기 남길게요D   줄거리  대한민국 대표 독거스타의 임신 스캔들 이번엔 제대로 사고쳤다  온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받고 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데  대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지고 주연김혜수의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분서주 하는데  통제불능 여배우 그녀의 무모한 계획은 계속 될까       톱스타 주연 역할을 맡은 배우 김헤수   정말 주연 그 자체임을 보여준 김혜수 그동안 묵직한 연기들만 보다가 오랜만에 아낌없이 망가지고 유쾌한 캐릭터를 연기한 혜수언니 단독 주연으로 2시간이라는 긴 러닝타임을 끌고가는데 전혀 무리없이 관객들을 몰입하게 만드는 연기는 정말 클래스가 다르다는걸 보여줍니다 bb       평구 역할을 맡은 배우 마동석   과연 영화 굿바이 싱글에서 평구의 역할은 무엇일까요  딱 바로 떠오르는거 그거 아닙니다  이번 영화에서 마블리 매력 아낌없이 보여주심 38사기동대에서와는 또 다른 모습이라 낯설면서도 귀여웠음         이번 영화에 보는 재미를 더해주는 조연들 또오해영으로 로코 여왕으로 떠오른 서현진 마블리랑 무슨 관계인지는 영화에서 확인하세요 서현진 저런 역할도 어울리네 앞으로가 더 기대되는 여배우        단지 역할을 맡은 배우 김현수   옴마나 마블리하고 인연이 깊었네 이번이 3번째 호흡이라니 흔히들 별그대 천송이 어린시절로 기억하고 있는 배우 김현수 이번 영화에서 정말 쉽지 않은 배역인데도 훌륭하게 연기한것 같음 앞으로도 다양한 작품에서 만나길        특별출연 배우 이성민   특특별출연이신거죠 엔딩크레딧을 끝까지 안보고 나와서 그저 연기를 보기만 해도 절로 웃음이 나고 흐뭇한 배우가 몇이나 될까 나에겐 미생의 이미지가 강하긴 하지만 그냥 좋은 배우 이성민 아저씨       그 외 조연으로 출연한 곽시양 안재홍 김용건 전석호 정말 조연 보는 재미가 쏠쏠한 영화 굿바이 싱글 각자의 캐릭터 옷을 잘 입은듯 실감나는 연기를 펼침 그 중에서도 곽시양이 연기한 배역은 정말실제로 저런 연예인이 있을 것 같아서 겁나 얄미웠음   영화 굿바이싱글  굿바이싱글 후기  굿바이싱글 줄거리\"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://oldboy16.tistory.com/166\",\"contents\":[{\"blog_tistory2\":\"피터 호튼 감독 해외영화 굿바이 마이 프렌드 줄거리 느낀점  피터 호튼 감독 해외영화 굿바이 마이 프렌드 줄거리 느낀점   나의 소중했던 친구야 잘가 영화 굿바이 마이 프렌드입니다     이 작품 같은 경우에는 당시에 되게 슬픈 영화로 유명했었는데요 저도 언젠가는 봐야지 봐야지하고 있었는데 뭔가 계속 미뤄지기만 하더라고요 그러다가 결국 이번에 봤는데요     보는 동안 슬프기는 한데 약간 너무 뻔한 느낌이라서 눈물이 나지 않았어요 근데 역시나 마지막 부분에 가니까 아 하면서 눈물이 나게 되더라고요 이 작품은 옆집 친구가 에이즈 걸린 아이에요    근데 그 친구랑 서로 같이 친하게 지내게 된답니다 집에서만 거의 지내는 그 친구를 위해 주인공은 같이 나가서 배도 태워주려고 하고 그렇게 되는데 물론 아이의 건강에 문제가 생기게 되면서 큰 일로 번지게 되기는 하지만요    두 친구의 정말 끈끈하고 순박한 우정을 볼 수 있는 장면이었어요 하지만 주인공의 엄마 같은 경우에는 이혼 후에 되게 괴팍한 성격으로 보여지는데요 그때도 에이즈 걸린 친구랑 놀지 말라는 식으로 되게 안 좋게 바라본답니다    주인공에게 정말 잘 대해주었던 그 친구의 어머니 같은 경우에도 나중에는 결국 주인공 어머니에게 엄청나게 화를 내게 되는데요 정말 짧은 부분이었음에도 불구하고 그때 눈물이 왈칵 쏟아지게 되더라고요 정말 슬펐어요 그 부분 뭔가 감췄던 감정을 거기서 드디어 드러내게 되는듯한 느낌이었거든요 되게 좋았어요   공감sns신고 저작자표시비영리변경금지 영화 소년해외영화 카테고리의 다른 글 리처드 라그라브네스 감독 PS 아이 러브 유 결말 후기0 20170319 줄리안 무어 주연 해외영화 눈먼 자들의 도시 줄거리 느낀점0 20170317 피터 호튼 감독 해외영화 굿바이 마이 프렌드 줄거리 느낀점0 20170317 팀 버튼 감독 해외영화 빅 피쉬 줄거리 느낀점0 20170316 크리스 콜롬버스 감독 해외영화 나 홀로 집에 결말 후기0 20170315 제이콥 트렘블레이 주연 해외영화 룸 결말 후기0 20170314\"}],\"dest_fields\":[\"blog_tistory2\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=qegreq3456&logNo=220761961634\",\"contents\":[{\"blog_post_view\":\"영화 굿바이싱글 드디어 봤네요 줄거리를 작성할까 하는데 스포가 될 수도 있답니다  영화에 대해 자세히 알고 보고싶으신 분들은 괜찮으실꺼에요 전체 스포가아니라 줄거리 설명에 필요한 스포를 좀 하겠습니다  6월 29일에 개봉한 굿바이싱글 최근에 굉장히 삭막하고 무서운 영화들이 많이 나왔다면 이번엔 경쾌하고 유쾌한 영화 굿바이싱글로 분위기 전환 해보세요  고주연  김혜수  영화의 주인공이자 철없는 국민진상 여자 배우 연하킬러에다가 남자를 좋아하는 캐릭임 평구  마동석  고주연과는 불알친구  실력좋은 스타일리스트 겸 매니져 3명의 자식이 있는 한집안의 가장임 단지  김현수  고주연과 한지붕에 살게 될 여중생 이 여중생의 정보는 줄거리에 기재할게요 스포 싫으시다면 줄거이 읽지마세욧 덕수  안재홍  산부인과의사 이 사건의 발판이 된 셈 재밌는캐릭터 은근  굿바이 싱글 줄거리  시상식 가야하는 전날 성형외과에 가서 입술 필러를 떡 하니 맞음  정말 생각없고 철없는 여배우 고주연 결국 일생겼다는 핑계로 시상식은 빠지고 집에서 아이스크림 퍼먹으며 시상결과 지켜보는 장면으로 영화 시작 스타또 고주연은 만나던 연하 남자친구가 있는데 알고보니 이놈 고주연 등처먹고 더 젊은 여자애랑 바람났음  남자란 젊고 예쁘면 그저 좋은 건가요 이 사건을 계기로 주위를 둘러보니 자기 편은 없다는 것을 느낀 고주연 이렇게 주변 사람들을 모아놓고 식사를 대접합니다 평구도 결국 마누라편 소속사사장님도 회사식구 미래도결국은 누군가 다른사람의 편일꺼라고 생각한 주연은 자식을 갖기로 폭탄 발언을 하게 된다 그리고 병원에 가는데 이게 무슨 청천벽력같은 소리 폐경이란다  30대 후반인 고주연 아이도 못갖게 되는 실정  이게 어떻게 이렇게 될수가 있나여 아이를 갖고싶은데 못갖는다니 병원 엘리베이터에서 마주친 여중생 단지 단지는 임신을해서 아기를 지우러 병원에 찾아 온 것이였다 그것을 말리는 고주연 그러다 의사의 눈싸인으로 고주연은 단지에게 조건을 새우며 아이를 낳으라고 한다 단지는 아이를 고주연대신 낳아주되 품고 있을 동안 고주연집에서 함께 살며 태교에 전념한다 뭐 이런계약 그러다 둘 연애사를 트게 되는데 고주연은 알다시피 연하남과 만나는걸로 유명했는데 연하남이 바람피움 단지는 학교에 엘리트인 남자애의 아이를 갖게 되었는데남자애기 회피함 그래서 둘다 남자에게 복수하기로 하고 복수하러가고  급속도로 친해지고 난리임 일없을땐 고주연이 단지랑 놀아주고 태교도 같이하고 신경 많이 써주고 그랬는데 고주연이 전남친한테 복수한답시고 아이를 가졌는데 남편은 누군지 명확히 안밝혔지만 정황상 그 연하남이 전 남편의 아이가 된것 하지만 지우지않고 길르겠다는 것으로 이미지가 개념찬연예인으로 봐낌 그래서 일도 많이 들어오고 바쁜하루를 보냄 뉴스에도 인터뷰하러 나가고 하다가 아나운서랑 눈이 맞게 된 고주연 그때부터 정신못차리고 또 남자만 졸졸 쫓아다니는 고주연으로 돌아왔는데 여기서부터 이제 영화의 클라이막스였던거 같아요 오해에 이해에 뭐 오만게 다 겹치면서 별에 별일이 생기는데 겁나재밌게 보고 나온 영화입니다  마동석씨의 나 무서운사람이다 그 장면 또한 영화로보니 2배로 재밌었구요 김혜수씨의 또다른 매력을 발견하게 된 영화에요 \"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=stelladoyu&logNo=220821981080\",\"contents\":[{\"blog_post_view\":\" 영화 리뷰 굿바이싱글 줄거리 리뷰 후기 내용 결말 김혜수 마동석 김용건 김현수 서현진 곽시양 김태곤 감독 영화 굿바이싱글 줄거리 대한민국 대표 독거스타의 임신 스캔들 이번엔 제대로 사고쳤다  온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받고 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데  대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지고 주연김혜수의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분서주 하는데  통제불능 여배우 그녀의 무모한 계획은 계속 될까 영화 굿바이싱글 리뷰 결말 어제 저녁 자기 전에 보고 잤는 데 꽤나 괜찮은 영화라고 생각되었어요 사실 흥행한 영화는 아니지만 막상 보고 나면 괜찮은 영화들이 꽤 있는 것처럼 저에게도 영화 굿바이싱글은 그런 영화 중 하나가 된 것 같아요 영화에서 철부지 톱스타 고주연 역을 맡은 배우 김혜수의 새로운 모습도 보았고 또 강한 남자의 모습으로만 나오던 배우 마동석의 스타일리스트 역도 색달랐던 것 같아요 그리고 영화에서 주는 청소년의 임신에 대한 시선도 현실적으로 담겨 있어 좋았던 것 같아요 영화 내용은 어느정도 추측 가능하시겠지만 평소 연하 연예인들과 연애를 즐겨 여러 스캔들을 몰고 다니던 고주연이 자신의 남자친구가 바람 피는 것을 알게되고 믿었던 연인으로부터의 배신감과 그리고우연히 들른 산부인과에서 자신의 폐경소식을 접하게 되면서 영원히 자신의 편이 되어줄 아이를 입양하기로 하는 데 입양도 쉽게 선택할 수 있는 문제가 아니기에 또 변덕이 심한 캐릭터로 나와 언제 변덕을 부릴지 모르는 조바심에 친구이자 스타일리스트인 평구마동석이 말리는 데 엘레베이터에서 임신한 청소년 단지를 알게 되면서 단지의 낙태를 말리고 자신이 단지의 아이를 키우기로 합니다 암암리의 거래로 단지는 아이를 낳기로 하고 돈을 받기로 계약을 합니다 그 후에 바람핀 남친에게 복수를 하고자 임신했다고 발표를 하게 되면서 급 인기를 얻게 되는 고주연은 앞뒤 안가리다가 결국 거짓이 만천하에 들어남에 따라 그녀의 연예계 생활은 마침표를 찍을 상황까지 가게 됩니다 한차례 후폭풍이 지나가고서야 단지가 그렸던 그림들을 보며 단지에게 사과를 하고자 찾아가게 된 곳은 미술 경연 대회가 열리는 장소 임산부인 단지는 주변시선에 의해 망설이는 모습을 본 고주연은 단지의 손을 잡고 들어가려 하나 현실적인 시선들은 냉정하기만 한대요 어른과 청소년을 떠나서하나같이 비판하던 건 같은 여성들 보듬어주기보단 임신한 모습에 비판을 해주는 모습이아직까지 우리가 갖고 있는 시선의 한계가 아닌가 싶습니다 그리고 또 임신한 청소년에게는 비판적이면서 상대적으로 아이의 아빠를 탓하진 않는 이중적인 태도도 보여주고요 사과 기자 회견 대신 택한 단지의 옆 그것은 천방지축이던 고주연에게 새로운 시작이지 않았을까 싶습니다 그 후단지는 아이를 출산하게 되고 고주연은 독립영화에 출연하면서 해피엔딩으로 끝납니다 영화에서 보여주는 내용들이 가볍고 진부하다고 느껴질 수도 있겠으나 우리 사회의 모습또한 이렇다고 보여주는 영화인 것 같습니다 코미디 영화지만 그래도 내용은 재미보단 실속이 있는 영화니 보아도 후회하지는 않을 겁니다 이상 영화 굿바이싱글 리뷰를 마칩니다\"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=ybjmd&logNo=220736791891\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 858 목록열기 스크랩 엮인글 영화 굿바이마이프렌드 리뷰 및 줄거리 이것저것 2016 6 15 1131 httpblognavercomybjmd220736791891 번역하기 전용뷰어 보기 이 포스트를 보낸곳 1 영화 굿바이마이프렌드 리뷰 옆집 아이와 자연스럽게 친구가 되는데 얼마되지 않아서 친구가 하늘로 멀리 떠나게 된다는 영화인 굿바이 마이 프렌드를 소개하겠습니다 먼저 영화의 줄거리는 친구들로부터 호모가 아니냐며 놀림감을 당하는 에릭은 옆집에 사는 남자를 만날려고 담장을 넘어와서 같이 다니게 됩니다   그 모습을 친구들이 보고나자 호모 출입금지구역이라면서 길을 잘못 들어왔다고 합니다 그리고 그들은 에릭과 싸울려고 하는데 에릭이 뭐라고 하니까 그냥가게되는데요 그런데 에릭은 덱스터가 에이즈라는 병에 걸렸다는 이야기를 듣게됩니다 그래서 접근하기 두려워하는데요   그러나 굿바이 마이프렌드에서는 저녁식사를 하러 덱스터집에 가기도 하게됩니다   그리고 덱스터가 빨리 낫기를 바라는마음에서 자생적으로 자라나는 풀을 뜯어서 먹으라고 주는데요 그게 탈이나서 였는지 덱스터는 응급실에 실려가게 됩니다 알고보니까 그 풀에는 독성분이 있었다고 합니다 결국 그들은 여행을 떠나기로 합니다 그래서 배를 타고 다른 마을에 갔다가 오는데 약을 잘 안 먹어서 그런지 병세가 악화되어서 덱스터가 죽는다는 내용의 굿바이 마이 프렌드의 이야기입니다   그리고 영화의 마지막부분에 덱스터의 어머니는 에릭어머니에게 주먹을 불끈쥐며 자신의 아들이 죽어서 에릭이 장례식에 참여해야되고 그리고 또는 에릭에게 손찌검 하지말라고 하게된답니다 제가 생각해도 에릭의 어머니는 조금 이기적인면이 있는것 같더라구요    그리고 에릭은 강가에 덱스터의 신발한짝을 떠나보내는데 왜 그랬는지 잘 모르겠습니다 그리고 마지막으로 영화제목처럼 멜로와 감동인 굿바이 마이 프렌드였습니다 태그저장 취소 인쇄 댓글쓰기 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=sundayhello&logNo=220775487370\",\"contents\":[{\"blog_post_view\":\"굿바이싱글 줄거리 내 스타일 영화   저는 너무 재미있게 본 영화인데  별로라는 사람들도 있고 역시 개인취향인거같아요  김혜수언니는 차이나타운이나 타짜 도둑들같은 스타일도 너무 좋지만 이렇게 철없이 귀엽게나오는 역할도 너무 이쁘더라구요 보는내내 이쁘다몸매쩐다연속 굿바이싱글 줄거리 대강알아보면요 재미있는 줄거리이면서도 눈물 주르륵 나는 부분도 있는 감성영화더라구요 마요미와 혜블리가 합쳤으니 저는 너무 좋았음 소재도 그렇고 고주연으로 나오는 김혜수씨 핫스타인데요 나이가 많아용 철 덜들고 사고뭉치 연예인이죠 남자관계 복잡하구요 고주연씨가 사는 집이 제가 살고싶은집임  저는 꼭 수영장 만들거에요  마블리가 매니저임 오래된친구로 나오는데요 정말 좋은친구 같더라구요 하정우아버님은 소속사 사장님이구 옆에 여자분은 미래라고 소속으로 나와용 셋다 든든한 지원군  이 사람은 고주연의 연인인데여 양다리 걸쳐버림  돈많은 고주연한테 차얻고 시계얻고 젊은애들이랑 바람나고 그런 못된놈인데요 고주연이 연예계에서 매장시켜버렸는데 나중에 임신한게 아니라고 의심하고 밝히게 되면서 복수하는 캐릭터    이분은 감초같은 역할하는 산부인과 전문의인데용 고주연이랑 친하심 영원한 내편이 없는거같아서 고주연은 아이를 만들기로 결심하나 나이 때문에 폐경이왔죠 굿바이싱글 줄거리 저는 재미솔솔한게 너무 좋았음 배우들 캐미도 이 여자아이는 산부인과에 낙태를 하러나온 중딩인데요 어른들이 쑥닥되는거를 고주연씨가 사이다로 한마디해줘서 이렇게 둘이 옥상에서 인사하고있는거  당돌한 아이임 고주연은 생각하죠 나는 아이를 못가지는데 필요하고 너는 아이를 지울려고한다면 돈줄테니 거래를하자고  연기를 너무 잘하고 얼굴도 이쁘더라구요 임신을 했는데 아이를 지우려고 했구요 미술을 전공하는데 소질도 있는아이에요 다만 집안이 못살구 언니랑둘이사는데 친언니도 완전 망나니같더라구요 돈을 위해서 결국 애기를 낳아서 고주연한테 주기로 결정 지금부터 둘의 동거시작  굿바이싱글 줄거리 소재괜찮죠 그리고 소속사식구들한테 말하죠 아이를 가진척할거라고  마동석은 나중에 중딩이 맘 바뀌면 어쩔려고라고 걱정하는데 중딩언니가 결국 돈 더받아서 동생은 놔두고 외국으로 남친이랑 놀러감  진짜 욕나오는언니였음 이때부터 두사람은 완전 절친이 되는데요 어린나이에 임신하고 의지할건 고주연뿐인데 고주연은 당당하게 임신했다고 거짓말을 해서 싱글맘 대표로 연예계 전성기를 다시누리면서 중딩한테 신경을 못써줌 여기서 둘은 갈등이 생기는 굿바이싱글 줄거리 전개입니다  열심히 감추는중 들킨만큼 대중도 맘을 돌아서게되고 아무튼 그사이에 사이다같이 남친들한테 복수도하고 너무 재미있는 이야기 요소들도 많아요   나중에 마당이 조그맣더라도 정말 수영장을 만들고 싶더라구요 멍멍이도 귀여웠고 배우들 캐미도 너무 좋았던 굿바이싱글 줄거리 실제로 보시면 더 좋으실듯  강추  전 너무좋았음\"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=express1211&logNo=220853765751\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 484 목록열기 스크랩 엮인글 영화리뷰 영화 굿바이싱글 줄거리 및 결말 2016 11 4 2339 URL 복사 본문 기타 기능 번역보기 오랜만에 연차내고 집에서 뒹굴뒹굴하다가 굿바이싱글 영화를 보았어요  올레TV에는 없는게 없더라구요 김혜수님을 너무너무 좋아하기도하고 마블리마동석님도 나온다고 하니 꼭 꼭 봐야지 라고 생각했던 영화랍니다 영화 굿바이싱글의 줄거리 및 결말까지 그리고 개인적인 후기까지 적어볼게용 국민 밉상녀이지만 잘나가는 노처녀 싱글연예인 대표 주연김혜수 그의 스타일리스트이지만 일을 잘해 매니저 겸 보디가드 겸 그녀를 지키는 평구마동석 시상식 전날에 입술에 필러를 티나게 맞고와 소속사 대표며 스타일리스트며 난리난리 나게하는 천방지축 김혜수가 나오면서 영화 굿바이싱글의 줄거리가 시작됩니다  흙수저이지만 그림을 좋아하고 잘하며 네일아트로 용돈모으는 중학생 단지김현수 나이 어린 남자후배와 사랑했으나 그의 바람으로 인해 상처 받고 자신을 온전히 사랑해 줄 누군가가 필요했던 주연은 평구의 아이들을 보고 아이를 갖겠다고 결심합니다 산부인과에 가서 검사를 받아봤는데 청천벽력같은 폐경기 진단 우울해서 나오는 그때 아이를 지우러 온 단지와 만나게 됩니다 배부른 단지의 모습에 사람들이 수군대자 단지를 감싸주면서 둘은 옥상에서 쭈쭈바를 먹으며 얘기를 하게 되는데요 단지에게 아이를 지우지 말고 낳아만 주면 자신이 키우겠다고 합니다 뱃속에 있는 단지 남친은 골프 국가대표로 뽑혀 해외에 나가게 된 상황 거기에 단지 남친은중학생 엄마 반지를 훔쳐다 주며 단지에게 알아서 하라는 뉘앙스를 풍깁니다 처음엔 말도 안된다고 생각했던 단지도 1억의 돈을 주면 아이를 낳아 주겠다고 합니다 워낙 집이 가난하고 단지의 친언니는 술접대를 하는 상황 거기에 미술을 하고 싶은 마음에 1억을 달라고 말합니다 단지는 주연네 집에 들어와서 먹고 자고 태교하며 둘은 같이 지내게 됩니다 둘은 점점 친해지면서 네일아트도 해주고 태교도 하고 외식도 하러 다니구요 서로 상처 준 남자에게 복수도 하게됩니다 바람핀 전 남친에게 복수하려다가 주연이 임신을 했다고 거짓말을 하게 됩니다 연예계는 주연의 임신으로 난리가 났죠 싱글녀가 임신을 했지만 당당했고 아이를 끝까지 책임진다는 자세로 좋은 이미지를 얻어 여러가지 CF 및 드라마를 캐스팅 받게 됩니다 주연은 바빠지기도 했고 요 위의 이성민 앵커와 썸을 타게 되면서 단지에게 소홀해지고 사이가 틀어지게 됩니다 단지는 주연이 같이가기로 한 산부인과 약속을 지키지 않자 혼자 밖에 나갔다가 주연의 전 남자친구에게 딱 걸립니다 눈치 빠른 주연의 전 남자친구는 주연이 임신한 것이 아니라 단지가 임신했음을 알게되고 그 사실을 연예계에 폭로합니다 설상가상으로 단지에게 돈돈돈거리고 애정이 전혀 없는 친언니와 그의 남자친구가 찾아와 단지를 내세워 주연에게 돈을 뜯어냅니다 단지에게 속았다고 생각한 주연은 크게 실망했고 단지는 미안함과 동시에 주연에 대한 서운함으로 서로를 떠납니다  참고로 단지 친언니  그녀의 남친은 주연에게 단지를 내세워 10억 뜯어냄  단지는 다른 기관에 들어가서 살고  단지의 친언니  그녀의 남친은 단지에게 약간의 돈만 쥐어주고 외국으로 떠납니다  주연은 모든 계약을 파기당하고 위자료에 집까지 팔고 기획사는 해체되게 됩니다 주연은 이기적인 자신의 마음과 생각 그리고 평구의 아내에게 쓴소리를 듣고 생각해보다가 단지와 오해가 있음을 깨닫고 단지에게 사과하고 싶어합니다 그시각 단지는 미술대회에 참가하고 싶지만 배가 부른상태라 주저하고 있었고 주연은 찾아가 단지에게 힘을 줍니다 주변 다른 엄마들이 극구 반대했지만 주연의 간곡한 호소로 결국 단지도 미술대회에 참가하게 되고 미술대회 도중에 진통으로 쓰러지게 됩니다 결국 단지는 수술실에서 아이를 낳게되고 주연은 연예계에 복귀하기 어려워 돈을 적게주는 독립영화를 찍으며 단지  단지의 아이와 함께 살게 됩니다 마지막에는 기획사 사람들  평구네  단지  단지 아이  주연 등이 모두 모여 평구네집에서 마지막 장면으로 끝납니다 단지는 아이를 무사히 낳았고 주연과 단지  아이가 모두 함께 살게 다만 주연이 옛날처럼 잘나가는 스타는 아니죠  독립영화를 하며 살고 있구요 단지는 남친도 새로 사귀는 것 같고 꿈을 찾아서 이것저것 시도해보는 중 아참 이성민 앵커와 주연도 해피엔딩 영화 굿바이싱글의 줄거리결말 이였습니다  결론은 해피엔딩이에요 영화 굿바이싱글 줄거리 어떠셨나요 단지의 심정변화가 더 자세히 나오면 좋겠다고 생각했어요  결말은 해피엔딩으로 좋더라구요 눈물도 찔끔 흘렸답니다  재밌었어요 태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 이 저작물은 아래 조건 만족 시 별도 허가 없이 사용 가능합니다 저작자 명시 필수 영리적 사용 불가 내용 변경 불가 인쇄 댓글 2 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=maisonc&logNo=220201303489\",\"contents\":[{\"blog_post_view\":\" 우정영화 굿바이마이프렌드 줄거리    평범한듯해 보이지만 평범하지 않은 소년들의 이야기 우정영화라고 하기에는 너무나 그 속에 가슴 따뜻한 내면들이 많기에  조심스레 적어본다  에이즈에 걸린소년 수혈을 받다가 에이즈에 걸린아이  혼자 외로운 아이덱스터 그리고 호모라고 놀림받는 에릭  둘은 서로 외롭다 벽을 하나사이에 두고 그들이 외롭다고 표현된 시각 참 남달랐다 에이즈라고 해서 외로운것이 아닌 다른사람에게 외면당해서 외로운 모습과 병이걸려 외로운것 그 상황이 같음을 이야기 하는 듯한 표현       이 영화를 보면 참 감동적이다  병이 걸린 덱스터를 위해 무단히 노력을 하는 에릭 쵸콜릿식단 나뭇잎 끓여먹이는등 그 진지함은  정말로 낫지 않을까 라는 엉뚱한 기대감을 하게 만든다   에릭에 모험심 그게 부러운아이 덱스터 담장넘어 에릭과의 하루가 즐거운 아이    그의 어머니는 그런 행동들 가끔은 투정부리며 변해가는 덱스터가 이상하게 느껴서 에릭을 멀리하려가가  점점 밝아지는 아들을 보고는 자신도 모르게 의지를 하게 된다   자식에게 무관심한 워커홀릭 엄마 편부모 가정에 대한 환경과 외로운 감정을 너무나 잘 표현한 영화  둘도없는 친구가 되면서도 부모보다 의지를 더욱 하게 된다    야채와과일만 먹어서 그렇다며 쵸콜릿만 먹게 만들기도 하고  정말 친 형처럼 아껴준다  그러던중 신문에서 발견한 에이즈치료약   에릭이 먹인 나뭇잎차 때문에 응급실로 가게된 덱스터  하지만 그들은 여행을 감행하고 정말로 절실하게 약을 구하러 다니게된다  하지만 점점 쇠약해져가는 덱스터  결국 집으로 돌아가게 되는 두 아이 그들이 놀던 둑이 무너지지만 에릭은 아무것도 못하는 상황  그 모습은 덱스터의 병을 어찌할수 없는 상황을 표현한것    마지막 덱스터의 죽음앞에 아무것도 못하는 에릭  마지막에 운동화를 바꿔 강물에 띄우는 모습에서  정말 잔잔한 눈물을 훔치게된다  왠지 보면서 슬프지만 미소가 나는 슬픔  그 슬픔이 가슴이 따뜻해지는 영화    우정영화 굿바이마이프렌드 줄거리  왠지 따뜻한 가슴이 그리울때 그 감성을 자극하기 위해  잔잔함이 뭇어나는 행복을 맛볼수 있는 굿바이마이프렌드를 추천해본다     위에 배너를 누르시면 메종드포레 홈페이지를 보실 수 있습니다  \"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=phj9899&logNo=220732184570\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 503 목록열기 스크랩 엮인글 굿바이싱글 마동석 김혜수의 코믹영화 줄거리와 결말 영화  세상이야기 2016 6 9 2310 httpblognavercomphj9899220732184570 번역하기 전용뷰어 보기   오는 6월29일 개봉하는 코믹로맨스 영화 굿바이싱글 오랜만에 만나는 핫한 코미디 로맨스 영화가 될것 같습니다 국민배우 김혜수와 마동석이 출연하는 이 영화는 지난 6월9일에 시사회를 열었고 많은 관람객과 네티즌에게 관심을 끌고 있기도 합니다 어떤 스토리가 전개되고 얼마나 많은 사랑을 받을지 기대가 됩니 다좋은반응을 기대해 봅니다 김혜수는 2015년 차이나타운 이후 2016년 영화 소중한여인에 이선균과함께 출연했 지만 아직 미개봉상태이고 굿바이싱글은 오는 6월29일 개봉예정 입니다 마동석은 오는 7월 개봉예정인 부산행과 2017년 개봉예정인 신과함께에 출연했 고 오는29일에 굿바이싱글을 선보입니다  지난 6월9일에 있었던 굿바이싱글시사회에 초대 되지 못해서 시사회후기에 대해 검색을 통해 어떤 영화인지 알려 드리려고 합니다 굿바이 싱글시사회리뷰글을 인용하자면 미혼모에 대 한 생각으로 감독님이 만든영화가 아닌가 싶고 이로인해 미혼모가 미화될까 염려는 되지만 많은 학생 미혼모가 조금은 힘이될수있 는 영화 일것 같다는 이야기와 김혜수의 연기가 좋았고 코믹장면이 많이 나온다고 리뷰해주셨 습니다 기해봅니다 굿바이싱글 마동석 김혜수주연 코믹영화 줄거리및 결말 주목받던 독거스타 김혜수주연분는 인기가 떨어지고 남자친구에게 공개적으로 이별을 통보 받은뒤 영원히 내편을 만들기에 돌입 합니다 굿바이싱글 마동석평구분은 김혜수 의 임신발표로전국민 스캔들로 확산되자 소속사 식구들과 이를 해결하기 위해 동분 서주합니다 마동석은 김혜수의 어린시절 부터 절친이였습니다 X알 친구죠  굿바이싱글 줄거리 굿바이싱글 김혜수 마동석 결말 블로거 탄탄님께서 김혜수 였기에 웃기지 않았을까 그리고 유추가 가능한 결말이라 좀 아쉬웠다 그러나 코믹장면이 많이나오고 그냥 코미디 영화라고 하기보단 미혼모문제에 대한 아픔도 담고있다 그냥 단순하게 보면 좋을것 같다는 의견이 였습니다 태그저장 취소 인쇄 댓글 11 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://victoryshadow.tistory.com/5\",\"contents\":[{\"blog_tistory2\":\"영화 굿바이 싱글 내용 줄거리 결말 김혜수 마동석  싱글이여 안녕 영화 굿바이 싱글입니다 이번에 엄청난 예매율을 자랑하는 영화가 있다고 해서 바로 극장에 달려가서 영화를 보았어요 이 영화 같은 경우에는 김혜수씨가 주연으로 나오면서 정말 인기리에 상영중이라고 하는데요   요즘 한국 영화 같은 경우에는 여성배우가 설자리가 없다고 해요 그러다 보니까 여성 배우가 주연으로 나오는 영화 또한 많이 없는데요   이번에 이렇게 김혜수씨가 주연으로 나오는 영화를 볼 수 있어서 얼마나 좋았는지 몰라요 내용도 생각보다 굉장히 신박하더라고요 그래서 정말 재미있게 보았답니다   이 영화 같은 경우에는 김혜수씨가 다 지난 여배우로 나와요 하지만 그녀는 정작 이렇게 오랜세월 힘들게 일을 했지만 자기 편 하나 없다는 것에 정말 많이 힘들어하고 어느날 아이만큼은 자신의 편이 되줄거라고 생각하고 가짜로 임신한척을 하죠    그녀 뒤에는 중학생인데 임신을 한 아이가 있고 말이에요 그 아이는 아이를 낳고 김혜수에게 주는 목적으로 김혜수씨는 임신했다는 것으로 새롭게 다시 인기를 얻는 목적으로 아이를 낳을 동안 같이 살기로 하죠   존칭을 써가며 살지만 이후에는 서로 친해지면서 가까워지게 되는데요 김혜수씨가 점차 바빠지게 되니 멀어지게 되고 나중에는 결국에 아이 임신이 가짜였다는 사실도 밝혀지게 된답니다    그러다가 마지막에는 김혜수씨가 자신의 편이 존재하지 않는것이 아니라 자기 자신이 문제라는 것을 알게 되죠 그러면서 해피앤딩 많이 배울 수 있는 영화였고 또 힐링이 되는 영화였답니다 무엇보다 코미디적인 요소와 감동코드가 적절히 배합된듯해서 정말 좋았어요 공감sns신고 저작자표시비영리변경금지 영화 카테고리의 다른 글 영화 스타워즈 에피소드2 클론의 습격 줄거리 스포 내용0 20160725 한지민 정재영 영화 플랜맨 줄거리 결말 내용 느낀점0 20160721 김민준 영화 웨딩스캔들 줄거리 내용 결말0 20160720 려원 봉태규 영화 두얼굴의여친 줄거리 결말 내용0 20160719 영화 굿바이 싱글 내용 줄거리 결말 김혜수 마동석0 20160718 트랜스포머2 패자의 역습 영화 줄거리 내용 감상문0 20160715\"}],\"dest_fields\":[\"blog_tistory2\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=totoro2010&logNo=100105646813\",\"contents\":[{\"blog_post_view\":\" 굿 바이 Good  Bye   사랑한다는 말보다 아름다운 인사 굿 바이 도쿄에서 잘나가는 오케스트라 첼리스트인 다이고모토키 마사히로 1억이 넘는 돈을 대출해서 장만한 첼로로 열심히 연주해보지만 관객도 별로 없는 공연장 인기도 없고 유지하기도 힘들고 그렇게된 형편으로인해서 악단이 해체되고 갑작스럽게 백수 신세가되어버린다 아내 미카히로스에 료코과 함께 어머니가 남겨주신 유일한 재산인 집으로 이사를 가게된다이사온 집엔 어머니와 아버지의 흔적이 가득하다   일자리를 찾던 그는 우연히 연령무관 고수익 보장이라는 파격적인 조건의 여행 가이드 구인광고를 발견하고 두근두근 면접을 보러 간다 면접은 1분도 안되는 초스피드로 진행되고 바로 합격한 다이고 그러나 여행사인줄만 알았던 회사는 인생의 마지막 여행을 떠나는 사람들을 배웅하는 납관일을 하는 곳 도저히 할 수 없을 것같은 다이고는 갈등을 하는데 마침 이쿠에이야마자키 츠토무가 내미는 돈뭉치에 마음이 흔들려 버리고 하루 아침에 화려한 첼리스트에서 초보 납관도우미가 된 다이고  7살때 아버지가 다른 여자와 떠나버리게 되면서 아버지에 대한 미움과 원망을 가지고 살았던 자신의 모습을 떠올리는 다이고 어릴적 자신에게 처음 첼로를 안겨준 아버지의 모습을 떠올려보지만 아버지의 얼굴을 흐릿하게 떠오르지 않고 옛적에 쓰던 작은 첼로를 열어보는데 그 곳엔 아버지가 건내준 큰 돌멩이가 신문에 쌓여 함께 넣어져있다 그 돌멩이는 멀리 있는 사람에게 자신의 소식을 전하기 위해 돌멩이를 주고받으며 편지형식으로 서로 전했다는 이야기를 하며 아버지와 함께 나누었던 것이다 그때의 아버지의 얼굴은 아마도 기억하고 싶지 않은 이유로 지웠을 것이라 생각되어진다  언어가 없던 시절 사람들은 돌을 통해 자신의 마음을 상대방에게 전했다고 해  마음이 편안하면 부드러운 돌을 무슨 일이 있으면 울퉁불퉁한 돌을 주는 것이다  돌을 받은 사람은 부드러운 돌이면 안심하고 거친 돌이면 걱정을 했다고해  다이고 아버지  그리고 보면 다이고에게 건내주었던 돌은 울퉁불퉁한 돌이었고 다이고가 아버지에게 건낸 돌은 작고 둥글둥글한 돌이였다 모든 것이 낯설고 거북하지만 차츰 베테랑 납관사 이쿠에이가 정성스럽게 고인의 마지막을 배웅하는 모습에 찡한 감동을 배워간다 첫번째 일은 자살한 사람의 시신을 배웅해야하는일 첫 일인데 너무나 지독한 일이 걸려 구토에 시달리는 다이고   다음엔 극장으로 가라는 연락을 받고 달려단 다이고는 홍보 영상을 찍게되고 주인공으로 누워있는 시신 역할을 담당하게된다 이왕 시작한 일 열심히는 해보려고 하지만 시신역할이란 영 나중에 문제가 되지 않을까 걱정을 하지만 그를 안심시키는 이쿠에이 홍보영상물이기때문에 아무나 볼수 있는 것이 아니라고 그들 달래고 촬영에 들어간다   그리고 계속되어지는 일들을 통해 다이고는 납관이라는 일이 너무나 필요하고 신성한 것이라는 것을 깨닫게된다 그리고 오랫만에 돌아온 고향에선 다이고를 알아보는 사람들이 한둘씩 나타나고 그 중 오래된 목욕탕에서 그를 알아보는 친구의 어머니를 만나게된다 자신의 아버지가 집을 나갔을때도 사람들 앞에서는 울지않고 목욕탕 한구석에서 슬피 울던 아이였다며 잘해주라며 미카에게 당부한다   두번째 일로 가게된 곳에서 5분이 늦었다는 이유로 유가족들에게 쓴소리를 듣게되면서 시작된다 하지만 떠나는 사람을 배웅하는 모습을 보던 유가족들은 나중에 감사함을 표현하게된다  오늘 아내는 지금까지 제가 본 중에 가장 예뻤습니다 고맙습니다  그 일로 더 이일을 사랑하기 시작하게된 다이고 하지만 아내 미카히로스에 료코와 친구들은 다이고에게 당장 일을 그만두라고 반대를 하게된다 하지만 시간이 흐르면서 일에 대한매력을 느끼게 되면서그만두지않겠다고 말을 한다 그 일로 인해서 미카는 친정을 떠나버리고 홀로남겨진 다이고는 열심히 일을 하며 지내게 된다 그러던 어느날 부인이 친정으로 갔다는 이야기를 듣고 이쿠에이와 함께 식사를 하게된 다이고  맛있단 말이지 미안스럽게도  아마도 먼저 떠난자들에 대해 남겨진자들이 살아가며 느끼는 좋은것들에 대해 미안한 마음을 이렇게 표현한것은 아닐까하는 생각이 든다   나름대로 뿌듯함을 가지고 열심히 일하는 사이 미카는 다시 다이고의 곁으로 돌아온다 이유는 임신 다시 그 일을 그만 두라고 설득을 하게되는데 그날 갑자기 자주 들리던 단골 목욕탕의 주인 아주머니의 죽음 소식이 들리고 그 곳으로 가게된 다이고와 미카 다이고의 배웅모습을 지켜보며 감동을 받은 미카는 더이상 그를 말리지 않게된다  죽음은 문이야 문을 열고나가면 다음세상으로 가는거지 그래서 죽음은 문이라고 생각해   그리고 며칠이 지난 어느날 아버지에 사망 소식을 통보받게되는 다이고 아버지에 대한 원망으로 인해 절대로 가지 않겠다고 하지만 미카는 그래도 마지막 모습을 뵈야한다며 설득하려한다 갈등 속에 결국 다이고는 미카와 함께 아버지의 시신이 있는 바닷가 어느곳으로 찾아가게된다 그리고 아버지의 시신앞에서 아무렇지 않게 앉아 있는데 상조회사 직원들이 들어와 아버지의 시신을 마구잡이 식으로 나무 관에 넣는 모습에 그만 화가나서 자신이 하겠다는 말을 한다 그리고 직접 자신의 손으로 아버지의 시신을 배웅하게된다 아버지의 몸을 씻기던 중 아버지에 손을 펴는데 무엇인가 쥐고 있는 것인지 잘 펴지지 않고 결국 펴진 손안엔 자신이 7살때 아버지와 주고 받은 작은 돌멩이가 쥐어져 있음을 알게된다 눈물을 흘리며 아버지의 몸을 닦고 결국 아버지가 자신을 깊이 사랑하고 있었다는 것을 깨닫게 되는 다이고    이렇게 영화는 막을 내린다 영화는 잔잔하고 소박한 느낌을 주는 형식으로 그려져있다 그 안에서 너무나 평범하게 일어나는 것들을 마지막 떠나는 여행으로 비유하며 아름다운 마지막을 선사하고 있다  우리나라에서는 시신을 함부로 다루는 경향이 있다고 한다 벌거벗겨 마구잡이 식으로 닦는 무례한 행동들이 많은 가족이 있는 앞에서 행해진다고 들었는데 이 영화속에서는 돌아가신 분이지만 너무나 아름답게 마무리 하도록 도와주는 장면들이 나오고 있다 그리고 떠나는 자와 남겨지는 자들의 마지막 정리할 수 잇는 그리고 추억할 수 있는 시간들을 준다는게 참 아름답다고 여겨졌다 또한 직업에 대한 시각도 변해야 된다는 뻔한 생각을 해보지만 역시나편견에서 벗어나기란 힘들다는 결론이다\"}],\"dest_fields\":[\"blog_post_view\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=blueyoung1203&logNo=220753020348\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 176 목록열기 스크랩 엮인글 영이의영화 영화굿바이싱글후기 김혜수마동석케미 굿바이싱글보고왔어요줄거리 2016 7 4 1141 URL 복사 본문 기타 기능 번역보기 영화굿바이싱글 후기줄거리 요즘 핫한 영화 영화굿바이싱글 보고왔어요 영화굿바이싱글 보면서 역시 김혜수 이 말이 절로 나오더라구요 김혜수 몸매도 얼굴도 연기도 여전했다 명품 그자체 솔직히 제가 김혜수 팬이어서 그러는게 아니라 굿바이싱글 김혜수가 아닌 다른 여배우가 주인공이었다면 결코 재밌지 않았을 것 같다는 생각이 들더라구요 김혜수의 유쾌통쾌한 코믹연기 덕분에 간만에 유쾌하고 즐겁게 영화를 본 것 같아요 개인적으로 CGV보다 메가박스가 더 좋은 일인 이날도 어김없이 동탄메가박스에서 굿바이싱글을 봤어요 동탄메가박스에서는 할인관람권을 매번 주는데좀 더 저렴하게 영화를 볼 수 있어 좋아요 동탄메가박스1주년기념이벤트 콤보3천원할인권 두개나 나왔어요 영화굿바이싱글을 보면서저녁시간이니 출출하니깐 팝콘보다는 동탄메가박스 바로 앞에 있는 핫도그집에서 핫도그를 사다가 먹었어요  불갈비핫도그  바베큐치킨핫도그  음료2개 가격 12000원  다 먹고나면 입이 심심할까봐 커널스팝콘도 샀어요 말도안돼 바가지다 커널스팝콘 가격 3000원 앞으로는 핫도그만 먹기로 핫도그는 영화시작전에 폭풍흡입 핫도그가 내용물이 막 떨어지거든요 잘먹어야해요 영화시작하면 깜깜하니깐 조금이나마 밝을때 편안하게 먹으면 좋잖아요 배도 고프니 아 얼마나 맛있던지 배고플때 먹으면 모든지 다 맛있는 것 같아요 영화굿바이싱글 줄거리 대한민국 대표 독거스타의 임신 스캔들이번엔 제대로 사고쳤다 온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받고 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데 대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지고 주연김혜수의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분서주 하는데 통제불능 여배우 그녀의 무모한 계획은 계속 될까 영화굿바이싱글 선 웃음 후 감동 영화굿바이싱글 후기 개인적으로 영화굿바이싱글 간만에 유쾌하고 즐겁게 본 영화였어요 굿바이싱글은 후반부에 살짝 루즈해지는 부분이 있었긴하지만 적당한 코미디와 부담스럽지 않은 메시지까지 포함되어있는 영화랍니다 김혜수와 마동석의 환상의 캐미가 돋보였던 영화 김혜수 마동석 연기 최고였어요 마동석은 이 영화에서 감칠맛을 더해주는 배우였어요 또한아역배우 김현수의 재발견 대박 더불어 김혜수의 여전한 미모와 몸매에 빠져들며 본 나 너무이쁘닷 이런 몸매로 하루만이라도 살아보고싶네요 굿바이싱글 본 뒤로 집에와서 먹고싶은 생각이 사라졌어여 여자아이는 임신으로 떳떳하지 못하고 꿈까지 내려놓았는데 잘못은 둘이했는데 왜 그 남자아이는떳떳하게 국가대표로 발탁되서 외국에 나갔다 라는 김혜수의 속시원한 대사가 생각이 나네요 후반부에 눈물이 고이더라구요 두 사람의 책임인데 왜 우리사회에서는 여자만 고개를 숙여야하고 따가운 눈총을 맞고 손가락질을 받아야하는지 영화굿바이싱글은 김혜수 민낯 막춤 등 역대급 코믹연기도 볼 수 있고 감동도 받는 그런 영화더라구요 임산부들 남자들보다는 여자들이 보면 좋은 영화일 것 같아요 7월엔 웃어라 저는 부모님도 보시라고 따로 예매해드렸거든요 아빠  너 마지막에 안울었어 슬퍼서 아빠는 울었다 아가씨보다 5배는 훨씬 재밌더라 아빠의 말에 공감 여러분들도 오랜만에 재밌고 따뜻한 메시지 담겨있는 영화보러 극장나들이 다녀오세요  어바웃영이의 포스팅은 제 돈주고 관람하고 제 개인적인 의견으로 작성한 후기입니다  태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 인쇄 댓글 151 공감 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://blog.naver.com/PostView.nhn?blogId=jin7daks&logNo=220742769916\",\"contents\":[{\"blog_post_body\":\"포스트 목록 전체보기목록열기 805 목록열기 스크랩 엮인글 영화 보고싶어요 영화기대를 한몸에 받고 있는 굿바이 싱글 줄거리 김혜수 코믹연기 2016 6 22 1005 URL 복사 본문 기타 기능 번역보기 안녕하세요 좋은아침입니다 에구 벌써 일주일의 반이라는 시간이 흘렀네요 시간이 왜이렇게 점점 빨리흐르는지 아무튼 이런 우울한 잡생각 떨쳐줄 재밌는 영화 오늘은 가져와봤는데요 바로 김혜수 마동석 주연의 굿바이 싱글 줄거리 정보 가져왔답니다 굿바이 싱글 감독 김태곤 출연 김혜수 마동석 김현수 개봉 2016 대한민국 상세보기 오는 6월29일 개봉을 앞둔 따끈따끈한 예정작인데요 TV에서 나오는 예고편만 봤는데 빵빵터지고 너무 기대가 되더라구요 조연에는 또 오해영으로 인기를 끌고있는 서현진과 우리의 아버지 김용건 그리고 당찬 아역 김현수까지 출연해서 관심을 한몸에 받고 있답니다 김혜수  저 예쁜 얼굴이랑 몸매로 드레스차려입고 집에서 아이스크림 퍼먹는 이 스틸컷이 왜이리 와닿던지 한물 간 스타가 뜨기위해 벌리는 해프닝을 그린 굿바이 싱글 줄거리 한번 알아볼까요  굿바이 싱글 줄거리  대한민국 대표 독거스타의 임신 스캔들 이번엔 제대로 사고쳤다 라면거 굿바이 싱글 줄거리 시작되는데요 한번 가 봅시다 굿바이 싱글 줄거리 온갖 찌라시와 스캔들의 주인공인 톱스타 주연김혜수 그러나 점차 내려가는 인기와 남자친구의 공개적 배신에 충격을 받게 됩니다 굿바이 싱글 줄거리 그런 주연은 영원한 내 편을 만들기 위해 대책 없는 계획에 돌입하게 되는데 대표 독거스타의 임신 발표는 전국민 스캔들로 일이 커지게 되고 주연의 불알친구이자 스타일리스트인 평구마동석와 소속사 식구들이 안절부절하며 뒷수습에 동분거주 하게 됩니다 통제불능 여배우 그녀의 무모한 계획은 계속 될까 라면서 굿바이 싱글 줄거리 끝이 납니다  톡톡튀는 연기와 코믹한 면으로 똘똘뭉쳤을 거 같은 이번 김혜수의 연기 평구 라는 이름으로 등장하는  마동석과 함께 재기발랄한 캐미를 터트릴거 같습니다 이러니 개봉전부터 난리가 났죠 당찬 연기를 보여줄거같은 김현수도 기대가 되고 서현진의 모습도 궁금해지니 이거이거 언제29일까지 기다릴까요 굿바이 싱글 줄거리 보니 궁금해서 참을수가 없습니다 아무튼 정말 재밌고 힘든일상 시원하게 풀어줄만한 영화라는것은 확실하니 가서 시원하게 웃고 오는것도 좋겠네요 그럼 오늘 준비한 영화소식 굿바이 싱글 줄거리 포스팅은 여기까지구요 행복한 수요일 되시길 그럼 예고편 두고 가겠습니당 굿바이 싱글 2차 예고편 태그저장 취소 PC모바일 어디서나 수정 가능한으로 쓴 글입니다 인쇄 댓글 1 댓글쓰기 11 이전 다음 포스트 목록 전체 카테고리의 다른 글 전체 포스트 보기 카테고리 다른 글 페이지 네비게이션\"}],\"dest_fields\":[\"blog_post_body\"]},{\"sub_url\":\"http://reviewisgood.tistory.com/37\",\"contents\":[{\"blog_tistory2\":\"영화줄거리를 결말까지 초간단 요약합니다 결말 스포가 있으니 주의하세요 김혜수가 임산부가 된다는 영화포스터만으로 화제가 되고 있는 영화 160629에 개봉예정이고 현재 시사회를 진행했는데 대박이라는 평가까지는 없지만 별로라는 평도 그다지 보이지 않아 대체로 무난하다는 평가 초중반 코믹 후반 감동이 법칙이다시피한 한국 코미디영화의 공식에서 그다지 벗어나지 않는다는 점은 아쉽지만 주연들의 호연과 김혜수의 대체불가한 연기는 강점  줄거리를 소개하면 영화상의 김혜수도 연예인 그러나 영화상에서는 현재 나이가 들면서 인기는 떨어지고 찌라시나 스캔들 등에서나 볼 수 있는 배우가 된 상황 김혜수의 매니저 마동석은 이런 김혜수의 뒷치닥거리를 하느라 매번 고생한다 서현진은 마동석과 부부로 나오고 3아이의 엄마로 나옴 그렇게 장면이 많이 나오진 않음 이후 연하의 남친 곽시양과 결혼까지 생각했으나 곽시양이 양다리를 핀 것에 충격을 먹고 자신의편이 되줄아이를 결혼을 하지 않고 만들고자 한다 그런데 입양이나 임신이나 김혜수에게는 버거운 것이어서 고민하던 중 마침 임신한 여중생을 만나게 됨 여중생은 아버지도 외면해버려서 낙태를 생각하고 있었음 그래서 김혜수는 그 중학생에게 거액의 돈을 주는 대신 애가 나오면 김혜수의 아이가 되는 것으로 하기로 함 그래서 김혜수는 임신한 것처럼 연기를 하고 대중들에게도 임신한 것을 발표함 미혼스타였던 김혜수의 임신발표는 세간을 강타하는 핫소식이 되고 그로 인해 왕년의 스타였던 김혜수는 다시 인기를 한몸에 받는다 그리고 임신한 중학생은 김혜수와 같이 사는데 각자 삶의 어려움을 가지고 있던 둘은 점차 어려움을 서로 의지하는 사이가 된다 그러다가 김혜수가 임신한 것이 거짓임이 드러나고 코믹일색이었던 초중반부와 달리 슬픔을 자아내는 이야기들이 나온다 그리고 조금은 뻔하지만 뻔하지 않은 결말로 새로운 가족이탄생하는것으로 영화는 끝난다 영화 굿바이 싱글 등장인물흥행 간략분석은 링크클릭 httpreviewisgoodtistorycom50  읽으셨던 포스팅이 유익하셨으면 아래 공감버튼 눌러주시면 감사하겠습니다 로그인 필요없어요  공감sns신고 저작자표시비영리변경금지 영화 리뷰 카테고리의 다른 글 인디펜던스 데이 리써전스줄거리 간략요약결말스포O0 20160629 영화 비밀은 없다 줄거리 간략 요약결말스포O1 20160628 영화 굿바이 싱글 줄거리 간략요약결말스포 조금0 20160625 영화 동갑내기 과외하기 줄거리 간략 요약결말스포O0 20160623 영화 정글북 줄거리 간략 요약결말스포O0 20160620 특별수사 사형수의 편지 줄거리 간략 요약결말스포O0 20160618\"}],\"dest_fields\":[\"blog_tistory2\"]}],\"collectCnt\":20,\"failResultArr\":[\"filtered___http://catcatat.tistory.com/1244\",\"filtered___http://blog.daum.net/_blog/hdn/ArticleContentsView.do?blogid=0f7bQ&articleno=437&looping=0&longOpen=\",\"filtered___http://heathcafe3.tistory.com/166\",\"filtered___http://nonameman.tistory.com/6\",\"filtered___http://catcatat.tistory.com/633\",\"filtered___http://hyunyul.com/220761050634\"]}";
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(str);
        JsonObject resultObj = (JsonObject) element;

        JsonArray resultArr = (JsonArray) resultObj.get("resultArr");
        System.out.println("#Result:"+resultArr.size());
    }
    */

    @Test
    public void test_date_get() {
        List<String> s = DateUtils.getArrayDateFromInput(-6);
        System.out.println("#result:"+s.toString());
    }

    @Test
    public void test_filterLastGenre() {
        String req = "미국 영화";
        req = StringUtil.filterLastGenre(req);
        System.out.println("#RES:"+req);
    }

    @Test
    public void test_prcAwardsStr() {
        Set<String> res = StringUtil.prcAwardsStr(null, "35회 상파울로 국제영화제. 2011");
        System.out.println("#RES1:"+res.toString());
        res = StringUtil.prcAwardsStr(res, "91회 아카데미 시상식. 2019");
        System.out.println("#RES2:"+res.toString());
    }

    @Test
    public void test_trans_json() {
        String req = "a, b, c";
        JsonArray jarr1 = JsonUtil.convertStringToJsonArrayWithDelemeter(req, ",");
        JsonObject res = new JsonObject();
        res.addProperty("reqstr", req);
        res.add("jarr1", jarr1);
        System.out.println("#res:"+res.toString());
    }


    @Test
    public void test_replaceExt() {
        String sss = "df,|^dfkf";
        System.out.println("#RES:"+sss.replace(",|^","\t"));
    }

    @Test
    public void test_convert() {
        String befo = new String("한글".getBytes(Charset.forName("utf-8")));
        System.out.println("#Bf:"+befo);
        String aftr = StringUtil.convertUTF8toMS949(befo);
        System.out.println("#Af:"+aftr);


    }
}