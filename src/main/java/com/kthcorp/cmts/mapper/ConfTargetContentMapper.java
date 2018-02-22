package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTargetContent;
import com.kthcorp.cmts.model.ConfTargetHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface ConfTargetContentMapper {

    @Transactional
    public int insConfTargetContent(ConfTargetContent req);
}
