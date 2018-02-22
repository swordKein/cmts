package com.kthcorp.cmts.util.hadoop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class HadoopWordCountTest {
    @Test
    public void test_hadoopWordCountMain() {
        try {
            WordCountJob.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
