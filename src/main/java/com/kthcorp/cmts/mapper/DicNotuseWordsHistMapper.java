package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicNotuseWordsHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicNotuseWordsHistMapper {
    List<DicNotuseWordsHist> getDicNotuseWordsHist();
    List<DicNotuseWordsHist> getDicNotuseWordsHistByReq(DicNotuseWordsHist req);

    int insDicNotuseWordsHist(DicNotuseWordsHist req);
}
