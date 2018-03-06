package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicAddWords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicAddWordsMapper {
    List<DicAddWords> getDicAddWords();
    List<DicAddWords> getDicAddWordsPaging(DicAddWords req);

    int insDicAddWords(DicAddWords req);
    int uptDicAddWords(DicAddWords req);

    int uptDicAddWordsByWord(DicAddWords req);
    int delDicAddWordsByWord(DicAddWords req);

    int countItems(DicAddWords req);
}
