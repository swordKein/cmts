package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicFilterWords;
import com.kthcorp.cmts.model.DicFilterWordsHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicFilterWordsHistMapper {
    List<DicFilterWordsHist> getDicFilterWordsHist();

    int insDicFilterWordsHist(DicFilterWordsHist req);
}
