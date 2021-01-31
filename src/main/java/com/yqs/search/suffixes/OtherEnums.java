package com.yqs.search.suffixes;

import java.util.HashSet;
import java.util.Set;

public class OtherEnums {
	private static final Set<String> OTHER_SET = new HashSet<>();

	static {
		OTHER_SET.add(".EXE");
		OTHER_SET.add(".JAR");
		OTHER_SET.add(".RAR");
		OTHER_SET.add(".JPG");
		OTHER_SET.add(".JPEG");
		OTHER_SET.add(".PNG");
		OTHER_SET.add(".GIF");
		OTHER_SET.add(".MP3");
		OTHER_SET.add(".MP4");
		OTHER_SET.add(".AVI");
		OTHER_SET.add(".RMVB");
		OTHER_SET.add(".ZIP");
		OTHER_SET.add(".CDR");
	}

	public static boolean contains(String suffix) {

		return OTHER_SET.contains(suffix);
	}

	public static boolean suffixContains(String suffix) {
		for (String s : OTHER_SET) {
			if (suffix.endsWith(s))
				return true;
		}
		return false;
	}

	public static Set<String> getValues() {

		return OTHER_SET;
	}
}
