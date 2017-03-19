package com.webcrawler.model;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *instace infomation
 *@author yankang
 *@date 2015年1月5日
 */
public class InstanceInfo {
	
	private Object target;
	private Method init;
	private Map<String, Method> solvings;
	
	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public Method getInit() {
		return init;
	}
	
	public void setInit(Method init) {
		this.init = init;
	}
	
	public Map<String, Method> getSolvings() {
		return solvings;
	}
	public void setSolvings(Map<String, Method> solvings) {
		this.solvings = solvings;
	}
	
	@Override
	public String toString() {
		return target.getClass().getSimpleName();
	}
}
