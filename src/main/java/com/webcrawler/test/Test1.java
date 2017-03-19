package com.webcrawler.test;

import com.webcrawler.anno.Action;
import com.webcrawler.anno.Init;

/**
 *
 *@author yankang
 *@date 2015年1月5日
 */
@Action
public class Test1 {
	
	@Init
	public void init1() {
		System.out.println(this.getClass());
	}
}
