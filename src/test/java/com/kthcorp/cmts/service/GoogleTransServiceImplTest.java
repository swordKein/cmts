package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.util.CommonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.PrintStream;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GoogleTransServiceImplTest {

	//@MockBean
	@Autowired
	private GoogleTransService googleTransService;

	@Test
	public void test_getTransResult() throws Exception {
		//String reqTxt = "Blind";
		String reqTxt = "Architects: BCHO Architects Location: Seoul, Republic of Korea Project team: Kang Woo-hyun, Erik Horn, Park Gi-hyun, Pai Yong-eun, Nicholas Locke, Kwon Sung-Hwa, Seo Ji-young, Park Joo- hyun, Eric Druse Project area: 4,403 sqm Project year: 2006 – 2010 Photographs: Kyungsub Shin, Wooseop Hwang    http://www.bchoarchitects.com/main/aboutbcho.htm I was approached to construct a new building at the site of the old Hankook Ilbo building originally designed by the renowned Korean architect Kim Swoo-geun in 1968. The area had recently been demolished as part of an urban renewal development project. The site is well-known to the public due to the close proximity to Gyeongbok Palace and Insa-dong , both landmarks of Seoul. Given this historical context, I personally believe it is inappropriate to destroy older sophisticated buildings built in the 1960s and 1970s and replace them. However, given the inevitable situation involving the government’s urban redevelopment project, I had no choice but to accept the situation, then decided to design in consideration of the historical and urban context of the site. 원래 1968 년에 유명한 한국어 건축가 Kim Swoo-geun 가 설계 한 옛 한국 일보 건물의 사이트에 새로운 건물을 구성하는 접근되었습니다. 이 지역은 최근 도시 갱신 개발 사업의 일환으로 철거되었다. 이 사이트는 경복궁과 인사동 (Insa-dong) 서울의 두 랜드 마크 가까운 때문에 대중에게 잘 알려져 있습니다. 이 역사적 맥락을 감안할 때, 개인적으로는 1960 년대와 1970 년대에 지어진 오래된 세련된 건물을 파괴하고이를 대체 할 수 부적절한하다고 생각합니다. 그러나, 정부의 도시 재개발 프로젝트를 포함하는 피할 수없는 상황을 고려해봤을 때, 제가 상황을 수용 할 수 있지만 선택의 여지가 없었습니다, \u200B\u200B다음 사이트의 역사와 도시 맥락을 고려하여 설계하기로 결정했습니다.  The specific architectural shape was made in response to the distinctive geographical features. The site consists of several acute angles, and within its figure, inducing interactions between horizontal and vertical dynamics, such as water flowing in various directions. This recalls a birch tree trunk that weathered severe hardships over time. 특정 건축 모양은 독특한 지리적 특징에 대한 응답으로 만들어졌습니다. 이 사이트는 물이 다양한 방향으로 흐르는 등의 수평 및 수직 역학 사이의 상호 작용을 유도 여러 급성 각도, 그 그림 안에 구성되어 있습니다. 이 시간이 지남에 심각한 어려움을 풍화 자작 나무 나무 줄기를 불러줍니다.   Directly south of the building an area with historical commercial alleyways, one famous called Pimatgol. Although the area has largely disappeared with the urban renewal project, it is still possible to connect with some of the remaining traces. The connection made through the site is directed toward Dongsipjagak, an ancient watch tower at the southeast corner of Gyeongbok Palace. This area connects to Samcheongdong, an area which remains vibrant, evoking both the historical tradition and urban context. ";
		reqTxt = "";
		reqTxt = "King Uther dies suddenly and Britain faces chaos when Merlin appoints Arthur, the unknown king's son and heir, as new king by birthright against his half-sister's ambitious plans. —Filipe Manuel Neto When King Uther dies and Britain faces chaos, Merlin presents an unknown named Arthur as the new king by birthright, as the late king's son, against the ambitious desires of his half-sister, Morgan. —Filipe Manuel Neto.";
		String result = googleTransService.getTransResult(reqTxt, "en", "ko");
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_getTransKoreanResult() throws Exception {
		//String reqTxt = "Blind";
		String reqTxt = "Architects: BCHO Architects Location: Seoul, Republic of Korea Project team: Kang Woo-hyun, Erik Horn, Park Gi-hyun, Pai Yong-eun, Nicholas Locke, Kwon Sung-Hwa, Seo Ji-young, Park Joo- hyun, Eric Druse Project area: 4,403 sqm Project year: 2006 – 2010 Photographs: Kyungsub Shin, Wooseop Hwang    http://www.bchoarchitects.com/main/aboutbcho.htm I was approached to construct a new building at the site of the old Hankook Ilbo building originally designed by the renowned Korean architect Kim Swoo-geun in 1968. The area had recently been demolished as part of an urban renewal development project. The site is well-known to the public due to the close proximity to Gyeongbok Palace and Insa-dong , both landmarks of Seoul. Given this historical context, I personally believe it is inappropriate to destroy older sophisticated buildings built in the 1960s and 1970s and replace them. However, given the inevitable situation involving the government’s urban redevelopment project, I had no choice but to accept the situation, then decided to design in consideration of the historical and urban context of the site. 원래 1968 년에 유명한 한국어 건축가 Kim Swoo-geun 가 설계 한 옛 한국 일보 건물의 사이트에 새로운 건물을 구성하는 접근되었습니다. 이 지역은 최근 도시 갱신 개발 사업의 일환으로 철거되었다. 이 사이트는 경복궁과 인사동 (Insa-dong) 서울의 두 랜드 마크 가까운 때문에 대중에게 잘 알려져 있습니다. 이 역사적 맥락을 감안할 때, 개인적으로는 1960 년대와 1970 년대에 지어진 오래된 세련된 건물을 파괴하고이를 대체 할 수 부적절한하다고 생각합니다. 그러나, 정부의 도시 재개발 프로젝트를 포함하는 피할 수없는 상황을 고려해봤을 때, 제가 상황을 수용 할 수 있지만 선택의 여지가 없었습니다, \u200B\u200B다음 사이트의 역사와 도시 맥락을 고려하여 설계하기로 결정했습니다.  The specific architectural shape was made in response to the distinctive geographical features. The site consists of several acute angles, and within its figure, inducing interactions between horizontal and vertical dynamics, such as water flowing in various directions. This recalls a birch tree trunk that weathered severe hardships over time. 특정 건축 모양은 독특한 지리적 특징에 대한 응답으로 만들어졌습니다. 이 사이트는 물이 다양한 방향으로 흐르는 등의 수평 및 수직 역학 사이의 상호 작용을 유도 여러 급성 각도, 그 그림 안에 구성되어 있습니다. 이 시간이 지남에 심각한 어려움을 풍화 자작 나무 나무 줄기를 불러줍니다.   Directly south of the building an area with historical commercial alleyways, one famous called Pimatgol. Although the area has largely disappeared with the urban renewal project, it is still possible to connect with some of the remaining traces. The connection made through the site is directed toward Dongsipjagak, an ancient watch tower at the southeast corner of Gyeongbok Palace. This area connects to Samcheongdong, an area which remains vibrant, evoking both the historical tradition and urban context. ";

		String result = googleTransService.getTransKoreanResult(reqTxt);
		//System.out.println("#Result:"+result);
		System.out.println("#Result:"+result);
	}

	@Test
	public void test_getTransResultWeb() throws Exception {
		String reqTxt = "Architects: BCHO Architects Location: Seoul, Republic of Korea Project team: Kang Woo-hyun, Erik Horn, Park Gi-hyun, Pai Yong-eun, Nicholas Locke, Kwon Sung-Hwa, Seo Ji-young, Park Joo- hyun, Eric Druse Project area: 4,403 sqm Project year: 2006 – 2010 Photographs: Kyungsub Shin, Wooseop Hwang    http://www.bchoarchitects.com/main/aboutbcho.htm I was approached to construct a new building at the site of the old Hankook Ilbo building originally designed by the renowned Korean architect Kim Swoo-geun in 1968. The area had recently been demolished as part of an urban renewal development project. The site is well-known to the public due to the close proximity to Gyeongbok Palace and Insa-dong , both landmarks of Seoul. Given this historical context, I personally believe it is inappropriate to destroy older sophisticated buildings built in the 1960s and 1970s and replace them. However, given the inevitable situation involving the government’s urban redevelopment project, I had no choice but to accept the situation, then decided to design in consideration of the historical and urban context of the site. 원래 1968 년에 유명한 한국어 건축가 Kim Swoo-geun 가 설계 한 옛 한국 일보 건물의 사이트에 새로운 건물을 구성하는 접근되었습니다. 이 지역은 최근 도시 갱신 개발 사업의 일환으로 철거되었다. 이 사이트는 경복궁과 인사동 (Insa-dong) 서울의 두 랜드 마크 가까운 때문에 대중에게 잘 알려져 있습니다. 이 역사적 맥락을 감안할 때, 개인적으로는 1960 년대와 1970 년대에 지어진 오래된 세련된 건물을 파괴하고이를 대체 할 수 부적절한하다고 생각합니다. 그러나, 정부의 도시 재개발 프로젝트를 포함하는 피할 수없는 상황을 고려해봤을 때, 제가 상황을 수용 할 수 있지만 선택의 여지가 없었습니다, \u200B\u200B다음 사이트의 역사와 도시 맥락을 고려하여 설계하기로 결정했습니다.  The specific architectural shape was made in response to the distinctive geographical features. The site consists of several acute angles, and within its figure, inducing interactions between horizontal and vertical dynamics, such as water flowing in various directions. This recalls a birch tree trunk that weathered severe hardships over time. 특정 건축 모양은 독특한 지리적 특징에 대한 응답으로 만들어졌습니다. 이 사이트는 물이 다양한 방향으로 흐르는 등의 수평 및 수직 역학 사이의 상호 작용을 유도 여러 급성 각도, 그 그림 안에 구성되어 있습니다. 이 시간이 지남에 심각한 어려움을 풍화 자작 나무 나무 줄기를 불러줍니다.   Directly south of the building an area with historical commercial alleyways, one famous called Pimatgol. Although the area has largely disappeared with the urban renewal project, it is still possible to connect with some of the remaining traces. The connection made through the site is directed toward Dongsipjagak, an ancient watch tower at the southeast corner of Gyeongbok Palace. This area connects to Samcheongdong, an area which remains vibrant, evoking both the historical tradition and urban context. ";

		System.out.println("#word count:"+reqTxt.length());
		reqTxt = CommonUtil.removeTex(reqTxt);
		//reqTxt = reqTxt.substring(0,200);
		String result = googleTransService.getTransKoreanResultWeb(CommonUtil.removeTex(reqTxt));
		System.out.println("#Result:"+result);
	}

}
