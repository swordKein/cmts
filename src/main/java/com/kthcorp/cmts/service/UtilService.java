package com.kthcorp.cmts.service;

import com.kthcorp.cmts.SpringBootWebApplication;
import com.kthcorp.cmts.util.pool.concurrent.mgr.GenericTaskThreadPoolExecutor;
import com.kthcorp.cmts.util.pool.concurrent.task.JobTask;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.GenericTaskArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class UtilService implements UtilServiceImpl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SpringBootWebApplication application;

    @Override
    public String getStr() {
        return "str";
    }

    // ThreadPool 설정 활성화 코드
    //private GenericTaskThreadPoolExecutor threadPool = application.getGenericTaskThreadPoolExecutor();
    //private GenericTaskThreadPoolExecutor threadPool;

    //ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringBootWebApplication.class);
    //private GenericTaskThreadPoolExecutor threadPool = (GenericTaskThreadPoolExecutor) ctx.getBean("threadPool");

    @Override
    public int runJobTask(Object className, String method, Object paramObj) {
        GenericTaskThreadPoolExecutor threadPool = application.getGenericTaskThreadPoolExecutor();
        //ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringBootWebApplication.class);
        //GenericTaskThreadPoolExecutor threadPool = (GenericTaskThreadPoolExecutor) ctx.getBean("threadPool");
        /*
        ArrayBlockingQueue bq = new ArrayBlockingQueue(400);
        RejectedExecutionHandler rh = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            }
        };
        GenericTaskThreadPoolExecutor threadPool = new GenericTaskThreadPoolExecutor(100, 400, 100, bq, rh);
        */

        int rtcode = 0;
        String rtmsg = "";

        try {
            GenericTaskArgument taskargument =new GenericTaskArgument();
            taskargument.setTargetObject(className);
            taskargument.setMethodName(method);


            // 형 변환
            Object[] parameters={paramObj};
            taskargument.setParameters(parameters);
            //System.out.println("#params:"+parameters.toString());

            //System.out.println("#params class:"+ ((Class<?>) paramObj).toString());
            //System.out.println("#params class:"+ paramObj.getClass().toString());
            //Class<?>[] parmeterTypes={(Class<?>) paramObj};
            Class<?>[] parmeterTypes={MultipartFile.class};
            //Type parmeterType = paramObj.getClass().getGenericSuperclass();
            //Class<?>[] parmeterTypes={MultipartFile.class};
            taskargument.setParameterTypes(parmeterTypes);

            JobTask JobTask = null;
            try {
                JobTask = new JobTask(taskargument, Thread.currentThread().getName() + "_JobTask");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(threadPool != null && threadPool.isAvailAbleExecutionResource()){
                try {
                    threadPool.execute(JobTask);
                    logger.info("#MLOG runJobTask ThreadPool Execute - get status : "+threadPool);
                    //System.out.println("#MLOG insertScanDataList ThreadPool Execute - get status : "+threadPool);
                    rtcode = 1;
                } catch (Exception e) {
                    logger.info("#MLOG runJobTask ThreadPool Execute ERROR! caused by "+e.getCause());
                    //System.out.println("#MLOG insertScanDataList ThreadPool Execute ERROR! caused by "+e.getCause());
                    rtcode = -4;
                }
            }else{
                // 쓰레드 런이 정상적이지 않은 경우 오류코드 리턴
                logger.error("#MLOG runJobTask cannot execute! no more threads..");
                System.out.println("#MLOG runJobTask cannot execute! no more threads..");
                rtcode = -4;
            }

        } catch (Exception e) {
            //logger.error("#MLOG /sendSignal/ by params:"+ paramObj.toString() + " ERROR! caused by "+e.getCause());
            e.printStackTrace();
            rtmsg = e.getCause().toString();
        }
        return rtcode;
    }

}
