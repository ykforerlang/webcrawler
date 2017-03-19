package com.webcrawler.model;
/**
 *
 *@author yankang
 *@date 2014��12��9��
 */
public class Task {

	private final String type;
	private final Object date;
	
	public Task(String type, Object date){
		this.type = type;
		this.date = date;
	}
	
	public String getType() {
		return type;
	}
	public Object getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "Task [type=" + type + ", date=" + date + "]";
	}
	
}
