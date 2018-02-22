package com.kthcorp.cmts.util.pool.concurrent.monitor;

import com.kthcorp.cmts.util.pool.concurrent.task.util.TaskExcutionUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * TaskPool status monitor thread. To monitor the status of {@link ThreadPoolExecutor}
 * and its status.
 */
public class TaskPoolMonitorThread implements Runnable
{
    private static Logger logger = LoggerFactory.getLogger(TaskPoolMonitorThread.class);
	/**
	 * 초
	 */
	public static final long SECOND =1000L;
	/**
	 * 분
	 */
	public static final long MINUTE =60*SECOND;
	/**
	 * 시
	 */
	public static final long HOUR =60*MINUTE;
	/**
	 * 기본 모니터링 Thread pool 상태 로그 term (10분)
	 */
	public static final long DEFAULT_TEN_MINUTES_TERM_LOG_TERM = 10 * MINUTE;
	
    ThreadPoolExecutor executor;
    
    long logterm = DEFAULT_TEN_MINUTES_TERM_LOG_TERM;
    
    public TaskPoolMonitorThread(ThreadPoolExecutor executor){
    	 this(executor,DEFAULT_TEN_MINUTES_TERM_LOG_TERM);
    }
    public TaskPoolMonitorThread(ThreadPoolExecutor executor,long logterm)
    {
        this.executor = executor; 
        this.logterm=logterm;
    }
 
    @Override
    public void run()
    {
    	logger.info("START THREAD POOL [TaskPoolMonitorThread monitor]");
        try
        {
            do
            {
            	logger.info(
                    String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                        this.executor.getPoolSize(),
                        this.executor.getMaximumPoolSize(),
                        this.executor.getActiveCount(),
                        this.executor.getCompletedTaskCount(),
                        this.executor.getTaskCount(),
                        this.executor.isShutdown(),
                        this.executor.isTerminated()));
                Thread.sleep(this.logterm);
            }
            while (true);
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
        }
    	logger.info("EXIT THREAD POOL [TaskPoolMonitorThread monitor]");
    }
}
