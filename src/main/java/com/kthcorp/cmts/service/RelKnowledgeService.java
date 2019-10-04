package com.kthcorp.cmts.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kthcorp.cmts.SpringBootWebApplication;
import com.kthcorp.cmts.mapper.RelKnowledgeMapper;
import com.kthcorp.cmts.model.RelKnowledge;
import com.kthcorp.cmts.util.pool.concurrent.mgr.GenericTaskThreadPoolExecutor;
import com.kthcorp.cmts.util.pool.concurrent.task.JobTask;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.GenericTaskArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RelKnowledgeService implements RelKnowledgeServiceImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RelKnowledgeMapper relKnowledgeMapper;

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
	public int addRelKnowledgesByType(String items) {
        int rt = 0;
        if (!"".equals(items)) {
            items = items.trim();

            JsonParser parser = new JsonParser();
            JsonArray itemsArr = (JsonArray) parser.parse(items);
            for (JsonElement je : itemsArr) {
                JsonObject jo = (JsonObject) je;
                System.out.println("#dicActions::"+jo.toString());
                
                RelKnowledge relKnowledge = new RelKnowledge();
                
                //공통
//                relKnowledge.setIdx(jo.get("idx") != null ? jo.get("idx").getAsString() : "");
                relKnowledge.setRegweek(jo.get("regweek") != null ? jo.get("regweek").getAsString() : "");
                relKnowledge.setStat(jo.get("stat") != null ? jo.get("stat").getAsString() : "");
                relKnowledge.setProgram(jo.get("program") != null ? jo.get("program").getAsString() : "");
                relKnowledge.setTitle(jo.get("title") != null ? jo.get("title").getAsString() : "");
                relKnowledge.setPart(jo.get("part") != null ? jo.get("part").getAsString() : "");
                relKnowledge.setAssetid(jo.get("assetid") != null ? jo.get("assetid").getAsString() : "");
                relKnowledge.setType(jo.get("type") != null ? jo.get("type").getAsString() : "");

                
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



}
