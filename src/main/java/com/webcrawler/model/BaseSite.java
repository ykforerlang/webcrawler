package com.webcrawler.model;
/**
 *
 *@author yankang
 *@date 2014年11月18日
 */
public class BaseSite {
	
	private String url;
	private String param;
	private String methodType;
	public BaseSite() {
		url = param = methodType = "";
	}
	public BaseSite(String url){
		this.url = url;
		param = methodType = "";
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	@Override
	public String toString() {
		return "BaseSite [url=" + url + ", param=" + param + ", methodType="
				+ methodType + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((methodType == null) ? 0 : methodType.hashCode());
		result = prime * result + ((param == null) ? 0 : param.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseSite other = (BaseSite) obj;
		if (methodType == null) {
			if (other.methodType != null)
				return false;
		} else if (!methodType.equals(other.methodType))
			return false;
		if (param == null) {
			if (other.param != null)
				return false;
		} else if (!param.equals(other.param))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	
	
	
	
}
