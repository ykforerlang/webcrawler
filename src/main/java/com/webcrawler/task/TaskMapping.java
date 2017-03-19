package com.webcrawler.task;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.anno.TaskSolving;
import com.webcrawler.model.RunInfo;

/**
 *task mapping
 *@author yankang
 *@date 2014��12��10��
 */
public class TaskMapping {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private final Map<String, RunInfo> map = new ConcurrentHashMap<String, RunInfo>();
	
	public void initTaskMapping(BeanMapping bm) {
		Collection<BaseAction> beans = bm.getBeans();
		for(Object value : beans){
			solveOneValue(value);
		}
	}


	private void solveOneValue(Object value) {
		Class<?> clazz = value.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods){
			TaskSolving taskSolving = method.getAnnotation(TaskSolving.class);
			if (taskSolving == null) continue;
			String key = taskSolving.value();
			RunInfo val = new RunInfo(value, method);
			map.put(key, val);
			log.info("add a task--map with key : " + key);
		}
		
	}
	
	public RunInfo getRunInfo(String key){
		return map.get(key);
	}
	
	
	
}
