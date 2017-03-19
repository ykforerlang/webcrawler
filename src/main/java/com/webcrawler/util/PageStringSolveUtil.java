package com.webcrawler.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author yankang
 * @date 2014年10月31日
 */
public class PageStringSolveUtil {
	/**
	 * 取得符合正则的list
	 * 
	 * @param str
	 * @param regex
	 * @param header
	 * @param tail
	 * @return
	 */
	public static List<String> getMatStringList(String str, String regex,
			String header, String tail) {
		List<String> resultList = new ArrayList<String>();

		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(str);
		String matStr = null;
		while (mat.find()) {
			matStr = mat.group();
			resultList.add(matStr.substring(header.length(), matStr.length()
					- tail.length()));
		}
		return resultList;
	}

	/**
	 * 取得符合正则的string
	 * 
	 * @param str
	 * @param regex
	 * @param header
	 * @param tail
	 * @return
	 */
	public static String getMatString(String str, String regex, String header,
			String tail) {
		Pattern pat = Pattern.compile(regex);
		Matcher mat = pat.matcher(str);
		String result = mat.find() ? mat.group() : null;
		if (result == null)
			return null;
		return result.substring(header.length(),
				result.length() - tail.length());
	}

	/**
	 * 匹配出字符串中的图片地址
	 * 
	 * @param parse
	 * @return
	 */
	public static String getImgSrc(String parse) {
		String regex = "src=\".*?\"";
		return getMatString(parse, regex, "src=\"", "\"");
	}

	/**
	 * 匹配出字符串中的链接地址
	 * 
	 * @param parse
	 * @return
	 */
	public static String getHref(String parse) {
		String regex = "href=\".*?\"";
		return getMatString(parse, regex, "href=\"", "\"");
	}

	/**
	 * 根据正则匹配出字符串
	 * 
	 * @param parse
	 * @param regex
	 * @return
	 */
	public static String getMatString(String parse, String regex) {
		return getMatString(parse, regex, "", "");
	}

	/**
	 * 得到网页上所有在两个标签中间的元素。
	 * 
	 * @param parse
	 * @param removeWhite
	 *            是否去掉空白元素
	 * @return
	 */
	public static List<String> elements(String parse, boolean removeWhile) {
		String[] split = parse.split("<[\\s\\S]*?>");
		if (!removeWhile)
			return Arrays.asList(split);

		List<String> result = new ArrayList<String>();
		for (String str : split) {
			if (str.trim().length() == 0)
				continue;

			result.add(str.trim());
		}
		return result;
	}

	public static List<String> tableElements(String table, boolean removeWhile) {
		table = table.replaceAll("</?table[\\s\\S]*?>", "")
				.replaceAll("</?tbody[\\s\\S]*?>", "")
				.replaceAll("</?tr[\\s\\S]*?>", "");
		
		String[] split = table.split("<[\\s\\S]*?>");
		if (!removeWhile)
			return Arrays.asList(split);

		List<String> result = new ArrayList<String>();
		for (String str : split) {
			if (str.trim().length() == 0)
				result.add("");

			result.add(str.trim());
		}
		return result;
	}
	
	public static List<String> elementsWithEmpty(String res){
		String[] split = res.split("<[\\s\\S]*?>");

		List<String> result = new ArrayList<String>();
		for (String str : split) {
			if (str.trim().length() == 0)
				result.add("");

			result.add(str.trim());
		}
		return result;
	}

	/**
	 * <tr>
	 * 是外层list
	 * <td>是内层list
	 * 
	 * @param table
	 * @return
	 */
	public static List<List<String>> tableToList(String table) {
		List<List<String>> result = new ArrayList<List<String>>();
		
		String[] rows = table.split("</tr>");
		for(String row : rows ){
			List<String> cols = PageStringSolveUtil.getMatStringList(row, "<t[dh][\\s\\S]*?</t[dh]>", "", "");
			List<String> rowList = new ArrayList<String>();
			for(String col : cols){
				rowList.add(col.replaceAll("<[\\s\\S]*?>", "").trim());
			}
			result.add(rowList);
		}
		return result;
	}
	
	

}
