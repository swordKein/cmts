package com.kthcorp.cmts.util.pool.concurrent.task.arg;

/**
 * Thread에서 실행 할Object.method(파라메터 타입...) Object[]로표현되는 파라메터정보를 
 * 제공하는Interface.
 * 
 * 
 * @author heejin Park.
 *
 */
public interface TaskArgument {
	/**
	 * 서비스를 수행하는 주체가되는 객체.
	 * ex)java.lng.String이라면 데이터 "ABCD"
	 * @return
	 */
	public Object getTargetObject();
	/**
	 * 메서드이름
	 * ex)java.lang.String.substring(1,2)을  수행할거라면
	 * substring을 리턴 하게 구현하면 된다.
	 * @return
	 */
	public String getMethodName();
	/**
	 * 수행할 메서드의 파라메터 타입Array
	 * ex)java.lang.String.substring(1,2)을  수행할거라면
	 * primitive int형[2]이므로 Class [] prameterTypes= {int.class,int.class};
	 * 로  prameterTypes을 리턴하게 구현하면된다.
	 * @return
	 */
	public Class[] getParameterTypes();
	/**
	 * 수행할 메서드의 파라메터 데이터Array
	 * ex)java.lang.String.substring(1,2)을  수행할거라면
	 * primitive int형[2]이므로 Object []parameters= {1,2};
	 * 로 parameters을 리턴하게 구현하면된다.
	 * @return
	 */
	public Object[] getParameters();

}
