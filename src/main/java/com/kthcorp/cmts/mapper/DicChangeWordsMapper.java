package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicChangeWords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicChangeWordsMapper {
    List<DicChangeWords> getDicChangeWords();
    List<DicChangeWords> getDicChangeWordsPaging(DicChangeWords req);

    int insDicChangeWords(DicChangeWords req);
    int uptDicChangeWords(DicChangeWords req);

    int uptDicChangeWordsByWord(DicChangeWords req);
    int delDicChangeWordsByWord(DicChangeWords req);

    int countItems(DicChangeWords req);
	int delDicChangeWords();
	int importDicChangeWords(DicChangeWords dicChangeWords);
	int cleanBlankDicChangeWords();
}
