package com.yqs.search.entity;

import lombok.Data;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import java.util.ArrayList;
import java.util.List;

@Data
public class Paging {
    //document对象ID
    private ScoreDoc afterScoreDoc;
    //每页条数
    private int pageSize = 5;
    //最大页数
    private int maxPageNum;
    //总结果数
    private int totalResultNum = 0;
    //数据条数
    private List<Document> docList = new ArrayList<>();


    public void setTotalResultNum(int totalResultNum) {

        this.totalResultNum = totalResultNum;

        //如果 条数总数量 >0 并且 条数总数量 小于 每页条数
        if (totalResultNum > 0 && totalResultNum < pageSize)
            //最大页数
            this.maxPageNum = 1;
        else
            // 否则，最大页数 等于 条数总数量 除以 每页条数
            this.maxPageNum = totalResultNum % pageSize == 0 ? totalResultNum / pageSize : (totalResultNum / pageSize) + 1;

    }

}
