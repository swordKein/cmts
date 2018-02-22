package com.kthcorp.cmts.util.pool.concurrent.task.reject;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class MemberJoinExecutionHandler implements RejectedExecutionHandler
{
	@Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor)
    {
        new Thread (runnable).start();
    }
}
