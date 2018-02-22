package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ConfTargetOrig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
//@Qualifier("TestDao")
public interface ConfTargetOrigMapper {
    @Select({"SELECT t.tg_id, t.title FROM conf_target_orig t"})
    public List<ConfTargetOrig> getAll();

    // 수집대상 최근 리스트 조회
    public List<ConfTargetOrig> getTargetListActivePage(ConfTargetOrig req);
    // 분석대상 최근 리스트 조회
    public List<ConfTargetOrig> getTargetListReadyToAnalyzePage(ConfTargetOrig req);

    public List<ConfTargetOrig> getTargetListByStat(ConfTargetOrig req);
    public List<ConfTargetOrig> getTargetListActiveFirst10();

    public ConfTargetOrig getConfTargetOrigById(ConfTargetOrig req);

    @Transactional
    public int uptTargetStat(ConfTargetOrig req);

    @Transactional
    public int insConfTargetOrig(ConfTargetOrig req);
}
