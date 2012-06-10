package org.djd.mensetsu.global;

import java.util.ArrayList;

public final class StringUtil {

	public static String marshall(ArrayList<Long> list) {
		StringBuilder sb = new StringBuilder(list.size() * 2);
		for (Long e : list) {
			sb.append(e);
			sb.append(',');
		}
		return sb.toString();
	}

	public static ArrayList<Long> unmarshall(String str) {
		if(null == str){
			return null;
		}
		ArrayList<Long> result = new ArrayList<Long>();

		String[] tokens = str.split(",");
		for (String s : tokens) {
			result.add(Long.valueOf(s));
		}
		return result;
	}
}
