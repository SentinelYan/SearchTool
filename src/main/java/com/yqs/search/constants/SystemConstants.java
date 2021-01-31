package com.yqs.search.constants;

import com.yqs.search.suffixes.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class SystemConstants {

    public static int GO = 1;

    public static int SYSTEM_STATUS = 0; // 0 = 初始值 1 = 扫描开始  2 = 扫描暂停  3 =   4 = 扫描重置
    //搜索的条数
    public static int SEARCH_NUM = 200000;
    // 最大页数
    public static int maxPageNum = 0;
    /***
     * 主页面常量
     */
    public static String TITLE_NAME = "调试问题搜索软件 v1.0";
    public static int SCENE_WEIGHT = 1124;
    public static int scene_HIGH = 700;
    public static String LabelA_STR = "关键词";
    public static String LabelB_STR = "过滤词";
    public static String LabelC_STR = "检索条件";
    public static String LabelD_STR = "扫描设置";
    public static String LabelE_STR = "时间范围";
    public static String BtnA_STR = "搜 索";
    public static String BtnB_STR = "重 置";
    public static String BtnC_STR = "扫描文件";
    public static String rbtnA_STR = "全新 ";
    public static String rbtnB_STR = "精简 ";
    public static String rbtnC_STR = "自动";
    public static int BTN_WEIGHT = 85;
    public static int BTN_HIGH = 31;
	public static final String YSM = "已扫描的文件数量:";
	public static final String QBLX = "全部类型";
	public static final String SYPF = "所有盘符";
	public static final String WBLX = "文本类型";
	public static final String ASJPX = "按时间排序";
	public static final String WJMPX = "文件名排序";
	public static final String QT = "其它文件";
	public static final String QT2 = "文件夹";

    public static Font hFont = Font.font("微软雅黑", FontWeight.BOLD, 14);
    public static Font YH = new Font("微软雅黑", 15);
    public static String YHSTR = "-fx-font-family: '微软雅黑';";

	//文件最后访问时间
	public static final String LASTACCTIME = "basic:lastAccessTime";

	public static  String DIAN = ".";

	//存放 普通模式 后缀名 List
	public static List<String> suffixList = new ArrayList<>();

	//存放 简洁模式 后缀名 List
    public static List<String> simpleSuffixList = new ArrayList<>();

    //精简模式 = 15  30
    public static int TASK_TIME = 10;

	static {
		suffixList.addAll(TxtEnums.getValues());
		suffixList.addAll(ExcelEnums.getValues());
		suffixList.addAll(WordEnums.getValues());
		suffixList.addAll(PptEnums.getValues());
		suffixList.add(PdfEnums.PDF);
	}
	static {
		simpleSuffixList.add(".TXT");
		simpleSuffixList.addAll(ExcelEnums.getValues());
		simpleSuffixList.addAll(WordEnums.getValues());
		simpleSuffixList.addAll(PptEnums.getValues());
		simpleSuffixList.add(PdfEnums.PDF);
	}
}
