package com.kthcorp.cmts.service;

import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.RelKnowledge;

import java.util.Map;

//@Service
public interface RelKnowledgeServiceImpl {
    public String getStr();

    //int runJobTask(Object className, String method, MultipartFile uploadFile);

    int delRelKnowledgesByType(RelKnowledge relKnowledge);

    int addRelKnowledgesByType(RelKnowledge relKnowledge);
    
    public String getRelKnowledgeListDownload(String type);
    
    //2019.11.06
    String uploadRelknowledgeFile(String readString, String type);
    int importRelKnowledgesByType(RelKnowledge relKnowledge);
    
    void makeFileRelKnowledge() throws Exception;
    void pushCsvToRelKnowledge() throws Exception;

    Map<String, Object> getJsonArrayFromRelKnowledge();
    
    JsonObject getCsvFileName(String strType);	//2019.11.20
}
