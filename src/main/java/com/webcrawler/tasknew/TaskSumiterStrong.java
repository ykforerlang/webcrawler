package com.webcrawler.tasknew;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.anno.TaskSubmiting;
import com.webcrawler.model.Task;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 *
 *@author yankang
 *@date 2015年1月5日
 */
public class TaskSumiterStrong implements MethodInterceptor {
	private final Logger log  = LoggerFactory.getLogger(this.getClass());
	private WebCrawlerInterface wc;
	public TaskSumiterStrong(WebCrawlerInterface wc) {
		this.wc = wc;
	}
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Object result = proxy.invokeSuper(obj, args);
		if(method.getAnnotation(TaskSubmiting.class) == null) return result;
		
		if(result == null) return null;
		
		if(result instanceof Task){
			Task task = (Task) result;
			wc.submit(task);
			log.debug("add a task " + task);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		List<Task> list = (List<Task>) result;
		for(Task task : list){
			wc.submit(task);
			log.debug("add a task " + task);
		}
		
		return result;
	}

}
