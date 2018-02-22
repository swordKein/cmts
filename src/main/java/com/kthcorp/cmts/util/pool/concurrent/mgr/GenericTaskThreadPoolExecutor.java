package com.kthcorp.cmts.util.pool.concurrent.mgr;

import com.kthcorp.cmts.util.pool.concurrent.task.reject.MemberJoinExecutionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class GenericTaskThreadPoolExecutor extends ThreadPoolExecutor {

	/*
	public GenericTaskThreadPoolExecutor()
	{
		super(min_pool_size, max_pool_size, keep_alive_seconds, TimeUnit.SECONDS
				, new ArrayBlockingQueue(1000),new MemberJoinExecutionHandler() );

		System.out.println("================= Initiate Origin GenericTaskThreadPool ==================");
	}
	*/
	public GenericTaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS
				, new ArrayBlockingQueue(1000),new MemberJoinExecutionHandler() );
		System.out.println("================= Initiate GenericTaskThreadPool ==================");
	}

	/*
	public GenericTaskThreadPoolExecutor()
	{
		super(100, 400, 600, TimeUnit.SECONDS
				, new ArrayBlockingQueue(1000),new MemberJoinExecutionHandler() );
		System.out.println("================= Initiate Origin GenericTaskThreadPool ==================");
	}
	*/

	public GenericTaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, BlockingQueue<Runnable> workQueue,RejectedExecutionHandler rejectedExecutionHandler) 
	{

		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue,rejectedExecutionHandler );
		System.out.println("================= Initiate GenericTaskThreadPool ==================");
	}
	
	public GenericTaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,RejectedExecutionHandler rejectedExecutionHandler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,rejectedExecutionHandler );
	}
	
public GenericTaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
		
	}

	public GenericTaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
		
	}

	public GenericTaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		
	}
/**
 * 가용할만한 리소스가 있는지 조회.
 * @return
 */
public boolean isAvailAbleExecutionResource(){
 return getMaximumPoolSize() - getActiveCount() >0 ? true: false;	
}
}
