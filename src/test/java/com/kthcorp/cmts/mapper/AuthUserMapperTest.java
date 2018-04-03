package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.AuthUser;
import com.kthcorp.cmts.model.DicAddWords;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AuthUserMapperTest {
    @Autowired
    private AuthUserMapper authUserMapper;

    @Test
    @Rollback(false)
    public void test_getAuthUsers() throws Exception {
        List<AuthUser> result = authUserMapper.getAuthUsers();
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getAuthUserById() throws Exception {
        AuthUser req = new AuthUser();
        req.setUserid("ghkdwo77");
        AuthUser result = authUserMapper.getAuthUserById(req);
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_insAuthUser() throws Exception {
        AuthUser req = new AuthUser();
        req.setUserid("ghkdwo77");
        req.setUsername("jaeyeon.hwang");
        req.setUsergrant("ADMIN");
        req.setUsercompany("KTH");
        req.setRegid("ghkdwo77");

        int result = authUserMapper.insAuthUser(req);
        System.out.println("#result:"+result);
    }

    @Test
    @Rollback(false)
    public void test_uptAuthUser() throws Exception {
        AuthUser req = new AuthUser();
        req.setUserid("ghkdwo77");
        req.setUsername("jaeyeon.hwang2");
        req.setUsergrant("ADMIN");
        req.setUsercompany("KTH");
        req.setRegid("ghkdwo77");

        int result = authUserMapper.uptAuthUser(req);
        System.out.println("#result:"+result);
    }


    @Test
    @Rollback(false)
    public void test_delAuthUser() throws Exception {
        AuthUser req = new AuthUser();
        req.setUserid("ghkdwo77");
        req.setUsername("jaeyeon.hwang2");
        req.setUsergrant("ADMIN");
        req.setUsercompany("KTH");
        req.setRegid("ghkdwo77");

        int result = authUserMapper.delAuthUser(req);
        System.out.println("#result:"+result);
    }
}
