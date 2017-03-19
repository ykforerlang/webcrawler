package com.webcrawler.task;
/**
 *
 *@author yankang
 *@date 2014年12月11日
 */
public interface BaseAction {
	
	public void startup() throws Exception;
	
	public void setCrawlerExecutor(CrawlerExecutor crawlerExecutor);
}
