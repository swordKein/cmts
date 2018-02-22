package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.GoogleApiHist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoogleApiHistMapper {
    List<GoogleApiHist> getGoogleApiHist();

    GoogleApiHist getGoogleTransSumSizeByMonth(GoogleApiHist req);

    int insGoogleApiHist(GoogleApiHist req);
}
