package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicChangeWordsHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicChangeWordsHistMapper {
    List<DicChangeWordsHist> getDicChangeWordsHist();
    List<DicChangeWordsHist> getDicChangeWordsHistByReq(DicChangeWordsHist req);

    int insDicChangeWordsHist(DicChangeWordsHist req);
}
