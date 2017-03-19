package com.webcrawler.task;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * bean container
 *
 * @author yankang
 * @date 2014��12��9��
 */
public class BeanMapping {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Map<String, BaseAction> map = new ConcurrentHashMap<String, BaseAction>();

	private List<String> packages;

	public BeanMapping() {

	}

	public BeanMapping(List<String> packs) {
		this.packages = packs;
	}

	public void setPackages(String... packages) {
		this.packages = Arrays.asList(packages);
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public void initMap() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		log.info("init task mapping");
		ClassLoader loader = this.getClass().getClassLoader();
		for (String string : packages) {
			String path = string.replaceAll("\\.", "/");
			URL resource = loader.getResource(path);
			log.info("solve path " + resource.getPath());
			solveSingleResource(resource, string);
		}

	}

	private void solveSingleResource(URL resource, String pack)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
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
			BaseAction ba = null;
			try {
				Object newInstance = clazz.newInstance();
				ba = (BaseAction) newInstance;
				map.put(name, ba);
				log.info("add a bean " + name);
			} catch (ClassCastException e) {
				log.info("exclude " + clazz.getName()
						+ " because is not BaseAction");
			} catch (InstantiationException e) {
				log.info("exclude "
						+ clazz.getName()
						+ " because the class may be interface, abstrat class...");
			}

		}

	}

	public Object getBean(String key) {
		return map.get(key);
	}

	public Collection<BaseAction> getBeans() {
		return map.values();
	}

	public void putBean(String key, BaseAction bean) {
		map.put(key, bean);
	}

	protected Map<String, BaseAction> getMap() {
		return map;
	}

	/*
	 * private static BeanMapping bm = null; public static BeanMapping
	 * newInstance(List<String> packs){ if(bm == null){ bm = new
	 * BeanMapping(packs); } return bm; } public static BeanMapping
	 * getInstace(){ return bm; }
	 */

}
