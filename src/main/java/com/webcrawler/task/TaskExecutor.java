package com.webcrawler.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *@author yankang
 *@date 2014��12��10��
 */
public class TaskExecutor {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final ExecutorService es;
	
	public TaskExecutor(int poolSize) {
		es = Executors.newFixedThreadPool(poolSize);
	}
	public Future<?> submit(Runnable run){
		log.info("submit a thread to solve task");
		return es.submit(run);
	}
	
	public void destroy() {
		es.shutdownNow();
	}
}
