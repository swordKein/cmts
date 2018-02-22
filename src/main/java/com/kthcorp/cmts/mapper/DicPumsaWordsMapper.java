package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.DicNotuseWords;
import com.kthcorp.cmts.model.DicPumsaWords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DicPumsaWordsMapper {
    List<DicPumsaWords> getDicPumsaWords();
}
