package com.kthcorp.cmts.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kthcorp.cmts.model.RelKnowledge;

import java.util.List;

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
    
    void makeFileRelKnowledge();
    void pushCsvToRelKnowledge();
}
