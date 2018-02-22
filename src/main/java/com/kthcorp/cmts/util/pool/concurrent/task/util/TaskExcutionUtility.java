package com.kthcorp.cmts.util.pool.concurrent.task.util;

import com.kthcorp.cmts.util.pool.concurrent.task.arg.TaskArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * TaskArgument로 정의한 작업을 수행한다.
 * @author Administrator
 *
 */
	public class TaskExcutionUtility {
		//static	org.apache.log4j.Logger log =org.apache.log4j.Logger.getLogger(TaskExcutionUtility.class);
		private static Logger log = LoggerFactory.getLogger(TaskExcutionUtility.class);
		
	static public Object executeTask(TaskArgument taskArgument)
	throws RuntimeException
	{
		Object returnValue = null;
		//System.out.println(taskArgument);
		log.debug("+++ TaskExcutionUtility start : taskArgument : "+taskArgument);
		Object targetObject = taskArgument.getTargetObject();
		Method method     = null;
		String methodName = taskArgument.getMethodName();
		Object[] parameters =  taskArgument.getParameters();
		Class <?>[] parameterTypes = taskArgument.getParameterTypes();
		//log.debug(taskArgument.toString());
		log.debug("+++ TaskExcutionUtility before Try : taskArgument : "+taskArgument);
		try{
			
		Class _class =targetObject.getClass();
		method = _class.getDeclaredMethod(methodName, parameterTypes);
		
		returnValue = method.invoke(targetObject, parameters);
		
		}catch(Exception e){
			log.debug(e.toString());
			throw new RuntimeException(e);
		}
		writeReturnValueLog (returnValue);
		return returnValue;
  
	}
	
	static private void writeReturnValueLog(Object returnValue){

		log.debug("Returnvalue ["+returnValue+"]");
	}
}