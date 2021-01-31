package com.yqs.search.suffixes;

import java.util.HashSet;
import java.util.Set;

public class PptEnums {
   
	private static final Set<String> PPT_SET = new HashSet<>();
	public static final String PPTX = ".PPTX";
	public static final String PPT = ".PPT";
	

	static {
		PPT_SET.add(".PPTX");
		PPT_SET.add(".PPT");
		
	}
	
	public static boolean contains(String suffix) {

		return PPT_SET.contains(suffix);
	}

	public static boolean suffixContains(String suffix) {

		for (String s : PPT_SET) {
			if (suffix.endsWith(s))
				return true;
		}
		return false;
	}

	public static Set<String> getValues() {

		return PPT_SET;
	}
}
