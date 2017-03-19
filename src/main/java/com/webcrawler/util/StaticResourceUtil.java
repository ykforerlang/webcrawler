package com.webcrawler.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcrawler.anno.StaticResource;

/**
 *
 * @author yankang
 * @date 2014年11月19日
 */
public class StaticResourceUtil {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private String basePath;

	/**
	 * 将注解值，与属性名 解析成键值对
	 * 
	 * @param obj
	 *            待解析对象
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public Map<String, String> parseStaticAnno(Object obj) {
		Map<String, String> map = new HashMap<String, String>();

		Field[] fields = obj.getClass().getDeclaredFields();
		StaticResource sr = null;
		try {
			for (Field f : fields) {
				if ((sr = f.getAnnotation(StaticResource.class)) == null)
					continue;
				f.setAccessible(true);
				String value = (String) f.get(obj);
				int lastIndexOf = value.lastIndexOf(".");
				String suffix = value.substring(lastIndexOf);

				String fullPath = getFullPath(obj, sr.value());
				map.put(f.getName(), fullPath + suffix);
			}

		} catch (Exception e) {
			throw new RuntimeException("解析" + obj + " 注解错误", e);
		}

		return map;
	}

	private String getFullPath(Object obj, String value)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = obj.getClass();
		String fullPath = "";
		String[] split = value.split("/");
		for (String str : split) {
			String sub = null;
			if (str.startsWith("${") && str.endsWith("}")) {
				str = str.substring(2, str.length() - 1);
				Field field = clazz.getDeclaredField(str);
				field.setAccessible(true);
				sub = (String) field.get(obj);
			} else {
				sub = str;
			}
			sub += "/";
			fullPath += sub;
		}

		fullPath = fullPath.substring(0, fullPath.length() - 1);
		fullPath = basePath + "/" + fullPath;
		log.debug("get full file path: " + fullPath);
		return fullPath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}
}
