package com.webcrawler.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *annotate a method which to solve task
 *@author yankang
 *@date 2014��12��10��
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TaskSolving {
	
	/**
	 * if value = ${a} , 则表明a是对应类的一个静态属性
	 * @return
	 */
	String value();
}
