package com.yqs.search.util;

import com.yqs.search.constants.SystemConstants;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;

public class FileHighLight {
	private static Set<Integer> integerSet = new HashSet<>();

	public static void sunday(String s, String p) {

		char[] sarray = s.toCharArray();
		char[] parray = p.toCharArray();

		int slen = s.length();
		int plen = p.length();

		int i = 0, j = 0;

		while (i <= slen - plen + j) {
			if (sarray[i] != parray[j]) {
				if (i == slen - plen + j) {
					break;
				}
				int pos = contains(parray, sarray[i + plen - j]);
				if (pos == -1) {
					i = i + plen + 1 - j;
					j = 0;
				} else {
					i = i + plen - pos - j;
					j = 0;
				}
			} else {
				if (j == plen - 1) {
					int start = i - j;
					int end = i;
					for (int ii = start; ii < end + 1; ii++) {
						integerSet.add(ii);
					}
					i = i - j + 1;
					j = 0;
				} else {
					i++;
					j++;
				}
			}
		}
	}

	public static int contains(char[] str, char ch) {

		for (int i = str.length - 1; i >= 0; i--) {
			if (str[i] == ch) {
				return i;
			}
		}
		return -1;
	}

	public static  String high(String str, String[] searchKeys, String qStr, String hStr) {


		str = str.replaceAll("\n+", "<br/>");
		String loStr = str.toLowerCase();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+loStr);
		for (String key : searchKeys) {
			sunday(loStr, key);
		}
		if (integerSet.size() == 0)
			return str;
		List<Integer> list = new ArrayList<>();
		list.addAll(integerSet);
		Collections.sort(list);
		StringBuilder stringBuilder = new StringBuilder(str);
		int start = -1;
		int groupNum = 0;
		// 标红
		int pre = 0;
		int le = (qStr + hStr).length();
		for (int i = 0; i < list.size(); i++) {
			int nowNum = list.get(i);
			// 上一个号码+1不等于当前号码，表示换了一组
			if (pre + 1 != nowNum && i != 0) {
				if (groupNum == 0) {
					stringBuilder.insert(pre + qStr.length() + 1, hStr);
				} else {
					stringBuilder.insert(pre + (groupNum + 1) * le - hStr.length() + 1, hStr);
				}
				groupNum++;
				start = nowNum;
				stringBuilder.insert(start + groupNum * le, qStr);

			} else if (start == -1) {
				start = nowNum;
				stringBuilder.insert(start, qStr);
			}
			pre = nowNum;
		}
		stringBuilder.insert(pre + (groupNum + 1) * le - hStr.length() + 1, hStr);
		integerSet.clear();

		str = null;
		searchKeys = null;
		qStr = null;
		hStr = null;
		loStr = null;
		return stringBuilder.toString();
	}

    public static TextFlow fileNameHigh(String str, String[] keys, String path, String updateTime) {
        List<Text> txtList = new ArrayList<>();
        Text t;
        Set<Character> set = new HashSet<>();
        for (String key : keys) {
            // 关键词转化为小写
            for (int i = 0; i < key.length(); i++)
                set.add(key.charAt(i));
        }
        // 标红的开头和结尾
        int s = -1;
        // 普通的开头和结尾
        int ss = -1;

        String str2 = str.toLowerCase();
        for (int i = 0; i < str.length(); i++) {
            char c = str2.charAt(i);
            if (set.contains(c)) {
                // 如果是开头
                if (s == -1) {
                    s = i;
                }
                if (ss != -1) {
                    t = new Text(str.substring(ss, i));
                    t.setFont(SystemConstants.hFont);
                    ss = -1;
                    txtList.add(t);
                }
            } else {
                // 如果此时有开头结尾，就要把红的装起来
                if (s != -1) {
                    t = new Text(str.substring(s, i));
                    t.setFill(Color.RED);
                    t.setFont(SystemConstants.hFont);
                    s = -1;
                    txtList.add(t);
                }
                if (ss == -1) {
                    ss = i;
                }
            }
        }
        if (ss != -1) {
            t = new Text(str.substring(ss));
            t.setFont(SystemConstants.hFont);
            txtList.add(t);
        }
        if (s != -1) {
            t = new Text(str.substring(s));
            t.setFont(SystemConstants.hFont);
            t.setFill(Color.RED);
            txtList.add(t);
        }

        if (path.toString().length() < 70) {
            List<Text> txtList2 = new ArrayList<>();
            txtList2.add(new Text("\n"));
            txtList2.addAll(txtList);
            txtList = txtList2;
        }
        t = new Text("\n" + path);
        txtList.add(t);

        t = new Text("\n" + "(" + TimeUtil.simpleDateFormat.format(new Date(Long.parseLong(updateTime))) + ")");
        txtList.add(t);
        Text[] tar = {};
        str2 = null;
        str = null;
        TextFlow tf = new TextFlow(txtList.toArray(tar));
        return tf;
    }

}
