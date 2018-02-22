package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTargetMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
//@Qualifier("TestDao")
public interface ConfTargetMapper {
    @Select({"SELECT t.tg_id, t.title FROM conf_target t"})
    List<ConfTarget> getAll();

    // 수집대상 최근 리스트 조회
    List<ConfTarget> getTargetListActivePage(ConfTarget req);
    // 분석대상 최근 리스트 조회
    List<ConfTarget> getTargetListReadyToAnalyzePage(ConfTarget req);

    List<ConfTarget> getTargetListByStat(ConfTarget req);
    List<ConfTarget> getTargetListActiveFirst10();

    ConfTarget getConfTargetById(ConfTarget req);
    ConfTarget getTargetListByPrefix(ConfTarget req);

    @Transactional
    int uptTargetStat(ConfTarget req);

    @Transactional
    int insConfTarget(ConfTarget req);
    @Transactional
    int uptConfTarget(ConfTarget req);
    @Transactional
    int delConfTarget(ConfTarget req);

}
