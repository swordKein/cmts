package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicNotuseWords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicNotuseWordsMapper {
    List<DicNotuseWords> getDicNotuseWords();
    List<DicNotuseWords> getDicNotuseWordsPaging(DicNotuseWords req);

    int insDicNotuseWords(DicNotuseWords req);
    int uptDicNotuseWords(DicNotuseWords req);
    int uptDicNotuseWordsByWord(DicNotuseWords req);
    int delDicNotuseWordsByWord(DicNotuseWords req);

    int countItems();
}
