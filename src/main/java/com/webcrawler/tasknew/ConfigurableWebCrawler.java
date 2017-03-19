package com.webcrawler.tasknew;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.model.InstanceInfo;
import com.webcrawler.model.RunInfo;
import com.webcrawler.model.Task;

/**
 *configureble web crawler  to instead WebCrawler
 *@author yankang
 *@date 2015年3月2日
 */
public class ConfigurableWebCrawler implements WebCrawlerInterface{
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private BlockingQueue<Task> taskQueue;
	private ThreadPoolExecutor tpe ;
	
	/**
	 * store all @bean class
	 */
	private BeanContainer container ;
	
	private Map<String, RunInfo> solvings = new ConcurrentHashMap<String, RunInfo>();
	
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
	public  ConfigurableWebCrawler(ThreadPoolExecutor tpe, String...packs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException {
		this(new LinkedBlockingQueue<Task>(), tpe, packs);
	}
	
	public ConfigurableWebCrawler(BlockingQueue<Task> bq, ThreadPoolExecutor tpe, String...packs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException{
		this.tpe = tpe;
	
		taskQueue = bq;
		
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

	private void initBeanContainer(WebCrawlerInterface webCrawler, String...packs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException {
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
					Runnable subRun = null;
					try {
						task = taskQueue.take();
						subRun = taskToRunnable(task);
						tpe.submit(subRun);
					} catch (InterruptedException e) {
						log.info("Thread which poll the task queue is interrupted");
						return;
					}catch (RejectedExecutionException e) {
						try {
							tpe.getQueue().put(subRun);
						} catch (InterruptedException e1) {
							return;
						}
					} catch (Exception e) {
					}
					
				}
				
			}
		};
		log.info("execute the thread to poll the queue");
		tpe.execute(run);	
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
					log.warn("error at run " , e);
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
			e.printStackTrace();
		}
	}
	
	public void stop() {
		tpe.shutdownNow();
	}


	@Override
	public Future<?> directSubmit(Task task) {
		Callable<?> call;
		try {
			call = taskToCallable(task);
			return tpe.submit(call);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}


	@SuppressWarnings("rawtypes")
	private Callable taskToCallable(Task task) throws Exception {
		String type = task.getType();
		final Object data = task.getDate();
		final RunInfo runInfo = solvings.get(type);
		if(runInfo == null) {
			log.warn("not map solving for " + type);
			throw new Exception("not map solving for " + type);
		}
		
		Callable callable = new Callable() {

			@Override
			public Object call() throws Exception {
				return runInfo.execute(data);
			}
		};
		
		return callable;	
	}
}
