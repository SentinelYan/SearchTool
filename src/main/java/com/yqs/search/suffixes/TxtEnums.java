package com.yqs.search.suffixes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TxtEnums {

    //存放所有后缀名的SET
	public static final List<String> ORDINARY_TXT_LIST = new ArrayList<>();
    /** 此set主要是转换作用 **/
    public static Set<String> ORDINARY_SET = new HashSet<String>();

	//精简模式被选中走这个Set
	private static final Set<String> SIMPLE_TXT_SET = new HashSet<>();

	//这是实际输出到界面的SER
	public static Set<String> RUN_SET = new HashSet<>();


	static {
		ORDINARY_TXT_LIST.add(".TXT");
		ORDINARY_TXT_LIST.add(".CSV");
		//将其他所有键值对添加到此映射中
		ORDINARY_SET.addAll(ORDINARY_TXT_LIST);

		SIMPLE_TXT_SET.add(".TXT");
		RUN_SET = SIMPLE_TXT_SET;
	}

    /**
     * 判断是否是简洁模式或者是全选模式
     */
	public static void setRUN_TXT_SET(int n){
	    //如果n等于0，则RunTXT等于简洁模式下的map
		if(n == 0){
            RUN_SET = SIMPLE_TXT_SET;
        }
		//否则 就不是简洁模式
		else RUN_SET = ORDINARY_SET;
	}

    /**
     * 判断字符串是否是以指定的后缀结束
     */
	public static boolean suffixContains(String suffix) {

		for (String s : RUN_SET) {
		    //测试字符串是否以指定的后缀结束
			if (suffix.endsWith(s))
				return true;
		}
		return false;
	}

    /**
     * 获取所有后缀名SET
     */
	public static List<String> getValues() {

		return ORDINARY_TXT_LIST;
	}

    /**
     * 获取简洁模式SET
     */
	public static Set<String> getSimleValues() {

		return SIMPLE_TXT_SET;
	}

}
