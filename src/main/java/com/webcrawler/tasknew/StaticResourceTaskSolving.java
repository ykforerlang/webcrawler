package com.webcrawler.tasknew;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.webcrawler.model.StaticResourceTask;
import com.webcrawler.util.MyHttpClient;

/**
 *
 *@author yankang
 *@date 2015年1月8日
 */
public class StaticResourceTaskSolving {
	private MyHttpClient client = new MyHttpClient();
	
	public void storeResource(Object obj) throws ClientProtocolException, IOException{
		String data = (String) obj;
		String[] split = data.split(StaticResourceTask.middle);
		String url = split[0];
		String file = split[1];
		client.storeStaticResource(url, file);
	}
}
