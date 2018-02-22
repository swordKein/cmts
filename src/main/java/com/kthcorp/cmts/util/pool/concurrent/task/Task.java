package com.kthcorp.cmts.util.pool.concurrent.task;

/**
 * Task
 * @author Heejin Park
 *
 */
public interface Task extends Runnable {
	
	public void execute() throws RuntimeException;
	public void run();
}


