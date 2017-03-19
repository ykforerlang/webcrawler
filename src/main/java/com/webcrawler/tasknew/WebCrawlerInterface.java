package com.webcrawler.tasknew;

import java.util.concurrent.Future;

import com.webcrawler.model.Task;

/**
 *
 *@author yankang
 *@date 2015年3月9日
 */
public interface WebCrawlerInterface {
	/**
	 * 提交给前一个分发队列
	 * @param task
	 */
	public void submit(Task task);
	
	/**
	 * 直接提交给线程池的队列
	 * @param task
	 * @return
	 */
	public Future<?> directSubmit(Task task);
	
	public void start();
	
	public void stop();
}
