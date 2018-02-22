package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.NlpProgs;
import com.kthcorp.cmts.model.TestVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NlpProgsMapper {
    List<NlpProgs> getNlpProgs();
    int insNlpProgs(NlpProgs req);
    int uptNlpProgs(NlpProgs req);

    List<TestVO> getTest1(TestVO req);
}
