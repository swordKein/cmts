package com.kthcorp.cmts.util.pool.concurrent.task.proxy;

import com.kthcorp.cmts.util.pool.concurrent.task.Task;
import com.kthcorp.cmts.util.pool.concurrent.task.arg.TaskArgument;
import com.kthcorp.cmts.util.pool.concurrent.task.handler.TaskInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.NoSuchElementException;

public class TaskProxyFactory {
	
	
	
	public  static Task getTask(TaskArgument argument) throws IllegalArgumentException, NoSuchElementException, IllegalStateException, Exception {
		
		Task task =(Task) argument.getTargetObject();
		
		return (Task)Proxy.newProxyInstance(
				Task.class.getClassLoader(),
				new Class<?>[] {Task.class},
				(InvocationHandler)new TaskInvocationHandler(task)
				);
	}
}
