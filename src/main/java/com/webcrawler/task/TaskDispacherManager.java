package com.webcrawler.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *@author yankang
 *@date 2014��12��9��
 */
public class TaskDispacherManager {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final ExecutorService es = Executors.newFixedThreadPool(1);
	
	public void submit(Runnable run){
		es.submit(run);
		log.info("submit a taskDispacher run");
	}
	
	public void shutDown() {
		es.shutdownNow();
	}
}
