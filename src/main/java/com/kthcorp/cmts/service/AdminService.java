package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminService implements AdminServiceImpl {
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;
    @Autowired
    private SchedTargetMappingHistMapper schedTargetMappingHistMapper;
    @Autowired
    private ConfTargetOrigMapper confTargetOrigMapper;
    @Autowired
    private ConfTargetMapper confTargetMapper;
    @Autowired
    private ConfPsMappingMapper confPsMappingMapper;
    @Autowired
    private ConfPresetMapper confPresetMapper;
    @Autowired
    private MovieCine21Mapper movieCine21Mapper;
    @Autowired
    private ManualJobHistMapper manualJobHistMapper;

	@Autowired
	private DicService dicService;
    @Autowired
    private DicKeywordsMapper dicKeywordsMapper;
	
    @Value("${spring.static.resource.location}")
    private String UPLOAD_DIR;
    
    @Override
    public List<Items> getItemsList(Items req) {
        List<Items> result = itemsMapper.getPagedItems(req);
        System.out.println("#AdminService.getItemsList result:"+result.toString());

        return result;
    }

    @Override
    public Integer countItems(Items req) {
        return itemsMapper.countItems(req);
    }

    @Override
    public Items getItemsByIdx(Items req) {
        Items result = itemsMapper.getItemsByIdx(req);
        System.out.println("#AdminService.getItemsByIdx result:"+result.toString());

        return result;
    }

    @Override
    public List<SchedTrigger> getSchedTriggerListByItemIdx(int idx) {
        List<SchedTrigger> result = schedTriggerMapper.getSchedTriggerListByItemIdx(idx);
        return result;
    }

    @Override
    public List<SchedTargetMappingHist> getSchedTriggerListBySc_id(SchedTargetMappingHist req) {
        List<SchedTargetMappingHist> result = null;
        if (req != null && req.getType() != null) {
            switch(req.getType()) {
                case "C" :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListForLast(req);
                    break;
                // 수집이 아닌 경우 history는 tg_id = 0인 상태이므로 쿼리를 달리한다.
                default :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListForLast2(req);
                    break;
            }
        }
        return result;
    }

    @Override
    public List<SchedTargetMappingHist> getSchedTriggerHistListAll(SchedTargetMappingHist req) {
        List<SchedTargetMappingHist> result = null;
        if (req != null && req.getType() != null) {
            switch(req.getType()) {
                case "C" :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListAll(req);
                    break;
                //
                default :
                    result = schedTargetMappingHistMapper.getSchedTargetMappingHistListAll(req);
                    break;
            }
        }
        return result;
    }

    @Override
    public List<ConfTargetOrig> getConfTargetOrigList(ConfTargetOrig req) {
        return confTargetOrigMapper.getTargetListActivePage(req);
    }

    @Override
    public List<ConfTarget> getConfTargetList(ConfTarget req) {
        return confTargetMapper.getTargetListActivePage(req);
    }

    @Override
    public int uptSchedTriggerRetry(SchedTrigger req) { return schedTriggerMapper.uptSchedTriggerRetry(req); }

    @Override
    public int uptConfTarget(ConfTarget req) {
        return confTargetMapper.uptConfTarget(req);
    }

    @Override
    public int delConfTarget(ConfTarget req) {
        /*
        SchedTargetMapping reqStm = new SchedTargetMapping();
        reqStm.setTg_id(req.getTg_id());
        int resStm = schedTriggerMapper.delSchedTargetMapping(reqStm);

        ConfPsMapping reqCsm = new ConfPsMapping();
        reqCsm.setTg_id(req.getTg_id());
        int resCsm = confPsMappingMapper.delConfPsMapping(reqCsm);

        int res = confTargetMapper.delConfTarget(req);
        */
        req.setStat("D");
        int res = confTargetMapper.uptTargetStat(req);
        return res;
    }

    @Override
    public int uptConfPreset(ConfPreset req) {
        return confPresetMapper.uptConfPreset(req);
    }

    @Override
    public int delConfPreset(ConfPreset req) {
        ConfPsMapping reqCpm = new ConfPsMapping();
        reqCpm.setPs_id(req.getPs_id());
        int resCpm = confPsMappingMapper.delConfPsMappingByPsId(reqCpm);

        //int res = confPresetMapper.delConfPreset(req);
        return resCpm;
    }

    @Override
    public List<MovieCine21> getMovieCine21(MovieCine21 req) {
        return movieCine21Mapper.getMovieCine21(req);
    }
    @Override
    public int cntMovieCine21(MovieCine21 req) {
        return movieCine21Mapper.cntMovieCine21(req);
    }

    @Override
    public List<ManualChange> getManualJobHist(ManualChange req) {
        return manualJobHistMapper.getManualJobHistPaging(req);
    }
    @Override
    public int cntManualJobHist() {
        return manualJobHistMapper.cntManualJobHist();
    }

    //CSV 다운로드
    @Override
	//public String getDicKeywordsListDownload(String type) {
   	public String getDicKeywordsListDownload() {
		String strFileName = "";
		
		String[] types = {"when","where","what","who","character","emotion"}; 
		
		for(String type : types) {
	    	List<Map<String, Object>> reqItems = null;
	    	String resultStr = "";
	    	String lineFeed = System.getProperty("line.separator");
	    	String seperator = "\t";
	    	
	    	//1. 헤더
	    	resultStr = "Type" + seperator + "Keyword" + lineFeed;
			
	    	//2. 모든 태그 유형 로딩 + 3. 태그 하나씩 불러오기  이 둘을 합치기 - 모든 태그 받기 getDicKeywordsListAll
	        DicKeywords reqKeyword = new DicKeywords();
	        reqKeyword.setType(type);
	        reqKeyword.setOrderby("old");
	    	List<DicKeywords> dicKeywordList = dicKeywordsMapper.getDicKeywordsList(reqKeyword);
	    	
			//4. 문자열화
	        for(DicKeywords item : dicKeywordList) {
	        	String strKeyword = item.getKeyword();
	        	resultStr += item.getType() + seperator + strKeyword + lineFeed;
	        }
	    	
			
			//5. 파일형태로 표출
	    	String fileNameContent = "DIC_KEYWORDS_"+type.toUpperCase()+"_.csv";
	    	int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");
	    	
	    	String strSeparator = (UPLOAD_DIR.substring(UPLOAD_DIR.length()-1).equals(File.separator) ? "" : File.separator);
	    	strFileName = UPLOAD_DIR + strSeparator + fileNameContent;
		}
		return strFileName;
	}

}
