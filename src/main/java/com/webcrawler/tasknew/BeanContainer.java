package com.webcrawler.tasknew;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.anno.Action;
import com.webcrawler.anno.Init;
import com.webcrawler.anno.TaskSolving;
import com.webcrawler.model.InstanceInfo;

/**
 *
 *@author yankang
 *@date 2015年1月5日
 */
public class BeanContainer {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * for strong the bean
	 */
	//private final Enhancer enhancer = new  Enhancer();
	private final TaskSumiterStrong tss ;
	
	
	/**
	 * the instaceinfo list whih the class annotion @Bean
	 */
	private List<InstanceInfo> list = new ArrayList<InstanceInfo>();
	
	
	public BeanContainer(WebCrawlerInterface webCrawler) {
		tss = new TaskSumiterStrong(webCrawler);
	}
	
	
	/**
	 * init the bean 
	 * @param packs
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
	 */
	public void initBeans(String...packs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException {
		ClassLoader loader = this.getClass().getClassLoader();
		for (String string : packs) {
			String path = string.replaceAll("\\.", "/");
			URL resource = loader.getResource(path);
			solveSingleResource(resource, string);
		}
	}

	private void solveSingleResource(URL resource, String pack)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException {
		log.info("solve a path " + pack);
		String f = resource.getFile();
		File file = new File(f);
		File[] files = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().endsWith(".class");
			}
		});
		for (File sub : files) {
			String name = sub.getName();
			// remove inner class
			if (name.indexOf("$") != -1)
				continue;
			name = name.substring(0, name.length() - 6);
			Class<?> clazz = Class.forName(pack + "." + name);
			log.info("check " + clazz.getName());
			
			if(clazz.getAnnotation(Action.class) == null) continue;
			log.info("solve " + clazz.getName());
			Object target = getStrong(clazz);
		/*	Object obj = null;
			try {
				obj = clazz.newInstance();
			} catch (InstantiationException e) {
				log.info("exclude "
						+ clazz.getName()
						+ " because the class may be interface, abstrat class...");
			}*/
			//cglib to strong the bean
			Map<String, Method> solvings = new HashMap<String,Method>();
			Method initMethod = null;
			//TaskSolving ts = null;
			
			Class<?> strongClazz = target.getClass();
			for(Method method : clazz.getDeclaredMethods()){
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				if(method.getAnnotation(Init.class) != null) {
					initMethod = strongClazz.getMethod(methodName, parameterTypes);
				}
				
				TaskSolving ts = method.getAnnotation(TaskSolving.class);
				if(ts== null)continue;
				String key =getKey(ts.value(), clazz);
				solvings.put(key, strongClazz.getMethod(methodName, parameterTypes));
				
			}
			InstanceInfo ii = new InstanceInfo();
			ii.setInit(initMethod);
			log.info("init method is " + initMethod);
			ii.setTarget(target);
			ii.setSolvings(solvings);
			list.add(ii);
			log.info("add a strong instance " + ii );
		}

	}
	
	/**
	 * if value is ${..} 则表明value 是这个类的静态属性
	 * @param value
	 * @param clazz
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private String getKey(String value, Class<?> clazz) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if(value.startsWith("${") && value.endsWith("}")){
			value = value.substring(2, value.length() -1);
			Field field = clazz.getDeclaredField(value);
			field.setAccessible(true);
			Object obj = field.get(null);
			if(obj instanceof String){
				return (String) obj;
			}
			throw new RuntimeException(value + " type is not String");
		}
		return value;
	}


	/**
	 * Dynamic Proxy to strong the bean
	 * @param pack
	 */
	public Object getStrong(Class<?> clazz) {
		Enhancer enhancer = new  Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(tss);
		return enhancer.create();
		
	}
	
	public List<InstanceInfo> getInfoList(){
		return list;
	}
	
}
