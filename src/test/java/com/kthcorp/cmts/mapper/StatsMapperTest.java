package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.InItems;
import com.kthcorp.cmts.model.Stats;
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
public class StatsMapperTest {
    @Autowired
    private StatsMapper statsMapper;

    @Test
    @Rollback(false)
    public void test_getStat() throws Exception {

        System.out.println("#rt:" + statsMapper.top_countInItems());
        System.out.println("#rt:" + statsMapper.top_countEndTagged());
        System.out.println("#rt:" + statsMapper.top_countCollecting());
        System.out.println("#rt:" + statsMapper.top_countCollected());
        System.out.println("#rt:" + statsMapper.top_countAnalyzing());
        System.out.println("#rt:" + statsMapper.top_countAnalyzed());
        System.out.println("#rt:" + statsMapper.top_countTagging());
        System.out.println("#rt:" + statsMapper.top_countTagged());

        System.out.println("#rt:" + statsMapper.mid_countReady());
        System.out.println("#rt:" + statsMapper.mid_countFailCollect());
        System.out.println("#rt:" + statsMapper.mid_countFailAnalyze());
        System.out.println("#rt:" + statsMapper.mid_countReadyTagging());

        Stats req = new Stats();
        req.setSdate("2018-01-25");
        req.setEdate("2018-01-31");
        System.out.println("#rt:" + statsMapper.down_countsInserted(req));
        System.out.println("#rt:" + statsMapper.down_countsCollected(req));
        System.out.println("#rt:" + statsMapper.down_countsAnalyzed(req));
        System.out.println("#rt:" + statsMapper.down_countsTagged(req));


        System.out.println("#rt:" + statsMapper.down_countsInserted(req));
        System.out.println("#rt:" + statsMapper.down_countsCollected(req));
        System.out.println("#rt:" + statsMapper.down_countsAnalyzed(req));
        System.out.println("#rt:" + statsMapper.down_countsTagged(req));

        System.out.println("#rt:" + statsMapper.down_countsTagged(req));

        System.out.println("#rt:" + statsMapper.down_countsTagged(req));
    }

}
