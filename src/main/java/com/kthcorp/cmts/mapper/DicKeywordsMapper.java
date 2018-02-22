package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicKeywords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicKeywordsMapper {
    List<DicKeywords> getDicKeywordsList(DicKeywords req);
    List<DicKeywords> getDicKeywordsPaging(DicKeywords req);
    List<DicKeywords> getDicKeywordsListAll();
    List<String> getKeywordTypes();

    List<DicKeywords> getDicKeywords(DicKeywords req);
    int insDicKeywords(DicKeywords req);
    int uptDicKeywords(DicKeywords req);
    int delDicKeywords(DicKeywords del);

    int countDicKeywords(DicKeywords req);
    int insDicNotMapKeywords(DicKeywords req);
    List<DicKeywords> getDicNotMapKeywords();

    int countItems(DicKeywords req);
}
