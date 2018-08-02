package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.SchedTrigger;
import com.kthcorp.cmts.util.CommonUtil;
import com.kthcorp.cmts.util.MapUtil;
import com.kthcorp.cmts.util.SeunjeonUtil;
import com.kthcorp.cmts.util.WordFreqUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class TestMapperTest {
    @Autowired
    private TestMapper testMapper;

    @Test
    @Rollback(false)
    public void test_getVocListTest() throws Exception{
        ArrayList<Map> result = testMapper.getFaqList();
        //System.out.println("#input data:"+result.toString());

        ArrayList<ArrayList<String>> resultx = null;
        ArrayList<ArrayList<String>> resultArr = null;
        ArrayList<String> s1 = new ArrayList<String>();
        s1.add("NNG||NNP");

        String str = "";
        Map<String, Integer> rx1 = null;
        Map<String, Integer> rx2 = null;
        int uptstat = 0;
        Map<String, Object> req = null;
        int cnt = 0;
        for(Map ma : result) {
            str = ma.get("question") + " " + ma.get("answer");
            resultx = SeunjeonUtil.getSimpleWords2(str, null);
            System.out.println("#elastic result:"+resultx);

            resultArr = SeunjeonUtil.getArrayWordsForMatchClass(resultx, s1);
            //System.out.println("#resultArr:"+resultArr);
            /*1
            for(ArrayList<String> res : resultArr) {
                System.out.println("### process Result:" + res);
            }
            */
            String tmp = "";
            for(ArrayList<String> res : resultArr) {
                tmp += " " + CommonUtil.removeTex(res.get(3));
            }

            //System.out.println("#TMP words:"+tmp);
            rx1 = WordFreqUtil.getWordCountsMap(tmp);
            //System.out.println("### Result before sort:" + rx1.toString());
            rx2 = MapUtil.sortByValue(rx1);
            //System.out.println("### Result after sort:" + rx2.toString());

            //Map<String, Integer> rx3 = new TreeMap(Collections.reverseOrder());
            //rx3.putAll(rx1);
            //System.out.println("### Result:" + rx2.toString());


            req = new HashMap<String, Object>();
            req.put("faq_id",ma.get("faq_id"));
            req.put("words",rx2.toString());
            //uptstat = testMapper.uptFaqListById(req);


            System.out.println("#uptstat words:"+rx2.toString());
            //System.out.println("# "+cnt+" 'th uptstat result:"+uptstat);
            cnt++;
            //System.out.println("#Result:"+str);
        }
        //System.out.println("#Result:"+result);
    }

    @Test
    public void updateVocListById() {
        Map<String, Object> req = new HashMap<String, Object>();
        req.put("voc_id",1);
        req.put("words","test");
        //int result = testMapper.uptVocListById(req);
        //System.out.println("#result:"+result);
    }

    @Test
    @Rollback(false)
    public void test_insYcDatas() {
        Map<String, Object> req = new HashMap();
        req.put("type", "yj");
        req.put("filecnt", "01");
        req.put("title", "test");
        req.put("words", "[]");
        //int rt = testMapper.insYcDatas(req);
    }

    @Test
    public void test_getYcDatas1st() {
        List<Map<String, Object>> result = testMapper.getYcDatas1st();
        for(Map<String, Object> im : result) {
            System.out.println("#lineMap:" + im.toString());
        }
        System.out.println("#Result.size:"+result.size());
    }

    @Test
    public void getSearchKeywordAndCount() {
        Map<String, Object> req = new HashMap();
        req.put("mtype","METASWHEN");
        List<Map<String, Object>> result = testMapper.getSearchKeywordAndCount(req);
        System.out.println("#RES.size:"+result.size());

        for(int i=0; i<5 ;i++) {
            System.out.println("#RES.item:"+result.get(i).toString());
        }
    }
}
