package com.kthcorp.cmts.service;

import com.google.gson.*;
import com.kthcorp.cmts.mapper.DicKeywordsMapper;
import com.kthcorp.cmts.mapper.RelKnowledgeMapper;
import com.kthcorp.cmts.model.DicKeywords;
import com.kthcorp.cmts.model.RelKnowledge;
import com.kthcorp.cmts.util.FileUtils;
import com.kthcorp.cmts.util.StringUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.httpclient.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RelKnowledgeService implements RelKnowledgeServiceImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RelKnowledgeMapper relKnowledgeMapper;
    @Autowired
    private DicKeywordsMapper dicKeywordsMapper;	//2019.11.20 CSV 파일 정보 저장

    @Value("${spring.static.resource.location}")
    private String UPLOAD_DIR;
    
    @Override
    public String getStr() {
        return "str";
    }
    
    //연관지식 일괄추가 전 삭제
    //from dicService.delDicKeywordsAllByType
	public int delRelKnowledgesByType(RelKnowledge relKnowledge) {
		//int intResult = relKnowledgeMapper.delRelKnowledgesByType(relKnowledge);
		
		String strRelKnowledgeType = relKnowledge.getRelKnowledgeType();
		int intResult = 0;
		
		if(strRelKnowledgeType.toLowerCase().equals("cook")) {
			intResult = relKnowledgeMapper.delRelKnowledgesCook(relKnowledge);
		}else if(strRelKnowledgeType.toLowerCase().equals("curr")) {
			intResult = relKnowledgeMapper.delRelKnowledgesCurr(relKnowledge);
		}else if(strRelKnowledgeType.toLowerCase().equals("docu")) {
			intResult = relKnowledgeMapper.delRelKnowledgesDocu(relKnowledge);
		}else if(strRelKnowledgeType.toLowerCase().equals("heal")) {
			intResult = relKnowledgeMapper.delRelKnowledgesHeal(relKnowledge);
		}else if(strRelKnowledgeType.toLowerCase().equals("hist")) {
			intResult = relKnowledgeMapper.delRelKnowledgesHist(relKnowledge);
		}else if(strRelKnowledgeType.toLowerCase().equals("tour")) {
			intResult = relKnowledgeMapper.delRelKnowledgesTour(relKnowledge);
		}
		
		return intResult;
	}

    //연관지식 일괄추가
	//from dicService.modifyDicsByTypesFromArrayList
	public int addRelKnowledgesByType(RelKnowledge relKnowledge) {
		String items = relKnowledge.getItems();
        int rt = 0;
        int cnt = 0;
        if (!"".equals(items)) {
            items = items.trim();

            JsonParser parser = new JsonParser();
            JsonArray itemsArr = (JsonArray) parser.parse(items);
            for (JsonElement je : itemsArr) {
                JsonObject jo = (JsonObject) je;
                System.out.println("#dicActions::"+jo.toString());
                
                RelKnowledge relKnowledgeItem = new RelKnowledge();
                rt++;
                
                //test
                cnt++;
                System.out.println("Processing : " + cnt + " / " + itemsArr.size());
                
                //공통
//				relKnowledgeItem.setRegweek(jo.get("regweek") != null ? jo.get("regweek").getAsString() : "");
//				relKnowledgeItem.setStat(jo.get("stat") != null ? jo.get("stat").getAsString() : "");
//				relKnowledgeItem.setProgram(jo.get("program") != null ? jo.get("program").getAsString() : "");
//				relKnowledgeItem.setTitle(jo.get("title") != null ? jo.get("title").getAsString() : "");
//				relKnowledgeItem.setPart(jo.get("part") != null ? jo.get("part").getAsString() : "");
//				relKnowledgeItem.setAssetid(jo.get("assetid") != null ? jo.get("assetid").getAsString() : "");
//				relKnowledgeItem.setType(jo.get("type") != null ? jo.get("type").getAsString() : "");

                //구분별
                if(relKnowledge.getRelKnowledgeType().equals("cook")) {
//                	relKnowledge.setIdx(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setRegweek(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setStat(jo.get("col1") != null ? jo.get("col1").getAsString() : "");
                	relKnowledge.setProgram("");
                	relKnowledge.setTitle(jo.get("col2") != null ? jo.get("col2").getAsString() : "");
                	relKnowledge.setPart(jo.get("col3") != null ? jo.get("col3").getAsString() : "");
                	relKnowledge.setAssetid(jo.get("col4") != null ? jo.get("col4").getAsString() : "");
                	relKnowledge.setType(jo.get("col5") != null ? jo.get("col5").getAsString() : "");
                	
                	relKnowledge.setCook_title_a(jo.get("col6") != null ? jo.get("col6").getAsString() : "");
                	relKnowledge.setCook_title_b(jo.get("col7") != null ? jo.get("col7").getAsString() : "");
                	relKnowledge.setCook_parts(jo.get("col8") != null ? jo.get("col8").getAsString() : "");
                	relKnowledge.setCook_parts_main(jo.get("col9") != null ? jo.get("col9").getAsString() : "");
                	relKnowledge.setCook_parts_sub(jo.get("col10") != null ? jo.get("col10").getAsString() : "");
                	relKnowledge.setCook_type_a(jo.get("col11") != null ? jo.get("col11").getAsString() : "");
                	relKnowledge.setCook_type_b(jo.get("col12") != null ? jo.get("col12").getAsString() : "");
                	relKnowledge.setCook_chef_a(jo.get("col13") != null ? jo.get("col13").getAsString() : "");
                	relKnowledge.setCook_chef_b(jo.get("col14") != null ? jo.get("col14").getAsString() : "");
                	relKnowledge.setCook_kor_area_a(jo.get("col15") != null ? jo.get("col15").getAsString() : "");
                	relKnowledge.setCook_kor_area_b(jo.get("col16") != null ? jo.get("col16").getAsString() : "");
                	relKnowledge.setCook_oth_area_a(jo.get("col17") != null ? jo.get("col17").getAsString() : "");
                	relKnowledge.setCook_oth_area_b(jo.get("col18") != null ? jo.get("col18").getAsString() : "");
                	relKnowledge.setCook_place_name(jo.get("col19") != null ? jo.get("col19").getAsString() : "");
                	relKnowledge.setCook_place_kor_area_a(jo.get("col20") != null ? jo.get("col20").getAsString() : "");
                	relKnowledge.setCook_place_kor_area_b(jo.get("col21") != null ? jo.get("col21").getAsString() : "");
                	relKnowledge.setCook_place_oth_area_a(jo.get("col22") != null ? jo.get("col22").getAsString() : "");
                	relKnowledge.setCook_place_oth_area_b(jo.get("col23") != null ? jo.get("col23").getAsString() : "");
                	relKnowledge.setCook_good_season(jo.get("col24") != null ? jo.get("col24").getAsString() : "");
                	relKnowledge.setCook_good_day(jo.get("col25") != null ? jo.get("col25").getAsString() : "");
                	relKnowledge.setCook_purpose(jo.get("col26") != null ? jo.get("col26").getAsString() : "");
                	relKnowledge.setCook_flavor(jo.get("col27") != null ? jo.get("col27").getAsString() : "");
                	relKnowledge.setCook_cooker(jo.get("col28") != null ? jo.get("col28").getAsString() : "");
                	relKnowledge.setCook_shoot_place(jo.get("col29") != null ? jo.get("col29").getAsString() : "");
                	relKnowledge.setCook_level_a(jo.get("col30") != null ? jo.get("col30").getAsString() : "");
                	relKnowledge.setCook_level_b(jo.get("col31") != null ? jo.get("col31").getAsString() : "");
                	relKnowledge.setCook_level_c(jo.get("col32") != null ? jo.get("col32").getAsString() : "");
                	relKnowledge.setCook_time(jo.get("col33") != null ? jo.get("col33").getAsString() : "");
                	relKnowledge.setCook_nutrient(jo.get("col34") != null ? jo.get("col34").getAsString() : "");
                	relKnowledge.setCook_ea(jo.get("col35") != null ? jo.get("col35").getAsString() : "");
                	relKnowledge.setCook_etc_a(jo.get("col36") != null ? jo.get("col36").getAsString() : "");
                	relKnowledge.setCook_etc_b(jo.get("col38") != null ? jo.get("col38").getAsString() : "");	 //원본에 기타특징과 비교 사이에 한칸 더 있음
                	
                	//필수 체크 - sum(len(program title part assetid))>0
                	if((relKnowledge.getProgram()+relKnowledge.getTitle()+relKnowledge.getPart()+relKnowledge.getAssetid()).equals("")){
                		System.out.println("Wrong data passed : " + je.toString());
                		continue;
                	}
                	
                    //DB Insert
                    //System.out.println("relKnowledge = " + relKnowledge.toString());
                	relKnowledgeMapper.addRelKnowledgesCook(relKnowledge);
                }else if(relKnowledge.getRelKnowledgeType().equals("curr")) {
//                	relKnowledge.setIdx(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setRegweek(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setStat(jo.get("col1") != null ? jo.get("col1").getAsString() : "");
                	relKnowledge.setProgram("");
                	relKnowledge.setTitle(jo.get("col2") != null ? jo.get("col2").getAsString() : "");
                	relKnowledge.setPart(jo.get("col3") != null ? jo.get("col3").getAsString() : "");
                	relKnowledge.setAssetid(jo.get("col4") != null ? jo.get("col4").getAsString() : "");
                	relKnowledge.setType("");
                	
                	relKnowledge.setCurr_subject(jo.get("col5") != null ? jo.get("col5").getAsString() : "");
                	relKnowledge.setCurr_person(jo.get("col6") != null ? jo.get("col6").getAsString() : "");
                	relKnowledge.setCurr_incident(jo.get("col7") != null ? jo.get("col7").getAsString() : "");
                	relKnowledge.setCurr_product(jo.get("col8") != null ? jo.get("col8").getAsString() : "");
                	relKnowledge.setCurr_process(jo.get("col9") != null ? jo.get("col9").getAsString() : "");
                	relKnowledge.setCurr_type(jo.get("col10") != null ? jo.get("col10").getAsString() : "");
                	relKnowledge.setCurr_mc(jo.get("col11") != null ? jo.get("col11").getAsString() : "");
                	relKnowledge.setCurr_actor(jo.get("col12") != null ? jo.get("col12").getAsString() : "");
                	relKnowledge.setCurr_keyword(jo.get("col13") != null ? jo.get("col13").getAsString() : "");
                	relKnowledge.setCur_etc(jo.get("col14") != null ? jo.get("col14").getAsString() : "");

                	//필수 체크 - sum(len(program title part assetid))>0
                	if((relKnowledge.getProgram()+relKnowledge.getTitle()+relKnowledge.getPart()+relKnowledge.getAssetid()).equals("")){
                		System.out.println("Wrong data passed : " +  je.toString());
                		continue;
                	}
                	
                    //DB Insert
                    //System.out.println("relKnowledge = " + relKnowledge.toString());
                	relKnowledgeMapper.addRelKnowledgesCurr(relKnowledge);
                }else if(relKnowledge.getRelKnowledgeType().equals("docu")) {
//                	relKnowledge.setIdx(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setRegweek(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setStat(jo.get("col1") != null ? jo.get("col1").getAsString() : "");
                	relKnowledge.setProgram("");
                	relKnowledge.setTitle(jo.get("col2") != null ? jo.get("col2").getAsString() : "");
                	relKnowledge.setPart(jo.get("col3") != null ? jo.get("col3").getAsString() : "");
                	relKnowledge.setAssetid(jo.get("col4") != null ? jo.get("col4").getAsString() : "");
                	relKnowledge.setType("");
                	
                	relKnowledge.setDocu_subject(jo.get("col5") != null ? jo.get("col5").getAsString() : "");
                	relKnowledge.setDocu_person(jo.get("col6") != null ? jo.get("col6").getAsString() : "");
                	relKnowledge.setDocu_incident(jo.get("col7") != null ? jo.get("col7").getAsString() : "");
                	relKnowledge.setDocu_area(jo.get("col8") != null ? jo.get("col8").getAsString() : "");
                	relKnowledge.setDocu_mc(jo.get("col9") != null ? jo.get("col9").getAsString() : "");
                	relKnowledge.setDocu_actor(jo.get("col10") != null ? jo.get("col10").getAsString() : "");
                	relKnowledge.setDocu_keyword(jo.get("col11") != null ? jo.get("col11").getAsString() : "");
                	relKnowledge.setDocu_etc(jo.get("col12") != null ? jo.get("col12").getAsString() : "");

                	//필수 체크 - sum(len(program title part assetid))>0
                	if((relKnowledge.getProgram()+relKnowledge.getTitle()+relKnowledge.getPart()+relKnowledge.getAssetid()).equals("")){
                		System.out.println("Wrong data passed : " +  je.toString());
                		continue;
                	}
                	
                    //DB Insert
                    //System.out.println("relKnowledge = " + relKnowledge.toString());
                	relKnowledgeMapper.addRelKnowledgesDocu(relKnowledge);
                }else if(relKnowledge.getRelKnowledgeType().equals("heal")) {
//                	relKnowledge.setIdx(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setRegweek(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setStat(jo.get("col1") != null ? jo.get("col1").getAsString() : "");
                	relKnowledge.setProgram("");
                	relKnowledge.setTitle(jo.get("col2") != null ? jo.get("col2").getAsString() : "");
                	relKnowledge.setPart(jo.get("col3") != null ? jo.get("col3").getAsString() : "");
                	relKnowledge.setAssetid(jo.get("col4") != null ? jo.get("col4").getAsString() : "");
                	relKnowledge.setType("");
                	
                	relKnowledge.setHeal_subject(jo.get("col5") != null ? jo.get("col5").getAsString() : "");
                	relKnowledge.setHeal_part(jo.get("col6") != null ? jo.get("col6").getAsString() : "");
                	relKnowledge.setHeal_symtoms(jo.get("col7") != null ? jo.get("col7").getAsString() : "");
                	relKnowledge.setHeal_cause_a(jo.get("col8") != null ? jo.get("col8").getAsString() : "");
                	relKnowledge.setHeal_cause_b(jo.get("col9") != null ? jo.get("col9").getAsString() : "");
                	relKnowledge.setHeal_cause_c(jo.get("col10") != null ? jo.get("col10").getAsString() : "");
                	relKnowledge.setHeal_diagnose(jo.get("col11") != null ? jo.get("col11").getAsString() : "");
                	relKnowledge.setHeal_treat_type(jo.get("col12") != null ? jo.get("col12").getAsString() : "");
                	relKnowledge.setHeal_prevention(jo.get("col13") != null ? jo.get("col13").getAsString() : "");
                	relKnowledge.setHeal_drug(jo.get("col14") != null ? jo.get("col14").getAsString() : "");
                	relKnowledge.setHeal_treat_name(jo.get("col15") != null ? jo.get("col15").getAsString() : "");
                	relKnowledge.setHeal_hospital(jo.get("col16") != null ? jo.get("col16").getAsString() : "");
                	relKnowledge.setHeal_doctor(jo.get("col17") != null ? jo.get("col17").getAsString() : "");
                	relKnowledge.setHeal_talker(jo.get("col18") != null ? jo.get("col18").getAsString() : "");
                	relKnowledge.setHeal_actor(jo.get("col19") != null ? jo.get("col19").getAsString() : "");
                	relKnowledge.setHeal_expert(jo.get("col20") != null ? jo.get("col20").getAsString() : "");
                	relKnowledge.setHeal_public(jo.get("col21") != null ? jo.get("col21").getAsString() : "");
                	relKnowledge.setHeal_course(jo.get("col22") != null ? jo.get("col22").getAsString() : "");
                	relKnowledge.setHeal_activity(jo.get("col23") != null ? jo.get("col23").getAsString() : "");
                	relKnowledge.setHeal_ingredient(jo.get("col24") != null ? jo.get("col24").getAsString() : "");
                	relKnowledge.setHeal_food_a(jo.get("col25") != null ? jo.get("col25").getAsString() : "");
                	relKnowledge.setHeal_food_b(jo.get("col26") != null ? jo.get("col26").getAsString() : "");
                	relKnowledge.setHeal_activity_part(jo.get("col27") != null ? jo.get("col27").getAsString() : "");
                	relKnowledge.setHeal_activity_name(jo.get("col28") != null ? jo.get("col28").getAsString() : "");
                	relKnowledge.setHeal_season(jo.get("col29") != null ? jo.get("col29").getAsString() : "");
                	relKnowledge.setHeal_target(jo.get("col30") != null ? jo.get("col30").getAsString() : "");
                	relKnowledge.setHeal_keyword(jo.get("col31") != null ? jo.get("col31").getAsString() : "");
                	relKnowledge.setHeal_etc(jo.get("col32") != null ? jo.get("col32").getAsString() : "");

                	//필수 체크 - sum(len(program title part assetid))>0
                	if((relKnowledge.getProgram()+relKnowledge.getTitle()+relKnowledge.getPart()+relKnowledge.getAssetid()).equals("")){
                		System.out.println("Wrong data passed : " +  je.toString());
                		continue;
                	}
                	
                    //DB Insert
                    //System.out.println("relKnowledge = " + relKnowledge.toString());
                	relKnowledgeMapper.addRelKnowledgesHeal(relKnowledge);
                }else if(relKnowledge.getRelKnowledgeType().equals("hist")) {
//                	relKnowledge.setIdx(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setRegweek(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setStat(jo.get("col1") != null ? jo.get("col1").getAsString() : "");
                	relKnowledge.setProgram(jo.get("col2") != null ? jo.get("col2").getAsString() : "");
                	relKnowledge.setTitle(jo.get("col3") != null ? jo.get("col3").getAsString() : "");
                	relKnowledge.setPart("");
                	relKnowledge.setAssetid(jo.get("col4") != null ? jo.get("col4").getAsString() : "");
                	relKnowledge.setType("");
                	
                	relKnowledge.setHist_subject(jo.get("col5") != null ? jo.get("col5").getAsString() : "");
                	relKnowledge.setHist_area_a(jo.get("col6") != null ? jo.get("col6").getAsString() : "");
                	relKnowledge.setHist_area_b(jo.get("col7") != null ? jo.get("col7").getAsString() : "");
                	relKnowledge.setHist_year_a(jo.get("col8") != null ? jo.get("col8").getAsString() : "");
                	relKnowledge.setHist_year_b(jo.get("col9") != null ? jo.get("col9").getAsString() : "");
                	relKnowledge.setHist_person(jo.get("col10") != null ? jo.get("col10").getAsString() : "");
                	relKnowledge.setHist_feat(jo.get("col11") != null ? jo.get("col11").getAsString() : "");
                	relKnowledge.setHist_incident(jo.get("col12") != null ? jo.get("col12").getAsString() : "");
                	relKnowledge.setHist_thing(jo.get("col13") != null ? jo.get("col13").getAsString() : "");
                	relKnowledge.setHist_area_c(jo.get("col14") != null ? jo.get("col14").getAsString() : "");
                	relKnowledge.setHist_actor(jo.get("col15") != null ? jo.get("col15").getAsString() : "");
                	relKnowledge.setHist_etc(jo.get("col16") != null ? jo.get("col16").getAsString() : "");

                	//필수 체크 - sum(len(program title part assetid))>0
                	if((relKnowledge.getProgram()+relKnowledge.getTitle()+relKnowledge.getPart()+relKnowledge.getAssetid()).equals("")){
                		System.out.println("Wrong data passed : " +  je.toString());
                		continue;
                	}
                	
                    //DB Insert
                    //System.out.println("relKnowledge = " + relKnowledge.toString());
                	relKnowledgeMapper.addRelKnowledgesHist(relKnowledge);
                }else if(relKnowledge.getRelKnowledgeType().equals("tour")) {
//                	relKnowledge.setIdx(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setRegweek(jo.get("col0") != null ? jo.get("col0").getAsString() : "");
                	relKnowledge.setStat(jo.get("col1") != null ? jo.get("col1").getAsString() : "");
                	relKnowledge.setProgram("");
                	relKnowledge.setTitle(jo.get("col2") != null ? jo.get("col2").getAsString() : "");
                	relKnowledge.setPart(jo.get("col3") != null ? jo.get("col3").getAsString() : "");
                	relKnowledge.setAssetid(jo.get("col4") != null ? jo.get("col4").getAsString() : "");
                	relKnowledge.setType(jo.get("col5") != null ? jo.get("col5").getAsString() : "");
                	
                	relKnowledge.setTour_theme(jo.get("col6") != null ? jo.get("col6").getAsString() : "");
                	relKnowledge.setTour_area_a(jo.get("col7") != null ? jo.get("col7").getAsString() : "");
                	relKnowledge.setTour_area_b(jo.get("col8") != null ? jo.get("col8").getAsString() : "");
                	relKnowledge.setTour_area_c(jo.get("col9") != null ? jo.get("col9").getAsString() : "");
                	relKnowledge.setTour_area_d(jo.get("col10") != null ? jo.get("col10").getAsString() : "");
                	relKnowledge.setTour_area_e(jo.get("col11") != null ? jo.get("col11").getAsString() : "");
                	relKnowledge.setTour_natural(jo.get("col12") != null ? jo.get("col12").getAsString() : "");
                	relKnowledge.setTour_nation(jo.get("col13") != null ? jo.get("col13").getAsString() : "");
                	relKnowledge.setTour_person(jo.get("col14") != null ? jo.get("col14").getAsString() : "");
                	relKnowledge.setTour_festival(jo.get("col15") != null ? jo.get("col15").getAsString() : "");
                	relKnowledge.setTour_type_a(jo.get("col16") != null ? jo.get("col16").getAsString() : "");
                	relKnowledge.setTour_type_b(jo.get("col17") != null ? jo.get("col17").getAsString() : "");
                	relKnowledge.setTour_type_c(jo.get("col18") != null ? jo.get("col18").getAsString() : "");
                	relKnowledge.setTour_season(jo.get("col19") != null ? jo.get("col19").getAsString() : "");
                	relKnowledge.setTour_human(jo.get("col20") != null ? jo.get("col20").getAsString() : "");
                	relKnowledge.setTour_tourist(jo.get("col21") != null ? jo.get("col21").getAsString() : "");
                	relKnowledge.setTour_mc_yn(jo.get("col22") != null ? jo.get("col22").getAsString() : "");
                	relKnowledge.setTour_mc(jo.get("col23") != null ? jo.get("col23").getAsString() : "");
                	relKnowledge.setTour_food(jo.get("col24") != null ? jo.get("col24").getAsString() : "");
                	relKnowledge.setTour_type_d(jo.get("col25") != null ? jo.get("col25").getAsString() : "");
                	relKnowledge.setTour_festival_name(jo.get("col26") != null ? jo.get("col26").getAsString() : "");
                	relKnowledge.setTour_trans(jo.get("col27") != null ? jo.get("col27").getAsString() : "");
                	relKnowledge.setTour_peaple(jo.get("col28") != null ? jo.get("col28").getAsString() : "");
                	relKnowledge.setTour_weather(jo.get("col29") != null ? jo.get("col29").getAsString() : "");
                	relKnowledge.setTour_music(jo.get("col30") != null ? jo.get("col30").getAsString() : "");
                	relKnowledge.setTour_period(jo.get("col31") != null ? jo.get("col31").getAsString() : "");
                	relKnowledge.setTour_companion(jo.get("col32") != null ? jo.get("col32").getAsString() : "");
                	relKnowledge.setTour_etc_a(jo.get("col33") != null ? jo.get("col33").getAsString() : "");
                	relKnowledge.setTour_etc_b(jo.get("col34") != null ? jo.get("col34").getAsString() : "");

                	//필수 체크 - sum(len(program title part assetid))>0
                	if((relKnowledge.getProgram()+relKnowledge.getTitle()+relKnowledge.getPart()+relKnowledge.getAssetid()).equals("")){
                		System.out.println("Wrong data passed : " +  je.toString());
                		continue;
                	}
                	
                    //DB Insert
                    //System.out.println("relKnowledge = " + relKnowledge.toString());
                	relKnowledgeMapper.addRelKnowledgesTour(relKnowledge);
                }else {
                	
                }
                
            }
        }
        
		/*
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
        */

        return rt;
	}

	//연관지식 다운로드 권재일 추가
	@Override
	public String getRelKnowledgeListDownload(String type) {
    	Calendar calendar = Calendar.getInstance();			//[파일업다운로드]
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		//[파일업다운로드]
        
        logger.debug("\n\n--------\n[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.getRelKnowledgeListDownload 시작");
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " type = " + type);
		String strFileName = "";
		
		List<Map<String, Object>> reqItems = null;
		String resultStr = "";
		String lineFeed = System.getProperty("line.separator");
		String seperator = "\t";
		
		RelKnowledge relKnowledge = new RelKnowledge();
		relKnowledge.setRelKnowledgeType(type);
		
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 1. Header");
		//1. 헤더
		//resultStr = "Type" + seperator + "Keyword" + lineFeed;
		if(relKnowledge.getRelKnowledgeType().equals("cook")) {
			resultStr = //38
					"작업일" + seperator + 
					"상태" + seperator + 
					"프로그램명" + seperator + 
					"Title" + seperator + 
					"ASSETID" + seperator + 
					"유형" + seperator + 
					"강습요리명" + seperator + 
					"비강습요리명" + seperator + 
					"요리메인재료" + seperator + 
					"요리메인재료구분" + seperator + 
					
					"요리기타재료" + seperator + 
					"요리유형" + seperator + 
					"요리분류" + seperator + 
					"요리강습자" + seperator + 
					"요리강습자외출연자" + seperator + 
					"국내지역도단위" + seperator + 
					"국내지역소단위" + seperator + 
					"해외지역국가" + seperator + 
					"해외지역세계구분" + seperator + 
					"식당명" + seperator +
					
					"국내식당위치도단위" + seperator + 
					"국내식당위치소단위" + seperator + 
					"해외식당위치해외지역" + seperator + 
					"해외식당위치국가" + seperator + 
					"요리적합계절" + seperator + 
					"요리적합일" + seperator + 
					"요리목적" + seperator + 
					"맛유형" + seperator + 
					"조리도구" + seperator + 
					"촬영장소" + seperator + 
					
					"요리강습수준" + seperator + 
					"요리강습비중" + seperator + 
					"요리관련내용비중" + seperator + 
					"요리소요시간" + seperator + 
					"영양성분" + seperator + 
					"프로그램회당소개요리수" + seperator + 
					"기타특징" + seperator + seperator +	//원본에 기타특징과 비교 사이에 한칸 더 있음 
					"비고" + lineFeed;
		}else if(relKnowledge.getRelKnowledgeType().equals("curr")) {
			resultStr = //15
					"작업일" + seperator + 
					"상태" + seperator + 
					"프로그램명" + seperator + 
					"Title" + seperator + 
					"ASSETID" + seperator + 
					"주제" + seperator + 
					"관련인물" + seperator + 
					"관련사건" + seperator + 
					"관련제품" + seperator + 
					"개선방안" + seperator +
					
					"유형" + seperator + 
					"진행자" + seperator + 
					"출연자" + seperator + 
					"키워드" + seperator + 
					"비고" + lineFeed;
		}else if(relKnowledge.getRelKnowledgeType().equals("docu")) {
			resultStr = //13
					"작업일" + seperator + 
					"상태" + seperator + 
					"프로그램명" + seperator + 
					"Title" + seperator + 
					"ASSETID" + seperator + 
					"주제" + seperator + 
					"관련인물" + seperator + 
					"관련사건" + seperator + 
					"관련지역" + seperator + 
					"진행자" + seperator + 
					
					"출연자" + seperator + 
					"키워드" + seperator + 
					"비고" + lineFeed;
		}else if(relKnowledge.getRelKnowledgeType().equals("heal")) {
			resultStr = //33
					"작업일" + seperator + 
					"상태" + seperator + 
					"프로그램명" + seperator + 
					"Title" + seperator + 
					"ASSETID" + seperator + 
					"질병명" + seperator + 
					"질병발생부위" + seperator + 
					"증상" + seperator + 
					"음식외원인" + seperator + 
					"원인음식" + seperator + 
					
					"원인성분" + seperator + 
					"진단/검사방법" + seperator + 
					"치료방법유형" + seperator + 
					"예방방법유형" + seperator + 
					"약품명/성분" + seperator + 
					"치료명" + seperator + 
					"병원명" + seperator + 
					"의사명" + seperator + 
					"강연자" + seperator + 
					"연예인출연자" + seperator + 
					
					"전문가출연자" + seperator + 
					"일반인출연자비중" + seperator + 
					"진료과목" + seperator + 
					"건강증진활동유형" + seperator + 
					"성분명" + seperator + 
					"건강음식재료명" + seperator + 
					"건강음식명" + seperator + 
					"건강증진활동부위" + seperator + 
					"건강증진활동명" + seperator + 
					"관련계절" + seperator + 
					
					"대상" + seperator + 
					"키워드" + seperator + 
					"비고" + lineFeed;
		}else if(relKnowledge.getRelKnowledgeType().equals("hist")) {
			resultStr = //17
					"작업일" + seperator + 
					"상태" + seperator + 
					"프로그램명" + seperator + 
					"Title" + seperator + 
					"ASSETID" + seperator + 
					"주제" + seperator + 
					"배경국가" + seperator + 
					"당시국가" + seperator + 
					"시대" + seperator + 
					"연대" + seperator + 
					
					"관련인물" + seperator + 
					"관련업적" + seperator + 
					"관련사건" + seperator + 
					"관련유물/유적" + seperator + 
					"관련지역" + seperator + 
					"출연자" + seperator + 
					"비고" + lineFeed;					
		}else if(relKnowledge.getRelKnowledgeType().equals("tour")) {
			resultStr = //35
					"작업일시" + seperator + 
					"상태" + seperator + 
					"프로그램명" + seperator + 
					"Title" + seperator + 
					"ASSETID" + seperator + 
					"유형" + seperator + 
					"여행테마" + seperator + 
					"국가명" + seperator + 
					"도시명" + seperator + 
					"지역명" + seperator + 
					
					"대륙명" + seperator + 
					"관광지명" + seperator + 
					"자연비중" + seperator + 
					"유적지관련국가민족" + seperator + 
					"관련인물" + seperator + 
					"축제명" + seperator + 
					"자연경관유형" + seperator + 
					"인공경관유형" + seperator + 
					"여행목적유형" + seperator + 
					"계절명" + seperator + 
					
					"여행자등장비중" + seperator + 
					"여행자명" + seperator + 
					"해설자유무" + seperator + 
					"해설자명" + seperator + 
					"음식명" + seperator + 
					"여행체험명" + seperator + 
					"공연명" + seperator + 
					"교통수단명" + seperator + 
					"관광지수" + seperator + 
					"여행날씨" + seperator + 
					
					"배경음악" + seperator + 
					"여행기간" + seperator + 
					"여행동반자유형" + seperator + 
					"기타" + seperator + 
					"비고" + lineFeed;
		}else {
			
		}
		
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 2. Body");
		//2. 모든 태그 유형 로딩 + 3. 태그 하나씩 불러오기  이 둘을 합치기 - 모든 태그 받기 getDicKeywordsListAll
		//DicKeywords reqKeyword = new DicKeywords();
		
		//type별 동작
		//List<RelKnowledge> relKnowledgeList = relKnowledgeMapper.getDicKeywordsList(relKnowledge);
		List<RelKnowledge> relKnowledgeList = null;
		if(relKnowledge.getRelKnowledgeType().equals("cook")) {
			relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListCook(relKnowledge);
		}else if(relKnowledge.getRelKnowledgeType().equals("curr")) {
			relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListCurr(relKnowledge);
		}else if(relKnowledge.getRelKnowledgeType().equals("docu")) {
			relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListDocu(relKnowledge);
		}else if(relKnowledge.getRelKnowledgeType().equals("heal")) {
			relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListHeal(relKnowledge);
		}else if(relKnowledge.getRelKnowledgeType().equals("hist")) {
			relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListHist(relKnowledge);
		}else if(relKnowledge.getRelKnowledgeType().equals("tour")) {
			relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListTour(relKnowledge);
		}else {
			
		}
		
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 3. toString");
		//4. 문자열화
		for(RelKnowledge item : relKnowledgeList) {
			//String strKeyword = item.getKeyword();
			//resultStr += item.getType() + seperator + strKeyword + lineFeed;
			if(relKnowledge.getRelKnowledgeType().equals("cook")) {
				String strRegweek = item.getRegweek();
				String strStat = item.getStat();
				String strTitle = item.getTitle();
				String strPart = item.getPart();
				String strAssetid = item.getAssetid();
				String strType = item.getType();
				String strCook_title_a = item.getCook_title_a();
				String strCook_title_b = item.getCook_title_b();
				String strCook_parts = item.getCook_parts();
				String strCook_parts_main = item.getCook_parts_main();
				String strCook_parts_sub = item.getCook_parts_sub();
				String strCook_type_a = item.getCook_type_a();
				String strCook_type_b = item.getCook_type_b();
				String strCook_chef_a = item.getCook_chef_a();
				String strCook_chef_b = item.getCook_chef_b();
				String strCook_kor_area_a = item.getCook_kor_area_a();
				String strCook_kor_area_b = item.getCook_kor_area_b();
				String strCook_oth_area_a = item.getCook_oth_area_a();
				String strCook_oth_area_b = item.getCook_oth_area_b();
				String strCook_place_name = item.getCook_place_name();
				String strCook_place_kor_area_a = item.getCook_place_kor_area_a();
				String strCook_place_kor_area_b = item.getCook_place_kor_area_b();
				String strCook_place_oth_area_a = item.getCook_place_oth_area_a();
				String strCook_place_oth_area_b = item.getCook_place_oth_area_b();
				String strCook_good_season = item.getCook_good_season();
				String strCook_good_day = item.getCook_good_day();
				String strCook_purpose = item.getCook_purpose();
				String strCook_flavor = item.getCook_flavor();
				String strCook_cooker = item.getCook_cooker();
				String strCook_shoot_place = item.getCook_shoot_place();
				String strCook_level_a = item.getCook_level_a();
				String strCook_level_b = item.getCook_level_b();
				String strCook_level_c = item.getCook_level_c();
				String strCook_time = item.getCook_time();
				String strCook_nutrient = item.getCook_nutrient();
				String strCook_ea = item.getCook_ea();
				String strCook_etc_a = item.getCook_etc_a();
				String strCook_etc_b = item.getCook_etc_b();
				
				resultStr += 
						strRegweek + seperator +
						strStat + seperator +
						strTitle + seperator +
						strPart + seperator +
						strAssetid + seperator +
						strType + seperator +
						strCook_title_a + seperator +
						strCook_title_b + seperator +
						strCook_parts + seperator +
						strCook_parts_main + seperator +
						strCook_parts_sub + seperator +
						strCook_type_a + seperator +
						strCook_type_b + seperator +
						strCook_chef_a + seperator +
						strCook_chef_b + seperator +
						strCook_kor_area_a + seperator +
						strCook_kor_area_b + seperator +
						strCook_oth_area_a + seperator +
						strCook_oth_area_b + seperator +
						strCook_place_name + seperator +
						strCook_place_kor_area_a + seperator +
						strCook_place_kor_area_b + seperator +
						strCook_place_oth_area_a + seperator +
						strCook_place_oth_area_b + seperator +
						strCook_good_season + seperator +
						strCook_good_day + seperator +
						strCook_purpose + seperator +
						strCook_flavor + seperator +
						strCook_cooker + seperator +
						strCook_shoot_place + seperator +
						strCook_level_a + seperator +
						strCook_level_b + seperator +
						strCook_level_c + seperator +
						strCook_time + seperator +
						strCook_nutrient + seperator +
						strCook_ea + seperator +
						strCook_etc_a + seperator + seperator +	//원본에 기타특징과 비교 사이에 한칸 더 있음
						strCook_etc_b + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("curr")) {
				String strRegweek = item.getRegweek();
				String strStat = item.getStat();
				String strTitle = item.getTitle();
				String strPart = item.getPart();
				String strAssetid = item.getAssetid();
				String strCurr_subject = item.getCurr_subject();
				String strCurr_person = item.getCurr_person();
				String strCurr_incident = item.getCurr_incident();
				String strCurr_product = item.getCurr_product();
				String strCurr_process = item.getCurr_process();
				String strCurr_type = item.getCurr_type();
				String strCurr_mc = item.getCurr_mc();
				String strCurr_actor = item.getCurr_actor();
				String strCurr_keyword = item.getCurr_keyword();
				String strCur_etc = item.getCur_etc();
				
				resultStr += 
						strRegweek + seperator +
						strStat + seperator +
						strTitle + seperator +
						strPart + seperator +
						strAssetid + seperator +
						strCurr_subject + seperator +
						strCurr_person + seperator +
						strCurr_incident + seperator +
						strCurr_product + seperator +
						strCurr_process + seperator +
						strCurr_type + seperator +
						strCurr_mc + seperator +
						strCurr_actor + seperator +
						strCurr_keyword + seperator +
						strCur_etc + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("docu")) {
				String strRegweek = item.getRegweek();
				String strStat = item.getStat();
				String strTitle = item.getTitle();
				String strPart = item.getPart();
				String strAssetid = item.getAssetid();
				String strDocu_subject = item.getDocu_subject();
				String strDocu_person = item.getDocu_person();
				String strDocu_incident = item.getDocu_incident();
				String strDocu_area = item.getDocu_area();
				String strDocu_mc = item.getDocu_mc();
				String strDocu_actor = item.getDocu_actor();
				String strDocu_keyword = item.getDocu_keyword();
				String strDocu_etc = item.getDocu_etc();
				
				resultStr += 
						strRegweek + seperator +
						strStat + seperator +
						strTitle + seperator +
						strPart + seperator +
						strAssetid + seperator +
						strDocu_subject + seperator +
						strDocu_person + seperator +
						strDocu_incident + seperator +
						strDocu_area + seperator +
						strDocu_mc + seperator +
						strDocu_actor + seperator +
						strDocu_keyword + seperator +
						strDocu_etc + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("heal")) {
				String strRegweek = item.getRegweek();
				String strStat = item.getStat();
				String strTitle = item.getTitle();
				String strPart = item.getPart();
				String strAssetid = item.getAssetid();
				String strHeal_subject = item.getHeal_subject();
				String strHeal_part = item.getHeal_part();
				String strHeal_symtoms = item.getHeal_symtoms();
				String strHeal_cause_a = item.getHeal_cause_a();
				String strHeal_cause_b = item.getHeal_cause_b();
				String strHeal_cause_c = item.getHeal_cause_c();
				String strHeal_diagnose = item.getHeal_diagnose();
				String strHeal_treat_type = item.getHeal_treat_type();
				String strHeal_prevention = item.getHeal_prevention();
				String strHeal_drug = item.getHeal_drug();
				String strHeal_treat_name = item.getHeal_treat_name();
				String strHeal_hospital = item.getHeal_hospital();
				String strHeal_doctor = item.getHeal_doctor();
				String strHeal_talker = item.getHeal_talker();
				String strHeal_actor = item.getHeal_actor();
				String strHeal_expert = item.getHeal_expert();
				String strHeal_public = item.getHeal_public();
				String strHeal_course = item.getHeal_course();
				String strHeal_activity = item.getHeal_activity();
				String strHeal_ingredient = item.getHeal_ingredient();
				String strHeal_food_a = item.getHeal_food_a();
				String strHeal_food_b = item.getHeal_food_b();
				String strHeal_activity_part = item.getHeal_activity_part();
				String strHeal_activity_name = item.getHeal_activity_name();
				String strHeal_season = item.getHeal_season();
				String strHeal_target = item.getHeal_target();
				String strHeal_keyword = item.getHeal_keyword();
				String strHeal_etc = item.getHeal_etc();
				
				resultStr += 
						strRegweek + seperator +
						strStat + seperator +
						strTitle + seperator +
						strPart + seperator +
						strAssetid + seperator +
						strHeal_subject + seperator +
						strHeal_part + seperator +
						strHeal_symtoms + seperator +
						strHeal_cause_a + seperator +
						strHeal_cause_b + seperator +
						strHeal_cause_c + seperator +
						strHeal_diagnose + seperator +
						strHeal_treat_type + seperator +
						strHeal_prevention + seperator +
						strHeal_drug + seperator +
						strHeal_treat_name + seperator +
						strHeal_hospital + seperator +
						strHeal_doctor + seperator +
						strHeal_talker + seperator +
						strHeal_actor + seperator +
						strHeal_expert + seperator +
						strHeal_public + seperator +
						strHeal_course + seperator +
						strHeal_activity + seperator +
						strHeal_ingredient + seperator +
						strHeal_food_a + seperator +
						strHeal_food_b + seperator +
						strHeal_activity_part + seperator +
						strHeal_activity_name + seperator +
						strHeal_season + seperator +
						strHeal_target + seperator +
						strHeal_keyword + seperator +
						strHeal_etc + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("hist")) {
				String strRegweek = item.getRegweek();
				String strStat = item.getStat();
				String strProgram = item.getProgram();
				String strTitle = item.getTitle();
				String strAssetid = item.getAssetid();
				String strHist_subject = item.getHist_subject();
				String strHist_area_a = item.getHist_area_a();
				String strHist_area_b = item.getHist_area_b();
				String strHist_year_a = item.getHist_year_a();
				String strHist_year_b = item.getHist_year_b();
				String strHist_person = item.getHist_person();
				String strHist_feat = item.getHist_feat();
				String strHist_incident = item.getHist_incident();
				String strHist_thing = item.getHist_thing();
				String strHist_area_c = item.getHist_area_c();
				String strHist_actor = item.getHist_actor();
				String strHist_etc = item.getHist_etc();
				
				resultStr += 
						strRegweek + seperator +
						strStat + seperator +
						strProgram + seperator +
						strTitle + seperator +
						strAssetid + seperator +
						strHist_subject + seperator +
						strHist_area_a + seperator +
						strHist_area_b + seperator +
						strHist_year_a + seperator +
						strHist_year_b + seperator +
						strHist_person + seperator +
						strHist_feat + seperator +
						strHist_incident + seperator +
						strHist_thing + seperator +
						strHist_area_c + seperator +
						strHist_actor + seperator +
						strHist_etc + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("tour")) {
				String strRegweek = item.getRegweek();
				String strStat = item.getStat();
				String strTitle = item.getTitle();
				String strPart = item.getPart();
				String strAssetid = item.getAssetid();
				String strType = item.getType();
				String strTour_theme = item.getTour_theme();
				String strTour_area_a = item.getTour_area_a();
				String strTour_area_b = item.getTour_area_b();
				String strTour_area_c = item.getTour_area_c();
				String strTour_area_d = item.getTour_area_d();
				String strTour_area_e = item.getTour_area_e();
				String strTour_natural = item.getTour_natural();
				String strTour_nation = item.getTour_nation();
				String strTour_person = item.getTour_person();
				String strTour_festival = item.getTour_festival();
				String strTour_type_a = item.getTour_type_a();
				String strTour_type_b = item.getTour_type_b();
				String strTour_type_c = item.getTour_type_c();
				String strTour_season = item.getTour_season();
				String strTour_human = item.getTour_human();
				String strTour_tourist = item.getTour_tourist();
				String strTour_mc_yn = item.getTour_mc_yn();
				String strTour_mc = item.getTour_mc();
				String strTour_food = item.getTour_food();
				String strTour_type_d = item.getTour_type_d();
				String strTour_festival_name = item.getTour_festival_name();
				String strTour_trans = item.getTour_trans();
				String strTour_peaple = item.getTour_peaple();
				String strTour_weather = item.getTour_weather();
				String strTour_music = item.getTour_music();
				String strTour_period = item.getTour_period();
				String strTour_companion = item.getTour_companion();
				String strTour_etc_a = item.getTour_etc_a();
				String strTour_etc_b = item.getTour_etc_b();
				
				resultStr += 
						strRegweek + seperator +
						strStat + seperator +
						strTitle + seperator +
						strPart + seperator +
						strAssetid + seperator +
						strType + seperator +
						strTour_theme + seperator +
						strTour_area_a + seperator +
						strTour_area_b + seperator +
						strTour_area_c + seperator +
						strTour_area_d + seperator +
						strTour_area_e + seperator +
						strTour_natural + seperator +
						strTour_nation + seperator +
						strTour_person + seperator +
						strTour_festival + seperator +
						strTour_type_a + seperator +
						strTour_type_b + seperator +
						strTour_type_c + seperator +
						strTour_season + seperator +
						strTour_human + seperator +
						strTour_tourist + seperator +
						strTour_mc_yn + seperator +
						strTour_mc + seperator +
						strTour_food + seperator +
						strTour_type_d + seperator +
						strTour_festival_name + seperator +
						strTour_trans + seperator +
						strTour_peaple + seperator +
						strTour_weather + seperator +
						strTour_music + seperator +
						strTour_period + seperator +
						strTour_companion + seperator +
						strTour_etc_a + seperator +
						strTour_etc_b + lineFeed;
			}else {
				
			}
		}
		
		
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4. File");
		//5. 파일형태로 표출
		String fileNameContent = "VOD_RT_"+type.toUpperCase()+"_"+DateUtil.formatDate(new Date(), "yyyyMMdd")+".csv";
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.1");
		int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");
		
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.2");
		String strSeparator = (UPLOAD_DIR.substring(UPLOAD_DIR.length()-1).equals(File.separator) ? "" : File.separator);
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.3");
		strFileName = UPLOAD_DIR + strSeparator + fileNameContent;
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " strFileName = " + strFileName);
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.getRelKnowledgeListDownload 끝(리턴)\n--------\n\n");
		return strFileName;
	}

	public String uploadRelknowledgeFile(String readString, String type) {
		// TODO Auto-generated method stub
    	Calendar calendar = Calendar.getInstance();			//[파일업다운로드]
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        
        
        //[파일업다운로드]
        
        logger.debug("\n\n--------\n[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.getRelKnowledgeListDownload 시작");
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " type = " + type);
		String strFileName = "";
		
		String resultStr = readString;
		
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4. File");
		//5. 파일형태로 표출
		String fileNameContent = "VOD_RT_"+type.toUpperCase()+".csv";	//날짜 요소 뺌 DateUtil.formatDate(new Date(), "yyyyMMdd")
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.1");
		int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR+"csv_import"+File.separator, fileNameContent, "utf-8");
		
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.2");
		String strSeparator = (UPLOAD_DIR.substring(UPLOAD_DIR.length()-1).equals(File.separator) ? "" : File.separator);
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.3");
		strFileName = UPLOAD_DIR + strSeparator + fileNameContent;
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " strFileName = " + strFileName);
		logger.debug("[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.uploadRelknowledgeFile 끝(리턴)\n--------\n\n");
		return strFileName;
		
	}

	public int importRelKnowledgesByType(RelKnowledge relKnowledge) {
		//intResult = relKnowledgeMapper.importRelKnowledgesByType(relKnowledge);
		
		String strRelKnowledgeType = relKnowledge.getRelKnowledgeType().toLowerCase();
		int intResult = 0;
		
		if(strRelKnowledgeType.equals("cook")) {
			intResult = relKnowledgeMapper.importRelKnowledgesCook(relKnowledge);
		}else if(strRelKnowledgeType.equals("curr")) {
			intResult = relKnowledgeMapper.importRelKnowledgesCurr(relKnowledge);
		}else if(strRelKnowledgeType.equals("docu")) {
			intResult = relKnowledgeMapper.importRelKnowledgesDocu(relKnowledge);
		}else if(strRelKnowledgeType.equals("heal")) {
			intResult = relKnowledgeMapper.importRelKnowledgesHeal(relKnowledge);
		}else if(strRelKnowledgeType.equals("hist")) {
			intResult = relKnowledgeMapper.importRelKnowledgesHist(relKnowledge);
		}else if(strRelKnowledgeType.equals("tour")) {
			intResult = relKnowledgeMapper.importRelKnowledgesTour(relKnowledge);
		}
		
		
		
		return intResult;
	}

	public void makeFileRelKnowledge() {
		// TODO Auto-generated method stub
		//from RelKnowledgeService.getRelKnowledgeListDownload
		// 테스트 완성 후 getRelKnowledgeListDownload 파라미터를 없앨것
		
		Calendar calendar = Calendar.getInstance();			//[파일업다운로드]
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		//[파일업다운로드]
        
		String strFileName = "";
		
		String[] types = {"cook","curr","docu","heal","hist","tour"};
		for(String type : types) {
	        logger.debug("\n\n--------\n[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.getRelKnowledgeListDownload 시작");
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " type = " + type);
			
			List<Map<String, Object>> reqItems = null;
			String resultStr = "";
			String lineFeed = System.getProperty("line.separator");
			String seperator = ",";
			
			RelKnowledge relKnowledge = new RelKnowledge();
			relKnowledge.setRelKnowledgeType(type);
			
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " 1. Header");
			//1. 헤더
			//resultStr = "Type" + seperator + "Keyword" + lineFeed;
			if(relKnowledge.getRelKnowledgeType().equals("cook")) {
				resultStr = //38
						"작업일" + seperator + 
						"상태" + seperator + 
						"프로그램명" + seperator + 
						"Title" + seperator + 
						"ASSETID" + seperator + 
						"유형" + seperator + 
						"강습요리명" + seperator + 
						"비강습요리명" + seperator + 
						"요리메인재료" + seperator + 
						"요리메인재료구분" + seperator + 
						
						"요리기타재료" + seperator + 
						"요리유형" + seperator + 
						"요리분류" + seperator + 
						"요리강습자" + seperator + 
						"요리강습자외출연자" + seperator + 
						"국내지역도단위" + seperator + 
						"국내지역소단위" + seperator + 
						"해외지역국가" + seperator + 
						"해외지역세계구분" + seperator + 
						"식당명" + seperator +
						
						"국내식당위치도단위" + seperator + 
						"국내식당위치소단위" + seperator + 
						"해외식당위치해외지역" + seperator + 
						"해외식당위치국가" + seperator + 
						"요리적합계절" + seperator + 
						"요리적합일" + seperator + 
						"요리목적" + seperator + 
						"맛유형" + seperator + 
						"조리도구" + seperator + 
						"촬영장소" + seperator + 
						
						"요리강습수준" + seperator + 
						"요리강습비중" + seperator + 
						"요리관련내용비중" + seperator + 
						"요리소요시간" + seperator + 
						"영양성분" + seperator + 
						"프로그램회당소개요리수" + seperator + 
						"기타특징" + seperator +
						"비고" + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("curr")) {
				resultStr = //15
						"작업일" + seperator + 
						"상태" + seperator + 
						"프로그램명" + seperator + 
						"Title" + seperator + 
						"ASSETID" + seperator + 
						"주제" + seperator + 
						"관련인물" + seperator + 
						"관련사건" + seperator + 
						"관련제품" + seperator + 
						"개선방안" + seperator +
						
						"유형" + seperator + 
						"진행자" + seperator + 
						"출연자" + seperator + 
						"키워드" + seperator + 
						"비고" + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("docu")) {
				resultStr = //13
						"작업일" + seperator + 
						"상태" + seperator + 
						"프로그램명" + seperator + 
						"Title" + seperator + 
						"ASSETID" + seperator + 
						"주제" + seperator + 
						"관련인물" + seperator + 
						"관련사건" + seperator + 
						"관련지역" + seperator + 
						"진행자" + seperator + 
						
						"출연자" + seperator + 
						"키워드" + seperator + 
						"비고" + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("heal")) {
				resultStr = //33
						"작업일" + seperator + 
						"상태" + seperator + 
						"프로그램명" + seperator + 
						"Title" + seperator + 
						"ASSETID" + seperator + 
						"질병명" + seperator + 
						"질병발생부위" + seperator + 
						"증상" + seperator + 
						"음식외원인" + seperator + 
						"원인음식" + seperator + 
						
						"원인성분" + seperator + 
						"진단/검사방법" + seperator + 
						"치료방법유형" + seperator + 
						"예방방법유형" + seperator + 
						"약품명/성분" + seperator + 
						"치료명" + seperator + 
						"병원명" + seperator + 
						"의사명" + seperator + 
						"강연자" + seperator + 
						"연예인출연자" + seperator + 
						
						"전문가출연자" + seperator + 
						"일반인출연자비중" + seperator + 
						"진료과목" + seperator + 
						"건강증진활동유형" + seperator + 
						"성분명" + seperator + 
						"건강음식재료명" + seperator + 
						"건강음식명" + seperator + 
						"건강증진활동부위" + seperator + 
						"건강증진활동명" + seperator + 
						"관련계절" + seperator + 
						
						"대상" + seperator + 
						"키워드" + seperator + 
						"비고" + lineFeed;
			}else if(relKnowledge.getRelKnowledgeType().equals("hist")) {
				resultStr = //17
						"작업일" + seperator + 
						"상태" + seperator + 
						"프로그램명" + seperator + 
						"Title" + seperator + 
						"ASSETID" + seperator + 
						"주제" + seperator + 
						"배경국가" + seperator + 
						"당시국가" + seperator + 
						"시대" + seperator + 
						"연대" + seperator + 
						
						"관련인물" + seperator + 
						"관련업적" + seperator + 
						"관련사건" + seperator + 
						"관련유물/유적" + seperator + 
						"관련지역" + seperator + 
						"출연자" + seperator + 
						"비고" + lineFeed;					
			}else if(relKnowledge.getRelKnowledgeType().equals("tour")) {
				resultStr = //35
						"작업일시" + seperator + 
						"상태" + seperator + 
						"프로그램명" + seperator + 
						"Title" + seperator + 
						"ASSETID" + seperator + 
						"유형" + seperator + 
						"여행테마" + seperator + 
						"국가명" + seperator + 
						"도시명" + seperator + 
						"지역명" + seperator + 
						
						"대륙명" + seperator + 
						"관광지명" + seperator + 
						"자연비중" + seperator + 
						"유적지관련국가민족" + seperator + 
						"관련인물" + seperator + 
						"축제명" + seperator + 
						"자연경관유형" + seperator + 
						"인공경관유형" + seperator + 
						"여행목적유형" + seperator + 
						"계절명" + seperator + 
						
						"여행자등장비중" + seperator + 
						"여행자명" + seperator + 
						"해설자유무" + seperator + 
						"해설자명" + seperator + 
						"음식명" + seperator + 
						"여행체험명" + seperator + 
						"공연명" + seperator + 
						"교통수단명" + seperator + 
						"관광지수" + seperator + 
						"여행날씨" + seperator + 
						
						"배경음악" + seperator + 
						"여행기간" + seperator + 
						"여행동반자유형" + seperator + 
						"기타" + seperator + 
						"비고" + lineFeed;
			}else {
				
			}
			
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " 2. Body");
			//2. 모든 태그 유형 로딩 + 3. 태그 하나씩 불러오기  이 둘을 합치기 - 모든 태그 받기 getDicKeywordsListAll
			//DicKeywords reqKeyword = new DicKeywords();
			
			//type별 동작
			//List<RelKnowledge> relKnowledgeList = relKnowledgeMapper.getDicKeywordsList(relKnowledge);
			List<RelKnowledge> relKnowledgeList = null;
			if(relKnowledge.getRelKnowledgeType().equals("cook")) {
				relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListCook(relKnowledge);
			}else if(relKnowledge.getRelKnowledgeType().equals("curr")) {
				relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListCurr(relKnowledge);
			}else if(relKnowledge.getRelKnowledgeType().equals("docu")) {
				relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListDocu(relKnowledge);
			}else if(relKnowledge.getRelKnowledgeType().equals("heal")) {
				relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListHeal(relKnowledge);
			}else if(relKnowledge.getRelKnowledgeType().equals("hist")) {
				relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListHist(relKnowledge);
			}else if(relKnowledge.getRelKnowledgeType().equals("tour")) {
				relKnowledgeList = relKnowledgeMapper.getRelKnowledgeListTour(relKnowledge);
			}else {
				
			}
			
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " 3. toString");
			//4. 문자열화
			for(RelKnowledge item : relKnowledgeList) {
				//String strKeyword = item.getKeyword();
				//resultStr += item.getType() + seperator + strKeyword + lineFeed;
				if(relKnowledge.getRelKnowledgeType().equals("cook")) {
//					String strRegweek = item.getRegweek();
//					String strStat = item.getStat();
//					String strTitle = item.getTitle();
//					String strPart = item.getPart();
//					String strAssetid = item.getAssetid();
//					String strType = item.getType();
//					String strCook_title_a = item.getCook_title_a();
//					String strCook_title_b = item.getCook_title_b();
//					String strCook_parts = item.getCook_parts();
//					String strCook_parts_main = item.getCook_parts_main();
//					String strCook_parts_sub = item.getCook_parts_sub();
//					String strCook_type_a = item.getCook_type_a();
//					String strCook_type_b = item.getCook_type_b();
//					String strCook_chef_a = item.getCook_chef_a();
//					String strCook_chef_b = item.getCook_chef_b();
//					String strCook_kor_area_a = item.getCook_kor_area_a();
//					String strCook_kor_area_b = item.getCook_kor_area_b();
//					String strCook_oth_area_a = item.getCook_oth_area_a();
//					String strCook_oth_area_b = item.getCook_oth_area_b();
//					String strCook_place_name = item.getCook_place_name();
//					String strCook_place_kor_area_a = item.getCook_place_kor_area_a();
//					String strCook_place_kor_area_b = item.getCook_place_kor_area_b();
//					String strCook_place_oth_area_a = item.getCook_place_oth_area_a();
//					String strCook_place_oth_area_b = item.getCook_place_oth_area_b();
//					String strCook_good_season = item.getCook_good_season();
//					String strCook_good_day = item.getCook_good_day();
//					String strCook_purpose = item.getCook_purpose();
//					String strCook_flavor = item.getCook_flavor();
//					String strCook_cooker = item.getCook_cooker();
//					String strCook_shoot_place = item.getCook_shoot_place();
//					String strCook_level_a = item.getCook_level_a();
//					String strCook_level_b = item.getCook_level_b();
//					String strCook_level_c = item.getCook_level_c();
//					String strCook_time = item.getCook_time();
//					String strCook_nutrient = item.getCook_nutrient();
//					String strCook_ea = item.getCook_ea();
//					String strCook_etc_a = item.getCook_etc_a();
//					String strCook_etc_b = item.getCook_etc_b();
					String strRegweek = StringUtil.nvl(item.getRegweek(), "");
					String strStat = StringUtil.nvl(item.getStat(), "");
					String strTitle = StringUtil.nvl(item.getTitle(), "");
					String strPart = StringUtil.nvl(item.getPart(), "");
					String strAssetid = StringUtil.nvl(item.getAssetid(), "");
					String strType = StringUtil.nvl(item.getType(), "");
					String strCook_title_a = StringUtil.nvl(item.getCook_title_a(), "");
					String strCook_title_b = StringUtil.nvl(item.getCook_title_b(), "");
					String strCook_parts = StringUtil.nvl(item.getCook_parts(), "");
					String strCook_parts_main = StringUtil.nvl(item.getCook_parts_main(), "");
					String strCook_parts_sub = StringUtil.nvl(item.getCook_parts_sub(), "");
					String strCook_type_a = StringUtil.nvl(item.getCook_type_a(), "");
					String strCook_type_b = StringUtil.nvl(item.getCook_type_b(), "");
					String strCook_chef_a = StringUtil.nvl(item.getCook_chef_a(), "");
					String strCook_chef_b = StringUtil.nvl(item.getCook_chef_b(), "");
					String strCook_kor_area_a = StringUtil.nvl(item.getCook_kor_area_a(), "");
					String strCook_kor_area_b = StringUtil.nvl(item.getCook_kor_area_b(), "");
					String strCook_oth_area_a = StringUtil.nvl(item.getCook_oth_area_a(), "");
					String strCook_oth_area_b = StringUtil.nvl(item.getCook_oth_area_b(), "");
					String strCook_place_name = StringUtil.nvl(item.getCook_place_name(), "");
					String strCook_place_kor_area_a = StringUtil.nvl(item.getCook_place_kor_area_a(), "");
					String strCook_place_kor_area_b = StringUtil.nvl(item.getCook_place_kor_area_b(), "");
					String strCook_place_oth_area_a = StringUtil.nvl(item.getCook_place_oth_area_a(), "");
					String strCook_place_oth_area_b = StringUtil.nvl(item.getCook_place_oth_area_b(), "");
					String strCook_good_season = StringUtil.nvl(item.getCook_good_season(), "");
					String strCook_good_day = StringUtil.nvl(item.getCook_good_day(), "");
					String strCook_purpose = StringUtil.nvl(item.getCook_purpose(), "");
					String strCook_flavor = StringUtil.nvl(item.getCook_flavor(), "");
					String strCook_cooker = StringUtil.nvl(item.getCook_cooker(), "");
					String strCook_shoot_place = StringUtil.nvl(item.getCook_shoot_place(), "");
					String strCook_level_a = StringUtil.nvl(item.getCook_level_a(), "");
					String strCook_level_b = StringUtil.nvl(item.getCook_level_b(), "");
					String strCook_level_c = StringUtil.nvl(item.getCook_level_c(), "");
					String strCook_time = StringUtil.nvl(item.getCook_time(), "");
					String strCook_nutrient = StringUtil.nvl(item.getCook_nutrient(), "");
					String strCook_ea = StringUtil.nvl(item.getCook_ea(), "");
					String strCook_etc_a = StringUtil.nvl(item.getCook_etc_a(), "");
					String strCook_etc_b = StringUtil.nvl(item.getCook_etc_b(), "");
					
					System.out.println(strRegweek + " / " + strStat + " / " + strTitle + " / " + strPart + " / " + strAssetid + " / " + strType);
					
					if(strRegweek.indexOf(",")>-1) strRegweek = "\""+strRegweek+"\"";
					if(strStat.indexOf(",")>-1) strStat = "\""+strStat+"\"";
					if(strTitle.indexOf(",")>-1) strTitle = "\""+strTitle+"\"";
					if(strPart.indexOf(",")>-1) strPart = "\""+strPart+"\"";
					if(strAssetid.indexOf(",")>-1) strAssetid = "\""+strAssetid+"\"";
					if(strType.indexOf(",")>-1) strType = "\""+strType+"\"";
					if(strCook_title_a.indexOf(",")>-1) strCook_title_a = "\""+strCook_title_a+"\"";
					if(strCook_title_b.indexOf(",")>-1) strCook_title_b = "\""+strCook_title_b+"\"";
					if(strCook_parts.indexOf(",")>-1) strCook_parts = "\""+strCook_parts+"\"";
					if(strCook_parts_main.indexOf(",")>-1) strCook_parts_main = "\""+strCook_parts_main+"\"";
					if(strCook_parts_sub.indexOf(",")>-1) strCook_parts_sub = "\""+strCook_parts_sub+"\"";
					if(strCook_type_a.indexOf(",")>-1) strCook_type_a = "\""+strCook_type_a+"\"";
					if(strCook_type_b.indexOf(",")>-1) strCook_type_b = "\""+strCook_type_b+"\"";
					if(strCook_chef_a.indexOf(",")>-1) strCook_chef_a = "\""+strCook_chef_a+"\"";
					if(strCook_chef_b.indexOf(",")>-1) strCook_chef_b = "\""+strCook_chef_b+"\"";
					if(strCook_kor_area_a.indexOf(",")>-1) strCook_kor_area_a = "\""+strCook_kor_area_a+"\"";
					if(strCook_kor_area_b.indexOf(",")>-1) strCook_kor_area_b = "\""+strCook_kor_area_b+"\"";
					if(strCook_oth_area_a.indexOf(",")>-1) strCook_oth_area_a = "\""+strCook_oth_area_a+"\"";
					if(strCook_oth_area_b.indexOf(",")>-1) strCook_oth_area_b = "\""+strCook_oth_area_b+"\"";
					if(strCook_place_name.indexOf(",")>-1) strCook_place_name = "\""+strCook_place_name+"\"";
					if(strCook_place_kor_area_a.indexOf(",")>-1) strCook_place_kor_area_a = "\""+strCook_place_kor_area_a+"\"";
					if(strCook_place_kor_area_b.indexOf(",")>-1) strCook_place_kor_area_b = "\""+strCook_place_kor_area_b+"\"";
					if(strCook_place_oth_area_a.indexOf(",")>-1) strCook_place_oth_area_a = "\""+strCook_place_oth_area_a+"\"";
					if(strCook_place_oth_area_b.indexOf(",")>-1) strCook_place_oth_area_b = "\""+strCook_place_oth_area_b+"\"";
					if(strCook_good_season.indexOf(",")>-1) strCook_good_season = "\""+strCook_good_season+"\"";
					if(strCook_good_day.indexOf(",")>-1) strCook_good_day = "\""+strCook_good_day+"\"";
					if(strCook_purpose.indexOf(",")>-1) strCook_purpose = "\""+strCook_purpose+"\"";
					if(strCook_flavor.indexOf(",")>-1) strCook_flavor = "\""+strCook_flavor+"\"";
					if(strCook_cooker.indexOf(",")>-1) strCook_cooker = "\""+strCook_cooker+"\"";
					if(strCook_shoot_place.indexOf(",")>-1) strCook_shoot_place = "\""+strCook_shoot_place+"\"";
					if(strCook_level_a.indexOf(",")>-1) strCook_level_a = "\""+strCook_level_a+"\"";
					if(strCook_level_b.indexOf(",")>-1) strCook_level_b = "\""+strCook_level_b+"\"";
					if(strCook_level_c.indexOf(",")>-1) strCook_level_c = "\""+strCook_level_c+"\"";
					if(strCook_time.indexOf(",")>-1) strCook_time = "\""+strCook_time+"\"";
					if(strCook_nutrient.indexOf(",")>-1) strCook_nutrient = "\""+strCook_nutrient+"\"";
					if(strCook_ea.indexOf(",")>-1) strCook_ea = "\""+strCook_ea+"\"";
					if(strCook_etc_a.indexOf(",")>-1) strCook_etc_a = "\""+strCook_etc_a+"\"";
					if(strCook_etc_b.indexOf(",")>-1) strCook_etc_b = "\""+strCook_etc_b+"\"";
					
					resultStr += 
							(strRegweek + seperator +
							strStat + seperator +
							strTitle + seperator +
							strPart + seperator +
							strAssetid + seperator +
							strType + seperator +
							strCook_title_a + seperator +
							strCook_title_b + seperator +
							strCook_parts + seperator +
							strCook_parts_main + seperator +
							strCook_parts_sub + seperator +
							strCook_type_a + seperator +
							strCook_type_b + seperator +
							strCook_chef_a + seperator +
							strCook_chef_b + seperator +
							strCook_kor_area_a + seperator +
							strCook_kor_area_b + seperator +
							strCook_oth_area_a + seperator +
							strCook_oth_area_b + seperator +
							strCook_place_name + seperator +
							strCook_place_kor_area_a + seperator +
							strCook_place_kor_area_b + seperator +
							strCook_place_oth_area_a + seperator +
							strCook_place_oth_area_b + seperator +
							strCook_good_season + seperator +
							strCook_good_day + seperator +
							strCook_purpose + seperator +
							strCook_flavor + seperator +
							strCook_cooker + seperator +
							strCook_shoot_place + seperator +
							strCook_level_a + seperator +
							strCook_level_b + seperator +
							strCook_level_c + seperator +
							strCook_time + seperator +
							strCook_nutrient + seperator +
							strCook_ea + seperator +
							strCook_etc_a + seperator +
							strCook_etc_b).replace("\r", "") + lineFeed;
				}else if(relKnowledge.getRelKnowledgeType().equals("curr")) {
//					String strRegweek = item.getRegweek();
//					String strStat = item.getStat();
//					String strTitle = item.getTitle();
//					String strPart = item.getPart();
//					String strAssetid = item.getAssetid();
//					String strCurr_subject = item.getCurr_subject();
//					String strCurr_person = item.getCurr_person();
//					String strCurr_incident = item.getCurr_incident();
//					String strCurr_product = item.getCurr_product();
//					String strCurr_process = item.getCurr_process();
//					String strCurr_type = item.getCurr_type();
//					String strCurr_mc = item.getCurr_mc();
//					String strCurr_actor = item.getCurr_actor();
//					String strCurr_keyword = item.getCurr_keyword();
//					String strCur_etc = item.getCur_etc();
					String strRegweek = StringUtil.nvl(item.getRegweek(), "");
					String strStat = StringUtil.nvl(item.getStat(), "");
					String strTitle = StringUtil.nvl(item.getTitle(), "");
					String strPart = StringUtil.nvl(item.getPart(), "");
					String strAssetid = StringUtil.nvl(item.getAssetid(), "");
					String strCurr_subject = StringUtil.nvl(item.getCurr_subject(), "");
					String strCurr_person = StringUtil.nvl(item.getCurr_person(), "");
					String strCurr_incident = StringUtil.nvl(item.getCurr_incident(), "");
					String strCurr_product = StringUtil.nvl(item.getCurr_product(), "");
					String strCurr_process = StringUtil.nvl(item.getCurr_process(), "");
					String strCurr_type = StringUtil.nvl(item.getCurr_type(), "");
					String strCurr_mc = StringUtil.nvl(item.getCurr_mc(), "");
					String strCurr_actor = StringUtil.nvl(item.getCurr_actor(), "");
					String strCurr_keyword = StringUtil.nvl(item.getCurr_keyword(), "");
					String strCur_etc = StringUtil.nvl(item.getCur_etc(), "");
					
					if(strRegweek.indexOf(",")>-1) strRegweek = "\""+strRegweek+"\"";
					if(strStat.indexOf(",")>-1) strStat = "\""+strStat+"\"";
					if(strTitle.indexOf(",")>-1) strTitle = "\""+strTitle+"\"";
					if(strPart.indexOf(",")>-1) strPart = "\""+strPart+"\"";
					if(strAssetid.indexOf(",")>-1) strAssetid = "\""+strAssetid+"\"";
					if(strCurr_subject.indexOf(",")>-1) strCurr_subject = "\""+strCurr_subject+"\"";
					if(strCurr_person.indexOf(",")>-1) strCurr_person = "\""+strCurr_person+"\"";
					if(strCurr_incident.indexOf(",")>-1) strCurr_incident = "\""+strCurr_incident+"\"";
					if(strCurr_product.indexOf(",")>-1) strCurr_product = "\""+strCurr_product+"\"";
					if(strCurr_process.indexOf(",")>-1) strCurr_process = "\""+strCurr_process+"\"";
					if(strCurr_type.indexOf(",")>-1) strCurr_type = "\""+strCurr_type+"\"";
					if(strCurr_mc.indexOf(",")>-1) strCurr_mc = "\""+strCurr_mc+"\"";
					if(strCurr_actor.indexOf(",")>-1) strCurr_actor = "\""+strCurr_actor+"\"";
					if(strCurr_keyword.indexOf(",")>-1) strCurr_keyword = "\""+strCurr_keyword+"\"";
					if(strCur_etc.indexOf(",")>-1) strCur_etc = "\""+strCur_etc+"\"";
					
					resultStr += 
							(strRegweek + seperator +
							strStat + seperator +
							strTitle + seperator +
							strPart + seperator +
							strAssetid + seperator +
							strCurr_subject + seperator +
							strCurr_person + seperator +
							strCurr_incident + seperator +
							strCurr_product + seperator +
							strCurr_process + seperator +
							strCurr_type + seperator +
							strCurr_mc + seperator +
							strCurr_actor + seperator +
							strCurr_keyword + seperator +
							strCur_etc).replace("\r", "") + lineFeed;
				}else if(relKnowledge.getRelKnowledgeType().equals("docu")) {
//					String strRegweek = item.getRegweek();
//					String strStat = item.getStat();
//					String strTitle = item.getTitle();
//					String strPart = item.getPart();
//					String strAssetid = item.getAssetid();
//					String strDocu_subject = item.getDocu_subject();
//					String strDocu_person = item.getDocu_person();
//					String strDocu_incident = item.getDocu_incident();
//					String strDocu_area = item.getDocu_area();
//					String strDocu_mc = item.getDocu_mc();
//					String strDocu_actor = item.getDocu_actor();
//					String strDocu_keyword = item.getDocu_keyword();
//					String strDocu_etc = item.getDocu_etc();
					String strRegweek = StringUtil.nvl(item.getRegweek(), "");
					String strStat = StringUtil.nvl(item.getStat(), "");
					String strTitle = StringUtil.nvl(item.getTitle(), "");
					String strPart = StringUtil.nvl(item.getPart(), "");
					String strAssetid = StringUtil.nvl(item.getAssetid(), "");
					String strDocu_subject = StringUtil.nvl(item.getDocu_subject(), "");
					String strDocu_person = StringUtil.nvl(item.getDocu_person(), "");
					String strDocu_incident = StringUtil.nvl(item.getDocu_incident(), "");
					String strDocu_area = StringUtil.nvl(item.getDocu_area(), "");
					String strDocu_mc = StringUtil.nvl(item.getDocu_mc(), "");
					String strDocu_actor = StringUtil.nvl(item.getDocu_actor(), "");
					String strDocu_keyword = StringUtil.nvl(item.getDocu_keyword(), "");
					String strDocu_etc = StringUtil.nvl(item.getDocu_etc(), "");
					
					if(strRegweek.indexOf(",")>-1) strRegweek = "\""+strRegweek+"\"";
					if(strStat.indexOf(",")>-1) strStat = "\""+strStat+"\"";
					if(strTitle.indexOf(",")>-1) strTitle = "\""+strTitle+"\"";
					if(strPart.indexOf(",")>-1) strPart = "\""+strPart+"\"";
					if(strAssetid.indexOf(",")>-1) strAssetid = "\""+strAssetid+"\"";
					if(strDocu_subject.indexOf(",")>-1) strDocu_subject = "\""+strDocu_subject+"\"";
					if(strDocu_person.indexOf(",")>-1) strDocu_person = "\""+strDocu_person+"\"";
					if(strDocu_incident.indexOf(",")>-1) strDocu_incident = "\""+strDocu_incident+"\"";
					if(strDocu_area.indexOf(",")>-1) strDocu_area = "\""+strDocu_area+"\"";
					if(strDocu_mc.indexOf(",")>-1) strDocu_mc = "\""+strDocu_mc+"\"";
					if(strDocu_actor.indexOf(",")>-1) strDocu_actor = "\""+strDocu_actor+"\"";
					if(strDocu_keyword.indexOf(",")>-1) strDocu_keyword = "\""+strDocu_keyword+"\"";
					if(strDocu_etc.indexOf(",")>-1) strDocu_etc = "\""+strDocu_etc+"\"";
					
					resultStr += 
							(strRegweek + seperator +
							strStat + seperator +
							strTitle + seperator +
							strPart + seperator +
							strAssetid + seperator +
							strDocu_subject + seperator +
							strDocu_person + seperator +
							strDocu_incident + seperator +
							strDocu_area + seperator +
							strDocu_mc + seperator +
							strDocu_actor + seperator +
							strDocu_keyword + seperator +
							strDocu_etc).replace("\r", "") + lineFeed;
				}else if(relKnowledge.getRelKnowledgeType().equals("heal")) {
//					String strRegweek = item.getRegweek();
//					String strStat = item.getStat();
//					String strTitle = item.getTitle();
//					String strPart = item.getPart();
//					String strAssetid = item.getAssetid();
//					String strHeal_subject = item.getHeal_subject();
//					String strHeal_part = item.getHeal_part();
//					String strHeal_symtoms = item.getHeal_symtoms();
//					String strHeal_cause_a = item.getHeal_cause_a();
//					String strHeal_cause_b = item.getHeal_cause_b();
//					String strHeal_cause_c = item.getHeal_cause_c();
//					String strHeal_diagnose = item.getHeal_diagnose();
//					String strHeal_treat_type = item.getHeal_treat_type();
//					String strHeal_prevention = item.getHeal_prevention();
//					String strHeal_drug = item.getHeal_drug();
//					String strHeal_treat_name = item.getHeal_treat_name();
//					String strHeal_hospital = item.getHeal_hospital();
//					String strHeal_doctor = item.getHeal_doctor();
//					String strHeal_talker = item.getHeal_talker();
//					String strHeal_actor = item.getHeal_actor();
//					String strHeal_expert = item.getHeal_expert();
//					String strHeal_public = item.getHeal_public();
//					String strHeal_course = item.getHeal_course();
//					String strHeal_activity = item.getHeal_activity();
//					String strHeal_ingredient = item.getHeal_ingredient();
//					String strHeal_food_a = item.getHeal_food_a();
//					String strHeal_food_b = item.getHeal_food_b();
//					String strHeal_activity_part = item.getHeal_activity_part();
//					String strHeal_activity_name = item.getHeal_activity_name();
//					String strHeal_season = item.getHeal_season();
//					String strHeal_target = item.getHeal_target();
//					String strHeal_keyword = item.getHeal_keyword();
//					String strHeal_etc = item.getHeal_etc();
					String strRegweek = StringUtil.nvl(item.getRegweek(), "");
					String strStat = StringUtil.nvl(item.getStat(), "");
					String strTitle = StringUtil.nvl(item.getTitle(), "");
					String strPart = StringUtil.nvl(item.getPart(), "");
					String strAssetid = StringUtil.nvl(item.getAssetid(), "");
					String strHeal_subject = StringUtil.nvl(item.getHeal_subject(), "");
					String strHeal_part = StringUtil.nvl(item.getHeal_part(), "");
					String strHeal_symtoms = StringUtil.nvl(item.getHeal_symtoms(), "");
					String strHeal_cause_a = StringUtil.nvl(item.getHeal_cause_a(), "");
					String strHeal_cause_b = StringUtil.nvl(item.getHeal_cause_b(), "");
					String strHeal_cause_c = StringUtil.nvl(item.getHeal_cause_c(), "");
					String strHeal_diagnose = StringUtil.nvl(item.getHeal_diagnose(), "");
					String strHeal_treat_type = StringUtil.nvl(item.getHeal_treat_type(), "");
					String strHeal_prevention = StringUtil.nvl(item.getHeal_prevention(), "");
					String strHeal_drug = StringUtil.nvl(item.getHeal_drug(), "");
					String strHeal_treat_name = StringUtil.nvl(item.getHeal_treat_name(), "");
					String strHeal_hospital = StringUtil.nvl(item.getHeal_hospital(), "");
					String strHeal_doctor = StringUtil.nvl(item.getHeal_doctor(), "");
					String strHeal_talker = StringUtil.nvl(item.getHeal_talker(), "");
					String strHeal_actor = StringUtil.nvl(item.getHeal_actor(), "");
					String strHeal_expert = StringUtil.nvl(item.getHeal_expert(), "");
					String strHeal_public = StringUtil.nvl(item.getHeal_public(), "");
					String strHeal_course = StringUtil.nvl(item.getHeal_course(), "");
					String strHeal_activity = StringUtil.nvl(item.getHeal_activity(), "");
					String strHeal_ingredient = StringUtil.nvl(item.getHeal_ingredient(), "");
					String strHeal_food_a = StringUtil.nvl(item.getHeal_food_a(), "");
					String strHeal_food_b = StringUtil.nvl(item.getHeal_food_b(), "");
					String strHeal_activity_part = StringUtil.nvl(item.getHeal_activity_part(), "");
					String strHeal_activity_name = StringUtil.nvl(item.getHeal_activity_name(), "");
					String strHeal_season = StringUtil.nvl(item.getHeal_season(), "");
					String strHeal_target = StringUtil.nvl(item.getHeal_target(), "");
					String strHeal_keyword = StringUtil.nvl(item.getHeal_keyword(), "");
					String strHeal_etc = StringUtil.nvl(item.getHeal_etc(), "");
					
					if(strRegweek.indexOf(",")>-1) strRegweek = "\""+strRegweek+"\"";
					if(strStat.indexOf(",")>-1) strStat = "\""+strStat+"\"";
					if(strTitle.indexOf(",")>-1) strTitle = "\""+strTitle+"\"";
					if(strPart.indexOf(",")>-1) strPart = "\""+strPart+"\"";
					if(strAssetid.indexOf(",")>-1) strAssetid = "\""+strAssetid+"\"";
					if(strHeal_subject.indexOf(",")>-1) strHeal_subject = "\""+strHeal_subject+"\"";
					if(strHeal_part.indexOf(",")>-1) strHeal_part = "\""+strHeal_part+"\"";
					if(strHeal_symtoms.indexOf(",")>-1) strHeal_symtoms = "\""+strHeal_symtoms+"\"";
					if(strHeal_cause_a.indexOf(",")>-1) strHeal_cause_a = "\""+strHeal_cause_a+"\"";
					if(strHeal_cause_b.indexOf(",")>-1) strHeal_cause_b = "\""+strHeal_cause_b+"\"";
					if(strHeal_cause_c.indexOf(",")>-1) strHeal_cause_c = "\""+strHeal_cause_c+"\"";
					if(strHeal_diagnose.indexOf(",")>-1) strHeal_diagnose = "\""+strHeal_diagnose+"\"";
					if(strHeal_treat_type.indexOf(",")>-1) strHeal_treat_type = "\""+strHeal_treat_type+"\"";
					if(strHeal_prevention.indexOf(",")>-1) strHeal_prevention = "\""+strHeal_prevention+"\"";
					if(strHeal_drug.indexOf(",")>-1) strHeal_drug = "\""+strHeal_drug+"\"";
					if(strHeal_treat_name.indexOf(",")>-1) strHeal_treat_name = "\""+strHeal_treat_name+"\"";
					if(strHeal_hospital.indexOf(",")>-1) strHeal_hospital = "\""+strHeal_hospital+"\"";
					if(strHeal_doctor.indexOf(",")>-1) strHeal_doctor = "\""+strHeal_doctor+"\"";
					if(strHeal_talker.indexOf(",")>-1) strHeal_talker = "\""+strHeal_talker+"\"";
					if(strHeal_actor.indexOf(",")>-1) strHeal_actor = "\""+strHeal_actor+"\"";
					if(strHeal_expert.indexOf(",")>-1) strHeal_expert = "\""+strHeal_expert+"\"";
					if(strHeal_public.indexOf(",")>-1) strHeal_public = "\""+strHeal_public+"\"";
					if(strHeal_course.indexOf(",")>-1) strHeal_course = "\""+strHeal_course+"\"";
					if(strHeal_activity.indexOf(",")>-1) strHeal_activity = "\""+strHeal_activity+"\"";
					if(strHeal_ingredient.indexOf(",")>-1) strHeal_ingredient = "\""+strHeal_ingredient+"\"";
					if(strHeal_food_a.indexOf(",")>-1) strHeal_food_a = "\""+strHeal_food_a+"\"";
					if(strHeal_food_b.indexOf(",")>-1) strHeal_food_b = "\""+strHeal_food_b+"\"";
					if(strHeal_activity_part.indexOf(",")>-1) strHeal_activity_part = "\""+strHeal_activity_part+"\"";
					if(strHeal_activity_name.indexOf(",")>-1) strHeal_activity_name = "\""+strHeal_activity_name+"\"";
					if(strHeal_season.indexOf(",")>-1) strHeal_season = "\""+strHeal_season+"\"";
					if(strHeal_target.indexOf(",")>-1) strHeal_target = "\""+strHeal_target+"\"";
					if(strHeal_keyword.indexOf(",")>-1) strHeal_keyword = "\""+strHeal_keyword+"\"";
					if(strHeal_etc.indexOf(",")>-1) strHeal_etc = "\""+strHeal_etc+"\"";
					
					resultStr += 
							(strRegweek + seperator +
							strStat + seperator +
							strTitle + seperator +
							strPart + seperator +
							strAssetid + seperator +
							strHeal_subject + seperator +
							strHeal_part + seperator +
							strHeal_symtoms + seperator +
							strHeal_cause_a + seperator +
							strHeal_cause_b + seperator +
							strHeal_cause_c + seperator +
							strHeal_diagnose + seperator +
							strHeal_treat_type + seperator +
							strHeal_prevention + seperator +
							strHeal_drug + seperator +
							strHeal_treat_name + seperator +
							strHeal_hospital + seperator +
							strHeal_doctor + seperator +
							strHeal_talker + seperator +
							strHeal_actor + seperator +
							strHeal_expert + seperator +
							strHeal_public + seperator +
							strHeal_course + seperator +
							strHeal_activity + seperator +
							strHeal_ingredient + seperator +
							strHeal_food_a + seperator +
							strHeal_food_b + seperator +
							strHeal_activity_part + seperator +
							strHeal_activity_name + seperator +
							strHeal_season + seperator +
							strHeal_target + seperator +
							strHeal_keyword + seperator +
							strHeal_etc).replace("\r", "") + lineFeed;
				}else if(relKnowledge.getRelKnowledgeType().equals("hist")) {
//					String strRegweek = item.getRegweek();
//					String strStat = item.getStat();
//					String strProgram = item.getProgram();
//					String strTitle = item.getTitle();
//					String strAssetid = item.getAssetid();
//					String strHist_subject = item.getHist_subject();
//					String strHist_area_a = item.getHist_area_a();
//					String strHist_area_b = item.getHist_area_b();
//					String strHist_year_a = item.getHist_year_a();
//					String strHist_year_b = item.getHist_year_b();
//					String strHist_person = item.getHist_person();
//					String strHist_feat = item.getHist_feat();
//					String strHist_incident = item.getHist_incident();
//					String strHist_thing = item.getHist_thing();
//					String strHist_area_c = item.getHist_area_c();
//					String strHist_actor = item.getHist_actor();
//					String strHist_etc = item.getHist_etc();
					String strRegweek = StringUtil.nvl(item.getRegweek(), "");
					String strStat = StringUtil.nvl(item.getStat(), "");
					String strProgram = StringUtil.nvl(item.getProgram(), "");
					String strTitle = StringUtil.nvl(item.getTitle(), "");
					String strAssetid = StringUtil.nvl(item.getAssetid(), "");
					String strHist_subject = StringUtil.nvl(item.getHist_subject(), "");
					String strHist_area_a = StringUtil.nvl(item.getHist_area_a(), "");
					String strHist_area_b = StringUtil.nvl(item.getHist_area_b(), "");
					String strHist_year_a = StringUtil.nvl(item.getHist_year_a(), "");
					String strHist_year_b = StringUtil.nvl(item.getHist_year_b(), "");
					String strHist_person = StringUtil.nvl(item.getHist_person(), "");
					String strHist_feat = StringUtil.nvl(item.getHist_feat(), "");
					String strHist_incident = StringUtil.nvl(item.getHist_incident(), "");
					String strHist_thing = StringUtil.nvl(item.getHist_thing(), "");
					String strHist_area_c = StringUtil.nvl(item.getHist_area_c(), "");
					String strHist_actor = StringUtil.nvl(item.getHist_actor(), "");
					String strHist_etc = StringUtil.nvl(item.getHist_etc(), "");
					
					if(strRegweek.indexOf(",")>-1) strRegweek = "\""+strRegweek+"\"";
					if(strStat.indexOf(",")>-1) strStat = "\""+strStat+"\"";
					if(strProgram.indexOf(",")>-1) strProgram = "\""+strProgram+"\"";
					if(strTitle.indexOf(",")>-1) strTitle = "\""+strTitle+"\"";
					if(strAssetid.indexOf(",")>-1) strAssetid = "\""+strAssetid+"\"";
					if(strHist_subject.indexOf(",")>-1) strHist_subject = "\""+strHist_subject+"\"";
					if(strHist_area_a.indexOf(",")>-1) strHist_area_a = "\""+strHist_area_a+"\"";
					if(strHist_area_b.indexOf(",")>-1) strHist_area_b = "\""+strHist_area_b+"\"";
					if(strHist_year_a.indexOf(",")>-1) strHist_year_a = "\""+strHist_year_a+"\"";
					if(strHist_year_b.indexOf(",")>-1) strHist_year_b = "\""+strHist_year_b+"\"";
					if(strHist_person.indexOf(",")>-1) strHist_person = "\""+strHist_person+"\"";
					if(strHist_feat.indexOf(",")>-1) strHist_feat = "\""+strHist_feat+"\"";
					if(strHist_incident.indexOf(",")>-1) strHist_incident = "\""+strHist_incident+"\"";
					if(strHist_thing.indexOf(",")>-1) strHist_thing = "\""+strHist_thing+"\"";
					if(strHist_area_c.indexOf(",")>-1) strHist_area_c = "\""+strHist_area_c+"\"";
					if(strHist_actor.indexOf(",")>-1) strHist_actor = "\""+strHist_actor+"\"";
					if(strHist_etc.indexOf(",")>-1) strHist_etc = "\""+strHist_etc+"\"";
					
					resultStr += 
							(strRegweek + seperator +
							strStat + seperator +
							strProgram + seperator +
							strTitle + seperator +
							strAssetid + seperator +
							strHist_subject + seperator +
							strHist_area_a + seperator +
							strHist_area_b + seperator +
							strHist_year_a + seperator +
							strHist_year_b + seperator +
							strHist_person + seperator +
							strHist_feat + seperator +
							strHist_incident + seperator +
							strHist_thing + seperator +
							strHist_area_c + seperator +
							strHist_actor + seperator +
							strHist_etc).replace("\r", "") + lineFeed;
				}else if(relKnowledge.getRelKnowledgeType().equals("tour")) {
//					String strRegweek = item.getRegweek();
//					String strStat = item.getStat();
//					String strTitle = item.getTitle();
//					String strPart = item.getPart();
//					String strAssetid = item.getAssetid();
//					String strType = item.getType();
//					String strTour_theme = item.getTour_theme();
//					String strTour_area_a = item.getTour_area_a();
//					String strTour_area_b = item.getTour_area_b();
//					String strTour_area_c = item.getTour_area_c();
//					String strTour_area_d = item.getTour_area_d();
//					String strTour_area_e = item.getTour_area_e();
//					String strTour_natural = item.getTour_natural();
//					String strTour_nation = item.getTour_nation();
//					String strTour_person = item.getTour_person();
//					String strTour_festival = item.getTour_festival();
//					String strTour_type_a = item.getTour_type_a();
//					String strTour_type_b = item.getTour_type_b();
//					String strTour_type_c = item.getTour_type_c();
//					String strTour_season = item.getTour_season();
//					String strTour_human = item.getTour_human();
//					String strTour_tourist = item.getTour_tourist();
//					String strTour_mc_yn = item.getTour_mc_yn();
//					String strTour_mc = item.getTour_mc();
//					String strTour_food = item.getTour_food();
//					String strTour_type_d = item.getTour_type_d();
//					String strTour_festival_name = item.getTour_festival_name();
//					String strTour_trans = item.getTour_trans();
//					String strTour_peaple = item.getTour_peaple();
//					String strTour_weather = item.getTour_weather();
//					String strTour_music = item.getTour_music();
//					String strTour_period = item.getTour_period();
//					String strTour_companion = item.getTour_companion();
//					String strTour_etc_a = item.getTour_etc_a();
//					String strTour_etc_b = item.getTour_etc_b();
					String strRegweek = StringUtil.nvl(item.getRegweek(), "");
					String strStat = StringUtil.nvl(item.getStat(), "");
					String strTitle = StringUtil.nvl(item.getTitle(), "");
					String strPart = StringUtil.nvl(item.getPart(), "");
					String strAssetid = StringUtil.nvl(item.getAssetid(), "");
					String strType = StringUtil.nvl(item.getType(), "");
					String strTour_theme = StringUtil.nvl(item.getTour_theme(), "");
					String strTour_area_a = StringUtil.nvl(item.getTour_area_a(), "");
					String strTour_area_b = StringUtil.nvl(item.getTour_area_b(), "");
					String strTour_area_c = StringUtil.nvl(item.getTour_area_c(), "");
					String strTour_area_d = StringUtil.nvl(item.getTour_area_d(), "");
					String strTour_area_e = StringUtil.nvl(item.getTour_area_e(), "");
					String strTour_natural = StringUtil.nvl(item.getTour_natural(), "");
					String strTour_nation = StringUtil.nvl(item.getTour_nation(), "");
					String strTour_person = StringUtil.nvl(item.getTour_person(), "");
					String strTour_festival = StringUtil.nvl(item.getTour_festival(), "");
					String strTour_type_a = StringUtil.nvl(item.getTour_type_a(), "");
					String strTour_type_b = StringUtil.nvl(item.getTour_type_b(), "");
					String strTour_type_c = StringUtil.nvl(item.getTour_type_c(), "");
					String strTour_season = StringUtil.nvl(item.getTour_season(), "");
					String strTour_human = StringUtil.nvl(item.getTour_human(), "");
					String strTour_tourist = StringUtil.nvl(item.getTour_tourist(), "");
					String strTour_mc_yn = StringUtil.nvl(item.getTour_mc_yn(), "");
					String strTour_mc = StringUtil.nvl(item.getTour_mc(), "");
					String strTour_food = StringUtil.nvl(item.getTour_food(), "");
					String strTour_type_d = StringUtil.nvl(item.getTour_type_d(), "");
					String strTour_festival_name = StringUtil.nvl(item.getTour_festival_name(), "");
					String strTour_trans = StringUtil.nvl(item.getTour_trans(), "");
					String strTour_peaple = StringUtil.nvl(item.getTour_peaple(), "");
					String strTour_weather = StringUtil.nvl(item.getTour_weather(), "");
					String strTour_music = StringUtil.nvl(item.getTour_music(), "");
					String strTour_period = StringUtil.nvl(item.getTour_period(), "");
					String strTour_companion = StringUtil.nvl(item.getTour_companion(), "");
					String strTour_etc_a = StringUtil.nvl(item.getTour_etc_a(), "");
					String strTour_etc_b = StringUtil.nvl(item.getTour_etc_b(), "");
					
					if(strRegweek.indexOf(",")>-1) strRegweek = "\""+strRegweek+"\"";
					if(strStat.indexOf(",")>-1) strStat = "\""+strStat+"\"";
					if(strTitle.indexOf(",")>-1) strTitle = "\""+strTitle+"\"";
					if(strPart.indexOf(",")>-1) strPart = "\""+strPart+"\"";
					if(strAssetid.indexOf(",")>-1) strAssetid = "\""+strAssetid+"\"";
					if(strType.indexOf(",")>-1) strType = "\""+strType+"\"";
					if(strTour_theme.indexOf(",")>-1) strTour_theme = "\""+strTour_theme+"\"";
					if(strTour_area_a.indexOf(",")>-1) strTour_area_a = "\""+strTour_area_a+"\"";
					if(strTour_area_b.indexOf(",")>-1) strTour_area_b = "\""+strTour_area_b+"\"";
					if(strTour_area_c.indexOf(",")>-1) strTour_area_c = "\""+strTour_area_c+"\"";
					if(strTour_area_d.indexOf(",")>-1) strTour_area_d = "\""+strTour_area_d+"\"";
					if(strTour_area_e.indexOf(",")>-1) strTour_area_e = "\""+strTour_area_e+"\"";
					if(strTour_natural.indexOf(",")>-1) strTour_natural = "\""+strTour_natural+"\"";
					if(strTour_nation.indexOf(",")>-1) strTour_nation = "\""+strTour_nation+"\"";
					if(strTour_person.indexOf(",")>-1) strTour_person = "\""+strTour_person+"\"";
					if(strTour_festival.indexOf(",")>-1) strTour_festival = "\""+strTour_festival+"\"";
					if(strTour_type_a.indexOf(",")>-1) strTour_type_a = "\""+strTour_type_a+"\"";
					if(strTour_type_b.indexOf(",")>-1) strTour_type_b = "\""+strTour_type_b+"\"";
					if(strTour_type_c.indexOf(",")>-1) strTour_type_c = "\""+strTour_type_c+"\"";
					if(strTour_season.indexOf(",")>-1) strTour_season = "\""+strTour_season+"\"";
					if(strTour_human.indexOf(",")>-1) strTour_human = "\""+strTour_human+"\"";
					if(strTour_tourist.indexOf(",")>-1) strTour_tourist = "\""+strTour_tourist+"\"";
					if(strTour_mc_yn.indexOf(",")>-1) strTour_mc_yn = "\""+strTour_mc_yn+"\"";
					if(strTour_mc.indexOf(",")>-1) strTour_mc = "\""+strTour_mc+"\"";
					if(strTour_food.indexOf(",")>-1) strTour_food = "\""+strTour_food+"\"";
					if(strTour_type_d.indexOf(",")>-1) strTour_type_d = "\""+strTour_type_d+"\"";
					if(strTour_festival_name.indexOf(",")>-1) strTour_festival_name = "\""+strTour_festival_name+"\"";
					if(strTour_trans.indexOf(",")>-1) strTour_trans = "\""+strTour_trans+"\"";
					if(strTour_peaple.indexOf(",")>-1) strTour_peaple = "\""+strTour_peaple+"\"";
					if(strTour_weather.indexOf(",")>-1) strTour_weather = "\""+strTour_weather+"\"";
					if(strTour_music.indexOf(",")>-1) strTour_music = "\""+strTour_music+"\"";
					if(strTour_period.indexOf(",")>-1) strTour_period = "\""+strTour_period+"\"";
					if(strTour_companion.indexOf(",")>-1) strTour_companion = "\""+strTour_companion+"\"";
					if(strTour_etc_a.indexOf(",")>-1) strTour_etc_a = "\""+strTour_etc_a+"\"";
					if(strTour_etc_b.indexOf(",")>-1) strTour_etc_b = "\""+strTour_etc_b+"\"";
					
					resultStr += 
							(strRegweek + seperator +
							strStat + seperator +
							strTitle + seperator +
							strPart + seperator +
							strAssetid + seperator +
							strType + seperator +
							strTour_theme + seperator +
							strTour_area_a + seperator +
							strTour_area_b + seperator +
							strTour_area_c + seperator +
							strTour_area_d + seperator +
							strTour_area_e + seperator +
							strTour_natural + seperator +
							strTour_nation + seperator +
							strTour_person + seperator +
							strTour_festival + seperator +
							strTour_type_a + seperator +
							strTour_type_b + seperator +
							strTour_type_c + seperator +
							strTour_season + seperator +
							strTour_human + seperator +
							strTour_tourist + seperator +
							strTour_mc_yn + seperator +
							strTour_mc + seperator +
							strTour_food + seperator +
							strTour_type_d + seperator +
							strTour_festival_name + seperator +
							strTour_trans + seperator +
							strTour_peaple + seperator +
							strTour_weather + seperator +
							strTour_music + seperator +
							strTour_period + seperator +
							strTour_companion + seperator +
							strTour_etc_a + seperator +
							strTour_etc_b).replace("\r", "") + lineFeed;
				}else {
					
				}
			}
			
			
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4. File");
			//5. 파일형태로 표출
			//String fileNameContent = "VOD_RT_"+type.toUpperCase()+"_"+DateUtil.formatDate(new Date(), "yyyyMMdd")+".csv";
			String fileNameContent = "VOD_RT_"+type.toUpperCase()+".csv";
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.1");
			int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");
			
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.2");
			String strSeparator = (UPLOAD_DIR.substring(UPLOAD_DIR.length()-1).equals(File.separator) ? "" : File.separator);
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " 4.3");
			strFileName = UPLOAD_DIR + strSeparator + fileNameContent;
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " strFileName = " + strFileName);
			logger.debug("[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.makeFileRelKnowledge 끝(리턴)\n--------\n\n");
			//return strFileName;
			
			System.out.println("strFileName = " + strFileName);
	    	
	    	
	    	//파일 정보를 저장 (파일명 + 생성시각 timestamp)
	    	fileNameContent = "DIC_KEYWORDS_"+type.toUpperCase();
	    	DicKeywords fileInfo = new DicKeywords();
	    	fileInfo.setFilePath(fileNameContent);
	    	
	    	int iFile = dicKeywordsMapper.updateCsvFileInfo(fileInfo);
		}
	}

	public void pushCsvToRelKnowledge() {
    	Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        
		String[] types = {"COOK","CURR","DOCU","HEAL","HIST","TOUR"};
		String strFilePath = "";
		
		for(String type : types) {
			//test
			//strFilePath = UPLOAD_DIR + "testRelKnowledge" + type + ".csv";			
			
			strFilePath = UPLOAD_DIR + "csv_import" + File.separator + "VOD_RT_" + type + ".csv";
			File file = new File(strFilePath);
			
			if(file.exists()) {
				RelKnowledge relKnowledge = new RelKnowledge();
				relKnowledge.setRelKnowledgeType(type);	//type라는 이름의 컬럼이 있으므로
				relKnowledge.setRelKnowledgeFilePath(strFilePath);
				
		    	//업로드하는 카테고리 데이터 모두 삭제
		    	//ApoController.delDicKeywordsAllByType - DicService.delDicKeywordsAllByType - dicKeywordsMapper.delDicKeywordsAllByType(dicKeywords)
				int rtins1 = this.delRelKnowledgesByType(relKnowledge);
				
				//csv 파일을 임포트
				int rtins2 = this.importRelKnowledgesByType(relKnowledge);
				
				//csv 파일을 이름변경
				File fileNew = new File(strFilePath + "_" + format.format(new Date()));
				file.renameTo(fileNew);
				
				//빈 데이터 정리
				//int rtins3 = dicKeywordsMapper.cleanBlankDicKeywords(dicKeywords);
			}
		
			
		}
	}

	@Override
	public Map<String, Object> getJsonArrayFromRelKnowledge() {
		Map<String,Object> result = new HashMap();
		String code = "";

		String json = "";
		List<Map<String,Object>> subList = null;

		code = "ASSOC_FOOD";
		subList = relKnowledgeMapper.getVodCook(null);
		result.put(code, subList);
		System.out.println("## getVodRT :: "+code+" :: "+subList.toString());

		code = "ASSOC_CURR";
		subList = relKnowledgeMapper.getVodCurr(null);
		result.put(code, subList);
		System.out.println("## getVodRT :: "+code+" :: "+subList.toString());

		code = "ASSOC_DOCU";
		subList = relKnowledgeMapper.getVodDocu(null);
		result.put(code, subList);
		System.out.println("## getVodRT :: "+code+" :: "+subList.toString());

		code = "ASSOC_HEAL";
		subList = relKnowledgeMapper.getVodHeal(null);
		result.put(code, subList);
		System.out.println("## getVodRT :: "+code+" :: "+subList.toString());

		code = "ASSOC_HIST";
		subList = relKnowledgeMapper.getVodHist(null);
		result.put(code, subList);
		System.out.println("## getVodRT :: "+code+" :: "+subList.toString());

		code = "ASSOC_TOUR";
		subList = relKnowledgeMapper.getVodTour(null);
		result.put(code, subList);
		System.out.println("## getVodRT :: "+code+" :: "+subList.toString());

		return result;
	}


}
