package com.kthcorp.cmts.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SnsMapper {
    Map<String, Object> getSnsTopWordsByTarget(Map<String, Object> req);
    int insSnsTopWords(Map<String, Object> req);

    int insSnsTopWords2(Map<String, Object> req);
    int delSnsTopWords2(Map<String, Object> req);
    List<Map<String, Object>> getSnsTopWords2Rank(Map<String, Object> req);
    List<Map<String, Object>> getSnsTopWords2RankByWord(Map<String, Object> req);
    String getMaxDateStr();

}
