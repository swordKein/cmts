package com.kthcorp.cmts.util.pool.concurrent.task.reject;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 */

/**
 * @author mdk
 *
 */
public class MemberJoinRecjectedExcutionFution implements Runnable {

	private HttpServletRequest resquest = null;
	
	
	

	public MemberJoinRecjectedExcutionFution() {
		super();
		// TODO Auto-generated constructor stub
	}




	public MemberJoinRecjectedExcutionFution(HttpServletRequest resquest) {
		super();
		this.resquest = resquest;
	}




	public HttpServletRequest getResquest() {
		return resquest;
	}




	public void setResquest(HttpServletRequest resquest) {
		this.resquest = resquest;
	}




	@Override
	public void run() {
		

	}
}
	
