package com.kthcorp.cmts.service;

import com.kthcorp.cmts.mapper.ConfTargetMapper;
import com.kthcorp.cmts.mapper.SchedTriggerMapper;
import com.kthcorp.cmts.model.ConfTarget;
import com.kthcorp.cmts.model.SchedTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class StepService implements StepServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(StepService.class);

    private ApplicationContext context;
    @Autowired
    public void context(ApplicationContext context) { this.context = context; }
    @Autowired
    private SchedTriggerMapper schedTriggerMapper;
    @Autowired
    private ConfTargetMapper confTargetMapper;

    @Override
    public void run(String... strings) throws Exception {
        //ApplicationContext context =
        //        new AnnotationConfigApplicationContext(StepService.class);

        System.out.println("#Context:"+context.toString());
        CollectServiceImpl bean = context.getBean(CollectServiceImpl.class);

        //bean.runMainService();

        ThreadPoolTaskExecutor t = context.getBean(ThreadPoolTaskExecutor.class);
        t.shutdown();
/*
        try {
            mainService.step00();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public String getTest() {
        return null;
    }

    public class MyBean {
        @Autowired
        private TaskExecutor executor;

        public void runTasks () {
            for (int i = 0; i < 1; i++) {
                executor.execute(getTask(i));
            }
        }

        private Runnable getTask (int i) {
            return () -> {
                System.out.printf("running task %d. Thread: %s%n",
                        i,
                        Thread.currentThread().getName());
            };
        }
    }


    // 수집 스케쥴의 stat를 P로 업데이트 한다
    @Override
    public int uptSchedTriggerForCollectStep02(SchedTrigger sched) {
        sched.setStat("P");  // stat != D && progs != null 아니면 progs 1을 더해준다
        // (추가) 수행 시 tcnt를 1씩 증가시킨다. 회차 기록
        int rtcode = schedTriggerMapper.uptSchedTriggerProgs(sched);

        logger.info("#STEP:02: uptSchedTriggerForCollectStep02:: result:"+rtcode+"/sc_id:"+sched.getSc_id()+"/stat:"+sched.getStat());
        return rtcode;
    }

    // 수집 ConfTarget 설정의 stat를 P로 업데이트 한다
    @Override
    public int uptConfTargetBeforeCollectProcess(ConfTarget tg) throws Exception {
        int rtcode = 0;

        // 설정의 stat를 P로 업데이트 한다
        tg.setStat("P");  // stat != D && progs != null 아니면 progs 1을 더해준다
        rtcode = confTargetMapper.uptTargetStat(tg);
        logger.info("#STEP:03: uptConfTargetBeforeCollectProcess:: result:"+rtcode+"/tg_id:"+tg.getTg_id()+"/stat:"+tg.getStat());
        return rtcode;
    }

    // 수집 완료 후 stat 업데이트
    @Override
    public int uptSchedTriggerProgsAfterCollectTargetOneProcess(int sc_id, String statTarget) throws Exception {
        int rtcode = 0;

        // 스케쥴의 stat를 S로 progs는 1을 더해 업데이트 한다
        SchedTrigger sched = new SchedTrigger();
        sched.setSc_id(sc_id);
        sched.setStat(statTarget);  // stat != D && progs != null 아니면 progs 1을 더해준다
        if("Y".equals(statTarget)) { sched.setProgs(1); }
        rtcode = schedTriggerMapper.uptSchedTriggerProgs(sched);

        logger.info("#STEP:03: uptSchedTriggerProgsAfterCollectTargetOneProcess:: result:"+rtcode
                +"/sc_id:"+sc_id+"/sched_stat:"+sched.getStat()+"/target_stat:"+statTarget);

        return rtcode;
    }

    // 정제 스케쥴의 stat를 P로 업데이트 한다
    @Override
    public int uptSchedTriggerForRefineStep05(SchedTrigger sched) {
        sched.setStat("P");  // stat != D && progs != null 아니면 progs 1을 더해준다
        int rtcode = schedTriggerMapper.uptSchedTriggerProgs(sched);

        logger.info("#STEP:05: uptSchedTriggerForRefineStep05:: result:"+rtcode+"/sc_id:"+sched.getSc_id()+"/stat:"+sched.getStat());
        return rtcode;
    }

    // 정제 ConfTarget 설정의 stat를 P로 업데이트 한다
    @Override
    public int uptConfTargetBeforeRefineProcess(ConfTarget tg) throws Exception {
        int rtcode = 0;

        // 설정의 stat를 P로 업데이트 한다
        /*
        tg.setStat("P");  // stat != D && progs != null 아니면 progs 1을 더해준다
        rtcode = confTargetMapper.uptTargetStat(tg);
        logger.info("#STEP:06: uptConfTargetBeforeRefineProcess:: result:"+rtcode+"/tg_id:"+tg.getTg_id()+"/stat:"+tg.getStat());
        */
        return rtcode;
    }

    // 정제 완료 후 stat 업데이트
    @Override
    public int uptSchedTriggerProgsAfterRefineTargetOneProcess(int sc_id, String statTarget) throws Exception {
        int rtcode = 0;

        // 스케쥴의 stat를 S로 progs는 1을 더해 업데이트 한다
        SchedTrigger sched = new SchedTrigger();
        sched.setSc_id(sc_id);
        sched.setStat(statTarget);
        // stat != D && progs != null 아니면 progs 1을 더해준다
        //if("Y".equals(statTarget)) { sched.setProgs(1); }
        rtcode = schedTriggerMapper.uptSchedTriggerProgs(sched);

        // 설정의 stat를 S로 업데이트 한다
        /*
        tg.setStat(statTarget);
        try {
            rtcode = confTargetMapper.uptTargetStat(tg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        logger.info("#STEP:06: uptSchedTriggerProgsAfterRefineTargetOneProcess:: result:"+rtcode
                +"/sc_id:"+sc_id+"/sched_stat:"+sched.getStat()+"/target_stat:"+statTarget);

        return rtcode;
    }





    // 분석 스케쥴의 stat를 P로 업데이트 한다
    @Override
    public int uptSchedTriggerForAnalyzeStep07(SchedTrigger sched) {
        sched.setStat("P");  // stat != D && progs != null 아니면 progs 1을 더해준다
        int rtcode = schedTriggerMapper.uptSchedTriggerProgs(sched);

        logger.info("#STEP:07: uptSchedTriggerForAnalyze Step07:: result:"+rtcode+"/sc_id:"+sched.getSc_id()+"/stat:"+sched.getStat());
        return rtcode;
    }

    // 분석 ConfTarget 설정의 stat를 P로 업데이트 한다
    @Override
    public int uptConfTargetBeforeAnalyzeProcess(ConfTarget tg) throws Exception {
        int rtcode = 0;

        // 설정의 stat를 P로 업데이트 한다
        /*
        tg.setStat("P");  // stat != D && progs != null 아니면 progs 1을 더해준다
        rtcode = confTargetMapper.uptTargetStat(tg);
        logger.info("#STEP:06: uptConfTargetBeforeAnalyzeProcess:: result:"+rtcode+"/tg_id:"+tg.getTg_id()+"/stat:"+tg.getStat());
        */
        return rtcode;
    }

    // 분석 완료 후 stat 업데이트
    @Override
    public int uptSchedTriggerProgsAfterAnalyzeTargetOneProcess(int sc_id, String statTarget) throws Exception {
        int rtcode = 0;

        // 스케쥴의 stat를 S로 progs는 1을 더해 업데이트 한다
        SchedTrigger sched = new SchedTrigger();
        sched.setSc_id(sc_id);
        sched.setStat(statTarget);
        // stat != D && progs != null 아니면 progs 1을 더해준다
        //if("Y".equals(statTarget)) { sched.setProgs(1); }
        rtcode = schedTriggerMapper.uptSchedTriggerProgs(sched);

        // 설정의 stat를 S로 업데이트 한다
        /*
        tg.setStat(statTarget);
        try {
            rtcode = confTargetMapper.uptTargetStat(tg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        logger.info("#STEP:09: uptSchedTriggerProgsAfterAnalyzeTargetOneProcess:: result:"+rtcode
                +"/sc_id:"+sc_id+"/sched_stat:"+sched.getStat()+"/target_stat:"+statTarget);

        return rtcode;
    }
}
