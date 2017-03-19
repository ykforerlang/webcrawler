package com.webcrawler.tasknew;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.model.InstanceInfo;
import com.webcrawler.model.RunInfo;
import com.webcrawler.model.Task;

/**
 *default webcrawler
 *use configurableWebCralwer instead, remain just for compatible
 *@author yankang
 *@date 2015年1月5日
 */
@Deprecated
public class WebCrawler implements WebCrawlerInterface{
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * task sumit to this queue
	 */
	private final LinkedBlockingQueue<Task> taskQueue = new LinkedBlockingQueue<Task>();
	
	/**
	 * store all @bean class
	 */
	private BeanContainer container ;
	
	private Map<String, RunInfo> solvings = new ConcurrentHashMap<String, RunInfo>();
	
	private ExecutorService executor;
	
	/**
	 * 1.init a thread to poll take the taskqueue
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
	 */
	
	public WebCrawler(int poolSize, String...packs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException {
		executor = Executors.newFixedThreadPool(poolSize);
		
		initBeanContainer(this, packs);
		
		initSovings();
		
		initInnerSolvings();
		
		initThread();
		
	}
	
	
	private void initInnerSolvings() throws NoSuchMethodException, SecurityException {
		StaticResourceTaskSolving sr = new StaticResourceTaskSolving();
		Method method = sr.getClass().getDeclaredMethod("storeResource", Object.class);
		
		RunInfo runInfo = new RunInfo(sr, method);
		solvings.put("img", runInfo);
	}


	private void initSovings() {
		List<InstanceInfo> infoList = container.getInfoList();
		for(InstanceInfo info : infoList){
			if (info.getSolvings() == null || info.getSolvings().size() == 0) continue;
			
			Object target = info.getTarget();
			Map<String, Method> solvingMap = info.getSolvings();
			Set<String> keySet = solvingMap.keySet();
			for(String key	 : keySet) {
				Method method = solvingMap.get(key);
				RunInfo runInfo = new RunInfo(target, method);
				solvings.put(key, runInfo);
				log.info("add a task--map with key : " + key);
			}
		}
		
	}


	/*public void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		//init BeanContainer
		initBeanContainer(this);
		
		//init the poll thread
		initThread();
		
	}*/

	private void initBeanContainer(WebCrawler webCrawler, String...packs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException {
		container = new BeanContainer(webCrawler);
		container.initBeans(packs);
		
	}

	private void initThread() {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				Thread current = Thread.currentThread();
				log.info("the poll thread runing...");
				while(!current.isInterrupted()){
					Task task = null;
					try {
						task = taskQueue.take();
						Runnable subRun = taskToRunnable(task);
						executor.submit(subRun);
					} catch (InterruptedException e) {
						log.info("Thread which poll the task queue is interrupted");
						return;
					} catch(Exception e){
						//just do 
						log.warn(e+ "");
					}
					
				}
				
			}
		};
		log.info("execute the thread to poll the queue");
		executor.execute(run);	
	}

	/**
	 * get Runnable by task;
	 * @param task
	 * @return
	 */
	private  Runnable taskToRunnable(Task task) throws Exception{
		String type = task.getType();
		final Object data = task.getDate();
		final RunInfo runInfo = solvings.get(type);
		if(runInfo == null) {
			log.warn("not map solving for " + type);
			throw new Exception("not map solving for " + type);
		}
		
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					runInfo.execute(data);
				} catch (Exception e) {
					e.printStackTrace();
					log.warn("error at run " + e.getMessage());
				}
				
			}
		};
	}
	
	/**
	 * submit a task
	 * @param task
	 */
	public void submit(Task task){
		taskQueue.offer(task);
		log.info("add task " + task);
	}
	
	public void start() {
		List<InstanceInfo> infoList = container.getInfoList();
		try {
			for(InstanceInfo info : infoList){
				if(info.getInit() == null) continue;
				
				info.getInit().invoke(info.getTarget());
			}	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void stop() {
		executor.shutdownNow();
	}


	@Override
	public Future<?> directSubmit(Task task) {
		throw new UnsupportedOperationException("not implement");
	}
	
}
