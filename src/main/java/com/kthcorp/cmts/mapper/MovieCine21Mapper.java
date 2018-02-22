package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.InItems;
import com.kthcorp.cmts.model.MovieCine21;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MovieCine21Mapper {
    int insMovieCine21(MovieCine21 req);
    int insMovieCine21List(Map<String,Object> reqMap);
    int cntMovieCine21(MovieCine21 req);

    List<MovieCine21> getMovieCine21(MovieCine21 req);
}
