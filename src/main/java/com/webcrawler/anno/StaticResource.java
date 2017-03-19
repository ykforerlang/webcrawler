package com.webcrawler.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *静态资源标志
 *@author yankang
 *@date 2014年11月2日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StaticResource {
	//文件名（不包括后缀），目录以/分隔， ${property}代表引用同一个bean下的属性，可以引用自身
	public String value();
}
