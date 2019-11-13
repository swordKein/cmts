package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicGenreWords;
import com.kthcorp.cmts.model.DicKeywords;
import com.kthcorp.cmts.model.DicSubgenre;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DicKeywordsMapper {
    List<DicKeywords> getDicKeywordsList(DicKeywords req);
    List<DicKeywords> getDicKeywordsPaging(DicKeywords req);
    int cntDicKeywordsByType(DicKeywords req);
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

    List<DicGenreWords> getDicGenreKeywordsByGenre(String genre);
    int cntTagsMetasByDicKeywords(DicKeywords req);
    List<DicKeywords> cntTagsMetasByDicKeywordsAndGenre(DicKeywords req);
    int insDicRankWords2(DicKeywords req);


    @Transactional(propagation= Propagation.REQUIRES_NEW)
    int uptDicKeywords2(DicKeywords req);

    List<DicKeywords> getRankOfDicKeywordsFreq1(DicKeywords req);
    List<DicKeywords> getDicRankKeywordsPaging(DicKeywords req);
    List<DicKeywords> getRankWordsByGenreAndType(DicKeywords req);

    int insDicSubgenreGenres(DicSubgenre req);
    List<DicSubgenre> getDicSubgenreGenres(String mtype);

    Map<String,Object> getResultTags(Map<String,Object> req);

    List<Map<String, Object>> getResultTagsList();
    
	int delDicKeywordsAllByType(DicKeywords dicKeywords);
	List<DicSubgenre> getAllDicSubgenreGenres();
	int importDicKeywordsByType(DicKeywords dicKeywords);
	
	int cleanBlankDicKeywords(DicKeywords dicKeywords);
	List<DicKeywords> get10DicKeywordsList(DicKeywords reqkey);
}
