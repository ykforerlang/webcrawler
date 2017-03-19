package com.webcrawler.task;

import java.util.Collection;

import com.webcrawler.model.Task;

/**
 *
 *@author yankang
 *@date 2014年12月11日
 */
public class CrawlerExecutor {
	private BeanMapping bm;
	private Crawler crawler;
	
	public CrawlerExecutor(int poolSize, String...packages) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		bm = new BeanMapping();
		bm.setPackages(packages);
		bm.initMap();
		
		crawler = new Crawler();
		crawler.setPoolSize(poolSize);
		crawler.init(bm);
	}
	
	public void startup() throws Exception {
		Collection<BaseAction> beans = bm.getBeans();
		for(BaseAction action : beans){
			action.setCrawlerExecutor(this);
			action.startup();
		}
	}
	
	
	public void submitTask(Task task){
		crawler.submitTask(task);
	}
	
	public void close() {
		crawler.destroy();
	}
}
