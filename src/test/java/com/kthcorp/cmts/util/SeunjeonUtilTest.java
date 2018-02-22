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
		ArrayList<ArrayList<String>> result = SeunjeonUtil.getSimpleWords2("사림이고수월한사릉한다바람공주데이터는하늘에있을수도없을수도더있지않을까", null);
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
