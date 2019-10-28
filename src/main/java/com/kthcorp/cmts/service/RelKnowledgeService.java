package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kthcorp.cmts.SpringBootWebApplication;
import com.kthcorp.cmts.mapper.RelKnowledgeMapper;
import com.kthcorp.cmts.model.RelKnowledge;
import com.kthcorp.cmts.util.FileUtils;
import com.kthcorp.cmts.util.pool.concurrent.mgr.GenericTaskThreadPoolExecutor;
import com.kthcorp.cmts.util.pool.concurrent.task.JobTask;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.GenericTaskArgument;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RelKnowledgeService implements RelKnowledgeServiceImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RelKnowledgeMapper relKnowledgeMapper;

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
		
		if(strRelKnowledgeType.equals("cook")) {
			intResult = relKnowledgeMapper.delRelKnowledgesCook(relKnowledge);
		}else if(strRelKnowledgeType.equals("curr")) {
			intResult = relKnowledgeMapper.delRelKnowledgesCurr(relKnowledge);
		}else if(strRelKnowledgeType.equals("docu")) {
			intResult = relKnowledgeMapper.delRelKnowledgesDocu(relKnowledge);
		}else if(strRelKnowledgeType.equals("heal")) {
			intResult = relKnowledgeMapper.delRelKnowledgesHeal(relKnowledge);
		}else if(strRelKnowledgeType.equals("hist")) {
			intResult = relKnowledgeMapper.delRelKnowledgesHist(relKnowledge);
		}else if(strRelKnowledgeType.equals("tour")) {
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
                	relKnowledge.setCook_etc_b(jo.get("col37") != null ? jo.get("col37").getAsString() : "");
                	
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
        
		System.out.println("\n\n--------\n[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.getRelKnowledgeListDownload 시작");
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " type = " + type);
		String strFileName = "";
		
		List<Map<String, Object>> reqItems = null;
		String resultStr = "";
		String lineFeed = System.getProperty("line.separator");
		String seperator = "\t";
		
		RelKnowledge relKnowledge = new RelKnowledge();
		relKnowledge.setRelKnowledgeType(type);
		
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " 1. Header");
		//1. 헤더
		//resultStr = "Type" + seperator + "Keyword" + lineFeed;
		if(relKnowledge.getRelKnowledgeType().equals("cook")) {
			resultStr = //38
					"작업일" + seperator + 
					"상태" + seperator + 
					"Title" + seperator + 
					"파트" + seperator + 
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
					"국내지역(도단위)" + seperator + 
					"국내지역(소단위)" + seperator + 
					"해외지역(국가)" + seperator + 
					"해외지역(세계구분)" + seperator + 
					"식당명" + seperator +
					
					"국내식당위치(도단위)" + seperator + 
					"국내식당위치(소단위)" + seperator + 
					"해외식당위치(해외지역)" + seperator + 
					"해외식당위치(국가)" + seperator + 
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
					"Title" + seperator + 
					"파트" + seperator + 
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
					"Title" + seperator + 
					"파트" + seperator + 
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
					"Title" + seperator + 
					"파트" + seperator + 
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
					"Title" + seperator + 
					"파트" + seperator + 
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
		
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " 2. Body");
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
		
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " 3. toString");
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
						strCook_etc_a + seperator +
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
		
		
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " 4. File");
		//5. 파일형태로 표출
		String fileNameContent = "VOD_RT_"+type.toUpperCase()+"_"+DateUtil.formatDate(new Date(), "yyyyMMdd")+".csv";
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " 4.1");
		int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");
		
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " 4.2");
		String strSeparator = (UPLOAD_DIR.substring(UPLOAD_DIR.length()-1).equals(File.separator) ? "" : File.separator);
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " 4.3");
		strFileName = UPLOAD_DIR + strSeparator + fileNameContent;
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " strFileName = " + strFileName);
		System.out.println("[파일업다운로드] " + format.format(new Date()) + " relKnowledgeService.getRelKnowledgeListDownload 끝(리턴)\n--------\n\n");
		return strFileName;
	}



}
