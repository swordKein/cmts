package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTargetHist;
import com.kthcorp.cmts.model.SchedTrigger;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface ConfTargetHistMapper {

    //public List<ConfTargetHist> getConfTargetHistList(ConfTargetHist req);

    //@Transactional
    //public int insConfTargetHist(ConfTargetHist req);

}
