package com.yqs.search.search;

import lombok.Data;
import org.apache.lucene.search.Query;

@Data
public class SearchBean {

    //用户的关键词内容、盘符、起始日期、结束日期等
    private SearchBody body;
    
    //查询对象
    private Query query;

    public void setBody(SearchBody body) {
        this.body = body;
        if (body != null) {
            this.query = Search.buildQuery(body);
        } else
            this.query = null;
    }

}
