package com.webcrawler.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.model.RunInfo;
import com.webcrawler.model.Task;

/**
 *
 * @author yankang
 * @date 2014年12月10日
 */
public class Crawler {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final LinkedBlockingQueue<Task> taskQueue = new LinkedBlockingQueue<Task>();
	//default the size is 20
	private int poolSize =20;

	//private BeanMapping bm;
	private TaskMapping tm;
	private TaskExecutor te;
	private TaskDispacherManager manager;

	public void init(BeanMapping bm) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		tm = new TaskMapping();
		//set runInfo by the object at bm
		tm.initTaskMapping(bm);
		//init the executor with poolsize
		te = new TaskExecutor(poolSize);
		manager = new TaskDispacherManager();
		initThread();
	}

	/**
	 * start a thread watching the queue
	 */
	private void initThread() {
		Runnable initRun = new Runnable() {

			@Override
			public void run() {
				Thread current = Thread.currentThread();
				log.info("poll thread is " + current);
				while (!current.isInterrupted()) {
					Task task = null;
					try {
						task = taskQueue.take();
						log.info("take a " + task+ "from  taskQueue");
					} catch (InterruptedException e) {
						log.info("poll thread interrupt");
						return;
					}
					final RunInfo runInfo = tm.getRunInfo(task.getType());
					final Object args = task.getDate();

					Runnable subRun = new Runnable() {

						@Override
						public void run() {
							try {
								runInfo.execute(args);
								log.info("execute a task");
							} catch (Exception e) {
								e.printStackTrace();
								log.info("error at execute task");
							}

						}
					};
					te.submit(subRun);
					log.info("submit a run ");
				}

			}
		};
		// manager manage the lifestyle of the thread
		manager.submit(initRun);
		log.info("add a thread to poll task queue");
	}

	/**
	 * submit a task
	 * 
	 * @param task
	 * @return
	 */
	public  void  submitTask(Task task) {
		taskQueue.offer(task);
		log.info("add a task");
	}

	public void destroy() {
		te.destroy();
		manager.shutDown();
	}


	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	public BlockingQueue<Task> getTaskQueue() {
		return taskQueue;
	}
	

	/*@Override
	public String toString() {
		return "Crawler [taskQueue=" + taskQueue + "]";
	}
	*/
	
}
