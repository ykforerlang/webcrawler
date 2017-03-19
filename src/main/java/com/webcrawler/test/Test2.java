package com.webcrawler.test;

import java.util.ArrayList;
import java.util.List;

import com.webcrawler.anno.Action;
import com.webcrawler.anno.Init;
import com.webcrawler.anno.TaskSolving;
import com.webcrawler.anno.TaskSubmiting;
import com.webcrawler.model.Task;

/**
 *
 *@author yankang
 *@date 2015年1月5日
 */
@Action
public class Test2 {
	private  final static String test = "testStatic";
	
	@Init
	@TaskSubmiting
	public Task init() {
		System.out.println(this.getClass());
		return new Task(test, "yankang");
		
	}
	
	@TaskSubmiting
	@TaskSolving("test")
	public Task sub() {
		return new Task("test", "test");
	}
	
	@TaskSubmiting
	@TaskSolving("${test}")
	public List<Task> test1(String str) {
		System.out.println(str);
		List<Task> list = new ArrayList<Task>();
		list.add(new Task("test2", "test2"));
		list.add(new Task("test2", "test2"));
		System.out.println(list);
		return  list;
		//throw new RuntimeException("exception...");
	}
	
	@TaskSolving("test2")
	public void test2(String str){
		System.out.println(str);
	}
	
}
