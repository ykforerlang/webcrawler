package com.webcrawler.model;
/**
 *download static resource . object : urlfile
 *@author yankang
 *@date 2015年1月8日
 */
public class StaticResourceTask extends Task{
	public  final static String middle= "-->";
	
	public StaticResourceTask(Object date) {
		super("img", date);
	}
	
	public StaticResourceTask(String url, String file){
		this(url + middle + file);
	}
	
	public String getUrl() {
		String data = (String) getDate();
		return data.split(middle)[0];
	}
	
	public String getFile() {
		String data = (String) getDate();
		return data.split(middle)[1];
	}

}
