package com.kthcorp.cmts.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RestUtilTest {

    @Test
    public void test_getSearchedEsData() throws Exception {
        JsonObject result = RestUtil.getSearchedEsData("idx_subgenre", "keywords", "WHO___사이코패스 WHAT___살인 WHAT___살해");
        System.out.println("#RES:"+result);
    }
    /*
    @Test
    public void test_getES() throws Exception {
        //String result = RestUtil.getES("{\"data\":\"아버지가방에들어가신다\"}");
        String result = RestUtil.getES("{\"analyzer\":\"korean\",\"text\":\"아버지가방에들어가신다\"}");
        System.out.println("#RESULT:"+result);
    }

    @Test
    public void test_getES2() throws Exception {
        //String result = RestUtil.getES("{\"data\":\"아버지가방에들어가신다\"}");
        String orig = "[헤럴드경제=신동윤 기자]요즘들어 세상 사람들을 놀라게하는 10대들의 강력 범죄가 끊이지 않고 있다. 특히 이들의 범행 과정이 갈수록 치밀해지고, 수법 역시 잔혹해짐에 따라 사회적인 파문은 갈 수록 커져가는 모양새다.\n" +
                "\n" +
                "최근들어 전국민을 충격의 도가니로 몰아넣은 사건은 바로 지난 3월 발생한 인천 8세 여아 유괴ㆍ살인 및 시신 훼손 사건이다.\n" +
                "\n" +
                "\n" +
                "부산 여중생 무차별 폭행사건 CCTV 영상. [헤럴드경제DB]\n" +
                "\n" +
                "지난달 29일 직접 살인을 저지른 고교 자퇴생 A(16) 양에겐 징역 20년, 공범인 B(18) 양에겐 징역 30년이 구형되기까지 법정에서 밝혀진 사건의 전말은 사람들을 경악케했다. 유괴에서 살해ㆍ시신 훼손까지 이르는 잔인한 수법을 비롯해, 12개의 가상 캐릭터로 살아온 피의자들이, 살해한 피해자 신체의 일부를 소장하려 했다는 엽기적인 행동으로 이어졌다는 진술이 알려지며 국민들은 분노할 수 밖에 없었다.\n" +
                "\n" +
                "이런 가운데 터져나온 ‘부산 여중생 무차별 폭행사건’과 ‘강릉 여고생 폭행사건’은 국민들에게 10대 강력범죄의 심각성을 다시 한 번 인식하게 하는 계기가 됐다. 이들 사건에서 가해자들은 피해자를 1시간 반에 걸쳐 공사 자재ㆍ유리병을 이용해 머리를 내려치는 등 100여 차례 폭행하고, 폭행 장면을 영상통화로 생중계한 뒤 동영상을 퍼뜨리는 행동까지도 보였다.\n" +
                "\n" +
                "상황이 이렇다보니 매번 반복되는 정부의 각종 학교폭력근절대책이 유의미한 성과를 보이지 못하고 있다는 지적이 잇따르고 있으며, 이런 문제는 통계가 잘 말해주고 있다.\n" +
                "\n" +
                "\n" +
                "최근 5년간(2012~2016년) 10대 강력범죄 검거 현황. [제공=박남춘 의원실, 경찰청]\n" +
                "\n" +
                "6일 국회 행정안전위원회 소속 박남춘 의원실이 경찰청으로부터 제출받은 자료에 따르면 지난 5년간(2012~2016년) 살인, 강도, 강간추행, 방화 등 ‘4대 강력범죄’로 검거된 10대(만10~18세)는 총 1만5849명에 이르는 것으로 나타났다. 그 수는 지난 2012년 3675명, 2013년 3494명, 2014년 3068명, 2015년 2760명으로 줄어들었지만, 지난해 2852명으로 다시 증가하는 모양새를 보이기도 했다. 이는 강간추행 등 성범죄의 증가가 영향을 미친 것으로 분석된다. 특히나 전체 강력범죄의 75.4%(1만1958명)가 성범죄인 것으로 확인돼 ‘위험수위’에 이르렀다는 지적이다.\n" +
                "\n" +
                "심각한 수준의 학교폭력으로 인해 경찰에 검거됐지만 미성년자라는 이유로 제대로 처벌을 받지않는 경우도 많은 것으로 조사됐다.\n" +
                "\n" +
                "\n" +
                "인천 연수구 초등생 유괴ㆍ살인 및 시신훼손 사건 피의자들의 모습. [헤럴드경제DB]\n" +
                "\n" +
                "국회 행안위 이재정 의원실이 경찰청 자료 ‘2013년 이후 학교폭력 적발 및 조치결과’를 분석해 본 결과 지난 4년 7개월간 학교폭력으로 인해 경찰에 검거된 수만 6만3429명에 이르렀다. 하지만 학폭 사범 중 구속 인원은 649명에 그치는 것으로 나타났다. 4만2625명이 불구속됐고 5838명은 만 14세 미만 촉법소년으로 법원 소년부에 송치ㆍ훈방 등의 조치를 받았다.\n" +
                "\n" +
                "이에 대해 이윤호 동국대 범죄심리학과 교수는 “과거에 비해 청소년들이 신체적으로 조숙하고, 범죄충동이나 범행동기를 갖고 있는 아이들이 수많은 매체를 통해 수법을 학습ㆍ경험할 수 있는 기회가 넓어지고 있다보니 과거에 비해 청소년 강력 범죄의 양상과 유형이 더 잔인해지고 있는 것”이라며 “질병을 조기에 발견해 예방해야하듯, 청소년 강력범죄 문제 역시 사법제도까지 가기 전에 학교나 지역사회 등 사회 기관의 역할과 책임을 강화해 사전에 예방하려는 노력이 좀 더 커져야 한다”고 지적했다.";
        orig = CommonUtil.removeTex(orig);
        String result = RestUtil.getES("{\"analyzer\":\"korean\",\"text\":\""+orig+"\"}");

        JsonObject jObj = JsonUtil.getJsonObject(result);
        JsonArray jArr = (JsonArray) jObj.get("tokens");
        String reqStr = "";
        for (JsonElement je : jArr) {
            JsonObject jo = (JsonObject) je;
            reqStr += jo.get("token") + " ";
            //System.out.println("#tag:" + jo.get("token"));
        }

        WordFreqUtil.countWords(reqStr);
        //System.out.println("#RESULT:" + result);
    }
    */
}