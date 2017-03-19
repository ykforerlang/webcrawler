package com.webcrawler.test;

import com.webcrawler.anno.Action;
import com.webcrawler.anno.Init;
import com.webcrawler.anno.TaskSubmiting;
import com.webcrawler.model.StaticResourceTask;
import com.webcrawler.model.Task;

/**
 *
 *@author yankang
 *@date 2015年1月8日
 */
@Action
public class Test3 {
	
	@Init
	@TaskSubmiting
	public Task init() {
		return new StaticResourceTask("http://image.game.uc.cn/2014/12/5/10086390_.jpg", "/web/test/1.jpg");
	}
}
