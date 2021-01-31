package com.yqs.search.store;

import com.yqs.search.entity.Paging;
//import com.yqs.search.views.WindowsB;
import com.yqs.search.App;
import javafx.scene.text.TextFlow;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Stores {

    //关键词分割
    public static String[] PUBLICKEY;

    //存储本电脑盘符
    public static File[] DIRROOTS;

    //需要跳过的文件的绝对路径
    public static Set<String> skipFilesMap = new HashSet<String>();

    //
    public static Set<String> searchKeySet = new HashSet<String>();

    // 文档编号
    public static Map<Integer, ScoreDoc> scoreDoc = new HashMap<>();

    // 缓存Paging对象
    public static  Map<Integer, Paging> cachePaging = new HashMap<Integer, Paging>();

    //一条TextFlow
    public static  Map<Integer, App.TextFlowData> textFlowDataMap = new HashMap<>();

    //存储路径的docMap 一条记录对应一个路径
    public static  Map<Document, Path> docMap = new HashMap<Document, Path>();

    //存储UI页面的 文件名/文件路径/最后修改时间  /// TextFlow
    public static  Map<String, TextFlow> textFlowMap = new HashMap<String, TextFlow>();
}
