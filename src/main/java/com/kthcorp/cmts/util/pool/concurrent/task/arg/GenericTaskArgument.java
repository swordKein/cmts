package com.kthcorp.cmts.util.pool.concurrent.task.arg;

import java.util.Arrays;

/**
 * 
 * @author Heejin Park
 *
 */
public class GenericTaskArgument implements TaskArgument {
	private  Object targetObject = null;
	private  String methodName = null;
	private   Class[] parameterTypes = null;
	private  Object[] parameters = null;
	
	public GenericTaskArgument() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GenericTaskArgument(Object targetObject, String methodName,
			Class[] parameterTypes, Object[] parameters) {
		super();
		this.targetObject = targetObject;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	public Object getTargetObject() {
		return targetObject;
	}
	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	@Override
	public String toString() {
		return "GenericTaskArgument [targetObject=" + targetObject
				+ ", methodName=" + methodName + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + ", parameters="
				+ Arrays.toString(parameters) + ", getTargetObject()="
				+ getTargetObject() + ", getMethodName()=" + getMethodName()
				+ ", getParameterTypes()="
				+ Arrays.toString(getParameterTypes()) + ", getParameters()="
				+ Arrays.toString(getParameters()) + "]";
	}
}
