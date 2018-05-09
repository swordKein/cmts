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
		reqStr = "다시 시작해 너를 빛나게 할 노래를 싱어송라이터인 그레타키이라 나이틀리는 남자친구 데이브애덤 리바인가 메이저 음반회사와 계약을 하게 되면서 뉴욕으로 오게 된다 그러나 행복도 잠시 오랜 연인이자 음악적 파트너로서 함께 노래를 만들고 부르는 것이 좋았던 그레타와 달리 스타가 된 데이브의 마음은 어느새 변해버린다 스타 음반프로듀서였지만 이제는 해고된 댄마크 러팔로은 미치기 일보직전 들른 뮤직바에서 그레타의 자작곡을 듣게 되고 아직 녹슬지 않은 촉을 살려 음반제작을 제안한다 거리 밴드를 결성한 그들은 뉴욕의 거리를 스튜디오 삼아 진짜로 부르고 싶었던 노래를 만들어가는데  ABOUT MELODY  존 카니 감독이 선사하는 또 하나의 음악여행 눈과 귀를 즐겁게 만드는 감성폭발 영화의 탄생 은 스타 명성을 잃은 음반프로듀서와 스타 남친을 잃은 싱어송라이터가 뉴욕에서 만나 함께 노래로 다시 시작하는 이야기를 그린 로맨틱 멜로디이다 제목처럼 인생에서 최악이라고 할만한 하루를 보낸 두 주인공이 우연히 만나 진짜로 부르고 싶은 노래를 통해 다시 시작하는 이야기를 그린다 로 세계적인 신드롬을 일으켰던 존 카니 감독은 으로 또 한 번의 신드롬을 예고한다 존 카니 감독의 전작 는 선댄스 영화제 관객상 수상을 시작으로 올해의 영화로 등극한 후 독립영화상을 수상하고 사운드트랙으로 그래미상 후보 주제곡 Falling Slowly로 아카데미 주제가상을 수상했다 국내에서도 다양성 영화 최초로 20만 이상의 관객을 동원하는 등 아트블록버스터의 힘을 과시한 바 있다 스토리와 음악을 조화롭게 만드는 탁월한 연출로 관객의 마음을 사로잡는 존 카니 감독은 이번 영화에서 역시 음악과 영화를 아름답게 섞어내어 더욱 깊어진 감성으로 자신의 진가를 유감없이 발휘한다 뉴욕 거리 곳곳을 배경으로 아름다운 선율 안에 인생과 사랑 예술을 담아 한층 업그레이드 된 탄탄한 스토리로 관객들을 몰입하게 만든다 전작 와는 다른 성향의 음악으로 영화를 만들고 싶었던 존 카니 감독은 한 때 프로 뮤지션이었던 자신의 과거 경험을 되살려 내어 영화 속에 실감나게 녹여내었다 존 카니 감독은 보다 더 많은 이야기가 담겨 있다며 영화에 대한 자신감을 표했다 또한 인생의 어느 지점을 마주한 캐릭터를 드러낼 수 있는 곡을 작업했다 멋진 곡들을 완성시켰고 아주 성공적으로 흘러갔다고 밝힌 바 영화 속 아름다운 멜로디와 서정적인 가사가 돋보이는 노래들은 관객들에게 충분한 만족감을 선사할 것이다 마크 러팔로는 존 카니 감독은 마치 뮤지컬 영화계의 존 카사베츠 같다며 감독에 대한 믿음을 드러냈고 키이라 나이틀리는 이렇게 희망으로 가득 찬 작품을 찾기는 힘들 것이라고 자부했다 이에 지난해 토론토 영화제에서 먼저 공개된 후 극찬을 이끌어냈고 올해 상하이국제영화제 예술공헌상을 수상하며 평단의 인정을 받았다 노래가 있는 단 하나의 로맨틱 멜로디 은 가슴 두근거리게 만드는 노래와 유쾌하게 공감을 일으키는 스토리 희망을 찾아가는 메시지가 청량감을 전하는 완벽한 여름 영화의 탄생을 기대하게 한다 키이라 나이틀리 마크 러팔로 헤일리 스테인펠드 마룬5 애덤 리바인과 씨 로 그린 모스 데프까지 할리우드 톱배우와 세계적인 톱가수의 환상적인 시너지 에는 연기력으로 정평이 난 할리우드 최고의 배우들과 세계적인 아티스트들이 총출동한다 다양한 작품을 통해 대체 불가능한 배우로 자리잡은 마크 러팔로와 시리즈로 아카데미상 후보로 지명된 키이라 나이틀리가 환상의 호흡을 선보인다 그리고 로 아카데미 후보에 오른 할리우드의 신성 헤일리 스테인펠드 의 폴 포츠 역할을 한 제임스 코든 로 아카데미상 후보에 오르고 뉴욕비평가협회상을 수상한 캐서린 키너가 등장한다 또한 2013년 피플지가 선정한 현존하는 최고의 섹시남 1위에 오른 마룬5 애덤 리바인이 주요 역할로 등장해 이목을 집중시킨다 그래미상을 수상한 싱어송라이터 씨 로 그린과 힙합의 보석이라 불리는 래퍼이자 배우인 모스 데프까지 세계적인 톱가수들을 캐스팅하며 최강 라인업을 완성 영화의 완성도를 높였다 제작자인 안소니 브렉맨은 우리 영화의 배우들은 그야말로 엄청난 조합이다 영화 속에서 이렇게 다양한 분야의 배우들을 한 자리에 모아 놓는다는 건 놀라운 일이다라고 소감을 밝혔다 마크 러팔로는 헐크와는 상반된 자유분방한 이미지로 색다른 매력을 선사한다 그의 캐릭터인 댄은 음악에 관해서는 뚝심 있고 열정적인 인물로 마크 러팔로는 심도 깊은 내면 연기로 내공 있는 연기력을 확인시켜 준다 싱어송라이터인 그레타 역할을 맡은 키이라 나이틀리는 촬영 전부터 보컬 코치를 받아 단련한 수준 높은 노래 실력을 선보인다 마크 러팔로는 키이라는 정말 대단하다 그녀는 무엇이든 해낼 의지가 있으며 또 재능 있는 가수다라고 칭찬을 아끼지 않았다 다른 출연 배우들도 밴드를 결성하는 영화 속 설정을 위해 악기 연주를 배워 직접 연주했다 헤일리 스테인펠드는 극 중 무뚝뚝한 십대 소녀지만 음악을 통해 아빠와의 관계를 개선해나가는 바이올렛 역할을 연기하기 위해 기타 연주를 익혔다 그녀는 감독님이 내가 연기한 것을 칭찬해 줄 때마다 너무 좋았다며 작품에 대한 열정을 드러냈다 을 통해 처음 스크린에 데뷔하는 마룬5의 애덤 리바인은 록스타가 된 그레타의 남자친구인 데이브로 분해 영화 속에서 노래는 물론 따뜻하고 다정한 모습부터 변심하는 모습 이별을 후회하는 과정까지 처음이라고 믿을 수 없을 정도로 다양한 감정연기를 소화해냈다 미국의 대중문화잡지 롤링스톤은 애덤 리바인의 연기에 대해 놀랍도록 훌륭하고 자연스럽다고 극찬했다 애덤 리바인은 첫 영화를 마친 후 사람들이 해준 조언은 대사를 외워라와 연기하려 하지 말고 그냥 듣고 반응해라였는데 아주 큰 도움이 되었다 연기 경험이 전무했지만 저 스스로 최선을 다하고 있다고 느끼며 연기할 수 있었다고 만족감을 드러냈다 댄의 오래된 친구이자 힙합스타로 출연한 씨 로 그린은 음악을 하는 것과 연기하는 것은 비슷하다 모두 움직임의 운율이다라고 소감을 밝혔다 내로라하는 연기파 배우들과 톱가수들의 환상적인 시너지가 만들어낸 최고의 팀워크는 을 더욱 빛나게 만드는 가장 중요한 요소로 작용했다 로맨틱한 멜로디와 가사 감성을 자극하는 명품 OST 2014년 제17회 상하이국제영화제 예술공헌상 수상 또 한 번의 신드롬 예고 뉴욕의 특색 있는 분위기와 로맨틱한 멜로디가 담긴 은 올해 제17회 상하이 국제영화제에서 음악상인 예술공헌상을 수상하며 평단의 인정을 먼저 받았다 또한 영화 개봉에 앞서 공개된 OST 음반이 음원차트 상위권을 휩쓸며 주목을 받고 있다 존 카니 감독은 에 전형적인 사랑 노래나 댄스곡 비트의 선율이 아닌 영화의 배경이 되는 뉴욕의 분위기와 맥박을 표현한 멜로디를 담고 싶어했다 또한 요즘 인기 있는 음악과 다른 장르의 음악임은 물론 캐릭터들의 깊은 내면까지 반영할 수 있는 노래여야 한다고 생각했다 그런 그가 영화의 음악작업을 위해 선택한 사람이 바로 그렉 알렉산더이다 영화의 총 음악감독을 맡은 그는 싱어송라이터이자 제작자로 2백만 장이 넘는 음반판매고를 기록하며 전 세계적으로 흥행했던 밴드 뉴 래디컬스의 리더이다 미셸 브랜치가 피쳐링한 산타나의 The Game of Love를 작곡해 그래미상을 수상하기도 한 실력파로 그가 만든 노래들은 TV시리즈  영화  등에서 등장하며 지금까지도 끊임없이 사랑 받고 있다 존 카니 감독과 의기투합한 그렉 알렉산더는 뉴욕 특유의 정서와 함께 따뜻하고 감성적인 노래를 작곡했다 존 카니 감독은 에서의 음악은 랑 다르길 원했다 그리고 그렉은 정말 멋진 곡들을 완성시켰다며 극찬했다 의 OST에는 그렉 알렉산더가 작곡하고 아름다운 멜로디와 서정적인 가사가 인상적인 영화 주제곡 Lost Stars를 비롯해 키이라 나이틀리의 보컬 능력을 만나볼 수 있는 Tell Me If You Wanna Go Home 존 카니 감독이 작사 작곡한 Like A Fool을 비롯한 16곡이 등장한다 영화에 등장하는 뮤지션들 애덤 리바인 이외에도 그래미상을 수상한 싱어송라이터 씨 로 그린도 합류해 더욱 다양한 음악적 색채를 선사한다 또한 출연배우인 헤일리 스테인펠드의 노래도 확인할 수 있다 특히 주제곡 Lost Stars는 애덤 리바인과 키이라 나이틀리가 각각의 버전으로 불러서 더욱 화제를 모으고 있다 키이라 나이틀리의 수준 높은 노래 실력과 애덤 리바인의 독특한 음색까지 주옥 같은 곡들로 영화를 가득 채웠다 센트럴파크 엠파이어 스테이트 빌딩 차이나타운 뉴욕 거리 곳곳에서 울려 퍼지는 완벽한 하모니 은 센트럴파크 호수 위 엠파이어 스테이트 빌딩이 보이는 옥상 차이나타운 뉴욕 지하철 등 특색 있는 뉴욕 거리 곳곳에서 촬영했다 영화에 채워진 다양한 장르의 음악에 맞는 각기 다른 장소는 캐릭터의 내면까지 표현하며 또 다른 감동을 일으킨다 밴드가 노래를 부르는 매 장면의 배경이 되는 뉴욕의 다양한 풍경은 여느 영화에서도 만나보지 못했던 색다른 모습으로 다가올 뿐만 아니라 촬영 당시 현장의 소리들을 고스란히 담아내어 뉴욕의 분위기를 그대로 전달해 관객들에게 마치 여행을 하는 듯한 느낌을 전한다 에선 뉴욕시 자체가 거대한 캐릭터나 다름 없었다 존 카니 감독은 음악을 완성한 후 뉴욕 거리 곳곳을 다니며 촬영하기를 원할 때 직접 자전거를 타고 시내를 돌아다니며 영화 속 댄과 그레타가 펼칠 장소를 찾아 다녔다 미술을 담당한 채드 키스와 함께 가능한 뉴욕을 새로운 모습으로 다르게 담아내고 싶었고 사실적으로 보여주길 원했다 거기에 영화의 감성을 녹여낼 수 있는 서사적인 풍경과 거리에서 들려오는 흥미로운 사운드를 잘 조화시키는 것도 중요했다 감독은 늘 제작진에게 이렇게 얘기했다 또 눈으로 장소를 찾고 있죠 왜 이곳에서 녹음을 하고 싶은 거죠 이런 주문에 제작진은 눈이 아닌 귀로 장소를 찾아냈다 자신의 고향인 아일랜드 더블린과 뉴욕을 흡사하다 말한 존 카니 감독은 오랜 시간 동안 쌓여온 뉴욕 특유의 분위기로 인해 어느 곳에 카메라를 두어도 이야기가 만들어지는 것에 만족감을 표하며 이 영화를 일종의 뉴욕에 바치는 연애편지라 표현했다 배우들 역시 뉴욕 거리 촬영에 굉장한 흥미를 표했는데 마크 러팔로는 뉴욕은 예술가가 되기에도 사랑에 빠지기에도 너무나 좋은 장소다 영화의 배경으로 딱 맞았다고 감탄했으며 애덤 리바인은 내 인생 최고의 시간이라고 말할 정도로 만족감을 표했다 더불어 실제로 뉴요커인 마크 러팔로의 자연스러운 모습과 영국에서 온 키이라 나이틀리가 뉴욕을 바라보는 이방인으로서의 시선이 캐릭터에 그대로 묻어나 한결 풍성한 이야기를 만들 수 있었다  OST  Lost Stars  Adam Levine  Keira Knightley Please dont see just a boy caught up in dreams and fantasies Please see me reaching out for someone I cant see Take my hand lets see where we wake up tomorrow Best laid plans sometimes are just a one night stand Ill be damned Cupids demanding back his arrow So lets get drunk on our tears and God tell us the reason youth is wasted on the young Its hunting season and the lambs are on the run Searching for meaning But are we all lost stars trying to light up the dark 보지 말아요 한낱 꿈과 환상에 빠진 소년을 날 봐요 보이는 누군가에게 손 뻗는 나를 그 손을 잡고 내일 아침 함께 눈을 떠요 미래를 계획하지만 때론 하룻밤 놀이일 뿐 어처구니없게도 큐피드는 화살을 되돌려 달라지 그럴땐 우리 눈물에 취해봐요 하느님 왜 청춘은 청춘에게 주기엔 낭비인가요 사냥 철이 왔으니 이 어린 양은 달려야 해요 의미를 찾아 헤매는 우린 어둠을 밝히고 싶어하는 길 잃은 별들인가요 Like A Fool  Keira Knightley We take a chance from time to time And put our necks out on the line And you have broken every promise that we made And I have loved you anyway Took a fine time to leave me hangin out to dry Understand now Im greivin So dont you waste my time Cause you have taken All the wind out from my sails And I have loved you just the same We finally find this then youre gone Been chasin rainbows all along And you have cursed me When theres no one left to blame And I have loved you just the same And you have broken every single fucking rule And I have loved you like a fool 때론 우린 운을 믿곤 해 위험한 모험도 감수하지 넌 우리가 한 모든 약속들을 산산이 부쉈지만 그래도 난 널 사랑했어 오랜 시간 끝에 난 홀로 남겨졌고 아직 난 슬프지만 이젠 널 잊을거야 넌 나의 돛에서 모든 바람을 앗아갔지만 그래도 난 널 사랑했어 함께 삶을 찾는 순간 넌 떠났지 무지개만 좇아 다닌 너였어 원망할 곳 없을 때 넌 날 저주했지만 그래도 난 널 사랑했어 마지막 남은 원칙들도 모두 어겨버린 널 난 바보처럼 사랑했어 Tell Me If You Go Home  Keira Knightley Maybe You dont have to smile so sad Laugh when youre feeling bad I promise I wont Chase you You dont have to dance so blue You dont have to say I do When baby you dont Just tell me The one thing you never told me Then let go of me Hell just throw me Maybe if you wanna go home Tell me if Im back on my own Giving back a heart thats on loan Just tell me if you wanna go home 아마도 넌 슬플 때 웃지 않아도 돼 웃어 네가 기분 나쁠 때는 약속할게 난 그러지 않을 거라고 너를 쫓아 넌 우울할 때 춤추지 않아도 돼 넌 네가 하기 싫을 때 할거라고 말할 필요가 없어 그냥 이제까지 내게 말하지 않은 하나만 말해줘 그냥 나를 완전히 놓아줘 그냥 나를 던져줘 날 바래다준다면 혼자로 남을 건지 말해줘 빌려줬던 마음을 되찾아 돌아갈건지 내게 말해줘  ABOUT MELODY  존 카니 감독이 선사하는 또 하나의 음악여행 눈과 귀를 즐겁게 만드는 감성폭발 영화의 탄생 은 스타 명성을 잃은 음반프로듀서와 스타 남친을 잃은 싱어송라이터가 뉴욕에서 만나 함께 노래로 다시 시작하는 이야기를 그린 로맨틱 멜로디이다 제목처럼 인생에서 최악이라고 할만한 하루를 보낸 두 주인공이 우연히 만나 진짜로 부르고 싶은 노래를 통해 다시 시작하는 이야기를 그린다 로 세계적인 신드롬을 일으켰던 존 카니 감독은 으로 또 한 번의 신드롬을 예고한다 존 카니 감독의 전작 는 선댄스 영화제 관객상 수상을 시작으로 올해의 영화로 등극한 후 독립영화상을 수상하고 사운드트랙으로 그래미상 후보 주제곡 Falling Slowly로 아카데미 주제가상을 수상했다 국내에서도 다양성 영화 최초로 20만 이상의 관객을 동원하는 등 아트블록버스터의 힘을 과시한 바 있다 스토리와 음악을 조화롭게 만드는 탁월한 연출로 관객의 마음을 사로잡는 존 카니 감독은 이번 영화에서 역시 음악과 영화를 아름답게 섞어내어 더욱 깊어진 감성으로 자신의 진가를 유감없이 발휘한다 뉴욕 거리 곳곳을 배경으로 아름다운 선율 안에 인생과 사랑 예술을 담아 한층 업그레이드 된 탄탄한 스토리로 관객들을 몰입하게 만든다 전작 와는 다른 성향의 음악으로 영화를 만들고 싶었던 존 카니 감독은 한 때 프로 뮤지션이었던 자신의 과거 경험을 되살려 내어 영화 속에 실감나게 녹여내었다 존 카니 감독은 보다 더 많은 이야기가 담겨 있다며 영화에 대한 자신감을 표했다 또한 인생의 어느 지점을 마주한 캐릭터를 드러낼 수 있는 곡을 작업했다 멋진 곡들을 완성시켰고 아주 성공적으로 흘러갔다고 밝힌 바 영화 속 아름다운 멜로디와 서정적인 가사가 돋보이는 노래들은 관객들에게 충분한 만족감을 선사할 것이다 마크 러팔로는 존 카니 감독은 마치 뮤지컬 영화계의 존 카사베츠 같다며 감독에 대한 믿음을 드러냈고 키이라 나이틀리는 이렇게 희망으로 가득 찬 작품을 찾기는 힘들 것이라고 자부했다 이에 지난해 토론토 영화제에서 먼저 공개된 후 극찬을 이끌어냈고 올해 상하이국제영화제 예술공헌상을 수상하며 평단의 인정을 받았다 노래가 있는 단 하나의 로맨틱 멜로디 은 가슴 두근거리게 만드는 노래와 유쾌하게 공감을 일으키는 스토리 희망을 찾아가는 메시지가 청량감을 전하는 완벽한 여름 영화의 탄생을 기대하게 한다 키이라 나이틀리 마크 러팔로 헤일리 스테인펠드 마룬5 애덤 리바인과 씨 로 그린 모스 데프까지 할리우드 톱배우와 세계적인 톱가수의 환상적인 시너지 에는 연기력으로 정평이 난 할리우드 최고의 배우들과 세계적인 아티스트들이 총출동한다 다양한 작품을 통해 대체 불가능한 배우로 자리잡은 마크 러팔로와 시리즈로 아카데미상 후보로 지명된 키이라 나이틀리가 환상의 호흡을 선보인다 그리고 로 아카데미 후보에 오른 할리우드의 신성 헤일리 스테인펠드 의 폴 포츠 역할을 한 제임스 코든 로 아카데미상 후보에 오르고 뉴욕비평가협회상을 수상한 캐서린 키너가 등장한다 또한 2013년 피플지가 선정한 현존하는 최고의 섹시남 1위에 오른 마룬5 애덤 리바인이 주요 역할로 등장해 이목을 집중시킨다 그래미상을 수상한 싱어송라이터 씨 로 그린과 힙합의 보석이라 불리는 래퍼이자 배우인 모스 데프까지 세계적인 톱가수들을 캐스팅하며 최강 라인업을 완성 영화의 완성도를 높였다 제작자인 안소니 브렉맨은 우리 영화의 배우들은 그야말로 엄청난 조합이다 영화 속에서 이렇게 다양한 분야의 배우들을 한 자리에 모아 놓는다는 건 놀라운 일이다라고 소감을 밝혔다 마크 러팔로는 헐크와는 상반된 자유분방한 이미지로 색다른 매력을 선사한다 그의 캐릭터인 댄은 음악에 관해서는 뚝심 있고 열정적인 인물로 마크 러팔로는 심도 깊은 내면 연기로 내공 있는 연기력을 확인시켜 준다 싱어송라이터인 그레타 역할을 맡은 키이라 나이틀리는 촬영 전부터 보컬 코치를 받아 단련한 수준 높은 노래 실력을 선보인다 마크 러팔로는 키이라는 정말 대단하다 그녀는 무엇이든 해낼 의지가 있으며 또 재능 있는 가수다라고 칭찬을 아끼지 않았다 다른 출연 배우들도 밴드를 결성하는 영화 속 설정을 위해 악기 연주를 배워 직접 연주했다 헤일리 스테인펠드는 극 중 무뚝뚝한 십대 소녀지만 음악을 통해 아빠와의 관계를 개선해나가는 바이올렛 역할을 연기하기 위해 기타 연주를 익혔다 그녀는 감독님이 내가 연기한 것을 칭찬해 줄 때마다 너무 좋았다며 작품에 대한 열정을 드러냈다 을 통해 처음 스크린에 데뷔하는 마룬5의 애덤 리바인은 록스타가 된 그레타의 남자친구인 데이브로 분해 영화 속에서 노래는 물론 따뜻하고 다정한 모습부터 변심하는 모습 이별을 후회하는 과정까지 처음이라고 믿을 수 없을 정도로 다양한 감정연기를 소화해냈다 미국의 대중문화잡지 롤링스톤은 애덤 리바인의 연기에 대해 놀랍도록 훌륭하고 자연스럽다고 극찬했다 애덤 리바인은 첫 영화를 마친 후 사람들이 해준 조언은 대사를 외워라와 연기하려 하지 말고 그냥 듣고 반응해라였는데 아주 큰 도움이 되었다 연기 경험이 전무했지만 저 스스로 최선을 다하고 있다고 느끼며 연기할 수 있었다고 만족감을 드러냈다 댄의 오래된 친구이자 힙합스타로 출연한 씨 로 그린은 음악을 하는 것과 연기하는 것은 비슷하다 모두 움직임의 운율이다라고 소감을 밝혔다 내로라하는 연기파 배우들과 톱가수들의 환상적인 시너지가 만들어낸 최고의 팀워크는 을 더욱 빛나게 만드는 가장 중요한 요소로 작용했다 로맨틱한 멜로디와 가사 감성을 자극하는 명품 OST 2014년 제17회 상하이국제영화제 예술공헌상 수상 또 한 번의 신드롬 예고 뉴욕의 특색 있는 분위기와 로맨틱한 멜로디가 담긴 은 올해 제17회 상하이 국제영화제에서 음악상인 예술공헌상을 수상하며 평단의 인정을 먼저 받았다 또한 영화 개봉에 앞서 공개된 OST 음반이 음원차트 상위권을 휩쓸며 주목을 받고 있다 존 카니 감독은 에 전형적인 사랑 노래나 댄스곡 비트의 선율이 아닌 영화의 배경이 되는 뉴욕의 분위기와 맥박을 표현한 멜로디를 담고 싶어했다 또한 요즘 인기 있는 음악과 다른 장르의 음악임은 물론 캐릭터들의 깊은 내면까지 반영할 수 있는 노래여야 한다고 생각했다 그런 그가 영화의 음악작업을 위해 선택한 사람이 바로 그렉 알렉산더이다 영화의 총 음악감독을 맡은 그는 싱어송라이터이자 제작자로 2백만 장이 넘는 음반판매고를 기록하며 전 세계적으로 흥행했던 밴드 뉴 래디컬스의 리더이다 미셸 브랜치가 피쳐링한 산타나의 The Game of Love를 작곡해 그래미상을 수상하기도 한 실력파로 그가 만든 노래들은 TV시리즈  영화  등에서 등장하며 지금까지도 끊임없이 사랑 받고 있다 존 카니 감독과 의기투합한 그렉 알렉산더는 뉴욕 특유의 정서와 함께 따뜻하고 감성적인 노래를 작곡했다 존 카니 감독은 에서의 음악은 랑 다르길 원했다 그리고 그렉은 정말 멋진 곡들을 완성시켰다며 극찬했다 의 OST에는 그렉 알렉산더가 작곡하고 아름다운 멜로디와 서정적인 가사가 인상적인 영화 주제곡 Lost Stars를 비롯해 키이라 나이틀리의 보컬 능력을 만나볼 수 있는 Tell Me If You Wanna Go Home 존 카니 감독이 작사 작곡한 Like A Fool을 비롯한 16곡이 등장한다 영화에 등장하는 뮤지션들 애덤 리바인 이외에도 그래미상을 수상한 싱어송라이터 씨 로 그린도 합류해 더욱 다양한 음악적 색채를 선사한다 또한 출연배우인 헤일리 스테인펠드의 노래도 확인할 수 있다 특히 주제곡 Lost Stars는 애덤 리바인과 키이라 나이틀리가 각각의 버전으로 불러서 더욱 화제를 모으고 있다 키이라 나이틀리의 수준 높은 노래 실력과 애덤 리바인의 독특한 음색까지 주옥 같은 곡들로 영화를 가득 채웠다 센트럴파크 엠파이어 스테이트 빌딩 차이나타운 뉴욕 거리 곳곳에서 울려 퍼지는 완벽한 하모니 은 센트럴파크 호수 위 엠파이어 스테이트 빌딩이 보이는 옥상 차이나타운 뉴욕 지하철 등 특색 있는 뉴욕 거리 곳곳에서 촬영했다 영화에 채워진 다양한 장르의 음악에 맞는 각기 다른 장소는 캐릭터의 내면까지 표현하며 또 다른 감동을 일으킨다 밴드가 노래를 부르는 매 장면의 배경이 되는 뉴욕의 다양한 풍경은 여느 영화에서도 만나보지 못했던 색다른 모습으로 다가올 뿐만 아니라 촬영 당시 현장의 소리들을 고스란히 담아내어 뉴욕의 분위기를 그대로 전달해 관객들에게 마치 여행을 하는 듯한 느낌을 전한다 에선 뉴욕시 자체가 거대한 캐릭터나 다름 없었다 존 카니 감독은 음악을 완성한 후 뉴욕 거리 곳곳을 다니며 촬영하기를 원할 때 직접 자전거를 타고 시내를 돌아다니며 영화 속 댄과 그레타가 펼칠 장소를 찾아 다녔다 미술을 담당한 채드 키스와 함께 가능한 뉴욕을 새로운 모습으로 다르게 담아내고 싶었고 사실적으로 보여주길 원했다 거기에 영화의 감성을 녹여낼 수 있는 서사적인 풍경과 거리에서 들려오는 흥미로운 사운드를 잘 조화시키는 것도 중요했다 감독은 늘 제작진에게 이렇게 얘기했다 또 눈으로 장소를 찾고 있죠 왜 이곳에서 녹음을 하고 싶은 거죠 이런 주문에 제작진은 눈이 아닌 귀로 장소를 찾아냈다 자신의 고향인 아일랜드 더블린과 뉴욕을 흡사하다 말한 존 카니 감독은 오랜 시간 동안 쌓여온 뉴욕 특유의 분위기로 인해 어느 곳에 카메라를 두어도 이야기가 만들어지는 것에 만족감을 표하며 이 영화를 일종의 뉴욕에 바치는 연애편지라 표현했다 배우들 역시 뉴욕 거리 촬영에 굉장한 흥미를 표했는데 마크 러팔로는 뉴욕은 예술가가 되기에도 사랑에 빠지기에도 너무나 좋은 장소다 영화의 배경으로 딱 맞았다고 감탄했으며 애덤 리바인은 내 인생 최고의 시간이라고 말할 정도로 만족감을 표했다 더불어 실제로 뉴요커인 마크 러팔로의 자연스러운 모습과 영국에서 온 키이라 나이틀리가 뉴욕을 바라보는 이방인으로서의 시선이 캐릭터에 그대로 묻어나 한결 풍성한 이야기를 만들 수 있었다  OST  Lost Stars  Adam Levine  Keira Knightley Please dont see just a boy caught up in dreams and fantasies Please see me reaching out for someone I cant see Take my hand lets see where we wake up tomorrow Best laid plans sometimes are just a one night stand Ill be damned Cupids demanding back his arrow So lets get drunk on our tears and God tell us the reason youth is wasted on the young Its hunting season and the lambs are on the run Searching for meaning But are we all lost stars trying to light up the dark 보지 말아요 한낱 꿈과 환상에 빠진 소년을 날 봐요 보이는 누군가에게 손 뻗는 나를 그 손을 잡고 내일 아침 함께 눈을 떠요 미래를 계획하지만 때론 하룻밤 놀이일 뿐 어처구니없게도 큐피드는 화살을 되돌려 달라지 그럴땐 우리 눈물에 취해봐요 하느님 왜 청춘은 청춘에게 주기엔 낭비인가요 사냥 철이 왔으니 이 어린 양은 달려야 해요 의미를 찾아 헤매는 우린 어둠을 밝히고 싶어하는 길 잃은 별들인가요 Like A Fool  Keira Knightley We take a chance from time to time And put our necks out on the line And you have broken every promise that we made And I have loved you anyway Took a fine time to leave me hangin out to dry Understand now Im greivin So dont you waste my time Cause you have taken All the wind out from my sails And I have loved you just the same We finally find this then youre gone Been chasin rainbows all along And you have cursed me When theres no one left to blame And I have loved you just the same And you have broken every single fucking rule And I have loved you like a fool 때론 우린 운을 믿곤 해 위험한 모험도 감수하지 넌 우리가 한 모든 약속들을 산산이 부쉈지만 그래도 난 널 사랑했어 오랜 시간 끝에 난 홀로 남겨졌고 아직 난 슬프지만 이젠 널 잊을거야 넌 나의 돛에서 모든 바람을 앗아갔지만 그래도 난 널 사랑했어 함께 삶을 찾는 순간 넌 떠났지 무지개만 좇아 다닌 너였어 원망할 곳 없을 때 넌 날 저주했지만 그래도 난 널 사랑했어 마지막 남은 원칙들도 모두 어겨버린 널 난 바보처럼 사랑했어 Tell Me If You Go Home  Keira Knightley Maybe You dont have to smile so sad Laugh when youre feeling bad I promise I wont Chase you You dont have to dance so blue You dont have to say I do When baby you dont Just tell me The one thing you never told me Then let go of me Hell just throw me Maybe if you wanna go home Tell me if Im back on my own Giving back a heart thats on loan Just tell me if you wanna go home 아마도 넌 슬플 때 웃지 않아도 돼 웃어 네가 기분 나쁠 때는 약속할게 난 그러지 않을 거라고 너를 쫓아 넌 우울할 때 춤추지 않아도 돼 넌 네가 하기 싫을 때 할거라고 말할 필요가 없어 그냥 이제까지 내게 말하지 않은 하나만 말해줘 그냥 나를 완전히 놓아줘 그냥 나를 던져줘 날 바래다준다면 혼자로 남을 건지 말해줘 빌려줬던 마음을 되찾아 돌아갈건지 내게 말해줘";

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
