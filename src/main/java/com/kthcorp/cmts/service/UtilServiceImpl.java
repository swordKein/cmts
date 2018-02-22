package com.kthcorp.cmts.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@Service
public interface UtilServiceImpl {
    public String getStr();

    //int runJobTask(Object className, String method, MultipartFile uploadFile);

    int runJobTask(Object className, String method, Object paramObj);
}
