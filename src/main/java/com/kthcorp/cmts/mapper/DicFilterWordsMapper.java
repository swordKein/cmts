package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicFilterWords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DicFilterWordsMapper {
    List<DicFilterWords> getDicFilterWords();
    List<DicFilterWords> getDicFilterWordsPaging(DicFilterWords req);

    int insDicFilterWords(DicFilterWords req);
    int uptDicFilterWords(DicFilterWords req);

    int uptDicFilterWordsByWord(DicFilterWords req);
    int delDicFilterWordsByWord(DicFilterWords req);

    int countItems(DicFilterWords req);
}
