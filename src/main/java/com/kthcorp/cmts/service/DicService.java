package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.JsonUtil;
import com.kthcorp.cmts.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DicService implements DicServiceImpl {
    static Logger logger = LoggerFactory.getLogger(DicService.class);

    @Value("${property.serverid}")
    private String serverid;

    @Autowired
    private DicFilterWordsMapper dicFilterWordsMapper;
    @Autowired
    private DicFilterWordsHistMapper dicFilterWordsHistMapper;

    @Autowired
    private DicNotuseWordsMapper dicNotuseWordsMapper;
    @Autowired
    private DicNotuseWordsHistMapper dicNotuseWordsHistMapper;

    @Autowired
    private DicAddWordsMapper dicAddWordsMapper;
    @Autowired
    private DicAddWordsHistMapper dicAddWordsHistMapper;

    @Autowired
    private DicChangeWordsMapper dicChangeWordsMapper;
    @Autowired
    private DicChangeWordsHistMapper dicChangeWordsHistMapper;
    @Autowired
    private MetaKeywordMappingMapper metaKeywordMappingMapper;
    @Autowired
    private DicKeywordsMapper dicKeywordsMapper;

    /** 수집 시 제외 컨텐츠 필터링 문자열 처리 **/

    @Override
    public List<DicFilterWords> getDicFilterWords() {
        List<DicFilterWords> result = dicFilterWordsMapper.getDicFilterWords();
        return result;
    }

    // 제외어 추가
    @Override
    @Transactional
    public int insDicFilterWords(DicFilterWords req) {
        int result = -2;
        List<DicFilterWords> filterWords = this.getDicFilterWords();
        boolean isExist = false;
        for (DicFilterWords fw : filterWords) {
            if(fw != null && fw.getWord() != null && req.getWord() != null) {
                if(fw.getWord().equals(req.getWord())) {
                    isExist = true;
                    req.setIdx(fw.getIdx());
                    if(req != null && req.getRegid() == null) req.setRegid(serverid);
                    int rtupt = dicFilterWordsMapper.uptDicFilterWords(req);
                    result = 0;
                    break;
                }
            }
        }
        if (!isExist) {
            if(req != null && req.getRegid() == null) req.setRegid(serverid);
            result = dicFilterWordsMapper.insDicFilterWords(req);
        }
        return result;
    }

    // 제외어 사전 이력 조회
    @Override
    public List<DicFilterWordsHist> getDicFilterWordsHist() {
        List<DicFilterWordsHist> result = dicFilterWordsHistMapper.getDicFilterWordsHist();
        return result;
    }

    // 제외어 사전 이력 등록
    @Override
    @Transactional
    public int insDicFilterWordsHist(DicFilterWordsHist req) {
        int result = -1;
        if (req != null) {
            if(req != null && req.getRegid() == null) req.setRegid(serverid);
            req.setType("use");
            result = dicFilterWordsHistMapper.insDicFilterWordsHist(req);
        }
        return result;
    }

    // 단건 제외어 필터 적용
    @Override
    public String filterByDicFilterWords(String req, int req_id) throws Exception {
        String result = "";
        //System.out.println("#filterByDicFilterWords by req:"+req+"/reg_id:"+req_id);

        List<DicFilterWords> dic = this.getDicFilterWords();

        //System.out.println("#filterByDicFilterWords DIC:"+dic.toString());

        boolean isFilter = false;
        for(DicFilterWords dfw : dic) {
            if(dfw.getWord() != null) {
                if(req.equals(dfw.getWord())) {
                    DicFilterWordsHist reqhist = new DicFilterWordsHist();
                    reqhist.setAction_id(req_id);
                    reqhist.setAction("Do filtering by dic_filter_words:"+dfw.getWord());
                    reqhist.setDic_idx(dfw.getIdx());
                    int rt = this.insDicFilterWordsHist(reqhist);
                    logger.info("#MLOG req_id:"+req_id+". Do filtering by dic_filter_words:"+dfw.getWord());

                    isFilter = true;
                    break;
                }
            }
        }
        if(!isFilter) {
            result = req;
        }

        return result;
    }

    // 리스트 제외어 필터 적용
    @Override
    public List<String> filterListByDicFilterWords(List<String> req, int req_id) {
        List<String> result = new ArrayList<String>();

        for(String s : req) {
            String res = "";
            try {
                res = this.filterByDicFilterWords(s, req_id);
            } catch (Exception e) { }

            if(!"".equals(result)) {
                result.add(res);
            }
        }

        return result;
    }


    /** 정제 시 키워드 중 불용어 처리 **/
    // 불용어 사전 조회
    @Override
    public List<DicNotuseWords> getDicNotuseWords() {
        List<DicNotuseWords> result = dicNotuseWordsMapper.getDicNotuseWords();
        return result;
    }

    // 불용어 사전에 등록
    @Override
    @Transactional
    public int insDicNotuseWords(DicNotuseWords req) {
        int result = -2;
        List<DicNotuseWords> NotuseWords = this.getDicNotuseWords();
        boolean isExist = false;
        for (DicNotuseWords fw : NotuseWords) {
            if(fw != null && fw.getWord() != null && req.getWord() != null) {
                if(fw.getWord().equals(req.getWord())) {
                    isExist = true;
                    req.setIdx(fw.getIdx());
                    if(req != null && req.getRegid() == null) req.setRegid(serverid);
                    int rtupt = dicNotuseWordsMapper.uptDicNotuseWords(req);
                    result = 0;
                    break;
                }
            }
        }
        if (!isExist) {
            if(req != null && req.getRegid() == null) req.setRegid(serverid);
            result = dicNotuseWordsMapper.insDicNotuseWords(req);
        }
        return result;
    }

    // 불용어 사전 이력 리스트 조회
    @Override
    public List<DicNotuseWordsHist> getDicNotuseWordsHist() {
        List<DicNotuseWordsHist> result = dicNotuseWordsHistMapper.getDicNotuseWordsHist();
        return result;
    }

    // 불용어 사전 이력 등록
    @Override
    @Transactional
    public int insDicNotuseWordsHist(DicNotuseWordsHist req) {
        int result = -1;

        // 1분 이내 필터링 이력이 있으면 이력 추가 저장하지 않음
        List<DicNotuseWordsHist> histList = dicNotuseWordsHistMapper.getDicNotuseWordsHistByReq(req);
        int histSize = 0;
        if (histList != null) histSize = histList.size();

        if (histSize == 0 && req != null) {
            req.setType("use");
            result = dicNotuseWordsHistMapper.insDicNotuseWordsHist(req);
        }
        return result;
    }

    // 단건 불용어 사전에 의해 제외 처리
    @Override
    public String filterByDicNotuseWords(List<DicNotuseWords> dic, String req, int req_id) throws Exception {
        String result = "";

        //List<DicNotuseWords> dic = this.getDicNotuseWords();

        boolean isNotuse = false;
        for(DicNotuseWords dfw : dic) {
            if(dfw.getWord() != null) {
                if(req.equals(dfw.getWord())) {
                    /*
                    DicNotuseWordsHist reqhist = new DicNotuseWordsHist();
                    reqhist.setAction_id(req_id);
                    reqhist.setAction("Do Notuseing by dic_Notuse_words:"+dfw.getWord());
                    reqhist.setDic_idx(dfw.getIdx());
                    reqhist.setRegid(serverid);
                    */
                    // 데이터가 매우 커서 제외 2018.03.16
                    //int rt = this.insDicNotuseWordsHist(reqhist);
                    //logger.info("#MLOG req_id:"+req_id+". Do Notuseing by dic_Notuse_words:"+dfw.getWord());

                    isNotuse = true;
                    break;
                }
            }
        }
        if(!isNotuse) {
            result = req;
        }

        return result;
    }

    // 리스트 불용어 사전에 의해 제외 처리
    @Override
    public List<String> filterListByDicNotuseWords(List<String> req, int req_id) {
        List<String> result = new ArrayList<String>();

        List<DicNotuseWords> dic = this.getDicNotuseWords();
        for(String s : req) {
            String res = "";
            try {
                res = this.filterByDicNotuseWords(dic, s, req_id);
            } catch (Exception e) { }

            if(!"".equals(result)) {
                result.add(res);
            }
        }

        return result;
    }


    /** 정제 시 키워드 중 대체어 처리 **/
    // 대체어 사전 조회
    @Override
    public List<DicChangeWords> getDicChangeWords() {
        List<DicChangeWords> result = dicChangeWordsMapper.getDicChangeWords();
        return result;
    }

    // 대체어 사전에 등록
    @Override
    @Transactional
    public int insDicChangeWords(DicChangeWords req) {
        int result = -2;
        List<DicChangeWords> ChangeWords = this.getDicChangeWords();
        boolean isExist = false;
        for (DicChangeWords fw : ChangeWords) {
            if(fw != null && fw.getWord() != null && req.getWord() != null) {
                if(fw.getWord().equals(req.getWord()) && fw.getWordto().equals(req.getWordto())) {
                    isExist = true;
                    req.setIdx(fw.getIdx());
                    if(req != null && req.getRegid() == null) req.setRegid(serverid);
                    int rtupt = dicChangeWordsMapper.uptDicChangeWords(req);
                    result = 0;
                    break;
                }
            }
        }
        if (!isExist) {
            if(req != null && req.getRegid() == null) req.setRegid(serverid);
            result = dicChangeWordsMapper.insDicChangeWords(req);
        }
        return result;
    }

    // 대체어 사전 이력 리스트 조회
    @Override
    public List<DicChangeWordsHist> getDicChangeWordsHist() {
        List<DicChangeWordsHist> result = dicChangeWordsHistMapper.getDicChangeWordsHist();
        return result;
    }

    // 대체어 사전 이력 등록
    @Override
    @Transactional
    public int insDicChangeWordsHist(DicChangeWordsHist req) {
        int result = -1;

        // 1분 이내 필터링 이력이 있으면 이력 추가 저장하지 않음
        List<DicChangeWordsHist> histList = dicChangeWordsHistMapper.getDicChangeWordsHistByReq(req);
        int histSize = 0;
        if (histList != null) histSize = histList.size();

        if (histSize == 0 && req != null) {
            req.setType("use");
            result = dicChangeWordsHistMapper.insDicChangeWordsHist(req);
        }
        return result;
    }

    // 단건 대체어 사전에 의해 제외 처리
    @Override
    public String filterByDicChangeWords(List<DicChangeWords> dic, String req, int req_id) throws Exception {
        String result = "";

        //List<DicChangeWords> dic = this.getDicChangeWords();

        for(DicChangeWords dfw : dic) {
            if(dfw.getWord() != null) {
                if(req.equals(dfw.getWord())) {
                    String wordTo = dfw.getWordto();
                    //System.out.println("#Do changeWords "+dfw.getWord()+" to "+wordTo);
                    /* 주석처리 2018.03.20
                    DicChangeWordsHist reqhist = new DicChangeWordsHist();
                    reqhist.setAction_id(req_id);
                    reqhist.setAction("Do Changeing by dic_change_words keyword:"+dfw.getWord()+" to "+wordTo);
                    reqhist.setDic_idx(dfw.getIdx());
                    reqhist.setRegid(serverid);

                    int rt = this.insDicChangeWordsHist(reqhist);
                    */
                    //logger.info("#MLOG req_id:"+req_id+". Do Changeing by dic_Change_words:"+dfw.getWord());

                    result = dfw.getWordto();
                    break;
                } else {
                    result = req;
                }
            }
        }

        return result;
    }

    // 리스트 대체어 사전에 의해 제외 처리
    @Override
    public List<String> filterListByDicChangeWords(List<String> req, int req_id) {
        List<String> result = new ArrayList<String>();

        List<DicChangeWords> dic = this.getDicChangeWords();
        for(String s : req) {
            String res = "";
            try {
                res = this.filterByDicChangeWords(dic, s, req_id);
            } catch (Exception e) { }

            if(!"".equals(result)) {
                result.add(res);
            }
        }

        return result;
    }

    // 1글자 제외 로직 반영
    @Override
    public List<String> filterListByLengthUnder2byte(List<String> req) {
        List<String> result = new ArrayList<String>();

        for(String s : req) {
            // _로 시작하면 2개품사 조합 중 첫째 단어가 공백인 경우 - 제외
            s = s.trim();
            s = s.replace("_","");

            // 한글자 제외
            if(!"".equals(s) && s.length() > 1) {
                result.add(s);
            }
        }

        return result;
    }

    /** 정제 시 키워드 중 추가어 처리 **/
    // 추가어 사전 조회
    @Override
    public List<DicAddWords> getDicAddWords() {
        List<DicAddWords> result = dicAddWordsMapper.getDicAddWords();
        return result;
    }

    // 추가어 사전에 등록
    @Override
    @Transactional
    public int insDicAddWords(DicAddWords req) {
        int result = -2;
        List<DicAddWords> AddWords = this.getDicAddWords();
        boolean isExist = false;
        for (DicAddWords fw : AddWords) {
            if(fw != null && fw.getWord() != null && req.getWord() != null) {
                if(fw.getWord().equals(req.getWord())) {
                    isExist = true;
                    req.setIdx(fw.getIdx());
                    if(req != null && req.getRegid() == null) req.setRegid(serverid);
                    int rtupt = dicAddWordsMapper.uptDicAddWords(req);
                    result = 0;
                    break;
                }
            }
        }
        if (!isExist) {
            if(req != null && req.getRegid() == null) req.setRegid(serverid);
            result = dicAddWordsMapper.insDicAddWords(req);
        }
        return result;
    }

    // 추가어 사전 이력 리스트 조회
    @Override
    public List<DicAddWordsHist> getDicAddWordsHist() {
        List<DicAddWordsHist> result = dicAddWordsHistMapper.getDicAddWordsHist();
        return result;
    }

    // 추가어 사전 이력 등록
    @Override
    @Transactional
    public int insDicAddWordsHist(DicAddWordsHist req) {
        int result = -1;

        // 1분 이내 필터링 이력이 있으면 이력 추가 저장하지 않음
        List<DicAddWordsHist> histList = dicAddWordsHistMapper.getDicAddWordsHistByReq(req);
        int histSize = 0;
        if (histList != null) histSize = histList.size();

        if (histSize == 0 && req != null) {
            req.setType("use");
            result = dicAddWordsHistMapper.insDicAddWordsHist(req);
        }
        return result;
    }

    // 단건 추가어 사전에 의해 제외 처리
    @Override
    public Double filterByDicAddWords(List<DicAddWords> dic, String req, Double reqFreq, int req_id) throws Exception {
        Double result = 0.0;

        //List<DicAddWords> dic = this.getDicAddWords();

        for(DicAddWords dfw : dic) {
            if(dfw.getWord() != null) {
                if(req.equals(dfw.getWord())) {
                    Double freq = dfw.getFreq();
                    System.out.println("#Do Adding by dic_Add_words keyword:"+dfw.getWord()
                            +" added FREQ:"+freq+ " ( "+reqFreq+"=>"+reqFreq*freq+" )");

                    /* 주석처리 2018.03.20
                    DicAddWordsHist reqhist = new DicAddWordsHist();
                    reqhist.setAction_id(req_id);
                    reqhist.setAction("Do Adding by dic_Add_words keyword:"+dfw.getWord()
                            +" added FREQ:"+freq+ " ( "+reqFreq+"=>"+reqFreq*freq+" )");
                    reqhist.setDic_idx(dfw.getIdx());
                    reqhist.setRegid(serverid);

                    int rt = this.insDicAddWordsHist(reqhist);
                    */
                    //logger.info("#MLOG req_id:"+req_id+". Do Adding by dic_Add_words:"+dfw.getWord());

                    result = reqFreq * freq;
                    break;
                } else {
                    result = reqFreq * 1.0;
                }
            }
        }

        return result;
    }

    // 리스트 추가어 사전에 의해 Freq 계산 처리
    @Override
    public HashMap<String, Double> filterListByDicAddWords(Map<String, Double> reqMap, int req_id) {
        HashMap<String, Double> result = new HashMap<String, Double>();
        List<DicAddWords> dic = this.getDicAddWords();

        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            Double tmpFreq = (Double) me.getValue();
            try {
                tmpFreq = filterByDicAddWords(dic, me.getKey().toString(), (Double) me.getValue(), req_id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String tmpKey = me.getKey().toString();
            tmpKey = tmpKey.trim();
            if (!"".equals(tmpKey)) {
                result.put(me.getKey().toString(), tmpFreq);
            } else {
                logger.debug("#ELOG.filterListByDicAddWords skip word is empty and freq:"+tmpFreq);
            }
        }

        return result;
    }

    @Override
    public List<String> getMetaTypes() {
        return metaKeywordMappingMapper.getMetaTypes();
    }

    @Override
    public List<MetaKeywordMapping> getMetaKeywordMappingListAll() {
        return metaKeywordMappingMapper.getMetaKeywordMappingListAll();
    }

    @Override
    public List<String> getTagTypes() {
        return dicKeywordsMapper.getKeywordTypes();
    }

    @Override
    public List<DicKeywords> getDicKeywordsListAll() {
        return dicKeywordsMapper.getDicKeywordsListAll();
    }


    // 키워드-매핑 사전
    // 키워드-매핑 사전 조회
    @Override
    public List<DicKeywords> getDicKeywords(DicKeywords req) {
        List<DicKeywords> result = dicKeywordsMapper.getDicKeywords(req);
        return result;
    }

    // 키워드-매핑 사전 등록
    @Override
    @Transactional
    public int insDicKeywords(DicKeywords req) {
        int result = -2;
        if (req != null && req.getKeyword() != null && !"".equals(req.getKeyword().trim())) {
            List<DicKeywords> dicKeywords = this.getDicKeywords(req);
            boolean isExist = false;
            for (DicKeywords fw : dicKeywords) {
                if (fw != null && fw.getKeyword() != null && req.getKeyword() != null) {

                    if (fw.getKeyword().equals(req.getKeyword())) {
                        System.out.println("#insDicKeywords exist then update keyword:" + req.getKeyword().toString()
                                + " to toword:" + req.getToword());
                        isExist = true;
                        req.setIdx(fw.getIdx());
                        if (req != null && req.getRegid() == null) req.setRegid(serverid);
                        if (req != null && req.getRatio() == null) req.setRatio(0.0);
                        req.setOldword(req.getKeyword());
                        req.setKeyword(req.getToword());

                        try {
                            int rtupt = dicKeywordsMapper.uptDicKeywords(req);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // update 시 중복일 경우 삭제 후 입력
                            System.out.println("#ELOG.dicService:delKeywords:: req:"+req.toString());
                            int rtdel = dicKeywordsMapper.delDicKeywords(req);

                            req.setOldword(req.getKeyword());
                            req.setOldword(req.getToword());
                            rtdel = dicKeywordsMapper.delDicKeywords(req);

                            int rtins = dicKeywordsMapper.insDicKeywords(req);
                        }
                        result = 0;
                        break;
                    }
                }
            }
            if (!isExist) {
                if (req != null && req.getRegid() == null) req.setRegid(serverid);
                if (req != null && req.getRatio() == null) req.setRatio(0.0);
                result = dicKeywordsMapper.insDicKeywords(req);
            }
        }
        return result;
    }

    // 미분류 키워드 사전 등록
    @Override
    @Transactional
    public int insDicNotMapKeywords(DicKeywords req) {
        int result = -2;
        int cnt = dicKeywordsMapper.countDicKeywords(req);
        if (cnt < 1) {
            if(req != null && req.getRegid() == null) req.setRegid(serverid);
            result = dicKeywordsMapper.insDicNotMapKeywords(req);
        }
        return result;
    }

    @Override
    public int insNotMapKeywords(JsonArray reqArr) {
        List<String> reqList = JsonUtil.convertJsonArrayToList(reqArr);
        int rt = 0;
        for (String s : reqList) {
            DicKeywords req = new DicKeywords();
            req.setKeyword(s);
            rt = this.insDicNotMapKeywords(req);
        }
        return rt;
    }

    @Override
    public int countItems(String type, String keyword) {
        int count = 0;

        switch(type) {
            case "NOTUSE" :
                    DicNotuseWords reqdnw = new DicNotuseWords();
                    reqdnw.setWord(keyword);
                    count = dicNotuseWordsMapper.countItems(reqdnw);
                break;

            case "CHANGE" :
                    DicChangeWords reqdcw = new DicChangeWords();
                    reqdcw.setWord(keyword);
                    count = dicChangeWordsMapper.countItems(reqdcw);
                break;

            case "ADD" :
                    DicAddWords reqdaw = new DicAddWords();
                    reqdaw.setWord(keyword);
                    count = dicAddWordsMapper.countItems(reqdaw);
                    break;

            case "FILTER" :
                    DicFilterWords reqdfw = new DicFilterWords();
                    reqdfw.setWord(keyword);
                    count = dicFilterWordsMapper.countItems(reqdfw);
                break;

            default :
                    DicKeywords req = new DicKeywords();
                    req.setType(type);
                    req.setKeyword(keyword);

                    count = dicKeywordsMapper.countItems(req);
                break;
        }

        return count;
    }

    @Override
    public JsonArray getDicKeywordsByType(String type, String keyword, int pageSize, int pageno) {
        if (pageSize < 1 && pageSize > 200) pageSize = 200;

        JsonArray result = new JsonArray();

        switch(type) {
            case "NOTUSE" :
                DicNotuseWords reqnotuse = new DicNotuseWords();
                reqnotuse.setPageSize(pageSize);
                reqnotuse.setPageNo(pageno);
                reqnotuse.setWord(keyword);
                List<DicNotuseWords> resnotuse = dicNotuseWordsMapper.getDicNotuseWordsPaging(reqnotuse);
                if (resnotuse != null) {
                    for (DicNotuseWords dic : resnotuse) {
                        result.add(dic.getWord());
                    }
                }
                break;

            case "CHANGE" :
                DicChangeWords reqchange = new DicChangeWords();
                reqchange.setPageSize(pageSize);
                reqchange.setPageNo(pageno);
                reqchange.setWord(keyword);
                List<DicChangeWords> reschange = dicChangeWordsMapper.getDicChangeWordsPaging(reqchange);
                if (reschange != null) {
                    for (DicChangeWords dic : reschange) {
                        result.add(dic.getWord()+"|"+dic.getWordto());
                    }
                }
                break;

            case "ADD" :
                DicAddWords reqadd = new DicAddWords();
                reqadd.setPageSize(pageSize);
                reqadd.setPageNo(pageno);
                reqadd.setWord(keyword);
                List<DicAddWords> resadd = dicAddWordsMapper.getDicAddWordsPaging(reqadd);
                if (resadd != null) {
                    for (DicAddWords dic : resadd) {
                        result.add(dic.getWord());
                    }
                }
                break;

            case "FILTER" :
                DicFilterWords reqfilter = new DicFilterWords();
                reqfilter.setPageSize(pageSize);
                reqfilter.setPageNo(pageno);
                reqfilter.setWord(keyword);
                List<DicFilterWords> resfilter = dicFilterWordsMapper.getDicFilterWordsPaging(reqfilter);
                if (resfilter != null) {
                    for (DicFilterWords dic : resfilter) {
                        result.add(dic.getWord());
                    }
                }
                break;

            default :
                DicKeywords reqkey = new DicKeywords();
                reqkey.setPageSize(pageSize);
                reqkey.setPageNo(pageno);
                reqkey.setType(type);
                reqkey.setKeyword(keyword);

                List<DicKeywords> resKeywords = dicKeywordsMapper.getDicKeywordsPaging(reqkey);
                if (resKeywords != null) {
                    for (DicKeywords dic : resKeywords) {
                        result.add(dic.getKeyword());
                    }
                }
                break;
        }

        return result;
    }

    @Override
    public int modifyDicsByTypesFromArrayList(String items) {
        int rt = 0;
        if (!"".equals(items)) {
            items = items.trim();

            JsonParser parser = new JsonParser();
            JsonArray itemsArr = (JsonArray) parser.parse(items);
            for (JsonElement je : itemsArr) {
                JsonObject jo = (JsonObject) je;
                System.out.println("#dicActions::"+jo.toString());

                String dicType = (jo.get("target_type") != null ? jo.get("target_type").getAsString() : "");
                dicType = dicType.trim().toUpperCase();

                String dicAction = (jo.get("action") != null ? jo.get("action").getAsString() : "");
                String oldword = (jo.get("word") != null ? jo.get("word").getAsString() : "");
                String newword = (jo.get("target_word") != null ? jo.get("target_word").getAsString() : "");

                System.out.println("#dicAction::"+dicAction.toString());

                switch (dicAction) {
                    case "add" :
                        rt = this.addDicsByParams(dicType, oldword, newword, 0.0);
                        break;

                    case "mod" :
                        rt = this.delDicsByParams(dicType, oldword, newword, 0.0);
                        rt = this.addDicsByParams(dicType, oldword, newword, 0.0);
                        break;

                    case "del" :
                        rt = this.delDicsByParams(dicType, oldword, newword, 0.0);
                        break;
                }
            }
        }

        return rt;
    }

    private int addDicsByParams(String dicType, String oldword, String newword, Double freq) {
        int rt = 0;
        switch(dicType) {
            case "NOTUSE":
                DicNotuseWords req = new DicNotuseWords();
                req.setWord(newword);
                req.setOldword(oldword);
                req.setFreq(freq);
                rt = this.insDicNotuseWords(req);
                break;

            case "CHANGE":
                DicChangeWords reqc = new DicChangeWords();

                String[] newwords = newword.trim().split("\\|");
                if (newwords != null && newwords.length > 1) {
                    String oldword1 = newwords[0];
                    String newword1 = newwords[1];
                    reqc.setWord(oldword1);
                    reqc.setOldword(oldword1);
                    reqc.setWordto(newword1);

                    rt = this.insDicChangeWords(reqc);
                }
                break;

            case "ADD":
                System.out.println("#add run::"+newword);

                DicAddWords reqa = new DicAddWords();
                reqa.setWord(newword);
                reqa.setOldword(oldword);
                reqa.setFreq(freq);
                rt = this.insDicAddWords(reqa);
                break;

            case "FILTER":
                DicFilterWords reqf = new DicFilterWords();
                reqf.setWord(newword);
                reqf.setOldword(oldword);
                reqf.setFreq(freq);
                rt = this.insDicFilterWords(reqf);
                break;
            case "WHEN": case "WHERE": case "WHO": case "WHAT": case "EMOTION":
                DicKeywords reqk = new DicKeywords();
                reqk.setKeyword(newword);
                reqk.setOldword(oldword);
                reqk.setRatio(freq);
                reqk.setType(dicType);
                rt = this.insDicKeywords(reqk);
                break;
        }
        return rt;
    }

    private int uptDicsByParams_(String dicType, String oldword, String newword, Double freq) {
        int rt = 0;
        switch(dicType) {
            case "NOTUSE":
                DicNotuseWords req = new DicNotuseWords();
                req.setWord(newword);
                req.setOldword(oldword);
                req.setFreq(freq);
                rt = this.uptDicNotuseWordsByWord(req);
                break;

            case "CHANGE":
                DicChangeWords reqc = new DicChangeWords();

                String[] newwords = newword.trim().split("\\|");
                String[] oldwords = oldword.trim().split("\\|");
                if (newwords != null && oldwords != null) {
                    String oldword1 = oldwords[0];
                    String words1 = newwords[0];
                    String newword1 = newwords[1];
                    reqc.setWord(words1);
                    reqc.setOldword(oldword1);
                    reqc.setWordto(newword1);

                    rt = this.uptDicChangeWordsByWord(reqc);
                }
                break;

            case "ADD":
                DicAddWords reqa = new DicAddWords();
                reqa.setWord(newword);
                reqa.setOldword(oldword);
                reqa.setFreq(freq);
                rt = this.uptDicAddWordsByWord(reqa);
                break;

            case "FILTER":
                DicFilterWords reqf = new DicFilterWords();
                reqf.setWord(newword);
                reqf.setOldword(oldword);
                reqf.setFreq(freq);
                rt = this.uptDicFilterWordsByWord(reqf);
                break;
            case "WHEN": case "WHERE": case "WHO": case "WHAT": case "EMOTION":
                DicKeywords reqk = new DicKeywords();
                reqk.setKeyword(newword);
                reqk.setOldword(oldword);
                reqk.setRatio(freq);
                reqk.setType(dicType);
                rt = this.uptDicKeywords(reqk);
                break;
        }
        return rt;
    }

    private int delDicsByParams(String dicType, String oldword, String newword, Double freq) {
        int rt = 0;
        switch(dicType) {
            case "NOTUSE":
                DicNotuseWords req = new DicNotuseWords();
                req.setWord(newword);
                req.setOldword(oldword);
                req.setFreq(freq);
                rt = this.delDicNotuseWordsByWord(req);
                break;

            case "CHANGE":
                DicChangeWords reqc = new DicChangeWords();
                String[] oldwords = oldword.trim().split("\\|");
                if (oldwords != null && oldwords.length > 1) {
                    String oldword1 = oldwords[0];
                    String newword1 = oldwords[1];
                    reqc.setWord(oldword1);
                    reqc.setOldword(oldword1);
                    reqc.setWordto(newword1);
                    rt = this.delDicChangeWordsByWord(reqc);
                }
                break;

            case "ADD":
                DicAddWords reqa = new DicAddWords();
                reqa.setWord(newword);
                reqa.setOldword(oldword);
                reqa.setFreq(freq);
                rt = this.delDicAddWordsByWord(reqa);
                break;

            case "FILTER":
                DicFilterWords reqf = new DicFilterWords();
                reqf.setWord(newword);
                reqf.setOldword(oldword);
                reqf.setFreq(freq);
                rt = this.delDicFilterWordsByWord(reqf);
                break;
            case "WHEN": case "WHERE": case "WHO": case "WHAT": case "EMOTION":
                DicKeywords reqk = new DicKeywords();
                reqk.setKeyword(newword);
                reqk.setOldword(oldword);
                reqk.setRatio(freq);
                reqk.setType(dicType);
                rt = this.delDicKeywords(reqk);
                break;
        }
        return rt;
    }

    public int uptDicNotuseWordsByWord(DicNotuseWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicNotuseWordsMapper.uptDicNotuseWordsByWord(req);
    }

    public int delDicNotuseWordsByWord(DicNotuseWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicNotuseWordsMapper.delDicNotuseWordsByWord(req);
    }

    public int uptDicChangeWordsByWord(DicChangeWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicChangeWordsMapper.uptDicChangeWordsByWord(req);

    }
    public int delDicChangeWordsByWord(DicChangeWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicChangeWordsMapper.delDicChangeWordsByWord(req);
    }
    public int uptDicAddWordsByWord(DicAddWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicAddWordsMapper.uptDicAddWordsByWord(req);

    }
    public int delDicAddWordsByWord(DicAddWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicAddWordsMapper.delDicAddWordsByWord(req);
    }
    public int uptDicFilterWordsByWord(DicFilterWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicFilterWordsMapper.uptDicFilterWordsByWord(req);

    }
    public int delDicFilterWordsByWord(DicFilterWords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicFilterWordsMapper.delDicFilterWordsByWord(req);
    }
    public int uptDicKeywords(DicKeywords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicKeywordsMapper.uptDicKeywords(req);

    }
    public int delDicKeywords(DicKeywords req) {
        if(req != null && req.getRegid() == null) req.setRegid(serverid);
        return dicKeywordsMapper.delDicKeywords(req);
    }

    @Override
    public List<DicKeywords> getRankWordsByGenreAndType(DicKeywords req) {
        return dicKeywordsMapper.getRankWordsByGenreAndType(req);
    }
    @Override
    public List<String> getKeywordTypes() {
        return dicKeywordsMapper.getKeywordTypes();
    }

    static List<DicSubgenre> dicSubgenre = null;
    static List<DicSubgenre> dicSubgenreOrig = null;
    static List<DicSubgenre> dicSubgenreFilter = null;

    @Override
    public Set getMixedGenreArrayFromGenre(String genre, String mtype) {
        genre = genre.trim();
        if (dicSubgenre == null) dicSubgenre = dicKeywordsMapper.getDicSubgenreGenres(mtype);

        Set result = new HashSet();
        if(genre.trim().contains(" ")) {
            String[] origGenres = genre.split(" ");
            Set<String> noDupMixedGenres = MapUtil.getNoDupSetFromStringArray(origGenres);

            for(DicSubgenre ds : dicSubgenre) {
                for(String origGenre : noDupMixedGenres) {
                    String ds1 = (ds != null && ds.getGenre() != null) ? ds.getGenre() : "";

                    if(ds1.equals(origGenre)) {
                        result.add(ds.getMeta());
                    }
                }
            }
        } else if (!"".equals(genre.trim())) {
            String origGenre = genre;
            for(DicSubgenre ds : dicSubgenre) {
                String ds1 = (ds != null && ds.getGenre() != null) ? ds.getGenre() : "";

                if(ds1.equals(origGenre)) {
                    result.add(ds.getMeta());
                }
            }
        }
        return result;
    }



    @Override
    public List<String> getMixedGenreArrayFromFilter(String genre, String mtype) {
        genre = genre.trim();
        if (dicSubgenreFilter == null) dicSubgenreFilter = dicKeywordsMapper.getDicSubgenreGenres(mtype);

        List<String> result = new ArrayList();
        if(genre.trim().contains(" ")) {
            String[] origGenres = genre.split(" ");
            Set<String> noDupMixedGenres = MapUtil.getNoDupSetFromStringArray(origGenres);

            for(DicSubgenre ds : dicSubgenreFilter) {
                for(String origGenre : noDupMixedGenres) {
                    String ds1 = (ds != null && ds.getGenre() != null) ? ds.getGenre() : "";

                    if(ds1.equals(origGenre)) {
                        result.add(ds.getMeta());
                    }
                }
            }
        } else if (!"".equals(genre.trim())) {
            String origGenre = genre;
            for(DicSubgenre ds : dicSubgenreFilter) {
                String ds1 = (ds != null && ds.getGenre() != null) ? ds.getGenre() : "";

                if(ds1.equals(origGenre)) {
                    result.add(ds.getMeta());
                }
            }
        }
        return result;
    }

    @Override
    public Set getMixedNationGenreArrayFromGenre(String genre, String origin, String mtype) {
        genre = genre.trim();
        if (dicSubgenreOrig == null) dicSubgenreOrig = dicKeywordsMapper.getDicSubgenreGenres(mtype);

        Set result = new HashSet();
        List<String> origGenres = new ArrayList();

        if(genre.trim().contains(" ")) {
            //System.out.println("#dicSubgenreOrig::"+dicSubgenreOrig.toString());
            String[] origGenres0 = genre.split(" ");
            if (origGenres0 != null) {
                for(String og : origGenres0) {
                    if(!"".equals(og.trim())) {
                        origGenres.add(og);
                    }
                }
            }
        } else if (!"".equals(genre.trim())) {
            origGenres.add(genre.trim());
        }

        Set<String> noDupMixedGenres = MapUtil.getNoDupSetFromStringArrayAddTag(origGenres, origin);
        //System.out.println("#noDupMixedGenres::"+noDupMixedGenres.toString());

        if(noDupMixedGenres != null) {
            for (DicSubgenre ds : dicSubgenreOrig) {
                for (String origGenre : noDupMixedGenres) {
                    String ds1 = (ds != null && ds.getGenre() != null) ? ds.getGenre() : "";

                    //System.out.println("##compare  origGenre:"+origGenre+"   vs  ds1:"+ds1);

                    if (ds1.equals(origGenre)) {
                        result.add(ds.getMeta());
                        break;
                    }
                }
            }
        }
        return result;
    }
}
