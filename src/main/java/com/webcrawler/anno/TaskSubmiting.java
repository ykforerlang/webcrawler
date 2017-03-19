package com.webcrawler.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *annotate a method which to submit a task/tasks, the method must return Task/List<Task>
 *@author yankang
 *@date 2014��12��10��
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TaskSubmiting {

}
