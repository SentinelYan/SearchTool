package com.yqs.search.suffixes;

import java.util.HashSet;
import java.util.Set;

public class ExcelEnums {

	private static final Set<String> EXCEL_SET = new HashSet<>();
	public static final String XLSX = ".XLSX";
	public static final String XLS = ".XLS";
	public static final String ET = ".ET";

	static {
		EXCEL_SET.add(".XLS");
		EXCEL_SET.add(".XLSX");
		EXCEL_SET.add(".ET");
	}

	public static boolean contains(String suffix) {

		return EXCEL_SET.contains(suffix);
	}

	public static boolean suffixContains(String suffix) {

		for (String s : EXCEL_SET) {
			if (suffix.endsWith(s))
				return true;
		}
		return false;
	}

	public static Set<String> getValues() {

		return EXCEL_SET;
	}
}
