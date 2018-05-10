package com.kthcorp.cmts.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SeunjeonUtilTest {

	@Test
	public void testAll() throws Exception{
		String reqStr = " 런던에 있는 7개 팀 중 일곱 팀 야마카시 팀1의 일원인 로건은 어느 날";
		SeunjeonUtil.parse1(reqStr);
		//SeunjeonUtil.parse2("삼성");
		//SeunjeonUtil.setUserDict("삼성");
		//SeunjeonUtil.getOrig("삼성");
		//SeunjeonUtil.parse3("삼성");

	}

	@Test
	public void test_getSimpleWordsxxx() throws Exception {
		//for(int i=0; i<10000; i++) {
			ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2("사림이고수월한사릉한다바람공주데이터는하늘에있을수도없을수도더있지않을까", null);
			System.out.println("#Result::" + result.toString());
			String res = "";
			for (ArrayList<String> cur : result) {
				res += " " + cur.get(3);
			}
			System.out.println("#Result text:"+res);
			//System.out.println("#Result:"+i+" 'th ::" + result.toString());
		//}
	}

	@Test
	public void test_getSimpleWords() throws Exception{
		ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2("바람공주데이터는하늘에있을수도없을수도더있지않을까", null);
		System.out.println("#Result:"+result.toString());

		ArrayList<ArrayList<String>> eunjeon_rule = new ArrayList<ArrayList<String>>();
		ArrayList<String> s1 = new ArrayList<String>();
		s1.add("NNG|JX&NNG");

		String lv = "";
		String cv = "";
		String lw = "";
		String ww = "";
		for (List<String> ls : result) {
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

				if(lv.equals("NNG") && ls.get(0).equals("NNG")) {
					if(ww.equals("")) {
						ww += lw + ls.get(3);
					} else {
						ww += ls.get(3);
					}
				} else {
					System.out.println("#WW:"+ww);
					ww = "";
				}


				System.out.println("####### lv:"+lv+"        ### ww:"+ww+ "         ##### "+cv);

				lv = ls.get(0);
				lw = ls.get(3);
			}
		}
	}



	@Test
	public void test_getMatchingSyntaxWords() throws Exception{
		String reqStr = "사림이고수월한사릉한다바람공주데이터는하늘에있을수도없을수도더있지않을까";
		reqStr = "다시 시작해 너를 빛나게 할 노래를 싱어송라이터인 그레타키이라 나이틀리는 남자친구 데이브애덤 리바인가 메이저 음반회사와 계약을 하게 되면서 뉴욕으로 오게 된다 그러나 행복도 잠시 오랜 연인이자 음악적 파트너로서 함께 노래를 만들고 부르는 것이 좋았던 그레타와 달리 스타가 된 데이브의 마음은 어느새 변해버린다 스타 음반프로듀서였지만 이제는 해고된 댄마크 러팔로은 미치기 일보직전 들른 뮤직바에서 그레타의 자작곡을 듣게 되고 아직 녹슬지 않은 촉을 살려 음반제작을 제안한다 거리 밴드를 결성한 그들은 뉴욕의 거리를 스튜디오 삼아 진짜로 부르고 싶었던 노래를 만들어가는데  ABOUT MELODY  존 카니 감독이 선사하는 또 하나의 음악여행 눈과 귀를 즐겁게 만드는 감성폭발 영화의 탄생 은 스타 명성을 잃은 음반프로듀서와 스타 남친을 잃은 싱어송라이터가 뉴욕에서 만나 함께 노래로 다시 시작하는 이야기를 그린 로맨틱 멜로디이다 제목처럼 인생에서 최악이라고 할만한 하루를 보낸 두 주인공이 우연히 만나 진짜로 부르고 싶은 노래를 통해 다시 시작하는 이야기를 그린다 로 세계적인 신드롬을 일으켰던 존 카니 감독은 으로 또 한 번의 신드롬을 예고한다 존 카니 감독의 전작 는 선댄스 영화제 관객상 수상을 시작으로 올해의 영화로 등극한 후 독립영화상을 수상하고 사운드트랙으로 그래미상 후보 주제곡 Falling Slowly로 아카데미 주제가상을 수상했다 국내에서도 다양성 영화 최초로 20만 이상의 관객을 동원하는 등 아트블록버스터의 힘을 과시한 바 있다 스토리와 음악을 조화롭게 만드는 탁월한 연출로 관객의 마음을 사로잡는 존 카니 감독은 이번 영화에서 역시 음악과 영화를 아름답게 섞어내어 더욱 깊어진 감성으로 자신의 진가를 유감없이 발휘한다 뉴욕 거리 곳곳을 배경으로 아름다운 선율 안에 인생과 사랑 예술을 담아 한층 업그레이드 된 탄탄한 스토리로 관객들을 몰입하게 만든다 전작 와는 다른 성향의 음악으로 영화를 만들고 싶었던 존 카니 감독은 한 때 프로 뮤지션이었던 자신의 과거 경험을 되살려 내어 영화 속에 실감나게 녹여내었다 존 카니 감독은 보다 더 많은 이야기가 담겨 있다며 영화에 대한 자신감을 표했다 또한 인생의 어느 지점을 마주한 캐릭터를 드러낼 수 있는 곡을 작업했다 멋진 곡들을 완성시켰고 아주 성공적으로 흘러갔다고 밝힌 바 영화 속 아름다운 멜로디와 서정적인 가사가 돋보이는 노래들은 관객들에게 충분한 만족감을 선사할 것이다 마크 러팔로는 존 카니 감독은 마치 뮤지컬 영화계의 존 카사베츠 같다며 감독에 대한 믿음을 드러냈고 키이라 나이틀리는 이렇게 희망으로 가득 찬 작품을 찾기는 힘들 것이라고 자부했다 이에 지난해 토론토 영화제에서 먼저 공개된 후 극찬을 이끌어냈고 올해 상하이국제영화제 예술공헌상을 수상하며 평단의 인정을 받았다 노래가 있는 단 하나의 로맨틱 멜로디 은 가슴 두근거리게 만드는 노래와 유쾌하게 공감을 일으키는 스토리 희망을 찾아가는 메시지가 청량감을 전하는 완벽한 여름 영화의 탄생을 기대하게 한다 키이라 나이틀리 마크 러팔로 헤일리 스테인펠드 마룬5 애덤 리바인과 씨 로 그린 모스 데프까지 할리우드 톱배우와 세계적인 톱가수의 환상적인 시너지 에는 연기력으로 정평이 난 할리우드 최고의 배우들과 세계적인 아티스트들이 총출동한다 다양한 작품을 통해 대체 불가능한 배우로 자리잡은 마크 러팔로와 시리즈로 아카데미상 후보로 지명된 키이라 나이틀리가 환상의 호흡을 선보인다 그리고 로 아카데미 후보에 오른 할리우드의 신성 헤일리 스테인펠드 의 폴 포츠 역할을 한 제임스 코든 로 아카데미상 후보에 오르고 뉴욕비평가협회상을 수상한 캐서린 키너가 등장한다 또한 2013년 피플지가 선정한 현존하는 최고의 섹시남 1위에 오른 마룬5 애덤 리바인이 주요 역할로 등장해 이목을 집중시킨다 그래미상을 수상한 싱어송라이터 씨 로 그린과 힙합의 보석이라 불리는 래퍼이자 배우인 모스 데프까지 세계적인 톱가수들을 캐스팅하며 최강 라인업을 완성 영화의 완성도를 높였다 제작자인 안소니 브렉맨은 우리 영화의 배우들은 그야말로 엄청난 조합이다 영화 속에서 이렇게 다양한 분야의 배우들을 한 자리에 모아 놓는다는 건 놀라운 일이다라고 소감을 밝혔다 마크 러팔로는 헐크와는 상반된 자유분방한 이미지로 색다른 매력을 선사한다 그의 캐릭터인 댄은 음악에 관해서는 뚝심 있고 열정적인 인물로 마크 러팔로는 심도 깊은 내면 연기로 내공 있는 연기력을 확인시켜 준다 싱어송라이터인 그레타 역할을 맡은 키이라 나이틀리는 촬영 전부터 보컬 코치를 받아 단련한 수준 높은 노래 실력을 선보인다 마크 러팔로는 키이라는 정말 대단하다 그녀는 무엇이든 해낼 의지가 있으며 또 재능 있는 가수다라고 칭찬을 아끼지 않았다 다른 출연 배우들도 밴드를 결성하는 영화 속 설정을 위해 악기 연주를 배워 직접 연주했다 헤일리 스테인펠드는 극 중 무뚝뚝한 십대 소녀지만 음악을 통해 아빠와의 관계를 개선해나가는 바이올렛 역할을 연기하기 위해 기타 연주를 익혔다 그녀는 감독님이 내가 연기한 것을 칭찬해 줄 때마다 너무 좋았다며 작품에 대한 열정을 드러냈다 을 통해 처음 스크린에 데뷔하는 마룬5의 애덤 리바인은 록스타가 된 그레타의 남자친구인 데이브로 분해 영화 속에서 노래는 물론 따뜻하고 다정한 모습부터 변심하는 모습 이별을 후회하는 과정까지 처음이라고 믿을 수 없을 정도로 다양한 감정연기를 소화해냈다 미국의 대중문화잡지 롤링스톤은 애덤 리바인의 연기에 대해 놀랍도록 훌륭하고 자연스럽다고 극찬했다 애덤 리바인은 첫 영화를 마친 후 사람들이 해준 조언은 대사를 외워라와 연기하려 하지 말고 그냥 듣고 반응해라였는데 아주 큰 도움이 되었다 연기 경험이 전무했지만 저 스스로 최선을 다하고 있다고 느끼며 연기할 수 있었다고 만족감을 드러냈다 댄의 오래된 친구이자 힙합스타로 출연한 씨 로 그린은 음악을 하는 것과 연기하는 것은 비슷하다 모두 움직임의 운율이다라고 소감을 밝혔다 내로라하는 연기파 배우들과 톱가수들의 환상적인 시너지가 만들어낸 최고의 팀워크는 을 더욱 빛나게 만드는 가장 중요한 요소로 작용했다 로맨틱한 멜로디와 가사 감성을 자극하는 명품 OST 2014년 제17회 상하이국제영화제 예술공헌상 수상 또 한 번의 신드롬 예고 뉴욕의 특색 있는 분위기와 로맨틱한 멜로디가 담긴 은 올해 제17회 상하이 국제영화제에서 음악상인 예술공헌상을 수상하며 평단의 인정을 먼저 받았다 또한 영화 개봉에 앞서 공개된 OST 음반이 음원차트 상위권을 휩쓸며 주목을 받고 있다 존 카니 감독은 에 전형적인 사랑 노래나 댄스곡 비트의 선율이 아닌 영화의 배경이 되는 뉴욕의 분위기와 맥박을 표현한 멜로디를 담고 싶어했다 또한 요즘 인기 있는 음악과 다른 장르의 음악임은 물론 캐릭터들의 깊은 내면까지 반영할 수 있는 노래여야 한다고 생각했다 그런 그가 영화의 음악작업을 위해 선택한 사람이 바로 그렉 알렉산더이다 영화의 총 음악감독을 맡은 그는 싱어송라이터이자 제작자로 2백만 장이 넘는 음반판매고를 기록하며 전 세계적으로 흥행했던 밴드 뉴 래디컬스의 리더이다 미셸 브랜치가 피쳐링한 산타나의 The Game of Love를 작곡해 그래미상을 수상하기도 한 실력파로 그가 만든 노래들은 TV시리즈  영화  등에서 등장하며 지금까지도 끊임없이 사랑 받고 있다 존 카니 감독과 의기투합한 그렉 알렉산더는 뉴욕 특유의 정서와 함께 따뜻하고 감성적인 노래를 작곡했다 존 카니 감독은 에서의 음악은 랑 다르길 원했다 그리고 그렉은 정말 멋진 곡들을 완성시켰다며 극찬했다 의 OST에는 그렉 알렉산더가 작곡하고 아름다운 멜로디와 서정적인 가사가 인상적인 영화 주제곡 Lost Stars를 비롯해 키이라 나이틀리의 보컬 능력을 만나볼 수 있는 Tell Me If You Wanna Go Home 존 카니 감독이 작사 작곡한 Like A Fool을 비롯한 16곡이 등장한다 영화에 등장하는 뮤지션들 애덤 리바인 이외에도 그래미상을 수상한 싱어송라이터 씨 로 그린도 합류해 더욱 다양한 음악적 색채를 선사한다 또한 출연배우인 헤일리 스테인펠드의 노래도 확인할 수 있다 특히 주제곡 Lost Stars는 애덤 리바인과 키이라 나이틀리가 각각의 버전으로 불러서 더욱 화제를 모으고 있다 키이라 나이틀리의 수준 높은 노래 실력과 애덤 리바인의 독특한 음색까지 주옥 같은 곡들로 영화를 가득 채웠다 센트럴파크 엠파이어 스테이트 빌딩 차이나타운 뉴욕 거리 곳곳에서 울려 퍼지는 완벽한 하모니 은 센트럴파크 호수 위 엠파이어 스테이트 빌딩이 보이는 옥상 차이나타운 뉴욕 지하철 등 특색 있는 뉴욕 거리 곳곳에서 촬영했다 영화에 채워진 다양한 장르의 음악에 맞는 각기 다른 장소는 캐릭터의 내면까지 표현하며 또 다른 감동을 일으킨다 밴드가 노래를 부르는 매 장면의 배경이 되는 뉴욕의 다양한 풍경은 여느 영화에서도 만나보지 못했던 색다른 모습으로 다가올 뿐만 아니라 촬영 당시 현장의 소리들을 고스란히 담아내어 뉴욕의 분위기를 그대로 전달해 관객들에게 마치 여행을 하는 듯한 느낌을 전한다 에선 뉴욕시 자체가 거대한 캐릭터나 다름 없었다 존 카니 감독은 음악을 완성한 후 뉴욕 거리 곳곳을 다니며 촬영하기를 원할 때 직접 자전거를 타고 시내를 돌아다니며 영화 속 댄과 그레타가 펼칠 장소를 찾아 다녔다 미술을 담당한 채드 키스와 함께 가능한 뉴욕을 새로운 모습으로 다르게 담아내고 싶었고 사실적으로 보여주길 원했다 거기에 영화의 감성을 녹여낼 수 있는 서사적인 풍경과 거리에서 들려오는 흥미로운 사운드를 잘 조화시키는 것도 중요했다 감독은 늘 제작진에게 이렇게 얘기했다 또 눈으로 장소를 찾고 있죠 왜 이곳에서 녹음을 하고 싶은 거죠 이런 주문에 제작진은 눈이 아닌 귀로 장소를 찾아냈다 자신의 고향인 아일랜드 더블린과 뉴욕을 흡사하다 말한 존 카니 감독은 오랜 시간 동안 쌓여온 뉴욕 특유의 분위기로 인해 어느 곳에 카메라를 두어도 이야기가 만들어지는 것에 만족감을 표하며 이 영화를 일종의 뉴욕에 바치는 연애편지라 표현했다 배우들 역시 뉴욕 거리 촬영에 굉장한 흥미를 표했는데 마크 러팔로는 뉴욕은 예술가가 되기에도 사랑에 빠지기에도 너무나 좋은 장소다 영화의 배경으로 딱 맞았다고 감탄했으며 애덤 리바인은 내 인생 최고의 시간이라고 말할 정도로 만족감을 표했다 더불어 실제로 뉴요커인 마크 러팔로의 자연스러운 모습과 영국에서 온 키이라 나이틀리가 뉴욕을 바라보는 이방인으로서의 시선이 캐릭터에 그대로 묻어나 한결 풍성한 이야기를 만들 수 있었다  OST  Lost Stars  Adam Levine  Keira Knightley Please dont see just a boy caught up in dreams and fantasies Please see me reaching out for someone I cant see Take my hand lets see where we wake up tomorrow Best laid plans sometimes are just a one night stand Ill be damned Cupids demanding back his arrow So lets get drunk on our tears and God tell us the reason youth is wasted on the young Its hunting season and the lambs are on the run Searching for meaning But are we all lost stars trying to light up the dark 보지 말아요 한낱 꿈과 환상에 빠진 소년을 날 봐요 보이는 누군가에게 손 뻗는 나를 그 손을 잡고 내일 아침 함께 눈을 떠요 미래를 계획하지만 때론 하룻밤 놀이일 뿐 어처구니없게도 큐피드는 화살을 되돌려 달라지 그럴땐 우리 눈물에 취해봐요 하느님 왜 청춘은 청춘에게 주기엔 낭비인가요 사냥 철이 왔으니 이 어린 양은 달려야 해요 의미를 찾아 헤매는 우린 어둠을 밝히고 싶어하는 길 잃은 별들인가요 Like A Fool  Keira Knightley We take a chance from time to time And put our necks out on the line And you have broken every promise that we made And I have loved you anyway Took a fine time to leave me hangin out to dry Understand now Im greivin So dont you waste my time Cause you have taken All the wind out from my sails And I have loved you just the same We finally find this then youre gone Been chasin rainbows all along And you have cursed me When theres no one left to blame And I have loved you just the same And you have broken every single fucking rule And I have loved you like a fool 때론 우린 운을 믿곤 해 위험한 모험도 감수하지 넌 우리가 한 모든 약속들을 산산이 부쉈지만 그래도 난 널 사랑했어 오랜 시간 끝에 난 홀로 남겨졌고 아직 난 슬프지만 이젠 널 잊을거야 넌 나의 돛에서 모든 바람을 앗아갔지만 그래도 난 널 사랑했어 함께 삶을 찾는 순간 넌 떠났지 무지개만 좇아 다닌 너였어 원망할 곳 없을 때 넌 날 저주했지만 그래도 난 널 사랑했어 마지막 남은 원칙들도 모두 어겨버린 널 난 바보처럼 사랑했어 Tell Me If You Go Home  Keira Knightley Maybe You dont have to smile so sad Laugh when youre feeling bad I promise I wont Chase you You dont have to dance so blue You dont have to say I do When baby you dont Just tell me The one thing you never told me Then let go of me Hell just throw me Maybe if you wanna go home Tell me if Im back on my own Giving back a heart thats on loan Just tell me if you wanna go home 아마도 넌 슬플 때 웃지 않아도 돼 웃어 네가 기분 나쁠 때는 약속할게 난 그러지 않을 거라고 너를 쫓아 넌 우울할 때 춤추지 않아도 돼 넌 네가 하기 싫을 때 할거라고 말할 필요가 없어 그냥 이제까지 내게 말하지 않은 하나만 말해줘 그냥 나를 완전히 놓아줘 그냥 나를 던져줘 날 바래다준다면 혼자로 남을 건지 말해줘 빌려줬던 마음을 되찾아 돌아갈건지 내게 말해줘  ABOUT MELODY  존 카니 감독이 선사하는 또 하나의 음악여행 눈과 귀를 즐겁게 만드는 감성폭발 영화의 탄생 은 스타 명성을 잃은 음반프로듀서와 스타 남친을 잃은 싱어송라이터가 뉴욕에서 만나 함께 노래로 다시 시작하는 이야기를 그린 로맨틱 멜로디이다 제목처럼 인생에서 최악이라고 할만한 하루를 보낸 두 주인공이 우연히 만나 진짜로 부르고 싶은 노래를 통해 다시 시작하는 이야기를 그린다 로 세계적인 신드롬을 일으켰던 존 카니 감독은 으로 또 한 번의 신드롬을 예고한다 존 카니 감독의 전작 는 선댄스 영화제 관객상 수상을 시작으로 올해의 영화로 등극한 후 독립영화상을 수상하고 사운드트랙으로 그래미상 후보 주제곡 Falling Slowly로 아카데미 주제가상을 수상했다 국내에서도 다양성 영화 최초로 20만 이상의 관객을 동원하는 등 아트블록버스터의 힘을 과시한 바 있다 스토리와 음악을 조화롭게 만드는 탁월한 연출로 관객의 마음을 사로잡는 존 카니 감독은 이번 영화에서 역시 음악과 영화를 아름답게 섞어내어 더욱 깊어진 감성으로 자신의 진가를 유감없이 발휘한다 뉴욕 거리 곳곳을 배경으로 아름다운 선율 안에 인생과 사랑 예술을 담아 한층 업그레이드 된 탄탄한 스토리로 관객들을 몰입하게 만든다 전작 와는 다른 성향의 음악으로 영화를 만들고 싶었던 존 카니 감독은 한 때 프로 뮤지션이었던 자신의 과거 경험을 되살려 내어 영화 속에 실감나게 녹여내었다 존 카니 감독은 보다 더 많은 이야기가 담겨 있다며 영화에 대한 자신감을 표했다 또한 인생의 어느 지점을 마주한 캐릭터를 드러낼 수 있는 곡을 작업했다 멋진 곡들을 완성시켰고 아주 성공적으로 흘러갔다고 밝힌 바 영화 속 아름다운 멜로디와 서정적인 가사가 돋보이는 노래들은 관객들에게 충분한 만족감을 선사할 것이다 마크 러팔로는 존 카니 감독은 마치 뮤지컬 영화계의 존 카사베츠 같다며 감독에 대한 믿음을 드러냈고 키이라 나이틀리는 이렇게 희망으로 가득 찬 작품을 찾기는 힘들 것이라고 자부했다 이에 지난해 토론토 영화제에서 먼저 공개된 후 극찬을 이끌어냈고 올해 상하이국제영화제 예술공헌상을 수상하며 평단의 인정을 받았다 노래가 있는 단 하나의 로맨틱 멜로디 은 가슴 두근거리게 만드는 노래와 유쾌하게 공감을 일으키는 스토리 희망을 찾아가는 메시지가 청량감을 전하는 완벽한 여름 영화의 탄생을 기대하게 한다 키이라 나이틀리 마크 러팔로 헤일리 스테인펠드 마룬5 애덤 리바인과 씨 로 그린 모스 데프까지 할리우드 톱배우와 세계적인 톱가수의 환상적인 시너지 에는 연기력으로 정평이 난 할리우드 최고의 배우들과 세계적인 아티스트들이 총출동한다 다양한 작품을 통해 대체 불가능한 배우로 자리잡은 마크 러팔로와 시리즈로 아카데미상 후보로 지명된 키이라 나이틀리가 환상의 호흡을 선보인다 그리고 로 아카데미 후보에 오른 할리우드의 신성 헤일리 스테인펠드 의 폴 포츠 역할을 한 제임스 코든 로 아카데미상 후보에 오르고 뉴욕비평가협회상을 수상한 캐서린 키너가 등장한다 또한 2013년 피플지가 선정한 현존하는 최고의 섹시남 1위에 오른 마룬5 애덤 리바인이 주요 역할로 등장해 이목을 집중시킨다 그래미상을 수상한 싱어송라이터 씨 로 그린과 힙합의 보석이라 불리는 래퍼이자 배우인 모스 데프까지 세계적인 톱가수들을 캐스팅하며 최강 라인업을 완성 영화의 완성도를 높였다 제작자인 안소니 브렉맨은 우리 영화의 배우들은 그야말로 엄청난 조합이다 영화 속에서 이렇게 다양한 분야의 배우들을 한 자리에 모아 놓는다는 건 놀라운 일이다라고 소감을 밝혔다 마크 러팔로는 헐크와는 상반된 자유분방한 이미지로 색다른 매력을 선사한다 그의 캐릭터인 댄은 음악에 관해서는 뚝심 있고 열정적인 인물로 마크 러팔로는 심도 깊은 내면 연기로 내공 있는 연기력을 확인시켜 준다 싱어송라이터인 그레타 역할을 맡은 키이라 나이틀리는 촬영 전부터 보컬 코치를 받아 단련한 수준 높은 노래 실력을 선보인다 마크 러팔로는 키이라는 정말 대단하다 그녀는 무엇이든 해낼 의지가 있으며 또 재능 있는 가수다라고 칭찬을 아끼지 않았다 다른 출연 배우들도 밴드를 결성하는 영화 속 설정을 위해 악기 연주를 배워 직접 연주했다 헤일리 스테인펠드는 극 중 무뚝뚝한 십대 소녀지만 음악을 통해 아빠와의 관계를 개선해나가는 바이올렛 역할을 연기하기 위해 기타 연주를 익혔다 그녀는 감독님이 내가 연기한 것을 칭찬해 줄 때마다 너무 좋았다며 작품에 대한 열정을 드러냈다 을 통해 처음 스크린에 데뷔하는 마룬5의 애덤 리바인은 록스타가 된 그레타의 남자친구인 데이브로 분해 영화 속에서 노래는 물론 따뜻하고 다정한 모습부터 변심하는 모습 이별을 후회하는 과정까지 처음이라고 믿을 수 없을 정도로 다양한 감정연기를 소화해냈다 미국의 대중문화잡지 롤링스톤은 애덤 리바인의 연기에 대해 놀랍도록 훌륭하고 자연스럽다고 극찬했다 애덤 리바인은 첫 영화를 마친 후 사람들이 해준 조언은 대사를 외워라와 연기하려 하지 말고 그냥 듣고 반응해라였는데 아주 큰 도움이 되었다 연기 경험이 전무했지만 저 스스로 최선을 다하고 있다고 느끼며 연기할 수 있었다고 만족감을 드러냈다 댄의 오래된 친구이자 힙합스타로 출연한 씨 로 그린은 음악을 하는 것과 연기하는 것은 비슷하다 모두 움직임의 운율이다라고 소감을 밝혔다 내로라하는 연기파 배우들과 톱가수들의 환상적인 시너지가 만들어낸 최고의 팀워크는 을 더욱 빛나게 만드는 가장 중요한 요소로 작용했다 로맨틱한 멜로디와 가사 감성을 자극하는 명품 OST 2014년 제17회 상하이국제영화제 예술공헌상 수상 또 한 번의 신드롬 예고 뉴욕의 특색 있는 분위기와 로맨틱한 멜로디가 담긴 은 올해 제17회 상하이 국제영화제에서 음악상인 예술공헌상을 수상하며 평단의 인정을 먼저 받았다 또한 영화 개봉에 앞서 공개된 OST 음반이 음원차트 상위권을 휩쓸며 주목을 받고 있다 존 카니 감독은 에 전형적인 사랑 노래나 댄스곡 비트의 선율이 아닌 영화의 배경이 되는 뉴욕의 분위기와 맥박을 표현한 멜로디를 담고 싶어했다 또한 요즘 인기 있는 음악과 다른 장르의 음악임은 물론 캐릭터들의 깊은 내면까지 반영할 수 있는 노래여야 한다고 생각했다 그런 그가 영화의 음악작업을 위해 선택한 사람이 바로 그렉 알렉산더이다 영화의 총 음악감독을 맡은 그는 싱어송라이터이자 제작자로 2백만 장이 넘는 음반판매고를 기록하며 전 세계적으로 흥행했던 밴드 뉴 래디컬스의 리더이다 미셸 브랜치가 피쳐링한 산타나의 The Game of Love를 작곡해 그래미상을 수상하기도 한 실력파로 그가 만든 노래들은 TV시리즈  영화  등에서 등장하며 지금까지도 끊임없이 사랑 받고 있다 존 카니 감독과 의기투합한 그렉 알렉산더는 뉴욕 특유의 정서와 함께 따뜻하고 감성적인 노래를 작곡했다 존 카니 감독은 에서의 음악은 랑 다르길 원했다 그리고 그렉은 정말 멋진 곡들을 완성시켰다며 극찬했다 의 OST에는 그렉 알렉산더가 작곡하고 아름다운 멜로디와 서정적인 가사가 인상적인 영화 주제곡 Lost Stars를 비롯해 키이라 나이틀리의 보컬 능력을 만나볼 수 있는 Tell Me If You Wanna Go Home 존 카니 감독이 작사 작곡한 Like A Fool을 비롯한 16곡이 등장한다 영화에 등장하는 뮤지션들 애덤 리바인 이외에도 그래미상을 수상한 싱어송라이터 씨 로 그린도 합류해 더욱 다양한 음악적 색채를 선사한다 또한 출연배우인 헤일리 스테인펠드의 노래도 확인할 수 있다 특히 주제곡 Lost Stars는 애덤 리바인과 키이라 나이틀리가 각각의 버전으로 불러서 더욱 화제를 모으고 있다 키이라 나이틀리의 수준 높은 노래 실력과 애덤 리바인의 독특한 음색까지 주옥 같은 곡들로 영화를 가득 채웠다 센트럴파크 엠파이어 스테이트 빌딩 차이나타운 뉴욕 거리 곳곳에서 울려 퍼지는 완벽한 하모니 은 센트럴파크 호수 위 엠파이어 스테이트 빌딩이 보이는 옥상 차이나타운 뉴욕 지하철 등 특색 있는 뉴욕 거리 곳곳에서 촬영했다 영화에 채워진 다양한 장르의 음악에 맞는 각기 다른 장소는 캐릭터의 내면까지 표현하며 또 다른 감동을 일으킨다 밴드가 노래를 부르는 매 장면의 배경이 되는 뉴욕의 다양한 풍경은 여느 영화에서도 만나보지 못했던 색다른 모습으로 다가올 뿐만 아니라 촬영 당시 현장의 소리들을 고스란히 담아내어 뉴욕의 분위기를 그대로 전달해 관객들에게 마치 여행을 하는 듯한 느낌을 전한다 에선 뉴욕시 자체가 거대한 캐릭터나 다름 없었다 존 카니 감독은 음악을 완성한 후 뉴욕 거리 곳곳을 다니며 촬영하기를 원할 때 직접 자전거를 타고 시내를 돌아다니며 영화 속 댄과 그레타가 펼칠 장소를 찾아 다녔다 미술을 담당한 채드 키스와 함께 가능한 뉴욕을 새로운 모습으로 다르게 담아내고 싶었고 사실적으로 보여주길 원했다 거기에 영화의 감성을 녹여낼 수 있는 서사적인 풍경과 거리에서 들려오는 흥미로운 사운드를 잘 조화시키는 것도 중요했다 감독은 늘 제작진에게 이렇게 얘기했다 또 눈으로 장소를 찾고 있죠 왜 이곳에서 녹음을 하고 싶은 거죠 이런 주문에 제작진은 눈이 아닌 귀로 장소를 찾아냈다 자신의 고향인 아일랜드 더블린과 뉴욕을 흡사하다 말한 존 카니 감독은 오랜 시간 동안 쌓여온 뉴욕 특유의 분위기로 인해 어느 곳에 카메라를 두어도 이야기가 만들어지는 것에 만족감을 표하며 이 영화를 일종의 뉴욕에 바치는 연애편지라 표현했다 배우들 역시 뉴욕 거리 촬영에 굉장한 흥미를 표했는데 마크 러팔로는 뉴욕은 예술가가 되기에도 사랑에 빠지기에도 너무나 좋은 장소다 영화의 배경으로 딱 맞았다고 감탄했으며 애덤 리바인은 내 인생 최고의 시간이라고 말할 정도로 만족감을 표했다 더불어 실제로 뉴요커인 마크 러팔로의 자연스러운 모습과 영국에서 온 키이라 나이틀리가 뉴욕을 바라보는 이방인으로서의 시선이 캐릭터에 그대로 묻어나 한결 풍성한 이야기를 만들 수 있었다  OST  Lost Stars  Adam Levine  Keira Knightley Please dont see just a boy caught up in dreams and fantasies Please see me reaching out for someone I cant see Take my hand lets see where we wake up tomorrow Best laid plans sometimes are just a one night stand Ill be damned Cupids demanding back his arrow So lets get drunk on our tears and God tell us the reason youth is wasted on the young Its hunting season and the lambs are on the run Searching for meaning But are we all lost stars trying to light up the dark 보지 말아요 한낱 꿈과 환상에 빠진 소년을 날 봐요 보이는 누군가에게 손 뻗는 나를 그 손을 잡고 내일 아침 함께 눈을 떠요 미래를 계획하지만 때론 하룻밤 놀이일 뿐 어처구니없게도 큐피드는 화살을 되돌려 달라지 그럴땐 우리 눈물에 취해봐요 하느님 왜 청춘은 청춘에게 주기엔 낭비인가요 사냥 철이 왔으니 이 어린 양은 달려야 해요 의미를 찾아 헤매는 우린 어둠을 밝히고 싶어하는 길 잃은 별들인가요 Like A Fool  Keira Knightley We take a chance from time to time And put our necks out on the line And you have broken every promise that we made And I have loved you anyway Took a fine time to leave me hangin out to dry Understand now Im greivin So dont you waste my time Cause you have taken All the wind out from my sails And I have loved you just the same We finally find this then youre gone Been chasin rainbows all along And you have cursed me When theres no one left to blame And I have loved you just the same And you have broken every single fucking rule And I have loved you like a fool 때론 우린 운을 믿곤 해 위험한 모험도 감수하지 넌 우리가 한 모든 약속들을 산산이 부쉈지만 그래도 난 널 사랑했어 오랜 시간 끝에 난 홀로 남겨졌고 아직 난 슬프지만 이젠 널 잊을거야 넌 나의 돛에서 모든 바람을 앗아갔지만 그래도 난 널 사랑했어 함께 삶을 찾는 순간 넌 떠났지 무지개만 좇아 다닌 너였어 원망할 곳 없을 때 넌 날 저주했지만 그래도 난 널 사랑했어 마지막 남은 원칙들도 모두 어겨버린 널 난 바보처럼 사랑했어 Tell Me If You Go Home  Keira Knightley Maybe You dont have to smile so sad Laugh when youre feeling bad I promise I wont Chase you You dont have to dance so blue You dont have to say I do When baby you dont Just tell me The one thing you never told me Then let go of me Hell just throw me Maybe if you wanna go home Tell me if Im back on my own Giving back a heart thats on loan Just tell me if you wanna go home 아마도 넌 슬플 때 웃지 않아도 돼 웃어 네가 기분 나쁠 때는 약속할게 난 그러지 않을 거라고 너를 쫓아 넌 우울할 때 춤추지 않아도 돼 넌 네가 하기 싫을 때 할거라고 말할 필요가 없어 그냥 이제까지 내게 말하지 않은 하나만 말해줘 그냥 나를 완전히 놓아줘 그냥 나를 던져줘 날 바래다준다면 혼자로 남을 건지 말해줘 빌려줬던 마음을 되찾아 돌아갈건지 내게 말해줘";
		ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2(reqStr, null);
		System.out.println("#Result:"+result.toString());

		ArrayList<ArrayList<String>> resultArr = new ArrayList<ArrayList<String>>();

				ArrayList<String> s1 = new ArrayList<String>();
		s1.add("NNG||NNB&&NNG||NNB&&NNG||NNB&&NNG");
		s1.add("NNG||NNB&&NNG||NNB");
		ArrayList<String> findTags = SeunjeonUtil.getListForRule2(s1);

		String lv = "";
		String cv = "";
		String lw = "";
		String ww = "";
		String lww = "";

		ArrayList<String> newResult = null;

		for (ArrayList<String> ls : result) {
			resultArr.add(ls);
			System.out.println("#current param:"+ls);
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
					String newStr = cv+" _ _ "+ww+" _ _ _ _";
					ArrayList<String> newItem = new ArrayList<String>();
					newItem.addAll(Arrays.asList(newStr.split(" ")));
					resultArr.add(newItem);
				}

				System.out.println("####### lv:"+lv+"     ### lww:"+lww+"   ### ww:"+ww+ "         ##### "+cv);

				lv = ls.get(0);
				lw = ls.get(3);
				lww = ww;
			}
		}

		for(ArrayList<String> res : resultArr) {
			System.out.println("### process Result:" + res);
		}
	}


	@Test
	public void test_getMatchingSyntaxWords2() throws Exception{
		String reqStr = "사림이고수월한사릉한다바람공주데이터는하늘에있을수도없을수도더있지않을까";
		reqStr = "오랜만에 영화를 봤네요  오빠가 엘지랑 에스케이 브이아이피여서 돈한푼안내고 영화보고왔네요 정말 감탄했어요  똑똑한 미저리가 생각납니다  평점먼저하자면 10점 만점에 9점 너무 많이주는걸까요 저는 손에 땀을쥐어가며 흥미롭게 봤어요  영화줄거리는 처음써봐서 잘될지 모르겠네요  나를찾아줘 줄거리 시작합니다 처음에 남자가 안좋은 표정으로 바에들어가서 여자와 농담따먹기하며 술을마십니다 술을마시고 있는데 전화를 받고 집을 들어가죠 거실에는 깨진 유리조각들이 널부러져 있죠 아내가안보이니까 경찰에 신고를해서 여자경찰관이 수사를합니다 알고보니 아내가 유명인사였드라고용 중간중간 아내가 일기장을쓴 내용들이 나오는데요 처음에는 남편이 완벽남에 여자를 엄청 사랑해 줬었죠 그럼데 시간이 지날수록 남편이 게을러지고 폭력까지 행한다고 그렇게 나옵니다  처음에는 저도 남편이 범인인줄알았어요  중반으로갈수록 아내가 남편이 바람펴서 복수하기위해 남편을 살인범으로 만드는 알리바이를 만들어 둔것인데요 아무튼 남편도 그렇게 좋은놈은 아닌듯  알고보니 그전 남자에게도 똑같이 복수한 전적이 있더라구요 여자가 남자를 조종하려고해서 시간을두고 멀어지려고 했는데 강간범으로 알리바이를만들고 신고를해서 사회생활을 못하게 만들었죠  아무튼 남자주인공이 자신이 죽이지 않았기때문에 떳떳하다고 변호사도 선임하지 않다가 아내가 머리가 너무똑똑해서 혼자힘으로는 도저히 힘들것 같아서 tv에나왔던 변호사를 찾아가죠 여자가 신고했던 남자둘을 만나는데 한남자가 조금이상함  그러는동안 아내가 다른곳에 은신해있다가 이상한 양아치 여자한테 돈많은걸 걸려서 빼앗기죠  그러고나서 여자가신고했던 조금이상했던 한남자를 찾아갑니다 알고보니 그여자를 정말사랑했던 남자인데 이남자도 미저리끼가 다분해보임  그러던중 남편이 연기를 기가막히게 하는 인터뷰를 보는데 다뉘우쳤다 잘못했다 아직도 사랑하고있다 이러고있으니 눈이 뒤집힘 또계획을짜서 미저리끼있는 남자를 죽이고 남편한테 돌아가요 그러고 연기를 살발하게하죠 어떻게 임신을 한건지 선물이라며 임테스트기를주고 남자는 분노하지만 똑같이 연기를하며 몇주를 그렇게 살아요 그러고 처음에나왔던 아네의 머리를 쓰다듬으면 말을하는데 대사는 기억이 안나고  처음에그장면이 나오고 마지막에도 똑같이 나오는데  그제서야 남편이 왜 그런말을 했는지 알꺼같더라구요 너무 두서없이 생각나는대로 막적은거 같네요  무튼 결말은 아내의 승리입니다 경찰들이 허술한건지 아내가 똑똑한건지  그렇게 많이 무서운장면은 없는데요 혼자 자해하고 미저리끼있는 남자를 죽일때 최큼 보기힘든정도 저만그런가요  오징어콤본가 오다리콤보인가 뭐시기 냠냠 먹으면서 잘봤어요 sf를 좋아하지만 이런 것들도 나름 좋아해요 뿅 나를 찾아줘 감독 데이빗 핀처 출연 벤 애플렉 로자먼드 파이크 개봉 2014 미국 평점 리뷰보기   나를 찾아줘 감독 데이빗 핀처 출연 벤 애플렉 로자먼드 파이크   스포가 포함될 수 있습니다  벤자민 버튼의 시간은 거꾸로 간다와 밀레니엄여자를 증오한 남자들 등을 연출한 데이빗 핀처 감독의 영화 나를찾아줘 친구가 이거 평점이 높다고 보자고 보자고 하다가 마침 고등학교 친구가 영화표를 공짜로 줘서 함께 코엑스 메가박스 프리미엄관에서 봤었던 영화 일단 한줄로 영화평을 하자면 퀄리티 높은 멘붕영화라고 할 수 있겠다 나를 찾아줘 라고 친구가 스릴러라고 하길래 뭔 소리지 심리적인 스릴러인가 했는데 내 기준의 스릴러는 쏘우같은 또는 데스티네이션 같은 긴장감이 도는 공포물을 생각 그것과는 전혀 다른 느낌의 완성도 높은 영화라고 할 수 있겠다   영화의 첫 장면은  아내의 머리속 두개골을 부수고 뇌를 꺼내 무슨 생각을 하는지 알고싶다   뭐지 이건 뭔 x소리지 라는 한문장 영화를 다보면 이해가 가는 문장이다    일단 영화 속의 주인공은 닉과 에이미이다 에이미는 뜻하지않게 부모님의 어린이 동화소설 어메이징 에이미의 주인공으로 유명인사가 되며 많은 사람들의 관심을 받는 작가이다   완벽한 결혼생활을 꿈꿔온다 그러던 중 자기가 찾던 가장 이상적이며 완벽한 사람인 닉을 만나 결혼을 하게 된다   결혼하여 5년동안 행복하게 살던 중 갑자기 아내가 사라지는데     맞다 이 영화는 아내의 실종사건과 관련해서 나오는 줄거리이다 닉이 아내를 찾기 위해 방송 그리고 경찰에게 알리면서 사건은 더욱 주목받게 된다  줄거리가 계속 이어져가면서 액자형식으로 에이미가 한 공책에 일기를 쓰는 듯한 장면이 겹치면서 이야기가 전개된다    모든 것이 완벽하기만 했던 그녀 에이미의 생활은 닉의 엄마가 유방암이 걸려 그의 고향으로 갔을 때부터 완벽한 삶이 망가져버린다  전부터 둘은 서로 실직이 되면서 가난을 느끼면 사는 부분부터 서로 틀어지게 되는데 결정적으로 고향에 내려가면서 부터 어긋나게 되버림   알고보니 닉이 자신이 가르치던 어린 제자와 바람이 났었던 것 이 모든 사실을 알고 있었던 에이미는  그야말로 닉에게 복수를 꿈꾸게 된다  닉과 그의 바람난 여제자가 성관계를 했던 집안 곳곳 그리고 사무실 닉의 쌍둥이 동생 마고의 창고까지  닉이 에이미를 죽게 했다는 자작 시나리오를 쓰면서 철저하게 증거까지 만들어  언론이 모두 에이미의 죽음 위에 닉이 범인 이라고 지목하게 만든다   진짜 아내인 에이미의 연기는 소름이 돋았다 완벽할 것 같은 그녀였지만 정말 너무나도 철저하고 완벽해버린 그녀의 생각과 행동이 보는 내내 소름을 돋게 만든 것   그러고 에이미는 머리를 자르고 염색을 하여 혼자 돌아다니면서 자살계획까지 세운다  달력에 꼬박꼬박 체크하면서 자살날짜와 언론까지 뒤흔드는 에이미에게 닉의 고통은 그저 즐겁기만 하다   그것도 잠시 에이미는강도커플에게 돈을 빼앗기게 되면서 계획이 틀어지게 되는데  결국 자신을 고등학교 때부터 쫓아다녔던 데시에게 연락하여 그의 별장에서 지내게 된다 별장을 지내던 중  닉은 거짓된 말과 발언으로 TV쇼에 나가 자신의 잘못을 뉘우치며 에이미가 돌아오길 바란다는 인터뷰를 하므로써 여론의 마음을 돌리고 에이미의 마음까지 돌려놓는다 인터뷰를 본 에이미는 동요되어 결국 자신을 사랑하는 데시를 죽이고 강간범으로 만들어 닉에게로 돌아가게 된다     처음부터 끝까지 모든 것을 알고 있는 닉은 돌아온 그녀가 석연치 않고  이같이 돌아가고 있는 상황이 어이가 없고 점점 무서워지지만 절대 닉을 해치지 않을 것이며 그의 아이를 가졌다는 에이미의 말을 듣고  거짓된 삶을 살아가기로 결정한다 마지막 부부로 함께 인터뷰에 나와 에이미에게 이렇게 말한다   우린 공범이야적막 저희 곧 부모가 되요     The END     이게 뭐야 어이없고 황당한 줄거리에 난 영화가 끝난후 실성하였다   친구랑 서로 마주보며 뭐냐 이거  라고 하며 웃어버린 것  그리고 영화관에 우산을 놓고 집에 갔었지 얼마나 어이없었으면   근데 생각해보면 스토리짜임새가 정말 꼼꼼하고 퀄리티가 매우 높았던 영화라고 할 수 있겠다  내 생각으로는 이렇게 완벽한 삶을 원했던 에이미의 바탕에는 그녀를 완벽한 주인공으로 만들기만 했던 어메이징 에이미의 작가 엄마도 한몫했던 거다    영화 속 초반에 이런 내용이 나온다 어메이징 에이미는 모든것에 완벽했지만 실상 그녀는 완벽하지 않았다고   그래서 바람피는 남편과의 지긋한삶을 살아가는 자신이 가장싫어하는 아내로 살아가기 보다는 바람핀 남편에게 버림받아 죽임을 당하는 아내로사는 비운의 여인의 삶을 택하려고  이같은 자작 스토리를 완벽하게 구사하여이혼보다는 죽음을 택하여 남편에게 복수를 꿈꾸는 에이미로 살아가는 것을 말이다   한편으로는 불쌍한 여인같기도하다   싸이코 같은 멘붕 영화이면서도 영화 스토리 및 장면 기법을 눈여겨 봐도 좋을 것 같다  난 이 영화에서 거의 처음부분에 닉과 에이미가 연애하는 장면 중 밀가루가 날리는 장면에서 밀가루 맞나 키스하는 장면이 진짜 아름답다고 생각한다   영화를 보시면 아실 듯    나를 찾아줘 예고편      영화리뷰 나를 찾아줘 2014  줄거리  결말  어메이징 로자먼드 파이크 by 러블리주      hello 마이럽 잇님들 여든세 번째 영화리뷰를 들고 온 러블리 주에요  작년에 개봉했을 당시에 감독의 이름만 듣고도 보고 싶었던 욕구가 솟구쳤던 영화가 있었는데요  오늘은 그 영화를 리뷰할까 합니다  며칠 전 새벽에 영화를 보고 나니 새벽 4시가 가까웠는데 정말 놀랍도록 소름 끼치는 모습의 로자먼드 파이크 연기가 내내 맴돌아서 잠을 쉽게 이룰 수 없었다지요  영화를 보고 난 다음 나도 모르게 세상 최고의 망할 냔이 여기에 있었구나라는 생각을 떨치지 못 했던 그 영화   나를 찾아줘  GONE GIRL  아래의 평점은 지극히 주관적인 저의 평점입니다  나를 찾아줘 감독 데이빗 핀처 출연 벤 애플렉 로자먼드 파이크 개봉 2014 미국 평점 리뷰보기   데이빗 핀처 감독의 작품으로 스릴러 장르를 연출하는데 있어서 탁월한 센스를 가진 감독이라고 개인적으로 생각을 하는데요  세븐 더 게임 파이트 클럽 패닉 룸 조디악 그리고 밀레니엄  여자를 증오한 남자들까지 다양한 작품 속에서 스릴러를 제대로 표현하는 센스 있는 감독님이지요  소셜 네트워크라는 영화로도 호평을 받았던 감독  개인적으로 세븐과 더 게임을 참 좋아하는 관객으로서 이 작품도 감독의 이름만으로도 보고 싶게 만드는 욕구가 엄청 솟았으나 개봉 당시에는 보지 못하고 뒤늦게 보았는데요  청소년 관람불가 등급 임에도 불구하고 단기간 100만 관객을 국내에서 돌파한 작품이에요 2012년에 출간된 길리언 플린의 동명 소설을 원작으로 한 작품으로 2014년 뉴욕 영화제의 개막작으로 선정되기도 했었지요  보통의 스릴러 영화들이 후반부로 갈수록 반전의 윤곽이 드러난다면 나를 찾아줘는 중반부에서 이미 반전을 보여주고 있는 작품인데요 반전을 이미 보여줬음에도 불구하고 오히려 더 스토리에 힘이 실리고 여자 주인공 에이미가 어떻게 더 악랄하게 변할까하는 모습을 기대하게 만들면서 관객이 더 몰입할 수 있게 만들어주는 작품이었어요  특히 주인공 에이미를 연기한 로자먼드 파이크의 소름 끼치는 모습이 이 영화의 메인이 아니었나 싶네요 이 작품으로 아카데미 시상식 여우주연상에 노미네이트가 된 그녀 아카데미에서 수상을 하지는 못했지만 런던 비평가 협회와 새턴 어워즈에서 여우주연상을 수상을 했을 정도로 강한 존재감을 보여줬다고 할 수 있지요  정말 운동화 신발 끈 같은 십자수 십장생 같은 여자였어요     모두 다 부러워하는 삶을 살아가는 완벽한 커플 닉과 에이미 결혼 5주년 기념일 아침 에이미가 흔적도 없이 실종된다 유년시절 어린이 동화 시리즈  어메이징 에이미 의 실제 여주인공이던 유명인사 아내가 사라지자 세상은 그녀의 실종사건으로 떠들썩해진다  한편 경찰은 에이미가 결혼기념일 선물로 숨겨뒀던 편지와 함께 곳곳에서 드러나는 단서들로 남편 닉을 유력한 용의자로 지목한다 미디어들이 살인 용의자 닉의 일거수일투족을 보도하기 시작하고 시간이 갈수록 세상의 관심이 그에게 더욱 집중된다  줄거리 스틸컷 출처 네이버 영화   스틸컷이 많은 작품이 아니어서 글의 내용이 더 많은 포스팅입니다      뉴욕에서 살던 여자 에이미로자먼드 파이크는 남편을 따라 미주리로 이사를 오게 되고 모든 사람들에게 완벽한 이미지의 에이미는 결혼생활도 완벽해 보이는 부부지만 속 사정은 삐걱거리고 있는데요  에이미는 결혼기념일이 되면 남편 닉벤 에플렉에게 단서를 주고 결혼기념일 선물을 찾게 합니다  두 사람만이 알 수 있는 단서를 닉에게 전해주고 그 단서를 통해서 닉이 찾을 수 있는지를 시험하는 에이미  결혼기념일마다 닉이 자신에 대해서 얼마나 기억을 하고 있고 관심이 있는지 확인을 하고 싶어 하는 여자의 심리를 고스란히 보여주고 있는 것 같았어요  하지만 닉은 결혼기념일이 되면은 그 단서를 가지고 선물을 찾아야 하는 것이 보통 부담스러운 것이 아니지요     두 사람이 처음 만났던 날 묘한 이끌림을 느꼈고 사람들이 많은 파티장에서 나온 두 사람은 설탕이 흩뿌려지는 곳을 지나 설탕처럼 달달함을 고스란히 전해주기도 하며  많은 사람들이 지켜보는 가운데 멋지게 프러포즈를 하면서 사람들에게 부러움의 대상이 됩니다     현재의 닉에게 결혼기념일 선물 찾기는 너무나 스트레스가 되었지만 달달했던 시절 닉에게는 에이미와 함께 결혼기념일 선물을 찾는 그 과정도 너무 큰 행복이었고 그들에게는 빼놓을 수 없는 즐거움이었지요      하지만 에이미의 시험은 여기서 끝이 나는 게 아니라 같은 여자가 봐도 과하다 싶을 정도로 도가 지나칩니다  닉이 자신에게 처음으로 보여줬던 관심과 사랑이 끝까지 유지되길 바라는 마음을 이해하지만 자신이 바라는 것이 어느 순간 닉에게 강요하는 꼴이 되어버렸고 자신의 생각한 대로 대답하고 행동하길 바라는 에이미의 모습이 점점 늘어나자 닉은 그녀에게 지쳐버리고 말았던 것이죠  예를 들어 둘 다 직장을 실직한 상태라면 닉은 둘이서 함께 헤쳐나가야 한다는 정석 같은 대답이 나오길 바라는 것이죠  그런 행동과 말들 속에서 처음 만났던 날 이끌렸던 두 사람의 모습을 자꾸만 찾으려고 하고 보상받으려고 하는 에이미     그런 에이미에게 점점 질려가던 닉은 그들의 5주년 결혼기념일에 그녀의 아내 에이미가 사라진 것을 알게 됩니다  쌍둥이 여동생과 함께 바를 운영하고 있던 닉은 집으로 돌아왔을 때 부서진 탁자를 보고 즉각 경찰에 신고를 하게 됩니다  신고를 받고 집으로 온 경찰은 에이미의 사진을 보고 유명한 동화 시리즈인  어메이징 에이미 의 실제 주인공인 에이미가 실종되었다는 사실을 알게 되지요  깨진 탁자를 보고 수상하게 여긴 닉은 즉각 경찰에 신고를 하기는 했지만 그녀의 실종을 그다지 심각하게 여기는 것처럼 보이지는 않습니다  그리고 부엌에서 발견된 핏자국을 보고 심각성을 느낀 경찰은 이것저것 닉에게 에이미에 관한 정보를 묻지만 닉은 에이미에 대해 시원하게 대답하지 못 합니다      한때 유명인사였던 에이미가 실종이 되었으니 빨리 찾을 수 있도록 공개수사로 전환해야 한다는 경찰  형사는 닉에게 기자 회견을 요청하고 에이미의 부모님과 함께 많은 사람들이 지켜보는 앞에서 얼떨결에 기자 회견을 열게 됩니다  순식간에 일어난 상황에 적응을 하지 못하는 닉 닉은 미처 준비되지도 않은 상황에서 자신의 아내가 실종되었으니 찾을 수 있도록 도와달라는 말만 겨우 하지만  에이미의 부모님은 마치 이런 일이 있기를 기다렸다는 듯이 에이미를 찾을 수 있도록 전용 전화라인도 만들어놓고 홈페이지도 만들어서 기자 회견장에서 알려주는 치밀함을 보여줍니다  기자 회견을 마치고 기자 들은 닉에게 에이미의 사진 옆에서 포즈를 취해달라며 웃어달라는 말도 안 되는 요구를 하는데요  기자 들의 요구에 미소를 지으며 사진을 찍은 닉은 오히려 구설수에 오르게 되지요  경찰이 온 순간부터 닉에게 점점 더 불리하게 돌아가는 상황     경찰은 에이미가 결혼기념일마다 단서를 남긴다는 사실을 알게 되었고 단서가 적힌 편지를 닉에게 보여줍니다  닉은 단서를 보고 자신의 사무실로 형사를 데려갔고 그 사무실에서 빨간색 여자 팬티를 발견합니다  사실 닉은 학생들을 가르치던 사람으로 자신의 수업을 듣던 학생과 불륜을 저지르고 있었던 거죠  그 팬티가 여학생의 것인지 누가 몰래 숨겨둔 것인지 알 수는 없지만 이로 인해서 닉은 점점 더 이상한 남편으로 비치기 시작합니다     그리고 에이미가 남긴 두 번째 단서 속의 장소를 찾아서 닉은 아버지의 집으로 향하고 닉의 행동을 주시하던 경찰은 닉을 더 의심하기 시작합니다  사실 경찰은 닉에게 에이미가 어떤 사람이었고 친한 친구는 누구며 혈액형이 무언인지에 대한 많은 질문을 하였지만  닉은 에이미는 집에서 책만 읽던 사람이라 친한 친구가 없으며 혈액형 또한 모른다고 대답을 합니다  하지만 에이미의 절친이라며 같은 동네에 살고 있던 여자가 나타나게 되고 그녀는 닉에게 에이미가 임신 중이었다면서 에이미를 어디로 숨긴 거냐며 따지듯 되묻기 시작합니다  닉은 에이미가 임신하지 않았다고 말하면서 절친이라는 여자와는 전혀 왕래도 없었다고 형사에게 어필하지만 형사가 가지고 온 사진에는 두 사람이 사이좋은 모습으로 찍힌 모습들이 가득하죠  설상가상으로 병원기록도 에이미가 임신이라는 것을 입증해줍니다  이렇게 점점 범인으로 몰리는 닉  형사는 집과 닉이 동생과 운영하는 술집도 다 에이미의 명의로 된 집이라는 것을 알아냈고 닉에게는 어마어마한 카드빚이 있다는 것도 알게 되는데요  명품 골프클럽과 비싼 전자제품들을 카드로 결제한 닉 하지만 닉은 누군가 명의를 도용해서 자신의 카드를 쓴 거라고 주장하지만 에이미의 생명보험 지급액 한도까지 높인 것을 알게 되면서 점점 의심이 커져갑니다  결국 변호사를 선임할 의사가 없었던 닉은 자신에게 불리한 상황으로 돌아가자 수임료가 비싸지만 승률이 높은 변호사를 고용하기로 결심합니다     그렇게 모든 미디어에서도 닉의 일거수일투족에 관심을갖고 닉의 행동에 딴죽을 거느라 바쁘고 닉을 세상에서 가장 나쁜 남편으로 몰아가고 있습니다  이렇게 영화가 중반부로 흘러가면서 다시 영화의 시점은 에이미가 사라진 날 아침으로 돌아옵니다  이제부터 시작되는 진짜 에이미의 이야기  에이미는 부부관계를 망친 장본인이 닉이라는 생각이 들기 시작했고 닉을 궁지에 몰아넣고 반성을 하게 만들기 위해서 자신만의 계획을 세우기 시작합니다  갑자기 일어난 실종사건이 아니라 하나부터 열까지 치밀하게 에이미에 의해서 계획된 실종  같은 동네에 사는여자와 계획적으로 친해진 에이미는 여자에게 닉의 험담을 하기 시작합니다 닉에게 학대를 받고 있다는 사실을 이야기하면서 비밀 이야기를 공유한 절친 사이로 만들기 시작하죠  게다가 그 여자는 임신한 상태로 그녀의 소변을 자신의 소변으로 둔갑시켜 병원에서는 임신한 상태로 결과가 나오게 만들어버리는 치밀한 계획도 세웁니다  그리고 안정적인 수입이 없는 닉에게 불리한 상황을 만들기 위해서 닉의 카드로 비싼 물건들을 결제한 것도 에이미가 한 행동이며 자신의 생명보험급 한도를 더 늘린 것도 닉이 아니라 에이미가 한 것이죠  이렇게 치밀한 계획을 세워둔 에이미는 결혼기념일 당일 아침 남편이 산책을 나간 후에 거실 테이블을 부수고 자신의 직접 피를 뽑아서 부엌에 핏자국을 남기고 뽑은 피를 부엌 바닥에 뿌린뒤 급하게 없애버린 것처럼 상황을 만들어 놓습니다  그리고 그 피를 둔기에 묻혀서 벽난로 속에 숨겨두기까지 합니다  여기서 끝이 아니라 지금껏 자신이 학대받고 살았다는 사실을 증명하기 위해서 가짜로 일기장을 작성하기 시작하는데요  그 일기는 진실된 이야기도 있기는 하지만 대부분 부풀려서 작성한 것이죠 그 일기장을 살짝 불태우고 경찰이 단서를 찾을 때 발견할 수 있도록 적당한 장소에 숨겨둡니다  그렇게 모든 단서를 닉에게 불리하게 만들어둔 그녀는 중고차를 사고 돈을 챙겨서 떠나게 되었던 것이죠     그녀는 더 완벽하게 사건을 만들기 위해서 자신의 머리도 갈색으로 염색하고 도도하고 완벽했던 그녀의 모습과는 다르게 행동하고 다닙니다  개걸스럽게 싸구려 음식을 먹던 그녀의 모습이 어쩌면 진짜의 에이미가 아니었을지 닉에게 완벽한 여성으로 보이기 위해서 그녀도 철저하게 꾸미고 자신을 조종하고 있었고  닉도 남들의 눈에 자신과 수준이 맞게 보이도록 은밀하게 만들어왔던 것이 아닐까하는 생각을 하게 만들었어요   ";

		ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2(reqStr, null);
		System.out.println("#Result:"+result.toString());

		ArrayList<ArrayList<String>> resultArr = null;

		ArrayList<String> s1 = new ArrayList<String>();
		s1.add("NNG||NNB&&NNG||NNB&&NNG||NNB");
		s1.add("NNG||NNB&&NNG||NNB");

		resultArr = SeunjeonUtil.getArrayWordsForFindClass(result, s1);

		for(ArrayList<String> res : resultArr) {
			//System.out.println("### process Result:" + res.get(3) + " ");
			System.out.print(res.get(3) + " ");

		}
	}

	@Test
	public void test_getMatchingSyntaxWords23() throws Exception{
		ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2("바람공주데이터는하늘에있을수도없을수도더있지않을까", null);
		System.out.println("#Result:"+result.toString());

		ArrayList<ArrayList<String>> resultArr = null;

		ArrayList<String> s1 = new ArrayList<String>();
		s1.add("NNG||NNB&&NNG||NNB&&NNG||NNB");
		s1.add("NNG||NNB&&NNG||NNB");

		resultArr = SeunjeonUtil.getArrayWordsForFindClass(result, s1);

		for(ArrayList<String> res : resultArr) {
			System.out.println("### process Result:" + res);
		}
	}


	@Test
	public void test_getMatchingSyntaxWords3() throws Exception{
		ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2("사림이고수월한사릉한후바람공주말씀데이터는하늘에있을수도없을수도더있지않을까", null);
		System.out.println("#Result:"+result.toString());

		ArrayList<ArrayList<String>> resultArr = null;

		ArrayList<String> s1 = new ArrayList<String>();
		//s1.add("NNG||NNP||NNG&&NNG");
		s1.add("NNG||NNP||NNB");
		s1.add("NNG||NNP||NNB&&NNG||NNP||NNB");
		s1.add("NNG||NNP||NNB&&NNG||NNP||NNB&&NNG||NNP||NNB");

		s1.add("SN||NR||VA||MM&&NNP||NNG||NNB");
		s1.add("XPN&&NNG||NNP||NNB||NNBC||NR||VV||VA||MM||ETN||ETM||XPN||XSN||XSV||XSA||XR||SL||SN");
		s1.add("NNG||NNP||NNB||NNBC||NR||VV||VA||MM||ETN||ETM||XPN||XSN||XSV||XSA||XR||SL||SN&&XSN||XSV||XSA||ETN||ETM");

		//s1.add("NNG&&NNG&&NNG");
		//s1.add("NNG||NNB&&NNG||NNB&&NNG||NNB");
		//s1.add("NNG||NNB&&NNG||NNB");

		resultArr = SeunjeonUtil.getArrayWordsForMatchClass2(result, s1);

		for(ArrayList<String> res : resultArr) {
			System.out.println("### process Result:" + res);
		}
	}



	@Test
	public void test_getMatchingSyntaxWords4() throws Exception{
		//String reqStr = "사림이고수월한사릉한후바람공주말씀데이터는하늘에있을수도없을수도더있지않을까";
		//String reqStr = " 런던에 있는 7개 팀 중 일곱 팀 야마카시 팀1의 일원인 로건은 어느 날 한 아이를 위한 체육관을 개관하는 일로 방콕에 가게 되고 그 곳에서 한 여인을 만나게 된다  태국 현지의 야쿠자 두목 키타노를 위해 오빠 키엔과 함께 일을 하고 있던 수 그녀는 어느 날 삼합회의 두목 웅 회장의 용 인장을 몰래 훔치게 된다 시간이 흐를수록 수는 키엔에게 더 이상 이런 일을 할 수 없다며 모든 일에서 빠지자고 제안을 하고 결국 그녀는 웅 회장에게 모든 일은 키타노가 시킨 것이라고 말하게 된다  결국 웅 회장은 사위인 키타노가 자신의 조직을 차지하려고 그런 일을 벌였다며 분개한다 그러던 중 키엔은 수의 간절한 부탁에 야쿠자에서 빠지려고 하고 야마카시 팀 일행은 그런 수와 키엔을 도와 두 조직과 싸우는 일에 동참하게 된다  결국 조직과 야마카시 팀은 운명을 건 한판 승부를 준비하게 되는데 액션 모험";
		String reqStr = " 런던에 있는 7개 팀 중 일곱 팀 야마카시 팀1의 일원인 로건은 어느 날";
		ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2(reqStr, null);
		System.out.println("#Result:"+result.toString());

		ArrayList<ArrayList<String>> resultArr = null;

		ArrayList<String> s1 = new ArrayList<String>();
		s1.add("NNG||NNP");
		s1.add("NNG||NNP&&NNG||NNP");
		s1.add("NNG||NNP&&NNG||NNP&&NNG||NNP");
		s1.add("SN||NR&&NNP||NNG||NNB||NNBC");
		s1.add("SN||NR&&NNP||NNG||NNB&&XSN");
		s1.add("XPN&&NNG||NNP");
		s1.add("NNG||NNP&&XSN");
		s1.add("NNG||NNP&&XSV&&ETM");
		s1.add("NNG||NNP&&XSV||XSA&&EC||EF");
		s1.add("VA||VV&&ETM||ETN||EC||EF");
		s1.add("VA&&NNP||NNG");
		s1.add("MM&&NNP||NNG||NNB");

		//s1.add("NNG&&NNG&&NNG");
		//s1.add("NNG||NNB&&NNG||NNB&&NNG||NNB");
		//s1.add("NNG||NNB&&NNG||NNB");

		resultArr = SeunjeonUtil.getArrayWordsForMatchClass2(result, s1);

		for(ArrayList<String> res : resultArr) {
			System.out.println("### process Result:" + res);
		}
	}


	@Test
	public void test1() {
		ArrayList<String> s1 = new ArrayList<String>();
		s1.add("NNG||JX&&NNG");


		String a = "";
		String b = "";
		String c = "";
		String d = "";
		ArrayList<String> tags = new ArrayList<String>();
		if (s1 != null && s1.size() > 0) {
			String[] x1 = null;
			for (String cond : s1) {
				ArrayList<String> tmps = new ArrayList<String>();

				if(cond.contains("&&")) {
					x1 = cond.split("\\&\\&");
					a = x1[0];
					b = x1[1];
					if(x1.length > 2) c = x1[2];
					if(x1.length > 3) d = x1[3];


					if (a.contains("||")) {
						String[] aa = a.split("\\|\\|");
						for (String aaa : aa) {
							tmps.add(aaa);
						}
					} else {
						if (!"".equals(a)) {
							tmps.add(a);
						}
					}
					if (b.contains("||")) {
						String[] bb = b.split("\\|\\|");
						for (String bbb : bb) {
							for (String aaa : tmps) {
								tags.add(aaa+"_"+bbb);
							}
						}
					} else {
						if (!"".equals(b)) {
							for (String aaa : tmps) {
								tags.add(aaa+"_"+b);
							}
						}
					}
				}
			}
		}


		for(String s : tags) {
			System.out.println("#condition tag:"+s);
		}
	}




	@Test
	public void test2() {
		ArrayList<String> s1 = new ArrayList<String>();
		s1.add("NNG&&NNG");
		s1.add("NNG&&NNG&&NNG");
		s1.add("NNG&&NNG&&NNG&&NNG");
		s1.add("SN||NR&&NNG||NNB");
		s1.add("SN||NR&&NNG||NNB&&NNG||NNB");
		s1.add("VA&&NNG||NNP");
		s1.add("MM&&NNG||NNB");
		ArrayList<String> tags = SeunjeonUtil.getListForRule2(s1);

		System.out.println("#tags:"+tags.toString());
	}
}
