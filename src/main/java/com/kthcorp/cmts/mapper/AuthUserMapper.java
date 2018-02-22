package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.AuthUser;
import com.kthcorp.cmts.model.DicAddWords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AuthUserMapper {
    List<AuthUser> getAuthUsers();

    int insAuthUser(AuthUser req);
    int uptAuthUser(AuthUser req);
    int delAuthUser(AuthUser req);
    AuthUser getAuthUserById(AuthUser req);
}
