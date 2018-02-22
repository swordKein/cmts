package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicAddWordsHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicAddWordsHistMapper {
    List<DicAddWordsHist> getDicAddWordsHist();
    List<DicAddWordsHist> getDicAddWordsHistByReq(DicAddWordsHist req);

    int insDicAddWordsHist(DicAddWordsHist req);
}
