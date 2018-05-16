package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTargetContent;
import com.kthcorp.cmts.model.SchedTargetContent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface SchedTargetContentMapper {

    @Transactional
    int insSchedTargetContent(SchedTargetContent req);

    SchedTargetContent getSchedTargetContentLastOne(SchedTargetContent req);
    List<SchedTargetContent> getSchedTargetContentList(SchedTargetContent req);

    int deleteSchedTargetContentOrigin(Integer itemidx);
}
