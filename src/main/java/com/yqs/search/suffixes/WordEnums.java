package com.yqs.search.suffixes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordEnums {
    //存放后缀名的List
	private static final List<String> WORD_SET = new ArrayList<>();
	public static final String DOC = ".DOC";
	public static final String DOCX = ".DOCX";
	public static final String WPS = ".WPS";
    public static Set<String> wordSet = new HashSet<String>();
	
	static {
		WORD_SET.add(".DOCX");
		WORD_SET.add(".DOC");
		WORD_SET.add(".WPS");
		wordSet.addAll(WORD_SET);
	}

	public static boolean contains(String suffix) {
        //判断Set集合是否包含指定的对象
		return wordSet.contains(suffix);
	}

    /**
     * 判断后缀是否以word存储的后缀结束，如果是则返回true,不是则返回false
     */
	public static boolean suffixContains(String suffix) {

		for (String s : WORD_SET) {
			if (suffix.endsWith(s))
				return true;
		}
		return false;
	}

    /**
     * 获取所有的后缀字段
     */
	public static List<String> getValues() {

		return WORD_SET;
	}
}
