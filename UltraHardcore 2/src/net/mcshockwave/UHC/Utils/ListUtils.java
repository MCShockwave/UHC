package net.mcshockwave.UHC.Utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	public static <T> T[] subarray(T[] li, int sub, int end) {
		List<T> list = new ArrayList<T>();

		for (int i = sub; i < end; i++) {
			list.add(li[i]);
		}

		return list.toArray(li);
	}

	public static <T> T[] subarray(T[] li, int sub) {
		return subarray(li, sub, li.length);
	}

	public static String arrayToString(Object[] os) {
		return arrayToString(os, false);
	}

	public static String arrayToString(Object[] os, boolean comma) {
		String ret = "";
		for (int i = 0; i < os.length - 1; i++) {
			ret += (comma ? "," : "") + " " + os[i];
		}
		return ret.replaceFirst(" ", "");
	}

}
