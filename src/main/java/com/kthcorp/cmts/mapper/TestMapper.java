package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.Items;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
//@Qualifier("TestDao")
public interface TestMapper {
    //@Select("SELECT NOW()")
    String getCurrentDateTime();

    ArrayList<Map> getVocList();
    int uptVocListById(Map<String, Object> reqMap);


    ArrayList<Map> getFaqList();
    int uptFaqListById(Map<String, Object> reqMap);

    int insYjItems(Map<String, Object> reqMap);

    int insYcDatas(Map<String, Object> reqMap);
    List<Map<String, Object>> getYcDatas1st();
    List<Map<String, Object>> getYcDatas2st();

    List<Items> getNoGenreItems();
}
