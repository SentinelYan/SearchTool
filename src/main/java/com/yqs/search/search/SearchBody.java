package com.yqs.search.search;

import lombok.Data;

import java.util.Date;

@Data
public class SearchBody {
    //关键词
	private String searchKey = "";
	//关键词分割
	private String[] searchKeys = new String[] {};
	//过滤词分割
	private String[] noSearchKeys;
	//过滤词
	private String noSearchKey = "";
	//搜索的所有设置 关键词、过滤词、盘符、开始事件、结束时间等
	private String cachePre;
	//开始时间
	private Date startDate;
	//结束时间
	private Date endDate;

}
