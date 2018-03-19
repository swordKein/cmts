package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.ManualChange;
import com.kthcorp.cmts.model.NlpProgs;
import com.kthcorp.cmts.model.TestVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
public interface ManualJobHistMapper {
    ManualChange getManualJobHistLastOne();

    @Transactional(propagation= Propagation.REQUIRES_NEW)
    int insManualJobHist(ManualChange req);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    int uptManualJobHist(ManualChange req);

    List<ManualChange> getManualJobHistPaging(ManualChange req);
    int cntManualJobHist();
}
