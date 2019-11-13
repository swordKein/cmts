package com.kthcorp.cmts.util;

import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String removeMetaTag(String req) {
        req = req.replace("metawho","who");
        req = req.replace("metawhen","when");
        req = req.replace("metawhat","what");
        req = req.replace("metawhere","where");
        req = req.replace("metaemotion","emotion");

        return req;
    }

    public static String addMetaTag(String req) {
        req = req.toUpperCase();

        req = req.replace("WHO","METASWHO");
        req = req.replace("WHEN","METASWHEN");
        req = req.replace("WHAT","METASWHAT");
        req = req.replace("WHERE","METASWHERE");
        req = req.replace("EMOTION","METASEMOTION");
        req = req.replace("CHARACTER","METASCHARACTER");
        req = req.replace("SUBGENRE","LIST_SUBGENRE");

        return req;
    }

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

    public static ArrayList<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        ArrayList<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public static double getRatioEngPerKor(String txt) {
        double ratio = 0.0;

        int cntEng = StringUtil.getCountAlphabets(txt);
        int cntKor = StringUtil.getCountKorean(txt);

        System.out.println("## cntEng:"+cntEng+" // cntKor:"+cntKor + " ==> "+(cntEng+1.0)/(cntKor+1.0));

        ratio =  (cntEng + 1.0) / (cntKor + 1.0);

        return ratio;
    }

    public static int getCountAlphabets(String reqTxt) {
        String pattern="[a-zA-Z]";
        int cnt = countPatternMatch(pattern, reqTxt);
        return cnt;
    }

    public static int getCountKorean(String reqTxt) {
        String pattern="[가-힣]";
        int cnt = countPatternMatch(pattern, reqTxt);
        return cnt;
    }

    public static int getCountNumber(String reqTxt) {
        String pattern="[0-9]";
        int cnt = countPatternMatch(pattern, reqTxt);
        return cnt;
    }

    public static int countPatternMatch(String pattern, String reqTxt) {
        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(reqTxt);
        int count  = 0;
        //int count2 = 0;
        for( int i = 0; m.find(i); i = m.end()){
            //if(count<4){
                count++;
               // count2=i;
           // }
        }
        //System.out.println(count+"  " + count2); //특정문자열(Pattern)의 갯수
        //System.out.println(s.substring(0,count2)); //특정 문자 갯수까지 출력하기
        return count;
    }

    public static String removeAllTags(String req) {
        return req.replaceAll("\\<[^>]*>","");
    }

    public static String removeAllTags2(String req) {
        String res = req.replaceAll("\\<[^>]*>","");
        res = res.replace("\n", "");
        res = res.replace("\\n", "");
        res = res.replace("\r", "");
        res = res.replace("\\r", "");
        res = res.replace("  "," ");
        res = res.replace("[","");
        res = res.replace("]", "");
        res = res.replace("\"","");
        res = res.replace("\'","");
        return res;
    }

    /*
    public static List<HashMap<String, Double>> getMapArrayFromStringSeperatedComma2(String content) {
        List<HashMap<String, Double>> result = new ArrayList<HashMap<String, Double>>();

        //content = "{수아=66, 영화=46, 차=30, 기섭=25, 범인=24, 사람=23, 자신=21, 눈=20, 여자=19, 사건=18, 연기=18, 시각=17, 김하늘=16, 경찰=16, 목격자=14, 스릴러=13, 형사=13, 장애=12, 때=12, 장면=12, 말=11, 유승호=11, 앞=11, 작품=11, 진술=10, 사고=10, 동현=10, 블라인드=10, 조=9, 조형사=9, 시각장애=9, 남자=9, 캐릭터=9, 택시=9, 일=9, 슬기=8, 위험=8, 동생=7, 여성=7, 모습=7, 안=7, 감정=7, 느낌=7, 부분=6, 배우=6, 등장=6, 수사=6, 집=6, 감각=6, 뺑소니=6, 고아원=6, 처음=6, 상황=6, 길=6, 후=6, 점=5, 속=5, 병원=5, 주연=5, 조희봉=5, 시간=5, 곳=5, 역할=5, 순간=5, 극복=5, 차안=5, 결국=5, 목격=5, 안내견=5, 명진=5, 관계=5, 외제=4, 스릴러영화=4, 눈물=4, 아이=4, 아래=4, 인물=4, 외제차=4, 장애인=4, 반항=4, 개봉=4, 영화속=4, 실종사건=4, 친동생=4, 표현=4, 해결=4, 역=4, 죽음=4, 마음=4, 시각장애인=4, 개인=4, 전화=4, 반전=4, 휴대폰=3, 냄새=3, 날=3, 본인=3, 감독=3, 연쇄=3, 술=3, 목=3, 억지=3, 재학=3, 물=3, 나중=3, 얘기=3, 한국=3, 안상훈=3, 조형=3, 출연=3, 일반=3, 최근=3, 의사=3, 목숨=3, 이해=3, 뒤=3, 살인범=3, 시계=3, 남성=3, 연출=3, 피해자=3, 옆=3, 존재감=3, 머리=3, 밤길=3, 얼굴=3, 이상=3, 희망=3, 전=2, 스포일러=2, 산부인과의사=2, 김하늘유승호=2, 블라인드감독안상훈=2, 수사망=2, 줄거리=2, 여타=2, 의견=2, 끝=2, 기사=2, 자연=2, 기타=2, 이름=2, 상영시간=2, 상=2, 혼자=2, 습득=2, 시력=2, 입장=2, 대한민국=2, 웃음=2, 다음=2, 생활=2, 선=2, 소년=2, 산부인과=2, 흥미=2, 바닥=2, 성격=2, 의료계통=2, 육체=2, 이야기=2, 소리=2, 진실=2, 성대모사=2, 결국범인=2, 안내견슬기=2, 나이=2, 필요=2, 리뷰=2, 수=2, 현장=2, 대학=2, 관람=2, 불꽃=2, 조합=2, 달=2, 감독안상훈=2, 고민=2, 뺑소니사건=2, 일대=2, 죄책감=2, 교통사고=2, 소독냄새=2, 안상훈출연김하늘=2, 사용=2, 안상훈출연=2, 설정=2, 전달=2, 조금=2, 추격자=2, 망막=2, 뺑소니사고=2, 소독=2, 예상=2, 유승호개봉=2, 확인=2, 감옥=2, 맞은편=2, 여주인공=2, 음료수=2, 밤=2, 출연김하늘=2, 감독안상훈출연=2, 남동생=2, 내용=2, 의료=2, 탐문=2, 전반=2, 동안=2, 출연김하늘유승호=2, 예리=2, 용기=2, 남자애=2, 여성캐릭터=2, 기타감각=2, 아르바이트=2, 수갑=2, 가죽=2, 경찰대=2, 김하늘유승호개봉=2, 블라인드감독=2, 대한민국리뷰=2, 영화제=2, 드라마=2, 양영조=2, 연쇄살인범=2, 어둠=2, 기분=2, 여우주연=2, 요소=2, 애=2, 계통=2, 상영=2, 분=2, 춤=2, 이용=2, 청소년=2, 수호천사=2, 시사회=2, 극=2, 설명=2, 관심=2, 예=2, 신고=2, 모범택시=2, 가해자=2, 비=2, 정도=2, 여우=2, 노파심=2, 사진=2, 방향=2, 도망=2, 세상=2, 욕=2, 주저=2, 소년기섭=2, 범죄=2, 전기=2, 오른손=2, 포스팅=2, 누나=2, 지하철=2, 표정=2, 이제=2, 정보=2, 행동=2, 인=2, 사실=2, 중요=2, 기절=2, 보호=2, 대학재학=2, 살인마=2, =1, 동일인물=1, 지망=1, 고급외제차=1, 남편=1, 주소=1, 시각장애안내견=1, 암시=1, 접근=1, 전자=1, 수사방향=1, 포인트=1, 전국관객수=1, 오상최민석=1, 모성애=1, 이터=1, 시작=1, 만족=1, 호흡=1, 영화속성별=1, 상상=1, 협박=1, 자취=1, 제공=1, 사투리=1, 혼자자취=1, 다운=1, 난항=1, 때차=1, 엊그제블라인드=1, 만남=1, 인상=1, 어이=1, 공식홈=1, 용맹=1, 입대전마지막=1, 거짓말=1, 신음=1, 초사이언=1, 비오=1, 요청전화=1, 요청=1, 진짜=1, 가죽느낌=1, 묘사=1, 피치=1, 칠갑장면=1, 구조=1, 코앞=1, 오드리헵번=1, 수면마취제=1, 옥신각신=1, 긴장감=1, 용의자=1, 사=1, 때혼자차=1, 충성=1, 삶=1, 차량=1, 보호아래=1, 열정=1, 신임=1, 동생동현=1, 며칠수경찰=1, 순간괴한=1, 프로필사진=1, 작비=1, 합선=1, 됨됨이=1, 탓=1, 당첨=1, 극장=1, 정황=1, 암시정도=1, 국내=1, 합세=1, 국산=1, 지식=1, 줄=1, 여자신음소리=1, 영화괴물=1, 성냥불꽃=1, 처음만남=1, 순제작비=1, 전기합선=1, 소중=1, 작품속경찰=1, 순간차=1, 아역=1, 경찰대학=1, 누님=1, 납득=1, 미스터리=1, 트라우마=1, 악역=1, 후자=1, 정보습득=1, 진짜가죽=1, 저녁=1, 소름=1, 대종상=1, 종류=1, 성품=1, 주차장=1, 르=1, 도견=1, 변화=1, 연속=1, 경우=1, 부분본인=1, 대학교재학=1, 산책=1, 추천=1, 순제=1, 사례금=1, 누나동생=1, 이상조형=1, 칠갑=1, 성별=1, 약자=1, 수상=1, 전마지막작품=1, 대부분=1, 택시뺑소니=1, 통계=1, 아침=1, 폭력=1, 구조요청=1, 요소요소=1, 제외=1, 수경찰=1, 한편=1, 청룡=1, 엔딩=1, 걱정=1, 길거리=1, 맘=1, 실력=1, 감동=1, 시선=1, 맞=1, 때혼자=1, 기대감=1, 맥=1, 최민석=1, 며칠=1, 기본지식=1, 자임=1, 시각안내견=1, 수준=1, 갈등=1, 성공=1, 소독약=1, 공통점=1, 성형외과피부과산부인과=1, 심장=1, 갈피=1, 치부=1, 개봉일=1, 여대생=1, 능력=1, 박보검=1, 실종=1, 유악=1, 합법다운=1, 발달=1, 뺑소니차량=1, 멜=1, 위주=1, 질문=1, 여자신음=1, 피부과산부인과의사=1, 막판=1, 열쇠=1, 해소=1, 치열=1, 주머니=1, 당황=1, 승호=1, 속시각안내견=1, 과정=1, 수리=1, 성향=1, 동물=1, 스크린=1, 번호=1, 잠깐=1, 국면=1, 안상훈감독작품=1, 몸=1, 때수아=1, 다중=1, 개인병원=1, 목소리확인=1, 살인범명진=1, 커피=1, 운전자=1, 확인한=1, 기대=1, 메스=1, 원장=1, 관람불가상영=1, 원래=1, 골목=1, 트레일러=1, 내공=1, 서로=1, 오드리헵번주연=1, 흥행=1, 회전=1, 다=1, 예전=1, 부족=1, 선정=1, 영상=1, 오버=1, 안내견연기=1, 대형=1, 학교=1, 고아원동생동현=1, 팔=1, 추측=1, 머리회전=1, 음료=1, 경력=1, 의자=1, 단서=1, 정류장=1, 때고아원=1, 결말=1, 믿음=1, 수면=1, 시트=1, 전국관객=1, 불량=1, 주변=1, 실망=1, 품위=1, 대종상영화제=1, 도움=1, 상반=1, 최악=1, 시간택시=1, 낙태=1, 사회=1, 성형외과피부과=1, 장애안내견=1, 영화블라인드=1, 속경찰=1, 케이스=1, 인간=1, 자석=1, 경찰대학교=1, 성욕=1, 또래=1, 마음이=1, 파악=1, 선택=1, 성원=1, 전화번호=1, 개봉전=1, 실수=1, 전문=1, 횡단보도=1, 양념역할=1, 택시기사=1, 막판고아원=1, 뒷이야기=1, 영화제수상=1, 다중인격=1, 시각변화=1, 수신=1, 엘레베이터=1, 습득능력=1, 심성=1, 중심=1, 볼거리=1, 불가상영시간=1, 감금=1, 상처=1, 필사=1, 문제=1, 발품=1, 폭풍눈물=1, 가=1, 진짜가죽느낌=1, 입대=1, 관객수=1, 자리=1, 서리=1, 여성범인=1, 경찰대학생=1, 감=1, 지하철역=1, 밖=1, 반=1, 속시각=1, 연쇄살인범명진=1, 기둥=1, 균형=1, 단순=1, 밥=1, 인격=1, 마취제=1, 신파=1, 도견슬기=1, 직업=1, 눈앞=1, 유인=1, 유일=1, 상관=1, 소독약냄새=1, 입대전=1, 사이=1, 지금=1, 합법=1, 진행=1, 표=1, 감회=1, 적정선=1, 리트리버=1, 사랑=1, 소매치기=1, 성형외과=1, 품=1, 이불=1, 현수막=1, 혼쭐=1, 범=1, 신음소리=1, 본명=1, 공식=1, 연쇄실종사건=1, 괴물=1, 베=1, 연기실력=1, 오빠=1, 뺑소니범=1, 영화속형사=1, 배달=1, 대학교=1, 왼손잡이=1, 진역=1, 국내개봉일=1, 초사=1, 미만=1, 블라인드공식홈=1, 덕분=1, 이언=1, 고아원동생=1, 기본=1, 길가=1, 목소리=1, 정면=1, 전마지막=1, 추적=1, 차밖=1, 헵번주연=1, 작품선택=1, 억지신파=1, 볼=1, 과=1, 며칠수=1, 범인명진=1, 피해자역할=1, 마지막=1, 전철=1, 국산차=1, 연관=1, 여주인공수아=1, 극복상황=1, 수반=1, 영화영화=1, 후비오=1, 무지=1, 눈길=1, 날집=1, 엘레=1, 헵번=1, 씬=1, 친절=1, 귀신=1, 오후=1, 조작=1, 취급=1, 고급=1, 피신=1, 부모=1, 시체=1, 눈빛=1, 약물=1, 이라니어이=1, 한국스릴러=1, 에서=1, 형상=1, 한=1, 계속=1, 때자신=1, 건물=1, 영상전화=1, 김하늘분=1, 의지=1, 주인공=1, 정신=1, 속형사=1, 현실=1, 공포=1, 안상훈감독=1, 불=1, 수첩=1, 주위=1, 섬세=1, 혼자차안=1, 작품선정=1, 사냥=1, 영화상=1, 공간=1, 이상관계=1, 주의=1, 양념=1, 성냥=1, 연기경력=1, 자취방=1, 무모=1, 주인=1, 분담=1, 여친=1, 촉망=1, 외국=1, 배달아르바이트=1, 전국=1, 베이터=1, 급=1, 경찰대학교재학=1, 마무리=1, 국내개봉=1, 보호아래피신=1, 불가상영=1, 블라인드공식=1, 진이=1, 남매=1, 마지막작품=1, 주장=1, 이유=1, 순경=1, 잘못=1, 때눈물=1, 무시=1, 그때=1, 이라니=1, 동안수아=1, 홈=1, 촬영=1, 작품속=1, 해피=1, 고민고민=1, 화=1, 그때수아=1, 지렁=1, 폭풍=1, 세끼=1, 심봉사=1, 관람불가=1, 래브라도=1, 체격=1, 트렁크=1, 태도=1, 영화얘기=1, 피부과=1, 추리=1, 아래피신=1, 기운=1, 엊그제=1, 유리=1, 앞유리=1, 배경=1, 가슴=1, 자취생활=1, 감상=1, 반항기=1, 사망=1, 정상인=1, 피부과산부인과=1, 갈등해소=1, 고급외제=1, 저녁수아=1, 유지=1, 벽돌=1, 괴한=1, 학생=1, 반려동물=1, 경찰서=1, 분위기=1, 원=1, 결국내용=1, 동일=1, 다음얘기=1, 외국스릴러=1, 단체=1, 탁자위=1, 종용=1, 경찰대학재학=1, 유승호분=1, 인기척=1, 싸이렌=1, 여타영화=1, 불꽃연기=1, 혼자차=1, 속성별=1, 탁자=1, 범주=1, 위=1, 오상=1, 거부=1, 영화속시각=1, 관객=1, 이전=1, 소유=1, 당시=1, 청룡영화상=1, 사투=1, 반려=1, 시사회표=1, 잠시=1, 정체=1, 이력=1, 등급=1, 일반택시=1, 혼자자취생활=1, 구조요청전화=1, 화답=1, 오른팔=1, 동정=1, 미만등급=1, 확대=1, 코=1, 프로필=1, 극장안=1, 곳예=1, 자초=1, 어둠안=1, 어우=1, 적응=1, 내리=1, 불가=1, 역할분담=1, 결국아래=1, 형식=1, 장애안내견연기=1, 감독작품=1, 심봉=1, 입=1, 대형스크린=1, 오드리=1, 엘레베=1, 자=1, 위협=1, 아가씨=1, 순간폭력=1, 분과길=1, 피해=1, 장=1, 담배=1, 수아옆=1, 위기=1, 소재=1, 당시사건=1, 여대생실종사건=1, 무게=1, 신용=1, 범인역=1, 포기=1, 택시뺑소니범=1, 혈관=1, 긴장=1, 정보습득능력=1, 경찰지망=1, 편견=1, 분과=1, 밥세끼=1, 대신=1}";

        content = content.replaceAll("\\{", "");
        content = content.replaceAll("\\}", "");
        String[] contentList = content.split(",");
        //System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:"+contentList.toString());
        for(String s : contentList) {
            if(s.contains("=")) {
                //    System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:"+s.trim().toString());
                String ss = s.trim();
                String sss[] = ss.split("=");
                if (sss != null && sss[0] != null && sss[1] != null) {
                    //System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:" + sss[0].toString() + "  ==>  " + sss[1].toString());
                    HashMap<String, Double> tmpMap = new HashMap<String, Double>();
                    tmpMap.put(sss[0], Double.valueOf(sss[1]));
                    //System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:" + tmpMap.toString());
                    result.add(tmpMap);
                }
            }
        }

        return result;
    }
    */

    public static HashMap<String, Double> getMapArrayFromStringSeperatedComma(String content) {
        HashMap<String, Double> result = new HashMap<String, Double>();

        //content = "{수아=66, 영화=46, 차=30, 기섭=25, 범인=24, 사람=23, 자신=21, 눈=20, 여자=19, 사건=18, 연기=18, 시각=17, 김하늘=16, 경찰=16, 목격자=14, 스릴러=13, 형사=13, 장애=12, 때=12, 장면=12, 말=11, 유승호=11, 앞=11, 작품=11, 진술=10, 사고=10, 동현=10, 블라인드=10, 조=9, 조형사=9, 시각장애=9, 남자=9, 캐릭터=9, 택시=9, 일=9, 슬기=8, 위험=8, 동생=7, 여성=7, 모습=7, 안=7, 감정=7, 느낌=7, 부분=6, 배우=6, 등장=6, 수사=6, 집=6, 감각=6, 뺑소니=6, 고아원=6, 처음=6, 상황=6, 길=6, 후=6, 점=5, 속=5, 병원=5, 주연=5, 조희봉=5, 시간=5, 곳=5, 역할=5, 순간=5, 극복=5, 차안=5, 결국=5, 목격=5, 안내견=5, 명진=5, 관계=5, 외제=4, 스릴러영화=4, 눈물=4, 아이=4, 아래=4, 인물=4, 외제차=4, 장애인=4, 반항=4, 개봉=4, 영화속=4, 실종사건=4, 친동생=4, 표현=4, 해결=4, 역=4, 죽음=4, 마음=4, 시각장애인=4, 개인=4, 전화=4, 반전=4, 휴대폰=3, 냄새=3, 날=3, 본인=3, 감독=3, 연쇄=3, 술=3, 목=3, 억지=3, 재학=3, 물=3, 나중=3, 얘기=3, 한국=3, 안상훈=3, 조형=3, 출연=3, 일반=3, 최근=3, 의사=3, 목숨=3, 이해=3, 뒤=3, 살인범=3, 시계=3, 남성=3, 연출=3, 피해자=3, 옆=3, 존재감=3, 머리=3, 밤길=3, 얼굴=3, 이상=3, 희망=3, 전=2, 스포일러=2, 산부인과의사=2, 김하늘유승호=2, 블라인드감독안상훈=2, 수사망=2, 줄거리=2, 여타=2, 의견=2, 끝=2, 기사=2, 자연=2, 기타=2, 이름=2, 상영시간=2, 상=2, 혼자=2, 습득=2, 시력=2, 입장=2, 대한민국=2, 웃음=2, 다음=2, 생활=2, 선=2, 소년=2, 산부인과=2, 흥미=2, 바닥=2, 성격=2, 의료계통=2, 육체=2, 이야기=2, 소리=2, 진실=2, 성대모사=2, 결국범인=2, 안내견슬기=2, 나이=2, 필요=2, 리뷰=2, 수=2, 현장=2, 대학=2, 관람=2, 불꽃=2, 조합=2, 달=2, 감독안상훈=2, 고민=2, 뺑소니사건=2, 일대=2, 죄책감=2, 교통사고=2, 소독냄새=2, 안상훈출연김하늘=2, 사용=2, 안상훈출연=2, 설정=2, 전달=2, 조금=2, 추격자=2, 망막=2, 뺑소니사고=2, 소독=2, 예상=2, 유승호개봉=2, 확인=2, 감옥=2, 맞은편=2, 여주인공=2, 음료수=2, 밤=2, 출연김하늘=2, 감독안상훈출연=2, 남동생=2, 내용=2, 의료=2, 탐문=2, 전반=2, 동안=2, 출연김하늘유승호=2, 예리=2, 용기=2, 남자애=2, 여성캐릭터=2, 기타감각=2, 아르바이트=2, 수갑=2, 가죽=2, 경찰대=2, 김하늘유승호개봉=2, 블라인드감독=2, 대한민국리뷰=2, 영화제=2, 드라마=2, 양영조=2, 연쇄살인범=2, 어둠=2, 기분=2, 여우주연=2, 요소=2, 애=2, 계통=2, 상영=2, 분=2, 춤=2, 이용=2, 청소년=2, 수호천사=2, 시사회=2, 극=2, 설명=2, 관심=2, 예=2, 신고=2, 모범택시=2, 가해자=2, 비=2, 정도=2, 여우=2, 노파심=2, 사진=2, 방향=2, 도망=2, 세상=2, 욕=2, 주저=2, 소년기섭=2, 범죄=2, 전기=2, 오른손=2, 포스팅=2, 누나=2, 지하철=2, 표정=2, 이제=2, 정보=2, 행동=2, 인=2, 사실=2, 중요=2, 기절=2, 보호=2, 대학재학=2, 살인마=2, =1, 동일인물=1, 지망=1, 고급외제차=1, 남편=1, 주소=1, 시각장애안내견=1, 암시=1, 접근=1, 전자=1, 수사방향=1, 포인트=1, 전국관객수=1, 오상최민석=1, 모성애=1, 이터=1, 시작=1, 만족=1, 호흡=1, 영화속성별=1, 상상=1, 협박=1, 자취=1, 제공=1, 사투리=1, 혼자자취=1, 다운=1, 난항=1, 때차=1, 엊그제블라인드=1, 만남=1, 인상=1, 어이=1, 공식홈=1, 용맹=1, 입대전마지막=1, 거짓말=1, 신음=1, 초사이언=1, 비오=1, 요청전화=1, 요청=1, 진짜=1, 가죽느낌=1, 묘사=1, 피치=1, 칠갑장면=1, 구조=1, 코앞=1, 오드리헵번=1, 수면마취제=1, 옥신각신=1, 긴장감=1, 용의자=1, 사=1, 때혼자차=1, 충성=1, 삶=1, 차량=1, 보호아래=1, 열정=1, 신임=1, 동생동현=1, 며칠수경찰=1, 순간괴한=1, 프로필사진=1, 작비=1, 합선=1, 됨됨이=1, 탓=1, 당첨=1, 극장=1, 정황=1, 암시정도=1, 국내=1, 합세=1, 국산=1, 지식=1, 줄=1, 여자신음소리=1, 영화괴물=1, 성냥불꽃=1, 처음만남=1, 순제작비=1, 전기합선=1, 소중=1, 작품속경찰=1, 순간차=1, 아역=1, 경찰대학=1, 누님=1, 납득=1, 미스터리=1, 트라우마=1, 악역=1, 후자=1, 정보습득=1, 진짜가죽=1, 저녁=1, 소름=1, 대종상=1, 종류=1, 성품=1, 주차장=1, 르=1, 도견=1, 변화=1, 연속=1, 경우=1, 부분본인=1, 대학교재학=1, 산책=1, 추천=1, 순제=1, 사례금=1, 누나동생=1, 이상조형=1, 칠갑=1, 성별=1, 약자=1, 수상=1, 전마지막작품=1, 대부분=1, 택시뺑소니=1, 통계=1, 아침=1, 폭력=1, 구조요청=1, 요소요소=1, 제외=1, 수경찰=1, 한편=1, 청룡=1, 엔딩=1, 걱정=1, 길거리=1, 맘=1, 실력=1, 감동=1, 시선=1, 맞=1, 때혼자=1, 기대감=1, 맥=1, 최민석=1, 며칠=1, 기본지식=1, 자임=1, 시각안내견=1, 수준=1, 갈등=1, 성공=1, 소독약=1, 공통점=1, 성형외과피부과산부인과=1, 심장=1, 갈피=1, 치부=1, 개봉일=1, 여대생=1, 능력=1, 박보검=1, 실종=1, 유악=1, 합법다운=1, 발달=1, 뺑소니차량=1, 멜=1, 위주=1, 질문=1, 여자신음=1, 피부과산부인과의사=1, 막판=1, 열쇠=1, 해소=1, 치열=1, 주머니=1, 당황=1, 승호=1, 속시각안내견=1, 과정=1, 수리=1, 성향=1, 동물=1, 스크린=1, 번호=1, 잠깐=1, 국면=1, 안상훈감독작품=1, 몸=1, 때수아=1, 다중=1, 개인병원=1, 목소리확인=1, 살인범명진=1, 커피=1, 운전자=1, 확인한=1, 기대=1, 메스=1, 원장=1, 관람불가상영=1, 원래=1, 골목=1, 트레일러=1, 내공=1, 서로=1, 오드리헵번주연=1, 흥행=1, 회전=1, 다=1, 예전=1, 부족=1, 선정=1, 영상=1, 오버=1, 안내견연기=1, 대형=1, 학교=1, 고아원동생동현=1, 팔=1, 추측=1, 머리회전=1, 음료=1, 경력=1, 의자=1, 단서=1, 정류장=1, 때고아원=1, 결말=1, 믿음=1, 수면=1, 시트=1, 전국관객=1, 불량=1, 주변=1, 실망=1, 품위=1, 대종상영화제=1, 도움=1, 상반=1, 최악=1, 시간택시=1, 낙태=1, 사회=1, 성형외과피부과=1, 장애안내견=1, 영화블라인드=1, 속경찰=1, 케이스=1, 인간=1, 자석=1, 경찰대학교=1, 성욕=1, 또래=1, 마음이=1, 파악=1, 선택=1, 성원=1, 전화번호=1, 개봉전=1, 실수=1, 전문=1, 횡단보도=1, 양념역할=1, 택시기사=1, 막판고아원=1, 뒷이야기=1, 영화제수상=1, 다중인격=1, 시각변화=1, 수신=1, 엘레베이터=1, 습득능력=1, 심성=1, 중심=1, 볼거리=1, 불가상영시간=1, 감금=1, 상처=1, 필사=1, 문제=1, 발품=1, 폭풍눈물=1, 가=1, 진짜가죽느낌=1, 입대=1, 관객수=1, 자리=1, 서리=1, 여성범인=1, 경찰대학생=1, 감=1, 지하철역=1, 밖=1, 반=1, 속시각=1, 연쇄살인범명진=1, 기둥=1, 균형=1, 단순=1, 밥=1, 인격=1, 마취제=1, 신파=1, 도견슬기=1, 직업=1, 눈앞=1, 유인=1, 유일=1, 상관=1, 소독약냄새=1, 입대전=1, 사이=1, 지금=1, 합법=1, 진행=1, 표=1, 감회=1, 적정선=1, 리트리버=1, 사랑=1, 소매치기=1, 성형외과=1, 품=1, 이불=1, 현수막=1, 혼쭐=1, 범=1, 신음소리=1, 본명=1, 공식=1, 연쇄실종사건=1, 괴물=1, 베=1, 연기실력=1, 오빠=1, 뺑소니범=1, 영화속형사=1, 배달=1, 대학교=1, 왼손잡이=1, 진역=1, 국내개봉일=1, 초사=1, 미만=1, 블라인드공식홈=1, 덕분=1, 이언=1, 고아원동생=1, 기본=1, 길가=1, 목소리=1, 정면=1, 전마지막=1, 추적=1, 차밖=1, 헵번주연=1, 작품선택=1, 억지신파=1, 볼=1, 과=1, 며칠수=1, 범인명진=1, 피해자역할=1, 마지막=1, 전철=1, 국산차=1, 연관=1, 여주인공수아=1, 극복상황=1, 수반=1, 영화영화=1, 후비오=1, 무지=1, 눈길=1, 날집=1, 엘레=1, 헵번=1, 씬=1, 친절=1, 귀신=1, 오후=1, 조작=1, 취급=1, 고급=1, 피신=1, 부모=1, 시체=1, 눈빛=1, 약물=1, 이라니어이=1, 한국스릴러=1, 에서=1, 형상=1, 한=1, 계속=1, 때자신=1, 건물=1, 영상전화=1, 김하늘분=1, 의지=1, 주인공=1, 정신=1, 속형사=1, 현실=1, 공포=1, 안상훈감독=1, 불=1, 수첩=1, 주위=1, 섬세=1, 혼자차안=1, 작품선정=1, 사냥=1, 영화상=1, 공간=1, 이상관계=1, 주의=1, 양념=1, 성냥=1, 연기경력=1, 자취방=1, 무모=1, 주인=1, 분담=1, 여친=1, 촉망=1, 외국=1, 배달아르바이트=1, 전국=1, 베이터=1, 급=1, 경찰대학교재학=1, 마무리=1, 국내개봉=1, 보호아래피신=1, 불가상영=1, 블라인드공식=1, 진이=1, 남매=1, 마지막작품=1, 주장=1, 이유=1, 순경=1, 잘못=1, 때눈물=1, 무시=1, 그때=1, 이라니=1, 동안수아=1, 홈=1, 촬영=1, 작품속=1, 해피=1, 고민고민=1, 화=1, 그때수아=1, 지렁=1, 폭풍=1, 세끼=1, 심봉사=1, 관람불가=1, 래브라도=1, 체격=1, 트렁크=1, 태도=1, 영화얘기=1, 피부과=1, 추리=1, 아래피신=1, 기운=1, 엊그제=1, 유리=1, 앞유리=1, 배경=1, 가슴=1, 자취생활=1, 감상=1, 반항기=1, 사망=1, 정상인=1, 피부과산부인과=1, 갈등해소=1, 고급외제=1, 저녁수아=1, 유지=1, 벽돌=1, 괴한=1, 학생=1, 반려동물=1, 경찰서=1, 분위기=1, 원=1, 결국내용=1, 동일=1, 다음얘기=1, 외국스릴러=1, 단체=1, 탁자위=1, 종용=1, 경찰대학재학=1, 유승호분=1, 인기척=1, 싸이렌=1, 여타영화=1, 불꽃연기=1, 혼자차=1, 속성별=1, 탁자=1, 범주=1, 위=1, 오상=1, 거부=1, 영화속시각=1, 관객=1, 이전=1, 소유=1, 당시=1, 청룡영화상=1, 사투=1, 반려=1, 시사회표=1, 잠시=1, 정체=1, 이력=1, 등급=1, 일반택시=1, 혼자자취생활=1, 구조요청전화=1, 화답=1, 오른팔=1, 동정=1, 미만등급=1, 확대=1, 코=1, 프로필=1, 극장안=1, 곳예=1, 자초=1, 어둠안=1, 어우=1, 적응=1, 내리=1, 불가=1, 역할분담=1, 결국아래=1, 형식=1, 장애안내견연기=1, 감독작품=1, 심봉=1, 입=1, 대형스크린=1, 오드리=1, 엘레베=1, 자=1, 위협=1, 아가씨=1, 순간폭력=1, 분과길=1, 피해=1, 장=1, 담배=1, 수아옆=1, 위기=1, 소재=1, 당시사건=1, 여대생실종사건=1, 무게=1, 신용=1, 범인역=1, 포기=1, 택시뺑소니범=1, 혈관=1, 긴장=1, 정보습득능력=1, 경찰지망=1, 편견=1, 분과=1, 밥세끼=1, 대신=1}";

        content = content.replaceAll("\\{", "");
        content = content.replaceAll("\\}", "");
        String[] contentList = content.split(",");
        //System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:"+contentList.toString());
        for(String s : contentList) {
            if(s.contains("=")) {
                //    System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:"+s.trim().toString());
                String ss = s.trim();
                String sss[] = ss.split("=");
                if (sss != null && sss[0] != null && sss[1] != null) {
                    //System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:" + sss[0].toString() + "  ==>  " + sss[1].toString());
                    //HashMap<String, Double> tmpMap = new HashMap<String, Double>();
                    result.put(sss[0], Double.valueOf(sss[1]));
                    //System.out.println("#StringUtil.getMapArrayFromStringSeperatedComma contentList:" + tmpMap.toString());
                    //result.add(tmpMap);
                }
            }
        }

        return result;
    }

    public static String getKeySringFromMapByCount(Map<String, Double> reqMap, int maxCount) {
        String result = "";
        try {
            String tmpKey = "";
            int i=0;

            Set entrySet = reqMap.entrySet();
            Iterator it = entrySet.iterator();

            while(it.hasNext() && i < maxCount){
                Map.Entry me = (Map.Entry)it.next();
                //System.out.println("#i:"+i+" 키:"+me.getKey()+" , "+ me.getValue());

                if (i == 0) {
                    result = me.getKey().toString();
                } else {
                    result = result + ", " + me.getKey();
                }

                i++;
            }

        /*
            while( keys.hasNext() && i < maxCount ){
                String key = keys.next();
                System.out.println("#키 :"+key+" , "+ reqMap.get(key).toString());

                if (i == 0) {
                    result = key;
                } else {
                    result = result + ", " + key;
                }

                i++;
            }
*/
            /*
            int i = 1;
            for (Map.Entry<String, Double> elem : reqMap.entrySet()) {
                //System.out.println( String.format("키 : %s, 값 : %s", elem.getKey(), elem.getValue()) );
                tmpKey = elem.getKey();
                tmpValue = elem.getValue();

                if (i == 0) {
                    result = tmpKey;
                } else {
                    result = result + ", " + tmpKey;
                }

                i++;
                System.out.println("#i:"+i);

                if (i > maxCount) break;
            }
            */
        } catch (Exception e) { e.printStackTrace(); }

        return result;
    }

    public static String getStringFromList(List<String> req) {
        String result = "";
        int i = 0;
        if (req != null) {
            for (String s : req) {
                if (!"".equals(s)) {
                    if (i == 0) {
                        result = s;
                    } else {
                        result = result + ", " + s;
                    }
                }
                i++;
            }
        }
        return result;
    }

    public static int countOccurrences(String haystack, char needle)
    {
        int count = 0;
        for (int i=0; i < haystack.length(); i++)
        {
            if (haystack.charAt(i) == needle)
            {
                count++;
            }
        }
        return count;
    }

    public static String removeBracket(String req) {
        req = req.trim();
        req = req.replace("[","");
        req = req.replace("]","");
        return req;
    }

    public static List<String> convertStringToListByComma(String req) {
        req = req.replace("[","");
        req = req.replace("]", "");
        req = req.replace("\"","");

        //System.out.println("#reqstr:"+req);

        List<String> result = new ArrayList();
        if(!"".equals(req.trim())) {
            if(req.contains(",")) {
                String reqs[] = req.trim().split(",");
                for(String s : reqs) {
                    result.add(s);
                }
            } else {
                result.add(req.trim());
            }
        }
        return result;
    }

    public static List<String> removeCharacterFromList(List<String> reqArr, String character) {
        List<String> resultArr = null;
        if (reqArr != null && reqArr.size() > 0) {
            resultArr = new ArrayList();

            for (String s : reqArr) {
                s = s.replace(character, "");
                resultArr.add(s);
            }
        }
        return resultArr;
    }

    public static String convertArrayStringToStringAddDelimeter(List<String> reqList, String delimeter) {
        String result = "";
        if (reqList != null && reqList.size() > 0 && !"".equals(delimeter.trim())) {
            int cnt = 0;
            for (String s : reqList) {
                if (cnt == 0) {
                    result = s;
                } else {
                    result += delimeter + " " + s;
                }
                cnt++;
            }
        }
        return result;
    }

    public static List<String> getCombinedWordsArrayFromToWords(List<Map<String, Object>> origArr) {
        List<String> resultArr = new ArrayList();

        for(Map<String, Object> rmap : origArr) {
            if (rmap != null && rmap.get("word") != null) {
                String addWord = rmap.get("word").toString();
                if (rmap.get("toword") != null && !"".equals(rmap.get("toword").toString())) {
                    addWord = rmap.get("toword").toString();
                }
                resultArr.add(addWord);
            }
        }

        return resultArr;
    }


    public static List<Map<String, Object>> getFilteredWordsArrayFromByWords(List<Map<String, Object>> origArr) {
        List<Map<String, Object>> resultArr = new ArrayList();

        for(Map<String, Object> rmap : origArr) {
            if (rmap != null && rmap.get("word") != null) {
                if (rmap.get("toword") != null && !"".equals(rmap.get("toword").toString())) {
                    resultArr.add(rmap);
                }
            }
        }

        return resultArr;
    }

    public static List<String> getCuttedArrayByLimit(List<String> origArr, int limit) {
        List<String> result = null;
        if (origArr != null && origArr.size() > 0) {
            result = new ArrayList();
            int cnt = 0;
            for(String s : origArr) {
                if (cnt < limit) {
                    result.add(s);
                }
                cnt++;
            }
        }

        return result;
    }

    public static String[] convertArrayListToStringArray(List<String> origArr) {
        String[] result = null;
        if (origArr != null && origArr.size() > 0) {
            result = new String[origArr.size()];
            result = origArr.toArray(result);
        }
        return result;
    }

    public static String getSortedStringStrsAddSeperator(List<String> origArr, String seperator) {
        String result = "";
        if (origArr != null && origArr.size() > 0) {
            String[] newArr = convertArrayListToStringArray(origArr);
            Arrays.sort(newArr);
            for(int i=0; i<newArr.length; i++) {
                if (i == 0) {
                    result = newArr[0];
                } else {
                    result += seperator + newArr[i];
                }
            }
        }
        return result;
    }

    public static String filterGenres(String req) {
        req = req.trim();

        Set<String> filterSet = new HashSet();
        filterSet.add("액션 영화");
        filterSet.add("SF 영화");
        filterSet.add("판타지 영화");
        filterSet.add("휴먼/가족 영화");
        filterSet.add("드라마 영화");
        filterSet.add("로맨스 영화");
        filterSet.add("코미디 영화");
        filterSet.add("공포/스릴러 영화");
        filterSet.add("무협 영화");
        filterSet.add("범죄/추리 영화");
        filterSet.add("사극 영화");
        filterSet.add("역사 영화");
        filterSet.add("모험 영화");
        filterSet.add("다큐 영화");
        filterSet.add("학원/순정 영화");
        filterSet.add("유아/어린이 영화");
        filterSet.add("전쟁 영화");
        filterSet.add("메디컬 영화");
        filterSet.add("교육 영화");
        filterSet.add("교양 영화");
        filterSet.add("스포츠 영화");
        filterSet.add("미스터리 영화");
        filterSet.add("뮤지컬 영화");
        filterSet.add("서부 영화");
        filterSet.add("성인 영화");
        filterSet.add("단편 영화");

        filterSet.add("액션 시리즈");
        filterSet.add("SF 시리즈");
        filterSet.add("판타지 시리즈");
        filterSet.add("휴먼/가족 시리즈");
        filterSet.add("드라마 시리즈");
        filterSet.add("로맨스 시리즈");
        filterSet.add("코미디 시리즈");
        filterSet.add("공포/스릴러 시리즈");
        filterSet.add("무협 시리즈");
        filterSet.add("범죄/추리 시리즈");
        filterSet.add("사극 시리즈");
        filterSet.add("역사 시리즈");
        filterSet.add("모험 시리즈");
        filterSet.add("다큐 시리즈");
        filterSet.add("학원/순정 시리즈");
        filterSet.add("유아/어린이 시리즈");
        filterSet.add("전쟁 시리즈");
        filterSet.add("메디컬 시리즈");
        filterSet.add("교육 시리즈");
        filterSet.add("교양 시리즈");
        filterSet.add("스포츠 시리즈");
        filterSet.add("미스터리 시리즈");
        filterSet.add("뮤지컬 시리즈");
        filterSet.add("서부 시리즈");
        filterSet.add("성인 시리즈");
        filterSet.add("단편 시리즈");

        filterSet.add("한국 영화");

        for(String fs : filterSet) {
            String fs2 = ", " + fs;
            req = req.replace(fs2, "");
            req = req.replace(fs, "");
        }

        return req;
    }

    public static List<String> removeDupListString(List<String> reqList) {
        List<String> result = null;

        if (reqList != null && reqList.size() > 0) {
            result = new ArrayList();

            Set<String> tmpSet = new HashSet();

            for (String s : reqList) {
                tmpSet.add(s);
            }

            for (String ss : tmpSet) {
                result.add(ss);
            }

        }
        return result;
    }

    public static String filterSubgenreMix(String subgenre) {
        String resStr = "";
        List<String> result = null;

        subgenre = subgenre.trim();
        if(!"".equals(subgenre)) {
            result = new ArrayList<String>();

            if (subgenre.contains(",")) {
                String genres[] = subgenre.split(",");
                for (String gen : genres) {
                    gen = gen.trim();

                    if (!gen.equals("액션 영화") && !gen.equals("모험 영화") && !gen.equals("SF 영화")
                            && !gen.equals("판타지 영화") && !gen.equals("범죄 영화") && !gen.equals("미스터리 영화")
                            && !gen.equals("스릴러 영화") && !gen.equals("서부 영화") && !gen.equals("에로 영화")) {
                        result.add(gen);
                    }
                }

                int i = 0;
                for(String gen2 : result) {
                    if (i == 0) {
                        resStr = gen2;
                    } else {
                        resStr = resStr + ", " + gen2;
                    }

                    i++;
                }
            } else {
                if (!subgenre.equals("액션 영화") && !subgenre.equals("모험 영화") && !subgenre.equals("SF 영화")
                        && !subgenre.equals("판타지 영화") && !subgenre.equals("범죄 영화") && !subgenre.equals("미스터리 영화")
                        && !subgenre.equals("스릴러 영화") && !subgenre.equals("서부 영화") && !subgenre.equals("에로 영화")) {
                    resStr = subgenre;
                }
            }
        }
        return resStr;
    }

    public static boolean filterLastTagValid(JsonObject jo) {
        boolean isValid = false;

        if (jo != null && jo.get("word") != null) {
            String word = jo.get("word").getAsString();
            if (!"".equals(word)) {
                String wordto = filterLastGenre(word);
                if (!"".equals(wordto)) {
                    isValid = true;
                }
            }
        }
        return isValid;
    }

    public static String filterLastGenre(String req) {
        req = req.trim();

        Set<String> filterSet = new HashSet();
        filterSet.add("한국 영화");
        filterSet.add("미국 영화");
        filterSet.add("영국 영화");
        filterSet.add("프랑스 영화");
        filterSet.add("일본 영화");
        filterSet.add("인도 영화");
        filterSet.add("중국 영화");
        filterSet.add("홍콩 영화");
        filterSet.add("대만 영화");
        filterSet.add("독일 영화");
        filterSet.add("태국 영화");
        filterSet.add("이탈리아 영화");

        filterSet.add("코믹 스릴러");

        filterSet.add("서브 장르");
        filterSet.add("테스트 서브장르1");
        filterSet.add("테스트 서브장르2");
        filterSet.add("테스트 서브장르3");
        filterSet.add("테스트 서브장르4");
        filterSet.add("테스트 서브장르5");
        filterSet.add("테스트 서브장르6");
        filterSet.add("테스트 서브장르7");
        filterSet.add("테스트 서브장르8");
        filterSet.add("테스트 서브장르9");

        /* 18.08.09 added */
        filterSet.add("과학/기술 영화");
        filterSet.add("의학/메디컬 영화");
        filterSet.add("괴물/돌연변이 영화");
        filterSet.add("동물 친구들 영화");
        filterSet.add("공룡 영화");
        filterSet.add("로봇/사이보그 영화");
        filterSet.add("사이코/살인마 영화");
        filterSet.add("수퍼히어로 영화");
        filterSet.add("유령 영화");
        filterSet.add("악령 영화");
        filterSet.add("스포츠 영화");
        filterSet.add("음모 영화");
        filterSet.add("탐정 영화");

        for (String fs : filterSet) {
            //System.out.println("#compare filterset fs:"+fs+"  vs  req:"+req);
            req = req.replace(fs, "");
        }
        return req;
    }

    public static String addAwardStatic(String reqAward) {
        String resAward = "";
        if (!"".equals(reqAward)) {
            reqAward = reqAward.trim();
            if (reqAward.contains("아카데미시상식") && !reqAward.contains("영국아카데미시상식")) {
                resAward = reqAward.replace("아카데미시상식", "오스카상");
            }

        }
        return resAward;
    }

    public static Set<String> prcAwardsStr(Set<String> awardList, String reqAward) {

        if (!"".equals(reqAward)) {
            if(awardList == null) awardList = new HashSet();

            try {
                reqAward = reqAward.trim();
                if (!reqAward.contains("__sub")) {
                    if (reqAward.contains("회 ")) {
                        String m1[] = reqAward.split("회 ");
                        if (m1.length > 1) {
                            if (reqAward.contains(".")) {
                                String awardNum = m1[0];
                                String awardNames = m1[1];
                                awardNames = awardNames.replace(" ", "");

                                System.out.println("#award prc: awards:" + awardNames);

                                String awardName = "";
                                String awardYear = "";

                                String tmp = "";
                                String tmp2 = "";

                                String m2[] = awardNames.split("\\.");
                                //System.out.println("#award prc m2.size:"+m2.length);
                                if (m2.length > 1) {
                                    awardName = m2[0].trim();
                                    awardYear = m2[1].trim();
                                    tmp2 = awardYear + " " + awardName;
                                    //System.out.println("#award prc: year:"+awardYear+" / awardName:"+awardName);
                                } else {
                                    awardName = m1[1];
                                }

                                tmp = awardNum + "회 " + awardName;
                                awardList.add(awardName);
                                if (!"".equals(tmp2)) awardList.add(tmp2);
                                awardList.add(tmp);

                                String revTmp = addAwardStatic(tmp);
                                if (!"".equals(revTmp)) {
                                    awardList.add(revTmp);
                                }
                                String revAwardName = addAwardStatic(awardName);
                                if (!"".equals(revAwardName)) {
                                    awardList.add(revAwardName);
                                }
                                String revTmp2 = addAwardStatic(tmp2);
                                if (!"".equals(revTmp2)) {
                                    awardList.add(revTmp2);
                                }
                            }

                        }
                    }
                } else {
                    /* 서브장르는 별도의 규칙으로 하드코딩 맵과 대조하여 존재할 경우만 취득 19.11.12 */
                    /*  베니스국제영화제__sub__황금사자상  */
                    String sub1 = reqAward.replace("__sub__", " ");
                    String sub2[] = sub1.split(" ");
                    Set<String> subSet = getSubAwards(sub2[0]);
                    if (subSet != null) {
                        String sub22 = sub2[1];
                        sub22 = sub22.trim();
                        boolean isExists = compareSetToString(subSet, sub22);
                        if (isExists) {
                            awardList.add(sub1);
                        }
                    }

                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        return awardList;
    }
    
    public static String nvl(String strOriginal, String strIfNull) {
    	return (strOriginal==null ? strIfNull : strOriginal);
    }
  
  

    public static boolean compareSetToString(Set<String> reqSet, String reqStr){
        boolean isExists = false;
        if (reqSet != null && !"".equals(reqStr)) {
            Iterator<String> iter = reqSet.iterator();

            while(iter.hasNext()) {
                String setStr = iter.next();
                setStr = setStr.trim();
                if (reqStr.equals(setStr)) {
                    isExists = true;
                }
            }
        }

        return isExists;
    }

    public static Set<String> getSubAwards(String mainAwards) {
        Map<String, Set<String>> subAwardsMap = new HashMap();

        Set<String> sub1 = new HashSet<String>(
                Arrays.asList("작품상","남우주연상","여우주연상","남우조연상",
                        "여우조연상","감독상","각본상","주제가상","음악상","외국어영화상","장편애니메이션상"));
        Set<String> sub2 = new HashSet<String>(
                Arrays.asList("황금사자상","은사자상-감독상","심사위원대상","볼피컵-여우주연상",
                        "볼피컵-남우주연상","심사위원특별상","각본상")
                );
        Set<String> sub3 = new HashSet<String>(
                Arrays.asList("금곰상","금곰상-단편영화상","은곰상-감독상","은곰상-남우주연상","은곰상-여우주연상",
                        "은곰상-심사위원대상","은곰상-예술공헌상","은곰상-각본상","은곰상-예술공헌상","파노라마관객상")
        );
        Set<String> sub4 = new HashSet<String>(
                Arrays.asList("황금종려상","심사위원대상","감독상","각본상","여우주연상","남우주연상","심사위원대상")
                );
        Set<String> sub5 = new HashSet<String>(
                Arrays.asList("최우수작품상","감독상","남우주연상","여우주연상","남우조연상","여우조연상","신인남우상",
                        "신인여우상","신인감독상")
                );
        Set<String> sub6 = new HashSet<String>(
                Arrays.asList("영화대상","영화작품상","영화감독상","영화남자최우수연기상","영화여자최우수연기상",
                        "영화남자조연상","영화여자조연상","영화남자신인연기상","영화여자신인연기상","영화신인감독상")
                );
        Set<String> sub7 = new HashSet<String>(
                Arrays.asList("시나리오상","남우주연상","여우주연상","남우조연상","여우조연상",
                        "신인남자배우상","신인여자배우상","신인감독상")
                );


        subAwardsMap.put("아카데미시상식",sub1);
        subAwardsMap.put("베니스국제영화제",sub2);
        subAwardsMap.put("베를린국제영화제",sub3);
        subAwardsMap.put("칸영화제",sub4);
        subAwardsMap.put("청룡영화상",sub5);
        subAwardsMap.put("백상예술대상",sub6);
        subAwardsMap.put("대종상영화제",sub7);

        return subAwardsMap.get(mainAwards);
    }
    
}
